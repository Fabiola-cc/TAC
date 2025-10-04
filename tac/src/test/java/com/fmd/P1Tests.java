package com.fmd;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tests para P1")
public class P1Tests {
    TestInit testInit = new TestInit();

    @Test
    void testLiteralAndArithmetic() {
        String code = "var a = 5 + 3 * 2;";
        List<String> expected = Arrays.asList(
                "t1 = 5",
                "t2 = 3",
                "t3 = 2",
                "t4 = t2 * t3",
                "t5 = t1 + t4",
                "a = t5"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testAssignmentAndPrint() {
        String code = "var x = 10; print(x);";
        List<String> expected = Arrays.asList(
                "t1 = 10",
                "x = t1",
                "call print(x)"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }


    @Test
    void testUnaryExpression() {
        String code = "var y = -5;";
        List<String> expected = Arrays.asList(
                "t1 = 5",
                "t2 = -t1",
                "y = t2"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }
}
