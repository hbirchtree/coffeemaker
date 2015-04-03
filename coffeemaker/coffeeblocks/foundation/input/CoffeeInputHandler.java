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
	
	private float walkingForce = 5f;
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
		if(key==GLFW_KEY_W){
			manager.getObject("player").getGameModel().positionalAcceleration = manager.getCamera().getCameraForwardVec(walkingForce);
			manager.getObject("player").getGameModel().positionalAcceleration.y = 0;
			manager.requestObjectUpdate("player");
		}
		if(key==GLFW_KEY_A){
			manager.getObject("player").getGameModel().positionalAcceleration = manager.getCamera().getCameraRightVec(-walkingForce);
			manager.getObject("player").getGameModel().positionalAcceleration.y = 0;
			manager.requestObjectUpdate("player");
		}
		if(key==GLFW_KEY_S){
			manager.getObject("player").getGameModel().positionalAcceleration = manager.getCamera().getCameraForwardVec(-walkingForce);
			manager.getObject("player").getGameModel().positionalAcceleration.y = 0;
			manager.requestObjectUpdate("player");
		}
		if(key==GLFW_KEY_D){
			manager.getObject("player").getGameModel().positionalAcceleration = manager.getCamera().getCameraRightVec(walkingForce);
			manager.getObject("player").getGameModel().positionalAcceleration.y = 0;
			manager.requestObjectUpdate("player");
		}
		if(key==GLFW_KEY_SPACE){
			manager.getObject("player").getGameModel().positionalAcceleration = VectorTools.vectorLimit(VectorTools.vectorMul(new Vector3f(0,1,0), 9.81f), 9.8f);
			manager.requestObjectUpdate("player");
		}
		if(key==GLFW_KEY_KP_0){
			manager.getObject("player").getGameModel().setPosition(new Vector3f(0,15,0));
			manager.requestObjectUpdate("player");
		}
//		if(glfwGetKey(window,GLFW_KEY_F3)==1&&glfwGetTime()>=controlDelay){
//			draw=!draw;
//			controlDelay = glfwGetTime()+0.2;
//		}
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
