package hr.fer.ruazosa.sharemylocation.dao;

import hr.fer.ruazosa.sharemylocation.entity.Group;
import hr.fer.ruazosa.sharemylocation.entity.Message;
import hr.fer.ruazosa.sharemylocation.entity.User;
import hr.fer.ruazosa.sharemylocation.entity.UserGroup;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.pixsee.fcm.Notification;
import org.pixsee.fcm.Sender;


import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created on 16/06/17.
 */
@Repository
@Transactional
public class ShareMyLocationDAOImpl implements ShareMyLocationDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public User registerUser(User user) {
        if (getUser(user) != null) {
            return null;
        }
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.save(user);
        return getUser(user);
    }

    @Override
    public User getUser(User user) {
        Session currentSession = sessionFactory.getCurrentSession();

        Query theQuery = currentSession.createQuery("from User where userName = '" + user.getUserName() + "'");
        List<User> userList = theQuery.getResultList();
        if (userList == null || userList.isEmpty()) {
            return null;
        }
        return userList.get(0);
    }

    @Override
    public User getUserForUserID(long userID) {
        Session currentSession = sessionFactory.getCurrentSession();

        Query theQuery = currentSession.createQuery("from User where id = '" + userID + "'");
        List<User> userList = theQuery.getResultList();
        if (userList == null || userList.isEmpty()) {
            return null;
        }
        return userList.get(0);
    }


    @Override
    public User loginUser(String userName, String userPassword) {
        Session currentSession = sessionFactory.getCurrentSession();

        Query theQuery = currentSession.createQuery("from User where userName = '" + userName + "' AND password = '" + userPassword + "'");
        List<User> userList = theQuery.getResultList();
        if (userList == null || userList.isEmpty()) {
            return null;
        }
        return userList.get(0);
    }

    @Override
    public User updatePassword(String userName, String userPassword) {
        User user = new User();
        user.setUserName(userName);
        User sUser = this.getUser(user);
        if (sUser == null) {
            return null;
        } else {
            sUser.setPassword(userPassword);
            Session currentSession = sessionFactory.getCurrentSession();
            currentSession.saveOrUpdate(sUser);
            return getUser(sUser);

        }
    }

    @Override
    public User changeUser(User user) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query theQuery = currentSession.createQuery("from User where id = '" + user.getId() + "'");
        List<User> userList = theQuery.getResultList();
        User currentUser = userList.get(0);
        if (!user.getUserName().equals(currentUser.getUserName()) && getUser(user) != null) {
            return null;
        }
        currentUser.setUserName(user.getUserName());
        currentUser.setUserFirstName(user.getUserFirstName());
        currentUser.setUserLastName(user.getUserLastName());
        currentUser.setEmail(user.getEmail());
        currentUser.setTelephoneNumber(user.getTelephoneNumber());
        currentUser.setUserIcon(user.getUserIcon());
        currentSession.saveOrUpdate(currentUser);
        return getUser(currentUser);

    }

    @Override
    public User changePassword(long userID, String password) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query theQuery = currentSession.createQuery("from User where id = '" + userID + "'");
        List<User> userList = theQuery.getResultList();
        User currentUser = userList.get(0);
        currentUser.setPassword(password);
        currentSession.saveOrUpdate(currentUser);
        return currentUser;
    }

    @Override
    public Group createGroup(Group group) {
        if (getGroup(group) != null) {
            return null;
        }
        Session currentSession = sessionFactory.getCurrentSession();
        //group.setNoOfMembers(1);
        currentSession.save(group);
        UserGroup ug = new UserGroup();
        ug.setGroupID(group.getId());
        ug.setUserID(group.getGroupAdmin());
        registerUserGroup(ug);
        return getGroup(group);
    }

    @Override
    public Group getGroup(Group group) {
        Session currentSession = sessionFactory.getCurrentSession();

        Query theQuery = currentSession.createQuery("from Group where groupName = '" + group.getGroupName() + "'");
        List<Group> groupList = theQuery.getResultList();
        if (groupList == null || groupList.isEmpty()) {
            return null;
        }
        return groupList.get(0);
    }

    /*@Override
    public Group testGroup(String groupName) {
        Session currentSession = sessionFactory.getCurrentSession();

        Query theQuery = currentSession.createQuery("from Group where groupName = '" + groupName + "'");
        List<Group> groupList = theQuery.getResultList();
        if (groupList == null || groupList.isEmpty()) {
            return null;
        }

        return groupList.get(0);
    }*/

    @Override
    public List<Group> getAllGroups() {
        Session currentSession = sessionFactory.getCurrentSession();

        Query theQuery = currentSession.createQuery("from Group");
        List<Group> groupList = theQuery.getResultList();
        if (groupList == null || groupList.isEmpty()) {
            return null;
        }
        return groupList;
    }

    /*@Override
    public List<User> getAllUsers() {
        Session currentSession = sessionFactory.getCurrentSession();

        Query theQuery = currentSession.createQuery("from User");
        List<User> userList = theQuery.getResultList();
        if (userList == null || userList.isEmpty()) {
            return null;
        }
        return userList;
    }*/

    @Override
    public List<Group> getUserGroups(long userID) {
        Session currentSession = sessionFactory.getCurrentSession();
        List<UserGroup> userGroupsList = getUserGroupsForID(userID);
        if (userGroupsList == null)
            return null;
        List<Group> groupList = new ArrayList<>();
        for (UserGroup ug : userGroupsList) {
            Query theQuery = currentSession.createQuery("from Group where id = " + ug.getGroupID());
            groupList.addAll(theQuery.getResultList());
        }
        if (groupList == null || groupList.isEmpty()) {
            return null;
        }
        return groupList;
    }

    @Override
    public List<Group> getOtherGroups(long userID) {
        Session currentSession = sessionFactory.getCurrentSession();
        List<UserGroup> userGroupsList = getUserGroupsForID(userID);
        List<Group> otherGroupsList = getAllGroups();
        if (userGroupsList == null)
            return otherGroupsList;
        Query theQuery;
        List<Group> groupList;
        String groupIDArray = "";

        for (UserGroup ug : userGroupsList) {
            groupIDArray += ug.getGroupID() + ",";
        }
        groupIDArray = groupIDArray.substring(0, groupIDArray.length()-1);
        //groupIDArray = "(" + groupIDArray + ")";
        theQuery = currentSession.createQuery("from Group where id not in (" + groupIDArray + ")");
        otherGroupsList = theQuery.getResultList();

        return otherGroupsList;
    }

    @Override
    public List<Group> removeInvalidGroups() {
        Session currentSession = sessionFactory.getCurrentSession();
        Query theQuery;
        List<Group> groupList;

        theQuery = currentSession.createQuery("from Group where icon is null or length(icon) < 20");
        groupList = theQuery.getResultList();
        for(Group group : groupList) {
            currentSession.remove(group);
        }
        return groupList;
    }

    /*@Override
    public List<UserGroup> getAllUserGroups() {
        Session currentSession = sessionFactory.getCurrentSession();

        Query theQuery = currentSession.createQuery("from UserGroup");
        List<UserGroup> userGroupList = theQuery.getResultList();
        if (userGroupList == null || userGroupList.isEmpty()) {
            return null;
        }
        return userGroupList;
    }*/

    @Override
    public UserGroup registerUserGroup(UserGroup userGroup) {
        if (getUserGroup(userGroup.getGroupID(), userGroup.getUserID()) != null) {
            return null;
        }
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.save(userGroup);
        Query theQuery = currentSession.createQuery("from Group where id = " + userGroup.getGroupID());
        List<Group> userGroupList = theQuery.getResultList();
        Group group = userGroupList.get(0);
        group.setNoOfMembers(group.getNoOfMembers() + 1);
        return getUserGroup(userGroup.getGroupID(), userGroup.getUserID());
    }

    @Override
    public UserGroup getUserGroup(long groupID, long userID) {
        Session currentSession = sessionFactory.getCurrentSession();

        Query theQuery = currentSession.createQuery("from UserGroup where  groupID = " + groupID +
                " AND  userID = " + userID);
        List<UserGroup> userGroupList = theQuery.getResultList();
        if (userGroupList == null || userGroupList.isEmpty()) {
            return null;
        }
        return userGroupList.get(0);
    }

    @Override
    public List<UserGroup> getUserGroupsForID(long userID) {
        Session currentSession = sessionFactory.getCurrentSession();

        Query theQuery = currentSession.createQuery("from UserGroup where userID = " + userID);
        List<UserGroup> userGroupList = theQuery.getResultList();
        if (userGroupList == null || userGroupList.isEmpty()) {
            return null;
        }
        return userGroupList;
    }

    @Override
    public UserGroup removeFromGroup(long groupID, long userID) {
        Session currentSession = sessionFactory.getCurrentSession();

        Query theQuery = currentSession.createQuery("from UserGroup where userID = " + userID +
                " AND groupID = " + groupID);
        List<UserGroup> userGroupList = theQuery.getResultList();
        if (userGroupList == null || userGroupList.isEmpty()) {
            return null;
        }
        currentSession.remove(userGroupList.get(0));
        theQuery = currentSession.createQuery("from Group where id = " + userGroupList.get(0).getGroupID());
        List<Group> groupList = theQuery.getResultList();
        Group group = groupList.get(0);
        group.setNoOfMembers(group.getNoOfMembers() - 1);
        return userGroupList.get(0);
    }

    @Override
    public Message addMessageToGroup(Message message) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.save(message);
        Message savedMessage = getMessage(message.getGroupID(), message.getUserID(), message.getMessageTimestamp());
        sendMessageToFCM(message);
        return savedMessage;
    }

    private void sendMessageToFCM(Message message) {
        long groupID = message.getGroupID();

        Session currentSession = sessionFactory.getCurrentSession();
        Query theQuery = currentSession.createQuery("from User as usr where usr.id in " +
                "(select ugrp.userID from UserGroup as ugrp where ugrp.groupID = " + groupID +")");
        List<User> listOfUsers = theQuery.getResultList();

        Sender fcmSender = new Sender("AAAAaAI9kq0:APA91bGZp1RJvBfAbJTXhQPrRQnKfeujkiZGPp4kUh1vdvLZsd7lrJlsxckoGMr" +
                "0W0kkz5IYbkzh6ZgUcaLXh4XvmuxARbgKES0IHOsqJ0Qto8aefxy8osvNMKxPhv_qz_uximDDGe6_");



        List<String> registrationIds = new ArrayList<>();
        for (User users: listOfUsers) {
            registrationIds.add(users.getUserToken());
        }
        Notification notification = new Notification();
        notification.setSound("default");
        notification.setTitle("ShareMyLocation");
        notification.setBody(message.getDescription());

        org.pixsee.fcm.Message fcmMessage = new org.pixsee.fcm.Message.MessageBuilder()
                .addRegistrationToken(registrationIds) // add array
                .notification(new Notification(message.getDescription()))
                .build();

        fcmSender.send(fcmMessage);
    }

    @Override
    public Message getMessage(long groupID, long userID, Date timestamp) {
        Session currentSession = sessionFactory.getCurrentSession();

        Query theQuery = currentSession.createQuery("from Message where  groupID = " + groupID +
                " AND  userID = " + userID +
                " AND  messageTimestamp = '" + timestamp + "'");
        List<Message> messageList = theQuery.getResultList();
        if (messageList == null || messageList.isEmpty()) {
            return null;
        }
        return messageList.get(0);
    }

    @Override
    public List<Message> getAllMessages() {
        Session currentSession = sessionFactory.getCurrentSession();

        Query theQuery = currentSession.createQuery("from Message");
        List<Message> messageList = theQuery.getResultList();
        if (messageList == null || messageList.isEmpty()) {
            return null;
        }
        return messageList;
    }

    @Override
    public List<Message> getAllGroupMessages(long groupID) {
        Session currentSession = sessionFactory.getCurrentSession();

        Query theQuery = currentSession.createQuery("from Message where groupID = " + groupID);
        List<Message> messageList = theQuery.getResultList();
        if (messageList == null || messageList.isEmpty()) {
            return null;
        }
        return messageList;
    }
}
