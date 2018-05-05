package com.price_recomendation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Example program to list links from a URL.
 */
public class Crawler {

	public Crawler() {
	}

	private HashMap<Integer, String> catCode2Str = new HashMap<Integer, String>();
	private HashMap<String, Integer> catStr2Code = new HashMap<String, Integer>();
	private ArrayList<String> regions = new ArrayList<String>();

	public void readCategories() throws IOException {
		String fileName = "src\\main\\resources\\Categories.txt";
		BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));

		String line = buf.readLine();
		while (line != null) {
			String newLine = line.toString();
			Integer catCode = Integer.parseInt(newLine.split("\\s")[0]);
			String catName = newLine.split("\\s")[1];
			catCode2Str.put(catCode, catName);
			catStr2Code.put(catName, catCode);
			System.out.println(catName + " : " + catCode);
			line = buf.readLine();
		}
		buf.close();
	}

	public void readRegions() throws IOException {

		ArrayList<String> regions = new ArrayList<String>();
		String fileName = "src\\main\\resources\\Regions.txt";
		BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));

		String line = buf.readLine();
		while (line != null) {
			regions.add(line.toString());
			System.out.println(line.toString());
			line = buf.readLine();
		}
		buf.close();

	}

	public void crawlPage(String query, String category) throws IOException {

		readCategories();
		readRegions();

		query = query.replaceAll("\\s+", "+");

		for (String listRegion : regions) {
			String url = "https://www.blocket.se/" + listRegion + "?q=" + query + "&cg=" + catStr2Code.get(category);
			print("Fetching %s...", url);
			Document doc = Jsoup.connect(url).get();
			// Elements links = doc.select("a[href]");
			Elements imports = doc.select("link[href]");
			Element numb_hits = doc.select("span.num_hits").first();

			int numResults = Integer.parseInt(numb_hits.text());

			print("\nImports: (%d)", imports.size());
			for (Element link : imports) {
				print(" * %s <%s> (%s)", link.tagName(), link.attr("abs:href"), link.attr("rel"));
			}

			if (numResults == 0) {
				System.out.println(" No results");
			} else {
				System.out.println(numResults + " results for searched query");
				Elements prices = doc.select("p[itemprop=price]");

				List<Integer> priceList = new ArrayList<Integer>();
				int price;
				int numAdsPrices = 0;
				for (Element priceElem : prices) {
					if (!priceElem.text().equals("")) {
						price = Integer.parseInt(priceElem.text().split("kr")[0].replaceAll("[^0-9.]", ""));
						System.out.print(price);
						System.out.println(" from: " + priceElem.text());
						priceList.add(numAdsPrices, price);
						numAdsPrices += 1;
					}
				}
				System.out.println(" Number of ads with prices " + numAdsPrices);
				if (numAdsPrices == 0) {
					System.out.println(" The result don't have any price labeled");
				} else {
					Elements categories = doc.select("option.mls[^value]");
					HashMap<String, Integer> categoriesMap = new HashMap<String, Integer>();
					for (Element cat : categories) {
						System.out.println(Integer.parseInt(cat.attr("value")) + " " + cat.text());

						categoriesMap.put(cat.text(), Integer.parseInt(cat.attr("value")));
					}

				}
			}
		}
	}

	private static void print(String msg, Object... args) {
		System.out.println(String.format(msg, args));
	}

	private static String trim(String s, int width) {
		if (s.length() > width)
			return s.substring(0, width - 1) + ".";
		else
			return s;
	}
}
