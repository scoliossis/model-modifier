package com.scale.modelModifier.utils.antibot;


import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry;

import java.util.List;

public interface AntiBotBridge {
    static AntiBotBridge from(Object instance) {
        return (AntiBotBridge) instance;
    }

    List<SocialInteractionsPlayerListEntry> bridge$getPlayers();
}