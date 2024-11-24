package com.argus.pressurized.entity.trains;

import com.argus.pressurized.util.RotatedBB;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RotatedSolidEntity extends Entity {

    public RotatedSolidEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {

    }

    @Override
    public void tick() {
        super.tick();

        RotatedBB hitbox = new RotatedBB(this.position(), new Vec3(4.0, 1.0, 4.0), new Vec3(0, this.getYRot(), 0));
        List<Entity> entities = this.level().getEntities(this, new AABB(-5, -5, -5, 5, 5, 5)); // Broad phase

        for (Entity entity : entities) {
            if (entity instanceof Player) {
                Player player = (Player) entity;

                Vec3 localPos = transformToLocalSpace(player.position(), hitbox);
                Vec3 localMovement = transformToLocalSpace(player.getDeltaMovement(), hitbox);

                Vec3 adjustedMovement = resolveMovementWithStepUp(localPos, localMovement, hitbox.getDimensions(), 0.5);

                Vec3 globalMovement = transformToGlobalSpace(adjustedMovement, hitbox);

                player.setDeltaMovement(globalMovement);

                if (Math.abs(localPos.y - hitbox.getDimensions().y / 2) < 0.1 || adjustedMovement.y > 0) {
                    player.setOnGround(true);
                }
            }
        }
    }

    private Vec3 resolveMovementWithStepUp(Vec3 localPosition, Vec3 localMovement, Vec3 dimensions, double stepHeight) {
        double halfWidth = dimensions.x / 2;
        double halfHeight = dimensions.y / 2;
        double halfDepth = dimensions.z / 2;

        Vec3 adjustedMovement = localMovement;

        double dx = localPosition.x + localMovement.x;
        double dy = localPosition.y + localMovement.y;
        double dz = localPosition.z + localMovement.z;

        boolean collidesHorizontally = Math.abs(dx) < halfWidth && Math.abs(dz) < halfDepth;

        if (collidesHorizontally) {
            for (double step = 0.1; step <= stepHeight; step += 0.1) {
                double newY = localPosition.y + step;
                boolean collidesAtNewY = Math.abs(newY) < halfHeight;

                if (!collidesAtNewY && isValidStepUp(localPosition, newY, dimensions)) {
                    adjustedMovement = new Vec3(localMovement.x, step, localMovement.z);
                    break;
                }
            }

            if (adjustedMovement.y == 0) {
                adjustedMovement = new Vec3(0, localMovement.y, 0);
            }
        }

        return adjustedMovement;
    }

    private boolean isValidStepUp(Vec3 localPosition, double newY, Vec3 dimensions) { //expand ruleset
        double halfWidth = dimensions.x / 2;
        double halfDepth = dimensions.z / 2;

        double headHeight = newY + dimensions.y;

        return !(
                Math.abs(localPosition.x) < halfWidth &&
                        Math.abs(localPosition.z) < halfDepth &&
                        headHeight > dimensions.y
        );
    }

    private Vec3 transformToLocalSpace(Vec3 point, RotatedBB hitbox) {
        Vec3 relativePoint = point.subtract(hitbox.getCenter());

        Vec3 rotation = hitbox.getRotation();
        relativePoint = rotateY(relativePoint, -rotation.y); // Yaw
        relativePoint = rotateX(relativePoint, -rotation.x); // Pitch
        relativePoint = rotateZ(relativePoint, -rotation.z); // Roll

        return relativePoint;
    }

    private Vec3 rotateY(Vec3 point, double angle) {
        double cos = Math.cos(Math.toRadians(angle));
        double sin = Math.sin(Math.toRadians(angle));
        return new Vec3(
                point.x * cos - point.z * sin,
                point.y,
                point.x * sin + point.z * cos
        );
    }

    private Vec3 rotateX(Vec3 point, double angle) {
        double cos = Math.cos(Math.toRadians(angle));
        double sin = Math.sin(Math.toRadians(angle));
        return new Vec3(
                point.x,
                point.y * cos - point.z * sin,
                point.y * sin + point.z * cos
        );
    }

    private Vec3 rotateZ(Vec3 point, double angle) {
        double cos = Math.cos(Math.toRadians(angle));
        double sin = Math.sin(Math.toRadians(angle));
        return new Vec3(
                point.x * cos + point.y * sin,
                point.x * -sin + point.y * cos,
                point.z
        );
    }

    private Vec3 transformToGlobalSpace(Vec3 localVector, RotatedBB hitbox) {
        Vec3 rotation = hitbox.getRotation();
        localVector = rotateZ(localVector, rotation.z); // Roll
        localVector = rotateX(localVector, rotation.x); // Pitch
        localVector = rotateY(localVector, rotation.y); // Yaw
        return localVector;
    }
}
