package coffeeblocks.foundation;

import coffeeblocks.opengl.CoffeeAnimator;

public class FloatContainer {
	public FloatContainer(){}
	public FloatContainer(float value){
		setValue(value);
	}
	public FloatContainer(FloatContainer binding){
		bindValue(binding);
	}
	
	private boolean bound = false;
	private FloatContainer binding = null;
	private float value = 0f;
	private float valueOffset = 0f;
	private float valueMultiplier = 1f;
	
	public float getValue(){
		float result = value;
		if(bound)
			result = binding.getValue();
		return (result+valueOffset)*valueMultiplier;
	}
	public void setValue(float value){
		this.value = value;
	}
	public void increaseValue(float value){
		this.value+=value;
	}

	public boolean isBound(){
		return bound;
	}
	
	public CoffeeAnimator.TransitionType animationType;
	public float transitionTime = 0;
	public float transitionRestTime = 0;
	public float animationIncrement = 0;
	public float animationIncrementIncrement = 0;
	
	public void bindValue(FloatContainer target){
		if(target==null)
			throw new IllegalArgumentException("Cannot bind value to null object");
		bound = true;
		binding = target;
	}
	public void unbindValue(){
		bound = false;
	}
}
