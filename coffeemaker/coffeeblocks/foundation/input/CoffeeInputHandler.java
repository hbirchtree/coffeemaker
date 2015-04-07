package coffeeblocks.foundation.input;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import coffeeblocks.foundation.CoffeeGameObjectManager;
import coffeeblocks.general.VectorTools;
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
	
	private float walkingForce = 3f;
	@Override
	public void coffeeReceiveKeyPress(int key) {
		// TODO Auto-generated method stub
		if(key==GLFW_KEY_W){
			manager.getObject("player").getGameModel().setPositionalAcceleration(manager.getCamera().getCameraForwardVec(walkingForce));
			manager.requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
		}
		if(key==GLFW_KEY_A){
			manager.getObject("player").getGameModel().setPositionalAcceleration(manager.getCamera().getCameraRightVec(-walkingForce));
			manager.requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
		}
		if(key==GLFW_KEY_S){
			manager.getObject("player").getGameModel().setPositionalAcceleration(manager.getCamera().getCameraForwardVec(-walkingForce));
			manager.requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
		}
		if(key==GLFW_KEY_D){
			manager.getObject("player").getGameModel().setPositionalAcceleration(manager.getCamera().getCameraRightVec(walkingForce));
			manager.requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
		}
		if(key==GLFW_KEY_SPACE){
			manager.getObject("player").getGameModel().setPositionalAcceleration(VectorTools.vectorLimit(VectorTools.vectorMul(new Vector3f(0,1,0), 5.0f), 5.0f));
			manager.requestObjectUpdate("player", GameObject.PropertyEnumeration.PHYS_ACCEL);
		}
		if(key==GLFW_KEY_KP_0){
			manager.getObject("player").getGameModel().setPosition(new Vector3f(0,15,0));
		}
		if(key==GLFW_KEY_KP_5){
			manager.getObject("skybox").getGameModel().selectTexture = 0;
		}
		if(key==GLFW_KEY_KP_6){
			manager.getObject("skybox").getGameModel().selectTexture = 1;
		}
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
