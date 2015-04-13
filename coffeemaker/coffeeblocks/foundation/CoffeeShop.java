package coffeeblocks.foundation;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.general.VectorTools;
import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.opengl.CoffeeAnimator;
import coffeeblocks.opengl.components.CoffeeCamera;

public class CoffeeShop extends CoffeeLogicLoop{
	
	private static final String PROPERTY_TIMER_JUMP_TO = "jump-to";
	private static final String PROPERTY_TIMER_TIME_TO_DIE = "time-to-die";
	private static final String PROPERTY_INT_SWITCH = "switch";
	private static final String PROPERTY_INT_TEXTURE = "texture";
	private static final String PROPERTY_DUBS_WALK_PACE = "walk-pace";
	private static final String PROPERTY_BOOL_CAN_JUMP = "can-jump";
	private static final String PROPERTY_BOOL_RUN_W = "ani.running.w";
	private static final String PROPERTY_BOOL_RUN_A = "ani.running.a";
	private static final String PROPERTY_BOOL_RUN_S = "ani.running.s";
	private static final String PROPERTY_BOOL_RUN_D = "ani.running.d";
	private static final String PROPERTY_BOOL_JUMP = "ani.jumping";
	private static final String SCENE_ID_MAIN = "main";
	private static final String SCENE_ID_SECOND = "second";
	private static final String ANI_RUNCYCLE = "ani.runcycle";
	private static final String OBJECT_ID_OVERLAY = "0.overlay";
	private static final String OBJECT_ID_TESTBOX = "1.testbox";
	private static final String OBJECT_ID_WATER = "2.water";
	private static final String OBJECT_ID_SKYBOX = "skybox";
	private static final String OBJECT_ID_PLAYER = "player";
	
