package slRenderer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import java.io.File;
import java.nio.FloatBuffer;
import java.util.Scanner;
//import static slRenderer.slSingleBatchRenderer.OGL_MATRIX_SIZE;
import static csc133.spot.OGL_MATRIX_SIZE;
import static org.lwjgl.opengl.GL20.*;

public class slShaderManager {
    private static String vsFilename;  // Vertex shader file path
    private static String fsFilename; // Fragment shader file path
    private static int csProgram;

    slShaderManager (String vs_filename, String fs_filename) {
        vsFilename = readShader(vs_filename);
        fsFilename = readShader(fs_filename);
        csProgram = -1;
    }

    // read in the shader file as strings
    private String readShader(String s_filename) {
        StringBuilder strShader = new StringBuilder();
        try {
            File sLocation = new File(System.getProperty("user.dir") + "/assets/shaders/" + s_filename);
            Scanner myReader = new Scanner(sLocation);
            while (myReader.hasNextLine()) {
                strShader.append(myReader.nextLine());
            }
            myReader.close();

        } catch (Exception e) {
            System.out.println("Cannot find file: " + e.getMessage());
        }
        return strShader.toString();
    }

    // Overloading here
    public int compile_shader(String vs_filename, String fs_filename) {
        vsFilename = readShader(vs_filename);
        fsFilename = readShader(fs_filename);

        return compile_shader();
    }

    public int compile_shader() {
        csProgram = glCreateProgram();
        int VSID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(VSID, vsFilename);
        glCompileShader(VSID);
        glAttachShader(csProgram, VSID);
        int FSID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(FSID, fsFilename);
        glCompileShader(FSID);
        glAttachShader(csProgram, FSID);
        glLinkProgram(csProgram);
        glUseProgram(csProgram);

        return csProgram;
    }

    public void loadMatrix4f(String strMatrixName, Matrix4f my_matrix4f) {
        int var_location = glGetUniformLocation(csProgram, strMatrixName);
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(OGL_MATRIX_SIZE);
        my_matrix4f.get(matrixBuffer);
        glUniformMatrix4fv(var_location, false, matrixBuffer);
    }

    public static void detach_shader() {
        glUseProgram(0);
    }

    public void set_shader_program() {
        glUseProgram(csProgram);
    }
}
