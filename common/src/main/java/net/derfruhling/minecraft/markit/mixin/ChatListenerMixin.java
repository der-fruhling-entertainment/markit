package net.derfruhling.minecraft.markit.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.derfruhling.minecraft.markit.MarkdownHam;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.FormattedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatListener.class)
public class ChatListenerMixin {
    @WrapOperation(method = "guessChatUUID", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/StringDecomposer;getPlainText(Lnet/minecraft/network/chat/FormattedText;)Ljava/lang/String;"))
    public String disableMarkdownForGuessChatUUID(FormattedText stringBuilder, Operation<String> original) {
        return MarkdownHam.disabled(() -> original.call(stringBuilder));
    }
}
