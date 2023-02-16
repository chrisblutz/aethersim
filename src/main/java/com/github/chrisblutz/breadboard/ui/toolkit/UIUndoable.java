package com.github.chrisblutz.breadboard.ui.toolkit;

import com.github.chrisblutz.breadboard.ui.toolkit.changebuffer.Change;
import com.github.chrisblutz.breadboard.ui.toolkit.changebuffer.ChangeBuffer;

public interface UIUndoable<T extends Change> {

    ChangeBuffer<T> getChangeBuffer();
}
