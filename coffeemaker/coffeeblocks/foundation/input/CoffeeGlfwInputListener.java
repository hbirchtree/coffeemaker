package coffeeblocks.foundation.input;

import java.util.List;

public interface CoffeeGlfwInputListener{
	default public void coffeeReceiveMouseMove(int x,int y){}
	default public void coffeeReceiveMousePress(int btn){}
	default public void coffeeReceiveMouseRelease(int btn){}
	default public void coffeeReceiveKeyPress(int key){}
	default public void coffeeReceiveKeyRelease(int key){}
	
	public List<Integer> getRegisteredKeys();
	default public boolean getMouseEvents(){return false;}
	default public boolean getKeyboardEvents(){return false;}
}
