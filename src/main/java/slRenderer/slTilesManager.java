package slRenderer;

import org.joml.Vector2i;

import java.util.Arrays;
import java.util.Random;
import static csc133.spot.*;

public class slTilesManager {
    private float[] verticesArray;
    private int[] vertexIndicesArray;

    private final int vps = 4; // Vertices Per Square
    private final int fpv = 9;  // Vertices Per Vertex
    private final int ips = 6; // Indices Per Square
    public static final int GU = 0; // gold unexposed
    public static final int GE = 1; // gold exposed
    public static final int MU = 2; // mine unexposed
    public static final int ME = 3; // mine exposed

    //umin, vmin, umax, vmax is the order
    private static final float[] GUTC = {0.5f, 0.5f, 1.0f, 0.0f};
    private static final float[] MUTC = GUTC;
    private static final float[] GETC = {0.0f, 1.0f, 0.5f, 0.5f};
    private static final float[] METC = {0.5f, 1.0f, 1.0f, 0.5f};

    private int[] cellStatusArray;
    private int[] cellStats;
    private int boardDisplayStatus = -1;
    private int num_mines;
    float zmin = 0.0f;
    public static int TILE_WIDTH = WIN_WIDTH / NUM_POLY_COLS;
    public static int TILE_HEIGHT = WIN_HEIGHT / NUM_POLY_ROWS;

    public slTilesManager(int total_mines) {
        num_mines = total_mines;
        cellStatusArray = new int[NUM_POLY_COLS * NUM_POLY_ROWS];
        cellStats = new int[] {0, NUM_POLY_COLS * NUM_POLY_ROWS - total_mines, total_mines};

        for (int ii = 0; ii < cellStatusArray.length; ++ii) {
            cellStatusArray[ii] = GU;
        }

        int cur_mines = 0, cur_index = -1;
        Random my_rand = new Random();
        while (cur_mines < num_mines) {
            cur_index = my_rand.nextInt(cellStatusArray.length);
            if (cellStatusArray[cur_index] != MU) {
                cellStatusArray[cur_index] = MU;
                ++cur_mines;
            }
        }
        setVertexArray();
        setVertexIndicesArray();
    }  //  public slGeometryManager(int num_mines)

    // TODO: should call fillSquarecoordinates for each cell array

    private void setVertexArray() {
        // all necessary variables
        int vertexCount = NUM_POLY_ROWS * NUM_POLY_COLS * vps;
        verticesArray = new float[vertexCount * fpv];
        float xmin = POLY_OFFSET, ymax = WIN_HEIGHT - POLY_OFFSET, zmin = 0.0f, zmax = 0.0f, xmax = xmin + POLYGON_LENGTH, ymin = ymax - POLYGON_LENGTH;
        float uvmin = 0.0f;
        float uvmax = 1.0f;
        int index = 0;
        int color_count = 0;
        int uv_count = 0;
        float[] xyz_coords = {xmin,ymin,zmin, xmax,ymin,zmin, xmax,ymax,zmax, xmin,ymax,zmax};
        float[] uv_coords = {uvmin, uvmax, uvmax, uvmax, uvmax, uvmin, uvmin, uvmin};
        float[] vs_colors = {0.0f, 0.0f, 1.0f, 1.0f};

        for (int row = 0; row < NUM_POLY_ROWS; row++) {
            for (int col = 0; col < NUM_POLY_COLS; col++) {
                for (float xyz_coordinate : xyz_coords) {
                    if (color_count == 3) {
                        color_count = 0;
                        for (float color : vs_colors) {
                            verticesArray[index++] = color;
                        }
                        verticesArray[index++] = uv_coords[uv_count++];
                        verticesArray[index++] = uv_coords[uv_count++];
                        if (uv_count >= uv_coords.length) {
                            uv_count = 0;
                        }
                    }
                    verticesArray[index++] = xyz_coordinate;
                    color_count++;
                }
                xmin = xmax + POLY_PADDING;
                xmax = xmin + POLYGON_LENGTH;
            }
            xmin = POLY_OFFSET;
            xmax = xmin + POLYGON_LENGTH;
            ymax = ymin - POLY_PADDING;
            ymin = ymax - POLYGON_LENGTH;
        }
    }  // float[] setVertexArray(...)

