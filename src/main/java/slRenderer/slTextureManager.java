package slRenderer;

import org.lwjgl.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.stb.STBImage.*;

interface TextureManagerInterface {
    void bind_texture();
    void unbind_texture();
}

class slTextureManager implements TextureManagerInterface {
    private final String texFilepath;
    private final int texID;
    private ByteBuffer texImage = null;

    public slTextureManager(String filepath) {
        this.texFilepath = filepath;

        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        IntBuffer texWidth = BufferUtils.createIntBuffer(1);
        IntBuffer texHeight = BufferUtils.createIntBuffer(1);
        IntBuffer texChannels = BufferUtils.createIntBuffer(1);

        texImage = stbi_load(texFilepath, texWidth, texHeight, texChannels, 0);
        if (texImage == null) {
            throw new RuntimeException("Failed to load a texture file \"" + texFilepath + "\" " + stbi_failure_reason());
        } else {
            int format = texChannels.get(0) == 4 ? GL_RGBA : GL_RGB;
            glTexImage2D(GL_TEXTURE_2D, 0, format, texWidth.get(0), texHeight.get(0), 0, format, GL_UNSIGNED_BYTE, texImage);
            System.out.println("Successfully loaded Mario texture!");
        }

        // Ensure texImage is not null before freeing it
        if (texImage != null) {
            stbi_image_free(texImage);
        }
    }

    @Override
    public void bind_texture() {
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    @Override
    public void unbind_texture() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
