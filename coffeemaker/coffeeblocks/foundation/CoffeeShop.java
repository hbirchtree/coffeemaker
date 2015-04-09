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
		mainKeys.add(GLFW.GLFW_KEY_SPACE);
		mainKeys.add(GLFW.GLFW_KEY_ESCAPE);
	}
	
	private void applyScene(String sceneId){
		manager.applyScene(sceneId);
		manager.getRenderer().addCoffeeListener(this); //fordi listen av lyttere blir tømt
		manager.getScene(sceneId).getPhysicsSystem().addCollisionListener(this);
	}
	
	private long clock = 0l;
	
	public void eventLoop(){
		applyScene(startScene);
		manager.getRenderer().addInputListener(this);
		
		getObject("water").getGameData().setTimerValue("switch",0l);
		getObject("skybox").getGameData().setTimerValue("switch",0l);
		getObject("water").getGameData().setIntValue("texture",0);
		getObject("player").getGameData().setDoubleValue("walk-pace",3d);
		getObject("player").getGameData().setBoolValue("can-jump",false);
		getObject("skybox").getGameData().setIntValue("texture",0);
		
		while(true){
			clock = System.currentTimeMillis();
			//Vi stiller opp kameraet og lyset, dette vil skje periodisk. Tidligere skjedde det ved hver oppdatering av fysikken, hvis tidsperiode var altfor variabel.
			getScene().getCamera().setCameraPos(
					Vector3f.add(getObject("player").getGameModel().getPosition(),
							getScene().getCamera().getCameraForwardVec(-5f),null));
			getScene().getLights().get(0).setPosition(
					getScene().getCamera().getCameraPos());
			
			if(getObject("water").getGameData().getTimerValue("switch")==null||
					clock>=getObject("water").getGameData().getTimerValue("switch")){
				
				getObject("water").getGameData().setIntValue(
						"texture",
						getObject("water").getGameData().getIntValue("texture")+1);
				
				if(getObject("water").getGameData().getIntValue("texture")>1)
					getObject("water").getGameData().setIntValue("texture",0);
				getScene().getObject("water").getGameModel().selectTexture = getObject("water").getGameData().getIntValue("texture");
				getObject("water").getGameData().setTimerValue("switch",clock+200);
			}
			if(getObject("skybox").getGameData().getTimerValue("switch")==null||
					clock>=getObject("skybox").getGameData().getTimerValue("switch")){
				getObject("skybox").getGameData().setIntValue(
						"texture",
						getObject("skybox").getGameData().getIntValue("texture")+1);
				
				if(getObject("skybox").getGameData().getIntValue("texture")>1)
					getObject("skybox").getGameData().setIntValue("texture",0);
				getScene().getObject("skybox").getGameModel().selectTexture = getObject("skybox").getGameData().getIntValue("texture");
				getObject("skybox").getGameData().setTimerValue("switch",clock+700);
			}
			if(getObject("player").getGameData().getBoolValue("can-jump")&&clock>getObject("player").getGameData().getTimerValue("jump-to")){
				getObject("player").getGameData().setBoolValue("can-jump",false);
			}
		}
	}

	@Override
	public void onGlfwFrameTick(double currentTime){
//		timers.put("clock", currentTime); 
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
	
	@Override
	public void coffeeReceiveKeyPress(int key){
		if(currentScene.equals("main")){
			if(key==GLFW.GLFW_KEY_W){
				getObject("player").getGameModel().setPositionalAcceleration(getScene().getCamera().
						getCameraForwardVec(
								getObject("player").getGameData().getDoubleValue("walk-pace").floatValue()));
				
				getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
			}
			if(key==GLFW.GLFW_KEY_A){
				getObject("player").getGameModel().setPositionalAcceleration(getScene().getCamera().
						getCameraRightVec(
								-getObject("player").getGameData().getDoubleValue("walk-pace").floatValue()));
				
				getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
			}
			if(key==GLFW.GLFW_KEY_S){
				getObject("player").getGameModel().setPositionalAcceleration(getScene().getCamera().
						getCameraForwardVec(
								-getObject("player").getGameData().getDoubleValue("walk-pace").floatValue()));
				
				getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
			}
			if(key==GLFW.GLFW_KEY_D){
				getObject("player").getGameModel().setPositionalAcceleration(getScene().getCamera().
						getCameraRightVec(
								getObject("player").getGameData().getDoubleValue("walk-pace").floatValue()));
				
				getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
			}
			if(key==GLFW.GLFW_KEY_SPACE&&getObject("player").getGameData().getBoolValue("can-jump")){
				getObject("player").getGameModel().setImpulse(new Vector3f(0,0.3f,0));
				getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_IMPULSE);
			}
		}else if(currentScene.equals("second")){
			
		}

		if(key==GLFW.GLFW_KEY_ESCAPE){
			manager.getRenderer().requestClose();
		}
		if(key==GLFW.GLFW_KEY_KP_0){
			getObject("player").getGameModel().setPosition(new Vector3f(0,15,0));
		}
	}
	@Override
	public void getCollisionNotification(String body1, String body2){
		if(doBodiesCollide(body1,body2,"player","death")){
			getObject("player").getGameModel().setPosition(new Vector3f(0,15,0));
			getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_POS);
		}else if(doBodiesCollide(body1,body2,"player","terrain")){
			getObject("player").getGameData().setBoolValue("can-jump", true);
			getObject("player").getGameData().setTimerValue("jump-to", clock+150);
		}
	}
	private boolean doBodiesCollide(String body1,String body2, String target1,String target2){
		//Vi vet ikke hvilken rekkefølge objektene blir listet
		return (body1.equals(target1)&&body2.equals(target2))||(body2.equals(target2)&&body1.equals(target1));
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
