package za.co.mamamoney.ussdprocessor.persistence.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class ForeignCurrencyRates {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "base_currency")
    private String baseCurrency;

    @Column(name = "zar_to_kws")
    private BigDecimal zarToKwsRate;

    @Column(name = "zar_to_mwk")
    private BigDecimal zarToMwkRate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getZarToKwsRate() {
        return zarToKwsRate;
    }

    public void setZarToKwsRate(BigDecimal zarToKwsRate) {
        this.zarToKwsRate = zarToKwsRate;
    }

    public BigDecimal getZarToMwkRate() {
        return zarToMwkRate;
    }

    public void setZarToMwkRate(BigDecimal zarToMwkRate) {
        this.zarToMwkRate = zarToMwkRate;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }
}
