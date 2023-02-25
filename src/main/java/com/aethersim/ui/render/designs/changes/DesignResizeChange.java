package com.aethersim.ui.render.designs.changes;

import com.aethersim.designs.Design;
import com.aethersim.ui.render.designs.DesignEditor;

public class DesignResizeChange extends EditorChange {

    private final int oldWidth, oldHeight;
    private final int newWidth, newHeight;
    private final boolean shiftChipsX, shiftChipsY;

    public DesignResizeChange(DesignEditor editor, Design design, int oldWidth, int oldHeight, int newWidth, int newHeight, boolean shiftChipsX, boolean shiftChipsY) {
        super(editor, design);

        this.oldWidth = oldWidth;
        this.oldHeight = oldHeight;
        this.newWidth = newWidth;
        this.newHeight = newHeight;
        this.shiftChipsX = shiftChipsX;
        this.shiftChipsY = shiftChipsY;
    }

    @Override
    public void doChange() {
        // Set the dimensions of the design to the new size
        getDesign().resize(newWidth, newHeight, shiftChipsX, shiftChipsY);
        getEditor().renderer.generate(getDesign());
    }

    @Override
    public void undoChange() {
        // Set the dimensions of the design back to the old size
        getDesign().resize(oldWidth, oldHeight, shiftChipsX, shiftChipsY);
        getEditor().renderer.generate(getDesign());
    }
}
