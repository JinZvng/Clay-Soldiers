package com.matthewperiut.clay.entity;
import net.minecraft.world.entity.animal.allay.Allay;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
/** Suspend the native collection brain only while a soldier is issuing flight orders. */
public final class AllayPilot {
 public static void tick(EntityTickEvent.Pre event) {
  if(event.getEntity() instanceof Allay allay && !allay.level().isClientSide()) update(allay);
 }
 public static void update(Allay allay) {
  var data=allay.getPersistentData();
  boolean controlled=allay.getPassengers().stream().anyMatch(p->p instanceof SoldierDollEntity s && s.isAlive() && s.orderMode!=3);
  if(controlled){
   if(!data.contains("ClayPilotPreviousAI")) {
    data.putBoolean("ClayPilotPreviousAI",allay.isNoAi());
    data.putBoolean("ClayPilotPreviousGravity",allay.isNoGravity());
   }
   allay.setNoAi(true);allay.setNoGravity(true);
  }else if(data.contains("ClayPilotPreviousAI")){
   allay.setNoGravity(data.getBoolean("ClayPilotPreviousGravity").orElse(false));data.remove("ClayPilotPreviousGravity");
   allay.setNoAi(data.getBoolean("ClayPilotPreviousAI").orElse(false));data.remove("ClayPilotPreviousAI");
  }
 }
}
