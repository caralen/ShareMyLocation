package hr.fer.ruazosa.sharemylocation.entity;

import javax.persistence.*;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

/**
 * Created on 16/06/17.
 */

@Entity
@Table(name="korisnik")
public class User {

    @Id
    @SequenceGenerator(name="pk_sequence",sequenceName="korisnik_korisnikid_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="pk_sequence")
    @Column(name="korisnikid", unique=true, nullable=false)
    private long id;

    @Column(name="korisnickoime")
    @Size(min=3, max=20)
    private String userName;

    @Size(min=3, max=100)
    @Column(name="lozinka")
    private String password;

    @Column(name="spol")
    private String sex;

    @Column(name="ime")
    private String userFirstName;

    @Column(name="prezime")
    private String userLastName;

    @Column(name="brtelefona")
    private String telephoneNumber;

    @Column(name="email")
    @Size(min=5, max=50)
    private String email;

    @Column(name="ikona")
    private String userIcon;

    @Column(name="token")
    private String userToken;

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

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

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

}
