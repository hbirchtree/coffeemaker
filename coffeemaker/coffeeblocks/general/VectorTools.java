package coffeeblocks.general;

import org.lwjgl.util.vector.Vector3f;

public class VectorTools {
	public static Vector3f vectorMul(Vector3f vec, float scalar){
		vec.x *= scalar;
		vec.y *= scalar;
		vec.z *= scalar;
		return vec;
	}
}
