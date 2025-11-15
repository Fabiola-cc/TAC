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
    void testStringAndBooleanLiterals() {
        String code = "var s = \"hola\"; var b = true;";
        List<String> expected = Arrays.asList(
                "t1 = \"hola\"",
                "s = t1",
                "t1 = 1",
                "b = t1"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testNullLiteral() {
        String code = "var n = null;";
        List<String> expected = Arrays.asList(
                "t1 = null",
                "n = t1"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testLiteralAndArithmetic() {
        String code = "var a = 5 + 3 * 2;";
        List<String> expected = Arrays.asList(
                "t1 = 5",
                "t2 = 3",
                "t3 = 2",
                "t4 = t2 * t3",
                "t3 = t1 + t4",
                "a = t3"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testAssignmentAndPrint() {
        String code = "var x = 10; print(x);";
        List<String> expected = Arrays.asList(
                "t1 = 10",
                "x = t1",
                "t1 = x",
                "call print(t1)"
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

    @Test
    void testIdentifierInExpression() {
        String code = "var a = 5; var b = a + 2;";
        List<String> expected = Arrays.asList(
                "t1 = 5",
                "a = t1",
                "t1 = 2",
                "t2 = a + t1",
                "b = t2"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testComplexArithmetic() {
        String code = "var x = (2 + 3) * (4 - 1) / 5;";
        List<String> expected = Arrays.asList(
                "t1 = 2",
                "t2 = 3",
                "t3 = t1 + t2",
                "t2 = 4",
                "t1 = 1",
                "t4 = t2 - t1",
                "t1 = t3 * t4",
                "t4 = 5",
                "t3 = t1 / t4",
                "x = t3"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testUnaryNotExpression() {
        String code = "var a = !true;";
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "t2 = !t1",
                "a = t2"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testNestedUnary() {
        String code = "var x = -(-3);";
        List<String> expected = Arrays.asList(
                "t1 = 3",
                "t2 = -t1",
                "t1 = -t2",
                "x = t1"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testMultipleAssignments() {
        String code = "var a = 1; var b = 2; a = b;";
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "a = t1",
                "t1 = 2",
                "b = t1",
                "a = b"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testAssignmentWithExpression() {
        String code = "var a = 5; a = a + 10;";
        List<String> expected = Arrays.asList(
                "t1 = 5",
                "a = t1",
                "t1 = 10",
                "t2 = a + t1",
                "a = t2"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testMixedExpression() {
        String code = "var x = 1; var y = 2; var z = -(x * y + 3) % 2;";
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "x = t1",
                "t1 = 2",
                "y = t1",
                "t1 = x * y",
                "t2 = 3",
                "t3 = t1 + t2",
                "t2 = -t3",
                "t3 = 2",
                "t1 = t2 % t3",
                "z = t1"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

}
