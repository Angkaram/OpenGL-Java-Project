package csc133;

import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import slRenderer.slDrawablesManager;
import slRenderer.slMouseListener;
import slRenderer.slTilesManager;

import javax.swing.*;

import static csc133.spot.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;

// Added by hand -shankar:
import static org.lwjgl.system.MemoryUtil.*;


public class slWindow {

    private long glfwWindow;

    private static final float ccRed = 0.05f;
    private static final float ccGreen = 0.05f;
    private static final float ccBlue = 0.05f;
    private static final float ccAlpha = 1.0f;

    private static slWindow my_window = null;
    private slDrawablesManager minesweeper_drawable;

    private slWindow() {

    }

    public static slWindow get() {
        if (slWindow.my_window == null){
            slWindow.my_window = new slWindow();
        }
        return slWindow.my_window;
    }

    public void run(int num_mines) {
        //print_legalese();
        init(num_mines);
        loop();

        // Clean up:
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init(int num_mines) {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Could not initialize GLFW");
        }

        // Configure GLFW:
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        glfwWindow = glfwCreateWindow(WIN_WIDTH, WIN_HEIGHT, WINDOW_TITLE, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("glfwCreateWindow(...) failed; bailing out!");
        }

        // TODO: implement this
        glfwSetCursorPosCallback(glfwWindow, slMouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, slMouseListener::mouseButtonCallback);

        glfwMakeContextCurrent(glfwWindow);
        glfwSwapInterval(1);

        glfwShowWindow(glfwWindow);

        GL.createCapabilities();
        minesweeper_drawable = new slDrawablesManager(num_mines);
    }

    public void loop() {
        Vector2i rcVec = new Vector2i(-1, -1);  // Row-Column Vec
        while (!glfwWindowShouldClose(glfwWindow)){
            glfwPollEvents();
            rcVec.set(-1, -1);
            // TODO: Implement this
            if (slMouseListener.mouseButtonDown(0)) {
                float xp = slMouseListener.getX();
                float yp = slMouseListener.getY();
                slMouseListener.mouseButtonDownReset(0);
                rcVec = slTilesManager.getRowColFromXY(xp, yp);
            }
            glClearColor(ccRed, ccGreen, ccBlue, ccAlpha);
            glClear(GL_COLOR_BUFFER_BIT);
            minesweeper_drawable.update(rcVec.x, rcVec.y);
            glfwSwapBuffers(glfwWindow);
        }
    }  // public void loop()

}  //  public class slWindow