package com.matthewperiut.clay.item.common;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.matthewperiut.clay.entity.soldier.teams.ITeam;
import com.matthewperiut.clay.registry.TeamRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static com.matthewperiut.clay.registry.TeamRegistry.CLAY_TEAM;

public class SpawnDollItem extends Item {
    public ArrayList<Supplier<EntityType<?>>> types;

    public SpawnDollItem(ArrayList<Supplier<EntityType<?>>> types, Properties settings) {
        super(settings);
        this.types = types;
    }

    public SpawnDollItem(Supplier<EntityType<?>> defaultType, Properties settings) {
        super(settings);
        this.types = new ArrayList<>();
        types.add(defaultType);
    }

    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!(world instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack itemStack = context.getItemInHand();
            BlockPos blockPos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockState = world.getBlockState(blockPos);

            BlockPos blockPos2;
            if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
                blockPos2 = blockPos;
            } else {
                blockPos2 = blockPos.relative(direction);
            }

            int count = itemStack.getCount();
            EntityType<?> entityType = this.getEntityType();
            ITeam team = this.getTeam(itemStack);

            for (int i = 0; i < count; i++) {
                if (types.size() > 1) {
                    entityType = this.getEntityType();
                }
                Entity entity = entityType.spawn((ServerLevel) world, itemStack, context.getPlayer(), blockPos2, EntitySpawnReason.SPAWN_ITEM_USE, false, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP);

                if (entity == null) continue;

                itemStack.shrink(1);
                world.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
                world.playSound(context.getPlayer(), blockPos, SoundEvents.GRAVEL_BREAK, SoundSource.BLOCKS, 1.f, 1.f);

                if (entity instanceof SoldierDollEntity soldier) {
                    soldier.setSoldierTeam(team);
                }
            }

            return InteractionResult.SUCCESS;
        }
    }

    public ITeam getTeam(ItemStack stack) {
        Optional<Map.Entry<ResourceKey<ITeam>, ITeam>> teamOptional = TeamRegistry.SOLDIER_TEAMS.entrySet().stream().filter(entry -> stack.is(entry.getValue().getTeamItem().getItem())).findFirst();
        return teamOptional.map(Map.Entry::getValue).orElse(CLAY_TEAM.get());
    }

    public EntityType<?> getEntityType() {
        int selected = RandomSource.create().nextInt(types.size());
        return types.get(selected).get();
    }
}
