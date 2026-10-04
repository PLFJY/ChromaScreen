package com.chromascreen.client.mixin;

import com.chromascreen.client.render.ChromaScreenRenderer;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
	@Unique private GpuBufferSlice chroma$projection;

	@Inject(method = "render", at = @At("HEAD"))
	private void chroma$captureProjection(GraphicsResourceAllocator allocator, boolean renderOutline,
		CameraRenderState camera, GpuBufferSlice terrainFog, Vector4f fogColor,
		boolean renderSky, boolean consistentDepth, CallbackInfo ci) {
		chroma$projection = RenderSystem.getProjectionMatrixBuffer();
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void chroma$renderLate(GraphicsResourceAllocator allocator, boolean renderOutline,
		CameraRenderState camera, GpuBufferSlice terrainFog, Vector4f fogColor,
		boolean renderSky, boolean consistentDepth, CallbackInfo ci) {
		ChromaScreenRenderer.render(camera, chroma$projection);
		chroma$projection = null;
	}
}
