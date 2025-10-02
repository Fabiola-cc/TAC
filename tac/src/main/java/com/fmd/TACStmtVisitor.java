package com.fmd;

import com.fmd.CompiscriptParser;
import com.fmd.CompiscriptBaseVisitor;
import com.fmd.modules.Symbol;
import com.fmd.modules.TACInstruction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Visitor para STATEMENTS
 *
 * CARACTERÍSTICAS:
 * - Devuelve Void (no devuelve nada)
 * - Procesa nodos que EJECUTAN ACCIONES
 * - Usa TACGenerator para crear etiquetas e instrucciones
 * - Usa TACExprVisitor para evaluar expresiones
 *
 * ORDEN DE IMPLEMENTACIÓN:
 * P1: Básico (declaración, asignación, print)
 * P2: Control de flujo (if, while, for, bloques)
 * P3: Funciones (declaración, return)
 * P4: Avanzado (foreach, switch, break/continue, try-catch)
 * P5: POO (clases, herencia)
 */
public class TACStmtVisitor extends CompiscriptBaseVisitor<Void> {

    private final TACGenerator generator;
    private final TACExprVisitor exprVisitor;

    private final TACFuncsVisitor funcsVisitor;

    public TACStmtVisitor(TACGenerator generator, TACExprVisitor exprVisitor) {
        this.generator = generator;
        this.exprVisitor = exprVisitor;

        this.funcsVisitor = new TACFuncsVisitor(this, generator);
    }

    
    // STATEMENTS BÁSICOS
    /**
     * Permite reconocer el offset de cada variable según su tipo
     * @param type string
     * @return offset int
     */
    private int typeSize(String type) {
        if (type == null) {
            return 4; // default (ej: unknown → int)
        }

        switch (type) {
            case "boolean":
                return 1; // 1 byte
            case "integer":
                return 4; // 4 bytes
            case "string":
                return 8; // referencia a string en heap
            default:
                // para arrays, clases, etc.
                if (type.endsWith("[]")) {
                    return 8; // referencia a array
                }
                return 8; // por defecto: referencia/objeto
        }
    }

    /**
     * Declaración de variable:
     *      let x: integer = 5;
     *
     * TAC generado:
     *   t1 = 5
     *   x = t1
     */
    @Override
    public Void visitVariableDeclaration(CompiscriptParser.VariableDeclarationContext ctx) {
        // Obtener nombre de la variable
        String varName = ctx.Identifier().getText();
        Symbol varSym = generator.getSymbol(varName);

        if (varSym == null) {
            System.err.println(varName + " is not a variable");
            return null;
        }

        // Información para tabla de símbolos
        varSym.setTacAddress(varName); // en TAC usaremos el mismo nombre
        varSym.setSize(typeSize(varSym.getType())); // ej: 4 para int, 8 para string
        varSym.setOffset(generator.allocateLocal(varSym.getSize()));

        // generar instrucciones TAC si tiene inicializador
        if (ctx.initializer() != null) {
            // Evaluar la expresión (llamar a exprVisitor)
            String value = exprVisitor.visit(ctx.initializer().expression());

            // Generar instrucción ASSIGN
            TACInstruction instr = new TACInstruction(TACInstruction.OpType.ASSIGN);
            instr.setResult(varName);
            instr.setArg1(value);
            generator.addInstruction(instr);
        }

        return null;
    }

    /**
     * Asignación simple:
     *      x = 10;
     *
     * TAC generado:
     *   t1 = 10
     *   x = t1
     */
    @Override
    public Void visitAssignment(CompiscriptParser.AssignmentContext ctx) {
        // 1. Si es asignación simple (x = expr):
        //    a. Evaluar expresión
        //    b. Generar instrucción ASSIGN
        // 2. Si es asignación a propiedad (obj.prop = expr):
        //    a. Evaluar objeto
        //    b. Evaluar expresión
        //    c. Generar instrucción de asignación a propiedad

        if (ctx.Identifier() != null && ctx.expression().size() == 1) {
            // Asignación simple: x = expression
            String varName = ctx.Identifier().getText();
            String value = exprVisitor.visit(ctx.expression(0));

            TACInstruction instr = new TACInstruction(TACInstruction.OpType.ASSIGN);
            instr.setResult(varName);
            instr.setArg1(value);
            generator.addInstruction(instr);
        } else if (ctx.expression().size() == 2) {
            // TODO: Asignación a propiedad que debe de ser agregado junto con los Metodos asignacion de propiedades y de arrays
            /*
            * obj.prop = expr (asignación a propiedad - Prioridad 5)
            * array[i] = expr (asignación a array - Prioridad 4)
            */
        }

        return null;
    }

