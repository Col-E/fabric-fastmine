package me.coley.fastmine.mixin;

import me.coley.fastmine.FastmineMod;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Injector to modify block breaking progress.
 *
 * @author Matt Coley
 */
@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
	@Inject(at = @At("RETURN"), method = "attackBlock")
	public void onAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		if (FastmineMod.doResetDelay())
			accessors().setBlockBreakingCooldown(0);
	}

	@Inject(at = @At("RETURN"), method = "breakBlock")
	public void breakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (FastmineMod.doResetDelay())
			accessors().setBlockBreakingCooldown(0);
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(at = @At("HEAD"), method = "updateBlockBreakingProgress")
	public void updateBlockBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		if (FastmineMod.doResetDelay())
			accessors().setBlockBreakingCooldown(0);
		// Skip progress updating if no modification is needed
		if (FastmineMod.isDefaultSpeed())
			return;
		// Get state, update progress again
		float progress = accessors().getCurrentBreakingProgress();
		MinecraftClient client = MinecraftClient.getInstance();
		BlockState state = client.world.getBlockState(pos);
		if (state != null) {
			float modifier = FastmineMod.getMultiplier();
			float increment = state.calcBlockBreakingDelta(client.player, client.player.world, pos);
			// The progress will be made again after this head injection, so we are adding onto it.
			// - 2X modifier adds 1X the progress.
			// - 3X modifier adds 2X the progress.
			// However, for slower speeds
			// - 0.50X modifier subtracts 0.50X the progress.
			// - 0.25X modifier subtracts 0.75X the progress.
			if (modifier > 1) {
				progress += increment * (modifier - 1);
			} else {
				progress -= increment * (1 - modifier);
			}
		}
		// Update progress
		accessors().setCurrentBreakingProgress(progress);
	}

	private ClientPlayerInteractionManagerAccessors accessors() {
		return ((ClientPlayerInteractionManagerAccessors) this);
	}
}
