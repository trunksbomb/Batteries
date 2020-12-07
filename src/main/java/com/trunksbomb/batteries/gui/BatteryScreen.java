package com.trunksbomb.batteries.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.trunksbomb.batteries.BatteriesMod;
import com.trunksbomb.batteries.container.BatteryContainer;
import com.trunksbomb.batteries.network.GuiPacket;
import com.trunksbomb.batteries.network.GuiPacketHandler;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class BatteryScreen extends ContainerScreen<BatteryContainer> {
  public static final int WIDTH = 200;
  public static final int HEIGHT = 164;
  public static final int BUTTON_WIDTH = 20;
  public static final int BUTTON_HEIGHT = 18;
  public static final int BUTTON_SCREEN_GAP_X = 7; //the gap between buttons when drawn on-screen
  public static final int BUTTON_SCREEN_GAP_Y = 3;
  public static final int BUTTON_START_X = 48;
  public static final int BUTTON_START_Y = 39;
  public static final int INVENTORY_START_X = 32;
  public static final int INVENTORY_START_Y = 19;
  public static final int PLAYER_INVENTORY_START_Y = 82;
  public static final int ARMOR_START_X = 7;
  public static final int ARMOR_START_Y = 19;

  //Texture Offsets
  public static final int ARMOR_TEXTURE_X = 42;
  public static final int ARMOR_TEXTURE_Y = 170;
  public static final int BUTTON_TEXTURE_GAP = 1; //the gap between buttons in the GUI texture
  public static final int BUTTON_TEXTURE_START_X = 201; //where the buttons start in the GUI texture
  public static final int BUTTON_TEXTURE_START_Y = 0; //same, but for Y-axis
  public static final int CHECK_TEXTURE_X = 25;
  public static final int CHECK_TEXTURE_Y = 170;
  public static final int PLUS_TEXTURE_X = 144;
  public static final int PLUS_TEXTURE_Y = 170;


  private Button whitelist, blacklist, hotbar, armor, inventory, fair, machine;
  private static final ResourceLocation TEXTURE = new ResourceLocation(BatteriesMod.MODID, "textures/battery_gui.png");

  public BatteryScreen(BatteryContainer batteryContainer, PlayerInventory inv, ITextComponent titleIn) {
    super(batteryContainer, inv, titleIn);
    xSize = WIDTH;
    ySize = HEIGHT;
  }

  @Override
  protected void init() {
    super.init();
    CompoundNBT nbt = this.container.battery.getOrCreateTag();
    whitelist = createButton(0, 0, 0, true,"batteries.gui.button.whitelist", (p) -> {
      blacklist.visible = true;
      whitelist.visible = false;
      nbt.putBoolean("whitelist", false);
    });
    blacklist = createButton(0, 0, 1, false, "batteries.gui.button.blacklist", (p) -> {
      blacklist.visible = false;
      whitelist.visible = true;
      nbt.putBoolean("whitelist", true);
    });
    hotbar = createButton(0, 1, 2, true, "batteries.gui.button.hotbar", (p) -> {
      nbt.putBoolean("chargeHotbar", !nbt.getBoolean("chargeHotbar"));
    });
    inventory = createButton(0, 2, 3, true, "batteries.gui.button.inventory", (p) -> {
      nbt.putBoolean("chargeInventory", !nbt.getBoolean("chargeInventory"));
    });
    armor = createButton(0, 3, 4, true, "batteries.gui.button.armor", (p) -> {
      nbt.putBoolean("chargeArmor", !nbt.getBoolean("chargeArmor"));
    });
    fair = createButton(0, 4, 5, true, "batteries.gui.button.fair", (p) -> {
      nbt.putBoolean("chargeFairly", !nbt.getBoolean("chargeFairly"));
    });
    machine = createButton(1, 0, 6, true, "batteries.gui.button.machine", (p) -> {
      nbt.putBoolean("chargeMachine", !nbt.getBoolean("chargeMachine"));
    });
    this.addButton(whitelist);
    this.addButton(blacklist);
    this.addButton(hotbar);
    this.addButton(inventory);
    this.addButton(armor);
    this.addButton(fair);
    this.addButton(machine);
  }

  private Button createButton(int rowIndex, int colIndex, int buttonIndex, boolean isVisible, String tooltipPath, net.minecraft.client.gui.widget.button.Button.IPressable pressable) {
    return new Button(guiLeft + BUTTON_START_X + colIndex * (BUTTON_WIDTH + BUTTON_SCREEN_GAP_X), guiTop + BUTTON_START_Y + rowIndex * (BUTTON_HEIGHT + BUTTON_SCREEN_GAP_Y), buttonIndex, isVisible, new TranslationTextComponent(tooltipPath), pressable);
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
    font.drawString(matrixStack, this.container.battery.getDisplayName().getString(), INVENTORY_START_X, 8, 4210752);
    for (Widget b : this.buttons) {
      if (b.isMouseOver(mouseX, mouseY))
        b.renderToolTip(matrixStack, mouseX - guiLeft, mouseY - guiTop);
      if (b instanceof Button) {
        boolean drawCheck = false;
        switch (((Button) b).index) {
          case 2: //hotbar
            drawCheck = nbt.getBoolean("chargeHotbar");
            break;
          case 3: //inventory
            drawCheck = nbt.getBoolean("chargeInventory");
            break;
          case 4: //armor
            drawCheck = nbt.getBoolean("chargeArmor");
            break;
          case 5: //fair
            drawCheck = nbt.getBoolean("chargeFairly");
            break;
          case 6: //machine
            drawCheck = nbt.getBoolean("chargeMachine");
            break;
        }
        if (drawCheck) {
          getMinecraft().getTextureManager().bindTexture(TEXTURE);
          this.blit(matrixStack, b.x - guiLeft + 4, b.y - guiTop + 4, CHECK_TEXTURE_X, CHECK_TEXTURE_Y, 16, 16);
        }
      }
    }
    for (int slot = 0; slot < 9; slot++) { //draw plus slots
      getMinecraft().getTextureManager().bindTexture(TEXTURE);
      int screenX = INVENTORY_START_X + 18 * slot;
      if (this.container.inventorySlots.get(slot).getStack().isEmpty())
        this.blit(matrixStack, screenX, INVENTORY_START_Y, PLUS_TEXTURE_X, PLUS_TEXTURE_Y, 16, 16);
    }
    for (int armorIndex = 0; armorIndex < 4; armorIndex++) { //draw armor placeholders
      int slot = 39 - armorIndex;
      int screenX = ARMOR_START_X;
      int screenY = ARMOR_START_Y + armorIndex * 18;
      int textureX = ARMOR_TEXTURE_X + armorIndex * 17;
      int textureY = ARMOR_TEXTURE_Y;
      if (this.playerInventory.getStackInSlot(slot).isEmpty())
        this.blit(matrixStack, screenX, screenY, textureX, textureY, 16, 16);
    }
    if (this.playerInventory.getStackInSlot(40).isEmpty())
      this.blit(matrixStack, ARMOR_START_X, 91, 110, 170, 16, 16); //draw shield placeholder
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
      int offset = this.isMouseOver(mouseX, mouseY) ? BUTTON_WIDTH + BUTTON_TEXTURE_GAP : 0;
      this.blit(matrixStack, x, y, BUTTON_TEXTURE_START_X + offset,
              BUTTON_TEXTURE_START_Y + index * (BUTTON_HEIGHT + BUTTON_TEXTURE_GAP), BUTTON_WIDTH, BUTTON_HEIGHT);
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
        case 6: //fair
          nbtName = "chargeMachine";
          break;
        default:
          nbtName = "";
      }
      GuiPacketHandler.INSTANCE.sendToServer(new GuiPacket(nbtName, BatteryScreen.this.container.battery.getOrCreateTag().getBoolean(nbtName)));
    }
  }
}
