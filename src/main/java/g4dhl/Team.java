package g4dhl;

import java.util.ArrayList;

public class Team implements  ITeam{
    private String teamName;
    private IGeneralManager generalManager;
    private IHeadCoach headCoach;
    private ArrayList<IPlayer> players;

    @Override
    public int getTeamId() {
        return 0;
    }

    @Override
    public String getTeamName() {
        return teamName;
    }

    @Override
    public void setTeam(int id) {

    }

    @Override
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public IGeneralManager getGeneralManager() {
        return generalManager;
    }

    @Override
    public void setGeneralManager(IGeneralManager generalManager) {
        this.generalManager = generalManager;
    }

    @Override
    public IHeadCoach getHeadCoach() {
        return headCoach;
    }

    @Override
    public void setHeadCoach(IHeadCoach headCoach) {
        this.headCoach = headCoach;
    }

    @Override
    public ArrayList<IPlayer> getPlayers() {
        return players;
    }

    @Override
    public IPlayer getPlayer(int index) {
        return null;
    }

    @Override
    public void addPlayer(IPlayer player) {

    }

    @Override
    public void setPlayers(ArrayList<IPlayer> players) {
        this.players = players;
    }
}
