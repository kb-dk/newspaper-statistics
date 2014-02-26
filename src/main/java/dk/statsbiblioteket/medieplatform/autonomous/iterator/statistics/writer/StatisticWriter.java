package dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.writer;

import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.model.StatisticsKey;

/**
 * Defines the statistics write operations which can be performed.
 */
public interface StatisticWriter {
    /**
     * Adds a node element.
     * @param type The type of element.
     * @param name The name attribute. May be null, in which case no name is included.
     */
    void addNode(String type, String name);

    /**
     * Adds a node element.
     * @param type The type of element.
     * @param name The name attribute. May be null, in which case no name is included.
     * @param summary A optional summary to include as attribute on the node.
     */
    void addNode(String type, String name, String summary);

    /**
     * Finishes the current node.
     */
    void endNode();

    /**
     * Adds a simple measurement, counted N.
     * @param name The name of the measurement.
     * @param metric The N measurement.
     */
    void addStatistic(StatisticsKey name, Number metric);

    void finish();
}
