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
    void testRelationalOperators() {
        String code = "let a = 5; let b = 10; print(a < b); print(a == b); print(a != b);";
        List<String> expected = Arrays.asList(
                "t1 = 5",
                "a = t1",
                "t2 = 10",
                "b = t2",
                "t3 = a < b",
                "t4 = t3",
                "call print(t4)",
                "t5 = a == b",
                "t6 = t5",
                "call print(t6)",
                "t7 = a != b",
                "t8 = t7",
                "call print(t8)"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testLogicalOperators() {
        String code = "let x = true; let y = false; print(x && y); print(x || y);";
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "x = t1",
                "t2 = 0",
                "y = t2",
                "t3 = 0",
                "if x == 0 goto L1",
                "t3 = y",
                "L1:",
                "t4 = t3",
                "call print(t4)",
                "t5 = 1",
                "if x != 0 goto L2",
                "t5 = y",
                "L2:",
                "t6 = t5",
                "call print(t6)"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testLogicalCombination() {
        String code = "let a = 1; let b = 2; if (a < b && b < 5){ print(1);}";
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "a = t1",
                "t2 = 2",
                "b = t2",
                "t4 = a < b",
                "t3 = 0",
                "if t4 == 0 goto L1",
                "t5 = 5",
                "t6 = b < t5",
                "t3 = t6",
                "L1:",
                "if t3 == 0 goto L2",
                "t7 = 1",
                "t8 = t7",
                "call print(t8)",
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
                "t3 = t2",
                "call print(t3)",
                "goto L2",
                "L1:",
                "t4 = 0",
                "t5 = t4",
                "call print(t5)",
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
                "t4 = i",
                "call print(t4)",
                "t5 = 1",
                "t6 = i + t5",
                "i = t6",
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
                "t2 = j",
                "call print(t2)",
                "t3 = 1",
                "t4 = j + t3",
                "j = t4",
                "t5 = 3",
                "t6 = j < t5",
                "if t6 != 0 goto L1",
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
                "t2 = 3",
                "t3 = j < t2",
                "if t3 == 0 goto L2",
                "t4 = j",
                "call print(t4)",
                "t5 = 1",
                "t6 = j + t5",
                "j = t6",
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
                "t4 = 100",
                "t5 = t4",
                "call print(t5)",
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
                "t4 = t3",
                "call print(t4)",
                "goto L4",
                "L3:",
                "t5 = 1",
                "t6 = t5",
                "call print(t6)",
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
                "t2 = 2",
                "t3 = i < t2",
                "if t3 == 0 goto L2",
                "t4 = i",
                "call print(t4)",
                "t5 = 1",
                "t6 = i + t5",
                "i = t6",
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
                "t2 = 2",
                "b = t2",
                "t3 = a + b",
                "t4 = t3",
                "call print(t4)"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testWhileLogicalCondition() {
        String code = "var a = 0; var b = 1; while (a < 3 && b < 5) { a = a + 1; }";
        List<String> expected = Arrays.asList(
                "t1 = 0",
                "a = t1",
                "t2 = 1",
                "b = t2",
                "L1:",
                "t4 = 3",
                "t5 = a < t4",
                "t3 = 0",
                "if t5 == 0 goto L3",
                "t6 = 5",
                "t7 = b < t6",
                "t3 = t7",
                "L3:",
                "if t3 == 0 goto L2",
                "t8 = 1",
                "t9 = a + t8",
                "a = t9",
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
                "t2 = 0",
                "j = t2",
                "L3:",
                "t3 = 1",
                "t4 = j + t3",
                "j = t4",
                "t5 = 2",
                "t6 = j < t5",
                "if t6 != 0 goto L3",
                "L4:",
                "t7 = 1",
                "t8 = i + t7",
                "i = t8",
                "t9 = 2",
                "t10 = i < t9",
                "if t10 != 0 goto L1",
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
                "t2 = 2",
                "b = t2",
                "t3 = a < b",
                "if t3 == 0 goto L1",
                "t4 = a",
                "call print(t4)",
                "goto L2",
                "L1:",
                "t5 = b",
                "call print(t5)",
                "L2:",
                "L3:",
                "t6 = 3",
                "t7 = a < t6",
                "if t7 == 0 goto L4",
                "t8 = 1",
                "t9 = a + t8",
                "a = t9",
                "t10 = 2",
                "t11 = a == t10",
                "if t11 == 0 goto L5",
                "t12 = a",
                "call print(t12)",
                "L5:",
                "goto L3",
                "L4:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }


}
