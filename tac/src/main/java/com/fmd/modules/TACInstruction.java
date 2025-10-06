package com.fmd.modules;

import java.util.List;
import java.util.ArrayList;

/**
 * Representa una instrucción TAC (Three-Address Code)
 */
public class TACInstruction {

    public enum OpType {
        ASSIGN,        // x = y
        BINARY_OP,     // x = y + z
        UNARY_OP,      // x = -y
        LABEL,         // label:
        GOTO,          // goto label
        IF_GOTO,       // if x relop y goto label
        CALL,          // call f
        ASSIGN_CALL,    // x = call f
        NEW,            // new object
        RETURN,         // return x
        END,             // end f
        TRY_BEGIN,     // try_begin catch_label
        TRY_END        // try_end
    }

    private OpType op;
    private String result;      // variable temporal o destino
    private String arg1;        // primer argumento
    private String arg2;        // segundo argumento (para binarios)
    private String operator;    // operador (+, -, *, /, %, etc.) 
    private String relop;       // operador relacional (para if)
    private String label;       // etiqueta (para goto/if)
    private List<String> params; // parámetros de llamada

    public TACInstruction(OpType op) {
        this.op = op;
        this.params = new ArrayList<>();
    }

    // Getters y setters
    public OpType getOp() { return op; }
    public void setResult(String result) { this.result = result; }
    public String getResult() { return result; }
    public void setArg1(String arg1) { this.arg1 = arg1; }
    public String getArg1() { return arg1; }
    public void setArg2(String arg2) { this.arg2 = arg2; }
    public String getArg2() { return arg2; }
    public void setOperator(String operator) { this.operator = operator; }  
    public String getOperator() { return operator; }                        
    public void setRelop(String relop) { this.relop = relop; }
    public String getRelop() { return relop; }
    public void setLabel(String label) { this.label = label; }
    public String getLabel() { return label; }
    public void addParam(String param) { this.params.add(param); }
    public List<String> getParams() { return params; }

    @Override
    public String toString() {
        switch(op) {
            case ASSIGN:
                return result + " = " + arg1;
            case BINARY_OP:
                return result + " = " + arg1 + " " + operator + " " + arg2;  
            case UNARY_OP:
                return result + " = " + operator + arg1;  
            case LABEL:
                return label + ":";
            case GOTO:
                return "goto " + label;
            case IF_GOTO:
                return "if " + arg1 + " " + relop + " " + arg2 + " goto " + label;
            case CALL:
                return "call " + arg1 + "(" + String.join(", ", params) + ")";
            case ASSIGN_CALL:
                return result + " = call " + arg1 + "(" + String.join(", ", params) + ")";
            case NEW:
                return result + " = " + "new " + arg1 + "(" + String.join(", ", params) + ")";
            case RETURN:
                return "return " + arg1;
            case END:
                return "end " + label;
            case TRY_BEGIN:
                return "try_begin " + label;  // label apunta al catch
            case TRY_END:
                return "try_end";
            default:
                return "UNKNOWN";
        }
    }
}