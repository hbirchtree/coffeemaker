package coffeeblocks.opengl;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class TextureHelper {
	public static int genTexture(String textureFilename,int textureUnit){
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
		GL13.glActiveTexture(textureUnit);
		GL11.glBindTexture(GL_TEXTURE_2D, textureId);
		
		GL11.glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, tWidth, tHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, img);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		
		return textureId;
	}
	
}
