package coffeeblocks.opengl.components;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

public class VAOHelper {
	public static int VERT_STRIDE = 4*(3+2+3+3);
	
	public static void genVAO(CoffeeRenderableObject object,FloatBuffer vertices, int vertLocation, int vertTexCoordLocation, int vertNormalLocation, int vertTangentLocation){
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
		
		object.getMaterial().setVaoHandle(vao);
		object.setVboHandle(vbo);
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
	    
	    v0.tangent = Vector3f.add(t, v0.tangent, null);
	    v1.tangent = Vector3f.add(t, v1.tangent, null);
	    v2.tangent = Vector3f.add(t, v2.tangent, null);
	    
	    inputFace.set(0, v0);
	    inputFace.set(1, v1);
	    inputFace.set(2, v2);
	    
		return inputFace;
	}
	
	public static void modifyVbo(int vboId, List<CoffeeVertex> mesh,ByteBuffer scratchBuf){
		if(mesh.size()==0)
			return;
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		
		for(int i=0;i<mesh.size();i++){
//			float[] data = mesh.get(i).getElements();
			float[] vpos = new float[3];
			vpos[0] = mesh.get(i).position.x;
			vpos[1] = mesh.get(i).position.y;
			vpos[2] = mesh.get(i).position.z;
			
			FloatBuffer buffer = scratchBuf.asFloatBuffer();
			buffer.rewind();
			buffer.put(vpos);
			buffer.flip();
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, i*VERT_STRIDE, scratchBuf);
		}
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
}
