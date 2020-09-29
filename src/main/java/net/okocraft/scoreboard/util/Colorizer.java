package net.okocraft.scoreboard.util;

import org.jetbrains.annotations.NotNull;

public final class Colorizer {
    private static final char COLOR_MARK = '&';
    private static final char COLOR_CHAR = '§';
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
        if (str == null) {
            return "";
        }

        char[] b = str.toCharArray();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < b.length; i++) {
            if (b[i] != COLOR_MARK || b.length == i + 1) {
                builder.append(b[i]);
                continue;
            }

            if (b[i + 1] == HEX_MARK) {
                if (i + 7 < b.length) {
                    try {
                        addMcColor(str.substring(i + 1, i + 8), builder);
                        i += 7;
                        continue;
                    } catch (IllegalArgumentException ignored) {
                    }
                }

                builder.append(b[i]).append(b[i + 1]);
                i++;
                continue;
            }

            if (-1 < COLOR_CODES.indexOf(b[i + 1])) {
                builder.append(COLOR_CHAR).append(Character.toLowerCase(b[i + 1]));
                i++;
            } else {
                builder.append(b[i]);
            }
        }

        return builder.toString();
    }

    private static void addMcColor(String hex, StringBuilder builder) throws IllegalArgumentException {
        char[] array = hex.toCharArray();

        if (array.length != 7) {
            throw new IllegalArgumentException("hex must be 7 characters.");
        }

        if (array[0] != HEX_MARK) {
            throw new IllegalArgumentException("hex must start with #");
        }

        try {
            Integer.parseInt(hex.substring(1), 16);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Could not parse hex: " + hex, e);
        }

        builder.append(COLOR_CHAR).append(X);

        for (char c : hex.substring(1).toCharArray()) {
            builder.append(COLOR_CHAR).append(c);
        }

    }
}
