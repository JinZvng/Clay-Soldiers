package com.matthewperiut.clay.neoforge;

import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.matthewperiut.clay.registry.EntityTypeRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** Tests real simulation ticks rather than invoking command methods once. Opt-in CI only. */
public final class ToyBehaviorProbe {
    private static ServerPlayer player;
    private static SoldierDollEntity flyer, walker, archer, horseRider;
    private static com.matthewperiut.clay.entity.horse.HorseDollEntity horse;
    private static Allay allay;
    private static Pig target, flightTarget;
    private static Vec3 hold;
    private static double startDistance;
    private static int ticks;
    public static volatile boolean done;
    public static volatile String failure;

    public static void start(ServerPlayer p) {
        player=p;
        var level=p.level();
        allay=EntityTypes.ALLAY.create(level,EntitySpawnReason.COMMAND);
        allay.setPos(p.position().add(10,2,0));allay.setNoAi(true);
        flyer=EntityTypeRegistry.RED_SOLDIER.get().create(level,EntitySpawnReason.COMMAND);
        flyer.setNoAi(true);flyer.setPos(allay.position());
        flyer.command(0,p,null);
        level.addFreshEntity(allay);level.addFreshEntity(flyer);
        startDistance=allay.distanceToSqr(p);
        walker=EntityTypeRegistry.RED_SOLDIER.get().create(level,EntitySpawnReason.COMMAND);
        walker.setPos(p.position().add(-8,.1,0));walker.command(0,p,null);level.addFreshEntity(walker);
        archer=EntityTypeRegistry.RED_SOLDIER.get().create(level,EntitySpawnReason.COMMAND);
        archer.setPos(p.position().add(0,.1,6));archer.command(2,p,null);level.addFreshEntity(archer);
        for(var item:java.util.List.of(Items.BOW,Items.FEATHER)) {
            var drop=new ItemEntity(level,archer.getX(),archer.getY(),archer.getZ(),new ItemStack(item));
            drop.setNoPickUpDelay();level.addFreshEntity(drop);
        }
        target=EntityTypes.PIG.create(level,EntitySpawnReason.COMMAND);
        target.setPos(archer.position().add(0,0,4));target.setNoAi(true);level.addFreshEntity(target);
        horse=EntityTypeRegistry.DIRT_HORSE.get().create(level,EntitySpawnReason.COMMAND);
        horse.setPos(p.position().add(-9,.1,-5));level.addFreshEntity(horse);
        horseRider=EntityTypeRegistry.RED_SOLDIER.get().create(level,EntitySpawnReason.COMMAND);
        horseRider.setPos(horse.position());level.addFreshEntity(horseRider);horseRider.startRiding(horse);horseRider.command(0,p,null);
        ClayMod.LOGGER.info("CLAY_BEHAVIOR_PROBE_STARTED");
    }
    public static void tick(ServerTickEvent.Post event) {
        if(player==null||done||failure!=null)return;
        try {
            ticks++;
            if(ticks==20) {
                check(archer.hasToy("bow")&&archer.hasToy("feather"),"Dropped material pickup failed while staying");
                archer.command(1,player,target);
            }
            if(ticks==100) {
                check(flyer.getVehicle()==allay,"Automatic nearby allay mounting failed");
                check(allay.distanceToSqr(player)<startDistance-20,"Allay did not physically follow over 100 ticks: start="+startDistance+", end="+allay.distanceToSqr(player)+", pos="+allay.position());
                check(horse.distanceToSqr(player)<36,"Horse-mounted soldier did not follow over 100 ticks");
                horseRider.command(2,player,null);
                check(walker.distanceToSqr(player)<36,"Ground soldier did not follow over 100 ticks");
                check(target.getHealth()<target.getMaxHealth(),"Bow arrows did not damage target over 80 ticks");
                flyer.command(2,player,null);hold=allay.position();
                walker.command(2,player,null);archer.command(2,player,null);
            }
            if(ticks==180) {
                check(allay.position().distanceToSqr(hold)<.25,"Stay order drifted or changed allay altitude");
                flightTarget=EntityTypes.PIG.create(player.level(),EntitySpawnReason.COMMAND);
                flightTarget.setPos(player.position().add(5,0,5));flightTarget.setNoAi(true);player.level().addFreshEntity(flightTarget);
                flyer.command(1,player,flightTarget);
            }
            if(ticks==280) {
                check(flightTarget.getHealth()<flightTarget.getMaxHealth(),"Mounted melee attack did not damage target over 100 ticks");
                done=true;
                ClayMod.LOGGER.info("CLAY_SUSTAINED_BEHAVIOR_OK: automatic mount, physical flying/ground follow, dropped upgrades, arrow hits, stable hover and mounted melee across 280 server ticks");
                horseRider.discard();horse.discard();flyer.discard();walker.discard();archer.discard();allay.discard();target.discard();flightTarget.discard();
            }
        }catch(Throwable error){failure=error.toString();ClayMod.LOGGER.error("CLAY_GAMEPLAY_SMOKE_FAILED",error);}
    }
    private static void check(boolean condition,String message){if(!condition)throw new IllegalStateException(message);}
}
