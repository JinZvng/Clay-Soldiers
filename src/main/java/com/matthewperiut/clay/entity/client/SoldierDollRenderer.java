package com.matthewperiut.clay.entity.client;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.matthewperiut.clay.entity.client.layer.SoldierDollLeatherArmorLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer;
import com.geckolib.cache.model.GeoBone;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import static com.matthewperiut.clay.registry.UpgradeRegistry.*;
public final class SoldierDollRenderer extends GeoEntityRenderer<SoldierDollEntity, ClayRenderState> {
    public SoldierDollRenderer(EntityRendererProvider.Context context, Identifier texture) {
        super(context, new SoldierDollModel(texture));
        this.shadowRadius = 0.1f;
        this.renderLayers.addLayer(new SoldierDollLeatherArmorLayer(this));
        this.renderLayers.addLayer(new BlockAndItemGeoLayer<SoldierDollEntity, Void, ClayRenderState>(context, this) {
            @Override protected List<RenderData> getRelevantBones(SoldierDollEntity entity, Void ignored, ClayRenderState state, float tick) {
                ItemStackRenderState main = new ItemStackRenderState();
                ItemStackRenderState off = new ItemStackRenderState();
                itemModelResolver.updateForLiving(main, entity.getMainHandItem(), ItemDisplayContext.THIRD_PERSON_LEFT_HAND, entity);
                itemModelResolver.updateForLiving(off, entity.getOffhandItem(), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, entity);
                return List.of(RenderData.item("lhand", ItemDisplayContext.THIRD_PERSON_LEFT_HAND, main), RenderData.item("rhand", ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, off));
            }
            @Override public void addRenderData(SoldierDollEntity entity, Void ignored, ClayRenderState state, float tick) {
                state.addGeckolibData(CONTENTS, getRelevantBones(entity, ignored, state, tick));
            }
            @Override protected void submitItemStackRender(PoseStack pose, GeoBone bone, ItemStackRenderState stack, ItemDisplayContext display, ClayRenderState state, SubmitNodeCollector tasks, int light) {
                pose.pushPose();
                pose.rotate(Axis.XN.rotation(105));
                pose.scale(0.2f, 0.2f, 0.2f);
                pose.translate(0.06f, -0.5f, 0f);
                pose.rotate(Axis.ZN.rotation(2.4f));
                super.submitItemStackRender(pose, bone, stack, display, state, tasks, light);
                pose.popPose();
            }
        });
    }
    @Override public ClayRenderState createRenderState(SoldierDollEntity entity, Void ignored) { return new ClayRenderState(); }
    @Override public void addRenderData(SoldierDollEntity entity, Void ignored, ClayRenderState state, float tick) {
        super.addRenderData(entity, ignored, state, tick);
        state.addGeckolibData(SoldierDollEntity.RIDING, entity.isPassenger());
        state.leather = entity.upgrades.contains(LEATHER_UPGRADE.get());
        state.wool = entity.upgrades.contains(WOOL_UPGRADE.get());
    }
    @Override protected int getBlockLightLevel(SoldierDollEntity entity, BlockPos pos) {
        return entity.isLightBlockUnaffected() ? 15 : super.getBlockLightLevel(entity, pos);
    }
}
