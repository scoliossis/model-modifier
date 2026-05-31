package com.scale.modelModifier.utils.antibot;

import com.scale.modelModifier.Main;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListWidget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.UUID;

// code stolen from another mod i made
// i dont want bedwars npc's being rayman, sorry. :pensive:
// i left in a teams check, but i think the whole squad should be raymen
public class TargetUtil {
    public static SocialInteractionsPlayerListWidget PLAYER_LIST_WIDGET;
    public static final HashMap<UUID, Integer> CONSECUTIVE_VALID_PLAYER_TICKS = new HashMap<>();

    public static boolean isTeam(Entity target) {
        if (!(target instanceof PlayerEntity player)) return false;

        return player.getTeamColorValue() == Main.p().getTeamColorValue();
    }

    public static boolean isBot(Entity target) {
        if (target == null) return false;
        return target.getId() != Main.p().getId() && CONSECUTIVE_VALID_PLAYER_TICKS.getOrDefault(target.getUuid(), 0) <= 5;
    }
}
