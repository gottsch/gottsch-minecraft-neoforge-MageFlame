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
package mod.gottsch.neoforge.mageflame.core;

import mod.gottsch.neoforge.mageflame.core.config.Config;
import mod.gottsch.neoforge.mageflame.core.setup.CommonSetup;
import mod.gottsch.neoforge.mageflame.core.setup.Registration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Mark Gottschling on Nov 6, 2022
 *
 */
@Mod(value = MageFlame.MOD_ID)
public class MageFlame {
	// logger
	public static Logger LOGGER = LogManager.getLogger(MageFlame.MOD_ID);

	public static final String MOD_ID = "mageflame";

	/**
	 * 
	 */
	public MageFlame() {
		// TODO change to the new Echelons style of config setup
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
		
		// register the deferred registries
        Registration.init();
        
		// Register the setup method for modloading
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		eventBus.addListener(CommonSetup::common);
//		eventBus.addListener(this::config);
	}

//	private void config(final ModConfigEvent event) {
//		if (event.getConfig().getModId().equals(MOD_ID)) {
//			if (event.getConfig().getType() == ModConfig.Type.SERVER) {
//				IConfigSpec<?> spec = event.getConfig().getSpec();
//				// get the toml config data
//				CommentedConfig commentedConfig = event.getConfig().getConfigData();
//
//				 if (spec == Config.SERVER_CONFIG) {
//					 Integrations.registerTreasure2Integration();
//				}
//			}
//		}
//	}
}
