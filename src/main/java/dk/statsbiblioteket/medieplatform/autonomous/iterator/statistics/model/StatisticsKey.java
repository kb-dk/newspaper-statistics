package dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.model;

public class StatisticsKey implements Comparable<StatisticsKey> {
    private final String type;
    private final String name;

    public StatisticsKey(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public StatisticsKey(String type) {
        this.type = type;
        name = null;
    }

    /**
     * The type of the statistics. Used at the tag string when outputting. Must be defined
     */
    public String getType() {
        return type;
    }
    /**
     * The name of the statistics. Used at the name attribute. Optional.
     */
    public String getName() {
        return name;
    }

    public boolean isNameDefined() {
        return name != null;
    }

    @Override
    public int compareTo(StatisticsKey sk) {
        if (isNameDefined()) {
            return name.compareTo(sk.getName());
        } else {
            return type.compareTo(sk.getType());
        }
    }
}
