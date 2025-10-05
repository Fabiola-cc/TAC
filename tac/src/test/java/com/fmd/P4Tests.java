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
    void testBreakInLoop() {
        String code = "let numbers: integer[] = [1, 2, 3, 4, 5]; ";
        List<String> expected = Arrays.asList(
                "t1 = 0",
                "i = t1",
                "L1:",
                "t2 = i < 3",
                "if t2 == 0 goto L2",
                "t3 = i == 1",
                "if t3 != 0 goto Lbreak1",
                "t4 = 1",
                "t5 = i + t4",
                "i = t5",
                "goto L1",
                "Lbreak1:",
                "L2:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testContinueInLoop() {
        String code = "for (let i = 0; i < 3; i = i + 1) { if (i == 1) { continue; } }";
        List<String> expected = Arrays.asList(
                "t1 = 0",
                "i = t1",
                "L1:",
                "t2 = i < 3",
                "if t2 == 0 goto L2",
                "t3 = i == 1",
                "if t3 != 0 goto L3",
                "call print(i)",
                "L3:",
                "t4 = 1",
                "t5 = i + t4",
                "i = t5",
                "goto L1",
                "L2:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testTryCatch() {
        String code = "try { print(1); } catch (e) { print(2); }";
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "call print(t1)",
                "goto L2",
                "L1:",
                "e = exception",
                "t2 = 2",
                "call print(t2)",
                "L2:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }


}
