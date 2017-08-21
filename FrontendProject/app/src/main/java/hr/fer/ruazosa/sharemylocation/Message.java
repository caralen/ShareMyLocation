package hr.fer.ruazosa.sharemylocation;

import java.util.Date;
import java.util.List;

/**
 * Created on 06.07.2017..
 */

public class Message {

    private long messageID;
    private long groupID;
    private long userID;
    private String messageTimestamp;
    private Double geogWidth;
    private Double geogLength;
    private String description;
    private int posReaction;
    private int negReaction;


    private String errorMessage;
    private List fieldValidationErrors;

    public List getFieldValidationErrors() {
        return fieldValidationErrors;
    }

    public void setFieldValidationErrors(List fieldValidationErrors) {
        this.fieldValidationErrors = fieldValidationErrors;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    public long getMessageID() {
        return messageID;
    }

    public void setMessageID(long messageID) {
        this.messageID = messageID;
    }

    public long getGroupID() {
        return groupID;
    }

    public void setGroupID(long groupID) {
        this.groupID = groupID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getMessageTimestamp() {
        return messageTimestamp;
    }

    public void setMessageTimestamp(String messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
    }

    public Double getGeogWidth() {
        return geogWidth;
    }

    public void setGeogWidth(Double geogWidth) {
        this.geogWidth = geogWidth;
    }

    public Double getGeogLength() {
        return geogLength;
    }

    public void setGeogLength(Double geogLength) {
        this.geogLength = geogLength;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPosReaction() {
        return posReaction;
    }

    public void setPosReaction(int posReaction) {
        this.posReaction = posReaction;
    }

    public int getNegReaction() {
        return negReaction;
    }

    public void setNegReaction(int negReaction) {
        this.negReaction = negReaction;
    }
}
