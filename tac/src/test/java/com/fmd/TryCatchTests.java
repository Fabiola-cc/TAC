package com.fmd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tests completos para Try-Catch")
public class TryCatchTests {
    TestInit testInit = new TestInit();

    // ========================================
    // TESTS BÁSICOS DE TRY-CATCH
    // ========================================

    @Test
    @DisplayName("Try-catch básico con print")
    void testBasicTryCatchWithPrint() {
        String code = "" +
                "try " +
                "   { print(1); } " +
                "catch (e) " +
                "   { print(2); }";

        List<String> expected = Arrays.asList(
                "try_begin L1",
                "t1 = 1",
                "t2 = t1",           // Temporal extra generado
                "call print(t2)",
                "try_end",
                "goto L2",
                "L1:",
                "e = exception",
                "t3 = 2",
                "t4 = t3",           // Temporal extra generado
                "call print(t4)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Try-catch con declaración de variables")
    void testTryCatchWithVariableDeclaration() {
        String code = """
            try {
                let x: integer = 10;
                print(x);
            } catch (err) {
                let y: integer = 0;
                print(y);
            }
            """;

        List<String> expected = Arrays.asList(
                "try_begin L1",
                "t1 = 10",
                "x = t1",
                "t2 = x",
                "call print(t2)",
                "try_end",
                "goto L2",
                "L1:",
                "err = exception",
                "t3 = 0",
                "y = t3",
                "t4 = y",
                "call print(t4)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Try-catch con operaciones aritméticas")
    void testTryCatchWithArithmetic() {
        String code = """
            try {
                let a: integer = 10;
                let b: integer = 5;
                let c: integer = a + b;
                print(c);
            } catch (e) {
                print(0);
            }
            """;

        List<String> expected = Arrays.asList(
                "try_begin L1",
                "t1 = 10",
                "a = t1",
                "t2 = 5",
                "b = t2",
                "t3 = a + b",
                "c = t3",
                "t4 = c",
                "call print(t4)",
                "try_end",
                "goto L2",
                "L1:",
                "e = exception",
                "t5 = 0",
                "t6 = t5",
                "call print(t6)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Try-catch con división")
    void testTryCatchWithDivision() {
        String code = """
            let x: integer = 10;
            let y: integer = 0;
            try {
                let result: integer = x / y;
                print(result);
            } catch (e) {
                print("Error de división");
            }
            """;

        List<String> expected = Arrays.asList(
                "t1 = 10",
                "x = t1",
                "t2 = 0",
                "y = t2",
                "try_begin L1",
                "t3 = x / y",
                "result = t3",
                "t4 = result",
                "call print(t4)",
                "try_end",
                "goto L2",
                "L1:",
                "e = exception",
                "t5 = \"Error de división\"",
                "t6 = t5",
                "call print(t6)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    // ========================================
    // TESTS DE TRY-CATCH CON ARRAYS
    // ========================================

    @Test
    @DisplayName("Try-catch con acceso a array")
    void testTryCatchWithArrayAccess() {
        String code = """
            let arr: integer[] = [1, 2, 3];
            try {
                let elem: integer = arr[10];
                print(elem);
            } catch (e) {
                print("Índice fuera de rango");
            }
            """;

        List<String> expected = Arrays.asList(
                "t1 = 1",
                "arr[0] = t1",
                "t2 = 2",
                "arr[1] = t2",
                "t3 = 3",
                "arr[2] = t3",
                "try_begin L1",
                "t4 = 10",
                "t5 = arr[t4]",
                "elem = t5",
                "t6 = elem",
                "call print(t6)",
                "try_end",
                "goto L2",
                "L1:",
                "e = exception",
                "t7 = \"Índice fuera de rango\"",
                "t8 = t7",
                "call print(t8)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Try-catch con modificación de array")
    void testTryCatchWithArrayModification() {
        String code = """
            let numeros: integer[] = [1, 2, 3];
            try {
                numeros[0] = 100;
                print(numeros[0]);
            } catch (e) {
                print("Error");
            }
            """;

        List<String> expected = Arrays.asList(
                "t1 = 1",
                "numeros[0] = t1",
                "t2 = 2",
                "numeros[1] = t2",
                "t3 = 3",
                "numeros[2] = t3",
                "try_begin L1",
                "t4 = 100",
                "numeros[0] = t4",
                "t5 = 0",
                "t6 = numeros[t5]",
                "t7 = t6",
                "call print(t7)",
                "try_end",
                "goto L2",
                "L1:",
                "e = exception",
                "t8 = \"Error\"",
                "t9 = t8",
                "call print(t9)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    // ========================================
    // TESTS DE TRY-CATCH ANIDADOS
    // ========================================

    @Test
    @DisplayName("Try-catch anidados")
    void testNestedTryCatch() {
        String code = """
            try {
                print(1);
                try {
                    print(2);
                } catch (inner) {
                    print(3);
                }
                print(4);
            } catch (outer) {
                print(5);
            }
            """;

        List<String> expected = Arrays.asList(
                "try_begin L1",
                "t1 = 1",
                "t2 = t1",
                "call print(t2)",

                "try_begin L3",
                "t3 = 2",
                "t4 = t3",
                "call print(t4)",
                "try_end",
                "goto L4",

                "L3:",
                "inner = exception",
                "t5 = 3",
                "t6 = t5",
                "call print(t6)",
                "L4:",

                "t7 = 4",
                "t8 = t7",
                "call print(t8)",
                "try_end",
                "goto L2",

                "L1:",
                "outer = exception",
                "t9 = 5",
                "t10 = t9",
                "call print(t10)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Try-catch anidados con variables")
    void testNestedTryCatchWithVariables() {
        String code = """
            try {
                let x: integer = 10;
                try {
                    let y: integer = x / 0;
                    print(y);
                } catch (e1) {
                    print("Error interno");
                }
            } catch (e2) {
                print("Error externo");
            }
            """;

        List<String> expected = Arrays.asList(
                "try_begin L1",
                "t1 = 10",
                "x = t1",

                "try_begin L3",
                "t2 = 0",
                "t3 = x / t2",
                "y = t3",
                "t4 = y",
                "call print(t4)",
                "try_end",
                "goto L4",

                "L3:",
                "e1 = exception",
                "t5 = \"Error interno\"",
                "t6 = t5",
                "call print(t6)",
                "L4:",

                "try_end",
                "goto L2",

                "L1:",
                "e2 = exception",
                "t7 = \"Error externo\"",
                "t8 = t7",
                "call print(t8)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    // ========================================
    // TESTS DE TRY-CATCH CON CONTROL DE FLUJO
    // ========================================

    @Test
    @DisplayName("Try-catch con if-else dentro del try")
    void testTryCatchWithIfElse() {
        String code = """
            try {
                let x: integer = 10;
                if (x > 5) {
                    print(1);
                } else {
                    print(0);
                }
            } catch (e) {
                print("Error");
            }
            """;

        List<String> expected = Arrays.asList(
                "try_begin L1",
                "t1 = 10",
                "x = t1",
                "t2 = 5",
                "t3 = x > t2",
                "if t3 == 0 goto L3",
                "t4 = 1",
                "t5 = t4",
                "call print(t5)",
                "goto L4",
                "L3:",
                "t6 = 0",
                "t7 = t6",
                "call print(t7)",
                "L4:",
                "try_end",
                "goto L2",
                "L1:",
                "e = exception",
                "t8 = \"Error\"",
                "t9 = t8",
                "call print(t9)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Try-catch dentro de un loop")
    void testTryCatchInsideLoop() {
        String code = """
            let i: integer = 0;
            while (i < 3) {
                try {
                    print(i);
                } catch (e) {
                    print("Error");
                }
                i = i + 1;
            }
            """;

        List<String> expected = Arrays.asList(
                "t1 = 0",
                "i = t1",

                "L1:",
                "t2 = 3",
                "t3 = i < t2",
                "if t3 == 0 goto L2",

                "try_begin L3",
                "t4 = i",
                "call print(t4)",
                "try_end",
                "goto L4",
                "L3:",
                "e = exception",
                "t5 = \"Error\"",
                "t6 = t5",
                "call print(t6)",
                "L4:",

                "t7 = 1",
                "t8 = i + t7",
                "i = t8",
                "goto L1",

                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Try-catch con while dentro del try")
    void testTryCatchWithWhileInside() {
        String code = """
            try {
                let i: integer = 0;
                while (i < 3) {
                    print(i);
                    i = i + 1;
                }
            } catch (e) {
                print("Error");
            }
            """;

        List<String> expected = Arrays.asList(
                "try_begin L1",
                "t1 = 0",
                "i = t1",

                "L3:",
                "t2 = 3",
                "t3 = i < t2",
                "if t3 == 0 goto L4",

                "t4 = i",
                "call print(t4)",
                "t5 = 1",
                "t6 = i + t5",
                "i = t6",
                "goto L3",

                "L4:",
                "try_end",
                "goto L2",

                "L1:",
                "e = exception",
                "t7 = \"Error\"",
                "t8 = t7",
                "call print(t8)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Try-catch con for loop dentro")
    void testTryCatchWithForLoop() {
        String code = """
            try {
                for (let i: integer = 0; i < 3; i = i + 1) {
                    print(i);
                }
            } catch (e) {
                print("Error");
            }
            """;

        List<String> expected = Arrays.asList(
                "try_begin L1",

                "t1 = 0",
                "i = t1",

                "L3:",
                "t2 = 3",
                "t3 = i < t2",
                "if t3 == 0 goto L4",

                "t4 = i",
                "call print(t4)",

                "t5 = 1",
                "t6 = i + t5",
                "i = t6",
                "goto L3",

                "L4:",
                "try_end",
                "goto L2",

                "L1:",
                "e = exception",
                "t7 = \"Error\"",
                "t8 = t7",
                "call print(t8)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    // ========================================
    // TESTS DE TRY-CATCH CON BREAK/CONTINUE
    // ========================================

    @Test
    @DisplayName("Try-catch con break dentro del try")
    void testTryCatchWithBreak() {
        String code = """
            while (true) {
                try {
                    break;
                } catch (e) {
                    print("Error");
                }
            }
            """;

        List<String> expected = Arrays.asList(
                "L1:",
                "t1 = 1",
                "if t1 == 0 goto L2",

                "try_begin L3",
                "goto L2",
                "try_end",
                "goto L4",

                "L3:",
                "e = exception",
                "t2 = \"Error\"",
                "t3 = t2",
                "call print(t3)",
                "L4:",

                "goto L1",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Try-catch con continue dentro del try")
    void testTryCatchWithContinue() {
        String code = """
            let i: integer = 0;
            while (i < 3) {
                try {
                    i = i + 1;
                    continue;
                    print("No se ejecuta");
                } catch (e) {
                    print("Error");
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

                "try_begin L3",
                "t4 = 1",
                "t5 = i + t4",
                "i = t5",
                "goto L1",
                "t6 = \"No se ejecuta\"",
                "t7 = t6",
                "call print(t7)",
                "try_end",
                "goto L4",

                "L3:",
                "e = exception",
                "t8 = \"Error\"",
                "t9 = t8",
                "call print(t9)",
                "L4:",

                "goto L1",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    // ========================================
    // TESTS DE TRY-CATCH CON FUNCIONES
    // ========================================

    @Test
    @DisplayName("Try-catch con llamada a función")
    void testTryCatchWithFunctionCall() {
        String code = """
            function divide(a: integer, b: integer): integer {
                return a / b;
            }
            
            try {
                let result: integer = divide(10, 0);
                print(result);
            } catch (e) {
                print("Error de división");
            }
            """;

        List<String> expected = Arrays.asList(
                "divide:",
                "t1 = a / b",
                "return t1",
                "end divide",
                "try_begin L1",
                "t4 = 10",
                "t3 = t4",
                "t6 = 0",
                "t5 = t6",
                "t2 = call divide(t3, t5)",
                "result = t2",
                "t7 = result",
                "call print(t7)",
                "try_end",
                "goto L2",
                "L1:",
                "e = exception",
                "t8 = \"Error de división\"",
                "t9 = t8",
                "call print(t9)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Try-catch dentro de una función")
    void testTryCatchInsideFunction() {
        String code = """
            function safeDivide(a: integer, b: integer): integer {
                try {
                    return a / b;
                } catch (e) {
                    return 0;
                }
            }
            
            let x: integer = safeDivide(10, 2);
            """;

        List<String> expected = Arrays.asList(
                "safeDivide:",
                "try_begin L1",
                "t1 = a / b",
                "return t1",
                "try_end",
                "goto L2",
                "L1:",
                "e = exception",
                "t2 = 0",
                "return t2",
                "L2:",
                "end safeDivide",
                "t5 = 10",
                "t4 = t5",
                "t7 = 2",
                "t6 = t7",
                "t3 = call safeDivide(t4, t6)",
                "x = t3"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    // ========================================
    // TESTS DE TRY-CATCH CON OPERACIONES COMPLEJAS
    // ========================================

    @Test
    @DisplayName("Try-catch con múltiples statements complejos")
    void testTryCatchWithComplexStatements() {
        String code = """
            try {
                let a: integer = 10;
                let b: integer = 20;
                let c: integer = a + b;
                
                if (c > 25) {
                    print("Grande");
                } else {
                    print("Pequeño");
                }
                
                let d: integer = c * 2;
                print(d);
            } catch (e) {
                print("Error complejo");
            }
            """;

        List<String> expected = Arrays.asList(
                "try_begin L1",

                "t1 = 10",
                "a = t1",
                "t2 = 20",
                "b = t2",
                "t3 = a + b",
                "c = t3",

                "t4 = 25",
                "t5 = c > t4",
                "if t5 == 0 goto L3",
                "t6 = \"Grande\"",
                "t7 = t6",
                "call print(t7)",
                "goto L4",
                "L3:",
                "t8 = \"Pequeño\"",
                "t9 = t8",
                "call print(t9)",
                "L4:",

                "t10 = 2",
                "t11 = c * t10",
                "d = t11",
                "t12 = d",
                "call print(t12)",

                "try_end",
                "goto L2",

                "L1:",
                "e = exception",
                "t13 = \"Error complejo\"",
                "t14 = t13",
                "call print(t14)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Try-catch con operaciones lógicas")
    void testTryCatchWithLogicalOperations() {
        String code = """
            try {
                let x: boolean = true;
                let y: boolean = false;
                let z: boolean = x && y;
                
                if (z || !x) {
                    print("Condición compleja");
                }
            } catch (e) {
                print("Error");
            }
            """;

        List<String> expected = Arrays.asList(
                "try_begin L1",

                "t1 = 1",
                "x = t1",
                "t2 = 0",
                "y = t2",

                "t3 = 0",
                "if x == 0 goto L3",
                "t3 = y",
                "L3:",
                "z = t3",

                "t4 = 1",
                "if z != 0 goto L4",
                "t5 = !x",
                "t4 = t5",
                "L4:",

                "if t4 == 0 goto L5",
                "t6 = \"Condición compleja\"",
                "t7 = t6",
                "call print(t7)",
                "L5:",

                "try_end",
                "goto L2",

                "L1:",
                "e = exception",
                "t8 = \"Error\"",
                "t9 = t8",
                "call print(t9)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    // ========================================
    // TESTS DE CASOS EDGE
    // ========================================

    @Test
    @DisplayName("Try-catch con try vacío")
    void testTryCatchWithEmptyTry() {
        String code = """
            try {
            } catch (e) {
                print("Error");
            }
            """;

        List<String> expected = Arrays.asList(
                "try_begin L1",
                "try_end",
                "goto L2",
                "L1:",
                "e = exception",
                "t1 = \"Error\"",
                "t2 = t1",
                "call print(t2)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Try-catch con catch vacío")
    void testTryCatchWithEmptyCatch() {
        String code = """
            try {
                print(1);
            } catch (e) {
            }
            """;

        List<String> expected = Arrays.asList(
                "try_begin L1",
                "t1 = 1",
                "t2 = t1",
                "call print(t2)",
                "try_end",
                "goto L2",
                "L1:",
                "e = exception",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Try-catch con variable de excepción no usada")
    void testTryCatchWithUnusedException() {
        String code = """
            try {
                let x: integer = 10 / 0;
            } catch (error) {
                print("Error genérico");
            }
            """;

        List<String> expected = Arrays.asList(
                "try_begin L1",
                "t1 = 10",
                "t2 = 0",
                "t3 = t1 / t2",
                "x = t3",
                "try_end",
                "goto L2",
                "L1:",
                "error = exception",
                "t4 = \"Error genérico\"",
                "t5 = t4",
                "call print(t5)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Try-catch con múltiples operaciones que pueden fallar")
    void testTryCatchWithMultipleRiskyOperations() {
        String code = """
            try {
                let arr: integer[] = [1, 2, 3];
                let x: integer = arr[10];
                let y: integer = 10 / 0;
                print(x + y);
            } catch (e) {
                print("Múltiples errores posibles");
            }
            """;

        List<String> expected = Arrays.asList(
                "try_begin L1",

                "t1 = 1",
                "arr[0] = t1",
                "t2 = 2",
                "arr[1] = t2",
                "t3 = 3",
                "arr[2] = t3",

                "t4 = 10",
                "t5 = arr[t4]",
                "x = t5",

                "t6 = 10",
                "t7 = 0",
                "t8 = t6 / t7",
                "y = t8",

                "t9 = x + y",
                "t10 = t9",
                "call print(t10)",

                "try_end",
                "goto L2",

                "L1:",
                "e = exception",
                "t11 = \"Múltiples errores posibles\"",
                "t12 = t11",
                "call print(t12)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    // ========================================
    // TESTS DE TRY-CATCH CON SWITCH
    // ========================================

    @Test
    @DisplayName("Try-catch con switch dentro")
    void testTryCatchWithSwitch() {
        String code = """
            try {
                let x: integer = 2;
                switch (x) {
                    case 1:
                        print(1);
                    case 2:
                        print(2);
                    default:
                        print(0);
                }
            } catch (e) {
                print("Error");
            }
            """;

        List<String> expected = Arrays.asList(
                "try_begin L1",

                "t1 = 2",
                "x = t1",
                "t2 = x",

                "t3 = 1",
                "if t2 == t3 goto L4",
                "t4 = 2",
                "if t2 == t4 goto L5",
                "goto L6",

                "L4:",
                "t5 = 1",
                "t6 = t5",
                "call print(t6)",
                "goto L3",

                "L5:",
                "t7 = 2",
                "t8 = t7",
                "call print(t8)",
                "goto L3",

                "L6:",
                "t9 = 0",
                "t10 = t9",
                "call print(t10)",
                "L3:",

                "try_end",
                "goto L2",

                "L1:",
                "e = exception",
                "t11 = \"Error\"",
                "t12 = t11",
                "call print(t12)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Switch con try-catch en un case")
    void testSwitchWithTryCatchInCase() {
        String code = """
            let x: integer = 1;
            switch (x) {
                case 1:
                    try {
                        print(1);
                    } catch (e) {
                        print("Error");
                    }
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
                "goto L3",

                "L2:",
                "try_begin L4",
                "t4 = 1",
                "t5 = t4",
                "call print(t5)",
                "try_end",
                "goto L5",
                "L4:",
                "e = exception",
                "t6 = \"Error\"",
                "t7 = t6",
                "call print(t7)",
                "L5:",
                "goto L1",

                "L3:",
                "t8 = 0",
                "t9 = t8",
                "call print(t9)",
                "L1:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    // ========================================
    // TESTS DE TRY-CATCH CON CLASES
    // ========================================

    @Test
    @DisplayName("Try-catch con instanciación de clase")
    void testTryCatchWithClassInstantiation() {
        String code = """
            class MyClass {
                let value: integer = 10;
            }
            
            try {
                let obj: MyClass = new MyClass();
                print(obj.value);
            } catch (e) {
                print("Error al crear objeto");
            }
            """;

        List<String> expected = Arrays.asList(
                "MyClass:",
                "t1 = 10",
                "value = t1",
                "end Class MyClass",

                "try_begin L1",
                "t2 = new MyClass()",
                "obj = t2",
                "t3 = obj.value",
                "t4 = t3",
                "call print(t4)",
                "try_end",
                "goto L2",

                "L1:",
                "e = exception",
                "t5 = \"Error al crear objeto\"",
                "t6 = t5",
                "call print(t6)",
                "L2:"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }
}