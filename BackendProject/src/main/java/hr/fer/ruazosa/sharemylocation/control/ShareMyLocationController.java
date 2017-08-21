package hr.fer.ruazosa.sharemylocation.control;

import hr.fer.ruazosa.sharemylocation.dao.ShareMyLocationDAO;
import hr.fer.ruazosa.sharemylocation.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created on 16/06/17.
 */

@RestController
public class ShareMyLocationController {

    @Autowired
    private ShareMyLocationDAO shareMyLocationDAO;


    @PostMapping(value = "/registerUser")
    public ResponseEntity registerUser(@Valid @RequestBody User user, BindingResult result) {

        if (result.hasErrors()) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("Field validation error");
            errorStatus.setFieldValidationErrors(result.getFieldErrors());
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }
        ;

        if (shareMyLocationDAO.registerUser(user) == null) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("Username already registered");
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }

        return new ResponseEntity(user, HttpStatus.OK);
    }

    @GetMapping(value = "/loginUser")
    public ResponseEntity loginUser(@RequestParam("userName") String userName,
                                    @RequestParam("userPassword") String userPassword,
                                    @RequestParam("token") Optional<String> token) {

        if (userName == null || userName.isEmpty()) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("userName must be specified in GET request");
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }
        if (userPassword == null || userPassword.isEmpty()) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("userPassword must be specified in GET request");
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }
        User user = shareMyLocationDAO.loginUser(userName, userPassword);

        if (user == null) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("Invalid username or password");
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }
        if (token.isPresent()) {
            user.setUserToken(token.get());
            shareMyLocationDAO.changeUser(user);
        }

        return new ResponseEntity(user, HttpStatus.OK);
    }

    @GetMapping(value = "/getUserForUserID")
    public ResponseEntity getUserForUserID(@RequestParam("id") long id) {

        User user = shareMyLocationDAO.getUserForUserID(id);

        if (user == null) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("Invalid username or password");
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }

        return new ResponseEntity(user, HttpStatus.OK);
    }

    @GetMapping("/resetPassword")
    public ResponseEntity resetPassword(@RequestParam("userName") String userName,
                                        @RequestParam("userPassword") String userPassword) {

        if (userName == null || userName.isEmpty()) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("userName must be specified in GET request");
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }
        if (userPassword == null || userPassword.isEmpty()) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("userPassword must be specified in GET request");
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }

        User bUser = shareMyLocationDAO.updatePassword(userName, userPassword);
        if (bUser == null) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("Invalid username or password");
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }

        return new ResponseEntity(bUser, HttpStatus.OK);


    }

    @PostMapping(value = "/changeUser")
    public ResponseEntity changeUser(@Valid @RequestBody User user, BindingResult result) {

        if (result.hasErrors()) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("Field validation error");
            errorStatus.setFieldValidationErrors(result.getFieldErrors());
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }
        ;
        User currentUser = shareMyLocationDAO.changeUser(user);


        return new ResponseEntity(currentUser, HttpStatus.OK);
    }

    @GetMapping(value = "/changePassword")
    public ResponseEntity changePassword(@RequestParam("userID") long userID,
                                         @RequestParam("password") String password) {

        if (password == null || password.isEmpty()) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("userPassword must be specified in GET request");
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }

        User currentUser = shareMyLocationDAO.changePassword(userID, password);

        return new ResponseEntity(currentUser, HttpStatus.OK);
    }



    @PostMapping(value = "/createGroup")
    public ResponseEntity createGroup(@Valid @RequestBody Group group, BindingResult result) {

        if (result.hasErrors()) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("Field validation error");
            errorStatus.setFieldValidationErrors(result.getFieldErrors());
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }
        ;

        if (shareMyLocationDAO.createGroup(group) == null) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("Group name already in use. Try another one.");
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }

        return new ResponseEntity(group, HttpStatus.OK);
    }

    /*@GetMapping(value = "/testGroup")
    public ResponseEntity testGroup(@RequestParam("groupName") String groupName) {

        if (groupName == null || groupName.isEmpty()) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("userName must be specified in GET request");
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }

        Group bGroup = shareMyLocationDAO.testGroup(groupName);

        if (bGroup == null) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("Invalid username or password");
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }

        return new ResponseEntity(bGroup, HttpStatus.OK);
    }*/

    @GetMapping(value = "/getAllGroups")
    public ResponseEntity getAllGroups() {


        List<Group> allGroupsList = shareMyLocationDAO.getAllGroups();

        if (allGroupsList == null) {
            ErrorStatus errorStatus = new ErrorStatus();
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }

        return new ResponseEntity(allGroupsList, HttpStatus.OK);
    }

    @GetMapping(value = "/getOtherGroups")
    public ResponseEntity getOtherGroups(@RequestParam("userID") long userID) {


        List<Group> otherGroupsList = shareMyLocationDAO.getOtherGroups(userID);

        if (otherGroupsList == null) {
            ErrorStatus errorStatus = new ErrorStatus();
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }

        return new ResponseEntity(otherGroupsList, HttpStatus.OK);
    }

    /*@GetMapping(value = "/getAllUsers")
    public ResponseEntity getAllUsers() {


        List<User> allUsersList = shareMyLocationDAO.getAllUsers();

        if (allUsersList == null) {
            ErrorStatus errorStatus = new ErrorStatus();
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }

        return new ResponseEntity(allUsersList, HttpStatus.OK);
    }*/
	
	@GetMapping(value = "/getUserGroups")
	public ResponseEntity getUserGroups(@RequestParam("userID") long userID) {
		
		List<Group> allUserGroups = shareMyLocationDAO.getUserGroups(userID);
		
		if (allUserGroups == null) {
            ErrorStatus errorStatus = new ErrorStatus();
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }

        return new ResponseEntity(allUserGroups, HttpStatus.OK);
	}

    /*@GetMapping(value = "/getAllUserGroups")
    public ResponseEntity getAllUserGroups() {


        List<UserGroup> allUsersGroupsList = shareMyLocationDAO.getAllUserGroups();

        if (allUsersGroupsList == null) {
            ErrorStatus errorStatus = new ErrorStatus();
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }

        return new ResponseEntity(allUsersGroupsList, HttpStatus.OK);
    }*/

    @PostMapping(value = "/registerUserGroup")
    public ResponseEntity registerUserGroup(@Valid @RequestBody UserGroup userGroup, BindingResult result) {

        if (result.hasErrors()) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("Field validation error");
            errorStatus.setFieldValidationErrors(result.getFieldErrors());
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }
        ;

        if (shareMyLocationDAO.registerUserGroup(userGroup) == null) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("The user is already in that group!");
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }

        return new ResponseEntity(userGroup, HttpStatus.OK);
    }

    @GetMapping(value = "/getUserGroup")
    public ResponseEntity getUserGroup(@RequestParam("userGroup") long userGroup,
                                       @RequestParam("groupID") long groupID,
                                       @RequestParam("userID") long userID) {

        UserGroup thisUserGroup = shareMyLocationDAO.getUserGroup(groupID, userID);

        if (thisUserGroup == null) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("UserGroup does not exist!");
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }

        return new ResponseEntity(userGroup, HttpStatus.OK);
    }

    @GetMapping(value = "/getUserGroupsForID")
    public ResponseEntity getUserGroupsForID(@RequestParam("userID") long userID) {

        List<UserGroup> userGroups = shareMyLocationDAO.getUserGroupsForID(userID);

        if (userGroups == null) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("");
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }

        return new ResponseEntity(userGroups, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity removeFromGroup(@RequestParam("groupID") long groupID,
                                          @RequestParam("userID") long userID)
    {
        UserGroup userGroup = shareMyLocationDAO.removeFromGroup(groupID, userID);

        if (userGroup == null) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("User not found in the given group!");
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }
        return new ResponseEntity(userGroup, HttpStatus.OK);

    }

    @PostMapping(value = "/addMessageToGroup")
    public ResponseEntity addMessageToGroup(@Valid @RequestBody Message message, BindingResult result) {

        if (result.hasErrors()) {
            ErrorStatus errorStatus = new ErrorStatus();
            errorStatus.setErrorMessage("Field validation error");
            errorStatus.setFieldValidationErrors(result.getFieldErrors());
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }


        shareMyLocationDAO.addMessageToGroup(message);

        return new ResponseEntity(message, HttpStatus.OK);
    }


    @GetMapping(value = "/getAllMessages")
    public ResponseEntity getAllMessages() {


        List<Message> allMessagesList = shareMyLocationDAO.getAllMessages();

        if (allMessagesList == null) {
            ErrorStatus errorStatus = new ErrorStatus();
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }

        return new ResponseEntity(allMessagesList, HttpStatus.OK);
    }

    @GetMapping(value = "/getAllGroupMessages")
    public ResponseEntity getAllGroups(@RequestParam("groupID") long groupID) {


        List<Message> groupMessagesList = shareMyLocationDAO.getAllGroupMessages(groupID);

        if (groupMessagesList == null) {
            return new ResponseEntity(new ArrayList<Message>(), HttpStatus.OK);
        }

        return new ResponseEntity(groupMessagesList, HttpStatus.OK);
    }

    @GetMapping(value = "/removeInvalidGroups")
    public ResponseEntity removeInvalidGroups() {


        List<Group> groupList = shareMyLocationDAO.removeInvalidGroups();

        if (groupList == null) {
            ErrorStatus errorStatus = new ErrorStatus();
            return new ResponseEntity(errorStatus, HttpStatus.OK);
        }

        return new ResponseEntity(groupList, HttpStatus.OK);
    }

}
