package com.trunksbomb.batteries.item;

import com.trunksbomb.batteries.Config;
import com.trunksbomb.batteries.capability.BatteryCapabilityProvider;
import com.trunksbomb.batteries.capability.BatteryContainerProvider;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BatteryItem extends Item {

  public enum Tier {
    ZERO, ONE, TWO, THREE, CREATIVE
  };

  private Tier tier;

  public BatteryItem(Tier tier, Properties properties) {
    super(properties.maxStackSize(1).setNoRepair());
    this.tier = tier;
  }

  @Override
  public void inventoryTick(ItemStack battery, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    if (entityIn instanceof ServerPlayerEntity) {
      ServerPlayerEntity player = (ServerPlayerEntity) entityIn;
      CompoundNBT nbt = battery.getOrCreateTag();
      boolean shouldChargeSlot = false;
      for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
        if (nbt.getBoolean("chargeHotbar") && (i == 40 || i < 9)) //40 is the offhand slot, 0-9 is hotbar
          shouldChargeSlot = true;
        else if (nbt.getBoolean("chargeInventory") && i >= 9 && i < 36) //9-35 is the main inventory
          shouldChargeSlot = true;
        else if (nbt.getBoolean("chargeArmor") && i >= 36 && i < 40) //36-39 is the armor slots
          shouldChargeSlot = true;
        else
          shouldChargeSlot = false;
        if (shouldChargeSlot)
          provideEnergy(battery, player.inventory.getStackInSlot(i));
      }
    }

  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return isEnabled(stack);
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    World world = context.getWorld();
    BlockPos pos = context.getPos();
    ItemStack itemStack = context.getItem();
    if (world.getBlockState(pos).getBlock() == Blocks.DIAMOND_BLOCK) {
      itemStack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energy -> energy.receiveEnergy(Integer.MAX_VALUE, false));
      return ActionResultType.SUCCESS;
    }
    return super.onItemUse(context);
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
      else {
        NetworkHooks.openGui((ServerPlayerEntity) playerIn, new BatteryContainerProvider(), playerIn.getPosition());
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
        int energyNeeded = Math.min(energy.getMaxEnergyStored() - energy.getEnergyStored(), getMaxTransfer(battery));
        if (energyNeeded <= 0)
          return;
        boolean whitelist = battery.getOrCreateTag().getBoolean("whitelist");
        AtomicBoolean matchedWhitelist = new AtomicBoolean(false);
        AtomicBoolean matchedBlacklist = new AtomicBoolean(false);
        List<ItemStack> listItems = getStoredItems(battery);
        listItems.forEach(itemStack -> {
          boolean matched = receivingStack.getItem().getClass() == itemStack.getItem().getClass();
          if (whitelist && matched) {
            matchedWhitelist.set(true);
          }
          else if (!whitelist && matched)
            matchedBlacklist.set(true);
        });

        if (whitelist && !matchedWhitelist.get() && listItems.size() > 0)
          return; //If on a whitelist and the whitelist has at least one item, but we didn't match -- stop
        if (!whitelist && matchedBlacklist.get() && listItems.size() > 0)
          return; //If on a blacklist and the blacklist has at least one item, and we did match -- stop

        if (tier != Tier.CREATIVE) {
          if (!battery.getOrCreateTag().getBoolean("chargeFairly") || (battery.getOrCreateTag().getBoolean("chargeFairly") && energyStorage.getEnergyStored() > energy.getEnergyStored())) {
            int energyToTransfer = energyStorage.extractEnergy(energyNeeded, true);
            int accepted = energy.receiveEnergy(energyToTransfer, false);
            energyStorage.extractEnergy(accepted, false);
          }
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
    return new BatteryCapabilityProvider(stack, startingEnergy, energyCapacity, energyTransfer);
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

  private List<ItemStack> getStoredItems(ItemStack battery) {
    ArrayList<ItemStack> itemList = new ArrayList<>();
    battery.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
      for (int i = 0; i < h.getSlots(); i++) {
        ItemStack itemStack = h.getStackInSlot(i);
        if (!itemStack.isEmpty())
          itemList.add(itemStack);
      }
    });
    return itemList;
  }

}
