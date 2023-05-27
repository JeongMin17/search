package mju.mongo.search;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "Restaurant_Menu")
public class Restaurant_Menu_Price {
    @Id
    private String id;
    @Field("NAME")
    private String name;
    @Field("MAIN_PRICE")
    private List<Integer> mainprice;
    @Field("SIDE_PRICE")
    private List<Integer> sideprice;

    // 생성자, 게터(getter), 세터(setter), toString() 등의 필요한 메서드를 추가합니다

    public Restaurant_Menu_Price() {
        // 기본 생성자
    }

    public Restaurant_Menu_Price(String name, List<Integer> mainprice) {
        this.name = name;
        this.mainprice = mainprice;
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

    public List<Integer> getMainprice() {
        return mainprice;
    }

    public void setMainprice(List<Integer> mainprice) {
        this.mainprice = mainprice;
    }

    public List<Integer> getSideprice() {
        return sideprice;
    }

    public void setSideprice(List<Integer> sideprice) {
        this.sideprice = sideprice;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id='" + id + '\'' +
                "id='" + name + '\'' +
                ", mainprice='" + mainprice + '\'' +
                '}';
    }
}