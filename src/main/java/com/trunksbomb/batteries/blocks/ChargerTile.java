package com.trunksbomb.batteries.blocks;

import com.trunksbomb.batteries.BatteriesMod;
import com.trunksbomb.batteries.Config;
import com.trunksbomb.batteries.capability.BatteryEnergyStorage;
import com.trunksbomb.batteries.item.BatteryItem;
import com.trunksbomb.batteries.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
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
import java.util.UUID;

public class ChargerTile extends TileEntity implements ITickableTileEntity {

  public static final UUID DEFAULT_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
  private final LazyOptional<IItemHandler> inventory = LazyOptional.of(() -> new ItemStackHandler(1));
  public UUID linkedBatteryUuid;
  public UUID linkedPlayerUuid;

  public ChargerTile(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
    this.linkedBatteryUuid = DEFAULT_UUID;
    this.linkedPlayerUuid = DEFAULT_UUID;
  }

  public ChargerTile() {
    this(BatteriesMod.CHARGER_TILE.get());
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
      return inventory.cast();
    if (cap == CapabilityEnergy.ENERGY) {
      if (this.getBlockState().get(ChargerBlock.TYPE) != ChargerBlock.Type.ENDER && getInventory() != null)
        return getInventory().getStackInSlot(0).getCapability(cap);
      else if (world != null && this.getBlockState().get(ChargerBlock.TYPE) == ChargerBlock.Type.ENDER && !this.linkedBatteryUuid.equals(DEFAULT_UUID)) {
        PlayerEntity player = world.getPlayerByUuid(this.linkedPlayerUuid);
        if (player != null) {
          for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack.getOrCreateTag().hasUniqueId("uuid") && stack.getOrCreateTag().getUniqueId("uuid").equals(this.linkedBatteryUuid)) {
              return stack.getCapability(cap);
            }
          }
        }
      }
    }
    return super.getCapability(cap, side);
  }

  public boolean hasBattery() {
    return (this.getBlockState().get(ChargerBlock.TYPE) == ChargerBlock.Type.ENDER && this.linkedBatteryUuid != DEFAULT_UUID) ||
            (this.inventory.isPresent() && !this.inventory.resolve().get().getStackInSlot(0).isEmpty());
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
    if (tag.hasUniqueId("battery_uuid"))
      linkedBatteryUuid = tag.getUniqueId("battery_uuid");
    if (tag.hasUniqueId("player_uuid"))
      linkedPlayerUuid = tag.getUniqueId("player_uuid");
    super.read(bs, tag);
  }

  @Override
  public CompoundNBT write(CompoundNBT tag) {
    inventory.ifPresent(h -> {
      CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
      tag.put("inv", compound);
    });
    tag.putUniqueId("battery_uuid", linkedBatteryUuid);
    tag.putUniqueId("player_uuid", linkedPlayerUuid);
    return super.write(tag);
  }


}
