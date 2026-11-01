package com.matthewperiut.clay.item.disruptor;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


import java.util.concurrent.atomic.AtomicBoolean;

public class DisruptorDispenserBehavior extends OptionalDispenseItemBehavior
{
    protected ItemStack execute(BlockSource pointer, ItemStack stack)
    {
        Level world = pointer.level();

        if (world.isClientSide()) {
            return stack;
        }

        Item item = stack.getItem();

        if (item instanceof DisruptorItem disruptor) {
            this.setSuccess(disruptor.killClayEntity(world, Vec3.ofCenter(pointer.pos())));
            if (this.isSuccess()) {
                if (!disruptor.unlimited) {
                    stack.hurtAndBreak(1, (ServerLevel) world, (ServerPlayer) null, i -> stack.setCount(0));

                }
            }
        }

        return stack;
    }
}
