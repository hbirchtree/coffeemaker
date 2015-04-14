package coffeeblocks.foundation.logic;

import coffeeblocks.foundation.CoffeeSceneManager;
import coffeeblocks.interfaces.listeners.CoffeeGlfwInputListener;
import coffeeblocks.interfaces.listeners.CoffeeRendererListener;
import coffeeblocks.interfaces.listeners.CollisionListener;

public abstract class CoffeeLogicLoop implements CoffeeGlfwInputListener,CoffeeRendererListener,CollisionListener{
	protected CoffeeSceneManager manager = null;
	public CoffeeLogicLoop(CoffeeSceneManager manager){
		this.manager = manager;
	}
	public abstract void eventLoop() throws InterruptedException;
}
