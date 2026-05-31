package com.scale.modelModifier.mixins.modelmodifier;

import com.scale.modelModifier.Main;
import com.scale.modelModifier.utils.model.ModelPartName;
import com.scale.modelModifier.utils.antibot.TargetUtil;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class OverwritePlayerModelMixin<AvatarlikeEntity extends PlayerLikeEntity & ClientPlayerLikeEntity>
        extends LivingEntityRenderer<AvatarlikeEntity, PlayerEntityRenderState, PlayerEntityModel> {

    public OverwritePlayerModelMixin(EntityRendererFactory.Context ctx, PlayerEntityModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void PlayerEntityRenderer(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        // i do wanna see these layers!!! gogogo
        Main.MODEL_PART_NAME_MAP.put(model.head.hashCode(), ModelPartName.HEAD);
        Main.MODEL_PART_NAME_MAP.put(model.body.hashCode(), ModelPartName.BODY);
        Main.MODEL_PART_NAME_MAP.put(model.leftArm.hashCode(), ModelPartName.LEFT_HAND);
        Main.MODEL_PART_NAME_MAP.put(model.rightArm.hashCode(), ModelPartName.RIGHT_HAND);
        Main.MODEL_PART_NAME_MAP.put(model.leftLeg.hashCode(), ModelPartName.LEFT_FOOT);
        Main.MODEL_PART_NAME_MAP.put(model.rightLeg.hashCode(), ModelPartName.RIGHT_FOOT);

        // i dont wanna see these stupid layers bore blehhhhh
        Main.MODEL_PART_NAME_MAP.put(model.hat.hashCode(), ModelPartName.HIDDEN);
        Main.MODEL_PART_NAME_MAP.put(model.jacket.hashCode(), ModelPartName.HIDDEN);
        Main.MODEL_PART_NAME_MAP.put(model.leftSleeve.hashCode(), ModelPartName.HIDDEN);
        Main.MODEL_PART_NAME_MAP.put(model.rightSleeve.hashCode(), ModelPartName.HIDDEN);
        Main.MODEL_PART_NAME_MAP.put(model.leftPants.hashCode(), ModelPartName.HIDDEN);
        Main.MODEL_PART_NAME_MAP.put(model.rightPants.hashCode(), ModelPartName.HIDDEN);
    }

    @Inject(method = "getTexture(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)Lnet/minecraft/util/Identifier;", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(PlayerEntityRenderState playerEntityRenderState, CallbackInfoReturnable<Identifier> cir) {
        Entity entity = Main.w().getEntityById(playerEntityRenderState.id);
        Main.isLastPlayerBot = TargetUtil.isBot(entity);
        Main.currentlyRenderingEntity = (PlayerEntity) entity;
        if (!Main.isEnabled() || entity == null) return;

        cir.setReturnValue(Main.model.texture());
    }

    @Redirect(method = "renderArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModelPart(Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IILnet/minecraft/client/texture/Sprite;)V"))
    private void onRenderArm(OrderedRenderCommandQueue instance, ModelPart modelPart, MatrixStack matrixStack, RenderLayer renderLayer, int light2, int overlay2, Sprite sprite, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, Identifier skinTexture, ModelPart arm, boolean sleeveVisible) {
        Main.isLastPlayerBot = false;
        if (!Main.isEnabled()) {
            queue.submitModelPart(arm, matrices, RenderLayers.entityTranslucent(skinTexture), light, OverlayTexture.DEFAULT_UV, null);
            return;
        }

        Main.currentlyRenderingEntity = Main.p();
        queue.submitModelPart(arm, matrices, RenderLayers.entityTranslucent(Main.model.texture()), light, OverlayTexture.DEFAULT_UV, null);
    }
}
