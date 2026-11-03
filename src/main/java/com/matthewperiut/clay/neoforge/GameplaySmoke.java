package com.matthewperiut.clay.neoforge;

import com.matthewperiut.clay.ClayMod;
import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.matthewperiut.clay.entity.horse.HorseDollEntity;
import com.matthewperiut.clay.registry.EntityTypeRegistry;
import com.matthewperiut.clay.upgrade.UpgradeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Opt-in development integration test. Never enabled by a production launch. */
public final class GameplaySmoke {
    private static final boolean ENABLED = Boolean.getBoolean("clay.gameplaySmoke");
    private static final Set<Integer> rendered = ConcurrentHashMap.newKeySet();
    private static int phase, ticks;
    private static volatile int soldierId = -1, horseId = -1;
    private static volatile String serverFailure;
    private static volatile boolean serverChecked, menuChecked;
    private static volatile int toySoldierId=-1,allayId=-1;

    public static void rendered(int id) { if (ENABLED) rendered.add(id); }

    public static void tick(ClientTickEvent.Post event) {
        if (!ENABLED || phase == 99 || phase == 100) return;
        try {
            advance(Minecraft.getInstance());
        } catch (Throwable failure) {
            phase = 99;
            ClayMod.LOGGER.error("CLAY_GAMEPLAY_SMOKE_FAILED", failure);
        }
    }

