package coffeeblocks.foundation;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.opengl.CoffeeRenderer;

public class CoffeeShop extends CoffeeLogicLoop{
	private String startScene = "main";
	private String currentScene = null;
	public CoffeeShop(CoffeeSceneManager manager) {
		super(manager);
		currentScene = startScene;
		mainKeys.add(GLFW.GLFW_KEY_W);
		mainKeys.add(GLFW.GLFW_KEY_A);
		mainKeys.add(GLFW.GLFW_KEY_S);
		mainKeys.add(GLFW.GLFW_KEY_D);
		mainKeys.add(GLFW.GLFW_KEY_ESCAPE);
	}
	
	private void applyScene(String sceneId){
		manager.applyScene(sceneId);
		manager.getRenderer().addCoffeeListener(this); //fordi listen av lyttere blir tømt
	}

	private double currentTime = 0;
	
	public void eventLoop(){
		applyScene(startScene);
		manager.getRenderer().addInputListener(this);
		double skyboxSwitch = currentTime+1d;
		int skyboxInt = 0;
		while(true){
			//Vi stiller opp kameraet og lyset, dette vil skje periodisk. Tidligere skjedde det ved hver oppdatering av fysikken, hvis tidsperiode var altfor variabel.
			getScene().getCamera().setCameraPos(Vector3f.add(getObject("player").getGameModel().getPosition(),getScene().getCamera().getCameraForwardVec(-5f),null));
			getScene().getLights().get(0).setPosition(getScene().getCamera().getCameraPos());
			
			if(currentTime>=skyboxSwitch){
				skyboxInt++;
				if(skyboxInt>=getScene().getObject("skybox").getGameModel().getTextureHandles().size())
					skyboxInt = 0;
				getScene().getObject("skybox").getGameModel().selectTexture = skyboxInt;
				getScene().getObject("water").getGameModel().selectTexture = skyboxInt;
				skyboxSwitch=currentTime+1d;
			}
		}
	}

	@Override
	public void onGlfwFrameTick(double currentTime){
		this.currentTime = currentTime; 
	}

	private List<Integer> mainKeys = new ArrayList<>();
	private List<Integer> secondaryKeys = new ArrayList<>();
	@Override
	public List<Integer> getRegisteredKeys() {
		// TODO Auto-generated method stub
		if(currentScene.equals("main")){
			return mainKeys;
		}else if(currentScene.equals("second")){
			return secondaryKeys;
		}
		return null;
	}
	
	private float walkingForce = 3f;
	@Override
	public void coffeeReceiveKeyPress(int key){
		if(key==GLFW.GLFW_KEY_W){
			getObject("player").getGameModel().setPositionalAcceleration(getScene().getCamera().getCameraForwardVec(walkingForce));
			getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
		}
		if(key==GLFW.GLFW_KEY_A){
			getObject("player").getGameModel().setPositionalAcceleration(getScene().getCamera().getCameraRightVec(-walkingForce));
			getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
		}
		if(key==GLFW.GLFW_KEY_S){
			getObject("player").getGameModel().setPositionalAcceleration(getScene().getCamera().getCameraForwardVec(-walkingForce));
			getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
		}
		if(key==GLFW.GLFW_KEY_D){
			getObject("player").getGameModel().setPositionalAcceleration(getScene().getCamera().getCameraRightVec(walkingForce));
			getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
		}
		/*
		if(key==GLFW_KEY_SPACE){
			manager.getObject("player").getGameModel().setPositionalAcceleration(VectorTools.vectorLimit(VectorTools.vectorMul(new Vector3f(0,1,0), 5.0f), 5.0f));
			manager.requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
		}
		if(key==GLFW_KEY_KP_0){
			manager.getObject("player").getGameModel().setPosition(new Vector3f(0,15,0));
		}
		if(key==GLFW_KEY_KP_5){
			manager.getObject("skybox").getGameModel().selectTexture = 0;
		}
		if(key==GLFW_KEY_KP_6){
			manager.getObject("skybox").getGameModel().selectTexture = 1;
		}
		*/
	}
	@Override
	public void onGlfwQuit(){
		System.exit(0);
	}
	private CoffeeGameObjectManager getScene(){
		return manager.getScene(currentScene);
	}
	private GameObject getObject(String object){
		return getScene().getObject(object);
	}
}
