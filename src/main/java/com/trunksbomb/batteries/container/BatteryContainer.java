package com.trunksbomb.batteries.container;

import com.mojang.datafixers.util.Pair;
import com.trunksbomb.batteries.BatteriesMod;
import com.trunksbomb.batteries.gui.BatteryScreen;
import com.trunksbomb.batteries.inventory.BatterySlot;
import com.trunksbomb.batteries.item.BatteryItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;

public class BatteryContainer extends Container {

  public ItemStack battery;
  public int numSlots;

  private static final EquipmentSlotType[] VALID_EQUIPMENT_SLOTS = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};

  public BatteryContainer(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
    super(BatteriesMod.BATTERY_CONTAINER.get(), windowId);
    if (player.getHeldItemMainhand().getItem() instanceof BatteryItem)
      this.battery = player.getHeldItemMainhand();
    else
      this.battery = player.getHeldItemOffhand();

    battery.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
      this.numSlots = h.getSlots();
      for (int i = 0; i < this.numSlots; i++) {
        this.addSlot(new BatterySlot(h, i, BatteryScreen.INVENTORY_START_X + i * 18, BatteryScreen.INVENTORY_START_Y));
      }
    });
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        int x = BatteryScreen.INVENTORY_START_X + col * 18;
        int y = BatteryScreen.PLAYER_INVENTORY_START_Y + row * 18;
        int index = (col + row * 9) + 9;
        this.addSlot(new Slot(playerInventory, index, x, y));
      }
    }
    for (int col = 0; col < 9; col++) {
      int x = BatteryScreen.INVENTORY_START_X + col * 18;
      int y = BatteryScreen.PLAYER_INVENTORY_START_Y + 58;
      this.addSlot(new Slot(playerInventory, col, x, y));
    }

    for (int col = 39; col >= 36; col--) {
      final EquipmentSlotType equipmentslottype = VALID_EQUIPMENT_SLOTS[-col+39];
      int x = BatteryScreen.ARMOR_START_X;
      int y = BatteryScreen.ARMOR_START_Y + (-col + 39) * 18;
      this.addSlot(new Slot(playerInventory, col, x, y){
        public int getSlotStackLimit() {
          return 1;
        }
        public boolean isItemValid(ItemStack stack) {
          return stack.canEquip(equipmentslottype, player);
        }
        public boolean canTakeStack(PlayerEntity playerIn) {
          ItemStack itemstack = this.getStack();
          return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.canTakeStack(playerIn);
        }
      });
    }
    this.addSlot(new Slot(playerInventory, 40, BatteryScreen.ARMOR_START_X, 91));
  }

  @Override
  public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
    return false;
  }

  @Override
  public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
    ItemStack cursorItem = player.inventory.getItemStack();
    if (slotId >= 0 && slotId < 9) {
      if (inventorySlots.get(slotId).isItemValid(cursorItem)) {
        ItemStack ghostStack = cursorItem.copy();
        ghostStack.setCount(1);
        inventorySlots.get(slotId).putStack(ghostStack);
      }
      else {
        inventorySlots.get(slotId).putStack(ItemStack.EMPTY);
      }
      return ItemStack.EMPTY;
    }
    return super.slotClick(slotId, dragType, clickTypeIn, player);
  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return true;
  }
}
