package com.matthewperiut.clay.entity.horse;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;

import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.extension.ISpawnReasonExtension;
import com.matthewperiut.clay.registry.ItemRegistry;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.*;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.util.GeckoLibUtil;

public class HorseDollEntity extends PathfinderMob implements GeoAnimatable
{
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    public HorseDollEntity(EntityType<? extends PathfinderMob> entityType, Level world)
    {
        super(entityType, world);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder setAttributesBuilder()
    {
        return PathfinderMob.createMobAttributes().add(Attributes.MAX_HEALTH, 5.00f).add(Attributes.ATTACK_DAMAGE, 0.0f).add(Attributes.ATTACK_SPEED, 0.8f).add(Attributes.MOVEMENT_SPEED, 0.32f);
    }

    public static AttributeSupplier setAttributes()
    {
        return setAttributesBuilder().build();
    }

    private <E extends GeoAnimatable> PlayState predicate(AnimationTest<E> event)
    {
        if (event.isMoving())
        {
            event.controller().setAnimation(RawAnimation.begin().thenPlay("animation.doll_horse.run"));
        } else {
            event.controller().setAnimation(RawAnimation.begin().thenPlay("animation.doll_horse.idle"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>( "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return factory;
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.0));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        super.registerGoals();
    }



    @Override
    protected SoundEvent getHurtSound(DamageSource src)
    {
        return SoundEvents.GRAVEL_BREAK;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.GRAVEL_STEP;
    }

    @Override
    public boolean skipAttackInteraction(Entity attacker)
    {
        if (attacker instanceof Player && level() instanceof net.minecraft.server.level.ServerLevel server) kill(server);
        return super.skipAttackInteraction(attacker);
    }

    @Override
    public void die(DamageSource damageSource)
    {
        if (damageSource.type().equals(this.level().damageSources().inFire().type()) || damageSource.type().equals(this.level().damageSources().lava().type()))
            level().addFreshEntity(new ItemEntity(level(), getX(), getY(), getZ(), new ItemStack(ItemRegistry.BRICK_HORSE.get(), 1)));
        super.die(damageSource);
    }


}
