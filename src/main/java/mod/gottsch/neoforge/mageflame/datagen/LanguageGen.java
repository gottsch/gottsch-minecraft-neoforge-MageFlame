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

import mod.gottsch.neoforge.mageflame.core.MageFlame;
import mod.gottsch.neoforge.mageflame.core.setup.Registration;
import mod.gottsch.neoforge.mageflame.core.util.LangUtil;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

/**
 * 
 * @author Mark Gottschling on Apr 6, 2022
 *
 */
public class LanguageGen extends LanguageProvider {

    public LanguageGen(PackOutput gen, String locale) {
        super(gen, MageFlame.MOD_ID, locale);
    }
    
    @Override
    protected void addTranslations() {
        // scrolls
        add(Registration.MAGE_FLAME_SCROLL.get(), "Scroll of Mage Flame");
        add(Registration.LESSER_REVELATION_SCROLL.get(), "Scroll of Lesser Revelation");
        add(Registration.GREATER_REVELATION_SCROLL.get(), "Scroll of Greater Revelation");
        add(Registration.WINGED_TORCH_SCROLL.get(), "Summon Winged Torch Scroll");
                
        // entities
        add(Registration.MAGE_FLAME_ENTITY.get(), "Mage Flame");
        add(Registration.LESSER_REVELATION_ENTITY.get(), "Lesser Revelation");
        add(Registration.GREATER_REVELATION_ENTITY.get(), "Greater Revelation");
        add(Registration.WINGED_TORCH_ENTITY.get(), "Winged Torch");
   
        /*
         *  Util.tooltips
         */
        // general
        add(LangUtil.tooltip("hold_shift"), "Hold [SHIFT] to expand");
        add(LangUtil.tooltip("light_level"), "Light Level: %s");
        add(LangUtil.tooltip("lifespan"), "Lifespan: %s");

        add(LangUtil.tooltip("mage_flame.desc"), "Allows the spellcaster to create a small ball of flames.");
        add(LangUtil.tooltip("mage_flame.lore"), 
        		"The weakest of the summoned flames,~"
        		+ "well-suited for the apprentice spellcaster.~"
        		+ "It will allow you to see, but not as bright~"
        		+ "as a regular torch. It is not strong enough~"
        		+ "to navigate through vines and other~\n"
        		+ "replaceable blocks, and will extinguish if~"
        		+ "it is unable to follow its owner.");
        
        add(LangUtil.tooltip("lesser_revelation.desc"), "A more powerful version of Mage Flame.");
        add(LangUtil.tooltip("lesser_revelation.lore"), 
        		"A ball of magic-green fire. It has a more~" +
        		"powerful light and increased lifespan.");
        		
        add(LangUtil.tooltip("greater_revelation.desc"), "The most powerful of the magical flames.");
        add(LangUtil.tooltip("greater_revelation.lore"), 
        		"The spellcaster is able to channel a great~"
        		+ "amount of power to generate a large ball~"
        		+ "of magic-green fire. Brighter than a torch~"
        		+ "and has staying power. It also has enough~"
        		+ "power to destroy replaceable blocks.");
        
        add(LangUtil.tooltip("winged_torch.desc"), "Allows the spellcaster to summon a winged torch.");
        add(LangUtil.tooltip("winged_torch.lore"), 
        		"The spellcaster is able reach into the nether~"
        		+ "plane and summon a winged torch. The torch~"
        		+ "will remain under your charge until you~"
        		+ "release it or it perishes.");

    }
}
