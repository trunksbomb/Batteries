package com.trunksbomb.batteries.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;

public class BatteryItemStackHandler extends ItemStackHandler {

  private ItemStack battery;

  public BatteryItemStackHandler(ItemStack battery, int size) {
    super(size);
    this.battery = battery;
  }
}
