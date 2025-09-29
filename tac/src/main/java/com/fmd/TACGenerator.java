package com.fmd;

import java.util.ArrayList;
import java.util.List;

import com.fmd.modules.TACInstruction;

public class TACGenerator {

    private List<TACInstruction> instructions;
    private int tempCounter;
    private int labelCounter;

    public TACGenerator() {
        this.instructions = new ArrayList<>();
        this.tempCounter = 0;
        this.labelCounter = 0;
    }

    // Método principal para generar TAC desde el AST
    public void generate(Object ast) {
        // TODO: recorrer el AST y generar instrucciones
    }

    // Genera un nuevo temporal
    public String newTemp() {
        tempCounter++;
        return "t" + tempCounter;
    }

    // Genera una nueva etiqueta
    public String newLabel() {
        labelCounter++;
        return "L" + labelCounter;
    }

    // Métodos vacíos para cada tipo de nodo AST
    public void handleAssignment(Object node) {
        // TODO
    }

    public void handleBinaryOperation(Object node) {
        // TODO
    }

    public void handleIf(Object node) {
        // TODO
    }

    public void handleWhile(Object node) {
        // TODO
    }

    public void handleDoWhile(Object node) {
        // TODO
    }

    public void handleFor(Object node) {
        // TODO
    }

    public void handleSwitch(Object node) {
        // TODO
    }

    public void handleFunctionCall(Object node) {
        // TODO
    }

    public void handleFunctionDeclaration(Object node) {
        // TODO
    }

    public void handleReturn(Object node) {
        // TODO
    }

    public void handlePrint(Object node) {
        // TODO
    }

    public void handleTryCatch(Object node) {
        // TODO
    }

    public void handleClass(Object node) {
        // TODO
    }

    public void handleNew(Object node) {
        // TODO
    }

    // Agrega una instrucción TAC
    public void addInstruction(TACInstruction instr) {
        instructions.add(instr);
    }

    // Devuelve todas las instrucciones generadas
    public List<TACInstruction> getInstructions() {
        return instructions;
    }

    // Imprime las instrucciones de manera legible
    public void printInstructions() {
        for (TACInstruction instr : instructions) {
            System.out.println(instr);
        }
    }
}
