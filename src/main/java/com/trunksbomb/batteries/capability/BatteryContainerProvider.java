package com.trunksbomb.batteries.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class BatteryContainerProvider implements INamedContainerProvider {
  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent("batteries.gui.name");
  }

  @Nullable
  @Override
  public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity player) {
    return new BatteryContainer(i, playerInventory, player);
  }
}
