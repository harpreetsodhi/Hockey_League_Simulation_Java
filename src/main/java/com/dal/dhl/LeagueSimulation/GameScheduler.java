package com.dal.dhl.LeagueSimulation;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.dal.dhl.stateMachine.DHLStateMachine;

import g4dhl.Game;
import g4dhl.GameSchedule;
import g4dhl.IConference;
import g4dhl.IDivision;
import g4dhl.IGameSchedule;
import g4dhl.ILeague;
import g4dhl.ITeam;
import g4dhl.ITeamStanding;
import g4dhl.TeamStanding;

public class GameScheduler {

	HashMap<ITeam, HashSet<Date>> teamScheduledMatches;
	ArrayList<IGameSchedule> gameScheduleList;
	ArrayList<ITeam> totalTeamList;
	ArrayList<ITeamStanding> teamStandingList;
	int gameScheduleCounter;
	int gamePerTeam;
	TimeConcept timeConcept;
	
	public ArrayList<IGameSchedule> schedulePlayoff(Game game, DHLStateMachine stateMachine) {

		gameScheduleList = new ArrayList<>();
		String gameType = "PlayOffs";
		ILeague league = game.getLeagues().get(0);
		String[] date = league.getSimulationStartDate().toString().split("-");
		int year = Integer.parseInt(date[0]);
		Date playOffStartDate = Date.valueOf(""+(year+1)+"-04-01");
		Date playOffEndDate = Date.valueOf(""+(year+1)+"-06-01");
		LocalDate roundOneMatchDate = playOffStartDate.toLocalDate().plusDays(6)
				.with( TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY) );
		playOffStartDate = Date.valueOf(roundOneMatchDate);
		HashMap<Integer, ITeam> playoffTeamList = new HashMap<>();
		for (ITeamStanding iTeamStanding : league.getTeamStandings()) {
			playoffTeamList.put(iTeamStanding.getTotalPoints(), iTeamStanding.getTeam());
		}
		ArrayList<ITeam> teamPlayoffs = new ArrayList<>();
		Map<Integer, ITeam> sortedTeamStanding = new TreeMap<>(Collections.reverseOrder());
		sortedTeamStanding.putAll(playoffTeamList);
		for (Entry<Integer, ITeam> entry : sortedTeamStanding.entrySet()) {  
			teamPlayoffs.add(entry.getValue());
		} 
		for (ITeam team : teamPlayoffs) {
			for (ITeam opponentTeam : teamPlayoffs) {
				addMatchSchedule(league, team, opponentTeam,playOffStartDate, playOffEndDate, league.getCurrentDate(), gameType);
			}
		}
		game.getLeagues().get(0).setGameSchedules(gameScheduleList);
		return gameScheduleList;
	}

	public ArrayList<IGameSchedule> scheduleRegularSeason(Game game, DHLStateMachine stateMachine) {
		teamScheduledMatches = new HashMap<ITeam, HashSet<Date>>();
		gameScheduleList = new ArrayList<>();
		totalTeamList = new ArrayList<ITeam>();
		teamStandingList = new ArrayList<>();
		gameScheduleCounter=1;
		gamePerTeam = 82;
		timeConcept = new TimeConcept();
		String gameType = "Regular";
		ILeague league = game.getLeagues().get(0);
		Date currDate = league.getCurrentDate();
		String[] date = league.getSimulationStartDate().toString().split("-");
		int year = Integer.parseInt(date[0]);
		Date regularSeasonEndDate = Date.valueOf(""+(year+1)+"-04-01");
		for (IConference conference : league.getConferences()) {
			int currentConferenceID = conference.getConferenceId();
			for (IDivision division : conference.getDivisions()) {

				for (ITeam team : division.getTeams()) {
					ITeamStanding teamStanding = new TeamStanding();
					totalTeamList.add(team);
					teamStanding.setConferenceName(conference.getConferenceName());
					teamStanding.setDivisionName(division.getDivisionName());
					teamStanding.setTeam(team);
					teamStandingList.add(teamStanding);
				}
				int currentDivisionId = division.getDivisionId();
				for (ITeam team : division.getTeams()) {
					Date regularSeasonScheduleDate = currDate;
					int teamDivisionMatchesCounter =0;
					int teamOtherDivisionMatchesCounter =0;
					int teamOtherConferenceMatchesCounter =0;
					while(teamDivisionMatchesCounter<(gamePerTeam/3)) {
						for (ITeam opponentTeam : division.getTeams()) {
							if(opponentTeam.getTeamId()!= team.getTeamId()) {
								addMatchSchedule(league, team, opponentTeam,regularSeasonScheduleDate, regularSeasonEndDate, currDate, gameType);
								teamDivisionMatchesCounter++;
								if(teamDivisionMatchesCounter==(gamePerTeam/3)) {
									break;
								}
							}
						}
					}
					// iterating till the team gets to play 1/3 of the matches with team from the other divisions than theirs
					boolean isDivisionMatchLimitReached = false;
					while(teamOtherDivisionMatchesCounter<(gamePerTeam/3)) {
						if(isDivisionMatchLimitReached) {
							break;
						}
						for (IDivision otherDivision : conference.getDivisions()) {
							if(isDivisionMatchLimitReached) {
								break;
							}
							if(otherDivision.getDivisionId()!= currentDivisionId ) {
								for (ITeam opponentTeam : otherDivision.getTeams()) {
									addMatchSchedule(league, team, opponentTeam,regularSeasonScheduleDate, regularSeasonEndDate, currDate, gameType);
									teamOtherDivisionMatchesCounter++;
									if(teamOtherDivisionMatchesCounter==(gamePerTeam/3)) {
										isDivisionMatchLimitReached = true;
										break;
									}	
								}

							}
						}
					}
					boolean isConferenceLevelMatchLimitReached = false;
					while(teamOtherConferenceMatchesCounter<(gamePerTeam/3)+1) {
						if(isConferenceLevelMatchLimitReached) {
							break;
						}
						for (IConference otherConference : league.getConferences()) {
							if(isConferenceLevelMatchLimitReached) {
								break;
							}
							if(currentConferenceID!= otherConference.getConferenceId()) {
								for (IDivision otherConferenceDivision : otherConference.getDivisions()) {
									if(teamOtherConferenceMatchesCounter<(gamePerTeam/3)) {
										for (ITeam opponentTeam : otherConferenceDivision.getTeams()) {
											addMatchSchedule(league, team, opponentTeam,regularSeasonScheduleDate, regularSeasonEndDate, currDate, gameType);
											teamOtherConferenceMatchesCounter++;
											if(teamOtherConferenceMatchesCounter==(gamePerTeam/3+1)) {
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
		stateMachine.setTeamList(totalTeamList);
		game.getLeagues().get(0).setTeamStandings(teamStandingList);
		return gameScheduleList;
	}

	private void addMatchSchedule(ILeague league, ITeam team, ITeam opponentTeam, Date startDate, Date endDate, Date currDate, String gameType) {
		IGameSchedule gameSchedule = new GameSchedule();
		gameSchedule.setLeagueId(league.getLeagueId());
		gameSchedule.setSeason(league.getSeason());
		gameSchedule.setGameType(gameType);
		gameSchedule.setTeamA(team);
		gameSchedule.setTeamB(opponentTeam);
		gameSchedule.setMatchDate(getGameDate(startDate, team, opponentTeam, endDate, currDate));
		gameSchedule.setGameScheduleId(gameScheduleCounter);
		gameScheduleCounter++;
		gameScheduleList.add(gameSchedule);
		addTeamDatesToDateExclusionList(team, opponentTeam, gameSchedule.getMatchDate());
		
	}

	private Date getGameDate(Date regularSeasonScheduleDate, ITeam team, ITeam opponentTeam, Date regularSeasonEndDate, Date currDate) {
		TimeConcept timeConcept = new TimeConcept();
		regularSeasonScheduleDate = timeConcept.getNextDate(regularSeasonScheduleDate);
		if(teamScheduledMatches.get(team)!=null && teamScheduledMatches.get(opponentTeam)!=null) {
			boolean isDateNotUnique = true;
			while(isDateNotUnique) {
				if(teamScheduledMatches.get(team).contains(regularSeasonScheduleDate) || teamScheduledMatches.get(opponentTeam).contains(regularSeasonScheduleDate)) {
					Date possibleDate = timeConcept.getNextDate(regularSeasonScheduleDate);
					if(possibleDate.compareTo(regularSeasonEndDate)==0) {
						regularSeasonScheduleDate = currDate;
						isDateNotUnique = false;
					}
					else 
						regularSeasonScheduleDate = possibleDate;
				}
				else {
					isDateNotUnique = false;
				}
			}
		}
		else if(teamScheduledMatches.get(team)!=null ) {
			boolean isDateNotUnique = true;
			while(isDateNotUnique) {
				if(teamScheduledMatches.get(team).contains(regularSeasonScheduleDate) ) {
					Date possibleDate = timeConcept.getNextDate(regularSeasonScheduleDate);
					if(possibleDate.compareTo(regularSeasonEndDate)==0) {
						regularSeasonScheduleDate = currDate;
					}
					else {
						regularSeasonScheduleDate = possibleDate;
					}
				}
				else {
					isDateNotUnique = false;
				}
			}
		}
		else if(teamScheduledMatches.get(team)!=null) {
			boolean isDateNotUnique = true;
			while(isDateNotUnique) {
				if( teamScheduledMatches.get(opponentTeam).contains(regularSeasonScheduleDate)) {
					Date possibleDate = timeConcept.getNextDate(regularSeasonScheduleDate);
					if(possibleDate.compareTo(regularSeasonEndDate)==0) {
						regularSeasonScheduleDate = currDate;
					}
					else {
						regularSeasonScheduleDate = possibleDate;
					}
				}
				else {
					isDateNotUnique = false;
				}
			}
		}
		return regularSeasonScheduleDate;
	}

	private void addTeamDatesToDateExclusionList(ITeam team, ITeam opponentTeam, Date matchDate) {
		if(teamScheduledMatches.get(team) != null) {
			teamScheduledMatches.get(team).add(matchDate);
		}
		else {
			HashSet<Date> dates = new HashSet<>();
			dates.add(matchDate);
			teamScheduledMatches.put(team, dates);
		}
		if(teamScheduledMatches.get(opponentTeam) != null) {
			teamScheduledMatches.get(opponentTeam).add(matchDate);
		}
		else {
			HashSet<Date> dates = new HashSet<>();
			dates.add(matchDate);
			teamScheduledMatches.put(opponentTeam, dates);
		}
	}
}