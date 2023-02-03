package com.github.chrisblutz.breadboard.ui.toolkit;

import com.github.chrisblutz.breadboard.logging.BreadboardLogging;
import com.github.chrisblutz.breadboard.ui.toolkit.display.timing.RenderPassManager;
import com.github.chrisblutz.breadboard.ui.toolkit.layout.UIDimension;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UIWindow {

    private class InputListener implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

        private UIFocusable currentFocused = null;
        private List<UIInteractable> currentlyHovered = new ArrayList<>();
        private List<UIInteractable> previouslyHovered = new ArrayList<>();

        private void setFocused(UIFocusable focusable, boolean keyboardTriggered) {
            // If the current focused element is the same as the new one, return
            if (focusable == currentFocused)
                return;

            // If the focus is on a new element, call the focus loss listener on the old element
            if (currentFocused != null)
                currentFocused.setFocusState(false, keyboardTriggered);

            // If the new element is not null, call the focus gain listener on it
            if (focusable != null)
                focusable.setFocusState(true, keyboardTriggered);

            // Set the focused element
            currentFocused = focusable;
        }

        private void calculateHovered(int x, int y) {
            // Swap the current hovered elements to the old elements, then clear
            // the hovered elements
            List<UIInteractable> temporaryHovered = previouslyHovered;
            previouslyHovered = currentlyHovered;
            currentlyHovered = temporaryHovered;
            currentlyHovered.clear();

            // Calculate hover for main container
            if (content != null)
                calculateHovered(content, x, y);

            // Call mouse entered/exited listeners on elements that need them.
            // Mouse exit events should be called on elements in the old hover list
            // but not the new one, and mouse enter events should be called on elements
            // in the new list but not the old one
            for (UIInteractable previouslyHoveredElement : previouslyHovered)
                if (!currentlyHovered.contains(previouslyHoveredElement))
                    previouslyHoveredElement.onMouseExited();

            for (UIInteractable currentlyHoveredElement : currentlyHovered)
                if (!previouslyHovered.contains(currentlyHoveredElement))
                    currentlyHoveredElement.onMouseEntered();
        }

        private void calculateHovered(UIComponent component, int x, int y) {
            // If the mouse is not in this component, return now
            if (!component.getRenderSpace().getRectangle().contains(x, y))
                return;

            // If we get here, the mouse is inside this component.  Add it to the hierarchy
            // if it's interactable
            if (component instanceof UIInteractable interactable)
                currentlyHovered.add(interactable);

            // If this is a container, check its children
            if (component instanceof UIContainer containerComponent)
                // For each child component, call this method again
                for (UIComponent childComponent : containerComponent.getComponents())
                    calculateHovered(childComponent, x, y);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // Click events should propagate through hovered elements, last to first,
            // until one returns true (indicating that the event should stop there)
            boolean foundInteractable = false;
            for (int index = currentlyHovered.size() - 1; index >= 0; index--) {
                UIInteractable interactable = currentlyHovered.get(index);
                if (interactable instanceof UIComponent component &&
                        interactable.onMouseClicked(e.getX() - component.getX(), e.getY() - component.getY(), e.getButton())) {
                    // If the element accepted the click, set focus, then break
                    if (interactable instanceof UIFocusable focusable)
                        setFocused(focusable, false);
                    foundInteractable = true;
                    break;
                }
            }

            // If we get here, we didn't find any interactable elements to focus on,
            // so we should set the focused element to null
            if (!foundInteractable)
                setFocused(null, false);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // Press events should propagate through hovered elements, last to first,
            // until one returns true (indicating that the event should stop there)
            boolean foundInteractable = false;
            for (int index = currentlyHovered.size() - 1; index >= 0; index--) {
                UIInteractable interactable = currentlyHovered.get(index);
                if (interactable instanceof UIComponent component &&
                        interactable.onMousePressed(e.getX() - component.getX(), e.getY() - component.getY(), e.getButton())) {
                    // If the element accepted the click, set focus, then break
                    if (interactable instanceof UIFocusable focusable)
                        setFocused(focusable, false);
                    foundInteractable = true;
                    break;
                }
            }

            // If we get here, we didn't find any interactable elements to focus on,
            // so we should set the element component to null
            if (!foundInteractable)
                setFocused(null, false);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // Release events do not propagate, and always target the focused element,
            // since that element received the press event.
            if (currentFocused != null &&
                    currentFocused instanceof UIInteractable interactable &&
                    currentFocused instanceof UIComponent component)
                interactable.onMouseReleased(e.getX() - component.getX(), e.getY() - component.getY(), e.getButton());
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // Calculate hovered elements, but don't fire any move listeners
            calculateHovered(e.getX(), e.getY());
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // Since the mouse has left the window, unset all hovered elements and call
            // their mouse exit listeners
            for (int index = currentlyHovered.size() - 1; index >= 0; index--)
                currentlyHovered.get(index).onMouseExited();
            // Clear hovered elements
            currentlyHovered.clear();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            // Calculate hovered elements, but don't fire any move listeners
            calculateHovered(e.getX(), e.getY());

            // Drag events do not propagate, and always target the focused element,
            // since that element received the press event.
            if (currentFocused != null &&
                    currentFocused instanceof UIInteractable interactable &&
                    currentFocused instanceof UIComponent component)
                interactable.onMouseDragged(e.getX() - component.getX(), e.getY() - component.getY());
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            // Calculate hovered elements, then fire mouse move listeners on them
            calculateHovered(e.getX(), e.getY());

            // Move events should propagate through hovered elements, last to first,
            // until one returns true (indicating that the event should stop there)
            for (int index = currentlyHovered.size() - 1; index >= 0; index--) {
                UIInteractable interactable = currentlyHovered.get(index);
                if (interactable instanceof UIComponent component &&
                        interactable.onMouseMoved(e.getX() - component.getX(), e.getY() - component.getY()))
                    break;
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            // Scroll events should propagate through hovered elements, last to first,
            // until one returns true (indicating that the event should stop there)
            for (int index = currentlyHovered.size() - 1; index >= 0; index--)
                if (currentlyHovered.get(index).onMouseScrolled(e.getUnitsToScroll()))
                    break;
        }

        @Override
        public void keyTyped(KeyEvent e) {
            // Key events start at the focused element, then propagate up through its parents
            if (currentFocused == null || currentFocused.onKeyTyped(e))
                return;

            // If the current focused element is a component, propagate
            if (currentFocused instanceof UIComponent currentFocusedComponent) {
                UIComponent currentComponent = currentFocusedComponent.getParent();
                // Continue until either we break out of the loop or reach the deepest parent
                while (currentComponent != null) {
                    if (currentComponent instanceof UIFocusable focusable && focusable.onKeyTyped(e))
                        break;

                    currentComponent = currentComponent.getParent();
                }
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            // Handle focus events here, before passing key events onto the focused element
            if (e.getKeyCode() == KeyEvent.VK_TAB) {
                // If shift is down, traverse focus backward.  Otherwise, traverse forward
                if (e.isShiftDown())
                    focusPrevious(true);
                else
                    focusNext(true);

                return;
            }

            // Key events start at the focused element, then propagate up through its parents
            if (currentFocused == null || currentFocused.onKeyPressed(e))
                return;

            // If the current focused element is a component, propagate
            if (currentFocused instanceof UIComponent currentFocusedComponent) {
                UIComponent currentComponent = currentFocusedComponent.getParent();
                // Continue until either we break out of the loop or reach the deepest parent
                while (currentComponent != null) {
                    if (currentComponent instanceof UIFocusable focusable && focusable.onKeyPressed(e))
                        break;

                    currentComponent = currentComponent.getParent();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // Key events start at the focused element, then propagate up through its parents
            if (currentFocused == null || currentFocused.onKeyReleased(e))
                return;

            // If the current focused element is a component, propagate
            if (currentFocused instanceof UIComponent currentFocusedComponent) {
                UIComponent currentComponent = currentFocusedComponent.getParent();
                // Continue until either we break out of the loop or reach the deepest parent
                while (currentComponent != null) {
                    if (currentComponent instanceof UIFocusable focusable && focusable.onKeyReleased(e))
                        break;

                    currentComponent = currentComponent.getParent();
                }
            }
        }
    }

    private class UIWindowContainer extends UIContainer {

        @Override
        public void render(UIGraphics graphics) {
            if (content != null)
                renderComponent(graphics, content);
        }

        @Override
        public List<UIComponent> getComponents() {
            return content != null ? Collections.singletonList(content) : List.of();
        }

        @Override
        public void pack() {
            // Set the minimum size of the frame, then update the internal panel
            if (content != null && content.getMinimumSize() != null) {
                setMinimumSize(content.getMinimumSize());
                internalFrame.setMinimumSize(new Dimension(
                        content.getMinimumSize().getWidth(),
                        content.getMinimumSize().getHeight()
                ));
            } else {
                internalFrame.setMinimumSize(new Dimension(0, 0));
            }
            internalPanel.updateFromContent();

            // Now settle the contents
            settle();
        }

        @Override
        public void settle() {
            // Settle the main content if it's a container
            if (content instanceof UIContainer containerContent)
                containerContent.settle();
        }
    }

    private class InternalPanel extends JPanel {

        private final InputListener inputListener;

        private InternalPanel() {
            this.inputListener = new InputListener();

            // Assign the input listener as the listener for all input events
            addMouseListener(inputListener);
            addMouseMotionListener(inputListener);
            addMouseWheelListener(inputListener);
            addKeyListener(inputListener);

            addComponentListener(new ComponentListener() {
                @Override
                public void componentResized(ComponentEvent e) {
                    // If this panel gets resized, resize the content and settle it (if it's a container)
                    content.getRenderSpace().update(0, 0, getWidth(), getHeight());
                    content.setSize(
                            new UIDimension(
                                    getWidth(),
                                    getHeight()
                            )
                    );

                    if (content instanceof UIContainer containerContent)
                        containerContent.settle();
                }

                @Override
                public void componentMoved(ComponentEvent e) {

                }

                @Override
                public void componentShown(ComponentEvent e) {

                }

                @Override
                public void componentHidden(ComponentEvent e) {

                }
            });

            setFocusTraversalKeysEnabled(false);
            setFocusable(true);
            SwingUtilities.invokeLater(this::requestFocusInWindow);
            requestFocusInWindow();
        }

        private void updateFromContent() {
            // Set the preferred size of this component based on the preferred size
            // of the content.  If the content has no preferred size, use the minimum.
            if (content.getPreferredSize() != null)
                setPreferredSize(new Dimension(
                        content.getPreferredSize().getWidth(),
                        content.getPreferredSize().getHeight()
                ));
            else if (content.getMinimumSize() != null)
                setPreferredSize(new Dimension(
                        content.getMinimumSize().getWidth(),
                        content.getMinimumSize().getHeight()
                ));

            // Set the size of the content based on its preferred size
            content.setSize(content.getPreferredSize());
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            // Call parent paint to clear canvas
            super.paintComponent(graphics);

            // Set up the graphics object for rendering by:
            //  - Turning on anti-aliasing
            Graphics2D g = (Graphics2D) graphics;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Create UIToolkit graphics based on the AWT graphics above
            UIGraphics uiToolkitGraphics = new UIGraphics(g);

            // Render the content
            content.render(uiToolkitGraphics);

            // Call all the render pass listeners
            content.onRenderPass();
        }
    }

    private String windowTitle;
    private final JFrame internalFrame;
    private final InternalPanel internalPanel;
    private UIComponent content;
    private final UIWindowContainer windowContainer;

    private RenderPassManager renderPassManager;

    public UIWindow(String windowTitle) {
        this.windowTitle = windowTitle;
        internalFrame = new JFrame(windowTitle);
        internalFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        internalFrame.setFocusTraversalKeysEnabled(false);

        // Create internal JPanel
        internalPanel = new InternalPanel();
        internalFrame.setContentPane(internalPanel);

        // Initialize render pass manager
        renderPassManager = new RenderPassManager(1000 / 60, () -> {
            internalPanel.revalidate();
            internalPanel.repaint();
        });

        this.windowContainer = new UIWindowContainer();
        this.windowContainer.setParentWindow(this);
    }

    public void setContent(UIComponent component) {
        this.content = component;
        content.setParent(windowContainer);
        content.setParentWindow(this);

        // Update the frame and internal panel with the information from the new content
        windowContainer.pack();

        // Pack the frame, so it fits around the preferred size of the content
        internalFrame.pack();
    }

    public void setVisible(boolean visible) {
        BreadboardLogging.getInterfaceLogger().info("Window '{}' is now set to be {}.", windowTitle, visible ? "shown" : "hidden");

        internalFrame.setVisible(visible);

        if (visible)
            renderPassManager.start();
        else
            renderPassManager.stop();
    }

    private UIFocusable getAdjacentFocusable(boolean searchForward) {
        // Find next focusable element
        UIFocusable nextFocusable;
        if (content instanceof UIContainer container)
            nextFocusable = searchForward ?
                    container.getNextFocusable() :
                    container.getPreviousFocusable();
        else if (content instanceof UIFocusable focusable)
            nextFocusable = focusable;
        else
            nextFocusable = null;

        // If the next focusable is null, find the first focusable
        if (nextFocusable == null) {
            // If the content is a container, find its first focusable
            if (content instanceof UIContainer container)
                nextFocusable = searchForward ?
                        container.getFirstFocusable() :
                        container.getLastFocusable();
            // If the content is focusable, focus on it
            else if (content instanceof UIFocusable focusable)
                nextFocusable = focusable;
        }

        return nextFocusable;
    }

    public void focusNext(boolean keyboardTriggered) {
        // Find the next focusable element searching from the front
        focus(getAdjacentFocusable(true), keyboardTriggered);
    }

    public void focusPrevious(boolean keyboardTriggered) {
        // Find the next focusable element searching from the back
        focus(getAdjacentFocusable(false), keyboardTriggered);
    }

    public void focus(UIFocusable focusable, boolean keyboardTriggered) {
        // Set the focusable element in the input listener
        internalPanel.inputListener.setFocused(focusable, keyboardTriggered);
    }

    public boolean isFocused(UIFocusable focusable) {
        return internalPanel.inputListener.currentFocused == focusable;
    }
}
