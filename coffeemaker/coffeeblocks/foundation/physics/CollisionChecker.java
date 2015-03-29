package coffeeblocks.foundation.physics;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.models.ModelContainer;
import coffeeblocks.metaobjects.GameObject;

public class CollisionChecker {
	
	// TODO : bruk kun PhysicsObject for å behandle fysikk
	
	List<CollisionListener> listeners = new ArrayList<>();
	public void checkCollision(List<GameObject> objects){
		
	}
	public void checkObjects(GameObject entity_1,GameObject entity_2){
		if(entity_1==null||entity_2==null)
			return;
		if(entity_1.getGameModel().getModelFaces().isEmpty()||entity_2.getGameModel().getModelFaces().isEmpty())
			return;
		ModelContainer e1 = entity_1.getGameModel();
		
		Vector3f e1_vol = e1.getPhysicalScale();
		Vector3f e1_pos = e1.position;
		Vector3f e1_1_pos = new Vector3f();
//		Vector3f e1_1_pos = floatAdd(floatAdd(floatMultiply(-0.5f,e1_1_vol),e1_1_vol),e1_pos);
//		Vector3f e1_2_pos = floatAdd(floatAdd(floatMultiply(0.5f,e1_1_vol),floatMultiply(-1,e1_1_vol)),e1_pos);
	}
	
	private boolean checkOverlap(float[] box1_1,float[] box1_2,float[] box2_1,float[] box2_2){
		if(checkAxisOverlap(0,box1_1,box1_2,box2_1,box2_2)&&checkAxisOverlap(1,box1_1,box1_2,box2_1,box2_2)&&checkAxisOverlap(2,box1_1,box1_2,box2_1,box2_2))
			return true;
		return false;
	}
	
	private boolean checkAxisOverlap(int dim,float[] box1_1,float[] box1_2,float[] box2_1,float[] box2_2){
		if(box1_1[dim]<box2_2[dim]&&box1_2[dim]>box2_1[dim])
			return true;
		return false;
	}
	
	public void addCollisionListener(CollisionListener listener){
		listeners.add(listener);
	}
}
