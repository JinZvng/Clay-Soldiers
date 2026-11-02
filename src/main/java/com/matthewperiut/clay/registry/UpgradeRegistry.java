package com.matthewperiut.clay.registry;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.upgrade.ISoldierUpgrade;
import com.matthewperiut.clay.upgrade.armor.SoldierLeatherUpgrade;
import com.matthewperiut.clay.upgrade.extension.SoldierFlintUpgrade;
import com.matthewperiut.clay.upgrade.extension.SoldierWoolUpgrade;
import com.matthewperiut.clay.upgrade.hand.SoldierStickUpgrade;
import com.matthewperiut.clay.upgrade.misc.SoldierGlowstoneUpgrade;
import com.matthewperiut.clay.upgrade.misc.SoldierGoldIngotUpgrade;
import com.matthewperiut.clay.platform.RegistrySupplier;

public class UpgradeRegistry {
    public static final Identifier REGISTRY_ID = Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "upgrades");
    public static final ResourceKey<Registry<ISoldierUpgrade>> UPGRADES_KEY =
            ResourceKey.createRegistryKey(REGISTRY_ID);
    public static final com.matthewperiut.clay.platform.SmallRegistry<ISoldierUpgrade> SOLDIER_UPGRADE_REGISTER = new com.matthewperiut.clay.platform.SmallRegistry<>(REGISTRY_ID);

    public static final RegistrySupplier<ISoldierUpgrade> STICK_UPGRADE = SOLDIER_UPGRADE_REGISTER.register(SoldierStickUpgrade.IDENTIFIER, SoldierStickUpgrade::new);
    public static final RegistrySupplier<ISoldierUpgrade> GLOWSTONE_UPGRADE = SOLDIER_UPGRADE_REGISTER.register(SoldierGlowstoneUpgrade.IDENTIFIER, SoldierGlowstoneUpgrade::new);
    public static final RegistrySupplier<ISoldierUpgrade> FLINT_UPGRADE = SOLDIER_UPGRADE_REGISTER.register(SoldierFlintUpgrade.IDENTIFIER, SoldierFlintUpgrade::new);
    public static final RegistrySupplier<ISoldierUpgrade> WOOL_UPGRADE = SOLDIER_UPGRADE_REGISTER.register(SoldierWoolUpgrade.IDENTIFIER, SoldierWoolUpgrade::new);
    public static final RegistrySupplier<ISoldierUpgrade> LEATHER_UPGRADE = SOLDIER_UPGRADE_REGISTER.register(SoldierLeatherUpgrade.IDENTIFIER, SoldierLeatherUpgrade::new);
    public static final RegistrySupplier<ISoldierUpgrade> GOLD_INGOT_UPGRADE = SOLDIER_UPGRADE_REGISTER.register(SoldierGoldIngotUpgrade.IDENTIFIER, SoldierGoldIngotUpgrade::new);

    public static final RegistrySupplier<ISoldierUpgrade> FEATHER = toy("feather", net.minecraft.world.item.Items.FEATHER);
    public static final RegistrySupplier<ISoldierUpgrade> BOW = toy("bow", net.minecraft.world.item.Items.BOW);
    public static final RegistrySupplier<ISoldierUpgrade> SHIELD = toy("shield", net.minecraft.world.item.Items.SHIELD);
    public static final RegistrySupplier<ISoldierUpgrade> SLIME = toy("slime", net.minecraft.world.item.Items.SLIME_BALL);
    public static final RegistrySupplier<ISoldierUpgrade> COPPER = toy("copper", net.minecraft.world.item.Items.COPPER_INGOT);
    public static final RegistrySupplier<ISoldierUpgrade> GUNPOWDER = toy("gunpowder", net.minecraft.world.item.Items.GUNPOWDER);
    private static RegistrySupplier<ISoldierUpgrade> toy(String name, net.minecraft.world.item.Item material) {
        return SOLDIER_UPGRADE_REGISTER.register(Identifier.fromNamespaceAndPath("clay", "upgrades/soldier/"+name), () -> new com.matthewperiut.clay.upgrade.ToyUpgrade(name, material));
    }
    public static void init() {
    }
}
