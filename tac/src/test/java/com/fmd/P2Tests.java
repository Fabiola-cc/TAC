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

@DisplayName("Tests para P2 (con reutilización de temporales)")
public class P2Tests {
    TestInit testInit = new TestInit();

    @Test
    void testRelationalOperators() {
        String code = "let a = 5; let b = 10; print(a < b); print(a == b); print(a != b);";
        List<String> expected = Arrays.asList(
                "t1 = 5",
                "a = t1",
                "t1 = 10",
                "b = t1",
                "t1 = a < b",
                "call print(t1)",
                "t1 = a == b",
                "call print(t1)",
                "t1 = a != b",
                "call print(t1)"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testLogicalOperators() {
        String code = "let x = true; let y = false; print(x && y); print(x || y);";
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "x = t1",
                "t1 = 0",
                "y = t1",
                "t1 = 0",
                "if x == 0 goto L1",
                "t1 = y",
                "L1:",
                "call print(t1)",
                "t1 = 1",
                "if x != 0 goto L2",
                "t1 = y",
                "L2:",
                "call print(t1)"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testLogicalCombination() {
        String code = "let a = 1; let b = 2; if (a < b && b < 5){ print(1);}";
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "a = t1",
                "t1 = 2",
                "b = t1",
                "t2 = a < b",
                "t1 = 0",
                "if t2 == 0 goto L1",
                "t3 = 5",
                "t4 = b < t3",
                "t1 = t4",
                "L1:",
                "if t1 == 0 goto L2",
                "t4 = 1",
                "call print(t4)",
                "L2:"
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
                "t2 = 0",
                "call print(t2)",
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
                "t1 = 3",
                "t2 = i < t1",
                "if t2 == 0 goto L2",
                "t1 = i",
                "call print(t1)",
                "t1 = 1",
                "t3 = i + t1",
                "i = t3",
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
                "t1 = j",
                "call print(t1)",
                "t1 = 1",
                "t2 = j + t1",
                "j = t2",
                "t2 = 3",
                "t1 = j < t2",
                "if t1 != 0 goto L1",
                "L2:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testForStatement() {
        String code = "for (var j = 0; j < 3; j = j + 1) { print(j); }";
        List<String> expected = Arrays.asList(
                "t1 = 0",
                "j = t1",
                "L1:",
                "t1 = 3",
                "t2 = j < t1",
                "if t2 == 0 goto L2",
                "t1 = j",
                "call print(t1)",
                "t1 = 1",
                "t3 = j + t1",
                "j = t3",
                "goto L1",
                "L2:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testIfWithoutElse() {
        String code = "if (1 < 2) { print(100); }";
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "t2 = 2",
                "t3 = t1 < t2",
                "if t3 == 0 goto L1",
                "t2 = 100",
                "call print(t2)",
                "L1:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testNestedIfElse() {
        String code = "if (true) { if (false) { print(0);} else { print(1); }}";
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "if t1 == 0 goto L1",
                "t2 = 0",
                "if t2 == 0 goto L3",
                "t3 = 0",
                "call print(t3)",
                "goto L4",
                "L3:",
                "t3 = 1",
                "call print(t3)",
                "L4:",
                "L1:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testForWithoutInitOrUpdate() {
        String code = "var i = 0; for (; i < 2;) { print(i); i = i + 1; }";
        List<String> expected = Arrays.asList(
                "t1 = 0",
                "i = t1",
                "L1:",
                "t1 = 2",
                "t2 = i < t1",
                "if t2 == 0 goto L2",
                "t1 = i",
                "call print(t1)",
                "t1 = 1",
                "t3 = i + t1",
                "i = t3",
                "goto L1",
                "L2:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testNestedBlocks() {
        String code = "{ var a = 1; { var b = 2; print(a + b); } }";
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "a = t1",
                "t1 = 2",
                "b = t1",
                "t1 = a + b",
                "call print(t1)"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testWhileLogicalCondition() {
        String code = "var a = 0; var b = 1; while (a < 3 && b < 5) { a = a + 1; }";
        List<String> expected = Arrays.asList(
                "t1 = 0",
                "a = t1",
                "t1 = 1",
                "b = t1",
                "L1:",
                "t2 = 3",
                "t3 = a < t2",
                "t1 = 0",
                "if t3 == 0 goto L3",
                "t2 = 5",
                "t4 = b < t2",
                "t1 = t4",
                "L3:",
                "if t1 == 0 goto L2",
                "t4 = 1",
                "t3 = a + t4",
                "a = t3",
                "goto L1",
                "L2:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testNestedDoWhile() {
        String code = "var i = 0; do { var j = 0; do { j = j + 1; } while (j < 2); i = i + 1; } while (i < 2);";
        List<String> expected = Arrays.asList(
                "t1 = 0",
                "i = t1",
                "L1:",
                "t1 = 0",
                "j = t1",
                "L3:",
                "t1 = 1",
                "t2 = j + t1",
                "j = t2",
                "t2 = 2",
                "t1 = j < t2",
                "if t1 != 0 goto L3",
                "L4:",
                "t1 = 1",
                "t2 = i + t1",
                "i = t2",
                "t2 = 2",
                "t1 = i < t2",
                "if t1 != 0 goto L1",
                "L2:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testMultipleBlocks() {
        String code = "var a = 1; var b = 2;if (a < b) { print(a);} else { print(b);} while (a < 3) {a = a + 1;if (a == 2) { print(a);}}";

        List<String> expected = Arrays.asList(
                "t1 = 1",
                "a = t1",
                "t1 = 2",
                "b = t1",
                "t1 = a < b",
                "if t1 == 0 goto L1",
                "t2 = a",
                "call print(t2)",
                "goto L2",
                "L1:",
                "t2 = b",
                "call print(t2)",
                "L2:",
                "L3:",
                "t1 = 3",
                "t2 = a < t1",
                "if t2 == 0 goto L4",
                "t1 = 1",
                "t3 = a + t1",
                "a = t3",
                "t3 = 2",
                "t1 = a == t3",
                "if t1 == 0 goto L5",
                "t3 = a",
                "call print(t3)",
                "L5:",
                "goto L3",
                "L4:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

}
