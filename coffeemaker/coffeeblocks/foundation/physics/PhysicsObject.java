package coffeeblocks.foundation.physics;

import org.lwjgl.util.vector.Vector3f;

public class PhysicsObject {
	public synchronized void tick(){
		positionalVelocity.x+=positionalAcceleration.x;
		positionalVelocity.y+=positionalAcceleration.y;
		positionalVelocity.z+=positionalAcceleration.z;
		
		rotationalVelocity.x+=rotationalAcceleration.x;
		rotationalVelocity.y+=rotationalAcceleration.y;
		rotationalVelocity.z+=rotationalAcceleration.z;
		
		position.x+=positionalVelocity.x*physicalMass;
		position.y+=positionalVelocity.y*physicalMass;
		position.z+=positionalVelocity.z*physicalMass;
		rotation.x+=rotationalVelocity.x;
		rotation.y+=rotationalVelocity.y;
		rotation.z+=rotationalVelocity.z;
	}
	
	protected float physicalMass = 1.0f;
	public synchronized void setPhysicalMass(float physicalMass){
		if(physicalMass<0)
			return;
		this.physicalMass = physicalMass;
	}
	public float getPhysicalMass(){
		return physicalMass;
	}
	
	protected Vector3f physicalScale = new Vector3f(1.0f,1.0f,1.0f);
	public synchronized void setPhysicalScale(Vector3f physicalScale){
		this.physicalScale = physicalScale;
	}
	public Vector3f getPhysicalScale(){
		return physicalScale;
	}
	
	public Vector3f position = new Vector3f(0,0,0);
	public Vector3f positionalVelocity = new Vector3f(0,0,0);
	public Vector3f positionalAcceleration = new Vector3f(0,0,0);
	public Vector3f scale = new Vector3f(1,1,1);
	public Vector3f rotation = new Vector3f(0,0,0);
	public Vector3f rotationalVelocity = new Vector3f(0,0,0);
	public Vector3f rotationalAcceleration = new Vector3f(0,0,0);
}
