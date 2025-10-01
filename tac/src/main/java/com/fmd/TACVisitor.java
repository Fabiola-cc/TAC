package com.fmd;

import java.util.ArrayList;
import java.util.List;

import com.fmd.CompiscriptParser;
import com.fmd.CompiscriptBaseVisitor;
import com.fmd.modules.TACInstruction;

public class TACVisitor extends CompiscriptBaseVisitor<Void> {

    private final TACExprVisitor exprVisitor = new TACExprVisitor();
    private final TACStmtVisitor stmtVisitor = new TACStmtVisitor();

    public TACVisitor() {}

    @Override
    public Void visitProgram(CompiscriptParser.ProgramContext ctx) {
        System.out.println("Se visito programa");
        for (CompiscriptParser.StatementContext stmt : ctx.statement()) {
            stmtVisitor.visit(stmt); // delega statements
        }
        return null;
    }

    @Override
    public Void visitBlock(CompiscriptParser.BlockContext ctx) {
        System.out.println("Se visito block");
        for (CompiscriptParser.StatementContext stmt : ctx.statement()) {
            stmtVisitor.visit(stmt); // delega statements
        }
        return null;
    }

    // Ejemplo de delegación a expresiones
    @Override
    public Void visitExpressionStatement(CompiscriptParser.ExpressionStatementContext ctx) {
        System.out.println("Se visito expression statement");
        String temp = exprVisitor.visit(ctx.expression()); // delega a expresion
        System.out.println("Resultado temporal de expresión: " + temp);
        return null;
    }
}
