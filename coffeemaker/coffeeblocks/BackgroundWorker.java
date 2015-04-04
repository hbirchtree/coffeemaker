package coffeeblocks;

import java.util.List;
import java.util.ArrayList;

import coffeeblocks.foundation.CoffeeGameObjectManager;
import coffeeblocks.foundation.CoffeeRendererListener;
import coffeeblocks.foundation.models.ModelContainer;
import coffeeblocks.foundation.models.ModelLoader;
import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.opengl.CoffeeRenderer;
import coffeeblocks.threads.CoffeeSynchronizerWaiter;

public class BackgroundWorker implements Runnable,CoffeeRendererListener {
	
	List<CoffeeSynchronizerWaiter> waiters = new ArrayList<>();
	public void addWaiter(CoffeeSynchronizerWaiter waiter){
		waiters.add(waiter);
	}
	
	private CoffeeRenderer renderer = null;
	public CoffeeRenderer getRenderer() {
		return renderer;
	}
	public void setRenderer(CoffeeRenderer renderer) {
		this.renderer = renderer;
	}
	
	private CoffeeGameObjectManager manager = null;
	public CoffeeGameObjectManager getManager() {
		return manager;
	}
	public void setManager(CoffeeGameObjectManager manager) {
		this.manager = manager;
	}
	
	private List<String> preload = new ArrayList<>();
	public void addPreload(String objectFile){
		if(objectFile!=null)
			preload.add(objectFile);
	}
	
	private boolean loaded = false;
	private boolean ready = false;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		for(String filename : preload)
			loadObject(filename);
		
		loaded = true;
	}
	
	private void loadObject(String filename){
		GameObject obj = new GameObject();
		ModelContainer model = ModelLoader.loadModel(filename);
		if(model==null)
			return;
		obj.setGameModel(model);
		manager.addObject(obj);
	}

	@Override
	public synchronized void onGlfwReady(){
		if(!loaded||ready)
			return;
		
		
		
		for(CoffeeSynchronizerWaiter waiter : waiters)
			waiter.processFinished();
		ready = true;
	}
}
