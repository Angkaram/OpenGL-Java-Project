package csc133;

import slRenderer.slKeyListener;
import slRenderer.slLevelSceneEditor;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import slRenderer.slMouseListener;

import static csc133.spot.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;

// Added by hand -shankar:
import static org.lwjgl.system.MemoryUtil.*;
import static slUtils.slTime.getTime;


public class slWindow {

    private long glfwWindow;

    private static final float ccRed = 0.0f;
    private static final float ccGreen = 0.0f;
    private static final float ccBlue = 0.0f;
    private static final float ccAlpha = 1.0f;

    private static slWindow my_window = null;
    private slLevelSceneEditor currentScene;


    private slWindow() {

    }

    public static slWindow get() {
        if (slWindow.my_window == null){
            slWindow.my_window = new slWindow();
        }
        return slWindow.my_window;
    }

    public void run() {
        //print_legalese();
        init();
        loop();

        // Clean up:
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        // error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Init GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Could not initialize GLFW");
        }

        // Configure GLFW:
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        //glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        // Create window:
        glfwWindow = glfwCreateWindow(WIN_WIDTH, WIN_HEIGHT, WINDOW_TITLE, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("glfwCreateWindow(...) failed; bailing out!");
        }

        glfwSetCursorPosCallback(glfwWindow, slMouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, slMouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, slMouseListener::mouseScrollCallback);

        glfwSetKeyCallback(glfwWindow, slKeyListener::keyCallback);

        glfwMakeContextCurrent(glfwWindow);
        glfwSwapInterval(1);

        glfwShowWindow(glfwWindow);

        GL.createCapabilities();

        currentScene = new slLevelSceneEditor();
        currentScene.init();
    }

    public void loop() {
        float beginTime = getTime();
        float endTime = getTime();
        float dt = -1.0f;
        while (!glfwWindowShouldClose(glfwWindow)){
            glfwPollEvents();

            glClearColor(ccRed, ccGreen, ccBlue, ccAlpha);
            glClear(GL_COLOR_BUFFER_BIT);
            if (dt >= 0) {
                currentScene.update(dt);
            }

            glfwSwapBuffers(glfwWindow);
            endTime = getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }
}