    /**
     * Print statement:
     *   print(x);
     *
     * TAC generado:
     *   call print(x)
     */
    @Override
    public Void visitPrintStatement(CompiscriptParser.PrintStatementContext ctx) {
        // 1. Evaluar la expresión a imprimir
        // 2. Generar instrucción CALL print

        String value = exprVisitor.visit(ctx.expression());

        TACInstruction instr = new TACInstruction(TACInstruction.OpType.CALL);
        instr.setArg1("print");
        instr.addParam(value);
        generator.addInstruction(instr);

        return null;
    }

    /**
     * Expression statement:
     *      suma(5, 10);
     *
     * TAC: Solo evalúa la expresión por sus efectos secundarios
     */
    @Override
    public Void visitExpressionStatement(CompiscriptParser.ExpressionStatementContext ctx) {
        // 1. Evaluar la expresión (puede tener efectos secundarios como llamadas)
        // 2. No hacer nada con el resultado
        exprVisitor.visit(ctx.expression());
        return null;
    }

    @Override
    public Void visitExpression(CompiscriptParser.ExpressionContext ctx) {
        // Evaluar la expresión
        exprVisitor.visit(ctx);
        return null;
    }

    
    // CONTROL DE FLUJO
    /**
     * Bloque:
     *      { stmt1; stmt2; }
     *
     * TAC: Procesar cada statement en orden
     */
    @Override
    public Void visitBlock(CompiscriptParser.BlockContext ctx) {
        // TODO P2: Implementar manejo de scope
        // 1. Visitar cada statement dentro del bloque

        for (CompiscriptParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }

        return null;
    }

    /**
     * If statement:
     *      if (condition) { ... } else { ... }
     *
     * TAC generado:
     *   t1 = condition
     *   if t1 == false goto L1
     *   [bloque then]
     *   goto L2
     * L1:
     *   [bloque else]
     * L2:
     */
    @Override
    public Void visitIfStatement(CompiscriptParser.IfStatementContext ctx) {
        // 1. Evaluar condición
        TACExprVisitor exprVisitor = new TACExprVisitor(generator); // si necesitas el generator
        String condition = exprVisitor.visit(ctx.expression());

        // 2. Crear etiquetas
        String elseLabel = generator.newLabel();
        String endLabel = generator.newLabel();

        // 3. Salto condicional: if condition == false goto elseLabel
        TACInstruction ifGoto = new TACInstruction(TACInstruction.OpType.IF_GOTO);
        ifGoto.setArg1(condition);
        ifGoto.setArg2("0");
        ifGoto.setRelop("==");
        ifGoto.setLabel(elseLabel);
        generator.addInstruction(ifGoto);

        // 4. Procesar bloque THEN
        visit(ctx.block(0));

        if (ctx.block().size() > 1) { // hay ELSE
            // 5a. Generar goto endLabel
            TACInstruction gotoEnd = new TACInstruction(TACInstruction.OpType.GOTO);
            gotoEnd.setLabel(endLabel);
            generator.addInstruction(gotoEnd);

            // 5b. Colocar etiqueta elseLabel
            TACInstruction elseLblInstr = new TACInstruction(TACInstruction.OpType.LABEL);
            elseLblInstr.setLabel(elseLabel);
            generator.addInstruction(elseLblInstr);

            // 5c. Procesar bloque ELSE
            visit(ctx.block(1));

            // 5d. Colocar etiqueta endLabel
            TACInstruction endLblInstr = new TACInstruction(TACInstruction.OpType.LABEL);
            endLblInstr.setLabel(endLabel);
            generator.addInstruction(endLblInstr);
        } else {
            // 6a. No hay else: solo colocar etiqueta elseLabel
            TACInstruction elseLblInstr = new TACInstruction(TACInstruction.OpType.LABEL);
            elseLblInstr.setLabel(elseLabel);
            generator.addInstruction(elseLblInstr);
        }

        return null;
    }



