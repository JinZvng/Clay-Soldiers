package com.matthewperiut.clay.neoforge;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import com.matthewperiut.clay.network.payload.WhistlePayload;
public final class WhistleScreen extends Screen {
 private final InteractionHand hand;
 public WhistleScreen(InteractionHand hand){super(Component.translatable("item.clay.command_whistle"));this.hand=hand;}
 protected void init(){
  for(int i=0;i<4;i++){final int mode=i;
   addRenderableWidget(Button.builder(Component.translatable("clay.order."+i),button->{
    ClientPacketDistributor.sendToServer(new WhistlePayload(mode,hand==InteractionHand.OFF_HAND));onClose();
   }).bounds(width/2-100,height/2-55+i*27,200,22).build());
  }
 }
 public boolean isPauseScreen(){return false;}
}
