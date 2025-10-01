package com.fmd;

import com.fmd.CompiscriptParser;
import com.fmd.CompiscriptBaseVisitor;
import com.fmd.modules.TACInstruction;

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

    public TACStmtVisitor(TACGenerator generator, TACExprVisitor exprVisitor) {
        this.generator = generator;
        this.exprVisitor = exprVisitor;
    }

    // =================================================================
    // PRIORIDAD 1: STATEMENTS BÁSICOS
    // =================================================================

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
        // 1. Obtener nombre de la variable
        // 2. Si tiene inicializador:
        //    a. Evaluar la expresión (llamar a exprVisitor)
        //    b. Generar instrucción ASSIGN
        // 3. Si no tiene inicializador, no generar nada (o asignar null)

        String varName = ctx.Identifier().getText();

        if (ctx.initializer() != null) {
            String value = exprVisitor.visit(ctx.initializer().expression());

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
            // TODO: Asignación a propiedad que debe de ser agregado junto con los métodos asignacion de propiedades y de arrays
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

    // =================================================================
    // PRIORIDAD 2: CONTROL DE FLUJO
    // =================================================================

    /**
     * Bloque:
     *      { stmt1; stmt2; }
     *
     * TAC: Procesar cada statement en orden
     */
    @Override
    public Void visitBlock(CompiscriptParser.BlockContext ctx) {
        // TODO P2: Implementar
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
        // TODO P2: Implementar
        // 1. Evaluar condición
        // 2. Crear etiquetas (L1 = else/fin, L2 = fin)
        // 3. Generar salto condicional if condition == false goto L1
        // 4. Procesar bloque THEN
        // 5. Si hay else:
        //    a. Generar goto L2
        //    b. Colocar etiqueta L1
        //    c. Procesar bloque ELSE
        //    d. Colocar etiqueta L2
        // 6. Si no hay else:
        //    a. Colocar etiqueta L1

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
        // TODO P2: Implementar
        // 1. Crear etiquetas (L1 = inicio, L2 = fin)
        // 2. Marcar inicio de loop: generator.enterLoop(L2, L1)
        // 3. Colocar etiqueta L1
        // 4. Evaluar condición
        // 5. Generar salto condicional if condition == false goto L2
        // 6. Procesar cuerpo
        // 7. Generar goto L1
        // 8. Colocar etiqueta L2
        // 9. Marcar fin de loop: generator.exitLoop()

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
        // TODO P2: Implementar
        // Similar a while pero evalúa condición al final
        // 1. Crear etiquetas
        // 2. Marcar inicio de loop
        // 3. Colocar etiqueta L1
        // 4. Procesar cuerpo
        // 5. Evaluar condición
        // 6. Generar salto if condition == true goto L1
        // 7. Colocar etiqueta L2
        // 8. Marcar fin de loop

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
        // TODO P2: Implementar
        // 1. Procesar inicialización (si existe)
        // 2. Crear etiquetas (L1 = inicio, L2 = fin, L3 = update)
        // 3. Marcar inicio de loop con L2 y L3
        // 4. Colocar etiqueta L1
        // 5. Evaluar condición (si existe)
        // 6. Generar salto condicional
        // 7. Procesar cuerpo
        // 8. Colocar etiqueta L3 (para continue)
        // 9. Procesar update (si existe)
        // 10. Generar goto L1
        // 11. Colocar etiqueta L2
        // 12. Marcar fin de loop

        return null;
    }

    // =================================================================
    // PRIORIDAD 3: FUNCIONES
    // =================================================================

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
        // TODO P3: Implementar
        // 1. Obtener nombre de la función
        // 2. Marcar inicio de función: generator.enterFunction(name)
        // 3. Generar etiqueta func_name:
        // 4. Procesar parámetros (si es necesario)
        // 5. Procesar cuerpo de la función
        // 6. Generar return null implícito (si no hay return)
        // 7. Marcar fin de función: generator.exitFunction()

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
        // TODO P3: Implementar
        // 1. Si hay expresión, evaluarla
        // 2. Si no hay expresión, usar "null"
        // 3. Generar instrucción RETURN

        String value = "null";

        if (ctx.expression() != null) {
            value = exprVisitor.visit(ctx.expression());
        }

        TACInstruction instr = new TACInstruction(TACInstruction.OpType.RETURN);
        instr.setArg1(value);
        generator.addInstruction(instr);

        return null;
    }

    // =================================================================
    // PRIORIDAD 4: ESTRUCTURAS AVANZADAS
    // =================================================================

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

    // =================================================================
    // PRIORIDAD 5: POO (Clases y Objetos)
    // =================================================================

    /**
     * Declaración de clase:
     *      class Perro : Animal { ... }
     *
     * TAC generado:
     * class_Perro:
     *   [miembros y métodos]
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

    // =================================================================
    // UTILIDADES
    // =================================================================

    /**
     * Método genérico para visitar cualquier statement
     */
    @Override
    public Void visitStatement(CompiscriptParser.StatementContext ctx) {
        return visitChildren(ctx);
    }
}