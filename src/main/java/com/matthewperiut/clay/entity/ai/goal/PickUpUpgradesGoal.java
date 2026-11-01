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
        this.setFlags(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canUse() {
        return entity.getAttacker() == null;
    }

    @Override
    public void tick() {
        List<ItemEntity> itemsNearby = entity.level().getEntitiesOfClass(ItemEntity.class, entity.getBoundingBox().expand(8.0, 8.0, 8.0), Entity::isGlowing);
        if (!itemsNearby.isEmpty()) {
            entity.getNavigation().startMovingTo(itemsNearby.get(entity.getRandom().nextBetween(0, itemsNearby.size() - 1)), 2);
        }
    }
}
