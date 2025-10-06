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
                "t2 = name",
                "call print(t2)",
                "end speak"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testClassWithThisUsage() {
        String code = """
        class Counter {
            let value: integer = 0;
            function inc() {
                this.value = this.value + 1;
            }
        }
        """;
        List<String> expected = Arrays.asList(
                "Counter:",
                "t1 = 0",
                "value = t1",
                "inc:",
                "t2 = this.value",
                "t3 = 1",
                "t4 = t2 + t3",
                "this.value = t4",
                "end inc"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

}
