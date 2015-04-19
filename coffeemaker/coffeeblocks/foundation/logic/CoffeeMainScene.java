package coffeeblocks.foundation.logic;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.CoffeeSceneManager;
import coffeeblocks.general.VectorTools;
import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.metaobjects.Vector3Container;
import coffeeblocks.opengl.CoffeeAnimator;

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
	protected static final String PROPERTY_TIMER_SWITCH = "switch";
	protected static final String PROPERTY_TIMER_JUMP_TO = "jump-to";
	protected static final String PROPERTY_TIMER_EXPIRY = "expire-time";
	protected static final String SCENE_ID_MAIN = "main";
	protected static final String ANI_RUNCYCLE = "ani.runcycle";
	
	protected static final String OBJECT_ID_TESTBOX = "1.testbox";
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
	
	private static final boolean GAME_BOOL_FALLDAMAGE = false;
	private static final boolean GAME_BOOL_GODMODE = false;
	
	private List<GameCharacter> characters = new ArrayList<>();
	
	public CoffeeMainScene(CoffeeSceneManager manager, CoffeeAnimator animator) {
		super(manager, animator);
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
			e.getGameData().setTimerValue(EnemyPursuer.MONSTER_PROP_TIMER_DEATH, 0l);
			e.getGameData().setStringValue(EnemyPursuer.MONSTER_PROP_STRING_TRACKING, null);
			e.getGameData().setVectorValue(EnemyPursuer.MONSTER_PROP_VECTOR_HOME, new Vector3Container());
		});
		
		getScene().addInstance(getScene().getInstantiable("monster.golem").createInstance("monster.golem.1", false));
		{
			GameObject instance = getScene().getInstantiable("monster.golem").createInstance("monster.golem.2", false);
			instance.getGameModel().getPosition().setValue(new Vector3f(0,40,0));
			getScene().addInstance(instance);
		}
		getScene().getInstancedObject("monster.golem.1").getGameData().getVectorValue(EnemyPursuer.MONSTER_PROP_VECTOR_HOME).setValue(
				getScene().getInstancedObject("monster.golem.1").getGameModel().getPosition().getValue());
		getScene().getInstancedObject("monster.golem.2").getGameData().getVectorValue(EnemyPursuer.MONSTER_PROP_VECTOR_HOME).setValue(
				getScene().getInstancedObject("monster.golem.2").getGameModel().getPosition().getValue());
		
		characters.add(new EnemyPursuer("monster.golem.1",OBJECT_ID_PLAYER));
		characters.add(new EnemyPursuer("monster.golem.2",OBJECT_ID_PLAYER));
	}
	@Override protected void tickSpecifics() {
		super.tickSpecifics();
		if(!isReady())
			setupSpecifics();
		
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
		manager.getRenderer().getAlListenPosition().bindValue(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition());
		getObject(OBJECT_ID_PLAYER).getSoundBox().get(0).getPosition().setValue(new Vector3f(25,0,0));

		//Vi initaliserer tekst-objektet vårt
		initText(getScene().getInstantiable("1.letter"));
		//Vi skriver litt tekst
		CoffeeTextStruct testStruct = new CoffeeTextStruct();
		testStruct.getPosition().bindValue(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition());
		testStruct.getRotation().bindValue(getObject(OBJECT_ID_PLAYER).getGameModel().getRotation());
		writeSentence(testStruct,"Hello!");
		testStruct.getScale().setValue(new Vector3f(0.2f,0.2f,0.2f));
		sentences.add(testStruct);
		System.out.println(sentences);
		
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_CAN_JUMP,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_W,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_A,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_S,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_RUN_D,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_JUMP,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_CAN_MOVE,true);
		getObject(OBJECT_ID_PLAYER).getGameData().setIntValue(PROPERTY_INT_STATE,PlayerState.ALIVE.toInt());
		getObject(OBJECT_ID_PLAYER).getGameData().setDoubleValue(PROPERTY_DUBS_WALK_PACE,24d);
		getObject(OBJECT_ID_PLAYER).getGameData().setDoubleValue(PROPERTY_DUBS_SPEEDLIMIT,25d);
		getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(ANI_RUNCYCLE, 0l);
	}
	@Override protected void tickPlayer() {
		super.tickPlayer();
		if(!isReady())
			setupPlayer();
		
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
				getObject(OBJECT_ID_PLAYER).getGameData().getBoolValue(PROPERTY_BOOL_CAN_JUMP))
			//Uten dette vil spilleren gli overalt, hvilket er ekstremt plagsomt.
			//Bi-effekten er at spilleren har en stor massetreghet som om den var uendelig tung, og kan derfor ikke dyttes av andre objekter.
			getScene().requestObjectUpdate(OBJECT_ID_PLAYER, GameObject.PropertyEnumeration.PHYS_CLEARFORCE,null); 
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

		characters.stream().forEach(e -> {
			e.onTick();
		});

		new ArrayList<>(getScene().getInstanceList()).stream().sequential().forEach(object -> {
			if(object.getGameData().getTimerValue(PROPERTY_TIMER_EXPIRY)!=null&&
					clock>=object.getGameData().getTimerValue(PROPERTY_TIMER_EXPIRY)){
				getScene().deleteInstance(object.getObjectId());
			}
			if(object.getGameData().getBoolValue(PROPERTY_INSTANCE_DELETEME)!=null){
				getScene().deleteInstance(object.getObjectId());
			}
		});
	}
	@Override public void handleKeyPress(int key) {
		super.handleKeyPress(key);
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
	@Override public void handleKeyRelease(int key) {
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
	@SuppressWarnings("unused") @Override public void handleCollisions(String body1, String body2) {
		if(doBodiesCollide(body1,body2,OBJECT_ID_PLAYER,"death")&&getObject(OBJECT_ID_PLAYER).getGameData().getIntValue(PROPERTY_INT_STATE)==PlayerState.ALIVE.toInt()){
			if(clock>=getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_DIE)&&clock>=getObject(OBJECT_ID_PLAYER).getGameData().getTimerValue(PROPERTY_TIMER_TIME_TO_LIVE)) //Slik at tiden ikke utvider seg uendelig. Hvis dødstimeren allerede er aktivert vil den ikke sette den på nytt.
				getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_TIME_TO_DIE, clock+250);
		}else if(doBodiesCollide(body1,body2,OBJECT_ID_PLAYER,"terrain")||doBodiesCollide(body1,body2,OBJECT_ID_PLAYER,"terrain.walkway")){
			if(GAME_BOOL_FALLDAMAGE&&Math.abs(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition().getVelocity().y)>30)
				playerDieFull("You didn't learn to fly in time!");
			getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_CAN_JUMP, true);
			getObject(OBJECT_ID_PLAYER).getGameData().setTimerValue(PROPERTY_TIMER_JUMP_TO, clock+150);
		}else if(doTypedBodiesCollide(body1,body2,OBJECT_ID_PLAYER,"bullet.")){
			playerDie();
		}
		
		characters.stream().forEach(e -> {
			if(body1.equals(e.getIdentifier()))
				e.handleCollision(body2);
			else if(body2.equals(e.getIdentifier()))
				e.handleCollision(body1);
		});
	}
	@Override public void handleMouseRelease(int key) {
		// TODO Auto-generated method stub
		
	}
	public void playerDie(String reason){
		playerDieFull("You were killed by "+reason+"!");
	}
	public void playerDieFull(String reason){
		if(GAME_BOOL_GODMODE)
			return;
		System.out.println(reason);
		super.playerDie();
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_CAN_MOVE,false);
		getObject(OBJECT_ID_PLAYER).getGameData().setIntValue(PROPERTY_INT_STATE,PlayerState.DEAD.toInt());
	}
	@Override public void playerRespawn(){
		super.playerRespawn();
		getObject(OBJECT_ID_PLAYER).getGameData().setBoolValue(PROPERTY_BOOL_CAN_MOVE,true);
		getObject(OBJECT_ID_PLAYER).getGameData().setIntValue(PROPERTY_INT_STATE,PlayerState.ALIVE.toInt());
	}
	
	//Ansvarlig for å inneholde informasjonen under kjøretid. Objektene opprettes og settes til riktige verdier før dette.
	private interface GameCharacter {
		public void onTick();
		public void handleCollision(String body);
		public String getIdentifier();
	}
	
	private class EnemyPursuer implements GameCharacter{
		//Forfølger spilleren når den kommer innenfor rekkevidde eller blir forstyrret fra avstand
		protected static final String MONSTER_PROP_BOOL_STATE = "state";
		protected static final String MONSTER_PROP_TIMER_DEATH = "deadtime";
		protected static final String MONSTER_PROP_STRING_TRACKING = "deadtime";
		protected static final String MONSTER_PROP_VECTOR_HOME = "home";
		protected GameObject target = null;
		protected String primaryVictim = null;
		protected float range = 30f;
		
		public EnemyPursuer(String target,String victim){
			this.target = getScene().getAnyObject(target);
			this.primaryVictim = victim;
		}
		public String getIdentifier(){
			return target.getObjectId();
		}
		public void onTick(){
			//Monstere får påført en kraft i retning av spilleren dersom de kan se spilleren
			//Dersom spilleren er innenfor rekkevidde, sett dette som mål
			if(logic_objectCanSeeOtherInRange(target.getObjectId(),primaryVictim,range)){
				target.getGameData().setStringValue(MONSTER_PROP_STRING_TRACKING,OBJECT_ID_PLAYER);
				target.getGameModel().getAnimationContainer().setAnimationState("detect", 0.1f);
			}

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
		public void handleCollision(String body){
			if(body.equals(OBJECT_ID_PLAYER)&&getObject(OBJECT_ID_PLAYER).getGameData().getIntValue(PROPERTY_INT_STATE)==PlayerState.ALIVE.toInt()){
				playerDie("the rock people");
			}else if(body.startsWith("bullet.")){
				this.target.getGameData().setStringValue(MONSTER_PROP_STRING_TRACKING,OBJECT_ID_PLAYER);
				target.getGameModel().getAnimationContainer().setAnimationState("detect", 0.2f);
				getScene().requestObjectUpdate(this.target.getObjectId(),GameObject.PropertyEnumeration.PHYS_CLEARFORCE,null);
			}
		}
	}
	private class EnemyTurret implements GameCharacter {
		GameObject target;
		public EnemyTurret(GameObject target){
			this.target = target;
		}
		
		@Override
		public void onTick() {
			
		}

		@Override
		public void handleCollision(String body) {
			
		}

		@Override
		public String getIdentifier() {
			return null;
		}
		
	}
}