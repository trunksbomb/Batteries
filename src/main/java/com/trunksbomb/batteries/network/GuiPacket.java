package com.trunksbomb.batteries.network;

import com.trunksbomb.batteries.item.BatteryItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class GuiPacket {

  private String nbtName;
  private boolean nbtValue;

  public GuiPacket() {}

  public GuiPacket(String nbtName, boolean nbtValue) {
    this.nbtName = nbtName;
    this.nbtValue = nbtValue;
  }

  public static void handle(GuiPacket message, Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> {
      ServerPlayerEntity player = context.get().getSender();
      ItemStack itemStack = player != null ?
              player.getHeldItemMainhand().getItem() instanceof BatteryItem ?
                      player.getHeldItemMainhand() :
                      player.getHeldItemOffhand() :
              ItemStack.EMPTY;
      if (itemStack.getItem() instanceof BatteryItem)
        itemStack.getOrCreateTag().putBoolean(message.nbtName, message.nbtValue);
    });
    message.done(context);
  }

  public static GuiPacket decode(PacketBuffer buffer) {
    GuiPacket packet = new GuiPacket();
    packet.nbtName = buffer.readString(32767);
    packet.nbtValue = buffer.readBoolean();
    return packet;
  }

  public static void encode(GuiPacket message, PacketBuffer buffer) {
    buffer.writeString(message.nbtName);
    buffer.writeBoolean(message.nbtValue);
  }

  public void done(Supplier<NetworkEvent.Context> ctx) {
    ctx.get().setPacketHandled(true);
  }
}
