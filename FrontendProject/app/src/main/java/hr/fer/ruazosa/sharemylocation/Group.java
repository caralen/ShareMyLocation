package hr.fer.ruazosa.sharemylocation;
import java.io.Serializable;
import java.util.List;

/**
 * Created on 21.06.17..
 */

public class Group implements Serializable{

    private long id;
    private String groupName;
    private long groupAdmin;
    private Integer noOfMembers;
    private String icon;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getGroupAdmin() {
        return groupAdmin;
    }

    public void setGroupAdmin(long groupAdmin) {
        this.groupAdmin = groupAdmin;
    }

    public int getNoOfMembers() {
        return noOfMembers;
    }

    public void setNoOfMembers(int noOfMembers) {
        this.noOfMembers = noOfMembers;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
