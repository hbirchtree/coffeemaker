package coffeeblocks.interfaces.listeners;

import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.metaobjects.GameObject.PropertyEnumeration;

public interface CoffeeGameObjectManagerListener {
	default public void newGameObjectAdded(GameObject object){} 
	default public void newEntityObjectAdded(Object object){}
	default public void existingGameObjectChanged(String objectId,GameObject.PropertyEnumeration property){}
	default public void existingGameObjectChanged(String objectId,PropertyEnumeration property, Object value){}
}
