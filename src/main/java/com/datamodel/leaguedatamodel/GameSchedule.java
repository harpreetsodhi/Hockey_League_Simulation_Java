package com.datamodel.leaguedatamodel;

import com.inputoutputmodel.IPropertyLoader;
import com.inputoutputmodel.InputOutputModelAbstractFactory;
import com.statemachine.IStateMachine;
import org.apache.log4j.Logger;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GameSchedule implements IGameSchedule {

	final static Logger logger = Logger.getLogger(GameSchedule.class);

	private static final String PLAYOFF_START_DATE = "playoffStartDate";
	private static final String PLAYOFF_END_DATE = "playoffEndDate";
	private static final String GAME_TYPE_PLAYOFF = "PlayOffs";
	private static final String REGULAR_SEASON_END_DATE = "seasonEndDate";
	private static final String GAME_TYPE_REGULAR = "Regular";
	private static final String GAME_SCHEDULED = "scheduled";
	private static final int GAME_PER_TEAM = 82;
	private static final int FRACTION_OF_GAMES_PER_CATEGORY = 3;

	ITimeConcept timeConcept;
	private int gameScheduleId;
	private int leagueId;
	private int season;
	private int winningTeam;
	private int lossingTeam;
	private int gameScheduleCounter;
	private int goalsPerGame = 0;
	private int penaltiesPerGame = 0;
	private int shots = 0;
	private int saves = 0;
	private Date matchDate;
	private String gameType;
	private String status;
	private ITeam teamA;
	private ITeam teamB;
	private HashMap<ITeam, HashSet<Date>> teamScheduledMatches;
	private List<IGameSchedule> gameScheduleList;
	private List<ITeam> totalTeamList;
	private List<ITeamStanding> teamStandingList;

	public GameSchedule() {
		super();
	}

	@Override
	public int getGameScheduleId() {
		return gameScheduleId;
	}

	@Override
	public void setGameScheduleId(int gameScheduleId) {
		this.gameScheduleId = gameScheduleId;
	}

	@Override
	public int getLeagueId() {
		return leagueId;
	}

	@Override
	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}

	@Override
	public int getSeason() {
		return season;
	}

	@Override
	public void setSeason(int season) {
		this.season = season;
	}

	@Override
	public ITeam getTeamA() {
		return teamA;
	}

	@Override
	public void setTeamA(ITeam teamA) {
		this.teamA = teamA;
	}

	@Override
	public ITeam getTeamB() {
		return teamB;
	}

	@Override
	public void setTeamB(ITeam teamB) {
		this.teamB = teamB;
	}

	@Override
	public Date getMatchDate() {
		return matchDate;
	}

	@Override
	public void setMatchDate(Date matchDate) {
		this.matchDate = matchDate;
	}

	@Override
	public int getWinningTeam() {
		return winningTeam;
	}

	@Override
	public void setWinningTeam(int winningTeam) {
		this.winningTeam = winningTeam;
	}

	@Override
	public int getLossingTeam() {
		return lossingTeam;
	}

	@Override
	public void setLossingTeam(int lossingTeam) {
		this.lossingTeam = lossingTeam;
	}

	@Override
	public String getGameType() {
		return gameType;
	}

	@Override
	public void setGameType(String gameType) {
		this.gameType = gameType;
	}


	@Override
	public int getGoalsPerGame() {
		return goalsPerGame;
	}

	@Override
	public void setGoalsPerGame(int goalsPerGame) {
		this.goalsPerGame = goalsPerGame;
	}

	@Override
	public int getPenaltiesPerGame() {
		return penaltiesPerGame;
	}

	@Override
	public void setPenaltiesPerGame(int penaltiesPerGame) {
		this.penaltiesPerGame = penaltiesPerGame;
	}

	@Override
	public int getShots() {
		return shots;
	}

	@Override
	public void setShots(int shots) {
		this.shots = shots;
	}

	@Override
	public int getSaves() {
		return saves;
	}

	@Override
	public void setSaves(int saves) {
		this.saves = saves;
	}

	@Override
	public HashMap<String, Double> getScheduledGamesAverageStats(List<IGameSchedule> gameSchedules) {

		HashMap<String, Double> averageStats = new HashMap<>();
		double gameCount = 0;
		double totalGoalsPerGame = 0;
		double totalPenaltiesPerGame = 0;
		double totalShots = 0;
		double totalSaves = 0;
		double avgGoalsPerGame = 0;
		double avgPenaltiesPerGame = 0;
		double avgShots = 0;
		double avgSaves = 0;

		for(IGameSchedule game : gameSchedules) {
			gameCount += 1;
			totalGoalsPerGame += game.getGoalsPerGame();
			totalPenaltiesPerGame += game.getPenaltiesPerGame();
			totalSaves += game.getSaves();
			totalShots += game.getShots();
		}

		avgGoalsPerGame = totalGoalsPerGame / gameCount;
		avgPenaltiesPerGame = totalPenaltiesPerGame / gameCount;
		avgShots = totalShots / gameCount;
		avgSaves = totalSaves / gameCount;

		averageStats.put("Goals", avgGoalsPerGame);
		averageStats.put("Penalties", avgPenaltiesPerGame);
		averageStats.put("Shots", avgShots);
		averageStats.put("Saves", avgSaves);

		return averageStats;
	}


	@Override
	public String toString() {
		return "GameSchedule [gameScheduleId=" + gameScheduleId + ", leagueId=" + leagueId + ", season=" + season + ","
				+ " teamA=" + teamA + ", teamB=" + teamB + ", matchDate=" + matchDate + ", winningTeam=" + winningTeam + ", lossingTeam=" + lossingTeam + ", gameType=" + gameType + "]";
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public List<IGameSchedule> schedulePlayoff(IGame game, IStateMachine stateMachine) {
		InputOutputModelAbstractFactory ioFactory = InputOutputModelAbstractFactory.instance();
		IPropertyLoader propertyLoader = ioFactory.createPropertyLoader();
		teamScheduledMatches = new HashMap<>();
		gameScheduleList = new ArrayList<>();
		ILeague league = game.getLeagues().get(0);
		String[] date = league.getSimulationStartDate().toString().split("-");
		int year = Integer.parseInt(date[0]);
		Date playOffStartDate = Date.valueOf("" + (year + 1) + propertyLoader.getPropertyValue(PLAYOFF_START_DATE));
		Date playOffEndDate = Date.valueOf("" + (year + 1) + propertyLoader.getPropertyValue(PLAYOFF_END_DATE));
		LocalDate roundOneMatchDate =
				playOffStartDate.toLocalDate().plusDays(6).with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY));
		playOffStartDate = Date.valueOf(roundOneMatchDate);
		List<ITeam> playoffTeamList = new ArrayList<>();
		league.getTeamStandings().sort((standing1, standing2) -> {
			double points1 = standing1.getTotalPoints();
			double points2 = standing2.getTotalPoints();
			if(points1 > points2) {
				return -1;
			} else {
				return 0;
			}
		});
		for(ITeamStanding iTeamStanding : league.getTeamStandings().subList(0, 10)) {
			playoffTeamList.add(iTeamStanding.getTeam());
		}

		for(ITeam team : playoffTeamList) {
			for(ITeam opponentTeam : playoffTeamList) {
				addMatchSchedule(league, team, opponentTeam, playOffStartDate, playOffEndDate, league.getCurrentDate()
						, GAME_TYPE_PLAYOFF);
			}
		}
		game.getLeagues().get(0).setGameSchedules(gameScheduleList);
		logger.debug("Playoff schdeule created.");
		return gameScheduleList;
	}

	@Override
	public List<IGameSchedule> scheduleRegularSeason(IGame game, IStateMachine stateMachine) {
		InputOutputModelAbstractFactory ioFactory = InputOutputModelAbstractFactory.instance();
		IPropertyLoader propertyLoader = ioFactory.createPropertyLoader();
		teamScheduledMatches = new HashMap<>();
		gameScheduleList = new ArrayList<>();
		totalTeamList = new ArrayList<>();
		teamStandingList = new ArrayList<>();
		gameScheduleCounter = 1;

		LeagueDataModelAbstractFactory factory = LeagueDataModelAbstractFactory.instance();
		timeConcept = factory.createTimeConcept();
		IDataModelObjectUtility utility = factory.createUtility();

		ILeague league = game.getLeagues().get(0);
		Date currDate = league.getCurrentDate();
		String[] date = league.getSimulationStartDate().toString().split("-");
		int year = Integer.parseInt(date[0]);
		Date regularSeasonEndDate =
				Date.valueOf("" + (year + 1) + propertyLoader.getPropertyValue(REGULAR_SEASON_END_DATE));
		for(IConference conference : league.getConferences()) {
			IConference currentConference = conference;
			for(IDivision division : conference.getDivisions()) {
				for(ITeam team : division.getTeams()) {
					ITeamStanding teamStanding = factory.createTeamStanding();
					totalTeamList.add(team);
					teamStanding.setConferenceName(conference.getConferenceName());
					teamStanding.setDivisionName(division.getDivisionName());
					teamStanding.setTeam(team);
					teamStandingList.add(teamStanding);
				}
				IDivision currentDivision = division;
				for(ITeam team : division.getTeams()) {
					Date regularSeasonScheduleDate = currDate;
					int teamDivisionMatchesCounter = 0;
					int teamOtherDivisionMatchesCounter = 0;
					int teamOtherConferenceMatchesCounter = 0;
					while(teamDivisionMatchesCounter < (GAME_PER_TEAM / FRACTION_OF_GAMES_PER_CATEGORY)) {
						for(ITeam opponentTeam : division.getTeams()) {
							if(utility.isDifferentObject(opponentTeam, team)) {
								addMatchSchedule(league, team, opponentTeam, regularSeasonScheduleDate,
										regularSeasonEndDate, currDate, GAME_TYPE_REGULAR);
								teamDivisionMatchesCounter++;
								if(teamDivisionMatchesCounter == (GAME_PER_TEAM / FRACTION_OF_GAMES_PER_CATEGORY)) {
									break;
								}
							}
						}
					}
					boolean isDivisionMatchLimitReached = false;
					while(teamOtherDivisionMatchesCounter < (GAME_PER_TEAM / FRACTION_OF_GAMES_PER_CATEGORY)) {
						if(isDivisionMatchLimitReached) {
							break;
						}
						for(IDivision otherDivision : conference.getDivisions()) {
							if(isDivisionMatchLimitReached) {
								break;
							}
							if(utility.isDifferentObject(otherDivision, currentDivision)) {
								for(ITeam opponentTeam : otherDivision.getTeams()) {
									addMatchSchedule(league, team, opponentTeam, regularSeasonScheduleDate,
											regularSeasonEndDate, currDate, GAME_TYPE_REGULAR);
									teamOtherDivisionMatchesCounter++;
									if(teamOtherDivisionMatchesCounter == (GAME_PER_TEAM / FRACTION_OF_GAMES_PER_CATEGORY)) {
										isDivisionMatchLimitReached = true;
										break;
									}
								}
							}
						}
					}
					boolean isConferenceLevelMatchLimitReached = false;
					while(teamOtherConferenceMatchesCounter < (GAME_PER_TEAM / FRACTION_OF_GAMES_PER_CATEGORY) + 1) {
						if(isConferenceLevelMatchLimitReached) {
							break;
						}
						for(IConference otherConference : league.getConferences()) {
							if(isConferenceLevelMatchLimitReached) {
								break;
							}
							if(currentConference.equals(otherConference)) {
								for(IDivision otherConferenceDivision : otherConference.getDivisions()) {
									if(teamOtherConferenceMatchesCounter <= (GAME_PER_TEAM / FRACTION_OF_GAMES_PER_CATEGORY)) {
										for(ITeam opponentTeam : otherConferenceDivision.getTeams()) {
											addMatchSchedule(league, team, opponentTeam, regularSeasonScheduleDate,
													regularSeasonEndDate, currDate, GAME_TYPE_REGULAR);
											teamOtherConferenceMatchesCounter++;
											if(teamOtherConferenceMatchesCounter == (GAME_PER_TEAM / FRACTION_OF_GAMES_PER_CATEGORY + 1)) {
												isConferenceLevelMatchLimitReached = true;
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		game.getLeagues().get(0).setTeamStandings(teamStandingList);
		game.getLeagues().get(0).setGameSchedules(gameScheduleList);
		logger.debug("Regular season schedule created.");
		return gameScheduleList;
	}

	private void addMatchSchedule(ILeague league, ITeam team, ITeam opponentTeam, Date startDate, Date endDate,
								  Date currDate, String gameType) {
		LeagueDataModelAbstractFactory dataModelFactory = LeagueDataModelAbstractFactory.instance();
		IGameSchedule gameSchedule = dataModelFactory.createGameSchedule();
		gameSchedule.setLeagueId(league.getLeagueId());
		gameSchedule.setSeason(league.getSeason());
		gameSchedule.setGameType(gameType);
		gameSchedule.setStatus(GAME_SCHEDULED);
		gameSchedule.setTeamA(team);
		gameSchedule.setTeamB(opponentTeam);
		gameSchedule.setMatchDate(getGameDate(startDate, team, opponentTeam, endDate, currDate));
		gameSchedule.setGameScheduleId(gameScheduleCounter);
		gameScheduleCounter++;
		gameScheduleList.add(gameSchedule);
		addTeamDatesToDateExclusionList(team, opponentTeam, gameSchedule.getMatchDate());
	}

	private Date getGameDate(Date regularSeasonScheduleDate, ITeam team, ITeam opponentTeam, Date regularSeasonEndDate
			, Date currDate) {
		LeagueDataModelAbstractFactory factory = LeagueDataModelAbstractFactory.instance();
		timeConcept = factory.createTimeConcept();
		IDataModelObjectUtility utility = factory.createUtility();

		regularSeasonScheduleDate = timeConcept.getNextDate(regularSeasonScheduleDate);
		if(utility.isNotNull(teamScheduledMatches)) {
			if(utility.isNotNull(teamScheduledMatches.get(team)) && utility.isNotNull(teamScheduledMatches.get(opponentTeam))) {
				boolean isDateNotUnique = true;
				while(isDateNotUnique) {
					if(teamScheduledMatches.get(team).contains(regularSeasonScheduleDate) || teamScheduledMatches.get(opponentTeam).contains(regularSeasonScheduleDate)) {
						Date possibleDate = timeConcept.getNextDate(regularSeasonScheduleDate);
						if(possibleDate.compareTo(regularSeasonEndDate) == 0) {
							regularSeasonScheduleDate = currDate;
							isDateNotUnique = false;
						} else
							regularSeasonScheduleDate = possibleDate;
					} else {
						isDateNotUnique = false;
					}
				}
			} else if(utility.isNotNull(teamScheduledMatches.get(team))) {
				boolean isDateNotUnique = true;
				while(isDateNotUnique) {
					if(teamScheduledMatches.get(team).contains(regularSeasonScheduleDate)) {
						Date possibleDate = timeConcept.getNextDate(regularSeasonScheduleDate);
						if(possibleDate.compareTo(regularSeasonEndDate) == 0) {
							regularSeasonScheduleDate = currDate;
						} else {
							regularSeasonScheduleDate = possibleDate;
						}
					} else {
						isDateNotUnique = false;
					}
				}
			} else if(utility.isNotNull(teamScheduledMatches.get(team))) {
				boolean isDateNotUnique = true;
				while(isDateNotUnique) {
					if(teamScheduledMatches.get(opponentTeam).contains(regularSeasonScheduleDate)) {
						Date possibleDate = timeConcept.getNextDate(regularSeasonScheduleDate);
						if(possibleDate.compareTo(regularSeasonEndDate) == 0) {
							regularSeasonScheduleDate = currDate;
						} else {
							regularSeasonScheduleDate = possibleDate;
						}
					} else {
						isDateNotUnique = false;
					}
				}
			}
		}
		return regularSeasonScheduleDate;
	}

	private void addTeamDatesToDateExclusionList(ITeam team, ITeam opponentTeam, Date matchDate) {
		if(teamScheduledMatches.get(team) == null) {
			HashSet<Date> dates = new HashSet<>();
			dates.add(matchDate);
			teamScheduledMatches.put(team, dates);
		} else {
			teamScheduledMatches.get(team).add(matchDate);
		}
		if(teamScheduledMatches.get(opponentTeam) == null) {
			HashSet<Date> dates = new HashSet<>();
			dates.add(matchDate);
			teamScheduledMatches.put(opponentTeam, dates);
		} else {
			teamScheduledMatches.get(opponentTeam).add(matchDate);
		}
	}
}
