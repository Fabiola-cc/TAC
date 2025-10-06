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
        assertEquals(0, z.getOffset(), "z empieza en 0 dentro del bloque anidado");
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


}
