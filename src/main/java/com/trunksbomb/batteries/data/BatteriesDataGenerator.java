package com.trunksbomb.batteries.data;

import com.trunksbomb.batteries.BatteriesMod;
import com.trunksbomb.batteries.blocks.ChargerBlock;
import com.trunksbomb.batteries.item.BatteryItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BatteriesDataGenerator {

  @SubscribeEvent
  public static void gatherData(GatherDataEvent event) {

    DataGenerator gen = event.getGenerator();

    if (event.includeClient()) {
      gen.addProvider(new BlockStateGenerator(gen, BatteriesMod.MODID, event.getExistingFileHelper()));
    }
  }

  public static class BlockStateGenerator extends BlockStateProvider {

    public BlockStateGenerator(DataGenerator gen, String modId, ExistingFileHelper exFileHelper) {
      super(gen, modId, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
      VariantBlockStateBuilder builder = getVariantBuilder(BatteriesMod.CHARGER_BLOCK.get());

      builder.forAllStates(state -> {
        ChargerBlock.Type type = state.get(ChargerBlock.TYPE);
        BatteryItem.Tier tier = state.get(ChargerBlock.TIER);
        Direction facing = state.get(BlockStateProperties.HORIZONTAL_FACING);

        String location = String.format("block/charger/charger_%s", tier.getString());
        ModelFile.UncheckedModelFile model = new ModelFile.UncheckedModelFile(new ResourceLocation(BatteriesMod.MODID, location));
        return ConfiguredModel.builder()
                .modelFile(model)
                .rotationY(getY(facing))
                .rotationX(0)
                .build();
      });

      builder = getVariantBuilder(BatteriesMod.ENDER_CHARGER_BLOCK.get());
      builder.forAllStates(state -> {
        String location = "block/charger/ender_charger";
        BatteryItem.Tier tier = state.get(ChargerBlock.TIER);
        Direction facing = state.get(BlockStateProperties.HORIZONTAL_FACING);

        if (tier != BatteryItem.Tier.NONE)
          location += "_full";
        ModelFile.UncheckedModelFile model = new ModelFile.UncheckedModelFile(new ResourceLocation(BatteriesMod.MODID, location));
        return ConfiguredModel.builder()
                .modelFile(model)
                .rotationY(getY(facing))
                .rotationX(0)
                .build();
      });
    }

    private int getY(Direction facing) {
      switch (facing) {
        case EAST:
          return 90;
        case SOUTH:
          return 180;
        case WEST:
          return 270;
      }
      return 0;
    }
  }


}
