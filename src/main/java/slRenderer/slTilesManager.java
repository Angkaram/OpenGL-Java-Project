package slRenderer;

import org.joml.Vector2i;
import java.util.Random;
import static csc133.spot.*;

public class slTilesManager {
    public static int vps = 4, ips = 6, fpv = 9;
    private float[] verticesArray;
    private int[] vertexIndicesArray;
    int fps = vps * fpv;
    int offset1 = 7; // here is the first offset
    int nxt_offsets = 8;

    public static final int GU = 0; // gold unexposed
    public static final int GE = 1; // gold exposed
    public static final int MU = 2; // mine unexposed
    public static final int ME = 3; // mine exposed

    // choose texture from grid of four (umin, vmin, umax, vmax)
    private static final float[] GUTC = {0.5f, 0.5f, 1.0f, 0.0f};
    private static final float[] MUTC = GUTC;
    private static final float[] GETC = {0.0f, 1.0f, 0.5f, 0.5f};
    private static final float[] METC = {0.0f, 0.5f, 0.5f, 0.0f}; // {0.5f, 1.0f, 1.0f, 0.5f}; for explosion

    private int[] cellStatusArray;
    private static int GE_again = 0; // gold exposed
    public static int NUM_GOLD = NUM_POLY_COLS * NUM_POLY_ROWS - NUM_MINES;
    private static int GU_again = NUM_GOLD;
    private final int[] cellOutputStats;
    private final int num_mines; // different use that NUM_MINES
    float z_min_coord = 0.0f;
    private int boardStatus = -1;

    public slTilesManager(int total_mines) {
        num_mines = total_mines;
        cellStatusArray = new int[NUM_POLY_COLS * NUM_POLY_ROWS];
        cellOutputStats = new int[] {0, NUM_POLY_COLS * NUM_POLY_ROWS - total_mines, total_mines};

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
    }  //  public slGeometryManager

    private void setVertexArray() {
        verticesArray = new float[(NUM_POLY_ROWS * NUM_POLY_COLS) * vps * fpv];
        int index = 0;
        for (int row = 0; row < NUM_POLY_ROWS; row++) {
            for (int col = 0; col < NUM_POLY_COLS; col++) {
                fillSquareCoordinates(index, row, col, verticesArray);
                index += (vps * fpv);
            }
        }

    }  // float[] setVertexArray

    private void fillSquareCoordinates(int index, int row, int col, float[] vertex_array) {
        int color_count = 0;
        int uv_count = 0; // increment depending

        float xmin = POLY_OFFSET + col * (POLYGON_LENGTH + POLY_PADDING);
        float ymax = WIN_HEIGHT - (POLY_OFFSET + row * (POLYGON_LENGTH + POLY_PADDING));
        float xmax = xmin + POLYGON_LENGTH, ymin = ymax - POLYGON_LENGTH;

        float[] vs_colors = {0.0f, 0.0f, 1.0f, 1.0f};
        float umin = GUTC[0], vmin = GUTC[1], umax = GUTC[2], vmax = GUTC[3];
        float[] uv_coords = {umin,vmin, umax,vmin, umax,vmax, umin,vmax};
        float[] xyz_coords = {xmin,ymin, z_min_coord, xmax,ymin, z_min_coord, xmax,ymax, z_min_coord, xmin,ymax, z_min_coord};

        for (float xyz_coordinate : xyz_coords) {
            vertex_array[index++] = xyz_coordinate;
            color_count++;
            if (color_count == 3) {
                color_count = 0;
                for (float color : vs_colors) {
                    vertex_array[index++] = color;
                }
                vertex_array[index++] = uv_coords[uv_count++];
                vertex_array[index++] = uv_coords[uv_count++];
            }
        }
    }

    public void setVertexIndicesArray() {
        vertexIndicesArray = new int[(NUM_POLY_ROWS * NUM_POLY_COLS) * ips];
        int index = 0;
        int v_index = 0;

        while (index < vertexIndicesArray.length) {
            vertexIndicesArray[index++] = v_index;
            vertexIndicesArray[index++] = v_index + 1;
            vertexIndicesArray[index++] = v_index + 2;
            vertexIndicesArray[index++] = v_index;
            vertexIndicesArray[index++] = v_index + 2;
            vertexIndicesArray[index++] = v_index + 3;

            v_index += vps;
        }
    }

