package com.matthewperiut.clay.entity.soldier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;

import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.entity.ai.goal.MeleeAttackTinyGoal;
import com.matthewperiut.clay.entity.ai.goal.SoldierAIFindTarget;
import com.matthewperiut.clay.entity.ai.goal.SoliderAIFollowTarget;
import com.matthewperiut.clay.entity.horse.HorseDollEntity;
import com.matthewperiut.clay.entity.soldier.teams.ITeam;
import com.matthewperiut.clay.extension.ISpawnReasonExtension;
import com.matthewperiut.clay.nbt.NBTValues;
import com.matthewperiut.clay.network.packet.SyncUpgradesS2CPacket;
import com.matthewperiut.clay.registry.ItemRegistry;
import com.matthewperiut.clay.registry.TeamRegistry;
import com.matthewperiut.clay.upgrade.ISoldierUpgrade;
import com.matthewperiut.clay.upgrade.UpgradeInstance;
import com.matthewperiut.clay.upgrade.UpgradeManager;
import org.jetbrains.annotations.NotNull;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.util.GeckoLibUtil;

import java.util.*;

import static com.matthewperiut.clay.entity.soldier.teams.ITeam.NBT_IDENTIFIER;
import static com.matthewperiut.clay.registry.TeamRegistry.CLAY_TEAM;

