package com.automation.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.skyscreamer.jsonassert.JSONAssert;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import org.json.JSONObject;
import org.json.*;

public class JsonUtil {
	
	
	private static final Gson gson = new Gson();
	static Map<String, String> categoryToUpdate = new HashMap<String, String>();
	
	public static boolean isValidJson(String jsonInString) {
		try {
			gson.fromJson(jsonInString, Object.class);
		} catch (JsonSyntaxException ex) {
			//logger.info(ex.getMessage());
			return false;
		}
		return true;
	}

	public static String getJsonValue(String json, String jsonPath) throws ParseException, PathNotFoundException {
		if (isPathValid(json, jsonPath)) {
			JSONParser jsonParser = new JSONParser();
			Object jsonObject = jsonParser.parse(json);
			return JsonPath.read(jsonObject, jsonPath).toString();
		}

		return null;
	}
	
	public static boolean isPathValid(String jsonString, String path) {
		try {
			String value = JsonPath.read(jsonString, path).toString();
		} catch (PathNotFoundException e) {
			return false;
		}
		return true;
	}
	
	public static Map<String, String> Get_Id_Path_From_Categories_Json(String json) throws ParseException, PathNotFoundException {
	
	Map<String, String> hm=new HashMap<String, String>(); 
	JSONObject obj = new JSONObject(json);
	//String pageName = obj.getJSONObject("pageInfo").getString("pageName");

	JSONArray arr = obj.getJSONArray("documents"); 
	for (int i = 0; i < arr.length(); i++)
	{
		String id = arr.getJSONObject(i).getString("id");
		String path = arr.getJSONObject(i).getString("path");
	//	System.out.println("id  "+id);
	 //   System.out.println("path  "+path);
	    hm.put(id, path);
	}
	return hm;
			
}


	public static String readJsonFile(String jsonFile) throws ParseException, IOException {
		Object jsonObject = null;
		JSONParser jsonParser = new JSONParser();
		//String filename = "src/main/resources/" + jsonFile;
		//class.getResourceAsStream("/config.json");
		String filename = System.getProperty("user.dir") + jsonFile;
	//	String filename =  "/usr/share/Parag/Expected_Documents_per_category.json";
		
		FileReader reader = new FileReader(filename);
		jsonObject = jsonParser.parse(reader);
		return jsonObject.toString();
		
				
	}

	public static boolean jsonLenientCompare(String actualJson, String expectedJson, boolean modeOfCompare) {
		try {
			JSONAssert.assertEquals(expectedJson, actualJson, modeOfCompare);
		} catch (Exception e) {
			return false;
		}
		return true;

	}

	public static String addNewJsonObject(String jsonInString, String jsonPathWithKeyName, String Value) throws ParseException {
		
		String json = null;

		if (Value.contains("{") || Value.contains("}") || Value.contains("[") || Value.contains("]")) {
			JSONParser jsonParser = new JSONParser();
			Object jsonObject = jsonParser.parse(Value);
			Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL)
					.addOptions(Option.SUPPRESS_EXCEPTIONS);
			DocumentContext documentContext = JsonPath.using(conf).parse(jsonInString);
			documentContext.set(JsonPath.compile(jsonPathWithKeyName), jsonObject);
			json = documentContext.jsonString();

		} else {
			Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL)
					.addOptions(Option.SUPPRESS_EXCEPTIONS);
			DocumentContext documentContext = JsonPath.using(conf).parse(jsonInString);
			documentContext.set(JsonPath.compile(jsonPathWithKeyName), Value);
			json = documentContext.jsonString();
			//System.out.println("Updated");
			//System.out.println(jsonPathWithKeyName + " " + Value);

		}
		return json;
	}

	
	public static Map<String, String> findCategoriesToUpdate(String jsonInString, Map<String, String> data) throws ParseException {

		String documents_length = getJsonValue(jsonInString, "$.documents.length()");
		int total_documents = Integer.parseInt(documents_length);

		for (int i = 0; i <= total_documents; i++) {

			if (getJsonValue(jsonInString,
					"$.documents[" + i + "].document.elements.category.categoryIds") != null) {

				String category_ids = getJsonValue(jsonInString,
						"$.documents[" + i + "].document.elements.category.categoryIds");

				for (Entry<String, String> m : data.entrySet()) {

					if (category_ids.contains(m.getKey())) {

						categoryToUpdate.put("$.documents[" + i + "].document.elements.category.path", m.getValue());

					}
				}

			}

		}

		//System.out.println("Category To Update " + categoryToUpdate);
		return categoryToUpdate;

	}

	
	public static String updateCategoryJson(String catagory_json, Map<String, String> categoryToUpdate2)
			throws ParseException {

		for (Entry<String, String> m : categoryToUpdate2.entrySet()) {
			catagory_json = addNewJsonObject(catagory_json, m.getKey(), m.getValue());

		}

		return catagory_json;

	}
	
	
	
	public static Map<String, Integer> getDocumentsCount(String jsonInString)
			throws ParseException {

		Map<String, Integer> documentCount = new HashMap<>();

		String documents_length = getJsonValue(jsonInString, "$.documents.length()");
		int total_documents = Integer.parseInt(documents_length);

			for (int i = 0; i <= total_documents; i++) {

				String jsonPath = "$.documents[" + i + "].document.elements.category.categoryIds[0]";

				if (isPathValid(jsonInString, jsonPath)) {

					String category_ids = getJsonValue(jsonInString, jsonPath);

					documentCount.put(category_ids, documentCount.getOrDefault(category_ids, 0) + 1);

					
				}
 
			}

		
		//System.out.println(documentCount);
		return documentCount;

	}
	
	
	
}
