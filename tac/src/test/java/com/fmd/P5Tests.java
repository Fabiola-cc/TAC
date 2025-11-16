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
                "Class Animal:",
                "t1 = \"doggy\"",
                "name = t1",
                "Function speak:",
                "t1 = name",
                "call print(t1)",
                "end speak",
                "end Class Animal"
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
                "Class Counter:",
                "t1 = 0",
                "value = t1",
                "Function inc:",
                "t1 = this.value",
                "t2 = 1",
                "t3 = t1 + t2",
                "this.value = t3",
                "end inc",
                "end Class Counter"
        );
        assertEquals(expected, testInit.generateTAC(code));
    }

}
