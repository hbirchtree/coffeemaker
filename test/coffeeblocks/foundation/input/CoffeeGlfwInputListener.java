package coffeeblocks.foundation.input;

import java.util.List;

public interface CoffeeGlfwInputListener{
	public void coffeeReceiveMouseMove(int x,int y);
	public void coffeeReceiveMousePress(int btn);
	public void coffeeReceiveMouseRelease(int btn);
	public void coffeeReceiveKeyPress(int key);
	public void coffeeReceiveKeyRelease(int key);
	
	public List<Integer> getRegisteredKeys();
	public boolean getMouseEvents();
}
