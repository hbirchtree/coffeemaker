package coffeeblocks.foundation;

import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.general.VectorTools;
import coffeeblocks.opengl.CoffeeAnimator;

public class Vector3Container {
	private boolean bound = false;
	private Vector3Container binding = null;
	
	public boolean isBound(){
		return bound;
	}
	
	public Vector3Container(){}
	public Vector3Container(Vector3f value){
		this.value = value;
	}
	public Vector3Container(Vector3Container value){
		bindValue(value);
	}
	
	private Vector3f value = new Vector3f();
	private Vector3f valueOffset = null;

	private Vector3f velocity = new Vector3f(); //For å øke verdien ved tick
	private Vector3f acceleration = new Vector3f(); //For å øke farten ved tick
	public Vector3f getValue() {
		Vector3f result = null;
		if(bound)
			result = binding.getValue();
		else
			result = value;
		if(valueOffset!=null)
			return Vector3f.add(result, valueOffset,null);
		return result;
	}
	public void setValue(Vector3f value) {
		unbindValue();
		this.value = value;
	}
	public void increaseValue(Vector3f value){
		setValue(Vector3f.add(value, this.value, null));
	}
	public Vector3f getVelocity() {
		if(bound)
			return binding.getVelocity();
		return velocity;
	}
	public void setVelocity(Vector3f velocity) {
		unbindValue();
		this.velocity = velocity;
	}
	public void increaseVelocity(Vector3f velocity){
		setVelocity(Vector3f.add(velocity, this.velocity, null));
	}
	public Vector3f getAcceleration() {
		if(bound)
			return binding.getAcceleration();
		return acceleration;
	}
	public void setAcceleration(Vector3f acceleration) {
		unbindValue();
		this.acceleration = acceleration;
	}
	public void increaseAcceleration(Vector3f acceleration){
		setAcceleration(Vector3f.add(acceleration, this.acceleration, null));
	}
	public javax.vecmath.Vector3f getValueVM() {
		if(bound)
			return binding.getValueVM();
		return VectorTools.lwjglToVMVec3f(value);
	}
	public void setValue(javax.vecmath.Vector3f value) {
		setValue(VectorTools.vmVec3ftoLwjgl(value));
	}
	public javax.vecmath.Vector3f getVelocityVM() {
		if(bound)
			return binding.getVelocityVM();
		return VectorTools.lwjglToVMVec3f(velocity);
	}
	public void setVelocity(javax.vecmath.Vector3f velocity) {
		setVelocity(VectorTools.vmVec3ftoLwjgl(velocity));
	}
	public javax.vecmath.Vector3f getAccelerationVM() {
		if(bound)
			return binding.getAccelerationVM();
		return VectorTools.lwjglToVMVec3f(acceleration);
	}
	public void setAcceleration(javax.vecmath.Vector3f acceleration) {
		setAcceleration(VectorTools.vmVec3ftoLwjgl(acceleration));
	}
	public Vector3f getValueOffset() {
		return valueOffset;
	}

	public void setValueOffset(Vector3f valueOffset) {
		this.valueOffset = valueOffset;
	}
	
	public void bindValue(Vector3Container target){
		if(target==null)
			throw new IllegalArgumentException("Cannot bind value to null object");
		bound = true;
		binding = target;
	}
	public void unbindValue(){
		bound = false;
	}
	
	public CoffeeAnimator.TransitionType animationType;
	public float transitionTime = 0f;
	public float transitionRestTime = 0f;
	public Vector3f animationIncrement = new Vector3f();
	public Vector3f animationIncrementIncrement = new Vector3f();
}