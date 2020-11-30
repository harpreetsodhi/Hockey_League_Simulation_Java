package com.datamodeltest.leaguedatamodeltest;

import com.datamodel.gameplayconfig.GamePlayConfigAbstractFactory;
import com.datamodel.gameplayconfig.GamePlayConfigFactory;
import com.datamodel.leaguedatamodel.IDrafting;
import com.datamodel.leaguedatamodel.ILeague;
import com.datamodel.leaguedatamodel.ITeam;
import com.datamodel.leaguedatamodel.LeagueDataModelAbstractFactory;
import com.inputoutputmodel.InputOutputModelAbstractFactory;
import com.inputoutputmodel.InputOutputModelFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DraftingTest {

	ILeague league;

	private final LeagueDataModelAbstractFactory leagueDataModelAbstractFactory =
			LeagueDataModelAbstractFactory.instance();
	private final IDrafting drafting = leagueDataModelAbstractFactory.createDrafting();

	@BeforeClass
	public static void setFactory() {
		LeagueDataModelAbstractFactory.setFactory(new LeagueDataModelFactoryTest());
		InputOutputModelAbstractFactory.setFactory(new InputOutputModelFactory());
	}

	@Before
	public void createLeague() {
		GamePlayConfigAbstractFactory.setFactory(new GamePlayConfigFactory());
		LeagueDataModelAbstractFactory.setFactory(new LeagueDataModelFactoryTest());
		LeagueMock leagueMock = new LeagueMock();
		league = leagueMock.getLeague();
	}

	@Test
	public void getDraftPickTest() {
		for(ITeam team : league.getAllTeams()) {
			drafting.createDraftPick(team);
			Assert.assertEquals(7, drafting.getDraftPick(team).length);
		}
	}

	@Test
	public void getDraftPickByRoundTest() {
		ITeam pickerTeam = leagueDataModelAbstractFactory.createTeam();
		ITeam ownerTeam = leagueDataModelAbstractFactory.createTeam();
		drafting.createDraftPick(ownerTeam);
		drafting.setDraftPickByRound(ownerTeam, pickerTeam, 2);
		Assert.assertEquals(pickerTeam, drafting.getDraftPickByRound(ownerTeam, 2));
	}
}

