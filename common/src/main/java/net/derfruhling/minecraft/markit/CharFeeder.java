package net.derfruhling.minecraft.markit;

import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSink;

@FunctionalInterface
public interface CharFeeder {
    boolean feedChar(Style arg, FormattedCharSink arg2, int i, char c);
}
