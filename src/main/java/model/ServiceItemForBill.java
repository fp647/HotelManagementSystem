package model;

import java.math.BigDecimal;

public class ServiceItemForBill {
    private String serviceName;
    private BigDecimal servicePricePerUnit;
    private int quantity;
    private BigDecimal totalServiceCost;

    // Constructor
    public ServiceItemForBill(String serviceName, BigDecimal servicePricePerUnit, int quantity) {
        this.serviceName = serviceName;
        this.servicePricePerUnit = servicePricePerUnit;
        this.quantity = quantity;
        this.totalServiceCost = servicePricePerUnit.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters
    public String getServiceName() { return serviceName; }
    public BigDecimal getServicePricePerUnit() { return servicePricePerUnit; }
    public int getQuantity() { return quantity; }
    public BigDecimal getTotalServiceCost() { return totalServiceCost; }

    @Override
    public String toString() {
        return "ServiceItemForBill{" +
               "serviceName='" + serviceName + '\'' +
               ", servicePricePerUnit=" + servicePricePerUnit +
               ", quantity=" + quantity +
               ", totalServiceCost=" + totalServiceCost +
               '}';
    }
}