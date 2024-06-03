package net.derfruhling.minecraft.markit.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.ParseResults;
import net.derfruhling.minecraft.markit.MarkdownHam;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.commands.SharedSuggestionProvider;
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

    @WrapOperation(method = "formatChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CommandSuggestions;formatText(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;I)Lnet/minecraft/util/FormattedCharSequence;"))
    public FormattedCharSequence disableMarkdownForCommands(ParseResults<SharedSuggestionProvider> parseResults, String string, int i, Operation<FormattedCharSequence> original) {
        return MarkdownHam.disabled(() -> original.call(parseResults, string, i));
    }
}
