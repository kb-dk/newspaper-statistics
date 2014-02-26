package dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.model;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class StatisticsTest {
    @Test
    public void subStatisticsAdditionTest() {
        Statistics rootStatistics = new Statistics();
        assertEquals(rootStatistics.substatisticsMap.size(), 0);

        Statistics firstSubstatistics = new Statistics();
        firstSubstatistics.addCount(new StatisticsKey("Books"), 1L);
        firstSubstatistics.addCount(new StatisticsKey("Travel"), 1L);
        rootStatistics.addSubstatistic(new StatisticsKey("Sections"), firstSubstatistics);
        assertEquals(rootStatistics.substatisticsMap.size(), 1);
        assertEquals(rootStatistics.substatisticsMap.get(new StatisticsKey("Sections")).countMap.size(), 2);
        assertEquals(rootStatistics.substatisticsMap.get(new StatisticsKey("Sections")).countMap.get(new StatisticsKey("Books")), new Long(1));

        Statistics secondSubstatistics = new Statistics();
        secondSubstatistics.addCount(new StatisticsKey("Books"), 1L);
        secondSubstatistics.addCount(new StatisticsKey("Cats"), 1L);
        rootStatistics.addSubstatistic(new StatisticsKey("Sections"), secondSubstatistics);
        assertEquals(rootStatistics.substatisticsMap.size(), 1);
        assertEquals(rootStatistics.substatisticsMap.get(new StatisticsKey("Sections")).countMap.size(), 3);
        assertEquals(rootStatistics.substatisticsMap.get(new StatisticsKey("Sections")).countMap.get(new StatisticsKey("Books")), new Long(2));
        assertEquals(rootStatistics.substatisticsMap.get(new StatisticsKey("Sections")).countMap.get(new StatisticsKey("Travel")), new Long(1));
        assertEquals(rootStatistics.substatisticsMap.get(new StatisticsKey("Sections")).countMap.get(new StatisticsKey("Cats")), new Long(1));
    }
}
