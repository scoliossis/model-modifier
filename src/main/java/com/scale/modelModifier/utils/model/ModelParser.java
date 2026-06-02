package com.scale.modelModifier.utils.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.scale.modelModifier.Main;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

// code from an old project i made, slightly modified to have extra swag
public class ModelParser {
    public static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .create();

    public static Model getModel(String entityKey) {
        if (!Main.isModelPresent(entityKey)) return null;

        try {
            Optional<Resource> dataJson = Main.getEntityResource(entityKey, "config.json");
            HashMap<String, LinkedTreeMap<String, Double>> config = dataJson.isPresent()
                    ? gson.fromJson(dataJson.get().getReader(), HashMap.class)
                    :  new HashMap<>();

            Vec3d scaleVec = parseVec3dFromJson(config.getOrDefault("size", new LinkedTreeMap<>()), new Vec3d(1, 1, 1));
            Vec3d offsetVec = parseVec3dFromJson(config.getOrDefault("held_item_offset", new LinkedTreeMap<>()), Vec3d.ZERO);

            HashMap<String, ArrayList<ModelFace>> modelFaces = getModelFaces(entityKey);
            Vector3f dimensions = getDimensions(modelFaces);

            Vec3d scaleMultiplier = new Vec3d(scaleVec.x/dimensions.x, scaleVec.y/dimensions.y, scaleVec.z/dimensions.z);

            for (ArrayList<ModelFace> modelFaceList : modelFaces.values()) {
                for (ModelFace modelFace : modelFaceList) {
                    modelFace.triangle = modelFace.triangle.multiply(scaleMultiplier);
                }
            }

            return new Model(
                    modelFaces,
                    Main.getEntityIdentifier(entityKey, "texturemap.png"),
                    offsetVec
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Vec3d parseVec3dFromJson(LinkedTreeMap<String, Double> json, Vec3d defaultVec3d) {
        return new Vec3d(
                json.getOrDefault("x", defaultVec3d.x),
                json.getOrDefault("y", defaultVec3d.y),
                json.getOrDefault("z", defaultVec3d.z)
        );
    }

    private static HashMap<String, ArrayList<ModelFace>> getModelFaces(String entityKey) {
        try {
            HashMap<String, ArrayList<ModelFace>> modelFaces = new HashMap<>();

            ArrayList<Vector3f> vertices = new ArrayList<>();
            ArrayList<Vector3f> normals = new ArrayList<>();
            ArrayList<Vector3f> textureCoordinates = new ArrayList<>();

            BufferedReader fileReader = Main.getEntityResource(entityKey, "model.obj").get().getReader();

            String modelPartName = "";
            // in case u have a obj with NO names
            modelFaces.put("", new ArrayList<>());

            for (String line; (line = fileReader.readLine()) != null;) {
                // some people format their objs with extra spaces ig.
                line = line.replaceAll("\\s+", " ");

                String[] lineSplit = line.split(" ");

                switch (lineSplit[0]) {
                    case "o" -> {
                        modelPartName = lineSplit[1];
                        modelFaces.putIfAbsent(modelPartName, new ArrayList<>());

                        System.out.println("adding faces for " + modelPartName);
                    }
                    case "v" -> vertices.add(parseVector3fFromLine(lineSplit));
                    case "vt" -> textureCoordinates.add(parseVector3fFromLine(lineSplit));
                    case "vn" -> normals.add(parseVector3fFromLine(lineSplit));
                    case "f" -> {
                        for (int i = 2; i < lineSplit.length-1; i++) {
                            String[] vectorParts = new String[] {
                                    lineSplit[1],
                                    lineSplit[i],
                                    lineSplit[i + 1],
                            };

                            Triangle vertexTriangle = getTriangle(vertices, vectorParts, 0);
                            Triangle textureCoordinatesTriangle = getTriangle(textureCoordinates, vectorParts, 1);
                            Triangle normalsTriangle = getTriangle(normals, vectorParts, 2);

                            modelFaces.get(modelPartName).add(new ModelFace(vertexTriangle, textureCoordinatesTriangle, normalsTriangle));
                        }
                    }
                }
            }

            return modelFaces;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private static Vector3f getDimensions(HashMap<String, ArrayList<ModelFace>> modelFaces) {
        return new Vector3f(
                getMaxFromAxis(modelFaces, Direction.Axis.X) - getMinFromAxis(modelFaces, Direction.Axis.X),
                getMaxFromAxis(modelFaces, Direction.Axis.Y) - getMinFromAxis(modelFaces, Direction.Axis.Y),
                getMaxFromAxis(modelFaces, Direction.Axis.Z) - getMinFromAxis(modelFaces, Direction.Axis.Z)
        );
    }

    private static Stream<Float> flatMapModelFacesAxis(HashMap<String, ArrayList<ModelFace>> modelFaces, Direction.Axis axis) {
        return modelFaces.values()
                .stream()
                .flatMap(List::stream)
                .map(face -> face.triangle.getMax(axis));
    }

    private static float getMinFromAxis(HashMap<String, ArrayList<ModelFace>> modelFaces, Direction.Axis axis) {
        return flatMapModelFacesAxis(modelFaces, axis).min(Float::compare).orElse(0f);
    }

    private static float getMaxFromAxis(HashMap<String, ArrayList<ModelFace>> modelFaces, Direction.Axis axis) {
        return flatMapModelFacesAxis(modelFaces, axis).max(Float::compare).orElse(0f);
    }

    private static Vector3f parseVector3fFromLine(String[] lineSplit) {
        return new Vector3f(
                Float.parseFloat(lineSplit[1]),
                Float.parseFloat(lineSplit[2]),
                lineSplit.length >= 4 ? Float.parseFloat(lineSplit[3]) : 0
        );
    }

    private static int getFaceElement(String face, int elementInt) {
        return Integer.parseInt(face.split("/")[elementInt]) - 1;
    }

    private static Triangle getTriangle(ArrayList<Vector3f> values, String[] vectorParts, int elementInt) {
        int p1 = getFaceElement(vectorParts[0], elementInt);
        int p2 = getFaceElement(vectorParts[1], elementInt);
        int p3 = getFaceElement(vectorParts[2], elementInt);
        if (p1 >= values.size() || p2 >= values.size() || p3 >= values.size())
            System.err.println("trying to access a face that doesnt exist (wifies reference) " + p1 + "/" + p2 + "/" + p3 + " more than " + values.size());

        return values.isEmpty() ? Triangle.EMPTY : new Triangle(
                values.get(Math.min(p1, values.size()-1)),
                values.get(Math.min(p2, values.size()-1)),
                values.get(Math.min(p3, values.size()-1))
        );
    }
}