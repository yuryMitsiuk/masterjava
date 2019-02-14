package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) {
        final CompletionService<?> service = new ExecutorCompletionService<>(executor);
        final int matrixSize = matrixA.length;
        final int[][] matrixBT = transform(matrixB);
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final List<Future<?>> futureList = new ArrayList<>();
        for (int i = 0; i < matrixSize; i++) {
            final int rowIndex = i;
            Future<?> future = service.submit(() -> calculateRow(matrixA[rowIndex], matrixBT, matrixC, rowIndex), null);
            futureList.add(future);
        }
        while (!futureList.isEmpty()) {
            futureList.remove(service.poll());
        }

//        try {
//            new ForkJoinPool(8).submit(() -> IntStream.range(0, matrixSize).parallel().forEach((rowIndex) -> calculateRow(matrixA[rowIndex], matrixBT, matrixC, rowIndex))).get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

        return matrixC;
    }

    private static void calculateRow(final int[] row1, final int[][] row2, final int[][] resultMatrix, int rowIndex) {
        for (int i = 0; i < row1.length; i++) {
            int summ = 0;
            for (int j = 0; j < row2.length; j++) {
                summ += row1[j]*row2[i][j];
            }
            resultMatrix[rowIndex][i] = summ;
        }
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        int[] thatColumn = new int[matrixSize];

        try {
            for (int j = 0; ; j++) {
                for (int k = 0; k < matrixSize; k++) {
                    thatColumn[k] = matrixB[k][j];
                }

                for (int i = 0; i < matrixSize; i++) {
                    int thisRow[] = matrixA[i];
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += thisRow[k] * thatColumn[k];
                    }
                    matrixC[i][j] = sum;
                }
            }
        } catch (IndexOutOfBoundsException ignored) { }

//        Remember about cache.
//        Used transparent matrix (matrixB)
//        for (int i = 0; i < matrixB.length; i++) {
//            for (int j =  0; j < matrixB.length; j++) {
//                matrixB[j][i] = matrixB[i][j];
//            }
//        }
//
//        for (int i = 0; i < matrixSize; i++) {
//            for (int j = 0; j < matrixSize; j++) {
//                int sum = 0;
//                for (int k = 0; k < matrixSize; k++) {
//                    sum += matrixA[i][k] * matrixB[j][k];
//                }
//                matrixC[i][j] = sum;
//            }
//        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    System.out.println("matrixA["+i+"]["+j+"] = "+matrixA[i][j]);
                    System.out.println("matrixB["+i+"]["+j+"] = "+matrixB[i][j]);
                    System.out.println();
                    return false;
                }
            }
        }
        return true;
    }

    private static int[][] transform(int[][] matrix) {
        int[][] transformed = new int[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++)
                transformed[j][i] = matrix[i][j];
        }
        return transformed;
    }
}
