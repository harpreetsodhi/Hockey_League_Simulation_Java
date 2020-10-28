package g4dhl;

import java.sql.Date;

public interface IPlayer {

	int getPlayerId();

	String getPlayerName();

	String getPlayerPosition();

	int getPlayerAgeYear();

	int getPlayerAgeDays();

	int getPlayerSkating();

	int getPlayerShooting();

	int getPlayerChecking();

	int getPlayerSaving();

	int getMaxPlayerStatValue();

	double getPlayerStrength();

	Date getRecoveryDate();

	void agePlayer();

	boolean setPlayerId(int playerId);

	boolean setPlayerName(String playerName);

	boolean setPlayerAgeYear(int playerAgeYear);

	boolean setPlayerAgeDays(int playerDays);

	boolean setPlayerSkating(int playerSkating);

	boolean setPlayerShooting(int playerShooting);

	boolean setPlayerChecking(int playerChecking);

	boolean setPlayerSaving(int playerSaving);

	boolean setPlayerPosition(String playerPosition);

	boolean setPlayerCaptain(boolean playerCaptain);

	boolean setPlayerIsInjured(boolean playerIsInjured);

	boolean setPlayerWasInjured(boolean playerWasInjured);

	void checkPlayerInjury(float randomInjuryChance, Date recoveryDate, Date currentDate);

	boolean setRecoveryDate(Date recoveryDate);

	boolean isPlayerCaptain();

	boolean isPlayerInjured();

	boolean wasPlayerInjured();

}
