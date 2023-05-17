package mju.mongo.search;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface RestaurantMenuPriceMain extends MongoRepository<Restaurant_Menu_Price, String> {
    List<Restaurant_Menu_Price> findByMainprice(int price);
}