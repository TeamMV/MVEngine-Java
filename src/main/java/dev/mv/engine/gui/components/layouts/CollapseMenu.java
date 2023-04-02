package dev.mv.engine.gui.components.layouts;

import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.components.ImageButton;
import dev.mv.engine.gui.components.TextLine;
import dev.mv.engine.gui.components.animations.TextAnimation;
import dev.mv.engine.gui.components.animations.TextAnimator;
import dev.mv.engine.gui.components.assets.GuiAssets;
import dev.mv.engine.gui.components.extras.Text;
import dev.mv.engine.gui.components.extras.Toggle;
import dev.mv.engine.gui.event.ClickListener;
import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.gui.utils.GuiUtils;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.input.Input;
import dev.mv.engine.render.shared.Color;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.font.BitmapFont;

import java.util.function.Predicate;

public class CollapseMenu extends AbstractLayout implements Toggle, Text {
    private boolean collapsed = true;
    private LayerSection layerSection;
    private boolean chroma;
    private ButtonSide side = ButtonSide.LEFT;

    private UpdateSection updateSection;
    private HorizontalLayout rootLayout;
    private VerticalLayout stackLayout;
    private HorizontalLayout headerLayout;
    private VerticalLayout contentLayout;

    private ImageButton collapseButton;
    private TextLine collapseButtonText;

    public CollapseMenu(Window window, int width, int height, Element parent) {
        super(window, width, height, parent);
        prepareElements();
    }

    public CollapseMenu(Window window, int x, int y, int width, int height, Element parent) {
        super(window, x, y, width, height, parent);
        prepareElements();
    }

    public CollapseMenu(Window window, VariablePosition position, Element parent) {
        super(window, position, parent);
        prepareElements();
    }

    public CollapseMenu(Window window, VariablePosition position, Element parent, ButtonSide side) {
        super(window, position, parent);
        this.side = side;
        prepareElements();
    }

    private void prepareElements() {
        layerSection = new LayerSection(window, null);
        layerSection.setLayerToRenderOn(1);
        updateSection = new UpdateSection(window, layerSection);
        rootLayout = new HorizontalLayout(window, this);
        rootLayout.alignContent(HorizontalLayout.Align.BOTTOM);
        stackLayout = new VerticalLayout(window, rootLayout);
        stackLayout.setSpacing(5);
        stackLayout.alignContent(VerticalLayout.Align.LEFT);
        headerLayout = new HorizontalLayout(window, stackLayout);
        headerLayout.setPadding(5, 5, 5, 5);
        headerLayout.showFrame();
        contentLayout = new VerticalLayout(window, stackLayout);
        contentLayout.setSpacing(5);
        stackLayout.addElement(headerLayout);

        collapseButton = new ImageButton(window, headerLayout, initialState.height, initialState.height);
        collapseButton.attachListener(new ClickListener() {
            @Override
            public void onCLick(Element element, int button) {

            }

            @Override
            public void onRelease(Element element, int button) {
                if (button == Input.BUTTON_LEFT) {
                    toggleCollapseState();
                    collapseButton.setTexture(isCollapsed() ? GuiAssets.ARROW_DOWN : GuiAssets.ARROW_UP);
                }
            }
        });
        collapseButtonText = new TextLine(window, headerLayout, initialState.height - textDistance() * 2);
        if (side == ButtonSide.LEFT) {
            headerLayout.addElement(collapseButton);
            headerLayout.addElement(collapseButtonText);
        }
        else {
            headerLayout.addElement(collapseButtonText);
            headerLayout.addElement(collapseButton);
        }
        headerLayout.alignContent(HorizontalLayout.Align.CENTER);
        headerLayout.setSpacing(5);

        contentLayout.setPadding(5, 5, 5, 5);
        contentLayout.setSpacing(5);
        contentLayout.showFrame();

        layerSection.addElement(updateSection);
        updateSection.addElement(rootLayout);
        rootLayout.addElement(stackLayout);
        super.addElement(layerSection);
    }

    protected int textDistance() {
        return initialState.height / 5;
    }

    @Override
    public int getSpacing() {
        return contentLayout.getSpacing();
    }

    @Override
    public void setSpacing(int spacing) {
        contentLayout.setSpacing(spacing);
    }

    @Override
    public void addElement(Element e) {
        contentLayout.addElement(e);
    }

