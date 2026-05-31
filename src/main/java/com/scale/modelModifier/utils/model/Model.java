package com.scale.modelModifier.utils.model;

import com.scale.modelModifier.Main;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;

// java 17 or smth, how cool, i love records
public record Model(String name, HashMap<String, ArrayList<ModelFace>> faces, Identifier texture, Vec3d heldItemOffset) {
    public static Model getModel(String name) {
        Main.heldItemOffsetOverwrite = null;

        if (!Main.isModelPresent(name)) {
            System.err.println("model not found: " + name);
            return null;
        }

        try {
            String offset = Main.getResource(name+"/helditemoffset.txt").get().getReader().readLine();
            Vec3d offsetVec = new Vec3d(
                    Double.parseDouble(offset.split(",")[0]),
                    Double.parseDouble(offset.split(",")[1]),
                    Double.parseDouble(offset.split(",")[2])
            );

            return new Model(name,
                    ModelParser.getModelFaces(Identifier.of("modelmodifier", name+"/model.obj")),
                    Identifier.of("modelmodifier", name+"/texturemap.png"),
                    offsetVec
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
