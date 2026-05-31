package com.scale.modelModifier.mixins;

import com.scale.modelModifier.Main;
import com.scale.modelModifier.utils.model.Model;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;

// https://github.com/scoliossis/Grotto-Finder/blob/master/src/main/java/com/scale/grotto_hack_2025_scale_bypahh_undtc/mixin/ToggleCommandMixin.java
@Mixin(ChatScreen.class)
public class CommandMixin {
    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    public void onChatInput(String string, boolean bl, CallbackInfo ci) {
        try {
            if (string.startsWith("/modelmodifier") && string.contains(" ")) {
                String modelName = string.split(" ")[1].toLowerCase();

                if (!Main.isModelPresent(modelName)) {
                    modelName = "none";
                    Main.model = null;
                }
                else Main.model = Model.getModel(modelName);

                ChatHud chatComponent = MinecraftClient.getInstance().inGameHud.getChatHud();
                chatComponent.addToMessageHistory(string);
                chatComponent.addMessage(Text.of("§6[§fModel Modifier§6] model set to " + modelName));

                Files.write(Main.CONFIG_FILE.toPath(), modelName.getBytes());

                ci.cancel();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}