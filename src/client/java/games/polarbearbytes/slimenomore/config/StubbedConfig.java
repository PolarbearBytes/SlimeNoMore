package games.polarbearbytes.slimenomore.config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class StubbedConfig extends Screen {
    private final Screen parent;
    protected StubbedConfig(Screen parent) {
        super(Text.literal("SlimeNoMore Config (Temp)"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        addDrawableChild(ButtonWidget.builder(Text.literal("Placeholder Config"), b -> {})
                .dimensions(this.width / 2 - 75, this.height / 2, 150, 20)
                .build());
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}