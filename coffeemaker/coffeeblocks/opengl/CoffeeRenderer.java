package coffeeblocks.opengl;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import coffeeblocks.foundation.CoffeeGameObjectManager;
import coffeeblocks.foundation.CoffeeRendererListener;
import coffeeblocks.foundation.input.CoffeeGlfwInputListener;
import coffeeblocks.foundation.models.ModelContainer;
import coffeeblocks.opengl.components.CoffeeCamera;
import coffeeblocks.opengl.components.CoffeeFramebufferManager;
import coffeeblocks.opengl.components.LimeLight;
import coffeeblocks.opengl.components.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;

public class CoffeeRenderer implements Runnable {

	// We need to strongly reference callback instances.
	private GLFWErrorCallback errorCallback;
//	private GLFWKeyCallback   keyCallback;
	
	private Vector4f clearColor = new Vector4f(0.8f,0.8f,1.0f,0f);
	private CoffeeCamera camera = null;
	private List<LimeLight> lights = null;
	public CoffeeCamera getCamera() {
		return camera;
	}
	public void setCamera(CoffeeCamera camera) {
		this.camera = camera;
	}
	public List<LimeLight> getLights() {
		return lights;
	}
	public void setLights(List<LimeLight> lights) {
		this.lights = lights;
	}

	private double fpsTimer = 0;
	private long framecount = 0;
	private double tick = 0;
	private boolean fpscounter = true;
	private float mouseSensitivity = 0.1f;
	private boolean draw = true;
	private boolean mouseGrabbed = false;
	private int rendering_swaps = 1;
	public void setSwapping(int swapping){
		this.rendering_swaps = swapping;
	}
	
	private CoffeeGameObjectManager scene = null;
	public void setScene(CoffeeGameObjectManager manager){
		if(manager==null)
			throw new RuntimeException("You cannot set an empty scene!");
//		if(scene!=null)
//			cleanupAll();
		this.scene = manager;
		setClearColor(scene.getClearColor());
		setCamera(scene.getCamera());
		setLights(scene.getLights());
	}
	public void setDrawingEnabled(boolean dodraw){
		draw = dodraw;
	}
	
	private Vector2d rendering_resolution = new Vector2d(1024,512);
	private Vector2d windowres = new Vector2d(1280,720);
	public Vector2d getWindowres() {
		return windowres;
	}
	public void setWindowres(int w,int h) {
		this.windowres = new Vector2d(w,h);
	}

	private float aspect = 1f;

	public float getMouseSensitivity() {
		return mouseSensitivity;
	}
	public void setMouseSensitivity(float mouseSensitivity) {
		this.mouseSensitivity = mouseSensitivity;
	}
	public int getRendering_swaps() {
		return rendering_swaps;
	}
	public void setRendering_swaps(int rendering_swaps) {
		this.rendering_swaps = rendering_swaps;
	}
	public Vector2d getRendering_resolution() {
		return rendering_resolution;
	}
	public void setRendering_resolution(Vector2d rendering_resolution){
		this.rendering_resolution = rendering_resolution;
	}

	// The window handle
	private long window;

	public synchronized void run() {
		try {
			init();
			loop();

			// Release window and window callbacks
			glfwDestroyWindow(window);
//			keyCallback.release();
		} finally {
			// Terminate GLFW and release the GLFWerrorfun
			glfwTerminate();
			errorCallback.release();
		}
		for(CoffeeRendererListener listener : listeners){
			listener.onGlfwQuit();
		}
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( glfwInit() != GL11.GL_TRUE )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure our window 
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT,GL_TRUE);
		glfwWindowHint(GLFW_OPENGL_PROFILE,GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR,3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR,3);
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

		int WIDTH = (int)windowres.x;
		int HEIGHT = (int)windowres.y;
		
		aspect = (float)(rendering_resolution.x/rendering_resolution.y);

		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, "Café", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
//		glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
//			@Override
//			public void invoke(long window, int key, int scancode, int action, int mods) {
//				
//			}
//		});
		

