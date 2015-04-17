package coffeeblocks.foundation.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import coffeeblocks.foundation.models.ModelReader.ModelIntermediate;
import coffeeblocks.opengl.components.CoffeeMaterial;

public interface ModelReader {
	public class ModelIntermediate {
		public CoffeeMaterial material = null;
		public String name = null;
		public String mtl = null;

		public List<Float> model = new ArrayList<>();

		public List<List<Integer>> faces = new ArrayList<>();

		public List<List<Float>> vertices = new ArrayList<>();
		public List<List<Float>> texCoords = new ArrayList<>();
		public List<List<Float>> normals = new ArrayList<>();
	}

	default public void setTargetObject(ModelContainer object){}
	public ModelIntermediate interpretFile(List<String> data, String filename);
	Map<String, ModelIntermediate> getModels();
	Map<String, CoffeeMaterial> getMaterials();
}
