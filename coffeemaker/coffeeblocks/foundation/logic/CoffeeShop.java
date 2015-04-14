package coffeeblocks.foundation.logic;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import coffeeblocks.foundation.CoffeeSceneManager;
import coffeeblocks.opengl.CoffeeAnimator;

public class CoffeeShop extends CoffeeLogicLoop{

	public CoffeeShop(CoffeeSceneManager manager) {
		super(manager);
		mainScene = new CoffeeMainScene(manager,anim);
		menuScene = new CoffeeMenuScene(manager,anim);
		inputKeys.add(GLFW.GLFW_KEY_W);
		inputKeys.add(GLFW.GLFW_KEY_A);
		inputKeys.add(GLFW.GLFW_KEY_S);
		inputKeys.add(GLFW.GLFW_KEY_D);
		inputKeys.add(GLFW.GLFW_KEY_KP_0);
		inputKeys.add(GLFW.GLFW_KEY_KP_1);
		inputKeys.add(GLFW.GLFW_KEY_KP_8);
		inputKeys.add(GLFW.GLFW_KEY_KP_9);
		inputKeys.add(GLFW.GLFW_KEY_SPACE);
		inputKeys.add(GLFW.GLFW_KEY_ESCAPE);
	}

	private void applyScene(CoffeeSceneTemplate scene){
		if(this.scene!=null)
			this.scene.cleanup();
		manager.applyScene(scene.getSceneId());
		manager.getRenderer().addSounds(scene.getScene()); //Yes, sound in the renderer.
		manager.getRenderer().setRendering_swaps(1);
		manager.getRenderer().setWindowres(1280, 720);
		manager.getRenderer().addCoffeeListener(this); //fordi listen av lyttere blir tømt
		//		manager.getRenderer().addCoffeeListener(poser);
		manager.getScene(scene.getSceneId()).getPhysicsSystem().addCollisionListener(this);
		this.scene = scene; 
		
		scene.setupCamera();
		scene.setupPlayer();
		scene.setupSpecifics();
	}
	
	private CoffeeSceneTemplate mainScene;
	private CoffeeSceneTemplate menuScene;

	private CoffeeSceneTemplate scene = null;
	private CoffeeAnimator anim = new CoffeeAnimator();
	private List<Integer> inputKeys = new ArrayList<>();
	private boolean liveInDreamLand = true;

	public void eventLoop(){
		manager.getRenderer().addInputListener(this);
		applyScene(menuScene);

		while(liveInDreamLand){
			scene.updateClock();
			scene.tickCamera();
			scene.tickSpecifics();
			scene.tickPlayer();
		}
	}
	
	@Override
	public void onGlfwFrameTick(double currentTime){
		scene.onGlfwFrameTick(currentTime);
	}
	@Override
	public void onGlfwFrameTick(float tickTime){
		scene.onGlfwFrameTick(tickTime);
	}
	@Override
	public List<Integer> getRegisteredKeys() {
		return inputKeys;
	}
	@Override
	public void coffeeReceiveKeyRelease(int key){
		scene.handleKeyRelease(key);
	}
	@Override
	public void coffeeReceiveKeyPress(int key){
		switch(key){
		case GLFW.GLFW_KEY_KP_8:
			applyScene(mainScene);
			break;
		}
		scene.handleKeyPress(key);
	}
	@Override
	public void getCollisionNotification(String body1, String body2){
		scene.handleCollisions(body1, body2);
	}
	@Override
	public void onGlfwQuit(){
		System.exit(0);
	}
}
