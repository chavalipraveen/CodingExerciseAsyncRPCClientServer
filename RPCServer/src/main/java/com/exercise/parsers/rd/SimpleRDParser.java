package com.exercise.parsers.rd;

/*
 * This class is responsible for recursive descent parsing of an expression tree
 * 
 * The grammar:
 * 
 * S = { E ";" } "."
 * E = T { ( "+" | "-" ) T }
 * T = F { ( "*" | "/" ) F }
 * F = NUMBER | "(" E ")"
 */

public class SimpleRDParser {

    private Tokenizer tokenizer;

    /**
     * Construct the parser passing in the Tokenizer
     * @param tokenizer
     */
    public SimpleRDParser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    /**
     * The main parse function that returns the 
     * expression evaluation result as a string
     * 
     * @return expression value after parsing and evaluation
     */
    public String parse() {
        tokenizer.getToken();
        return statement();
    }

    private String statement() {
        // S = { E ";" } "."
        StringBuilder finalValue = new StringBuilder();
        while (tokenizer.token != Token.PERIOD) {
            int value = expression();
            finalValue.append(value);
            tokenizer.getToken();
        }
        return finalValue.toString();
    }

    private int expression() {
        // E = T { ( "+" | "-" ) T }
        int left = term();
        while (tokenizer.token == Token.PLUS || tokenizer.token == Token.MINUS) {
            int saveToken = tokenizer.token;
            tokenizer.getToken();
            switch (saveToken) {
                case Token.PLUS:
                    left += term();
                    break;
                case Token.MINUS:
                    left -= term();
                    break;
            }
        }
        return left;
    }

    private int term() {
        // T = F { ( "*" | "/" ) F }
        int left = factor();
        while (tokenizer.token == Token.MULTIPLY || tokenizer.token == Token.DIVIDE) {
            int saveToken = tokenizer.token;
            tokenizer.getToken();
            switch (saveToken) {
                case Token.MULTIPLY:
                    left *= factor();
                    break;
                case Token.DIVIDE:
                    left /= factor();
                    break;
            }
        }
        return left;
    }

    private int factor() {
        // F = NUMBER | "(" E ")"
        int value = 0;
        switch (tokenizer.token) {
            case Token.NUMBER:
                value = tokenizer.number();
                tokenizer.getToken();  // flush NUMBER
                break;
            case Token.LEFT_PAREN:
                tokenizer.getToken();
                value = expression();
                if (tokenizer.token != Token.RIGHT_PAREN)
                    tokenizer.error("Missing ')'");
                tokenizer.getToken();  // flush ")"
                break;
            default:
                tokenizer.error("Expecting NUMBER or (");
                break;
        }
        return value;
    }
}
