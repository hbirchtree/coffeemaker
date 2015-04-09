package coffeeblocks.foundation;

import coffeeblocks.foundation.input.CoffeeGlfwInputListener;
import coffeeblocks.foundation.physics.CollisionListener;

public abstract class CoffeeLogicLoop implements CoffeeGlfwInputListener,CoffeeRendererListener,CollisionListener{
	protected CoffeeSceneManager manager = null;
	public CoffeeLogicLoop(CoffeeSceneManager manager){
		this.manager = manager;
	}
	public abstract void eventLoop();
}
