//package org.jsoup.examples;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

/**
 * Example program to list links from a URL.
 */
public class ListLinks {
    public static void main(String[] args) throws IOException {
        Validate.isTrue(args.length == 2, "usage: supply url to fetch");
        String categoryName = "";
        String query = args[0];
        if (!args[1].isEmpty()){
            categoryName = args[1];
        }
        String url = "https://www.blocket.se/hela_sverige?q=" + query;
        print("Fetching %s...", url);

        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
        Element numb_hits = doc.select("span.num_hits").first();
        
        int numResults = Integer.parseInt(numb_hits.text());
        

        print("\nMedia: (%d)", media.size());
        for (Element src : media) {
            if (src.tagName().equals("img"))
                print(" * %s: <%s> %sx%s (%s)",
                        src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
                        trim(src.attr("alt"), 20));
            else
                print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
        }

        print("\nImports: (%d)", imports.size());
        for (Element link : imports) {
            print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
        }

        print("\nLinks: (%d)", links.size());
        for (Element link : links) {
            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
        }
        
        
        if (numResults == 0){
            System.out.println(" No results");
        }
        else{
            System.out.println( numResults + " results for searched query");
            Elements prices = doc.select("p[itemprop=price]");
            
            List<Integer> priceList = new ArrayList<Integer>();
            int price;
            int numAdsPrices = 0;
            for (Element priceElem : prices){
                if (!priceElem.text().equals("")){
                price = Integer.parseInt(priceElem.text().split("kr")[0].replaceAll("[^0-9.]", ""));
                System.out.println(price);
                priceList.add(numAdsPrices, price);
                numAdsPrices += 1;
                }
            }
            System.out.println(" Number of ads with prices " + numAdsPrices);
            if(numAdsPrices == 0){
                System.out.println(" The result don't have any price labeled");
            }else{
                Elements categories = doc.select("option.mls[^value]");
                HashMap<String, Integer> categoriesMap = new HashMap<String, Integer>();
                for (Element cat : categories){
                    System.out.println(Integer.parseInt(cat.attr("value")) + " " + cat.text());
                    categoriesMap.put(cat.text(), Integer.parseInt(cat.attr("value")));
                }

                url = "https://www.blocket.se/hela_sverige?q=" + query + "&cg=" + categoriesMap.get(categoryName);
                System.out.println(url);
            }
        }   
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
}

