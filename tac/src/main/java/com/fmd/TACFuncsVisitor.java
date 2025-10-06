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
            for (CompiscriptParser.ParameterContext param : ctx.parameters().parameter()) {
                Symbol paramSym = generator.getSymbol(param.Identifier().getText());
                paramSym.setOffset(generator.allocateLocal(stmtVisitor.typeSize(paramSym.getType())));
            }
        }

        // Generar etiqueta func_name:
        TACInstruction funcInstruction = new TACInstruction(TACInstruction.OpType.LABEL);
        funcInstruction.setLabel(funcName);
        generator.addInstruction(funcInstruction);

        // Procesar cuerpo de la función (incluyendo return)
        stmtVisitor.visit(ctx.block());

        // Reservar espacio para locales
        int varSize = 0;
        Map<String, Symbol> members = generator.getSymbolTable(thisScopeLine);
        funcSym.setMembers(members); // actualizar miembros de funcion
        for (Symbol member : members.values()) {
            varSize += member.getOffset();
        }
        funcSym.setLocalVarSize(varSize);

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

        // Si hay expresión, evaluarla
        if(ctx.expression()!=null){
            stmtVisitor.visit(ctx.expression()); // evaluar expresion
            String lastVar = generator.getLastInstruction().getResult(); // tomar la última variable temporal registrada
            returnInstruction.setArg1(lastVar); // usarla como argumento de return
        } else { // Si no hay expresión, usar "null"
            returnInstruction.setArg1("null");
        }

        generator.addInstruction(returnInstruction);
        generator.setAssignment(false);
        return null;
    }
}
