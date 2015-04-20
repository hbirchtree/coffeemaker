package coffeeblocks.opengl.components;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.metaobjects.FloatContainer;

public class CoffeeMaterial {
	public CoffeeMaterial(){}
	public CoffeeMaterial(CoffeeMaterial material){
		this.textureHandles = new ArrayList<>(material.getTextureHandles());
		this.multitextured = material.isMultitextured();
		this.multitexture = material.getMultitexture();
		
		this.bumpTexture = material.getBumpTexture();
		this.highlightTexture = material.getHighlightTexture();
		this.diffuseTexture = material.getDiffuseTexture();
		this.specularTexture = material.getSpecularTexture();
		this.alphaTexture = material.getTransparencyTexture();
		
		this.bumpTextureHandle = material.getBumpTextureHandle();
		this.transparencyTextureHandle = material.getTransparencyTextureHandle();
		this.specularTextureHandle = material.getSpecularTextureHandle();
		this.highlightTextureHandle = material.getHighlightTextureHandle();
		
		this.shininess = new FloatContainer(material.getShininessObject());
		this.transparency = new FloatContainer(material.getTransparencyObject());
		this.dissolution = material.dissolution;
		this.illum = material.illum;
		
		this.specularColor = new Vector3f(material.specularColor);
		
		this.selectTexture = material.selectTexture;
	}
	
	private boolean multitextured = false;
	private List<String> multitexture = new ArrayList<>();
	
	private FloatContainer shininess = new FloatContainer(500f);
	private FloatContainer transparency = new FloatContainer(1f);
	public float dissolution = 0f; //Unimplemented
	public int illum = 0; //Unimplemented
	
	private String diffuseTexture = "";
	public String ambientTexture = ""; //Unimplemented
	private String specularTexture = "";
	private String highlightTexture = "";
	private String alphaTexture = "";
	private String bumpTexture = "";
	
	private Vector3f specularColor = new Vector3f(1,1,1);
	public Vector3f ambientColor = new Vector3f(1,1,1); //Unimplemented
	public Vector3f diffuseColor = new Vector3f(1,1,1); //Unimplemented
	
	private Vector3f colorMultiplier = new Vector3f(1,1,1);
	
	private int specularTextureHandle = 0;
	private int transparencyTextureHandle = 0;
	private int highlightTextureHandle = 0;
	private int bumpTextureHandle = 0;
	
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
	
	
	
	public int selectTexture = 0;
	private List<Integer> textureHandles = new ArrayList<>();
	private int vaoHandle = 0;
	
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
	public String getDiffuseTexture() {
		return diffuseTexture;
	}
	public void setDiffuseTexture(String diffuseTexture) {
		this.diffuseTexture = diffuseTexture;
	}
	public FloatContainer getShininessObject(){
		return shininess;
	}
	public float getShininess() {
		return shininess.getValue();
	}
	public void setShininess(float shininess) {
		this.shininess.setValue(shininess);
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
	public FloatContainer getTransparencyObject(){
		return transparency;
	}
	public float getTransparency() {
		return transparency.getValue();
	}
	public void setTransparency(float transparency) {
		this.transparency.setValue(transparency);
	}
	
	
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

	public int getBumpTextureHandle() {
		return bumpTextureHandle;
	}
	public void setBumpTextureHandle(int bumpTextureHandle) {
		this.bumpTextureHandle = bumpTextureHandle;
	}
	
	public int getVaoHandle() {
		return vaoHandle;
	}
	public void setVaoHandle(int vaoHandle) {
		this.vaoHandle = vaoHandle;
	}
	
	public int getTextureHandle(){
		if(isMultitextured())
			return textureHandles.get(selectTexture);
		else return textureHandles.get(selectTexture);
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
	public Vector3f getColorMultiplier() {
		return colorMultiplier;
	}
	public void setColorMultiplier(Vector3f colorMultiplier) {
		this.colorMultiplier = colorMultiplier;
	}
}
