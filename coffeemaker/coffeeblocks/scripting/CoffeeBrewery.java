package coffeeblocks.scripting;

import coffeeblocks.foundation.CoffeeGameObjectManager;
import coffeeblocks.foundation.physics.CollisionChecker;
import coffeeblocks.opengl.CoffeeRenderer;

public class CoffeeBrewery {
	private CoffeeRenderer renderer = null;
	private CoffeeGameObjectManager manager = null;
	private CollisionChecker lhc = null;
	public CoffeeBrewery(CoffeeRenderer renderer, CoffeeGameObjectManager manager, CollisionChecker lhc){
		this.renderer = renderer;
		this.manager = manager;
		this.lhc = lhc;
	}
}
