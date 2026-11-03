package com.matthewperiut.clay.neoforge;

import com.matthewperiut.clay.item.CommandWhistleItem;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.matthewperiut.clay.network.payload.WhistlePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public final class WhistleScreen extends Screen {
    private final InteractionHand hand;
    private int selectedTeam;
    private StringWidget selection;
    public WhistleScreen(InteractionHand hand) {
        super(Component.translatable("item.clay.command_whistle"));
        this.hand=hand;
        var mc=Minecraft.getInstance();
        selectedTeam=mc.player==null?-1:CommandWhistleItem.selectedTeam(mc.player.getItemInHand(hand));
        if(selectedTeam<0&&mc.level!=null&&mc.player!=null) {
            var nearby=mc.level.getEntitiesOfClass(SoldierDollEntity.class,mc.player.getBoundingBox().inflate(32),SoldierDollEntity::isAlive);
            selectedTeam=nearby.stream().min(java.util.Comparator.comparingDouble(mc.player::distanceToSqr)).map(s->(int)s.getSoldierTeam().getTeamId()).orElse(0);
        }
        if(selectedTeam<0||selectedTeam>15)selectedTeam=0;
    }
    @Override protected void init() {
        addRenderableWidget(new StringWidget(width/2-140,height/2-110,280,20,title,font));
        selection=addRenderableWidget(new StringWidget(width/2-100,height/2-82,200,20,selectionText(),font));
        for(int i=0;i<4;i++) {
            final int mode=i;
            addRenderableWidget(Button.builder(Component.translatable("clay.order."+i),button->{
                ClientPacketDistributor.sendToServer(new WhistlePayload(mode,hand==InteractionHand.OFF_HAND,selectedTeam));
                onClose();
            }).tooltip(Tooltip.create(Component.translatable("clay.order.help."+i))).bounds(width/2-100,height/2-45+i*27,200,22).build());
        }
        addRenderableWidget(Button.builder(Component.literal("◀"),button->changeColor(-1)).bounds(width/2-134,height/2-82,26,20).build());
        addRenderableWidget(Button.builder(Component.literal("▶"),button->changeColor(1)).bounds(width/2+108,height/2-82,26,20).build());
        addRenderableWidget(new StringWidget(width/2-160,height/2+72,320,20,Component.translatable("clay.whistle.menu_hint"),font));
    }
    private Component selectionText(){return Component.translatable("clay.whistle.color",CommandWhistleItem.teamName(selectedTeam));}
    private void changeColor(int direction){selectedTeam=Math.floorMod(selectedTeam+direction,16);selection.setMessage(selectionText());}
    public int selectedTeam(){return selectedTeam;}
    @Override public boolean isPauseScreen(){return false;}
}
