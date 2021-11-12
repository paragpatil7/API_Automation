package com.automation.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TestUtil {
	
	
	@SuppressWarnings("unchecked")
	public static String CreateNewJson_documents_per_category(Map<String, Integer> documentCount,Map<String, String> hm)
	{
		JSONArray jarray=new JSONArray();
				
		for(Entry<String,Integer> m :documentCount.entrySet())
		{
		String categoryID=m.getKey();
		int Count=m.getValue();
		String path=hm.get(categoryID);
		
		//System.out.println("categoryID "+categoryID + "Count "+Count + "path "+path );
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("category", categoryID);
	    jsonObject.put("path", path);
	    jsonObject.put("documents", Count);
	    jarray.add(jsonObject);
	    
		}
		
			
		try {
	         FileWriter file = new FileWriter(System.getProperty("user.dir")+"\\reports\\Documents_per_category.json");
	       //  FileWriter file = new FileWriter("/usr/share/Parag/Documents_per_category.json");
	         file.write(jarray.toString());
	         file.close();
	      } catch (IOException e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	      }
	    //  System.out.println("JSON file created: "+jarray.toString());
	      return jarray.toString();
		
	}
	
	
	


}
