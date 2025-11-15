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

@DisplayName("Tests para P3")
public class P3Tests {
    TestInit testInit = new TestInit();

    @Test
    void testFunctionDefinition() {
        String code = "function speak(name: string): string {\n" +
                "   let printVar: string = \" makes a sound.\";\n" +
                "   return name + printVar;\n" +
                "}";
        List<String> expected = Arrays.asList(
                "speak:",
                "t1 = \" makes a sound.\"",
                "printVar = t1",
                "t1 = name + printVar",
                "return t1",
                "end speak"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testFunctionCall() {
        String code = "function speak(name: string): string {\n" +
                "   let printVar: string = \" makes a sound.\";\n" +
                "   return name + printVar;\n" +
                "}\n" + "speak(\"hola\");";
        List<String> expected = Arrays.asList(
                "speak:",
                "t1 = \" makes a sound.\"",
                "printVar = t1",
                "t1 = name + printVar",
                "return t1",
                "end speak",
                "t1 = \"hola\"",
                "call speak(t1)"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Función anidada simple")
    void testNestedFunction() {
        String code = """
            function outer(a: integer): integer {
                function inner(b: integer): integer {
                    return a + b;
                }
                return inner(5);
            }
        """;
        List<String> expected = Arrays.asList(
                "outer:",
                "inner:",
                "t1 = a + b",
                "return t1",
                "end inner",
                "t2 = 5",
                "t1 = call inner(t2)",
                "return t1",
                "end outer"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Closure que captura variable externa")
    void testClosureCapture() {
        String code = """
            function makeAdder(x: integer) {
                function add(y: integer): integer {
                    return x + y;
                }
                return add;
            }
            
            let add5 = makeAdder(5);
            add5(3);
        """;
        List<String> expected = Arrays.asList(
                "makeAdder:",
                "add:",
                "t1 = x + y",
                "return t1",
                "end add",
                "return add",
                "end makeAdder",
                "t2 = 5",
                "t1 = call makeAdder(t2)",
                "add5 = t1",
                "t1 = 3",
                "call add5(t1)"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Función anidada con múltiples niveles de alcance")
    void testDeeplyNestedFunctions() {
        String code = """
            function a() {
                let v: integer = 1;
                function b() {
                    let w: integer = 2;
                    function c() {
                        return v + w;
                    }
                    return c();
                }
                return b();
            }
        """;
        List<String> expected = Arrays.asList(
                "a:",
                "t1 = 1",
                "v = t1",
                "b:",
                "t1 = 2",
                "w = t1",
                "c:",
                "t1 = v + w",
                "return t1",
                "end c",
                "t1 = call c()",
                "return t1",
                "end b",
                "t1 = call b()",
                "return t1",
                "end a"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    @DisplayName("Variables externas modificadas dentro de función anidada")
    void testClosureModifyOuterVar() {
        String code = """
            function counter() {
                let count: integer = 0;
                function inc() {
                    count = count + 1;
                    return count;
                }
                return inc;
            }
            
            let c = counter();
            c();
            c();
        """;
        List<String> expected = Arrays.asList(
                "counter:",
                "t1 = 0",
                "count = t1",
                "inc:",
                "t1 = 1",
                "t2 = count + t1",
                "count = t2",
                "return count",
                "end inc",
                "return inc",
                "end counter",
                "t2 = call counter()",
                "c = t2",
                "call c()",
                "call c()"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }
}
