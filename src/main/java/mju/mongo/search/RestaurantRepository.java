package mju.mongo.search;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

//public interface RestaurantRepository extends MongoRepository<Restaurant_Information, String> {
  //  List<Restaurant_Information> findByNameContaining(String keyword);
//}

public interface RestaurantRepository extends MongoRepository<Restaurant_Information, String> {
    List<Restaurant_Information> findByNameContainingOrLocationContainingOrTimeContaining(String name, String location, String time, String number);
}