package slRenderer;

import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL20.*;
import java.util.Arrays;
import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;

public class slDrawablesManager {
    private slTilesManager board_manager;
    private slShaderManager shader_manager;
    private slTextureManager texture_manager;

    private int vaoId, vboId, eboId;

    public slDrawablesManager(int num_mines, String vertexShaderPath, String fragmentShaderPath, String texturePath) {
        this.board_manager = new slTilesManager(num_mines);
        this.shader_manager = new slShaderManager(vertexShaderPath, fragmentShaderPath);
        this.texture_manager = new slTextureManager(texturePath);

        shader_manager.compile_shader();
        initRendering();
    }

    private void initRendering() {
        shader_manager.set_shader_program();

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        setupVertexBuffers();
        setupElementBuffers();

        glBindVertexArray(0); // Unbind VAO at the end of setup
    }

    private void setupVertexBuffers() {
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(board_manager.getVertices().length);
        vertexBuffer.put(board_manager.getVertices()).flip();

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Assuming position only; adjust as needed for additional vertex attributes
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);
        System.out.println(Arrays.toString(board_manager.getVertices())); // Check if vertices are as expected
    }

    private void setupElementBuffers() {
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(board_manager.getIndices().length);
        indexBuffer.put(board_manager.getIndices()).flip();

        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        System.out.println(Arrays.toString(board_manager.getIndices())); // Check if indices are as expected
    }




    public void update(int row, int col) {
        shader_manager.set_shader_program();  // Ensure shader program is active
        glBindVertexArray(vaoId);  // Bind VAO

        // Check if any actual geometry should be drawn
        if (board_manager.getIndices().length > 0) {
            glDrawElements(GL_TRIANGLES, board_manager.getIndices().length, GL_UNSIGNED_INT, 0);
        } else {
            System.out.println("No indices to draw.");
        }

        glBindVertexArray(0);  // Unbind VAO
    }

}
