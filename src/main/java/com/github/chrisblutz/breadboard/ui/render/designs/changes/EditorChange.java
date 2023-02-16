package com.github.chrisblutz.breadboard.ui.render.designs.changes;

import com.github.chrisblutz.breadboard.designs.Design;
import com.github.chrisblutz.breadboard.ui.render.designs.DesignEditor;
import com.github.chrisblutz.breadboard.ui.toolkit.changebuffer.Change;

public abstract class EditorChange implements Change {

    private final DesignEditor editor;
    private final Design design;

    public EditorChange(DesignEditor editor, Design design) {
        this.editor = editor;
        this.design = design;
    }

    public DesignEditor getEditor() {
        return editor;
    }

    public Design getDesign() {
        return design;
    }
}
