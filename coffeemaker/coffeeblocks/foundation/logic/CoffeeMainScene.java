package coffeeblocks.foundation.logic;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.CoffeeSceneManager;
import coffeeblocks.foundation.logic.CoffeeShop.SceneApplier;
import coffeeblocks.general.VectorTools;
import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.metaobjects.Vector3Container;
import coffeeblocks.metaobjects.Vector3Container.VectorOffsetCallback;
import coffeeblocks.opengl.CoffeeAnimator;
import coffeeblocks.opengl.components.CoffeeSprite;

public class CoffeeMainScene extends CoffeeSceneTemplate {
	
	protected static final String PROPERTY_BOOL_CAN_JUMP = "can-jump";
	protected static final String PROPERTY_BOOL_RUN_W = "ani.running.w";
	protected static final String PROPERTY_BOOL_RUN_A = "ani.running.a";
	protected static final String PROPERTY_BOOL_RUN_S = "ani.running.s";
	protected static final String PROPERTY_BOOL_RUN_D = "ani.running.d";
	protected static final String PROPERTY_BOOL_CAN_MOVE = "mobile";
	protected static final String PROPERTY_BOOL_JUMP = "ani.jumping";
	protected static final String PROPERTY_DUBS_WALK_PACE = "walk-pace";
	protected static final String PROPERTY_DUBS_SPEEDLIMIT = "speed-limit";
	protected static final String PROPERTY_INT_TEXTURE = "texture";
	protected static final String PROPERTY_INT_STATE = "state";
	
	protected static final String PROPERTY_INT_HEALTH = "health";
	protected static final String PROPERTY_INT_HEALTH_MAX = "health.max";
	protected static final String PROPERTY_DUBS_HEALTH_MAX_SCALE = "health.max.scale";
	
	protected static final String PROPERTY_TIMER_SWITCH = "switch";
	protected static final String PROPERTY_TIMER_JUMP_TO = "jump-to";
	protected static final String PROPERTY_TIMER_EXPIRY = "expire-time";
	protected static final String SCENE_ID_MAIN = "main";
	protected static final String ANI_RUNCYCLE = "ani.runcycle";
	
	protected static final String OBJECT_ID_WATER = "2.water";
	protected static final String OBJECT_ID_SKYBOX = "skybox";
	
	private enum PlayerState {
		DEAD(0),ALIVE(1);
		
		private final int value;
		private PlayerState(final int value){
			this.value = value;
		}
		public int toInt(){
			return value;
		}
	}
	
	private static final boolean GAME_BOOL_FALLDAMAGE = true;
	private static final boolean GAME_BOOL_GODMODE = false;
	
	private List<GameCharacter> characters = new ArrayList<>();
	