    @Override
    public void addElements(Element[] e) {
        contentLayout.addElements(e);
    }

    @Override
    public void removeElement(Element e) {
        contentLayout.removeElement(e);
    }

    public void removeElements(Element[] e) {
        contentLayout.removeElements(e);
    }

    @Override
    public void removeElements(Predicate<? super Element> predicate) {
        contentLayout.removeElements(predicate);
    }

    public Element[] items() {
        return contentLayout.elements();
    }

    @Override
    public void draw(DrawContext2D draw) {
        layerSection.draw(draw);
    }

    @Override
    public BitmapFont getFont() {
        return collapseButtonText.getFont();
    }

    @Override
    public void setFont(BitmapFont font) {
        collapseButtonText.setFont(font);
    }

    @Override
    public String getText() {
        return collapseButtonText.getText();
    }

    @Override
    public void setText(String text) {
        collapseButtonText.setText(text);
    }

    @Override
    public void setUseChroma(boolean chroma) {
        this.chroma = chroma;
        collapseButtonText.setUseChroma(chroma);
    }

    @Override
    public void disable() {
        collapseButton.disable();
    }

    @Override
    public void enable() {
        collapseButton.enable();
    }

    @Override
    public void toggle() {
        collapseButton.toggle();
    }

    @Override
    public boolean isEnabled() {
        return collapseButton.isEnabled();
    }

    public void collapse() {
        collapsed = true;
        stackLayout.removeElement(contentLayout);
        collapseButton.setTexture(GuiAssets.ARROW_DOWN);
        gui.enableAllUpdates();
        layerSection.dismiss();
    }

    public void inflate() {
        collapsed = false;
        stackLayout.addElement(contentLayout);
        collapseButton.setTexture(GuiAssets.ARROW_UP);
        gui.disableAllUpdates();
        updateSection.enable();
        layerSection.prioritize();
    }

    public void toggleCollapseState() {
        collapsed = !collapsed;
        if (collapsed) collapse();
        else inflate();
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    @Override
    public void setTheme(Theme theme) {
        super.setTheme(theme);
        collapseButton.setTexture(isCollapsed() ? GuiAssets.ARROW_DOWN : GuiAssets.ARROW_UP);
        rootLayout.setTheme(theme);
        contentLayout.setTheme(theme);
    }

    @Override
    public void attachListener(EventListener listener) {
        collapseButton.attachListener(listener);
    }

    @Override
    public void click(int x, int y, int btn) {
        super.click(x, y, btn);
        if (GuiUtils.mouseNotInside(rootLayout)) {
            collapse();
        }
    }

    @Override
    public int getX() {
        return headerLayout != null ? headerLayout.getX() : 0;
    }

    @Override
    public void setX(int x) {
        rootLayout.setX(x);
    }

    @Override
    public int getY() {
        return headerLayout != null ? headerLayout.getY() : 0;
    }

    @Override
    public void setY(int y) {
        rootLayout.setY(y);
    }

    @Override
    public int getWidth() {
        return headerLayout != null ? headerLayout.getWidth() : 0;
    }

    @Override
    public void setWidth(int width) {
        initialState.width = width;
        initialState.originX = initialState.posX + width / 2;
    }

    @Override
    public int getHeight() {
        //for some weird reason, the root layout height works here even if it should'nt lol.
        //maybe it doesn't...
        //return rootLayout != null ? rootLayout.getHeight() : 0;
        return headerLayout != null ? headerLayout.getHeight() : 0;
    }

    @Override
    public void setHeight(int height) {
        initialState.height = height;
        initialState.originY = initialState.posY + height / 2;
    }

    @Override
    public void setBaseColor(Color color) {
        headerLayout.setBaseColor(color);
        contentLayout.setBaseColor(color);
    }

    @Override
    public void setOutlineColor(Color color) {
        headerLayout.setOutlineColor(color);
        contentLayout.setOutlineColor(color);
    }

    @Override
    public void setTextColor(Color color) {
        headerLayout.setTextColor(color);
        contentLayout.setTextColor(color);
    }

    @Override
    public void setExtraColor(Color color) {
        headerLayout.setExtraColor(color);
        contentLayout.setExtraColor(color);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        headerLayout.resize(width, height);
        contentLayout.resize(width, height);
    }

    public enum ButtonSide {
        LEFT,
        RIGHT
    }
}
