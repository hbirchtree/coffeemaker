package coffeeblocks.metaobjects;

import coffeeblocks.foundation.GameMetaData;
import coffeeblocks.foundation.models.ModelContainer;
import coffeeblocks.openal.SoundObject;

public class GameObject extends InstantiableObject{
	
	public enum PropertyEnumeration {
		NONE,
		
		PHYS_ACCEL,PHYS_POS,PHYS_ROT,PHYS_SCALE,
		PHYS_IMPULSE,PHYS_ACTIVATION,PHYS_CLEARFORCE,
		PHYS_GRAVITY,
		
		MODEL_POS,MODEL_ROT,MODEL_SCALE,
		
		RENDER_TEXTURE,RENDER_SHADER
	}
	
	// TODO : legg til spill-relatert metadata
	
	public GameObject(){
		
	}
	
	public GameObject(String id,InstantiableObject source){
		this(source);
		this.id = id;
	}
	public GameObject(InstantiableObject source){
		gameModel = new ModelContainer(source.getGameModel());
		gameData = new GameMetaData(source.getGameData());
		source.getSoundBox().stream().forEach(e -> soundBox.add(new SoundObject(e)));
	}
	
	private String id = null;
	public synchronized void setObjectId(String id){
		this.id = id;
	}
	public String getObjectId(){
		return id;
	}

}
