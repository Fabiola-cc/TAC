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
                "t2 = 3",
                "t3 = i < t2",
                "if t3 == 0 goto L2",
                "t4 = 1",
                "t5 = i == t4",
                "if t5 == 0 goto L3",
                "L3:",
                "t6 = 1",
                "t7 = i + t6",
                "i = t7",
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
                "t2 = 2",
                "numbers[1] = t2",
                "t3 = 3",
                "numbers[2] = t3",
                "t4 = 4",
                "numbers[3] = t4",
                "t5 = 5",
                "numbers[4] = t5",
                "t6 = 0",
                "t7 = 5",
                "L1:",
                "t8 = t6 < t7",
                "if t8 == 0 goto L2",
                "t9 = numbers[t6]",
                "num = t9",
                "t10 = num",
                "call print(t10)",
                "t11 = t6 + 1",
                "t6 = t11",
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
                "t2 = 2",
                "b = t2",
                "t3 = a > b",
                "if t3 == 1 goto L1",
                "goto L2",
                "L1:",
                "t4 = a",
                "goto L3",
                "L2:",
                "t4 = b",
                "L3:",
                "x = t4"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testNestedTernary() {
        String code = "let a: integer = 1;\nlet b: integer = 2;\nlet c: integer = 3;\nlet x: integer = (a > b) ? a : ((b > c) ? b : c);";
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "a = t1",
                "t2 = 2",
                "b = t2",
                "t3 = 3",
                "c = t3",
                "t4 = a > b",
                "if t4 == 1 goto L1",
                "goto L2",
                "L1:",
                "t5 = a",
                "goto L3",
                "L2:",
                "t6 = b > c",
                "if t6 == 1 goto L4",
                "goto L5",
                "L4:",
                "t7 = b",
                "goto L6",
                "L5:",
                "t7 = c",
                "L6:",
                "t5 = t7",
                "L3:",
                "x = t5"
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
                "t2 = 2",
                "numbers[1] = t2",
                "t3 = 3",
                "numbers[2] = t3",
                "t4 = 4",
                "numbers[3] = t4",
                "t5 = 5",
                "numbers[4] = t5"
        );

        List<String> actual = testInit.generateTAC(code); // tu método para generar TAC

        assertEquals(expected, actual);
    }

    @Test
    void testHandleArrayAccess() {
        String code = "let numbers: integer[] = [1, 2, 3]; let x = numbers[1];";

        List<String> expected = Arrays.asList(
                "t1 = 1",
                "numbers[0] = t1",
                "t2 = 2",
                "numbers[1] = t2",
                "t3 = 3",
                "numbers[2] = t3",
                "t4 = 1",
                "t5 = numbers[t4]", // temporal que captura numbers[1]
                "x = t5"
        );

        List<String> actual = testInit.generateTAC(code);

        assertEquals(expected, actual);
    }




}
