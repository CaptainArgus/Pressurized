package com.argus.pressurized.util;

import net.minecraft.world.phys.Vec3;

public class RotatedBB {
    private Vec3 center; // Center of the hitbox
    private Vec3 dimensions; // Width, height, depth
    private Vec3 rotation; // Rotation in degrees (yaw, pitch, roll)

    public RotatedBB(Vec3 center, Vec3 dimensions, Vec3 rotation) {
        this.center = center;
        this.dimensions = dimensions;
        this.rotation = rotation;
    }

    public void setCenter(Vec3 center) {
        this.center = center;
    }

    public void setDimensions(Vec3 dimensions) {
        this.dimensions = dimensions;
    }

    public void setRotation(Vec3 rotation) {
        this.rotation = rotation;
    }

    public Vec3 getCenter() {
        return center;
    }

    public Vec3 getDimensions() {
        return dimensions;
    }

    public Vec3 getRotation() {
        return rotation;
    }

    public void rotate(Vec3 rotation) {
        this.rotation.add(rotation);
    }
}
