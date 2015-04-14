package coffeeblocks.foundation.logic;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.CoffeeSceneManager;
import coffeeblocks.general.VectorTools;
import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.opengl.CoffeeAnimator;

public class CoffeeMainScene extends CoffeeSceneTemplate {
	
	protected static final String PROPERTY_BOOL_CAN_JUMP = "can-jump";
	protected static final String PROPERTY_BOOL_RUN_W = "ani.running.w";
	protected static final String PROPERTY_BOOL_RUN_A = "ani.running.a";
	protected static final String PROPERTY_BOOL_RUN_S = "ani.running.s";
	protected static final String PROPERTY_BOOL_RUN_D = "ani.running.d";
	protected static final String PROPERTY_BOOL_JUMP = "ani.jumping";
	protected static final String PROPERTY_DUBS_WALK_PACE = "walk-pace";
	protected static final String PROPERTY_INT_TEXTURE = "texture";
	protected static final String PROPERTY_TIMER_SWITCH = "switch";
	protected static final String PROPERTY_TIMER_JUMP_TO = "jump-to";
	protected static final String PROPERTY_TIMER_EXPIRY = "expire-time";
	protected static final String SCENE_ID_MAIN = "main";
	protected static final String ANI_RUNCYCLE = "ani.runcycle";
	protected static final String OBJECT_ID_TESTBOX = "1.testbox";
	protected static final String OBJECT_ID_WATER = "2.water";
	protected static final String OBJECT_ID_SKYBOX = "skybox";
	
