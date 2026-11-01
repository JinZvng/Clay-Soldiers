package com.matthewperiut.clay.entity.client.layer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.layer.GeoRenderLayer;

import static com.matthewperiut.clay.registry.UpgradeRegistry.LEATHER_UPGRADE;
import static com.matthewperiut.clay.registry.UpgradeRegistry.WOOL_UPGRADE;

public class SoldierDollLeatherArmorLayer extends GeoRenderLayer<SoldierDollEntity> {
    static final Identifier SHELL_TEXTURE = Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "textures/entity/upgrade/tiny_chestplate.png");
    static final Identifier UPGRADED_TEXTURE = Identifier.fromNamespaceAndPath(ClayMod.MOD_ID, "textures/entity/upgrade/tiny_chestplate_with_wool.png");
    static final RenderType BASE_ARMOR_RENDER_TYPE = RenderType.armorCutoutNoCull(SHELL_TEXTURE);
    static final RenderType UPGRADED_ARMOR_RENDER_TYPE = RenderType.armorCutoutNoCull(UPGRADED_TEXTURE);


    public SoldierDollLeatherArmorLayer(GeoRenderer<SoldierDollEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, SoldierDollEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!animatable.upgrades.contains(LEATHER_UPGRADE.get())) return;

        RenderType renderLayer = BASE_ARMOR_RENDER_TYPE;

        if (animatable.upgrades.contains(WOOL_UPGRADE.get())) renderLayer = UPGRADED_ARMOR_RENDER_TYPE;

        super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, renderLayer,
                bufferSource.getBuffer(renderLayer), partialTick, packedLight, OverlayTexture.NO_OVERLAY,
                1);
    }
}
