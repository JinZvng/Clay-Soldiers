package com.matthewperiut.clay.item.disruptor;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;


public class ClayMaterial implements Tier
{
    @Override
    public int getUses()
    {
        return 32;
    }

    @Override
    public float getMiningSpeedMultiplier()
    {
        return 0;
    }

    @Override
    public float getAttackDamage()
    {
        return 0;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return null;
    }

    @Override
    public int getEnchantmentValue()
    {
        return 0;
    }

    @Override
    public Ingredient getRepairIngredient()
    {
        return null;
    }
}
