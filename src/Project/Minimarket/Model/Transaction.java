package Project.Minimarket.Model;

public class Transaction {
    private String id_transaction, id_product;
    private int id_account, amount;
    private double discount, total;

    // public Transaction(String id_transaction, String id_product, int id_account,
    // int amount, double discount,
    // double total) {
    // this.id_transaction = id_transaction;
    // this.id_product = id_product;
    // this.id_account = id_account;
    // this.amount = amount;
    // this.discount = discount;
    // this.total = total;
    // }

    public String getIDTransaction() {
        return id_transaction;
    }

    public void setIDTransaction(String id_transaction) {
        this.id_transaction = id_transaction;
    }

    public String getIDProduct() {
        return id_product;
    }

    public void setIDProduct(String id_product) {
        this.id_product = id_product;
    }

    public int getIDAccount() {
        return id_account;
    }

    public void setIDAccount(int id_account) {
        this.id_account = id_account;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
