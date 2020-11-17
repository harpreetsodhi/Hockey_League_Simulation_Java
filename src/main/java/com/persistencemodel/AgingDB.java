package com.persistencemodel;

import com.datamodel.gameplayconfig.AgingConfig;
import com.datamodel.gameplayconfig.IAgingConfig;
import com.datamodel.gameplayconfig.IGameplayConfig;
import org.json.simple.JSONObject;

public class AgingDB implements IAgingDB {

    @Override
    public void loadAging(JSONObject agingObject, IGameplayConfig gameplayConfig) {
        IAgingConfig aging = new AgingConfig();
        aging.setAgingId((int) (long) agingObject.get("agingId"));
        aging.setAverageRetirementAge((int) (long) agingObject.get("averageRetirementAge"));
        aging.setAgingId((int) (long) agingObject.get("maximumAge"));
        gameplayConfig.setAging(aging);
    }
}
