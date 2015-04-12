package coffeeblocks.opengl.components;

import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.Vector3Container;

public class LimeLight {
	private String lightId = "";
	public String getLightId() {
		return lightId;
	}
	public void setLightId(String lightId) {
		this.lightId = lightId;
	}
	
	private Vector3Container position = new Vector3Container();
	private Vector3f intensities = new Vector3f();
	private float attenuation = 0.1f;
	private float ambientCoefficient = 0.005f;
	public float getAttenuation() {
		return attenuation;
	}
	public synchronized void setAttenuation(float attenuation) {
		this.attenuation = attenuation;
	}
	public float getAmbientCoefficient() {
		return ambientCoefficient;
	}
	public synchronized void setAmbientCoefficient(float ambientCoefficient) {
		this.ambientCoefficient = ambientCoefficient;
	}
	public Vector3Container getPosition() {
		return position;
	}
	public Vector3f getIntensities() {
		return intensities;
	}
	public synchronized void setIntensities(Vector3f intensities) {
		if(intensities!=null)
			this.intensities = intensities;
	}
	public void setIntensities(float r,float g,float b) {
		intensities.x = r;
		intensities.y = g;
		intensities.z = b;
	}
}