	public CoffeeMainScene(CoffeeSceneManager manager, CoffeeAnimator animator) {
		super(manager, animator);
	}
	public String getSceneId(){
		return SCENE_ID_MAIN;
	}
	@Override
	protected void setupSpecifics() {
		getObject(OBJECT_ID_WATER).getGameData().setTimerValue(PROPERTY_TIMER_SWITCH,0l);
		getObject(OBJECT_ID_WATER).getGameData().setIntValue(PROPERTY_INT_TEXTURE,0);

		getObject(OBJECT_ID_SKYBOX).getGameData().setTimerValue(PROPERTY_TIMER_SWITCH,0l);
		getObject(OBJECT_ID_SKYBOX).getGameData().setIntValue(PROPERTY_INT_TEXTURE,0);
	}
	@Override
	protected void tickSpecifics() {
		super.tickSpecifics();
		if(!isReady())
			setupSpecifics();
		//Miljø
		if(getObject(OBJECT_ID_WATER).getGameData().getTimerValue(PROPERTY_TIMER_SWITCH)==null||
				clock>=getObject(OBJECT_ID_WATER).getGameData().getTimerValue(PROPERTY_TIMER_SWITCH)){
			getObject(OBJECT_ID_TESTBOX).getGameModel().getRotation().setValue(new Vector3f());
			animator.addTransition(getObject(OBJECT_ID_TESTBOX).getGameModel().getRotation(),
					new Vector3f(0,360f,0), CoffeeAnimator.TransitionType.ValueLinear, 1200f);

			getObject(OBJECT_ID_WATER).getGameData().setIntValue(
					PROPERTY_INT_TEXTURE,
					getObject(OBJECT_ID_WATER).getGameData().getIntValue(PROPERTY_INT_TEXTURE)+1);

			if(getObject(OBJECT_ID_WATER).getGameData().getIntValue(PROPERTY_INT_TEXTURE)>1)
				getObject(OBJECT_ID_WATER).getGameData().setIntValue(PROPERTY_INT_TEXTURE,0);
			getScene().getObject(OBJECT_ID_WATER).getGameModel().getMaterial().selectTexture = 
					getObject(OBJECT_ID_WATER).getGameData().getIntValue(PROPERTY_INT_TEXTURE);
			getObject(OBJECT_ID_WATER).getGameData().setTimerValue(PROPERTY_TIMER_SWITCH,clock+400);
			
//			spawnBullet();
		}
		
		//Vi sletter prosjektiler som er gamle
//		for(GameObject object : new ArrayList<>(getScene().getObjectList()))
//			if(object.getGameData().getTimerValue(PROPERTY_TIMER_EXPIRY)!=null&&
//			clock>=object.getGameData().getTimerValue(PROPERTY_TIMER_EXPIRY)){
//				getScene().deleteInstance(object.getObjectId());
//			}
	}
	@Override
	protected void setupPlayer() {
		super.setupPlayer();
		manager.getRenderer().getAlListenPosition().bindValue(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition());
		getObject(OBJECT_ID_PLAYER).getSoundBox().get(0).getPosition().setValue(new Vector3f(25,0,0));
		
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_CAN_JUMP,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_W,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_A,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_S,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_D,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_JUMP,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setDoubleValue(PROPERTY_DUBS_WALK_PACE,12d);
		getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(ANI_RUNCYCLE, 0l);
	}
	@Override
	protected void tickPlayer() {
		if(!isReady())
			setupPlayer();
		super.tickPlayer();
		//Spillervariabler
		if(getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_CAN_JUMP)&&
				clock>getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_JUMP_TO)){
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_CAN_JUMP,false);
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
		getObject(OBJECT_ID_PLAYER).getGameModel().getAnimationContainer().morphToState();
		getObject(OBJECT_ID_PLAYER).getGameModel().getAnimationContainer().setAnimationState(null); //tilbakestiller modellen for å unngå at animasjoner gjentas unødvendig
	}
	@Override
	public void onGlfwFrameTick(double currentTime) {
		//Denne operasjonen er lagt i render-loopen for å kjøre på et tidspunkt hvor fysikk-systemet ikke har tick.
		//Dersom fysikk-tick skjer ved samme tidspunkt som denne operasjonen vil det føre til en rekke ulike feil avhengig av hvor den er i tick'et og forårsake en krasj.
		//Denne løsningen er mest elegant, ettersom synkronisering av trådene ville være en altfor dyr og unødvendig operasjon.
		if(getScene().getPhysicsSystem().performRaytestHeight(OBJECT_ID_PLAYER)<=0.1f)
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_JUMP,false);
		else
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_JUMP,true);
	}
	@Override
	public void handleKeyPress(int key) {
		super.handleKeyPress(key);
		switch(key){
		case GLFW.GLFW_KEY_W:{
			Vector3f accel = getScene().getCamera().
					getCameraForwardVec(1f);
			accel.y = 0;
			accel.normalise();
			accel = VectorTools.vectorMul(accel, 
					getObject(OBJECT_ID_PLAYER).getGameData().getDoubleValue(PROPERTY_DUBS_WALK_PACE).floatValue());
			System.out.println("Got it");
			getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_ACCEL,accel);
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_W, true);
			return;
		}
		case GLFW.GLFW_KEY_A:{
			Vector3f accel = getScene().getCamera().
					getCameraRightVec(1f);
			accel.y = 0;
			accel.normalise();
			accel = VectorTools.vectorMul(accel, 
					-getObject(OBJECT_ID_PLAYER).getGameData().getDoubleValue(PROPERTY_DUBS_WALK_PACE).floatValue());
			getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_ACCEL,accel);
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_A, true);
			return;
		}
		case GLFW.GLFW_KEY_S:{
			Vector3f accel = getScene().getCamera().
					getCameraForwardVec(1f);
			accel.y = 0;
			accel.normalise();
			accel = VectorTools.vectorMul(accel, 
					-getObject(OBJECT_ID_PLAYER).getGameData().getDoubleValue(PROPERTY_DUBS_WALK_PACE).floatValue());
			getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_ACCEL,accel);
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_S, true);
			return;
		}
		case GLFW.GLFW_KEY_D:{
			Vector3f accel = getScene().getCamera().
					getCameraRightVec(1f);
			accel.y = 0;
			accel.normalise();
			accel = VectorTools.vectorMul(accel, 
					getObject(OBJECT_ID_PLAYER).getGameData().getDoubleValue(PROPERTY_DUBS_WALK_PACE).floatValue());
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
	}
	@Override
	public void handleKeyRelease(int key) {
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
	@Override
	public void handleMousePress(int key) {
		if(key==GLFW.GLFW_MOUSE_BUTTON_1){
			//Hvis flere objekter opprettes i samme millisekund vil den ene bli overskrevet. Sånt skjer (ikke).
			spawnBullet();
		}
	}
	public void spawnBullet(){
		GameObject obj = getScene().getInstantiable("testbox").createInstance(".testytestybox"+clock,true);
		Vector3f dir = getScene().getCamera().getCameraForwardVec(1f);
		dir.y = 0f;
		dir.normalise(); //Vi normaliserer vektoren for å få en jevn avstand fra spilleren uavhengig av vinkel kameraet kan ha
		dir = VectorTools.vectorMul(dir, 3f);
		obj.getGameModel().getPosition().setValue(Vector3f.add(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition().getValue(),dir,null));
		obj.getGameModel().getPosition().setVelocity(new Vector3f(0,1,0));
		obj.getGameModel().getRotation().setValue(new Vector3f(0,45,0));
		obj.getGameData().setTimerValue(PROPERTY_TIMER_EXPIRY, clock+500);
		getScene().addObject(obj);
	}
	@Override
	public void handleCollisions(String body1, String body2) {
		if(doBodiesCollide(body1,body2,OBJECT_ID_PLAYER,"death")){
			if(clock>=getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_DIE)&&clock>=getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_LIVE)) //Slik at tiden ikke utvider seg uendelig. Hvis dødstimeren allerede er aktivert vil den ikke sette den på nytt.
				getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_TIME_TO_DIE, clock+250);
		}else if(doBodiesCollide(body1,body2,OBJECT_ID_PLAYER,"terrain")||doBodiesCollide(body1,body2,OBJECT_ID_PLAYER,"terrain.walkway")){
			if(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition().getVelocity().y>30)
				playerDie();
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_CAN_JUMP, true);
			getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_JUMP_TO, clock+150);
		}
	}
	@Override
	public void handleMouseRelease(int key) {
		// TODO Auto-generated method stub
		
	}
}