package com.matthewperiut.clay.entity.ai.goal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import com.matthewperiut.clay.entity.horse.HorseDollEntity;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.matthewperiut.clay.upgrade.UpgradeManager;

import java.util.List;

public abstract class SoldierAIFindTarget<T extends Entity> extends Goal {
    protected final SoldierDollEntity soldier;
    protected final EntityTypeTest<Entity, T> typeFilter;
    protected T target;
    protected int decrementTravelTime = 20;

    public SoldierAIFindTarget(SoldierDollEntity soldier, EntityTypeTest<Entity, T> filter) {
        this.target = null;
        this.soldier = soldier;
        this.typeFilter = filter;
    }

    @Override
    public boolean canUse() {
        return this.soldier.getFollowingEntity() == null;
    }

    @Override
    public void tick() {
        findTarget();
    }

    @Override
    public void stop() {
        this.target = null;
    }

    protected void findTarget() {
        if (decrementTravelTime-- > 0) {
            return;
        }
        decrementTravelTime = 20;
        AABB box = new AABB(this.soldier.position().subtract(8, 4, 8), this.soldier.position().add(8, 4, 8));

        Level world = this.soldier.level();

        List<T> targets = world.getEntities(this.typeFilter, box, this::isTargetable);

        if (targets.isEmpty()) return;

        this.target = targets.get(this.soldier.getRandom().nextInt(0, targets.size()));
        this.soldier.setFollowingEntity(this.target);
    }

    protected abstract boolean isTargetable(T searchTarget);

    public static class PlayerRideable extends SoldierAIFindTarget<HorseDollEntity> {

        public PlayerRideable(SoldierDollEntity soldier, EntityTypeTest<Entity, HorseDollEntity> filter) {
            super(soldier, filter);
        }

        @Override
        protected boolean isTargetable(HorseDollEntity searchTarget) {
            return searchTarget.isAlive() && !searchTarget.isVehicle() && searchTarget.hasLineOfSight(soldier);
        }

        @Override
        public boolean canUse() {
            return !this.soldier.isPassenger() && super.canUse();
        }
    }

    public static class Upgrade extends SoldierAIFindTarget<ItemEntity> {

        public Upgrade(SoldierDollEntity soldier, EntityTypeTest<Entity, ItemEntity> filter) {
            super(soldier, filter);
        }

        @Override
        protected boolean isTargetable(ItemEntity searchTarget) {
            return UpgradeManager.INSTANCE.canApplyUpdate(soldier, searchTarget.getItem());
        }

        @Override
        public boolean canUse() {
            return !this.soldier.maxedOutUpgrades() && super.canUse();
        }
    }
}
