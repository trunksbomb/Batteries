package com.trunksbomb.batteries.inventory;

import com.trunksbomb.batteries.item.BatteryItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BatterySlot extends SlotItemHandler {
  public BatterySlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
    super(itemHandler, index, xPosition, yPosition);
  }

  @Override
  public boolean isItemValid(ItemStack stack) {

    return !(stack.getItem() instanceof BatteryItem) && stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
  }

  @Override
  public boolean canTakeStack(PlayerEntity playerIn) {
    return true;
  }

  @Override
  public void putStack(ItemStack stack) {
    super.putStack(stack.copy());
    stack.grow(1);//hack for JEI when they auto-fill recipes, re fill it after it drains so its a 'mock slot'
  }
}
