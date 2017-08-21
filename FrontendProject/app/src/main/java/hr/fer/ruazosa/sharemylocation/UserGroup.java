package hr.fer.ruazosa.sharemylocation;

/**
 * Created by Alen on 6.7.2017..
 */

public class UserGroup {

    private long userGroup;
    private long groupID;
    private long userID;

    private String errorMessage;



    public long getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(long userGroup) {
        this.userGroup = userGroup;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
