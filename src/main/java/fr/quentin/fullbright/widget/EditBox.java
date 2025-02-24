package fr.quentin.fullbright.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A custom editable text box widget for the Fullbright mod.
 * Allows users to input and edit multi-line text.
 */
public class EditBox extends ClickableWidget {
    /**
     * List of lines of text in the edit box.
     */
    private final List<String> lines = new ArrayList<>();

    /**
     * Current cursor position (X coordinate).
     */
    private int cursorX = 0;

    /**
     * Current cursor position (Y coordinate).
     */
    private int cursorY = 0;

    /**
     * Vertical scroll offset for the edit box.
     */
    private int scrollOffset = 0;

    /**
     * Maximum number of visible lines in the edit box.
     */
    private static final int MAX_VISIBLE_LINES = 10;

    /**
     * Reference to the Minecraft client instance.
     */
    private final MinecraftClient client;

    /**
     * Flag indicating if the scrollbar is being dragged.
     */
    private boolean isDraggingScrollbar;

    /**
     * Selection start position (character index).
     */
    private int selectionStart = -1;

    /**
     * Selection end position (character index).
     */
    private int selectionEnd = -1;

    /**
     * Selection start line index.
     */
    private int selectionStartLine = -1;

    /**
     * Selection end line index.
     */
    private int selectionEndLine = -1;

    /**
     * Flag indicating if text selection is in progress.
     */
    private boolean isSelecting = false;

    /**
     * Flag indicating if the cursor is visible.
     */
    private final boolean cursorVisible = true;

    /**
     * Constructs a new EditBox widget.
     *
     * @param x      The x-coordinate of the widget.
     * @param y      The y-coordinate of the widget.
     * @param width  The width of the widget.
     * @param height The height of the widget.
     */
    public EditBox(int x, int y, int width, int height) {
        super(x, y, width, height, Text.of(""));
        this.client = MinecraftClient.getInstance();
        lines.add(""); // Initialize with an empty line
    }

    /**
     * Sets the text content of the edit box.
     *
     * @param text The text to set.
     */
    public void setText(String text) {
        lines.clear();
        Collections.addAll(lines, text.split("\n"));
        if (lines.isEmpty()) lines.add("");
        cursorX = 0;
        cursorY = 0;
        scrollOffset = 0;
        clearSelection();
    }

    /**
     * Gets the text content of the edit box.
     *
     * @return The text content.
     */
    public String getText() {
        return String.join("\n", lines);
    }

    /**
     * Clears the current text selection.
     */
    private void clearSelection() {
        selectionStart = -1;
        selectionEnd = -1;
        selectionStartLine = -1;
        selectionEndLine = -1;
        isSelecting = false;
    }

    /**
     * Gets the currently selected text.
     *
     * @return The selected text.
     */
    private String getSelectedText() {
        if (!hasSelection()) return "";

        StringBuilder selectedText = new StringBuilder();
        int startLine = Math.min(selectionStartLine, selectionEndLine);
        int endLine = Math.max(selectionStartLine, selectionEndLine);

        for (int i = startLine; i <= endLine; i++) {
            String line = lines.get(i);
            int start = (i == selectionStartLine) ? Math.min(selectionStart, selectionEnd) : 0;
            int end = (i == selectionEndLine) ? Math.max(selectionStart, selectionEnd) : line.length();

            if (i > startLine) selectedText.append("\n");
            selectedText.append(line, start, end);
        }

        return selectedText.toString();
    }

    /**
     * Checks if there is an active text selection.
     *
     * @return True if there is an active selection, false otherwise.
     */
    private boolean hasSelection() {
        return selectionStart != -1 && selectionEnd != -1 &&
                selectionStartLine != -1 && selectionEndLine != -1;
    }

