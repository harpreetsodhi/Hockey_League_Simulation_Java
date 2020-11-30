package com.datamodeltest.leaguedatamodeltest;

import com.datamodel.leaguedatamodel.IPlayer;
import com.datamodel.leaguedatamodel.IRandomPlayer;
import com.datamodel.leaguedatamodel.LeagueDataModelAbstractFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class RandomPlayerTest {

	private final LeagueDataModelAbstractFactory leagueDataModelAbstractFactory =
			LeagueDataModelAbstractFactory.instance();
	private final IRandomPlayer randomPlayer = leagueDataModelAbstractFactory.createRandomPlayer();

	@BeforeClass
	public static void createConference() {
		LeagueDataModelAbstractFactory.setFactory(new LeagueDataModelFactoryTest());
	}

	@Test
	public void generatePlayerTest() {
		String currentDate = "2020-11-21";
		int playersToGenerate = 5;
		List<IPlayer> randomPlayers = randomPlayer.getRandomPlayers(playersToGenerate, currentDate);
		Assert.assertEquals(playersToGenerate, randomPlayers.size());
	}
}