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
    private int[] price;
    @Field("FOODTYPE")
    private String foodtype;

    // 생성자, 게터(getter), 세터(setter), toString() 등의 필요한 메서드를 추가합니다

    public Restaurant_Menu() {
        // 기본 생성자
    }

    public Restaurant_Menu(String name, String menu[], int[] price, String foodtype) {
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

    public int[] getPrice() {
        return price;
    }

    public void setPrice(int[] price) {
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

    public int findMenuByName(String keyword) {
        // 여기에서 실제 검색 및 결과를 반환하는 로직을 구현해야 합니다.
        // 예를 들어, MongoDB의 find 메소드를 사용하여 검색할 수 있습니다.
        // 실제로 사용하는 데이터베이스와 라이브러리에 따라 코드가 달라질 수 있습니다.

        // 예시적으로 Restaurant_Menu 객체를 생성하여 반환합니다.
        Restaurant_Menu menu = new Restaurant_Menu();
        int a = 0;
        menu.setName("등촌샤브칼국수 용인역북점");
        menu.setMenu(Arrays.asList("버섯칼국수", "소고기샤브", "들깨칼국수", "야채", "볶음밥", "사리", "공기밥", "메밀왕만두 5개", "메밀전병 2개").toArray(new String[0]));
        menu.setPrice(new int[] {10000, 10000, 10000, 4000, 2000, 1000, 1000, 5000, 5000});
        menu.setFoodtype("한식");

        String[] menuArray = menu.getMenu();
        int[] priceArray = menu.getPrice();

        int index = -1;
        for (int i = 0; i < menuArray.length; i++) {
            if (menuArray[i].equals(keyword)) {
                index = i;
                break;
            }
        }

        return priceArray[index];
    }
}