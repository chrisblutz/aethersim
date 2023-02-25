package com.aethersim.ui.toolkit;

import com.aethersim.ui.toolkit.changebuffer.Change;
import com.aethersim.ui.toolkit.changebuffer.ChangeBuffer;

public interface UIUndoable<T extends Change> {

    ChangeBuffer<T> getChangeBuffer();
}
