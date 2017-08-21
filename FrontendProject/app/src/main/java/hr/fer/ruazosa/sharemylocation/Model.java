package hr.fer.ruazosa.sharemylocation;

/**
 * Created on 6/26/2017.
 */

public class Model{

    private String groupIcon;
    private String groupName;
    private long groupID;


    private boolean isGroupHeader = false;


    public Model(String groupIcon, String groupName) {
        super();
        this.groupIcon = groupIcon;
        this.groupName = groupName;

    }

    public long getGroupID() {
        return groupID;
    }

    public void setGroupID(long groupID) {
        this.groupID = groupID;
    }

    public String getIcon(){
        return groupIcon;
    }

    public void setIcon(String icon){
        groupIcon=icon;
    }

    public String getName(){
        return groupName;
    }

    public void setGroupName(String name){
        groupName=name;
    }



}
