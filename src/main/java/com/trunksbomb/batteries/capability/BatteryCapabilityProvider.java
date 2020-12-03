package com.trunksbomb.batteries.capability;

import com.trunksbomb.batteries.item.ExampleItem;
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

public class BatteryCapabilityProvider implements ICapabilitySerializable<CompoundNBT> {

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
    else if (!(itemStack.getItem() instanceof ExampleItem) && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
      return itemCapability.cast();
    return LazyOptional.empty();
  }

  @Override
  public CompoundNBT serializeNBT() {
    if (itemCapability.isPresent()) {
      return itemCapability.resolve().get().serializeNBT();
    }
    return new CompoundNBT();
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    itemCapability.ifPresent(h -> {
      h.deserializeNBT(nbt);
    });
  }
}
