package net.okocraft.scoreboard.util;

import org.jetbrains.annotations.NotNull;

public final class Colorizer {
    private static final char COLOR_MARK = '&';
    private static final char COLOR_CHAR = '§';
    private static final char HEX_MARK = '#';
    private static final char X = 'x';
    private static final int HEX_COLOR_CODE_LENGTH = 7;
    private static final int HEXADECIMAL = 16;
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

            int next = i + 1;

            if (b[next] == HEX_MARK) {
                if (i + HEX_COLOR_CODE_LENGTH < b.length) {
                    try {
                        addMcColor(str.substring(next, next + HEX_COLOR_CODE_LENGTH), builder);
                        i += HEX_COLOR_CODE_LENGTH;
                        continue;
                    } catch (IllegalArgumentException ignored) {
                    }
                }

                builder.append(b[i]).append(b[next]);
                i++;
                continue;
            }

            if (-1 < COLOR_CODES.indexOf(b[next])) {
                builder.append(COLOR_CHAR).append(Character.toLowerCase(b[next]));
                i++;
            } else {
                builder.append(b[i]);
            }
        }

        return builder.toString();
    }

    private static void addMcColor(String hex, StringBuilder builder) throws IllegalArgumentException {
        char[] array = hex.toCharArray();

        if (array.length != HEX_COLOR_CODE_LENGTH) {
            throw new IllegalArgumentException("hex must be 7 characters.");
        }

        if (array[0] != HEX_MARK) {
            throw new IllegalArgumentException("hex must start with #");
        }

        try {
            Integer.parseInt(hex.substring(1), HEXADECIMAL);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Could not parse hex: " + hex, e);
        }

        builder.append(COLOR_CHAR).append(X);

        for (char c : hex.substring(1).toCharArray()) {
            builder.append(COLOR_CHAR).append(c);
        }

    }
}
