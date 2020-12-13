package com.trunksbomb.batteries;

import com.trunksbomb.batteries.blocks.ChargerBlock;
import com.trunksbomb.batteries.blocks.ChargerTile;
import com.trunksbomb.batteries.container.BatteryContainer;
import com.trunksbomb.batteries.gui.BatteryScreen;
import com.trunksbomb.batteries.item.BatteryItem;
import com.trunksbomb.batteries.item.ExampleItem;
import com.trunksbomb.batteries.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BatteriesMod.MODID)
public class BatteriesMod
{
    public static final String MODID = "batteries";

    //Registries
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);
    //Item Group (creative tab)
    public static ItemGroup itemGroup = new ItemGroup(BatteriesMod.MODID) {

        @Override
        public ItemStack createIcon() {
            return new ItemStack(BATTERY.get());
        }
    };

    //Items
    public static final RegistryObject<Item> BATTERY = ITEMS.register("battery", () -> new BatteryItem(BatteryItem.Tier.ZERO, new Item.Properties().group(itemGroup)));
    public static final RegistryObject<Item> BATTERY1 = ITEMS.register("battery1", () -> new BatteryItem(BatteryItem.Tier.ONE, new Item.Properties().group(itemGroup)));
    public static final RegistryObject<Item> BATTERY2 = ITEMS.register("battery2", () -> new BatteryItem(BatteryItem.Tier.TWO, new Item.Properties().group(itemGroup)));
    public static final RegistryObject<Item> BATTERY3 = ITEMS.register("battery3", () -> new BatteryItem(BatteryItem.Tier.THREE, new Item.Properties().group(itemGroup)));
    public static final RegistryObject<Item> BATTERY_ENDER = ITEMS.register("battery_ender", () -> new BatteryItem(BatteryItem.Tier.ENDER, new Item.Properties().group(itemGroup)));
    public static final RegistryObject<Item> BATTERY_CREATIVE = ITEMS.register("battery_creative", () -> new BatteryItem(BatteryItem.Tier.CREATIVE, new Item.Properties().group(itemGroup)));
    public static final RegistryObject<Item> EXAMPLE = ITEMS.register("example", () -> new ExampleItem(new Item.Properties().group(itemGroup)));

    //Blocks
    public static final RegistryObject<Block> CHARGER_BLOCK = BLOCKS.register("charger", () -> new ChargerBlock(Block.Properties.create(Material.ANVIL, MaterialColor.GRAY), ChargerBlock.Type.NORMAL));
    public static final RegistryObject<Block> ENDER_CHARGER_BLOCK = BLOCKS.register("ender_charger", () -> new ChargerBlock(Block.Properties.create(Material.ANVIL, MaterialColor.GRAY), ChargerBlock.Type.ENDER));

    //BlockItems
    public static final RegistryObject<BlockItem> CHARGER_BLOCK_ITEM = ITEMS.register("charger", () -> new BlockItem(CHARGER_BLOCK.get(), new Item.Properties().group(itemGroup)));
    public static final RegistryObject<BlockItem> ENDER_CHARGER_BLOCK_ITEM = ITEMS.register("ender_charger", () -> new BlockItem(ENDER_CHARGER_BLOCK.get(), new Item.Properties().group(itemGroup)));

    //TileEntities
    public static final RegistryObject<TileEntityType<ChargerTile>> CHARGER_TILE = TILES.register("charger", () -> TileEntityType.Builder.create(ChargerTile::new, CHARGER_BLOCK.get(), ENDER_CHARGER_BLOCK.get()).build(null));

    //Container
    public static final RegistryObject<ContainerType<BatteryContainer>> BATTERY_CONTAINER = CONTAINERS.register("battery_container", () -> IForgeContainerType.create(((windowId, inv, data) -> new BatteryContainer(windowId, inv, inv.player))));
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public BatteriesMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        //Register Config
        Config.setup(FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        //Register everything
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        ScreenManager.registerFactory(BATTERY_CONTAINER.get(), BatteryScreen::new);

        //Network Registry
        PacketHandler.setup();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("batteries", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }

        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {

        }
    }
}
