package coffeeblocks.foundation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
	
	private Map<String,GameObject> instances = new HashMap<>();
	public Collection<GameObject> getInstanceList(){
		return new ArrayList<>(instances.values());
	}
	public void addInstance(GameObject instance){
		String name = instance.getObjectId();
		//Vi gjør dette for å unngå at bokstaver overskrives av hverandre. Vi kunne ha optimalisert det ved å bruke samme bokstav på to steder, men glemmer det nå.
		while(instances.containsKey(instance.getObjectId()))
			instance.setObjectId(name+(int)(Math.random()*1000));
		instances.put(instance.getObjectId(),instance);
		listeners.stream().forEach(listener -> listener.newGameObjectAdded(instance));
	}
	public void deleteInstance(String objectId){
		if(instances.containsKey(objectId))
			instances.remove(objectId);
	}
	public GameObject getInstancedObject(String objectId){
		return instances.get(objectId);
	}
	public GameObject getAnyObject(String objectId){
		GameObject obj = getObject(objectId);
		if(obj!=null)
			return obj;
		obj = getInstancedObject(objectId);
		if(obj!=null)
			return obj;
		return null;
	}
	
	private Map<String,GameObject> objects = new HashMap<>();
	public Collection<GameObject> getObjectList(){
		return objects.values();
	}
	public Collection<GameObject> getRenderablesObjects(){
		Collection<GameObject> result = getObjectList().stream().sequential().collect(Collectors.toList());
		result.addAll(getInstanceList().stream().sequential().collect(Collectors.toList()));
		return result;
	}
	public Collection<ModelContainer> getRenderables(){
		List<ModelContainer> result = getObjectList().stream().map(GameObject::getGameModel).sequential().collect(Collectors.toList());
		result.addAll(getInstanceList().stream().map(GameObject::getGameModel).sequential().collect(Collectors.toList()));
		return result;
	}
	public Collection<ModelContainer> getRenderablesOrdered(){
		Comparator<GameObject> byId = (o1,o2) -> o2.getObjectId().compareTo(o1.getObjectId());
		List<ModelContainer> result = getRenderablesObjects().stream().sorted(byId).map(GameObject::getGameModel).sequential().collect(Collectors.toList());
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
		listeners.parallelStream().forEach(listener -> listener.newGameObjectAdded(object));
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