    private static void advance(Minecraft mc) {
        if(ToyBehaviorProbe.failure!=null)throw new IllegalStateException(ToyBehaviorProbe.failure);
        if (serverFailure != null) throw new IllegalStateException(serverFailure);
        if (phase == 0) {
            if (mc.gui.screen() != null && mc.gui.screen().getClass().getSimpleName().equals("AccessibilityOnboardingScreen")) {
                mc.setScreenAndShow(new TitleScreen());
            }
            if (mc.gui.screen() instanceof TitleScreen && ++ticks >= 20) {
                mc.options.pauseOnLostFocus = false;
                mc.options.renderDistance().set(2);
                mc.options.simulationDistance().set(5);
                phase = 1;
                CreateWorldScreen.openFresh(mc, () -> mc.setScreenAndShow(new TitleScreen()));
            }
            return;
        }
        if (phase == 1) {
            if (!(mc.gui.screen() instanceof CreateWorldScreen screen)) return;
            var state = screen.getUiState();
            state.setName("Clay Smoke Test");
            state.setSeed("1");
            state.setGameMode(WorldCreationUiState.SelectedGameMode.CREATIVE);
            state.setDifficulty(Difficulty.PEACEFUL);
            state.setGenerateStructures(false);
            state.getNormalPresetList().stream().filter(entry -> entry.preset() != null && entry.preset().is(WorldPresets.FLAT)).findFirst().ifPresent(state::setWorldType);
            for (var child : screen.children()) {
                if (child instanceof Button button && button.getMessage().getString().equals("Create New World")) {
                    phase = 2;
                    ticks = 0;
                    button.onPress(new KeyEvent(257, 0, 0));
                    return;
                }
            }
            return;
        }
        if (phase == 2) {
            if (mc.level == null || mc.player == null || mc.getSingleplayerServer() == null || ++ticks < 40) return;
            mc.setScreenAndShow(new WhistleScreen(net.minecraft.world.InteractionHand.MAIN_HAND));
            if(mc.gui.screen().children().stream().filter(c->c instanceof Button).count()!=4)throw new IllegalStateException("Whistle menu did not expose four orders");
            phase = 3;
            ticks = 0;
            var server = mc.getSingleplayerServer();
            server.execute(() -> {
                try {
                    var player = server.getPlayerList().getPlayers().getFirst();
                    var level = server.overworld();
                    var pos = player.position().add(player.getLookAngle().scale(3));
                    var horse = EntityTypeRegistry.DIRT_HORSE.get().create(level, EntitySpawnReason.COMMAND);
                    var soldier = EntityTypeRegistry.CLAY_SOLDIER.get().create(level, EntitySpawnReason.COMMAND);
                    if (horse == null || soldier == null) throw new IllegalStateException("Could not create Clay entities");
                    horse.setPos(pos.x, player.getY() + 0.1, pos.z);
                    soldier.setPos(horse.position());
                    horse.setNoAi(true);
                    soldier.setNoAi(true);
                    level.addFreshEntity(horse);
                    level.addFreshEntity(soldier);
                    UpgradeManager.INSTANCE.applyUpdate(soldier, new ItemStack(Items.GLOWSTONE_DUST));
                    if (!soldier.startRiding(horse)) throw new IllegalStateException("Soldier could not mount horse");
                    horseId = horse.getId();
                    soldierId = soldier.getId();
                } catch (Throwable failure) { serverFailure = failure.toString(); }
            });
            return;
        }
        if (mc.level == null || mc.player == null) throw new IllegalStateException("Disconnected from the integrated test world");
        if (phase == 3) {
            if (!(mc.level.getEntity(soldierId) instanceof SoldierDollEntity soldier)
                    || !(mc.level.getEntity(horseId) instanceof HorseDollEntity horse)
                    || !soldier.isPassenger() || !rendered.contains(soldierId) || !rendered.contains(horseId)) return;
            if (++ticks < 20) return;
            mc.setScreenAndShow(null);
            var area = soldier.getBoundingBox().inflate(8);
            int items = mc.level.getEntitiesOfClass(ItemEntity.class, area).size();
            // Same client death-event callback as ClientboundEntityEventPacket.
            // The entity is mounted in an actual ClientLevel at this point.
            soldier.handleEntityEvent((byte) 3);
            if (!horse.isAlive()) throw new IllegalStateException("Client death callback killed the mount");
            horse.die(mc.level.damageSources().lava());
            if (mc.level.getEntitiesOfClass(ItemEntity.class, area).size() != items) throw new IllegalStateException("Death callback spawned client-side drops");
            phase = 4;
            ticks = 0;
            var server = mc.getSingleplayerServer();
            server.execute(() -> {
                try {
                    var level = server.overworld();
                    var actor = level.getEntity(soldierId);
                    var mount = level.getEntity(horseId);
                    if (actor == null || mount == null) throw new IllegalStateException("Missing server test entities");
                    var bounds = actor.getBoundingBox().inflate(8);
                    actor.kill(level);
                    if (actor.isAlive() || mount.isAlive()) throw new IllegalStateException("Server did not apply soldier and mount deaths");
                    int drops = level.getEntitiesOfClass(ItemEntity.class, bounds, item -> item.getItem().is(Items.GLOWSTONE_DUST)).stream().mapToInt(item -> item.getItem().getCount()).sum();
                    if (drops != 1) throw new IllegalStateException("Expected one authoritative upgrade drop, got " + drops);
                    testToyFeatures(level, server.getPlayerList().getPlayers().getFirst());
                    serverChecked = true;
                } catch (Throwable failure) { serverFailure = failure.toString(); }
            });
            return;
        }
        if (phase == 4 && serverChecked) {
            if(!(mc.level.getEntity(toySoldierId) instanceof SoldierDollEntity toy)||!rendered.contains(toySoldierId)||mc.level.getEntity(allayId)==null||!toy.hasToy("bow")||!toy.hasToy("shield")||!toy.hasToy("copper")||!mc.player.getMainHandItem().is(com.matthewperiut.clay.registry.ItemRegistry.COMMAND_WHISTLE.get()))return;
            mc.setScreenAndShow(new WhistleScreen(net.minecraft.world.InteractionHand.MAIN_HAND));
            var button=(Button)mc.gui.screen().children().getFirst();
            button.onPress(new KeyEvent(257,0,0)); // Real menu -> client packet -> server handler.
            phase=5;ticks=0;return;
        }
        if(phase==5 && ++ticks==20){
            mc.getSingleplayerServer().execute(()->{try{
                var level=mc.getSingleplayerServer().overworld();
                var toy=(SoldierDollEntity)level.getEntity(toySoldierId);
                var allay=level.getEntity(allayId);
                if(toy.orderMode!=0)throw new IllegalStateException("Whistle menu packet did not issue follow order");
                toy.kill(level);
                if(!allay.isAlive())throw new IllegalStateException("Soldier death killed allay");
                ToyBehaviorProbe.start(mc.getSingleplayerServer().getPlayerList().getPlayers().getFirst());
                menuChecked=true;
            }catch(Throwable failure){serverFailure=failure.toString();}});
        }
        if(phase==5 && menuChecked && ToyBehaviorProbe.done && ticks>40){
            phase=100;
            ClayMod.LOGGER.info("CLAY_GAMEPLAY_SMOKE_OK: world, mounted deaths, allay flight, upgraded soldier rendering, save/load and actual whistle menu packet passed");
        }
    }

