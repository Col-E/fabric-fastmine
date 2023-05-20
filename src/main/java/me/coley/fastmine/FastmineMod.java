package me.coley.fastmine;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * Base mod and state. Things could be cleaned up, but I made this in just a few minutes.
 *
 * @author Matt Coley
 */
public class FastmineMod implements ClientModInitializer {
	private static final int X1_SPEED_INDEX = 2;
	private static final float[] multipilers = {
			0.25F,
			0.50F,
			1.00F,
			2.00F,
			3.00F,
			4.00F,
			8.00F,
	};
	private static final String[] multiplierStrings = {
			"x0.25",
			"x0.50",
			"x1.00",
			"x2.00",
			"x3.00",
			"x4.00",
			"x8.00",
	};
	private static int multiplierIndex = X1_SPEED_INDEX;
	private static float multiplier = multipilers[multiplierIndex];
	private static boolean resetDelay = true;
	private KeyBinding bindToggleDelay;
	private KeyBinding bindFaster;
	private KeyBinding bindSlower;

	@Override
	public void onInitializeClient() {
		this.bindToggleDelay = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.fastmine.toggledelay",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_RIGHT_BRACKET,
				"category.fastmine"
		));
		this.bindFaster = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.fastmine.faster",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_EQUAL,
				"category.fastmine"
		));
		this.bindSlower = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.fastmine.slower",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_MINUS,
				"category.fastmine"
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			ClientPlayerEntity player = client.player;
			if (player != null) {
				boolean indexDirty = false;
				while (bindFaster.wasPressed()) {
					multiplierIndex = clampMultiplierIndex(++multiplierIndex);
					indexDirty = true;
				}
				while (bindSlower.wasPressed()) {
					multiplierIndex = clampMultiplierIndex(--multiplierIndex);
					indexDirty = true;
				}
				while (bindToggleDelay.wasPressed()) {
					resetDelay = !resetDelay;
					player.sendMessage(Text.literal("Reset hit delay: " + resetDelay), false);
				}
				}
				if (indexDirty) {
					multiplier = multipilers[multiplierIndex];
					player.sendMessage(Text.literal("Mining multiplier: " +
							multiplierStrings[multiplierIndex]), false);
				}
			}
		});
	}

	/**
	 * @return {@code true} when the current multiplier is 1X the default speed <i>(no difference in game behavior)</i>.
	 */
	public static boolean isDefaultSpeed() {
		return multiplierIndex == X1_SPEED_INDEX;
	}

	/**
	 * @return Mining speed multiplier.
	 */
	public static float getMultiplier() {
		return multiplier;
	}

	/**
	 * @return {@code true} for resetting the standard {@code 5} tick block hit delay.
	 */
	public static boolean doResetDelay() {
		return resetDelay;
	}

	/**
	 * @param newValue
	 * 		New value intended for {@link #multiplierIndex}.
	 *
	 * @return Clamped value to prevent {@link IndexOutOfBoundsException}.
	 */
	private static int clampMultiplierIndex(int newValue) {
		if (newValue < 0)
			return 0;
		if (newValue >= multipilers.length)
			return multipilers.length - 1;
		return newValue;
	}
}
