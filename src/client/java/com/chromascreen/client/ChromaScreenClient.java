package com.chromascreen.client;

import com.chromascreen.block.ChromaBlocks;
import com.chromascreen.client.render.ChromaRenderCache;
import com.chromascreen.client.render.ChromaRenderPipeline;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;

public class ChromaScreenClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ChromaRenderPipeline.initialize();
		ClientChunkEvents.CHUNK_LOAD.register(ChromaRenderCache::onChunkLoad);
		ClientChunkEvents.CHUNK_UNLOAD.register(ChromaRenderCache::onChunkUnload);
		LevelRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, outline) ->
			!ChromaBlocks.isChroma(Minecraft.getInstance().level.getBlockState(outline.pos()).getBlock()));
	}
}
