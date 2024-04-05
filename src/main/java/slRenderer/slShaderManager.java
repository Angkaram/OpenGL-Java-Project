package slRenderer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static csc133.spot.shader_program;
import static org.lwjgl.opengl.GL20.*;

public class slShaderManager {

    private final String vsFilename; // Vertex shader file path
    private final String fsFilename; // Fragment shader file path

    public slShaderManager(String vs_filename, String fs_filename) {
        this.vsFilename = vs_filename;
        this.fsFilename = fs_filename;
    }

    public int compile_shader() {
        int programID = glCreateProgram();

        // compile vertex shader
        String vertexShaderSource = loadAsString(vsFilename);
        int vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        assert vertexShaderSource != null;
        glShaderSource(vertexShaderID, vertexShaderSource);
        glCompileShader(vertexShaderID);
        if (glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Failed to compile vertex shader.");
            System.err.println(glGetShaderInfoLog(vertexShaderID));
            return -1;
        }
        else {
            System.out.println("Vertex Shader Compiled Successfully");
        }
        glAttachShader(programID, vertexShaderID);

        // compile fragment shader
        String fragmentShaderSource = loadAsString(fsFilename);
        int fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        assert fragmentShaderSource != null;
        glShaderSource(fragmentShaderID, fragmentShaderSource);
        glCompileShader(fragmentShaderID);
        if (glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Failed to compile fragment shader.");
            System.err.println(glGetShaderInfoLog(fragmentShaderID));
            return -1;
        }
        else {
            System.out.println("Fragment Shader Compiled Successfully");
        }
        glAttachShader(programID, fragmentShaderID);

        // link and use the shader program
        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Failed to link shader program.");
            System.err.println(glGetProgramInfoLog(programID));
            return -1;
        }
        glUseProgram(programID);

        // detach and delete shaders after linking
        glDetachShader(programID, vertexShaderID);
        glDetachShader(programID, fragmentShaderID);
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);

        return programID;
    }

    public void setShaderProgram() {
        GL20.glUseProgram(shader_program);
    }

    // this unbinds the shader program (put in rendering code)
    public static void detachShader() {
        GL20.glUseProgram(0);
    }

    public void loadMatrix4f(String uniformName, Matrix4f matrix) {
        int location = GL20.glGetUniformLocation(shader_program, uniformName);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrix.get(buffer);
        GL20.glUniformMatrix4fv(location, false, buffer);
    }

    public static String loadAsString(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not read the file: " + filePath);
            return null;
        }
    }
}
