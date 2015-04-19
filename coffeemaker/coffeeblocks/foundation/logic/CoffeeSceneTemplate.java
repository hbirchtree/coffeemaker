package coffeeblocks.foundation.logic;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.CoffeeGameObjectManager;
import coffeeblocks.foundation.CoffeeSceneManager;
import coffeeblocks.general.VectorTools;
import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.metaobjects.InstantiableObject;
import coffeeblocks.metaobjects.Vector3Container;
import coffeeblocks.opengl.CoffeeAnimator;
import coffeeblocks.opengl.components.CoffeeText;

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
	public void cleanup(){
		manager.getRenderer().cleanupAll();
	}
	protected boolean readyStatus = false;
	public void setReadyStatus(boolean readyStatus){
		this.readyStatus = readyStatus;
	}
	public boolean isReady(){
		return readyStatus;
	}

	protected float mouseSensitivity = 0.1f;
	
	protected static final String PROPERTY_TIMER_TIME_TO_DIE = "time-to-die";
	protected static final String PROPERTY_TIMER_TIME_TO_LIVE = "time-to-live";
	protected static final String PROPERTY_VECTOR_SPAWNPOSITION = "spawn-position";
	protected static final String OBJECT_ID_OVERLAY = "0.overlay";
	protected static final String OBJECT_ID_PLAYER = "player";
	protected static final String PROPERTY_INSTANCE_DELETEME = "delete-me";
	
	abstract public String getSceneId();
	public void handleMouseMove(double x, double y){
		getScene().getCamera().offsetOrientation(mouseSensitivity*(float)x, mouseSensitivity*(float)y);
		manager.getRenderer().glfwResetCursor();
	}
	abstract public void handleKeyRelease(int key);
	abstract public void handleMousePress(int key);
	abstract public void handleMouseRelease(int key);
	abstract public void handleCollisions(String body1, String body2);
	abstract public void onGlfwFrameTick(double currentTime);
	
	
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
	
	public void onGlfwFrameTick(float tickTime){
		animator.tickTransitions(tickTime);
	}
	
	public void tick(){
		tickCamera();
		tickSpecifics();
		tickPlayer();
		if(!isReady())
			readyStatus = true;
	}
	
	abstract protected void setupSpecifics();
	protected void tickSpecifics(){
		if(!isReady())
			setupSpecifics();
	}
	protected void setupPlayer(){
		//Skjermoverlegget
		billboard(OBJECT_ID_OVERLAY, true);
		getObject(OBJECT_ID_OVERLAY).getGameModel().getPosition().bindValue(getScene().getCamera().getCameraPos());
		getObject(OBJECT_ID_OVERLAY).getGameModel().getMaterial().getTransparencyObject().setValue(0f);
		
		billboard(OBJECT_ID_PLAYER, false); //Vi vil at spilleren skal vende seg fra kameraet, dette ved å binde rotasjonen mot kameraet
		getObject(OBJECT_ID_PLAYER).getGameData().setVectorValue(PROPERTY_VECTOR_SPAWNPOSITION,
				new Vector3Container(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition().getValue()));
		getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_ACTIVATION,null);
		getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_TIME_TO_DIE,0l);
		getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_TIME_TO_LIVE,0l);
		getObject(OBJECT_ID_PLAYER).getGameModel().setObjectDeactivation(false);
	}
	protected void tickPlayer(){
		if(!isReady())
			setupPlayer();
		
		//Vi oppdaterer tekstens posisjon med arbeidstråder
		sentences.stream().forEach(t -> t.updateOffsets());
		
		getObject(OBJECT_ID_OVERLAY).getGameModel().getPosition().setValueOffset(getScene().getCamera().getCameraForwardVec(0.5f));
		if(clock>=getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_DIE)&&
				getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_DIE)!=0)
			playerDie();
		if(clock>=getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_LIVE)&&
				getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_LIVE)!=0)
			playerRespawn();
	}
	protected void setupCamera(){
		getScene().getCamera().getCameraPos().bindValue(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition());
	}
	protected void tickCamera(){
		if(!isReady())
			setupCamera();
		//Kameraets posisjon relativt til spilleren endrer seg, derfor må vi endre offset for kameraets posisjon for å holde det i bane rundt spilleren.
		getScene().getCamera().getCameraPos().setValueOffset(Vector3f.add(
				VectorTools.vectorMul(getScene().getCamera().getUp(),0.8f),getScene().getCamera().getCameraForwardVec(-5f),null));
	}
	
	
	protected void playerDie(){
		getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_TIME_TO_DIE, 0l);
		animator.addTransition(getObject(OBJECT_ID_OVERLAY).getGameModel().getMaterial().getTransparencyObject(), 1f, CoffeeAnimator.TransitionType.ValueLinear, 300f);
		getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_TIME_TO_LIVE, clock+1000);
	}
	protected void playerRespawn(){
		getObject(OBJECT_ID_OVERLAY).getGameModel().getMaterial().getTransparencyObject().setValue(0f);
		getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_POS,getObject(OBJECT_ID_PLAYER).getGameData().getVectorValue(PROPERTY_VECTOR_SPAWNPOSITION).getValue());
		getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_CLEARFORCE,null);
		getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_TIME_TO_LIVE, 0l);
	}
	
	//Nyttefunksjoner
	protected boolean doBodiesCollide(String body1,String body2, String target1,String target2){
		//Vi vet ikke hvilken rekkefølge objektene blir listet
		return (body1.equals(target1)&&body2.equals(target2))||(body2.equals(target2)&&body1.equals(target1));
	}
	protected boolean doTypedBodiesCollide(String body1, String body2, String identifier1, String identifier2){
		return (body1.startsWith(identifier1)&&body2.startsWith(identifier2))||(body2.startsWith(identifier2)&&body1.startsWith(identifier1));
	}
	protected String choosePrefixed(String prefix, String... opts){
		for(String opt : opts)
			if(opt.startsWith(prefix))
					return opt;
		return null;
	}
	protected CoffeeGameObjectManager getScene(){
		return manager.getScene(getSceneId());
	}
	protected GameObject getObject(String object){
		return getScene().getObject(object);
	}
	protected GameObject getAnyObject(String object){
		return getScene().getAnyObject(object);
	}
	protected boolean performRaytest(String object,Vector3f startPoint){ //Skyter fra startPoint til objektet
		return getScene().getPhysicsSystem().performRaytest(startPoint,object);
	}
	protected float pingDistance(String from,String to){ //Skyter fra startPoint til objektet
		return getScene().getPhysicsSystem().performRaytestDistance(from, to);
	}
	protected float pingDistance(String from,Vector3f to){ //Skyter fra startPoint til objektet
		return getScene().getPhysicsSystem().performRaytestDistancePoint(from, VectorTools.lwjglToVMVec3f(to));
	}
	protected boolean logic_objectCanSeeOtherInRange(String from, String to,float range){
		if(from==null||to==null)
			return false;
		float distance = pingDistance(from,to);
		if(distance>range)
			return false;
		if(distance==Float.NaN)
			return false;
		return true;
	}
	protected boolean logic_objectCanSeeOther(String from, String to){
		GameObject fromO = getObject(from);
		if(fromO==null)
			getScene().getInstancedObject(to);
		if(fromO==null)
			return false;
		return performRaytest(to,fromO.getGameModel().getPosition().getValue());
	}
	protected void billboard(String objectId,boolean spherical){
		getAnyObject(objectId).getGameModel().getRotation().bindValue(getScene().getCamera().getCameraRotation());
		if(spherical)
			getAnyObject(objectId).getGameModel().getRotation().setValueMultiplier(new Vector3f(-1,-1,0));
		else
			getAnyObject(objectId).getGameModel().getRotation().setValueMultiplier(new Vector3f(0,-1,0));
	}
	

	protected class CoffeeTextStruct {
		private Vector3Container targetPos = new Vector3Container();
		private Vector3Container targetRot = new Vector3Container();
		public float textSpacing = 1.5f;
		private Vector3Container targetScl = new Vector3Container(1,1,1);
		private List<GameObject> objects = new ArrayList<>();
		public void addObject(GameObject obj){
			obj.getGameModel().getPosition().bindValue(targetPos);
			obj.getGameModel().getRotation().bindValue(targetRot);
			obj.getGameModel().getScale().bindValue(targetScl);
			objects.add(obj);
		}
		public void updateOffsets(){
			Vector3f base = getScene().getCamera().getCameraRightVec(targetScl.getValue().x);
			float offset = -(float)objects.size()/2;
			for(int i=0;i<objects.size();i++){
				objects.get(i).getGameModel().getPosition().setValueOffset(VectorTools.vectorMul(base, i*textSpacing+offset));
			}
		}
		public void updateObjects(){
			//Her kan vi nyttegjøre oss av Java Streams med arbeidstråder! Wee!
			objects.parallelStream().forEach(obj ->{
				obj.getGameModel().getPosition().bindValue(targetPos);
				obj.getGameModel().getRotation().bindValue(targetRot);
				obj.getGameModel().getScale().bindValue(targetScl);
			});
		}
		public Vector3Container getPosition(){
			return targetPos;
		}
		public Vector3Container getRotation(){
			return targetRot;
		}
		public Vector3Container getScale(){
			return targetScl;
		}
		public void removeAll(){
			objects.parallelStream().forEach(o -> o.getGameData().setBoolValue(PROPERTY_INSTANCE_DELETEME,true));
			objects.clear();
		}
	}
	
	private CoffeeText text = null;
	protected List<CoffeeTextStruct> sentences = new ArrayList<>();
	protected void initText(InstantiableObject source){
		text = new CoffeeText(source);
	}
	protected void writeLetter(CoffeeTextStruct textTarget,char lch){
//		if(text==null)
//			System.err.println("Text not initialized!");
//		GameObject test = text.createLetter(lch);
//		getScene().addInstance(test);
//		textTarget.addObject(test);
//		System.out.println("Added "+lch);
	}
	protected void writeSentence(CoffeeTextStruct textTarget,String sentence){
		for(char l : sentence.toCharArray())
			writeLetter(textTarget,l);
		textTarget.updateObjects();
	}
}