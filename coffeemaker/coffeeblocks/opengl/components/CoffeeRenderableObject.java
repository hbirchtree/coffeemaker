package coffeeblocks.opengl.components;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public interface CoffeeRenderableObject {
	public FloatBuffer getVertexData();
	public int getVertexDataSize();
	
	public List<CoffeeVertex> getVertices();
	
	public Vector3f getPositionVector();
	public Vector3f getRotationVector();
	public Vector3f getScaleVector();
	
	public boolean isStaticDraw();
	public int getVboHandle();
	public void setVboHandle(int handle);
	
	public String getVertShaderFilename();
	public String getFragShaderFilename();
	public ShaderBuilder getShader();
	public void setShader(ShaderBuilder shader);
	public CoffeeMaterial getMaterial();
	public boolean isBaked();
	public void setObjectBaked(boolean baked);
	
	public void cleanupObject();
}