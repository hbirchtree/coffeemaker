package coffeeblocks.opengl.components;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.List;

import javax.vecmath.Vector2d;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.models.ModelContainer;
import coffeeblocks.foundation.models.ModelLoader;

public class CoffeeFramebufferManager {
	private int framebuffer = 0;
	private int renderbuffer = 0;
	private int texture = 0;
	private boolean validFramebuffer = false;
	private boolean enabled = true;
	private CoffeeCamera orthocamera = new CoffeeCamera();
	private ModelContainer renderMesh;
	
	public CoffeeFramebufferManager(int multisamples,int textureFormat){
	}
	
	public int getFramebuffer(){
		return framebuffer;
	}
	public int getTexture(){
		return texture;
	}
	
	public boolean isValidFramebuffer(){
		return validFramebuffer&&enabled;
	}
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
	public void setRenderBuffer(float aspect,int width, int height){
		
		renderMesh = ModelLoader.loadModel("testgame/models/quad.obj");
		renderMesh.setShader(new ShaderBuilder());
		renderMesh.setShaderFiles("testgame/shaders/vsh.txt", "testgame/shaders/fsh_nolight.txt");
		ShaderHelper.setupShader(renderMesh);
		renderMesh.getMaterial().setTextureHandle(getTexture());
		renderMesh.setPosition(new Vector3f(0,0,-1.07f));
		renderMesh.setScale(new Vector3f(aspect,1,1));
		renderMesh.setRotation(new Vector3f(0,90,0));

		framebuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
		
		//Texture
		texture = TextureHelper.allocateTexture(GL11.GL_RGBA,GL11.GL_RGBA,width,height,null);
		GL11.glBindTexture(GL_TEXTURE_2D, texture);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER,GL30.GL_COLOR_ATTACHMENT0,GL_TEXTURE_2D,texture,0);
		
		renderbuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderbuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_STENCIL_INDEX8, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_DRAW_FRAMEBUFFER, GL30.GL_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, renderbuffer);
		
//		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER,0);
		
		
		//Stencil
//		stencilrenderbuffer = GL30.glGenRenderbuffers();
//		stenciltexture = TextureHelper.allocateTexture(GL11.GL_RGBA, width, height);
//		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, stencilrenderbuffer);
//		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER,GL30.GL_COLOR_ATTACHMENT0,GL_TEXTURE_2D,stenciltexture,0);
//		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER,multisamples, GL30.GL_STENCIL_INDEX8, width, height);
//		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RENDERBUFFER, stencilrenderbuffer);
//		
//		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER,0);
		
		if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER)!=GL30.GL_FRAMEBUFFER_COMPLETE)
			throw new IllegalStateException("Failed to allocate framebuffer render-target");
		
//		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
		GL11.glBindTexture(GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		validFramebuffer = true;
	}
	public void storeFramebuffer(Vector2d rendering_resolution){
		if(!isValidFramebuffer())
			return;
		
		GL11.glViewport(0, 0, (int)rendering_resolution.x, (int)rendering_resolution.y);
		GL11.glBindTexture(GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, getFramebuffer());
	}
	public void renderFramebuffer(Vector2d windowres,List<LimeLight> lights){
		if(!isValidFramebuffer())
			return;
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		glDisable(GL_DEPTH_TEST);
		GL11.glViewport(0, 0, (int)windowres.x, (int)windowres.y);

		GL20.glUseProgram(renderMesh.getShader().getProgramId());

		renderMesh.getShader().setUniform("camera", orthocamera.matrixOrtho());

		renderMesh.getShader().setUniform("model", ShaderHelper.rotateMatrice(renderMesh));
		for(LimeLight light : lights){
			renderMesh.getShader().setUniform("light.position", light.getPosition());
			renderMesh.getShader().setUniform("light.intensities", light.getIntensities());
			renderMesh.getShader().setUniform("light.attenuation", light.getAttenuation());
			renderMesh.getShader().setUniform("light.ambientCoefficient", light.getAmbientCoefficient());
		}

		renderMesh.getShader().setUniform("materialTex", 0);
		renderMesh.getShader().setUniform("materialShininess", renderMesh.getMaterial().getShininess());
		renderMesh.getShader().setUniform("materialSpecularColor", renderMesh.getMaterial().getSpecularColor());
		renderMesh.getShader().setUniform("materialTransparency", renderMesh.getMaterial().getTransparency());

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

		GL30.glBindVertexArray(renderMesh.getMaterial().getVaoHandle());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, renderMesh.getVertexDataSize());

		GL30.glBindVertexArray(0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL20.glUseProgram(0);

		glEnable(GL_DEPTH_TEST);
	}
}
