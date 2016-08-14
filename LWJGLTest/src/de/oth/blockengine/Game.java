package de.oth.blockengine;

import static de.oth.blockengine.helper.Log.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.util.vector.Vector3f;

import de.oth.blockengine.entities.Camera;
import de.oth.blockengine.entities.Entity;
import de.oth.blockengine.entities.Light;
import de.oth.blockengine.helper.Utility;
import de.oth.blockengine.input.Keyboard;
import de.oth.blockengine.input.KeyboardHandler;
import de.oth.blockengine.input.MouseHandler;
import de.oth.blockengine.loader.ModelData;
import de.oth.blockengine.loader.OBJLoader;
import de.oth.blockengine.models.RawModel;
import de.oth.blockengine.models.TexturedModel;
import de.oth.blockengine.renderer.Loader;
import de.oth.blockengine.renderer.MasterRenderer;
import de.oth.blockengine.textures.ModelTexture;
import de.oth.blockengine.world.BlockType;
import de.oth.blockengine.world.WorldData;

/**
 * This class defines the game loop and holds instances of every class
 * Start the window with start()
 */
public class Game {
	private long windowID;
	private Configuration config = new Configuration();
	private GLFWKeyCallback keyCallback;
	private MouseHandler mouseCallback;
	private Loader loader;
	private MasterRenderer renderer;
	private RawModel model;
	private Entity activePlayerBlock;
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	private Camera camera = new Camera();
	private Light light;
	private WorldData world;
	private int fps;
    private long lastFPS;
	private Random rand = new Random();
	
    float temp = 0;
	
    /**
     * Create and initialize a new game with default configuration
     */
	public Game() {
		System.out.println("LWJGL " + Version.getVersion() + "!");
		
		loader = new Loader();
		
		config.loadConfiguration();

		if (glfwInit() != true) {
			System.err.println("Error initializing GLFW");
			System.exit(1);
		}

		// Window Hints for OpenGL context
		glfwWindowHint(GLFW_SAMPLES, 4);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_ANY_PROFILE);
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
		if(Configuration.FULLSCREEN){
			windowID = glfwCreateWindow(config.getWidth(), config.getHeight(), "LWJGL Block Engine", glfwGetPrimaryMonitor(), NULL);

		} else {
			windowID = glfwCreateWindow(config.getWidth(), config.getHeight(), "LWJGL Block Engine", NULL, NULL);
		}

		if (windowID == NULL) {
			System.err.println("Error creating a window");
			System.exit(1);
		}

		glfwMakeContextCurrent(windowID);
		GL.createCapabilities();

