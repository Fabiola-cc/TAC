package com.fmd;

import java.util.*;

import com.fmd.modules.Symbol;
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
    private Boolean isAssignment;

    // Tabla de simbolos
    private Map<String, SemanticVisitor.Entorno> scopeTable = new LinkedHashMap<>();
    private String currentScopeLine;        // Id del scope actual
    private int currentOffset = 0;
    private Stack<Integer> offsetStack; // para recordar offsets de cada scope

    public TACGenerator(Map<String, SemanticVisitor.Entorno> scopeTable) {
        this.instructions = new ArrayList<>();
        this.tempCounter = 0;
        this.labelCounter = 0;
        this.breakLabels = new Stack<>();
        this.continueLabels = new Stack<>();
        this.currentFunction = null;
        this.currentClass = null;
        this.currentScopeLine = "0";
        this.scopeTable = scopeTable;
        this.offsetStack = new Stack<>();
        this.isAssignment = false;
    }

    // MÉTODOS BÁSICOS
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
     * Devuelve la última instrucción generada
     * @return instrucció TAC
     */
    public TACInstruction getLastInstruction() {
        return instructions.get(instructions.size() - 1);
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

    /**
     * Mantiene el control del offset en memoria
     */
    public int allocateLocal(int size) {
        int assignedOffset = currentOffset; // inicio del bloque
        currentOffset += size;              // avanzar para el siguiente
        return assignedOffset;              // devolver el inicio real
    }


    // PRIORIDAD 2: MANEJO DE LOOPS (break/continue)
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


    // PRIORIDAD 3: CONTEXTO DE FUNCIONES
    /**
     * Marca el inicio de una función
     * @param functionName Nombre de la función
     */
    public void enterFunction(String functionName) {
        offsetStack.push(currentOffset);
        currentOffset = 0;
        this.currentFunction = functionName;
    }

    /**
     * Marca el fin de una función
     */
    public void exitFunction() {
        this.currentFunction = null;
        currentOffset += offsetStack.pop();
    }

    /**
     * Obtiene el nombre de la función actual
     * @return Nombre de la función o null si no hay función activa
     */
    public String getCurrentFunction() {
        return currentFunction;
    }


    // PRIORIDAD 5: CONTEXTO DE CLASES (POO)
    /**
     * Marca el inicio de una clase
     * @param className Nombre de la clase
     */
    public void enterClass(String className) {
        offsetStack.push(currentOffset);
        currentOffset = 0;
        this.currentClass = className;
    }

    /**
     * Marca el fin de una clase
     */
    public void exitClass() {
        this.currentClass = null;
        currentOffset += offsetStack.pop();
    }

    /**
     * Obtiene el nombre de la clase actual
     * @return Nombre de la clase o null si no hay clase activa
     */
    public String getCurrentClass() {
        return currentClass;
    }


    // UTILIDADES
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

    /**
     * Buscar simbolo en el entorno actual registrado
     * */
    public Symbol getSymbol(String name) {
        SemanticVisitor.Entorno thisScope = scopeTable.get(currentScopeLine);

        return thisScope.obtener(name);
    }

    public SemanticVisitor.Entorno getScope(String line) {
        return scopeTable.get(line);
    }

    public Map<String, Symbol> getSymbolTable(String line) {
        return scopeTable.get(line).getSymbolsLocal();
    }

    /** imprime recursivamente los símbolos por scope */
    public void imprimirSimbolos() {
        System.out.println("===== TABLAS DE SÍMBOLOS POR SCOPE =====");
        for (Map.Entry<String, SemanticVisitor.Entorno> entry : scopeTable.entrySet()) {
            SemanticVisitor.Entorno entorno = entry.getValue();
            Map<String, Symbol> locals = entorno.getSymbolsLocal(); // usa el local

            System.out.println("Scope (" + locals.size() + " symbols)");
            for (Symbol sym : locals.values()) {
                System.out.println("  " + sym.toStringTAC());
            }
            System.out.println();
        }
        System.out.println("========================================");
    }


    public void setCurrentScopeLine(String currentScopeLine) {
        this.currentScopeLine = currentScopeLine;
    }

    public Boolean getAssignment() {
        return isAssignment;
    }

    public void setAssignment(Boolean assignment) {
        isAssignment = assignment;
    }

    /**
     * Permite reconocer el offset de cada variable según su tipo
     * @param type string
     * @return offset int
     */
    public int typeSize(String type) {
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
}