package de.oth.blocklib.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.oth.blocklib.entities.Camera;
import de.oth.blocklib.entities.Light;
import de.oth.blocklib.helper.Maths;

/**
 * Extends ShaderProgram. Here is the location of the
 * vertex shader and the fragment shader hardcoded.
 * @see ShaderProgram
 */
public class StaticShader extends ShaderProgram {

	private static final String VERTEX_FILE = "shaders/vertexShader";	
	private static final String FRAGMENT_FILE = "shaders/fragmentShader";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition;
	private int location_lightColour;
	private int location_skyColour;
	
	/**
	 * Use constructor of StaticShader.
	 * @see StaticShader
	 */
	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	/**
	 * Binds Attribute of VAO to variable in shader
	 */
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	/**
	 * Loads the location of all matrices
	 * 
	 */
	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix= super.getUniformLocation("transformationMatrix");	
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_lightPosition = super.getUniformLocation("lightPosition");
		location_lightColour = super.getUniformLocation("lightColour");
		location_skyColour = super.getUniformLocation("skyColour");
	}

	/**
	 * Load up the colour of the sky onto the fragmentshader.
	 * @param r red value
	 * @param g green value
	 * @param b blue value
	 */
	public void loadSkyColour(float r, float g, float b){
		super.loadVector(location_skyColour, new Vector3f(r,g,b));
	}
	
	/**
	 * Loads TransformationMatrix
	 * 
	 * @param matrix
	 *            Transformation Matrix
	 */
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	/**
	 * Loads light position and colour
	 * 
	 * @param light
	 *            Light with Position and Colour
	 */
	public void loadLight(Light light){
		super.loadVector(location_lightPosition, light.getPosition());
		super.loadVector(location_lightColour, light.getColour());
	}

	/**
	 * Load the projection matrix in the shader.
	 * @param projection projection matrix to load in the shader.
	 */
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	/**
	 * Load the view matrix in the shader.
	 * @param camera Camera to create the view matrix from.
	 */
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
}
