package edu.yale.library.ladybird.entity;


import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ImportFile implements java.io.Serializable {
    private Integer id;
    private Integer importId;
    private Date date;
    private String fileLocation;
    private int oid;
    private int userId;
    private Integer code;
    private String error;
    private String type;
    private String label;

    public ImportFile() {
    }

    public Integer getImportId() {
        return importId;
    }

    public void setImportId(Integer importId) {
        this.importId = importId;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer ImportId() {
        return this.importId;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFileLocation() {
        return this.fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public int getOid() {
        return this.oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "ImportFile{"
                + "id=" + id
                + ", importId=" + importId
                + ", date=" + date
                + ", fileLocation='" + fileLocation + '\''
                + ", oid=" + oid
                + ", userId=" + userId
                + ", code=" + code
                + ", error='" + error + '\''
                + ", type='" + type + '\''
                + ", label='" + label + '\''
                + '}';
    }
}


