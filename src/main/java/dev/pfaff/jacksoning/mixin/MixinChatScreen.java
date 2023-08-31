package dev.pfaff.jacksoning.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public final class MixinChatScreen {
	@Inject(method = "sendMessage", at = @At(value = "INVOKE", target = "Ljava/lang/String;startsWith(Ljava/lang/String;)Z"), cancellable = true)
	private final void sendMessage$clientCommands(String chatText, boolean addToHistory, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (chatText.startsWith(".")) {
			var commandEndI = chatText.indexOf(' ');
			var command = commandEndI == -1 ? chatText : chatText.substring(0, commandEndI);
			var args = chatText.substring(commandEndI+1).split(";");
			switch (command) {
				default -> {
					MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("Unrecognized command: " + command));
				}
			}
			callbackInfo.setReturnValue(true);
		}
	}
}
