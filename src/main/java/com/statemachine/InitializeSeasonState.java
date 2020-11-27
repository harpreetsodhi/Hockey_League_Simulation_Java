package com.statemachine;
import com.datamodel.leaguedatamodel.LeagueDataModelFactory;
import com.datamodel.leaguedatamodel.LeagueDataModelAbstractFactory;
import com.datamodel.leaguedatamodel.IGameSchedule;

public class InitializeSeasonState implements IState {

	
	@Override
	public void entry() {
	}


	@Override
	public IState doTask() {
		StateMachineAbstractFactory stateFactory = StateMachineAbstractFactory.instance();
		IStateMachine stateMachine = stateFactory.createStateMachine(null);
       
		LeagueDataModelAbstractFactory dataModelFactory = LeagueDataModelFactory.getNewInstance();
		IGameSchedule gameSchedule = dataModelFactory.createGameSchedule();
		gameSchedule.scheduleRegularSeason(stateMachine.getGame(), stateMachine);
		return stateFactory.createAdvanceTimeState();
	}
}