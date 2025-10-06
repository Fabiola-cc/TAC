package com.fmd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tests para Switch y Break")
public class SwitchBreakTests {
    TestInit testInit = new TestInit();

    // ========================================
    // TESTS DE SWITCH BÁSICO
    // ========================================

    @Test
    @DisplayName("Switch básico con integer")
    void testBasicSwitchInteger() {
        String code = """
            let x: integer = 2;
            switch (x) {
                case 1:
                    print(1);
                case 2:
                    print(2);
                default:
                    print(0);
            }
        """;
        List<String> expected = Arrays.asList(
                "t1 = 2",
                "x = t1",
                "t2 = x",
                "t3 = 1",
                "if t2 == t3 goto L2",
                "t4 = 2",
                "if t2 == t4 goto L3",
                "goto L4",
                "L2:",
                "t5 = 1",
                "t6 = t5",
                "call print(t6)",
                "goto L1",          // Break automático (sin duplicado)
                "L3:",
                "t7 = 2",
                "t8 = t7",
                "call print(t8)",
                "goto L1",          // Break automático (sin duplicado)
                "L4:",
                "t9 = 0",
                "t10 = t9",
                "call print(t10)",
                "L1:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Switch con string")
    void testSwitchWithString() {
        String code = """
            let dia: string = "lunes";
            switch (dia) {
                case "lunes":
                    print("Inicio de semana");
                case "viernes":
                    print("Fin de semana");
                default:
                    print("Otro dia");
            }
        """;
        List<String> expected = Arrays.asList(
                "t1 = \"lunes\"",
                "dia = t1",
                "t2 = dia",
                "t3 = \"lunes\"",
                "if t2 == t3 goto L2",
                "t4 = \"viernes\"",
                "if t2 == t4 goto L3",
                "goto L4",
                "L2:",
                "t5 = \"Inicio de semana\"",
                "t6 = t5",
                "call print(t6)",
                "goto L1",
                "L3:",
                "t7 = \"Fin de semana\"",
                "t8 = t7",
                "call print(t8)",
                "goto L1",
                "L4:",
                "t9 = \"Otro dia\"",
                "t10 = t9",
                "call print(t10)",
                "L1:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Switch sin default")
    void testSwitchWithoutDefault() {
        String code = """
            let num: integer = 1;
            switch (num) {
                case 1:
                    print(1);
                case 2:
                    print(2);
            }
        """;
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "num = t1",
                "t2 = num",
                "t3 = 1",
                "if t2 == t3 goto L2",
                "t4 = 2",
                "if t2 == t4 goto L3",
                "goto L1",
                "L2:",
                "t5 = 1",
                "t6 = t5",
                "call print(t6)",
                "goto L1",
                "L3:",
                "t7 = 2",
                "t8 = t7",
                "call print(t8)",
                "goto L1",
                "L1:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    // ========================================
    // TESTS DE BREAK EN SWITCH
    // ========================================

    @Test
    @DisplayName("Switch con break explícito")
    void testSwitchWithExplicitBreak() {
        String code = """
            let x: integer = 1;
            switch (x) {
                case 1:
                    print(1);
                    break;
                case 2:
                    print(2);
                    break;
                default:
                    print(0);
            }
        """;
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "x = t1",
                "t2 = x",
                "t3 = 1",
                "if t2 == t3 goto L2",
                "t4 = 2",
                "if t2 == t4 goto L3",
                "goto L4",
                "L2:",
                "t5 = 1",
                "t6 = t5",
                "call print(t6)",
                "goto L1",          // Break explícito (SIN duplicado)
                "L3:",
                "t7 = 2",
                "t8 = t7",
                "call print(t8)",
                "goto L1",          // Break explícito (SIN duplicado)
                "L4:",
                "t9 = 0",
                "t10 = t9",
                "call print(t10)",
                "L1:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Switch con múltiples statements por case")
    void testSwitchMultipleStatementsPerCase() {
        String code = """
            let x: integer = 1;
            let y: integer = 0;
            switch (x) {
                case 1:
                    y = 10;
                    print(y);
                case 2:
                    y = 20;
                    print(y);
            }
        """;
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "x = t1",
                "t2 = 0",
                "y = t2",
                "t3 = x",
                "t4 = 1",
                "if t3 == t4 goto L2",
                "t5 = 2",
                "if t3 == t5 goto L3",
                "goto L1",
                "L2:",
                "t6 = 10",
                "y = t6",
                "t7 = y",
                "call print(t7)",
                "goto L1",
                "L3:",
                "t8 = 20",
                "y = t8",
                "t9 = y",
                "call print(t9)",
                "goto L1",
                "L1:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Switch mezclado: break explícito e implícito")
    void testSwitchMixedBreaks() {
        String code = """
            let x: integer = 1;
            switch (x) {
                case 1:
                    print(1);
                    break;
                case 2:
                    print(2);
                default:
                    print(0);
            }
        """;
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "x = t1",
                "t2 = x",
                "t3 = 1",
                "if t2 == t3 goto L2",
                "t4 = 2",
                "if t2 == t4 goto L3",
                "goto L4",
                "L2:",
                "t5 = 1",
                "t6 = t5",
                "call print(t6)",
                "goto L1",
                "L3:",
                "t7 = 2",
                "t8 = t7",
                "call print(t8)",
                "goto L1",
                "L4:",
                "t9 = 0",
                "t10 = t9",
                "call print(t10)",
                "L1:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    // ========================================
    // TESTS DE BREAK EN LOOPS
    // ========================================

    @Test
    @DisplayName("Break en while loop")
    void testBreakInWhile() {
        String code = """
            let i: integer = 0;
            while (i < 10) {
                if (i == 5) {
                    break;
                }
                i = i + 1;
            }
        """;
        List<String> expected = Arrays.asList(
                "t1 = 0",
                "i = t1",
                "L1:",
                "t2 = 10",
                "t3 = i < t2",
                "if t3 == 0 goto L2",
                "t4 = 5",
                "t5 = i == t4",
                "if t5 == 0 goto L3",
                "goto L2",
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
    @DisplayName("Break en for loop")
    void testBreakInFor() {
        String code = """
            for (let i: integer = 0; i < 5; i = i + 1) {
                if (i == 3) {
                    break;
                }
                print(i);
            }
        """;
        List<String> expected = Arrays.asList(
                "t1 = 0",
                "i = t1",
                "L1:",
                "t2 = 5",
                "t3 = i < t2",
                "if t3 == 0 goto L2",
                "t4 = 3",
                "t5 = i == t4",
                "if t5 == 0 goto L3",
                "goto L2",
                "L3:",
                "t6 = i",
                "call print(t6)",
                "t7 = 1",
                "t8 = i + t7",
                "i = t8",
                "goto L1",
                "L2:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    // ========================================
    // TESTS DE SWITCH ANIDADO
    // ========================================

    @Test
    @DisplayName("Switch anidado en loop")
    void testSwitchInsideLoop() {
        String code = """
            for (let i: integer = 0; i < 3; i = i + 1) {
                switch (i) {
                    case 0:
                        print(0);
                    case 1:
                        print(1);
                        break;
                }
            }
        """;
        List<String> expected = Arrays.asList(
                "t1 = 0",
                "i = t1",
                "L1:",
                "t2 = 3",
                "t3 = i < t2",
                "if t3 == 0 goto L2",
                "t4 = i",
                "t5 = 0",
                "if t4 == t5 goto L4",
                "t6 = 1",
                "if t4 == t6 goto L5",
                "goto L3",
                "L4:",
                "t7 = 0",
                "t8 = t7",
                "call print(t8)",
                "goto L3",
                "L5:",
                "t9 = 1",
                "t10 = t9",
                "call print(t10)",
                "goto L3",
                "L3:",
                "t11 = 1",
                "t12 = i + t11",
                "i = t12",
                "goto L1",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    // ========================================
    // TESTS DE CASOS EDGE
    // ========================================

    @Test
    @DisplayName("Switch con solo default")
    void testSwitchOnlyDefault() {
        String code = """
            let x: integer = 5;
            switch (x) {
                default:
                    print(999);
            }
        """;
        List<String> expected = Arrays.asList(
                "t1 = 5",
                "x = t1",
                "t2 = x",
                "goto L2",
                "L2:",
                "t3 = 999",
                "t4 = t3",
                "call print(t4)",
                "L1:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Switch vacío (sin cases)")
    void testEmptySwitch() {
        String code = """
            let x: integer = 1;
            switch (x) {
            }
        """;
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "x = t1",
                "t2 = x",
                "goto L1",
                "L1:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Break múltiple en diferentes contextos")
    void testBreakInDifferentContexts() {
        String code = """
            let x: integer = 1;
            while (x < 5) {
                switch (x) {
                    case 1:
                        break;
                    case 2:
                        x = x + 1;
                }
                x = x + 1;
            }
        """;
        List<String> expected = Arrays.asList(
                "t1 = 1",
                "x = t1",
                "L1:",
                "t2 = 5",
                "t3 = x < t2",
                "if t3 == 0 goto L2",
                "t4 = x",
                "t5 = 1",
                "if t4 == t5 goto L4",
                "t6 = 2",
                "if t4 == t6 goto L5",
                "goto L3",
                "L4:",
                "goto L3",          // Break del switch (SIN duplicado)
                "L5:",
                "t7 = 1",
                "t8 = x + t7",
                "x = t8",
                "goto L3",          // Break automático
                "L3:",
                "t9 = 1",
                "t10 = x + t9",
                "x = t10",
                "goto L1",
                "L2:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }
}