package coffeeblocks.opengl;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.CoffeeRendererListener;
import coffeeblocks.foundation.input.CoffeeGlfwInputListener;
import coffeeblocks.foundation.models.ModelContainer;
import coffeeblocks.general.VectorTools;
import coffeeblocks.opengl.components.CoffeeCamera;
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
import java.util.Stack;

public class CoffeeRenderer implements Runnable {

	// We need to strongly reference callback instances.
	private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback   keyCallback;
	
	private CoffeeCamera camera = null;
	private LimeLight light_sun = null;
	

	private Stack<Integer> textureUnits = new Stack<>();

	// The window handle
	private long window;

	public synchronized void run() {
		try {
			init();
			loop();

			// Release window and window callbacks
			glfwDestroyWindow(window);
			keyCallback.release();
		} finally {
			// Terminate GLFW and release the GLFWerrorfun
			glfwTerminate();
			errorCallback.release();
			for(CoffeeRendererListener listener : listeners){
				listener.onGlfwQuit();
			}
		}
		for(CoffeeRendererListener listener : listeners){
			listener.onGlfwQuit();
		}
	}

	private void init() {
		textureUnits.push(GL13.GL_TEXTURE6);
		textureUnits.push(GL13.GL_TEXTURE5);
		textureUnits.push(GL13.GL_TEXTURE4);
		textureUnits.push(GL13.GL_TEXTURE3);
		textureUnits.push(GL13.GL_TEXTURE2);
		textureUnits.push(GL13.GL_TEXTURE1);
		textureUnits.push(GL13.GL_TEXTURE0);
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
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR,2);
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

		int WIDTH = 1280;
		int HEIGHT = 720;

		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, "Café", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		
		glfwSetInputMode(window,GLFW_CURSOR,GLFW_CURSOR_DISABLED);
		glfwSetCursorPos(window,0,0);

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
					glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
			}
		});

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
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}

	//Mouse input
	private final float mouseSensitivity = 0.1f;
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
	public void loopHandleKeyboardInput(){
		if(glfwGetKey(window,GLFW_KEY_W)==1)
			camera.moveCameraForward(0.1f);
		if(glfwGetKey(window,GLFW_KEY_S)==1)
			camera.moveCameraForward(-0.1f);
		if(glfwGetKey(window,GLFW_KEY_A)==1)
			camera.moveCameraRight(-0.1f);
		if(glfwGetKey(window,GLFW_KEY_D)==1)
			camera.moveCameraRight(0.1f);
		if(glfwGetKey(window,GLFW_KEY_D)==1)
			camera.moveCameraRight(0.1f);
		if(glfwGetKey(window,GLFW_KEY_SPACE)==1)
			camera.offsetPosition(new Vector3f(0,0.2f,0));
		if(glfwGetKey(window,GLFW_MOD_SHIFT)==1)
			camera.offsetPosition(new Vector3f(0,-0.2f,0));
		if(glfwGetKey(window,GLFW_KEY_F3)==1&&glfwGetTime()>=controlDelay){
			draw=!draw;
			controlDelay = glfwGetTime()+0.2;
		}

		for(CoffeeGlfwInputListener listener : inputListeners)
			for(int key : listener.getRegisteredKeys())
				if(glfwGetKey(window,key)==1)
					listener.coffeeReceiveKeyPress(key);
	}
	
	private boolean draw = true;
	private double controlDelay = 0;

	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the ContextCapabilities instance and makes the OpenGL
		// bindings available for use.
		GLContext.createFromCurrent();
		
		// Set the clear color
		glClearColor(0.8f, 0.8f, 1.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glDepthFunc(GL_LESS);
		glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

		for(CoffeeRendererListener listener : listeners)
			listener.onGlfwReady();
		
		camera = new CoffeeCamera();
		
		light_sun = new LimeLight();
		light_sun.setIntensities(1f, 0.6f, 0.8f);
		
		camera.setCameraPos(new Vector3f(3,0,5));
		camera.lookAt(new Vector3f(0,0,0));
		
		boolean fpscounter = true;
		
		double fpsTimer = glfwGetTime()+1d;
		long framecount = 0;
		
		while ( glfwWindowShouldClose(window) == GL_FALSE ){
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			if(fpscounter){
				framecount++;
				if(glfwGetTime()>=fpsTimer){
					System.out.println("FPS: "+framecount);
					framecount = 0;
					fpsTimer = glfwGetTime()+1;
				}
			}
			
			light_sun.setPosition(Vector3f.add(camera.getCameraPos(), VectorTools.vectorMul(camera.getForward(), 4f), null));
			
			loopHandleMouseInput();
			loopHandleKeyboardInput();
			
			loopRenderObjects();
			loopCheckListeners();
			
			glfwSwapBuffers(window); // swap the color buffers
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
		for(ModelContainer object : displayLists)
			if(object.textureHandle!=0)
				GL11.glDeleteTextures(object.textureHandle);
	}
	
	private void loopCheckListeners(){
		for(CoffeeRendererListener listener : listeners){
			listener.onGlfwFrameTick();
		}
	}
	
	private void loopRenderObjects(){
		for(ModelContainer object : displayLists){
			if(!draw)
				continue;
			if(!object.isObjectBaked()){
				int textureUnit = textureUnits.pop();
				ShaderHelper.compileShaders(object,textureUnit,false);
			}
			GL20.glUseProgram(object.getShader().getProgramId());
			
			if(object.isBillboard()){
				object.rotation = camera.getForward();
			}

			object.getShader().setUniform("camera", camera.matrix());
			
			object.getShader().setUniform("model", ShaderHelper.rotateMatrice(object));
			
			object.getShader().setUniform("light.position", light_sun.getPosition());
			object.getShader().setUniform("light.intensities", light_sun.getIntensities());
			object.getShader().setUniform("light.attenuation", light_sun.getAttenuation());
			object.getShader().setUniform("light.ambientCoefficient", light_sun.getAmbientCoefficient());
			
			object.getShader().setUniform("materialTex", object.glTextureUnit-GL13.GL_TEXTURE0);
			object.getShader().setUniform("materialShininess", object.getMaterial().getShininess());
			object.getShader().setUniform("materialSpecularColor", object.getMaterial().getSpecularColor());
			
			GL13.glActiveTexture(object.glTextureUnit);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, object.textureHandle);

			GL30.glBindVertexArray(object.vaoHandle);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, object.getVertexDataSize());

			GL30.glBindVertexArray(0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			GL20.glUseProgram(0);
		}
	}

	private List<ModelContainer> displayLists = new ArrayList<>();
	
	public synchronized void addModel(ModelContainer object){
		if(displayLists.contains(object))
			throw new IllegalArgumentException("Object is already in the displaylist!");
		displayLists.add(object);
	}
	public synchronized void removeModel(ModelContainer object){
		displayLists.remove(object);
	}
	private List<CoffeeRendererListener> listeners = new ArrayList<>();
	private List<CoffeeGlfwInputListener> inputListeners = new ArrayList<>();	
	public synchronized void addCoffeeListener(CoffeeRendererListener listener){
		listeners.add(listener);
	}
	public synchronized void addInputListener(CoffeeGlfwInputListener listener){
		inputListeners.add(listener);
	}

}