public class SoldierDollEntity extends PathfinderMob implements com.geckolib.animatable.GeoEntity, net.neoforged.neoforge.entity.IEntityWithComplexSpawn {
    public static final com.geckolib.constant.dataticket.DataTicket<Boolean> RIDING = com.geckolib.constant.dataticket.DataTicket.create("clay_riding", Boolean.class);
    public static final Identifier TEXTURE_ID = Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "textures/entity/soldier/lightgray.png");
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    public HashSet<ISoldierUpgrade> upgrades = new HashSet<>();
    public HashMap<ISoldierUpgrade, UpgradeInstance> upgradeInstances = new HashMap<>();
    public Queue<ISoldierUpgrade> removeUpgrades = new LinkedList<>();
    protected boolean isLightBlockUnaffected = false;
    private Entity followingEntity;
    private ITeam team;
    private boolean isAnimating = false;

    public SoldierDollEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
        super(type, worldIn);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes().add(Attributes.MAX_HEALTH, 5.00f).add(Attributes.ATTACK_DAMAGE, 1.0f).add(Attributes.ATTACK_SPEED, 0.0f).add(Attributes.MOVEMENT_SPEED, 0.25f);
    }

    private <E extends GeoAnimatable> PlayState predicate(AnimationTest<E> event) {
        if (event.renderState().getOrDefaultGeckolibData(RIDING, false)) {
            event.controller().setAnimation(RawAnimation.begin().thenPlay("animation.clay_soldier.ride"));
            return PlayState.CONTINUE;
        }
        if (event.isMoving()) {
            event.controller().setAnimation(RawAnimation.begin().thenPlay("animation.clay_soldier.run"));
        } else {
            event.controller().setAnimation(RawAnimation.begin().thenPlay("animation.clay_soldier.idle"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(
                new AnimationController<>( "controller", 0, this::predicate),
                new AnimationController<SoldierDollEntity>("attackController", test -> PlayState.STOP).triggerableAnim("attack", RawAnimation.begin().thenPlay("animation.clay_soldier.attack"))
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    protected void selectTargets() {
        this.targetSelector.addGoal(3, new SoldierAIFindTarget.PlayerRideable(this, EntityTypeTest.forClass(HorseDollEntity.class)));
        this.targetSelector.addGoal(3, new SoldierAIFindTarget.Upgrade(this, EntityTypeTest.forClass(ItemEntity.class)));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, SoldierDollEntity.class, true, (e, world) -> {
            if (e instanceof SoldierDollEntity target) {
                return orderMode == 3 && !target.getSoldierTeam().isinSameTeam(this.getSoldierTeam().getTeamId());
            }
            return false;
        }));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new com.matthewperiut.clay.entity.ai.goal.CommandGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1, 1));
        this.goalSelector.addGoal(3, new MeleeAttackTinyGoal(this, 1, false));
        this.goalSelector.addGoal(2, new SoliderAIFollowTarget.PlayerRideable(this, 1));
        this.goalSelector.addGoal(2, new SoliderAIFollowTarget.Upgrade(this, 1));
        this.goalSelector.addGoal(1, new FloatGoal(this));

        selectTargets(); // priority 3

        super.registerGoals();
    }



    @Override
    protected SoundEvent getHurtSound(DamageSource src) {
        return SoundEvents.GRAVEL_BREAK;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GRAVEL_STEP;
    }
//region EVENTS

    @Override
    public void die(DamageSource damageSource) {
        // The client also invokes die() when handling the entity-death packet.
        // Mount removal and drops must only run in the authoritative world.
        if (level() instanceof ServerLevel server) {
            Entity vehicle = getVehicle();
            if (vehicle instanceof HorseDollEntity) vehicle.kill(server);
            if (damageSource.type().equals(server.damageSources().inFire().type())
                    || damageSource.type().equals(server.damageSources().lava().type())) {
                server.addFreshEntity(new ItemEntity(server, getX(), getY(), getZ(), new ItemStack(ItemRegistry.BRICK_SOLDIER.get())));
            }
            for (ISoldierUpgrade upgrade : upgrades) {
                server.addFreshEntity(new ItemEntity(server, getX(), getY(), getZ(), upgrade.getUpgradeItem()));
            }
        }
        boolean blast = !toyExploded && hasToy("gunpowder");
        toyExploded=true;
        super.die(damageSource);
        if(blast && level() instanceof ServerLevel server) server.explode(this,getX(),getY(),getZ(),.6f,Level.ExplosionInteraction.NONE);
    }

    public void afterSoldierAttack(Entity target) {


        if (target instanceof SoldierDollEntity targetSoldier) {
            this.upgrades.forEach(u -> u.onAttack(targetSoldier, this));
            ISoldierUpgrade upgrade;
            while ((upgrade = removeUpgrades.poll()) != null) {
                UpgradeManager.INSTANCE.removeUpgrade(this, upgrade);
            }
        }
    }

    @Override
    public void handleDamageEvent(DamageSource damageSource) {
        super.handleDamageEvent(damageSource);
    }

    @Override
    public boolean skipAttackInteraction(Entity attacker) {
        if (attacker instanceof Player) {
            if (level() instanceof ServerLevel server) kill(server);
        }

        return super.skipAttackInteraction(attacker);
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if (hasToy("feather") && source.is(net.minecraft.tags.DamageTypeTags.IS_FALL)) return false;
        if (hasToy("shield") && tickCount >= shieldReady && source.getEntity()!=null) {
            var direction=source.getEntity().position().subtract(position()).normalize();
            if(getLookAngle().dot(direction)>.2) {shieldReady=tickCount+40;return false;}
        }
        if(hasToy("copper"))amount*=.7f;
        boolean hurt = super.hurtServer(world, source, amount);
        if (hurt && source.getEntity() instanceof SoldierDollEntity attacker) {
            for (ISoldierUpgrade upgrade : new HashSet<>(upgrades)) upgrade.doPostHurtEffects(attacker, this);
            ISoldierUpgrade expired;
            while ((expired = removeUpgrades.poll()) != null) UpgradeManager.INSTANCE.removeUpgrade(this, expired);
        }
        return hurt;
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        if(hasToy("bow") && target instanceof LivingEntity living){shootToyBow(living);return false;}
        swingForAttack(InteractionHand.MAIN_HAND);
        boolean hit = super.doHurtTarget(world, target);
        if (hit) {
            if(hasToy("slime") && target instanceof LivingEntity living) living.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.SLOWNESS,40,0));
            triggerAnim("attackController", "attack");
            afterSoldierAttack(target);
        }
        return hit;
    }
