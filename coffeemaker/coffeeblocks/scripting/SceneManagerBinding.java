package coffeeblocks.scripting;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import coffeeblocks.foundation.CoffeeSceneManager;

public class SceneManagerBinding extends TwoArgFunction {
	private CoffeeSceneManager source = null;
	public SceneManagerBinding(CoffeeSceneManager source){
		this.source=source;
	}
	public LuaValue call(LuaValue modname,LuaValue env){
		LuaTable lib = new LuaTable();
		lib.set("clearcolor", new LuaSceneManager(source));
//		env.set("sceneman", lib);
		env.get("package").get("loaded").set("sceneman", lib);
		return lib;
	}
	private class LuaSceneManager extends OneArgFunction{
		private CoffeeSceneManager source = null;
		public LuaSceneManager(CoffeeSceneManager source){
			this.source=source;
		}
		@Override
		public LuaValue call(LuaValue arg) {
			// TODO Auto-generated method stub
			return LuaValue.valueOf(source.getScene("main").getClearColor().toString());
		}
	}
}
