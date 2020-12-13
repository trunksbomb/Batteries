package com.trunksbomb.batteries.network;

import com.trunksbomb.batteries.BatteriesMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
  private static final String PROTOCOL_VERSION = "1";
  public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
    new ResourceLocation(BatteriesMod.MODID, "main"),
          () -> PROTOCOL_VERSION,
          PROTOCOL_VERSION::equals,
          PROTOCOL_VERSION::equals
  );

  public static void setup() {
    int id = 0;
    INSTANCE.registerMessage(id++, GuiPacket.class,
            GuiPacket::encode,
            GuiPacket::decode,
            GuiPacket::handle);
    INSTANCE.registerMessage(id++, UuidPacket.class,
            UuidPacket::encode,
            UuidPacket::decode,
            UuidPacket::handle);
  }

  public static <MSG> void sendToAllPlayers(World world, MSG message) {
    for (PlayerEntity player : world.getPlayers()) {
      INSTANCE.sendTo(message, ((ServerPlayerEntity) player).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }
  }
}
