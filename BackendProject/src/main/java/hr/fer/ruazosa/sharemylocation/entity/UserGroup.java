package hr.fer.ruazosa.sharemylocation.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Created by antun on 30.06.17..
 */

@Entity
@Table(name="korisnikGrupa")
public class UserGroup {
    @Id
    @SequenceGenerator(name = "pk_sequence", sequenceName = "korisnikGrupa_korisnikGrupa_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    @Column(name = "korisnikGrupa", unique = true, nullable = false)
    private long userGroup;

    @Column(name = "grupaID", nullable = false)
    private long groupID;

    @Column(name = "korisnikID",nullable = false)
    private long userID;


    public long getUserGroup() {return userGroup;}

    public void setUserGroup(long userGroup) {
        userGroup = userGroup;
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
}
