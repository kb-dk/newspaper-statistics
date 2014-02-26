package dk.statsbiblioteket.medieplatform.newspaper.statistics;

import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.model.Statistics;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.model.StatisticsKey;
import dk.statsbiblioteket.medieplatform.newspaper.statistics.collector.FilmCollector;
import dk.statsbiblioteket.medieplatform.newspaper.statistics.collector.PageCollector;

/**
 * Overides the addSubstatistic operation so PageCollector.PAGES_IN_SECTIONS_STAT are converted into
 * FilmCollector.SECTIONS_STAT, eg. the numberOfPages are ignored and each section is only counted once.
 */
public class FilmStatistics extends Statistics {
    public FilmStatistics() {
        super();
        substatisticsMap.put(new StatisticsKey(FilmCollector.SECTIONS_STAT), new SectionStatistics());
    }

    @Override
    public void addSubstatistic(StatisticsKey key, Statistics statisticsToAdd) {
        if (key.getType().equals(PageCollector.PAGES_IN_SECTIONS_STAT)) {
            super.addSubstatistic(new StatisticsKey(FilmCollector.SECTIONS_STAT), statisticsToAdd);
        } else {
            super.addSubstatistic(key, statisticsToAdd);
        }
    }

    /**
     * Overrides the addCount so the count number is ignore, and the count is just increment by 1.
     */
    private static class CountNormalizerStatistics extends Statistics  {

        @Override
        public void addCount(StatisticsKey key, Long countToAdd) {
            super.addCount(key, 1L);
        }
    }

    private static class SectionStatistics extends CountNormalizerStatistics {

        @Override
        public String getSummary() {
            return countMap.size() + "";
        }
    }
}
