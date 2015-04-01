package coffeeblocks.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.*;

public class JsonParser {
	public static Map<String,Object> parseFile(String filename){
		//Skal analysere JSON-filen rekursivt frem til den kun inneholder Javas egne datatyper.
		
		String data = FileImporter.readFileToString(filename);
		
		JSONObject rootObject = new JSONObject(data);
		Map<String,Object> result = parseObject(rootObject);
		return result;
	}
	private static Map<String,Object> parseObject(JSONObject dataSrc){
		Map<String,Object> data = new HashMap<>();
		for(String key : dataSrc.keySet()){
			Object obj = dataSrc.get(key);
			if(obj instanceof JSONObject){
				data.put(key, parseObject(((JSONObject)obj)));
			}else if(obj instanceof JSONArray){
				data.put(key, parseArray(((JSONArray)obj)));
			}else{
				data.put(key, obj);
			}
		}
		return data;
	}
	private static List<Object> parseArray(JSONArray dataSrc){
		List<Object> data = new ArrayList<>();
		//JSONArray kan ikke brukes med foreach-iterasjon?
		for(int i=0;i<dataSrc.length();i++){
			Object obj = dataSrc.get(i);
			if(obj instanceof JSONObject){
				data.add(parseObject(((JSONObject)obj)));
			}else if(obj instanceof JSONArray){
				data.add(parseArray(((JSONArray)obj)));
			}else{
				data.add(obj);
			}
		}
		return data;
	}
}
