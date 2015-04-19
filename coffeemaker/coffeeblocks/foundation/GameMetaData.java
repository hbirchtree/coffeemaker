package coffeeblocks.foundation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import coffeeblocks.metaobjects.Vector3Container;

public class GameMetaData {
	//Meningen med denne klassen er å holde verdier relatert til objektet. Dette kan programmeres fra og benyttes i en eventloop, men er ikke knyttet opp mot et spesifik formål.
	//Den skal heller være et generelt grunnlag å bygge på, som tillater flere muligheter og samtidig ordnet kode
	private Map<String,Boolean> boolValues = new HashMap<>();
	private Map<String,String> stringValues = new HashMap<>();
	private Map<String,Long> timerValues = new HashMap<>();
	private Map<String,Double> doubleValues = new HashMap<>();
	private Map<String,Integer> intValues = new HashMap<>();
	private Map<String,Vector3Container> vectorValues = new HashMap<>();
	
	public Set<String> getBoolKeys(){
		return boolValues.keySet();
	}
	public Set<String> getStringKeys(){
		return stringValues.keySet();
	}
	public Set<String> getTimerKeys(){
		return timerValues.keySet();
	}
	public Set<String> getDoubleKeys(){
		return doubleValues.keySet();
	}
	public Set<String> getIntKeys(){
		return intValues.keySet();
	}
	public Set<String> getVectorKeys(){
		return vectorValues.keySet();
	}
	
	public GameMetaData(GameMetaData data) {
		data.getBoolKeys().stream().forEach(key -> setBoolValue(key,data.getBoolValue(key)));
		data.getStringKeys().stream().forEach(key -> setStringValue(key,data.getStringValue(key)));
		data.getTimerKeys().stream().forEach(key -> setTimerValue(key,data.getTimerValue(key)));
		data.getIntKeys().stream().forEach(key -> setIntValue(key,data.getIntValue(key)));
		data.getDoubleKeys().stream().forEach(key -> setDoubleValue(key,data.getDoubleValue(key)));
		data.getVectorKeys().stream().forEach(key -> setVectorValue(key,data.getVectorValue(key)));
	}
	public GameMetaData(){}
	public void setStringValue(String key, String value){
		stringValues.put(key, value);
	}
	public String getStringValue(String key){
		if(!stringValues.containsKey(key))
			return "EMPTY STRING!!!";
		return stringValues.get(key);
	}
	
	public void setBoolValue(String key, Boolean value){
		boolValues.put(key, value);
	}
	public Boolean getBoolValue(String key){
		if(!boolValues.containsKey(key))
			return false;
		return boolValues.get(key);
	}
	
	public void setTimerValue(String key, Long value){
		timerValues.put(key, value);
	}
	public Long getTimerValue(String key){
		if(!timerValues.containsKey(key))
			return Long.MAX_VALUE;
		return timerValues.get(key);
	}
	
	public void setDoubleValue(String key, Double value){
		doubleValues.put(key, value);
	}
	public Double getDoubleValue(String key){
		if(!doubleValues.containsKey(key))
			return Double.POSITIVE_INFINITY;
		return doubleValues.get(key);
	}
	
	public void setIntValue(String key, Integer value){
		intValues.put(key, value);
	}
	public Integer getIntValue(String key){
		if(!intValues.containsKey(key))
			return Integer.MAX_VALUE;
		return intValues.get(key);
	}
	
	public void setVectorValue(String key, Vector3Container value){
		vectorValues.put(key, value);
	}
	public Vector3Container getVectorValue(String key){
		return vectorValues.get(key);
	}
}
