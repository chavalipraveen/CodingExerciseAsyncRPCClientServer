package com.exercise.server.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.Test;

import com.exercise.parsers.rd.SimpleRDParser;
import com.exercise.parsers.rd.Tokenizer;

public class BasicExpressionTest {

    @Test
    public void passTest() {
        String[] testInput = {"(9/3)+(3-1)*5;.", "2+3-9/3*1;.", "10-9+8/4*2;.", "1+2\n+2+3\n+5-1;."};
        String[] testOutput = {"13", "2", "5", "12"};
        for (int i = 0; i < testInput.length; i++) {
            StringReader sr = new StringReader(testInput[i]);
            BufferedReader br = new BufferedReader(sr);
            Tokenizer tokenizer = new Tokenizer(br);
            SimpleRDParser parser = new SimpleRDParser(tokenizer);
            String output = "";
            try {
                output = parser.parse();
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            } finally {
                assertEquals(output, testOutput[i]);
            }
        }
    }

    @Test(expected = Exception.class)
    public void failTest() throws Exception {
        String badInput = "1+2+3";
        String badOutput = "";
        StringReader sr = new StringReader(badInput);
        BufferedReader br = new BufferedReader(sr);
        Tokenizer tokenizer = new Tokenizer(br);
        SimpleRDParser parser = new SimpleRDParser(tokenizer);
        String output = parser.parse();
        assertEquals(output, badOutput);
    }
}
