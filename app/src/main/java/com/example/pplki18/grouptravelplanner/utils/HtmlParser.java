package com.example.pplki18.grouptravelplanner.utils;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class HtmlParser {
    public static HtmlParseResult parse(String html) {
        HtmlParseResult result = new HtmlParseResult();
        Document doc = Jsoup.parse(html);
//            File input = new File("tripadvisor.html");
//            Document doc = Jsoup.parse(input, "UTF-8", "");

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
            Element hotel_price = hotel_list.get(i).selectFirst("div[data-sizegroup*='mini-meta-price']");
            String price = hotel_price.text();

            // photo
            Element hotel_photo = hotel_list.get(i).selectFirst("a[class*='respListingPhoto']").selectFirst("div[class*='inner']");
            String[] photo_long = hotel_photo.attr("style").split("\\(");
            String photo = photo_long[1];
            photo = photo.substring(0, photo.length() - 2);

            hotel.setHotel_id(url);
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

    public static class HtmlParseResult {
        private ArrayList<Hotel> hotels;
        private String nextPage;

        public HtmlParseResult() {

        }

        public void setHotels(ArrayList<Hotel> hotels) {
            this.hotels = hotels;
        }

        public void setNextPage(String nextPage) {
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
