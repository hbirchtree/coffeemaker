package coffeeblocks.opengl;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.CoffeeRendererListener;
import coffeeblocks.foundation.input.CoffeeGlfwInputListener;
import coffeeblocks.foundation.models.ModelContainer;
import coffeeblocks.foundation.models.ModelLoader;
import coffeeblocks.threads.CoffeeSynchronizerWaiter;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.util.ArrayList;
import java.util.List;

public class CoffeeRenderer implements Runnable {

	// We need to strongly reference callback instances.
	private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback   keyCallback;
	
	private CoffeeCamera camera = null;
	private LimeLight light_sun = null;

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
		
//		glfwSetInputMode(window,GLFW_CURSOR,GLFW_CURSOR_DISABLED);
//		glfwSetCursorPos(window,0,0);

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
	
	
	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the ContextCapabilities instance and makes the OpenGL
		// bindings available for use.
		GLContext.createFromCurrent();
		
		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.1f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glDepthFunc(GL_LESS);
		glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

		for(CoffeeRendererListener listener : listeners)
			listener.onGlfwReady();
		
		camera = new CoffeeCamera();
		
		light_sun = new LimeLight();
		light_sun.setPosition(new Vector3f(5f,10f,5f));
		light_sun.setIntensities(1f, 1f, 1f);
		
		//Tråden må vente på at alle modellene er lastet inn, derfor venter vi her.
//		double nextCheck = glfwGetTime()+1;
//		while(glfwWindowShouldClose(window) == GL_FALSE){
//			if(glfwGetTime()>=nextCheck){
//				if(loaderFinished)
//					break;
//				nextCheck = glfwGetTime()+1;
//			}
//			//vi holder vinduet aktivt og unngår at det fryser
//			glfwSwapBuffers(window);
//			glfwPollEvents();
//		}
		
		camera.setCameraPos(new Vector3f(3,5,5));
		
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
		for(CoffeeGlfwInputListener listener : inputListeners)
			for(int key : listener.getRegisteredKeys())
				if(glfwGetKey(window,key)==1)
					listener.coffeeReceiveKeyPress(key);
		for(CoffeeRendererListener listener : listeners){
			listener.onGlfwFrameTick();
		}
	}
	
	private void loopRenderObjects(){
		for(ModelContainer object : displayLists){
			if(!object.isObjectBaked())
				ShaderHelper.compileShaders(object);
			GL20.glUseProgram(object.getShader().getProgramId());

			object.getShader().setUniform("camera", camera.matrix());
			
			object.getShader().setUniform("model", ShaderHelper.rotateMatrice(object));
			
			object.getShader().setUniform("light.position", light_sun.getPosition());
			object.getShader().setUniform("light.intensities", light_sun.getIntensities());
			object.getShader().setUniform("light.attenuation", light_sun.getAttenuation());
			object.getShader().setUniform("light.ambientCoefficient", light_sun.getAmbientCoefficient());
			
			object.getShader().setUniform("materialTex", 0);
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

