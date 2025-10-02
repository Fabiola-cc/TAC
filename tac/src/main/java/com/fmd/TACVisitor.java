package com.fmd;

import com.fmd.modules.Symbol;

import java.util.HashMap;
import java.util.Map;

/**
 * Visitor Coordinador Principal
 *
 * RESPONSABILIDADES:
 * - Punto de entrada para la generación de TAC
 * - Crea e inicializa el TACGenerator
 * - Crea e inicializa TACExprVisitor y TACStmtVisitor
 * - Coordina el recorrido del programa
 * - Imprime el TAC generado
 *
 * FLUJO:
 * 1. Main llama a TACVisitor.visit(tree)
 * 2. TACVisitor crea generator, exprVisitor, stmtVisitor
 * 3. TACVisitor delega cada statement a stmtVisitor
 * 4. stmtVisitor usa exprVisitor cuando necesita evaluar expresiones
 * 5. Ambos visitors usan el mismo generator
 * 6. Al final, TACVisitor imprime todas las instrucciones
 */
public class TACVisitor extends CompiscriptBaseVisitor<Void> {

    private final TACGenerator generator;
    private final TACExprVisitor exprVisitor;
    private final TACStmtVisitor stmtVisitor;

    /**
     * Constructor: inicializa toda la arquitectura
     */
    public TACVisitor(Map<String, Symbol> symTable) {
        // 1. Crear el generador (estado compartido)
        this.generator = new TACGenerator(symTable);

        // 2. Crear el visitor de expresiones (usa el generator)
        this.exprVisitor = new TACExprVisitor(generator);

        // 3. Crear el visitor de statements (usa generator y exprVisitor)
        this.stmtVisitor = new TACStmtVisitor(generator, exprVisitor);
    }

    /**
     * Punto de entrada: procesa el programa
     */
    @Override
    public Void visitProgram(CompiscriptParser.ProgramContext ctx) {
        // Procesar cada statement del programa
        for (CompiscriptParser.StatementContext stmt : ctx.statement()) {
            stmtVisitor.visit(stmt);
        }

        // Imprimir resultados
        System.out.println("TAC GENERADO\n");

        generator.printInstructions();
        return null;
    }

    /**
     * Metodo para imprimir todos los simbolos
     */
    public void printTable(){
        generator.imprimirSimbolos(new HashMap<>());
    }

    /**
     * Obtiene el generador (útil para testing)
     */
    public TACGenerator getGenerator() {
        return generator;
    }

    /**
     * Obtiene el visitor de expresiones (útil para testing)
     */
    public TACExprVisitor getExprVisitor() {
        return exprVisitor;
    }

    /**
     * Obtiene el visitor de statements (útil para testing)
     */
    public TACStmtVisitor getStmtVisitor() {
        return stmtVisitor;
    }
}