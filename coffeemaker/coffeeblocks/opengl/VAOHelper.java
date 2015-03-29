package coffeeblocks.opengl;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class VAOHelper {
	public static int genVAO(FloatBuffer vertices, int vertLocation, int vertTexCoordLocation, int vertNormalLocation){
		int vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		int vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

		GL20.glEnableVertexAttribArray(vertLocation);
		GL20.glVertexAttribPointer(vertLocation, 3, GL11.GL_FLOAT, false, 4*(3+2+3),0);
		
		GL20.glEnableVertexAttribArray(vertTexCoordLocation);
		GL20.glVertexAttribPointer(vertTexCoordLocation, 2, GL11.GL_FLOAT, false, 4*(3+2+3),4*3);
		
		GL20.glEnableVertexAttribArray(vertNormalLocation);
		GL20.glVertexAttribPointer(vertNormalLocation, 3, GL11.GL_FLOAT, false, 4*(3+2+3),4*(3+2));
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		return vao;
	}
}