    private void fillSquareCoordinates(int indx, int row, int col, float[] vert_array) {
        float xmin = POLY_OFFSET + (POLY_PADDING + POLYGON_LENGTH) * col;
        float ymax = WIN_HEIGHT - POLY_OFFSET - (POLY_PADDING + POLYGON_LENGTH) * row;
        float xmax = xmin + POLYGON_LENGTH;
        float ymin = ymax - POLYGON_LENGTH;
        float zmin = 0.0f; // Use the global Z coordinate for all vertices

        float[] xyzCoords = {
                xmin, ymin, zmin, // Bottom-left
                xmax, ymin, zmin, // Bottom-right
                xmax, ymax, zmin, // Top-right
                xmin, ymax, zmin  // Top-left
        };

        float[] uvCoords = {
                0.0f, 0.0f, // UV for bottom-left
                1.0f, 0.0f, // UV for bottom-right
                1.0f, 1.0f, // UV for top-right
                0.0f, 1.0f  // UV for top-left
        };

        // Populate the vertex array at the index
        for (int i = 0; i < 4; i++) { // 4 vertices per square
            int base = indx + i * fpv;
            vert_array[base] = xyzCoords[i * 3];       // X
            vert_array[base + 1] = xyzCoords[i * 3 + 1]; // Y
            vert_array[base + 2] = xyzCoords[i * 3 + 2]; // Z
            vert_array[base + 3] = uvCoords[i * 2];    // U
            vert_array[base + 4] = uvCoords[i * 2 + 1];  // V
        }
    }

    public void setVertexIndicesArray() {
        vertexIndicesArray = new int[(NUM_POLY_ROWS * NUM_POLY_COLS) * ips];
        int index = 0;
        int vertex_index = 0;

        while (index < vertexIndicesArray.length) {
            vertexIndicesArray[index++] = vertex_index;
            vertexIndicesArray[index++] = vertex_index + 1;
            vertexIndicesArray[index++] = vertex_index + 2;
            vertexIndicesArray[index++] = vertex_index;
            vertexIndicesArray[index++] = vertex_index + 2;
            vertexIndicesArray[index++] = vertex_index + 3;
            vertex_index += vps;
        }
    }  //  public int[] setVertexIndicesArray(...)

    public void updateForPolygonStatusChange(int row, int col, boolean printStats) {
        int fps = vps * fpv; // Floats Per Square
        int tc_offset = 5; // UVs come after XYZ (3 floats)
        int indx = (row * NUM_POLY_COLS + col) * fps + 3; // Start index for UV coordinates of the first vertex

        float[] currentTC;
        int old_state = cellStatusArray[row * NUM_POLY_COLS + col];
        int new_state = -1;

        if (old_state == GU) {
            new_state = GE;
            currentTC = GETC; // Assuming GETC holds correct UVs for GE
            cellStats[0]++;
            cellStats[1]--;
        } else if (old_state == MU) {
            new_state = ME;
            currentTC = METC; // Assuming METC holds correct UVs for ME
        } else {
            return; // No update needed if state doesn't change
        }

        if (printStats && boardDisplayStatus < 0) {
            printStats();
        }

        cellStatusArray[row * NUM_POLY_COLS + col] = new_state;

        // Update UVs for all four vertices
        for (int i = 0; i < 4; i++) {
            verticesArray[indx] = currentTC[0]; // U min
            verticesArray[indx + 1] = currentTC[1]; // V min
            indx += tc_offset;
        }
    }

    private void printStats() {
        // Example implementation
        System.out.println("Current Stats: Exposed Gold: " + cellStats[0] + ", Unexposed Gold: " + cellStats[1]);
    }

    public void setCellStatus(int row, int col, int status) {
        cellStatusArray[row * NUM_POLY_COLS + col] = status;
    }   //  public void setCellStatus(int row, int col, int status)

    public int getCellStatus(int row, int col) {
        return cellStatusArray[row * NUM_POLY_COLS + col];
    }

    public void updateStatusArrayToDisplayAll() {
        for (int i = 0; i < cellStatusArray.length; i++) {
            int state = cellStatusArray[i];
            if (state == GU) {
                cellStatusArray[i] = GE;
            } else if (state == MU) {
                cellStatusArray[i] = ME;
            }
        }
    }

    public void printMineSweeperArray() {
        for (int i = 0; i < cellStatusArray.length; i++) {
            System.out.print(cellStatusArray[i] + " ");
            if ((i + 1) % NUM_POLY_COLS == 0) { // New line after each full row
                System.out.println();
            }
        }
        if (cellStatusArray.length % NUM_POLY_COLS != 0) {
            System.out.println(); // end on new line
        }
    }

    public int[] getCellStats() {
        return Arrays.copyOf(cellStats, cellStats.length); // Return a copy to prevent external modifications
    }

    public static Vector2i getRowColFromXY(float xpos, float ypos) {
        int col = (int) (xpos / TILE_WIDTH);
        int row = (int) (ypos / TILE_HEIGHT);
        if (row >= 0 && row < NUM_POLY_ROWS && col >= 0 && col < NUM_POLY_COLS) {
            return new Vector2i(row, col);
        }
        return new Vector2i(-1, -1);  // No valid cell was clicked
    }

    public float[] getVertices() {
        return verticesArray;
    }

    public int[] getIndices() {
        return vertexIndicesArray;
    }

}  //  public class slTilesManager