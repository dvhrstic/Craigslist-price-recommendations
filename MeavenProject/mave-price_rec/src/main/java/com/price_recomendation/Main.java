package com.price_recomendation;

import java.io.IOException;

public class Main {
	
	public static void main (String[] args) throws IOException{
		
		Crawler crawler = new Crawler();
		String category = "Bilar";
		String query = "Volvo	xc90";
        crawler.crawlPage(query, category);
        
	}

}

