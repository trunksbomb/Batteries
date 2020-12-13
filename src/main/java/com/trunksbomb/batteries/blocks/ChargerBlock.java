package com.trunksbomb.batteries.blocks;

import com.trunksbomb.batteries.item.BatteryItem;
import com.trunksbomb.batteries.network.PacketHandler;
import com.trunksbomb.batteries.network.UuidPacket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;


public class ChargerBlock extends Block {

  public static final EnumProperty<BatteryItem.Tier> TIER = EnumProperty.create("battery", BatteryItem.Tier.class);
  public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

  public enum Type implements IStringSerializable {
    NORMAL, ENDER;

    @Override
    public String getString() {
      if (this == Type.ENDER) {
        return "ender";
      }
      return "normal";
    }
  }

  public ChargerBlock(Properties properties, Type type) {
    super(properties.hardnessAndResistance(1.8F).harvestTool(ToolType.PICKAXE));
    this.setDefaultState(this.stateContainer.getBaseState().with(TIER, BatteryItem.Tier.NONE).with(TYPE, type));
  }

  @Override
  @Nonnull
  @SuppressWarnings("deprecation")
  public ActionResultType onBlockActivated(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult hit) {
    ItemStack heldItem = player.getHeldItemMainhand();
    TileEntity te = world.getTileEntity(pos);
    boolean succeeded = false;
    BatteryItem.Tier tier = BatteryItem.Tier.NONE;

    if (hand != Hand.MAIN_HAND ||
            !(te instanceof ChargerTile))
      return ActionResultType.PASS;
    ChargerTile charger = (ChargerTile) te;

    if (heldItem.getItem() instanceof BatteryItem && !charger.hasBattery()) {
      tier = ((BatteryItem) heldItem.getItem()).getTier();
      if (state.get(TYPE) == Type.NORMAL && tier != BatteryItem.Tier.ENDER) {
        heldItem = charger.insertBattery(heldItem, false);
        succeeded = true;
      }
      else if (state.get(TYPE) == Type.ENDER && tier == BatteryItem.Tier.ENDER) {
        if (!world.isRemote) {
          UUID uuid = heldItem.getOrCreateTag().hasUniqueId("uuid") ? heldItem.getOrCreateTag().getUniqueId("uuid") : UUID.randomUUID();
          heldItem.getOrCreateTag().putUniqueId("uuid", uuid);
          charger.linkedBatteryUuid = uuid;
          PacketHandler.sendToAllPlayers(world, new UuidPacket(uuid, player.getUniqueID(), pos));
        }
        charger.linkedPlayerUuid = player.getUniqueID();
        world.setBlockState(pos, state.with(TIER, BatteryItem.Tier.ENDER));
        succeeded = true;
      }
    }
    else if (heldItem.isEmpty() && charger.hasBattery()) {
      if (state.get(TYPE) == Type.NORMAL && state.get(TIER) != BatteryItem.Tier.ENDER) {
        heldItem = charger.extractBattery(false);
        succeeded = true;
      }
      else if (state.get(TYPE) == Type.ENDER && state.get(TIER) == BatteryItem.Tier.ENDER) {
        charger.linkedBatteryUuid = ChargerTile.DEFAULT_UUID;
        charger.linkedPlayerUuid = ChargerTile.DEFAULT_UUID;
        world.setBlockState(pos, state.with(TIER, BatteryItem.Tier.NONE));
        succeeded = true;
      }


    }

    if (succeeded) {
      player.setHeldItem(Hand.MAIN_HAND, heldItem);
      world.setBlockState(pos, state.with(TIER, tier));
      return ActionResultType.SUCCESS;
    }

    return super.onBlockActivated(state, world, pos, player, hand, hit);
  }

  @Override
  @SuppressWarnings("deprecation")
  public void onReplaced(BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
    if (state.getBlock() != newState.getBlock()) {
      TileEntity te = worldIn.getTileEntity(pos);
      IItemHandler items = te != null ? te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null) : null;
      if (items != null) {
        for (int i = 0; i < items.getSlots(); i++) {
          ItemStack stack = items.getStackInSlot(i);
          if (!stack.isEmpty()) {
            InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
          }
        }
      }
    }

    super.onReplaced(state, worldIn, pos, newState, isMoving);
  }

  @Override
  public void onBlockPlacedBy(World worldIn, @Nonnull BlockPos pos, BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
    Direction facing = placer != null ? placer.getHorizontalFacing() : Direction.NORTH;
    worldIn.setBlockState(pos, state.with(BlockStateProperties.HORIZONTAL_FACING, facing).with(TYPE, this.getDefaultState().get(TYPE)));
    super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(BlockStateProperties.HORIZONTAL_FACING).add(TIER).add(TYPE);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new ChargerTile();
  }
}
