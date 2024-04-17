package csc133;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class spot {

    public static long my_oglwindow = 0;
    public static int WIN_WIDTH = 1000, WIN_HEIGHT = 1000;
    public static int WIN_POS_X = 50, WIN_POS_Y = 150;
    public static String WINDOW_TITLE = "CSC 133";
    public static final int OGL_MATRIX_SIZE = 16;
    public static final int vertices_per_square = 4, floats_per_vertices = 2;
    public static int NUM_POLY_ROWS = 9, NUM_POLY_COLS = 6;

    public static final float POLY_OFFSET = 40.0f, POLY_PADDING = 40.0f, SQUARE_LENGTH = 100.0f;

    public static final float FRUSTUM_LEFT = 0.0f,   FRUSTUM_RIGHT = (float)WIN_WIDTH,
            FRUSTUM_BOTTOM = 0.0f, FRUSTUM_TOP = (float)WIN_HEIGHT,
            Z_NEAR = 0.0f, Z_FAR = 10.0f;

    public static final Vector3f VEC_RENDER_COLOR =
            new Vector3f(0.0f, 0.498f, 0.0153f);

    public static final Vector4f liveColor = new Vector4f(1.0f, 1.0f, 0.0f, 1.0f);
    public static final Vector4f deadColor = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);

    public static final float defaultSL = 0.0f, defaultSR = WIN_WIDTH,
            defaultSB = 0.0f, defaultST = WIN_HEIGHT;
    public static final float defaultZNear = 0.0f, defaultZFar = 10.0f;

    public static int shader_program = 0;
    public static int indices_per_square = 6;
    public static int verts_per_square = 4;

    public static final float alpha = 200.0f; // Speed of the polygon across the window;
    public static final float POLYGON_LENGTH = 100.0f; // may need to adjust

    public static final int NUM_MINES = 4;
}
