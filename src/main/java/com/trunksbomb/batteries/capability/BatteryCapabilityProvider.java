package com.trunksbomb.batteries.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BatteryCapabilityProvider implements ICapabilitySerializable {

  private ItemStack itemStack;
  private int energyCapacity;
  private int energyTransfer;
  private final LazyOptional<IEnergyStorage> energyCapability = LazyOptional.of(() -> new BatteryEnergyStorage(itemStack, energyCapacity, energyTransfer));
  private final LazyOptional<BatteryItemStackHandler> itemCapability = LazyOptional.of(() -> new BatteryItemStackHandler(itemStack, 9));

  public BatteryCapabilityProvider(ItemStack itemStack, int startingEnergy, int energyCapacity, int energyTransfer) {
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
    else if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
      return itemCapability.cast();
    return LazyOptional.empty();
  }

  @Override
  public INBT serializeNBT() {
    if (itemCapability.isPresent()) {
      itemCapability.ifPresent(BatteryItemStackHandler::save);
      return itemCapability.resolve().get().serializeNBT();
    }
    return new CompoundNBT();
  }

  @Override
  public void deserializeNBT(INBT nbt) {
    if (itemCapability.isPresent()) {
      System.out.println("Deserializing now");
      System.out.println(nbt.toString());
      itemCapability.ifPresent(BatteryItemStackHandler::load);
    }
  }
}
