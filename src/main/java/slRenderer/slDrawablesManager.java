package slRenderer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;
import javax.swing.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static slRenderer.slTilesManager.*;
import static org.lwjgl.opengl.ARBVertexArrayObject.*;
import static org.lwjgl.opengl.GL20.*;

public class slDrawablesManager {

    private final Vector3f my_camera_location = new Vector3f(0, 0, 0.0f);
    private slTilesManager board_manager;
    private slShaderManager shader_manager;

    // Vertex Array Object, Vertex Buffer Object, and Element Buffer Object
    private int vaoId, vboId, eboId;
    private float[] vertices;
    private int[] indices;

    public slDrawablesManager(int num_mines) {
        board_manager = new slTilesManager(num_mines);
        initRendering();
    }

    private void initRendering() {
        // Initialize OpenGL contexts
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Create a float buffer for vertices
        vertices = new float[]{
                // Define your vertices array with positions
        };
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create an integer buffer for indices
        indices = new int[]{
                // Define your indices array for drawing squares
        };
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();

        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        // First, ensure the shader program is set
        shader_manager.set_shader_program(); // Only sets the shader program to be active
        // Now, get the shader program ID for attribute location
        int shaderProgramId = shader_manager.getShaderProgram();
        int posAttr = glGetAttribLocation(shaderProgramId, "aPos");
        glEnableVertexAttribArray(posAttr);
        glVertexAttribPointer(posAttr, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);

        // Unbind VAO
        glBindVertexArray(0);
    }

    public void update(int row, int col) {
        if (row >= 0 && col >= 0) {
            int status = board_manager.getCellStatus(row, col);
            if (status == GE) {
                // Draw the element at (row, col)
                glBindVertexArray(vaoId);
                glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
                glBindVertexArray(0);
            } else {
                board_manager.updateTileStatus(row, col);
                // Redraw only if needed
            }
        }
    }

} // public class slDrawablesManager
