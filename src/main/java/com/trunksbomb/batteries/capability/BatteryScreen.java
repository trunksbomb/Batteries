package com.trunksbomb.batteries.capability;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.trunksbomb.batteries.BatteriesMod;
import com.trunksbomb.batteries.network.GuiPacket;
import com.trunksbomb.batteries.network.GuiPacketHandler;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class BatteryScreen extends ContainerScreen<BatteryContainer> {
  public static final int WIDTH = 176;
  public static final int HEIGHT = 147;
  public static final int BUTTON_WIDTH = 20;
  public static final int BUTTON_HEIGHT = 18;
  public static final int BUTTON_OFFSET_X = 177;
  public static final int BUTTON_OFFSET_Y = 0;
  public static final int BUTTON_GAP = 1;
  public static final int BUTTON_SCREEN_GAP = 7;
  public static final int INVENTORY_START_X = 8;
  public static final int INVENTORY_START_Y = 19;
  public static final int PLAYER_INVENTORY_START_Y = 65;

  private Button whitelist, blacklist, hotbar, armor, inventory, fair;
  private static final ResourceLocation TEXTURE = new ResourceLocation(BatteriesMod.MODID, "textures/battery_gui.png");

  public BatteryScreen(BatteryContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
    xSize = WIDTH;
    ySize = HEIGHT;
  }

  @Override
  protected void init() {
    super.init();
    CompoundNBT nbt = this.container.battery.getOrCreateTag();
    whitelist = new Button(guiLeft + 16, guiTop + 41, 0, nbt.getBoolean("whitelist"), new TranslationTextComponent("batteries.gui.button.whitelist"), (p) -> {
      blacklist.visible = true;
      whitelist.visible = false;
      nbt.putBoolean("whitelist", false);
    });
    blacklist = new Button(guiLeft + 16, guiTop + 41, 1, !nbt.getBoolean("whitelist"), new TranslationTextComponent("batteries.gui.button.blacklist"), (p) -> {
      blacklist.visible = false;
      whitelist.visible = true;
      nbt.putBoolean("whitelist", true);
    });
    hotbar = new Button(guiLeft + 16 + (BUTTON_WIDTH + BUTTON_SCREEN_GAP), guiTop + 41, 2, true, new TranslationTextComponent("batteries.gui.button.hotbar"), (p) -> {
      nbt.putBoolean("chargeHotbar", !nbt.getBoolean("chargeHotbar"));
    });
    inventory = new Button(guiLeft + 16 + 2 * (BUTTON_WIDTH + BUTTON_SCREEN_GAP), guiTop + 41, 4, true, new TranslationTextComponent("batteries.gui.button.inventory"), (p) -> {
      nbt.putBoolean("chargeInventory", !nbt.getBoolean("chargeInventory"));
    });
    armor = new Button(guiLeft + 16 + 3 * (BUTTON_WIDTH + BUTTON_SCREEN_GAP), guiTop + 41, 3, true, new TranslationTextComponent("batteries.gui.button.armor"), (p) -> {
      nbt.putBoolean("chargeArmor", !nbt.getBoolean("chargeArmor"));
    });
    fair = new Button(guiLeft + 16 + 4 * (BUTTON_WIDTH + BUTTON_SCREEN_GAP), guiTop + 41, 5, true, new TranslationTextComponent("batteries.gui.button.armor"), (p) -> {
      nbt.putBoolean("chargeFairly", !nbt.getBoolean("chargeFairly"));
    });
    this.addButton(whitelist);
    this.addButton(blacklist);
    this.addButton(hotbar);
    this.addButton(inventory);
    this.addButton(armor);
    this.addButton(fair);
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(matrixStack);
    super.render(matrixStack, mouseX, mouseY, partialTicks);
    this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
    CompoundNBT nbt = this.container.battery.getOrCreateTag();
    font.drawString(matrixStack, this.container.battery.getDisplayName().getString(), 8, 8, 4210752);
    this.container.battery.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> font.drawString(matrixStack, Integer.toString(e.getEnergyStored()), 90, 8, 4210752));
    for (Widget b : this.buttons) {
      if (b.isMouseOver(mouseX, mouseY))
        b.renderToolTip(matrixStack, mouseX - guiLeft, mouseY - guiTop);
      if (b instanceof Button) {
        boolean drawCheck = false;
        switch (((Button) b).index) {
          case 2: //hotbar
            drawCheck = nbt.getBoolean("chargeHotbar");
            break;
          case 3: //armor
            drawCheck = nbt.getBoolean("chargeArmor");
            break;
          case 4: //inventory
            drawCheck = nbt.getBoolean("chargeInventory");
            break;
          case 5: //fair
            drawCheck = nbt.getBoolean("chargeFairly");
            break;
        }
        if (drawCheck) {
          getMinecraft().getTextureManager().bindTexture(TEXTURE);
          this.blit(matrixStack, b.x - guiLeft, b.y - guiTop, 0, 147, b.getWidth(), b.getHeightRealms()); //not sure why it's named that.
        }
      }
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
    getMinecraft().getTextureManager().bindTexture(TEXTURE);

    this.blit(matrixStack, guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
  }

  class Button extends ExtendedButton {
    private int index;
    ITextComponent title;

    public Button(int screenX, int screenY, int index, boolean visible, ITextComponent title, IPressable pressable) {
      super(screenX, screenY, 20, 18, title, pressable);
      this.index = index;
      this.visible = visible;
      this.title = title;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      if (!visible)
        return;
      int offset = this.isMouseOver(mouseX, mouseY) ? BUTTON_WIDTH + BUTTON_GAP : 0;
      this.blit(matrixStack, x, y, BUTTON_OFFSET_X + offset,
              BUTTON_OFFSET_Y + index * (BUTTON_HEIGHT + BUTTON_GAP), BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    @Override
    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
      if (!visible)
        return;
      BatteryScreen.this.renderTooltip(matrixStack, this.title, mouseX, mouseY);
    }

    @Override
    public void onPress() {
      super.onPress();
      String nbtName;
      switch (index) {
        case 0:
        case 1:
          nbtName = "whitelist";
          break;
        case 2: //hotbar
          nbtName = "chargeHotbar";
          break;
        case 3: //armor
          nbtName = "chargeArmor";
          break;
        case 4: //inventory
          nbtName = "chargeInventory";
          break;
        case 5: //fair
          nbtName = "chargeFairly";
          break;
        default:
          nbtName = "";
      }
      GuiPacketHandler.INSTANCE.sendToServer(new GuiPacket(nbtName, BatteryScreen.this.container.battery.getOrCreateTag().getBoolean(nbtName)));
    }
  }
}
