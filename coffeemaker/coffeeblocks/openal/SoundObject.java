package coffeeblocks.openal;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;

import static org.lwjgl.openal.AL10.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.openal.WaveData;

import coffeeblocks.general.VectorTools;
import coffeeblocks.metaobjects.Vector3Container;

public class SoundObject {
	private boolean baked = false;
	public boolean isBaked(){
		return baked;
	}
	private boolean hasSource = false;
	public boolean hasSource(){
		return hasSource;
	}
	
	private String soundId = null;
	public String getSoundId(){
		return soundId;
	}
	
	public Vector3Container getPosition() {
		return position;
	}
	public Vector3Container getOrientation() {
		return orientation;
	}
	public int getSource(){
		return source;
	}
	public int getBuffer(){
		return buffer;
	}

	private Vector3Container position = new Vector3Container();
	private Vector3Container orientation = new Vector3Container();
	private float pitch = 1f;
	private float gain = 1f;
	private int buffer = 0;
	private int source = 0;
	private String soundFile = null;
	public String getSoundFile(){
		return soundFile;
	}
	
	public void setPitch(float pitch){
		this.pitch = pitch;
	}
	public float getPitch(){
		return pitch;
	}
	public void setGain(float gain){
		this.gain = gain;
	}
	public float getGain(){
		return gain;
	}
	
	public SoundObject(String soundId, String soundFile){
		this.soundFile = soundFile;
		this.soundId = soundId;
	}
	
	public SoundObject(SoundObject e) {
		soundFile = e.getSoundFile();
		position = new Vector3Container(e.getPosition());
		orientation = new Vector3Container(e.getOrientation());
		this.pitch = e.getPitch();
		this.gain = e.getGain();
		this.buffer = e.getBuffer();
		this.soundId = e.getSoundId()+"."+this.source;
	}

	public boolean initSound(){
		WaveData data;
		try {
			data = WaveData.create(new BufferedInputStream(new FileInputStream(soundFile)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		buffer = alGenBuffers();
		alBufferData(buffer,data.format,data.data,data.samplerate);
		data.dispose();
		if(alGetError()!=AL_NO_ERROR)
			return false;
		
		return genSource();
	}
	public boolean genSource(){
		source = alGenSources();
		alSourcei(source, AL_BUFFER, buffer);
		if(alGetError()!=AL_NO_ERROR)
			return false;
		
		updateSource();
		
		baked = true;
		return true;
	}
	
	public void updateSource(){
		alSource(source, AL_POSITION, VectorTools.vecToFloatBuffer(position.getValue()));
		alSource(source, AL_VELOCITY, VectorTools.vecToFloatBuffer(position.getVelocity()));
		alSourcef(source, AL_PITCH, pitch);
		alSourcef(source, AL_GAIN, gain);
		if(alGetError()!=AL_NO_ERROR)
			throw new RuntimeException("Failed to update audio source!");
		hasSource = true;
	}
	
	public static FloatBuffer genOrientationBuf(Vector3f at, Vector3f up){
		FloatBuffer buf = BufferUtils.createFloatBuffer(6);
		float[] floats = new float[6];
		floats[0] = at.x;
		floats[1] = at.y;
		floats[2] = at.z;
		floats[3] = up.x;
		floats[4] = up.y;
		floats[5] = up.z;
		buf.put(floats);
		buf.flip();
		return buf;
	}
}
