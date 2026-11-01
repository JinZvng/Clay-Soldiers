package com.matthewperiut.clay.registry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.entity.horse.HorseDollEntity;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.matthewperiut.clay.entity.soldier.variant.*;
import com.matthewperiut.clay.util.ClientInfoStorage;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

import static com.matthewperiut.clay.ClayRegistries.getIdentifier;

public class EntityTypeRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES_SOLDIERS = DeferredRegister.create(ClayMod.MOD_ID, net.minecraft.core.registries.Registries.ENTITY_TYPE);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES_HORSES = DeferredRegister.create(ClayMod.MOD_ID, net.minecraft.core.registries.Registries.ENTITY_TYPE);
    private static final float SOLDIER_HEIGHT = 0.5f;
    private static final float SOLDIER_WIDTH = 0.25f;

    // Soldiers
    public static final RegistrySupplier<EntityType<? extends SoldierDollEntity>> CLAY_SOLDIER = ENTITY_TYPES_SOLDIERS.register("soldier/clay", () -> EntityType.Builder.of(RegularSoldierDoll::new, MobCategory.CREATURE).sized(SOLDIER_WIDTH, SOLDIER_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("soldier/clay"))));
    public static final RegistrySupplier<EntityType<? extends SoldierDollEntity>> RED_SOLDIER = ENTITY_TYPES_SOLDIERS.register("soldier/red", () -> EntityType.Builder.of(RedSoldierDoll::new, MobCategory.CREATURE).sized(SOLDIER_WIDTH, SOLDIER_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("soldier/red"))));
    public static final RegistrySupplier<EntityType<? extends SoldierDollEntity>> YELLOW_SOLDIER = ENTITY_TYPES_SOLDIERS.register("soldier/yellow", () -> EntityType.Builder.of(YellowSoldierDoll::new, MobCategory.CREATURE).sized(SOLDIER_WIDTH, SOLDIER_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("soldier/yellow"))));
    public static final RegistrySupplier<EntityType<? extends SoldierDollEntity>> GREEN_SOLDIER = ENTITY_TYPES_SOLDIERS.register("soldier/green", () -> EntityType.Builder.of(GreenSoldierDoll::new, MobCategory.CREATURE).sized(SOLDIER_WIDTH, SOLDIER_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("soldier/green"))));
    public static final RegistrySupplier<EntityType<? extends SoldierDollEntity>> BLUE_SOLDIER = ENTITY_TYPES_SOLDIERS.register("soldier/blue", () -> EntityType.Builder.of(BlueSoldierDoll::new, MobCategory.CREATURE).sized(SOLDIER_WIDTH, SOLDIER_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("soldier/blue"))));
    public static final RegistrySupplier<EntityType<? extends SoldierDollEntity>> ORANGE_SOLDIER = ENTITY_TYPES_SOLDIERS.register("soldier/orange", () -> EntityType.Builder.of(OrangeSoldierDoll::new, MobCategory.CREATURE).sized(SOLDIER_WIDTH, SOLDIER_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("soldier/orange"))));
    public static final RegistrySupplier<EntityType<? extends SoldierDollEntity>> MAGENTA_SOLDIER = ENTITY_TYPES_SOLDIERS.register("soldier/magenta", () -> EntityType.Builder.of(MagentaSoldierDoll::new, MobCategory.CREATURE).sized(SOLDIER_WIDTH, SOLDIER_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("soldier/magenta"))));
    public static final RegistrySupplier<EntityType<? extends SoldierDollEntity>> LIGHTBLUE_SOLDIER = ENTITY_TYPES_SOLDIERS.register("soldier/lightblue", () -> EntityType.Builder.of(LightblueSoldierDoll::new, MobCategory.CREATURE).sized(SOLDIER_WIDTH, SOLDIER_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("soldier/lightblue"))));
    public static final RegistrySupplier<EntityType<? extends SoldierDollEntity>> LIME_SOLDIER = ENTITY_TYPES_SOLDIERS.register("soldier/lime", () -> EntityType.Builder.of(LimeSoldierDoll::new, MobCategory.CREATURE).sized(SOLDIER_WIDTH, SOLDIER_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("soldier/lime"))));
    public static final RegistrySupplier<EntityType<? extends SoldierDollEntity>> PINK_SOLDIER = ENTITY_TYPES_SOLDIERS.register("soldier/pink", () -> EntityType.Builder.of(PinkSoldierDoll::new, MobCategory.CREATURE).sized(SOLDIER_WIDTH, SOLDIER_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("soldier/pink"))));
    public static final RegistrySupplier<EntityType<? extends SoldierDollEntity>> CYAN_SOLDIER = ENTITY_TYPES_SOLDIERS.register("soldier/cyan", () -> EntityType.Builder.of(CyanSoldierDoll::new, MobCategory.CREATURE).sized(SOLDIER_WIDTH, SOLDIER_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("soldier/cyan"))));
    public static final RegistrySupplier<EntityType<? extends SoldierDollEntity>> PURPLE_SOLDIER = ENTITY_TYPES_SOLDIERS.register("soldier/purple", () -> EntityType.Builder.of(PurpleSoldierDoll::new, MobCategory.CREATURE).sized(SOLDIER_WIDTH, SOLDIER_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("soldier/purple"))));
    public static final RegistrySupplier<EntityType<? extends SoldierDollEntity>> BROWN_SOLDIER = ENTITY_TYPES_SOLDIERS.register("soldier/brown", () -> EntityType.Builder.of(BrownSoldierDoll::new, MobCategory.CREATURE).sized(SOLDIER_WIDTH, SOLDIER_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("soldier/brown"))));
    public static final RegistrySupplier<EntityType<? extends SoldierDollEntity>> BLACK_SOLDIER = ENTITY_TYPES_SOLDIERS.register("soldier/black", () -> EntityType.Builder.of(BlackSoldierDoll::new, MobCategory.CREATURE).sized(SOLDIER_WIDTH, SOLDIER_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("soldier/black"))));
    public static final RegistrySupplier<EntityType<? extends SoldierDollEntity>> GRAY_SOLDIER = ENTITY_TYPES_SOLDIERS.register("soldier/gray", () -> EntityType.Builder.of(GraySoldierDoll::new, MobCategory.CREATURE).sized(SOLDIER_WIDTH, SOLDIER_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("soldier/gray"))));
    public static final RegistrySupplier<EntityType<? extends SoldierDollEntity>> WHITE_SOLDIER = ENTITY_TYPES_SOLDIERS.register("soldier/white", () -> EntityType.Builder.of(WhiteSoldierDoll::new, MobCategory.CREATURE).sized(SOLDIER_WIDTH, SOLDIER_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("soldier/white"))));

    private static final float HORSE_HEIGHT = 0.3f;
    private static final float HORSE_WIDTH = 0.3f;
    // Horses
    public static final RegistrySupplier<EntityType<HorseDollEntity>> DIRT_HORSE = ENTITY_TYPES_HORSES.register("horse/dirt", () -> EntityType.Builder.of(HorseDollEntity::new, MobCategory.CREATURE).sized(HORSE_WIDTH, HORSE_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("horse/dirt"))));
    public static final RegistrySupplier<EntityType<HorseDollEntity>> GRASS_HORSE = ENTITY_TYPES_HORSES.register("horse/grass", () -> EntityType.Builder.of(HorseDollEntity::new, MobCategory.CREATURE).sized(HORSE_WIDTH, HORSE_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("horse/grass"))));
    public static final RegistrySupplier<EntityType<HorseDollEntity>> MYCELIUM_HORSE = ENTITY_TYPES_HORSES.register("horse/mycelium", () -> EntityType.Builder.of(HorseDollEntity::new, MobCategory.CREATURE).sized(HORSE_WIDTH, HORSE_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("horse/mycelium"))));
    public static final RegistrySupplier<EntityType<HorseDollEntity>> SNOW_HORSE = ENTITY_TYPES_HORSES.register("horse/snow", () -> EntityType.Builder.of(HorseDollEntity::new, MobCategory.CREATURE).sized(HORSE_WIDTH, HORSE_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("horse/snow"))));
    public static final RegistrySupplier<EntityType<HorseDollEntity>> SAND_HORSE = ENTITY_TYPES_HORSES.register("horse/sand", () -> EntityType.Builder.of(HorseDollEntity::new, MobCategory.CREATURE).sized(HORSE_WIDTH, HORSE_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("horse/sand"))));
    public static final RegistrySupplier<EntityType<HorseDollEntity>> GRAVEL_HORSE = ENTITY_TYPES_HORSES.register("horse/gravel", () -> EntityType.Builder.of(HorseDollEntity::new, MobCategory.CREATURE).sized(HORSE_WIDTH, HORSE_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("horse/gravel"))));
    public static final RegistrySupplier<EntityType<HorseDollEntity>> FULL_SNOW_HORSE = ENTITY_TYPES_HORSES.register("horse/snowy", () -> EntityType.Builder.of(HorseDollEntity::new, MobCategory.CREATURE).sized(HORSE_WIDTH, HORSE_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("horse/snowy"))));
    public static final RegistrySupplier<EntityType<HorseDollEntity>> FULL_GRASS_HORSE = ENTITY_TYPES_HORSES.register("horse/grassy", () -> EntityType.Builder.of(HorseDollEntity::new, MobCategory.CREATURE).sized(HORSE_WIDTH, HORSE_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("horse/grassy"))));
    public static final RegistrySupplier<EntityType<HorseDollEntity>> LAPIS_HORSE = ENTITY_TYPES_HORSES.register("horse/lapis", () -> EntityType.Builder.of(HorseDollEntity::new, MobCategory.CREATURE).sized(HORSE_WIDTH, HORSE_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("horse/lapis"))));
    public static final RegistrySupplier<EntityType<HorseDollEntity>> CARROT_HORSE = ENTITY_TYPES_HORSES.register("horse/carrot", () -> EntityType.Builder.of(HorseDollEntity::new, MobCategory.CREATURE).sized(HORSE_WIDTH, HORSE_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("horse/carrot"))));
    public static final RegistrySupplier<EntityType<HorseDollEntity>> CLAY_HORSE = ENTITY_TYPES_HORSES.register("horse/clay", () -> EntityType.Builder.of(HorseDollEntity::new, MobCategory.CREATURE).sized(HORSE_WIDTH, HORSE_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("horse/clay"))));
    public static final RegistrySupplier<EntityType<HorseDollEntity>> SOUL_SAND_HORSE = ENTITY_TYPES_HORSES.register("horse/soul_sand", () -> EntityType.Builder.of(HorseDollEntity::new, MobCategory.CREATURE).sized(HORSE_WIDTH, HORSE_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("horse/soul_sand"))));
    public static final RegistrySupplier<EntityType<HorseDollEntity>> CAKE_HORSE = ENTITY_TYPES_HORSES.register("horse/cake", () -> EntityType.Builder.of(HorseDollEntity::new, MobCategory.CREATURE).sized(HORSE_WIDTH, HORSE_HEIGHT).build(ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, getIdentifier("horse/cake"))));

    public static void init() {
        ENTITY_TYPES_HORSES.register();
        ENTITY_TYPES_SOLDIERS.register();
    }

    public static void clientRegister() {
        registerSoldier(CLAY_SOLDIER, SoldierDollEntity.TEXTURE_ID);
        registerSoldier(RED_SOLDIER, RedSoldierDoll.TEXTURE_ID);
        registerSoldier(YELLOW_SOLDIER, YellowSoldierDoll.TEXTURE_ID);
        registerSoldier(GREEN_SOLDIER, GreenSoldierDoll.TEXTURE_ID);
        registerSoldier(BLUE_SOLDIER, BlueSoldierDoll.TEXTURE_ID);
        registerSoldier(ORANGE_SOLDIER, OrangeSoldierDoll.TEXTURE_ID);
        registerSoldier(MAGENTA_SOLDIER, MagentaSoldierDoll.TEXTURE_ID);
        registerSoldier(LIGHTBLUE_SOLDIER, LightblueSoldierDoll.TEXTURE_ID);
        registerSoldier(LIME_SOLDIER, LimeSoldierDoll.TEXTURE_ID);
        registerSoldier(PINK_SOLDIER, PinkSoldierDoll.TEXTURE_ID);
        registerSoldier(CYAN_SOLDIER, CyanSoldierDoll.TEXTURE_ID);
        registerSoldier(PURPLE_SOLDIER, PurpleSoldierDoll.TEXTURE_ID);
        registerSoldier(BROWN_SOLDIER, BrownSoldierDoll.TEXTURE_ID);
        registerSoldier(BLACK_SOLDIER, BlackSoldierDoll.TEXTURE_ID);
        registerSoldier(GRAY_SOLDIER, GraySoldierDoll.TEXTURE_ID);
        registerSoldier(WHITE_SOLDIER, WhiteSoldierDoll.TEXTURE_ID);

        registerHorse(DIRT_HORSE, Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "textures/entity/mount/horse/dirt1.png"));
        registerHorse(GRASS_HORSE, Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "textures/entity/mount/horse/dirt2.png"));
        registerHorse(MYCELIUM_HORSE, Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "textures/entity/mount/horse/dirt3.png"));
        registerHorse(SNOW_HORSE, Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "textures/entity/mount/horse/dirt4.png"));
        registerHorse(SAND_HORSE, Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "textures/entity/mount/horse/sand.png"));
        registerHorse(GRAVEL_HORSE, Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "textures/entity/mount/horse/gravel3.png"));
        registerHorse(FULL_GRASS_HORSE, Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "textures/entity/mount/horse/grass1.png"));
        registerHorse(FULL_SNOW_HORSE, Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "textures/entity/mount/horse/snow.png"));
        registerHorse(LAPIS_HORSE, Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "textures/entity/mount/horse/lapis1.png"));
        registerHorse(CARROT_HORSE, Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "textures/entity/mount/horse/carrot1.png"));
        registerHorse(CLAY_HORSE, Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "textures/entity/mount/horse/clay.png"));
        registerHorse(SOUL_SAND_HORSE, Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "textures/entity/mount/horse/soulsand1.png"));
        registerHorse(CAKE_HORSE, Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "textures/entity/mount/horse/cake.png"));
    }

    private static void registerHorse(RegistrySupplier<EntityType<HorseDollEntity>> entityType, Identifier texture) {
        ClientInfoStorage.add(entityType::get, texture, ClientInfoStorage.RendererType.horse.ordinal());
    }

    private static void registerSoldier(RegistrySupplier<EntityType<? extends SoldierDollEntity>> soldierType, Identifier texture) {
        ClientInfoStorage.add(soldierType::get, texture, ClientInfoStorage.RendererType.soldier.ordinal());
    }
}
