package com.matthewperiut.clay.item.disruptor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.matthewperiut.clay.entity.horse.HorseDollEntity;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;

import java.util.List;
import java.util.function.Consumer;

public class DisruptorItem extends Item
{
    public DisruptorItem(ClayMaterial material, Properties settings)
    {
        super(settings.durability(material.getDurability()));
    }

    boolean unlimited = false;

    public DisruptorItem(Properties settings)
    {
        super(settings);
        unlimited = true;
    }

    public boolean killClayEntity(Level world, Vec3 pos)
    {
        AABB area = new AABB(pos.subtract(new Vec3(16, 16, 16)), pos.add(new Vec3(16, 16, 16)));
        List<Entity> entityList = world.getEntities(null, area);

        boolean found = false;
        for (Entity entity : entityList)
        {
            if (entity instanceof SoldierDollEntity || entity instanceof HorseDollEntity)
            {
                entity.kill();
                found = true;
            }
        }

        return found;
    }

    public boolean removeClayEntity(Level world, Entity user, ItemStack stack)
    {
        boolean found = killClayEntity(world, user.position());

        if (found)
        {
            if (!unlimited)
            {
                stack.hurtAndBreak(1, (ServerLevel) world, (ServerPlayer) user, item -> stack.setCount(0));
                ((Player) user).getCooldowns().set(this, 20);

                return true;
            }

            if (user instanceof Player)
            {
                ((Player) user).getCooldowns().set(this, 20);
                return true;
            }
            else
            {
                stack.hurtAndBreak(1, (ServerLevel) world, (ServerPlayer) user, item -> stack.setCount(0));
                ((Player) user).getCooldowns().set(this, 20);

                return true;
            }
        }
        return false;
    }

    public boolean removeClayBlock(Level world, Entity user, ItemStack stack)
    {
        Vec3 pos = user.position();
        AABB area = new AABB(pos.subtract(new Vec3(16, 16, 16)), pos.add(new Vec3(16, 16, 16)));
        boolean found = false;

        for (int x = (int) area.minX; x <= (int) area.maxX; x++)
        {
            for (int y = (int) area.minY; y <= (int) area.maxY; y++)
            {
                for (int z = (int) area.minZ; z <= (int) area.maxZ; z++)
                {
                    BlockPos blockPos = new BlockPos(x, y, z);
                    BlockState blockState = world.getBlockState(blockPos);

                    if (blockState.getBlock().getDefaultState().equals(Blocks.CLAY.getDefaultState()))
                    {
                        world.destroyBlock(blockPos, true);
                        found = true;
                    }
                }
            }
        }

        if (found)
        {
            if (!unlimited)
            {
                stack.hurtAndBreak(1, (ServerLevel) world, (ServerPlayer) user, item -> stack.setCount(0));
                ((Player) user).getCooldowns().set(this, 20);
                return true;
            }

            if (user instanceof Player)
            {
                ((Player) user).getCooldowns().set(this, 20);
                return true;
            }
            else
            {
                stack.hurtAndBreak(1, (ServerLevel) world, (ServerPlayer) user, item -> stack.setCount(0));
                ((Player) user).getCooldowns().set(this, 20);
                return true;
            }
        }
        return false;
    }


    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand)
    {

        ItemStack itemStack = user.getItemInHand(hand);

        if (removeClayEntity(world, user, itemStack) || removeClayBlock(world, user, itemStack))
            return InteractionResult.SUCCESS;

        return InteractionResult.FAIL;
    }

}
