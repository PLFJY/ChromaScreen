package com.chromascreen.block;

import com.chromascreen.ChromaScreen;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;

public final class ChromaBlocks {
	public static final int GREEN = 0x00FF00;
	public static final int BLUE = 0x0000FF;
	public static final int RED = 0xFF0000;

	public static final ChromaScreenBlock GREEN_SCREEN = register("green_screen", GREEN);
	public static final ChromaScreenBlock BLUE_SCREEN = register("blue_screen", BLUE);
	public static final ChromaScreenBlock RED_SCREEN = register("red_screen", RED);

	private ChromaBlocks() {
	}

	private static ChromaScreenBlock register(String name, int rgb) {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, ChromaScreen.id(name));
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, ChromaScreen.id(name));
		ChromaScreenBlock block = new ChromaScreenBlock(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).setId(blockKey), rgb);
		Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
		Registry.register(BuiltInRegistries.ITEM, itemKey, new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(itemKey)));
		return block;
	}

	public static boolean isChroma(Block block) {
		return block instanceof ChromaScreenBlock;
	}

	public static void initialize() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COLORED_BLOCKS).register(entries -> {
			entries.accept(GREEN_SCREEN);
			entries.accept(BLUE_SCREEN);
			entries.accept(RED_SCREEN);
		});
	}
}
