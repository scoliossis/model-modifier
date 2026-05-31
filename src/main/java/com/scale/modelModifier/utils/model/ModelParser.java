package com.scale.modelModifier.utils.model;

import com.scale.modelModifier.Main;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// code from an old project i made
// slightly modified to be specific for minecraft models
public class ModelParser {
    public static HashMap<String, ArrayList<ModelFace>> getModelFaces(Identifier identifier) {
        try {
            HashMap<String, ArrayList<ModelFace>> modelFaces = new HashMap<>();

            ArrayList<Vector3f> vertices = new ArrayList<>();
            ArrayList<Vector3f> normals = new ArrayList<>();
            ArrayList<Vector3f> textureCoordinates = new ArrayList<>();

            BufferedReader fileReader = Main.getResource(identifier.getPath()).get().getReader();

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

            // wow this is a great looking variable :sob: it just gets the highest y of the model
            float maxY = modelFaces.values()
                    .stream()
                    .flatMap(List::stream)
                    .map(face -> face.triangle.getMax().y)
                    .max(Float::compare)
                    .orElse(0f);

            System.out.println("maxY: " + maxY);
            // the model should be 2 blocks high!
            float scale = 2 / maxY;
            // so we make EVERYTHING that scale
            for (ArrayList<ModelFace> modelFaceArrayList : modelFaces.values()) {
                for (ModelFace modelFace : modelFaceArrayList) {
                    modelFace.triangle = modelFace.triangle.multiply(new Vector3f(scale, scale, scale));
                }
            }

            return modelFaces;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
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