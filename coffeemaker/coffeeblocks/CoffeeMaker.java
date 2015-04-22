package coffeeblocks;

import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import coffeeblocks.foundation.CoffeeSceneManager;
import coffeeblocks.foundation.logic.CoffeeLogicLoop;
import coffeeblocks.foundation.logic.CoffeeShop;
import coffeeblocks.general.JsonParser;
import coffeeblocks.interfaces.listeners.CoffeeRendererListener;
import coffeeblocks.opengl.CoffeeRenderer;

public class CoffeeMaker implements CoffeeRendererListener{
	
	// TODO : lag klasse for spill-logikk
	private CoffeeSceneManager sceneManager = new CoffeeSceneManager();
	private CoffeeRenderer renderer = null;
	private Thread renderingThread = null;
	private CoffeeLogicLoop logic = null;
	
	private UncaughtExceptionHandler rendererEH = new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				// TODO Auto-generated method stub
				System.err.println(e.getMessage());
				System.exit(1);
			}
		};

	public static void main(String[] args){
		CoffeeMaker main = new CoffeeMaker();
		if(args.length<1||args[0].isEmpty()){
			System.out.println("Ingen hovedfil ble spesifisert. Denne er nødvendig for at motoren skal kunne starte og fungere."
					+"Vær vennlig og spesifiser en slik fil, ofte under navnet main.json");
			return;
		}
		main.lhcStart(new File(args[0]));
	}
	public static void testSystem(){
		//Systemet behøver OpenGL 3.3 for å kjøre spillet
		System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
		System.out.println("Testing system capabilities...");
		long window;
		try {
			if(glfwInit()!=GL_TRUE)
				throw new IllegalStateException("Unable to initialize GLFW");

			glfwDefaultWindowHints();
			glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
			glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
			
			window = glfwCreateWindow(1, 1, "Café OpenGL test", NULL, NULL);
			if(window==NULL)
				throw new RuntimeException("Failed to create the GLFW window");
			glfwMakeContextCurrent(window);
			GLContext ctxt = GLContext.createFromCurrent();
			ContextCapabilities caps = ctxt.getCapabilities();
			if(!caps.OpenGL33)
				throw new IllegalStateException("Ooops! This system does *not* seem to report OpenGL 3.3 compatiblity. You will probably encounter problems. :(");

			System.out.println("If you are seeing this, you passed the critical test! Yay!");
			glfwDestroyWindow(window);
		} finally {
			glfwTerminate();
		}
	}
	public void lhcStart(File mainfile){
		String filename = mainfile.getAbsolutePath();
		
		if(System.getProperty("os.name").startsWith("Windows"))
			filename = filename.replace('\\', '/'); //Fordi Java ikke vil konvertere navnet den får :(
		
		//Viktig! Brukes for å hente LWJGL's biblioteker uten å spesifisere obskure argumenter til Java hver gang!
		System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
		
		Map<String,Object> properties = JsonParser.parseFile(filename);

		if(properties.isEmpty())
			throw new IllegalStateException("Ingen data");
		
		Map<String,String> startOpts = new HashMap<>();
		CoffeeJsonParsing.parseSceneStructure(filename.substring(0, filename.lastIndexOf("/")+1),properties, sceneManager, startOpts);
		if(sceneManager.getScenes().size()==0){
			System.out.println("Kunne ikke finne noen scene å rendre!");
			System.exit(1);
		}

		renderer = new CoffeeRenderer();
		sceneManager.setRenderer(renderer);
		renderer.addCoffeeListener(this);
		logic = new CoffeeShop(sceneManager);
		logic.setFont(startOpts.get("font.obj"), startOpts.get("font.src"));
		
		mayThyComputerNotBurn();
	}
	public void mayThyComputerNotBurn(){
		renderingThread = new Thread(renderer);
		renderingThread.setUncaughtExceptionHandler(rendererEH);
		renderingThread.start();
		
		renderer.addCoffeeListener(logic);
		try {
			logic.eventLoop();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onGlfwQuit() {
		// TODO Auto-generated method stub
		System.exit(0);
	}
}
