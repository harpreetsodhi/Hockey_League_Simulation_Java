package com.datamodeltest.leaguedatamodeltest;

import com.datamodel.gameplayconfig.GamePlayConfigAbstractFactory;
import com.datamodel.gameplayconfig.GamePlayConfigFactory;
import com.datamodel.leaguedatamodel.*;
import com.inputoutputmodel.IDisplayTradingOffers;
import com.inputoutputmodel.InputOutputModelAbstractFactory;
import com.inputoutputmodel.InputOutputModelFactory;
import com.inputoutputmodeltest.InputOutputModelFactoryTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TradingTest {

	ILeague league;
	ITrading trading;

	@Before
	public void createLeague() {
		GamePlayConfigAbstractFactory.setFactory(new GamePlayConfigFactory());
		LeagueDataModelAbstractFactory.setFactory(new LeagueDataModelFactoryTest());
		InputOutputModelAbstractFactory.setFactory(new InputOutputModelFactory());
		LeagueMock leagueMock = new LeagueMock();
		league = leagueMock.getLeague();
		trading = LeagueDataModelAbstractFactory.instance().createTrading();
	}

	@Test
	public void isInterestedInPlayersTradeTest() {
		Assert.assertFalse(trading.isInterestedInPlayersTrade());
	}

	@Test
	public void getOfferingTeamTest() {
		ITeam team = league.getAllTeams().get(0);
		trading.setOfferingTeam(team);
		Assert.assertEquals(team, trading.getOfferingTeam());
	}

	@Test
	public void getAcceptingTeamTest() {
		ITeam team = league.getAllTeams().get(0);
		trading.setAcceptingTeam(team);
		Assert.assertEquals(team, trading.getAcceptingTeam());
	}

	@Test
	public void getOfferedPlayersTest() {
		List<IPlayer> players = new ArrayList<>();
		trading.setOfferedPlayers(players);
		Assert.assertEquals(players, trading.getOfferedPlayers());
	}

	@Test
	public void getRequestedPlayersTest() {
		List<IPlayer> players = new ArrayList<>();
		trading.setRequestedPlayers(players);
		Assert.assertEquals(players, trading.getRequestedPlayers());
	}

	@Test
	public void getPossibleTradeCombinationsTest() {
		List<List<Integer>> tradingCombinations = new ArrayList<>();
		int noOfPlayers = 30;
		int maxPlayersPerTrade = 3;
		trading.setPossibleTradeCombinations(noOfPlayers - 1, maxPlayersPerTrade, tradingCombinations);
		Assert.assertEquals(tradingCombinations.size(), 4525);
	}

	@Test
	public void isTradePossibleUserTeamTest() {
		ITeam userTeam = league.getAllTeams().get(0);
		userTeam.setTeamCreatedBy("user");
		Assert.assertFalse("User team cannot trade", trading.isTradePossible(userTeam));
	}

	@Test
	public void isTradePossibleAiTeamTest() {
		ITeam aiTeamWithLowerLossPoint = league.getAllTeams().get(0);
		aiTeamWithLowerLossPoint.setTeamCreatedBy("import");
		aiTeamWithLowerLossPoint.setLossPointCount(2);
		Assert.assertFalse("Team cannot trade if loss point is less",
				trading.isTradePossible(aiTeamWithLowerLossPoint));
	}

	@Test
	public void generateBestTradeOfferWithAiTeamTest() {

		ITeam team = league.getAllTeams().get(0);
		trading.generateBestTradeOffer(team);
		boolean isInterested = trading.isInterestedInPlayersTrade();
		if(isInterested) {
			Assert.assertEquals(team, trading.getOfferingTeam());
			Assert.assertTrue(trading.getOfferingTeam().getPlayers().containsAll(trading.getOfferedPlayers()));
			Assert.assertTrue(trading.getAcceptingTeam().getPlayers().containsAll(trading.getRequestedPlayers()));
			Assert.assertNotEquals(trading.getOfferingTeam(), trading.getAcceptingTeam());
			Assert.assertTrue(trading.getOfferedPlayers().size() <= league.getGamePlayConfig().getTrading().getMaxPlayersPerTrade());
			Assert.assertTrue(trading.getRequestedPlayers().size() <= league.getGamePlayConfig().getTrading().getMaxPlayersPerTrade());
		} else {
			Assert.assertNull(trading.getOfferingTeam());
			Assert.assertNull(trading.getAcceptingTeam());
			Assert.assertNull(trading.getOfferedPlayers());
			Assert.assertNull(trading.getRequestedPlayers());
		}
	}

	@Test
	public void generateAiTradeOfferToUserTest() {
		ITeam aiTeam = league.getAllTeams().get(0);
		aiTeam.setTeamCreatedBy("import");

		ITeam userTeam = league.getAllTeams().get(1);
		userTeam.setTeamCreatedBy("user");

		InputOutputModelAbstractFactory.setFactory(new InputOutputModelFactoryTest());
		IDisplayTradingOffers displayTradingOffers =
				InputOutputModelAbstractFactory.instance().createDisplayTradingOffers();
		Assert.assertFalse(trading.generateAiTradeOfferToUser(aiTeam.getPlayers(), userTeam.getPlayers(),
				displayTradingOffers));
	}

	@Test
	public void generateAiTradeOfferToAiTest() {
		league.getGamePlayConfig().getTrading().setRandomAcceptanceChance(1.0f);
		league.getGamePlayConfig().getTrading().getGMTable().setGambler(0.0f);
		league.getGamePlayConfig().getTrading().getGMTable().setNormal(0.0f);
		league.getGamePlayConfig().getTrading().getGMTable().setShrewd(0.0f);
		ITeam team = league.getAllTeams().get(0);
		Trading trading = new Trading();
		Assert.assertTrue(trading.generateAiTradeOfferToAi(team));
	}

	@Test
	public void tradePlayersTest() {
		ITeam offeringTeam = league.getAllTeams().get(0);
		ITeam acceptingTeam = league.getAllTeams().get(1);
		List<IPlayer> offeredPlayers = new ArrayList<>();
		List<IPlayer> requestedPlayers = new ArrayList<>();

		trading.setOfferingTeam(offeringTeam);
		trading.setAcceptingTeam(acceptingTeam);
		trading.setOfferedPlayers(offeredPlayers);
		trading.setRequestedPlayers(requestedPlayers);

		offeringTeam.setLossPointCount(8);
		trading.tradePlayers();
		Assert.assertEquals(0, offeringTeam.getLossPointCount());
	}

	@Test
	public void tradeDraftTest() {
		ITeam team = league.getAllTeams().get(0);
		team.setLossPointCount(8);
		IDrafting drafting = new DraftingMock();
		trading.tradeDraft(team, drafting);
		Assert.assertEquals(0, team.getLossPointCount());
	}

	@Test
	public void generateDraftPickOfferToUserTest() {
		ITeam aiTeam = league.getAllTeams().get(0);
		aiTeam.setTeamCreatedBy("import");

		ITeam userTeam = league.getAllTeams().get(1);
		userTeam.setTeamCreatedBy("user");

		InputOutputModelAbstractFactory.setFactory(new InputOutputModelFactoryTest());
		IDisplayTradingOffers displayTradingOffers =
				InputOutputModelAbstractFactory.instance().createDisplayTradingOffers();
		Assert.assertFalse(trading.generateDraftPickOfferToUser(aiTeam, 1, aiTeam.getPlayers(), displayTradingOffers));
	}

	@Test
	public void generateDraftPickOfferToAiTest() {
		league.getGamePlayConfig().getTrading().setRandomAcceptanceChance(1.0f);
		ITeam team = league.getAllTeams().get(0);
		Trading trading = new Trading();
		Assert.assertTrue(trading.generateDraftPickOfferToAi());
	}
}
