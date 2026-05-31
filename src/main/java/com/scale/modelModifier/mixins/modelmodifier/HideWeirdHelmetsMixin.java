package com.scale.modelModifier.mixins.modelmodifier;

import com.scale.modelModifier.Main;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeadFeatureRenderer.class)
public class HideWeirdHelmetsMixin {
    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/EntityRenderState;FF)V",
            at = @At("HEAD"),
            cancellable = true)
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, EntityRenderState state, float limbAngle, float limbDistance, CallbackInfo ci) {
        if (state instanceof PlayerEntityRenderState && Main.isEnabled())
            ci.cancel();
    }
}
