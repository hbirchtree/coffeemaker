package coffeeblocks.foundation.physics;

import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.metaobjects.Vector3Container;

public abstract class PhysicsObject {
	public enum PhysicsType {
		Undefined,Box,Sphere,Complex,StaticPlane,Capsule
	};
	
	public synchronized void tick(){
		position.increaseVelocity(position.getAcceleration());
		position.increaseValue(position.getVelocity());
	}
	
	protected PhysicsType physicsType = PhysicsType.Undefined;
	public PhysicsType getPhysicsType(){
		return physicsType;
	}
	public void setPhysicsType(PhysicsType type){
		this.physicsType = type;
	}
	
	protected String collisionMeshFile = null;
	public void setCollisionMeshFile(String collisionMeshFile){
		this.collisionMeshFile = collisionMeshFile;
	}
	public String getCollisionMeshFile(){
		return collisionMeshFile;
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
	
	protected Vector3f physicalScale = new Vector3f(1.0f,1.0f,1.0f); //Skala for det fysiske objektet
	public synchronized void setPhysicalScale(Vector3f physicalScale){
		this.physicalScale = physicalScale;
	}
	public Vector3f getPhysicalScale(){
		return physicalScale;
	}
	
	protected float restitution = 0f; //Hvor mye objektet gir etter ved støt
	protected float friction = 0.5f; //Friksjon.
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

	protected Vector3f physicalRotation = new Vector3f(0,0,0);
	public Vector3f getPhysicalRotation() {
		return physicalRotation;
	}
	public void setPhysicalRotation(Vector3f physicalRotation) {
		this.physicalRotation = physicalRotation;
	}
	

	protected Vector3f physicalLinearFactor = new Vector3f(1,1,1);
	public Vector3f getPhysicalLinearFactor() {
		return physicalLinearFactor;
	}
	public void setPhysicalLinearFactor(Vector3f physicalLinearFactor) {
		this.physicalLinearFactor = physicalLinearFactor;
	}

	protected Vector3Container position = new Vector3Container();
	public Vector3Container getPosition(){
		return position;
	}
	
	public Vector3f getImpulse() {
		return impulse;
	}
	public void setImpulse(Vector3f impulse) {
		this.impulse = impulse;
	}

	protected Vector3f impulse = new Vector3f();
	protected Vector3f physicalInertia = new Vector3f();
	public void setPhysicalInertia(Vector3f inertia) {
		// TODO Auto-generated method stub
		if(inertia!=null)
			this.physicalInertia = inertia;
	}
	public Vector3f getPhysicalInertia(){
		return physicalInertia;
	}
	
	protected boolean objectDeactivation = true;
	public boolean getObjectDeactivation(){
		return objectDeactivation;
	}
	public void setObjectDeactivation(boolean deactivation){
		this.objectDeactivation = deactivation;
	}
	
	protected boolean notifiesForce = false;
	public boolean isNotifyForce(){
		return notifiesForce;
	}
	public void setNotifyForce(boolean notify){
		this.notifiesForce = notify;
	}
	
	protected boolean updateRotation = true;
	public boolean isUpdateRotation(){
		return updateRotation;
	}
	public void setUpdateRotation(boolean updateRotation){
		this.updateRotation = updateRotation;
	}
}
