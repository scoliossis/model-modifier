package com.scale.modelModifier.mixins.modelmodifier;

import com.scale.modelModifier.Main;
import com.scale.modelModifier.utils.model.ModelFace;
import com.scale.modelModifier.utils.model.ModelPartName;
import com.scale.modelModifier.utils.model.Triangle;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(ModelPart.class)
public class OverwriteRenderCuboid {
    @Shadow public float originX;
    @Shadow public float originY;
    @Shadow public float originZ;

    @Inject(method = "renderCuboids", at = @At("HEAD"), cancellable = true)
    private void onRenderCuboids(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, int color, CallbackInfo ci) {
        if (!Main.isEnabled()) return;

        ModelPartName modelPartName = Main.MODEL_PART_NAME_MAP.get(this.hashCode());
        if (modelPartName == null) return;

        // we dont wanna see the original part, thats poop
        ci.cancel();

        // .div(16) came to me in a dream (net.minecraft.client.model.ModelPart.Vertex.SCALE_FACTOR)
        Vector3f origin = new Vector3f(this.originX, this.originY, this.originZ).div(16);

        // LivingEntityRenderer.render() line 93 does this (according to a comment in my old impl)
        Vector3f offset = new Vector3f(0, -1.501F, 0);

        ArrayList<ModelFace> modelFaces = new ArrayList<>(modelPartName.getFaces());

        // triple t deserves his own special rendering code methinks
        if (Main.currentlyRenderingEntity.getMainHandStack().isEmpty() && modelPartName.isMainArm(Main.currentlyRenderingEntity)) {
            ArrayList<ModelFace> baseHeldItemFaces = Main.model.faces().get(ModelPartName.BASE_HELD_ITEM.name);
            if (baseHeldItemFaces != null) modelFaces.addAll(baseHeldItemFaces);
        }

        // minecraft decided to use quads to draw cuboids, but i want triangles :pensive:
        for (ModelFace modelFace : modelFaces) {
            Triangle triangle = modelFace.triangle
                    // minecraft flips the vertices, awesome.
                    .negate()
                    .subtract(origin)
                    .subtract(offset);

            vertexConsumer
                    .vertex(entry, triangle.pointA)
                    // the v coordinates are inverted. not sure why.
                    .texture(modelFace.textureCoordinates.pointA.x, 1-modelFace.textureCoordinates.pointA.y)
                    .normal(entry, modelFace.normals.pointA)
                    .color(color).overlay(overlay).light(light);

            vertexConsumer
                    .vertex(entry, triangle.pointB)
                    .texture(modelFace.textureCoordinates.pointB.x, 1-modelFace.textureCoordinates.pointB.y)
                    .normal(entry, modelFace.normals.pointB)
                    .color(color).overlay(overlay).light(light);

            vertexConsumer
                    .vertex(entry, triangle.pointC)
                    .texture(modelFace.textureCoordinates.pointC.x, 1-modelFace.textureCoordinates.pointC.y)
                    .normal(entry, modelFace.normals.pointC)
                    .color(color).overlay(overlay).light(light);

            // a quad with 2 sides the same is a triangle!
            vertexConsumer
                    .vertex(entry, triangle.pointC)
                    .texture(modelFace.textureCoordinates.pointC.x, 1-modelFace.textureCoordinates.pointC.y)
                    .normal(entry, modelFace.normals.pointC)
                    .color(color).overlay(overlay).light(light);
        }
    }
}
