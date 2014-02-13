package dk.statsbiblioteket.medieplatform.newspaper.statistics.collector;

import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.SinkCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.StatisticCollector;

/**
 * Handles the collection of page level statistics.
 *
 * Uses SinkCollectors as children.
 */
public class UnmatchedPageCollector extends StatisticCollector {
    public UnmatchedPageCollector() {
        doNotWrite();
    }
    @Override
    public StatisticCollector createChild(String event) {
        return new SinkCollector();
    }
}
