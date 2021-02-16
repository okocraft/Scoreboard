package net.okocraft.scoreboard.util;

import org.jetbrains.annotations.NotNull;

public final class Colorizer {

    private static final char COLOR_MARK = '&';
    private static final String COLOR_MARK_STRING = String.valueOf(COLOR_MARK);
    private static final char COLOR_SECTION = '§';
    private static final String COLOR_SECTION_STRING = String.valueOf(COLOR_SECTION);
    private static final char HEX_MARK = '#';
    private static final char X = 'x';
    private static final String COLOR_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";

    private Colorizer() {
        throw new UnsupportedOperationException();
    }

    /**
     * Colorize given string. (convert to minecraft 1.16+ color format)
     * <p>
     * Example: {@code &aHello, &bWorld!} {@literal ->} {@code §aHello, §bWorld!}
     *
     * @param str the string to colorize
     * @return the colorized string
     */
    @NotNull
    public static String colorize(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }

        if (str.contains(COLOR_MARK_STRING)) {
            StringBuilder builder = new StringBuilder(str);

            int i = builder.indexOf(COLOR_MARK_STRING);

            if (i == -1) {
                return builder.toString();
            }

            int l = builder.length();

            while (i != -1 && i + 1 < l) {
                if (builder.charAt(i + 1) == HEX_MARK) {
                    if (i + 7 < l) {
                        String hex = builder.substring(i + 2, i + 8);

                        try {
                            Integer.parseInt(hex, 16);
                        } catch (NumberFormatException e) {
                            i = builder.indexOf(COLOR_MARK_STRING, i + 1);
                            continue;
                        }

                        builder.setCharAt(i, COLOR_SECTION);
                        builder.setCharAt(i + 1, X);

                        for (int j = i + 7; j != i + 1; j--) {
                            builder.insert(j, COLOR_SECTION_STRING);
                        }

                        l = builder.length();
                    }

                    i = builder.indexOf(COLOR_MARK_STRING, i + 1);
                    continue;
                }

                if (-1 < COLOR_CODES.indexOf(builder.charAt(i + 1))) {
                    builder.setCharAt(i, COLOR_SECTION);
                }

                i = builder.indexOf(COLOR_MARK_STRING, i + 1);
            }

            return builder.toString();
        } else {
            return str;
        }
    }
}
