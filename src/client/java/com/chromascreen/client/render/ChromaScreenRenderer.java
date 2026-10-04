package com.chromascreen.client.render;

import com.chromascreen.block.ChromaScreenBlock;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Optional;
import java.util.OptionalDouble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;

/** Draws RGB faces into the completed world target, after normal world rendering. */
public final class ChromaScreenRenderer {
	private ChromaScreenRenderer() {
	}

	public static void render(CameraRenderState camera, GpuBufferSlice projection) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		if (level == null || projection == null) {
			return;
		}
		LongOpenHashSet positions = ChromaRenderCache.positions(level);
		if (positions.isEmpty()) {
			return;
		}

		// Integer origin keeps vertex coordinates precise far from world spawn.
		int originX = (int) Math.floor(camera.pos.x);
		int originY = (int) Math.floor(camera.pos.y);
		int originZ = (int) Math.floor(camera.pos.z);
		Matrix4f modelView = new Matrix4f(camera.viewRotationMatrix)
			.translate((float) (originX - camera.pos.x), (float) (originY - camera.pos.y), (float) (originZ - camera.pos.z));

		try (ByteBufferBuilder bytes = new ByteBufferBuilder(4096)) {
			BufferBuilder vertices = new BufferBuilder(bytes, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_COLOR);
			for (long packed : positions) {
				BlockPos pos = BlockPos.of(packed);
				if (!(level.getBlockState(pos).getBlock() instanceof ChromaScreenBlock block)) {
					continue;
				}
				float x = pos.getX() - originX;
				float y = pos.getY() - originY;
				float z = pos.getZ() - originZ;
				int rgb = block.rgb();
				for (Direction direction : Direction.values()) {
					if (!positions.contains(pos.relative(direction).asLong())) {
						emitFace(vertices, x, y, z, direction, rgb);
					}
				}
			}

			try (MeshData mesh = vertices.build()) {
				if (mesh == null) {
					return;
				}
				int indexCount = mesh.drawState().indexCount();
				RenderSystem.AutoStorageIndexBuffer indices = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);
				try (GpuBuffer vertexBuffer = RenderSystem.getDevice().createBuffer(() -> "Chroma screen vertices", GpuBuffer.USAGE_VERTEX, mesh.vertexBuffer())) {
					RenderTarget target = minecraft.gameRenderer.mainRenderTarget();
					try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
						() -> "Chroma screen late pass", target.getColorTextureView(), Optional.empty(), target.getDepthTextureView(), OptionalDouble.empty())) {
						RenderSystem.bindDefaultUniforms(pass);
						pass.setUniform("Projection", projection);
						pass.setUniform("DynamicTransforms", RenderSystem.getDynamicUniforms().writeTransform(modelView));
						pass.setPipeline(RenderSystem.getCompiledPipeline(ChromaRenderPipeline.PIPELINE));
						pass.setVertexBuffer(0, vertexBuffer.slice(0, vertexBuffer.size()));
						pass.setIndexBuffer(indices.getBuffer(indexCount), indices.type());
						pass.drawIndexed(indexCount, 1, 0, 0, 0);
					}
				}
			}
		}
	}

	private static void emitFace(BufferBuilder out, float x, float y, float z, Direction face, int rgb) {
		int r = (rgb >>> 16) & 255;
		int g = (rgb >>> 8) & 255;
		int b = rgb & 255;
		switch (face) {
			case DOWN -> quad(out, r, g, b, x, y, z, x + 1, y, z, x + 1, y, z + 1, x, y, z + 1);
			case UP -> quad(out, r, g, b, x, y + 1, z, x, y + 1, z + 1, x + 1, y + 1, z + 1, x + 1, y + 1, z);
			case NORTH -> quad(out, r, g, b, x, y, z, x, y + 1, z, x + 1, y + 1, z, x + 1, y, z);
			case SOUTH -> quad(out, r, g, b, x + 1, y, z + 1, x + 1, y + 1, z + 1, x, y + 1, z + 1, x, y, z + 1);
			case WEST -> quad(out, r, g, b, x, y, z + 1, x, y + 1, z + 1, x, y + 1, z, x, y, z);
			case EAST -> quad(out, r, g, b, x + 1, y, z, x + 1, y + 1, z, x + 1, y + 1, z + 1, x + 1, y, z + 1);
		}
	}

	private static void quad(BufferBuilder out, int r, int g, int b,
		float x0, float y0, float z0, float x1, float y1, float z1,
		float x2, float y2, float z2, float x3, float y3, float z3) {
		out.addVertex(x0, y0, z0).setColor(r, g, b, 255);
		out.addVertex(x1, y1, z1).setColor(r, g, b, 255);
		out.addVertex(x2, y2, z2).setColor(r, g, b, 255);
		out.addVertex(x3, y3, z3).setColor(r, g, b, 255);
	}
}
