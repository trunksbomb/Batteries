package com.trunksbomb.batteries.item;

import com.trunksbomb.batteries.capability.BatteryCapabilityProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;
import java.util.List;

public class ExampleItem extends Item {
  public ExampleItem(Properties properties) {
    super(properties);
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
    return new BatteryCapabilityProvider(stack, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    stack.getCapability(CapabilityEnergy.ENERGY, null).ifPresent(energy -> {
      tooltip.add(new TranslationTextComponent("batteries.tooltip.battery.amount", String.format("%,d", energy.getEnergyStored()), String.format("%,d", energy.getMaxEnergyStored())).mergeStyle(TextFormatting.GREEN));
    });
  }
}
