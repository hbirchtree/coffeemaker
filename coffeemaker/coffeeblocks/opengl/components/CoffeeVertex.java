package coffeeblocks.opengl.components;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class CoffeeVertex {
	public CoffeeVertex(){}
	public CoffeeVertex(CoffeeVertex otherVertex){
		this.position = new Vector3f(otherVertex.position);
		this.texCoord = new Vector2f(otherVertex.texCoord);
		this.normal = new Vector3f(otherVertex.normal);
		this.tangent = new Vector3f(otherVertex.tangent);
	}
	
	public static int VERTEX_DATA_SIZE = 11;
	
	public Vector3f position = new Vector3f();
	public Vector2f texCoord = new Vector2f();
	public Vector3f normal = new Vector3f();
	public Vector3f tangent = new Vector3f();
	
	public static float[] staticGetElements(CoffeeVertex val){
		float[] el = new float[11];
		el[0] = val.position.x;
		el[1] = val.position.y;
		el[2] = val.position.z;
		el[3] = val.texCoord.x;
		el[4] = val.texCoord.y;
		el[5] = val.normal.x;
		el[6] = val.normal.y;
		el[7] = val.normal.z;
		el[8] = val.tangent.x;
		el[9] = val.tangent.y;
		el[10] = val.tangent.z;
		return el;
	}
	public float[] getElements(){
		return staticGetElements(this);
	}
}
