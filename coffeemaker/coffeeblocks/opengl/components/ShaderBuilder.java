package coffeeblocks.opengl.components;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import coffeeblocks.general.FileImporter;

import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL20.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class ShaderBuilder {
	private int programId;
	
	Map<String,Integer> attributes = new HashMap<>();
	Map<String,Integer> uniforms = new HashMap<>();
	
	public int getUniformValue(String name){
		Integer value = uniforms.get(name);
		if(value==null)
			return 0;
		return value.intValue();
	}
	public int getAttributeValue(String name){
		Integer value = uniforms.get(name);
		if(value==null)
			return 0;
		return value.intValue();
	}
	
	
	public int getUniform(String uniformName){
		int uniform = GL20.glGetUniformLocation(programId,uniformName);
		uniforms.put(uniformName, uniform);
		return uniform;
	}
	public void setUniform(String uniformName,Vector3f value){
		Integer loc = uniforms.get(uniformName);
		if(loc!=null)
			GL20.glUniform3f(loc, value.x, value.y, value.z);
	}
	public void setUniform(String uniformName,float value){
		Integer loc = uniforms.get(uniformName);
		if(loc!=null)
			GL20.glUniform1f(loc, value);
	}
	public void setUniform(String uniformName,int value){
		Integer loc = uniforms.get(uniformName);
		if(loc!=null)
			GL20.glUniform1i(loc, value);
	}
	public void setUniform(String uniformName,FloatBuffer value){
		Integer loc = uniforms.get(uniformName);
		if(loc!=null)
			GL20.glUniformMatrix4(loc, false, value);
	}
	
	public int getAttrib(String attribName){
		int attrib = GL20.glGetAttribLocation(programId, attribName);
		attributes.put(attribName, attrib);
		return attrib;
	}
	
	public int getProgramId(){
		return programId;
	}
	
	
	public void buildShader(String vertShaderFile,String fragShaderFile){
		programId = glCreateProgram();
		
		int vertShader = compileShader(vertShaderFile,GL_VERTEX_SHADER);
		int fragShader = compileShader(fragShaderFile,GL_FRAGMENT_SHADER);
		
		glAttachShader(programId,vertShader);
		glAttachShader(programId,fragShader);
		
		glLinkProgram(programId);
		
		if(glGetProgrami(programId,GL_LINK_STATUS) == GL11.GL_FALSE)
			throw new RuntimeException("Failed to link shader:"+glGetProgramInfoLog(programId, 1000));
		
		glValidateProgram(programId);
		
		if(glGetProgrami(programId,GL_VALIDATE_STATUS) == GL11.GL_FALSE)
			throw new RuntimeException("Failed to link shader:"+glGetProgramInfoLog(programId, 1000));
	}
	
	
	private int compileShader(String filename,int shaderType){
		int handle = glCreateShader(shaderType);
		if(handle==0)
			throw new RuntimeException("Failed to create shader");
		
		String code = FileImporter.readFileToString(filename);
		glShaderSource(handle,code);
		
		glCompileShader(handle);
		
		int shaderStatus = glGetShaderi(handle,GL20.GL_COMPILE_STATUS);
		
		if(shaderStatus == GL11.GL_FALSE){
			System.out.println(glGetProgramInfoLog(handle, 1000));
			throw new IllegalStateException("Compilation error for OpenGL shader:");
		}
		
		return handle;
	}
}
