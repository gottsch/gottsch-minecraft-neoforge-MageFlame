package mod.gottsch.neoforge.mageflame.core.setup;

import mod.gottsch.neoforge.mageflame.core.MageFlame;
import mod.gottsch.neoforge.mageflame.core.block.SummonFlameBlock;
import mod.gottsch.neoforge.mageflame.core.entity.creature.GreaterRevelationEntity;
import mod.gottsch.neoforge.mageflame.core.entity.creature.LesserRevelationEntity;
import mod.gottsch.neoforge.mageflame.core.entity.creature.MageFlameEntity;
import mod.gottsch.neoforge.mageflame.core.entity.creature.WingedTorchEntity;
import mod.gottsch.neoforge.mageflame.core.item.GreaterRevelationScroll;
import mod.gottsch.neoforge.mageflame.core.item.LesserRevelationScroll;
import mod.gottsch.neoforge.mageflame.core.item.MageFlameScroll;
import mod.gottsch.neoforge.mageflame.core.item.WingedTorchScroll;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 
 * @author Mark Gottschling on Nov 6, 2022
 *
 */
public class Registration {
	public static final String MAGE_FLAME = "mage_flame";
	public static final String LESSER_REVELATION = "lesser_revelation";
	public static final String GREATER_REVELATION = "greater_revelation"; 
	public static final String WINGED_TORCH = "winged_torch"; 
	
	/*
	 * deferred registries
	 */
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MageFlame.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MageFlame.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MageFlame.MOD_ID);
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, MageFlame.MOD_ID);

	// Blocks
	public static final DeferredHolder<Block, SummonFlameBlock> MAGE_FLAME_BLOCK = Registration.BLOCKS.register(MAGE_FLAME,
			() -> new SummonFlameBlock(Block.Properties.of().mapColor(MapColor.FIRE)
					.noCollission().lightLevel((state) -> {
						return 11;
					}).noLootTable()));
	public static final DeferredHolder<Block, SummonFlameBlock> LESSER_REVELATION_BLOCK = Registration.BLOCKS.register(LESSER_REVELATION,
			() -> new SummonFlameBlock(Block.Properties.of().mapColor(MapColor.FIRE)
					.noCollission().lightLevel((state) -> {
						return 13;
					}).noLootTable()));
	public static final DeferredHolder<Block, SummonFlameBlock> GREATER_REVELATION_BLOCK = Registration.BLOCKS.register(GREATER_REVELATION,
			() -> new SummonFlameBlock(Block.Properties.of().mapColor(MapColor.FIRE)
					.noCollission().lightLevel((state) -> {
						return 15;
					}).noLootTable()));

	// items
	public static final DeferredHolder<Item, MageFlameScroll> MAGE_FLAME_SCROLL = Registration.ITEMS.register("mage_flame_scroll", () -> new MageFlameScroll(new Item.Properties()));
	public static final DeferredHolder<Item, LesserRevelationScroll> LESSER_REVELATION_SCROLL = Registration.ITEMS.register("lesser_revelation_scroll", () -> new LesserRevelationScroll(new Item.Properties()));
	public static final DeferredHolder<Item, GreaterRevelationScroll> GREATER_REVELATION_SCROLL = Registration.ITEMS.register("greater_revelation_scroll", () -> new GreaterRevelationScroll(new Item.Properties()));
	public static final DeferredHolder<Item, WingedTorchScroll> WINGED_TORCH_SCROLL = Registration.ITEMS.register("winged_torch_scroll", () -> new WingedTorchScroll(new Item.Properties()));

	// entities
	public static final DeferredHolder<EntityType<?>, EntityType<MageFlameEntity>> MAGE_FLAME_ENTITY  = Registration.ENTITIES.register(MAGE_FLAME, () -> EntityType.Builder.of(MageFlameEntity::new, MobCategory.CREATURE)
			.sized(0.125F, 0.125F)
			.clientTrackingRange(8)
			.setTrackingRange(20)
			.setShouldReceiveVelocityUpdates(false)
			.build(MAGE_FLAME));
	
	public static final DeferredHolder<EntityType<?>, EntityType<LesserRevelationEntity>> LESSER_REVELATION_ENTITY  = Registration.ENTITIES.register(LESSER_REVELATION, () -> EntityType.Builder.of(LesserRevelationEntity::new, MobCategory.CREATURE)
			.sized(0.125F, 0.125F)
			.clientTrackingRange(8)
			.setTrackingRange(20)
			.setShouldReceiveVelocityUpdates(false)
			.build(LESSER_REVELATION));
	
	public static final DeferredHolder<EntityType<?>, EntityType<GreaterRevelationEntity>> GREATER_REVELATION_ENTITY  = Registration.ENTITIES.register(GREATER_REVELATION, () -> EntityType.Builder.of(GreaterRevelationEntity::new, MobCategory.CREATURE)
			.sized(0.1875F, 0.1875F)
			.clientTrackingRange(8)
			.setTrackingRange(20)
			.setShouldReceiveVelocityUpdates(false)
			.build(GREATER_REVELATION));
	
	public static final DeferredHolder<EntityType<?>, EntityType<WingedTorchEntity>> WINGED_TORCH_ENTITY  = Registration.ENTITIES.register(WINGED_TORCH, () -> EntityType.Builder.of(WingedTorchEntity::new, MobCategory.CREATURE)
			.sized(0.375F, 0.25F)
			.clientTrackingRange(8)
			.setTrackingRange(20)
			.setShouldReceiveVelocityUpdates(false)
			.build(WINGED_TORCH));

	// particles
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> REVELATION_PARTICLE = Registration.PARTICLES.register("revelation_particle", () -> new SimpleParticleType(true));
	
	
	/**
	 * 
	 */
	public static void init() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		BLOCKS.register(eventBus);
		ITEMS.register(eventBus);
		ENTITIES.register(eventBus);
		PARTICLES.register(eventBus);
	}
}