    /**
     * Handles key press events for the edit box.
     *
     * @param keyCode   The key code of the key that was pressed.
     * @param scanCode  The scan code of the key that was pressed.
     * @param modifiers The modifiers (e.g., shift, ctrl) that were pressed.
     * @return True if the key press was handled, false otherwise.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean isControlDown = (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;

        if (isControlDown) {
            if (keyCode == GLFW.GLFW_KEY_C) {
                if (hasSelection()) {
                    client.keyboard.setClipboard(getSelectedText());
                    return true;
                }
            } else if (keyCode == GLFW.GLFW_KEY_V) {
                String clipboard = client.keyboard.getClipboard();
                if (clipboard != null && !clipboard.isEmpty()) {
                    if (hasSelection()) {
                        deleteSelectedText();
                    }
                    insertText(clipboard);
                    return true;
                }
            } else if (keyCode == GLFW.GLFW_KEY_X) {
                if (hasSelection()) {
                    client.keyboard.setClipboard(getSelectedText());
                    deleteSelectedText();
                    return true;
                }
            } else if (keyCode == GLFW.GLFW_KEY_A) {
                selectAll();
                return true;
            }
        }

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if (hasSelection()) {
                deleteSelectedText();
            }
            lines.add(cursorY + 1, "");
            cursorY++;
            cursorX = 0;
            adjustScroll();
            clearSelection();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (hasSelection()) {
                deleteSelectedText();
                return true;
            }
            if (cursorX > 0) {
                lines.set(cursorY, lines.get(cursorY).substring(0, cursorX - 1) + lines.get(cursorY).substring(cursorX));
                cursorX--;
            } else if (cursorY > 0) {
                String previousLine = lines.remove(cursorY);
                cursorY--;
                cursorX = lines.get(cursorY).length();
                lines.set(cursorY, lines.get(cursorY) + previousLine);
            }
            adjustScroll();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DELETE) {
            if (hasSelection()) {
                deleteSelectedText();
                return true;
            }
            if (cursorX < lines.get(cursorY).length()) {
                String currentLine = lines.get(cursorY);
                lines.set(cursorY, currentLine.substring(0, cursorX) + currentLine.substring(cursorX + 1));
            } else if (cursorY < lines.size() - 1) {
                String nextLine = lines.remove(cursorY + 1);
                lines.set(cursorY, lines.get(cursorY) + nextLine);
            }
            return true;
        }

        if ((modifiers & GLFW.GLFW_MOD_SHIFT) != 0) {
            if (keyCode == GLFW.GLFW_KEY_UP && cursorY > 0) {
                if (!isSelecting) {
                    startSelection();
                }
                cursorY--;
                cursorX = Math.min(cursorX, lines.get(cursorY).length());
                updateSelection();
                adjustScroll();
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_DOWN && cursorY < lines.size() - 1) {
                if (!isSelecting) {
                    startSelection();
                }
                cursorY++;
                cursorX = Math.min(cursorX, lines.get(cursorY).length());
                updateSelection();
                adjustScroll();
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_LEFT && cursorX > 0) {
                if (!isSelecting) {
                    startSelection();
                }
                cursorX--;
                updateSelection();
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_RIGHT && cursorX < lines.get(cursorY).length()) {
                if (!isSelecting) {
                    startSelection();
                }
                cursorX++;
                updateSelection();
                return true;
            }
        } else {
            clearSelection();
            if (keyCode == GLFW.GLFW_KEY_UP && cursorY > 0) {
                cursorY--;
                cursorX = Math.min(cursorX, lines.get(cursorY).length());
                adjustScroll();
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_DOWN && cursorY < lines.size() - 1) {
                cursorY++;
                cursorX = Math.min(cursorX, lines.get(cursorY).length());
                adjustScroll();
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_LEFT && cursorX > 0) {
                cursorX--;
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_RIGHT && cursorX < lines.get(cursorY).length()) {
                cursorX++;
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes the currently selected text.
     */
    private void deleteSelectedText() {
        if (!hasSelection()) return;

        int startLine = Math.min(selectionStartLine, selectionEndLine);
        int endLine = Math.max(selectionStartLine, selectionEndLine);
        int startX = Math.min(selectionStart, selectionEnd);
        int endX = Math.max(selectionStart, selectionEnd);

        if (startLine == endLine) {
            String line = lines.get(startLine);
            lines.set(startLine, line.substring(0, startX) + line.substring(endX));
        } else {
            String firstLine = lines.get(startLine);
            String lastLine = lines.get(endLine);
            String newLine = firstLine.substring(0, startX) + lastLine.substring(endX);
            lines.set(startLine, newLine);

            if (endLine >= startLine + 1) {
                lines.subList(startLine + 1, endLine + 1).clear();
            }
        }

        cursorX = startX;
        cursorY = startLine;
        clearSelection();
    }

