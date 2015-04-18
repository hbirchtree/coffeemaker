package coffeeblocks.foundation.models;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;

import coffeeblocks.opengl.CoffeeAnimator;
import coffeeblocks.opengl.components.CoffeeVertex;
import coffeeblocks.opengl.components.VAOHelper;

public class CoffeeAnimationContainer {
	public static final int FLOATLIST_VERTEX_DATA_SIZE = 8;
	
	public CoffeeAnimationContainer(){}
	public CoffeeAnimationContainer(CoffeeAnimationContainer animationContainer) {
		this.staticDraw = animationContainer.isStaticallyDrawn();
		this.base = animationContainer.getBaseMesh();
		this.vboHandle = 0; //Vi vil ikke arve denne, animasjonene ville blitt de samme
		this.states = animationContainer.states;
	}
	public static FloatBuffer convertVerticesToFloatBuffer(List<CoffeeVertex> vertices,int size,int vsize){
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
			size+=vsize;
		}
		
		FloatBuffer result = BufferUtils.createFloatBuffer(size).put(floatsArray);
		
		result.flip();
		return result;
	}
	private List<CoffeeVertex> currentMesh = null;
	public void setCurrentMesh(List<CoffeeVertex> mesh){
		this.currentMesh = mesh;
	}
	public List<CoffeeVertex> getCurrentMesh(){
		if(currentMesh!=null)
			return currentMesh;
		else
			return base;
	}
	
	public static List<CoffeeVertex> convertFloatListToVertices(List<Float> faces){
		List<CoffeeVertex> mesh = new ArrayList<>();
		int pointer = 0;
		while(pointer<faces.size()){
			CoffeeVertex vert = new CoffeeVertex();
			for(int i=0;i<FLOATLIST_VERTEX_DATA_SIZE;i++)
				switch(i){
				case 0: vert.position.x = faces.get(pointer+i); break;
				case 1: vert.position.y = faces.get(pointer+i); break;
				case 2: vert.position.z = faces.get(pointer+i); break;
				case 3: vert.texCoord.x = faces.get(pointer+i); break;
				case 4: vert.texCoord.y = faces.get(pointer+i); break;
				case 5: vert.normal.x = faces.get(pointer+i); break;
				case 6: vert.normal.y = faces.get(pointer+i); break;
				case 7: vert.normal.z = faces.get(pointer+i); break;
				}
			mesh.add(vert);
			pointer+=FLOATLIST_VERTEX_DATA_SIZE;
		}
		List<CoffeeVertex> converted = new ArrayList<>();
		pointer = 0;
		while(pointer<mesh.size()){
			converted.addAll(VAOHelper.genTangents(mesh.subList(pointer,pointer+3)));
			pointer+=3;
		}
		return converted;
	}
	private boolean staticDraw = true;
	public boolean isStaticallyDrawn(){
		return staticDraw;
	}
	public void setStaticDraw(boolean staticDraw){
		this.staticDraw = staticDraw;
	}
	
	private List<CoffeeVertex> base = new ArrayList<>();
	public List<CoffeeVertex> getBaseMesh(){
		return base;
	}
	public int getBaseMeshSize(){
		return base.size()*CoffeeVertex.VERTEX_DATA_SIZE;
	}
	public FloatBuffer getBaseVertexData(){
		return convertVerticesToFloatBuffer(base,getBaseMeshSize(),CoffeeVertex.VERTEX_DATA_SIZE);
	}
	
	private Map<String,List<CoffeeVertex> > states = new HashMap<>();
	public void addState(String name, List<CoffeeVertex> model){
		if(model.size()!=base.size())
			throw new IllegalArgumentException("Cannot add model that does not have a corresponding set of vertices for animation!");
		states.put(name, model);
	}
	public void clearStates(){
		states.clear();
	}
	public List<CoffeeVertex> getState(String name){
		return states.get(name);
	}
	public void morphToState(){
		if(getAnimationState()!=null&&!states.containsKey(getAnimationState()))
			throw new IllegalArgumentException("Non-existant animation state was requested for model!");
		List<CoffeeVertex> targetMesh;
		if(getAnimationState()!=null)
			targetMesh = states.get(getAnimationState());
		else
			targetMesh = base;
		List<CoffeeVertex> workMesh = new ArrayList<>(getCurrentMesh());
		for(int i=0;i<targetMesh.size();i++){
			workMesh.set(i, CoffeeAnimator.morphVertToTarget(workMesh.get(i), targetMesh.get(i), getAnimationSpeed()));
		}
		setCurrentMesh(workMesh);
	}
	
	private float animationSpeed = 0f;
	public float getAnimationSpeed(){
		return animationSpeed;
	}
	public void setAnimationSpeed(float animationSpeed){
		this.animationSpeed = animationSpeed;
	}
	
	private String animationState = null;
	public String getAnimationState(){
		return animationState;
	}
	public void setAnimationState(String animationState){
		this.animationState = animationState;
	}
	public void setAnimationState(String animationState, float animationSpeed){
		this.animationState = animationState;
		setAnimationSpeed(animationSpeed);
	}
	
	private int vboHandle = 0;
	public int getVboHandle(){
		return vboHandle;
	}
	public void setVboHandle(int vboHandle){
		this.vboHandle = vboHandle;
	}
}
