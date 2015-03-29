package coffeeblocks.foundation;

public interface CoffeeRendererListener {
	default public void onGlfwReady(){}
	default public void onGlfwFrameTick(){}
	default public void onGlfwQuit(){}
}
