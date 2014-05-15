package dk.statsbiblioteket.medieplatform.newspaper.statistics.collector;

import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.AttributeParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.NodeEndParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.SinkCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.StatisticCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.model.Statistics;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.model.StatisticsKey;
import dk.statsbiblioteket.medieplatform.newspaper.statistics.FilmStatistics;

/**
 * Handles the collection of film level statistics.
 */
public class FilmCollector extends StatisticCollector {
    public static final String DATE_STAT_KEY = "Date";
    public static final String SECTIONS_STAT = "Sections";
    public static final String EDITION_DATE_STAT = "Edition-dates";
    public static final String AVISID_STAT = "AvisIDs";
    public static final String AVISID_STAT_KEY = "AvisID";
    private final FilmStatistics statistics = new FilmStatistics();
    private final Statistics editionDates = new Statistics();
    private final Statistics avisIDs = new Statistics();

    @Override
    protected StatisticCollector createChild(String eventName) {
        if (eventName.equals("UNMATCHED")) {
            return new UnmatchedCollector();
        } else if (eventName.equals("FILM-ISO-target")) {
            return new SinkCollector();
        } else {
            editionDates.addCount(new StatisticsKey(DATE_STAT_KEY,
                    getSimpleName(eventName).substring(0, eventName.lastIndexOf('-'))),
                    1L);
            return new EditionCollector();
        }
    }

    /**
     * @return A specialized FilmStatistics.
     *
     * @see dk.statsbiblioteket.medieplatform.newspaper.statistics.FilmStatistics
     */
    @Override
    public Statistics getStatistics() {
        return statistics;
    }
    
    @Override 
    public void handleAttribute(AttributeParsingEvent event) {
        if(event.getName().endsWith("film.xml")) {
            addAvisIDStat(event);
        }
    }

    /**
     * Adds a custom edition date list to the statistics.
     */
    @Override
    public StatisticCollector handleNodeEnd(NodeEndParsingEvent event) {
        getStatistics().addSubstatistic(new StatisticsKey(EDITION_DATE_STAT), editionDates);
        getStatistics().addSubstatistic(new StatisticsKey(AVISID_STAT), avisIDs);
        return super.handleNodeEnd(event);
    }

    @Override
    protected String[] mandatoryCounts() {
        return new String[] {"Edition"};
    }
    
    private void addAvisIDStat(AttributeParsingEvent event) {
        String filmAttributeName = event.getName().substring(event.getName().lastIndexOf("/")+1);
        String avisID = filmAttributeName.split("-")[0];
        avisIDs.addCount(new StatisticsKey(AVISID_STAT_KEY, avisID), 1L);
    }
}
