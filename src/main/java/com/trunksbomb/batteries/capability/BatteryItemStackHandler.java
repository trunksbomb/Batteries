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

  @Override
  protected void onContentsChanged(int slot) {
    super.onContentsChanged(slot);
    save();
  }

  public void save() {
    battery.getOrCreateTag().put("Inventory", serializeNBT());
  }

  public void load() {
    if (battery.getOrCreateTag().contains("Inventory")) {
      deserializeNBT(battery.getTag().getCompound("Inventory"));
      System.out.println("Loading");
    }
  }

/*  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    ListNBT items = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < items.size(); i++) {
      CompoundNBT item = items.getCompound(i);
      int slot = item.getInt("Slot");
      if (slot > 0 && slot < stacks.size()) {
        stacks.set(slot, ItemStack.read(item));
        System.out.println("Putting item in slot " + slot);
      }
    }
    onLoad();
  }*/
}
