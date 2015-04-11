package coffeeblocks.foundation.models;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.physics.PhysicsObject;
import coffeeblocks.opengl.components.CoffeeMaterial;
import coffeeblocks.opengl.components.CoffeeVertex;
import coffeeblocks.opengl.components.ShaderBuilder;

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
	
	private CoffeeAnimationContainer animations = new CoffeeAnimationContainer();
	public CoffeeAnimationContainer getAnimationContainer(){
		return animations;
	}
	public List<CoffeeVertex> getVertices(){
		return animations.getBaseMesh();
	}
	public synchronized void addVertex(CoffeeVertex vertex){
		animations.getBaseMesh().add(vertex);
	}
	public synchronized void clearVertices(){
		animations.getBaseMesh().clear();
	}
	public synchronized void addVertices(List<CoffeeVertex> vertices){
		animations.getBaseMesh().addAll(vertices);
	}
	private List<Float> faces = null;
	public synchronized void setModelFaces(List<Float> faces){
		//Vi deler automatisk opp lista til 3-dimensjonale punkter
		if(faces==null)
			throw new IllegalArgumentException("Not a valid array of vertices");
		this.faces = faces;
		
		List<CoffeeVertex> converted = CoffeeAnimationContainer.convertFloatListToVertices(faces);
		clearVertices();
		addVertices(converted);
	}
	public List<Float> getModelFaces(){
		return faces;
	}
	
	public int getVertexDataSize(){
		return animations.getBaseMeshSize();
	}
	
	public FloatBuffer getVertexData(){
		return animations.getBaseVertexData();
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
