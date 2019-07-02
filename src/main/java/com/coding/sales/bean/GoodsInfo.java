package com.coding.sales.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.ListResourceBundle;

public class GoodsInfo implements Serializable{
    /*
    * 世园会五十国钱币册
        编号：001001
        单位：册
        价格：998.00元
        2019北京世园会纪念银章大全40g
        编号：001002
        单位：盒
        价格：1380.00
        可使用9折打折券 招财进宝
        编号：003001
        单位：条
        价格：1580.00
        可使用95折打折券 水晶之恋
        编号：003002
        单位：条
        价格：980.00
        参与满减：第3件半价，满3送1 中国经典钱币套装
        编号：002002
        单位：套
        价格：998.00
        参与满减：每满2000减30，每满1000减10

        守扩之羽比翼双飞4.8g
        编号：002001
        单位：条
        价格：1080.00
        参与满减：第3件半价，满3送1 可使用95折打折券
        中国银象棋12g
        编号：002003
        单位：套
        价格：698.00
        参与满减：每满3000元减350, 每满2000减30，每满1000减10 可使用9折打折券*/


    public GoodsInfo(String cName, String cCode, String cUnit, BigDecimal bPirce, BigDecimal bDiscount, boolean bDisCountForEvery, List<HashMap> listDisCountForEvery) {
        this.cName = cName;
        this.cCode = cCode;
        this.cUnit = cUnit;
        this.bPirce = bPirce;
        this.bDiscount = bDiscount;
        this.bDisCountForEvery = bDisCountForEvery;
        this.listDisCountForEvery = listDisCountForEvery;
    }

    private String cName ;
    private String cCode;
    private String cUnit;
    private BigDecimal bPirce;
    private BigDecimal bDiscount;
    private boolean bDisCountForEvery;
    private List<HashMap> listDisCountForEvery;

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getcCode() {
        return cCode;
    }

    public void setcCode(String cCode) {
        this.cCode = cCode;
    }

    public String getcUnit() {
        return cUnit;
    }

    public void setcUnit(String cUnit) {
        this.cUnit = cUnit;
    }

    public BigDecimal getbPirce() {
        return bPirce;
    }

    public void setbPirce(BigDecimal bPirce) {
        this.bPirce = bPirce;
    }

    public BigDecimal getbDiscount() {
        return bDiscount;
    }

    public void setbDiscount(BigDecimal bDiscount) {
        this.bDiscount = bDiscount;
    }

    public boolean isbDisCountForEvery() {
        return bDisCountForEvery;
    }

    public void setbDisCountForEvery(boolean bDisCountForEvery) {
        this.bDisCountForEvery = bDisCountForEvery;
    }

    public List<HashMap> getListDisCountForEvery() {
        return listDisCountForEvery;
    }

    public void setListDisCountForEvery(List<HashMap> listDisCountForEvery) {
        this.listDisCountForEvery = listDisCountForEvery;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GoodsInfo)) return false;

        GoodsInfo goodsInfo = (GoodsInfo) o;

        if (isbDisCountForEvery() != goodsInfo.isbDisCountForEvery()) return false;
        if (getcName() != null ? !getcName().equals(goodsInfo.getcName()) : goodsInfo.getcName() != null) return false;
        if (getcCode() != null ? !getcCode().equals(goodsInfo.getcCode()) : goodsInfo.getcCode() != null) return false;
        if (getcUnit() != null ? !getcUnit().equals(goodsInfo.getcUnit()) : goodsInfo.getcUnit() != null) return false;
        if (getbPirce() != null ? !getbPirce().equals(goodsInfo.getbPirce()) : goodsInfo.getbPirce() != null)
            return false;
        if (getbDiscount() != null ? !getbDiscount().equals(goodsInfo.getbDiscount()) : goodsInfo.getbDiscount() != null)
            return false;
        return getListDisCountForEvery() != null ? getListDisCountForEvery().equals(goodsInfo.getListDisCountForEvery()) : goodsInfo.getListDisCountForEvery() == null;
    }

    @Override
    public int hashCode() {
        int result = getcName() != null ? getcName().hashCode() : 0;
        result = 31 * result + (getcCode() != null ? getcCode().hashCode() : 0);
        result = 31 * result + (getcUnit() != null ? getcUnit().hashCode() : 0);
        result = 31 * result + (getbPirce() != null ? getbPirce().hashCode() : 0);
        result = 31 * result + (getbDiscount() != null ? getbDiscount().hashCode() : 0);
        result = 31 * result + (isbDisCountForEvery() ? 1 : 0);
        result = 31 * result + (getListDisCountForEvery() != null ? getListDisCountForEvery().hashCode() : 0);
        return result;
    }


}
