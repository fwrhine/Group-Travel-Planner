package com.example.pplki18.grouptravelplanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class XPathParser {
    public void parse() {

        try {

            File input = new File("html.txt");
            Document doc = Jsoup.connect("https://www.tripadvisor.com/Hotels-g60763-New_York_City_New_York-Hotels.html").get();
//            Document doc = Jsoup.parse(input, "UTF-8", "https://www.tripadvisor.com/Hotels-g60763-New_York_City_New_York-Hotels.html");

            Elements hotelList = doc.select("a[class='property_title prominent ']");

            for (Element element: hotelList) {
                System.out.println(element.text());
            }
            System.out.println("hello");
            // img with src ending .png


//            File inputFile = new File("html.txt");
//            String html = FileUtils.readFileToString(inputFile);
//            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dBuilder;
//            InputSource doc = new InputSource(new InputStreamReader(new FileInputStream(new File("html.txt"))));
//
//
////            dBuilder = dbFactory.newDocumentBuilder();
////
////            Document doc = dBuilder.parse(inputFile);
////            doc.getDocumentElement().normalize();
//
//            XPath xPath =  XPathFactory.newInstance().newXPath();
//
//            String expression = "//div[contains(@class,\"listItem\")]//div[contains(@class,\"listing collapsed\")]";
//            NodeList hotelList = (NodeList) xPath.compile(expression).evaluate(
//                    doc, XPathConstants.NODESET);
//
//            if (hotelList != null && hotelList.getLength() == 0) {
//                expression = "//div[contains(@class,\"listItem\")]//div[@class=\"listing \"]";
//                hotelList = (NodeList) xPath.compile(expression).evaluate(
//                        doc, XPathConstants.NODESET);
//            }
//
//            ArrayList<Hotel> hotels = new ArrayList<>();
//
//            for (int i = 0; i < hotelList.getLength(); i++) {
//                Node hotel = hotelList.item(i);
//                System.out.println("\nCurrent Element :" + hotel.getNodeName());
//
//                String XPATH_HOTEL_LINK = ".//a[contains(@class,\"property_title\")]/@href";
////                String XPATH_REVIEWS  = ".//a[@class=\"review_count\"]//text()";
////                String XPATH_RANK = ".//div[@class=\"popRanking\"]//text()";
//                String XPATH_RATING = ".//span[contains(@class,\"rating\")]/@alt";
//                String XPATH_HOTEL_NAME = ".//a[contains(@class,\"property_title\")]//text()";
////                String XPATH_HOTEL_FEATURES = ".//div[contains(@class,\"common_hotel_icons_list\")]//li//text()";
//                String XPATH_HOTEL_PRICE = ".//div[contains(@data-sizegroup,\"mini-meta-price\")]/text()";
////                String XPATH_VIEW_DEALS = ".//div[contains(@data-ajax-preserve,\"viewDeals\")]//text()'";
////                String XPATH_BOOKING_PROVIDER = ".//div[contains(@data-sizegroup,\"mini-meta-provider\")]//text()";
//
//                NodeList raw_hotel_link = (NodeList) xPath.compile(XPATH_HOTEL_LINK).evaluate(
//                        hotel, XPathConstants.NODESET);
//                Node raw_rating = (Node) xPath.compile(XPATH_RATING).evaluate(
//                        hotel, XPathConstants.NODESET);
//                Node raw_hotel_name = (Node) xPath.compile(XPATH_HOTEL_NAME).evaluate(
//                        hotel, XPathConstants.NODESET);
//                Node raw_hotel_price = (Node) xPath.compile(XPATH_HOTEL_PRICE).evaluate(
//                        hotel, XPathConstants.NODESET);
//
//                String url = (raw_hotel_link != null) ? raw_hotel_link.item(0).getTextContent() : "";
//                String rating = (raw_rating != null) ? raw_rating.getTextContent() : "-";
//                String name = (raw_hotel_name != null) ? raw_hotel_name.getTextContent() : "";
//                String price = (raw_hotel_price != null) ? raw_hotel_price.getTextContent() : "";
//
//                Hotel hotelObj = new Hotel();
//
//                hotelObj.setWebsite(url);
//                hotelObj.setRating(rating);
//                hotelObj.setName(name);
//                hotelObj.setAddress("");
//                hotelObj.setPrice(price);
//
//                hotels.add(hotelObj);
//            }
//
//            System.out.println(hotels.toString());

//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
//        } catch (XPathExpressionException e) {
//            e.printStackTrace();
        }
    }
}
