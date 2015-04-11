package coffeeblocks.foundation;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.general.VectorTools;
import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.opengl.CoffeeAnimator;

public class CoffeeShop extends CoffeeLogicLoop{
	private String currentScene = null;
	public CoffeeShop(CoffeeSceneManager manager) {
		super(manager);
		currentScene = "main";
		mainKeys.add(GLFW.GLFW_KEY_W);
		mainKeys.add(GLFW.GLFW_KEY_A);
		mainKeys.add(GLFW.GLFW_KEY_S);
		mainKeys.add(GLFW.GLFW_KEY_D);
		mainKeys.add(GLFW.GLFW_KEY_KP_0);
		mainKeys.add(GLFW.GLFW_KEY_KP_1);
		mainKeys.add(GLFW.GLFW_KEY_KP_8);
		mainKeys.add(GLFW.GLFW_KEY_KP_9);
		mainKeys.add(GLFW.GLFW_KEY_SPACE);
		mainKeys.add(GLFW.GLFW_KEY_ESCAPE);
	}
	
	private void applyScene(String sceneId){
		manager.applyScene(sceneId);
		manager.getRenderer().addCoffeeListener(this); //fordi listen av lyttere blir tømt
		manager.getScene(sceneId).getPhysicsSystem().addCollisionListener(this);
		currentScene = sceneId;
		
		getObject("water").getGameData().setTimerValue("switch",0l);
		getObject("skybox").getGameData().setTimerValue("switch",0l);
		getObject("water").getGameData().setIntValue("texture",0);
		getObject("player").getGameData().setDoubleValue("walk-pace",4d);
		getObject("player").getGameModel().setObjectDeactivation(false);
		getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACTIVATION);
		getObject("player").getGameData().setBoolValue("can-jump",false);
		getObject("skybox").getGameData().setIntValue("texture",0);
	}
	
	private long clock = 0l;
	private boolean fpsMode = false;
	
	private void drawHud(){
		getScene().billboard("sun", true);
		getObject("sun").getGameModel().getPosition().setValue(
				Vector3f.add(Vector3f.add(
							getScene().getCamera().getCameraPos(),
							getScene().getCamera().getCameraRightVec(-2f),null),
						getScene().getCamera().getCameraForwardVec(1.0f*getScene().getCamera().getFieldOfView()/85),null));
	}
	CoffeeAnimator anim = new CoffeeAnimator();
	
	public void eventLoop(){
		long raytest = clock+100;
		applyScene(currentScene);
		manager.getRenderer().addInputListener(this);
		getScene().getCamera().bindCameraPos(getObject("player").getGameModel().getPosition(),
						Vector3f.add(VectorTools.vectorMul(getScene().getCamera().getUp(),0.8f),getScene().getCamera().getCameraForwardVec(0.2f),null));
		
		while(true){
			clock = System.currentTimeMillis();
			//Vi stiller opp kameraet og lyset, dette vil skje periodisk. Tidligere skjedde det ved hver oppdatering av fysikken, hvis tidsperiode var altfor variabel.
//			if(fpsMode)
//				getScene().getCamera().setCameraPos(
//						Vector3f.add(getObject("player").getGameModel().getPosition().getValue(),
//								getScene().getCamera().getUp(),null));
//			else
//			getScene().getCamera().lookAt(getObject("player").getGameModel().getPosition().getValue());
//			getScene().getCamera().setCameraPos(
//					Vector3f.add(getObject("player").getGameModel().getPosition().getValue(),
//							getScene().getCamera().getCameraForwardVec(-5f),null));
			getScene().getLights().get(0).setPosition(
					getScene().getCamera().getCameraPos());
//			drawHud();
//			if(clock>=raytest){
//				if(performRaytest("player",new Vector3f(0,15,0)))
//						System.out.println("It's a hit!");
//				raytest = clock+100;
//			}
//			if(clock%1000%10==0)
//				getObject("testbox").getGameModel().getRotation().increaseValue(new Vector3f(0,0.003f,0));
			if(getObject("water").getGameData().getTimerValue("switch")==null||
					clock>=getObject("water").getGameData().getTimerValue("switch")){
				getObject("testbox").getGameModel().getRotation().setValue(new Vector3f());
				anim.addTransition(getObject("testbox").getGameModel().getRotation(), new Vector3f(0,180f,0), CoffeeAnimator.TransitionType.ValueExpo, 300f);
				
				getObject("water").getGameData().setIntValue(
						"texture",
						getObject("water").getGameData().getIntValue("texture")+1);
				
				if(getObject("water").getGameData().getIntValue("texture")>1)
					getObject("water").getGameData().setIntValue("texture",0);
				getScene().getObject("water").getGameModel().getMaterial().selectTexture = getObject("water").getGameData().getIntValue("texture");
				getObject("water").getGameData().setTimerValue("switch",clock+400);
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
	@Override
	public void onGlfwFrameTick(float tickTime){
		anim.tickTransitions(tickTime);
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
			switch(key){
			case GLFW.GLFW_KEY_W:{
				getObject("player").getGameModel().getPosition().setAcceleration(getScene().getCamera().
						getCameraForwardVec(
								getObject("player").getGameData().getDoubleValue("walk-pace").floatValue()));
				getScene().billboard("player", false);
				getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
				return;
			}
			case GLFW.GLFW_KEY_A:{
				getObject("player").getGameModel().getPosition().setAcceleration(getScene().getCamera().
						getCameraRightVec(
								-getObject("player").getGameData().getDoubleValue("walk-pace").floatValue()));
				getScene().billboard("player", false);
				getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
				return;
			}
			case GLFW.GLFW_KEY_S:{
				getObject("player").getGameModel().getPosition().setAcceleration(getScene().getCamera().
						getCameraForwardVec(
								-getObject("player").getGameData().getDoubleValue("walk-pace").floatValue()));
				getScene().billboard("player", false);
				getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
				return;
			}
			case GLFW.GLFW_KEY_D:{
				getObject("player").getGameModel().getPosition().setAcceleration(getScene().getCamera().
						getCameraRightVec(
								getObject("player").getGameData().getDoubleValue("walk-pace").floatValue()));
				getScene().billboard("player", false);
				getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
				return;
			}
			case GLFW.GLFW_KEY_SPACE:{
				if(!getObject("player").getGameData().getBoolValue("can-jump"))
					return;
				getObject("player").getGameModel().setImpulse(new Vector3f(0,0.5f,0));
				getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_IMPULSE);
				return;
			}
			}
			
		}else if(currentScene.equals("second")){
			
		}

		switch(key){
		case GLFW.GLFW_KEY_ESCAPE:{
			manager.getRenderer().requestClose();
			return;
		}
		case GLFW.GLFW_KEY_KP_0:{
			getObject("player").getGameModel().getPosition().setValue(new Vector3f(0,15,0));
			getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_POS);
			getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_CLEARFORCE);
			return;
		}
		case GLFW.GLFW_KEY_KP_1:{
			getScene().getPhysicsSystem().setGravity(VectorTools.lwjglToVMVec3f(new Vector3f(0,0,0)));
			return;
		}
		case GLFW.GLFW_KEY_KP_9:{
			applyScene("main");
			return;
		}
		case GLFW.GLFW_KEY_KP_8:{
			applyScene("second");
			return;
		}
		}
		
	}
	@Override
	public void getCollisionNotification(String body1, String body2){
		if(doBodiesCollide(body1,body2,"player","death")){
			getObject("player").getGameModel().getPosition().setValue(new Vector3f(0,15,0));
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
	private boolean performRaytest(String object,Vector3f startPoint){
		return getScene().getPhysicsSystem().performRaytest(startPoint,object);
	}
}
