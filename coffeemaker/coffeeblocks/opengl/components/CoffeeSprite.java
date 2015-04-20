package coffeeblocks.opengl.components;

import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.metaobjects.InstantiableObject;

public class CoffeeSprite {
	public CoffeeSprite(InstantiableObject source){
		this.sourceObject = source;
	}
	private InstantiableObject sourceObject = null;
	public GameObject createSprite(String id,String texture){
		GameObject out = sourceObject.createInstance("."+id+(int)Math.random()*1000, true);
		out.getGameModel().getMaterial().setDiffuseTexture(texture);
		return out;
	}
}
