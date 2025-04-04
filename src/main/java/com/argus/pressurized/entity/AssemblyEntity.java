package com.argus.pressurized.entity;

import com.argus.pressurized.Pressurized;
import com.argus.pressurized.util.RotatedBB;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssemblyEntity extends Entity {

    private static final EntityDataAccessor<CompoundTag> BLOCK_DATA = SynchedEntityData.defineId(AssemblyEntity.class, EntityDataSerializers.COMPOUND_TAG);

    private final Map<BlockPos, BlockState> blockMap;

    public AssemblyEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        blockMap = new HashMap<>();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            CompoundTag serializedData = new CompoundTag();
            ListTag blockList = new ListTag();
            for (Map.Entry<BlockPos, BlockState> entry : blockMap.entrySet()) {
                CompoundTag blockTag = new CompoundTag();
                blockTag.put("pos", NbtUtils.writeBlockPos(entry.getKey()));
                blockTag.put("state", NbtUtils.writeBlockState(entry.getValue()));
                blockList.add(blockTag);
            }
            serializedData.put("blocks", blockList);
            this.entityData.set(BLOCK_DATA, serializedData);
        }
            for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5))) { //check for all entities nearby
                Vec3 motion = livingEntity.getDeltaMovement();
                List<RotatedBB> colliders = new ArrayList<>();
                boolean unsafeMotion = false;
                List<Vec3> unsafeMotionAdjustments = new ArrayList<>(); //step height check history. index 0 is always the initial step height
                RotatedBB entityRotatedBB = RotatedBB.convertAABBtoRotatedBB(livingEntity.getBoundingBox());

                //for all blockstates in this assembly
                for (Map.Entry<BlockPos, BlockState> entry : blockMap.entrySet()) {
                    BlockPos relativePos = entry.getKey();
                    BlockState blockState = entry.getValue();

                    //get list of shapes and convert to rotatedBB
                    VoxelShape shape = blockState.getCollisionShape(this.level(), relativePos);
                    if (!shape.isEmpty()) {
                        for (AABB collider : shape.toAabbs()) {
                            Vec3 rotatedBBOffset = rotatePosition(relativePos, this.getYRot());

                            RotatedBB rotatedCollider = RotatedBB.convertAABBtoRotatedBB(collider.move(rotatedBBOffset).move(0, relativePos.getY(), 0).move(-.5, 0, -.5));
                            rotatedCollider.setRotationY(this.getYRot());

                            colliders.add(rotatedCollider);
                        }
                    }
                }

                //Pressurized.LOGGER.info("there are " + colliders.size() + " colliders");
                for (RotatedBB collider : colliders) {
                    collider.particles(this);
                    //Pressurized.LOGGER.info(collider.toString());

                    if (collider.checkCollision(entityRotatedBB)) {
                        Pressurized.LOGGER.info("COLLIDING");
                    }
                }

            /*
            //for all rotatedBB test collision to the entity
            for (RotatedBB collider : colliders) {
                Pressurized.LOGGER.info("cen/rot: " + Math.round(collider.getCenter().x * 100.0) / 100.0 + ", " + Math.round(collider.getCenter().y * 100.0) / 100.0 + ", " + Math.round(collider.getCenter().z * 100.0) / 100.0 + " | " + collider.getRotationY());
                Vec3 intersectDistance = collider.intersectDistance(entityRotatedBB, motion);
                Pressurized.LOGGER.info("pos/vel/int: " + Math.round(livingEntity.position().x * 100.0) / 100.0 + ", " + Math.round(livingEntity.position().y * 100.0) / 100.0 + ", " + Math.round(livingEntity.position().z * 100.0) / 100.0 + " | " + Math.round(livingEntity.getDeltaMovement().x * 100.0) / 100.0 + ", " + Math.round(livingEntity.getDeltaMovement().y * 100.0) / 100.0 + ", " + Math.round(livingEntity.getDeltaMovement().z * 100.0) / 100.0 + " | " + Math.round(intersectDistance.x * 100.0) / 100.0 + ", " + Math.round(intersectDistance.y * 100.0) / 100.0 + ", " + Math.round(intersectDistance.z * 100.0) / 100.0);

                //livingEntity.setDeltaMovement(intersectDistance);
                //motion = livingEntity.getDeltaMovement();
            }
             */

        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        ListTag blockList = compound.getList("blocks", 10); // 10 is the ID for compound tags
        blockMap.clear();
        HolderLookup<Block> blockLookup = this.level().holderLookup(Registries.BLOCK);
        for (int i = 0; i < blockList.size(); i++) {
            CompoundTag blockTag = blockList.getCompound(i);
            BlockPos pos = NbtUtils.readBlockPos(blockTag.getCompound("pos"));
            BlockState state = NbtUtils.readBlockState(blockLookup, blockTag.getCompound("state"));
            blockMap.put(pos, state);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        ListTag blockList = new ListTag();
        for (Map.Entry<BlockPos, BlockState> entry : blockMap.entrySet()) {
            CompoundTag blockTag = new CompoundTag();
            blockTag.put("pos", NbtUtils.writeBlockPos(entry.getKey()));
            blockTag.put("state", NbtUtils.writeBlockState(entry.getValue()));
            blockList.add(blockTag);
        }
        compound.put("blocks", blockList);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BLOCK_DATA, new CompoundTag());
    }

    private void deserializeBlockMap(CompoundTag data) {
        ListTag blockList = data.getList("blocks", 10); //10 is the id for compound tags
        blockMap.clear();

        HolderLookup<Block> blockLookup = this.level().holderLookup(Registries.BLOCK);

        for (int i = 0; i < blockList.size(); i++) {
            CompoundTag blockTag = blockList.getCompound(i);
            BlockPos pos = NbtUtils.readBlockPos(blockTag.getCompound("pos"));
            BlockState state = NbtUtils.readBlockState(blockLookup, blockTag.getCompound("state"));
            blockMap.put(pos, state);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (BLOCK_DATA.equals(key)) {
            deserializeBlockMap(this.entityData.get(BLOCK_DATA));
        }
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

    public void addBlockState(BlockPos pos, BlockState blockState) {
        blockMap.put(pos, blockState);
    }

    public Map<BlockPos, BlockState> getBlockMap() {
        return blockMap;
    }
}
