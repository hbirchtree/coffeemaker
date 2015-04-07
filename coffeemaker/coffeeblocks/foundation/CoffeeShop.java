package coffeeblocks.foundation;

import java.util.List;

public class CoffeeShop extends CoffeeLogicLoop{
	private String startScene = "main";
	private String currentScene = null;
	public CoffeeShop(CoffeeSceneManager manager) {
		super(manager);
		currentScene = startScene;
	}

	public void eventLoop(){
		
	}

	@Override
	public List<Integer> getRegisteredKeys() {
		// TODO Auto-generated method stub
		return null;
	}
}
