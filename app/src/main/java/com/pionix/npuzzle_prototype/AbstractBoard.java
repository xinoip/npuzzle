package com.pionix.npuzzle_prototype;



import java.io.*;
import java.util.Random;
import java.util.Scanner;

public abstract class AbstractBoard {
    protected static int _numberOfBoards;
    protected int _numberOfMoves;
    protected char _lastMove;
    protected int _rowSize, _colSize;

    public abstract int[] toOneDimension();

    public abstract int cell(int row, int col);

    public abstract void setCell(int row, int col, int val);

    public abstract void resize(int row, int col);

    public void setSize(int row, int col) {
        resize(row, col);
        reset();
    }

    public int getRowSize() {
        return _rowSize;
    }

    public int getColSize() {
        return _colSize;
    }

    public void print() {
        System.out.printf(this.toString());
    }

    @Override
    public String toString() {
        final int EMPTY = -1;
        final int BLOCK = -2;

        String rtr = "";

        int currentTile = 0;
        for (int i = 0; i < _rowSize; i++) {
            for (int j = 0; j < _colSize; j++) {
                currentTile = cell(i, j);

                if (currentTile == EMPTY) {
                    rtr += "__";

                } else if (currentTile == BLOCK) {
                    rtr += "WW";

                } else {
                    rtr += currentTile;

                }

                // Check number of digits for alignment of board
                if (currentTile / 10 <= 0 && currentTile != EMPTY && currentTile != BLOCK) {
                    rtr += "  ";

                } else {
                    rtr += " ";

                }

            }
            rtr += "\n";

        }
        return rtr;
    }

    public void reset() {
        final int EMPTY = -1;
        final int BLOCK = -2;
        int currCorrect = 1;

        for (int i = 0; i < _rowSize; i++) {
            for (int j = 0; j < _colSize; j++) {
                if (cell(i, j) != BLOCK) {
                    setCell(i, j, currCorrect);
                    currCorrect++;
                }

            }

        }
        setCell(_rowSize - 1, _colSize - 1, EMPTY);

        _numberOfMoves = 0;
        _lastMove = 'S';
    }

    public boolean move(char move) {
        final int EMPTY = -1;
        final int BLOCK = -2;
        boolean isMoved = false;

        int emptyTileRow = 0, emptyTileCol = 0;
        boolean foundEmpty = false;
        for (int i = 0; i < _rowSize; i++) {
            for (int j = 0; j < _colSize; j++) {
                if (cell(i, j) == EMPTY) {
                    emptyTileRow = i;
                    emptyTileCol = j;
                    foundEmpty = true;
                    break;
                }
            }
            if (foundEmpty)
                break;
        }

        if (!foundEmpty) {
            System.out.println("There are no empty tile to move!");
            return isMoved;
        }

        int newRow = emptyTileRow, newCol = emptyTileCol;
        switch (move) {
            case 'L':
                if (cell(newRow, newCol - 1) != BLOCK && emptyTileCol != 0) {
                    newCol--;
                    isMoved = true;
                }
                break;
            case 'R':
                if (cell(newRow, newCol + 1) != BLOCK && emptyTileCol != _colSize - 1) {
                    newCol++;
                    isMoved = true;
                }
                break;

            case 'U':
                if (cell(newRow - 1, newCol) != BLOCK && emptyTileRow != 0) {
                    newRow--;
                    isMoved = true;
                }
                break;

            case 'D':
                if (cell(newRow + 1, newCol) != BLOCK && emptyTileRow != _rowSize - 1) {
                    newRow++;
                    isMoved = true;
                }
                break;
            default:
                break;
        }
        if (isMoved) {
            int tmp = cell(emptyTileRow, emptyTileCol);
            setCell(emptyTileRow, emptyTileCol, cell(newRow, newCol));
            setCell(newRow, newCol, tmp);
            emptyTileRow = newRow;
            emptyTileCol = newCol;
            _numberOfMoves++;
            _lastMove = move;
        }

        return isMoved;
    }

    public boolean isSolved() {
        final int EMPTY = -1;
        final int BLOCK = -2;
        int currCorrect = 1;
        for (int i = 0; i < _rowSize; i++) {
            for (int j = 0; j < _colSize; j++) {
                if (cell(i, j) != BLOCK) {
                    if (cell(i, j) != EMPTY && cell(i, j) != currCorrect) {
                        return false;

                    }
                    currCorrect++;

                }

            }

        }
        return true;
    }

    public boolean Equals(AbstractBoard board) {
        final int otherRowSize = board._rowSize;
        final int otherColSize = board._colSize;

        if (otherColSize == _colSize && otherRowSize == _rowSize) {
            for (int i = 0; i < _rowSize; i++) {
                for (int j = 0; j < _colSize; j++) {
                    if (board.cell(i, j) != cell(i, j)) {
                        return false;

                    }

                }

            }

        } else {
            return false;

        }

        return true;
    }

    public void shuffle(int n) {
        Random rand = new Random();
        int random = 0;
        for(int i = 0; i < n; i++) {
//            random = (int) Math.random() * 4;
            random = rand.nextInt() % 4;
            switch (random) {
                case 0:
                    move('U');
                    break;
                case 1:
                    move('D');
                    break;
                case 2:
                    move('L');
                    break;
                case 3:
                    move('R');
                    break;
            }
        }
    }

    public int NumberOfBoards() {
        return _numberOfBoards;
    }

    public char lastMove() {
        return _lastMove;
    }

    public int numberOfMoves() {
        return _numberOfMoves;
    }
}
