package com.scale.modelModifier.mixins.modelmodifier;

import com.scale.modelModifier.Main;
import com.scale.modelModifier.utils.model.ModelFace;
import com.scale.modelModifier.utils.model.Triangle;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
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
    // fancy shadows
    @Shadow public float originX, originY, originZ;

    @Inject(method = "renderCuboids", at = @At("HEAD"), cancellable = true)
    private void onRenderCuboids(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, int color, CallbackInfo ci) {
        if (!Main.shouldOverwriteModel() || Main.lastAccessedModel.modelPartMap() == null) return;

        // maybe a bad way to get the model part name, but it works
        String modelPartName = null;
        for (String k : Main.lastAccessedModel.modelPartMap().keySet()) {
            if (Main.lastAccessedModel.modelPartMap().get(k) == (Object) this) {
                modelPartName = k;
                break;
            }
        }

        // if we have somehow swapped to rendering a different entity, then give up
        // afaik this only happens when rendering models that implement "SimpleSpecialModelRenderer" because they dont call setAngles
        // todo: this is a dodgy way to fix the above issue, if the last entity rendered before one of them has a same named modelPart it will be drawn on the weird entity
        if (modelPartName == null) return;

        // we dont wanna see the original part, thats poop
        ci.cancel();

        ArrayList<ModelFace> modelFaces = new ArrayList<>();

        // triple t deserves his own special rendering code methinks
        if (modelPartName == EntityModelPartNames.RIGHT_ARM && !Main.lastAccessedModel.holdingItem()) {
            ArrayList<ModelFace> baseHeldItemFaces = Main.lastAccessedModel.model().faces().get("baseitem");
            if (baseHeldItemFaces != null) modelFaces.addAll(baseHeldItemFaces);
        }

        // if they didnt put a part here, we just assume thats intentional, and give up
        modelFaces.addAll(Main.lastAccessedModel.model().faces().getOrDefault(modelPartName, new ArrayList<>()));
        if (modelFaces.isEmpty()) return;

        // .div(16) came to me in a dream (net.minecraft.client.model.ModelPart.Vertex.SCALE_FACTOR)
        Vector3f origin = new Vector3f(this.originX, this.originY, this.originZ).div(16);

        // LivingEntityRenderer.render() line 93 does this (according to a comment in my old impl)
        Vector3f offset = new Vector3f(0, -1.501F, 0);

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
