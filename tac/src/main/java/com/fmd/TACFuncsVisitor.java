package com.fmd;

import com.fmd.modules.Symbol;
import com.fmd.modules.TACInstruction;
import org.springframework.expression.spel.ast.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TACFuncsVisitor extends CompiscriptBaseVisitor<Void>{
    private final TACGenerator generator;
    private final TACStmtVisitor stmtVisitor;

    TACFuncsVisitor(TACStmtVisitor stmtVisitor, TACGenerator generator) {
        this.generator = generator;
        this.stmtVisitor = stmtVisitor;
    }

    /**
     * Declaración de función:
     *      function suma(a, b) { ... }
     *
     * TAC generado:
     * func_suma:
     *   [código de la función]
     *   return null
     */
    @Override
    public Void visitFunctionDeclaration(CompiscriptParser.FunctionDeclarationContext ctx) {

        String funcName = ctx.Identifier().getText();
        Symbol funcSym = generator.getSymbol(funcName);

        // Número de parámetros
        int paramCount = ctx.parameters() != null ? ctx.parameters().parameter().size() : 0;
        funcSym.setParamCount(paramCount);

        // Marcar inicio de función, para acceso a scope
        String thisScopeLine = String.valueOf(ctx.block().start.getLine());
        generator.enterFunction(funcName);
        generator.setCurrentScopeLine(thisScopeLine);

        if (paramCount > 0) {
            for (Symbol paramSym : funcSym.getParams()) {
                int updateOffset = generator.allocateLocal(generator.typeSize(paramSym.getType()));
                paramSym.setOffset(updateOffset);
                generator.getSymbol(paramSym.getName()).setOffset(updateOffset);
            }
        }

        // Generar etiqueta func_name:
        TACInstruction funcInstruction = new TACInstruction(TACInstruction.OpType.LABEL_FUNCTION);
        funcInstruction.setLabel(funcName);
        generator.addInstruction(funcInstruction);

        // Procesar cuerpo de la función (incluyendo return)
        stmtVisitor.visit(ctx.block());

        // Reservar espacio para locales
        int varSize = 0;
        Map<String, Symbol> members = generator.getSymbolTable(thisScopeLine);
        funcSym.setMembers(members); // actualizar miembros de funcion
        for (Symbol member : members.values())
            varSize += member.getOffset();

        for (Symbol param : funcSym.getParams())
            varSize += param.getOffset();

        funcSym.setLocalVarSize(varSize);

        if (funcSym.getEnclosingClassName() == null) {
            Symbol classSym = generator.getSymbol(funcSym.getEnclosingClassName());
            if (classSym != null) {
                classSym.getMembers().put(funcSym.getName(), funcSym);
            }
        }

        // etiqueta de fin
        TACInstruction funcEndInstruction = new TACInstruction(TACInstruction.OpType.END);
        funcEndInstruction.setLabel(funcName);
        generator.addInstruction(funcEndInstruction);

        // Marcar fin de función:
        generator.exitFunction();
        return null;
    }

    /**
     * Return statement:
     *      return x;
     *
     * TAC generado:
     *   t1 = x
     *   return t1
     */
    @Override
    public Void visitReturnStatement(CompiscriptParser.ReturnStatementContext ctx) {
        generator.setAssignment(true);
        TACInstruction returnInstruction = new TACInstruction(TACInstruction.OpType.RETURN);

        String result = "null";
        if(ctx.expression()!=null){ // Si hay expresión, evaluarla
            if(generator.getSymbol(ctx.expression().getText())!=null){
                result = generator.getSymbol(ctx.expression().getText()).getName();
            } else {
                stmtVisitor.visit(ctx.expression()); // evaluar expresion
                result = generator.getLastInstruction().getResult(); // tomar la última variable temporal registrada
            }

        }
        returnInstruction.setArg1(result); // argumento de return

        generator.addInstruction(returnInstruction);
        generator.setAssignment(false);
        generator.freeTemp(result);
        return null;
    }
}
