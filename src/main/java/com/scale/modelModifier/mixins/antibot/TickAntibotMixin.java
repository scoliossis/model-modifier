package com.scale.modelModifier.mixins.antibot;

import com.scale.modelModifier.Main;
import com.scale.modelModifier.utils.antibot.AntiBotBridge;
import com.scale.modelModifier.utils.antibot.TargetUtil;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientPlayerEntity.class)
public class TickAntibotMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        TargetUtil.PLAYER_LIST_WIDGET.update(Main.p().networkHandler.getPlayerUuids(), 0, true);
        List<SocialInteractionsPlayerListEntry> players = AntiBotBridge.from(TargetUtil.PLAYER_LIST_WIDGET).bridge$getPlayers();
        for (SocialInteractionsPlayerListEntry player : players) {
            if (Main.w().getEntity(player.getUuid()) != null) {
                // if player is offline, their streak is reset, online gains a score, higher score means they likely arent a bot!
                if (!player.isOffline()) {
                    int currentTicks = TargetUtil.CONSECUTIVE_VALID_PLAYER_TICKS.getOrDefault(player.getUuid(), 0);
                    TargetUtil.CONSECUTIVE_VALID_PLAYER_TICKS.put(player.getUuid(), currentTicks + 1);
                }
                else TargetUtil.CONSECUTIVE_VALID_PLAYER_TICKS.remove(player.getUuid());
            }
        }
    }
}