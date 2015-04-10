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

	private String specularTexture = ""; //Planned
	private String highlightTexture = ""; //Unimplemented
	private String alphaTexture = ""; //Unimplemented
	private String bumpTexture = ""; //Planned
	
	public boolean hasBumpMap(){
		return !bumpTexture.isEmpty();
	}
	public boolean hasSpecularMap(){
		return !specularTexture.isEmpty();
	}
	public boolean hasHighlightMap(){
		return !highlightTexture.isEmpty();
	}
	public boolean hasTransparencyMap(){
		return !alphaTexture.isEmpty();
	}
	
	public String getSpecularTexture() {
		return specularTexture;
	}
	public void setSpecularTexture(String specularTexture) {
		this.specularTexture = specularTexture;
	}
	public String getHighlightTexture() {
		return highlightTexture;
	}
	public void setHighlightTexture(String highlightTexture) {
		this.highlightTexture = highlightTexture;
	}
	public String getTransparencyTexture() {
		return alphaTexture;
	}
	public void setTransparencyTexture(String alphaTexture) {
		this.alphaTexture = alphaTexture;
	}
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
	
	private int specularTextureHandle = 0;
	private int transparencyTextureHandle = 0;
	private int highlightTextureHandle = 0;
	
	public int getSpecularTextureHandle() {
		return specularTextureHandle;
	}
	public void setSpecularTextureHandle(int specularTextureHandle) {
		this.specularTextureHandle = specularTextureHandle;
	}
	public int getTransparencyTextureHandle() {
		return transparencyTextureHandle;
	}
	public void setTransparencyTextureHandle(int transparencyTextureHandle) {
		this.transparencyTextureHandle = transparencyTextureHandle;
	}
	public int getHighlightTextureHandle() {
		return highlightTextureHandle;
	}
	public void setHighlightTextureHandle(int highlightTextureHandle) {
		this.highlightTextureHandle = highlightTextureHandle;
	}

	private int bumpTextureHandle = 0;
	public int getBumpTextureHandle() {
		return bumpTextureHandle;
	}
	public void setBumpTextureHandle(int bumpTextureHandle) {
		this.bumpTextureHandle = bumpTextureHandle;
	}
	
	private int vaoHandle = 0;
	public int getVaoHandle() {
		return vaoHandle;
	}
	public void setVaoHandle(int vaoHandle) {
		this.vaoHandle = vaoHandle;
	}
	
	public int selectTexture = 0;
	private List<Integer> textureHandles = new ArrayList<>();
	public int getTextureHandle(){
		return textureHandles.get(selectTexture);
	}
	public void setTextureHandle(int textureHandle) {
		if(textureHandles.size()>0)
			textureHandles.clear();
		textureHandles.add(textureHandle);
	}
	public void setTextureHandles(List<Integer> handles){
		textureHandles.clear();
		this.textureHandles.addAll(handles);
	}
	public List<Integer> getTextureHandles(){
		return textureHandles;
	}
	public String getBumpTexture() {
		return bumpTexture;
	}
	public void setBumpTexture(String bumpTexture) {
		this.bumpTexture = bumpTexture;
	}
}