		// Get the resolution of the primary monitor
		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		// Center our window
		glfwSetWindowPos(
				window,
				(GLFWvidmode.width(vidmode) - WIDTH) / 2,
				(GLFWvidmode.height(vidmode) - HEIGHT) / 2
				);
		

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(this.rendering_swaps);
		
		toggleGrabMouse();

		// Make the window visible
		glfwShowWindow(window);
	}
	
	public void requestClose(){
		glfwSetWindowShouldClose(window, GL_TRUE);
	}

	public void loopHandleMouseInput(){
		DoubleBuffer xb = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer yb = BufferUtils.createDoubleBuffer(1);
		glfwGetCursorPos(window,xb,yb);
		double x,y;
		x = xb.get();
		y = yb.get();
		camera.offsetOrientation(mouseSensitivity*(float)x, mouseSensitivity*(float)y);
		glfwSetCursorPos(window,0,0);
	}
	
	private double controlDelay = 0;
	
	public void loopHandleKeyboardInput(){
		for(CoffeeGlfwInputListener listener : inputListeners)
			for(int key : listener.getRegisteredKeys())
				if(glfwGetKey(window,key)==1)
					listener.coffeeReceiveKeyPress(key);
		if(glfwGetTime()>=controlDelay){
			if(glfwGetKey(window,GLFW_KEY_F9)==1){
				toggleGrabMouse();
				controlDelay=glfwGetTime()+0.5d;
			}
		}
	}

	private void fpsCount(){
		if(fpscounter){
			framecount++;
			if(glfwGetTime()>=fpsTimer){
				System.out.println("FPS: "+framecount+"\nTick: "+String.format("%3f", tick*1000f)+"ms");
				framecount = 0;
				fpsTimer = glfwGetTime()+1;
			}
		}
	}
	
	private void toggleGrabMouse(){
		if(!mouseGrabbed){
			glfwSetInputMode(window,GLFW_CURSOR,GLFW_CURSOR_DISABLED);
			glfwSetCursorPos(window,0,0);
			mouseGrabbed=!mouseGrabbed;
		}else{
			glfwSetInputMode(window,GLFW_CURSOR,GLFW_CURSOR_NORMAL);
			mouseGrabbed=!mouseGrabbed;
		}
	}
	
	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the ContextCapabilities instance and makes the OpenGL
		// bindings available for use.
		GLContext.createFromCurrent();
		
		// Set the clear color
		glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

		for(CoffeeRendererListener listener : listeners)
			listener.onGlfwReady();
		
		fpsTimer = glfwGetTime()+1;
		
