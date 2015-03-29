package coffeeblocks;

import java.util.ArrayList;
import java.util.List;

import coffeeblocks.foundation.CoffeeGameObjectManager;
import coffeeblocks.foundation.CoffeeRendererListener;
import coffeeblocks.foundation.input.CoffeeInputHandler;
import coffeeblocks.opengl.CoffeeRenderer;
import static org.lwjgl.glfw.GLFW.*;

public class CoffeeMaker implements CoffeeRendererListener{
	
	// TODO : lag klasse for spill-logikk
	
	private CoffeeInputHandler inputHandler = null;
	private CoffeeGameObjectManager objManager = null;
	private CoffeeRenderer renderer = null;
	private BackgroundWorker backgroundWorker = null;
	private Thread renderingThread = null;
	private Thread backgroundWorkerThread = null;
	
	public static void main(String[] args){
		CoffeeMaker main = new CoffeeMaker();
		main.rendererSpawn();
	}
	public void rendererSpawn(){
		objManager = new CoffeeGameObjectManager();
		inputHandler = new CoffeeInputHandler(objManager);
		List<Integer> inputKeys = new ArrayList<Integer>();
		inputKeys.add(GLFW_KEY_UP);
		inputKeys.add(GLFW_KEY_DOWN);
		inputKeys.add(GLFW_KEY_KP_0);
		inputHandler.addRegisteredKeys(inputKeys);
		
		renderer = new CoffeeRenderer();

		backgroundWorker = new BackgroundWorker();
		backgroundWorker.setRenderer(renderer);
		backgroundWorker.setManager(objManager);
		backgroundWorkerThread = new Thread(backgroundWorker);
		backgroundWorkerThread.start();
		
		renderer.addCoffeeListener(this);
		renderer.addCoffeeListener(objManager);
		renderer.addCoffeeListener(backgroundWorker);
		renderer.addInputListener(inputHandler);
		renderingThread = new Thread(renderer);
		renderingThread.start();
	}
	@Override
	public void onGlfwQuit() {
		// TODO Auto-generated method stub
		System.exit(0);
	}
}