		glfwSwapInterval(1);
		glfwShowWindow(windowID);
	}

	/**
	 * Initializes opengl, shaders, entities, camera and loads everything
	 * 
	 */
	public void init() {
		// Debuginformation
		System.out.println("OpenGL: " + glGetString(GL_VERSION));
		lastFPS = Utility.getTimeInMilliseconds();

		// Init Shader & Renderer
		glEnable(GL_DEPTH_TEST);
		renderer = new MasterRenderer(world);
		
		// Load 3d models
		ModelData modelData = OBJLoader.loadOBJ("cube");
		model = loader.loadToVAO(modelData.getVertices(), 
				modelData.getTextureCoords(), 
				modelData.getNormals(), 
				modelData.getIndices());
		
		// Load textures and create models with them
		ModelTexture texture;
		texture = new ModelTexture(loader.loadTexture("stone"));
		TexturedModel texMod1 = new TexturedModel(model, texture);
		
		texture = new ModelTexture(loader.loadTexture("spritesheet"));
		TexturedModel texMod2 = new TexturedModel(model, texture);
		
		Entity enti = new Entity(texMod2, new Vector3f(0f,0,-1.5f),0,0,0,0.25f);
		entities.add(enti);
		
		activePlayerBlock = new Entity(texMod1, new Vector3f(0f,0,-1.5f),0,0,0,0.25f);
		activePlayerBlock.setPosition(new Vector3f(camera.getPosition().x+10, camera.getPosition().y+10, camera.getPosition().z+10));

		entities.add(activePlayerBlock);
		
		light = new Light(new Vector3f(3000,2000,3000), new Vector3f(1,1,1));
		
		// Create World
		world = new WorldData(Configuration.getWorldSize(), loader, texture);

		
//		for(int i = 0; i < 0; i++){
//			for(int k = 0; k< 0; k++){
//				for(int j = 0; j< 0; j++){
//					Entity entity = new Entity(texMod1, new Vector3f(0.20f*i-0.9f,-0.6f-0.2f*j,-1.8f-0.2f*k),0,0,0,0.2f);
//					entitys.add(entity);
//				}				
//			}
//		}
	}

	/**
	 * This method is used to update the game logic
	 * 
	 * @param delta
	 *            time since last call
	 */
	public void update(float delta) {
		
		//process input: keyboard
		Keyboard.HandleInput(delta, camera, windowID);
		
		//Update active player block
		updatePlayerBlock(delta);
		
		//Temp
		if(Configuration.runDemo){
			temp += delta;
			if(temp > 2){
				if(rand.nextBoolean()){
					world.changeBlock(rand.nextInt(world.worldSize), rand.nextInt(world.worldSize), rand.nextInt(world.worldSize), BlockType.Nothing);					
				} else {
					world.changeBlock(rand.nextInt(world.worldSize), rand.nextInt(world.worldSize), rand.nextInt(world.worldSize), BlockType.Grass);					
				}
				world.recreateMesh();	
				temp = 0;
			}			
		}
	}

	public void updatePlayerBlock(float delta){
		activePlayerBlock.setRotX(0.5f);
//		activePlayerBlock.setPosition(camera.getPosition().add(new Vector3f(10,10,10), new Vector3f(10,10,10), new Vector3f(10,10,10)));
	}
	
	/**
	 * This method is used to initialize the renderer & shader
	 * 
	 * @param delta
	 *            time since last call
	 */
	public void render(float delta) {
		// Add every entity in the renderer
		for (Entity entity : entities) {
			renderer.processEntity(entity);
		}
		
		// render scene
		renderer.render(light, camera, world);
	}

	/**
	 * Called after the game ends to cleanup
	 * 
	 */
	public void dispose() {
		loader.cleanUp();
		renderer.cleanUp();
		world.cleanUp();
	}

	/**
	 * Starts the engine
	 * 
	 */
	public void start() {
		float now, last, delta;
		last = 0;

		// Initialize the Game
		init();

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetInputMode(windowID, GLFW_CURSOR, GLFW_CURSOR_DISABLED); // to hide and lock the cursor
        glfwSetKeyCallback(windowID, keyCallback = new KeyboardHandler());
        glfwSetCursorPosCallback(windowID, mouseCallback = new MouseHandler(camera, config, renderer));
        
		// Loop continuously and render and update
		while (glfwWindowShouldClose(windowID) != true) {
			// Get the time
			now = (float) glfwGetTime();
			delta = now - last;
			last = now;
			
			// Reset mouse position
			glfwSetCursorPos(windowID, Configuration.getWidth()/2, Configuration.getWidth()/2);

			// Update and render
			update(delta);
			render(delta);
			performanceLog(delta);

			// Poll the events and swap the buffers
			glfwPollEvents();
			glfwSwapBuffers(windowID);
		}

		// Dispose the game
		dispose();

		// Destroy the window
		glfwDestroyWindow(windowID);
		glfwTerminate();

		System.exit(0);
	}

	/**
	 * Prints information about the performance of the engine
	 * @param delta time since last update call
	 */
	public void performanceLog(float delta){
		// Calculate Memory
		int mb = 1024*1024;
        if (Utility.getTimeInMilliseconds() - lastFPS > 1000) {
		    //Getting the runtime reference from system
		    Runtime runtime = Runtime.getRuntime();
		     
		    info("##### Heap utilization statistics [MB] #####");
		     
		    //Print used memory
		    info("Used Memory:"
		            + (runtime.totalMemory() - runtime.freeMemory()) / mb);
		 
		    //Print free memory
		    info("Free Memory:"
		        + runtime.freeMemory() / mb);
		     
		    //Print total available memory
		    info("Total Memory:" + runtime.totalMemory() / mb);
		 
		        //Print Maximum available memory
		    info("Max Memory:" + runtime.maxMemory() / mb);
        }
        
		// Calculate FPS
	    if (Utility.getTimeInMilliseconds() - lastFPS > 1000) {
	    	glfwSetWindowTitle(windowID, "FPS: " + fps + " | RT: ~" + renderer.getTimeToRender() + "�s");
	    	info("FPS: " + fps);
	        fps = 0; //reset the FPS counter
	        lastFPS += 1000; //add one second
	    }
	    fps++;
	}


	
	public static void main(String[] args) {
		new Game().start();
	}
}