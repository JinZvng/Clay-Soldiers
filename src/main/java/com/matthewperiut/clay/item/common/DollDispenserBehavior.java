package com.matthewperiut.clay.item.common;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;


public class DollDispenserBehavior implements DispenseItemBehavior
{
    public static DollDispenserBehavior DOLL_DISPENSE = new DollDispenserBehavior();

    @Override
    public ItemStack dispense(BlockSource pointer, ItemStack stack)
    {
        if (stack.getItem() instanceof SpawnDollItem doll)
        {
            ServerLevel world = pointer.level();
            BlockPos pos = pointer.pos();
            DispenserBlockEntity dispenserEntity = pointer.blockEntity();
            BlockState dispenserBlock = world.getBlockState(dispenserEntity.getBlockPos());
            Direction direction = dispenserBlock.getValue(BlockStateProperties.FACING);
            BlockPos spawnPos = pos.relative(direction);

            EntityType<?> entityType = ((SpawnDollItem)stack.getItem()).getEntityType();
            Entity e = entityType.spawn(world, stack, null, spawnPos, EntitySpawnReason.DISPENSER, false, false);
            if (e != null)
            {
                world.playSound(null, pos, SoundEvents.GRAVEL_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                world.gameEvent(GameEvent.ENTITY_PLACE, spawnPos, GameEvent.Context.of(e));
                stack.setCount(stack.getCount() - 1);
            }
        }

        return stack;
    }
}
