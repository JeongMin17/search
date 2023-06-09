package mju.mongo.search;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RestaurantMenu extends MongoRepository<Restaurant_Menu, String> {
    List<Restaurant_Menu> findByNameContaining(String keyword);
}