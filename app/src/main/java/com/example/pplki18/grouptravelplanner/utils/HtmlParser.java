package com.example.pplki18.grouptravelplanner.utils;

import android.util.Log;

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

        Elements hotel_list = doc.select("div[class*='listItem'] > div[class*='listing collapsed']");

        ArrayList<Hotel> hotels = new ArrayList<>();
        for (int i = 1; i < hotel_list.size(); i++) {
            Hotel hotel = new Hotel();

            // url and name
            Element hotel_title = hotel_list.get(i).selectFirst("a[class*='property_title']");
            String url = hotel_title.attr("href");
            String name = hotel_title.text();

            // rating
            String rating = "-";
            Element hotel_rating = hotel_list.get(i).selectFirst("span[class*='rating']");
            if (hotel_rating != null) {
                String[] rating_long = hotel_rating.attr("alt").split(" ");
                rating = rating_long[0];
            }

            // price
            @SuppressWarnings("SpellCheckingInspection") Element hotel_price = hotel_list.get(i).selectFirst("div" +
                    "[data-sizegroup*='mini-meta-price']");
            String price = hotel_price.text();

            // photo
            String photo = "";
            Element hotel_photo = hotel_list.get(i).selectFirst("a[class*='respListingPhoto']")
                    .selectFirst("div[class*='inner']");
            System.out.println(hotel_photo.toString());
            if (hotel_photo != null) {
                String[] photo_long = hotel_photo.attr("style").split("\\(");
                System.out.println(photo_long[1]);
                photo = photo_long[1];
                photo = photo.substring(0, photo.length() - 2);
            }


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

        // name
        Element hotel_name = doc.selectFirst("h1[class*='ui_header h1']");
        String name = hotel_name.text();

        // rating
        Element hotel_rating = doc.selectFirst("span[class*='ui_bubble_rating']");
        String[] rating_long = hotel_rating.attr("alt").split(" ");
        String rating = rating_long[0];

        // price
        // TODO
        Element hotel_price = doc.selectFirst("div[class*='bb_price_text']");
        String price;
        if (hotel_price == null) {
            hotel_price = doc.selectFirst("div[data-sizegroup*='hr_chevron_prices']");
            price = (hotel_price != null) ? hotel_price.text() : "";
        } else {
            price = hotel_price.text();
        }

        // phone
        Element hotel_phone = doc.selectFirst("span[class*='is-hidden-mobile detail']");
        String phone = (hotel_phone != null) ? hotel_phone.text() : "";

        // street address
        Element hotel_address = doc.selectFirst("span[class*='street-address']");
        String street_address = (hotel_address != null) ? hotel_address.text() : "";

        // locality
        Element hotel_locality = doc.selectFirst("span[class*='locality']");
        String locality = (hotel_locality != null) ? hotel_locality.text() : "";

        // description
        Element hotel_desc = doc.selectFirst("span[class*='introText']");
        String desc = (hotel_desc != null) ? hotel_desc.text() : "";

        // description extension
        hotel_desc = doc.selectFirst("span[class*='introTextExtension']");
        if (desc.length() > 0 && hotel_desc != null) {
            desc = desc.substring(0, desc.length()-5) + hotel_desc.text();
        }

        // amenities
        Elements hotel_amenities = doc.select("div[class*='HighlightedAmenities__" +
                "amenities']").select("div[class*='ui_column is-6']");

        ArrayList<ArrayList<String>> amenities = new ArrayList<>();
        for (Element amenity_elem: hotel_amenities) {
            ArrayList<String> amenities_sub = new ArrayList<>();
            Elements amenities_sub_elem = amenity_elem.select("div[class*='HighlightedAmenities__" +
                    "amenityItem']");
            for (Element amenity_sub_elem: amenities_sub_elem) {
                String amenity = amenity_sub_elem.text();
                amenities_sub.add(amenity);
            }
            amenities.add(amenities_sub);
        }

        // photo
        Element hotel_photo = doc.selectFirst("meta[property*='og:image']");
        String photo = hotel_photo.attr("content");

        hotel.setName(name);
        hotel.setRating(rating);
        hotel.setPrice(price);
        hotel.setPhone_number(phone);
        hotel.setAddress(street_address + ", " + locality);
        if (desc.length() > 0) {
            hotel.setDescription(desc.substring(0, desc.length()-4));
        }
        hotel.setAmenities(amenities);
        hotel.setPhoto(photo);

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
