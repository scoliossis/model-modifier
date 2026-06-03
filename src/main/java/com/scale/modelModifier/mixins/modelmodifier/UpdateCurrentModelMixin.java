package com.scale.modelModifier.mixins.modelmodifier;

import com.scale.modelModifier.Main;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Model.class)
public class UpdateCurrentModelMixin<S> {
    @Shadow @Final protected ModelPart root;

    @Inject(method = "setAngles", at = @At("HEAD"))
    public void setAngles(S state, CallbackInfo ci) {
        if (state instanceof EntityRenderState entityRenderState) Main.getModel(entityRenderState, this.root);
        else Main.lastAccessedModel = null;
    }
}
