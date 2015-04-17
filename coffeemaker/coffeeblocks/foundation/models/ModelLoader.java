package coffeeblocks.foundation.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coffeeblocks.foundation.models.ModelReader.ModelIntermediate;
import coffeeblocks.general.FileImporter;
import coffeeblocks.opengl.components.CoffeeMaterial;
import coffeeblocks.opengl.components.CoffeeVertex;

public class ModelLoader {
	public static class CoffeeModel {
		public CoffeeMaterial material;
		public List<CoffeeVertex> model;
	}
	
	public static ModelContainer loadModel(String filename){
		List<String> data = FileImporter.readFile(filename);
		
		ModelContainer container = new ModelContainer();
		ModelReader reader = null;
		if(filename.endsWith(".obj"))
			reader = new WavefrontModelReader();
		else if(filename.endsWith(".md5mesh"))
			reader = new MD5ModelReader();
		reader.setTargetObject(container);
		try{
			ModelIntermediate mdl = reader.interpretFile(data,filename);
			container.setModelFaces(mdl.model);
			container.setMaterial(mdl.material);
		}
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			return null;
		}
		
		return container;
	}
	public static Map<String,CoffeeModel> loadModelLibrary(String filename){
		List<String> data = FileImporter.readFile(filename);
		
		Map<String,CoffeeModel> container = new HashMap<>();
		ModelReader reader = null;
		if(filename.endsWith(".obj"))
			reader = new WavefrontModelReader();
//		else
//			reader = new RawModelReader();
		try{
			reader.interpretFile(data,filename);
		}
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			return null;
		}
		reader.getModels().values().stream().forEach(e -> {
			CoffeeModel mdl = new CoffeeModel();
			mdl.material = e.material;
			mdl.model = CoffeeAnimationContainer.convertFloatListToVertices(e.model);
			container.put(e.name, mdl);
		});
		
		return container;
	}
}
