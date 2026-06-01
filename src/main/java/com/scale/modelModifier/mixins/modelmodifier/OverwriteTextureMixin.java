package com.scale.modelModifier.mixins.modelmodifier;

import com.scale.modelModifier.Main;
import com.scale.modelModifier.utils.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class OverwriteTextureMixin <T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
        extends EntityRenderer<T, S>
        implements FeatureRendererContext<S, M> {

    @Shadow protected M model;

    protected OverwriteTextureMixin(EntityRendererFactory.Context context) {
        super(context);
    }

    @Inject(
            method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
            at = @At("HEAD")
    )
    public void updateRenderState(T livingEntity, S livingEntityRenderState, float f, CallbackInfo ci) {
        Main.getModel(livingEntityRenderState, this.model.getRootPart());
    }

    @Redirect(method = "getRenderLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;)Lnet/minecraft/util/Identifier;"))
    public Identifier getTexture(LivingEntityRenderer<T, S, M> instance, S s) {
        Main.getModel(s);

        if (Main.lastAccessedModel == null) return instance.getTexture(s);
        return Main.lastAccessedModel.model().texture();
    }
}
