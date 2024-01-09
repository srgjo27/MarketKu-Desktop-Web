package Project.Minimarket.Model;

public class Product {
    private String id_product, product_name, id_category, unit;
    private int stock;
    private double price;

    // public Product(String id_product, String product_name, String id_category,
    // int stock, String unit, double price) {
    // this.id_product = id_product;
    // this.product_name = product_name;
    // this.id_category = id_category;
    // this.stock = stock;
    // this.unit = unit;
    // this.price = price;
    // }

    public void setIDProduct(String id_product) {
        this.id_product = id_product;
    }

    public String getIDProduct() {
        return id_product;
    }

    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    public String getProductName() {
        return product_name;
    }

    public void setIDCategory(String id_category) {
        this.id_category = id_category;
    }

    public String getIDCategory() {
        return id_category;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStock() {
        return stock;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
}