	public CoffeeMainScene(CoffeeSceneManager manager, CoffeeAnimator animator, SceneApplier sceneApplier) {
		super(manager, animator, sceneApplier);
	}
	public String getSceneId(){
		return SCENE_ID_MAIN;
	}
	@Override protected void setupSpecifics() {
		getObject(OBJECT_ID_WATER).getGameData().setTimerValue(PROPERTY_TIMER_SWITCH,0l);
		getObject(OBJECT_ID_WATER).getGameData().setIntValue(PROPERTY_INT_TEXTURE,0);

		getObject(OBJECT_ID_SKYBOX).getGameData().setTimerValue(PROPERTY_TIMER_SWITCH,0l);
		getObject(OBJECT_ID_SKYBOX).getGameData().setIntValue(PROPERTY_INT_TEXTURE,0);
		
		getScene().getInstantiableList().stream().filter(e -> e.getObjectPreseedName().startsWith("monster")).forEach(e -> {
			e.getGameData().setBoolValue(EnemyPursuer.MONSTER_PROP_BOOL_STATE, true);
			e.getGameData().setIntValue(PROPERTY_INT_HEALTH, 1600);
			e.getGameData().setIntValue(PROPERTY_INT_HEALTH_MAX, 1600);
			e.getGameData().setDoubleValue(PROPERTY_DUBS_HEALTH_MAX_SCALE, 2.0);
			e.getGameData().setTimerValue(EnemyPursuer.MONSTER_PROP_TIMER_DEATH, 0l);
			e.getGameData().setStringValue(EnemyPursuer.MONSTER_PROP_STRING_TRACKING, null);
			e.getGameData().setVectorValue(EnemyPursuer.MONSTER_PROP_VECTOR_HOME, new Vector3Container());
		});
		
		for(int i=0;i<4;i++){
			spawnMonster(new Vector3f(0,30+i*2,0));
		}
	}
	private void spawnMonster(Vector3f pos){ //i enumererer monsteret
		String id = "monster.golem."+monster_ticker;
		getScene().addInstance(getScene().getInstantiable("monster.golem").createInstance(id, false));
		GameObject o = getAnyObject(id);
		o.getGameModel().getPosition().setValue(pos);
		o.getGameModel().getRotation().setOffsetCallback(new Vector3Container.VectorOffsetCallback() {
			@Override
			public Vector3f getOffset() {
				return new Vector3f(
						0,
						VectorTools.getEuclideanRotationAngle(o.getGameModel().getPositionVector(),
								getObject(OBJECT_ID_PLAYER).getGameModel().getPositionVector(), false)-90,
						0
						);
			}
		});
		getScene().getInstancedObject(id).getGameData().getVectorValue(EnemyPursuer.MONSTER_PROP_VECTOR_HOME).setValue(
				getScene().getInstancedObject(id).getGameModel().getPosition().getValue());
		getCharacters().add(new EnemyPursuer(id,OBJECT_ID_PLAYER));
		monster_ticker++;
	}
	private synchronized List<GameCharacter> getCharacters(){
		return characters;
	}
	private int monster_ticker = 0;
	@Override protected void tickSpecifics() {
		super.tickSpecifics();
		
		//Miljø
		if(getObject(OBJECT_ID_WATER).getGameData().getTimerValue(PROPERTY_TIMER_SWITCH)==null||
				clock>=getObject(OBJECT_ID_WATER).getGameData().getTimerValue(PROPERTY_TIMER_SWITCH)){

			getObject(OBJECT_ID_WATER).getGameData().setIntValue(
					PROPERTY_INT_TEXTURE,
					getObject(OBJECT_ID_WATER).getGameData().getIntValue(PROPERTY_INT_TEXTURE)+1);

			if(getObject(OBJECT_ID_WATER).getGameData().getIntValue(PROPERTY_INT_TEXTURE)>1)
				getObject(OBJECT_ID_WATER).getGameData().setIntValue(PROPERTY_INT_TEXTURE,0);
			getScene().getObject(OBJECT_ID_WATER).getGameModel().getMaterial().selectTexture = 
					getObject(OBJECT_ID_WATER).getGameData().getIntValue(PROPERTY_INT_TEXTURE);
			getObject(OBJECT_ID_WATER).getGameData().setTimerValue(PROPERTY_TIMER_SWITCH,clock+400);
		}
	}
	
	@Override protected void setupPlayer() {
		super.setupPlayer();
		activateScreenOverlay();
		manager.getRenderer().getAlListenPosition().bindValue(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition());
		getObject(OBJECT_ID_PLAYER).getSoundBox().get(0).getPosition().setValue(new Vector3f(25,0,0));

		//Vi skriver litt tekst
		//Vi bruker en callback for å spare tid ved tick. Veldig nyttig!
		{
			CoffeeTextStruct testStruct = new CoffeeTextStruct(new Vector3Container.VectorOffsetCallback() {
				@Override
				public Vector3f getOffset() {
					return getScene().getCamera().getCameraRightVec(1f);
				}
			});
			testStruct.getPosition().bindValue(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition());
			testStruct.getPosition().setValueOffset(new Vector3f(0,1.5f,0));
			billboardContainer(testStruct.getRotation(),true);
			writeSentence(testStruct,"Bruk WASD for å bevege deg");
			testStruct.getScale().setValue(new Vector3f(0.13f,0.13f,0.13f));
			sentences.put("help",testStruct);
		}
		{
			CoffeeTextStruct testStruct = new CoffeeTextStruct(new Vector3Container.VectorOffsetCallback() {
				@Override
				public Vector3f getOffset() {
					return getScene().getCamera().getCameraRightVec(0.5f);
				}
			});
			testStruct.getPosition().bindValue(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition());
			testStruct.getPosition().setValueOffset(new Vector3f(0,1.3f,0));
			billboardContainer(testStruct.getRotation(),true);
			writeSentence(testStruct,"Skyt monstrene!");
			testStruct.getScale().setValue(new Vector3f(0.1f,0.1f,0.1f));
			sentences.put("help.shoot",testStruct);
		}
		
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_CAN_JUMP,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_W,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_A,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_S,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_D,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_JUMP,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_CAN_MOVE,true);
		getObject(OBJECT_ID_PLAYER).getGameData().setIntValue(PROPERTY_INT_STATE,PlayerState.ALIVE.toInt());
		getObject(OBJECT_ID_PLAYER).getGameData().setIntValue(PROPERTY_INT_HEALTH,100);
		getObject(OBJECT_ID_PLAYER).getGameData().setIntValue(PROPERTY_INT_HEALTH_MAX,100);
		getObject(OBJECT_ID_PLAYER).getGameData().setDoubleValue(PROPERTY_DUBS_HEALTH_MAX_SCALE,2.0);
		getObject(OBJECT_ID_PLAYER).getGameData().setDoubleValue(PROPERTY_DUBS_WALK_PACE,24d);
		getObject(OBJECT_ID_PLAYER).getGameData().setDoubleValue(PROPERTY_DUBS_SPEEDLIMIT,25d);
		getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(ANI_RUNCYCLE, 0l);

