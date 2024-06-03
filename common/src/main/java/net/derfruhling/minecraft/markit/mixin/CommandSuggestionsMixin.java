package net.derfruhling.minecraft.markit.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.ParseResults;
import net.derfruhling.minecraft.markit.MarkdownHam;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandSuggestions.class)
public class CommandSuggestionsMixin {
    @Shadow private @Nullable ParseResults<SharedSuggestionProvider> currentParse;

    @Redirect(method = "formatChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FormattedCharSequence;forward(Ljava/lang/String;Lnet/minecraft/network/chat/Style;)Lnet/minecraft/util/FormattedCharSequence;"))
    public FormattedCharSequence formatChat(String string, Style emptyStyle) {
        return MarkdownHam.createEditorCharSequence(string, emptyStyle);
    }

    @WrapOperation(method = "formatChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CommandSuggestions;formatText(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;I)Lnet/minecraft/util/FormattedCharSequence;"))
    public FormattedCharSequence disableForCommands(ParseResults<SharedSuggestionProvider> parseResults, String string, int i, Operation<FormattedCharSequence> original) {
        return MarkdownHam.disabled(() -> original.call(parseResults, string, i));
    }

    @WrapOperation(method = "fillNodeUsage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;width(Ljava/lang/String;)I"))
    public int unformattedWidth(Font instance, String string, Operation<Integer> original) {
        return MarkdownHam.disabled(() -> original.call(instance, string));
    }

    @WrapOperation(method = "renderSuggestions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CommandSuggestions$SuggestionsList;render(Lnet/minecraft/client/gui/GuiGraphics;II)V"))
    public void disableForSuggestions(CommandSuggestions.SuggestionsList instance, GuiGraphics guiGraphics, int i, int j, Operation<Void> original) {
        if(this.currentParse != null) {
            MarkdownHam.disabled(() -> original.call(instance, guiGraphics, i, j));
        } else {
            original.call(instance, guiGraphics, i, j);
        }
    }
}
