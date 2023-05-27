package mju.mongo.search;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

//public interface RestaurantMenu extends MongoRepository<Restaurant_Menu, String> {
//  List<Restaurant_Menu> findByNameContaining(String keyword);
//}

public interface RestaurantMenu extends MongoRepository<Restaurant_Menu, String> {
    @Query("{ 'id': ?0 }")
    List<Restaurant_Menu> findByIdAsString(String id);

    @Query("{ 'name': { '$regex': '?0', '$options': 'i' } }")
    List<Restaurant_Menu> findByNameContainingIgnoreCase(String name);
    List<Restaurant_Menu> findByNameContainingOrMainmenuContainingOrSidemenuContainingOrFoodtypeContaining(String name, String mainmenu, String sidemenu, String foodtype);
}