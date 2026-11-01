package com.matthewperiut.clay.registry;
import com.matthewperiut.clay.entity.horse.HorseDollEntity;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
public final class AttributeRegistry {
    public static void register(EntityAttributeCreationEvent event) {
        EntityTypeRegistry.ENTITY_TYPES_HORSES.forEach(type -> event.put((EntityType<? extends LivingEntity>) type.get(), HorseDollEntity.setAttributesBuilder().build()));
        EntityTypeRegistry.ENTITY_TYPES_SOLDIERS.forEach(type -> event.put((EntityType<? extends LivingEntity>) type.get(), SoldierDollEntity.createAttributes().build()));
    }
}
