package coffeeblocks.foundation;

import coffeeblocks.foundation.input.CoffeeGlfwInputListener;

public abstract class CoffeeLogicLoop implements CoffeeGlfwInputListener{
	protected CoffeeSceneManager manager = null;
	public CoffeeLogicLoop(CoffeeSceneManager manager){
		this.manager = manager;
	}
	public abstract void eventLoop();
}
