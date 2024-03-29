package coffeeblocks.opengl.components;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class ShaderHelper {
	public static void setupShader(CoffeeRenderableObject object){
		if(object.getShader()!=null&&object.getShader().getProgramId()==0)
			return;
		ShaderBuilder shader = new ShaderBuilder();
		try{
			shader.buildShader(object.getVertShaderFilename(), object.getFragShaderFilename());
		}catch(RuntimeException e){
			System.err.println(e.getMessage());
			System.exit(1);
		}
		object.setShader(shader);
		
		GL20.glUseProgram(shader.getProgramId());

		try{
			shader.getUniforms(
					//For spesielt detaljerte objekter
					"materialBump",
					"materialSpecular",
					"materialHighlight",
					"materialTransparency",
					
					//Standard
					"materialTex",
					"camera",
					"cameraPosition",
					"model",
					"fogParams.fDensity",
					"fogParams.fColor",

					//Materialegenskaper
					"materialShininess",
					"materialTransparencyValue",
					"materialSpecularColor",
					"colorMul",
					
					//Belysning					
					"light.position",
					"light.attenuation",
					"light.ambientCoefficient",
					"light.intensities");

			shader.getAttribs(
					"vert",
					"vertTexCoord",
					"vertNormal",
					"vertTangent");
		}catch(RuntimeException e){
			System.err.println(e.getMessage());
		}
		GL20.glUseProgram(0);
		
	}
	public static void loadTextures(CoffeeRenderableObject object,Map<String,Integer> textureRegister){
		//Vi vil slippe å laste inn samme tekstur flere ganger, derfor registrerer vi håndtaket for hver teksturfil
		if(object.getMaterial().isMultitextured()){
			List<Integer> textures = new ArrayList<>();
			for(String file : object.getMaterial().getMultitexture()){
				if(!textureRegister.containsKey(file)){
					int text = TextureHelper.genTexture(file);
					textures.add(text);
					textureRegister.put(file, text);
					
				}else
					textures.add(textureRegister.get(file));
			}
			object.getMaterial().setTextureHandles(textures);
		}else{
			int texture = 0;
			if(!textureRegister.containsKey(object.getMaterial().getDiffuseTexture())){
				texture = TextureHelper.genTexture(object.getMaterial().getDiffuseTexture());
				textureRegister.put(object.getMaterial().getDiffuseTexture(), texture);
			}else
				texture = textureRegister.get(object.getMaterial().getDiffuseTexture());
			object.getMaterial().setTextureHandle(texture);
		}
		
		if(object.getMaterial().hasBumpMap()){
			int bumpMap = TextureHelper.genTexture(object.getMaterial().getBumpTexture());
			object.getMaterial().setBumpTextureHandle(bumpMap);
		}
		if(object.getMaterial().hasTransparencyMap()){
			int alphaMap = TextureHelper.genTexture(object.getMaterial().getTransparencyTexture());
			object.getMaterial().setTransparencyTextureHandle(alphaMap);
		}
		if(object.getMaterial().hasHighlightMap()){
			int highMap = TextureHelper.genTexture(object.getMaterial().getHighlightTexture());
			object.getMaterial().setHighlightTextureHandle(highMap);
		}
		if(object.getMaterial().hasSpecularMap()){
			int specMap = TextureHelper.genTexture(object.getMaterial().getSpecularTexture());
			object.getMaterial().setSpecularTextureHandle(specMap);
		}
		object.setTextureLoaded(true);
	}
	public static void uploadVertices(CoffeeRenderableObject object){
		ShaderBuilder shader = object.getShader();
		VAOHelper.genVAO(object,object.getVertexData(),shader.getAttrib("vert"),
				shader.getAttrib("vertTexCoord"),shader.getAttrib("vertNormal"),shader.getAttrib("vertTangent"));
	}
	public static void compileShaders(CoffeeRenderableObject object,Map<String,Integer> textureRegister){
		if(!object.isTextureLoaded()){
			setupShader(object);
			loadTextures(object,textureRegister);
		}
		
		uploadVertices(object);
		
		object.setObjectBaked(true);
		
//		return shader;
	}
	
	public static FloatBuffer rotateMatrice(CoffeeRenderableObject object){
		FloatBuffer result = BufferUtils.createFloatBuffer(16);
		
		Matrix4f modelMatrix = new Matrix4f();
		Matrix4f.translate(object.getPositionVector(), modelMatrix, modelMatrix);
		Matrix4f.scale(object.getScaleVector(), modelMatrix, modelMatrix);
		Vector3f rotation = object.getRotationVector();
		Matrix4f.rotate(rotation.y*(float)Math.PI/180f, new Vector3f(0,1,0), modelMatrix, modelMatrix);
		Matrix4f.rotate(rotation.z*(float)Math.PI/180f, new Vector3f(0,0,1), modelMatrix, modelMatrix);
		Matrix4f.rotate(rotation.x*(float)Math.PI/180f, new Vector3f(1,0,0), modelMatrix, modelMatrix);
		
		modelMatrix.store(result);
		result.flip();
		
		return result;
	}
}