	private String currentScene = null;
	public CoffeeShop(CoffeeSceneManager manager) {
		super(manager);
		currentScene = SCENE_ID_MAIN;
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
		manager.getRenderer().setRendering_swaps(1);
		manager.getRenderer().setWindowres(1280, 720);
		manager.getRenderer().addCoffeeListener(this); //fordi listen av lyttere blir tømt
//		manager.getRenderer().addCoffeeListener(poser);
		manager.getScene(sceneId).getPhysicsSystem().addCollisionListener(this);
		currentScene = sceneId;
		
		getObject(OBJECT_ID_WATER).getGameData().setTimerValue(PROPERTY_INT_SWITCH,0l);
		getObject(OBJECT_ID_SKYBOX).getGameData().setTimerValue(PROPERTY_INT_SWITCH,0l);
		getObject(OBJECT_ID_WATER).getGameData().setIntValue(PROPERTY_INT_TEXTURE,0);
		getObject(OBJECT_ID_PLAYER).getGameData().setDoubleValue(PROPERTY_DUBS_WALK_PACE,12d);
		getObject(OBJECT_ID_PLAYER).getGameModel().setObjectDeactivation(false);
		getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_ACTIVATION,null);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_CAN_JUMP,false);
		getObject(OBJECT_ID_SKYBOX).getGameData().setIntValue(PROPERTY_INT_TEXTURE,0);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_W,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_A,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_S,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_D,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_JUMP,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(ANI_RUNCYCLE, 0l);
		getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_TIME_TO_DIE,0l);
	}
	
	private long clock = 0l;
	
	private void drawHud(){
		billboard("sun", true);
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
		
		//Skjermoverlegget
		getObject(OBJECT_ID_OVERLAY).getGameModel().getPosition().bindValue(getScene().getCamera().getCameraPos());
		billboard(OBJECT_ID_OVERLAY, true);
		getObject(OBJECT_ID_OVERLAY).getGameModel().getMaterial().getTransparencyObject().setValue(0f);
		
		//Kameraet
		getScene().getCamera().getCameraPos().bindValue(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition());
		
		//Belysning
//		getScene().getLights().get(0).getPosition().bindValue(getScene().getCamera().getCameraPos());
		
		//Spilleren
		manager.getRenderer().getAlListenPosition().bindValue(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition());
		getObject(OBJECT_ID_PLAYER).getSoundBox().get(0).getPosition().setValue(new Vector3f(25,0,0));
		billboard(OBJECT_ID_PLAYER, false);
		
		while(true){
			clock = System.currentTimeMillis();
			//Kameraets posisjon relativt til spilleren endrer seg, derfor må vi endre offset for kameraets posisjon for å holde det i bane rundt spilleren.
			getScene().getCamera().getCameraPos().setValueOffset(Vector3f.add(
					VectorTools.vectorMul(getScene().getCamera().getUp(),0.8f),getScene().getCamera().getCameraForwardVec(-5f),null));
			getObject(OBJECT_ID_OVERLAY).getGameModel().getPosition().setValueOffset(getScene().getCamera().getCameraForwardVec(0.5f));
			
//			drawHud();
			//Miljø
			if(getObject(OBJECT_ID_WATER).getGameData().getTimerValue(PROPERTY_INT_SWITCH)==null||
					clock>=getObject(OBJECT_ID_WATER).getGameData().getTimerValue(PROPERTY_INT_SWITCH)){
				getObject(OBJECT_ID_TESTBOX).getGameModel().getRotation().setValue(new Vector3f());
				anim.addTransition(getObject(OBJECT_ID_TESTBOX).getGameModel().getRotation(),
						new Vector3f(0,360f,0), CoffeeAnimator.TransitionType.ValueLinear, 1200f);
				
				getObject(OBJECT_ID_WATER).getGameData().setIntValue(
						PROPERTY_INT_TEXTURE,
						getObject(OBJECT_ID_WATER).getGameData().getIntValue(PROPERTY_INT_TEXTURE)+1);
				
				if(getObject(OBJECT_ID_WATER).getGameData().getIntValue(PROPERTY_INT_TEXTURE)>1)
					getObject(OBJECT_ID_WATER).getGameData().setIntValue(PROPERTY_INT_TEXTURE,0);
				getScene().getObject(OBJECT_ID_WATER).getGameModel().getMaterial().selectTexture = getObject(OBJECT_ID_WATER).getGameData().getIntValue(PROPERTY_INT_TEXTURE);
				getObject(OBJECT_ID_WATER).getGameData().setTimerValue(PROPERTY_INT_SWITCH,clock+400);
			}
			
			//Spillervariabler
			if(getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_CAN_JUMP)&&clock>getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_JUMP_TO)){
				getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_CAN_JUMP,false);
			}
			if(clock>=getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_DIE)&&getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_DIE)!=0){
				playerDie();
			}
			
			//Animasjon
			if(getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_RUN_W)||
					getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_RUN_A)||
					getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_RUN_S)||
					getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_RUN_D)){
				if(clock%800<400)
					getObject(OBJECT_ID_PLAYER).getGameModel().getAnimationContainer().setAnimationState("run.1", 0.01f);
				else
					getObject(OBJECT_ID_PLAYER).getGameModel().getAnimationContainer().setAnimationState("run.2", 0.01f);
			}else if(!getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_JUMP))
				//Uten dette vil spilleren gli overalt, hvilket er ekstremt plagsomt.
				//Bi-effekten er at spilleren har en stor massetreghet som om den var uendelig tung, og kan derfor ikke dyttes av andre objekter.
				getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_CLEARFORCE,null); 
			if(getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_JUMP)) //Hopping prioriteres over gå-animasjonen
				getObject(OBJECT_ID_PLAYER).getGameModel().getAnimationContainer().setAnimationState("jump", 0.01f);
