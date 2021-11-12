package com.automation.util;
import static io.restassured.RestAssured.given;

public class RestUtil {

	
		
	public static String makeGetCall2(String url)
	{
		
		return given().get(url).asString();
	}

}

