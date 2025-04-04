package com.argus.pressurized.util;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.entity.Entity.collideBoundingBox;

public class RotatedBB {
    private Vec3 center; // Center coordinates
    private Vec3 scale;  // Half-widths (extent) along x, y, and z axes
    private double rotationY; // Rotation around the y-axis in radians
    private double cos;
    private double sin;

    // Constructor
    public RotatedBB(Vec3 center, Vec3 scale, double rotationY) {
        this.center = center;
        this.scale = scale;
        this.rotationY = rotationY;
        this.cos = Math.cos(rotationY);
        this.sin = Math.sin(rotationY);
    }

    // Getters and setters
    public Vec3 getCenter() {
        return center;
    }

    public void setCenter(Vec3 center) {
        this.center = center;
    }

    public Vec3 getScale() {
        return scale;
    }

    public void setScale(Vec3 scale) {
        this.scale = scale;
    }

    public double getRotationY() {
        return rotationY;
    }

    public void setRotationY(double rotationY) {
        this.rotationY = rotationY;
        cos = Math.cos(rotationY);
        sin = Math.sin(rotationY);
    }

    static Vec3 collide(Vec3 p_20273_, Entity e) {
        AABB aabb = e.getBoundingBox();
        List<VoxelShape> list = e.level().getEntityCollisions(e, aabb.expandTowards(p_20273_));
        Vec3 vec3 = p_20273_.lengthSqr() == 0.0D ? p_20273_ : collideBoundingBox(e, p_20273_, aabb, e.level(), list);
        boolean flag = p_20273_.x != vec3.x;
        boolean flag1 = p_20273_.y != vec3.y;
        boolean flag2 = p_20273_.z != vec3.z;
        boolean flag3 = flag1 && p_20273_.y < 0.0D;
        if (e.getStepHeight() > 0.0F && flag3 && (flag || flag2)) {
            Vec3 vec31 = collideBoundingBox(e, new Vec3(p_20273_.x, (double) e.getStepHeight(), p_20273_.z), aabb,
                    e.level(), list);
            Vec3 vec32 = collideBoundingBox(e, new Vec3(0.0D, (double) e.getStepHeight(), 0.0D),
                    aabb.expandTowards(p_20273_.x, 0.0D, p_20273_.z), e.level(), list);
            if (vec32.y < (double) e.getStepHeight()) {
                Vec3 vec33 =
                        collideBoundingBox(e, new Vec3(p_20273_.x, 0.0D, p_20273_.z), aabb.move(vec32), e.level(), list)
                                .add(vec32);
                if (vec33.horizontalDistanceSqr() > vec31.horizontalDistanceSqr()) {
                    vec31 = vec33;
                }
            }

            if (vec31.horizontalDistanceSqr() > vec3.horizontalDistanceSqr()) {
                return vec31.add(collideBoundingBox(e, new Vec3(0.0D, -vec31.y + p_20273_.y, 0.0D), aabb.move(vec31),
                        e.level(), list));
            }
        }

        return vec3;
    }

    // Projection of corners along the motion vector
    private static double[] projectCorners(List<Vec3> corners, Vec3 motion) {
        double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
        for (Vec3 corner : corners) {
            double projection = corner.dot(motion);
            min = Math.min(min, projection);
            max = Math.max(max, projection);
        }
        return new double[]{min, max};
    }

    // Check if the two boxes are colliding using SAT
    public boolean checkCollision(RotatedBB other) {

        if (center.y - scale.y > other.center.y + other.scale.y || center.y + scale.y < other.center.y - other.scale.y) {
            return false;
        }

        // Get corners of both boxes
        List<Vec3> corners1 = List.of(getCorners());
        List<Vec3> corners2 = List.of(other.getCorners());

        // Combine axes for SAT (edges of both boxes)
        List<Vec3> axes = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            axes.add(getEdgeNormal(corners1.get(i), corners1.get((i + 1) % 4)));
            axes.add(getEdgeNormal(corners2.get(i), corners2.get((i + 1) % 4)));
        }

        // Check for overlap on all axes
        for (Vec3 axis : axes) {
            if (!overlapsOnAxis(axis, corners1, corners2)) {
                return false; // If there is a separating axis, no collision
            }
        }

        return true; // No separating axis found, so they are colliding
    }

    // Get the normal of the edge between two points
    private static Vec3 getEdgeNormal(Vec3 p1, Vec3 p2) {
        Vec3 edge = new Vec3(p2.x - p1.x, 0, p2.z - p1.z);
        return new Vec3(-edge.z, 0, edge.x); // Perpendicular in 2D
    }

    // Check if two boxes overlap along a given axis
    private static boolean overlapsOnAxis(Vec3 axis, List<Vec3> corners1, List<Vec3> corners2) {
        double[] range1 = projectCorners(corners1, axis);
        double[] range2 = projectCorners(corners2, axis);
        return range1[1] >= range2[0] && range2[1] >= range1[0];
    }

    // Rotation helper: Rotate a Vec3 by an angle around the Y-axis
    private Vec3 rotateY(Vec3 vec, double angle) {
        return new Vec3(
                cos * vec.x - sin * vec.z,
                vec.y,
                sin * vec.x + cos * vec.z
        );
    }

    // Generate rotated corners for 3D collision
    private Vec3[] getCorners() {
        Vec3[] localCorners = new Vec3[] {
                new Vec3(-scale.x, -scale.y, -scale.z),
                new Vec3(scale.x, -scale.y, -scale.z),
                new Vec3(scale.x, -scale.y, scale.z),
                new Vec3(-scale.x, -scale.y, scale.z),
                new Vec3(-scale.x, scale.y, -scale.z),
                new Vec3(scale.x, scale.y, -scale.z),
                new Vec3(scale.x, scale.y, scale.z),
                new Vec3(-scale.x, scale.y, scale.z),
        };
        Vec3[] worldCorners = new Vec3[8];
        for (int i = 0; i < 8; i++) {
            worldCorners[i] = center.add(rotateY(localCorners[i], rotationY));
        }
        return worldCorners;
    }

    private RotatedBB move(Vec3 moveVec) {
        return new RotatedBB(center.add(moveVec), scale, rotationY);
    }

    public static RotatedBB convertAABBtoRotatedBB(AABB aabb) {
        Vec3 scale = new Vec3(aabb.maxX - aabb.minX, aabb.maxY - aabb.minY, aabb.maxZ - aabb.minZ).scale(.5);
        Vec3 center = new Vec3(aabb.minX, aabb.minY, aabb.minZ).add(scale);
        return new RotatedBB(center, scale, 0);
    }

    public void particles(Entity e) {
        e.level().addParticle(new DustParticleOptions(Vec3.ZERO.toVector3f(), 1), center.x, center.y, center.z, 0, 0, 0);

        for (Vec3 vec : getCorners()) {
            e.level().addParticle(new DustParticleOptions(new Vector3f(1, 0, 0), 1), vec.x, vec.y, vec.z, 0, 0, 0);
        }
    }

    public String toString() {
        return "Center: " + center + " | Scale: " + scale + " | Rotation: " + rotationY;
    }
}