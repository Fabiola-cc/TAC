package com.fmd;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tests para P3")
public class P3Tests {
    TestInit testInit = new TestInit();

    @Test
    void testFunctionDefinition() {
        String code = "function speak(name: string): string {\n" +
                "   let printVar: string = \" makes a sound.\";\n" +
                "   return name + printVar;\n" +
                "}";
        List<String> expected = Arrays.asList(
                "speak:",
                "printVar = \" makes a sound.\"",
                "t1 = name + printVar",
                "return t1",
                "end speak"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testFunctionCall() {
        String code = "function speak(name: string): string {\n" +
                "   let printVar: string = \" makes a sound.\";\n" +
                "   return name + printVar;\n" +
                "}\n" + "speak(\"hola\");";
        List<String> expected = Arrays.asList(
                "speak:",
                "printVar = \" makes a sound.\"",
                "t1 = name + printVar",
                "return t1",
                "end speak",
                "t2 = \"hola\"",
                "call speak(t2)"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }
}
