package com.scale.modelModifier.mixins.antibot;

import com.scale.modelModifier.utils.antibot.AntiBotBridge;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(SocialInteractionsPlayerListWidget.class)
public abstract class AntiBotAccessorMixin implements AntiBotBridge {
    @Shadow @Final private List<SocialInteractionsPlayerListEntry> players;

    @Override
    public List<SocialInteractionsPlayerListEntry> bridge$getPlayers() {
        return this.players;
    }
}
