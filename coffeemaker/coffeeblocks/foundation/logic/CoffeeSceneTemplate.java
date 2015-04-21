package coffeeblocks.foundation.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private CoffeeShop.SceneApplier sceneApplier = null;
	public CoffeeSceneTemplate(CoffeeSceneManager manager,CoffeeAnimator animator,CoffeeShop.SceneApplier sceneApplier){
		this.manager = manager;
		this.animator = animator;
		this.sceneApplier = sceneApplier;
	}
	protected CoffeeSceneManager manager = null;
	protected CoffeeAnimator animator = null;
	protected long clock = 0;
	
	public void updateClock(){
		clock = System.currentTimeMillis();
	}
	public void cleanup(){
		//Hvis vi gjør dette blir alle teksturene lastet ut. Alle.
//		manager.getRenderer().cleanupAll();
	}
	protected boolean readyStatus = false;
	public void setReadyStatus(boolean readyStatus){
		this.readyStatus = readyStatus;
	}
	public boolean isReady(){
		return readyStatus;
	}
	protected void applyScene(String id){
		sceneApplier.gotoScene(id);
	}

	protected float mouseSensitivity = 0.1f;
	
	protected static final String PROPERTY_TIMER_TIME_TO_DIE = "time-to-die";
	protected static final String PROPERTY_TIMER_TIME_TO_LIVE = "time-to-live";
	protected static final String PROPERTY_VECTOR_SPAWNPOSITION = "spawn-position";
	protected static final String OBJECT_ID_OVERLAY = "1.overlay";
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
	protected void activateScreenOverlay(){
		try{
			getScene().addInstance(getScene().getInstantiable(OBJECT_ID_OVERLAY).createInstance(OBJECT_ID_OVERLAY, false));
		}catch(NullPointerException e){
			System.err.println("Could not find screen overlay object! Make sure that object "+OBJECT_ID_OVERLAY+" is instantiable!");
			System.out.println(getScene().getInstantiableIdList());
		}
		billboard(OBJECT_ID_OVERLAY, true);
		getAnyObject(OBJECT_ID_OVERLAY).getGameModel().getPosition().bindValue(getScene().getCamera().getCameraPos());
		getAnyObject(OBJECT_ID_OVERLAY).getGameModel().getMaterial().getTransparencyObject().setValue(0f);
		System.out.println(getAnyObject(OBJECT_ID_OVERLAY).getGameModel().getMaterial().getTransparencyObject().getValue());
		getAnyObject(OBJECT_ID_OVERLAY).getGameModel().getPosition().setOffsetCallback(new Vector3Container.VectorOffsetCallback() {
			@Override
			public Vector3f getOffset() {
				return getScene().getCamera().getCameraForwardVec(0.5f);
			}
		});
	}
	protected void setupPlayer(){
		//Skjermoverlegget
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
		
		if(clock>=getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_DIE)&&
				getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_DIE)!=0)
			playerDie();
		if(clock>=getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_LIVE)&&
				getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_LIVE)!=0)
			playerRespawn();
	}
	protected void setupCamera(){
		getScene().getCamera().getCameraPos().bindValue(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition());
		//Vi lager en callback for å slippe å oppdatere den manuelt
		//Kameraets posisjon relativt til spilleren endrer seg, derfor må vi endre offset for kameraets posisjon for å holde det i bane rundt spilleren.
		getScene().getCamera().getCameraPos().setOffsetCallback(new Vector3Container.VectorOffsetCallback() {
			@Override
			public Vector3f getOffset() {
				return Vector3f.add(
						VectorTools.vectorMul(getScene().getCamera().getUp(),0.8f),getScene().getCamera().getCameraForwardVec(-5f),null);
			}
		});
	}
	protected void tickCamera(){
		if(!isReady())
			setupCamera();
	}
	
	
	protected void playerDie(){
		if(getAnyObject(OBJECT_ID_OVERLAY)!=null)
			animator.addTransition(getAnyObject(OBJECT_ID_OVERLAY).getGameModel().getMaterial().getTransparencyObject(), 1f, CoffeeAnimator.TransitionType.ValueLinear, 300f);
		getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_TIME_TO_DIE, 0l);
		getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_TIME_TO_LIVE, clock+3000);
	}
	protected void playerRespawn(){
		if(getAnyObject(OBJECT_ID_OVERLAY)!=null)
			getAnyObject(OBJECT_ID_OVERLAY).getGameModel().getMaterial().getTransparencyObject().setValue(0f);
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
		GameObject res = getScene().getObject(object);
		if(res==null)
			throw new NullPointerException("Object \""+object+"\" not found!");
		return res;
	}
	protected GameObject getAnyObject(String object){
		GameObject res = getScene().getAnyObject(object);
		if(res==null)
			throw new NullPointerException("Object \""+object+"\" not found!");
		return res;
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
	protected void billboardContainer(Vector3Container object,boolean spherical){
		object.bindValue(getScene().getCamera().getCameraRotation());
		if(spherical)
			object.setValueMultiplier(new Vector3f(-1,-1,0));
		else
			object.setValueMultiplier(new Vector3f(0,-1,0));
	}

	protected class CoffeeTextStruct {
		//Tar seg av bokstavelementene slik at vi enkelt kan endre de som vi vil
		private Vector3Container targetPos = new Vector3Container();
		private Vector3Container targetRot = new Vector3Container();
		public float textSpacing = 0.2f;
		private Vector3Container targetScl = new Vector3Container(1,1,1);
		public List<GameObject> objects = new ArrayList<>();
		private Vector3Container.VectorOffsetCallback offsetCallback = null;
		public CoffeeTextStruct(Vector3Container.VectorOffsetCallback offsetCallback){
			this.offsetCallback = offsetCallback;
		}
		public void addObject(GameObject obj){
			obj.getGameModel().getPosition().bindValue(targetPos);
			obj.getGameModel().getRotation().bindValue(targetRot);
			obj.getGameModel().getScale().bindValue(targetScl);
			updateObjects();
			objects.add(obj);
		}
		private void updateObjects(){
			//Her kan vi nyttegjøre oss av Java Streams med arbeidstråder! Wee!
			float offset = (objects.size()<15 ? -1 : -1.5f);
			objects.parallelStream().forEach(obj ->{
				obj.getGameModel().getPosition().bindValue(targetPos);
				obj.getGameModel().getRotation().bindValue(targetRot);
				obj.getGameModel().getScale().bindValue(targetScl);
				obj.getGameModel().getPosition().setOffsetCallback(new Vector3Container.VectorOffsetCallback() {
					@Override
					public Vector3f getOffset() {
						return VectorTools.vectorMul(offsetCallback.getOffset(), objects.indexOf(obj)*textSpacing+offset);
					}
				});
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
			objects.parallelStream().forEach(o -> {
				o.getGameModel().setDrawObject(false);
				o.getGameData().setBoolValue(PROPERTY_INSTANCE_DELETEME,true);
			});
			objects.clear();
		}
	}
	
	private CoffeeText text = null;
	protected Map<String,CoffeeTextStruct> sentences = new HashMap<>();
	public void initText(InstantiableObject source){
		text = new CoffeeText(source);
	}
	public CoffeeText getTextObject(){
		return text;
	}
	protected void writeLetter(CoffeeTextStruct textTarget,char lch){
		if(text==null){
			System.err.println("Text not initialized!");
			return;
		}
		GameObject test = text.createLetter(lch);
		getScene().addInstance(test);
		textTarget.addObject(test);
	}
	protected void writeSentence(CoffeeTextStruct textTarget,String sentence){
		for(char l : sentence.toCharArray())
			writeLetter(textTarget,l);
		textTarget.updateObjects();
	}
}