    /**
     * While loop:
     *      while (condition) { ... }
     *
     * TAC generado:
     * L1:
     *   t1 = condition
     *   if t1 == false goto L2
     *   [cuerpo]
     *   goto L1
     * L2:
     */
    @Override
    public Void visitWhileStatement(CompiscriptParser.WhileStatementContext ctx) {
        // 1. Crear etiquetas (inicio y fin)
        String startLabel = generator.newLabel();
        String endLabel = generator.newLabel();

        // 2. Marcar inicio de loop para break/continue
        generator.enterLoop(endLabel, startLabel);

        // 3. Colocar etiqueta inicio (L1)
        TACInstruction startLblInstr = new TACInstruction(TACInstruction.OpType.LABEL);
        startLblInstr.setLabel(startLabel);
        generator.addInstruction(startLblInstr);

        // 4. Evaluar condición
        TACExprVisitor exprVisitor = new TACExprVisitor(generator);
        String condition = exprVisitor.visit(ctx.expression());

        // 5. Salto condicional: if condition == false goto L2
        TACInstruction ifGoto = new TACInstruction(TACInstruction.OpType.IF_GOTO);
        ifGoto.setArg1(condition);
        ifGoto.setArg2("0");
        ifGoto.setRelop("==");
        ifGoto.setLabel(endLabel);
        generator.addInstruction(ifGoto);

        // 6. Procesar cuerpo del while
        visit(ctx.block());

        // 7. Generar goto inicio (L1)
        TACInstruction gotoStart = new TACInstruction(TACInstruction.OpType.GOTO);
        gotoStart.setLabel(startLabel);
        generator.addInstruction(gotoStart);

        // 8. Colocar etiqueta fin (L2)
        TACInstruction endLblInstr = new TACInstruction(TACInstruction.OpType.LABEL);
        endLblInstr.setLabel(endLabel);
        generator.addInstruction(endLblInstr);

        // 9. Marcar fin de loop
        generator.exitLoop();

        return null;
    }


    /**
     * Do-While loop:
     *      do { ... } while (condition);
     *
     * TAC generado:
     * L1:
     *   [cuerpo]
     *   t1 = condition
     *   if t1 == true goto L1
     * L2:
     */
    @Override
    public Void visitDoWhileStatement(CompiscriptParser.DoWhileStatementContext ctx) {
        // 1. Crear etiquetas (inicio y fin)
        String startLabel = generator.newLabel();
        String endLabel = generator.newLabel();

        // 2. Marcar inicio de loop para break/continue
        generator.enterLoop(endLabel, startLabel);

        // 3. Colocar etiqueta inicio (L1)
        TACInstruction startLblInstr = new TACInstruction(TACInstruction.OpType.LABEL);
        startLblInstr.setLabel(startLabel);
        generator.addInstruction(startLblInstr);

        // 4. Procesar cuerpo del do-while
        visit(ctx.block());

        // 5. Evaluar condición
        TACExprVisitor exprVisitor = new TACExprVisitor(generator);
        String condition = exprVisitor.visit(ctx.expression());

        // 6. Generar salto: if condition != 0 goto startLabel
        TACInstruction ifGoto = new TACInstruction(TACInstruction.OpType.IF_GOTO);
        ifGoto.setArg1(condition);
        ifGoto.setArg2("0");
        ifGoto.setRelop("!="); // true = cualquier valor distinto de 0
        ifGoto.setLabel(startLabel);
        generator.addInstruction(ifGoto);

        // 7. Colocar etiqueta fin (L2)
        TACInstruction endLblInstr = new TACInstruction(TACInstruction.OpType.LABEL);
        endLblInstr.setLabel(endLabel);
        generator.addInstruction(endLblInstr);

        // 8. Marcar fin de loop
        generator.exitLoop();

        return null;
    }


