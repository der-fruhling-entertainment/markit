package net.derfruhling.minecraft.markit.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.derfruhling.minecraft.markit.MarkdownHam;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EditBox.class)
public class EditBoxMixin {
    @WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I", ordinal = 0))
    public int disableForCommandSuggestion(GuiGraphics instance, Font font, String string, int i, int j, int k, Operation<Integer> original) {
        return MarkdownHam.disabled(() -> original.call(instance, font, string, i, j, k));
    }
}
