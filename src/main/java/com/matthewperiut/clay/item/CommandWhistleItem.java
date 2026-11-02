package com.matthewperiut.clay.item;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
public class CommandWhistleItem extends Item {
 public static java.util.function.Consumer<InteractionHand> openMenu=hand->{};
 public CommandWhistleItem(Properties p){super(p);}
 private static CompoundTag data(ItemStack s){return s.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}
 private static void save(ItemStack s,CompoundTag d){s.set(DataComponents.CUSTOM_DATA,CustomData.of(d));}
 public InteractionResult interactLivingEntity(ItemStack stack,Player p,LivingEntity target,InteractionHand hand){
  if(p.level().isClientSide())return p.isShiftKeyDown()?InteractionResult.SUCCESS:use(p.level(),p,hand);
  if(target instanceof SoldierDollEntity s && p.isShiftKeyDown()){
   var d=data(stack);d.putShort("team",s.getSoldierTeam().getTeamId());save(stack,d);
   p.sendOverlayMessage(Component.translatable("clay.whistle.selected",s.getName()));return InteractionResult.SUCCESS;
  }
  return use(p.level(),p,hand);
 }
 public InteractionResult use(Level level,Player p,InteractionHand hand){
  if(level.isClientSide()){if(!p.isShiftKeyDown())openMenu.accept(hand);return InteractionResult.SUCCESS;}
  return InteractionResult.SUCCESS;
 }
 public static InteractionResult issue(Level level,Player p,InteractionHand hand,int selectedMode){
  if(!(p.getItemInHand(hand).getItem() instanceof CommandWhistleItem)||selectedMode<0||selectedMode>3)return InteractionResult.FAIL;
  var stack=p.getItemInHand(hand);var d=data(stack);int mode=selectedMode;
  d.putInt("mode",mode);save(stack,d);

  if(!d.contains("team")){p.sendOverlayMessage(Component.translatable("clay.whistle.select"));return InteractionResult.SUCCESS;}
  LivingEntity target=null;
  if(mode==1){
   var start=p.getEyePosition();var end=start.add(p.getLookAngle().scale(32));double best=1024;
   var block=level.clip(new net.minecraft.world.level.ClipContext(start,end,net.minecraft.world.level.ClipContext.Block.COLLIDER,net.minecraft.world.level.ClipContext.Fluid.NONE,p));best=start.distanceToSqr(block.getLocation());
   for(var e:level.getEntitiesOfClass(LivingEntity.class,p.getBoundingBox().inflate(32),e->e!=p&&e.isAlive()&&!(e instanceof Player)&&!(e instanceof SoldierDollEntity s&&s.getSoldierTeam().getTeamId()==d.getShort("team").orElse((short)0)))){
    var hit=e.getBoundingBox().inflate(.2).clip(start,end);if(hit.isPresent()&&start.distanceToSqr(hit.get())<best){best=start.distanceToSqr(hit.get());target=e;}
   }
   if(target==null){p.sendOverlayMessage(Component.translatable("clay.whistle.no_target"));return InteractionResult.SUCCESS;}
   target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.GLOWING,40,0));
  }
  int count=0;
  for(var s:level.getEntitiesOfClass(SoldierDollEntity.class,p.getBoundingBox().inflate(32),s->s.isAlive()&&s.getSoldierTeam().getTeamId()==d.getShort("team").orElse((short)0))){s.command(mode,p,target);count++;}
  p.sendOverlayMessage(Component.translatable("clay.whistle.sent",Component.translatable("clay.order."+mode),count));
  p.getCooldowns().addCooldown(stack,10);return InteractionResult.SUCCESS;
 }
 public void appendHoverText(ItemStack s,Item.TooltipContext c,net.minecraft.world.item.component.TooltipDisplay display,java.util.function.Consumer<Component> out,TooltipFlag f){out.accept(Component.translatable("clay.whistle.help"));out.accept(Component.translatable("clay.whistle.mode",Component.translatable("clay.order."+data(s).getInt("mode").orElse(0))));}
}