//endregion EVENTS



    public Entity getFollowingEntity() {
        return followingEntity;
    }

    public void setFollowingEntity(Entity followingEntity) {
        this.followingEntity = followingEntity;
    }

    public boolean maxedOutUpgrades() {
        return false;
    }

    public ITeam getSoldierTeam() {
        if(team!=null)return team;
        String color=BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getPath().replace("soldier/", "");
        return TeamRegistry.SOLDIER_TEAMS.entrySet().stream().filter(e->e.getKey().identifier().getPath().equals(color)).map(java.util.Map.Entry::getValue).findFirst().orElse(CLAY_TEAM.get());
    }

    public void setSoldierTeam(@NotNull ITeam team) {
        this.team = team;
    }

    //region SYNC

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        CompoundTag nbt = input.read("ClayData", CompoundTag.CODEC).orElseGet(CompoundTag::new);
        orderMode=nbt.getInt("OrderMode").orElse(3);
        try { commander=java.util.UUID.fromString(nbt.getString("Commander").orElse("")); } catch(IllegalArgumentException ignored) {commander=null;}
        holdX=nbt.getDouble("HoldX").orElse(getX());holdY=nbt.getDouble("HoldY").orElse(getY());holdZ=nbt.getDouble("HoldZ").orElse(getZ());
        // Explicit attack targets are deliberately released on reload.
        if(orderMode==1)orderMode=2;
        short teamId = nbt.getShort(NBT_IDENTIFIER).orElse((short) 0);
        Optional<Map.Entry<ResourceKey<ITeam>, ITeam>> teamOptional = TeamRegistry.SOLDIER_TEAMS.entrySet()
                .stream()
                .filter(t -> t.getValue().isinSameTeam(teamId))
                .findFirst();
        setSoldierTeam(teamOptional.map(Map.Entry::getValue).orElse(CLAY_TEAM.get()));

        ListTag nbtListForUpgrades = nbt.getListOrEmpty(NBTValues.SOLDIER_UPGRADES.getKey());
        for (int i = 0; i < nbtListForUpgrades.size(); i++) {
            CompoundTag nbtCompound = nbtListForUpgrades.getCompoundOrEmpty(i);
            Identifier id = Identifier.parse(nbtCompound.getString(NBTValues.SOLDIER_UPGRADES_ID.getKey()).orElse(""));
            UpgradeManager.INSTANCE.handleNBTRead(this, id, nbtCompound);
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("OrderMode",orderMode);if(commander!=null)nbt.putString("Commander",commander.toString());
        nbt.putDouble("HoldX",holdX);nbt.putDouble("HoldY",holdY);nbt.putDouble("HoldZ",holdZ);
        nbt.putShort(NBT_IDENTIFIER, getSoldierTeam().getTeamId());
        ListTag nbtListForUpgrades = new ListTag();
        for (ISoldierUpgrade upgrade : this.upgrades) {
            CompoundTag nbtTag = new CompoundTag();
            UpgradeManager.INSTANCE.handleNBTWrite(this, upgrade, nbtTag);
            nbtListForUpgrades.add(nbtTag);
        }
        nbt.put(NBTValues.SOLDIER_UPGRADES.getKey(), nbtListForUpgrades);
        output.store("ClayData", CompoundTag.CODEC, nbt);
    }

    @Override
    public void writeSpawnData(net.minecraft.network.RegistryFriendlyByteBuf buf) {
        SyncUpgradesS2CPacket syncUpgradeData = new SyncUpgradesS2CPacket(this);
        syncUpgradeData.write(buf);
    }

    @Override
    public void readSpawnData(net.minecraft.network.RegistryFriendlyByteBuf buf) {
        SyncUpgradesS2CPacket syncUpgradeData = new SyncUpgradesS2CPacket(buf);
        this.upgrades = new HashSet<>(syncUpgradeData.getUpgrades());
        this.upgrades.forEach(u -> u.onAdd(this));
    }
//endregion SYNC

//region CLIENT

    public boolean isLightBlockUnaffected() {
        return isLightBlockUnaffected;
    }

    public void setLightBlockUnaffected(boolean lightBlockUnaffected) {
        this.isLightBlockUnaffected = lightBlockUnaffected;
    }

