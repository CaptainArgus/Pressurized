package com.argus.pressurized.entity;

import com.argus.pressurized.Pressurized;
import com.argus.pressurized.util.RotatedBB;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3d;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollisionTestEntity extends Entity {

    private static final EntityDataAccessor<Float> ROTATION_Y = SynchedEntityData.defineId(CollisionTestEntity.class, EntityDataSerializers.FLOAT);

    private float yRotation; // Y-axis rotation of the entity in degrees

    public CollisionTestEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public void tick() {
        super.tick();

        for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
            if (entity.isAlive()) {
                Vec3 entityMotion = entity.getDeltaMovement();
                entity.setDeltaMovement(entityMotion.multiply(1, 0, 1));
                entity.resetFallDistance();
                entity.setOnGround(true);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.yRotation = compound.getFloat("rotationY");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putFloat("rotationY", this.yRotation);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ROTATION_Y, 0.0f);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    /**
     * Rotates the position around the entity's center by the specified yaw (yRotation).
     *
     * @param relativePos The relative position of the block.
     * @param yRotation   The rotation angle in degrees (around the Y-axis).
     * @return The rotated position of the block.
     */
    private Vec3 rotatePosition(BlockPos relativePos, float yRotation) {
        float radians = (float) Math.toRadians(yRotation);

        Vec3 relativeVec = new Vec3(relativePos.getX(), 0, relativePos.getZ());

        //Vec3 translatedVec = relativeVec.subtract(this.getX(), 0, this.getZ());

        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);

        double newX = relativeVec.x * cos - relativeVec.z * sin;
        double newZ = relativeVec.x * sin + relativeVec.z * cos;

        return new Vec3(newX + this.getX(), this.getY(), newZ + this.getZ());
    }

    public float getyRotation() {
        return yRotation;
    }

    public void setyRotation(float yRotation) {
        this.yRotation = yRotation;
    }
}

