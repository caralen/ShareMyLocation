package hr.fer.ruazosa.sharemylocation.entity;

import java.util.List;

/**
 * Created on 16/06/17.
 */
public class ErrorStatus {
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List getFieldValidationErrors() {
        return fieldValidationErrors;
    }

    public void setFieldValidationErrors(List fieldValidationErrors) {
        this.fieldValidationErrors = fieldValidationErrors;
    }

    private String errorMessage;
    private List fieldValidationErrors;

}
