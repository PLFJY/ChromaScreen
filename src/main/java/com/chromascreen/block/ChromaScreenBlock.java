package com.chromascreen.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/** A normal solid block whose world model is supplied by the late client pass. */
public final class ChromaScreenBlock extends Block {
	private final int rgb;

	public ChromaScreenBlock(BlockBehaviour.Properties properties, int rgb) {
		super(properties);
		this.rgb = rgb;
	}

	public int rgb() {
		return rgb;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}
}
