package com.trunksbomb.batteries;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class Config {
  private static final ForgeConfigSpec.Builder CONFIG = new ForgeConfigSpec.Builder();
  private static ForgeConfigSpec COMMON_CONFIG;

  public static ForgeConfigSpec.ConfigValue<Integer> BATTERY0_CAPACITY;
  public static ForgeConfigSpec.ConfigValue<Integer> BATTERY1_CAPACITY;
  public static ForgeConfigSpec.ConfigValue<Integer> BATTERY2_CAPACITY;
  public static ForgeConfigSpec.ConfigValue<Integer> BATTERY3_CAPACITY;

  public static ForgeConfigSpec.ConfigValue<Integer> BATTERY0_TRANSFER;
  public static ForgeConfigSpec.ConfigValue<Integer> BATTERY1_TRANSFER;
  public static ForgeConfigSpec.ConfigValue<Integer> BATTERY2_TRANSFER;
  public static ForgeConfigSpec.ConfigValue<Integer> BATTERY3_TRANSFER;

  public static ForgeConfigSpec.ConfigValue<Boolean> FAIR_CHARGING;
  public static ForgeConfigSpec.ConfigValue<Boolean> TEST_CHARGING;
  static {
    initConfig();
  }

  private static void initConfig() {
    CONFIG.push(BatteriesMod.MODID);
    BATTERY0_CAPACITY = CONFIG.comment("Tier 0 Battery Storage Capacity").define("battery0_capacity", 100000);
    BATTERY1_CAPACITY = CONFIG.comment("Tier 1 Battery Storage Capacity").define("battery1_capacity", 750000);
    BATTERY2_CAPACITY = CONFIG.comment("Tier 2 Battery Storage Capacity").define("battery2_capacity", 2000000);
    BATTERY3_CAPACITY = CONFIG.comment("Tier 3 Battery Storage Capacity").define("battery3_capacity", 10000000);
    BATTERY0_TRANSFER = CONFIG.comment("Tier 0 Battery Transfer Rate").define("battery0_transfer", 100);
    BATTERY1_TRANSFER = CONFIG.comment("Tier 1 Battery Transfer Rate").define("battery1_transfer", 300);
    BATTERY2_TRANSFER = CONFIG.comment("Tier 2 Battery Transfer Rate").define("battery2_transfer", 1000);
    BATTERY3_TRANSFER = CONFIG.comment("Tier 3 Battery Transfer Rate").define("battery3_transfer", 5000);
    FAIR_CHARGING = CONFIG.comment("Enable \"Fair Charging\", meaning a battery will only charge an item if the item has less FE stored than the battery").define("fair_charging", true);
    TEST_CHARGING = CONFIG.comment("For debugging, enable right-click on diamond block to charge a held battery").define("test_charging", false);
    CONFIG.pop();
    COMMON_CONFIG = CONFIG.build();
  }

  public static void setup(Path path) {
    final CommentedFileConfig configData = CommentedFileConfig.builder(path)
            .sync()
            .autosave()
            .writingMode(WritingMode.REPLACE)
            .build();
    configData.load();
    COMMON_CONFIG.setConfig(configData);
  }
}
