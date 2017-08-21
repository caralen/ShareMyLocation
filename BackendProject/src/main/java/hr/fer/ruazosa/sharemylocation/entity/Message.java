package hr.fer.ruazosa.sharemylocation.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Created by antun on 30.06.17..
 */

@Entity
@Table(name = "poruka")
public class Message {

    @Id
    @SequenceGenerator(name = "pk_sequence", sequenceName = "poruka_porukaid_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    @Column(name = "porukaID", unique = true, nullable = false)
    private long messageID;

    @Column(name = "grupaID", nullable = false)
    private long groupID;

    @Column(name = "korisnikID", nullable = false)
    private long userID;

    @Column(name = "vrijemePoruke")
    private Date messageTimestamp;

    @Column(name = "geogSirina")
    private Double geogWidth;

    @Column(name = "geogDuzina")
    private Double geogLength;

    @Column(name = "opis")
    private String description;

    @Column(name = "pozReakcija")
    private int posReaction;

    @Column(name = "negReakcija")
    private int negReaction;

    public long getMessageID() {
        return messageID;
    }

    public void setMessageID(long messageID) {
        this.messageID = messageID;
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

    public Date getMessageTimestamp() {
        return messageTimestamp;
    }

    public void setMessageTimestamp(Date messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
    }

    public Double getGeogWidth() {
        return geogWidth;
    }

    public void setGeogWidth(Double geogWidth) {
        this.geogWidth = geogWidth;
    }

    public Double getGeogLength() {
        return geogLength;
    }

    public void setGeogLength(Double geogLength) {
        this.geogLength = geogLength;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPosReaction() {
        return posReaction;
    }

    public void setPosReaction(int posReaction) {
        this.posReaction = posReaction;
    }

    public int getNegReaction() {
        return negReaction;
    }

    public void setNegReaction(int negReaction) {
        this.negReaction = negReaction;
    }
}
