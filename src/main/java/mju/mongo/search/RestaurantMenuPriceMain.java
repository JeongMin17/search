package mju.mongo.search;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface RestaurantMenuPriceMain extends MongoRepository<Restaurant_Menu_Price, String> {
    List<Restaurant_Menu_Price> findByMainprice(int price);

    @Query("{ 'id': ?0 }")
    List<Restaurant_Menu_Price> findByIdAsString(String searchID);
}