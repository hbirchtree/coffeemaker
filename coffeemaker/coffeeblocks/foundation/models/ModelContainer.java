package coffeeblocks.foundation.models;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.physics.PhysicsObject;
import coffeeblocks.opengl.components.CoffeeMaterial;
import coffeeblocks.opengl.components.ShaderBuilder;

public class ModelContainer extends PhysicsObject {
	private boolean objectBaked = false;
	public boolean isObjectBaked() {
		return objectBaked;
	}
	public void setObjectBaked(boolean objectBaked) {
		this.objectBaked = objectBaked;
	}

	private ShaderBuilder shader = null;
	public synchronized void setShader(ShaderBuilder shader){
		this.shader = shader;
	}
	public ShaderBuilder getShader(){
		return shader;
	}
	
	private CoffeeMaterial material = new CoffeeMaterial();
	public CoffeeMaterial getMaterial() {
		return material;
	}

	private String vertShader = "";
	private String fragShader = "";
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
	
	private boolean billboard = false; //Vil alltid orientere seg mot kamera
	public boolean isBillboard() {
		return billboard;
	}
	public void setBillboard(boolean billboard) {
		this.billboard = billboard;
	}
	
	private boolean noDepthRendering = false;
	public boolean isNoDepthRendering() {
		return noDepthRendering;
	}
	public void setNoDepthRendering(boolean noDepthRendering) {
		this.noDepthRendering = noDepthRendering;
	}

	public int vaoHandle = 0;
	
	public int textureHandle = 0;
	public int glTextureUnit = 0;
	
//	public int vertLocation = 0;
//	public int vertTexCoordLocation = 0;
	
	private List<Float> faces = null;
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
	private Vector3f physicalInertia = new Vector3f();
	public void setPhysicalInertia(Vector3f inertia) {
		// TODO Auto-generated method stub
		if(inertia!=null)
			this.physicalInertia = inertia;
	}
	public Vector3f getPhysicalInertia(){
		return physicalInertia;
	}
}
