package com.matthewperiut.clay.registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import com.matthewperiut.clay.entity.horse.HorseDollEntity;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;

public class AttributeRegistry {

    public static void init() {
        EntityTypeRegistry.ENTITY_TYPES_HORSES.forEach(e -> {
            EntityAttributeRegistry.register(() -> (EntityType<? extends LivingEntity>) e.get(), HorseDollEntity::setAttributesBuilder);
        });
        EntityTypeRegistry.ENTITY_TYPES_SOLDIERS.forEach(e -> {
            EntityAttributeRegistry.register(() -> (EntityType<? extends LivingEntity>) e.get(), SoldierDollEntity::createAttributes);
        });
    }
}
