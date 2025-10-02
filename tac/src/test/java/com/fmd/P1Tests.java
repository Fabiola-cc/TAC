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

@DisplayName("Tests para P1")
public class P1Tests {

    private List<String> generateTAC(String code) {
        // Lexer y parser
        CompiscriptLexer lexer = new CompiscriptLexer(CharStreams.fromString(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CompiscriptParser parser = new CompiscriptParser(tokens);
        ParseTree tree = parser.program();

        // Generación TAC
        TACVisitor visitor_tac = new TACVisitor();
        visitor_tac.visit(tree);

        // Retornar TAC como lista de strings
        return visitor_tac.getGenerator().getInstructions().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Test
    void testLiteralAndArithmetic() {
        String code = "var a = 5 + 3 * 2;";
        List<String> expected = Arrays.asList(
                "t1 = 3 * 2",
                "t2 = 5 + t1",
                "a = t2"
        );
        assertEquals(expected, generateTAC(code));
    }

    @Test
    void testAssignmentAndPrint() {
        String code = "var x = 10; print(x);";
        List<String> expected = Arrays.asList(
                "x = 10",
                "call print(x)"
        );
        assertEquals(expected, generateTAC(code));
    }


    @Test
    void testUnaryExpression() {
        String code = "var y = -5;";
        List<String> expected = Arrays.asList(
                "t1 = -5",
                "y = t1"
        );
        assertEquals(expected, generateTAC(code));
    }
}
