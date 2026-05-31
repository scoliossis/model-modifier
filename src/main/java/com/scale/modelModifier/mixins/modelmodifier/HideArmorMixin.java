package com.scale.modelModifier.mixins.modelmodifier;

import com.scale.modelModifier.Main;
import com.scale.modelModifier.utils.model.ModelPartName;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class HideArmorMixin<S extends BipedEntityRenderState, M extends BipedEntityModel<S>> extends FeatureRenderer<S, M> {
    @Shadow
    protected abstract void renderArmor(MatrixStack matrices, OrderedRenderCommandQueue queue, ItemStack stack, EquipmentSlot slot, int light, S state);

    public HideArmorMixin(FeatureRendererContext<S, M> context) {
        super(context);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, S bipedEntityRenderState, float f, float g, CallbackInfo ci) {
        if (bipedEntityRenderState.entityType != EntityType.PLAYER || !Main.isEnabled()) return;

        // ONLY show the helmet, cause i like it, and only show it if the model has a head
        if (Main.model.faces().containsKey(ModelPartName.HEAD.name)) {
            this.renderArmor(matrixStack, orderedRenderCommandQueue, bipedEntityRenderState.equippedHeadStack, EquipmentSlot.HEAD, i, bipedEntityRenderState);
        }
        ci.cancel();
    }
}