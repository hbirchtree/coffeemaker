package coffeeblocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coffeeblocks.foundation.CoffeeGameObjectManager;
import coffeeblocks.foundation.CoffeeSceneManager;
import coffeeblocks.foundation.models.ModelLoader;
import coffeeblocks.foundation.models.ModelLoader.CoffeeModel;
import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.metaobjects.InstantiableObject;
import coffeeblocks.foundation.physics.PhysicsObject;
import coffeeblocks.general.FileImporter;
import coffeeblocks.openal.SoundObject;
import coffeeblocks.opengl.components.CoffeeCamera;
import coffeeblocks.opengl.components.LimeLight;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

//Veldig stygg kode. Kan sikkert forbedres på noe vis, men akkurat nå behøves det bare noe som fungerer.

public class CoffeeJsonParsing {
	@SuppressWarnings("unchecked")
	public static void parseSceneStructure(String filepath,Map<String,Object> source, CoffeeSceneManager sceneManager){
		for(String key : source.keySet()){
			Object item = source.get(key);
			if(key.startsWith("scene:")&&item instanceof HashMap){
				Map<String,Object> map = ((HashMap<String,Object>)item);
				if(key.split(":").length<2)
					continue;
				parseSceneObject(key.split(":")[1],map,sceneManager,filepath);
			}else if(key.equals("font")){
				Map<String,Object> map = ((HashMap<String,Object>)item);
			}
		}
	}
	public static void parseModels(Object source,String filepath,Map<String,Map<String,CoffeeModel>> modelsIndex){
		if(!(source instanceof HashMap))
			throw new IllegalArgumentException("Ugyldig JSON");
		Map<String,Object> map = ((HashMap<String, Object>)source);
		for(String key : map.keySet()){
			String modelFile = filepath+((String)map.get(key));
			String modelName = key;
			if(!modelsIndex.containsKey(modelFile))
				modelsIndex.put(modelName,ModelLoader.loadModelLibrary(modelFile));
		}
	}
	private static CoffeeModel readModelSource(String source,Map<String,Map<String,CoffeeModel>> modelsIndex){
		String[] modelblargh = source.split(":");
		String modelSrc = modelblargh[0];
		String modelName = modelblargh[1];
		if(modelsIndex.containsKey(modelSrc))
			for(String key : modelsIndex.get(modelSrc).keySet())
				if(key.startsWith(modelName))
						return modelsIndex.get(modelSrc).get(key);
		
		throw new IllegalStateException("3D object "+modelSrc+":"+modelName+" could not be found in set: "+modelsIndex);
	}
	@SuppressWarnings("unchecked")
	public static void parseSceneObject(String sceneId, Map<String,Object> scene,CoffeeSceneManager sceneManager,String filepath){
		sceneManager.createNewScene(sceneId);
		Map<String,Map<String,CoffeeModel>> modelsIndex = new HashMap<>();
		CoffeeGameObjectManager manager = sceneManager.getScene(sceneId);
		for(String skey : scene.keySet()){
			Object sitem = scene.get(skey);
			if(skey.equals("models"))
				parseModels(sitem,filepath,modelsIndex);
		}
		for(String skey : scene.keySet()){
			Object sitem = scene.get(skey);
			if(skey.equals("camera")){
				CoffeeCamera cam = parseCameraObject(sitem);
				manager.addEntity("camera",cam);
			}else if (skey.equals("clearcolor")&&sitem instanceof ArrayList){
				List<Object> coloring = ((List<Object>)sitem);
				manager.setClearColor(new Vector4f(Float.valueOf(
						coloring.get(0).toString()),Float.valueOf(coloring.get(1).toString()),Float.valueOf(coloring.get(2).toString()),Float.valueOf(coloring.get(3).toString())));
			}else if(skey.equals("objects")&&sitem instanceof ArrayList){
				for(Object model : ((ArrayList<?>)sitem)){
					InstantiableObject obj = parseGameObject(filepath,model,modelsIndex);
					if(!obj.isInstancedObject())
						manager.addObject(obj.createInstance());
					else
						manager.addInstantiableObject(obj.getObjectPreseedName(),obj);
				}
			}else if(skey.equals("lights")&&sitem instanceof ArrayList){
				List<LimeLight> lights = new ArrayList<>();
				for(Object lightobj : ((ArrayList<?>)sitem)){
					LimeLight light = parseLightObject(lightobj);
					lights.add(light);
				}
				manager.addEntity("lights",lights);
			}
		}
	}
	private static Vector3f readVector(Object obj){
		List<Object> pos = ((ArrayList<Object>)obj);
		return new Vector3f(Float.valueOf(pos.get(0).toString()),Float.valueOf(pos.get(1).toString()),Float.valueOf(pos.get(2).toString()));
	}
	@SuppressWarnings("unchecked")
	public static InstantiableObject parseGameObject(String filepath,Object source,Map<String,Map<String,CoffeeModel>> modelsIndex){
		if(!(source instanceof HashMap))
			throw new IllegalArgumentException("Ugyldig JSON");
		Map<String,Object> map = ((HashMap<String, Object>)source);
		InstantiableObject gobj = new GameObject();
		if(!map.containsKey("model"))
			throw new IllegalArgumentException("Ugyldig JSON: ingen modell for objekt");
		String modelFile = filepath+((String)map.get("model"));
		gobj.setGameModel(ModelLoader.loadModel(modelFile));
		if(!(map.containsKey("vshader")&&map.containsKey("fshader")))
			throw new IllegalArgumentException("Ugyldig JSON: ingen shaders for objekt");
		gobj.getGameModel().setShaderFiles(filepath+((String)map.get("vshader")), filepath+((String)map.get("fshader")));
		for(String key : map.keySet()){
			Object obj = map.get(key);
			//Vi aner ikke hvilke datatyper brukeren kommer til å skrive inn; string, int, double: vi gjør det enkelt.
			if(key.equals("position")&&obj instanceof ArrayList){
				gobj.getGameModel().getPosition().setValue(readVector(obj));
			}else if(key.equals("rotation")&&obj instanceof ArrayList){
				gobj.getGameModel().getRotation().setValue(readVector(obj));
			}else if(key.equals("position.offset")&&obj instanceof ArrayList){
				gobj.getGameModel().setModelOffset(readVector(obj));
			}else if(key.equals("object-id")&&obj instanceof String){ //Objektets id. kun nødvendig for statiske objekt
				gobj.setObjectPreseedName(((String)obj));
			}else if(key.equals("instantiable")&&obj instanceof Boolean){ //Om vi kan klone objektet vårt, f.eks. pistolkuler
				gobj.setInstancedObject((Boolean)obj);
			}else if(key.equals("stream-draw")&&obj instanceof Boolean){ //Bestemmer om objektet kan animeres
				gobj.getGameModel().getAnimationContainer().setStaticDraw(!(Boolean)obj);
			}else if(key.equals("notify-force")&&obj instanceof Boolean){ //Om kollisjonssystemet skal rapportere kraften påført objektet
				gobj.getGameModel().setNotifyForce((Boolean)obj);
			}else if(key.equals("scale")&&obj instanceof ArrayList){ //3D-modellens skala
				gobj.getGameModel().getScale().setValue(readVector(obj));
			}else if(key.equals("physics.scale")&&obj instanceof ArrayList){ //Fysisk skala
				gobj.getGameModel().setPhysicalScale(readVector(obj));
			}else if(key.equals("physics.rotation")&&obj instanceof ArrayList){ //Fysisk rotasjon
				gobj.getGameModel().setPhysicalRotation(readVector(obj));
			}else if(key.equals("physics.inertia")&&obj instanceof ArrayList){ //Objektets motstand til bevegelse, tenk gyro
				gobj.getGameModel().setPhysicalInertia(readVector(obj));
			}else if(key.equals("physics.linearity")&&obj instanceof ArrayList){ //Objektets motstand til rotasjon, 0,1,0 vil stoppe all rotasjon i X og Z
				gobj.getGameModel().setPhysicalLinearFactor(readVector(obj));
			}else if(key.equals("physics.mass")&&(obj instanceof Integer||obj instanceof Double)){ //Objektets masse. Objekter med masse har virtuelt uendelig massetreghet
				gobj.getGameModel().setPhysicalMass(Float.valueOf(obj.toString()));
			}else if(key.equals("physics.restitution")&&(obj instanceof Integer||obj instanceof Double)){ //Hvor mye energi bevares ved fall eller lignende
				gobj.getGameModel().setRestitution(Float.valueOf(obj.toString()));
			}else if(key.equals("physics.update-rotation")&&obj instanceof Boolean){ //Om 3D-modellen skal motta oppdateringer fra fysikk-systemet
				gobj.getGameModel().setUpdateRotation((Boolean)obj);
			}else if(key.equals("physics.friction")&&(obj instanceof Integer||obj instanceof Double)){ //Friksjon.
				gobj.getGameModel().setFriction(Float.valueOf(obj.toString()));
			}else if(key.equals("physics.shape")&&(obj instanceof Integer)){ //Form, enumerert i PhysicsObject
				gobj.getGameModel().setPhysicsType(PhysicsObject.PhysicsType.values()[Integer.valueOf(obj.toString())]);
			}else if(key.equals("physics.collision")&&(obj instanceof String)){ //Kollisjonsmodell ved PhysicsType.Complex
				gobj.getGameModel().setCollisionMeshFile(filepath+"/"+(String)obj);
			}else if(key.equals("textures")&&obj instanceof ArrayList){ //Alternative teksturer for objektet, kan byttes til via CoffeeMaterial-klassen
				List<Object> textures = ((ArrayList<Object>)obj);
				for(Object text : textures)
					if(text instanceof String)
						gobj.getGameModel().getMaterial().addTexture(
								filepath+FileImporter.getBasename((String)map.get("model"))+"/"+(String)text);
				gobj.getGameModel().getMaterial().setMultitextured(true);
			}else if(key.equals("poses")&&obj instanceof HashMap){ //Alternative modeller som modellen omformes til ved interpolering
				Map<String,Object> poses = ((HashMap<String, Object>)obj);
				for(String pose : poses.keySet()){
					String modelfile = filepath+FileImporter.getBasename((String)map.get("model"))+"/"+(String)poses.get(pose);
					gobj.getGameModel().getAnimationContainer().addState(pose,ModelLoader.loadModel(modelfile).getVertices());
				}
			}else if(key.equals("sounds")&&obj instanceof HashMap){ //Lydeffekter/musikk for dette objektet, posisjoneres ved dette objektets posisjon i det 3-dimensjonale rommet.
				Map<String,Object> sounds = ((HashMap<String, Object>)obj);
				for(String sound : sounds.keySet()){
					String soundfile = filepath+"/"+(String)sounds.get(sound);
					gobj.addSoundBox(new SoundObject(sound,soundfile));
				}
			}
		}
		return gobj;
	}
	@SuppressWarnings("unchecked")
	public static CoffeeCamera parseCameraObject(Object source){
		if(!(source instanceof HashMap))
			throw new IllegalArgumentException("Ugyldig JSON");
		Map<String,Object> map = ((HashMap<String, Object>)source);
		CoffeeCamera cam = new CoffeeCamera();
		for(String key : map.keySet()){
			Object obj = map.get(key);
			if(key.equals("position")&&obj instanceof ArrayList){
				List<Object> pos = ((ArrayList<Object>)obj);
				cam.getCameraPos().setValue(new Vector3f(Float.valueOf(pos.get(0).toString()),Float.valueOf(pos.get(1).toString()),Float.valueOf(pos.get(2).toString())));
//			}else if(key.equals("look-at")&&obj instanceof ArrayList){
//				List<Object> pos = ((ArrayList<Object>)obj);
//				cam.lookAt(new Vector3f(Float.valueOf(pos.get(0).toString()),Float.valueOf(pos.get(1).toString()),Float.valueOf(pos.get(2).toString())));
			}else if(key.equals("fov")&&(obj instanceof Integer||obj instanceof Double)){
				cam.setFieldOfView(Float.valueOf(obj.toString()));
			}
		}
		return cam;
	}
	@SuppressWarnings("unchecked")
	public static LimeLight parseLightObject(Object source){
		if(!(source instanceof HashMap))
			throw new IllegalArgumentException("Ugyldig JSON");
		Map<String,Object> map = ((HashMap<String, Object>)source);
		LimeLight light = new LimeLight();
		for(String key : map.keySet()){
			Object obj = map.get(key);
			if(key.equals("position")&&obj instanceof ArrayList){
				List<Object> pos = ((ArrayList<Object>)obj);
				light.getPosition().setValue(new Vector3f(Float.valueOf(pos.get(0).toString()),Float.valueOf(pos.get(1).toString()),Float.valueOf(pos.get(2).toString())));
			}else if(key.equals("color")&&obj instanceof String){
				light.setIntensities(hexToVec(((String)obj)));
			}else if(key.equals("attenuation")&&(obj instanceof Integer||obj instanceof Double)){
				light.setAttenuation(Float.valueOf(obj.toString()));
			}else if(key.equals("ambientcoeff")&&(obj instanceof Integer||obj instanceof Double)){
				light.setAmbientCoefficient(Float.valueOf(obj.toString()));
			}else if(key.equals("object-id")&&(obj instanceof Integer||obj instanceof Double)){
				light.setLightId((String)obj);
			}
		}
		return light;
	}
	public static Vector3f hexToVec(String hexcolor){
		if(hexcolor.length()==7)
			hexcolor = hexcolor.substring(1, hexcolor.length());
		String r = hexcolor.substring(0, 2);
		String g = hexcolor.substring(2, 4);
		String b = hexcolor.substring(4, 6);
		Vector3f color = new Vector3f((float)Integer.parseInt(r, 16)/255f,(float)Integer.parseInt(g, 16)/255f,(float)Integer.parseInt(b, 16)/255f);
		return color;
	}
}
