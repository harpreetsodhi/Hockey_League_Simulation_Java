package com.datamodel.leaguedatamodel;

import com.datamodel.gameplayconfig.*;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.inputoutputmodel.IDisplayToUser;
import com.inputoutputmodel.IPropertyLoader;
import com.inputoutputmodel.InputOutputModelAbstractFactory;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;


public class ImportJson {
	final static Logger logger = Logger.getLogger(ImportJson.class);

	private static final String SEASON_START_DATE = "seasonStartDate";
	private IDisplayToUser displayToUser;

	public ImportJson() {
		InputOutputModelAbstractFactory ioFactory = InputOutputModelAbstractFactory.instance();
		IDisplayToUser displayToUser = ioFactory.createDisplayToUser();
	}

	public ILeague parseJson(String filePath) {
		Object jsonObj = null;
		try {
			JSONParser parser = new JSONParser();

			Reader reader = new FileReader(filePath);
			jsonObj = parser.parse(reader);
			Reader readerValidation = new FileReader(filePath);

			StringBuilder buffer = new StringBuilder();
			char[] arr = new char[8 * 1024];
			int numCharsRead;
			while((numCharsRead = readerValidation.read(arr, 0, arr.length)) > -1) {
				buffer.append(arr, 0, numCharsRead);
			}
			String jsonString = buffer.toString();

			Gson gson = new Gson();
			gson.fromJson(jsonString, Object.class);
			readerValidation.close();
			reader.close();
		} catch(JsonSyntaxException ej) {
			String errorMsg = ej.getLocalizedMessage();
			logger.info("Error in json:" + errorMsg.substring(errorMsg.indexOf(":") + 1));
			displayToUser.displayMsgToUser("Error in json:" + errorMsg.substring(errorMsg.indexOf(":") + 1));
			System.exit(1);

		} catch(IOException | ParseException e) {
			logger.debug(e.getMessage());
			displayToUser.displayMsgToUser(e.getMessage());
			System.exit(1);
		}
		JSONObject jsonObject = (JSONObject) jsonObj;

		JSONObject gameplayConfigObj = containObjectKey(jsonObject, "gameplayConfig");

		JSONObject agingObj = containObjectKey(gameplayConfigObj, "aging");
		int averageRetirementAge = containIntKey(agingObj, "averageRetirementAge");
		int maximumAge = containIntKey(agingObj, "maximumAge");
		float statDecayChance = containFloatKey(agingObj, "statDecayChance");
		GamePlayConfigAbstractFactory gamePlayConfigFactory = GamePlayConfigAbstractFactory.instance();
		IAgingConfig aging = gamePlayConfigFactory.createAgingConfig();
		aging.setAverageRetirementAge(averageRetirementAge);
		aging.setMaximumAge(maximumAge);
		aging.setStatDecayChance(statDecayChance);

		JSONObject gameResolverObj = containObjectKey(gameplayConfigObj, "gameResolver");
		float penaltyChance = containFloatKey(gameResolverObj, "penaltyChance");
		IGameResolverConfig gameResolver = gamePlayConfigFactory.createGameResolverConfig();
		gameResolver.setPenaltyChance(penaltyChance);


		JSONObject injuriesObj = containObjectKey(gameplayConfigObj, "injuries");
		float randomInjuryChance = containFloatKey(injuriesObj, "randomInjuryChance");
		int injuryDaysLow = containIntKey(injuriesObj, "injuryDaysLow");
		int injuryDaysHigh = containIntKey(injuriesObj, "injuryDaysHigh");
		IInjuryConfig injuries = gamePlayConfigFactory.createInjuryConfig();
		injuries.setRandomInjuryChance(randomInjuryChance);
		injuries.setInjuryDaysLow(injuryDaysLow);
		injuries.setInjuryDaysHigh(injuryDaysHigh);

		JSONObject trainingObj = containObjectKey(gameplayConfigObj, "training");
		int daysUntilStatIncreaseCheck = containIntKey(trainingObj, "daysUntilStatIncreaseCheck");
		ITrainingConfig training = gamePlayConfigFactory.createTrainingConfig();
		training.setDaysUntilStatIncreaseCheck(daysUntilStatIncreaseCheck);

		JSONObject tradingObj = containObjectKey(gameplayConfigObj, "trading");
		int lossPoint = containIntKey(tradingObj, "lossPoint");
		float randomTradeOfferChance = containFloatKey(tradingObj, "randomTradeOfferChance");
		int maxPlayersPerTrade = containIntKey(tradingObj, "maxPlayersPerTrade");
		float randomAcceptanceChance = containFloatKey(tradingObj, "randomAcceptanceChance");
		JSONObject gmTableObj = containObjectKey(tradingObj, "gmTable");
		float shrewd = containFloatKey(gmTableObj, "shrewd");
		float normal = containFloatKey(gmTableObj, "normal");
		float gambler = containFloatKey(gmTableObj, "gambler");
		IGeneralManagerConfig gmTable = gamePlayConfigFactory.createGeneralManagerConfig();
		gmTable.setShrewd(shrewd);
		gmTable.setNormal(normal);
		gmTable.setGambler(gambler);
		ITradingConfig trading = gamePlayConfigFactory.createTradingConfig();
		trading.setLossPoint(lossPoint);
		trading.setRandomTradeOfferChance(randomTradeOfferChance);
		trading.setMaxPlayersPerTrade(maxPlayersPerTrade);
		trading.setRandomAcceptanceChance(randomAcceptanceChance);
		trading.setGMTable(gmTable);
		IGamePlayConfig gamePlayConfig = gamePlayConfigFactory.createGamePlayConfig();
		LeagueDataModelAbstractFactory dataModelFactory = LeagueDataModelAbstractFactory.instance();
		ILeague leagueObj = dataModelFactory.createLeague();
		gamePlayConfig.setAging(aging);
		gamePlayConfig.setInjury(injuries);
		gamePlayConfig.setTraining(training);
		gamePlayConfig.setTrading(trading);
		gamePlayConfig.setGameResolver(gameResolver);
		leagueObj.setGamePlayConfig(gamePlayConfig);

		String leagueName = containStringKey(jsonObject, "leagueName");
		leagueObj.setLeagueName(leagueName);
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		InputOutputModelAbstractFactory ioFactory = InputOutputModelAbstractFactory.instance();
		IPropertyLoader propertyLoader = ioFactory.createPropertyLoader();
		String currentDate = currentYear + propertyLoader.getPropertyValue(SEASON_START_DATE);
		leagueObj.setCurrentDate(Date.valueOf(currentDate));
		leagueObj.setSimulationStartDate(Date.valueOf(currentDate));
		JSONArray conferencesArray = containArray(jsonObject, "conferences");
		IDrafting drafting = dataModelFactory.createDrafting();
		for(int a = 0; a < conferencesArray.size(); a++) {
			JSONObject conference = (JSONObject) conferencesArray.get(a);
			String conferenceName = containStringKey(conference, "conferenceName");
			JSONArray divisionsArray = containArray(conference, "divisions");
			IConference conferenceObj = dataModelFactory.createConference();
			conferenceObj.setConferenceName(conferenceName);
			for(int b = 0; b < divisionsArray.size(); b++) {
				JSONObject division = (JSONObject) divisionsArray.get(b);
				String divisionName = containStringKey(division, "divisionName");
				JSONArray teamsArray = containArray(division, "teams");
				IDivision divisionObj = dataModelFactory.createDivision();
				divisionObj.setDivisionName(divisionName);
				for(int c = 0; c < teamsArray.size(); c++) {
					JSONObject team = (JSONObject) teamsArray.get(c);
					JSONObject generalManager = containObjectKey(team, "generalManager");
					String generalManagerName = containStringKey(generalManager, "name");
					String generalManagerPersonality = containStringKey(generalManager, "personality");
					JSONObject headCoach = containObjectKey(team, "headCoach");
					String headCoachName = containStringKey(headCoach, "name");
					float headCoachSkating = containFloatKey(headCoach, "skating");
					float headCoachShooting = containFloatKey(headCoach, "shooting");
					float headCoachChecking = containFloatKey(headCoach, "checking");
					float headCoachSaving = containFloatKey(headCoach, "saving");
					String teamName = containStringKey(team, "teamName");
					IGeneralManager generalManagerObj = dataModelFactory.createGeneralManager();
					generalManagerObj.setGeneralManagerName(generalManagerName);
					generalManagerObj.setGeneralManagerPersonality(generalManagerPersonality);
					IHeadCoach headCoachObj = dataModelFactory.createHeadCoach();
					headCoachObj.setHeadCoachName(headCoachName);
					headCoachObj.setHeadCoachSkating(headCoachSkating);
					headCoachObj.setHeadCoachShooting(headCoachShooting);
					headCoachObj.setHeadCoachChecking(headCoachChecking);
					headCoachObj.setHeadCoachSaving(headCoachSaving);
					JSONArray playersArray = containArray(team, "players");
					ITeam teamObj = dataModelFactory.createTeam();
					teamObj.setTeamName(teamName);
					teamObj.setTeamCreatedBy("import");
					teamObj.setGeneralManager(generalManagerObj);
					teamObj.setHeadCoach(headCoachObj);
					int captainCount = 0;
					int forwardCount = 0;
					int defenseCount = 0;
					int goalieCount = 0;
					for(int i = 0; i < playersArray.size(); i++) {
						JSONObject player = (JSONObject) playersArray.get(i);
						String playerName = containStringKey(player, "playerName");
						String playerPosition = containStringKey(player, "position");
						Boolean isPlayerCaptain = containKeyCaptain(player, "captain");
						if(playerPosition.equals("goalie")) {
							goalieCount++;
						} else if(playerPosition.equals("forward")) {
							forwardCount++;
						} else {
							defenseCount++;
						}
						if(isPlayerCaptain) {
							captainCount++;
						}
						int playerBirthDay = containIntKey(player, "birthDay");
						int playerBirthMonth = containIntKey(player, "birthMonth");
						int playerBirthYear = containIntKey(player, "birthYear");
						LocalDate playerBirthDate = LocalDate.of(playerBirthYear, playerBirthMonth, playerBirthDay);
						int playerSkating = containIntKey(player, "skating");
						int playerShooting = containIntKey(player, "shooting");
						int playerChecking = containIntKey(player, "checking");
						int playerSaving = containIntKey(player, "saving");
						IPlayer playerObj = dataModelFactory.createPlayer();
						playerObj.setPlayerName(playerName);
						playerObj.setPlayerPosition(playerPosition);
						playerObj.setPlayerCaptain(isPlayerCaptain);
						playerObj.setPlayerBirthDay(playerBirthDay);
						playerObj.setPlayerBirthMonth(playerBirthMonth);
						playerObj.setPlayerBirthYear(playerBirthYear);
						playerObj.calculatePlayerAge(playerBirthDate, LocalDate.parse(currentDate));
						playerObj.setPlayerSkating(playerSkating);
						playerObj.setPlayerShooting(playerShooting);
						playerObj.setPlayerChecking(playerChecking);
						playerObj.setPlayerSaving(playerSaving);
						teamObj.addPlayer(playerObj);
					}
					if((playersArray.size() == 30 && forwardCount == 16 && defenseCount == 10 && goalieCount == 4 && captainCount == 1) == false) {
						logger.error("ERROR: A team should have 16 forwards, 10 defense, 4 goalies and 1 captain.");
						logger.error(leagueName + " -> " + conferenceName + " -> " + divisionName + " -> " + teamName + " has " + forwardCount + " forwards, " + defenseCount + " defense, " + goalieCount + " goalies and " + captainCount + " captain(s).");
						displayToUser.displayMsgToUser("ERROR: A team should have 16 forwards, 10 defense, 4 goalies " + "and 1 captain.");
						displayToUser.displayMsgToUser(leagueName + " -> " + conferenceName + " -> " + divisionName +
								" -> " + teamName + " has " + forwardCount + " forwards, " + defenseCount + " defense,"
								+ " " + goalieCount + " goalies and " + captainCount + " captain(s).");
						System.exit(1);
					} else {
						teamObj.setActiveRoster();
					}
					drafting.createDraftPick(teamObj);
					divisionObj.addTeam(teamObj);
				}
				conferenceObj.addDivision(divisionObj);
			}
			leagueObj.addConference(conferenceObj);
		}
		JSONArray freeAgentsArray = containArray(jsonObject, "freeAgents");
		int forwardCount = 0;
		int defenseCount = 0;
		int goalieCount = 0;
		for(int x = 0; x < freeAgentsArray.size(); x++) {
			JSONObject freeAgent = (JSONObject) freeAgentsArray.get(x);
			String freeAgentName = containStringKey(freeAgent, "playerName");
			String freeAgentPosition = containStringKey(freeAgent, "position");
			int freeAgentBirthDay = containIntKey(freeAgent, "birthDay");
			int freeAgentBirthMonth = containIntKey(freeAgent, "birthMonth");
			int freeAgentBirthYear = containIntKey(freeAgent, "birthYear");
			LocalDate freeAgentBirthDate = LocalDate.of(freeAgentBirthYear, freeAgentBirthMonth, freeAgentBirthDay);
			int freeAgentSkating = containIntKey(freeAgent, "skating");
			int freeAgentShooting = containIntKey(freeAgent, "shooting");
			int freeAgentChecking = containIntKey(freeAgent, "checking");
			int freeAgentSaving = containIntKey(freeAgent, "saving");
			IPlayer freeAgentObj = dataModelFactory.createFreeAgent();
			freeAgentObj.setPlayerName(freeAgentName);
			freeAgentObj.setPlayerPosition(freeAgentPosition);
			freeAgentObj.setPlayerBirthDay(freeAgentBirthDay);
			freeAgentObj.setPlayerBirthMonth(freeAgentBirthMonth);
			freeAgentObj.setPlayerBirthYear(freeAgentBirthYear);
			freeAgentObj.calculatePlayerAge(freeAgentBirthDate, LocalDate.parse(currentDate));
			freeAgentObj.setPlayerSkating(freeAgentSkating);
			freeAgentObj.setPlayerShooting(freeAgentShooting);
			freeAgentObj.setPlayerChecking(freeAgentChecking);
			freeAgentObj.setPlayerSaving(freeAgentSaving);
			freeAgentObj.setRosterStatus(false);
			leagueObj.addFreeAgent(freeAgentObj);
			if(freeAgentPosition.equals("goalie")) {
				goalieCount++;
			} else if(freeAgentPosition.equals("forward")) {
				forwardCount++;
			} else {
				defenseCount++;
			}
		}
		if(forwardCount < 16) {
			logger.info(forwardCount + " forward(s) found! At least 16 required to create a team.");
			displayToUser.displayMsgToUser(forwardCount + " forward(s) found! At least 16 required to create a team.");
			System.exit(1);
		}
		if(defenseCount < 10) {
			logger.info(defenseCount + " defense(s) found! At least 10 required to create a team.");
			displayToUser.displayMsgToUser(defenseCount + " defense(s) found! At least 10 required to create a team.");
			System.exit(1);
		}
		if(goalieCount < 4) {
			logger.info(goalieCount + " goalie(s) found! At least 4 goalies required to form a team.");
			displayToUser.displayMsgToUser(goalieCount + " goalie(s) found! At least 4 goalies required to form a " +
					"team" + ".");
			System.exit(1);
		}

		JSONArray managersArray = containArray(jsonObject, "generalManagers");
		if(managersArray.size() < 1) {
			displayToUser.displayMsgToUser("At least one manager is required to form a team.");
			System.exit(1);
		}
		for(int y = 0; y < managersArray.size(); y++) {
			JSONObject managers = (JSONObject) managersArray.get(y);
			String managerName = containStringKey(managers, "name");
			String managerPersonality = containStringKey(managers, "personality");
			IGeneralManager managerObj = dataModelFactory.createGeneralManager();
			managerObj.setGeneralManagerName(managerName);
			managerObj.setGeneralManagerPersonality(managerPersonality);
			leagueObj.setManager(managerObj);
		}

		JSONArray coachesArray = containArray(jsonObject, "coaches");
		if(coachesArray.size() < 1) {
			displayToUser.displayMsgToUser("At least one coach is required to form a team.");
			System.exit(1);
		}
		for(int z = 0; z < coachesArray.size(); z++) {
			JSONObject coaches = (JSONObject) coachesArray.get(z);
			String coachName = containStringKey(coaches, "name");
			float coachSkating = containFloatKey(coaches, "skating");
			float coachShooting = containFloatKey(coaches, "shooting");
			float coachChecking = containFloatKey(coaches, "checking");
			float coachSaving = containFloatKey(coaches, "saving");
			IHeadCoach coachObj = dataModelFactory.createHeadCoach();
			coachObj.setHeadCoachName(coachName);
			coachObj.setHeadCoachSkating(coachSkating);
			coachObj.setHeadCoachShooting(coachShooting);
			coachObj.setHeadCoachChecking(coachChecking);
			coachObj.setHeadCoachSaving(coachSaving);
			leagueObj.setCoach(coachObj);
		}
		return leagueObj;
	}

