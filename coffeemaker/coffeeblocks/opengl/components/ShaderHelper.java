package coffeeblocks.opengl.components;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.models.ModelContainer;

public class ShaderHelper {
	public static ShaderBuilder setupShader(ModelContainer object){
		ShaderBuilder shader = new ShaderBuilder();
		try{
			shader.buildShader(object.getVertShader(), object.getFragShader());
		}catch(RuntimeException e){
			System.err.println(e.getMessage());
			System.exit(1);
		}
		object.setShader(shader);
		
		System.out.println(object.getVertices().size());
		
		GL20.glUseProgram(shader.getProgramId());

		try{
			if(object.getMaterial().hasBumpMap()/*&&object.getMaterial().hasHighlightMap()&&
					object.getMaterial().hasSpecularMap()&&object.getMaterial().hasTransparencyMap()*/){
				shader.getUniform("materialBump");
				shader.getUniform("materialSpecular");
				shader.getUniform("materialHighlight");
				shader.getUniform("materialTransparency");
			}
			shader.getUniform("materialTex");
			shader.getUniform("camera");
			shader.getUniform("model");

			shader.getUniform("materialShininess");
			shader.getUniform("materialTransparencyValue");
			shader.getUniform("materialSpecularColor");
			shader.getUniform("light.position");
			shader.getUniform("light.attenuation");
			shader.getUniform("light.ambientCoefficient");
			shader.getUniform("light.intensities");

			shader.getAttrib("vert");
			shader.getAttrib("vertTexCoord");
			shader.getAttrib("vertNormal");
			shader.getAttrib("vertTangent");
		}catch(RuntimeException e){
			System.err.println(e.getMessage());
		}
		
		
		int vao = VAOHelper.genVAO(object.getVertexData(),shader.getAttrib("vert"),
				shader.getAttrib("vertTexCoord"),shader.getAttrib("vertNormal"),shader.getAttrib("vertTangent"));
		object.getMaterial().setVaoHandle(vao);
		return shader;
	}
	public static ShaderBuilder compileShaders(ModelContainer object,int textureUnit){
		ShaderBuilder shader = setupShader(object);
		
		if(object.getMaterial().isMultitextured()){
			List<Integer> textures = new ArrayList<>();
			for(String file : object.getMaterial().getMultitexture())
				textures.add(TextureHelper.genTexture(file));
			object.getMaterial().setTextureHandles(textures);
		}else{
			int texture = TextureHelper.genTexture(object.getMaterial().getDiffuseTexture());
			object.getMaterial().setTextureHandle(texture);
		}
		
		if(object.getMaterial().hasBumpMap()){
			int bumpMap = TextureHelper.genTexture(object.getMaterial().getBumpTexture());
			object.getMaterial().setBumpTextureHandle(bumpMap);
		}

		GL20.glUseProgram(0);
		
		object.setObjectBaked(true);
		
		return shader;
	}
	
	public static FloatBuffer rotateMatrice(ModelContainer object){
		FloatBuffer result = BufferUtils.createFloatBuffer(16);
		
		Matrix4f modelMatrix = new Matrix4f();
		Matrix4f.translate(object.getPosition(), modelMatrix, modelMatrix);
		Matrix4f.scale(object.getScale(), modelMatrix, modelMatrix);
		Matrix4f.rotate(object.getRotation().y*(float)Math.PI/180f, new Vector3f(0,1,0), modelMatrix, modelMatrix);
		Matrix4f.rotate(object.getRotation().z*(float)Math.PI/180f, new Vector3f(0,0,1), modelMatrix, modelMatrix);
		Matrix4f.rotate(object.getRotation().x*(float)Math.PI/180f, new Vector3f(1,0,0), modelMatrix, modelMatrix);
		
		modelMatrix.store(result);
		result.flip();
		
		return result;
	}
}
