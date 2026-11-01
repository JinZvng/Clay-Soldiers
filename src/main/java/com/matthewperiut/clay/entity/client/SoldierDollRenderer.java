package com.matthewperiut.clay.entity.client;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.matthewperiut.clay.entity.client.layer.SoldierDollLeatherArmorLayer;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.layer.BlockAndItemGeoLayer;

import static com.matthewperiut.clay.entity.soldier.SoldierDollEntity.HAS_STICK;

public class SoldierDollRenderer extends GeoEntityRenderer<SoldierDollEntity> {
    private static final String LEFT_HAND = "lhand";
    private static final String RIGHT_HAND = "rhand";
    private static final String TORSO = "torso";
    private static final String RIGHT_ARM = "rarm";
    private static final String LEFT_ARM = "larm";

    private static final ItemStack stick = new ItemStack(Items.STICK, 1);

    public SoldierDollRenderer(EntityRendererProvider.Context blockRenderDispatcher, Identifier texture_id) {
        super(blockRenderDispatcher, new SoldierDollModel(texture_id));
        this.shadowRadius = 0.1f;

        BlockAndItemGeoLayer<SoldierDollEntity> itemLayer = new BlockAndItemGeoLayer<>(this) {
            @Override
            protected ItemStack getStackForBone(GeoBone bone, SoldierDollEntity animatable) {
                return switch (bone.getName()) {
                    case LEFT_HAND -> animatable.getEntityData().get(HAS_STICK) ? stick : animatable.getItemInHand(InteractionHand.MAIN_HAND);
                    case RIGHT_HAND -> animatable.getItemInHand(InteractionHand.OFF_HAND);
                    default -> super.getStackForBone(bone, animatable);
                };
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, SoldierDollEntity animatable, MultiBufferSource bufferSource,
                                              float partialTick, int packedLight, int packedOverlay) {

                if (stack.isOf(Items.STICK)) {
                    poseStack.multiply(Axis.XN.rotation(105));
                    poseStack.scale(0.2f, 0.2f, 0.2f);
                    poseStack.translate(0.06f, -0.5f, 0f);
                    poseStack.multiply(Axis.ZN.rotation(2.4f));
                }
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        };
        this.addRenderLayer(itemLayer);
        this.addRenderLayer(new SoldierDollLeatherArmorLayer(this));
    }

    @Override
    protected int getBlockLightLevel(SoldierDollEntity entity, BlockPos pos) {
        return entity.isLightBlockUnaffected() ? 15 : super.getBlockLightLevel(entity, pos);
    }

    @Override
    public void render(SoldierDollEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
