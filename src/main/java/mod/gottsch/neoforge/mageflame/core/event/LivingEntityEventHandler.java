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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURCoordsE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mage Flame.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neoforge.mageflame.core.event;

import mod.gottsch.neoforge.mageflame.core.MageFlame;
import mod.gottsch.neoforge.mageflame.core.entity.creature.ISummonFlameEntity;
import mod.gottsch.neoforge.mageflame.core.entity.creature.SummonFlameBaseEntity;
import mod.gottsch.neoforge.mageflame.core.registry.SummonFlameRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

/**
 * 
 * @author Mark Gottschling on Nov 6, 2022
 *
 */
@EventBusSubscriber(modid = MageFlame.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class LivingEntityEventHandler {
//	@SubscribeEvent
//	public static void onEntityUpdate(LivingTickEvent event) {
//		if (event.getEntity().level().isClientSide) {
//			return;
//		}
//	}
	
	// TODO RESEARCH this could be better implemented in Entity.Load()
	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
		if (event.getEntity().level().isClientSide) {
			return;
		}
		if (event.getEntity() instanceof ISummonFlameEntity) {
			// MageFlame.LOGGER.debug("entity is joing the level -> {}", event.getEntity().getClass().getSimpleName());
			SummonFlameBaseEntity entity = (SummonFlameBaseEntity)event.getEntity();
			ServerLevel serverLevel = (ServerLevel)event.getEntity().level();
			
			// register the entity
			if (entity.getOwnerUUID() != null) {
				if(!SummonFlameRegistry.isRegistered(entity.getOwnerUUID())) {
					SummonFlameRegistry.register(entity.getOwnerUUID(), event.getEntity().getUUID());
				}
				/*
				 * NOTE this is an edge-case scenario where the entity was not unregistered and killed
				 *  and it attempts to reunite with owner.
				 *  TODO need to add additional info like gameTime to determine which is the younger (to keep).
				 */
				else if (!SummonFlameRegistry.get(entity.getOwnerUUID()).equals(event.getEntity().getUUID())) {
					// MageFlame.LOGGER.debug("event entity -> {} has a previously registered owner -> {} and not the existing entity", event.getEntity().getStringUUID(), entity.getOwnerUUID());
					/*
					 *  registered to another entity, check who the younger is
					 */
					Entity existingEntity = serverLevel.getEntity(SummonFlameRegistry.get(entity.getOwnerUUID()));
					if (existingEntity != null && existingEntity instanceof ISummonFlameEntity) {
						// MageFlame.LOGGER.debug("found existing entity -> {}", existingEntity.getStringUUID());
						// MageFlame.LOGGER.debug("existing birth -> {}, entity birth -> {}", ((ISummonFlameEntity)existingEntity).getBirthTime(), entity.getBirthTime());
						SummonFlameBaseEntity existingFlameEntity = (SummonFlameBaseEntity)existingEntity;
						// check if this entity is younger than the existing entity
						if (entity.getBirthTime() > ((ISummonFlameEntity)existingEntity).getBirthTime()) {
							// MageFlame.LOGGER.debug("killing existing -> {}", existingEntity.getStringUUID());
							// kill the existing registered entity
							existingFlameEntity.setOwner(null);
							SummonFlameRegistry.register(entity.getOwnerUUID(), event.getEntity().getUUID());
							// MageFlame.LOGGER.debug("registering entity -> {} to owner -> {}", event.getEntity().getUUID(), entity.getOwnerUUID());
						} else {
							// MageFlame.LOGGER.debug("killing myself -> {}", event.getEntity().getStringUUID());
							entity.setOwner(null);
						}
					}
				}
			}
			
			// update position and light blocks
			entity.updateLightBlocks();
		}
	}
}
