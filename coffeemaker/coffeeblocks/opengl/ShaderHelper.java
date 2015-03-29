package coffeeblocks.opengl;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.models.ModelContainer;

public class ShaderHelper {
	public static ShaderBuilder compileShaders(ModelContainer object){
		ShaderBuilder shader = new ShaderBuilder();
		shader.buildShader(object.getVertShader(), object.getFragShader());
		object.setShader(shader);
		
		GL20.glUseProgram(shader.getProgramId());

		shader.getUniform("camera");
		
		shader.getUniform("model");
		shader.getUniform("materialTex");
		shader.getUniform("materialShininess");
		shader.getUniform("materialSpecularColor");
		
		shader.getUniform("light.position");
		shader.getUniform("light.attenuation");
		shader.getUniform("light.ambientCoefficient");
		shader.getUniform("light.intensities");
		
		shader.getAttrib("vert");
		shader.getAttrib("vertTexCoord");
		shader.getAttrib("vertNormal");
		
		object.glTextureUnit = GL13.GL_TEXTURE0;
		
		int texture = TextureHelper.genTexture(object.getMaterial().getDiffuseTexture(),object.glTextureUnit);
		int vao = VAOHelper.genVAO(object.getVertexData(),shader.getAttrib("vert"),shader.getAttrib("vertTexCoord"),shader.getAttrib("vertNormal"));
		
		object.textureHandle = texture;
		object.vaoHandle = vao;
		
		shader.setUniform("materialTex", texture);

		GL20.glUseProgram(0);
		
		object.setObjectBaked(true);
		
		return shader;
	}
	
	public static FloatBuffer rotateMatrice(ModelContainer object){
		FloatBuffer result = BufferUtils.createFloatBuffer(16);
		
		Matrix4f modelMatrix = new Matrix4f();
		Matrix4f.scale(object.scale, modelMatrix, modelMatrix);
		Matrix4f.rotate(object.rotation.z*(float)Math.PI/180, new Vector3f(0,0,1), modelMatrix, modelMatrix);
		Matrix4f.rotate(object.rotation.y*(float)Math.PI/180, new Vector3f(0,1,0), modelMatrix, modelMatrix);
		Matrix4f.rotate(object.rotation.x*(float)Math.PI/180, new Vector3f(1,0,0), modelMatrix, modelMatrix);
		Matrix4f.translate(object.position, modelMatrix, modelMatrix);
		
		modelMatrix.store(result);
		result.flip();
		
		return result;
	}
}
