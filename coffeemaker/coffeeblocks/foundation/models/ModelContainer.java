package coffeeblocks.foundation.models;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;

import coffeeblocks.foundation.physics.PhysicsObject;
import coffeeblocks.opengl.ShaderBuilder;
import coffeeblocks.opengl.CoffeeMaterial;

public class ModelContainer extends PhysicsObject {
	private boolean objectBaked = false;
	public boolean isObjectBaked() {
		return objectBaked;
	}
	public void setObjectBaked(boolean objectBaked) {
		this.objectBaked = objectBaked;
	}

	protected ShaderBuilder shader = null;
	public synchronized void setShader(ShaderBuilder shader){
		this.shader = shader;
	}
	public ShaderBuilder getShader(){
		return shader;
	}
	
	protected CoffeeMaterial material = new CoffeeMaterial();
	public CoffeeMaterial getMaterial() {
		return material;
	}

	protected String vertShader = "/home/havard/vsh.txt";
	protected String fragShader = "/home/havard/fsh.txt";
	public synchronized void setShaderFiles(String vertShader,String fragShader){
		this.vertShader = vertShader;
		this.fragShader = fragShader;
	}
	public String getFragShader(){
		return fragShader;
	}
	public String getVertShader(){
		return vertShader;
	}
	
	public int glTextureUnit = 0;
	public int vaoHandle = 0;
	public int textureHandle = 0;
	public int vertLocation = 0;
	public int vertTexCoordLocation = 0;
	
	protected List<Float> faces = null;
	public synchronized void setModelFaces(List<Float> faces){
		//Vi deler automatisk opp lista til 3-dimensjonale punkter
		if(faces==null)
			throw new IllegalArgumentException("Not a valid array of vertices");
		this.faces = faces;
	}
	public List<Float> getModelFaces(){
		return faces;
	}
	
	public int getVertexDataSize(){
		return faces.size();
	}
	
	public FloatBuffer getVertexData(){
		int size = getVertexDataSize();
		float[] floatsArray = new float[size];
		size = 0;
		for(Float val : faces){
			floatsArray[size] = val;
			size++;
		}
		
		FloatBuffer result = BufferUtils.createFloatBuffer(size).put(floatsArray);
		
		result.flip();
		return result;
	}
}
