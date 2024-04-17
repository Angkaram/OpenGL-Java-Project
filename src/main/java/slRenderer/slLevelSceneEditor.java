package slRenderer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static csc133.spot.alpha;
import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL20.*;
import static csc133.spot.*;

public class slLevelSceneEditor {

    private final Vector3f my_camera_loc = new Vector3f(0, 0, 0.0f);
    private slShaderManager testShader;
    private slTextureManager testTexture;

    private float xmin = POLY_OFFSET, ymin = POLY_OFFSET, zmin = 0.0f, xmax = xmin+ SQUARE_LENGTH,
            ymax = ymin+ SQUARE_LENGTH, zmax = 0.0f;

    private final float uvmin = 0.0f, uvmax = 1.0f;
    // TODO: Add the vertices here: you need to add the UV Coordinates for the textures here -
    // colors will be discarded by the fragment texture with the texels. Nevertheless, we do send
    // them in.
    private final float[] vertexArray = {
            // Position          // Color            // Texture
            xmin, ymin, zmax,    1.0f, 1.0f, 0.0f, 1.0f,  uvmin, uvmax, // bottom left
            xmax, ymin, zmax,    1.0f, 1.0f, 0.0f, 1.0f,  uvmax, uvmax, // bottom right
            xmax, ymax, zmin,    1.0f, 1.0f, 0.0f, 1.0f,  uvmax, uvmin, // top right
            xmin, ymax, zmin,    1.0f, 1.0f, 0.0f, 1.0f,  uvmin, uvmin  // top left
    };

    private final int[] rgElements = {0, 1, 2, //top triangle
                                      2, 3, 0 // bottom triangle
    };
    int positionStride = 3;
    int colorStride = 4;
    int textureStride = 2;
    int vertexStride = (positionStride + colorStride + textureStride) * Float.BYTES;

    private int vaoID, vboID, eboID;
    final private int  vpoIndex = 0, vcoIndex = 1, vtoIndex = 2;

    slCamera my_camera;

    public slLevelSceneEditor() {

    }

    public void init() {
        my_camera = new slCamera(my_camera_loc);
        my_camera.setOrthoProjection();

        // TODO: put the new shaders once working
        //testShader = new slShaderManager("vs_0.glsl", "fs_0.glsl");
        testShader = new slShaderManager("vs_texture_1.glsl", "fs_texture_1.glsl");

        testShader.compile_shader();
        // TODO: Add texture manager object here:
        testTexture = new slTextureManager(System.getProperty("user.dir") + "/assets/shaders/FourTextures.png");

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

//        glEnable(GL_BLEND);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//        glEnable(GL_DEPTH_TEST);
//        glDepthFunc(GL_LESS);
//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        // GL_STATIC_DRAW good for now; we can later change to dynamic vertices:
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(rgElements.length);
        elementBuffer.put(rgElements).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(vpoIndex, positionStride, GL_FLOAT, false, vertexStride, 0);
        glEnableVertexAttribArray(vpoIndex);

        glVertexAttribPointer(vcoIndex, colorStride, GL_FLOAT, false, vertexStride, positionStride * Float.BYTES);
        glEnableVertexAttribArray(vcoIndex);

        // TODO: Add the vtoIndex --> "Vertex Texture Object Index" here via glVertexAttribPointer and enable it -
        // similar to other AttribPointers above.
        glVertexAttribPointer(vtoIndex, textureStride, GL_FLOAT, false, vertexStride, (positionStride + colorStride) * Float.BYTES);
        glEnableVertexAttribArray(vtoIndex);
    }

    public void update(float dt) {

        //TODO: Add camera motion here:

        my_camera.relativeMoveCamera(dt * alpha, dt * alpha);

        if (my_camera.getCurLookFrom().x < -FRUSTUM_RIGHT) {
            my_camera.restoreCamera();
        }

        testShader.set_shader_program();


        testShader.loadMatrix4f("uProjMatrix", my_camera.getProjectionMatrix());
        testShader.loadMatrix4f("uViewMatrix", my_camera.getViewMatrix());

        glBindVertexArray(vaoID);

        glEnableVertexAttribArray(vpoIndex);
        glEnableVertexAttribArray(vcoIndex);
        glEnableVertexAttribArray(vtoIndex);

        testTexture.bind_texture();
        //System.out.println("Successfully bound the texture!");

        glDrawElements(GL_TRIANGLES, rgElements.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(vpoIndex);
        glDisableVertexAttribArray(vcoIndex);
        glDisableVertexAttribArray(vtoIndex);

        glBindVertexArray(0);
        testShader.detach_shader();
    }

}
