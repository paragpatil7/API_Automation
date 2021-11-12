package com.automation.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;

import com.automation.util.JsonUtil;
import com.automation.util.RestUtil;
import com.automation.util.TestUtil;
import com.jayway.jsonpath.PathNotFoundException;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UtilUnitTests {
	static ExtentTest test;
	static ExtentReports report;

	@BeforeClass
	public static void startTest()
	{
	report = new ExtentReports(System.getProperty("user.dir")+"\\reports\\ExtentReportResults_UnitTest_Integration.html");
	test = report.startTest("ExtentDemo");
	}

	
	
	//make get rest api call for List of documents
	@Test
	public void A_MakeGetAPICall_toGet_ListOfDocuments_Test() throws IOException, PathNotFoundException, ParseException  {	
			
		String URL="https://content-us-1.content-cms.com/api/06b21b25-591a-4dd3-a189-197363ea3d1f/delivery/v1/search?q=classification:content&fl=document:[json]&fl=type&rows=100";
		String ListOfDocuments=RestUtil.makeGetCall2(URL);
		String documents_length = JsonUtil.getJsonValue(ListOfDocuments, "$.documents.length()");
		int total_documents = Integer.parseInt(documents_length);
		if(total_documents>0)
			test.log(LogStatus.PASS, "In ListOfDocuments Json total documents count is grater then 0 so Json is valid, Document Count is "+total_documents);
		else
			test.log(LogStatus.FAIL, "In ListOfDocuments Json total documents count is not grater then 0 so Json is not valid");
		
	}
	
	//make get rest api call for List of Categories
	@Test 
	public void B_MakeGetAPICall_toGet_ListOfCategories_Test() throws IOException, PathNotFoundException, ParseException  {	
		String URL2="https://content-us-1.content-cms.com/api/06b21b25-591a-4dd3-a189-197363ea3d1f/delivery/v1/search?q=classification:category&rows=100";
		String ListOfCategories=RestUtil.makeGetCall2(URL2);
		
		
		String documents_length = JsonUtil.getJsonValue(ListOfCategories, "$.documents.length()");
		int total_documents = Integer.parseInt(documents_length);
		if(total_documents>0)
			test.log(LogStatus.PASS, "In ListOfCategories Json total documents count is grater then 0 so Json is valid, Document Count is "+total_documents);
		else
			test.log(LogStatus.FAIL, "In ListOfCategories Json total documents count is not grater then 0 so Json is not valid");
	}

	//get ID and Path from Categories Json and store in hashmap 
	@Test
	public void C_Get_Id_Path_From_Categories_Json_() throws PathNotFoundException, ParseException
	{
	String URL2="https://content-us-1.content-cms.com/api/06b21b25-591a-4dd3-a189-197363ea3d1f/delivery/v1/search?q=classification:category&rows=100";
	String ListOfCategories=RestUtil.makeGetCall2(URL2);
	Map<String, String> hm=JsonUtil.Get_Id_Path_From_Categories_Json(ListOfCategories);
	if(hm.size()>0)
	{
		test.log(LogStatus.PASS, "Fetch Id and Path from Categories Json and stored that on HashMap : HashMap is  "+hm );
	}
	else
	{
	    test.log(LogStatus.FAIL, "HashMap is Null so not able to Fetch Id and Path from Categories Json ");
	}
	
	}
	
	//Create a list with json path where we need to update the Path in ListOfDocuments Json
	@Test
	public void D_Create_list_with_jsonpath_where_we_need_to_update_the_Path() throws PathNotFoundException, ParseException
	{ 
	String URL="https://content-us-1.content-cms.com/api/06b21b25-591a-4dd3-a189-197363ea3d1f/delivery/v1/search?q=classification:content&fl=document:[json]&fl=type&rows=100";
	String ListOfDocuments=RestUtil.makeGetCall2(URL);
	String URL2="https://content-us-1.content-cms.com/api/06b21b25-591a-4dd3-a189-197363ea3d1f/delivery/v1/search?q=classification:category&rows=100";
	String ListOfCategories=RestUtil.makeGetCall2(URL2);
	Map<String, String> hm=JsonUtil.Get_Id_Path_From_Categories_Json(ListOfCategories);
	Map<String, String> categoryToUpdate=JsonUtil.findCategoriesToUpdate(ListOfDocuments, hm);
	if(categoryToUpdate.size()>0)
	{
		test.log(LogStatus.PASS, "Created the Hashmap with Json path where we need to update the path in ListOfDocuments Json "+categoryToUpdate );
	}
	else
	{
	    test.log(LogStatus.FAIL, "categoryToUpdate HashMap is Null so not able to Create the list to update in ListOfDocuments Json");
	}
	
	}
	
	//update the ListOfDocuments json with path so it will create the merge Json
	@Test
	public void E_Create_merge_Json_with_path() throws PathNotFoundException, ParseException
	{
		String URL="https://content-us-1.content-cms.com/api/06b21b25-591a-4dd3-a189-197363ea3d1f/delivery/v1/search?q=classification:content&fl=document:[json]&fl=type&rows=100";
		String ListOfDocuments=RestUtil.makeGetCall2(URL);
		String URL2="https://content-us-1.content-cms.com/api/06b21b25-591a-4dd3-a189-197363ea3d1f/delivery/v1/search?q=classification:category&rows=100";
		String ListOfCategories=RestUtil.makeGetCall2(URL2);
		Map<String, String> hm=JsonUtil.Get_Id_Path_From_Categories_Json(ListOfCategories);
		Map<String, String> categoryToUpdate=JsonUtil.findCategoriesToUpdate(ListOfDocuments, hm);
		ListOfDocuments = JsonUtil.updateCategoryJson(ListOfDocuments, categoryToUpdate);
		test.log(LogStatus.PASS, "Created ListOfDocuments merge Json with path"+ListOfDocuments );
		
	}
	
	//find the document count and create final Json and Compare the Actual and Expected final Json 
	@Test
	public void F_Create_Final_Json() throws PathNotFoundException, ParseException, IOException
	{
		String URL="https://content-us-1.content-cms.com/api/06b21b25-591a-4dd3-a189-197363ea3d1f/delivery/v1/search?q=classification:content&fl=document:[json]&fl=type&rows=100";
		String ListOfDocuments_Json=RestUtil.makeGetCall2(URL);
		String URL2="https://content-us-1.content-cms.com/api/06b21b25-591a-4dd3-a189-197363ea3d1f/delivery/v1/search?q=classification:category&rows=100";
		String ListOfCategories=RestUtil.makeGetCall2(URL2);
		Map<String, String> HM_Id_and_Path=JsonUtil.Get_Id_Path_From_Categories_Json(ListOfCategories);
		Map<String, String> categoryToUpdate=JsonUtil.findCategoriesToUpdate(ListOfDocuments_Json, HM_Id_and_Path);
		ListOfDocuments_Json = JsonUtil.updateCategoryJson(ListOfDocuments_Json, categoryToUpdate);
		Map<String, Integer> documentCount=JsonUtil.getDocumentsCount(ListOfDocuments_Json);
		String Documents_per_category_Json=	TestUtil.CreateNewJson_documents_per_category(documentCount,HM_Id_and_Path);
		test.log(LogStatus.PASS, "Final Json - Documents per category"+Documents_per_category_Json);
		
		//Compare the Actual and Expected final Json Compare
		String Expected_Json = JsonUtil.readJsonFile("/Expected_Documents_per_category.json");
		boolean Compare=JsonUtil.jsonLenientCompare(Documents_per_category_Json, Expected_Json, true);
		System.out.println("Actual and Expected Json Compare "+Compare);
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



