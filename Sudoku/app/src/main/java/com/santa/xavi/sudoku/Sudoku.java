package com.santa.xavi.sudoku;

import java.util.*;

public class Sudoku {
    int ROWS = 9;
    int COLS = 9;
    int WIDTH = 3;
    public int[][] start(int[][] S) {
        Node n = new Node(0, 0, 0);
        // Here we make a graph with all the empty spaces
        LinkedList<Node> graph = new LinkedList<Node>();
        graph = makeGraph(S, graph);

        // Here we use DFS to complete the sudoku
        S = solveSudoku(S, graph, 0);
        return S;
    }

    public  LinkedList<Node> makeGraph(int[][] S, LinkedList<Node> G) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (S[i][j] == 0) {
                    Node n = new Node(i, j, 0);
                    G.add(n);
                }
            }
        }
        return G;
    }

    public  boolean checkCol(int[][] S, int row, int col, int num) {
        for (int i = 0; i < ROWS; i++) {
            if (S[i][col] == num && row != i) { // the number already exists in the column
                return false;
            }
        }
        return true; // PASS
    }

    public  boolean checkRow(int[][] S, int row, int col, int num) {
        for (int j = 0; j < COLS; j++) {
            if (S[row][j] == num && col != j) { // the number already exists in the row
                return false;
            }
        }
        return true; // PASS
    }

    public  boolean checkSquare(int[][] S, int row, int col, int num) {
        //Know which square the number is from
        int sq = col / 3 + row / 3 * 3 + 1;
        int startRow = ((sq - 1) / 3) * 3;
        int startCol = ((sq - 1) % 3) * 3;
        for (int i = 0; i < ROWS / WIDTH; i++) {
            for (int j = 0; j < COLS / WIDTH; j++) {
                if (S[startRow + i][startCol + j] == num) {
                    if (row != startRow + i || col != startCol + j) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public  boolean checkIn(int[][] S, int row, int col, int num) {
        boolean a = checkCol(S, row, col, num);
        boolean b = checkRow(S, row, col, num);
        boolean c = checkSquare(S, row, col, num);
        return (a && b && c);
    }

    public  boolean checkSudoku(int[][] S) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (!checkIn(S, i, j, S[i][j])) {

                    System.out.println("Sudoku has no solution");
                    return false;
                }
            }
        }
        System.out.println("Sudoku is correct");
        return true;
    }

    public  int[][] solveSudoku(int[][] S, LinkedList<Node> G, int p) {
        if (p == G.size()) {
            if (checkSudoku(S)) {
                return S;
            }
        }
        Node n = G.get(p);
        n.n++;
        if (n.n > ROWS) {
            n.n = 0;
            S[n.i][n.j] = 0;
            p--;
        } else {
            while (!checkIn(S, n.i, n.j, n.n)) {
                n.n++;
            }
            if (n.n > ROWS) {
                n.n = 0;
                S[n.i][n.j] = 0;
                p--;
            } else {
                S[n.i][n.j] = n.n;
                p++;
            }
        }
        solveSudoku(S, G, p);
        return S;
    }
}
