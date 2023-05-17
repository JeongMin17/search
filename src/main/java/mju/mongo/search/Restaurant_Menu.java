package mju.mongo.search;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Arrays;

@Document(collection = "Restaurant_Menu")
public class Restaurant_Menu {
    @Id
    private String id;
    @Field("NAME")
    private String name;
    @Field("MENU")
    private String[] menu;
    @Field("PRICE")
    private String[] price;
    @Field("FOODTYPE")
    private String foodtype;

    // 생성자, 게터(getter), 세터(setter), toString() 등의 필요한 메서드를 추가합니다

    public Restaurant_Menu() {
        // 기본 생성자
    }

    public Restaurant_Menu(String name, String menu[], String price[], String foodtype) {
        this.name = name;
        this.menu = menu;
        this.price = price;
        this.foodtype = foodtype;
    }

    // getter, setter, toString 등의 메서드 추가

    // 예시로 getter/setter를 생성한 것입니다.
    // 실제 필요한 필드와 메서드를 추가/수정하십시오.

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getMenu() {
        return menu;
    }

    public void setMenu(String[] menu) {
        this.menu = menu;
    }

    public String[] getPrice() {
        return price;
    }

    public void setPrice(String[] price) {
        this.price = price;
    }

    public String getFoodtype() {
        return foodtype;
    }

    public void setFoodtype(String foodtype) {
        this.foodtype = foodtype;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location='" + menu + '\'' +
                ", time='" + price + '\'' +
                ", number='" + foodtype + '\'' +
                '}';
    }
}