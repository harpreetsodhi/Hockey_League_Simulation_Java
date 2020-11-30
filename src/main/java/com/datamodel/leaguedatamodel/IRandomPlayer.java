package com.datamodel.leaguedatamodel;

import java.util.List;

public interface IRandomPlayer {

	List<IPlayer> getRandomPlayers(int totalPlayers, String currentDate);

	String generatePlayerName();

	int generatePlayerSkating(String position);

	int generatePlayerShooting(String position);

	int generatePlayerChecking(String position);

	int generatePlayerSaving(String position);

	int generatePlayerBirthDay();

	int generatePlayerBirthMonth();

	int generatePlayerBirthYear(String currentYear);


}
