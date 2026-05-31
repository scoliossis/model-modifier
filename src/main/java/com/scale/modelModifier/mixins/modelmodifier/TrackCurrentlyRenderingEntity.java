package com.scale.modelModifier.mixins.modelmodifier;

import com.scale.modelModifier.Main;
import com.scale.modelModifier.utils.antibot.TargetUtil;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public class TrackCurrentlyRenderingEntity {
    // since setAngles is called between model parts, it seems like the perfect place to set the currently rendering entity
    @Inject(method = "setAngles(Lnet/minecraft/client/render/entity/state/BipedEntityRenderState;)V", at = @At("HEAD"))
    private void onRender(BipedEntityRenderState bipedEntityRenderState, CallbackInfo ci) {
        if (bipedEntityRenderState instanceof PlayerEntityRenderState playerEntityRenderState) {
            Entity entity = Main.w().getEntityById(playerEntityRenderState.id);
            Main.isLastPlayerBot = TargetUtil.isBot(entity);
            Main.currentlyRenderingEntity = (PlayerEntity) entity;
        }
    }
}