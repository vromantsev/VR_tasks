package dev.reed.core.streams.task1.entity;

public class OrderItem {

    private Product product;
    private Integer quantity;

    public OrderItem() {}

    public OrderItem(final Product product, final Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
