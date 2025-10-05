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

public class P5Tests {
    TestInit testInit = new TestInit();

    @Test
    void testClassDefinition() {
        String code = """
        class Animal {
            let name: string = "doggy";
            function speak() {
                print(name);
            }
        }
        """;
        List<String> expected = Arrays.asList(
                "Animal:",
                "t1 = \"doggy\"",
                "name = t1",
                "speak:",
                "call print(name)",
                "end speak"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }
}