//		CoffeeFramebufferManager framebuffer = new CoffeeFramebufferManager(0,GL11.GL_RGBA);
//		try{
//			framebuffer.setRenderBuffer(aspect,(int)rendering_resolution.x, (int)rendering_resolution.y);
//		}catch(IllegalStateException e){
//			System.err.println(e.getMessage());
//		}
//		framebuffer.setEnabled(false);
		
		while (glfwWindowShouldClose(window)==GL_FALSE){
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
			fpsCount(); //Skriver ut FPS og tikke-tid
			for(CoffeeRendererListener listener : new ArrayList<>(listeners))
				listener.onGlfwFrameTick(); //Vi varsler lyttere om at et nytt tikk har skjedd
			for(CoffeeRendererListener listener : listeners)
				listener.onGlfwFrameTick((float)tick); //Dette for lyttere som avhenger av mengden tid passert (fysikk bl.a)
			tick = glfwGetTime(); //Vi bruker den midlertidig
			for(CoffeeRendererListener listener : listeners)
				listener.onGlfwFrameTick(glfwGetTime()); //Vi vil ha oversikt over tiden
			tick = glfwGetTime(); //Måler mengden tid det tar for å rendre objektene

			if(mouseGrabbed&&scene!=null)
				loopHandleMouseInput(); //Tar inn handlinger gjort med mus og endrer kameravinkel følgende (burde bli konfigurerbart gjennom Lua)
			loopHandleKeyboardInput(); //Håndterer hendelser for tastatur, sender hendelser til lyttere om deres registrerte knapper

//			framebuffer.storeFramebuffer(rendering_resolution);
			if(draw&&scene!=null) //Slå av rendring av objekter, dermed kan vi ta vekk og bytte objekter som skal vises
				loopRenderObjects(); //Rendring av objektene, enten til et framebuffer eller direkte
//			framebuffer.renderFramebuffer(windowres, lights);
			
			glfwSwapBuffers(window); // swap the color buffers
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents(); //Oppdater handlinger gjort med vinduet osv.
			tick = glfwGetTime()-tick;
		}
		cleanupAll();
	}
	
	public void cleanupAll(){
		for(ModelContainer object : scene.getRenderables())
			cleanupObject(object);
	}
	
	private void cleanupObject(ModelContainer object){
		if(object.getMaterial().getTextureHandle()!=0)
			GL11.glDeleteTextures(object.getMaterial().getTextureHandle());
		if(object.getMaterial().getVaoHandle()!=0)
			GL30.glDeleteVertexArrays(object.getMaterial().getVaoHandle());
		if(object.getShader().getProgramId()!=0)
			GL20.glDeleteProgram(object.getShader().getProgramId());
	}
	
	private void loopRenderObjects(){
		for(ModelContainer object : scene.getRenderables()){
			if(!object.isObjectBaked()){
				ShaderHelper.compileShaders(object,GL13.GL_TEXTURE0);
			}
			if(object.isNoDepthRendering()){
				glDisable(GL_DEPTH_TEST);
			}
			GL20.glUseProgram(object.getShader().getProgramId());

			object.getShader().setUniform("camera", camera.matrix());

			object.getShader().setUniform("model", ShaderHelper.rotateMatrice(object));
			for(LimeLight light : lights){ //Selv om vi kun støtter ett lys for øyeblikket skriver vi koden som om vi støttet flere. Vi kan legge til flere lys i fremtiden om nødvendig.
				object.getShader().setUniform("light.position", light.getPosition());
				object.getShader().setUniform("light.intensities", light.getIntensities());
				object.getShader().setUniform("light.attenuation", light.getAttenuation());
				object.getShader().setUniform("light.ambientCoefficient", light.getAmbientCoefficient());
			}

			object.getShader().setUniform("materialTex", 0); //Vi leser fargetekstur fra GL_TEXTURE(0)
			object.getShader().setUniform("materialBump", 1);
			object.getShader().setUniform("materialSpecular", 2);
			object.getShader().setUniform("materialHighlight", 3);
			object.getShader().setUniform("materialTransparency", 4);
			object.getShader().setUniform("materialShininess", object.getMaterial().getShininess());
			object.getShader().setUniform("materialSpecularColor", object.getMaterial().getSpecularColor());
			object.getShader().setUniform("materialTransparencyValue", object.getMaterial().getTransparency());
			
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, object.getMaterial().getTextureHandle());
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, object.getMaterial().getBumpTextureHandle());
			GL13.glActiveTexture(GL13.GL_TEXTURE2);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, object.getMaterial().getSpecularTextureHandle());
			GL13.glActiveTexture(GL13.GL_TEXTURE3);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, object.getMaterial().getHighlightTextureHandle());
			GL13.glActiveTexture(GL13.GL_TEXTURE4);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, object.getMaterial().getTransparencyTextureHandle());

			GL30.glBindVertexArray(object.getMaterial().getVaoHandle());
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, object.getVertexDataSize());

			GL30.glBindVertexArray(0);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			GL20.glUseProgram(0);
			
			if(object.isNoDepthRendering())
				glEnable(GL_DEPTH_TEST);
		}
	}
	private List<CoffeeRendererListener> listeners = new ArrayList<>();
	private List<CoffeeGlfwInputListener> inputListeners = new ArrayList<>();	
	public void addCoffeeListener(CoffeeRendererListener listener){
		listeners.add(listener);
	}
	public void addInputListener(CoffeeGlfwInputListener listener){
		inputListeners.add(listener);
	}
	public void clearListeners(){
		listeners.clear();
	}
	public Vector4f getClearColor() {
		return clearColor;
	}
	public void setClearColor(Vector4f clearColor) {
		this.clearColor = clearColor;
	}

}

