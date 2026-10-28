package com.shadow.crystaltest;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

/**
 * Passive diagnostic mod. Records local input/target observations only.
 * It does not automate attacks, send packets, or claim that the server accepted an action.
 */
public final class CrystalPvpTestClient implements ClientModInitializer {
    private Path logFile;
    private long lastNoticeMillis;

    @Override
    public void onInitializeClient() {
        Path dir = FabricLoader.getInstance().getConfigDir().resolve("crystal-test-client");
        logFile = dir.resolve("events.log");
        try {
            Files.createDirectories(dir);
            log("session_start", "Client diagnostic logger started; events are local observations only.");
        } catch (IOException e) {
            System.err.println("[Crystal PvP Test Client] Could not initialize log: " + e.getMessage());
        }

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.currentScreen != null) return;

        // A use-key press while holding an End Crystal and aiming at a block is only
        // an observed placement attempt; server acceptance cannot be verified client-side.
        while (client.options.useKey.wasPressed()) {
            if (client.player.getMainHandStack().isOf(Items.END_CRYSTAL)
                    && client.crosshairTarget instanceof BlockHitResult hit) {
                log("crystal_use_attempt", "block=" + hit.getBlockPos()
                        + ", side=" + hit.getSide()
                        + ", dimension=" + client.world.getRegistryKey().getValue());
                notifyPlayer(client, "[Crystal Test] Crystal use attempt logged");
            }
        }

        // Log a normal attack input only when the crosshair is over an End Crystal.
        while (client.options.attackKey.wasPressed()) {
            if (client.crosshairTarget instanceof EntityHitResult hit
                    && hit.getEntity() instanceof EndCrystalEntity crystal) {
                log("crystal_attack_attempt", "entity_id=" + crystal.getId()
                        + ", pos=" + crystal.getBlockPos()
                        + ", dimension=" + client.world.getRegistryKey().getValue());
                notifyPlayer(client, "[Crystal Test] Crystal attack attempt logged");
            }
        }
    }

    private void notifyPlayer(MinecraftClient client, String message) {
        long now = System.currentTimeMillis();
        if (now - lastNoticeMillis < 1200) return;
        lastNoticeMillis = now;
        if (client.player != null) client.player.sendMessage(net.minecraft.text.Text.literal(message), true);
    }

    private void log(String event, String details) {
        if (logFile == null) return;
        String safeDetails = details.replace('\n', ' ').replace('\r', ' ');
        String line = Instant.now() + " | " + event + " | " + safeDetails + System.lineSeparator();
        try {
            Files.writeString(logFile, line, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("[Crystal PvP Test Client] Could not write log: " + e.getMessage());
        }
    }
}
