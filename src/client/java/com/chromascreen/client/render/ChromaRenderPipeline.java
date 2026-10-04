package com.chromascreen.client.render;

import com.chromascreen.ChromaScreen;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;

/** Vanilla position/color shader, with opaque output and ordinary world depth. */
public final class ChromaRenderPipeline {
	public static final RenderPipeline PIPELINE = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(ChromaScreen.id("pipeline/chroma_screen"))
			.withColorTargetState(ColorTargetState.DEFAULT)
			.withDepthStencilState(DepthStencilState.DEFAULT)
			.build()
	);

	private ChromaRenderPipeline() {
	}

	public static void initialize() {
		// Force registration before the client's shader pipeline cache is built.
	}
}
