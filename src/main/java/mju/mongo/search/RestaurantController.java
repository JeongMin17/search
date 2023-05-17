package mju.mongo.search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private RestaurantMenu restaurantMenuRepository;

    @GetMapping("/searchRestaurant")
    public List<String> searchRestaurant(@RequestParam("searchValue") String searchValue) {
        // 코모란을 사용하여 명사 추출
        Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);
        KomoranResult komoranResult = komoran.analyze(searchValue);
        List<String> nouns = komoranResult.getNouns();

        Set<String> restaurantIds = new HashSet<>();

        // 추출한 명사들로 검색 수행
        for (String noun : nouns) {
            List<Restaurant_Information> restaurantInformationList = restaurantRepository.findByNameContainingOrLocationContainingOrTimeContaining(noun, noun, noun, noun);
            for (Restaurant_Information restaurantInformation : restaurantInformationList) {
                //restaurantIds.add(restaurantInformation.getName());
                if (!searchValue.contains("추천하지마")) {
                    restaurantIds.add(restaurantInformation.getName());
                }
            }

            List<Restaurant_Menu> restaurantMenuList = restaurantMenuRepository.findByNameContainingOrMenuContainingOrFoodtypeContaining(noun, noun, noun, noun);
            for (Restaurant_Menu restaurantMenu : restaurantMenuList) {
                //restaurantIds.add(restaurantMenu.getName());
                if (!searchValue.contains("추천하지마")) {
                    restaurantIds.add(restaurantMenu.getName());
                }
            }
        }

        return new ArrayList<>(restaurantIds);
    }
}