package com.argus.pressurized.entity;

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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

import java.util.HashMap;
import java.util.Map;

public class BlockCollectionEntity extends Entity {

    private static final EntityDataAccessor<CompoundTag> BLOCK_DATA = SynchedEntityData.defineId(BlockCollectionEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<Float> ROTATION_X = SynchedEntityData.defineId(BlockCollectionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ROTATION_Y = SynchedEntityData.defineId(BlockCollectionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ROTATION_Z = SynchedEntityData.defineId(BlockCollectionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(BlockCollectionEntity.class, EntityDataSerializers.FLOAT);

    private final Map<BlockPos, BlockState> blockMap = new HashMap<>();

    public BlockCollectionEntity(EntityType<?> entityType, Level world) {
        super(entityType, world);

        //Pressurized.LOGGER.info("<DEBUG> IDS: " + BLOCK_DATA.getId() + ", " + ROTATION_X.getId() + ", " + ROTATION_Y.getId() + ", " + ROTATION_Z.getId() + ", " + SCALE.getId());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BLOCK_DATA, new CompoundTag());
        this.entityData.define(ROTATION_X, 0.0f);
        this.entityData.define(ROTATION_Y, 0.0f);
        this.entityData.define(ROTATION_Z, 0.0f);
        this.entityData.define(SCALE, 1.0f);
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
        this.setRotationX(compound.getFloat("rotationX"));
        this.setRotationY(compound.getFloat("rotationY"));
        this.setRotationZ(compound.getFloat("rotationZ"));
        this.setScale(compound.getFloat("scale"));
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
        compound.putFloat("rotationX", this.getRotationX());
        compound.putFloat("rotationY", this.getRotationY());
        compound.putFloat("rotationZ", this.getRotationZ());
        compound.putFloat("scale", this.getScale());
    }

    public Map<BlockPos, BlockState> getBlockMap() {
        return blockMap;
    }

    public void addBlock(BlockPos relativePos, BlockState blockState) {
        blockMap.put(relativePos, blockState);
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

    public float getRotationX() {
        return this.entityData.get(ROTATION_X);
    }

    public void setRotationX(float rotationX) {
        this.entityData.set(ROTATION_X, rotationX);
    }

    public float getRotationY() {
        return this.entityData.get(ROTATION_Y);
    }

    public void setRotationY(float rotationY) {
        this.entityData.set(ROTATION_Y, rotationY);
    }

    public float getRotationZ() {
        return this.entityData.get(ROTATION_Z);
    }

    public void setRotationZ(float rotationZ) {
        this.entityData.set(ROTATION_Z, rotationZ);
    }

    public float getScale() {
        return this.entityData.get(SCALE);
    }

    public void setScale(float scale) {
        this.entityData.set(SCALE, scale);
    }
}
