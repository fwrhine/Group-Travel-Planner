package com.example.pplki18.grouptravelplanner.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParser {
    public static void parse(String html) {
        Document doc = Jsoup.parse(html);
//            File input = new File("tripadvisor.html");
//            Document doc = Jsoup.parse(input, "UTF-8", "");

        Elements hotel_list = doc.select("div[class*='listItem']").select("div[class*='listing collapsed']");
        for (Element element: hotel_list) {
            Hotel hotel = new Hotel();

            // url and name
            Element hotel_title = element.selectFirst("a[class*='property_title']");
            String url = hotel_title.attr("href");
            String name = hotel_title.text();

            // rating
            Element hotel_rating = element.selectFirst("span[class*='rating']");
            String[] rating_long = hotel_rating.attr("alt").split(" ");
            String rating = rating_long[0];

            // price
            Element hotel_price = element.selectFirst("div[data-sizegroup*='mini-meta-price']");
            String price = hotel_price.text();

            // photo
            Element hotel_photo = element.selectFirst("a[class*='respListingPhoto']");
            String photo = hotel_photo.text();


            hotel.setHotel_id(url);
            hotel.setName(name);
            hotel.setRating(rating);
            hotel.setPrice(price);
            hotel.setPhoto(photo);

            System.out.println(hotel.getPhoto());
        }
    }
}
