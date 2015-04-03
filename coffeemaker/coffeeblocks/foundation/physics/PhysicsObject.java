package coffeeblocks.foundation.physics;

import org.lwjgl.util.vector.Vector3f;

public class PhysicsObject {
	public enum PhysicsType {
		Undefined,Box,Sphere,Complex,StaticPlane,Capsule
	};
	
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
	
	private PhysicsType physicsType = PhysicsType.Undefined;
	public PhysicsType getPhysicsType(){
		return physicsType;
	}
	public void setPhysicsType(PhysicsType type){
		this.physicsType = type;
	}
	
	private float physicalMass = 1.0f;
	public synchronized void setPhysicalMass(float physicalMass){
		if(physicalMass<0)
			return;
		this.physicalMass = physicalMass;
	}
	public Vector3f getPosition() {
		return position;
	}
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	public Vector3f getRotation() {
		return rotation;
	}
	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}
	public float getPhysicalMass(){
		return physicalMass;
	}
	
	private Vector3f physicalScale = new Vector3f(1.0f,1.0f,1.0f);
	public synchronized void setPhysicalScale(Vector3f physicalScale){
		this.physicalScale = physicalScale;
	}
	public Vector3f getPhysicalScale(){
		return physicalScale;
	}
	
	private float restitution = 0f;
	private float friction = 0.5f;
	public float getRestitution() {
		return restitution;
	}
	public void setRestitution(float restitution) {
		this.restitution = restitution;
	}
	public float getFriction() {
		return friction;
	}
	public void setFriction(float friction) {
		this.friction = friction;
	}

	private Vector3f physicalRotation = new Vector3f(0,0,0);
	public Vector3f getPhysicalRotation() {
		return physicalRotation;
	}
	public void setPhysicalRotation(Vector3f physicalRotation) {
		this.physicalRotation = physicalRotation;
	}
	

	private Vector3f physicalLinearFactor = new Vector3f(1,1,1);
	public Vector3f getPhysicalLinearFactor() {
		return physicalLinearFactor;
	}
	public void setPhysicalLinearFactor(Vector3f physicalLinearFactor) {
		this.physicalLinearFactor = physicalLinearFactor;
	}

	private Vector3f position = new Vector3f(0,0,0);
	public Vector3f positionalVelocity = new Vector3f(0,0,0);
	public Vector3f positionalAcceleration = new Vector3f(0,0,0);
	public Vector3f scale = new Vector3f(1,1,1);
	private Vector3f rotation = new Vector3f(0,0,0);
	public Vector3f rotationalVelocity = new Vector3f(0,0,0);
	public Vector3f rotationalAcceleration = new Vector3f(0,0,0);
}
