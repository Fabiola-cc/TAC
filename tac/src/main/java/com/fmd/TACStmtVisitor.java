package com.fmd;

import com.fmd.CompiscriptParser;
import com.fmd.CompiscriptBaseVisitor;
import com.fmd.modules.Symbol;
import com.fmd.modules.TACInstruction;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
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

        // 2. Crear etiquetas (inicio y fin)
        String startLabel = generator.newLabel();
        String endLabel = generator.newLabel();

        // 3. Marcar inicio de loop
        generator.enterLoop(endLabel, null); // no necesitamos updateLabel

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

        // 8. Generar expresión de actualización (incremento) inmediatamente después del cuerpo
        if (ctx.expression().size() > 1 && ctx.expression(1) != null) {
            visit(ctx.expression(1));
        }

        // 9. Saltar al inicio del loop
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
        // Crear temporales para índice
        String temp_index = generator.newTemp();
        String temp_len = generator.newTemp();
        String itemName = ctx.Identifier().getText();

        // Inicializar índice = 0
        TACInstruction indexInstr = new TACInstruction(TACInstruction.OpType.ASSIGN);
        indexInstr.setResult(temp_index);
        indexInstr.setArg1("0");
        generator.addInstruction(indexInstr);

        // Obtener longitud de la colección
        String listName = exprVisitor.visit(ctx.expression());
        Symbol list = generator.getSymbol(listName);
        int listLen = (list.getDimensions() != null) ? list.getDimensions().get(0) : 0;

        TACInstruction lenInstr = new TACInstruction(TACInstruction.OpType.ASSIGN);
        lenInstr.setResult(temp_len);
        lenInstr.setArg1(String.valueOf(listLen));
        generator.addInstruction(lenInstr);


        // Crear etiquetas
        String loopLabel = generator.newLabel();
        String loopEndLabel = generator.newLabel();

        // Marcar inicio de loop
        generator.enterLoop(temp_len, loopLabel);
        TACInstruction loopInstr = new TACInstruction(TACInstruction.OpType.LABEL);
        loopInstr.setLabel(loopLabel);
        generator.addInstruction(loopInstr);

        // Crear loop mientras índice < longitud
        TACInstruction condInstr = new TACInstruction(TACInstruction.OpType.BINARY_OP);
        String cond_temp = generator.newTemp();
        condInstr.setResult(cond_temp);
        condInstr.setArg1(temp_index);
        condInstr.setOperator("<");
        condInstr.setArg2(temp_len);
        generator.addInstruction(condInstr);

        TACInstruction moveInstr = new TACInstruction(TACInstruction.OpType.IF_GOTO);
        moveInstr.setArg1(cond_temp);
        moveInstr.setRelop("==");
        moveInstr.setArg2("0"); // FALSE
        moveInstr.setLabel(loopEndLabel);
        generator.addInstruction(moveInstr);

        //  Obtener elemento actual
        String nameList = ctx.expression().getText(); // TODO
        String access_temp = generator.newTemp();

        TACInstruction accessInstr = new TACInstruction(TACInstruction.OpType.ASSIGN);
        accessInstr.setResult(access_temp);
        accessInstr.setArg1(nameList + "[" + temp_index + "]");
        generator.addInstruction(accessInstr);

        //  Asignar a variable iteradora
        TACInstruction iterInstr = new TACInstruction(TACInstruction.OpType.ASSIGN);
        iterInstr.setResult(itemName);
        iterInstr.setArg1(access_temp);
        generator.addInstruction(iterInstr);

        //  Procesar cuerpo
        visit(ctx.block()); // Revisar uso de item_name

        // Incrementar índice
        String temp = generator.newTemp();
        TACInstruction tempCounterInstr = new TACInstruction(TACInstruction.OpType.BINARY_OP);
        tempCounterInstr.setResult(temp);
        tempCounterInstr.setArg1(temp_index);
        tempCounterInstr.setOperator("+");
        tempCounterInstr.setArg2("1");
        generator.addInstruction(tempCounterInstr);

        TACInstruction counterInstr = new TACInstruction(TACInstruction.OpType.ASSIGN);
        counterInstr.setResult(temp_index);
        counterInstr.setArg1(temp);
        generator.addInstruction(counterInstr);

        TACInstruction restartInstr = new TACInstruction(TACInstruction.OpType.GOTO);
        restartInstr.setLabel(loopLabel);
        generator.addInstruction(restartInstr);

        // Marcar fin de loop
        TACInstruction loopEndInstr = new TACInstruction(TACInstruction.OpType.LABEL);
        loopEndInstr.setLabel(loopEndLabel);
        generator.addInstruction(loopEndInstr);
        generator.exitLoop();
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
        TACExprVisitor exprVisitor = new TACExprVisitor(generator);

        // 1. Evaluar la expresión del switch y guardarla en un temporal
        String switchExpr = exprVisitor.visit(ctx.expression());
        String switchTemp = generator.newTemp();
        TACInstruction assignSwitch = new TACInstruction(TACInstruction.OpType.ASSIGN);
        assignSwitch.setResult(switchTemp);
        assignSwitch.setArg1(switchExpr);
        generator.addInstruction(assignSwitch);

        // 2. Crear etiqueta final del switch
        String endLabel = generator.newLabel();

        // 3. Crear etiquetas para cada case
        List<String> caseLabels = new ArrayList<>();
        for (int i = 0; i < ctx.switchCase().size(); i++) {
            caseLabels.add(generator.newLabel());
        }
        String defaultLabel = ctx.defaultCase() != null ? generator.newLabel() : endLabel;

        // 4. Generar comparaciones y saltos condicionales a cada case
        for (int i = 0; i < ctx.switchCase().size(); i++) {
            CompiscriptParser.SwitchCaseContext caseCtx = ctx.switchCase(i);
            String caseValue = exprVisitor.visit(caseCtx.expression());

            // if switchTemp == caseValue goto caseLabel
            TACInstruction ifGoto = new TACInstruction(TACInstruction.OpType.IF_GOTO);
            ifGoto.setArg1(switchTemp);
            ifGoto.setArg2(caseValue);
            ifGoto.setRelop("==");
            ifGoto.setLabel(caseLabels.get(i));
            generator.addInstruction(ifGoto);
        }

        // 5. Salto a default si ningún case coincide
        TACInstruction gotoDefault = new TACInstruction(TACInstruction.OpType.GOTO);
        gotoDefault.setLabel(defaultLabel);
        generator.addInstruction(gotoDefault);

        // 6. Generar código de cada case
        for (int i = 0; i < ctx.switchCase().size(); i++) {
            // Etiqueta del case
            TACInstruction caseLabelInstr = new TACInstruction(TACInstruction.OpType.LABEL);
            caseLabelInstr.setLabel(caseLabels.get(i));
            generator.addInstruction(caseLabelInstr);

            // Statements del case
            visit((ParseTree) ctx.switchCase(i).statement());

            // Saltar al final del switch al terminar el case
            TACInstruction gotoEnd = new TACInstruction(TACInstruction.OpType.GOTO);
            gotoEnd.setLabel(endLabel);
            generator.addInstruction(gotoEnd);
        }

        // 7. Generar código del default si existe
        if (ctx.defaultCase() != null) {
            TACInstruction defaultLabelInstr = new TACInstruction(TACInstruction.OpType.LABEL);
            defaultLabelInstr.setLabel(defaultLabel);
            generator.addInstruction(defaultLabelInstr);

            visit((ParseTree) ctx.defaultCase().statement());
        }

        // 8. Etiqueta final del switch
        TACInstruction endInstr = new TACInstruction(TACInstruction.OpType.LABEL);
        endInstr.setLabel(endLabel);
        generator.addInstruction(endInstr);

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
        // 1. Obtener etiqueta de continue actual
        String continueLabel = generator.getCurrentContinueLabel();

        // 2. Verificar que exista (si no, error)
        if (continueLabel != null) {
            TACInstruction gotoContinue = new TACInstruction(TACInstruction.OpType.GOTO);
            gotoContinue.setLabel(continueLabel);
            generator.addInstruction(gotoContinue);
        } else {
            System.err.println("ERROR: continue fuera de un loop");
        }

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
        // 1. Crear etiquetas
        String catchLabel = generator.newLabel();
        String endLabel = generator.newLabel();

        // 2. Procesar bloque try
        visit(ctx.block(0)); // ctx.block(0) es el try

        // 3. Salto al final del try
        TACInstruction gotoEnd = new TACInstruction(TACInstruction.OpType.GOTO);
        gotoEnd.setLabel(endLabel);
        generator.addInstruction(gotoEnd);

        // 4. Etiqueta catch
        TACInstruction catchLblInstr = new TACInstruction(TACInstruction.OpType.LABEL);
        catchLblInstr.setLabel(catchLabel);
        generator.addInstruction(catchLblInstr);

        // 5. Asignar excepción a variable del catch
        String catchVar = ctx.Identifier().getText();
        TACInstruction assignEx = new TACInstruction(TACInstruction.OpType.ASSIGN);
        assignEx.setResult(catchVar);
        assignEx.setArg1("exception"); // representamos la excepción de forma abstracta
        generator.addInstruction(assignEx);

        // 6. Procesar bloque catch
        visit(ctx.block(1)); // ctx.block(1) es el catch

        // 7. Etiqueta final
        TACInstruction endLblInstr = new TACInstruction(TACInstruction.OpType.LABEL);
        endLblInstr.setLabel(endLabel);
        generator.addInstruction(endLblInstr);

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