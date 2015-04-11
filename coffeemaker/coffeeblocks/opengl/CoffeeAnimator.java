package coffeeblocks.opengl;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.Vector3Container;
import coffeeblocks.general.VectorTools;
import coffeeblocks.opengl.components.CoffeeVertex;

public class CoffeeAnimator {
	public enum TransitionType {
		ValueLinear, ValueExpo, ValueIExpo
	}
	
	public static CoffeeVertex morphVertToTarget(CoffeeVertex base, CoffeeVertex target, float percentage){
		//percentage determines how *much* it should approach the target destination
		CoffeeVertex workVert = new CoffeeVertex(base);
		
		Vector3f deltaPos = Vector3f.sub(target.position, base.position, null);
		
		workVert.position.x = base.position.x + deltaPos.x*percentage;
		workVert.position.y = base.position.y + deltaPos.y*percentage;
		workVert.position.z = base.position.z + deltaPos.z*percentage;
		
		return workVert;
	}
	
	private List<Vector3Container> transitions = new ArrayList<>();
	public void addTransition(Vector3Container value , Vector3f target, TransitionType transition, float time){
		//time skal være i ms
		if(transitions.contains(value))
			return;
//			throw new IllegalArgumentException("Cannot add same value twice into transition system!");
		value.transitionTime = time;
		value.transitionRestTime = time;
		value.animationType = transition;
		switch(transition){
		case ValueExpo:
			value.animationIncrement = VectorTools.vectorMul(Vector3f.sub(target, value.getValue(), null),1/time);
			break;
		case ValueIExpo:
			break;
		case ValueLinear:
			value.animationIncrement = VectorTools.vectorMul(Vector3f.sub(target, value.getValue(), null),1/time);
			break;
		default:
			break;
		}
		transitions.add(value);
	}
	public void tickTransitions(float tickTime){
		for(Vector3Container transitional : new ArrayList<>(transitions)){
			switch(transitional.animationType){
			case ValueExpo:
				transitional.transitionRestTime -= tickTime*1000f;
//				System.out.println("Resttime:"+transitional.transitionRestTime);
				if(transitional.transitionRestTime<=0){
					transitions.remove(transitional);
					break;
				}
//				System.out.println(transitional.animationIncrement.toString());
//				transitional.animationIncrement = Vector3f.add(transitional.animationIncrementIncrement, transitional.animationIncrement, null);
//				System.out.println(VectorTools.vectorMul(transitional.animationIncrement,(transitional.transitionTime-transitional.transitionRestTime)).toString());
				transitional.setValue(VectorTools.vectorMul(transitional.animationIncrement,transitional.transitionTime-transitional.transitionRestTime));
				System.out.println(transitional.getValue());
				break;
			case ValueIExpo:
				break;
			case ValueLinear:
				transitional.transitionRestTime -= tickTime;
				if(transitional.transitionRestTime<=0){
					transitions.remove(transitional);
					break;
				}
				transitional.increaseValue(transitional.animationIncrement);
				break;
			default:
				break;
			}
		}
	}
}