    /**
     * Inserts text at the current cursor position.
     *
     * @param text The text to insert.
     */
    private void insertText(String text) {
        String[] newLines = text.split("\n");
        String currentLine = lines.get(cursorY);
        if (newLines.length == 1) {
            lines.set(cursorY, currentLine.substring(0, cursorX) + text + currentLine.substring(cursorX));
            cursorX += text.length();
        } else {
            String firstNewLine = newLines[0];
            String lastNewLine = newLines[newLines.length - 1];

            lines.set(cursorY, currentLine.substring(0, cursorX) + firstNewLine);

            for (int i = 1; i < newLines.length - 1; i++) {
                lines.add(cursorY + i, newLines[i]);
            }

            lines.add(cursorY + newLines.length - 1,
                    lastNewLine + currentLine.substring(cursorX));

            cursorY += newLines.length - 1;
            cursorX = lastNewLine.length();
        }
        adjustScroll();
    }

    /**
     * Selects all text in the edit box.
     */
    private void selectAll() {
        selectionStartLine = 0;
        selectionEndLine = lines.size() - 1;
        selectionStart = 0;
        selectionEnd = lines.getLast().length();
        cursorY = lines.size() - 1;
        cursorX = lines.get(cursorY).length();
        isSelecting = true;
    }

    /**
     * Starts a new text selection.
     */
    private void startSelection() {
        isSelecting = true;
        selectionStartLine = cursorY;
        selectionStart = cursorX;
        updateSelection();
    }

    /**
     * Updates the current text selection.
     */
    private void updateSelection() {
        if (isSelecting) {
            selectionEndLine = cursorY;
            selectionEnd = cursorX;
        }
    }

