package com.automation.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.automation.util.JsonUtil;
import com.automation.util.RestUtil;
import com.automation.util.TestUtil;
import com.jayway.jsonpath.PathNotFoundException;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;



public class E2E_Integration_System_Functional_Test {
	static ExtentTest test;
	static ExtentReports report;

	@BeforeClass
	public static void startTest()
	{
	report = new ExtentReports(System.getProperty("user.dir")+"\\reports\\ExtentReportResults_Functional_System_Integration.html");
	test = report.startTest("ExtentDemo");
	}


	
	//End 2 End Test
	@Test 
	public void E2ETest() throws IOException, PathNotFoundException, ParseException  {	
		//making ListOfDocuments json call and checking response Json is valid
		String URL="https://content-us-1.content-cms.com/api/06b21b25-591a-4dd3-a189-197363ea3d1f/delivery/v1/search?q=classification:content&fl=document:[json]&fl=type&rows=100";
		String ListOfDocuments_Json=RestUtil.makeGetCall2(URL);
		String documents_length1 = JsonUtil.getJsonValue(ListOfDocuments_Json, "$.documents.length()");
		int total_documents1 = Integer.parseInt(documents_length1);
		if(total_documents1>0)
			test.log(LogStatus.PASS, "Extract Data :List Of Documents Response recived. In ListOfDocuments Json total documents count is grater then 0 so Response is valid, Document Count is "+total_documents1);
		else
			test.log(LogStatus.FAIL, "In ListOfDocuments Json total documents count is not grater then 0 so Json is not valid");
		
		
		//making ListOfCategories json call and checking response Json is valid
		String URL2="https://content-us-1.content-cms.com/api/06b21b25-591a-4dd3-a189-197363ea3d1f/delivery/v1/search?q=classification:category&rows=100";
		String ListOfCategories_Json=RestUtil.makeGetCall2(URL2);
		String documents_length = JsonUtil.getJsonValue(ListOfCategories_Json, "$.documents.length()");
		int total_documents = Integer.parseInt(documents_length);
		if(total_documents>0)
			test.log(LogStatus.PASS, "Extract Data :List Of Categories Response recived. In ListOfCategories Json total documents count is grater then 0 so Response is valid, Document Count is "+total_documents);
		else
			test.log(LogStatus.FAIL, "In ListOfCategories Json total documents count is not grater then 0 so Json is not valid");
		
		
		//Checking the ListOfDocuments Json is in valid format
		boolean result=JsonUtil.isValidJson(ListOfDocuments_Json); 
		if (result)
				test.log(LogStatus.PASS, "Checked the ListOfDocuments Json format and it is valid");
		else
			test.log(LogStatus.FAIL, "ListOfDocuments Json is not valid Json");
		
		//Checking the ListOfCategories Json is in valid format
		boolean result2=JsonUtil.isValidJson(ListOfCategories_Json); 
		if (result2)
			test.log(LogStatus.PASS, "Checked the ListOfCategories Json format and it is valid");
		else
			test.log(LogStatus.FAIL, "ListOfCategories Json is not valid Json");
		
		
		//fetched Id and Path from ListOfDocuments Json and stored in Hashmap
		Map<String, String> HM_Id_and_Path=JsonUtil.Get_Id_Path_From_Categories_Json(ListOfCategories_Json);
		if(HM_Id_and_Path.size()>0)
		{
			test.log(LogStatus.PASS, "Fetch Id and Path from Categories Json and stored that on HashMap");// +HM_Id_and_Path );
		}
		else
		{
		    test.log(LogStatus.FAIL, "HashMap is Null so not able to Fetch Id and Path from Categories Json ");
		}
		
		
			
		//checked the ListOfDocuments where we need to update the path,& created hashmap of it
		Map<String, String> HM_categoryToUpdate=JsonUtil.findCategoriesToUpdate(ListOfDocuments_Json, HM_Id_and_Path);
		if(HM_categoryToUpdate.size()>0)
		{
			test.log(LogStatus.PASS, "Sarch the same Id in ListOfDocuments Json and updated the path if there is Id match , Created the Merge Json ");//+HM_categoryToUpdate );
		
		}
		else
		{
		    test.log(LogStatus.FAIL, "categoryToUpdate HashMap is Null so not able to Create the list to update in ListOfDocuments Json");
		}
		
		
		//update the ListOfDocuments json with path
		ListOfDocuments_Json = JsonUtil.updateCategoryJson(ListOfDocuments_Json, HM_categoryToUpdate);
		test.log(LogStatus.PASS, "Merge Json is -----------------  "+ListOfDocuments_Json);
				
		
		//Fetch the DocumentsCount from merge Json
		Map<String, Integer> documentCount=JsonUtil.getDocumentsCount(ListOfDocuments_Json);
		
		//Crerated the Final Json
		String Documents_per_category_Json=	TestUtil.CreateNewJson_documents_per_category(documentCount,HM_Id_and_Path);
		test.log(LogStatus.PASS, "Final Json - Documents per category"+Documents_per_category_Json);
	
		//Compare the Actual and Expected final Json Compare
		String Expected_Json = JsonUtil.readJsonFile("/Expected_Documents_per_category.json");
		boolean Compare=JsonUtil.jsonLenientCompare(Documents_per_category_Json, Expected_Json, true);
		if(Compare)
		{
			test.log(LogStatus.PASS, "Actual and Expected Documents per category Json are same so Test case is working as expected ");
		}
		else
		{
		test.log(LogStatus.FAIL, "Actual and Expected Documents per category Json are not same ");	
		}
	
		
	}
	
	
	@AfterClass
	public static void endTest()
	{
	report.endTest(test);
	report.flush();
	}
	
}



