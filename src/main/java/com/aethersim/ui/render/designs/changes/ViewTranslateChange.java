package com.aethersim.ui.render.designs.changes;

import com.aethersim.designs.Design;
import com.aethersim.ui.render.designs.DesignEditor;

public class ViewTranslateChange extends EditorChange {

    private final int translateX, translateY;

    public ViewTranslateChange(DesignEditor editor, Design design, int translateX, int translateY) {
        super(editor, design);

        this.translateX = translateX;
        this.translateY = translateY;
    }

    @Override
    public void doChange() {
        getEditor().translateX += translateX;
        getEditor().translateY += translateY;
    }

    @Override
    public void undoChange() {
        getEditor().translateX -= translateX;
        getEditor().translateY -= translateY;
    }
}
