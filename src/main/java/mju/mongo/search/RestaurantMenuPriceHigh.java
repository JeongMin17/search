package mju.mongo.search;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

//public interface RestaurantMenu extends MongoRepository<Restaurant_Menu, String> {
//  List<Restaurant_Menu> findByNameContaining(String keyword);
//}

//ublic interface RestaurantMenuPrice extends MongoRepository<Restaurant_Menu_Price, String> {
//    List<Restaurant_Menu_Price> findByMainprice(List<Integer> mainprice);
//}

public interface RestaurantMenuPriceHigh extends MongoRepository<Restaurant_Menu_Price, String> {
    List<Restaurant_Menu_Price> findByMainpriceGreaterThanEqual(int price);
}