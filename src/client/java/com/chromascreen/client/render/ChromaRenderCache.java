package com.chromascreen.client.render;

import com.chromascreen.block.ChromaBlocks;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

/** Tracks only chroma positions in loaded client chunks. */
public final class ChromaRenderCache {
	private static final LongOpenHashSet POSITIONS = new LongOpenHashSet();
	private static ClientLevel level;

	private ChromaRenderCache() {
	}

	public static LongOpenHashSet positions(ClientLevel currentLevel) {
		ensureLevel(currentLevel);
		return POSITIONS;
	}

	public static void onChunkLoad(ClientLevel currentLevel, LevelChunk chunk) {
		ensureLevel(currentLevel);
		removeChunk(chunk);
		LevelChunkSection[] sections = chunk.getSections();
		int originX = chunk.getPos().getMinBlockX();
		int originZ = chunk.getPos().getMinBlockZ();
		for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
			LevelChunkSection section = sections[sectionIndex];
			if (section.hasOnlyAir() || !section.maybeHas(state -> ChromaBlocks.isChroma(state.getBlock()))) {
				continue;
			}
			int originY = currentLevel.getMinY() + sectionIndex * 16;
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					for (int x = 0; x < 16; x++) {
						if (ChromaBlocks.isChroma(section.getBlockState(x, y, z).getBlock())) {
							POSITIONS.add(BlockPos.asLong(originX + x, originY + y, originZ + z));
						}
					}
				}
			}
		}
	}

	public static void onChunkUnload(ClientLevel currentLevel, LevelChunk chunk) {
		ensureLevel(currentLevel);
		removeChunk(chunk);
	}

	public static void onBlockChanged(ClientLevel currentLevel, BlockPos pos) {
		ensureLevel(currentLevel);
		long packed = pos.asLong();
		if (ChromaBlocks.isChroma(currentLevel.getBlockState(pos).getBlock())) {
			POSITIONS.add(packed);
		} else {
			POSITIONS.remove(packed);
		}
	}

	private static void removeChunk(LevelChunk chunk) {
		int chunkX = chunk.getPos().x();
		int chunkZ = chunk.getPos().z();
		POSITIONS.removeIf(packed -> (BlockPos.getX(packed) >> 4) == chunkX && (BlockPos.getZ(packed) >> 4) == chunkZ);
	}

	private static void ensureLevel(ClientLevel currentLevel) {
		if (level != currentLevel) {
			POSITIONS.clear();
			level = currentLevel;
		}
	}
}
