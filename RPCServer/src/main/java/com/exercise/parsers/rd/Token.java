package com.exercise.parsers.rd;

class Token {

    public static final int SEMICOLON = 0;
    public static final int PERIOD = 1;
    public static final int PLUS = 2;
    public static final int MINUS = 3;
    public static final int MULTIPLY = 4;
    public static final int DIVIDE = 5;
    public static final int ASSIGN = 6;
    public static final int LEFT_PAREN = 7;
    public static final int RIGHT_PAREN = 8;
    public static final int LETTER = 9;
    public static final int NUMBER = 10;

    private static String[] tokens = {";", ".", "+", "-", "*", "/", "=", "(", ")", "letter", "number"};

    public static String toString(int i) {
        if (i < 0 || i > NUMBER)
            return "";
        return tokens[i];
    } // toString

} // Token
