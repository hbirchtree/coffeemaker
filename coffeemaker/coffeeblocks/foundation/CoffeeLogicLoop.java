package coffeeblocks.foundation;

import coffeeblocks.foundation.input.CoffeeGlfwInputListener;
import coffeeblocks.opengl.CoffeeRenderer;

public abstract class CoffeeLogicLoop implements CoffeeGlfwInputListener,CoffeeRendererListener{
	protected CoffeeSceneManager manager = null;
	public CoffeeLogicLoop(CoffeeSceneManager manager){
		this.manager = manager;
	}
	public abstract void eventLoop();
}
