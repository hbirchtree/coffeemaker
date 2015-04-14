package coffeeblocks.foundation.logic;

import coffeeblocks.foundation.CoffeeSceneManager;
import coffeeblocks.opengl.CoffeeAnimator;

public class CoffeeMenuScene extends CoffeeSceneTemplate{

	protected static final String SCENE_ID_SECOND = "second";

	public CoffeeMenuScene(CoffeeSceneManager manager, CoffeeAnimator animator) {
		super(manager, animator);
	}

	@Override
	public String getSceneId() {
		return SCENE_ID_SECOND;
	}

	@Override
	public void setupSpecifics() {
		
	}

	@Override
	public void tickSpecifics() {
		
	}

	@Override
	public void handleKeyRelease(int key) {
		
	}

	@Override
	public void handleMousePress(int key) {
		
	}

	@Override
	public void handleMouseRelease(int key) {
		
	}

	@Override
	public void handleCollisions(String body1, String body2) {
		
	}

	@Override
	public void onGlfwFrameTick(double currentTime) {
		
	}

}
