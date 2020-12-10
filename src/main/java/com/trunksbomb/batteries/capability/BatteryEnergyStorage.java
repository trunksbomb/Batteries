package com.trunksbomb.batteries.capability;

import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.EnergyStorage;

public class BatteryEnergyStorage extends EnergyStorage {

  private final ItemStack itemStack;

  public BatteryEnergyStorage(ItemStack itemStack, int energyCapacity, int energyTransfer) {
    super(energyCapacity, energyTransfer);
    this.itemStack = itemStack;
    this.energy = itemStack.getOrCreateTag().contains("energy") ? itemStack.getOrCreateTag().getInt("energy") : 0;
    itemStack.getOrCreateTag().putInt("transfer", energyTransfer);
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    int extracted = super.extractEnergy(maxExtract, simulate);
    itemStack.getOrCreateTag().putInt("energy", this.energy);
    return extracted;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    int received = super.receiveEnergy(maxReceive, simulate);
    itemStack.getOrCreateTag().putInt("energy", this.energy);
    return received;
  }
}
