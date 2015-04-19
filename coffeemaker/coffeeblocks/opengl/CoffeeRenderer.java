package coffeeblocks.opengl;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALContext;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector4f;

import coffeeblocks.foundation.CoffeeGameObjectManager;
import coffeeblocks.general.VectorTools;
import coffeeblocks.interfaces.listeners.CoffeeGlfwInputListener;
import coffeeblocks.interfaces.listeners.CoffeeRendererListener;
import coffeeblocks.metaobjects.Vector3Container;
import coffeeblocks.openal.SoundObject;
import coffeeblocks.opengl.components.CoffeeCamera;
import coffeeblocks.opengl.components.CoffeeRenderableObject;
import coffeeblocks.opengl.components.CoffeeVertex;
import coffeeblocks.opengl.components.LimeLight;
import coffeeblocks.opengl.components.ShaderHelper;
import coffeeblocks.opengl.components.VAOHelper;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Vector2d;

public class CoffeeRenderer implements Runnable {

	// We need to strongly reference callback instances.
	private GLFWErrorCallback errorCallback;
//	private GLFWKeyCallback keyCallback;
	private GLFWMouseButtonCallback mouseCallback;
	private GLFWCursorPosCallback mousePosCallback;
	
	Map<String,Integer> textureRegister = new HashMap<>();
	
	private Vector4f clearColor = new Vector4f(0.8f,0.8f,1.0f,1f);
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
	private long triCount = 0;
	private double tick = 0;
	private float lastTick = 0f;
	private boolean fpscounter = true;
	
	private ByteBuffer vertBuffer = BufferUtils.createByteBuffer(4*3);
	private float fogDensity = 0.005f;
	
	private float mouseSensitivity = 0.1f;
	private boolean draw = true;
	private boolean doMouseGrab = true;
	private boolean mouseGrabbed = false;
	private int rendering_swaps = 0;
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
		getCamera().setAspect((float)windowres.x/(float)windowres.y);
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
	private ALContext alContext;

	public synchronized void run() {
		try {
			init();
			try{
				//Vi gjør dette fordi feilen ellers vil forsvinne ut i intet og ikke bli printet i det hele tatt.
				//Dette gjør debugging MYE enklere
				loop();
			}catch(RuntimeException e){
				e.printStackTrace();
			}

			// Release window and window callbacks
			glfwDestroyWindow(window);
			alContext.destroy();
//			keyCallback.release();
			mouseCallback.release();
			mousePosCallback.release();
		} finally {
			// Terminate GLFW and release the GLFWerrorfun
			glfwTerminate();
			errorCallback.release();
			for(CoffeeRendererListener listener : listeners)
				listener.onGlfwQuit();
		}
		for(CoffeeRendererListener listener : listeners)
			listener.onGlfwQuit();
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
		
//		aspect = (float)(rendering_resolution.x/rendering_resolution.y);

		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, "Café", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
//		glfwSetKeyCallback(window, keyCallback = GLFWKeyCallback((window, key, scancode, action, mods) ->  {
//			for(CoffeeGlfwInputListener listener : inputListeners)
//				if(listener.getRegisteredKeys().contains(key)){
//					if(action == GLFW_PRESS){
//						listener.coffeeReceiveKeyPress(key);
//					}else if(action == GLFW_REPEAT){
//						listener.coffeeReceiveKeyPress(key);
//					}else if(action == GLFW_RELEASE)
//						listener.coffeeReceiveKeyRelease(key);
//				}
//			if(glfwGetTime()>=controlDelay){
//				if(key==GLFW_KEY_F9&&action==GLFW_PRESS){
//					toggleGrabMouse();
//					controlDelay=glfwGetTime()+0.5d;
//				}
//			}
//		}));
		glfwSetCursorPosCallback(window, mousePosCallback = GLFWCursorPosCallback((window,xpos,ypos) -> {
			for(CoffeeGlfwInputListener listener : inputListeners)
				if(listener.getMouseEvents()&&mouseGrabbed)
					listener.coffeeReceiveMouseMove(xpos, ypos);
		}));
		glfwSetMouseButtonCallback(window, mouseCallback = GLFWMouseButtonCallback((window,button,action,mods) -> {
			for(CoffeeGlfwInputListener listener : inputListeners)
				if(listener.getRegisteredMouseButtons().contains(button))
					if(action==GLFW_PRESS){
						listener.coffeeReceiveMousePress(button);
					}else{
						listener.coffeeReceiveMouseRelease(button);
					}
		}));


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
		alContext = ALContext.create();
		alContext.makeCurrent();
		// Enable v-sync
		glfwSwapInterval(this.rendering_swaps);
		
		if(doMouseGrab)
			toggleGrabMouse();

		// Make the window visible
		glfwShowWindow(window);
	}
	
	public void requestClose(){
		glfwSetWindowShouldClose(window, GL_TRUE);
	}
	
