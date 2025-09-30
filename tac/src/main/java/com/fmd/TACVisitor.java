package com.fmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;

import com.fmd.CompiscriptLexer;
import com.fmd.CompiscriptParser;
import com.fmd.CompiscriptBaseVisitor;

public class TACVisitor extends CompiscriptBaseVisitor<Void> {

    public TACVisitor() {}

    @Override
    public Void visitProgram(CompiscriptParser.ProgramContext ctx) {
        for (CompiscriptParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }
        return null;
    }

    // General Statements
    @Override
    public Void visitBlock(CompiscriptParser.BlockContext ctx) {
        for (CompiscriptParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }
        return null;
    }

}