		CoffeeSprite test = new CoffeeSprite(getScene().getInstantiable("0.sprite"));
		{
			GameObject testO = test.createSprite("hp","testgame/models/elements/healthbar.png");
			object_id_healthbar = testO.getObjectId();
			testO.getGameModel().getPosition().bindValue(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition());
			billboardContainer(testO.getGameModel().getRotation(),false);
			float _health_scale = getObject(OBJECT_ID_PLAYER).getGameData().getDoubleValue(PROPERTY_DUBS_HEALTH_MAX_SCALE).floatValue();
			testO.getGameModel().getScale().setValue(new Vector3f(_health_scale,0.2f,_health_scale));
			testO.getGameModel().getRotation().setValueOffset(new Vector3f(0,-15,0));
			testO.getGameModel().getPosition().setOffsetCallback(new VectorOffsetCallback(){
				@Override
				public Vector3f getOffset() {
					return Vector3f.add(getScene().getCamera().getCameraRightVec(-2f), getScene().getCamera().getCameraUpVec(1f), null);
				}
			});
			getScene().addInstance(testO);
		}
		{
			GameObject testO = test.createSprite("heart","testgame/models/elements/heart.png");
			testO.getGameModel().getPosition().bindValue(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition());
			billboardContainer(testO.getGameModel().getRotation(),false);
			testO.getGameModel().getScale().setValue(new Vector3f(0.3f,0.3f,0.3f));
			testO.getGameModel().getRotation().setValueOffset(new Vector3f(0,-10,0));
			testO.getGameModel().getPosition().setOffsetCallback(new VectorOffsetCallback(){
				@Override
				public Vector3f getOffset() {
					return Vector3f.add(getScene().getCamera().getCameraRightVec(-4.1f),getScene().getCamera().getCameraUpVec(0.9f), null);
				}
			});
			getScene().addInstance(testO);
		}
	}
	private String object_id_healthbar = null;
	@Override protected void tickPlayer() {
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
		}else if(!getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_JUMP)&&
				getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_CAN_JUMP)){
			//Uten dette vil spilleren gli overalt, hvilket er ekstremt plagsomt.
			//Bi-effekten er at spilleren har en stor massetreghet som om den var uendelig tung, og kan derfor ikke dyttes av andre objekter.
//			getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_CLEARFORCE,null);
			
		}
		if(getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_JUMP)) //Hopping prioriteres over gå-animasjonen
			getObject(OBJECT_ID_PLAYER).getGameModel().getAnimationContainer().setAnimationState("jump", 0.01f);
		getObject(OBJECT_ID_PLAYER).getGameModel().getAnimationContainer().morphToState();
		getObject(OBJECT_ID_PLAYER).getGameModel().getAnimationContainer().setAnimationState(null); //tilbakestiller modellen for å unngå at animasjoner gjentas unødvendig
	}
	@Override public void onGlfwFrameTick(double currentTime) {
		//Denne operasjonen er lagt i render-loopen for å kjøre på et tidspunkt hvor fysikk-systemet ikke har tick.
		//Dersom fysikk-tick skjer ved samme tidspunkt som denne operasjonen vil det føre til en rekke ulike feil avhengig av hvor den er i tick'et og forårsake en krasj.
		//Denne løsningen er mest elegant, ettersom synkronisering av trådene ville være en altfor dyr og unødvendig operasjon.
		if(getScene().getPhysicsSystem().performRaytestHeight(OBJECT_ID_PLAYER)<=0.1f)
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_JUMP,false);
		else
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_JUMP,true);

		new ArrayList<>(getCharacters()).stream().forEach(e -> {
			e.onTick();
		});

		new ArrayList<>(getScene().getInstanceList()).parallelStream().sequential().forEach(object -> {
			if(object.getGameData().getTimerValue(PROPERTY_TIMER_EXPIRY)!=null&&
					clock>=object.getGameData().getTimerValue(PROPERTY_TIMER_EXPIRY)){
//				System.err.println("Object expired: "+object.getObjectId());
				getScene().deleteInstance(object.getObjectId());
			}
			if(object.getGameData().getBoolValue(PROPERTY_INSTANCE_DELETEME)){
//				System.err.println("Object deleted: "+object.getObjectId());
				getScene().deleteInstance(object.getObjectId());
			}
		});
	}
	@Override public void handleKeyPress(int key) {
		super.handleKeyPress(key);
		sentences.get("help").removeAll();
		sentences.get("help.shoot").removeAll();
		switch(key){
		case GLFW.GLFW_KEY_W:{
			if(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition().getVelocity().length()>getObject(OBJECT_ID_PLAYER).getGameData().getDoubleValue(PROPERTY_DUBS_SPEEDLIMIT).floatValue()||
					!getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_CAN_MOVE))
				return;
			playerWalkDirection(getScene().getCamera().
					getCameraForwardVec(1f),1);
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_W, true);
			return;
		}
		case GLFW.GLFW_KEY_A:{
			if(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition().getVelocity().length()>getObject(OBJECT_ID_PLAYER).getGameData().getDoubleValue(PROPERTY_DUBS_SPEEDLIMIT).floatValue()||
					!getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_CAN_MOVE))
				return;
			playerWalkDirection(getScene().getCamera().
					getCameraRightVec(1f),-1);
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_A, true);
			return;
		}
		case GLFW.GLFW_KEY_S:{
			if(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition().getVelocity().length()>getObject(OBJECT_ID_PLAYER).getGameData().getDoubleValue(PROPERTY_DUBS_SPEEDLIMIT).floatValue()||
					!getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_CAN_MOVE))
				return;
			playerWalkDirection(getScene().getCamera().
					getCameraForwardVec(1f),-1);
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_S, true);
			return;
		}
		case GLFW.GLFW_KEY_D:{
			if(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition().getVelocity().length()>getObject(OBJECT_ID_PLAYER).getGameData().getDoubleValue(PROPERTY_DUBS_SPEEDLIMIT).floatValue()||
					!getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_CAN_MOVE))
				return;
			playerWalkDirection(getScene().getCamera().
					getCameraRightVec(1f),1);
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_D, true);
			return;
		}
		case GLFW.GLFW_KEY_SPACE:{
			if(!getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_CAN_JUMP)||
					!getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_CAN_MOVE))
				return;
			getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_IMPULSE,new Vector3f(0,0.5f,0));
			return;
		}
		case GLFW.GLFW_KEY_KP_5:{
			spawnMonster(new Vector3f(clock%10,30,clock%30));
			return;
		}
		}
	}
	private void playerWalkDirection(Vector3f source, float multiplier){
		Vector3f accel = source;
		accel.y = 0;
		accel.normalise();
		accel = VectorTools.vectorMul(accel, 
				multiplier*getObject(OBJECT_ID_PLAYER).getGameData().getDoubleValue(PROPERTY_DUBS_WALK_PACE).floatValue());
		getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_ACCEL,accel);
	}
	public void playerSeizeMovement(PlayerMovementDirection direction){
		Vector3f vec = null;
		switch(direction){
		case East:
			vec = getScene().getCamera().getCameraRightVec(1f);
			break;
		case North:
			vec = getScene().getCamera().getCameraForwardVec(1f);
			break;
		case South:
			vec = getScene().getCamera().getCameraForwardVec(-1f);
			break;
		case West:
			vec = getScene().getCamera().getCameraRightVec(-1f);
			break;
		}		
		vec.y = 0;
		vec.normalise();
		float scalar = getObject(OBJECT_ID_PLAYER).getGameModel().getPhysicalMass()*Vector3f.dot(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition().getVelocity(), vec)*0.5f;
		vec = VectorTools.vectorMul(vec, scalar);
		vec.negate();
		getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_IMPULSE,vec);
	}
	private enum PlayerMovementDirection{
		North,South,East,West
	}
	@Override public void handleKeyRelease(int key) {
		switch(key){
		case GLFW.GLFW_KEY_W:{
			if(getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_RUN_W))
				playerSeizeMovement(PlayerMovementDirection.North);
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_W, false);
			return;
		}
		case GLFW.GLFW_KEY_A:{
			if(getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_RUN_A))
				playerSeizeMovement(PlayerMovementDirection.West);
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_A, false);
			return;
		}
		case GLFW.GLFW_KEY_S:{
			if(getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_RUN_S))
				playerSeizeMovement(PlayerMovementDirection.South);
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_S, false);
			return;
		}
		case GLFW.GLFW_KEY_D:{
			if(getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_RUN_D))
				playerSeizeMovement(PlayerMovementDirection.East);
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_D, false);
			return;
		}
		}
	}
	@Override public void handleMousePress(int key) {
		if(key==GLFW.GLFW_MOUSE_BUTTON_1){
			//Hvis flere objekter opprettes i samme millisekund vil den ene bli overskrevet. Sånt skjer (ikke).
			spawnProjectile("bullet","."+clock,300f);
		}
	}
	public void spawnProjectile(String instanceId,String identifier,float speed){
		GameObject obj = getScene().getInstantiable(instanceId).createInstance(identifier,true);
		Vector3f dir = getScene().getCamera().getCameraForwardVec(1f);
		dir.y = 0f;
		dir.normalise(); //Vi normaliserer vektoren for å få en jevn avstand fra spilleren uavhengig av vinkel kameraet kan ha
		obj.getGameModel().getPosition().setValue(Vector3f.add(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition().getValue(),VectorTools.vectorMul(dir, 3f),null));
		obj.getGameModel().getPosition().setVelocity(VectorTools.vectorMul(dir, speed));
		obj.getGameModel().getRotation().setValue(getObject(OBJECT_ID_PLAYER).getGameModel().getRotation().getValue());
		obj.getGameData().setTimerValue(PROPERTY_TIMER_EXPIRY, clock+500);
		getScene().addInstance(obj);
	}
	private static final String DEATH_REASON_FALL_HIGH = "Did you not learn the Prefect way of flight?";
	private static final String DEATH_REASON_FALL_LOW = "You didn't learn to fly in time!";
	private static final String DEATH_REASON_BULLET = "Stop hitting yourself to death!";
	@Override public void handleCollisions(String body1, String body2) {
		if(doBodiesCollide(body1,body2,OBJECT_ID_PLAYER,"death")&&getObject(OBJECT_ID_PLAYER).getGameData().getIntValue(PROPERTY_INT_STATE)==PlayerState.ALIVE.toInt()){
			if(clock>=getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_DIE)&&clock>=getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_LIVE)) //Slik at tiden ikke utvider seg uendelig. Hvis dødstimeren allerede er aktivert vil den ikke sette den på nytt.
				playerDieFull("Why did you do that?");
		}else if(doBodiesCollide(body1,body2,OBJECT_ID_PLAYER,"terrain")||doBodiesCollide(body1,body2,OBJECT_ID_PLAYER,"terrain.walkway")){
			if(GAME_BOOL_FALLDAMAGE&&Math.abs(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition().getVelocity().y)>30)
				playerTakeDamage(DEATH_REASON_FALL_HIGH,-50);
			else if(GAME_BOOL_FALLDAMAGE&&Math.abs(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition().getVelocity().y)>20)
				playerTakeDamage(DEATH_REASON_FALL_LOW,-40);
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_CAN_JUMP, true);
			getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_JUMP_TO, clock+150);
		}else if(doTypedBodiesCollide(body1,body2,OBJECT_ID_PLAYER,"bullet.")){
			playerTakeDamage(DEATH_REASON_BULLET,-30);
		}
		
		new ArrayList<>(getCharacters()).stream().forEach(e -> {
			if(body1.equals(e.getIdentifier()))
				e.handleCollision(body2);
			else if(body2.equals(e.getIdentifier()))
				e.handleCollision(body1);
		});
	}
	@Override public void handleMouseRelease(int key) {
		
	}
	public void playerTakeDamage(String reason, int amount){
		getObject(OBJECT_ID_PLAYER).getGameData().setIntValue(PROPERTY_INT_HEALTH, getObject(OBJECT_ID_PLAYER).getGameData().getIntValue(PROPERTY_INT_HEALTH)+amount);
		if(getObject(OBJECT_ID_PLAYER).getGameData().getIntValue(PROPERTY_INT_HEALTH)<=0)
			playerDieFull(reason);
		if(getObject(OBJECT_ID_PLAYER).getGameData().getIntValue(PROPERTY_INT_HEALTH)<0)
			getObject(OBJECT_ID_PLAYER).getGameData().setIntValue(PROPERTY_INT_HEALTH, 0);
		updateHealth();
	}
	public void updateHealth(){
		Vector3f vec = getAnyObject(object_id_healthbar).getGameModel().getScale().getValue();
		float max_scale = getObject(OBJECT_ID_PLAYER).getGameData().getDoubleValue(PROPERTY_DUBS_HEALTH_MAX_SCALE).floatValue();
		float curr_hlth = getObject(OBJECT_ID_PLAYER).getGameData().getIntValue(PROPERTY_INT_HEALTH).floatValue();
		float scale = max_scale*curr_hlth/100f;
		if(scale>max_scale)
			scale = max_scale;
		vec.x = scale;
		vec.z = scale;
		Vector3f col = getAnyObject(object_id_healthbar).getGameModel().getMaterial().getColorMultiplier();
		//Vi gir den ulik farge avhengig av mengden
		if(curr_hlth>100){
			col.x = 0;
			col.y = 1;
			col.z = 0;
		}else if(curr_hlth<100&&curr_hlth>=60){
			col.x = 1;
			col.y = 1;
			col.z = 1;
		}else if(curr_hlth<60&&curr_hlth>=30){
			col.x = 1;
			col.y = 1;
			col.z = 0;
		}else if(curr_hlth<30&&curr_hlth>=0){
			col.x = 1;
			col.y = 0;
			col.z = 0;
		}else if(curr_hlth<0){
			col.x = 0;
			col.y = 0;
			col.z = 0;
		}
		col.normalise();
	}
	public void playerDie(String reason){
		playerDieFull("You were killed by "+reason+"!");
	}
	public void playerDieFull(String reason){
		if(GAME_BOOL_GODMODE)
			return;
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_CAN_MOVE,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setIntValue(PROPERTY_INT_STATE,PlayerState.DEAD.toInt());
		
		CoffeeTextStruct testStruct = new CoffeeTextStruct(new Vector3Container.VectorOffsetCallback() {
			@Override
			public Vector3f getOffset() {
				return getScene().getCamera().getCameraRightVec(1f);
			}
		});
		testStruct.getPosition().bindValue(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition());
		testStruct.getPosition().setValueOffset(new Vector3f(0,2f,0));
		billboardContainer(testStruct.getRotation(),true);
		writeSentence(testStruct,reason);
		testStruct.getScale().setValue(new Vector3f(0.15f,0.15f,0.15f));
		sentences.put("death-text",testStruct);
		
		super.playerDie();
	}
	@Override public void playerRespawn(){
		super.playerRespawn();
		writeSentence(sentences.get("help"),"!!!");
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_CAN_MOVE,true);
		getObject(OBJECT_ID_PLAYER).getGameData().setIntValue(PROPERTY_INT_STATE,PlayerState.ALIVE.toInt());
		if(sentences.get("death-text")!=null)
			sentences.remove("death-text").removeAll();
		getObject(OBJECT_ID_PLAYER).getGameData().setIntValue(PROPERTY_INT_HEALTH, getObject(OBJECT_ID_PLAYER).getGameData().getIntValue(PROPERTY_INT_HEALTH_MAX));
		updateHealth();
	}
	
	//Ansvarlig for å inneholde informasjonen under kjøretid. Objektene opprettes og settes til riktige verdier før dette.
	private interface GameCharacter {
		public void onTick();
		public void handleCollision(String body);
		public String getIdentifier();
	}
	
	private class EnemyBase implements GameCharacter{
		protected static final String MONSTER_PROP_BOOL_STATE = "state";
		protected static final String MONSTER_PROP_TIMER_DEATH = "deadtime";
		protected static final String MONSTER_PROP_STRING_TRACKING = "deadtime";
		protected static final String MONSTER_PROP_VECTOR_HOME = "home";
		protected static final String MONSTER_PROP_DREASON = "You died.";
		protected GameObject target = null;
		protected String primaryVictim = null;
		protected float range = 30f;
		
		public EnemyBase(String target,String victim){
			this.target = getScene().getAnyObject(target);
			this.primaryVictim = victim;
		}
		public String getIdentifier(){
			return target.getObjectId();
		}
		public void enemyTakeDamage(int amount){
			target.getGameData().setIntValue(PROPERTY_INT_HEALTH, target.getGameData().getIntValue(PROPERTY_INT_HEALTH)+amount);
			if(target.getGameData().getIntValue(PROPERTY_INT_HEALTH)<=0)
				enemyDie();
			if(target.getGameData().getIntValue(PROPERTY_INT_HEALTH)<0)
				target.getGameData().setIntValue(PROPERTY_INT_HEALTH, 0);
		}
		public void enemyDie(){
			target.getGameData().setBoolValue(MONSTER_PROP_BOOL_STATE,false); //Vi setter verdien for monsterets levestatus
			target.getGameModel().getAnimationContainer().setAnimationState("detect", 0.2f);
			animator.addTransition(target.getGameModel().getMaterial().getTransparencyObject(), 0.0f, CoffeeAnimator.TransitionType.ValueLinear, 500);
		}
		@Override
		public void onTick() {
			if(!isAlive()) //Vi legger sannsynligvis til mer her senere
				return;
		}
		public boolean isAlive(){
			return target.getGameData().getBoolValue(MONSTER_PROP_BOOL_STATE);
		}
		public boolean checkCanSeeVictim(){
			if(logic_objectCanSeeOtherInRange(target.getObjectId(),primaryVictim,range)){
				target.getGameData().setStringValue(MONSTER_PROP_STRING_TRACKING,OBJECT_ID_PLAYER);
				return true;
			}
			return false;
		}

		public void handleCollision(String body){
			if(!isAlive())
				return;
			if(body.equals(OBJECT_ID_PLAYER)&&getObject(OBJECT_ID_PLAYER).getGameData().getIntValue(PROPERTY_INT_STATE)==PlayerState.ALIVE.toInt()){
				playerTakeDamage(MONSTER_PROP_DREASON,-40);
			}
		}
	}
	
	private class EnemyPursuer extends EnemyBase{
		//Forfølger spilleren når den kommer innenfor rekkevidde eller blir forstyrret fra avstand
		
		public EnemyPursuer(String target,String victim){
			super(target,victim);
		}
		@Override public void enemyDie(){
			super.enemyDie();
			target.getGameData().setTimerValue(PROPERTY_TIMER_EXPIRY,clock+500);
			playerTakeDamage("... Healed to death? NOOOOOO!!",50);
			
			spawnMonster(target.getGameModel().getPosition().getValue());
			spawnMonster(target.getGameModel().getPosition().getValue());
			playerTakeDamage("The enemy has evolved!",0);
		}
		@Override public void handleCollision(String body){
			if(!isAlive())
				return;
			super.handleCollision(body);
			if(body.startsWith("bullet.")){
				this.target.getGameData().setStringValue(MONSTER_PROP_STRING_TRACKING,OBJECT_ID_PLAYER);
				target.getGameModel().getAnimationContainer().setAnimationState("detect", 0.2f);
				enemyTakeDamage(-40);
				getScene().requestObjectUpdate(this.target.getObjectId(),GameObject.PropertyEnumeration.PHYS_CLEARFORCE,null);
			}
		}
		@Override public void onTick(){
			if(!isAlive())
				return;
			super.onTick();
			
			if(checkCanSeeVictim())
				target.getGameModel().getAnimationContainer().setAnimationState("detect", 0.1f);

			//Dersom den skal følge noe, påfør en kraft
			if(target.getGameData().getStringValue(MONSTER_PROP_STRING_TRACKING)!=null&&
					getObject(target.getGameData().getStringValue(MONSTER_PROP_STRING_TRACKING))!=null){
				if(logic_objectCanSeeOther(target.getGameData().getStringValue(MONSTER_PROP_STRING_TRACKING),target.getObjectId())){

					if(clock%800>400)
						target.getGameModel().getAnimationContainer().setAnimationState("stand.1", 0.01f);
					else
						target.getGameModel().getAnimationContainer().setAnimationState("stand.2", 0.01f);
					float distance = pingDistance(target.getObjectId(),target.getGameData().getStringValue(MONSTER_PROP_STRING_TRACKING));
					
					if(distance<200){
						Vector3f vec = Vector3f.sub(getObject(target.getGameData().getStringValue(MONSTER_PROP_STRING_TRACKING)).getGameModel().getPosition().getValue(),
								target.getGameModel().getPosition().getValue(), null);
						vec.y = 0;
						vec.normalise(); //Skal ikke skalere med avstand
						getScene().requestObjectUpdate(target.getObjectId(), GameObject.PropertyEnumeration.PHYS_ACCEL, 
								VectorTools.vectorMul(vec,500-distance));
					}
					
				}else{
					//Hvis ikke innenfor synsfelt, slutt
					target.getGameData().setStringValue(MONSTER_PROP_STRING_TRACKING,null);
				}
			}else if(pingDistance(target.getObjectId(),target.getGameData().getVectorValue(MONSTER_PROP_VECTOR_HOME).getValue())>30){
				target.getGameModel().getAnimationContainer().setAnimationState(null, 0.01f);
				//Hvis ingenting å gjøre, gå hjem
				Vector3f vec = Vector3f.sub(target.getGameData().getVectorValue(MONSTER_PROP_VECTOR_HOME).getValue(),
						target.getGameModel().getPosition().getValue(), null);
				vec.y = 0;
				vec.normalise(); //Skal ikke skalere med avstand
				getScene().requestObjectUpdate(target.getObjectId(), GameObject.PropertyEnumeration.PHYS_ACCEL, 
						VectorTools.vectorMul(vec,400));
			}else{
				target.getGameModel().getAnimationContainer().setAnimationState(null, 0.01f);
			}
			target.getGameModel().getAnimationContainer().morphToState();
		}
	}
	private class EnemyTurret extends EnemyBase{
		
		public EnemyTurret(String target, String victim) {
			super(target, victim);
		}

		@Override public void enemyDie(){
			super.enemyDie();
			target.getGameData().setTimerValue(PROPERTY_TIMER_EXPIRY,clock+500);
			playerTakeDamage("... Healed to death? NOOOOOO!!",50);
			
			spawnMonster(target.getGameModel().getPosition().getValue());
			spawnMonster(target.getGameModel().getPosition().getValue());
			playerTakeDamage("The enemy has evolved!",0);
		}
		@Override public void handleCollision(String body){
			if(!isAlive())
				return;
			super.handleCollision(body);
			if(body.startsWith("bullet.")){
				this.target.getGameData().setStringValue(MONSTER_PROP_STRING_TRACKING,OBJECT_ID_PLAYER);
				target.getGameModel().getAnimationContainer().setAnimationState("detect", 0.2f);
				enemyTakeDamage(-40);
				getScene().requestObjectUpdate(this.target.getObjectId(),GameObject.PropertyEnumeration.PHYS_CLEARFORCE,null);
			}
		}
		@Override public void onTick(){
			if(!isAlive())
				return;
			super.onTick();
			
			if(checkCanSeeVictim())
				target.getGameModel().getAnimationContainer().setAnimationState("detect", 0.1f);

			//Dersom den skal følge noe, påfør en kraft
			if(target.getGameData().getStringValue(MONSTER_PROP_STRING_TRACKING)!=null&&
					getObject(target.getGameData().getStringValue(MONSTER_PROP_STRING_TRACKING))!=null){
				if(logic_objectCanSeeOther(target.getGameData().getStringValue(MONSTER_PROP_STRING_TRACKING),target.getObjectId())){

					if(clock%800>400)
						target.getGameModel().getAnimationContainer().setAnimationState("stand.1", 0.01f);
					else
						target.getGameModel().getAnimationContainer().setAnimationState("stand.2", 0.01f);
					float distance = pingDistance(target.getObjectId(),target.getGameData().getStringValue(MONSTER_PROP_STRING_TRACKING));
					
					if(distance<200){
						Vector3f vec = Vector3f.sub(getObject(target.getGameData().getStringValue(MONSTER_PROP_STRING_TRACKING)).getGameModel().getPosition().getValue(),
								target.getGameModel().getPosition().getValue(), null);
						vec.y = 0;
						vec.normalise(); //Skal ikke skalere med avstand
						spawnProjectile("bullet","."+clock,300f);
					}
					
				}else{
					//Hvis ikke innenfor synsfelt, slutt
					target.getGameData().setStringValue(MONSTER_PROP_STRING_TRACKING,null);
				}
			}else if(pingDistance(target.getObjectId(),target.getGameData().getVectorValue(MONSTER_PROP_VECTOR_HOME).getValue())>30){
				target.getGameModel().getAnimationContainer().setAnimationState(null, 0.01f);
				//Hvis ingenting å gjøre, gå hjem
				Vector3f vec = Vector3f.sub(target.getGameData().getVectorValue(MONSTER_PROP_VECTOR_HOME).getValue(),
						target.getGameModel().getPosition().getValue(), null);
				vec.y = 0;
				vec.normalise(); //Skal ikke skalere med avstand
				getScene().requestObjectUpdate(target.getObjectId(), GameObject.PropertyEnumeration.PHYS_ACCEL, 
						VectorTools.vectorMul(vec,400));
			}else{
				target.getGameModel().getAnimationContainer().setAnimationState(null, 0.01f);
			}
			target.getGameModel().getAnimationContainer().morphToState();
		}
	}
}