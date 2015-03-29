package coffeeblocks.metaobjects;

import coffeeblocks.foundation.GameMetaData;
import coffeeblocks.foundation.models.ModelContainer;

public class GameObject{
	
	// TODO : legg til spill-relatert metadata
	
	public GameObject(String dataSource){
		
	}
	
	private ModelContainer gameModel = null;
	public synchronized ModelContainer getGameModel(){
		return gameModel;
	}
	public synchronized void setGameModel(ModelContainer gameModel){
		this.gameModel = gameModel;
	}
	
	private GameMetaData gameData = null;
	public synchronized GameMetaData getGameData(){
		return gameData;
	}
	public synchronized void setGameData(GameMetaData gameData){
		this.gameData = gameData;
	}
	
}