	public String containStringKey(JSONObject obj, String key) {
		if(obj.containsKey(key) == false) {
			displayToUser.displayMsgToUser("Invalid JSON, It does not have " + key + " information");
			System.exit(1);
		}
		String hasKey = "";
		try {
			hasKey = (String) obj.get(key);
		} catch(Exception e) {
			logger.error("Invalid JSON, It has invalid value for" + key);
			displayToUser.displayMsgToUser("Invalid JSON, It has invalid value for" + key);
			System.exit(1);
		}

		if(hasKey == null || hasKey.trim().isEmpty()) {
			logger.error("Invalid JSON, It does not have value for the " + key);
			displayToUser.displayMsgToUser("Invalid JSON, It does not have value for the " + key);
			System.exit(1);
		}
		return hasKey;
	}

	public int containIntKey(JSONObject obj, String key) {
		if(obj.containsKey(key) == false) {
			logger.error("Invalid JSON, It does not have " + key + " information");
			displayToUser.displayMsgToUser("Invalid JSON, It does not have " + key + " information");
			System.exit(1);
		}
		int value = 0;
		try {
			value = (int) (long) obj.get(key);
		} catch(Exception e) {
			logger.error("Invalid JSON, It has invalid player stats value for " + key);
			displayToUser.displayMsgToUser("Invalid JSON, It has invalid player stats value for " + key);
			System.exit(1);
		}
		PlayerStats[] allStats = PlayerStats.values();
		for(PlayerStats PlayerStat : allStats)
			if(PlayerStat.name().equalsIgnoreCase(key) && (value < 1 || value > 20)) {
				logger.error("Invalid JSON, It has invalid Player stats value for " + key);
				displayToUser.displayMsgToUser("Invalid JSON, It has invalid Player stats value for " + key);
				System.exit(1);
			}
		return value;
	}

