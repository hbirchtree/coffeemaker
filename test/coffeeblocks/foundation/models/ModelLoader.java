package coffeeblocks.foundation.models;

import java.util.List;
import coffeeblocks.general.FileImporter;

public class ModelLoader {
	public static ModelContainer loadModel(String filename){
		List<String> data = FileImporter.readFile(filename);
		
		ModelContainer container = new ModelContainer();
		ModelReader reader = null;
		if(filename.endsWith(".obj"))
			reader = new WavefrontModelReader();
//		else
//			reader = new RawModelReader();
		reader.setTargetObject(container);
		try{
			container.setModelFaces(reader.interpretFile(data,filename));
		}
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			return null;
		}
		
		return container;
	}
	
}
