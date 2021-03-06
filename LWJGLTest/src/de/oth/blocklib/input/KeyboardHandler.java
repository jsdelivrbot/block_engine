package de.oth.blocklib.input;

import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.*;

/**
 * The GLFWKeyCallback class is an abstract method that can't be instantiated by.
 * itself and must instead be extended
 */
public class KeyboardHandler extends GLFWKeyCallback {

	public static boolean[] keys = new boolean[65536];

	/**
	 * The GLFWKeyCallback class is an abstract method that can't be
	 * instantiated by itself and must instead be extended
	 */
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		keys[key] = action != GLFW_RELEASE;
		Keyboard.keyPressed(key, action);
	}

	/**
	 * boolean method that returns true if a given key is pressed.
	 * 
	 * @param keycode keycode from GLFW
	 * @return pressed or not
	 */
	public static boolean isKeyDown(int keycode) {
		return keys[keycode];
	}
}