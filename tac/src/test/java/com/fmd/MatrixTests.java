package com.fmd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tests para matrices")
public class MatrixTests {
    TestInit testInit = new TestInit();

    @Test
    void testMatrixInitialization() {
        String code = """
        var matrix: integer[][] = [[1,2,3],[4,5,6]];
        """;

        List<String> expected = Arrays.asList(
                "t1 = 1",
                "matrix[0][0] = t1",
                "t1 = 2",
                "matrix[0][1] = t1",
                "t1 = 3",
                "matrix[0][2] = t1",
                "t1 = 4",
                "matrix[1][0] = t1",
                "t1 = 5",
                "matrix[1][1] = t1",
                "t1 = 6",
                "matrix[1][2] = t1"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testMatrixRead() {
        String code = """
        var matrix: integer[][] = [[1,2,3],[4,5,6]];
        print(matrix[0][1]);
        print(matrix[1][2]);
        """;

        List<String> expected = Arrays.asList(
                "t1 = 1",
                "matrix[0][0] = t1",
                "t1 = 2",
                "matrix[0][1] = t1",
                "t1 = 3",
                "matrix[0][2] = t1",
                "t1 = 4",
                "matrix[1][0] = t1",
                "t1 = 5",
                "matrix[1][1] = t1",
                "t1 = 6",
                "matrix[1][2] = t1",

                // print(matrix[0][1])
                "t1 = 0",
                "t2 = matrix[t1]",
                "t1 = 1",
                "t3 = t2[t1]",
                "call print(t3)",

                // print(matrix[1][2])
                "t3 = 1",
                "t1 = matrix[t3]",
                "t3 = 2",
                "t4 = t1[t3]",
                "call print(t4)"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }

    @Test
    void testMatrixWrite() {
        String code = """
        var matrix: integer[][] = [[1,2,3],[4,5,6]];
        matrix[0][1] = 9;
        print(matrix[0][1]);
        """;

        List<String> expected = Arrays.asList(
                "t1 = 1",
                "matrix[0][0] = t1",
                "t1 = 2",
                "matrix[0][1] = t1",
                "t1 = 3",
                "matrix[0][2] = t1",
                "t1 = 4",
                "matrix[1][0] = t1",
                "t1 = 5",
                "matrix[1][1] = t1",
                "t1 = 6",
                "matrix[1][2] = t1",

                // matrix[0][1] = 9
                "t1 = 9",
                "matrix[0][1] = t1",

                // print(matrix[0][1])
                "t1 = 0",
                "t2 = matrix[t1]",
                "t1 = 1",
                "t3 = t2[t1]",
                "call print(t3)"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }
}
