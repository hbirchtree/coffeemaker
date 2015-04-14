package coffeeblocks.foundation;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

public class GameMetaData {
	//Meningen med denne klassen er å holde verdier relatert til objektet. Dette kan programmeres fra og benyttes i en eventloop, men er ikke knyttet opp mot et spesifik formål.
	//Den skal heller være et generelt grunnlag å bygge på, som tillater flere muligheter og samtidig ordnet kode
	private Map<String,Boolean> boolValues = new HashMap<>();
	private Map<String,String> stringValues = new HashMap<>();
	private Map<String,Long> timerValues = new HashMap<>();
	private Map<String,Double> doubleValues = new HashMap<>();
	private Map<String,Integer> intValues = new HashMap<>();
	private Map<String,Vector3f> vectorValues = new HashMap<>();
	
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
	
	public void setVectorValue(String key, Vector3f value){
		vectorValues.put(key, value);
	}
	public Vector3f getVectorValue(String key){
		return vectorValues.get(key);
	}
}
