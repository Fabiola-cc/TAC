package com.fmd;

import com.fmd.CompiscriptParser;
import com.fmd.CompiscriptBaseVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

public class TACStmtVisitor extends CompiscriptBaseVisitor<Void> {

    private final TACExprVisitor exprVisitor;

    public TACStmtVisitor() {
        this.exprVisitor = new TACExprVisitor();
    }

    @Override
    public Void visitVariableDeclaration(CompiscriptParser.VariableDeclarationContext ctx) {
        System.out.println("Visiting variable declaration: " + ctx.Identifier().getText());
        if (ctx.initializer() != null) {
            exprVisitor.visit(ctx.initializer().expression());
        }
        return null;
    }

    @Override
    public Void visitAssignment(CompiscriptParser.AssignmentContext ctx) {
        System.out.println("Visiting assignment");

        if (ctx.Identifier() != null) {
            // Primera alternativa: simple assignment
            String id = ctx.Identifier().getText();
            System.out.println("Assignment to: " + id);
            exprVisitor.visit(ctx.expression(0)); // Solo hay una expresión aquí
        } else if (ctx.expression().size() == 2) {
            // Segunda alternativa: property assignment
            System.out.println("Property assignment: " + ctx.expression(0).getText() + "." + ctx.Identifier().getText());
            exprVisitor.visit(ctx.expression(0)); // expresión del objeto
            exprVisitor.visit(ctx.expression(1)); // expresión del valor
        }

        return null;
    }


    @Override
    public Void visitPrintStatement(CompiscriptParser.PrintStatementContext ctx) {
        System.out.println("Visiting print statement");
        exprVisitor.visit(ctx.expression());
        return null;
    }

    @Override
    public Void visitBlock(CompiscriptParser.BlockContext ctx) {
        System.out.println("Visiting block (stmtVisitor)");
        for (var stmt : ctx.statement()) {
            visit(stmt);
        }
        return null;
    }

    // Puedes agregar más métodos de statements aquí con prints
}
