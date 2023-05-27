package mju.mongo.search;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

//public interface RestaurantRepository extends MongoRepository<Restaurant_Information, String> {
  //  List<Restaurant_Information> findByNameContaining(String keyword);
//}

public interface RestaurantRepository extends MongoRepository<Restaurant_Information, String> {

    @Query("{ 'id': ?0 }")
    List<Restaurant_Information> findByIdAsString(String id);

    @Query("{ 'name': { '$regex': '?0', '$options': 'i' } }")
    List<Restaurant_Information> findByNameContainingIgnoreCase(String name);
    List<Restaurant_Information> findByNameContainingOrLocationContainingOrTimeContaining(String name, String location, String time, String number);
}