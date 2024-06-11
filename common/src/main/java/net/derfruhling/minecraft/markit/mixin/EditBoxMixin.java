package net.derfruhling.minecraft.markit.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.derfruhling.minecraft.markit.MarkdownHam;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiFunction;

@Mixin(value = EditBox.class)
public abstract class EditBoxMixin extends AbstractWidget {
    public EditBoxMixin(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    @WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I", ordinal = 0))
    public int disableForCommandSuggestion(GuiGraphics instance, Font font, String string, int i, int j, int k, Operation<Integer> original) {
        return MarkdownHam.disabled(() -> original.call(instance, font, string, i, j, k));
    }

    @Shadow @Final private Font font;

    @Shadow private boolean bordered;

    @Shadow private int displayPos;

    @Shadow private int cursorPos;

    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Ljava/util/function/BiFunction;apply(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0))
    public Object renderEverythingInFirstRender(BiFunction<String, Integer, FormattedCharSequence> instance,
                                                Object partial,
                                                Object displayPos,
                                                @Local(ordinal = 0) String string) {
        return instance.apply(string, (Integer)displayPos);
    }

    @WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I"))
    public int wrapInEditor(GuiGraphics instance, Font font, FormattedCharSequence formattedCharSequence, int i, int j, int k, Operation<Integer> original) {
        return MarkdownHam.divertToEditor(() -> original.call(instance, font, formattedCharSequence, i, j, k));
    }

    @ModifyExpressionValue(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I"))
    public int replaceWithPartialLength(int original,
                                        @Local(ordinal = 0) String string) {
        int x = bordered ? getX() + 4 : getX();
        int l = cursorPos - displayPos;
        return l >= 0 && l <= string.length() ? x + MarkdownHam.divertToEditor(() -> font.width(string.substring(0, l))) : original;
    }

    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Ljava/lang/String;isEmpty()Z", ordinal = 1))
    public boolean noSecondRender(String instance) {
        return true; // pretend string is empty
    }
}
