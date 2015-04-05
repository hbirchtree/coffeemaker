package coffeeblocks.scripting;

import java.io.FileNotFoundException;

import javax.script.Bindings;
import javax.script.SimpleBindings;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import coffeeblocks.foundation.CoffeeSceneManager;
import coffeeblocks.foundation.physics.CollisionChecker;
import coffeeblocks.opengl.CoffeeRenderer;

public class CoffeeBrewery {
	private Globals globals = JsePlatform.standardGlobals();
	
	private CoffeeRenderer renderer = null;
	private CoffeeSceneManager manager = null;
	private CollisionChecker lhc = null;
	public CoffeeBrewery(String logicFile,CoffeeRenderer renderer, CoffeeSceneManager manager, CollisionChecker lhc) throws FileNotFoundException{
		this.renderer = renderer;
		this.manager = manager;
		this.lhc = lhc;
		
		LuaValue chunk = globals.loadfile(logicFile);
		chunk.load(new SceneManagerBinding(manager));
		chunk.call(LuaValue.valueOf(logicFile));
	}
}
