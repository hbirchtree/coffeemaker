package coffeeblocks.foundation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.util.vector.Vector4f;

import coffeeblocks.foundation.models.ModelContainer;
import coffeeblocks.foundation.physics.CollisionChecker;
import coffeeblocks.interfaces.listeners.CoffeeGameObjectManagerListener;
import coffeeblocks.interfaces.listeners.CollisionListener;
import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.metaobjects.InstantiableObject;
import coffeeblocks.openal.SoundObject;
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
	
	private Map<String,InstantiableObject> instantiables = new HashMap<>();
	public Collection<InstantiableObject> getInstantiableList(){
		return instantiables.values();
	}
	public void addInstantiableObject(String id,InstantiableObject object){
		instantiables.put(id, object);
	}
	public Set<String> getInstantiableIdList(){
		return instantiables.keySet();
	}
	public InstantiableObject getInstantiable(String key){
		return instantiables.get(key);
	}
	public Collection<ModelContainer> getInstantiableModels(){
		Collection<ModelContainer> result = new ArrayList<>();
		getInstantiableList().stream().forEach(o -> result.add(o.getGameModel()));
		return result;
	}
	public Collection<SoundObject> getInstantiableSounds(){
		Collection<SoundObject> result = new ArrayList<>();
		getInstantiableList().stream().forEach(o -> result.addAll(o.getSoundBox()));
		return result;
	}
	
	private List<GameObject> instances = new ArrayList<>();
	public Collection<GameObject> getInstanceList(){
		return new ArrayList<>(instances);
	}
	public void addInstance(GameObject instance){
		instances.add(instance);
	}
	public void deleteInstance(String objectId){
		getInstanceList().stream().filter(o -> (o.getObjectId()==objectId)).forEach(o -> instances.remove(o));
	}
	
	private Map<String,GameObject> objects = new HashMap<>();
	public Collection<GameObject> getObjectList(){
		return objects.values();
	}
	public Collection<GameObject> getRenderablesObjects(){
		Collection<GameObject> result = new ArrayList<>();
		getObjectList().stream().forEach(o -> result.add(o));
		getInstanceList().stream().forEach(o -> result.add(o));
		return result;
	}
	public Collection<ModelContainer> getRenderables(){
		Collection<ModelContainer> result = new ArrayList<>();
		getObjectList().stream().forEach(o -> result.add(o.getGameModel()));
		getInstanceList().stream().forEach(o -> result.add(o.getGameModel()));
		return result;
	}
	public Collection<ModelContainer> getRenderablesOrdered(){
		Comparator<GameObject> byId = (o1,o2) -> o2.getObjectId().compareTo(o1.getObjectId());
		Collection<ModelContainer> result = new ArrayList<>();
		getRenderablesObjects().stream().sorted(byId).forEach(o -> result.add(o.getGameModel()));
		return result;
	}
	public Set<String> getRenderableIds(){
		return objects.keySet();
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
}
