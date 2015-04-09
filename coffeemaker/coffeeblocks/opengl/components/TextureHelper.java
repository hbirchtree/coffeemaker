package coffeeblocks.opengl.components;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class TextureHelper {
	public static int genTexture(String textureFilename){
		ByteBuffer img = null;
		int tWidth;
		int tHeight;
		try {
			InputStream in = new FileInputStream(textureFilename);
			PNGDecoder decoder = new PNGDecoder(in);
			tWidth = decoder.getWidth();
			tHeight = decoder.getHeight();
			img = ByteBuffer.allocateDirect(4*tWidth*tHeight);
			decoder.decode(img, tWidth*4,Format.RGBA);
			img.flip();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		int textureId = GL11.glGenTextures();
//		GL13.glActiveTexture(textureUnit);
		GL11.glBindTexture(GL_TEXTURE_2D, textureId);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, tWidth, tHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, img);
		
		GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

		GL11.glBindTexture(GL_TEXTURE_2D, 0);
		return textureId;
	}
	public static int allocateTexture(int internalFormat,int colorFormat,int width, int height, ByteBuffer source){
		int handle = GL11.glGenTextures();
		GL11.glBindTexture(GL_TEXTURE_2D, handle);
		GL11.glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, colorFormat, GL11.GL_UNSIGNED_BYTE, 0);
//		GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
//		GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL_TEXTURE_2D, 0);
		return handle;
	}
}
