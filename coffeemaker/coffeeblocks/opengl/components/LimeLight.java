package coffeeblocks.opengl.components;

import org.lwjgl.util.vector.Vector3f;

public class LimeLight {
	private Vector3f position = new Vector3f();
	private Vector3f intensities = new Vector3f();
	private float attenuation = 0.1f;
	private float ambientCoefficient = 0.005f;
	public float getAttenuation() {
		return attenuation;
	}
	public void setAttenuation(float attenuation) {
		this.attenuation = attenuation;
	}
	public float getAmbientCoefficient() {
		return ambientCoefficient;
	}
	public void setAmbientCoefficient(float ambientCoefficient) {
		this.ambientCoefficient = ambientCoefficient;
	}
	public Vector3f getPosition() {
		return position;
	}
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	public Vector3f getIntensities() {
		return intensities;
	}
	public void setIntensities(Vector3f intensities) {
		if(intensities!=null)
			this.intensities = intensities;
	}
	public void setIntensities(float r,float g,float b) {
		intensities.x = r;
		intensities.y = g;
		intensities.z = b;
	}
}
