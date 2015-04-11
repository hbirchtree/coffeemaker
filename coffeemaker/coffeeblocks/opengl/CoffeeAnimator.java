package coffeeblocks.opengl;

import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.opengl.components.CoffeeVertex;

public class CoffeeAnimator {
	public static CoffeeVertex morphVertToTarget(CoffeeVertex base, CoffeeVertex target, float percentage){
		//percentage determines how *much* it should approach the target destination
		CoffeeVertex workVert = new CoffeeVertex(base);
		
		Vector3f deltaPos = Vector3f.sub(target.position, base.position, null);
		
		workVert.position.x = base.position.x + deltaPos.x*percentage;
		workVert.position.y = base.position.y + deltaPos.y*percentage;
		workVert.position.z = base.position.z + deltaPos.z*percentage;
		
		return workVert;
	}
}
