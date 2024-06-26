package net.derfruhling.minecraft.markit;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class MarkdownHam {
    private static final HashMap<String, BiFunction<Style, Boolean, Style>> STYLES = new HashMap<>();
    private static final Style MARKER_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);

    private static final ThreadLocal<Boolean> DIVERT_TO_EDITOR_MODE = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Boolean> DIVERT_DISABLED = ThreadLocal.withInitial(() -> false);

    static {
        STYLES.put("**", (s, isEditor) -> s.withBold(!s.isBold()));
        STYLES.put("__", (s, isEditor) -> s.withUnderlined(!s.isUnderlined()));
        STYLES.put("~~", (s, isEditor) -> s.withStrikethrough(!s.isStrikethrough()));
        STYLES.put("||", (s, isEditor) -> isEditor ? s : s.withObfuscated(!s.isObfuscated()));
    }

    public static boolean iterateFormattedEditor(CharFeeder feeder,
                                                 String string,
                                                 int offset,
                                                 Keeper<Style> styleKeeper,
                                                 Style original,
                                                 FormattedCharSink sink) {
        int length = string.length();
        Style style = styleKeeper.get();

        // filter here by whether the part is probably the username of somebody
        // this is done because usernames can contain underscores
        boolean applyMarkdownHam = !(DIVERT_DISABLED.get() || original.getClickEvent() != null || original.getHoverEvent() != null || original.getInsertion() != null);

        for (int i = offset; i < length; ++i) {
            char c1 = string.charAt(i);
            char c2;

            // Minecraft appends an underscore for the little bar in text fields
            // Make sure that it's not interpreted as a marker
            if(applyMarkdownHam && string.length() > 1 && (c1 == '*' || c1 == '_' || (c1 == '\\' && i + 1 < length) || ((c1 == '~' || c1 == '|') && i + 1 < length && string.charAt(i + 1) == c1))) {
                var value = feeder.feedChar(MARKER_STYLE, sink, i, c1);
                if(c1 == '\\' && i + 1 < length) {
                    c2 = string.charAt(i + 1);
                    value &= feeder.feedChar(style, sink, i + 1, c2);

                    ++i;

                    if(string.length() > i + 1) {
                        char c3 = string.charAt(i + 1);
                        // allow escaping a whole marker at once
                        if(STYLES.containsKey(String.valueOf(c2) + c3)) {
                            value &= feeder.feedChar(style, sink, i + 1, c3);
                            ++i;
                        }
                    }
                } else if ((c1 == '*' || c1 == '_') && (i + 1 >= length || string.charAt(i + 1) != c1)) {
                    style = style.withItalic(!style.isItalic());
                } else if (c1 != '\\') {
                    c2 = string.charAt(i + 1);
                    value &= feeder.feedChar(MARKER_STYLE, sink, i + 1, c2);
                    var styleToApply = STYLES.get(String.valueOf(c1) + c2);
                    if(styleToApply != null) {
                        style = styleToApply.apply(style, true);
                    }

                    ++i;
                }
                styleKeeper.set(style);
                if (!value) return false;
            } else if (c1 == '§') {
                if (i + 1 >= length) {
                    return true;
                }

                c2 = string.charAt(i + 1);
                ChatFormatting chatFormatting = ChatFormatting.getByCode(c2);
                if (chatFormatting != null) {
                    style = chatFormatting == ChatFormatting.RESET ? original : style.applyLegacyFormat(chatFormatting);
                    styleKeeper.set(style);
                }

                ++i;
            } else if (Character.isHighSurrogate(c1)) {
                if (i + 1 >= length) {
                    return sink.accept(i, style, 65533);
                }

                c2 = string.charAt(i + 1);
                if (Character.isLowSurrogate(c2)) {
                    if (!sink.accept(i, style, Character.toCodePoint(c1, c2))) {
                        return false;
                    }

                    ++i;
                } else if (!sink.accept(i, style, 65533)) {
                    return false;
                }
            } else if (!feeder.feedChar(style, sink, i, c1)) {
                return false;
            }
        }

        return true;
    }

    private static @NotNull Style createLinkResetStyle(Style style) {
        return style
                .withClickEvent(null)
                .withColor(ChatFormatting.RESET)
                .withUnderlined(false);
    }

    private static @NotNull Style createLinkUrlStyle(Style style, String url) {
        return style
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                .withColor(ChatFormatting.BLUE)
                .withUnderlined(true);
    }

    public static boolean iterateFormatted(CharFeeder feeder,
                                           String string,
                                           int offset,
                                           Keeper<Style> styleKeeper,
                                           Style original,
                                           FormattedCharSink sink) {
        if(DIVERT_TO_EDITOR_MODE.get()) {
            return iterateFormattedEditor(feeder, string, offset, styleKeeper, original, sink);
        }

        int length = string.length();
        Style style = styleKeeper.get();

        // filter here by whether the part is probably the username of somebody
        // this is done because usernames can contain underscores
        boolean applyMarkdownHam = !(DIVERT_DISABLED.get() || original.getClickEvent() != null || original.getHoverEvent() != null || original.getInsertion() != null);

        for (int i = offset; i < length; ++i) {
            char c1 = string.charAt(i);
            char c2;

            // Minecraft appends an underscore for the little bar in text fields
            // Make sure that it's not interpreted as a marker
            if(applyMarkdownHam && string.length() > 1 && (c1 == '*' || c1 == '_' || (c1 == '\\' && i + 1 < length) || ((c1 == '~' || c1 == '|') && i + 1 < length && string.charAt(i + 1) == c1))) {
                if(c1 == '\\' && i + 1 < length) {
                    c2 = string.charAt(i + 1);
                    feeder.feedChar(style, sink, i, c2);

                    ++i;

                    if(string.length() > i + 1) {
                        char c3 = string.charAt(i + 1);
                        // allow escaping a whole marker at once
                        if(STYLES.containsKey(String.valueOf(c2) + c3)) {
                            feeder.feedChar(style, sink, i, c3);
                            i++;
                        }
                    }
                } else if ((c1 == '*' || c1 == '_') && (i + 1 >= length || string.charAt(i + 1) != c1)) {
                    style = style.withItalic(!style.isItalic());
                } else if(c1 != '\\') {
                    c2 = string.charAt(i + 1);
                    var styleToApply = STYLES.get(String.valueOf(c1) + c2);
                    if(styleToApply != null) {
                        style = styleToApply.apply(style, true);
                    }

                    ++i;
                }
                styleKeeper.set(style);
            } else if (c1 == '§') {
                if (i + 1 >= length) {
                    break;
                }

                c2 = string.charAt(i + 1);
                ChatFormatting chatFormatting = ChatFormatting.getByCode(c2);
                if (chatFormatting != null) {
                    style = chatFormatting == ChatFormatting.RESET ? original : style.applyLegacyFormat(chatFormatting);
                    styleKeeper.set(style);
                }

                ++i;
            } else if (Character.isHighSurrogate(c1)) {
                if (i + 1 >= length) {
                    if (!sink.accept(i, style, 65533)) {
                        return false;
                    }
                    break;
                }

                c2 = string.charAt(i + 1);
                if (Character.isLowSurrogate(c2)) {
                    if (!sink.accept(i, style, Character.toCodePoint(c1, c2))) {
                        return false;
                    }

                    ++i;
                } else if (!sink.accept(i, style, 65533)) {
                    return false;
                }
            } else if (!feeder.feedChar(style, sink, i, c1)) {
                return false;
            }
        }

        return true;
    }

    public static void divertToEditor(Runnable r) {
        try {
            DIVERT_TO_EDITOR_MODE.set(true);
            r.run();
        } finally {
            DIVERT_TO_EDITOR_MODE.set(false);
        }
    }

    public static <T> T divertToEditor(Supplier<T> r) {
        try {
            DIVERT_TO_EDITOR_MODE.set(true);
            return r.get();
        } finally {
            DIVERT_TO_EDITOR_MODE.set(false);
        }
    }

    public static <T> T disabled(Supplier<T> r) {
        try {
            DIVERT_DISABLED.set(true);
            return r.get();
        } finally {
            DIVERT_DISABLED.set(false);
        }
    }

    public static FormattedCharSequence createCharSequence(String string, Style original) {
        return formattedCharSink -> {
            MarkdownHam.iterateFormatted(
                    (style, sink, m, c) -> {
                        if (Character.isSurrogate(c)) {
                            return sink.accept(m, style, 65533);
                        }
                        return sink.accept(m, style, c);
                    },
                    string,
                    0,
                    new Keeper.Value<>(original),
                    original,
                    formattedCharSink
            );

            return true;
        };
    }

    public static FormattedCharSequence createEditorCharSequence(String string, Style original) {
        return formattedCharSink -> {
            MarkdownHam.iterateFormattedEditor(
                    (style, sink, m, c) -> {
                        if (Character.isSurrogate(c)) {
                            return sink.accept(m, style, 65533);
                        }
                        return sink.accept(m, style, c);
                    },
                    string,
                    0,
                    new Keeper.Value<>(original),
                    original,
                    formattedCharSink
            );

            return true;
        };
    }
}
