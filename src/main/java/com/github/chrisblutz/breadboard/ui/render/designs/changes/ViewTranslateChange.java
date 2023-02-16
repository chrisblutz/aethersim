package com.github.chrisblutz.breadboard.ui.render.designs.changes;

import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.ui.render.designs.DesignEditor;

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
