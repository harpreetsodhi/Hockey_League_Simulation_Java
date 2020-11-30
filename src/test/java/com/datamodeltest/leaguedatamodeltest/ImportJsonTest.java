package com.datamodeltest.leaguedatamodeltest;

import com.datamodel.leaguedatamodel.ImportJson;
import com.inputoutputmodel.IDisplayToUser;
import com.inputoutputmodel.InputOutputModelAbstractFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ImportJsonTest {

	private static ImportJson parserObj;
	private final IDisplayToUser displayToUser;

	public ImportJsonTest() {
		InputOutputModelAbstractFactory ioFactory = InputOutputModelAbstractFactory.instance();
		displayToUser = ioFactory.createDisplayToUser();
	}

	@BeforeClass
	public static void initializeParser() {
		parserObj = new ImportJson();
		InputOutputModelAbstractFactory ioFactory = InputOutputModelAbstractFactory.instance();
		IDisplayToUser displayToUser = ioFactory.createDisplayToUser();
	}

	@AfterClass
	public static void disconnectParser() {
		parserObj = null;
	}

	@Test
	public void containStringKeyTest() {
		String jsonFileMock = "{\"leagueName\":\"DHL\"}";
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj = (JSONObject) parser.parse(jsonFileMock);
		} catch(ParseException e) {
			displayToUser.displayMsgToUser(e.getLocalizedMessage());
		}
		String val = parserObj.containStringKey(jsonObj, "leagueName");
		Assert.assertEquals(val, "DHL");
	}

	@Test
	public void containArrayTest() {
		String jsonFileMock = "{\"player\":[{\"name\":\"one\",\"position\":\"goli\"}]}";
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			jsonObj = (JSONObject) parser.parse(jsonFileMock);
		} catch(ParseException e) {
			displayToUser.displayMsgToUser(e.getLocalizedMessage());
		}
		jsonArray = parserObj.containArray(jsonObj, "player");
		Assert.assertEquals(1, jsonArray.size());
	}

	@Test
	public void containKeyCaptainTest() {
		String jsonFileMock = "{\"captain\":true}";
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj = (JSONObject) parser.parse(jsonFileMock);
		} catch(ParseException e) {
			displayToUser.displayMsgToUser(e.getLocalizedMessage());
		}
		Boolean val = parserObj.containKeyCaptain(jsonObj, "captain");
		Assert.assertEquals(true, val);
	}

	@Test
	public void containObjectKeyTest() {
		String jsonFileMock = "{\"gameplayConfig\": {\"aging\": {}}}";
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = new JSONObject();
		JSONObject jsonOutput = new JSONObject();
		try {
			jsonObj = (JSONObject) parser.parse(jsonFileMock);
			jsonOutput = (JSONObject) parser.parse("{\"aging\": {}}");
		} catch(ParseException e) {
			displayToUser.displayMsgToUser(e.getLocalizedMessage());
		}
		JSONObject val = parserObj.containObjectKey(jsonObj, "gameplayConfig");
		Assert.assertEquals(jsonOutput, val);
	}

	@Test
	public void containFloatKeyTest() {
		String jsonFileMock = "{\"injury\": 0.6}";
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj = (JSONObject) parser.parse(jsonFileMock);
		} catch(ParseException e) {
			displayToUser.displayMsgToUser(e.getLocalizedMessage());
		}
		float val = parserObj.containFloatKey(jsonObj, "injury");
		Assert.assertEquals(val, (float) 0.6, 0.0);
	}

	@Test
	public void containIntKeyTest() {
		String jsonFileMock = "{\"recovery\": 10}";
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj = (JSONObject) parser.parse(jsonFileMock);
		} catch(ParseException e) {
			displayToUser.displayMsgToUser(e.getLocalizedMessage());
		}
		float val = parserObj.containIntKey(jsonObj, "recovery");
		Assert.assertEquals(10, val, 0.0);
	}
}