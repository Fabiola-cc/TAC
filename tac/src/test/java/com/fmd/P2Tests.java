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

@DisplayName("Tests para P2")
public class P2Tests {
    TestInit testInit = new TestInit();

    @Test
    void testRelationalAndEquality() {
        String code = "let a = 5; let b = 10; print(a < b); print(a == b); print(a != b);";
        List<String> expected = Arrays.asList(
                "a = 5",
                "b = 10",
                "t1 = a < b",
                "call print(t1)",
                "t2 = a == b",
                "call print(t2)",
                "t3 = a != b",
                "call print(t3)"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testLogicalOperators() {
        String code = "let x = true; let y = false; print(x && y); print(x || y);";
        List<String> expected = Arrays.asList(
                "x = true",
                "y = false",
                "t1 = x && y",
                "call print(t1)",
                "t2 = x || y",
                "call print(t2)"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }


    @Test
    void testIfStatement() {
        String code = "if (true) { print(1); } else { print(0); }";
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "if t1 == 0 goto L1",
                "t2 = 1",
                "call print(t2)",
                "goto L2",
                "L1:",
                "t3 = 0",
                "call print(t3)",
                "L2:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testWhileStatement() {
        String code = "var i = 0; while (i < 3) { print(i); i = i + 1; }";
        List<String> expected = Arrays.asList(
                "t1 = 0",
                "i = t1",
                "L1:",
                "t2 = 3",
                "t3 = i < t2",
                "if t3 == 0 goto L2",
                "call print(i)",
                "t4 = 1",
                "t5 = i + t4",
                "i = t5",
                "goto L1",
                "L2:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testDoWhileStatement() {
        String code = "var j = 0; do { print(j); j = j + 1; } while (j < 3);";
        List<String> expected = Arrays.asList(
                "t1 = 0",
                "j = t1",
                "L1:",
                "call print(j)",
                "t2 = 1",
                "t3 = j + t2",
                "j = t3",
                "t4 = 3",
                "t5 = j < t4",
                "if t5 != 0 goto L1",
                "L2:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testForStatement() {
        String code = "for (var j = 0; j < 3; j = j + 1) { print(j); }";
        List<String> expected = Arrays.asList(
                "j = 0",
                "L1:",
                "t1 = j",
                "t2 = 3",
                "t3 = t1 < t2",
                "if t3 == 0 goto L2",
                "call print(j)",
                "t4 = j",
                "t5 = 1",
                "t6 = t4 + t5",
                "j = t6",
                "goto L1",
                "L2:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }
}
