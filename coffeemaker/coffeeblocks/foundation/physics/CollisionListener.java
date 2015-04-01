package coffeeblocks.foundation.physics;

import org.lwjgl.util.vector.Vector3f;

public interface CollisionListener {
	//Vi bruker ID for å kommunisere det lettere. Mottakere skal allerede ha tilgang til CoffeeGameObjectManager.
	default public void getCollisionNotification(String body1, String body2){}
	default public void updateObject(String objectId){}
	default public void updateObjectPosition(String objectId,Vector3f position){}
}
