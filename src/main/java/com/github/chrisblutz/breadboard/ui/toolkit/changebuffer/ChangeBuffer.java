package com.github.chrisblutz.breadboard.ui.toolkit.changebuffer;

import java.util.Stack;

public class ChangeBuffer<T extends Change> {

    private final Stack<Changeset<T>> pastChangesets = new Stack<>();
    private final Stack<Changeset<T>> futureChangesets = new Stack<>();

    public void doAndAppend(Changeset<T> changeset) {
        changeset.doForward();
        append(changeset);
    }

    public void append(Changeset<T> changeset) {
        // Add the newest change to the top of the past changes stack, then clear the future actions (since
        // the actions have branched off of where the previous "future" actions would be valid)
        pastChangesets.push(changeset);
        clearFutureBuffer();
    }

    public void undo() {
        // If we don't have any items left to undo, return early
        if (pastChangesets.isEmpty())
            return;

        // Pop the first past action, perform it in reverse, then add it to the future stack
        Changeset<T> current = pastChangesets.pop();
        current.doBackward();
        futureChangesets.push(current);
    }

    public void undoAll() {
        // Perform undo calls until the past change buffer is empty
        while (!pastChangesets.isEmpty())
            undo();
    }

    public void redo() {
        // If we don't have any items left to redo, return early
        if (futureChangesets.isEmpty())
            return;

        // Pop the first future action, perform it, then add it to the past stack
        Changeset<T> current = futureChangesets.pop();
        current.doForward();
        pastChangesets.push(current);
    }

    public void redoAll() {
        // Perform redo calls until the future change buffer is empty
        while (!futureChangesets.isEmpty())
            redo();
    }

    public void clearPastBuffer() {
        pastChangesets.clear();
    }

    public void clearFutureBuffer() {
        futureChangesets.clear();
    }

    public void clearBuffer() {
        clearPastBuffer();
        clearFutureBuffer();
    }
}
