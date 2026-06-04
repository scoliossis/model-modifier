package com.scale.modelModifier;

import com.scale.modelModifier.mixins.modelmodifier.ChildPartMapAccessor;
import com.scale.modelModifier.utils.antibot.TargetUtil;
import com.scale.modelModifier.utils.model.LivingEntityInfo;
import com.scale.modelModifier.utils.model.Model;
import com.scale.modelModifier.utils.model.ModelParser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// todo: everything is a mess, comments are MISSING its all gone wrong.
public class Main implements ModInitializer {
    public static ClientPlayerEntity p() {
        return MinecraftClient.getInstance().player;
    }
    public static ClientWorld w() {
        return MinecraftClient.getInstance().world;
    }

    public static LivingEntityInfo lastAccessedModel = null;

    public static final HashMap<String, Model> OVERWRITTEN_MODELS = new HashMap<>();

    public static boolean shouldOverwriteModel() {
        return lastAccessedModel != null
                && !lastAccessedModel.isBot();
    }

    public static String getModelKey(EntityType<?> entityType) {
        return entityType == null ? null : entityType.getUntranslatedName()
                .toLowerCase();
    }

    public static Model getModel(EntityRenderState entityRenderState) {
        return getModel(entityRenderState, null);
    }
    public static Model getModel(EntityRenderState entityRenderState, ModelPart rootModel) {
        lastAccessedModel = null;

        String entityKey = getModelKey(entityRenderState.entityType);
        Model model = TargetUtil.isBot(entityRenderState) ? null : getModel(entityKey);
        if (model == null) return null;

        ModelPart rootPart = rootModel == null && lastAccessedModel != null ? lastAccessedModel.rootPart() : rootModel;

        boolean isHoldingItem = false;
        if (entityRenderState instanceof PlayerEntityRenderState playerEntityRenderState) {
            Entity entity = w().getEntityById(playerEntityRenderState.id);
            if (entity instanceof LivingEntity livingEntity) isHoldingItem = !livingEntity.getStackInArm(Arm.RIGHT).isEmpty();
        }

        lastAccessedModel = new LivingEntityInfo(
                OVERWRITTEN_MODELS.get(entityKey),
                rootPart,
                getModelPartChildren(rootPart),
                TargetUtil.isBot(entityRenderState),
                isHoldingItem
        );
        return model;
    }

    public static Map<String, ModelPart> getModelPartChildren(ModelPart root) {
        if (root == null) return null;

        Map<String, ModelPart> children = ((ChildPartMapAccessor) (Object)root).getChildren();
        for (ModelPart child : children.values().toArray(new ModelPart[0])) {
            // Thinking about the swinging times and all
            // I'm gonna tell my children's children what life is all about
            // And hopefully, they'll have just enough soul to figure it out
            children.putAll(getModelPartChildren(child));
        }
        return children;
    }

    public static Model getModel(String entityKey) {
        if (!OVERWRITTEN_MODELS.containsKey(entityKey)) {
            Model model = ModelParser.getModel(entityKey);
            OVERWRITTEN_MODELS.put(entityKey, model);
        }
        return OVERWRITTEN_MODELS.get(entityKey);
    }

    @Override
    public void onInitialize() {
        // deprecated blehhhh
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public void reload(ResourceManager manager) {
                OVERWRITTEN_MODELS.clear();
                System.out.println("[Model Modifier] we gotta recheck all the models now. take a good second to think about what you've done.");
            }

            @Override
            public Identifier getFabricId() {
                return Identifier.of("modelmodifier", "whydoigottachooseanameforthis");
            }
        });
    }

    public static Identifier getEntityIdentifier(String key, String resource) {
        return Identifier.of("modelmodifier", key+"/"+resource);
    }

    public static Optional<Resource> getEntityResource(String key, String resource) {
        return MinecraftClient.getInstance().getResourceManager().getResource(getEntityIdentifier(key, resource));
    }

    public static boolean isModelPresent(String key) {
        boolean isPresent = getEntityResource(key, "model.obj").isPresent() && getEntityResource(key, "texturemap.png").isPresent();

        System.out.println("[Model Modifier] model for " + key + " has " + (isPresent ? "" : "NOT ") + "been found");
        return isPresent;
    }
}
