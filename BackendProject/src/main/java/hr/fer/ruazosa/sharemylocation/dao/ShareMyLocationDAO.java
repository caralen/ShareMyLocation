package hr.fer.ruazosa.sharemylocation.dao;

import hr.fer.ruazosa.sharemylocation.entity.Group;
import hr.fer.ruazosa.sharemylocation.entity.Message;
import hr.fer.ruazosa.sharemylocation.entity.User;
import hr.fer.ruazosa.sharemylocation.entity.UserGroup;

import java.util.Date;
import java.util.List;

/**
 * Created on 16/06/17.
 */
public interface ShareMyLocationDAO {

    User registerUser(User user);
    User getUser(User user);
    User getUserForUserID(long userID);
    User loginUser(String userName, String userPassword);
    User updatePassword(String userName, String userPassword);
    User changeUser(User user);
    User changePassword(long userID, String password);

    Group createGroup(Group group);
    Group getGroup(Group group);
    //Group testGroup(String groupName);
    List<Group> getAllGroups();
    //List<User> getAllUsers();
    List<Group> getUserGroups(long userID);
    List<Group> getOtherGroups(long userID);
    List<Group> removeInvalidGroups();

    //List<UserGroup> getAllUserGroups();
    UserGroup registerUserGroup(UserGroup userGroup);
    UserGroup getUserGroup(long groupID, long userID);
    List<UserGroup> getUserGroupsForID(long userID);
    UserGroup removeFromGroup(long groupID, long userID);

    Message addMessageToGroup (Message message);
    Message getMessage (long groupID, long userID, Date timestamp);
    List<Message> getAllMessages ();
    List<Message> getAllGroupMessages(long groupID);
}