    public void updateForPolygonStatusChange(int row, int col, boolean printStats) {
        int i = (row * NUM_POLY_COLS + col) * fps + offset1;
        float umin = GETC[0], vmin = GETC[1], umax = GETC[2], vmax = GETC[3];
        int prior_state = cellStatusArray[row * NUM_POLY_COLS + col];
        int next_state = -1;

        if (prior_state == GU ) { // gold hit
            GE_again++;
            GU_again--;
            next_state = GE;
            ++cellOutputStats[0];
            --cellOutputStats[1];

            if (printStats && boardStatus < 0) {
                printStats();
            }
            // win if no mine hit
            if (cellOutputStats[1] == 0) {
                boardStatus = 2;
                System.out.println("You win. This time...");
            }
        }
        else if (prior_state == MU) {  // mine clicked
            next_state = ME;
            boardStatus = 1;
            System.out.println("Game over. Say hello to Mari-bro");
        }
        else {
            return;
        }

        if (next_state == ME) {
            umin = METC[0]; umax = METC[1]; vmin = METC[2]; vmax = METC[3];
            updateStatusArrayToDisplayAll();
        }
        cellStatusArray[row * NUM_POLY_COLS + col] = next_state;

        verticesArray[i++] = umin;
        verticesArray[i] = vmin;
        i += nxt_offsets;
        verticesArray[i++] = umax;
        verticesArray[i] = vmin;
        i += nxt_offsets;
        verticesArray[i++] = umax;
        verticesArray[i] = vmax;
        i += nxt_offsets;
        verticesArray[i++] = umin;
        verticesArray[i] = vmax;
    }

    private void printStats() {
        System.out.println(cellOutputStats[0] + " " + cellOutputStats[1] + " " + cellOutputStats[2]);
    }

    // the only options are GE, ME, GU, MU
    public void setCellStatus(int row, int col, int status) {
        cellStatusArray[row * NUM_POLY_COLS + col] = status;
    }

    public int getCellStatus(int row, int col) {
        return cellStatusArray[row * NUM_POLY_COLS + col];
    }

    private int changeSquareTexture(int index, float umin, float vmin, float umax, float vmax) {
        int xyz_color_offset = 7;
        float[] uv_coords = {umin, vmin, umax, vmin, umax, vmax, umin, vmax};
        int uv_index = 0;
        for (int j = 0; j < vps; j++) {
            verticesArray[index++] = uv_coords[uv_index++];
            verticesArray[index++] = uv_coords[uv_index++];
            index += xyz_color_offset;
        }
        return index;
    }

    public void updateStatusArrayToDisplayAll() {
        int vertex_i = 7;
        for (int i = 0; i < cellStatusArray.length; i++) {
            if (cellStatusArray[i] == GU) {
                cellStatusArray[i] = GE;
                vertex_i = changeSquareTexture(vertex_i, GETC[0], GETC[1], GETC[2], GETC[3]);
            } else if (cellStatusArray[i] == MU) {
                cellStatusArray[i] = ME;
                vertex_i = changeSquareTexture(vertex_i, METC[0], METC[1], METC[2], METC[3]);
            } else {
                for (int j = 0; j < vps; j++) {
                    vertex_i += fpv;
                }
            }
        }
    }

    public void printBoard() {
        int row_count = 0;

        for (int cell : cellStatusArray) {
            if (row_count >= NUM_POLY_COLS) {
                row_count = 0;
                System.out.println();
            }
            System.out.print(cell);
            row_count++;
        }
        System.out.println("\n"); // for readability
    }

    public int[] getCellStats() {
        return cellOutputStats;
    }

    public static Vector2i getRowColFromXY(float xpos, float ypos) {
        Vector2i retVec = new Vector2i(-1, -1);
        int col = (int) ((xpos - POLY_OFFSET) / (POLYGON_LENGTH + POLY_PADDING));
        int row = (int) ((ypos - POLY_OFFSET) / (POLYGON_LENGTH + POLY_PADDING));
        float xmin = POLY_OFFSET + col * (POLYGON_LENGTH + POLY_PADDING);
        float xmax = xmin + POLYGON_LENGTH;
        float ymin = POLY_OFFSET + row * (POLYGON_LENGTH + POLY_PADDING);
        float ymax = ymin + POLYGON_LENGTH;
        if (xpos >= xmin && xpos <= xmax && ypos >= ymin && ypos <= ymax) {
            return retVec.set(row, col);
        } else {
            return retVec;
        }
    }

    public float[] getVertices() {
        return verticesArray;
    }

    public int[] getIndices() {
        return vertexIndicesArray;
    }
}  //  public class slTilesManager