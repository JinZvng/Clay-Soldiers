package com.matthewperiut.clay.entity.ai.goal;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;

import com.matthewperiut.clay.entity.horse.HorseDollEntity;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.matthewperiut.clay.upgrade.UpgradeManager;

import java.util.EnumSet;

public abstract class SoliderAIFollowTarget extends Goal {

    protected final SoldierDollEntity soldier;
    protected double speed;

    public SoliderAIFollowTarget(SoldierDollEntity soldier, double speed) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.soldier = soldier;
        this.speed = speed;
    }

    @Override
    public boolean canUse() {
        Entity target = getTarget();
        return target != null && target.isAlive() && hasValidTarget();
    }

    @Override
    public void tick() {
        Entity target = getTarget();
        this.soldier.getLookControl().setLookAt(target, 30.0F, 30.0F);
        double distanceToTarget = this.soldier.distanceToSqr(target.getX(), target.getY(), target.getZ());
        if (this.soldier.getRandom().nextFloat() < 0.25F) {
            this.soldier.getNavigation().moveTo(target, this.speed);
        }
        action(distanceToTarget);
    }

    @Override
    public void stop() {
        this.cleanUp();
    }


    abstract void action(double distanceToTarget);

    abstract Entity getTarget();

    abstract boolean hasValidTarget();

    // TODO maybe this doesn't need to be abstract
    abstract void cleanUp();

    public static class PlayerRideable extends SoliderAIFollowTarget {

        public PlayerRideable(SoldierDollEntity soldier, double speed) {
            super(soldier, speed);
        }

        @Override
        public Entity getTarget() {
            return soldier.getFollowingEntity();
        }

        @Override
        boolean hasValidTarget() {
            Entity target = getTarget();
            return target instanceof HorseDollEntity && !target.isVehicle();
        }

        @Override
        void cleanUp() {
            soldier.setFollowingEntity(null);
        }

        @Override
        void action(double distanceToTarget) {
            Entity target = getTarget();
            if (distanceToTarget <= 2 && target.tickCount > 20) {
                soldier.startRiding(target);
                cleanUp();
            }
        }
    }

    public static class Upgrade extends SoliderAIFollowTarget {
        public Upgrade(SoldierDollEntity soldier, double speed) {
            super(soldier, speed);
        }

        @Override
        void action(double distanceToTarget) {
            if (distanceToTarget <= 2) {
                ItemEntity target = getTarget();
                UpgradeManager.INSTANCE.applyUpdate(soldier, target.getItem());
                target.getItem().shrink(1);
                soldier.level().playSound(null, soldier.getX(), soldier.getY(), soldier.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((soldier.getRandom().nextFloat() - soldier.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                if (target.getItem().getCount() == 0) {
                    target.makeFakeItem();
                }
                cleanUp();
            }
        }

        @Override
        ItemEntity getTarget() {
            if (this.soldier.getFollowingEntity() instanceof ItemEntity)
                return (ItemEntity) this.soldier.getFollowingEntity();
            return null;
        }

        @Override
        boolean hasValidTarget() {
            return getTarget() != null && getTarget().getItem().getCount() >= 1;
        }

        @Override
        void cleanUp() {
            soldier.setFollowingEntity(null);
        }
    }
}
