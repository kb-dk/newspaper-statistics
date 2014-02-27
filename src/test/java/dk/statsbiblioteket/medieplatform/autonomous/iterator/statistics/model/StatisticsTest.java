package dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.model;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class StatisticsTest {
    @Test
    public void subStatisticsAdditionTest() {
        Statistics rootStatistics = new Statistics();
        assertEquals(rootStatistics.substatisticsMap.size(), 0);

        Statistics firstSubstatistics = new Statistics();
        StatisticsKey sectionsKey = new StatisticsKey("Sections");
        StatisticsKey booksKey = new StatisticsKey("Books");
        StatisticsKey travelKey = new StatisticsKey("Travel");
        firstSubstatistics.addCount(booksKey, 1L);
        firstSubstatistics.addCount(travelKey, 1L);
        rootStatistics.addSubstatistic(sectionsKey, firstSubstatistics);
        assertEquals(rootStatistics.substatisticsMap.size(), 1);
        assertEquals(rootStatistics.substatisticsMap.get(sectionsKey).countMap.size(), 2);
        assertEquals(rootStatistics.substatisticsMap.get(sectionsKey).countMap.get(booksKey), new Long(1));

        Statistics secondSubstatistics = new Statistics();
        StatisticsKey catsKey = new StatisticsKey("Cats");
        secondSubstatistics.addCount(booksKey, 1L);
        secondSubstatistics.addCount(catsKey, 1L);
        rootStatistics.addSubstatistic(sectionsKey, secondSubstatistics);
        assertEquals(rootStatistics.substatisticsMap.size(), 1);
        assertEquals(rootStatistics.substatisticsMap.get(sectionsKey).countMap.size(), 3);
        assertEquals(rootStatistics.substatisticsMap.get(sectionsKey).countMap.get(booksKey), new Long(2));
        assertEquals(rootStatistics.substatisticsMap.get(sectionsKey).countMap.get(travelKey), new Long(1));
        assertEquals(rootStatistics.substatisticsMap.get(sectionsKey).countMap.get(catsKey), new Long(1));
    }
}
