package slRenderer;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

abstract class slGoLBoard {
    protected int NUM_ROWS;
    protected int NUM_COLS;

    protected boolean[][] cellArrayA;
    protected boolean[][] cellArrayB;
    protected static boolean[][] liveCellArray;
    protected boolean[][] nextCellArray;

    protected slGoLBoard(int numRows, int numCols) {
        NUM_ROWS = numRows;
        NUM_COLS = numCols;
        cellArrayA = new boolean[NUM_ROWS][NUM_COLS];
        cellArrayB = new boolean[NUM_ROWS][NUM_COLS];

        Random myRandom = new Random();
        for (int row = 0; row < NUM_ROWS; ++row) {
            for (int col = 0; col < NUM_COLS; ++col) {
                cellArrayA[row][col] = myRandom.nextBoolean();
                cellArrayB[row][col] = myRandom.nextBoolean();
            }
        }
        liveCellArray = cellArrayA;
        nextCellArray = cellArrayB;
    }  //  public slGoLBoard(int numRows, int numCols)

    // Create a Board with a given number of cells alive - the alive cells
    // are placed randomly placed applying Durstenfeld-Knuth random shuffling
    private slGoLBoard(int numRows, int numCols, int numAlive) {
        NUM_ROWS = numRows;
        NUM_COLS = numCols;
        boolean[] tmpArray = new boolean[NUM_ROWS * NUM_COLS];
        for (int i = 0; i < numAlive; ++i) {
            tmpArray[i] = true;
        }
        for (int i = numAlive; i < NUM_ROWS * NUM_COLS; ++i) {
            tmpArray[i] = false;
        }
        Random myRandom = new Random();
        // Durstenfeld-Knuth random shuffle:
        for (int i = 0; i < NUM_ROWS * NUM_COLS - 2; ++i) {
            int j = myRandom.nextInt(i, NUM_ROWS * NUM_COLS);
            boolean tmp = tmpArray[i];
            tmpArray[i] = tmpArray[j];
            tmpArray[j] = tmp;
        }

        cellArrayA = new boolean[NUM_ROWS][NUM_COLS];
        cellArrayB = new boolean[NUM_ROWS][NUM_COLS];
        int i = 0;
        for (int row = 0; row < NUM_ROWS; ++row) {
            for (int col = 0; col < NUM_COLS; ++col) {
                cellArrayA[row][col] = tmpArray[i];
                ++i;
                cellArrayB[row][col] = false;
            }
        }
        liveCellArray = cellArrayA;
        nextCellArray = cellArrayB;
    }  //  public slGoLBoard(int numRows, int numCols, int numAlive)

    protected boolean[][] getLiveCellArray() {
        return liveCellArray;
    }

    private boolean[][] getNextCellArray() {
        return nextCellArray;
    }

    private void setCellAlive(int row, int col) {
        liveCellArray[row][col] = true;
    }

    private void setCellDead(int row, int col) {
        liveCellArray[row][col] = false;
    }

    private void setAllCells(boolean value) {
        for (boolean[] rows : liveCellArray) {
            for (boolean cell : rows) {
                cell = value;
            }
        }
    }  //  void setAllCells()

    private void copyLiveToNext() {
        for (int row = 0; row < nextCellArray.length; ++row) {
            for (int col = 0; col < nextCellArray[row].length; ++col) {
                liveCellArray[row][col] = nextCellArray[row][col];
            }
        }
        return;
    }  //  void copyLiveToNext()

    protected void printGoLBoard() {
        for (boolean[] my_row : liveCellArray) {
            for (boolean my_val : my_row) {
                if (my_val == true) {
                    System.out.print(1 + " ");
                } else {
                    System.out.print(0 + " ");
                }
            }  //  for (bool my_val : my_row)
            System.out.println();
        }  //  for (bool[] my_row : my_array)
    }  //  void printGoLBoard()

    protected void saveStatusToFile(String fileName) {
        try {
            if (!fileName.endsWith(".ca")) {
                fileName += ".ca";
            }
            FileWriter writer = new FileWriter(fileName);
            for (boolean[] row : liveCellArray) {
                for (boolean cell : row) {
                    writer.write(cell ? '1' : '0');
                }
                writer.write('\n');
            }
            writer.close();
            System.out.println("Status saved to " + fileName);
        } catch (IOException e) {
            System.out.println("An error occurred while saving the status to the file: " + e.getMessage());
        }
    }

    protected void loadStatusFromFile(String fileName) {
        try (Scanner scanner = new Scanner(new File(fileName))) {
            // Read the cell states from the file
            List<boolean[]> rows = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim(); //  remove leading/trailing whitespace
                if (!line.isEmpty()) {
                    // Parse the line to extract boolean values for cell states
                    boolean[] cells = new boolean[line.length()];
                    for (int col = 0; col < line.length(); ++col) {
                        cells[col] = line.charAt(col) == '1'; // '1' represents live cell and '0' represents dead cell
                    }
                    rows.add(cells);
                }
            }

            // get the dimensions of the board
            int numRows = rows.size();
            int numCols = (numRows > 0) ? rows.get(0).length : 0;

            // put the initial state in new cell array with the dimensions read from the file
            boolean[][] loadedCellArray = new boolean[numRows][numCols];

            // fill the cell array with the loaded cell states
            for (int row = 0; row < numRows; ++row) {
                boolean[] cells = rows.get(row);
                for (int col = 0; col < numCols; ++col) {
                    loadedCellArray[row][col] = cells[col];
                }
            }
            liveCellArray = loadedCellArray;
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName);
        }
    }


        // call these abstract so that we must implement it in the derived class (subclass)
    abstract int countLiveTwoDegreeNeighbors(int row, int col);
    abstract int updateNextCellArray();

}  //  public class slGoLBoard

 