package com.datamodeltest.leaguedatamodeltest;

import com.datamodel.gameplayconfig.GamePlayConfigAbstractFactory;
import com.datamodel.gameplayconfig.GamePlayConfigFactory;
import com.datamodel.leaguedatamodel.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DraftingTest {

    ILeague league;

    private LeagueDataModelAbstractFactory leagueDataModelAbstractFactory = LeagueDataModelAbstractFactory.instance();
    private IDrafting drafting = leagueDataModelAbstractFactory.createDrafting();

    @BeforeClass
    public static void createConference(){
        LeagueDataModelAbstractFactory.setFactory(new LeagueDataModelFactoryTest());
    }

    @Before
    public void createLeague(){
        GamePlayConfigAbstractFactory.setFactory(new GamePlayConfigFactory());
        LeagueDataModelAbstractFactory.setFactory(new LeagueDataModelFactoryTest());
        LeagueMock leagueMock = new LeagueMock();
        league = leagueMock.getLeague();
    }

    @Test
    public void getDraftPickTest() {
        for (ITeam team : league.getAllTeams()) {
            drafting.createDraftPick(team);
            Assert.assertEquals(7,drafting.getDraftPick(team).length);
        }
    }

    @Test
    public void getDraftPickByRoundTest() {
        ITeam pickerTeam = new Team();
        ITeam ownerTeam = new Team();
        drafting.createDraftPick(ownerTeam);
        drafting.setDraftPickByRound(ownerTeam,pickerTeam,2);
        Assert.assertEquals(pickerTeam,drafting.getDraftPickByRound(ownerTeam,2));
    }
}

