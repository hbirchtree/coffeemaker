package coffeeblocks.general;
import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

public class VectorTools {
	public static Vector3f vectorMul(Vector3f vec, float scalar){
		Vector3f ovec = new Vector3f(vec);
		ovec.x *= scalar;
		ovec.y *= scalar;
		ovec.z *= scalar;
		return ovec;
	}
	public static Vector3f vectorMul(Vector3f vec, Vector3f scalar){
		Vector3f ovec = new Vector3f(vec);
		ovec.x *= scalar.x;
		ovec.y *= scalar.y;
		ovec.z *= scalar.z;
		return ovec;
	}
	public static Vector3f vectorLimitMin(Vector3f src, Vector3f valueMin){
		Vector3f value = new Vector3f(src);
		if(value.x<valueMin.x)
			value.x = valueMin.x;
		if(value.y<valueMin.y)
			value.y = valueMin.y;
		if(value.z<valueMin.z)
			value.z = valueMin.z;
		return value;
	}
	public static Vector3f vectorLimitMax(Vector3f src,Vector3f valueMax){
		Vector3f value = new Vector3f(src);
		if(value.x>valueMax.x)
			value.x = valueMax.x;
		if(value.y>valueMax.y)
			value.y = valueMax.y;
		if(value.z>valueMax.z)
			value.z = valueMax.z;
		return value;
	}
	public static Vector3f parseStrVector(String source,String separator){
		String[] splits = source.split(separator);
		if(splits.length!=3)
			throw new IllegalArgumentException("Cannot parse string to floating-point vector!");
		return new Vector3f(Float.valueOf(splits[0]),Float.valueOf(splits[1]),Float.valueOf(splits[2]));
	}
	public static javax.vecmath.Vector3f lwjglToVMVec3f(Vector3f vec){
		return new javax.vecmath.Vector3f(vec.x,vec.y,vec.z);
	}
	public static Vector3f vmVec3ftoLwjgl(javax.vecmath.Vector3f vec){
		return new Vector3f(vec.x,vec.y,vec.z);
	}
	public static FloatBuffer vecToFloatBuffer(Vector3f vec){
		FloatBuffer buf = BufferUtils.createFloatBuffer(3).put(new float[]{vec.x,vec.y,vec.z});
		buf.flip();
		return buf;
	}
	public static FloatBuffer listFloatsToBuffer(List<Float> floats){
		FloatBuffer buf = BufferUtils.createFloatBuffer(floats.size());
		float[] statfloats = new float[floats.size()];
		for(int i=0;i<floats.size();i++){
			statfloats[i] = floats.get(i);
		}
		buf.put(statfloats);
		buf.flip();
		return buf;
	}
}