    /**
     * For loop:
     *      for (init; condition; update) { ... }
     *
     * TAC generado:
     *   [init]
     * L1:
     *   t1 = condition
     *   if t1 == false goto L2
     *   [cuerpo]
     *   [update]
     *   goto L1
     * L2:
     */
    @Override
    public Void visitForStatement(CompiscriptParser.ForStatementContext ctx) {
        // 1. Procesar inicialización (variableDeclaration o assignment)
        if (ctx.variableDeclaration() != null) {
            visit(ctx.variableDeclaration());
        } else if (ctx.assignment() != null) {
            visit(ctx.assignment());
        }

        // 2. Crear etiquetas (inicio, fin, update)
        String startLabel = generator.newLabel();
        String endLabel = generator.newLabel();
        String updateLabel = generator.newLabel();

        // 3. Marcar inicio de loop
        generator.enterLoop(endLabel, updateLabel);

        // 4. Etiqueta de inicio del loop
        TACInstruction startLblInstr = new TACInstruction(TACInstruction.OpType.LABEL);
        startLblInstr.setLabel(startLabel);
        generator.addInstruction(startLblInstr);

        // 5. Evaluar condición si existe
        if (ctx.expression(0) != null) {
            TACExprVisitor exprVisitor = new TACExprVisitor(generator);
            String condition = exprVisitor.visit(ctx.expression(0));

            // 6. Salto condicional: if condition == 0 goto endLabel
            TACInstruction ifGoto = new TACInstruction(TACInstruction.OpType.IF_GOTO);
            ifGoto.setArg1(condition);
            ifGoto.setArg2("0");
            ifGoto.setRelop("==");
            ifGoto.setLabel(endLabel);
            generator.addInstruction(ifGoto);
        }

        // 7. Procesar cuerpo del loop
        visit(ctx.block());

        // 8. Etiqueta update (solo si hay update en el for)
        if (ctx.expression().size() > 1 && ctx.expression(1) != null) {
            // Generar etiqueta update
            TACInstruction updateLblInstr = new TACInstruction(TACInstruction.OpType.LABEL);
            updateLblInstr.setLabel(updateLabel);
            generator.addInstruction(updateLblInstr);

            // Procesar expresión de actualización
            visit(ctx.expression(1));
        }

        // 9. Goto inicio del loop
        TACInstruction gotoStart = new TACInstruction(TACInstruction.OpType.GOTO);
        gotoStart.setLabel(startLabel);
        generator.addInstruction(gotoStart);

        // 10. Etiqueta de fin del loop
        TACInstruction endLblInstr = new TACInstruction(TACInstruction.OpType.LABEL);
        endLblInstr.setLabel(endLabel);
        generator.addInstruction(endLblInstr);

        // 11. Marcar fin de loop
        generator.exitLoop();

        return null;
    }

    // FUNCIONES
    @Override
    public Void visitFunctionDeclaration(CompiscriptParser.FunctionDeclarationContext ctx) {
        funcsVisitor.visit(ctx);
        return null;
    }

    @Override
    public Void visitReturnStatement(CompiscriptParser.ReturnStatementContext ctx) {
        funcsVisitor.visit(ctx);
        return null;
    }

    // ESTRUCTURAS AVANZADAS
    /**
     * Declaración de constante:
     *      const PI = 314;
     *
     * TAC: Similar a variable, pero conceptualmente es constante
     */
    @Override
    public Void visitConstantDeclaration(CompiscriptParser.ConstantDeclarationContext ctx) {
        // TODO P4: Implementar
        // Similar a variableDeclaration

        return null;
    }

    /**
     * Foreach loop:
     *      foreach (item in lista) { ... }
     *
     * TAC generado:
     *   t1 = 0                  // índice
     *   t2 = lista.length       // longitud
     * L1:
     *   t3 = t1 < t2
     *   if t3 == false goto L2
     *   t4 = lista[t1]
     *   item = t4
     *   [cuerpo]
     *   t5 = t1 + 1
     *   t1 = t5
     *   goto L1
     * L2:
     */
    @Override
    public Void visitForeachStatement(CompiscriptParser.ForeachStatementContext ctx) {
        // TODO P4: Implementar
        // 1. Obtener variable iteradora y colección
        // 2. Crear temporales para índice y longitud
        // 3. Inicializar índice = 0
        // 4. Obtener longitud de la colección
        // 5. Crear etiquetas
        // 6. Marcar inicio de loop
        // 7. Crear loop mientras índice < longitud
        // 8. Dentro del loop:
        //    a. Obtener elemento actual
        //    b. Asignar a variable iteradora
        //    c. Procesar cuerpo
        //    d. Incrementar índice
        // 9. Marcar fin de loop

        return null;
    }

