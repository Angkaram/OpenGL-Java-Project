package slRenderer;

import org.joml.Vector2i;
import javax.swing.*;
import java.util.Random;

import static csc133.spot.*;

public class slTilesManager {
    private float[] verticesArray;
    private int[] vertexIndicesArray;

    private final int vps = 4; // Vertices Per Square
    private final int fpv = 3;  // Floats Per Vertex (X, Y, Z)

    // Constants for tile states
    public static final int GU = 0; // Gold Unexposed
    public static final int GE = 1; // Gold Exposed
    public static final int MU = 2; // Mine Unexposed
    public static final int ME = 3; // Mine Exposed

    private static final float[] GUTC = {0.5f, 0.5f, 1.0f, 0.0f}; // Texture coordinates for gold
    private static final float[] MUTC = GUTC;
    private static final float[] GETC = {0.0f, 1.0f, 0.5f, 0.5f};
    private static final float[] METC = {0.5f, 1.0f, 1.0f, 0.5f};

    private int[] cellStatusArray;
    private int num_mines;
    private int total_cells;
    private static int TILE_WIDTH = WIN_WIDTH/NUM_POLY_COLS;
    private static int TILE_HEIGHT = WIN_HEIGHT/NUM_POLY_ROWS;

    public slTilesManager(int total_mines) {
        this.num_mines = total_mines;
        this.total_cells = NUM_POLY_COLS * NUM_POLY_ROWS;
        cellStatusArray = new int[total_cells];
        Random rand = new Random();

        // Initialize all cells as gold unexposed
        for (int i = 0; i < total_cells; i++) {
            cellStatusArray[i] = GU;
        }

        // Randomly assign mines
        int minesPlaced = 0;
        while (minesPlaced < num_mines) {
            int idx = rand.nextInt(total_cells);
            if (cellStatusArray[idx] == GU) {
                cellStatusArray[idx] = MU;
                minesPlaced++;
            }
        }

        setVertexArray();
        setVertexIndicesArray();
    }

    private void setVertexArray() {
        verticesArray = new float[total_cells * vps * fpv];
        for (int row = 0; row < NUM_POLY_ROWS; row++) {
            for (int col = 0; col < NUM_POLY_COLS; col++) {
                int index = (row * NUM_POLY_COLS + col) * vps * fpv;
                fillSquareCoordinates(index, row, col);
            }
        }
    }

    private void fillSquareCoordinates(int index, int row, int col) {
        float size = 1.0f;  // Size of each tile
        float x = col * size;
        float y = row * size;
        float z = 0.0f; // Z coordinate is constant as this is a 2D view

        // Lower left vertex
        verticesArray[index++] = x;
        verticesArray[index++] = y;
        verticesArray[index] = z;

        // Lower right vertex
        verticesArray[index++] = x + size;
        verticesArray[index++] = y;
        verticesArray[index] = z;

        // Upper right vertex
        verticesArray[index++] = x + size;
        verticesArray[index++] = y + size;
        verticesArray[index] = z;

        // Upper left vertex
        verticesArray[index++] = x;
        verticesArray[index++] = y + size;
        verticesArray[index] = z;
    }

    public void setVertexIndicesArray() {
        vertexIndicesArray = new int[total_cells * 6]; // 6 indices per tile (2 triangles per square)
        int offset = 0;
        for (int i = 0; i < total_cells; i++) {
            int vertexBase = i * vps;
            // Triangle 1
            vertexIndicesArray[offset++] = vertexBase;
            vertexIndicesArray[offset++] = vertexBase + 1;
            vertexIndicesArray[offset++] = vertexBase + 2;
            // Triangle 2
            vertexIndicesArray[offset++] = vertexBase;
            vertexIndicesArray[offset++] = vertexBase + 2;
            vertexIndicesArray[offset++] = vertexBase + 3;
        }
    }

    public int getCellStatus(int row, int col) {
        return cellStatusArray[row * NUM_POLY_COLS + col];
    }

    // Additional methods (updateForPolygonStatusChange, printStats, setCellStatus) would go here

    public static Vector2i getRowColFromXY(float xpos, float ypos) {
        // Implementation based on your specific window mapping
        int row = (int) (ypos / TILE_HEIGHT);
        int col = (int) (xpos / TILE_WIDTH);
        if (row >= 0 && row < NUM_POLY_ROWS && col >= 0 && col < NUM_POLY_COLS) {
            return new Vector2i(row, col);
        }
        return new Vector2i(-1, -1); // Indicates no valid cell was clicked
    }

    public void updateTileStatus(int row, int col) {
        int index = row * NUM_POLY_COLS + col;
        int currentStatus = cellStatusArray[index];
        if (currentStatus == GU) {
            cellStatusArray[index] = GE;  // Example: change from unexposed gold to exposed gold
        } else if (currentStatus == MU) {
            cellStatusArray[index] = ME;  // Example: change from unexposed mine to exposed mine
        }
        // Add additional logic as required for your game's rules
    }
}
