package com.matthewperiut.clay.entity.ai.goal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;

import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;

import java.util.EnumSet;
import java.util.List;

public class PickUpUpgradesGoal extends Goal {

    SoldierDollEntity entity;


    public PickUpUpgradesGoal(SoldierDollEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return entity.getLastHurtByMob() == null;
    }

    @Override
    public void tick() {
        List<ItemEntity> itemsNearby = entity.level().getEntitiesOfClass(ItemEntity.class, entity.getBoundingBox().inflate(8.0, 8.0, 8.0), Entity::isCurrentlyGlowing);
        if (!itemsNearby.isEmpty()) {
            entity.getNavigation().moveTo(itemsNearby.get(entity.getRandom().nextInt(itemsNearby.size())), 2);
        }
    }
}
