package edu.yale.library.beans;

import java.util.Date;

/**
 * AuthorityControl
 */
public class AuthorityControl implements java.io.Serializable
{


    private Integer acid;
    private Date date;
    private int fdid;
    private String value;
    private String code;
    private int userId;

    public AuthorityControl()
    {
    }


    public AuthorityControl(Date date, int fdid, String value, int userId)
    {
        this.date = date;
        this.fdid = fdid;
        this.value = value;
        this.userId = userId;
    }

    public AuthorityControl(Date date, int fdid, String value, String code, int userId)
    {
        this.date = date;
        this.fdid = fdid;
        this.value = value;
        this.code = code;
        this.userId = userId;
    }

    public Integer getAcid()
    {
        return this.acid;
    }

    public void setAcid(Integer acid)
    {
        this.acid = acid;
    }

    public Date getDate()
    {
        return this.date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public int getFdid()
    {
        return this.fdid;
    }

    public void setFdid(int fdid)
    {
        this.fdid = fdid;
    }

    public String getValue()
    {
        return this.value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getCode()
    {
        return this.code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public int getUserId()
    {
        return this.userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }


}


