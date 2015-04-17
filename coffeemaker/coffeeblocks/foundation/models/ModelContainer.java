package coffeeblocks.foundation.models;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.physics.PhysicsObject;
import coffeeblocks.metaobjects.Vector3Container;
import coffeeblocks.opengl.components.CoffeeMaterial;
import coffeeblocks.opengl.components.CoffeeRenderableObject;
import coffeeblocks.opengl.components.CoffeeVertex;
import coffeeblocks.opengl.components.ShaderBuilder;

public class ModelContainer extends PhysicsObject implements CoffeeRenderableObject{
	private boolean objectBaked = false; //For å vite om det er lastet inn i minnet

	public ModelContainer(){}
	public ModelContainer(ModelContainer model) {
		vertShader = model.getVertShaderFilename();
		fragShader = model.getFragShaderFilename();
		
		position = new Vector3Container(model.getPosition());
		modelOffset = model.getModelOffset();
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
		
		objectBaked = model.isBaked();
	}

	public synchronized void tick(){
		super.tick();
		rotation.increaseVelocity(rotation.getAcceleration());
		rotation.increaseValue(rotation.getVelocity());
	}
	
	public boolean isBaked() {
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
	public void setMaterial(CoffeeMaterial material) {
		this.material = material;
	}

	private String vertShader = ""; //Punkt-programmet for å vise modellen
	private String fragShader = ""; //Fargeleggings-programmet
	public synchronized void setShaderFiles(String vertShader,String fragShader){
		this.vertShader = vertShader;
		this.fragShader = fragShader;
	}
	public String getFragShaderFilename(){
		return fragShader;
	}
	public String getVertShaderFilename(){
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
		return animations.getCurrentMesh();
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
		
		//Vi leser listen og lager tangenter med det samme
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
	
	private Vector3f modelOffset = new Vector3f(); //Offset fra fysikkmodellen
	public Vector3f getModelOffset(){
		return modelOffset;
	}
	public void setModelOffset(Vector3f offset){
		this.modelOffset = offset;
	}
	@Override
	public Vector3f getPositionVector() {
		// TODO Auto-generated method stub
		return Vector3f.add(position.getValue(),modelOffset,null);
	}
	@Override
	public Vector3f getRotationVector() {
		// TODO Auto-generated method stub
		return rotation.getValue();
	}
	@Override
	public Vector3f getScaleVector() {
		// TODO Auto-generated method stub
		return scale.getValue();
	}
	@Override
	public boolean isStaticDraw() {
		// TODO Auto-generated method stub
		return animations.isStaticallyDrawn();
	}
	@Override
	public int getVboHandle() {
		// TODO Auto-generated method stub
		return animations.getVboHandle();
	}
	@Override
	public void setVboHandle(int handle) {
		animations.setVboHandle(handle);
	}
	@Override
	public void cleanupObject() {
		// TODO Auto-generated method stub
		
	}
}
