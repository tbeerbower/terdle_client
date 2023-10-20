package com.techelevator.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextEffect {

    private static final Pattern pattern = Pattern.compile("\\\u001B\\[([\\d;]+)m");
    private static final String ESCAPE_FORMAT = "\u001B[%sm%s\u001B[0m";
    private static final Map<String, Code> codeMap = new HashMap<>();
    static {
        for (Code code : Code.values()) {
            codeMap.put(code.getCode(), code);
        }
    }

    public enum Code {
        RESET("0"),
        BOLD("1"),
        ITALIC("3"),
        UNDERLINE("4"),
        REVERSE("7"),
        CROSS_OUT("9"),
        BLACK("30"),
        RED("31"),
        GREEN("32"),
        YELLOW("33"),
        BLUE("34"),
        PURPLE("35"),
        CYAN("36"),
        WHITE("37"),
        BACKGROUND_BLACK("40"),
        BACKGROUND_RED("41"),
        BACKGROUND_GREEN("42"),
        BACKGROUND_YELLOW("43"),
        BACKGROUND_BLUE("44"),
        BACKGROUND_PURPLE("45"),
        BACKGROUND_CYAN("46"),
        BACKGROUND_WHITE("47");

        private final String code;

        Code(String code) {
            this.code = code;
        }
        public String getCode() {
            return code;
        }
    }

    private final Code[] codes;

    public TextEffect(Code... codes) {
        this.codes = codes;
    }

    public Code[] getCodes() {
        return codes;
    }

    public TextEffect join(Code... codes) {
        return new TextEffect(Stream.concat(Arrays.stream(this.codes), Arrays.stream(codes))
            .toArray(size -> (Code[]) Array.newInstance(this.codes.getClass().getComponentType(), size)));
    }

    public String apply(String text) {
        return String.format(ESCAPE_FORMAT,
            Arrays.stream(codes).map(Code::getCode).collect(Collectors.joining(";")),
            text);
    }

    public static TextEffect decode(String text) {
        TextEffect textEffect = new TextEffect();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String[] codes = matcher.group(1).split(";");
            // get enum codes
            textEffect = textEffect.join(
                Arrays.stream(codes).filter(code -> codeMap.get(code) != Code.RESET).
                    map(code -> codeMap.get(code)).collect(Collectors.toList()).toArray(new Code[]{}));
        }
        return textEffect;
    }
}
