package com.matthewperiut.clay.entity.soldier.teams;
import net.minecraft.world.item.ItemStack;


// IDEA: Maybe this can support later alliances
public interface ITeam {
    String NBT_IDENTIFIER = "Team";

    short getTeamId();

    String getName();

    ItemStack getTeamItem();

    default boolean isinSameTeam(short teamId) {
        return getTeamId() == teamId;
    }
}
