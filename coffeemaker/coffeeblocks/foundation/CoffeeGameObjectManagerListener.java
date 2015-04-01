package coffeeblocks.foundation;

import coffeeblocks.metaobjects.GameObject;

public interface CoffeeGameObjectManagerListener {
	default public void newGameObjectAdded(GameObject object){} 
	default public void newEntityObjectAdded(Object object){}
	default public void existingGameObjectChanged(String objectId){}
}
