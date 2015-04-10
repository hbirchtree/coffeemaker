package coffeeblocks.foundation.models;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.physics.PhysicsObject;
import coffeeblocks.opengl.components.CoffeeMaterial;
import coffeeblocks.opengl.components.CoffeeVertex;
import coffeeblocks.opengl.components.ShaderBuilder;
import coffeeblocks.opengl.components.VAOHelper;

public class ModelContainer extends PhysicsObject {
	private boolean objectBaked = false;

	public synchronized void tick(){
		super.tick();
		rotationalVelocity.x+=rotationalAcceleration.x;
		rotationalVelocity.y+=rotationalAcceleration.y;
		rotationalVelocity.z+=rotationalAcceleration.z;
		
		rotation.x+=rotationalVelocity.x;
		rotation.y+=rotationalVelocity.y;
		rotation.z+=rotationalVelocity.z;
	}
	
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
	
	private boolean noDepthRendering = false;
	public boolean isNoDepthRendering() {
		return noDepthRendering;
	}
	public void setNoDepthRendering(boolean noDepthRendering) {
		this.noDepthRendering = noDepthRendering;
	}
	
	private List<CoffeeVertex> vertices = new ArrayList<>();
	public List<CoffeeVertex> getVertices(){
		return vertices;
	}
	public synchronized void addVertex(CoffeeVertex vertex){
		vertices.add(vertex);
	}
	public synchronized void clearVertices(){
		vertices.clear();
	}
	public synchronized void addVertices(List<CoffeeVertex> vertices){
		this.vertices.addAll(vertices);
	}
	public static final int VERTEX_DATA_SIZE = 8;
	private List<Float> faces = null;
	public synchronized void setModelFaces(List<Float> faces){
		//Vi deler automatisk opp lista til 3-dimensjonale punkter
		if(faces==null)
			throw new IllegalArgumentException("Not a valid array of vertices");
		this.faces = faces;
		
		int pointer = 0;
		while(pointer<getVertexDataSize()){
			CoffeeVertex vert = new CoffeeVertex();
			for(int i=0;i<VERTEX_DATA_SIZE;i++)
				switch(i){
				case 0: vert.position.x = faces.get(i); break;
				case 1: vert.position.y = faces.get(i); break;
				case 2: vert.position.z = faces.get(i); break;
				case 3: vert.texCoord.x = faces.get(i); break;
				case 4: vert.texCoord.y = faces.get(i); break;
				case 5: vert.normal.x = faces.get(i); break;
				case 6: vert.normal.y = faces.get(i); break;
				case 7: vert.normal.z = faces.get(i); break;
				}
			addVertex(vert);
			pointer+=VERTEX_DATA_SIZE;
		}
		List<CoffeeVertex> converted = new ArrayList<>();
		pointer = 0;
		while(pointer<vertices.size()){
			converted.addAll(VAOHelper.genTangents(vertices.subList(pointer,pointer+3)));
			pointer+=3;
		}
		clearVertices();
		addVertices(converted);
	}
	public List<Float> getModelFaces(){
		return faces;
	}
	
	public int getVertexDataSize(){
		return vertices.size()*(VERTEX_DATA_SIZE+3);
	}
	
	public FloatBuffer getVertexData(){
		int size = getVertexDataSize(); //de tre siste er generert i programmet
		float[] floatsArray = new float[size];
		size = 0;
		for(CoffeeVertex val : vertices){
			floatsArray[size+0] = val.position.x;
			floatsArray[size+1] = val.position.y;
			floatsArray[size+2] = val.position.z;
			floatsArray[size+3] = val.texCoord.x;
			floatsArray[size+4] = val.texCoord.y;
			floatsArray[size+5] = val.normal.x;
			floatsArray[size+6] = val.normal.y;
			floatsArray[size+7] = val.normal.z;
			floatsArray[size+8] = val.tangent.x;
			floatsArray[size+9] = val.tangent.y;
			floatsArray[size+10] = val.tangent.z;
			size+=VERTEX_DATA_SIZE+3;
		}
		
		FloatBuffer result = BufferUtils.createFloatBuffer(size).put(floatsArray);
		
		result.flip();
		return result;
	}
	public Vector3f getScale() {
		return scale;
	}
	private Vector3f scale = new Vector3f(1,1,1);
	public void setScale(Vector3f scale) {
		this.scale = scale;
	}
	private Vector3f rotation = new Vector3f(0,0,0);
	public Vector3f getRotation() {
		return rotation;
	}
	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}
	private Vector3f rotationalVelocity = new Vector3f(0,0,0);
	public Vector3f getRotationalVelocity() {
		return rotationalVelocity;
	}
	public void setRotationalVelocity(Vector3f rotationalVelocity) {
		this.rotationalVelocity = rotationalVelocity;
	}
	private Vector3f rotationalAcceleration = new Vector3f(0,0,0);
	public Vector3f getRotationalAcceleration() {
		return rotationalAcceleration;
	}
	public void setRotationalAcceleration(Vector3f rotationalAcceleration) {
		this.rotationalAcceleration = rotationalAcceleration;
	}
}
