package coffeeblocks.opengl.components;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import coffeeblocks.foundation.models.CoffeeAnimationContainer;
import coffeeblocks.metaobjects.Vector3Container;

public class CoffeeText implements CoffeeRenderableObject{
	public CoffeeText(){
		sourceObject = true;
		addVertices(
				new CoffeeVertex( 0f, 1f,-1f,	 1f, 1f,	 1f, 1f, 1f),
				new CoffeeVertex( 0f, 1f, 1f,	 0f, 1f,	 1f, 1f, 1f),
				new CoffeeVertex( 0f,-1f,-1f,	 1f, 0f,	 1f, 1f, 1f),
				new CoffeeVertex( 0f, 1f, 1f,	 1f, 1f,	 1f, 1f, 1f),
				new CoffeeVertex( 0f,-1f, 1f,	 0f, 1f,	 1f, 1f, 1f),
				new CoffeeVertex( 0f,-1f,-1f,	 1f, 0f,	 1f, 1f, 1f)
				);
		
		/*
		 * Vert: Vector3f[0.0, 1.0, -1.0]Vector2f[1.0, 1.0]Vector3f[1.0, 1.0, 1.0]Vector3f[0.0, 0.0, -2.0]
Vert: Vector3f[0.0, 1.0, 1.0]Vector2f[0.0, 1.0]Vector3f[1.0, 1.0, 1.0]Vector3f[0.0, 0.0, -2.0]
Vert: Vector3f[0.0, -1.0, -1.0]Vector2f[1.0, 0.0]Vector3f[1.0, 1.0, 1.0]Vector3f[0.0, 0.0, -2.0]
Vert: Vector3f[0.0, 1.0, 1.0]Vector2f[0.0, 1.0]Vector3f[1.0, 1.0, 1.0]Vector3f[0.0, 0.0, -2.0]
Vert: Vector3f[0.0, -1.0, 1.0]Vector2f[0.0, 0.0]Vector3f[1.0, 1.0, 1.0]Vector3f[0.0, 0.0, -2.0]
Vert: Vector3f[0.0, -1.0, -1.0]Vector2f[1.0, 0.0]Vector3f[1.0, 1.0, 1.0]Vector3f[0.0, 0.0, -2.0]
		 */
	}
	public CoffeeText(CoffeeText defaultTextObject) {
		if(!isSourceObject())
			throw new IllegalArgumentException("CoffeeText object is not a source");
	}
	private boolean sourceObject = false;
	private int gridSize = 0;
	private int tileSize = 0;
	private int vboHandle = 0;
	private Vector4f fontColor = new Vector4f(0,0,0,1);
	private ShaderBuilder shader = null;
	private CoffeeMaterial material = new CoffeeMaterial();
	private Vector3Container position = new Vector3Container();
	private Vector3Container rotation = new Vector3Container();
	private Vector3Container scale = new Vector3Container(1f,1f,1f);
	private List<CoffeeVertex> vertices = new ArrayList<>();
	private String vertShader = "";
	private String fragShader = "";
	
	public Vector3Container getPosition() {
		return position;
	}
	public Vector3Container getRotation() {
		return rotation;
	}
	public Vector3Container getScale() {
		return scale;
	}
	public boolean isSourceObject(){
		return sourceObject;
	}

	public int getTileSize() {
		return tileSize;
	}
	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}
	public int getGridSize() {
		return gridSize;
	}
	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}
	public Vector4f getFontColor() {
		return fontColor;
	}
	public void setFontColor(Vector4f fontColor) {
		this.fontColor = fontColor;
	}

	private void addVertices(CoffeeVertex... vectors){
		if(!sourceObject)
			return;
		for(CoffeeVertex vec : vectors)
			vertices.add(vec);
	}
	
	public void preloadText(){
		ShaderHelper.setupShader(this);
		ShaderHelper.loadTextures(this);
	}
	
	public void loadText(){
		ShaderHelper.compileShaders(this);
	}
	
	@Override
	public FloatBuffer getVertexData() {
		// TODO Auto-generated method stub
		return CoffeeAnimationContainer.convertVerticesToFloatBuffer(vertices, getVertexDataSize(), CoffeeVertex.VERTEX_DATA_SIZE);
	}

	public void setVertShader(String vertShader) {
		this.vertShader = vertShader;
	}
	public void setFragShader(String fragShader) {
		this.fragShader = fragShader;
	}
	@Override
	public int getVertexDataSize() {
		// TODO Auto-generated method stub
		return vertices.size()*CoffeeVertex.VERTEX_DATA_SIZE;
	}

	@Override
	public List<CoffeeVertex> getVertices() {
		// TODO Auto-generated method stub
		return vertices;
	}

	@Override
	public Vector3f getPositionVector() {
		// TODO Auto-generated method stub
		return position.getValue();
	}

	@Override
	public Vector3f getRotationVector() {
		// TODO Auto-generated method stub
		return rotation.getValue();
	}

	@Override
	public Vector3f getScaleVector() {
		// TODO Auto-generated method stub
		return scale.getValue();
	}

	@Override
	public boolean isStaticDraw() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getVboHandle() {
		// TODO Auto-generated method stub
		return vboHandle;
	}

	@Override
	public void setVboHandle(int handle) {
		// TODO Auto-generated method stub
		this.vboHandle = handle;
	}

	@Override
	public String getVertShaderFilename() {
		// TODO Auto-generated method stub
		return vertShader;
	}

	@Override
	public String getFragShaderFilename() {
		// TODO Auto-generated method stub
		return fragShader;
	}

	@Override
	public ShaderBuilder getShader() {
		// TODO Auto-generated method stub
		return shader;
	}

	@Override
	public void setShader(ShaderBuilder shader) {
		// TODO Auto-generated method stub
		this.shader = shader;
	}

	@Override
	public CoffeeMaterial getMaterial() {
		// TODO Auto-generated method stub
		return material;
	}

	private boolean baked = false;
	private boolean textureBaked = false;
	@Override
	public boolean isBaked() {
		// TODO Auto-generated method stub
		return baked;
	}

	@Override
	public void setObjectBaked(boolean baked) {
		// TODO Auto-generated method stub
		this.baked = baked;
	}

	@Override
	public void cleanupObject() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isTextureLoaded() {
		// TODO Auto-generated method stub
		return textureBaked;
	}

	@Override
	public void setTextureLoaded(boolean textureLoaded) {
		// TODO Auto-generated method stub
		this.textureBaked = textureLoaded;
	}
}
