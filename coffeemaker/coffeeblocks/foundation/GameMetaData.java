package coffeeblocks.foundation;

import java.util.HashMap;
import java.util.Map;

public class GameMetaData {
	//Meningen med denne klassen er å holde verdier relatert til objektet. Dette kan programmeres fra og benyttes i en eventloop, men er ikke knyttet opp mot et spesifik formål.
	//Den skal heller være et generelt grunnlag å bygge på, som tillater flere muligheter og samtidig ordnet kode
	private Map<String,Boolean> boolValues = new HashMap<>();
	private Map<String,String> stringValues = new HashMap<>();
	private Map<String,Long> timerValues = new HashMap<>();
	private Map<String,Double> doubleValues = new HashMap<>();
	private Map<String,Integer> intValues = new HashMap<>();
	
	public void setStringValue(String key, String value){
		stringValues.put(key, value);
	}
	public String getStringValue(String key){
		return stringValues.get(key);
	}
	
	public void setBoolValue(String key, Boolean value){
		boolValues.put(key, value);
	}
	public Boolean getBoolValue(String key){
		return boolValues.get(key);
	}
	
	public void setTimerValue(String key, Long value){
		timerValues.put(key, value);
	}
	public Long getTimerValue(String key){
		return timerValues.get(key);
	}
	
	public void setDoubleValue(String key, Double value){
		doubleValues.put(key, value);
	}
	public Double getDoubleValue(String key){
		return doubleValues.get(key);
	}
	
	public void setIntValue(String key, Integer value){
		intValues.put(key, value);
	}
	public Integer getIntValue(String key){
		return intValues.get(key);
	}
}
