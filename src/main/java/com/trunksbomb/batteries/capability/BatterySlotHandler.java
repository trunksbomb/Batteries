package com.trunksbomb.batteries.capability;

import com.trunksbomb.batteries.item.BatteryItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class BatterySlotHandler extends SlotItemHandler {
  public BatterySlotHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
    super(itemHandler, index, xPosition, yPosition);
  }

  @Override
  public boolean isItemValid(@Nonnull ItemStack stack) {
    return !(stack.getItem() instanceof BatteryItem) && stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
  }
}
