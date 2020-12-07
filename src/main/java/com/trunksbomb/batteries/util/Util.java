package com.trunksbomb.batteries.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class Util {

  public static Set<BlockPos> getCuboidAroundPos(BlockPos pos, int radius, int height) {
    Set<BlockPos> shape = new HashSet<>();
    for (int x = pos.getX() - radius; x < pos.getX() + radius; x++) {
      for (int z = pos.getZ() - radius; z < pos.getZ() + radius; z++) {
        for (int y = pos.getY() - radius; y < pos.getY() + radius; y++) {
          shape.add(new BlockPos(x, y, z));
        }
      }
    }
    return shape;
  }

  public static void spawnParticlesFacingPlayer(World world, PlayerEntity player, BlockPos spawnPos, int count) {
    final double shootVelocity = 1.5F;
    count = Math.max(1, count);
    Vector3d centeredSpawnVec = Vector3d.copyCentered(new Vector3i(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()));
    Direction dir = getDirectionToPos(player.getPositionVec(), centeredSpawnVec).getOpposite();
    Vector3d offset = new Vector3d(dir.getDirectionVec().getX(), dir.getDirectionVec().getY(), dir.getDirectionVec().getZ());
    Vector3d finalSpawnVec = new Vector3d(centeredSpawnVec.getX(), centeredSpawnVec.getY(), centeredSpawnVec.getZ()).add(offset.mul(0.6d, 0.5d, 0.6d));
    Vector3d playerChestHeight = player.getPositionVec().add(0.0F, player.getHeight() * 0.75F, 0.0F);
    for (int i = 0; i < count; i++) {
      Vector3d randomOffset = new Vector3d(0.3F - Math.random() * 0.6F, 0.3F - Math.random() * 0.6F, 0.3F - Math.random() * 0.6F);
      Vector3d newFinalSpawnVec = finalSpawnVec.add(randomOffset.getX(), randomOffset.getY(), randomOffset.getZ());
      Vector3d shootVec = centeredSpawnVec.subtract(playerChestHeight).normalize();
      world.addParticle(RedstoneParticleData.REDSTONE_DUST, newFinalSpawnVec.getX(), newFinalSpawnVec.getY(), newFinalSpawnVec.getZ(), 0.1F, 0.1F, 0.1F);
      world.addParticle(ParticleTypes.CRIT, playerChestHeight.getX(), playerChestHeight.getY(), playerChestHeight.getZ(), shootVec.getX() * shootVelocity, shootVec.getY() * shootVelocity, shootVec.getZ() * shootVelocity);
    }
  }

  /**
   * Get facing horizontal Direction from vec1 to vec2.
   * For example, if vec1 is directly south of vec2, facing direction is NORTH.
   * Use Direction#getOpposite() to get the horizontal Direction from vec2 to vec1.
   * @param vec1 Vector3d from which to calculate Direction
   * @param vec2 Vector3d to which to calculate Direction
   * @return the Direction (NORTH, SOUTH, EAST, WEST) from vec1 to vec2
   */
  public static Direction getDirectionToPos(Vector3d vec1, Vector3d vec2) {
    double degrees = getHorizontalAngleToPos(vec1, vec2);
    if (45 <= degrees && degrees < 135)
      return Direction.WEST;
    if (135 <= degrees && degrees < 225)
      return Direction.SOUTH;
    if (225 <= degrees && degrees < 315)
      return Direction.EAST;
    return Direction.NORTH;
  }

  /**
   * get angle from vec1 and vec2, in degrees from 0-360
   * For reference:
   * WEST: 45-135
   * SOUTH: 135-225
   * EAST: 225-315
   * NORTH: 0-45, 315-360
   * @param vec1 Vector3d from which to calculate angle
   * @param vec2 Vector3d to which to calculate angle
   * @return the angle from vec1 to vec2, in degrees normalized to 0-360
   */
  public static double getHorizontalAngleToPos(Vector3d vec1, Vector3d vec2) {
    Vector3d sub = vec2.subtract(vec1);
    return 180 + Math.atan2(sub.getX(), sub.getZ()) / Math.PI * 180; //+180 normalizes from [-180 - 180] to [0 - 360]
  }
}
