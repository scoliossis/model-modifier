package com.scale.modelModifier.utils.model;

import net.minecraft.client.model.ModelPart;

import java.util.Map;

public record LivingEntityInfo(Model model, ModelPart rootPart, Map<String, ModelPart> modelPartMap, boolean isBot,
                               boolean holdingItem) {
}
