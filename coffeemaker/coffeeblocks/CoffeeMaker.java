package coffeeblocks;

import java.util.Map;

import coffeeblocks.foundation.CoffeeRendererListener;
import coffeeblocks.foundation.CoffeeSceneManager;
import coffeeblocks.general.JsonParser;
import coffeeblocks.opengl.CoffeeRenderer;

public class CoffeeMaker implements CoffeeRendererListener{
	
	// TODO : lag klasse for spill-logikk
	private CoffeeSceneManager sceneManager = new CoffeeSceneManager();
	private CoffeeRenderer renderer = null;
	private Thread renderingThread = null;
	
	public static void main(String[] args){
		CoffeeMaker main = new CoffeeMaker();
		if(args.length<1||args[0].isEmpty()){
			System.out.println("Ingen hovedfil ble spesifisert. Denne er nødvendig for at motoren skal kunne starte og fungere."
					+"Vær vennlig og spesifiser en slik fil, ofte under navnet main.json");
			return;
		}
		main.parseMainFile(args[0]);
		main.rendererSpawn();
	}
	public void parseMainFile(String filename){
		Map<String,Object> properties = JsonParser.parseFile(filename);

		if(properties.isEmpty())
			throw new IllegalStateException("Ingen data");
		
		renderer = new CoffeeRenderer();
		sceneManager.setRenderer(renderer);
		renderer.addCoffeeListener(this);
		
		CoffeeJsonParsing.parseSceneStructure(filename.substring(0, filename.indexOf("/", -1)+1),properties, sceneManager);
		if(sceneManager.getScenes().size()==0){
			System.out.println("Kunne ikke finne noen scene å rendre!");
			System.exit(1);
		}
	}
	public void rendererSpawn(){
		sceneManager.applyScene("main");
		renderingThread = new Thread(renderer);
		renderingThread.start();
	}
	@Override
	public void onGlfwQuit() {
		// TODO Auto-generated method stub
		System.exit(0);
	}
}
