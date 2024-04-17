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

    public static int TILE_WIDTH = WIN_WIDTH / NUM_POLY_COLS; // Ensure these are defined and updated correctly elsewhere in your code
    public static int TILE_HEIGHT = WIN_HEIGHT / NUM_POLY_ROWS;

    public slTilesManager(int total_mines) {
        this.num_mines = total_mines;
        this.total_cells = NUM_POLY_COLS * NUM_POLY_ROWS;
        cellStatusArray = new int[total_cells];
        Random rand = new Random();

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
        int index = 0;
        for (int row = 0; row < NUM_POLY_ROWS; row++) {
            for (int col = 0; col < NUM_POLY_COLS; col++) {
                fillSquareCoordinates(index, row, col);
                index += vps * fpv;
            }
        }
    }

    private void fillSquareCoordinates(int index, int row, int col) {
        float x = col * TILE_WIDTH;
        float y = row * TILE_HEIGHT;
        float z = 0.0f;  // Assuming a 2D plane at z=0

        // Define vertices in counter-clockwise order
        // Lower left vertex
        verticesArray[index++] = x;
        verticesArray[index++] = y;
        verticesArray[index++] = z;

        // Lower right vertex
        verticesArray[index++] = x + TILE_WIDTH;
        verticesArray[index++] = y;
        verticesArray[index++] = z;

        // Upper right vertex
        verticesArray[index++] = x + TILE_WIDTH;
        verticesArray[index++] = y + TILE_HEIGHT;
        verticesArray[index++] = z;

        // Upper left vertex
        verticesArray[index++] = x;
        verticesArray[index++] = y + TILE_HEIGHT;
        verticesArray[index] = z;
    }

    private void setVertexIndicesArray() {
        int numTiles = NUM_POLY_COLS * NUM_POLY_ROWS;  // Assuming a grid of tiles
        vertexIndicesArray = new int[numTiles * 6];  // 6 indices per tile (2 triangles)

        for (int tile = 0, offset = 0; tile < numTiles; tile++) {
            int vertexBase = tile * vps;  // vps is the number of vertices per square (4 in this case)

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


    public static Vector2i getRowColFromXY(float xpos, float ypos) {
        int col = (int) (xpos / TILE_WIDTH);
        int row = (int) (ypos / TILE_HEIGHT);
        if (row >= 0 && row < NUM_POLY_ROWS && col >= 0 && col < NUM_POLY_COLS) {
            return new Vector2i(row, col);
        }
        return new Vector2i(-1, -1);  // No valid cell was clicked
    }

    // Method to get the vertices array
    public float[] getVertices() {
        return verticesArray;
    }

    // Method to get the indices array
    public int[] getIndices() {
        return vertexIndicesArray;
    }
}

