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
	
	private String assetSource = null;
	public String getAssetSource() {
		return assetSource;
	}
	public void setAssetSource(String assetSource){
		this.assetSource = assetSource;
	}
	
	private boolean loaded = false;
	private boolean ready = false;
	List<String> testFiles = new ArrayList<>();

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		testFiles.add("/home/havard/test5.obj");
		testFiles.add("/home/havard/test_uv.obj");
		
		for(String filename : testFiles)
			loadObject(filename);
		
		loaded = true;
	}
	
	private void loadObject(String filename){
		GameObject obj = new GameObject(null);
		ModelContainer model = ModelLoader.loadModel(filename);
		if(model==null)
			return;
//		model.scale.x = model.scale.y = model.scale.z = 0.5f;
		obj.setGameModel(model);
		manager.addObject(obj);
	}

	@Override
	public synchronized void onGlfwReady(){
		if(!loaded||ready)
			return;
		
		for(GameObject object : manager.getObjectList())
			renderer.addModel(object.getGameModel());
		
		for(CoffeeSynchronizerWaiter waiter : waiters)
			waiter.processFinished();
		ready = true;
	}
}
