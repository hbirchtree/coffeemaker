import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
 
public class HelloWorld {
 
    // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;
 
    
    // The window handle
    private long window;
 
    public void run() {
        System.out.println("Hello LWJGL " + Sys.getVersion() + "!");
 
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
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
 
        int WIDTH = 300;
        int HEIGHT = 300;
 
        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");
 
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
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        FloatBuffer pos = BufferUtils.createFloatBuffer(4).put(new float[] { 5.0f, 5.0f, 10.0f, 0.0f });
        FloatBuffer green = BufferUtils.createFloatBuffer(4).put(new float[] { 0.0f, 0.8f, 0.2f, 1.0f });
        green.flip();
        pos.flip();
        
        glLight(GL_LIGHT0, GL_POSITION, pos);
		glEnable(GL_CULL_FACE);
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		glEnable(GL_DEPTH_TEST);
        
        int gear2 = glGenLists(1);
		glNewList(gear2, GL_COMPILE);
		glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, green);
		box();
		glEndList();
		
		glEnable(GL_NORMALIZE);

		glMatrixMode(GL_PROJECTION);

		float h = (float)300 / (float)300;
		glFrustum(-1.0f, 1.0f, -h, h, 5.0f, 60.0f);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glTranslatef(0.0f, 0.0f, -40.0f);
 
		float angle = 2.0f;
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( glfwWindowShouldClose(window) == GL_FALSE ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            
            glPushMatrix();
    		glTranslatef(0.0f, 0.0f, 0.0f);
    		glRotatef(angle, 1.0f, 0.0f, 0.0f);
    		glCallList(gear2);
    		glPopMatrix();
    		
    		glPopMatrix();
    		

            glfwSwapBuffers(window); // swap the color buffers
 
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            angle += 5.0f;
        }
    }
    
    private static void box() {
		glShadeModel(GL_SMOOTH);

		glNormal3f(1.0f, 1.0f, 0.0f);
		
		//Side 1
		glBegin(GL_TRIANGLES);
		glVertex3f(0.0f,0.0f,0.0f);
		glVertex3f(3.0f,0.0f,0.0f);
		glVertex3f(3.0f,3.0f,0.0f);
		glEnd();
		
		glBegin(GL_TRIANGLES);
		glVertex3f(0.0f,0.0f,0.0f);
		glVertex3f(3.0f,3.0f,0.0f);
		glVertex3f(0.0f,3.0f,0.0f);
		glEnd();

		//Side 2
		glBegin(GL_TRIANGLES);
		glVertex3f(0.0f,0.0f,0.0f);
		glVertex3f(3.0f,0.0f,0.0f);
		glVertex3f(3.0f,0.0f,3.0f);
		glEnd();

		glBegin(GL_TRIANGLES);
		glVertex3f(0.0f,0.0f,0.0f);
		glVertex3f(3.0f,0.0f,3.0f);
		glVertex3f(0.0f,0.0f,3.0f);
		glEnd();
		
		glShadeModel(GL_SMOOTH);
    }

    public static void main(String[] args) {
        new HelloWorld().run();
    }
 
}