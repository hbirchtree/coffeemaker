package coffeeblocks.foundation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.metaobjects.GameObject;

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
		manager.getScene(sceneId).getPhysicsSystem().addCollisionListener(this);
	}

	Map<String,Double> timers = new HashMap<>();
	
	public void eventLoop(){
		applyScene(startScene);
		manager.getRenderer().addInputListener(this);
		timers.put("clock", 0d);
		int skyboxInt = 0;
		while(true){
			//Vi stiller opp kameraet og lyset, dette vil skje periodisk. Tidligere skjedde det ved hver oppdatering av fysikken, hvis tidsperiode var altfor variabel.
			getScene().getCamera().setCameraPos(Vector3f.add(getObject("player").getGameModel().getPosition(),getScene().getCamera().getCameraForwardVec(-5f),null));
			getScene().getLights().get(0).setPosition(getScene().getCamera().getCameraPos());
			
			if(timers.get("water_switch")==null||timers.get("clock")>=timers.get("skybox_switch")){
				skyboxInt++;
				if(skyboxInt>=2)
					skyboxInt = 0;
				getScene().getObject("water").getGameModel().selectTexture = skyboxInt;
				timers.put("water_switch",timers.get("clock")+1d);
			}
			if(timers.get("clock")>=timers.get("skybox_switch")){
				skyboxInt++;
				if(skyboxInt>=2)
					skyboxInt = 0;
				getScene().getObject("skybox").getGameModel().selectTexture = skyboxInt;
				getScene().getObject("water").getGameModel().selectTexture = skyboxInt;
				timers.put("skybox_switch",timers.get("clock")+1d);
			}
		}
	}

	@Override
	public void onGlfwFrameTick(double currentTime){
		timers.put("clock", currentTime); 
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
		if(currentScene.equals("main")){
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
		}else if(currentScene.equals("second")){
			
		}

		if(key==GLFW.GLFW_KEY_ESCAPE){
			manager.getRenderer().requestClose();
		}
		if(key==GLFW.GLFW_KEY_KP_0){
			getObject("player").getGameModel().setPosition(new Vector3f(0,15,0));
		}
		/*
		if(key==GLFW_KEY_SPACE){
			manager.getObject("player").getGameModel().setPositionalAcceleration(VectorTools.vectorLimit(VectorTools.vectorMul(new Vector3f(0,1,0), 5.0f), 5.0f));
			manager.requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
		}
		*/
	}
	@Override
	public void getCollisionNotification(String body1, String body2){
		if(body1.equals("player")&&body2.equals("death")){
			System.out.println("Collision:"+body1+","+body2);
			getObject("player").getGameModel().setPosition(new Vector3f(0,15,0));
		}
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
