package com.fmd;

import com.fmd.modules.Symbol;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OffsetTests {
    TestInit testInit = new TestInit();

    @Test
    void testOffsets_GlobalVariables() {
        String code = """
        let a: integer = 10;
        let b: string = "hi";
        let c: boolean = true;
    """;

        // Ejecutar generación de TAC
        testInit.generateTAC(code);

        TACGenerator generator = testInit.visitor_tac.getGenerator();
        generator.setCurrentScopeLine("0");

        // Buscar los símbolos en la tabla global
        Symbol a = generator.getSymbol("a");
        Symbol b = generator.getSymbol("b");
        Symbol c = generator.getSymbol("c");

        // Validar sus offsets
        assertEquals(0, a.getOffset(), "Offset de 'a' debería ser 0");
        assertEquals(4, b.getOffset(), "Offset de 'b' debería ser 4");
        assertEquals(12, c.getOffset(), "Offset de 'c' debería ser 12");
    }

    @Test
    void testOffsets_NestedBlocks() {
        String code = """
        {
            let x: integer = 5;
            let y: integer = 10;
            {
                let z: integer = 15;
            }
        }
    """;

        testInit.generateTAC(code);
        TACGenerator generator = testInit.visitor_tac.getGenerator();

        generator.setCurrentScopeLine("1");
        Symbol x = generator.getSymbol("x");
        Symbol y = generator.getSymbol("y");

        generator.setCurrentScopeLine("4");
        Symbol z = generator.getSymbol("z");

        assertEquals(0, x.getOffset());
        assertEquals(4, y.getOffset());
        assertEquals(8, z.getOffset());
    }

    @Test
    void testOffsets_FunctionScope() {
        String code = """
        function sum(a: integer, b: integer): integer {
            let temp: integer = a + b;
            return temp;
        }
    """;

        testInit.generateTAC(code);
        TACGenerator generator = testInit.visitor_tac.getGenerator();

        generator.setCurrentScopeLine("1");
        Symbol func = generator.getSymbol("sum");

        // Parámetros
        Symbol a = func.getMembers().get("a");
        Symbol b = func.getMembers().get("b");
        Symbol temp = func.getMembers().get("temp");

        assertEquals(0, a.getOffset());
        assertEquals(4, b.getOffset());
        assertEquals(8, temp.getOffset());
    }

    @Test
    void testOffsets_BlockWithFunction() {
        String code = """
    {
        let x: integer = 1;
        function bar() {
            let y: integer = 2;
            let z: integer = 3;
        }
    }
    """;

        testInit.generateTAC(code);
        TACGenerator generator = testInit.visitor_tac.getGenerator();

        // Variables del bloque
        generator.setCurrentScopeLine("1");
        Symbol x = generator.getSymbol("x");

        // Variables dentro de la función
        generator.setCurrentScopeLine("3");
        Symbol y = generator.getSymbol("y");
        Symbol z = generator.getSymbol("z");

        assertEquals(0, x.getOffset(), "Variable del bloque externo");
        assertEquals(0, y.getOffset(), "Inicio de nuevo frame (función)");
        assertEquals(4, z.getOffset(), "Segundo símbolo en la función");
    }

    @Test
    @DisplayName("Offsets dentro de una clase")
    void testOffsets_ClassScope() {
        String code = """
        class Point {
            let x: integer = 10;
            let y: integer = 20;
            
            function move() {
                let dx: integer = 1;
                let dy: integer = 2;
            }
        }
    """;

        testInit.generateTAC(code);
        TACGenerator generator = testInit.visitor_tac.getGenerator();

        // Campos de clase
        generator.setCurrentScopeLine("1");
        Symbol point = generator.getSymbol("Point");
        Symbol x = point.getMembers().get("x");
        Symbol y = point.getMembers().get("y");

        // Variables dentro del metodo
        Symbol move = point.getMembers().get("move");
        Symbol dx = move.getMembers().get("dx");
        Symbol dy = move.getMembers().get("dy");

        assertEquals(0, x.getOffset(), "Primer campo de clase");
        assertEquals(4, y.getOffset(), "Segundo campo de clase");
        assertEquals(0, dx.getOffset(), "Primer variable del método (nuevo frame)");
        assertEquals(4, dy.getOffset(), "Segundo variable del método");
    }

    @Test
    @DisplayName("Offsets dentro de estructuras de control")
    void testOffsets_ControlStructures() {
        String code = """
        function demo() {
            let a: integer = 1;
            if (a > 0) {
                let b: integer = 2;
            }
            for (let i: integer = 0; i < 3; i = i + 1) {
                let c: integer = 3;
            }
        }
    """;

        testInit.generateTAC(code);
        TACGenerator generator = testInit.visitor_tac.getGenerator();

        generator.setCurrentScopeLine("1");
        Symbol func = generator.getSymbol("demo");
        Symbol a = func.getMembers().get("a");

        generator.setCurrentScopeLine("3");
        Symbol b = generator.getSymbol("b");

        generator.setCurrentScopeLine("6");
        Symbol i = generator.getSymbol("i");
        Symbol c = generator.getSymbol("c");

        assertEquals(0, a.getOffset(), "Primera variable local");
        assertEquals(4, b.getOffset(), "No se reinicia dentro de if");
        assertEquals(8, i.getOffset(), "For no reinicia offset");
        assertEquals(12, c.getOffset(), "Tampoco reinicia dentro del for");
    }

}
