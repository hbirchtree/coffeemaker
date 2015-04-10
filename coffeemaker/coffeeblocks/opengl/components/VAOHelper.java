package coffeeblocks.opengl.components;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.general.VectorTools;

public class VAOHelper {
	private static int VERT_STRIDE = 4*(3+2+3+3);
	
	public static int genVAO(FloatBuffer vertices, int vertLocation, int vertTexCoordLocation, int vertNormalLocation, int vertTangentLocation){
		int vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STREAM_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		int vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

		GL20.glEnableVertexAttribArray(vertLocation);
		GL20.glVertexAttribPointer(vertLocation, 3, GL11.GL_FLOAT, false, VERT_STRIDE,0);
		
		GL20.glEnableVertexAttribArray(vertTexCoordLocation);
		GL20.glVertexAttribPointer(vertTexCoordLocation, 2, GL11.GL_FLOAT, false, VERT_STRIDE,4*3);
		
		GL20.glEnableVertexAttribArray(vertNormalLocation);
		GL20.glVertexAttribPointer(vertNormalLocation, 3, GL11.GL_FLOAT, false, VERT_STRIDE,4*(3+2));
		
		GL20.glEnableVertexAttribArray(vertTangentLocation);
		GL20.glVertexAttribPointer(vertTangentLocation, 3, GL11.GL_FLOAT, false, VERT_STRIDE,4*(3+2+3));
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		return vao;
	}
	
	public static List<CoffeeVertex> genTangents(List<CoffeeVertex> inputFace){
		CoffeeVertex v0 = inputFace.get(0);
		CoffeeVertex v1 = inputFace.get(1);
		CoffeeVertex v2 = inputFace.get(2);
		
		Vector3f edge1 = Vector3f.sub(v1.position, v0.position, null);
		Vector3f edge2 = Vector3f.sub(v2.position, v0.position, null);
		
		float deltaU1 = v1.texCoord.x - v0.texCoord.x;
		float deltaV1 = v1.texCoord.y - v0.texCoord.y;
		float deltaU2 = v2.texCoord.x - v0.texCoord.x;
		float deltaV2 = v2.texCoord.y - v0.texCoord.y;
		
		float f = 1f/(deltaU1*deltaV2-deltaU2*deltaV1);
		
		Vector3f t = new Vector3f();
		Vector3f bt = new Vector3f();
		
		t.x = f * (deltaV2 * edge1.x - deltaV1 * edge2.x);
	    t.y = f * (deltaV2 * edge1.y - deltaV1 * edge2.y);
	    t.z = f * (deltaV2 * edge1.z - deltaV1 * edge2.z);

	    bt.x = f * (-deltaU2 * edge1.x - deltaU1 * edge2.x);
	    bt.y = f * (-deltaU2 * edge1.y - deltaU1 * edge2.y);
	    bt.z = f * (-deltaU2 * edge1.z - deltaU1 * edge2.z);
		
	    t.normalise();
	    bt.normalise();
	    
	    v0.tangent = t;
	    v0.bitangent = bt;
	    v1.tangent = t;
	    v1.bitangent = bt;
	    v2.tangent = t;
	    v2.bitangent = bt;
	    
		return inputFace;
	}
}
