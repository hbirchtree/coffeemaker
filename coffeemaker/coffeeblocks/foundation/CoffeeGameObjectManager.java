package coffeeblocks.foundation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.physics.CollisionListener;
import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.opengl.components.CoffeeCamera;

public class CoffeeGameObjectManager implements CoffeeRendererListener,CollisionListener{
	private Map<String,GameObject> objects = new HashMap<>();
	private Map<String,Object> entities = new HashMap<>(); //for å lagre referanser til lys, kamera og andre objekter som skal være tilgjengelige.
	private List<CoffeeGameObjectManagerListener> listeners = new ArrayList<>();
	
	public void addListener(CoffeeGameObjectManagerListener listener){
		listeners.add(listener);
	}
	
	public synchronized void addEntity(String id,Object object){
		this.entities.put(id,object);
	}
	public synchronized void addObject(GameObject object){
		if(object==null&&!objects.values().contains(object))
			throw new IllegalArgumentException();
		objects.put(object.getObjectId(),object);
		for(CoffeeGameObjectManagerListener listener : listeners)
			listener.newGameObjectAdded(object);
	}
	public GameObject getObject(String objectId){
		return objects.get(objectId);
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
	public void requestObjectUpdate(String objectId){
		if(!objects.containsKey(objectId))
			throw new IllegalArgumentException("Could not update non-existant object!");
		for(CoffeeGameObjectManagerListener listener : listeners)
			listener.existingGameObjectChanged(objectId);
	}
	@Override
	public void updateObject(String objectId){
		if(!objects.containsKey(objectId))
			throw new RuntimeException("Physics reported non-existant object!");
		if(objectId.equals("player"))
			getCamera().setCameraPos(Vector3f.add(getObject("player").getGameModel().getPosition(),getCamera().getCameraForwardVec(-5f),null));
	}
	
	public CoffeeCamera getCamera(){
		for(String id : entities.keySet())
			if(id.equals("camera"))
				return (CoffeeCamera)entities.get(id);
		throw new IllegalStateException("Camera is not available in the GameObject manager!");
	}
	
	public Collection<GameObject> getObjectList(){
		return objects.values();
	}
	
	@Override
	public void onGlfwFrameTick(){
		billboard("player", false);
	}
}
