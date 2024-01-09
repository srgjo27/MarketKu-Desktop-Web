package Project.Minimarket.Model;

public class Category {
    public String id_category, category_name;

    // public Category(String id_category, String category_name) {
    // this.id_category = id_category;
    // this.category_name = category_name;
    // }

    public void setIDCategory(String id_category) {
        this.id_category = id_category;
    }

    public String getIDCategory() {
        return id_category;
    }

    public void setCategoryName(String category_name) {
        this.category_name = category_name;
    }

    public String getCategoryName() {
        return category_name;
    }
}
