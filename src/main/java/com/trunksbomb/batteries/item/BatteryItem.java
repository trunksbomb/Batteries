package com.trunksbomb.batteries.item;

import com.trunksbomb.batteries.Config;
import com.trunksbomb.batteries.capability.EnergyCapabilityProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.List;

public class BatteryItem extends Item {

  public enum Tier {
    ZERO, ONE, TWO, THREE, CREATIVE
  };

  private Tier tier;

  public BatteryItem(Tier tier, Properties properties) {
    super(properties);
    this.tier = tier;
  }

  @Override
  public void inventoryTick(ItemStack battery, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    if (entityIn instanceof ServerPlayerEntity) {
      ServerPlayerEntity player = (ServerPlayerEntity) entityIn;

      for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
        provideEnergy(battery, player.inventory.getStackInSlot(i));
      }
    }

  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return isEnabled(stack);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemStack = playerIn.getHeldItem(handIn);
    IEnergyStorage energyStorage = itemStack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
    boolean currentlyEnabled = isEnabled(itemStack);
    if (!worldIn.isRemote && energyStorage != null) {
      if (playerIn.isCrouching()) {
        //TODO: Create a block for charging batteries -- until then, enable the next line to give energy on shift+right click
        //itemStack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energy -> energy.receiveEnergy(getMaxTransfer(itemStack), false));
        itemStack.getOrCreateTag().putBoolean("enabled", !currentlyEnabled);
      }
    }
    return super.onItemRightClick(worldIn, playerIn, handIn);
  }

  private void provideEnergy(ItemStack battery, ItemStack receivingStack) {
    if (!isEnabled(battery))
      return;
    IEnergyStorage energyStorage = battery.getCapability(CapabilityEnergy.ENERGY).orElse(null);
    if (!(receivingStack.getItem() instanceof BatteryItem)) {
      receivingStack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energy -> {
        if (tier != Tier.CREATIVE) {
          int energyToTransfer = energyStorage.extractEnergy(getMaxTransfer(battery), false);
          energy.receiveEnergy(energyToTransfer, false);
        }
        else {
          energy.receiveEnergy(energy.getMaxEnergyStored() - energy.getEnergyStored(), false);
        }
      });
    }
  }

  @Override
  public boolean showDurabilityBar(ItemStack stack) {
    return true;
  }

  @Override
  public double getDurabilityForDisplay(ItemStack stack) {
    return stack.getCapability(CapabilityEnergy.ENERGY, null).map(energy -> 1D - energy.getEnergyStored() / (double) energy.getMaxEnergyStored()).orElse(0.5D);
  }

  @Nullable
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
    int energyCapacity = Integer.MAX_VALUE;
    int energyTransfer = Integer.MAX_VALUE;
    int startingEnergy = 0;
    if (stack.getItem() instanceof BatteryItem) {
      switch (((BatteryItem) stack.getItem()).tier) {
        case ZERO:
          energyCapacity = Config.BATTERY0_CAPACITY.get();
          energyTransfer = Config.BATTERY0_TRANSFER.get();
          break;
        case ONE:
          energyCapacity = Config.BATTERY1_CAPACITY.get();
          energyTransfer = Config.BATTERY1_TRANSFER.get();
          break;
        case TWO:
          energyCapacity = Config.BATTERY2_CAPACITY.get();
          energyTransfer = Config.BATTERY2_TRANSFER.get();
          break;
        case THREE:
          energyCapacity = Config.BATTERY3_CAPACITY.get();
          energyTransfer = Config.BATTERY3_TRANSFER.get();
          break;
        case CREATIVE:
          startingEnergy = Integer.MAX_VALUE;
          break;
      }
    }
    return new EnergyCapabilityProvider(stack, startingEnergy, energyCapacity, energyTransfer);
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    stack.getCapability(CapabilityEnergy.ENERGY, null).ifPresent(energy -> {
      tooltip.add(new TranslationTextComponent("batteries.tooltip.battery.amount", String.format("%,d", energy.getEnergyStored()), String.format("%,d", energy.getMaxEnergyStored())).mergeStyle(TextFormatting.GREEN));
    });
    tooltip.add(new TranslationTextComponent("batteries.tooltip.battery.enabled", (isEnabled(stack) ? "yes" : "no")));
  }

  private int getMaxTransfer(ItemStack stack) {
    return stack.getOrCreateTag().getInt("transfer");
  }

  private boolean isEnabled(ItemStack stack) {
    return stack.getOrCreateTag().getBoolean("enabled");
  }
}
