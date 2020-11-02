package com.datamodeltest.leaguedatamodeltest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.datamodel.leaguedatamodel.ImportJson;

public class ImportJsonTest {
	private static ImportJson parserObj;

	@BeforeClass
	public static void initializeParser() {
		parserObj = new ImportJson();
	}

	@AfterClass
	public static void disconnectParser() {
		parserObj = null;
	}

	@Test
	public void containStringKeyTest() {
		String jsonFile = "{\"leagueName\":\"DHL\"}";
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj = (JSONObject) parser.parse(jsonFile);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		String val = parserObj.containStringKey(jsonObj, "leagueName");
		Assert.assertEquals(val, "DHL");
	}

	@Test
	public void containArrayTest() {
		String jsonFile = "{\"player\":[{\"name\":\"one\",\"position\":\"goli\"}]}";
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			jsonObj = (JSONObject) parser.parse(jsonFile);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		jsonArray = parserObj.containArray(jsonObj, "player");
		Assert.assertEquals(1, jsonArray.size());
	}

	@Test
	public void containKeyCaptainTest() {
		String jsonFile = "{\"captain\":true}";
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj = (JSONObject) parser.parse(jsonFile);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		Boolean val = parserObj.containKeyCaptain(jsonObj, "captain");
		Assert.assertEquals(true, val);
	}

	@Test
	public void containObjectKeyTest() {
		String jsonFile = "{\"gameplayConfig\": {\"aging\": {}}}";
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = new JSONObject();
		JSONObject jsonOutput = new JSONObject();
		try {
			jsonObj = (JSONObject) parser.parse(jsonFile);
			jsonOutput = (JSONObject) parser.parse("{\"aging\": {}}");
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		JSONObject val = parserObj.containObjectKey(jsonObj, "gameplayConfig");
		Assert.assertEquals(jsonOutput, val);
	}

	@Test
	public void containFloatKeyTest() {
		String jsonFile = "{\"injury\": 0.6}";
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj = (JSONObject) parser.parse(jsonFile);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		float val = parserObj.containFloatKey(jsonObj, "injury");
		Assert.assertTrue(val == (float) 0.6);
	}

	@Test
	public void containIntKeyTest() {
		String jsonFile = "{\"recovery\": 10}";
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj = (JSONObject) parser.parse(jsonFile);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		float val = parserObj.containIntKey(jsonObj, "recovery");
		Assert.assertTrue(val == 10);
	}

}