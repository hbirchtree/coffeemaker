package coffeeblocks.foundation.logic;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.CoffeeGameObjectManager;
import coffeeblocks.foundation.CoffeeSceneManager;
import coffeeblocks.general.VectorTools;
import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.opengl.CoffeeAnimator;

public abstract class CoffeeSceneTemplate{
	public CoffeeSceneTemplate(CoffeeSceneManager manager,CoffeeAnimator animator){
		this.manager = manager;
		this.animator = animator;
	}
	protected CoffeeSceneManager manager = null;
	protected CoffeeAnimator animator = null;
	protected Long clock = null;
	
	public void updateClock(){
		clock = System.currentTimeMillis();
	}
	
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
	protected static final String PROPERTY_TIMER_TIME_TO_DIE = "time-to-die";
	protected static final String PROPERTY_TIMER_TIME_TO_LIVE = "time-to-live";
	protected static final String PROPERTY_VECTOR_SPAWNPOSITION = "spawn-position";
	protected static final String SCENE_ID_MAIN = "main";
	protected static final String SCENE_ID_SECOND = "second";
	protected static final String ANI_RUNCYCLE = "ani.runcycle";
	protected static final String OBJECT_ID_OVERLAY = "0.overlay";
	protected static final String OBJECT_ID_TESTBOX = "1.testbox";
	protected static final String OBJECT_ID_WATER = "2.water";
	protected static final String OBJECT_ID_SKYBOX = "skybox";
	protected static final String OBJECT_ID_PLAYER = "player";
	
	abstract public String getSceneId();
	abstract public void setupSpecifics();
	abstract public void tickSpecifics();
	abstract public void handleKeyRelease(int key);
	abstract public void handleMousePress(int key);
	abstract public void handleMouseRelease(int key);
	abstract public void handleCollisions(String body1, String body2);
	abstract public void onGlfwFrameTick(double currentTime);
	
	public void cleanup(){
		manager.getRenderer().cleanupAll();
	}
	
	public void handleKeyPress(int key){
		switch(key){
		case GLFW.GLFW_KEY_ESCAPE:{
			manager.getRenderer().requestClose();
			return;
		}
		case GLFW.GLFW_KEY_KP_0:{
			playerDie();
			return;
		}
		case GLFW.GLFW_KEY_KP_1:{
			manager.getRenderer().al_playSound("test");
			return;
		}
		}
	}
	
	public void playerDie(){
		getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_TIME_TO_DIE, 0l);
		animator.addTransition(getObject(OBJECT_ID_OVERLAY).getGameModel().getMaterial().getTransparencyObject(), 1f, CoffeeAnimator.TransitionType.ValueLinear, 300f);
		getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_TIME_TO_LIVE, clock+1000);
	}
	public void playerRespawn(){
		getObject(OBJECT_ID_OVERLAY).getGameModel().getMaterial().getTransparencyObject().setValue(0f);
		getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_POS,getObject(OBJECT_ID_PLAYER).getGameData().getVectorValue(PROPERTY_VECTOR_SPAWNPOSITION));
		getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_CLEARFORCE,null);
		getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_TIME_TO_LIVE, 0l);
	}
	
	
	public void setupPlayer(){
		//Skjermoverlegget
		billboard(OBJECT_ID_OVERLAY, true);
		getObject(OBJECT_ID_OVERLAY).getGameModel().getPosition().bindValue(getScene().getCamera().getCameraPos());
		getObject(OBJECT_ID_OVERLAY).getGameModel().getMaterial().getTransparencyObject().setValue(0f);
		
		billboard(OBJECT_ID_PLAYER, false); //Vi vil at spilleren skal vende seg fra kameraet, dette ved å binde rotasjonen mot kameraet
		getObject(OBJECT_ID_PLAYER).getGameData().setVectorValue(PROPERTY_VECTOR_SPAWNPOSITION,
				new Vector3f(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition().getValue()));
		getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_ACTIVATION,null);
		getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_TIME_TO_DIE,0l);
		getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_TIME_TO_LIVE,0l);
		getObject(OBJECT_ID_PLAYER).getGameModel().setObjectDeactivation(false);
	}
	public void tickPlayer(){
		getObject(OBJECT_ID_OVERLAY).getGameModel().getPosition().setValueOffset(getScene().getCamera().getCameraForwardVec(0.5f));
		if(clock>=getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_DIE)&&
				getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_DIE)!=0)
			playerDie();
		if(clock>=getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_LIVE)&&
				getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_LIVE)!=0)
			playerRespawn();
	}
	
	public void onGlfwFrameTick(float tickTime){
		animator.tickTransitions(tickTime);
	}
	
	public void setupCamera(){
		getScene().getCamera().getCameraPos().bindValue(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition());
	}
	public void tickCamera(){
		//Kameraets posisjon relativt til spilleren endrer seg, derfor må vi endre offset for kameraets posisjon for å holde det i bane rundt spilleren.
		getScene().getCamera().getCameraPos().setValueOffset(Vector3f.add(
				VectorTools.vectorMul(getScene().getCamera().getUp(),0.8f),getScene().getCamera().getCameraForwardVec(-5f),null));
	}
	
	//Nyttefunksjoner
	protected boolean doBodiesCollide(String body1,String body2, String target1,String target2){
		//Vi vet ikke hvilken rekkefølge objektene blir listet
		return (body1.equals(target1)&&body2.equals(target2))||(body2.equals(target2)&&body1.equals(target1));
	}
	public CoffeeGameObjectManager getScene(){
		return manager.getScene(getSceneId());
	}
	protected GameObject getObject(String object){
		return getScene().getObject(object);
	}
	protected boolean performRaytest(String object,Vector3f startPoint){ //Skyter fra startPoint til objektet
		return getScene().getPhysicsSystem().performRaytest(startPoint,object);
	}
	protected void billboard(String objectId,boolean spherical){
		getObject(objectId).getGameModel().getRotation().bindValue(getScene().getCamera().getCameraRotation());
		if(spherical)
			getObject(objectId).getGameModel().getRotation().setValueMultiplier(new Vector3f(-1,-1,0));
		else
			getObject(objectId).getGameModel().getRotation().setValueMultiplier(new Vector3f(0,-1,0));
	}
}