//endregion

    public int orderMode=3;
    private java.util.UUID commander, orderedTarget;
    private double holdX,holdY,holdZ;
    private int shieldReady, bowReady;
    private boolean toyExploded;
    public boolean hasToy(String name){return upgrades.stream().anyMatch(u->u instanceof com.matthewperiut.clay.upgrade.ToyUpgrade t && t.kind.equals(name));}
    public void command(int mode,Player player,LivingEntity target){
        orderMode=mode;commander=player.getUUID();orderedTarget=target==null?null:target.getUUID();holdX=getX();holdY=getVehicle()==null?getY():getVehicle().getY();holdZ=getZ();
        setFollowingEntity(null);setTarget(mode==1?target:null);getNavigation().stop();
    }
    public void tickCommand(){
        if(!(level() instanceof ServerLevel server))return;
        LivingEntity target=orderedTarget==null?null:(server.getEntity(orderedTarget) instanceof LivingEntity living ? living:null);
        if(orderMode==1)setTarget(target);
        if(orderMode!=1)setTarget(null);
        net.minecraft.world.phys.Vec3 destination=null;
        if(orderMode==0){var p=commander==null?null:server.getPlayerByUUID(commander);if(p!=null)destination=p.position();}
        if(orderMode==2)destination=new net.minecraft.world.phys.Vec3(holdX,holdY,holdZ);
        if(orderMode==1){
            if(target==null||!target.isAlive()){orderMode=2;holdX=getX();holdY=getVehicle()==null?getY():getVehicle().getY();holdZ=getZ();return;}
            destination=target.position();
            getLookControl().setLookAt(target,30,30);
            if(hasToy("bow"))shootToyBow(target);
            else if(distanceToSqr(target)<2 && tickCount%20==0)doHurtTarget(server,target);
        }
        if(destination==null){getNavigation().stop();if(getVehicle() instanceof HorseDollEntity horse)horse.getNavigation().stop();return;}
        if(getVehicle() instanceof net.minecraft.world.entity.animal.allay.Allay allay){
            allay.getNavigation().stop();
            double altitude=orderMode==2?0:(orderMode==1&&!hasToy("bow")?0:1);
            var delta=destination.add(0,altitude,0).subtract(allay.position());
            var step=delta.lengthSqr()>2?delta.normalize().scale(.18):delta.scale(.08);
            if(allay.horizontalCollision && delta.lengthSqr()>2)step=step.add(0,.18,0);
            // NoAI allays do not run normal flying travel. Move once with collision
            // resolution, then clear velocity to avoid applying movement twice.
            allay.move(net.minecraft.world.entity.MoverType.SELF,step);
            allay.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
        } else {
            var mover=getVehicle() instanceof HorseDollEntity horse?horse:this;
            double stopDistance=orderMode==1?(hasToy("bow")?36:1):4;
            if(mover.distanceToSqr(destination)>stopDistance)mover.getNavigation().moveTo(destination.x,destination.y,destination.z,1.15);
            else mover.getNavigation().stop();
        }
    }
    private void shootToyBow(LivingEntity target){
        if(!(level() instanceof ServerLevel server)||tickCount<bowReady||distanceToSqr(target)>144||!hasLineOfSight(target))return;
        bowReady=tickCount+35;
        var arrow=new net.minecraft.world.entity.projectile.arrow.Arrow(server,this,new ItemStack(Items.ARROW),new ItemStack(Items.BOW));
        var delta=target.getEyePosition().subtract(arrow.position());
        arrow.shoot(delta.x,delta.y+Math.sqrt(delta.x*delta.x+delta.z*delta.z)*.12,delta.z,1.2f,6);
        if(hasToy("slime"))arrow.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.SLOWNESS,40,0));
        arrow.setBaseDamage(.8);server.addFreshEntity(arrow);
    }

    // temp stick fix
    public static final EntityDataAccessor<Boolean> HAS_STICK;

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_STICK, false);

    }
    static {
        HAS_STICK = SynchedEntityData.defineId(SoldierDollEntity.class, EntityDataSerializers.BOOLEAN);
    }

    @Override
    public void tick() {
        super.tick();
        if(level() instanceof ServerLevel && isAlive()) {
            if(orderMode!=3)tickCommand();
            if(tickCount%5==0) {
                for(var item:level().getEntitiesOfClass(ItemEntity.class,getBoundingBox().inflate(1))) {
                    if(!item.getItem().isEmpty() && UpgradeManager.INSTANCE.canApplyUpdate(this,item.getItem())) {
                        UpgradeManager.INSTANCE.applyUpdate(this,item.getItem());item.getItem().shrink(1);
                        if(item.getItem().isEmpty())item.discard();
                    }
                }
            }
            if(tickCount%20==0 && !isPassenger() && orderMode!=2) {
                var allays=level().getEntitiesOfClass(net.minecraft.world.entity.animal.allay.Allay.class,getBoundingBox().inflate(1.5),a->a.isAlive()&&!a.isVehicle());
                if(!allays.isEmpty())startRiding(allays.getFirst());
            }
            if(orderMode==3 && hasToy("bow") && getTarget()!=null) shootToyBow(getTarget());
        }
        if (!level().isClientSide() && tickCount % 20 == 0) {
            if(getMainHandItem().is(Items.STICK)) {
                entityData.set(HAS_STICK, true);
            }
        }
    }
}