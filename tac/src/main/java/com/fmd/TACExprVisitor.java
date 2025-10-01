package com.fmd;

import com.fmd.CompiscriptParser;
import com.fmd.CompiscriptBaseVisitor;

public class TACExprVisitor extends CompiscriptBaseVisitor<String> {

    @Override
    public String visitLiteralExpr(CompiscriptParser.LiteralExprContext ctx) {
        System.out.println("Visiting literal: " + ctx.getText());
        return ctx.getText(); // devuelve valor literal
    }

    @Override
    public String visitIdentifierExpr(CompiscriptParser.IdentifierExprContext ctx) {
        System.out.println("Visiting identifier: " + ctx.Identifier().getText());
        return ctx.Identifier().getText();
    }

    @Override
    public String visitAdditiveExpr(CompiscriptParser.AdditiveExprContext ctx) {
        System.out.println("Visiting additive expression: " + ctx.getText());
        // Visitar sub-expresiones
        for (int i = 0; i < ctx.multiplicativeExpr().size(); i++) {
            visit(ctx.multiplicativeExpr(i));
        }
        return "t?"; // temporal simulado
    }

    @Override
    public String visitMultiplicativeExpr(CompiscriptParser.MultiplicativeExprContext ctx) {
        System.out.println("Visiting multiplicative expression: " + ctx.getText());
        for (int i = 0; i < ctx.unaryExpr().size(); i++) {
            visit(ctx.unaryExpr(i));
        }
        return "t?"; // temporal simulado
    }

    // Puedes agregar más métodos de expresiones aquí con prints
}
