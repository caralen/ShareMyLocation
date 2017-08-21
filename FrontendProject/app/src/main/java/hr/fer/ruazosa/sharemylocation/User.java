package hr.fer.ruazosa.sharemylocation;

import java.io.Serializable;
import java.util.List;

/**
 * Created on 18.6.2017..
 */

public class User  implements Serializable{

    private long id;
    private String userName;
    private String password;
    private String sex;
    private String userFirstName;
    private String userLastName;
    private String telephoneNumber;
    private String userIcon;
    private String email;
    private String userToken;

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

    private String errorMessage;
    private List fieldValidationErrors;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object user) {
        if (user instanceof User) {
            User newUser = (User) user;
            return this.getUserName().equals(newUser.getUserName())
                    && this.getUserFirstName().equals(newUser.getUserFirstName())
                    && this.getUserLastName().equals(newUser.getUserLastName())
                    && this.getEmail().equals(newUser.getEmail())
                    && this.getTelephoneNumber().equals(newUser.getTelephoneNumber());
        }
        return false;
    }


}
