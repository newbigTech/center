package cn.htd.goodscenter.dto.vms;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 批量上架商品DTO
 */
public class BatchModifyPriceItemInDTO implements Serializable {
    @NotEmpty(message = "itemCode不能为NULL")
    private String itemCode;

    //销售价
    @NotNull(message = "销售价不能为空")
    @Pattern(regexp = "[0-9]+(.[0-9]{2})?", message = "销售价必须是小数位不超过两位的正数")
    private BigDecimal salePrice;

    //零售价
    @NotNull(message = "零售价不能为空")
    @Pattern(regexp = "[0-9]+(.[0-9]{2})?", message = "零售价必须是小数位不超过两位的正数")
    private BigDecimal retailPrice;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }
}
