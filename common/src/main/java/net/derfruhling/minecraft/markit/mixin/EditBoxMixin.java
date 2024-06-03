package net.derfruhling.minecraft.markit.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.derfruhling.minecraft.markit.MarkdownHam;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiFunction;

@Mixin(EditBox.class)
public class EditBoxMixin {
    @WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I", ordinal = 0))
    public int disableForCommandSuggestion(GuiGraphics instance, Font font, String string, int i, int j, int k, Operation<Integer> original) {
        return MarkdownHam.disabled(() -> original.call(instance, font, string, i, j, k));
    }

    @Shadow @Final private Font font;

    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Ljava/util/function/BiFunction;apply(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0))
    public Object renderEverythingInFirstRender(BiFunction<String, Integer, FormattedCharSequence> instance,
                                                Object partial,
                                                Object displayPos,
                                                @Local(index = 7) String string) {
        return instance.apply(string, (Integer)displayPos);
    }

    @ModifyExpressionValue(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I"))
    public int replaceWithPartialLength(int original,
                                        @Local(index = 7) String string,
                                        @Local(index = 6) int l,
                                        @Local(index = 12) int x) {
        return l >= 0 && l <= string.length() ? x + MarkdownHam.divertToEditor(() -> font.width(string.substring(0, l))) : original;
    }

    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Ljava/lang/String;isEmpty()Z", ordinal = 1))
    public boolean noSecondRender(String instance) {
        return true; // pretend string is empty
    }
}
