/*
 * This file is part of  Treasure2.
 * Copyright (c) 2022 Mark Gottschling (gottsch)
 *
 * Treasure2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Treasure2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Treasure2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neoforge.mageflame.datagen;

import mod.gottsch.neoforge.mageflame.core.MageFlame;
import mod.gottsch.neoforge.mageflame.core.setup.Registration;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 
 * @author Mark Gottschling on Sep 11, 2022
 *
 */
public class BlockStates extends BlockStateProvider {

	public BlockStates(PackOutput gen, ExistingFileHelper helper) {
        super(gen, MageFlame.MOD_ID, helper);
    }
	
	@Override
	protected void registerStatesAndModels() {
		simpleBlock(Registration.MAGE_FLAME_BLOCK.get());
		simpleBlock(Registration.LESSER_REVELATION_BLOCK.get());
		simpleBlock(Registration.GREATER_REVELATION_BLOCK.get());
	}

}
