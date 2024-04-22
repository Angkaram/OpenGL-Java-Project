package csc133;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class spot {

    public static int WIN_WIDTH = 900, WIN_HEIGHT = 1300;

    public static String WINDOW_TITLE = "CSC 133";
    public static int NUM_POLY_ROWS = 9, NUM_POLY_COLS = 6;

    public static final float POLY_OFFSET = 40.0f, POLY_PADDING = 40.0f, SQUARE_LENGTH = 100.0f;

    public static final float FRUSTUM_LEFT = 0.0f,   FRUSTUM_RIGHT = (float)WIN_WIDTH,
            FRUSTUM_BOTTOM = 0.0f, FRUSTUM_TOP = (float)WIN_HEIGHT,
            Z_NEAR = 0.0f, Z_FAR = 10.0f;

    public static final Vector3f VEC_RENDER_COLOR =
            new Vector3f(0.0f, 0.498f, 0.0153f);

    public static final float alpha = 200.0f; // Speed of the polygon across the window;
    public static final float POLYGON_LENGTH = 100.0f; // may need to adjust

    public static final int NUM_MINES = 4;
}
