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
    private static volatile boolean serverChecked;

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
        if (serverFailure != null) throw new IllegalStateException(serverFailure);
        if (phase == 0) {
            if (mc.screen != null && mc.screen.getClass().getSimpleName().equals("AccessibilityOnboardingScreen")) {
                mc.setScreenAndShow(new TitleScreen());
            }
            if (mc.screen instanceof TitleScreen && ++ticks >= 20) {
                mc.options.renderDistance().set(2);
                mc.options.simulationDistance().set(5);
                phase = 1;
                CreateWorldScreen.openFresh(mc, () -> mc.setScreenAndShow(new TitleScreen()));
            }
            return;
        }
        if (phase == 1) {
            if (!(mc.screen instanceof CreateWorldScreen screen)) return;
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
                    serverChecked = true;
                } catch (Throwable failure) { serverFailure = failure.toString(); }
            });
            return;
        }
        if (phase == 4 && serverChecked && ++ticks >= 30) {
            phase = 100;
            ClayMod.LOGGER.info("CLAY_GAMEPLAY_SMOKE_OK: world created, soldier and horse rendered, mounted client death handled, server death/drop verified, connection preserved");
        }
    }
}
