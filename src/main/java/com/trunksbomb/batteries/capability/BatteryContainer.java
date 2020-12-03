package com.trunksbomb.batteries.capability;

import com.trunksbomb.batteries.BatteriesMod;
import com.trunksbomb.batteries.item.BatteryItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class BatteryContainer extends Container {

  public ItemStack battery;

  public ItemStackHandler handler;

  public int numSlots;

  public BatteryContainer(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
    super(BatteriesMod.BATTERY_CONTAINER.get(), windowId);
    if (player.getHeldItemMainhand().getItem() instanceof BatteryItem)
      this.battery = player.getHeldItemMainhand();
    else
      this.battery = player.getHeldItemOffhand();

    battery.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
      this.numSlots = h.getSlots();
      for (int i = 0; i < this.numSlots; i++) {
        this.addSlot(new BatterySlotHandler(h, i, BatteryScreen.INVENTORY_START_X + i * 18, BatteryScreen.INVENTORY_START_Y));
      }
    });
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        int x = BatteryScreen.INVENTORY_START_X + col * 18;
        int y = BatteryScreen.PLAYER_INVENTORY_START_Y + row * 18;
        int index = (col + row * 9) + 9;
        this.addSlot(new Slot(playerInventory, index, x+1, y+1));
      }
    }
    for (int col = 0; col < 9; col++) {
      int x = BatteryScreen.INVENTORY_START_X + col * 18;
      int y = BatteryScreen.PLAYER_INVENTORY_START_Y + 58;
      this.addSlot(new Slot(playerInventory, col, x+1, y+1));
    }
  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return true;
  }
}
