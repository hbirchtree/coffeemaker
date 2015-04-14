package coffeeblocks.metaobjects;

import java.util.ArrayList;
import java.util.List;

import coffeeblocks.foundation.GameMetaData;
import coffeeblocks.foundation.models.ModelContainer;
import coffeeblocks.openal.SoundObject;

public class InstantiableObject {
	private boolean instancedObject = false;
	public boolean isInstancedObject(){
		return instancedObject;
	}
	public void setInstancedObject(boolean instancedObject){
		this.instancedObject = instancedObject;
	}
	
	private String objectPrefix = null;
	public String getObjectPrefix() {
		return objectPrefix;
	}
	public void setObjectPrefix(String objectPrefix) {
		this.objectPrefix = objectPrefix;
	}
	
	private String preseedName = null;
	public String getObjectPreseedName(){
		return preseedName; 
	}
	public void setObjectPreseedName(String preseed){
		this.preseedName = preseed;
	}
	
	public GameObject createInstance(){ //Brukes for å lage statiske objekter
		return createInstance(preseedName,false);
	}
	public GameObject createInstance(String id,boolean prefix){ //Brukes for alle objekter, inkludert instanser
		GameObject result = new GameObject(this);
		if(prefix)
			result.setObjectId(objectPrefix+id);
		else
			result.setObjectId(id);
		return result;
	}

	protected ModelContainer gameModel = null;
	public ModelContainer getGameModel(){
		return gameModel;
	}
	public synchronized void setGameModel(ModelContainer gameModel){
		this.gameModel = gameModel;
	}
	
	protected GameMetaData gameData = new GameMetaData();
	public GameMetaData getGameData(){
		return gameData;
	}
	public synchronized void setGameData(GameMetaData gameData){
		this.gameData = gameData;
	}
	
	protected List<SoundObject> soundBox = new ArrayList<>();
	public List<SoundObject> getSoundBox() {
		return soundBox;
	}
	public void addSoundBox(SoundObject soundBox) {
		this.soundBox.add(soundBox);
	}
}
