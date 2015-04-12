package coffeeblocks.metaobjects;

import coffeeblocks.foundation.GameMetaData;
import coffeeblocks.foundation.models.ModelContainer;
import coffeeblocks.openal.SoundObject;

public class GameObject{
	
	public enum PropertyEnumeration {
		NONE,
		
		PHYS_ACCEL,PHYS_POS,PHYS_ROT,PHYS_SCALE,
		PHYS_IMPULSE,PHYS_ACTIVATION,PHYS_CLEARFORCE,
		
		MODEL_POS,MODEL_ROT,MODEL_SCALE,
		
		RENDER_TEXTURE,RENDER_SHADER
	}
	
	// TODO : legg til spill-relatert metadata
	
	public GameObject(){
		
	}
	
	private String id = null;
	public synchronized void setObjectId(String id){
		this.id = id;
	}
	public String getObjectId(){
		return id;
	}
	
	private ModelContainer gameModel = null;
	public ModelContainer getGameModel(){
		return gameModel;
	}
	public synchronized void setGameModel(ModelContainer gameModel){
		this.gameModel = gameModel;
	}
	
	private GameMetaData gameData = new GameMetaData();
	public GameMetaData getGameData(){
		return gameData;
	}
	public synchronized void setGameData(GameMetaData gameData){
		this.gameData = gameData;
	}
	
	private SoundObject soundBox = null;
	public SoundObject getSoundBox() {
		return soundBox;
	}
	public void setSoundBox(SoundObject soundBox) {
		this.soundBox = soundBox;
	}

}
