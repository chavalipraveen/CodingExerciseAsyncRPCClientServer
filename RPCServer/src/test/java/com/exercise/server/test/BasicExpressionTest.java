package com.exercise.server.test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.Test;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;

import com.exercise.parsers.rd.SimpleRDParser;
import com.exercise.parsers.rd.Tokenizer;
import com.exercise.server.RPCWorker;

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

    @Test
    public void conditionTest() {
        String[] testInput = {"9/3 == 4/2;.", "6/2 == 9/3;.", "(10 - 8)/2 == (2-1)/1;.", "2*1 == 4/2;.", "1==2;.",
                "4==4;."};
        String[] testOutput = {"false", "true", "true", "true", "false", "true"};
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

    @Test
    public void passTestWithActors() {

        ActorSystem system = ActorSystem.create("RPCServerTest");
        ActorRef worker = system.actorOf(Props.create(RPCWorker.class));

        String[] testInput = {"(9/3)+(3-1)*5;.", "2+3-9/3*1;.", "10-9+8/4*2;.", "1+2\n+2+3\n+5-1;."};
        String[] testOutput = {"13", "2", "5", "12"};
        for (int i = 0; i < testInput.length; i++) {
            Timeout timeout = new Timeout(Duration.create(5, SECONDS));
            Future<Object> future = Patterns.ask(worker, testInput[i], timeout);
            String output = "";
            try {
                output = (String) Await.result(future, Duration.create(10, SECONDS));
            } catch (Exception e) {
                System.out.println("Error awaiting result: " + e.getLocalizedMessage());
            }
            assertEquals(output, testOutput[i]);
        }
    }

    @Test(expected = Exception.class)
    public void failTest() throws Exception {
        String badInput = "1+2+3= =2+1;.";
        String badOutput = "false";
        StringReader sr = new StringReader(badInput);
        BufferedReader br = new BufferedReader(sr);
        Tokenizer tokenizer = new Tokenizer(br);
        SimpleRDParser parser = new SimpleRDParser(tokenizer);
        String output = parser.parse();
        assertEquals(output, badOutput);
    }

}