//			poser.updateObject(OBJECT_ID_PLAYER);
			getObject(OBJECT_ID_PLAYER).getGameModel().getAnimationContainer().morphToState();
			getObject(OBJECT_ID_PLAYER).getGameModel().getAnimationContainer().setAnimationState(null);
		}
	}

	@Override
	public void onGlfwFrameTick(double currentTime){
		//Denne operasjonen er lagt i render-loopen for å kjøre på et tidspunkt hvor fysikk-systemet ikke har tick.
		//Dersom fysikk-tick skjer ved samme tidspunkt som denne operasjonen vil det føre til en rekke ulike feil avhengig av hvor den er i tick'et og forårsake en krasj.
		//Denne løsningen er mest elegant, ettersom synkronisering av trådene ville være en altfor dyr og unødvendig operasjon.
		if(getScene().getPhysicsSystem().performRaytestHeight(OBJECT_ID_PLAYER)<=0.1f){
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_JUMP,false);
		}else{
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_JUMP,true);
		}
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
		if(currentScene.equals(SCENE_ID_MAIN)){
			return mainKeys;
		}else if(currentScene.equals(SCENE_ID_SECOND)){
			return secondaryKeys;
		}
		return null;
	}
	
	@Override
	public void coffeeReceiveKeyRelease(int key){
		if(currentScene.equals(SCENE_ID_MAIN)){
			switch(key){
			case GLFW.GLFW_KEY_W:{
				getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_W, false);
				return;
			}
			case GLFW.GLFW_KEY_A:{
				getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_A, false);
				return;
			}
			case GLFW.GLFW_KEY_S:{
				getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_S, false);
				return;
			}
			case GLFW.GLFW_KEY_D:{
				getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_D, false);
				return;
			}
			}
			
		}
	}
	
	@Override
	public void coffeeReceiveKeyPress(int key){
		if(currentScene.equals(SCENE_ID_MAIN)){
			switch(key){
			case GLFW.GLFW_KEY_W:{
				Vector3f accel = getScene().getCamera().
						getCameraForwardVec(
								getObject(OBJECT_ID_PLAYER).getGameData().getDoubleValue(PROPERTY_DUBS_WALK_PACE).floatValue());
				accel.y = 0;
				getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_ACCEL,accel);
				getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_W, true);
				return;
			}
			case GLFW.GLFW_KEY_A:{
				Vector3f accel = getScene().getCamera().
						getCameraRightVec(
								-getObject(OBJECT_ID_PLAYER).getGameData().getDoubleValue(PROPERTY_DUBS_WALK_PACE).floatValue());
				accel.y = 0;
				getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_ACCEL,accel);
				getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_A, true);
				return;
			}
			case GLFW.GLFW_KEY_S:{
				Vector3f accel = getScene().getCamera().
						getCameraForwardVec(
								-getObject(OBJECT_ID_PLAYER).getGameData().getDoubleValue(PROPERTY_DUBS_WALK_PACE).floatValue());
				accel.y = 0;
				getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_ACCEL,accel);
				getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_S, true);
				return;
			}
			case GLFW.GLFW_KEY_D:{
				Vector3f accel = getScene().getCamera().
						getCameraRightVec(
								getObject(OBJECT_ID_PLAYER).getGameData().getDoubleValue(PROPERTY_DUBS_WALK_PACE).floatValue());
				accel.y = 0;
				getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_ACCEL,accel);
				getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_D, true);
				return;
			}
			case GLFW.GLFW_KEY_SPACE:{
				if(!getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_CAN_JUMP))
					return;
				getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_IMPULSE,new Vector3f(0,0.5f,0));
				return;
			}
			}
			
		}else if(currentScene.equals(SCENE_ID_SECOND)){
			
		}

		switch(key){
		case GLFW.GLFW_KEY_ESCAPE:{
			manager.getRenderer().requestClose();
			return;
		}
		case GLFW.GLFW_KEY_KP_0:{
			getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_POS,new Vector3f(0,15,0));
			getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_CLEARFORCE,null);
			return;
		}
		case GLFW.GLFW_KEY_KP_1:{
//			getScene().getPhysicsSystem().setGravity(VectorTools.lwjglToVMVec3f(new Vector3f(0,0,0)));
			manager.getRenderer().al_playSound("test");
			return;
		}
		case GLFW.GLFW_KEY_KP_9:{
			manager.getRenderer().al_playSound("test2");
			return;
		}
//		case GLFW.GLFW_KEY_KP_8:{
//			applyScene("second");
//			return;
//		}
		}
		
	}
	@Override
	public void getCollisionNotification(String body1, String body2){
		if(doBodiesCollide(body1,body2,OBJECT_ID_PLAYER,"death")){
			if(clock>=getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_DIE)) //Slik at tiden ikke utvider seg uendelig. Hvis dødstimeren allerede er aktivert vil den ikke sette den på nytt.
				getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_TIME_TO_DIE, clock+1000);
		}else if(doBodiesCollide(body1,body2,OBJECT_ID_PLAYER,"terrain")||doBodiesCollide(body1,body2,OBJECT_ID_PLAYER,"terrain.walkway")){
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_CAN_JUMP, true);
			getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_JUMP_TO, clock+150);
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
	private boolean performRaytest(String object,Vector3f startPoint){ //Skyter fra startPoint til objektet
		return getScene().getPhysicsSystem().performRaytest(startPoint,object);
	}
	
	public void playerDie(){
		getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_TIME_TO_DIE, 0l);
		System.out.println("BLARGHH!!!");
		getObject(OBJECT_ID_OVERLAY).getGameModel().getMaterial().getTransparencyObject().setValue(0f);
		anim.addTransition(getObject(OBJECT_ID_PLAYER).getGameModel().getMaterial().getTransparencyObject(), 1f, CoffeeAnimator.TransitionType.ValueLinear, 300f);
	}

	public void billboard(String objectId,boolean spherical){
		getObject(objectId).getGameModel().getRotation().bindValue(getScene().getCamera().getCameraRotation());
		if(spherical)
			getObject(objectId).getGameModel().getRotation().setValueMultiplier(new Vector3f(-1,-1,0));
		else
			getObject(objectId).getGameModel().getRotation().setValueMultiplier(new Vector3f(0,-1,0));
		
		
	}
}
