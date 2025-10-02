package com.fmd;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;
import java.util.stream.Collectors;

public class TestInit {
    public List<String> generateTAC(String code) {
        // Lexer y parser
        CompiscriptLexer lexer = new CompiscriptLexer(CharStreams.fromString(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CompiscriptParser parser = new CompiscriptParser(tokens);
        ParseTree tree = parser.program();

        // Semántico
        SemanticVisitor visitor = new SemanticVisitor();
        visitor.visit(tree);

        // Generación TAC
        TACVisitor visitor_tac = new TACVisitor(visitor.getAllSymbols());
        visitor_tac.visit(tree);

        // Retornar TAC como lista de strings
        return visitor_tac.getGenerator().getInstructions().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
