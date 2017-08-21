package hr.fer.ruazosa.sharemylocation.entity;

/**
 * Created by Nika on 6/25/2017.
 */

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "grupa")
public class Group {

    @Id
    @SequenceGenerator(name = "pk_sequence", sequenceName = "grupa_grupaid_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    @Column(name = "grupaID", unique = true, nullable = false)
    private long id;

    @Column(name = "imeGrupa")
    @Size(min = 3, max = 20)
    private String groupName;

    @Column(name = "adminGrupa")
    private long groupAdmin;

    @Column(name = "brClanova")
    private int noOfMembers;

    @Column(name = "ikona")
    private String icon;


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

    public int getNoOfMembers() { return noOfMembers; }

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
