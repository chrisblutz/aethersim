package com.aethersim.ui.toolkit.changebuffer;

import com.aethersim.ui.toolkit.exceptions.UIToolkitException;

/**
 * This class represents an ordered sequence of changes that, when combined, form a single action from the
 * perspective of the user.  For example, editing a text field may involve changing the text and moving the cursor.
 * These are technically separate actions, and likely will be implemented as their own {@link Change} classes,
 * but they operate as one "action", or changeset.
 * <p>
 * The {@code Changeset} object ensures that the changes are performed sequentially from first to last when "do"-ing
 * (like a "redo"), and from last to first when "undo"-ing.
 */
public class Changeset<T extends Change> {

    private final Change[] actions;

    public Changeset(Change... actions) {
        // Make sure all actions passed to this change are not null
        for (Change action : actions)
            if (action == null)
                throw new UIToolkitException("Changes in a changeset may not be null.");
        this.actions = actions;
    }

    public void doForward() {
        for (Change action : actions)
            action.doChange();
    }

    public void doBackward() {
        for (int index = actions.length - 1; index >= 0; index--)
            actions[index].undoChange();
    }
}
