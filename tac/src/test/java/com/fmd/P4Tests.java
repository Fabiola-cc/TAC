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

@DisplayName("Tests para P4")
public class P4Tests {
    TestInit testInit = new TestInit();

    @Test
    void testContinueInLoop() {
        String code = "for (let i = 0; i < 3; i = i + 1) { if (i == 1) { continue; } }";
        List<String> expected = Arrays.asList(
                "t1 = 0",
                "i = t1",
                "L1:",
                "t1 = 3",
                "t2 = i < t1",
                "if t2 == 0 goto L2",
                "t1 = 1",
                "t3 = i == t1",
                "if t3 == 0 goto L3",
                "goto L1",
                "L3:",
                "t3 = 1",
                "t1 = i + t3",
                "i = t1",
                "goto L1",
                "L2:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testForEachLoop() {
        String code = "let numbers: integer[] = [1, 2, 3, 4, 5];\n" +
                "foreach (num in numbers) {\n" +
                "    print(num);\n" +
                "}\n";
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "numbers[0] = t1",
                "t1 = 2",
                "numbers[1] = t1",
                "t1 = 3",
                "numbers[2] = t1",
                "t1 = 4",
                "numbers[3] = t1",
                "t1 = 5",
                "numbers[4] = t1",
                "t1 = 0",
                "t2 = 5",
                "L1:",
                "t3 = t1 < t2",
                "if t3 == 0 goto L2",
                "t4 = numbers[t1]",
                "num = t4",
                "t5 = num",
                "call print(t5)",
                "t5 = t1 + 1",
                "t1 = t5",
                "goto L1",
                "L2:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testConstDeclaration() {
        String code = "const MSG: string = \"Hello\";";
        List<String> expected = Arrays.asList(
                "t1 = \"Hello\"",
                "MSG = t1"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testTernaryExpression() {
        String code = "let a: integer = 3; let b: integer = 2; let x: integer = (a > b) ? a : b;";
        List<String> expected = Arrays.asList(
                "t1 = 3",
                "a = t1",
                "t1 = 2",
                "b = t1",
                "t1 = a > b",
                "if t1 == 1 goto L1",
                "goto L2",
                "L1:",
                "t2 = a",
                "goto L3",
                "L2:",
                "t2 = b",
                "L3:",
                "x = t2"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testNestedTernary() {
        String code = "let a: integer = 1;\nlet b: integer = 2;\nlet c: integer = 3;\nlet x: integer = (a > b) ? a : ((b > c) ? b : c);";
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "a = t1",
                "t1 = 2",
                "b = t1",
                "t1 = 3",
                "c = t1",
                "t1 = a > b",
                "if t1 == 1 goto L1",
                "goto L2",
                "L1:",
                "t2 = a",
                "goto L3",
                "L2:",
                "t3 = b > c",
                "if t3 == 1 goto L4",
                "goto L5",
                "L4:",
                "t4 = b",
                "goto L6",
                "L5:",
                "t4 = c",
                "L6:",
                "t2 = t4",
                "L3:",
                "x = t2"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testArrayLiteralAssignment() {
        String code = "let numbers: integer[] = [1, 2, 3, 4, 5];";

        // Esto es lo que esperamos que genere el TAC según tu visitArrayLiteral
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "numbers[0] = t1",
                "t1 = 2",
                "numbers[1] = t1",
                "t1 = 3",
                "numbers[2] = t1",
                "t1 = 4",
                "numbers[3] = t1",
                "t1 = 5",
                "numbers[4] = t1"
        );

        List<String> actual = testInit.generateTAC(code);

        assertEquals(expected, actual);
    }

    @Test
    void testHandleArrayAccess() {
        String code = "let numbers: integer[] = [1, 2, 3]; let x = numbers[1];";

        List<String> expected = Arrays.asList(
                "t1 = 1",
                "numbers[0] = t1",
                "t1 = 2",
                "numbers[1] = t1",
                "t1 = 3",
                "numbers[2] = t1",
                "t1 = 1",
                "t2 = numbers[t1]", // temporal que captura numbers[1]
                "x = t2"
        );

        List<String> actual = testInit.generateTAC(code);

        assertEquals(expected, actual);
    }




}
