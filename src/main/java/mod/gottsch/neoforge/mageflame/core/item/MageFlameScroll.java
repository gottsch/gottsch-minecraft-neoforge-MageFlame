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
package mod.gottsch.neoforge.mageflame.core.item;

import java.util.List;

import mod.gottsch.neoforge.mageflame.core.config.Config;
import mod.gottsch.neoforge.mageflame.core.setup.Registration;
import mod.gottsch.neoforge.mageflame.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * 
 * @author Mark Gottschling Jan 19, 2023
 *
 */
public class MageFlameScroll extends SummonFlameBaseItem {

	public MageFlameScroll(Properties properties) {
		super(properties);
	}
	
	public EntityType<? extends Mob> getSummonFlameEntity() {
		return Registration.MAGE_FLAME_ENTITY.get();
	}
	
	@Override
	public void appendBaseText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
		
		tooltip.add(Component.translatable(LangUtil.tooltip("mage_flame.desc")).withStyle(ChatFormatting.YELLOW));
		tooltip.add(Component.literal(LangUtil.NEWLINE));
		tooltip.add(Component.translatable(LangUtil.tooltip("light_level"), Registration.MAGE_FLAME_BLOCK.get().getLightEmission(Registration.MAGE_FLAME_BLOCK.get().defaultBlockState(), level, null)));					
		tooltip.add(Component.translatable(LangUtil.tooltip("lifespan"), ticksToTime(Config.SERVER.mageFlameLifespan.get())));
	}

	@Override
	public void appendAdvancedText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
		appendLore(stack, level, tooltip, flag, "mage_flame.lore");
	}
}
