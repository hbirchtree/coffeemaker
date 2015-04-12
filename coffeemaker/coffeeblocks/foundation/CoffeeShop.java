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
		manager.getRenderer().addSounds(getScene()); //Yes, sound in the renderer.
		manager.getRenderer().addCoffeeListener(this); //fordi listen av lyttere blir tømt
//		manager.getRenderer().addCoffeeListener(poser);
		manager.getScene(sceneId).getPhysicsSystem().addCollisionListener(this);
		currentScene = sceneId;
		
		getObject("water").getGameData().setTimerValue("switch",0l);
		getObject("skybox").getGameData().setTimerValue("switch",0l);
		getObject("water").getGameData().setIntValue("texture",0);
		getObject("player").getGameData().setDoubleValue("walk-pace",12d);
		getObject("player").getGameModel().setObjectDeactivation(false);
		getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACTIVATION,null);
		getObject("player").getGameData().setBoolValue("can-jump",false);
		getObject("skybox").getGameData().setIntValue("texture",0);
		getObject("player").getGameData().setBoolValue("ani.running.w",false);
		getObject("player").getGameData().setBoolValue("ani.running.a",false);
		getObject("player").getGameData().setBoolValue("ani.running.s",false);
		getObject("player").getGameData().setBoolValue("ani.running.d",false);
		getObject("player").getGameData().setBoolValue("ani.jumping",false);
		getObject("player").getGameData().setTimerValue("ani.runcycle", 0l);
	}
	
	private long clock = 0l;
	private boolean fpsMode = false;
	
	private void drawHud(){
		getScene().billboard("sun", true);
		getObject("sun").getGameModel().getPosition().setValue(
				Vector3f.add(Vector3f.add(
							getScene().getCamera().getCameraPos().getValue(),
							getScene().getCamera().getCameraRightVec(-2f),null),
						getScene().getCamera().getCameraForwardVec(1.0f*getScene().getCamera().getFieldOfView()/85),null));
	}
	CoffeeAnimator anim = new CoffeeAnimator();
	
	public void eventLoop(){
		applyScene(currentScene);
		manager.getRenderer().addInputListener(this);
		getScene().getCamera().getCameraPos().bindValue(getObject("player").getGameModel().getPosition());
		getScene().getLights().get(0).getPosition().bindValue(getScene().getCamera().getCameraPos());
		
		while(true){
			clock = System.currentTimeMillis();
			//Kameraets posisjon relativt til spilleren endrer seg, derfor må vi endre offset for kameraets posisjon for å holde det i bane rundt spilleren.
			getScene().getCamera().getCameraPos().setValueOffset(Vector3f.add(
					VectorTools.vectorMul(getScene().getCamera().getUp(),0.8f),getScene().getCamera().getCameraForwardVec(-5f),null));
//			drawHud();
			//Miljø
			if(getObject("water").getGameData().getTimerValue("switch")==null||
					clock>=getObject("water").getGameData().getTimerValue("switch")){
				getObject("testbox").getGameModel().getRotation().setValue(new Vector3f());
				anim.addTransition(getObject("testbox").getGameModel().getRotation(),
						new Vector3f(0,360f,0), CoffeeAnimator.TransitionType.ValueExpo, 1000f);
				
				getObject("water").getGameData().setIntValue(
						"texture",
						getObject("water").getGameData().getIntValue("texture")+1);
				
				if(getObject("water").getGameData().getIntValue("texture")>1)
					getObject("water").getGameData().setIntValue("texture",0);
				getScene().getObject("water").getGameModel().getMaterial().selectTexture = getObject("water").getGameData().getIntValue("texture");
				getObject("water").getGameData().setTimerValue("switch",clock+400);
			}
			
			//Spillervariabler
			if(getObject("player").getGameData().getBoolValue("can-jump")&&clock>getObject("player").getGameData().getTimerValue("jump-to")){
				getObject("player").getGameData().setBoolValue("can-jump",false);
			}
			
			//Animasjon
			if(getObject("player").getGameData().getBoolValue("ani.running.w")||
					getObject("player").getGameData().getBoolValue("ani.running.a")||
					getObject("player").getGameData().getBoolValue("ani.running.s")||
					getObject("player").getGameData().getBoolValue("ani.running.d")){
				if(clock%800<400)
					getObject("player").getGameModel().getAnimationContainer().setAnimationState("run.1", 0.01f);
				else
					getObject("player").getGameModel().getAnimationContainer().setAnimationState("run.2", 0.01f);
			}
			if(getObject("player").getGameData().getBoolValue("ani.jumping")) //Hopping prioriteres over gå-animasjonen
				getObject("player").getGameModel().getAnimationContainer().setAnimationState("jump", 0.01f);
//			poser.updateObject("player");
			getObject("player").getGameModel().getAnimationContainer().morphToState();
			getObject("player").getGameModel().getAnimationContainer().setAnimationState(null);;
		}
	}

	@Override
	public void onGlfwFrameTick(double currentTime){
		//Denne operasjonen er lagt i render-loopen for å kjøre på et tidspunkt hvor fysikk-systemet ikke har tick.
		//Dersom fysikk-tick skjer ved samme tidspunkt som denne operasjonen vil det føre til en rekke ulike feil avhengig av hvor den er i tick'et og forårsake en krasj.
		//Denne løsningen er mest elegant, ettersom synkronisering av trådene ville være en altfor dyr og unødvendig operasjon.
		if(getScene().getPhysicsSystem().performRaytestHeight("player")<=0.1f){
			getObject("player").getGameData().setBoolValue("ani.jumping",false);
		}else
			getObject("player").getGameData().setBoolValue("ani.jumping",true);
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
	public void coffeeReceiveKeyRelease(int key){
		if(currentScene.equals("main")){
			switch(key){
			case GLFW.GLFW_KEY_W:{
				getObject("player").getGameData().setBoolValue("ani.running.w", false);
				return;
			}
			case GLFW.GLFW_KEY_A:{
				getObject("player").getGameData().setBoolValue("ani.running.a", false);
				return;
			}
			case GLFW.GLFW_KEY_S:{
				getObject("player").getGameData().setBoolValue("ani.running.s", false);
				return;
			}
			case GLFW.GLFW_KEY_D:{
				getObject("player").getGameData().setBoolValue("ani.running.d", false);
				return;
			}
			}
			
		}
	}
	
	@Override
	public void coffeeReceiveKeyPress(int key){
		if(currentScene.equals("main")){
			switch(key){
			case GLFW.GLFW_KEY_W:{
				Vector3f accel = getScene().getCamera().
						getCameraForwardVec(
								getObject("player").getGameData().getDoubleValue("walk-pace").floatValue());
				accel.y = 0;
				getScene().billboard("player", false);
				getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL,accel);
				getObject("player").getGameData().setBoolValue("ani.running.w", true);
				return;
			}
			case GLFW.GLFW_KEY_A:{
				Vector3f accel = getScene().getCamera().
						getCameraRightVec(
								-getObject("player").getGameData().getDoubleValue("walk-pace").floatValue());
				accel.y = 0;
				getScene().billboard("player", false);
				getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL,accel);
				getObject("player").getGameData().setBoolValue("ani.running.a", true);
				return;
			}
			case GLFW.GLFW_KEY_S:{
				Vector3f accel = getScene().getCamera().
						getCameraForwardVec(
								-getObject("player").getGameData().getDoubleValue("walk-pace").floatValue());
				accel.y = 0;
				getScene().billboard("player", false);
				getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL,accel);
				getObject("player").getGameData().setBoolValue("ani.running.s", true);
				return;
			}
			case GLFW.GLFW_KEY_D:{
				Vector3f accel = getScene().getCamera().
						getCameraRightVec(
								getObject("player").getGameData().getDoubleValue("walk-pace").floatValue());
				accel.y = 0;
				getScene().billboard("player", false);
				getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL,accel);
				getObject("player").getGameData().setBoolValue("ani.running.d", true);
				return;
			}
			case GLFW.GLFW_KEY_SPACE:{
				if(!getObject("player").getGameData().getBoolValue("can-jump"))
					return;
				getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_IMPULSE,new Vector3f(0,0.5f,0));
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
			getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_POS,new Vector3f(0,15,0));
			getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_CLEARFORCE,null);
			return;
		}
		case GLFW.GLFW_KEY_KP_1:{
//			getScene().getPhysicsSystem().setGravity(VectorTools.lwjglToVMVec3f(new Vector3f(0,0,0)));
			manager.getRenderer().al_playSound("test");
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
			getScene().requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_POS,new Vector3f(0,15,0));
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
