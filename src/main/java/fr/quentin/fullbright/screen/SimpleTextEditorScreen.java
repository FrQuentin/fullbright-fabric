package fr.quentin.fullbright.screen;

import fr.quentin.fullbright.config.NoteConfig;
import fr.quentin.fullbright.widget.EditBox;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * A simple text editor screen for the Fullbright mod.
 * Allows users to edit and save notes.
 */
public class SimpleTextEditorScreen extends Screen {
    /**
     * The editable text box where the user can input text.
     */
    private EditBox editBox;

    /**
     * The initial text to be displayed in the editor.
     */
    private final String initialText;

    /**
     * Constructs a new SimpleTextEditorScreen.
     *
     * @param initialText The initial text to be displayed in the editor.
     */
    public SimpleTextEditorScreen(String initialText) {
        super(Text.translatable("fullbright.screen.text_editor"));
        this.initialText = initialText;
    }

    /**
     * Initializes the screen by setting up the EditBox and buttons.
     */
    @Override
    protected void init() {
        // Initialize the EditBox with the initial text or the current note
        this.editBox = new EditBox(this.width / 2 - 150, this.height / 2 - 90, 300, 150);
        this.editBox.setText(initialText != null ? initialText : NoteConfig.getInstance().getNote());
        this.addDrawableChild(editBox);

        // Button to save the text
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("fullbright.button.save"), button -> saveText())
                .dimensions(this.width / 2 - 155, this.height / 2 + 80, 100, 20)
                .build());

        // Button to cancel and close the screen
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("fullbright.button.cancel"), button -> this.close())
                .dimensions(this.width / 2 - 50, this.height / 2 + 80, 100, 20)
                .build());

        // Button to clear all text in the EditBox
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("fullbright.button.clear"), button -> clearText())
                .dimensions(this.width / 2 + 55, this.height / 2 + 80, 100, 20)
                .build());
    }

    /**
     * Clears the text in the EditBox.
     */
    private void clearText() {
        editBox.setText("");
    }

    /**
     * Saves the text from the EditBox to the NoteConfig and closes the screen.
     */
    private void saveText() {
        String text = editBox.getText();
        NoteConfig.getInstance().setNote(text);
        this.close();
    }

    /**
     * Handles key press events for the EditBox.
     *
     * @param keyCode   The key code of the key that was pressed.
     * @param scanCode  The scan code of the key that was pressed.
     * @param modifiers The modifiers (e.g., shift, ctrl) that were pressed.
     * @return True if the key press was handled, false otherwise.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.editBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Handles character typed events for the EditBox.
     *
     * @param chr        The character that was typed.
     * @param modifiers  The modifiers (e.g., shift, ctrl) that were pressed.
     * @return True if the character was handled, false otherwise.
     */
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (this.editBox.charTyped(chr, modifiers)) {
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    /**
     * Renders the screen and its components.
     *
     * @param context The drawing context provided by the game.
     * @param mouseX  The x-coordinate of the mouse.
     * @param mouseY  The y-coordinate of the mouse.
     * @param delta   The delta time for the frame.
     */
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawTextWithShadow(this.textRenderer, Text.translatable("fullbright.screen.text_editor.name"), this.width / 2 - 150, this.height / 2 - 120, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    /**
     * Determines whether the game should be paused when this screen is open.
     *
     * @return False, indicating the game should not pause.
     */
    @Override
    public boolean shouldPause() {
        return false;
    }
}
