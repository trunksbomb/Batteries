package com.trunksbomb.batteries.blocks;

import com.trunksbomb.batteries.item.BatteryItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;


public class ChargerBlock extends Block {

  public static final EnumProperty<BatteryItem.Tier> TIER = EnumProperty.create("battery", BatteryItem.Tier.class);

  @Override
  @SuppressWarnings("deprecation")
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
    ItemStack heldItem = player.getHeldItemMainhand();
    TileEntity te = world.getTileEntity(pos);
    boolean isRemote = world.isRemote;
    boolean succeeded = false;
    BatteryItem.Tier tier = BatteryItem.Tier.NONE;

    if (hand != Hand.MAIN_HAND ||
            !(te instanceof ChargerTile))
      return ActionResultType.PASS;
    ChargerTile charger = (ChargerTile) te;

    if (heldItem.getItem() instanceof BatteryItem && !charger.hasBattery()) {
      tier = ((BatteryItem) heldItem.getItem()).getTier();
      heldItem = charger.insertBattery(heldItem, false);
      succeeded = true;
    }
    else if (heldItem.isEmpty() && charger.hasBattery()) {
      heldItem = charger.extractBattery(false);
      succeeded = true;

    }

    if (succeeded) {
      player.setHeldItem(Hand.MAIN_HAND, heldItem);
      world.setBlockState(pos, state.with(TIER, tier));
      return ActionResultType.SUCCESS;
    }

    return super.onBlockActivated(state, world, pos, player, hand, hit);
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    Direction facing = placer != null ? placer.getHorizontalFacing() : Direction.NORTH;
    worldIn.setBlockState(pos, state.with(BlockStateProperties.HORIZONTAL_FACING, facing));
    super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
  }

  public ChargerBlock(Properties properties) {
    super(properties.hardnessAndResistance(1.8F).harvestTool(ToolType.PICKAXE));
    this.setDefaultState(this.stateContainer.getBaseState().with(TIER, BatteryItem.Tier.NONE));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(BlockStateProperties.HORIZONTAL_FACING).add(TIER);
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