    private static void testToyFeatures(net.minecraft.server.level.ServerLevel level, net.minecraft.server.level.ServerPlayer player) {
        var s=EntityTypeRegistry.BLUE_SOLDIER.get().create(level,EntitySpawnReason.COMMAND);
        var allay=net.minecraft.world.entity.EntityTypes.ALLAY.create(level,EntitySpawnReason.COMMAND);
        s.setPos(player.position().add(player.getLookAngle().scale(3)).add(0,.1,0));allay.setPos(s.position());
        level.addFreshEntity(s);level.addFreshEntity(allay);
        var control=new ItemStack(com.matthewperiut.clay.registry.ItemRegistry.COMMAND_WHISTLE.get());
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,control);
        var whistleItem=(com.matthewperiut.clay.item.CommandWhistleItem)control.getItem();
        whistleItem.interactLivingEntity(control,player,s,net.minecraft.world.InteractionHand.MAIN_HAND);
        whistleItem.interactLivingEntity(control,player,allay,net.minecraft.world.InteractionHand.MAIN_HAND);
        if(s.getVehicle()!=allay)throw new IllegalStateException("Direct whistle selection/mount interaction failed");
        var beforeFlight=allay.position();
        s.command(0,player,null);com.matthewperiut.clay.entity.AllayPilot.update(allay);s.tickCommand();
        if(!allay.isNoAi())throw new IllegalStateException("Native allay brain still competing with pilot");
        if(allay.position().distanceToSqr(beforeFlight)==0)throw new IllegalStateException("Follow command did not pilot allay");
        s.command(2,player,null);s.tickCommand();
        var manager=UpgradeManager.INSTANCE;
        for(var item:java.util.List.of(Items.BOW,Items.SHIELD,Items.COPPER_INGOT,Items.FEATHER))manager.applyUpdate(s,new ItemStack(item));
        for(var name:java.util.List.of("bow","shield","copper","feather"))if(!s.hasToy(name))throw new IllegalStateException("Upgrade missing: "+name);
        if(manager.canApplyUpdate(s,new ItemStack(Items.STICK))||manager.canApplyUpdate(s,new ItemStack(Items.LEATHER))||manager.canApplyUpdate(s,new ItemStack(Items.SLIME_BALL)))throw new IllegalStateException("Conflicting upgrades accepted");
        if(s.hurtServer(level,level.damageSources().fall(),100))throw new IllegalStateException("Feather did not prevent fall damage");
        var target=net.minecraft.world.entity.EntityTypes.PIG.create(level,EntitySpawnReason.COMMAND);
        target.setPos(s.position().add(3,0,0));level.addFreshEntity(target);

        whistleItem.interactLivingEntity(control,player,target,net.minecraft.world.InteractionHand.MAIN_HAND);s.tickCommand();
        if(s.getTarget()!=target)throw new IllegalStateException("Attack command lost explicit target");
        var saved=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess());
        s.saveWithoutId(saved);
        var restored=EntityTypeRegistry.BLUE_SOLDIER.get().create(level,EntitySpawnReason.COMMAND);
        restored.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess(),saved.buildResult()));
        if(!restored.hasToy("bow")||!restored.hasToy("feather")||restored.orderMode!=2)throw new IllegalStateException("Saved upgrades/orders did not restore");
        restored.discard();
        if(level.getEntitiesOfClass(net.minecraft.world.entity.projectile.arrow.Arrow.class,s.getBoundingBox().inflate(12)).isEmpty())throw new IllegalStateException("Bow attack did not spawn an arrow");
        s.command(3,player,null);com.matthewperiut.clay.entity.AllayPilot.update(allay);
        if(allay.isNoAi())throw new IllegalStateException("Allay AI was not restored on release");
        if(s.orderMode!=3||s.getTarget()!=null)throw new IllegalStateException("Release command failed");
        target.discard();s.command(2,player,null);
        toySoldierId=s.getId();allayId=allay.getId();
        var whistle=new ItemStack(com.matthewperiut.clay.registry.ItemRegistry.COMMAND_WHISTLE.get());
        var tag=new net.minecraft.nbt.CompoundTag();tag.putShort("team",s.getSoldierTeam().getTeamId());
        whistle.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,net.minecraft.world.item.component.CustomData.of(tag));
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,whistle);
        ClayMod.LOGGER.info("CLAY_TOY_FEATURES_OK: allay mount, flight orders, attack, stay, release, material upgrades, conflicts and fall protection");
    }

}
