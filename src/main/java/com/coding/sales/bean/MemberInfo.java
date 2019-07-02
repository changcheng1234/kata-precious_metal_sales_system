package com.coding.sales.bean;

import java.io.Serializable;

public class MemberInfo implements Serializable{
    /*
    * 姓名,等级,卡号,积分
马丁,普卡,6236609999,9860
王立,金卡,6630009999,48860
李想,白金卡,8230009999,98860
张三,钻石卡,9230009999,198860*/

    public MemberInfo(String cName, String cLevel, String cCode, long iRank) {
        this.cName = cName;
        this.cLevel = cLevel;
        this.cCode = cCode;
        this.iRank = iRank;
    }

    private String cName ;
    private String cLevel ;
    private String cCode ;
    private long iRank;

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getcLevel() {
        return cLevel;
    }

    public void setcLevel(String cLevel) {
        this.cLevel = cLevel;
    }

    public String getcCode() {
        return cCode;
    }

    public void setcCode(String cCode) {
        this.cCode = cCode;
    }

    public long getiRank() {
        return iRank;
    }

    public void setiRank(long iRank) {
        this.iRank = iRank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberInfo)) return false;

        MemberInfo that = (MemberInfo) o;

        if (getiRank() != that.getiRank()) return false;
        if (getcName() != null ? !getcName().equals(that.getcName()) : that.getcName() != null) return false;
        if (getcLevel() != null ? !getcLevel().equals(that.getcLevel()) : that.getcLevel() != null) return false;
        return getcCode() != null ? getcCode().equals(that.getcCode()) : that.getcCode() == null;
    }

    @Override
    public int hashCode() {
        int result = getcName() != null ? getcName().hashCode() : 0;
        result = 31 * result + (getcLevel() != null ? getcLevel().hashCode() : 0);
        result = 31 * result + (getcCode() != null ? getcCode().hashCode() : 0);
        result = 31 * result + (int) (getiRank() ^ (getiRank() >>> 32));
        return result;
    }
}
