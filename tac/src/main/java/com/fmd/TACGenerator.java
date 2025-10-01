package com.fmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.fmd.modules.TACInstruction;

/**
 * Generador de Código de Tres Direcciones (TAC)
 *
 * RESPONSABILIDADES:
 * - Generar temporales únicos (t1, t2, t3, ...)
 * - Generar etiquetas únicas (L1, L2, L3, ...)
 * - Almacenar TODAS las instrucciones TAC en orden
 * - Mantener contexto para break/continue (pilas de etiquetas)
 */
public class TACGenerator {

    // Lista de todas las instrucciones TAC generadas
    private List<TACInstruction> instructions;

    // Contadores para generar nombres únicos
    private int tempCounter;
    private int labelCounter;

    // Pilas para manejar break y continue en loops
    private Stack<String> breakLabels;     // Etiquetas de salida de loops
    private Stack<String> continueLabels;  // Etiquetas de continuación de loops

    // Contexto actual (para funciones, clases, etc.)
    private String currentFunction;        // Nombre de la función actual
    private String currentClass;           // Nombre de la clase actual

    public TACGenerator() {
        this.instructions = new ArrayList<>();
        this.tempCounter = 0;
        this.labelCounter = 0;
        this.breakLabels = new Stack<>();
        this.continueLabels = new Stack<>();
        this.currentFunction = null;
        this.currentClass = null;
    }

    // =================================================================
    // PRIORIDAD 1: MÉTODOS BÁSICOS
    // =================================================================

    /**
     * Genera un nuevo temporal único
     * @return Nombre del temporal (t1, t2, t3, ...)
     */
    public String newTemp() {
        tempCounter++;
        return "t" + tempCounter;
    }

    /**
     * Genera una nueva etiqueta única
     * @return Nombre de la etiqueta (L1, L2, L3, ...)
     */
    public String newLabel() {
        labelCounter++;
        return "L" + labelCounter;
    }

    /**
     * Añade una instrucción TAC a la lista
     * @param instr Instrucción a añadir
     */
    public void addInstruction(TACInstruction instr) {
        instructions.add(instr);
    }

    /**
     * Devuelve todas las instrucciones generadas
     * @return Lista de instrucciones TAC
     */
    public List<TACInstruction> getInstructions() {
        return instructions;
    }

    /**
     * Imprime todas las instrucciones de manera legible
     */
    public void printInstructions() {
        if (instructions.isEmpty()) {
            System.out.println("(No se generaron instrucciones TAC)");
            return;
        }

        for (int i = 0; i < instructions.size(); i++) {
            System.out.printf("%3d: %s\n", i, instructions.get(i));
        }
    }

    // =================================================================
    // PRIORIDAD 2: MANEJO DE LOOPS (break/continue)
    // =================================================================

    /**
     * Marca el inicio de un loop (push etiquetas para break/continue)
     * @param breakLabel Etiqueta de salida del loop
     * @param continueLabel Etiqueta de continuación del loop
     */
    public void enterLoop(String breakLabel, String continueLabel) {
        breakLabels.push(breakLabel);
        continueLabels.push(continueLabel);
    }

    /**
     * Marca el fin de un loop (pop etiquetas)
     */
    public void exitLoop() {
        if (!breakLabels.isEmpty()) {
            breakLabels.pop();
        }
        if (!continueLabels.isEmpty()) {
            continueLabels.pop();
        }
    }

    /**
     * Obtiene la etiqueta de break del loop actual
     * @return Etiqueta de break o null si no hay loop activo
     */
    public String getCurrentBreakLabel() {
        return breakLabels.isEmpty() ? null : breakLabels.peek();
    }

    /**
     * Obtiene la etiqueta de continue del loop actual
     * @return Etiqueta de continue o null si no hay loop activo
     */
    public String getCurrentContinueLabel() {
        return continueLabels.isEmpty() ? null : continueLabels.peek();
    }

    // =================================================================
    // PRIORIDAD 3: CONTEXTO DE FUNCIONES
    // =================================================================

    /**
     * Marca el inicio de una función
     * @param functionName Nombre de la función
     */
    public void enterFunction(String functionName) {
        this.currentFunction = functionName;
    }

    /**
     * Marca el fin de una función
     */
    public void exitFunction() {
        this.currentFunction = null;
    }

    /**
     * Obtiene el nombre de la función actual
     * @return Nombre de la función o null si no hay función activa
     */
    public String getCurrentFunction() {
        return currentFunction;
    }

    // =================================================================
    // PRIORIDAD 5: CONTEXTO DE CLASES (POO)
    // =================================================================

    /**
     * Marca el inicio de una clase
     * @param className Nombre de la clase
     */
    public void enterClass(String className) {
        this.currentClass = className;
    }

    /**
     * Marca el fin de una clase
     */
    public void exitClass() {
        this.currentClass = null;
    }

    /**
     * Obtiene el nombre de la clase actual
     * @return Nombre de la clase o null si no hay clase activa
     */
    public String getCurrentClass() {
        return currentClass;
    }

    // =================================================================
    // UTILIDADES
    // =================================================================

    /**
     * Limpia todas las instrucciones y resetea contadores
     */
    public void reset() {
        instructions.clear();
        tempCounter = 0;
        labelCounter = 0;
        breakLabels.clear();
        continueLabels.clear();
        currentFunction = null;
        currentClass = null;
    }

    /**
     * Obtiene el número total de instrucciones generadas
     * @return Cantidad de instrucciones
     */
    public int getInstructionCount() {
        return instructions.size();
    }

}