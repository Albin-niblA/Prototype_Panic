package model.upgrades;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpgradeManager {
    private List<Upgrades> upgrades = new ArrayList<>();

    public List<Upgrades> rollThree() {
        // collect all upgrades in a list, shuffle it, and return 3;
        List<Upgrades> allUpgrades = new ArrayList<>(List.of(Upgrades.values()));
        Collections.shuffle(allUpgrades);
        upgrades = allUpgrades.subList(0, 3);
        return upgrades;
    }

    public List<Upgrades> getUpgrades() {
        return upgrades;
    }
}