    /**
     * Handles character typed events for the edit box.
     *
     * @param chr        The character that was typed.
     * @param modifiers  The modifiers (e.g., shift, ctrl) that were pressed.
     * @return True if the character was handled, false otherwise.
     */
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (chr >= 32 && chr <= 126) {
            if (hasSelection()) {
                deleteSelectedText();
            }
            String currentLine = lines.get(cursorY);
            lines.set(cursorY, currentLine.substring(0, cursorX) + chr + currentLine.substring(cursorX));
            cursorX++;
            return true;
        }
        return false;
    }

    /**
     * Handles mouse click events for the edit box.
     *
     * @param mouseX The x-coordinate of the mouse click.
     * @param mouseY The y-coordinate of the mouse click.
     * @param button The mouse button that was clicked.
     * @return True if the mouse click was handled, false otherwise.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int scrollbarWidth = 6;
        int scrollbarX = this.getX() + this.width - scrollbarWidth;

        if (mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
                mouseY >= this.getY() && mouseY <= this.getY() + this.height) {

            isDraggingScrollbar = true;

            float availableScrollSpace = this.height;
            float relativeClickPosition = (float)(mouseY - this.getY()) / availableScrollSpace;
            int maxScrollOffset = Math.max(0, lines.size() - MAX_VISIBLE_LINES);
            scrollOffset = (int)(relativeClickPosition * maxScrollOffset);

            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));

            return true;
        }

        if (mouseX >= this.getX() && mouseX <= this.getX() + this.width &&
                mouseY >= this.getY() && mouseY <= this.getY() + this.height) {
            int line = (int)((mouseY - this.getY()) / 15) + scrollOffset;
            if (line >= 0 && line < lines.size()) {
                int clickX = (int)(mouseX - this.getX() - 5);
                String lineText = lines.get(line);
                cursorX = getCharacterIndexAtPosition(clickX, lineText);
                cursorY = line;

                long window = client.getWindow().getHandle();
                if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS ||
                        GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS) {
                    if (!isSelecting) {
                        startSelection();
                    }
                    updateSelection();
                } else {
                    clearSelection();
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Gets the character index at a specific position in the text.
     *
     * @param x    The x-coordinate of the position.
     * @param text The text to measure.
     * @return The character index at the position.
     */
    private int getCharacterIndexAtPosition(int x, String text) {
        int totalWidth = 0;
        for (int i = 0; i < text.length(); i++) {
            int charWidth = client.textRenderer.getWidth(String.valueOf(text.charAt(i)));
            if (totalWidth + charWidth / 2 > x) {
                return i;
            }
            totalWidth += charWidth;
        }
        return text.length();
    }

    /**
     * Handles mouse drag events for the edit box.
     *
     * @param mouseX     The x-coordinate of the mouse.
     * @param mouseY     The y-coordinate of the mouse.
     * @param button     The mouse button being dragged.
     * @param deltaX     The change in x-coordinate since the last event.
     * @param deltaY     The change in y-coordinate since the last event.
     * @return True if the mouse drag was handled, false otherwise.
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDraggingScrollbar) {
            float availableScrollSpace = this.height;
            float relativeClickPosition = (float)(mouseY - this.getY()) / availableScrollSpace;
            int maxScrollOffset = Math.max(0, lines.size() - MAX_VISIBLE_LINES);
            scrollOffset = (int)(relativeClickPosition * maxScrollOffset);

            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));

            return true;
        } else if (mouseX >= this.getX() && mouseX <= this.getX() + this.width &&
                mouseY >= this.getY() && mouseY <= this.getY() + this.height) {
            int line = (int)((mouseY - this.getY()) / 15) + scrollOffset;
            if (line >= 0 && line < lines.size()) {
                int clickX = (int)(mouseX - this.getX() - 5);
                String lineText = lines.get(line);
                cursorX = getCharacterIndexAtPosition(clickX, lineText);
                cursorY = line;

                if (!isSelecting) {
                    startSelection();
                }
                updateSelection();
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    /**
     * Handles mouse release events for the edit box.
     *
     * @param mouseX The x-coordinate of the mouse release.
     * @param mouseY The y-coordinate of the mouse release.
     * @param button The mouse button that was released.
     * @return True if the mouse release was handled, false otherwise.
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDraggingScrollbar = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    /**
     * Appends clickable narrations for accessibility.
     *
     * @param builder The narration message builder.
     */
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    /**
     * Adjusts the scroll offset to ensure the cursor is visible.
     */
    private void adjustScroll() {
        if (cursorY < scrollOffset) {
            scrollOffset = Math.max(0, cursorY);
        } else if (cursorY >= scrollOffset + MAX_VISIBLE_LINES) {
            scrollOffset = Math.min(lines.size() - MAX_VISIBLE_LINES, cursorY - MAX_VISIBLE_LINES + 1);
        }
    }

    /**
     * Renders the edit box widget.
     *
     * @param context The drawing context provided by the game.
     * @param mouseX  The x-coordinate of the mouse.
     * @param mouseY  The y-coordinate of the mouse.
     * @param delta   The delta time for the frame.
     */
    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xFF222222);

        int scrollbarWidth = 6;
        int totalLines = Math.max(1, lines.size());

        float visibleRatio = (float) MAX_VISIBLE_LINES / totalLines;
        int scrollbarHeight = Math.max(20, (int) (visibleRatio * this.height));
        scrollbarHeight = Math.min(scrollbarHeight, this.height);

        int maxScroll = Math.max(0, totalLines - MAX_VISIBLE_LINES);
        float scrollFraction = maxScroll > 0 ? (float) scrollOffset / maxScroll : 0;
        int scrollbarY = this.getY() + (int) (scrollFraction * (this.height - scrollbarHeight));
        int scrollbarX = this.getX() + this.width - scrollbarWidth;

        scrollbarY = Math.max(this.getY(), Math.min(scrollbarY, this.getY() + this.height - scrollbarHeight));

        context.fill(scrollbarX, scrollbarY, scrollbarX + scrollbarWidth, scrollbarY + scrollbarHeight, 0xFFAAAAAA);

        int y = this.getY() + 5;
        for (int i = scrollOffset; i < Math.min(lines.size(), scrollOffset + MAX_VISIBLE_LINES); i++) {
            String line = lines.get(i);
            int lineX = this.getX() + 5;

            if (hasSelection() && i >= Math.min(selectionStartLine, selectionEndLine) &&
                    i <= Math.max(selectionStartLine, selectionEndLine)) {

                int selStart = (i == selectionStartLine) ? selectionStart : 0;
                int selEnd = (i == selectionEndLine) ? selectionEnd : line.length();

                if (selectionStartLine > selectionEndLine ||
                        (selectionStartLine == selectionEndLine && selectionStart > selectionEnd)) {
                    int temp = selStart;
                    selStart = selEnd;
                    selEnd = temp;
                }

                int startX = lineX + client.textRenderer.getWidth(line.substring(0, selStart));
                int endX = lineX + client.textRenderer.getWidth(line.substring(0, selEnd));

                context.fill(startX, y, endX, y + 12, 0x80808080);
            }

            context.drawTextWithShadow(this.client.textRenderer, line, lineX, y, 0xFFFFFF);
            y += 15;
        }

        if (cursorVisible && cursorY >= scrollOffset && cursorY < scrollOffset + MAX_VISIBLE_LINES) {
            int cursorPosY = this.getY() + 5 + (cursorY - scrollOffset) * 15;
            int cursorPosX = this.getX() + 5 + this.client.textRenderer.getWidth(lines.get(cursorY).substring(0, cursorX));
            context.fill(cursorPosX, cursorPosY, cursorPosX + 2, cursorPosY + 12, 0xFFFFAA00);
        }
    }
}
