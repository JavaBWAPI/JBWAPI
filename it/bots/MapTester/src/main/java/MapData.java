
class MapData {
    private final String name;
    private final int startLocationsAmount;
    private final int areaAmount;
    private final int basesAmount;
    private final int chokeAmount;

    MapData(String name, int startLocationsAmount, int areaAmount, int basesAmount, int chokeAmount) {
        this.name = name;
        this.startLocationsAmount = startLocationsAmount;
        this.areaAmount = areaAmount;
        this.basesAmount = basesAmount;
        this.chokeAmount = chokeAmount;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapData mapData = (MapData) o;
        return startLocationsAmount == mapData.startLocationsAmount &&
                areaAmount == mapData.areaAmount &&
                basesAmount == mapData.basesAmount &&
                chokeAmount == mapData.chokeAmount &&
                name.equals(mapData.name);
    }

    public String toString() {
        return "MapData{" +
                "name='" + name + '\'' +
                ", startLocationsAmount=" + startLocationsAmount +
                ", areaAmount=" + areaAmount +
                ", basesAmount=" + basesAmount +
                ", chokeAmount=" + chokeAmount +
                '}';
    }
}
