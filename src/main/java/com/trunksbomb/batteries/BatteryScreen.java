package com.trunksbomb.batteries;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.trunksbomb.batteries.capability.BatteryContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.ArrayList;
import java.util.List;

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
    whitelist = new Button(guiLeft + 16, guiTop + 41, 0, this.container.battery.getOrCreateTag().getBoolean("whitelist"), new TranslationTextComponent("batteries.gui.button.whitelist"), (p) -> {
      blacklist.visible = true;
      whitelist.visible = false;
      this.container.battery.getOrCreateTag().putBoolean("whitelist", false);
    });
    blacklist = new Button(guiLeft + 16, guiTop + 41, 1, !this.container.battery.getOrCreateTag().getBoolean("whitelist"), new TranslationTextComponent("batteries.gui.button.blacklist"), (p) -> {
      blacklist.visible = false;
      whitelist.visible = true;
      this.container.battery.getOrCreateTag().putBoolean("whitelist", true);
    });
    hotbar = new Button(guiLeft + 16 + (BUTTON_WIDTH + BUTTON_SCREEN_GAP), guiTop + 41, 2, true, new TranslationTextComponent("batteries.gui.button.hotbar"), (p) -> {
      this.container.battery.getOrCreateTag().putBoolean("chargeHotbar", !this.container.battery.getOrCreateTag().getBoolean("chargeHotbar"));
    });
    inventory = new Button(guiLeft + 16 + 2 * (BUTTON_WIDTH + BUTTON_SCREEN_GAP), guiTop + 41, 4, true, new TranslationTextComponent("batteries.gui.button.inventory"), (p) -> {
      this.container.battery.getOrCreateTag().putBoolean("chargeInventory", !this.container.battery.getOrCreateTag().getBoolean("chargeInventory"));
    });
    armor = new Button(guiLeft + 16 + 3 * (BUTTON_WIDTH + BUTTON_SCREEN_GAP), guiTop + 41, 3, true, new TranslationTextComponent("batteries.gui.button.armor"), (p) -> {
      this.container.battery.getOrCreateTag().putBoolean("chargeArmor", !this.container.battery.getOrCreateTag().getBoolean("chargeArmor"));
    });
    fair = new Button(guiLeft + 16 + 4 * (BUTTON_WIDTH + BUTTON_SCREEN_GAP), guiTop + 41, 5, true, new TranslationTextComponent("batteries.gui.button.armor"), (p) -> {
      this.container.battery.getOrCreateTag().putBoolean("chargeFairly", !this.container.battery.getOrCreateTag().getBoolean("chargeFairly"));
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
    font.drawString(matrixStack, this.container.battery.getDisplayName().getString(), 8, 8, 4210752);
    for (Widget b : this.buttons) {
      if (b.isMouseOver(mouseX, mouseY))
        b.renderToolTip(matrixStack, mouseX - guiLeft, mouseY - guiTop);
      if (b instanceof Button) {
        boolean drawCheck = false;
        switch (((Button) b).index) {
          case 2: //hotbar
            drawCheck = this.container.battery.getOrCreateTag().getBoolean("chargeHotbar");
            break;
          case 3: //armor
            drawCheck = this.container.battery.getOrCreateTag().getBoolean("chargeArmor");
            break;
          case 4: //inventory
            drawCheck = this.container.battery.getOrCreateTag().getBoolean("chargeInventory");
            break;
          case 5: //fair
            drawCheck = this.container.battery.getOrCreateTag().getBoolean("chargeFairly");
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
  }
}
