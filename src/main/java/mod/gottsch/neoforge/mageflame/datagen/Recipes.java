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
package mod.gottsch.neoforge.mageflame.datagen;

import mod.gottsch.neoforge.mageflame.core.setup.Registration;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;

/**
 * 
 * @author Mark Gottschling Jan 20, 2023
 *
 */
public class Recipes extends RecipeProvider {

		public Recipes(PackOutput generator) {
			super(generator);
		}

		@Override
		protected void buildRecipes(RecipeOutput recipe) {
			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Registration.MAGE_FLAME_SCROLL.get())
			.requires(Items.TORCH)
			.requires(Items.PAPER)
			.unlockedBy("has_torch", InventoryChangeTrigger.TriggerInstance.hasItems(
					Items.TORCH))
			.save(recipe);
			
			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Registration.LESSER_REVELATION_SCROLL.get())
			.requires(Items.TORCH)
			.requires(Items.FLINT_AND_STEEL)
			.requires(Items.PAPER)
			.unlockedBy("has_torch", InventoryChangeTrigger.TriggerInstance.hasItems(
					Items.TORCH))
			.save(recipe);
			
			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Registration.GREATER_REVELATION_SCROLL.get())
			.requires(Items.TORCH)
			.requires(Items.FLINT_AND_STEEL)
			.requires(Items.GLOWSTONE_DUST)
			.requires(Items.PAPER)
			.unlockedBy("has_torch", InventoryChangeTrigger.TriggerInstance.hasItems(
					Items.TORCH))
	        .save(recipe);
			
	        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.WINGED_TORCH_SCROLL.get())
	        .pattern(" e ")
	        .pattern("ftf")
	        .pattern("spb")
	        .define('e', Items.SPIDER_EYE)
	        .define('f', Items.FEATHER)
	        .define('t', Items.TORCH)
	        .define('s', Items.FLINT_AND_STEEL)
	        .define('p', Items.PAPER)
	        .define('b', Items.BLAZE_POWDER)
			.unlockedBy("has_torch", InventoryChangeTrigger.TriggerInstance.hasItems(
					Items.TORCH))
	        .save(recipe);	   
		}
}
