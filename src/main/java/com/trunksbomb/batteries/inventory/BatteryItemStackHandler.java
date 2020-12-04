package com.trunksbomb.batteries.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class BatteryItemStackHandler extends ItemStackHandler {

  private ItemStack battery;

  public BatteryItemStackHandler(ItemStack battery, int size) {
    super(size);
    this.battery = battery;
  }
}
