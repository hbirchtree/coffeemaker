package coffeeblocks.foundation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import coffeeblocks.foundation.models.ModelContainer;
import coffeeblocks.foundation.physics.CollisionChecker;
import coffeeblocks.foundation.physics.CollisionListener;
import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.opengl.components.CoffeeCamera;
import coffeeblocks.opengl.components.LimeLight;

public class CoffeeGameObjectManager implements CollisionListener{
	public CoffeeGameObjectManager(){
		physicsSystem = new CollisionChecker(this);
		this.addListener(physicsSystem);
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
	
	public void requestObjectUpdate(String objectId, GameObject.PropertyEnumeration prop,Object value){
		for(CoffeeGameObjectManagerListener listener : listeners)
			listener.existingGameObjectChanged(objectId, prop, value);
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
	@SuppressWarnings("unchecked")
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
		CoffeeCamera camera = getCamera();
		if(spherical)
			getObject(objectId).getGameModel().getRotation().setValue(new Vector3f(-camera.getVertiAngle(),-camera.getHorizAngle(),0));
		else
			getObject(objectId).getGameModel().getRotation().setValue(new Vector3f(0,-camera.getHorizAngle(),0));
	}
}
