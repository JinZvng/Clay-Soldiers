package com.matthewperiut.clay.item.soldier;
import net.minecraft.world.entity.EntityType;

import com.matthewperiut.clay.item.common.SpawnDollItem;

import java.util.function.Supplier;

public class SoldierDollItem extends SpawnDollItem
{
    public SoldierDollItem(Supplier<EntityType<?>> defaultType, Properties settings)
    {
        super(defaultType, settings);
    }
    @Override public void appendHoverText(net.minecraft.world.item.ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<net.minecraft.network.chat.Component> out, net.minecraft.world.item.TooltipFlag flag) {
        out.accept(net.minecraft.network.chat.Component.translatable("clay.soldier.help"));
        out.accept(net.minecraft.network.chat.Component.translatable("clay.upgrades.help"));
        out.accept(net.minecraft.network.chat.Component.translatable("clay.upgrades.limits"));
    }
}
