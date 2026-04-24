package model.upgrades;

public enum Rarity {
    Common(0.50f, "8C8C8C", "F2F2F2"),
    Uncommon(0.25f, "F5971B", "FFD094"),
    Rare(0.15f, "48A0F7", "A7D2FC"),
    Epic(0.10f, "D76EFA", "F9A7FA");

    float chance;
    String gradientStart;
    String gradientEnd;

    Rarity(float chance, String gradientStart, String gradientEnd) {
        this.chance = chance;
        this.gradientStart = gradientStart;
        this.gradientEnd = gradientEnd;
    }

    public float getChance() {
        return chance;
    }

    public String getGradientStart() {
        return gradientStart;
    }

    public String getGradientEnd() {
        return gradientEnd;
    }
}
