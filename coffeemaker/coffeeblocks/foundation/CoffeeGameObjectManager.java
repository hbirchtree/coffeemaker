package coffeeblocks.foundation;

import java.util.ArrayList;
import java.util.Collection;

import coffeeblocks.metaobjects.GameObject;

public class CoffeeGameObjectManager implements CoffeeRendererListener{
	private Collection<GameObject> objects = new ArrayList<>();
	
	public synchronized void addObject(GameObject object){
		if(object==null&&!objects.contains(object))
			throw new IllegalArgumentException();
		objects.add(object);
	}
	
	public Collection<GameObject> getObjectList(){
		return objects;
	}
	
	@Override
	public void onGlfwFrameTick(){
		for(GameObject object : objects)
			object.getGameModel().tick();
	}
	
	public CoffeeGameObjectManager(){
		
	}
}
