package coffeeblocks.foundation.models;

import java.util.ArrayList;
import java.util.List;

public abstract class RawModelReader implements ModelReader {

	// TODO : ta i bruk FileImporter for fillesing!
	
	private void interpretFace(String line){
		List<List<Float>> face = new ArrayList<>();
		int begin = line.indexOf('{');
		int end = line.indexOf('}', line.length()-1);
		String faceStr = line.substring(begin+1, end);
		for(int a=0;a<3;a++){
			begin = faceStr.indexOf('{');
			end = faceStr.indexOf('}');
			face.add(interpretFloatArray(faceStr));
			faceStr = faceStr.substring(end+1,faceStr.length());
		}
		model.add(face);
	}
	
	private List<Float> interpretFloatArray(String rawtext){
		List<Float> vertex = new ArrayList<>();
		int begin = rawtext.indexOf('{');
		int end = rawtext.indexOf('}');
		String vertStr = rawtext.substring(begin+1,end);
		for(String p : vertStr.split(","))
			vertex.add(Float.valueOf(p));
		return vertex;
	}
	
	private void interpretLine(String line){
		String[] linePieces = line.split(":");
		interpretFace(linePieces[0]);
	}

	private List<List<List<Float>> > model = null;
	
	public List<List<List<Float>>> interpretFile(List<String> data) {
		model = new ArrayList<>();
		
		for(String line : data)
			interpretLine(line);
		
		return model;
	}

}