	public float containFloatKey(JSONObject obj, String key) {
		String SHREWD = "shrewd";
		String NORMAL = "normal";
		String GAMBLER = "gambler";
		if(obj.containsKey(key) == false) {
			logger.error("Invalid JSON, It does not have " + key + " information");
			displayToUser.displayMsgToUser("Invalid JSON, It does not have " + key + " information");
			System.exit(1);
		}
		float value = 0;
		try {
			value = (float) (double) obj.get(key);
		} catch(Exception e) {
			logger.error("Invalid JSON, It has invalid headCoach stats value for " + key);
			displayToUser.displayMsgToUser("Invalid JSON, It has invalid headCoach stats value for " + key);
			System.exit(1);
		}
		if(key.equals(SHREWD) || key.equals(NORMAL) || key.equals(GAMBLER)) {
			if(value < -1 || value > 1) {
				logger.error("Invalid JSON, It has invalid gmTable value for " + key);
				displayToUser.displayMsgToUser("Invalid JSON, It has invalid gmTable value for " + key);
				System.exit(1);
			}
		} else {
			if(value < 0 || value > 1) {
				logger.error("Invalid JSON, It has invalid headCoach stats value for " + key);
				displayToUser.displayMsgToUser("Invalid JSON, It has invalid headCoach stats value for " + key);
				System.exit(1);
			}
		}
		return value;
	}

