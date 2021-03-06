package com.datamodeltest.leaguedatamodeltest;

import com.datamodel.leaguedatamodel.*;

import java.sql.Date;

public class MockGame {

	public static Game mockGame(int conf, int div, int teams) {
		Game game = new Game();
		ILeague league = new League();
		league.setLeagueName("mock");
		String str = "2020-09-30";
		league.setCurrentDate(Date.valueOf(str));
		league.setSimulationStartDate(league.getCurrentDate());
		for(int i = 0; i < conf; i++) {
			IConference conferenceObj = new Conference();
			conferenceObj.setConferenceId(i);
			conferenceObj.setConferenceName("C" + i);
			for(int j = 0; j < div; j++) {
				IDivision divisionObj = new Division();
				divisionObj.setDivisionName("D" + j);
				divisionObj.setDivisionId(Integer.parseInt("" + i + j));
				for(int k = 0; k < teams; k++) {
					ITeam teamObj = new Team();
					teamObj.setTeamId(Integer.parseInt("" + i + j + k));
					teamObj.setTeamName("T" + i + j + k);
					divisionObj.addTeam(teamObj);
				}
				conferenceObj.addDivision(divisionObj);
			}
			league.addConference(conferenceObj);
		}
		game.addLeague(league);
		return game;
	}
}