package coffeeblocks.foundation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import coffeeblocks.foundation.input.CoffeeGlfwInputListener;
import coffeeblocks.opengl.CoffeeRenderer;

public class CoffeeSceneManager implements CoffeeGlfwInputListener,CoffeeRendererListener{
	//CoffeeGameObjectManager inneholder en scene, fullstendig med objekter, kamera og lys.
	//Denne klassen laster detaljene inn i CoffeeRenderer for å bytte scene og dermed objekter
	private boolean sceneToBeApplied = false;
	private String sceneApply = "";
	
	public CoffeeSceneManager(){
		keys.add(GLFW.GLFW_KEY_KP_9);
		keys.add(GLFW.GLFW_KEY_KP_8);
	}
	
	private CoffeeRenderer renderer = null;
	public void setRenderer(CoffeeRenderer renderer){
		if(renderer==null)
			throw new IllegalArgumentException("Cannot use null renderer!");
		this.renderer = renderer;
	}
	
	private Map<String,CoffeeGameObjectManager> scenes = new HashMap<>();
	public void createNewScene(String name){
		scenes.put(name, new CoffeeGameObjectManager());
	}
	public CoffeeGameObjectManager getScene(String name){
		if(!scenes.containsKey(name))
			throw new IllegalArgumentException("Unable to find specified scene!");
		return scenes.get(name);
	}
	public void scheduleSceneApply(String scene){
		sceneToBeApplied=true;
		sceneApply = scene;
	}
	
	public void applyScene(String name){
		if(!scenes.containsKey(name))
			throw new IllegalArgumentException("Unable to find specified scene!");
		CoffeeGameObjectManager scene = scenes.get(name);
		renderer.clearListeners();
		renderer.addCoffeeListener(this);
		renderer.addInputListener(this);
		renderer.addCoffeeListener(scene.getPhysicsSystem());
		renderer.addInputListener(scene.getInputHandler());
		renderer.setDrawingEnabled(false); //Slår av tegning av objekter
		renderer.setScene(scene);
		renderer.setDrawingEnabled(true);
	}
	
	public Collection<String> getScenes(){
		return scenes.keySet();
	}

	List<Integer> keys = new ArrayList<>();
	@Override
	public List<Integer> getRegisteredKeys() {
		// TODO Auto-generated method stub
		return keys;
	}
	@Override
	public void coffeeReceiveKeyPress(int key){
		if(key==GLFW.GLFW_KEY_KP_9)
			scheduleSceneApply("main");
		if(key==GLFW.GLFW_KEY_KP_8)
			scheduleSceneApply("second");
	}
	@Override
	public boolean getKeyboardEvents(){
		return true;
	}
	@Override
	public void onGlfwFrameTick(){
		if(sceneToBeApplied)
			applyScene(sceneApply);
	}
}
