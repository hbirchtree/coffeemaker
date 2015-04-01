package coffeeblocks.opengl.components;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public class CoffeeMaterial {
	private boolean multitextured = false;
	private List<String> multitexture = new ArrayList<>();
	public boolean isMultitextured() {
		return multitextured;
	}
	public void setMultitextured(boolean multitextured) {
		this.multitextured = multitextured;
	}
	public List<String> getMultitexture() {
		return multitexture;
	}
	public void addTexture(String textureFile){
		this.multitexture.add(textureFile);
	}
	
	private float shininess = 100f;
	private float transparency = 1f;
	public float dissolution = 0f; //Unimplemented
	public int illum = 0; //Unimplemented
	
	private String diffuseTexture = "/home/havard/texture.png"; //Texture
	public String ambientTexture = ""; //Unimplemented
	public String specularTexture = ""; //Unimplemented
	public String highlightTexture = ""; //Unimplemented
	public String alphaTexture = ""; //Unimplemented
	public String bumbTexture = ""; //Unimplemented
	
	private Vector3f specularColor = new Vector3f(1,1,1);
	public Vector3f ambientColor = new Vector3f(1,1,1); //Unimplemented
	public Vector3f diffuseColor = new Vector3f(1,1,1); //Unimplemented
	public String getDiffuseTexture() {
		return diffuseTexture;
	}
	public void setDiffuseTexture(String diffuseTexture) {
		this.diffuseTexture = diffuseTexture;
	}
	public float getShininess() {
		return shininess;
	}
	public void setShininess(float shininess) {
		this.shininess = shininess;
	}
	public Vector3f getSpecularColor() {
		return specularColor;
	}
	public void setSpecularColor(Vector3f specularColor) {
		this.specularColor = specularColor;
	}
	public void setSpecularColor(float r, float g,float b) {
		specularColor.x = r;
		specularColor.x = g;
		specularColor.x = b;
	}
	public float getTransparency() {
		return transparency;
	}
	public void setTransparency(float transparency) {
		this.transparency = transparency;
	}
}
