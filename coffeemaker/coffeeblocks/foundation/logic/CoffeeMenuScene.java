package coffeeblocks.foundation.logic;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.CoffeeSceneManager;
import coffeeblocks.foundation.logic.CoffeeShop.SceneApplier;
import coffeeblocks.opengl.CoffeeAnimator;

public class CoffeeMenuScene extends CoffeeSceneTemplate{

	protected static final String SCENE_ID_SECOND = "second";

	public CoffeeMenuScene(CoffeeSceneManager manager, CoffeeAnimator animator, SceneApplier sceneApplier) {
		super(manager, animator, sceneApplier);
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

	@Override public void handleKeyPress(int key){
		switch(key){
		case GLFW.GLFW_KEY_W:{
			applyScene("main");
			return;
		}
		case GLFW.GLFW_KEY_J:{
			getObject(OBJECT_ID_PLAYER).getGameModel().getPosition().setValue(new Vector3f());
			animator.addTransition(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition(), new Vector3f(0,15,0), CoffeeAnimator.TransitionType.ValueLinear, 1000);
			return;
		}
		case GLFW.GLFW_KEY_K:{
			getObject(OBJECT_ID_PLAYER).getGameModel().getPosition().setValue(new Vector3f());
			animator.addTransition(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition(), new Vector3f(0,-15,0), CoffeeAnimator.TransitionType.ValueLinear, 1000);
			return;
		}
		}
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
