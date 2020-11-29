package com.statemachinetest;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import com.datamodel.gameplayconfig.GamePlayConfigAbstractFactory;
import com.datamodel.gameplayconfig.GamePlayConfigFactory;
import com.datamodel.leaguedatamodel.IGame;
import com.datamodel.leaguedatamodel.ILeague;
import com.datamodel.leaguedatamodel.LeagueDataModelAbstractFactory;
import com.datamodeltest.leaguedatamodeltest.LeagueDataModelFactoryTest;
import com.statemachine.StateMachineAbstractFactory;
import com.statemachine.StateMachineFactory;

public class TrophyStateTest {

    static ILeague league;
    static StateMachineAbstractFactory stateFactory = null;
    static LeagueDataModelAbstractFactory factory = null;

    @BeforeClass
    public static void loadMockLeague() {
        GamePlayConfigAbstractFactory.setFactory(new GamePlayConfigFactory());
        LeagueDataModelAbstractFactory.setFactory(new LeagueDataModelFactoryTest());
        StateMachineLeagueMock leagueMock = new StateMachineLeagueMock();
        league = leagueMock.league;
        StateMachineAbstractFactory.setFactory(new StateMachineFactory());
        factory = LeagueDataModelAbstractFactory.instance();
        stateFactory = StateMachineAbstractFactory.instance();
        IGame game = factory.createGame();
        game.addLeague(league);
    }

    @Test
    public void entryTest() {
        stateFactory.createTrophySystemState().entry();
        assertNotNull(stateFactory);
    }

    @Test
    public void doTaskTest() {
        stateFactory.createTrophySystemState().doTask();
        assertNotNull(stateFactory);
    }


}
