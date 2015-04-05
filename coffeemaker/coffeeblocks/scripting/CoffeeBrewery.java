package coffeeblocks.scripting;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.luaj.vm2.ast.Chunk;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.Visitor;
import org.luaj.vm2.parser.LuaParser;
import org.luaj.vm2.parser.ParseException;

import coffeeblocks.foundation.CoffeeSceneManager;
import coffeeblocks.foundation.physics.CollisionChecker;
import coffeeblocks.opengl.CoffeeRenderer;

public class CoffeeBrewery {
	private CoffeeRenderer renderer = null;
	private CoffeeSceneManager manager = null;
	private CollisionChecker lhc = null;
	public CoffeeBrewery(String logicFile,CoffeeRenderer renderer, CoffeeSceneManager manager, CollisionChecker lhc) throws FileNotFoundException{
		this.renderer = renderer;
		this.manager = manager;
		this.lhc = lhc;
		LuaParser main = new LuaParser(new FileInputStream(logicFile));
		try {
			Chunk chunk = main.Chunk();
			chunk.accept(new Visitor(){
				public void visit(Exp.NameExp exp){
					System.out.println(exp.toString());
				}
			});
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
