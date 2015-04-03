package coffeeblocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coffeeblocks.foundation.CoffeeGameObjectManager;
import coffeeblocks.foundation.models.ModelLoader;
import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.foundation.physics.PhysicsObject;
import coffeeblocks.general.FileImporter;
import coffeeblocks.opengl.CoffeeRenderer;
import coffeeblocks.opengl.components.CoffeeCamera;
import coffeeblocks.opengl.components.LimeLight;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

//Veldig stygg kode. Kan sikkert forbedres på noe vis, men akkurat nå behøves det bare noe som fungerer.

public class CoffeeJsonParsing {
	public static void parseSceneStructure(String filepath,Map<String,Object> source, CoffeeGameObjectManager manager,CoffeeRenderer renderer){
		for(String key : source.keySet()){
			Object item = source.get(key);
			if(key.equals("scene")&&item instanceof HashMap){
				Map<String,Object> map = ((HashMap<String,Object>)item);
				for(String skey : map.keySet()){
					Object sitem = map.get(skey);
					if(skey.equals("camera")){
						CoffeeCamera cam = parseCameraObject(sitem);
						manager.addEntity("camera",cam);
						renderer.setCamera(cam);
					}else if (skey.equals("clearcolor")&&sitem instanceof ArrayList){
						List<Object> coloring = ((ArrayList)sitem);
						renderer.setClearColor(new Vector4f(Float.valueOf(coloring.get(0).toString()),Float.valueOf(coloring.get(1).toString()),Float.valueOf(coloring.get(2).toString()),Float.valueOf(coloring.get(3).toString())));
					}else if(skey.equals("objects")&&sitem instanceof ArrayList){
						for(Object model : ((ArrayList)sitem))
							manager.addObject(parseGameObject(filepath,model));
					}else if(skey.equals("lights")&&sitem instanceof ArrayList)
						for(Object lightobj : ((ArrayList)sitem)){
							LimeLight light = parseLightObject(lightobj);
							manager.addEntity(light.getLightId(),light);
							renderer.addLight(light);
						}
				}
			}else if(key.equals("logic")&&item instanceof String){
				System.out.println(((String)item));
			}
		}
	}
	public static GameObject parseGameObject(String filepath,Object source){
		if(!(source instanceof HashMap))
			throw new IllegalArgumentException("Ugyldig JSON");
		Map<String,Object> map = ((HashMap)source);
		GameObject gobj = new GameObject();
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
			if(key.equals("position")){
				if(obj instanceof ArrayList){
					List<Object> pos = ((ArrayList)obj);
					gobj.getGameModel().setPosition(new Vector3f(Float.valueOf(pos.get(0).toString()),Float.valueOf(pos.get(1).toString()),Float.valueOf(pos.get(2).toString())));
				}
			}else if(key.equals("rotation")&&obj instanceof ArrayList){
				List<Object> pos = ((ArrayList)obj);
				gobj.getGameModel().setRotation(new Vector3f(Float.valueOf(pos.get(0).toString()),Float.valueOf(pos.get(1).toString()),Float.valueOf(pos.get(2).toString())));
			}else if(key.equals("object-id")&&obj instanceof String){
				gobj.setObjectId(((String)obj));
			}else if(key.equals("scale")&&obj instanceof ArrayList){
				List<Object> pos = ((ArrayList)obj);
				gobj.getGameModel().scale = new Vector3f(Float.valueOf(pos.get(0).toString()),Float.valueOf(pos.get(1).toString()),Float.valueOf(pos.get(2).toString()));
			}else if(key.equals("physics.scale")&&obj instanceof ArrayList){
				List<Object> pos = ((ArrayList)obj);
				gobj.getGameModel().setPhysicalScale(new Vector3f(Float.valueOf(pos.get(0).toString()),Float.valueOf(pos.get(1).toString()),Float.valueOf(pos.get(2).toString())));
			}else if(key.equals("physics.rotation")&&obj instanceof ArrayList){
				List<Object> pos = ((ArrayList)obj);
				gobj.getGameModel().setPhysicalRotation(new Vector3f(Float.valueOf(pos.get(0).toString()),Float.valueOf(pos.get(1).toString()),Float.valueOf(pos.get(2).toString())));
			}else if(key.equals("physics.inertia")&&obj instanceof ArrayList){
				List<Object> pos = ((ArrayList)obj);
				gobj.getGameModel().setPhysicalInertia(new Vector3f(Float.valueOf(pos.get(0).toString()),Float.valueOf(pos.get(1).toString()),Float.valueOf(pos.get(2).toString())));
			}else if(key.equals("physics.linearity")&&obj instanceof ArrayList){
				List<Object> pos = ((ArrayList)obj);
				gobj.getGameModel().setPhysicalLinearFactor(new Vector3f(Float.valueOf(pos.get(0).toString()),Float.valueOf(pos.get(1).toString()),Float.valueOf(pos.get(2).toString())));
			}else if(key.equals("physics.mass")&&(obj instanceof Integer||obj instanceof Double)){
				gobj.getGameModel().setPhysicalMass(Float.valueOf(obj.toString()));
			}else if(key.equals("physics.restitution")&&(obj instanceof Integer||obj instanceof Double)){
				gobj.getGameModel().setRestitution(Float.valueOf(obj.toString()));
			}else if(key.equals("physics.friction")&&(obj instanceof Integer||obj instanceof Double)){
				gobj.getGameModel().setFriction(Float.valueOf(obj.toString()));
			}else if(key.equals("physics.shape")&&(obj instanceof Integer)){
				gobj.getGameModel().setPhysicsType(PhysicsObject.PhysicsType.values()[Integer.valueOf(obj.toString())]);
			}else if(key.equals("physics.collision")&&(obj instanceof String)){
				gobj.getGameModel().setCollisionMeshFile(filepath+"/"+(String)obj);
			}else if(key.equals("textures")&&obj instanceof ArrayList){
				List<Object> textures = ((ArrayList)obj);
				for(Object text : textures)
					if(text instanceof String)
						gobj.getGameModel().getMaterial().addTexture((String)text);
				gobj.getGameModel().getMaterial().setMultitextured(true);
			}
		}
		return gobj;
	}
	public static CoffeeCamera parseCameraObject(Object source){
		if(!(source instanceof HashMap))
			throw new IllegalArgumentException("Ugyldig JSON");
		Map<String,Object> map = ((HashMap)source);
		CoffeeCamera cam = new CoffeeCamera();
		for(String key : map.keySet()){
			Object obj = map.get(key);
			if(key.equals("position")&&obj instanceof ArrayList){
				List<Object> pos = ((ArrayList)obj);
				cam.setCameraPos(new Vector3f(Float.valueOf(pos.get(0).toString()),Float.valueOf(pos.get(1).toString()),Float.valueOf(pos.get(2).toString())));
			}else if(key.equals("look-at")&&obj instanceof ArrayList){
				List<Object> pos = ((ArrayList)obj);
//				cam.lookAt(new Vector3f(Float.valueOf(pos.get(0).toString()),Float.valueOf(pos.get(1).toString()),Float.valueOf(pos.get(2).toString())));
			}else if(key.equals("fov")&&(obj instanceof Integer||obj instanceof Double)){
				cam.setFieldOfView(Float.valueOf(obj.toString()));
			}
		}
		return cam;
	}
	public static LimeLight parseLightObject(Object source){
		if(!(source instanceof HashMap))
			throw new IllegalArgumentException("Ugyldig JSON");
		Map<String,Object> map = ((HashMap)source);
		LimeLight light = new LimeLight();
		for(String key : map.keySet()){
			Object obj = map.get(key);
			if(key.equals("position")&&obj instanceof ArrayList){
				List<Object> pos = ((ArrayList)obj);
				light.setPosition(new Vector3f(Float.valueOf(pos.get(0).toString()),Float.valueOf(pos.get(1).toString()),Float.valueOf(pos.get(2).toString())));
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
