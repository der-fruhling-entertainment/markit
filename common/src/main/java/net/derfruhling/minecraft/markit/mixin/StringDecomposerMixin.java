package net.derfruhling.minecraft.markit.mixin;

import net.derfruhling.minecraft.markit.Keeper;
import net.derfruhling.minecraft.markit.MarkdownHam;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StringDecomposer.class)
public abstract class StringDecomposerMixin {
    @Shadow
    private static boolean feedChar(Style arg, FormattedCharSink arg2, int i, char c) {
        return false;
    }

    /**
     * @author der_frühling
     * @reason Markdown
     */
    @Overwrite
    public static boolean iterateFormatted(String string, int offset, Style style, Style original, FormattedCharSink sink) {
        return MarkdownHam.iterateFormatted(
                StringDecomposerMixin::feedChar,
                string,
                offset,
                new Keeper.Value<>(style),
                original,
                sink
        );
    }
}
