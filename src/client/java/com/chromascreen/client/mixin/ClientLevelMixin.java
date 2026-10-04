package com.chromascreen.client.mixin;

import com.chromascreen.client.render.ChromaRenderCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
	@Inject(method = "setBlock", at = @At("RETURN"))
	private void chroma$trackBlock(BlockPos pos, BlockState state, int flags, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue()) {
			ChromaRenderCache.onBlockChanged((ClientLevel) (Object) this, pos);
		}
	}

	// Server corrections call super.setBlock directly, bypassing ClientLevel.setBlock.
	@Inject(method = "setServerVerifiedBlockState", at = @At("RETURN"))
	private void chroma$trackServerBlock(BlockPos pos, BlockState state, int flags, CallbackInfo ci) {
		ChromaRenderCache.onBlockChanged((ClientLevel) (Object) this, pos);
	}
}
