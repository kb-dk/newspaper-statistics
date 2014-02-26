package dk.statsbiblioteket.medieplatform.newspaper.statistics;

import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.NodeEndParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.StatisticCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.model.Statistics;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.model.StatisticsKey;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.writer.StatisticWriter;
import dk.statsbiblioteket.medieplatform.newspaper.statistics.collector.FilmCollector;
import dk.statsbiblioteket.medieplatform.newspaper.statistics.collector.PageCollector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FilmCollectorTest {
        private StatisticCollector parentCollector;
        private StatisticWriter writer;
        private Properties properties;

        @BeforeMethod
        public void setupMethod(Method method) {
            parentCollector = mock(StatisticCollector.class);
            when(parentCollector.getStatistics()).thenReturn(mock(Statistics.class));
            writer = mock(StatisticWriter.class);
            properties = new Properties();
        }

    @Test
    public void sectionsInEditionTest() throws IOException {
        FilmCollector filmCollectorUT = new FilmCollector();
        filmCollectorUT.initialize("Film1", parentCollector, writer, properties);

        String section1 = "Sektion 1";
        addPagesInSectionStat(filmCollectorUT, section1);
        addPagesInSectionStat(filmCollectorUT, section1);
        String section2 = "Sektion 2";
        addPagesInSectionStat(filmCollectorUT, section2);
        filmCollectorUT.handleNodeEnd(new NodeEndParsingEvent("Film1"));
        verify(writer).addNode("Film", filmCollectorUT.getName());
        verify(writer).addNode(FilmCollector.EDITION_DATE_STAT, (String)null,null);
        verify(writer).addNode(FilmCollector.SECTIONS_STAT,null, "2");
        verify(writer).addStatistic(new StatisticsKey("Section", section1), 2L);
        verify(writer).addStatistic(new StatisticsKey("Section", section2), 1L);
        verify(writer, times(0)).addStatistic(new StatisticsKey(PageCollector.PAGES_IN_SECTIONS_STAT), 1L);
    }

    @Test
    public void noSectionsTest() throws IOException {
        FilmCollector filmCollectorUT = new FilmCollector();
        filmCollectorUT.initialize("Film1", parentCollector, writer, properties);
        filmCollectorUT.handleNodeEnd(new NodeEndParsingEvent("Film1"));
        verify(writer).addNode("Film", filmCollectorUT.getName());
        verify(writer).addNode(FilmCollector.EDITION_DATE_STAT, (String)null,null);
        verify(writer).addNode("Sections",null, "0");
    }

    @Test
    public void noEditionsTest() throws IOException {
        FilmCollector filmCollectorUT = new FilmCollector();
        filmCollectorUT.initialize("Film1", parentCollector, writer, properties);
        filmCollectorUT.handleNodeEnd(new NodeEndParsingEvent("Film1"));
        verify(writer).addNode("Film", filmCollectorUT.getName());
        verify(writer).addStatistic(new StatisticsKey("Editions"), 0L);
    }

    private void addPagesInSectionStat(FilmCollector filmCollectorUT, String section) {
        Statistics pagesInSectionStat = new Statistics();
        pagesInSectionStat.addCount(new StatisticsKey("Section", section), 1L);
        Statistics pageStatistics = new Statistics();
        pageStatistics.addSubstatistic(new StatisticsKey(PageCollector.PAGES_IN_SECTIONS_STAT), pagesInSectionStat);
        filmCollectorUT.addStatistics(pageStatistics);
    }
}