    /**
     * Switch statement:
     *      switch (x) { case 1: ... default: ... }
     *
     * TAC generado:
     *   t1 = x
     *   t2 = t1 == 1
     *   if t2 == true goto L1
     *   t3 = t1 == 2
     *   if t3 == true goto L2
     *   goto Ldefault
     * L1:
     *   [código case 1]
     * L2:
     *   [código case 2]
     * Ldefault:
     *   [código default]
     * Lend:
     */
    @Override
    public Void visitSwitchStatement(CompiscriptParser.SwitchStatementContext ctx) {
        // TODO P4: Implementar
        // 1. Evaluar expresión del switch
        // 2. Crear etiquetas para cada case y default
        // 3. Para cada case:
        //    a. Evaluar valor del case
        //    b. Comparar con expresión del switch
        //    c. Generar salto condicional
        // 4. Generar salto a default
        // 5. Para cada case:
        //    a. Colocar etiqueta
        //    b. Procesar statements
        // 6. Colocar etiqueta default
        // 7. Procesar default statements
        // 8. Colocar etiqueta end

        return null;
    }

    /**
     * Break statement:
     *      break;
     *
     * TAC generado:
     *   goto Lend
     */
    @Override
    public Void visitBreakStatement(CompiscriptParser.BreakStatementContext ctx) {
        // TODO P4: Implementar
        // 1. Obtener etiqueta de break actual: generator.getCurrentBreakLabel()
        // 2. Si es null, error (break fuera de loop)
        // 3. Generar goto a la etiqueta

        String breakLabel = generator.getCurrentBreakLabel();

        if (breakLabel != null) {
            TACInstruction gotoBreak = new TACInstruction(TACInstruction.OpType.GOTO);
            gotoBreak.setLabel(breakLabel);
            generator.addInstruction(gotoBreak);
        } else {
            System.err.println("ERROR: break fuera de un loop");
        }

        return null;
    }

    /**
     * Continue statement:
     *      continue;
     *
     * TAC generado:
     *   goto Lstart
     */
    @Override
    public Void visitContinueStatement(CompiscriptParser.ContinueStatementContext ctx) {
        // TODO P4: Implementar
        // Similar a break pero usando getCurrentContinueLabel()

        return null;
    }

    /**
     * Try-Catch:
     *      try { ... } catch (err) { ... }
     *
     * TAC generado (simplificado):
     *   [bloque try]
     *   goto Lend
     * Lcatch:
     *   err = exception
     *   [bloque catch]
     * Lend:
     */
    @Override
    public Void visitTryCatchStatement(CompiscriptParser.TryCatchStatementContext ctx) {
        // TODO P4: Implementar (simplificado)
        // El manejo real de excepciones requiere soporte del runtime
        // 1. Crear etiquetas
        // 2. Procesar bloque try
        // 3. Generar goto al final
        // 4. Colocar etiqueta catch
        // 5. Asignar excepción a variable
        // 6. Procesar bloque catch
        // 7. Colocar etiqueta end

        return null;
    }

    
    // POO (Clases y Objetos)
    /**
     * Declaración de clase:
     *      class Perro : Animal { ... }
     *
     * TAC generado:
     * class_Perro:
     *   [miembros y Metodos]
     */
    @Override
    public Void visitClassDeclaration(CompiscriptParser.ClassDeclarationContext ctx) {
        // TODO P5: Implementar
        // 1. Obtener nombre de la clase
        // 2. Obtener clase padre (si existe)
        // 3. Marcar inicio de clase: generator.enterClass(name)
        // 4. Generar etiqueta class_name:
        // 5. Procesar cada miembro (variables, funciones)
        // 6. Marcar fin de clase: generator.exitClass()

        return null;
    }

    
    // UTILIDADES
    /**
     * Metodo genérico para visitar cualquier statement
     */
    @Override
    public Void visitStatement(CompiscriptParser.StatementContext ctx) {
        return visitChildren(ctx);
    }
}