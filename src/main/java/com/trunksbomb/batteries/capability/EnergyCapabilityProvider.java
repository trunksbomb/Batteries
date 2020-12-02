package com.trunksbomb.batteries.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnergyCapabilityProvider implements ICapabilityProvider {

  private ItemStack itemStack;
  private int energyCapacity;
  private int energyTransfer;
  private final LazyOptional<IEnergyStorage> energyCapability = LazyOptional.of(() -> new BatteryEnergyStorage(itemStack, energyCapacity, energyTransfer));

  public EnergyCapabilityProvider(ItemStack itemStack, int startingEnergy, int energyCapacity, int energyTransfer) {
    this.itemStack = itemStack;
    this.energyCapacity = energyCapacity;
    this.energyTransfer = energyTransfer;
    if (startingEnergy > 0)
      this.energyCapability.ifPresent(energy -> energy.receiveEnergy(startingEnergy, false));
  }
  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    if (cap == CapabilityEnergy.ENERGY)
      return energyCapability.cast();
    return LazyOptional.empty();
  }
}
