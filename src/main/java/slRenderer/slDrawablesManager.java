package slRenderer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static csc133.spot.*;
import static org.lwjgl.opengl.ARBVertexArrayObject.*;
import static org.lwjgl.opengl.GL20.*;

public class slDrawablesManager {

    private final Vector3f my_camera_location = new Vector3f(0, 0, 0.0f);
    private slCamera my_camera;
    private final slTilesManager board_manager = new slTilesManager(NUM_MINES);

    private final float [] vertexArray = board_manager.getVertices();
    private final int[] vertexIndicesArray = board_manager.getIndices();
    private slShaderManager shader_manager;
    private slTextureManager texture_manager;
    private int vaoID, vboID, eboID;
    private final int vpoIndex = 0, vcoIndex = 1, vtoIndex = 2;
    private int positionStride = 3, colorStride = 4, textureStride = 2, vertexStride = (positionStride + colorStride + textureStride) * Float.BYTES;

    public slDrawablesManager(int num_mines) {
        initRendering();
    }

    private void initRendering() {
        my_camera = new slCamera(new Vector3f(my_camera_location));
        my_camera.setOrthoProjection();

        // TODO: uncomment for testing purposes
        //shader = new slShaderManager("vs_0.glsl", "fs_0.glsl");
        shader_manager = new slShaderManager("vs_texture_1.glsl", "fs_texture_1.glsl");
        shader_manager.compile_shader();
        texture_manager = new slTextureManager(System.getProperty("user.dir") + "/assets/shaders/FourTextures.png");

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
        shader_manager.set_shader_program();
        shader_manager.loadMatrix4f("uProjMatrix", my_camera.getProjectionMatrix());
        shader_manager.loadMatrix4f("uViewMatrix", my_camera.getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(vpoIndex);
        glEnableVertexAttribArray(vcoIndex);
        glEnableVertexAttribArray(vtoIndex);

        // TODO: need to change indices once grid works?
        glDrawElements(GL_TRIANGLES, vertexIndicesArray.length, GL_UNSIGNED_INT, 2);

        glDisableVertexAttribArray(vpoIndex);
        glDisableVertexAttribArray(vcoIndex);
        glDisableVertexAttribArray(vtoIndex);
        glBindVertexArray(0);
        shader_manager.detach_shader();
    }

}