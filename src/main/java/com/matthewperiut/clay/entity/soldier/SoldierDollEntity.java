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
import dev.architectury.extensions.network.EntitySpawnExtension;
import dev.architectury.networking.NetworkManager;
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

public class SoldierDollEntity extends PathfinderMob implements GeoAnimatable, EntitySpawnExtension {
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
        if (this.isPassenger()) {
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

    private PlayState attackPredicate(AnimationTest<SoldierDollEntity> event) {
        if (this.swingTime > 0) {
            event.controller().reset();
            event.controller().setAnimation(RawAnimation.begin().thenPlay("animation.clay_soldier.attack"));
            
            return PlayState.STOP;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(
                new AnimationController<>( "controller", 0, this::predicate),
                new AnimationController<>( "attackController", 1, this::attackPredicate)
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
                return !target.getSoldierTeam().isinSameTeam(this.getSoldierTeam().getTeamId());
            }
            return false;
        }));
    }

    @Override
    protected void registerGoals() {
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
        return SoundEvents.GRAVEL_BREAK.value();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GRAVEL_STEP.value();
    }
//region EVENTS

    @Override
    public void die(DamageSource damageSource) {
        if (this.isPassenger()) Objects.requireNonNull(this.getVehicle()).discard();
        if (damageSource.getType().equals(this.level().damageSources().inFire().getType()) || (damageSource.getType().equals(this.level().damageSources().lava().getType())))
            level().addFreshEntity(new ItemEntity(level(), getX(), getY(), getZ(), new ItemStack(ItemRegistry.BRICK_SOLDIER.get(), 1)));
        for (ISoldierUpgrade upgrade : upgrades) {
            level().addFreshEntity(new ItemEntity(level(), getX(), getY(), getZ(), upgrade.getUpgradeItem()));
        }
        super.die(damageSource);
    }

    @Override
    public void onAttack(Entity target) {
        super.onAttack(target);

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
            discard();
        }
        if (attacker instanceof SoldierDollEntity attackerSoldier) {
            this.upgrades.forEach(u -> u.doPostHurtEffects(attackerSoldier, this));
            ISoldierUpgrade upgrade;
            while ((upgrade = removeUpgrades.poll()) != null) {
                UpgradeManager.INSTANCE.removeUpgrade(this, upgrade);
            }
        }

        return super.skipAttackInteraction(attacker);
    }

    @Override
    public boolean doHurtTarget(ServerLevel world, Entity target) {
        swing(InteractionHand.MAIN_HAND);
        return super.doHurtTarget(world, target);
    }
//endregion EVENTS

    @Override
    public boolean requiresCustomPersistence() {
        if (this instanceof ISpawnReasonExtension) {
            return ((ISpawnReasonExtension) this).clay$getSpawnReason() == EntitySpawnReason.SPAWN_EGG;
        }
        return super.requiresCustomPersistence();
    }

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
        return team == null ? CLAY_TEAM.get() : team;
    }

    public void setSoldierTeam(@NotNull ITeam team) {
        this.team = team;
    }

    //region SYNC

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        CompoundTag nbt = input.read("ClayData", CompoundTag.CODEC).orElseGet(CompoundTag::new);
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
    public void saveAdditionalSpawnData(FriendlyByteBuf buf) {
        SyncUpgradesS2CPacket syncUpgradeData = new SyncUpgradesS2CPacket(this);
        syncUpgradeData.write(buf);
    }

    @Override
    public void loadAdditionalSpawnData(FriendlyByteBuf buf) {
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

    // temp stick fix
    public static final EntityDataAccessor<Boolean> HAS_STICK;

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.add(HAS_STICK, false);

    }
    static {
        HAS_STICK = SynchedEntityData.defineId(SoldierDollEntity.class, EntityDataSerializers.BOOLEAN);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide() && tickCount % 20 == 0) {
            if(getMainHandItem().is(Items.STICK)) {
                entityData.set(HAS_STICK, true);
            }
        }
    }
}