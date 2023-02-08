package implementations;

import java.util.ArrayDeque;
import java.util.Arrays;

public class Paint {
    private static class Cell {
        private int row, col;

        private Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    private final char[][] drawingBoard;

    public Paint(char[][] board) {
        this.drawingBoard = board;
    }

    public Paint(int height, int width, char color) {
        this.drawingBoard = createDrawingBoard(height, width, color);
    }

    public void fill(int row, int col, char color) {
        if (isInBound(row, col)) {
            char oldChar = this.drawingBoard[row][col];

            ArrayDeque<Cell> toVisit = new ArrayDeque<>();
            toVisit.push(new Cell(row, col));

            while (!toVisit.isEmpty()) {
                Cell currentCell = toVisit.pop();
                row = currentCell.row;
                col = currentCell.col;
                this.drawingBoard[row][col] = color;

                if (canFill(row - 1, col, oldChar)) {
                    toVisit.push(new Cell(row - 1, col));
                }

                if (canFill(row + 1, col, oldChar)) {
                    toVisit.push(new Cell(row + 1, col));
                }

                if (canFill(row, col - 1, oldChar)) {
                    toVisit.push(new Cell(row, col - 1));
                }

                if (canFill(row, col + 1, oldChar)) {
                    toVisit.push(new Cell(row, col + 1));
                }
            }
        }
    }

    private boolean isInBound(int row, int col) {
        return 0 <= row && row < this.drawingBoard.length
                && 0 <= col && col < this.drawingBoard[row].length;
    }

    private boolean canFill(int row, int col, char wantedColor) {
        return isInBound(row, col) && wantedColor == this.drawingBoard[row][col];
    }

    public void printBoard() {
        StringBuilder buffer = new StringBuilder();

        for (char[] row : this.drawingBoard) {
            for (char cell : row) {
                buffer.append(cell);
            }
            buffer.append(System.lineSeparator());
        }

        System.out.println(buffer.toString().trim());
    }

    private char[][] createDrawingBoard(int height, int width, char color) {
        char[][] board = new char[height][width];

        for (char[] row : board) {
            Arrays.fill(row, color);
        }
        return board;
    }
}