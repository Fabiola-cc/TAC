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
                "t2 = 2",
                "matrix[0][1] = t2",
                "t3 = 3",
                "matrix[0][2] = t3",
                "t4 = 4",
                "matrix[1][0] = t4",
                "t5 = 5",
                "matrix[1][1] = t5",
                "t6 = 6",
                "matrix[1][2] = t6"
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
                "t2 = 2",
                "matrix[0][1] = t2",
                "t3 = 3",
                "matrix[0][2] = t3",
                "t4 = 4",
                "matrix[1][0] = t4",
                "t5 = 5",
                "matrix[1][1] = t5",
                "t6 = 6",
                "matrix[1][2] = t6",
                "t7 = 0",
                "t8 = matrix[t7]",
                "t9 = 1",
                "t10 = t8[t9]",
                "t11 = t10",
                "call print(t11)",
                "t12 = 1",
                "t13 = matrix[t12]",
                "t14 = 2",
                "t15 = t13[t14]",
                "t16 = t15",
                "call print(t16)"
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
                "t2 = 2",
                "matrix[0][1] = t2",
                "t3 = 3",
                "matrix[0][2] = t3",
                "t4 = 4",
                "matrix[1][0] = t4",
                "t5 = 5",
                "matrix[1][1] = t5",
                "t6 = 6",
                "matrix[1][2] = t6",
                "t7 = 9",
                "matrix[0][1] = t7",
                "t8 = 0",
                "t9 = matrix[t8]",
                "t10 = 1",
                "t11 = t9[t10]",
                "t12 = t11",
                "call print(t12)"
        );

        assertEquals(expected, testInit.generateTAC(code));
    }
}
