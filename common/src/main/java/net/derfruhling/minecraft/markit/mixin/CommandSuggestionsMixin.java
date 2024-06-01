package net.derfruhling.minecraft.markit.mixin;

import net.derfruhling.minecraft.markit.Keeper;
import net.derfruhling.minecraft.markit.MarkdownHam;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandSuggestions.class)
public class CommandSuggestionsMixin {
    @Redirect(method = "formatChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FormattedCharSequence;forward(Ljava/lang/String;Lnet/minecraft/network/chat/Style;)Lnet/minecraft/util/FormattedCharSequence;"))
    public FormattedCharSequence formatChat(String string, Style emptyStyle) {
        return MarkdownHam.createEditorCharSequence(string, emptyStyle);
    }
}
