package com.scale.modelModifier.mixins.modelmodifier.player;

import com.scale.modelModifier.Main;
import com.scale.modelModifier.utils.model.LivingEntityInfo;
import com.scale.modelModifier.utils.model.Model;
import net.minecraft.client.model.ModelPart;
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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntityRenderer.class)
public abstract class HandlePlayerArmMixin <AvatarlikeEntity extends PlayerLikeEntity>
        extends LivingEntityRenderer<AvatarlikeEntity, PlayerEntityRenderState, PlayerEntityModel> {
    public HandlePlayerArmMixin(EntityRendererFactory.Context ctx, PlayerEntityModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Redirect(method = "renderArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModelPart(Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IILnet/minecraft/client/texture/Sprite;)V"))
    private void onRenderArm(OrderedRenderCommandQueue instance, ModelPart modelPart, MatrixStack matrixStack, RenderLayer renderLayer, int light2, int overlay2, Sprite sprite, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, Identifier skinTexture, ModelPart arm, boolean sleeveVisible) {
        Model handModel = Main.getModel(Main.getModelKey(EntityType.PLAYER));
        Main.lastAccessedModel = null;

        if (handModel == null) {
            queue.submitModelPart(arm, matrices, RenderLayers.entityTranslucent(skinTexture), light, OverlayTexture.DEFAULT_UV, null);
            return;
        }

        Main.lastAccessedModel = new LivingEntityInfo(
                handModel,
                this.model.getRootPart(),
                Main.getModelPartChildren(this.model.getRootPart()),
                false,
                !Main.p().getStackInArm(Arm.RIGHT).isEmpty()
        );

        queue.submitModelPart(arm, matrices, RenderLayers.entityTranslucent(handModel.texture()), light, OverlayTexture.DEFAULT_UV, null);
    }
}
