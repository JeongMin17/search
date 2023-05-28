package mju.mongo.search;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "Restaurant_Information")
public class Restaurant_Information {
    @Id
    private String id;
    @Field("NAME")
    private String name;
    @Field("LOCATION")
    private String location;
    @Field("TIME")
    private String time;
    @Field("NUMBER")
    private String number;
    @Field("LAT")
    private Float lat;
    @Field("LNG")
    private Float lng;

    // 생성자, 게터(getter), 세터(setter), toString() 등의 필요한 메서드를 추가합니다

    public Restaurant_Information() {
        // 기본 생성자
    }

    public Restaurant_Information(String name, String location, String time, String number) {
        this.name = name;
        this.location = location;
        this.time = time;
        this.number = number;
    }

    // getter, setter, toString 등의 메서드 추가

    // 예시로 getter/setter를 생성한 것입니다.
    // 실제 필요한 필드와 메서드를 추가/수정하십시오.

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Float getLat() { return lat; }

    public void setLat(Float lat){
        this.lat = lat;
    }

    public Float getLng() { return lng; }

    public void setLng(Float lng){
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", time='" + time + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}