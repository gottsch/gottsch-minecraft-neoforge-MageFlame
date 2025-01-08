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
package mod.gottsch.neoforge.mageflame.core.setup;


import mod.gottsch.neoforge.mageflame.core.MageFlame;
import mod.gottsch.neoforge.mageflame.core.config.Config;
import mod.gottsch.neoforge.mageflame.core.entity.creature.GreaterRevelationEntity;
import mod.gottsch.neoforge.mageflame.core.entity.creature.LesserRevelationEntity;
import mod.gottsch.neoforge.mageflame.core.entity.creature.MageFlameEntity;
import mod.gottsch.neoforge.mageflame.core.entity.creature.WingedTorchEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;

/**
 * 
 * @author Mark Gottschling Jan 19, 2023
 *
 */
@Mod.EventBusSubscriber(modid = MageFlame.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetup {
	/**
	 * 
	 * @param event
	 */
	public static void common(final FMLCommonSetupEvent event) {
		// add mod specific logging
		Config.instance.addRollingFileAppender(MageFlame.MOD_ID);
//		MageFlameNetworking.register();

		// treasure2 integration (needs to be registered BEFORE LevelEvent.Load)
//		Integrations.registerTreasure2Integration();
	}
	
	/**
	 * attach defined attributes to the entity.
	 * @param event
	 */
	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		event.put(Registration.MAGE_FLAME_ENTITY.get(), MageFlameEntity.createAttributes().build());
		event.put(Registration.LESSER_REVELATION_ENTITY.get(), LesserRevelationEntity.createAttributes().build());
		event.put(Registration.GREATER_REVELATION_ENTITY.get(), GreaterRevelationEntity.createAttributes().build());
		event.put(Registration.WINGED_TORCH_ENTITY.get(), WingedTorchEntity.createAttributes().build());

	}
	
	@SubscribeEvent
	public static void registerEntitySpawnPlacements(SpawnPlacementRegisterEvent event) {

		event.register(Registration.MAGE_FLAME_ENTITY.get(),
				SpawnPlacements.Type.NO_RESTRICTIONS,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                MageFlameEntity::checkSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
		
		event.register(Registration.LESSER_REVELATION_ENTITY.get(),
				SpawnPlacements.Type.NO_RESTRICTIONS,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                LesserRevelationEntity::checkSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
		
		event.register(Registration.GREATER_REVELATION_ENTITY.get(),
				SpawnPlacements.Type.NO_RESTRICTIONS,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                GreaterRevelationEntity::checkSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
		
		event.register(Registration.WINGED_TORCH_ENTITY.get(),
				SpawnPlacements.Type.NO_RESTRICTIONS,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                WingedTorchEntity::checkSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
	}
	
	@SubscribeEvent
	public static void registemItemsToTab(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(Registration.MAGE_FLAME_SCROLL.get(), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.accept(Registration.LESSER_REVELATION_SCROLL.get(), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.accept(Registration.GREATER_REVELATION_SCROLL.get(), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.accept(Registration.WINGED_TORCH_SCROLL.get(), TabVisibility.PARENT_AND_SEARCH_TABS);

		}
	}
}
