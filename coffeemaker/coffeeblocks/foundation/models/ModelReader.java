package coffeeblocks.foundation.models;

import java.util.List;

public interface ModelReader {
	default public void setTargetObject(ModelContainer object){}
	public List<Float> interpretFile(List<String> data, String filename);
}
