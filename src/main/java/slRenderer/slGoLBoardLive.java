package slRenderer;

// derive slGoBoardLive from slGoBoard
public class slGoLBoardLive extends slGoLBoard {

    public slGoLBoardLive(int numRows, int numCols) {
        super(numRows, numCols);
    }

    @Override
    public int countLiveTwoDegreeNeighbors(int row, int col) {
        int my_r = row, my_c = col; // current square col and row
        int prev_r = row - 1, prev_c = col - 1;
        int next_r = row + 1, next_c = col + 1;
        int my_count = 0; // count of neighbors that are alive

        for (int i = prev_r; i <= next_r; i++) {
            for (int j = prev_c; j <= next_c; j++) {
                int new_r = (NUM_ROWS + i) % NUM_ROWS;
                int new_c = (NUM_COLS + j) % NUM_COLS;

                if (!(i == row && j == col)) {
                    if (liveCellArray[new_r][new_c]) {
                        my_count++;
                    }
                }
                else {
                    if (liveCellArray[new_r][new_c]) {
                        my_count++;
                    }
                }
            }
        }
        if (liveCellArray[my_r][my_c]) {
            return (my_count - 1);
        }
        return my_count;
    }

    // return how many live cells are in the updated board
    /*
        Rules:
        1. Live Two Degree Neighbors < 2 --> Kill
        2. Live Two Degree Neighbors == 2 || Live Neighbors == 3 --> Retain
        3. Live Two Degree Neighbors > 3 --> Kill
        4. Dead with Live Two Degree Neighbors == 3 --> Alive again
     */
    public int updateNextCellArray() {
        int retVal = 0;

        for (int row = 0; row < NUM_ROWS; ++row) {
            for (int col = 0; col < NUM_COLS; ++col) {
                boolean ccs = liveCellArray[row][col]; // current cell status
                int nln = countLiveTwoDegreeNeighbors(row, col); // number of live neighbors initialized to initial state

                if (!ccs && nln == 3) {
                    // Rule 4: Dead with 3 live neighbors becomes alive
                    nextCellArray[row][col] = true;
                    ++retVal;
                } else if (ccs && (nln < 2 || nln > 3)) {
                    // Rules 1 and 3: Live cells with fewer than 2 or more than 3 live neighbors die
                    nextCellArray[row][col] = false;
                } else if (ccs && (nln == 2 || nln == 3)) {
                    // Rule 2: Live cells with 2 or 3 live neighbors survive
                    nextCellArray[row][col] = true;
                    ++retVal;
                } else {
                    // in all other cases, keep the current state!
                    nextCellArray[row][col] = ccs;
                }
            }
        }

        // here, we swap liveCellArray and nextCellArray using a temp variable
        boolean[][] tmp = liveCellArray;
        liveCellArray = nextCellArray;
        nextCellArray = tmp;

        return retVal;
    } // updateNextCellArray()
}