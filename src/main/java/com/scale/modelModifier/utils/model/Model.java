package com.scale.modelModifier.utils.model;

import com.scale.modelModifier.Main;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;

// java 17 or smth, how cool, i love records
public record Model(HashMap<String, ArrayList<ModelFace>> faces, Identifier texture, Vec3d heldItemOffset) {

}
