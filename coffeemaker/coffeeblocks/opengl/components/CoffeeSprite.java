package coffeeblocks.opengl.components;

import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.metaobjects.InstantiableObject;

public class CoffeeSprite {
	public CoffeeSprite(InstantiableObject source){
		this.sourceObject = source;
	}
	private InstantiableObject sourceObject = null;
	public GameObject createSprite(String texture){
		GameObject out = sourceObject.createInstance("."+Math.random(), true);
		out.getGameModel().getMaterial().setDiffuseTexture(texture);
		return out;
	}
}
