package com.trunksbomb.batteries.network;

import com.trunksbomb.batteries.blocks.ChargerBlock;
import com.trunksbomb.batteries.blocks.ChargerTile;
import com.trunksbomb.batteries.item.BatteryItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class UuidPacket {

  private UUID linkedBatteryUuid;
  private UUID linkedPlayerUuid;
  private BlockPos pos;

  public UuidPacket() {}

  public UuidPacket(UUID linkedBatteryUuid, UUID linkedPlayerUuid, BlockPos pos) {
    this.linkedBatteryUuid = linkedBatteryUuid;
    this.linkedPlayerUuid = linkedPlayerUuid;
    this.pos = pos;
  }

  public static void handle(UuidPacket message, Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> {
      ServerPlayerEntity player = context.get().getSender();
      ItemStack itemStack = player != null ?
              player.getHeldItemMainhand().getItem() instanceof BatteryItem ?
                      player.getHeldItemMainhand() :
                      player.getHeldItemOffhand() :
              ItemStack.EMPTY;
      if (itemStack.getItem() instanceof BatteryItem)
        itemStack.getOrCreateTag().putUniqueId("uuid", message.linkedBatteryUuid);
      if (player != null && player.world != null) {
        World world = player.world;
        BlockState state = world.getBlockState(message.pos);
        TileEntity te = world.getTileEntity(message.pos);
        if (state.getBlock() instanceof ChargerBlock && te != null) {
          ((ChargerTile) te).linkedBatteryUuid = message.linkedBatteryUuid;
          ((ChargerTile) te).linkedPlayerUuid = message.linkedPlayerUuid;
        }
      }
    });
    message.done(context);
  }

  public static UuidPacket decode(PacketBuffer buffer) {
    UuidPacket packet = new UuidPacket();
    packet.linkedBatteryUuid = buffer.readUniqueId();
    packet.linkedPlayerUuid = buffer.readUniqueId();
    packet.pos = buffer.readBlockPos();
    return packet;
  }

  public static void encode(UuidPacket message, PacketBuffer buffer) {
    buffer.writeUniqueId(message.linkedBatteryUuid);
    buffer.writeUniqueId(message.linkedPlayerUuid);
    buffer.writeBlockPos(message.pos);
  }

  public void done(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().setPacketHandled(true);
  }
}
