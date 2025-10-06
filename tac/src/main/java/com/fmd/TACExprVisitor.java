package com.fmd;

import com.fmd.CompiscriptParser;
import com.fmd.CompiscriptBaseVisitor;
import com.fmd.modules.Symbol;
import com.fmd.modules.TACInstruction;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TACExprVisitor extends CompiscriptBaseVisitor<String> {

    private final TACGenerator generator;

    public TACExprVisitor(TACGenerator generator) {
        this.generator = generator;
    }


    // EXPRESIONES BÁSICAS
    @Override
    public String visitLiteralExpr(CompiscriptParser.LiteralExprContext ctx) {

        // Si es un array literal
        if (ctx.arrayLiteral() != null) {
            return visitArrayLiteral(ctx.arrayLiteral());
        } else {
            String temp = generator.newTemp();
            String value;

            if (ctx.getText().equals("true")) {
                value = "1";
            } else if (ctx.getText().equals("false")) {
                value = "0";
            } else {
                value = ctx.getText(); // número, string, etc.
            }

            TACInstruction instr = new TACInstruction(TACInstruction.OpType.ASSIGN);
            instr.setResult(temp);
            instr.setArg1(value);
            generator.addInstruction(instr);

            return temp;
        }
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
        String result = null;

        // Si la llamada es para asignación
        if( generator.getAssignment() ){
            callInstruction = new TACInstruction(TACInstruction.OpType.ASSIGN_CALL);
            result = generator.newTemp();
            callInstruction.setResult(result);
        }

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
        return result;
    }

    private String handleArrayAccess(CompiscriptParser.IndexExprContext ctx, String arrayName) {
        // Evaluar el índice
        String indexVal = visit(ctx.expression());

        // Crear temporal que contendrá el valor del array en ese índice
        String temp = generator.newTemp();

        // Generar TAC: temporal = array[index]
        TACInstruction instr = new TACInstruction(TACInstruction.OpType.ASSIGN);
        instr.setResult(temp);
        instr.setArg1(arrayName + "[" + indexVal + "]");
        generator.addInstruction(instr);

        return temp;
    }


    private String handlePropertyAccess(CompiscriptParser.PropertyAccessExprContext ctx, String objName) {
        String propertyName = ctx.getText();

        String result = generator.newTemp();
        TACInstruction instr = new TACInstruction(TACInstruction.OpType.ASSIGN);
        instr.setResult(result);
        instr.setArg1(objName + propertyName);
        generator.addInstruction(instr);

        return result;
    }

    @Override
    public String visitArrayLiteral(CompiscriptParser.ArrayLiteralContext ctx) {
        //  Obtener nombre del array desde la variable declarada
        String varName = getAssignedVariable(ctx); // "let numbers = [...]"
        Symbol arraySym = generator.getSymbol(varName);

        // Tamaño por elemento (ejemplo: integer = 4 bytes)
        int elementSize = generator.typeSize(arraySym.getType().replace("[]", ""));
        arraySym.setElementSize(elementSize);

        // Dimensiones del array
        List<Integer> dimensions = calculateDimensions(ctx);
        arraySym.setDimensions(dimensions);

        // Calcular tamaño total del array en bytes
        int totalElements = dimensions.stream().reduce(1, (a, b) -> a * b);
        int pointerSize = 4;
        int totalSize = pointerSize + totalElements * elementSize;
        arraySym.setSize(totalSize);

        // Asignar offset en el frame, usando allocateLocal con tamaño total
        arraySym.setOffset(generator.allocateLocal(arraySym.getSize()));

        // Registrar TACAddress (mismo nombre de variable)
        arraySym.setTacAddress(varName);

        // Generar TAC para cada elemento usando índice
        List<Integer> currentIndex = new ArrayList<>(Collections.nCopies(dimensions.size(), 0));
        generateMatrixAssignments(ctx, varName, currentIndex, 0);

        return varName;
    }

    /**
     * Extrae el ArrayLiteralContext navegando la jerarquía de reglas
     */
    private CompiscriptParser.ArrayLiteralContext getArrayLiteral(
            CompiscriptParser.ExpressionContext expr
    ) {
        // expression -> assignmentExpr
        if (expr.assignmentExpr() == null) return null;

        CompiscriptParser.AssignmentExprContext assignExpr = expr.assignmentExpr();

        // assignmentExpr -> conditionalExpr (regla ExprNoAssign)
        if (!(assignExpr instanceof CompiscriptParser.ExprNoAssignContext)) return null;

        CompiscriptParser.ExprNoAssignContext noAssign =
                (CompiscriptParser.ExprNoAssignContext) assignExpr;

        // conditionalExpr -> TernaryExpr
        CompiscriptParser.ConditionalExprContext condExpr = noAssign.conditionalExpr();
        if (!(condExpr instanceof CompiscriptParser.TernaryExprContext)) return null;

        CompiscriptParser.TernaryExprContext ternary =
                (CompiscriptParser.TernaryExprContext) condExpr;

        // logicalOrExpr -> logicalAndExpr -> ... -> primaryExpr
        // Necesitamos navegar hasta primaryExpr
        CompiscriptParser.LogicalOrExprContext logicalOr = ternary.logicalOrExpr();
        if (logicalOr == null || logicalOr.logicalAndExpr().isEmpty()) return null;

        CompiscriptParser.LogicalAndExprContext logicalAnd = logicalOr.logicalAndExpr(0);
        if (logicalAnd == null || logicalAnd.equalityExpr().isEmpty()) return null;

        CompiscriptParser.EqualityExprContext equality = logicalAnd.equalityExpr(0);
        if (equality == null || equality.relationalExpr().isEmpty()) return null;

        CompiscriptParser.RelationalExprContext relational = equality.relationalExpr(0);
        if (relational == null || relational.additiveExpr().isEmpty()) return null;

        CompiscriptParser.AdditiveExprContext additive = relational.additiveExpr(0);
        if (additive == null || additive.multiplicativeExpr().isEmpty()) return null;

        CompiscriptParser.MultiplicativeExprContext multiplicative = additive.multiplicativeExpr(0);
        if (multiplicative == null || multiplicative.unaryExpr().isEmpty()) return null;

        CompiscriptParser.UnaryExprContext unary = multiplicative.unaryExpr(0);
        if (unary == null || unary.primaryExpr() == null) return null;

        CompiscriptParser.PrimaryExprContext primary = unary.primaryExpr();
        if (primary.literalExpr() == null) return null;

        CompiscriptParser.LiteralExprContext literal = primary.literalExpr();
        return literal.arrayLiteral();
    }

    /**
     * Calcula las dimensiones de un array/matriz recursivamente
     */
    private List<Integer> calculateDimensions(CompiscriptParser.ArrayLiteralContext ctx) {
        List<Integer> dims = new ArrayList<>();

        // Primera dimensión: número de elementos en este nivel
        int size = ctx.expression() != null ? ctx.expression().size() : 0;
        dims.add(size);

        // Si el primer elemento es también un array, obtener dimensiones internas
        if (size > 0) {
            CompiscriptParser.ArrayLiteralContext innerArray =
                    getArrayLiteral(ctx.expression(0));

            if (innerArray != null) {
                dims.addAll(calculateDimensions(innerArray));
            }
        }

        return dims;
    }

    /**
     * Genera asignaciones TAC recursivamente para matrices
     */
    private void generateMatrixAssignments(
            CompiscriptParser.ArrayLiteralContext ctx,
            String varName,
            List<Integer> currentIndex,
            int depth
    ) {
        if (ctx.expression() == null) return;

        for (int i = 0; i < ctx.expression().size(); i++) {
            currentIndex.set(depth, i);

            CompiscriptParser.ExpressionContext expr = ctx.expression(i);
            CompiscriptParser.ArrayLiteralContext innerArray = getArrayLiteral(expr);

            if (innerArray != null) {
                // Es un sub-array, recursión
                generateMatrixAssignments(innerArray, varName, currentIndex, depth + 1);
            } else {
                // Es un valor escalar, generar asignación
                String val = visit(expr);

                // Construir índice multidimensional: varName[i][j]...
                StringBuilder indexStr = new StringBuilder(varName);
                for (int j = 0; j <= depth; j++) {
                    indexStr.append("[").append(currentIndex.get(j)).append("]");
                }

                TACInstruction instr = new TACInstruction(TACInstruction.OpType.ASSIGN);
                instr.setResult(indexStr.toString());
                instr.setArg1(val);
                generator.addInstruction(instr);
            }
        }
    }

    private String getAssignedVariable(ParseTree ctx) {
        ParseTree parent = ctx.getParent();
        while (parent != null) {
            if (parent instanceof CompiscriptParser.VariableDeclarationContext) {
                return ((CompiscriptParser.VariableDeclarationContext) parent).Identifier().getText();
            } else if (parent instanceof CompiscriptParser.AssignmentContext) {
                return ((CompiscriptParser.AssignmentContext) parent).Identifier().getText();
            }
            parent = parent.getParent();
        }
        return null; // no se encontró
    }





    /**
     * cond ? expr1 : expr2
     *
     * @param ctx the parse tree
     * @return String result
     */
    @Override
    public String visitTernaryExpr(CompiscriptParser.TernaryExprContext ctx) {
        if (ctx.logicalOrExpr() != null && ctx.expression().isEmpty()) {
            return visit(ctx.logicalOrExpr());
        }

        String labelTrue =generator.newLabel();
        String labelFalse =generator.newLabel();
        String labelEnd =generator.newLabel();

        String orExpr = visit(ctx.logicalOrExpr());
        TACInstruction init = new TACInstruction(TACInstruction.OpType.IF_GOTO);
        init.setArg1(orExpr);
        init.setRelop("==");
        init.setArg2("1"); // True
        init.setLabel(labelTrue);
        generator.addInstruction(init);

        TACInstruction elseInstr = new TACInstruction(TACInstruction.OpType.GOTO);
        elseInstr.setLabel(labelFalse);
        generator.addInstruction(elseInstr);

        TACInstruction initLabel = new TACInstruction(TACInstruction.OpType.LABEL);
        initLabel.setLabel(labelTrue);
        generator.addInstruction(initLabel);

        String result = generator.newTemp();
        String trueResult = visit(ctx.expression(0));
        TACInstruction assignTrueInstr = new TACInstruction(TACInstruction.OpType.ASSIGN);
        assignTrueInstr.setResult(result);
        assignTrueInstr.setArg1(trueResult);
        generator.addInstruction(assignTrueInstr);

        TACInstruction toEndInstr = new TACInstruction(TACInstruction.OpType.GOTO);
        toEndInstr.setLabel(labelEnd);
        generator.addInstruction(toEndInstr);

        TACInstruction elseLabel = new TACInstruction(TACInstruction.OpType.LABEL);
        elseLabel.setLabel(labelFalse);
        generator.addInstruction(elseLabel);

        String falseResult = visit(ctx.expression(1));
        TACInstruction assignFalseInstr = new TACInstruction(TACInstruction.OpType.ASSIGN);
        assignFalseInstr.setResult(result);
        assignFalseInstr.setArg1(falseResult);
        generator.addInstruction(assignFalseInstr);

        TACInstruction endLabel = new TACInstruction(TACInstruction.OpType.LABEL);
        endLabel.setLabel(labelEnd);
        generator.addInstruction(endLabel);

        return result;
    }

    @Override
    public String visitNewExpr(CompiscriptParser.NewExprContext ctx) {
        String result = generator.newTemp();

        String className = ctx.Identifier().getText();
        TACInstruction newInstruction = new TACInstruction(TACInstruction.OpType.NEW);
        newInstruction.setResult(result);
        newInstruction.setArg1(className);

        if (ctx.arguments() != null) {
            List<CompiscriptParser.ExpressionContext> args = ctx.arguments().expression();
            for (CompiscriptParser.ExpressionContext arg : args) {
                String tempName = generator.newTemp();
                String literalValue = visit(arg); // evaluar expresion

                TACInstruction paramInstruction = new TACInstruction(TACInstruction.OpType.ASSIGN);
                paramInstruction.setResult(tempName);
                paramInstruction.setArg1(literalValue);
                generator.addInstruction(paramInstruction);

                newInstruction.addParam(tempName); // guardarla como parametro
            }
        }
        generator.addInstruction(newInstruction);
        return result;
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

    @Override
    public String visitAssignExpr(CompiscriptParser.AssignExprContext ctx) {
        generator.setAssignment(true);
        // ctx.lhs = lhs, ctx.assignmentExpr() = rhs
        String lhs = ctx.lhs.getText();

        String rhs = visit(ctx.assignmentExpr()); // recursivo para rhs

        if (rhs != null) {
            TACInstruction instr = new TACInstruction(TACInstruction.OpType.ASSIGN);
            instr.setResult(lhs);
            instr.setArg1(rhs);
            generator.addInstruction(instr);
        }

        generator.setAssignment(false);
        return lhs; // devuelve el nombre de la variable asignada
    }


}
