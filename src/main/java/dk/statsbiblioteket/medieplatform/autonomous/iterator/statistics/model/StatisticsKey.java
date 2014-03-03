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

    /**
     * May be overriden by subclasses to provide a specific way to add keys to this key.
     */
    public void add(StatisticsKey keyToAdd) {}

    @Override
    public int compareTo(StatisticsKey sk) {
        if (isNameDefined()) {
            return name.compareTo(sk.getName());
        } else {
            return type.compareTo(sk.getType());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StatisticsKey)) {
            return false;
        }

        StatisticsKey that = (StatisticsKey) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (!type.equals(that.type)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StatisticsKey{" +
               "type='" + type + '\'' +
               ", name='" + name + '\'' +
               '}';
    }
}
