package com.trunksbomb.batteries.blocks;

import com.trunksbomb.batteries.BatteriesMod;
import com.trunksbomb.batteries.Config;
import com.trunksbomb.batteries.capability.BatteryEnergyStorage;
import com.trunksbomb.batteries.item.BatteryItem;
import com.trunksbomb.batteries.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChargerTile extends TileEntity implements ITickableTileEntity {

  private final LazyOptional<IItemHandler> inventory = LazyOptional.of(() -> new ItemStackHandler(1));

  public ChargerTile(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
  }

  public ChargerTile() {
    this(BatteriesMod.CHARGER_TILE.get());
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
      return inventory.cast();
    if (cap == CapabilityEnergy.ENERGY && getInventory() != null)
      return getInventory().getStackInSlot(0).getCapability(cap);
    return super.getCapability(cap, side);
  }

  public boolean hasBattery() {
    return this.inventory.isPresent() && !this.inventory.resolve().get().getStackInSlot(0).isEmpty();
  }

  @Nullable
  public IItemHandler getInventory() {
    return this.inventory.orElse(null);
  }

  public ItemStack getBattery() {
    return getInventory() != null ? getInventory().getStackInSlot(0) : ItemStack.EMPTY;
  }
  public ItemStack insertBattery(ItemStack battery, boolean simulate) {
    return battery.getItem() instanceof BatteryItem && getInventory() != null ? getInventory().insertItem(0, battery, simulate) : battery;
  }

  public ItemStack extractBattery(boolean simulate) {
    return getInventory() != null ? getInventory().extractItem(0, 1, simulate) : ItemStack.EMPTY;
  }


  @Override
  public void tick() {
    if (this.world == null)
      return;

    ItemStack battery = getBattery();
    IEnergyStorage energy = battery.getCapability(CapabilityEnergy.ENERGY).orElse(null);
    boolean receivedEnergy = false;
    if (!battery.isEmpty() && battery.getItem() instanceof BatteryItem &&
          energy instanceof BatteryEnergyStorage) {
      BatteryItem.Tier tier = ((BatteryItem) battery.getItem()).getTier();
      if (Config.CREATIVE_CHARGERS.get()) {
        receivedEnergy = energy.receiveEnergy(BatteryItem.getMaxTransfer(battery), false) > 0;
      }
    }

    if (receivedEnergy && this.world.isRemote && Math.random() < 0.05F) {
      Util.spawnParticlesOnFace(world, pos, Direction.NORTH, 3);
      Util.spawnParticlesOnFace(world, pos, Direction.SOUTH, 3);
    }
  }

  @Override
  public void read(BlockState bs, CompoundNBT tag) {
    inventory.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(tag.getCompound("inv")));
    super.read(bs, tag);
  }

  @Override
  public CompoundNBT write(CompoundNBT tag) {
    inventory.ifPresent(h -> {
      CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
      tag.put("inv", compound);
    });
    return super.write(tag);
  }
}
