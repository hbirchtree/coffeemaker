package coffeeblocks.foundation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import coffeeblocks.foundation.input.CoffeeInputHandler;
import coffeeblocks.foundation.models.ModelContainer;
import coffeeblocks.foundation.physics.CollisionChecker;
import coffeeblocks.foundation.physics.CollisionListener;
import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.opengl.components.CoffeeCamera;
import coffeeblocks.opengl.components.LimeLight;

import org.lwjgl.glfw.GLFW;

public class CoffeeGameObjectManager implements CollisionListener{
	public CoffeeGameObjectManager(){
		physicsSystem = new CollisionChecker(this);
		this.addListener(physicsSystem);
		physicsSystem.addCollisionListener(this);
	}
	
	private CollisionChecker physicsSystem;
	public CollisionChecker getPhysicsSystem(){
		return physicsSystem;
	}
	
	private Vector4f clearColor = new Vector4f();
	public Vector4f getClearColor() {
		return clearColor;
	}
	public void setClearColor(Vector4f clearColor) {
		this.clearColor = clearColor;
	}
	
	
	private Map<String,GameObject> objects = new HashMap<>();
	public Collection<GameObject> getObjectList(){
		return objects.values();
	}
	public Collection<ModelContainer> getRenderables(){
		Collection<ModelContainer> result = new ArrayList<>();
		for(GameObject object : objects.values())
			result.add(object.getGameModel());
		return result;
	}
	public GameObject getObject(String objectId){
		return objects.get(objectId);
	}
	public synchronized void addObject(GameObject object){
		if(object==null&&!objects.values().contains(object))
			throw new IllegalArgumentException();
		objects.put(object.getObjectId(),object);
		for(CoffeeGameObjectManagerListener listener : listeners)
			listener.newGameObjectAdded(object);
	}
	@Override
	public void updateObject(String objectId){
		if(!objects.containsKey(objectId))
			throw new RuntimeException("Physics reported non-existant object!");
		if(objectId.equals("player")){
			getCamera().setCameraPos(Vector3f.add(getObject("player").getGameModel().getPosition(),getCamera().getCameraForwardVec(-5f),null));
			getLights().get(0).setPosition(getCamera().getCameraPos());
		}
	}
	
	public void requestObjectUpdate(String objectId, GameObject.PropertyEnumeration prop){
		for(CoffeeGameObjectManagerListener listener : listeners)
			listener.existingGameObjectChanged(objectId, prop);
	}
	
	private Map<String,Object> entities = new HashMap<>(); //for å lagre referanser til lys, kamera og andre objekter som skal være tilgjengelige.
	public synchronized void addEntity(String id,Object object){
		this.entities.put(id,object);
	}
	public CoffeeCamera getCamera(){
		for(String id : entities.keySet())
			if(id.equals("camera"))
				return (CoffeeCamera)entities.get(id);
		throw new IllegalStateException("Camera is not available in the GameObject manager!");
	}
	public List<LimeLight> getLights(){
		for(String id : entities.keySet())
			if(id.equals("lights")){
				try{
					return (List<LimeLight>)entities.get(id);
				}catch(ClassCastException e){
					System.err.println(e.getMessage());
					System.exit(1);
				}
			}
		throw new IllegalStateException("Camera is not available in the GameObject manager!");
	}
	private List<CoffeeGameObjectManagerListener> listeners = new ArrayList<>();
	public void addListener(CoffeeGameObjectManagerListener listener){
		listeners.add(listener);
	}
	
	public void billboard(String objectId,boolean spherical){
		if(!objects.containsKey(objectId))
			throw new IllegalArgumentException("Tried to billboard non-existant object!");
		Vector3f rotation = getObject(objectId).getGameModel().getRotation();
		rotation = new Vector3f(0,0,0);
		if(spherical)
			rotation.z = getCamera().getVertiAngle();
		rotation.y = getCamera().getHorizAngle();
		getObject(objectId).getGameModel().setRotation(rotation);
	}
}
