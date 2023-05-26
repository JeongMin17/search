package mju.mongo.search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private RestaurantMenu restaurantMenuRepository;

    @Autowired
    private RestaurantMenuPriceHigh restaurantMenuFoodpriceRepository;
    @Autowired
    private RestaurantMenuPriceLow restaurantMenuFoodpriceLow;
    @Autowired
    private RestaurantMenuPriceMain restaurantMenuFoodpriceMain;

    @GetMapping("/searchRestaurant")
    public List<String> searchRestaurant(@RequestParam("searchValue") String searchValue) {
        // 코모란을 사용하여 명사 추출
        Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);
        KomoranResult komoranResult = komoran.analyze(searchValue);
        List<String> nouns = komoranResult.getNouns();

        Set<String> restaurantIds = new HashSet<>();
        List<String> restaurantTypes = new ArrayList<>();


        //4자리 이상 숫자 뽑기
        Pattern pattern = Pattern.compile("\\b(\\d{4,})\\b"); // 4자리 이상 숫자 패턴
        Matcher matcher = pattern.matcher(searchValue);

        int quantityThreshold = 0;
        if (matcher.find()) {
            String match = matcher.group(1);
            quantityThreshold = Integer.parseInt(match);
        }


        //띄어쓰기 빼고 단어 추출
        String[] SearchNoSpace = searchValue.split(" ");
        List<String> noSearch = new ArrayList<>();


        //뺄 단어들 리스트
        List<String> noSearchNouns = new ArrayList<>();


        //천원과 만원으로 숫자 뽑기
        for (String value : SearchNoSpace) {
            if (value.matches("\\d천원")) {
                int intValue = Integer.parseInt(value.replace("천원", ""));
                noSearchNouns.add("천원");
                quantityThreshold = intValue * 1000;
            }
            if (value.matches("\\d만원")) {
                int intValue = Integer.parseInt(value.replace("만원", ""));
                noSearchNouns.add("만원");
                quantityThreshold = intValue * 10000;;
            }
        }

        //완전 똑같은 메뉴로 검색할 시 검색어 저장
        for (String noun : SearchNoSpace) {
            List<Restaurant_Information> restaurantInformationList = restaurantRepository.findByNameContainingOrLocationContainingOrTimeContaining(noun, noun, noun, noun);
            if (!restaurantInformationList.isEmpty()) {
                nouns.add(noun);
            }

            List<Restaurant_Menu> restaurantMenuList = restaurantMenuRepository.findByNameContainingOrMainmenuContainingOrSidemenuContainingOrFoodtypeContaining(noun, noun, noun, noun);
            if (!restaurantMenuList.isEmpty()) {
                nouns.add(noun);
            }
        }

        //예외상황 제외 (싫은 음식들)
        for (int i = 1; i < SearchNoSpace.length; i++) {
            if (SearchNoSpace[i].equals("말고") || SearchNoSpace[i].equals("제외하고") || SearchNoSpace[i].equals("제외") || SearchNoSpace[i].equals("싫어") || SearchNoSpace[i].equals("싫고") || SearchNoSpace[i].equals("못먹어") || SearchNoSpace[i].equals("못먹고") || SearchNoSpace[i].equals("빼고") || SearchNoSpace[i].equals("제외하고")) {
                noSearch.add(SearchNoSpace[i-1]);
            }
        }

        for (String word : noSearch) {
            List<Token> tokens = komoran.analyze(word).getTokenList();
            for (Token token : tokens) {
                if (token.getPos().startsWith("NNG")) {
                    noSearchNouns.add(token.getMorph());
                }
            }
        }
        //문장에서 단어들 제외하기
        //nouns.removeAll(noSearchNouns);




        //집이라는 단어 삭제
        for(int i=0;i<nouns.size();i++){
            String str = nouns.get(i);
            if(str.equals("집")) nouns.remove(i);
        }



        // 추출한 명사들로 검색 수행
        for (String noun : nouns) {
            List<Restaurant_Information> restaurantInformationList = restaurantRepository.findByNameContainingOrLocationContainingOrTimeContaining(noun, noun, noun, noun);
            for (Restaurant_Information restaurantInformation : restaurantInformationList) {
                restaurantIds.add(restaurantInformation.getName());
            }

            List<Restaurant_Menu> restaurantMenuList = restaurantMenuRepository.findByNameContainingOrMainmenuContainingOrSidemenuContainingOrFoodtypeContaining(noun, noun, noun, noun);
            for (Restaurant_Menu restaurantMenu : restaurantMenuList) {
                restaurantIds.add(restaurantMenu.getName());
            }
        }

        Set<String> norestaurant = new HashSet<>();
        //만약 제외할 단어가 있을 시에
        if(!noSearchNouns.isEmpty()) {
            for (String noun : noSearchNouns) {
                List<Restaurant_Information> restaurantInformationList = restaurantRepository.findByNameContainingOrLocationContainingOrTimeContaining(noun, noun, noun, noun);
                for (Restaurant_Information restaurantInformation : restaurantInformationList) {
                    norestaurant.add(restaurantInformation.getName());
                }

                List<Restaurant_Menu> restaurantMenuList = restaurantMenuRepository.findByNameContainingOrMainmenuContainingOrSidemenuContainingOrFoodtypeContaining(noun, noun, noun, noun);
                for (Restaurant_Menu restaurantMenu : restaurantMenuList) {
                    norestaurant.add(restaurantMenu.getName());
                }
            }
            restaurantIds.removeAll(norestaurant);
        }


        int quantityThresholdStr = Integer.parseInt(String.valueOf(quantityThreshold));

        //얼마 이상, 이하인 음식점 검색
        if(nouns.indexOf("초과") != -1 || nouns.indexOf("이상") != -1) {
            List<Restaurant_Menu_Price> restaurantMenuFoodtypeList = restaurantMenuFoodpriceRepository.findByMainpriceGreaterThanEqual(quantityThresholdStr);
            for (Restaurant_Menu_Price restaurantMenuFoodtype : restaurantMenuFoodtypeList) {
                restaurantTypes.add(restaurantMenuFoodtype.getName());
            }
            restaurantIds.retainAll(restaurantTypes);
        } else if (nouns.indexOf("미만") != -1 || nouns.indexOf("이하") != -1) {
            List<Restaurant_Menu_Price> restaurantMenuFoodtypeList = restaurantMenuFoodpriceLow.findByMainpriceLessThanEqual(quantityThresholdStr);
            for (Restaurant_Menu_Price restaurantMenuFoodtype : restaurantMenuFoodtypeList) {
                restaurantTypes.add(restaurantMenuFoodtype.getName());
            }
            restaurantIds.retainAll(restaurantTypes);
        }else if (quantityThreshold != 0) {
            List<Restaurant_Menu_Price> restaurantMenuFoodtypeList = restaurantMenuFoodpriceMain.findByMainprice(quantityThresholdStr);
            for (Restaurant_Menu_Price restaurantMenuFoodtype : restaurantMenuFoodtypeList) {
                restaurantTypes.add(restaurantMenuFoodtype.getName());

            }
            restaurantIds.retainAll(restaurantTypes);
        }


        return new ArrayList<>(restaurantIds);
        //return nouns;
    }
}