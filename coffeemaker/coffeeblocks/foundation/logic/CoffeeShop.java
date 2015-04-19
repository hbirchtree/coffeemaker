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
		inputKeys.add(GLFW.GLFW_KEY_F);
		inputKeys.add(GLFW.GLFW_KEY_KP_0);
		inputKeys.add(GLFW.GLFW_KEY_KP_1);
		inputKeys.add(GLFW.GLFW_KEY_KP_8);
		inputKeys.add(GLFW.GLFW_KEY_KP_9);
		inputKeys.add(GLFW.GLFW_KEY_SPACE);
		inputKeys.add(GLFW.GLFW_KEY_ESCAPE);
		
		inputButtons.add(GLFW.GLFW_MOUSE_BUTTON_1);
		inputButtons.add(GLFW.GLFW_MOUSE_BUTTON_2);
		inputButtons.add(GLFW.GLFW_MOUSE_BUTTON_3);
	}

	private void applyScene(CoffeeSceneTemplate scene){
		if(this.scene!=null){
			this.scene.cleanup();
			this.scene.setReadyStatus(false);
		}
		manager.applyScene(scene.getSceneId());
		manager.getRenderer().addSounds(scene.getScene()); //Yes, sound in the renderer.
		manager.getRenderer().setRendering_swaps(1);
		manager.getRenderer().setWindowres(1280, 720);
		manager.getRenderer().addCoffeeListener(this); //fordi listen av lyttere blir tømt
		manager.getScene(scene.getSceneId()).getPhysicsSystem().addCollisionListener(this);
		
		scene.setupCamera();
		scene.setupPlayer();
		scene.setupSpecifics();
		scene.setReadyStatus(true);
		this.scene = scene;
	}
	
	private CoffeeSceneTemplate mainScene;
	private CoffeeSceneTemplate menuScene;

	private CoffeeSceneTemplate scene = null;
	private CoffeeAnimator anim = new CoffeeAnimator();
	private List<Integer> inputKeys = new ArrayList<>();
	private List<Integer> inputButtons = new ArrayList<>();
	private boolean liveInDreamLand = true;

	public void eventLoop() throws InterruptedException{
		manager.getRenderer().addInputListener(this);
		applyScene(mainScene);
		
		while(liveInDreamLand){
			scene.updateClock();
			scene.tick();
		}
	}
	
	@Override
	public void onGlfwFrameTick(double currentTime){
		if(scene!=null) scene.onGlfwFrameTick(currentTime);
	}
	@Override
	public void onGlfwFrameTick(float tickTime){
		if(scene!=null) scene.onGlfwFrameTick(tickTime);
	}
	@Override
	public List<Integer> getRegisteredKeys() {
		return inputKeys;
	}
	@Override
	public void coffeeReceiveKeyRelease(int key){
		if(scene!=null) scene.handleKeyRelease(key);
	}
	@Override
	public void coffeeReceiveMousePress(int btn){
		if(scene!=null) scene.handleMousePress(btn);
	}
	@Override
	public void coffeeReceiveMouseRelease(int btn){
		if(scene!=null) scene.handleMouseRelease(btn);
	}
	@Override
	public void coffeeReceiveMouseMove(double x,double y){
		if(scene!=null) scene.handleMouseMove(x, y);
	}
	@Override
	public void coffeeReceiveKeyPress(int key){
		switch(key){
		case GLFW.GLFW_KEY_KP_8:
			applyScene(mainScene);
			return;
		}
		if(scene!=null) scene.handleKeyPress(key);
	}
	@Override
	public boolean getMouseEvents(){return true;}
	@Override
	public void getCollisionNotification(String body1, String body2){
		scene.handleCollisions(body1, body2);
	}
	@Override
	public void onGlfwQuit(){
		System.exit(0);
	}
	
	@Override
	public List<Integer> getRegisteredMouseButtons() {
		return inputButtons;
	}
}
