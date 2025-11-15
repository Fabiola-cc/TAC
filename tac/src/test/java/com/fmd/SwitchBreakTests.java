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
                "t1 = x",
                "t2 = 1",
                "if t1 == t2 goto L2",
                "t3 = 2",
                "if t1 == t3 goto L3",
                "goto L4",
                "L2:",
                "t4 = 1",
                "call print(t4)",
                "goto L1",
                "L3:",
                "t4 = 2",
                "call print(t4)",
                "goto L1",
                "L4:",
                "t4 = 0",
                "call print(t4)",
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
                "t1 = dia",
                "t2 = \"lunes\"",
                "if t1 == t2 goto L2",
                "t3 = \"viernes\"",
                "if t1 == t3 goto L3",
                "goto L4",
                "L2:",
                "t4 = \"Inicio de semana\"",
                "call print(t4)",
                "goto L1",
                "L3:",
                "t4 = \"Fin de semana\"",
                "call print(t4)",
                "goto L1",
                "L4:",
                "t4 = \"Otro dia\"",
                "call print(t4)",
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
                "t1 = num",
                "t2 = 1",
                "if t1 == t2 goto L2",
                "t3 = 2",
                "if t1 == t3 goto L3",
                "goto L1",
                "L2:",
                "t4 = 1",
                "call print(t4)",
                "goto L1",
                "L3:",
                "t4 = 2",
                "call print(t4)",
                "goto L1",
                "L1:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    // TESTS DE BREAK EN SWITCH
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
                "t1 = x",
                "t2 = 1",
                "if t1 == t2 goto L2",
                "t3 = 2",
                "if t1 == t3 goto L3",
                "goto L4",
                "L2:",
                "t4 = 1",
                "call print(t4)",
                "goto L1",
                "L3:",
                "t4 = 2",
                "call print(t4)",
                "goto L1",
                "L4:",
                "t4 = 0",
                "call print(t4)",
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
                "t1 = 0",
                "y = t1",
                "t1 = x",
                "t2 = 1",
                "if t1 == t2 goto L2",
                "t3 = 2",
                "if t1 == t3 goto L3",
                "goto L1",
                "L2:",
                "t4 = 10",
                "y = t4",
                "t4 = y",
                "call print(t4)",
                "goto L1",
                "L3:",
                "t4 = 20",
                "y = t4",
                "t4 = y",
                "call print(t4)",
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
                "t1 = x",
                "t2 = 1",
                "if t1 == t2 goto L2",
                "t3 = 2",
                "if t1 == t3 goto L3",
                "goto L4",
                "L2:",
                "t4 = 1",
                "call print(t4)",
                "goto L1",
                "L3:",
                "t4 = 2",
                "call print(t4)",
                "goto L1",
                "L4:",
                "t4 = 0",
                "call print(t4)",
                "L1:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    // TESTS DE BREAK EN LOOPS

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
                "t1 = 10",
                "t2 = i < t1",
                "if t2 == 0 goto L2",
                "t1 = 5",
                "t3 = i == t1",
                "if t3 == 0 goto L3",
                "goto L2",
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
                "t1 = 5",
                "t2 = i < t1",
                "if t2 == 0 goto L2",
                "t1 = 3",
                "t3 = i == t1",
                "if t3 == 0 goto L3",
                "goto L2",
                "L3:",
                "t3 = i",
                "call print(t3)",
                "t3 = 1",
                "t1 = i + t3",
                "i = t1",
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
                "t1 = 3",
                "t2 = i < t1",
                "if t2 == 0 goto L2",
                "t1 = i",
                "t3 = 0",
                "if t1 == t3 goto L4",
                "t4 = 1",
                "if t1 == t4 goto L5",
                "goto L3",
                "L4:",
                "t5 = 0",
                "call print(t5)",
                "goto L3",
                "L5:",
                "t5 = 1",
                "call print(t5)",
                "goto L3",
                "L3:",
                "t4 = 1",
                "t1 = i + t4",
                "i = t1",
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
                "t1 = x",
                "goto L2",
                "L2:",
                "t2 = 999",
                "call print(t2)",
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
                "t1 = x",
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
                "t1 = 5",
                "t2 = x < t1",
                "if t2 == 0 goto L2",
                "t1 = x",
                "t3 = 1",
                "if t1 == t3 goto L4",
                "t4 = 2",
                "if t1 == t4 goto L5",
                "goto L3",
                "L4:",
                "goto L3",          // Break del switch (SIN duplicado)
                "L5:",
                "t5 = 1",
                "t6 = x + t5",
                "x = t6",
                "goto L3",          // Break automático
                "L3:",
                "t4 = 1",
                "t1 = x + t4",
                "x = t1",
                "goto L1",
                "L2:"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }
}