package coffeeblocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import coffeeblocks.foundation.CoffeeGameObjectManager;
import coffeeblocks.foundation.CoffeeRendererListener;
import coffeeblocks.foundation.input.CoffeeInputHandler;
import coffeeblocks.foundation.physics.CollisionChecker;
import coffeeblocks.general.JsonParser;
import coffeeblocks.opengl.CoffeeRenderer;
import static org.lwjgl.glfw.GLFW.*;

public class CoffeeMaker implements CoffeeRendererListener{
	
	// TODO : lag klasse for spill-logikk
	
	private CoffeeInputHandler inputHandler = null;
	private CoffeeGameObjectManager objManager = null;
	private CoffeeRenderer renderer = null;
	private CollisionChecker lhc = null;
	private BackgroundWorker backgroundWorker = null;
	private Thread renderingThread = null;
//	private Thread backgroundWorkerThread = null;
	
	public static void main(String[] args){
		CoffeeMaker main = new CoffeeMaker();
		if(args.length<1||args[0].isEmpty()){
			System.out.println("Ingen hovedfil ble spesifisert. Denne er nødvendig for at motoren skal kunne starte og fungere.");
			System.out.println("Vær vennlig og spesifiser en slik fil, ofte under navnet main.json");
			return;
		}
		main.parseMainFile(args[0]);
		main.rendererSpawn();
	}
	public void parseMainFile(String filename){
		Map<String,Object> properties = JsonParser.parseFile(filename);

		if(properties.isEmpty())
			throw new IllegalStateException("Ingen data");

		//Vi initialiserer hovedkomponentene
		renderer = new CoffeeRenderer();
		objManager = new CoffeeGameObjectManager();
		inputHandler = new CoffeeInputHandler(objManager);
		backgroundWorker = new BackgroundWorker();
		lhc = new CollisionChecker(objManager);
		objManager.addListener(lhc);
		lhc.addCollisionListener(objManager);
		renderer.addCoffeeListener(this);
		renderer.addCoffeeListener(objManager);
		renderer.addCoffeeListener(backgroundWorker);
		renderer.addCoffeeListener(lhc);
		renderer.addInputListener(inputHandler);
		backgroundWorker.setRenderer(renderer);
		backgroundWorker.setManager(objManager);
		
		CoffeeJsonParsing.parseSceneStructure(filename.substring(0, filename.indexOf("/", -1)+1),properties, objManager,renderer);
		
	}
	public void rendererSpawn(){
		List<Integer> inputKeys = new ArrayList<Integer>();
		inputKeys.add(GLFW_KEY_UP);
		inputKeys.add(GLFW_KEY_DOWN);
		inputKeys.add(GLFW_KEY_KP_0);
		inputKeys.add(GLFW_KEY_W);
		inputKeys.add(GLFW_KEY_A);
		inputKeys.add(GLFW_KEY_S);
		inputKeys.add(GLFW_KEY_D);
		inputKeys.add(GLFW_KEY_SPACE);
		inputHandler.addRegisteredKeys(inputKeys);
		backgroundWorker.run();
		
		renderingThread = new Thread(renderer);
		renderingThread.start();
		
		while(true){
			
			
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	public void onGlfwQuit() {
		// TODO Auto-generated method stub
		System.exit(0);
	}
}
