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
		
		position.x+=positionalVelocity.x*physicalMass;
		position.y+=positionalVelocity.y*physicalMass;
		position.z+=positionalVelocity.z*physicalMass;
	}
	
	private PhysicsType physicsType = PhysicsType.Undefined;
	public PhysicsType getPhysicsType(){
		return physicsType;
	}
	public void setPhysicsType(PhysicsType type){
		this.physicsType = type;
	}
	
	private String collisionMeshFile = null;
	public void setCollisionMeshFile(String collisionMeshFile){
		this.collisionMeshFile = collisionMeshFile;
	}
	public String getCollisionMeshFile(){
		return collisionMeshFile;
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
	private Vector3f positionalVelocity = new Vector3f(0,0,0);
	private Vector3f positionalAcceleration = new Vector3f(0,0,0);
	public Vector3f getPositionalVelocity() {
		return positionalVelocity;
	}
	public void setPositionalVelocity(Vector3f positionalVelocity) {
		this.positionalVelocity = positionalVelocity;
	}
	public Vector3f getPositionalAcceleration() {
		return positionalAcceleration;
	}
	public void setPositionalAcceleration(Vector3f positionalAcceleration) {
		this.positionalAcceleration = positionalAcceleration;
	}
	public Vector3f getImpulse() {
		return impulse;
	}
	public void setImpulse(Vector3f impulse) {
		this.impulse = impulse;
	}

	private Vector3f impulse = new Vector3f();
	private Vector3f physicalInertia = new Vector3f();
	public void setPhysicalInertia(Vector3f inertia) {
		// TODO Auto-generated method stub
		if(inertia!=null)
			this.physicalInertia = inertia;
	}
	public Vector3f getPhysicalInertia(){
		return physicalInertia;
	}
}
