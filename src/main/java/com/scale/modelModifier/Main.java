package com.scale.modelModifier;

import com.scale.modelModifier.utils.model.Model;
import com.scale.modelModifier.utils.model.ModelPartName;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Optional;

public class Main implements ModInitializer {
    public static final File CONFIG_FILE = new File("modelmodifier/enabled.json");

    public static ClientPlayerEntity p() {
        return MinecraftClient.getInstance().player;
    }
    public static ClientWorld w() {
        return MinecraftClient.getInstance().world;
    }

    public static final HashMap<Integer, ModelPartName> MODEL_PART_NAME_MAP = new HashMap<>();

    // this should be somewhere else ig.
    public static boolean isLastPlayerBot = false;
    public static PlayerEntity currentlyRenderingEntity = null;
    public static Vec3d heldItemOffsetOverwrite = Vec3d.ZERO;

    public static Model model;

    public static boolean isEnabled() {
        return model != null && !Main.isLastPlayerBot;
    }

    @Override
    public void onInitialize() {
        // fabric event bus, how cool, we gotta load the model here otherwise mc.getResourceManager() is null
        ClientLifecycleEvents.CLIENT_STARTED.register((MinecraftClient minecraft) -> {
            model = Model.getModel("rayman");

            if (!CONFIG_FILE.exists()) {
                new File(CONFIG_FILE.getParent()).mkdirs();
                System.out.println("[Model Modifier] model set to: " + model.name() + " by default.");
            }
            else {
                try {
                    model = Model.getModel(Files.readString(CONFIG_FILE.toPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static Optional<Resource> getResource(String identifierName) {
        return MinecraftClient.getInstance().getResourceManager().getResource(Identifier.of("modelmodifier", identifierName));
    }

    public static boolean isModelPresent(String modelName) {
        return getResource(modelName+"/model.obj").isPresent()
                && getResource(modelName+"/texturemap.png").isPresent()
                && getResource(modelName+"/helditemoffset.txt").isPresent();
    }

    public static Vec3d getHeldItemOffset() {
        return heldItemOffsetOverwrite == null ? model.heldItemOffset() : heldItemOffsetOverwrite;
    }
}