	public void glfwResetCursor(){
		glfwSetCursorPos(window,0,0);
	}
	
	private double controlDelay = 0;

	public void loopHandleKeyboardInput(){
		for(CoffeeGlfwInputListener listener : inputListeners)
			for(int key : listener.getRegisteredKeys()){
				if(glfwGetKey(window,key)==1)
					listener.coffeeReceiveKeyPress(key);
				else
					listener.coffeeReceiveKeyRelease(key);
			}
		if(glfwGetTime()>=controlDelay){
			if(glfwGetKey(window,GLFW_KEY_F9)==1){
				toggleGrabMouse();
				controlDelay=glfwGetTime()+0.5d;
			}
		}
	}

	public void loopHandleAudio(){
		for(SoundObject obj : new ArrayList<>(soundObjects.values())){
			if(!obj.isBaked()&&!obj.initSound())
				soundObjects.remove(obj);
			if(!obj.hasSource())
				obj.genSource();
		}
		if(scene!=null)scene.getInstantiableSounds().parallelStream().filter(o -> !o.isBaked()).sequential().filter(o -> !o.initSound()).forEach(obj -> soundObjects.remove(obj));
		
		AL10.alListener(AL10.AL_POSITION, VectorTools.vecToFloatBuffer(al_listen_position.getValue()));
		AL10.alListener(AL10.AL_VELOCITY, VectorTools.vecToFloatBuffer(al_listen_position.getVelocity()));
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
	
	public void grabMouse(boolean todoOrNotToDo){ //Denne vil kunne brukes fra utsiden for å påtvinge en spesiell tilstand.
		if(mouseGrabbed&&todoOrNotToDo)
			toggleGrabMouse();
		else if(!mouseGrabbed&&!todoOrNotToDo)
			toggleGrabMouse();
	}
	
	private void fpsCount(){
		if(fpscounter){
			framecount++;
			if(glfwGetTime()>=fpsTimer){
				System.out.println("FPS: "+framecount+"\nTriangles: "+triCount
						+"\nTick: "+String.format("%3f", tick*1000f)+"ms");
				framecount = 0;
				triCount = 0;
				fpsTimer = glfwGetTime()+1;
			}
		}
	}

	double clockLoop = 0;
	double tickLoop = 0;
	double renderingLoop = 0;
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
		
		while (glfwWindowShouldClose(window)==GL_FALSE){
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
			fpsCount(); //Skriver ut FPS og tikke-tid
			lastTick = (float)tick;
			tick = glfwGetTime(); //Måler mengden tid det tar for å rendre objektene

			//Under testing senket dette prosessorbruk i forhold til vanlige for-looper
			listeners.stream().forEach(listener -> {
				//Vi varsler lyttere om at et nytt tikk har skjedd
				listener.onGlfwFrameTick();
				//Vi vil ha oversikt over spilltiden i de andre trådene
				listener.onGlfwFrameTick(glfwGetTime());
			});
			//Dersom prosessene ikke skapte problemer med ComodificationException kunne dette gitt en grei ytelsesøkning
			listeners.stream().sequential().forEach(listener -> {
				//Dette for lyttere som avhenger av mengden tid passert (fysikk bl.a)
				listener.onGlfwFrameTick(lastTick);
			});
			
			loopHandleKeyboardInput();
			loopHandleAudio();

//			framebuffer.storeFramebuffer(rendering_resolution);
			if(draw&&scene!=null){ //Slå av rendring av objekter, dermed kan vi ta vekk og bytte objekter som skal vises
				for(CoffeeRenderableObject object : scene.getInstantiableModels()) //Vi vil forhåndslaste shaders og teksturer for disse objektene slik at de ikke forårsaker problemer
					if(!object.isTextureLoaded()){
						ShaderHelper.loadTextures(object,textureRegister);
						ShaderHelper.setupShader(object);
					}
				loopRenderObjects(); //Rendring av objektene, enten til et framebuffer eller direkte
			}
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
		if(scene!=null)
			for(CoffeeRenderableObject object : scene.getRenderables())
				cleanupObject(object);
	}
	
	private void cleanupObject(CoffeeRenderableObject object){
		if(object.getMaterial().getTextureHandle()!=0)
			GL11.glDeleteTextures(object.getMaterial().getTextureHandle());
		if(object.getMaterial().getBumpTextureHandle()!=0)
			GL11.glDeleteTextures(object.getMaterial().getBumpTextureHandle());
		if(object.getMaterial().getHighlightTextureHandle()!=0)
			GL11.glDeleteTextures(object.getMaterial().getHighlightTextureHandle());
		if(object.getMaterial().getSpecularTextureHandle()!=0)
			GL11.glDeleteTextures(object.getMaterial().getSpecularTextureHandle());
		if(object.getMaterial().getTransparencyTextureHandle()!=0)
			GL11.glDeleteTextures(object.getMaterial().getTransparencyTextureHandle());
		if(object.getMaterial().getVaoHandle()!=0)
			GL30.glDeleteVertexArrays(object.getMaterial().getVaoHandle());
		if(object.getShader().getProgramId()!=0)
			GL20.glDeleteProgram(object.getShader().getProgramId());
		object.setObjectBaked(false);
		object.setTextureLoaded(false);
	}
	
	private void loopRenderObjects(){
		scene.getRenderablesOrdered().stream().sequential().forEach(e -> renderObject(e));
	}
	private void renderObject(CoffeeRenderableObject object){
		if(!object.isDrawObject())
			return;
		if(!object.isBaked()){
			ShaderHelper.compileShaders(object,textureRegister);
		}
		GL20.glUseProgram(object.getShader().getProgramId());
		
		if(!object.isDepthTest())
			glDisable(GL_DEPTH_TEST);

		//Animasjon ved å endre modellen fra en annen tråd
		if(!object.isStaticDraw()){
			//Vi bruker en enkel ByteBuffer for alle for å unngå tonnevis med allokasjoner per sekund. Vi tilbakestiller denne hver gang vi skal rendre på nytt.
			//Dersom vi ikke gjør dette synker ytelsen *dramatisk*
			VAOHelper.modifyVbo(object.getVboHandle(), object.getVertices(),vertBuffer);
		}
		
		object.getShader().setUniform("camera", camera.matrix());
		object.getShader().setUniform("cameraPosition", camera.getCameraPos().getValue());

		object.getShader().setUniform("model", ShaderHelper.rotateMatrice(object));
		for(LimeLight light : lights){ //Selv om vi kun støtter ett lys for øyeblikket skriver vi koden som om vi støttet flere. Vi kan legge til flere lys i fremtiden om nødvendig.
			object.getShader().setUniform("light.position", light.getPosition().getValue());
			object.getShader().setUniform("light.intensities", light.getIntensities());
			object.getShader().setUniform("light.attenuation", light.getAttenuation());
			object.getShader().setUniform("light.ambientCoefficient", light.getAmbientCoefficient());
		}

		object.getShader().setUniform("materialTex", 0); //Vi leser fargetekstur fra GL_TEXTURE(0), samme med de under
		object.getShader().setUniform("materialBump", 1);
		object.getShader().setUniform("materialSpecular", 2);
		object.getShader().setUniform("materialHighlight", 3);
		object.getShader().setUniform("materialTransparency", 4);
		object.getShader().setUniform("materialShininess", object.getMaterial().getShininess());
		object.getShader().setUniform("materialSpecularColor", object.getMaterial().getSpecularColor());
		object.getShader().setUniform("materialTransparencyValue", object.getMaterial().getTransparency());
		
		object.getShader().setUniform("fogParams.fDensity", fogDensity);
		object.getShader().setUniform("fogParams.fColor", getClearColor());
		
		//Vi legger inn teksturene i minne for å kunne bruke de
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

		//Vi bind'er lokasjonen i minne hvor punktene befinner seg og tegner det på skjermen
		GL30.glBindVertexArray(object.getMaterial().getVaoHandle());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, object.getVertexDataSize());
		triCount+=object.getVertexDataSize()/(CoffeeVertex.VERTEX_DATA_SIZE)/3; //Vi vil vite hvor mange polygoner vi har på skjermen

		//Vi ber tilstandsmaskinen om å tilbakestilles for å unngå mulige feil
		GL30.glBindVertexArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL20.glUseProgram(0);
		
		if(!object.isDepthTest())
			glEnable(GL_DEPTH_TEST);
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

	private Vector3Container al_listen_position = new Vector3Container();
	public Vector3Container getAlListenPosition(){
		return al_listen_position;
	}
	
	private Map<String,SoundObject> soundObjects = new HashMap<>();
	public void addSoundObject(SoundObject obj){
		if(obj==null)
			throw new IllegalArgumentException("Invalid sound object!");
		soundObjects.put(obj.getSoundId(), obj);
	}
	public void addSounds(CoffeeGameObjectManager manager){
		manager.getObjectList().parallelStream().forEach(object -> object.getSoundBox().stream().forEach(sound -> {
			try{
				addSoundObject(sound);
			}catch(IllegalArgumentException e){
				System.err.println(e.getMessage());
			}
		}));
	}

	public void al_playSound(String id){
		if(soundObjects.containsKey(id))
			AL10.alSourcePlay(soundObjects.get(id).getSource());
		else
			System.out.println("Could not find sound: "+id);
	}
	public void al_pauseSound(String id){
		if(soundObjects.containsKey(id))
			AL10.alSourcePause(soundObjects.get(id).getSource());
	}
	public void al_stopSound(String id){
		if(soundObjects.containsKey(id))
			AL10.alSourceStop(soundObjects.get(id).getSource());
	}
}

