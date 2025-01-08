/*
 * This file is part of  Mage Flame.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
 *
 * Mage Flame is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Mage Flame is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mage Flame.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neoforge.mageflame.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.neoforge.common.util.BlockSnapshot;

import java.util.Collection;
import java.util.Map.Entry;

/**
 * 
 * @author Mark Gottschling Feb 6, 2023
 *
 */
public class LevelUtil {
	/**
	 * Convenience method
	 * @param level
	 * @param pos
	 * @param state
	 * @param flag
	 * @return
	 */
	public static boolean setBlockForced(Level level, BlockPos pos, BlockState state, int flag) {
		return setBlockForced(level, pos, state, flag, 512);  
	}

	/**
	 * 
	 * @param level
	 * @param pos
	 * @param state
	 * @param flag
	 * @param value512
	 * @return
	 */
	private static boolean setBlockForced(Level level, BlockPos pos, BlockState state, int flag, int value512) {
		if (level.isOutsideBuildHeight(pos)) {
			return false;
		} else if (!level.isClientSide && level.isDebug()) {
			return false;
		} else {
			LevelChunk levelChunk = level.getChunkAt(pos);

			pos = pos.immutable(); // Forge - prevent mutable BlockPos leaks
			BlockSnapshot blockSnapshot = null;
			if (level.captureBlockSnapshots && !level.isClientSide) {
				blockSnapshot = BlockSnapshot.create(level.dimension(), level, pos, flag);
				level.capturedBlockSnapshots.add(blockSnapshot);
			}

			BlockState oldState = level.getBlockState(pos);
			int oldLight = oldState.getLightEmission(level, pos);
			int oldOpacity = oldState.getLightBlock(level, pos);

			BlockState updatedChunkBlockState = setBlockStateForced(levelChunk, pos, state, (flag & 64) != 0);
			if (updatedChunkBlockState == null) {
				if (blockSnapshot != null) level.capturedBlockSnapshots.remove(blockSnapshot);
				return false;
			} else {
				BlockState currentLevelBlockstate = level.getBlockState(pos);
				if ((flag & 128) == 0 && currentLevelBlockstate != updatedChunkBlockState && (currentLevelBlockstate.getLightBlock(level, pos) != oldOpacity || currentLevelBlockstate.getLightEmission(level, pos) != oldLight || currentLevelBlockstate.useShapeForLightOcclusion() || updatedChunkBlockState.useShapeForLightOcclusion())) {
					level.getProfiler().push("queueCheckLight");
					level.getChunkSource().getLightEngine().checkBlock(pos);
					level.getProfiler().pop();
				}

				if (blockSnapshot == null) { // Don't notify clients or update physics while capturing blockstates
					level.markAndNotifyBlock(pos, levelChunk, updatedChunkBlockState, state, flag, value512);
				}

				return true;
			}
		}
	}

	/**
	 * 
	 * @param levelChunk
	 * @param pos
	 * @param state
	 * @param updateFlag
	 * @return
	 */
	private static BlockState setBlockStateForced(LevelChunk levelChunk, BlockPos pos, BlockState state, boolean updateFlag) {
		int yPos = pos.getY();
		LevelChunkSection levelChunkSection = levelChunk.getSection(levelChunk.getSectionIndex(yPos));
		boolean onlyAirFlag = levelChunkSection.hasOnlyAir();

		int x = pos.getX() & 15;
		int y = yPos & 15;
		int z = pos.getZ() & 15;
		BlockState updatedChunkBlockState = levelChunkSection.setBlockState(x, y, z, state);
		if (updatedChunkBlockState == state) {
			return null;
		} else {
			Block block = state.getBlock();
			updateHeightmaps(levelChunk.getHeightmaps(), Types.MOTION_BLOCKING, x, yPos, z, state);
			updateHeightmaps(levelChunk.getHeightmaps(), Types.MOTION_BLOCKING_NO_LEAVES, x, yPos, z, state);
			updateHeightmaps(levelChunk.getHeightmaps(), Types.OCEAN_FLOOR, x, yPos, z, state);
			updateHeightmaps(levelChunk.getHeightmaps(), Types.WORLD_SURFACE, x, yPos, z, state);
			boolean flag1 = levelChunkSection.hasOnlyAir();
			if (onlyAirFlag != flag1) {
				levelChunk.getLevel().getChunkSource().getLightEngine().updateSectionStatus(pos, flag1);
			}

			if (!levelChunkSection.getBlockState(x, y, z).is(block)) {
				return null;
			} else {
				if (!levelChunk.getLevel().isClientSide && !levelChunk.getLevel().captureBlockSnapshots) {
					state.onPlace(levelChunk.getLevel(), pos, updatedChunkBlockState, updateFlag);
				}

				levelChunk.setUnsaved(true);
				return updatedChunkBlockState;
			}
		}
	}

	/**
	 * 
	 * @param heightmaps
	 * @param heightType
	 * @param x
	 * @param y
	 * @param z
	 * @param state
	 */
	private static void updateHeightmaps(Collection<Entry<Types, Heightmap>> heightmaps, Types heightType, int x, int y,
			int z, BlockState state) {
		for(Entry<Types, Heightmap> entry : heightmaps) {
			if (entry.getKey() == heightType) {
				entry.getValue().update(x, y, z, state);
			}
		}		
	}
}
