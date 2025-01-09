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
package mod.gottsch.neoforge.mageflame.core.entity.creature;

import mod.gottsch.neo.gottschcore.spatial.Coords;
import mod.gottsch.neo.gottschcore.spatial.ICoords;
import mod.gottsch.neoforge.mageflame.core.MageFlame;
import mod.gottsch.neoforge.mageflame.core.config.Config;
import mod.gottsch.neoforge.mageflame.core.util.LevelUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * 
 * @author Mark Gottschling Jan 21, 2023
 *
 */
public abstract class SummonFlameBaseEntity extends FlyingMob implements ISummonFlameEntity {
	private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(SummonFlameBaseEntity.class, EntityDataSerializers.OPTIONAL_UUID);
	public static final String OWNER = "owner";
	public static final String LAST_LIGHT_COORDS = "currentLightCoords";
	public static final String CURRENT_LIGHT_COORDS = "currentLightCoords";
	public static final String BIRTH_TIME = "birthTime";
	public static final String LIFESPAN = "lifespan";
	
	private ICoords currentLightCoords;
	private ICoords lastLightCoords;
	private long birthTime;
	private double lifespan;

	/**
	 * 
	 * @param entity
	 * @param level
	 * @param lifespan
	 */
	protected SummonFlameBaseEntity(EntityType<? extends FlyingMob> entity, Level level, double lifespan) {
		super(entity, level);
		this.birthTime = level.getGameTime();
		this.lifespan = lifespan;
		this.moveControl = new SummonFlameMoveControl(this);
	}
	
	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		// do not play a sound
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.CAMPFIRE_CRACKLE;
	}
	
	@Override
	public void doDeathEffects() {
		if (!level().isClientSide) {
			double d0 = this.getX();
			double d1 = this.getY() + 0.2;
			double d2 = this.getZ();
			((ServerLevel)level()).sendParticles(ParticleTypes.SMOKE, d0, d1, d2, 1, 0D, 0D, 0D, (double)0);
		}
	}

	@Override
	public void checkDespawn() {
		// does NOT despawn
	}

	@Override
	public boolean removeWhenFarAway(double distance) {
		return false;
	}

	/**
	 * 
	 * @param mob
	 * @param level
	 * @param spawnType
	 * @param pos
	 * @param random
	 * @return
	 */
	public static boolean checkSpawnRules(EntityType<? extends FlyingMob> mob, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
		return false;
	}

	/**
	 * 
	 */
	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new SummonFlameFollowOwnerGoal(this, 3F));
	}

	/**
	 * 
	 * @return
	 */
	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 0.5)
				.add(Attributes.MOVEMENT_SPEED, 0.3F);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.level().isClientSide) {
			if (updateLifespan() < 0) {
				die(((ServerLevel)this.level()).damageSources().generic());
			}
		}
	}
	
	protected double updateLifespan() {
		return --this.lifespan;
	}
	
	@Override
	public void aiStep() {
		super.aiStep();

		if (this.level().isClientSide) {
			if (this.level().getGameTime() % 10 == 0) {
				BlockState state = this.level().getBlockState(this.blockPosition());
				if (state.getFluidState().isEmpty() || canLiveInFluid()) {
					doLivingEffects();
				}
			}
		}
		else {
			// check for death scenarios ie no owner, if in water
			if (this.level().getGameTime() % 10 == 0) {
				BlockState state = this.level().getBlockState(this.blockPosition());
				if (this.getOwner() == null || (!state.getFluidState().isEmpty() && !canLiveInFluid())) {
					// kill self
					die();
					return;
				}
			}
			if (this.level().getGameTime() % Config.SERVER.updateLightTicks.get() == 0) {
				updateLightBlocks();
			}
		}
	}

	@Override
	public void updateLightBlocks() {
        if (this.dead) {
            return;
        }
		
		// initial setup
		if (getCurrentLightCoords() == null) {
			if (!updateLightCoords()) {
				die();
				return;
			}
			// set last = current as they are in the same place
			setLastLightCoords(getCurrentLightCoords());
			LevelUtil.setBlockForced(this.level(), getCurrentLightCoords().toPos(), getFlameBlock().defaultBlockState(), 3);
		} else {
			if (!blockPosition().equals(getCurrentLightCoords().toPos())) {
				// test location if fluids
				BlockState currentState = level().getBlockState(blockPosition());
				if (!currentState.getFluidState().isEmpty() && !canLiveInFluid()) {
					LevelUtil.setBlockForced(this.level(), getCurrentLightCoords().toPos(), Blocks.AIR.defaultBlockState(), 3);
				}
				else {
					if (!updateLightCoords()) {
						die();
						return;
					}

					// update block with flame
					LevelUtil.setBlockForced(this.level(), getCurrentLightCoords().toPos(), getFlameBlock().defaultBlockState(), 3);

					// delete old
					LevelUtil.setBlockForced(this.level(), getLastLightCoords().toPos(), Blocks.AIR.defaultBlockState(), 3);
				}
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public boolean updateLightCoords() {
		BlockPos pos = this.blockPosition();
		BlockPos newPos = pos;

		/*
		 *  want to short-circuit as quickly as possible here,
		 *  with the fewest object created
		 */
		// check in place
		if (testPlacement(pos)) {
		}
		// up
		else if (testPlacement(newPos = pos.above())) {
		}
		// check west
		else if (testPlacement(newPos = pos.west())) {
		}
		// check east
		else if (testPlacement(newPos = pos.east()))	{
		}		
		// check north
		else if (testPlacement(newPos = pos.north())) {
		}	
		// check south
		else if (testPlacement(newPos = pos.south())) {
		}	
		// check down
		else if (testPlacement(newPos = pos.below())) {
		}
		else {
			return false;
		}

		// update positions
		setLastLightCoords(getCurrentLightCoords());
		setCurrentLightCoords(new Coords(newPos));

		return true;
	}

	protected boolean testPlacement(BlockPos pos) {
		BlockState state = this.level().getBlockState(pos);
		// check block
		if (state.isAir()) {
			return true;
		}
		return false;
	}

	@Override
	public void die() {
		die(((ServerLevel)level()).damageSources().generic());
	}
	
	@Override
	public void die(DamageSource damageSource) {
		doDeathEffects();

		// hide the entity
		setInvisible(true);

		// set dead
		this.dead = true;
				
		// remove light blocks
		if (getCurrentLightCoords() != null && level().getBlockState(getCurrentLightCoords().toPos()).getBlock() == getFlameBlock()) {
			LevelUtil.setBlockForced(this.level(), getCurrentLightCoords().toPos(), Blocks.AIR.defaultBlockState(), 3);
		}
		if (getLastLightCoords() != null && level().getBlockState(getLastLightCoords().toPos()).getBlock() == getFlameBlock()) {
			LevelUtil.setBlockForced(this.level(), getLastLightCoords().toPos(), Blocks.AIR.defaultBlockState(), 3);
		}			
		remove(RemovalReason.KILLED);
		super.die(damageSource);
	}

	/**
	 * Set initial values of synced data
	 */
	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_OWNER_UUID, Optional.empty());
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);

		if (this.getOwnerUUID() != null) {
			tag.putUUID(OWNER, this.getOwnerUUID());
		}

		if (this.getCurrentLightCoords() != null) {
			CompoundTag coordsTag = new CompoundTag();
			tag.put(CURRENT_LIGHT_COORDS, getCurrentLightCoords().save(coordsTag));
		}
		if (this.getLastLightCoords() != null) {
			CompoundTag coordsTag = new CompoundTag();
			tag.put(LAST_LIGHT_COORDS, getLastLightCoords().save(coordsTag));
		}
		
		tag.putLong(BIRTH_TIME, getBirthTime());
		tag.putDouble(LIFESPAN, getLifespan());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);

		if (tag.hasUUID(OWNER)) {
			UUID uuid = tag.getUUID(OWNER);
			try {
				this.setOwnerUUID(uuid);
			} catch (Throwable throwable) {
				MageFlame.LOGGER.warn("Unable to set owner of flame ball to -> {}", uuid);
			}
		}
		if (tag.contains(CURRENT_LIGHT_COORDS)) {
			ICoords coords = Coords.EMPTY.load(tag.getCompound(CURRENT_LIGHT_COORDS));
			setCurrentLightCoords(coords);
		}
		if (tag.contains(LAST_LIGHT_COORDS)) {
			ICoords coords = Coords.EMPTY.load(tag.getCompound(LAST_LIGHT_COORDS));
			setLastLightCoords(coords);
		}
		if (tag.contains(BIRTH_TIME)) {
			setBirthTime(tag.getLong(BIRTH_TIME));
		}
		if (tag.contains(LIFESPAN)) {
			setLifespan(tag.getDouble(LIFESPAN));
		}
	}

	@Override
	@Nullable
	public LivingEntity getOwner() {
		try {
			UUID uuid = this.getOwnerUUID();
			return uuid == null ? null : this.level().getPlayerByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	@Override
	public void setOwner(LivingEntity entity) {
		if (entity == null) {
			setOwnerUUID(null);
		}
		else {
			setOwnerUUID(entity.getUUID());
		}
	}

	@Override
	@Nullable
	public UUID getOwnerUUID() {
		return this.entityData.get(DATA_OWNER_UUID).orElse((UUID)null);
	}

	private void setOwnerUUID(@Nullable UUID owner) {
		this.entityData.set(DATA_OWNER_UUID, Optional.ofNullable(owner));
	}

	@Override
	public ICoords getCurrentLightCoords() {
		return currentLightCoords;
	}

	@Override
	public void setCurrentLightCoords(ICoords currentLightCoords) {
		this.currentLightCoords = currentLightCoords;
	}

	@Override
	public ICoords getLastLightCoords() {
		return lastLightCoords;
	}

	@Override
	public void setLastLightCoords(ICoords lastLightCoords) {
		this.lastLightCoords = lastLightCoords;
	}

	/*
	 * 
	 */
	public static class SummonFlameFollowOwnerGoal extends Goal {
		private SummonFlameBaseEntity flameBall;
		// the distance away at which the flame ball starts to follow
		private float startDistance;
		private LivingEntity owner;

		/**
		 * 
		 * @param flameBall
		 * @param startDistance
		 */
		public SummonFlameFollowOwnerGoal(SummonFlameBaseEntity flameBall, float startDistance) {
			this.flameBall = flameBall;
			this.startDistance = startDistance;
		}

		@Override
		public boolean canUse() {
			if (this.flameBall.random.nextInt(reducedTickDelay(7)) != 0) {
				return false;
			}

			LivingEntity entity = this.flameBall.getOwner();
			if (entity == null) {
				return false;
			} else if (entity.isSpectator()) {
				return false;
			}

			this.owner = entity;
			MoveControl moveControl = this.flameBall.getMoveControl();
			double distance = 0;
			if (moveControl.hasWanted()) {
				double d0 = moveControl.getWantedX() - this.flameBall.getX();
				double d1 = moveControl.getWantedY() - this.flameBall.getY();
				double d2 = moveControl.getWantedZ() - this.flameBall.getZ();
				distance = d0 * d0 + d1 * d1 + d2 * d2;
			}
			boolean outsideProximity = this.flameBall.distanceToSqr(entity) > startDistance * startDistance;
			return (distance < 1.0D && outsideProximity) || distance > 3600.0D; // TODO need an additional check here if within radius of player
		}

		@Override
		public boolean canContinueToUse() {
			double d0 = this.flameBall.getMoveControl().getWantedX() - this.flameBall.getX();
			double d1 = this.flameBall.getMoveControl().getWantedY() - this.flameBall.getY();
			double d2 = this.flameBall.getMoveControl().getWantedZ() - this.flameBall.getZ();
			double d3 = d0 * d0 + d1 * d1 + d2 * d2;

			if (d3 >= 1D) {
				return true;
			}
			return false;
		}

		@Override
		public void start() {
			Vec3 initialPos = this.flameBall.selectSummonOffsetPos(this.owner);
			Vec3 wantedPos = this.flameBall.selectSpawnPos(this.flameBall.level(), new Vec3(initialPos.x, initialPos.y, initialPos.z), this.flameBall.getDirection());
			this.flameBall.getMoveControl().setWantedPosition(wantedPos.x, wantedPos.y, wantedPos.z, 1.0D);
		}

		@Override
		public void stop() {
			this.owner = null;
		}

		@Override
		public void tick() {
			if (this.flameBall.random.nextInt(reducedTickDelay(5)) == 0) {
				if (this.flameBall.distanceToSqr(this.owner) >= 36.0D) {
					// teleport to owner
					Vec3 offsetPos = this.flameBall.selectSummonOffsetPos(this.owner);
					Vec3 wantedPos = this.flameBall.selectSpawnPos(this.flameBall.level(), new Vec3(offsetPos.x, offsetPos.y, offsetPos.z), this.flameBall.getDirection());
					this.flameBall.getMoveControl().setWantedPosition(wantedPos.x, wantedPos.y, wantedPos.z, 1.0D);
					this.flameBall.moveTo(wantedPos.x, wantedPos.y, wantedPos.z, this.flameBall.getYRot(), this.flameBall.getXRot());
				}
			}
		}
	}

	/*
	 * 
	 */
	class SummonFlameMoveControl extends MoveControl {
		public SummonFlameMoveControl(SummonFlameBaseEntity entity) {
			super(entity);
		}

		public void tick() {
			if (this.operation == Operation.MOVE_TO) {
				Vec3 vec3 = new Vec3(this.wantedX - mob.getX(), this.wantedY - mob.getY(), this.wantedZ - mob.getZ());
				double d0 = vec3.length();
				if (d0 < mob.getBoundingBox().getSize()) {
					this.operation = Operation.WAIT;
					mob.setDeltaMovement(mob.getDeltaMovement().scale(0.5D));
				} else {
					mob.setDeltaMovement(mob.getDeltaMovement().add(vec3.scale(this.speedModifier * 0.05D / d0)));
					if (mob.getTarget() == null) {
						Vec3 vec31 = mob.getDeltaMovement();
						mob.setYRot(-((float)Mth.atan2(vec31.x, vec31.z)) * (180F / (float)Math.PI));
						mob.yBodyRot = mob.getYRot();
					} else {
						double d2 = mob.getTarget().getX() - mob.getX();
						double d1 = mob.getTarget().getZ() - mob.getZ();
						mob.setYRot(-((float)Mth.atan2(d2, d1)) * (180F / (float)Math.PI));
						mob.yBodyRot = mob.getYRot();
					}
				}

			}
		}
	}

	@Override
	public double getLifespan() {
		return lifespan;
	}

	@Override
	public void setLifespan(double lifespan) {
		this.lifespan = lifespan;
	}
	
	@Override
	public long getBirthTime() {
		return birthTime;
	}

	@Override
	public void setBirthTime(long birthTime) {
		this.birthTime = birthTime;
	}
}
