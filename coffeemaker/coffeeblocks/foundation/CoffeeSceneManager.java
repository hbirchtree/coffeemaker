package coffeeblocks.foundation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import coffeeblocks.foundation.input.CoffeeGlfwInputListener;
import coffeeblocks.opengl.CoffeeRenderer;

public class CoffeeSceneManager implements CoffeeRendererListener{
	//CoffeeGameObjectManager inneholder en scene, fullstendig med objekter, kamera og lys.
	//Denne klassen laster detaljene inn i CoffeeRenderer for å bytte scene og dermed objekter
	private boolean sceneToBeApplied = false;
	private String sceneApply = "";
	
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
		renderer.addCoffeeListener(scene.getPhysicsSystem());
		renderer.setScene(scene);
		sceneToBeApplied = false;
		sceneApply = "";
	}
	
	public Collection<String> getScenes(){
		return scenes.keySet();
	}
	@Override
	public void onGlfwFrameTick(){
		if(sceneToBeApplied)
			applyScene(sceneApply);
	}
}
