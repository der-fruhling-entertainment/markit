package net.derfruhling.minecraft.markit.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.derfruhling.minecraft.markit.MarkdownHam;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(BookEditScreen.class)
public class BookEditScreenMixin {
    @Redirect(method = "rebuildDisplayCache", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;width(Ljava/lang/String;)I"))
    public int getLineWidth(Font instance, String string, @Local(index = 5) List<BookEditScreen.LineInfo> lines, @Local(index = 12) int k) {
        return instance.width(FormattedText.of(string, lines.get(k).style));
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I", ordinal = 1))
    public int wrapDisabledForAuthor(GuiGraphics instance, Font arg, Component arg2, int i, int j, int k, boolean bl, Operation<Integer> original) {
        return MarkdownHam.disabled(() -> original.call(instance, arg, arg2, i, j, k, bl));
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I", ordinal = 3))
    public int wrap(GuiGraphics instance, Font arg, Component arg2, int i, int j, int k, boolean bl, Operation<Integer> original) {
        return MarkdownHam.divertToEditor(() -> original.call(instance, arg, arg2, i, j, k, bl));
    }

    @WrapOperation(method = "getDisplayCache", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/BookEditScreen;rebuildDisplayCache()Lnet/minecraft/client/gui/screens/inventory/BookEditScreen$DisplayCache;"))
    public BookEditScreen.DisplayCache wrap(BookEditScreen instance, Operation<BookEditScreen.DisplayCache> original) {
        return MarkdownHam.divertToEditor(() -> original.call(instance));
    }
}
