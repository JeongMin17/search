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
    public String searchRestaurant(@RequestParam("searchValue") String searchValue) {
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
                quantityThreshold = intValue * 10000;
                ;
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
                noSearch.add(SearchNoSpace[i - 1]);
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
        for (int i = 0; i < nouns.size(); i++) {
            String str = nouns.get(i);
            if (str.equals("집")) nouns.remove(i);
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
        if (!noSearchNouns.isEmpty()) {
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
        if (nouns.indexOf("초과") != -1 || nouns.indexOf("이상") != -1) {
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
        } else if (quantityThreshold != 0) {
            List<Restaurant_Menu_Price> restaurantMenuFoodtypeList = restaurantMenuFoodpriceMain.findByMainprice(quantityThresholdStr);
            for (Restaurant_Menu_Price restaurantMenuFoodtype : restaurantMenuFoodtypeList) {
                restaurantTypes.add(restaurantMenuFoodtype.getName());
            }
            restaurantIds.retainAll(restaurantTypes);
        }
        List<Map<String, String>> resultList = new ArrayList<>();
        List<Map<String, Float>> resultRate = new ArrayList<>();
        //List<Float> weight = new ArrayList<>();
        Map<String, List<Float>> infoWeight = new HashMap<>();

        for (String noun : restaurantIds) {
            List<Restaurant_Information> restaurantInformationList = restaurantRepository.findByNameContainingOrLocationContainingOrTimeContaining(noun, noun, noun, noun);
            for (Restaurant_Information restaurantInformation : restaurantInformationList) {
                Map<String, String> infoMap = new HashMap<>();
                Map<String, Float> infoRate = new HashMap<>();

                List<Float> weight = new ArrayList<>();

                infoMap.put("ID", restaurantInformation.getId());
                infoMap.put("NAME", restaurantInformation.getName());
                infoMap.put("LOCATION", restaurantInformation.getLocation());
                infoMap.put("TIME", restaurantInformation.getTime());
                infoMap.put("NUMBER", restaurantInformation.getNumber());
                infoRate.put("RATE", restaurantInformation.getRate());

                weight.add(restaurantInformation.getHealth());
                weight.add(restaurantInformation.getDate());
                weight.add(restaurantInformation.getMeeting());
                weight.add(restaurantInformation.getSingle());
                weight.add(restaurantInformation.getLowcost());

                infoWeight.put(restaurantInformation.getId(),weight);

                resultRate.add(infoRate);
                resultList.add(infoMap);
            }
        }

        List<Map<String, Object>> combinedList = new ArrayList<>();

// resultList와 resultRate의 크기가 같은지 확인
        if (resultList.size() == resultRate.size()) {
            int size = resultList.size();

            for (int i = 0; i < size; i++) {
                Map<String, String> resultMap = resultList.get(i);
                Map<String, Float> resultRateMap = resultRate.get(i);

                Map<String, Object> combinedMap = new HashMap<>();
                combinedMap.putAll(resultMap);
                combinedMap.putAll(resultRateMap);

                combinedList.add(combinedMap);
            }
        }

        //return combinedList;


        List<String> Health = Arrays.asList("건강","영양","위생","청결","채소","저칼로리","바프","건강한","위생적인","다이어트","깨끗한","채식","건강하게","웰빙","식단");
        List<String> date = Arrays.asList("데이트","인스타","감성","애인","연인","갬성","여자친구","남자친구","여친","남친","인스타그램","데이트장소","데이트코스","만남","인테리어","분위기");
        List<String> meeting = Arrays.asList("모임","개강파티","파티","소주","회식","가족","모여서","다같이","동료","가족모임","신나게","단체","단체석","생일","넓은");
        List<String> single = Arrays.asList("혼밥","조용한","조용히","혼자","간단히","홀로","간편히","간편하게","여유롭게","가볍게","고독","한끼","편한","혼자서","간단하게");
        List<String> lowcost = Arrays.asList("가성비","싸고","저렴","저렴한","싸게","물가","저렴하게","혜자","갓성비","효율적인","가격대비","대학생","낮은가격","부담없이","학생","거지");


        List<List<String>> lists = Arrays.asList(Health, date, meeting, single, lowcost);
        List<String> matchingLists = new ArrayList<>();


        for (List<String> list : lists) {
            Set<String> set = new HashSet<>(list);
            for (String term : SearchNoSpace) {
                if (set.contains(term)) {
                    matchingLists.add(list.toString());
                    break;
                }
            }
            for (String term : nouns) {
                if (set.contains(term)) {
                    matchingLists.add(list.toString());
                    break;
                }
            }
        }
        List<Integer> indexList = new ArrayList<>();
        if (!matchingLists.isEmpty()) {
            for (String listName : matchingLists) {
                if (listName.contains("건강")) {
                    indexList.add(0);
                } else if (listName.contains("데이트")) {
                    indexList.add(1);
                } else if (listName.contains("모임")) {
                    indexList.add(2);
                } else if (listName.contains("혼밥")) {
                    indexList.add(3);
                } else if (listName.contains("가성비")) {
                    indexList.add(4);
                }
            }
        }
        List<Integer> distinctList = new ArrayList<>(new HashSet<>(indexList));



        String maxId = null;
        float maxSum = Float.MIN_VALUE;

        for (Map.Entry<String, List<Float>> entry : infoWeight.entrySet()) {
            String id = entry.getKey();
            List<Float> values = entry.getValue();

            if(distinctList.size() == 1){
                float sum = values.get(distinctList.get(0));
                if (sum > maxSum) {
                    maxSum = sum;
                    maxId = id;
                }
            }
            else if (distinctList.size() == 2) {
                float sum = values.get(distinctList.get(0)) + values.get(distinctList.get(1));
                if (sum > maxSum) {
                    maxSum = sum;
                    maxId = id;
                }
            }
            else if (distinctList.size() == 3) {
                float sum = values.get(distinctList.get(0)) + values.get(distinctList.get(1))+ values.get(distinctList.get(2));
                if (sum > maxSum) {
                    maxSum = sum;
                    maxId = id;
                }
            }
            else if (distinctList.size() == 4) {
                float sum = values.get(distinctList.get(0)) + values.get(distinctList.get(1))+ values.get(distinctList.get(2))+ values.get(distinctList.get(3));
                if (sum > maxSum) {
                    maxSum = sum;
                    maxId = id;
                }
            }
        }

        return maxId;
        //return infoWeight.toString();



        //return resultList.toString();
        //return new ArrayList<>(restaurantIds);
        //return nouns;
    }

    @GetMapping("/RestaurantInfo")
    public Map<String, Object> searchRestaurantINFO(@RequestParam("searchID") String searchID) {
        List<Map<String, String>> resultList = new ArrayList<>();
        List<Restaurant_Information> restaurantInformationList = restaurantRepository.findByIdAsString(searchID);
        List<Restaurant_Menu> restaurantMenuList = restaurantMenuRepository.findByIdAsString(searchID);
        List<Restaurant_Menu_Price> restaurantMenuPriceList = restaurantMenuFoodpriceMain.findByIdAsString(searchID);
        Map<String, String> infoMap = new HashMap<>();
        Map<String, Float> infoMaplatlng = new HashMap<>();
        Map<String, Float> infoRate = new HashMap<>();
        Map<String, String[]> infoMenu = new HashMap<>();
        Map<String, List<Integer>> infoMenuPrice = new HashMap<>();
        for (Restaurant_Information restaurantInformation : restaurantInformationList) {
            infoMaplatlng.put("lat", restaurantInformation.getLat());
            infoMaplatlng.put("lng", restaurantInformation.getLng());
            infoRate.put("RATE", restaurantInformation.getRate());
        }
        for (Restaurant_Information restaurantInformation : restaurantInformationList) {
            infoMap.put("ID", restaurantInformation.getId());
            infoMap.put("NAME", restaurantInformation.getName());
            infoMap.put("LOCATION", restaurantInformation.getLocation());
            infoMap.put("TIME", restaurantInformation.getTime());
            infoMap.put("NUMBER", restaurantInformation.getNumber());
        }
        for (Restaurant_Menu restaurantInformation : restaurantMenuList) {
            infoMenu.put("MAINMENU", restaurantInformation.getMainMenu());
            infoMenu.put("SIDEMENU", restaurantInformation.getSideMenu());
        }
        for (Restaurant_Menu_Price restaurantInformation : restaurantMenuPriceList) {
            infoMenuPrice.put("MAINPRICE", restaurantInformation.getMainprice());
            infoMenuPrice.put("SIDEPRICE", restaurantInformation.getSideprice());
        }
        //for (Restaurant_Menu restaurantInformation : restaurantMenuList) {
        //    infoMap.put("MAINMENU", Collections.singletonList(String.join(", ", restaurantInformation.getMainMenu())).toString());
        //    infoMap.put("SIDEMENU", Collections.singletonList(String.join(", ", restaurantInformation.getSideMenu())).toString());
        //}
        //for (Restaurant_Menu_Price restaurantInformation : restaurantMenuPriceList) {
        //    List<Integer> mainprice = restaurantInformation.getMainprice();
        //    infoMap.put("MAINPRICE", mainprice.toString());
        //    List<Integer> sideprice = restaurantInformation.getSideprice();
        //    infoMap.put("SIDEPRICE", sideprice.toString());
        //}
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("info", infoMap);
        resultMap.put("infoMap", infoMaplatlng);
        resultMap.put("infoMenu", infoMenu);
        resultMap.put("infoMenuPrice", infoMenuPrice);
        resultMap.put("infoRate", infoRate);
        return resultMap;
    }
}