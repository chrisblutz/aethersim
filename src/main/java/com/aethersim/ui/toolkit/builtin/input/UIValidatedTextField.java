package com.aethersim.ui.toolkit.builtin.input;

import com.aethersim.ui.toolkit.builtin.listeners.OnChangeListener;

public class UIValidatedTextField extends UITextField {

    public UIValidatedTextField(String validationRegex, OnChangeListener<String> onTextChangeListener) {
        super(onTextChangeListener);

        setValidationRegex(validationRegex);
    }

    public UIValidatedTextField(String validationRegex, String placeholderText, OnChangeListener<String> onTextChangeListener) {
        super(placeholderText, onTextChangeListener);

        setValidationRegex(validationRegex);
    }

    @Override
    public String getValidationRegex() {
        return super.getValidationRegex();
    }

    @Override
    public void setValidationRegex(String validationRegex) {
        super.setValidationRegex(validationRegex);
    }
}
