package coffeeblocks.foundation.input;

import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.glfw.GLFW.*;

import coffeeblocks.foundation.CoffeeGameObjectManager;
import coffeeblocks.metaobjects.GameObject;

public class CoffeeInputHandler implements CoffeeGlfwInputListener{

	private CoffeeGameObjectManager manager = null;
	
	public CoffeeInputHandler(CoffeeGameObjectManager man){
		if(man==null)
			throw new IllegalArgumentException("Input handler cannot operate with null object manager!");
		this.manager = man;
	}
	
	@Override
	public void coffeeReceiveMouseMove(int x, int y) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void coffeeReceiveMousePress(int btn) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void coffeeReceiveMouseRelease(int btn) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void coffeeReceiveKeyPress(int key) {
		// TODO Auto-generated method stub
		if(key==GLFW_KEY_UP)
			for(GameObject object : manager.getObjectList())
				object.getGameModel().rotationalVelocity.y = 0.4f;
		if(key==GLFW_KEY_KP_0)
			for(GameObject object : manager.getObjectList())
				object.getGameModel().rotationalVelocity.y = 0f;
		if(key==GLFW_KEY_DOWN)
			for(GameObject object : manager.getObjectList())
				object.getGameModel().rotationalVelocity.y = -0.4f;
	}
	@Override
	public void coffeeReceiveKeyRelease(int key) {
		// TODO Auto-generated method stub
		
	}

	private List<Integer> registeredKeys = new ArrayList<>();
	public void addRegisteredKeys(List<Integer> registeredKeys) {
		this.registeredKeys.addAll(registeredKeys);
	}
	@Override
	public List<Integer> getRegisteredKeys() {
		// TODO Auto-generated method stub
		return registeredKeys;
	}

	@Override
	public boolean getMouseEvents() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
