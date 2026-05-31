package com.scale.modelModifier.utils.model;

import com.scale.modelModifier.Main;
import lombok.AllArgsConstructor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;

import java.util.ArrayList;

@AllArgsConstructor
// head, body, lefthand, righthand, leftfoot, rightfoot
public enum ModelPartName {
    // layers with names are attempted to be rendered from the obj
    // if the obj doesnt contain a section with their name, nothing is drawn in its place
    HEAD("head"),
    BODY("body"),
    LEFT_HAND("lefthand"),
    RIGHT_HAND("righthand"),
    LEFT_FOOT("leftfoot"),
    RIGHT_FOOT("rightfoot"),

    // triple t needs his bat
    BASE_HELD_ITEM("baseitem"),

    // for silly layers we dont wanna see blehh
    HIDDEN("hidden");

    public boolean isMainArm(PlayerEntity player) {
        return (player.getMainArm() == Arm.LEFT && this == LEFT_HAND) || (player.getMainArm() == Arm.RIGHT && this == RIGHT_HAND);
    }

    public final String name;

    public ArrayList<ModelFace> getFaces() {
        if (!Main.model.faces().containsKey(name)) return new ArrayList<>();
        return Main.model.faces().get(name);
    }
}