	public Boolean containKeyCaptain(JSONObject obj, String key) {
		if(obj.containsKey(key) == false) {
			logger.error("Invalid JSON, It does not have " + key + " information");
			displayToUser.displayMsgToUser("Invalid JSON, It does not have " + key + " information");
			System.exit(1);
		}
		if(obj.get(key) == null) {
			logger.error("Invalid JSON, It does not have value for the " + key);
			displayToUser.displayMsgToUser("Invalid JSON, It does not have value for " + key);
			System.exit(1);
		}
		Boolean hasKeyCaptain = false;
		try {
			hasKeyCaptain = (Boolean) obj.get(key);
		} catch(Exception e) {
			logger.error("Invalid JSON, It has invalid value for Player stats Captain");
			displayToUser.displayMsgToUser("Invalid JSON, It has invalid value for Player stats Captain");
			System.exit(1);
		}
		return hasKeyCaptain;
	}

	public JSONArray containArray(JSONObject obj, String arrayKey) {
		if(obj.containsKey(arrayKey) == false) {
			logger.error("Invalid JSON, It does not have " + arrayKey + " information");
			displayToUser.displayMsgToUser("Invalid JSON, It does not have " + arrayKey + " information");
			System.exit(1);
		}
		JSONArray hasArray = (JSONArray) obj.get(arrayKey);
		if(hasArray == null || hasArray.size() == 0) {
			logger.error("Invalid JSON, It does not have value for the " + arrayKey);
			displayToUser.displayMsgToUser("Invalid JSON, It does not have value for the " + arrayKey);
			System.exit(1);
		}
		return hasArray;
	}

	public JSONObject containObjectKey(JSONObject obj, String objectKey) {
		if(obj.containsKey(objectKey) == false) {
			logger.error("Invalid JSON, It does not have " + objectKey + " information");
			displayToUser.displayMsgToUser("Invalid JSON, It does not have " + objectKey + " information");
			System.exit(1);
		}
		JSONObject jsonObject = (JSONObject) obj.get(objectKey);
		return jsonObject;
	}

	enum PlayerStats {
		SKATING, SHOOTING, CHECKING, SAVING
	}
}

