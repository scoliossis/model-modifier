package com.scale.modelModifier.mixins.modelmodifier;

import com.scale.modelModifier.Main;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeldItemFeatureRenderer.class)
public abstract class MoveHeldItemMixin<S extends ArmedEntityRenderState, M extends EntityModel<S>>
        extends FeatureRenderer<S, M> {
    @Shadow
    public abstract void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, S armedEntityRenderState, float f, float g);

    public MoveHeldItemMixin(FeatureRendererContext<S, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Redirect(
            method = "renderItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderState;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;III)V")
    )
    protected void onRenderItem(ItemRenderState instance, MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int light, int overlay, int i, S entityState, ItemRenderState itemState, ItemStack stack, Arm arm, MatrixStack matrices2, OrderedRenderCommandQueue queue, int light2) {
        if (Main.isEnabled()) {
            matrices.push();
            matrices.translate(Main.model.heldItemOffset().multiply(arm == Arm.LEFT ? -1 : 1, 1, 1));
        }

        itemState.render(matrices, orderedRenderCommandQueue, light, overlay, i);

        if (Main.isEnabled()) matrices.pop();
    }
}
