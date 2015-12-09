package com.exercise.parsers.rd;

import java.io.BufferedReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for tokenizing the given input string
 *
 */
public class Tokenizer {

    private final Logger log = LoggerFactory.getLogger(Tokenizer.class);
    private char ch = ' ';
    private int intValue = 0;
    private Buffer buffer;
    public int token;

    public Tokenizer(BufferedReader br) {
        buffer = new Buffer(br);
        token = Token.SEMICOLON;
    }

    public int getToken() {
        while (Character.isWhitespace(ch)) {
            ch = buffer.get();
        }
        if (Character.isLetter(ch)) {
            Character.toLowerCase(ch);
            ch = buffer.get();
            token = Token.LETTER;
        } else if (Character.isDigit(ch)) {
            intValue = getNumber();
            token = Token.NUMBER;
        } else {
            switch (ch) {
                case ';':
                    ch = buffer.get();
                    token = Token.SEMICOLON;
                    break;

                case '.':
                    ch = buffer.get();
                    token = Token.PERIOD;
                    break;

                case '+':
                    ch = buffer.get();
                    token = Token.PLUS;
                    break;

                case '-':
                    ch = buffer.get();
                    token = Token.MINUS;
                    break;

                case '*':
                    ch = buffer.get();
                    token = Token.MULTIPLY;
                    break;

                case '/':
                    ch = buffer.get();
                    token = Token.DIVIDE;
                    break;

                case '=':
                    ch = buffer.get();
                    token = Token.ASSIGN;
                    break;

                case '(':
                    ch = buffer.get();
                    token = Token.LEFT_PAREN;
                    break;

                case ')':
                    ch = buffer.get();
                    token = Token.RIGHT_PAREN;
                    break;

                default:
                    error("Illegal character " + ch);
                    break;
            }
        }
        return token;
    }

    public int number() {
        return intValue;
    }

    public void error(String msg) {
        log.error(msg);
    }

    private int getNumber() {
        int rslt = 0;
        do {
            rslt = rslt * 10 + Character.digit(ch, 10);
            ch = buffer.get();
        } while (Character.isDigit(ch));
        return rslt;
    }

}

class Buffer {
    private final Logger log = LoggerFactory.getLogger(Buffer.class);

    private String line = "";
    private int column = 0;
    private BufferedReader br;

    public Buffer(BufferedReader br) {
        this.br = br;
    }

    public char get() {
        column++;
        if (column >= line.length()) {
            try {
                line = br.readLine();
            } catch (Exception e) {
                log.error("Invalid read operation");
            } // try
            if (line == null) {
                log.info("Line is null");
            }
            column = 0;
            if (log.isDebugEnabled()) {
                log.debug(line);
            }
            line = line + "\n";
        }
        return line.charAt(column);
    }

}
