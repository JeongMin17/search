package mju.mongo.search;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    private final RestaurantRepository restaurantInformation;
    private final RestaurantMenu restaurantMenu;

    public RestaurantController(RestaurantRepository restaurantInformation, RestaurantMenu restaurantMenu) {
        this.restaurantInformation = restaurantInformation;
        this.restaurantMenu = restaurantMenu;
    }

    @GetMapping("/searchByKeyword")
    public List<Restaurant_Information> searchByKeyword(@RequestParam("keyword") String keyword) {
        return restaurantInformation.findByNameContaining(keyword);
    }
}