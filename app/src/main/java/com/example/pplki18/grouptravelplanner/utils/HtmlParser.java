package com.example.pplki18.grouptravelplanner.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

@SuppressWarnings("SpellCheckingInspection")
public class HtmlParser {
    @SuppressWarnings("SpellCheckingInspection")
    public static HtmlParseResult parseHotelList(String html) {
        HtmlParseResult result = new HtmlParseResult();
        Document doc = Jsoup.parse(html);
//            File input = new File("tripadvisor.html");
//            Document doc = Jsoup.parseHotelList(input, "UTF-8", "");

        Elements hotel_list = doc.select("div[class*='listItem']").select("div[class*='listing collapsed']");

        ArrayList<Hotel> hotels = new ArrayList<>();
        for (int i = 1; i < hotel_list.size(); i++) {
            Hotel hotel = new Hotel();

            // url and name
            Element hotel_title = hotel_list.get(i).selectFirst("a[class*='property_title']");
            String url = hotel_title.attr("href");
            String name = hotel_title.text();

            // rating
            Element hotel_rating = hotel_list.get(i).selectFirst("span[class*='rating']");
            String[] rating_long = hotel_rating.attr("alt").split(" ");
            String rating = rating_long[0];

            // price
            @SuppressWarnings("SpellCheckingInspection") Element hotel_price = hotel_list.get(i).selectFirst("div[data-sizegroup*='mini-meta-price']");
            String price = hotel_price.text();

            // photo
            Element hotel_photo = hotel_list.get(i).selectFirst("a[class*='respListingPhoto']").selectFirst("div[class*='inner']");
            String[] photo_long = hotel_photo.attr("style").split("\\(");
            String photo = photo_long[1];
            photo = photo.substring(0, photo.length() - 2);

            hotel.setHotel_id("https://www.tripadvisor.com" + url);
            hotel.setName(name);
            hotel.setRating(rating);
            hotel.setPrice(price);
            hotel.setPhoto(photo);

            hotels.add(hotel);
        }

        result.setHotels(hotels);

        Element nextPage = doc.selectFirst("a[class*='nav next']");

        if (nextPage != null) {
            String nextPageLink = nextPage.attr("href");
            result.setNextPage(nextPageLink);
        } else {
            result.setNextPage("");
        }

        return result;
    }

    public static Hotel parseHotel(String html) {
        Document doc = Jsoup.parse(html);

        Hotel hotel = new Hotel();
        Element hotel_name = doc.selectFirst("h1[class*='ui_header h1']");
        String name = hotel_name.text();

        Element hotel_rating = doc.selectFirst("span[class*='ui_bubble_rating bubble_50']");
        String[] rating_long = hotel_rating.attr("alt").split(" ");
        String rating = rating_long[0];

        Element hotel_price = doc.selectFirst("div[class*='bb_price_text']");
        String price = (hotel_price != null) ? hotel_price.text() : "";

        Element hotel_phone = doc.selectFirst("span[class*='is-hidden-mobile detail']");
        String phone = (hotel_phone != null) ? hotel_phone.text() : "";

        Element hotel_address = doc.selectFirst("span[class*='street-address']");
        String street_address = (hotel_address != null) ? hotel_address.text() : "";

        Element hotel_locality = doc.selectFirst("span[class*='locality']");
        String locality = (hotel_locality != null) ? hotel_locality.text() : "";

//        Element hotel_photo = doc.selectFirst("div[class*='mainImg']").selectFirst("img");
//        String photo = hotel_photo.attr("src");

        hotel.setName(name);
        hotel.setRating(rating);
        hotel.setPrice(price);
        hotel.setPhone_number(phone);
        hotel.setAddress(street_address + ", " + locality);
//        hotel.setPhoto(photo);

        System.out.println("hotel rating: " + hotel_rating.attr("alt"));
        return hotel;
    }

    public static class HtmlParseResult {
        private ArrayList<Hotel> hotels;
        private String nextPage;

        HtmlParseResult() {

        }

        void setHotels(ArrayList<Hotel> hotels) {
            this.hotels = hotels;
        }

        void setNextPage(String nextPage) {
            this.nextPage = nextPage;
        }

        public ArrayList<Hotel> getHotels() {
            return hotels;
        }

        public String getNextPage() {
            return nextPage;
        }



    }
}
