package me.coley.fastmine.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Getters and setters needed to modify block breaking progress.
 *
 * @author Matt Coley
 */
@Mixin(ClientPlayerInteractionManager.class)
public interface ClientPlayerInteractionManagerAccessors {
	@Accessor("currentBreakingProgress")
	float getCurrentBreakingProgress();

	@Accessor("currentBreakingProgress")
	void setCurrentBreakingProgress(float value);

	@Accessor("blockBreakingCooldown")
	void setBlockBreakingCooldown(int value);
}