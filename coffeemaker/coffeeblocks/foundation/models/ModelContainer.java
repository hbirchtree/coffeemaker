package coffeeblocks.foundation.models;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.physics.PhysicsObject;
import coffeeblocks.metaobjects.Vector3Container;
import coffeeblocks.opengl.components.CoffeeMaterial;
import coffeeblocks.opengl.components.CoffeeVertex;
import coffeeblocks.opengl.components.ShaderBuilder;

public class ModelContainer extends PhysicsObject {
	private boolean objectBaked = false; //For å vite om det er lastet inn i minnet

	public ModelContainer(){}
	public ModelContainer(ModelContainer model) {
		vertShader = model.getVertShader();
		fragShader = model.getFragShader();
		
		position = new Vector3Container(model.getPosition());
		rotation = new Vector3Container(model.getRotation());
		scale = new Vector3Container(model.getScale());
		physicalScale = new Vector3f(model.getPhysicalScale());
		physicalRotation = new Vector3f(model.getPhysicalRotation());
		physicalMass = model.getPhysicalMass();
		friction = model.getFriction();
		restitution = model.getRestitution();
		impulse = new Vector3f(model.getImpulse());
		
		physicalInertia = model.getPhysicalInertia();
		physicalLinearFactor = model.getPhysicalLinearFactor();
		material = model.getMaterial();
		shader = model.getShader();
		animations = new CoffeeAnimationContainer(model.getAnimationContainer());
		physicsType = model.getPhysicsType();
		collisionMeshFile = model.getCollisionMeshFile();
		
		updateRotation = model.isUpdateRotation();
		notifiesForce = model.isNotifyForce();
		objectDeactivation = model.getObjectDeactivation();
		
		objectBaked = model.isObjectBaked();
	}

	public synchronized void tick(){
		super.tick();
		rotation.increaseVelocity(rotation.getAcceleration());
		rotation.increaseValue(rotation.getVelocity());
	}
	
	public boolean isObjectBaked() {
		return objectBaked;
	}
	public void setObjectBaked(boolean objectBaked) {
		this.objectBaked = objectBaked;
	}

	private ShaderBuilder shader = null; //Tar hånd om shader-programmet, uniforme variabler og attributer
	public synchronized void setShader(ShaderBuilder shader){
		this.shader = shader;
	}
	public ShaderBuilder getShader(){
		return shader;
	}
	
	private CoffeeMaterial material = new CoffeeMaterial(); //Materialet er teksturer, bumpmap, specular osv.
	public CoffeeMaterial getMaterial() {
		return material;
	}

	private String vertShader = ""; //Punkt-programmet for å vise modellen
	private String fragShader = ""; //Fargeleggings-programmet
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
	
	private boolean noDepthRendering = false; //Om elementet skal tegnes over andre eller om dybdebufferet skal brukes
	public boolean isNoDepthRendering() {
		return noDepthRendering;
	}
	public void setNoDepthRendering(boolean noDepthRendering) {
		this.noDepthRendering = noDepthRendering;
	}
	
	private CoffeeAnimationContainer animations = new CoffeeAnimationContainer(); 
	//Animasjoner; alternative modeller objektet kan gå over til.
	//Inneholder også modellen til objektet
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
	private List<Float> faces = null; //Den importerte modellen
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
	private Vector3Container scale = new Vector3Container();
	public Vector3Container getScale() {
		return scale;
	}
	private Vector3Container rotation = new Vector3Container(); //i GRADER
	public Vector3Container getRotation() {
		return rotation;
	}
}
