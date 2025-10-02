package com.fmd;

import com.fmd.CompiscriptParser;
import com.fmd.CompiscriptBaseVisitor;
import com.fmd.modules.TACInstruction;

import java.util.ArrayList;
import java.util.List;

public class TACExprVisitor extends CompiscriptBaseVisitor<String> {

    private final TACGenerator generator;

    public TACExprVisitor(TACGenerator generator) {
        this.generator = generator;
    }


    // EXPRESIONES BÁSICAS
    @Override
    public String visitLiteralExpr(CompiscriptParser.LiteralExprContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitIdentifierExpr(CompiscriptParser.IdentifierExprContext ctx) {
        return ctx.Identifier().getText();
    }

    @Override
    public String visitAdditiveExpr(CompiscriptParser.AdditiveExprContext ctx) {
        if (ctx.multiplicativeExpr().size() == 1) {
            return visit(ctx.multiplicativeExpr(0));
        }

        String result = visit(ctx.multiplicativeExpr(0));

        for (int i = 1; i < ctx.multiplicativeExpr().size(); i++) {
            String right = visit(ctx.multiplicativeExpr(i));
            String temp = generator.newTemp();
            String op = ctx.getChild(2 * i - 1).getText();

            TACInstruction instr = new TACInstruction(TACInstruction.OpType.BINARY_OP);
            instr.setResult(temp);
            instr.setArg1(result);
            instr.setArg2(right);
            instr.setOperator(op);
            generator.addInstruction(instr);

            result = temp;
        }

        return result;
    }

    @Override
    public String visitMultiplicativeExpr(CompiscriptParser.MultiplicativeExprContext ctx) {
        if (ctx.unaryExpr().size() == 1) {
            return visit(ctx.unaryExpr(0));
        }

        String result = visit(ctx.unaryExpr(0));

        for (int i = 1; i < ctx.unaryExpr().size(); i++) {
            String right = visit(ctx.unaryExpr(i));
            String temp = generator.newTemp();
            String op = ctx.getChild(2 * i - 1).getText(); // '*', '/' o '%'

            TACInstruction instr = new TACInstruction(TACInstruction.OpType.BINARY_OP);
            instr.setResult(temp);
            instr.setArg1(result);
            instr.setArg2(right);
            instr.setOperator(op);
            generator.addInstruction(instr);

            result = temp;
        }

        return result;
    }

    @Override
    public String visitUnaryExpr(CompiscriptParser.UnaryExprContext ctx) {
        if (ctx.unaryExpr() == null) {
            return visit(ctx.primaryExpr());
        }

        String operand = visit(ctx.unaryExpr());
        String temp = generator.newTemp();
        String op = ctx.getChild(0).getText(); // '-' o '!'

        TACInstruction instr = new TACInstruction(TACInstruction.OpType.UNARY_OP);
        instr.setResult(temp);
        instr.setArg1(operand);
        instr.setOperator(op);
        generator.addInstruction(instr);

        return temp;
    }

    @Override
    public String visitPrimaryExpr(CompiscriptParser.PrimaryExprContext ctx) {
        if (ctx.literalExpr() != null) {
            return visit(ctx.literalExpr());
        } else if (ctx.leftHandSide() != null) {
            return visit(ctx.leftHandSide());
        } else {
            return visit(ctx.expression());
        }
    }


    // COMPARACIONES Y LÓGICA
    @Override
    public String visitRelationalExpr(CompiscriptParser.RelationalExprContext ctx) {
        if (ctx.additiveExpr().size() == 1) {
            return visit(ctx.additiveExpr(0));
        }

        // Evaluar el primer operando
        String left = visit(ctx.additiveExpr(0));

        // Procesar cada operación relacional de izquierda a derecha
        for (int i = 1; i < ctx.additiveExpr().size(); i++) {
            String right = visit(ctx.additiveExpr(i));
            String temp = generator.newTemp();

            // Obtener el operador: '<', '>', '<=', '>='
            String op = ctx.getChild(2 * i - 1).getText();

            TACInstruction instr = new TACInstruction(TACInstruction.OpType.BINARY_OP);
            instr.setResult(temp);
            instr.setArg1(left);
            instr.setArg2(right);
            instr.setOperator(op);
            generator.addInstruction(instr);

            left = temp;
        }

        return left;
    }

    @Override
    public String visitEqualityExpr(CompiscriptParser.EqualityExprContext ctx) {
        if (ctx.relationalExpr().size() == 1) {
            return visit(ctx.relationalExpr(0));
        }

        // Evaluar el primer operando
        String left = visit(ctx.relationalExpr(0));

        // Procesar cada operación de igualdad de izquierda a derecha
        for (int i = 1; i < ctx.relationalExpr().size(); i++) {
            String right = visit(ctx.relationalExpr(i));
            String temp = generator.newTemp();

            // Obtener el operador: '==' o '!='
            String op = ctx.getChild(2 * i - 1).getText();

            TACInstruction instr = new TACInstruction(TACInstruction.OpType.BINARY_OP);
            instr.setResult(temp);
            instr.setArg1(left);
            instr.setArg2(right);
            instr.setOperator(op);
            generator.addInstruction(instr);

            left = temp;
        }

        return left;
    }

    @Override
    public String visitLogicalAndExpr(CompiscriptParser.LogicalAndExprContext ctx) {
        if (ctx.equalityExpr().size() == 1) {
            return visit(ctx.equalityExpr(0));
        }

        String result = generator.newTemp();
        String endLabel = generator.newLabel();

        String left = visit(ctx.equalityExpr(0));

        // Inicializamos resultado = 0 (false)
        TACInstruction init = new TACInstruction(TACInstruction.OpType.ASSIGN);
        init.setResult(result);
        init.setArg1("0");
        generator.addInstruction(init);

        // Si left == 0, saltar al endLabel
        TACInstruction ifGoto = new TACInstruction(TACInstruction.OpType.IF_GOTO);
        ifGoto.setArg1(left);
        ifGoto.setArg2("0");
        ifGoto.setRelop("==");
        ifGoto.setLabel(endLabel);
        generator.addInstruction(ifGoto);

        // Evaluamos right
        String right = visit(ctx.equalityExpr(1));

        // Asignamos right a result
        TACInstruction assignRight = new TACInstruction(TACInstruction.OpType.ASSIGN);
        assignRight.setResult(result);
        assignRight.setArg1(right);
        generator.addInstruction(assignRight);

        TACInstruction lblInstr = new TACInstruction(TACInstruction.OpType.LABEL);
        lblInstr.setLabel(endLabel);
        generator.addInstruction(lblInstr);

        return result;
    }



    @Override
    public String visitLogicalOrExpr(CompiscriptParser.LogicalOrExprContext ctx) {
        if (ctx.logicalAndExpr().size() == 1) {
            return visit(ctx.logicalAndExpr(0));
        }

        String result = generator.newTemp();
        String endLabel = generator.newLabel();

        String left = visit(ctx.logicalAndExpr(0));

        // Inicializamos resultado = 1 (true)
        TACInstruction init = new TACInstruction(TACInstruction.OpType.ASSIGN);
        init.setResult(result);
        init.setArg1("1");
        generator.addInstruction(init);

        // Si left != 0, saltar al endLabel
        TACInstruction ifGoto = new TACInstruction(TACInstruction.OpType.IF_GOTO);
        ifGoto.setArg1(left);
        ifGoto.setArg2("0");
        ifGoto.setRelop("!=");
        ifGoto.setLabel(endLabel);
        generator.addInstruction(ifGoto);

        // Evaluamos right
        String right = visit(ctx.logicalAndExpr(1));

        // Asignamos right a result
        TACInstruction assignRight = new TACInstruction(TACInstruction.OpType.ASSIGN);
        assignRight.setResult(result);
        assignRight.setArg1(right);
        generator.addInstruction(assignRight);

        TACInstruction lblInstr = new TACInstruction(TACInstruction.OpType.LABEL);
        lblInstr.setLabel(endLabel);
        generator.addInstruction(lblInstr);

        return result;
    }



    @Override
    public String visitLeftHandSide(CompiscriptParser.LeftHandSideContext ctx) {
        String result = visit(ctx.primaryAtom());

        for (CompiscriptParser.SuffixOpContext suffix : ctx.suffixOp()) {
            result = visitSuffixOp(suffix, result);
        }

        return result;
    }

    private String visitSuffixOp(CompiscriptParser.SuffixOpContext ctx, String base) {
        if (ctx instanceof CompiscriptParser.CallExprContext) {
            return handleFunctionCall((CompiscriptParser.CallExprContext) ctx, base);
        } else if (ctx instanceof CompiscriptParser.IndexExprContext) {
            return handleArrayAccess((CompiscriptParser.IndexExprContext) ctx, base);
        } else if (ctx instanceof CompiscriptParser.PropertyAccessExprContext) {
            return handlePropertyAccess((CompiscriptParser.PropertyAccessExprContext) ctx, base);
        }
        return base;
    }

    private String handleFunctionCall(CompiscriptParser.CallExprContext ctx, String funcName) {
        TACInstruction callInstruction = new TACInstruction(TACInstruction.OpType.CALL);
        callInstruction.setArg1(funcName);

        if (ctx.arguments() != null) {
            List<CompiscriptParser.ExpressionContext> args = ctx.arguments().expression();
            for (CompiscriptParser.ExpressionContext arg : args) {
                String tempName = generator.newTemp();
                String literalValue = visit(arg); // evaluar expresion

                TACInstruction paramInstruction = new TACInstruction(TACInstruction.OpType.ASSIGN);
                paramInstruction.setResult(tempName);
                paramInstruction.setArg1(literalValue);
                generator.addInstruction(paramInstruction);

                callInstruction.addParam(tempName); // guardarla como parametro
            }
        }
        generator.addInstruction(callInstruction);
        return null;
    }

    private String handleArrayAccess(CompiscriptParser.IndexExprContext ctx, String arrayName) {
        // TODO P4: Implementar
        return null;
    }

    private String handlePropertyAccess(CompiscriptParser.PropertyAccessExprContext ctx, String objName) {
        // TODO P5: Implementar
        return null;
    }

    @Override
    public String visitArrayLiteral(CompiscriptParser.ArrayLiteralContext ctx) {
        // TODO P4: Implementar
        return null;
    }

    @Override
    public String visitTernaryExpr(CompiscriptParser.TernaryExprContext ctx) {
        if (ctx.logicalOrExpr() != null && ctx.expression().isEmpty()) {
            return visit(ctx.logicalOrExpr());
        }
        // TODO P4: Implementar
        return null;
    }

    @Override
    public String visitNewExpr(CompiscriptParser.NewExprContext ctx) {
        // TODO P5: Implementar
        return null;
    }

    @Override
    public String visitThisExpr(CompiscriptParser.ThisExprContext ctx) {
        return "this";
    }


    // DELEGACIÓN
    public String visitConditionalExpr(CompiscriptParser.ConditionalExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public String visitExpression(CompiscriptParser.ExpressionContext ctx) {
        return visit(ctx.assignmentExpr());
    }

    public String visitAssignmentExpr(CompiscriptParser.AssignmentExprContext ctx) {
        if (ctx instanceof CompiscriptParser.ExprNoAssignContext) {
            CompiscriptParser.ExprNoAssignContext exprCtx =
                    (CompiscriptParser.ExprNoAssignContext) ctx;
            return visit(exprCtx.conditionalExpr());
        }
        return visitChildren(ctx);
    }
}