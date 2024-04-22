package slRenderer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static slRenderer.slTilesManager.*;
import static csc133.spot.*;
import static org.lwjgl.opengl.ARBVertexArrayObject.*;
import static org.lwjgl.opengl.GL20.*;

public class slDrawablesManager {

    private final Vector3f my_camera_location = new Vector3f(0, 0, 0.0f);
    private slCamera my_camera;
    private final slTilesManager board_manager = new slTilesManager(NUM_MINES);
    private final float [] vertexArray = board_manager.getVertices();
    private final int[] vertexIndicesArray = board_manager.getIndices();
    private slShaderManager shaderManager;
    private slTextureManager textureManager;
    private int vaoID, vboID, eboID;
    private final int vpoIndex = 0, vcoIndex = 1, vtoIndex = 2;
    private final int positionStride = 3;
    private final int colorStride = 4;
    private final int textureStride = 2;
    private final int vertexStride = (positionStride + colorStride + textureStride) * Float.BYTES;

    public slDrawablesManager(int num_mines) {
        initRendering();
    }

    private void initRendering() {
        board_manager.printBoard(); // print board to terminal
        my_camera = new slCamera(new Vector3f(my_camera_location));
        my_camera.setOrthoProjection();

        shaderManager = new slShaderManager("vs_texture_1.glsl", "fs_texture_1.glsl");
        //shader = new slShaderManager("vs_0.glsl", "fs_0.glsl"); // for testing
        shaderManager.compile_shader();

        textureManager = new slTextureManager(System.getProperty("user.dir") + "/assets/shaders/FourTextures.png");

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(vertexIndicesArray.length);
        elementBuffer.put(vertexIndicesArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(vpoIndex, positionStride, GL_FLOAT, false, vertexStride, 0);
        glEnableVertexAttribArray(vpoIndex);

        glVertexAttribPointer(vcoIndex, colorStride, GL_FLOAT, false, vertexStride, positionStride * Float.BYTES);
        glEnableVertexAttribArray(vcoIndex);

        glVertexAttribPointer(vtoIndex, textureStride, GL_FLOAT, false, vertexStride, (colorStride + positionStride) * Float.BYTES);
        glEnableVertexAttribArray(vtoIndex);
    }  // void initRendering()

    public void update(int row, int col) {
        shaderManager.set_shader_program(); // set then bind
        textureManager.bind_texture();
        shaderManager.loadMatrix4f("uProjMatrix", my_camera.getProjectionMatrix());
        shaderManager.loadMatrix4f("uViewMatrix", my_camera.getViewMatrix());
        glBindVertexArray(vaoID);
        if (row != -1 && col != -1 && board_manager.getCellStatus(row, col) != GE) {
            if (board_manager.getCellStatus(row, col) == GU || board_manager.getCellStatus(row, col) == MU) {
                board_manager.updateForPolygonStatusChange(row, col, true);
                glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_DYNAMIC_DRAW); // has to be GL_DYNAMIC_DRAW
            }
        }

        glEnableVertexAttribArray(vpoIndex);
        glEnableVertexAttribArray(vcoIndex);
        glEnableVertexAttribArray(vtoIndex);

        glDrawElements(GL_TRIANGLES, vertexIndicesArray.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(vpoIndex);
        glDisableVertexAttribArray(vcoIndex);
        glDisableVertexAttribArray(vtoIndex);

        glBindVertexArray(0);
        shaderManager.detach_shader();
        textureManager.unbind_texture();
    }  //  public void update
}