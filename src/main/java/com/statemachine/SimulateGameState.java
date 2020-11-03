package com.statemachine;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import com.datamodel.leaguedatamodel.IGame;
import com.datamodel.leaguedatamodel.IGameSchedule;
import com.datamodel.leaguedatamodel.ISimulateMatch;
import com.datamodel.leaguedatamodel.ITeam;
import com.datamodel.leaguedatamodel.SimulateMatch;
import com.inputoutputmodel.IPropertyLoader;
import com.inputoutputmodel.PropertyLoader;

public class SimulateGameState implements IState {

	private static final String TRADE_END_MONTH  = "tradeEndMonth"; 
	private static final String STATUS_SCHEDULED = "scheduled";
	private static final String STATUS_PLAYED = "played";
	StateMachine stateMachine;

	public SimulateGameState(StateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}

	@Override
	public void entry() {
	}

	@Override
	public void exit() {
		stateMachine.setCurrentState(stateMachine.getAging());
	}

	@Override
	public IState doTask() {
		HashSet<ITeam> gameDayTeams = new HashSet<>();
		ISimulateMatch simulateMatch = new SimulateMatch();
		IGame game = stateMachine.getGame();
		for (IGameSchedule gameSchedule : game.getLeagues().get(0).getGameSchedules()) {
			Date curreDate = game.getLeagues().get(0).getCurrentDate();
			Date matchDate = gameSchedule.getMatchDate();
			if (curreDate.compareTo(matchDate) == 0 && gameSchedule.getStatus().equals(STATUS_SCHEDULED)) {
				simulateMatch.simulateMatchResult(gameSchedule.getTeamA(), gameSchedule.getTeamA().getTeamStrength(),
						gameSchedule.getTeamB(), gameSchedule.getTeamA().getTeamStrength(),
						game.getLeagues().get(0).getGamePlayConfig().getGameResolver().getRandomWinChance(), game);
				gameDayTeams.add(gameSchedule.getTeamA());
				gameDayTeams.add(gameSchedule.getTeamB());
				gameSchedule.setStatus(STATUS_PLAYED);
				stateMachine.setGameDayTeams(gameDayTeams);
			}
		}
		if (gameDayTeams != null) {
			stateMachine.setCurrentState(stateMachine.getInjuryCheck());
			stateMachine.getCurrentState().entry();
		}
		String[] date = stateMachine.getGame().getLeagues().get(0).getSimulationStartDate().toString().split("-");
		int year = Integer.parseInt(date[0]);
		IPropertyLoader propertyLoader = new PropertyLoader();
		Date tradeEndMonth = Date.valueOf("" + (year + 1) + propertyLoader.getPropertyValue(TRADE_END_MONTH));
		LocalDate tradeEndDate = tradeEndMonth.toLocalDate().with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
		Date lastTradeDate = Date.valueOf(tradeEndDate);
		Date currDate = stateMachine.getGame().getLeagues().get(0).getCurrentDate();
		if (currDate.compareTo(lastTradeDate) < 0) {
			return stateMachine.getExecuteTrades();
		}
		return stateMachine.getAging();
	}
}