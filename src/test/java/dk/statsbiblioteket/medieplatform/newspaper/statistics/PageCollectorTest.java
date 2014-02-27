package dk.statsbiblioteket.medieplatform.newspaper.statistics;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.AttributeParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.NodeEndParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.StatisticCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.model.Statistics;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.model.StatisticsKey;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.model.WeightedMean;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.writer.StatisticWriter;
import dk.statsbiblioteket.medieplatform.newspaper.statistics.collector.PageCollector;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class PageCollectorTest {
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
    public void doNotIgnoreZeroAccuracyTest() throws IOException {
        PageCollector pageCollectorUT = new PageCollector();
        pageCollectorUT.initialize("page1", parentCollector, writer, properties);

        pageCollectorUT.handleAttribute(createAltoEvent("Page1.alto.xml", 0.0));

        pageCollectorUT.handleNodeEnd(new NodeEndParsingEvent("page1"));
        ArgumentCaptor<Statistics> statisticsCaptor = ArgumentCaptor.forClass(Statistics.class);
        verify(parentCollector).addStatistics(statisticsCaptor.capture());
        statisticsCaptor.getValue().writeStatistics(writer);
        verify(writer).addStatistic(new StatisticsKey(PageCollector.OCR_ACCURACY_STAT), new WeightedMean(0.0,1));
    }

    @Test
    public void ignoreZeroAccuracyTest() throws IOException {
        properties.setProperty(PageCollector.ALTO_IGNORE_ZERO_ACCURACY_PROPERTY, "true");
        PageCollector pageCollectorUT = new PageCollector();
        pageCollectorUT.initialize("page1", parentCollector, writer, properties);

        pageCollectorUT.handleAttribute(createAltoEvent("Page1.alto.xml", 0.0));

        pageCollectorUT.handleNodeEnd(new NodeEndParsingEvent("page1"));
        ArgumentCaptor<Statistics> statisticsCaptor = ArgumentCaptor.forClass(Statistics.class);
        verify(parentCollector).addStatistics(statisticsCaptor.capture());
        statisticsCaptor.getValue().writeStatistics(writer);
        verify(writer, times(0)).addStatistic(new StatisticsKey(PageCollector.OCR_ACCURACY_STAT), new WeightedMean(0.0,1));
    }

    @Test
    public void pagesInSectionsTest() throws IOException {
        PageCollector pageCollectorUT = new PageCollector();
        String name = "1. Sektion";
        pageCollectorUT.initialize("page1", parentCollector, writer, properties);

        pageCollectorUT.handleAttribute(createModsSectionEvent("Page1.mods.xml", name));
        pageCollectorUT.handleNodeEnd(new NodeEndParsingEvent("page1"));
        ArgumentCaptor<Statistics> statisticsCaptor = ArgumentCaptor.forClass(Statistics.class);
        verify(parentCollector).addStatistics(statisticsCaptor.capture());
        statisticsCaptor.getValue().writeStatistics(writer);

        verify(writer).addStatistic(new StatisticsKey(PageCollector.SECTION_STAT_KEY, name),1L);
    }

    @Test
    public void noSectionsTest() throws IOException {
        PageCollector pageCollectorUT = new PageCollector();
        pageCollectorUT.initialize("page1", parentCollector, writer, properties);
        final String modsXmlStructure =
                "<mods:mods xmlns:mods=\"http://www.loc.gov/mods/v3\">" +
                        "  <mods:part>" +
                        "  </mods:part>" +
                        "</mods:mods>";
        AttributeParsingEvent event = new AttributeParsingEvent("Page1.mods.xml") {
            @Override
            public InputStream getData() throws IOException {
                return new ByteArrayInputStream(modsXmlStructure.getBytes());
            }
            @Override
            public String getChecksum() throws IOException {
                throw new RuntimeException("not implemented");
            }
        };
        pageCollectorUT.handleAttribute(event);
        pageCollectorUT.handleNodeEnd(new NodeEndParsingEvent("page1"));
        ArgumentCaptor<Statistics> statisticsCaptor = ArgumentCaptor.forClass(Statistics.class);
        verify(parentCollector).addStatistics(statisticsCaptor.capture());
        statisticsCaptor.getValue().writeStatistics(writer);
        verify(writer, times(0)).addStatistic(new StatisticsKey(anyString()), anyLong());
    }

    private AttributeParsingEvent createAltoEvent(String name, double accuracy) {
        final String altoXmlStructure =
                "<alto xmlns=\"http://www.loc.gov/standards/alto/ns-v2#\">" +
                "  <Layout>" +
                "    <Page ACCURACY=\"" + accuracy + "\"></Page>" +
                "  </Layout>" +
                "</alto>";

        return new AttributeParsingEvent(name) {
            @Override
            public InputStream getData() throws IOException {
                return new ByteArrayInputStream(altoXmlStructure.getBytes());
            }

            @Override
            public String getChecksum() throws IOException {
                throw new RuntimeException("not implemented");
            }
        };
    }

    private AttributeParsingEvent createModsSectionEvent(String name, String section) {
        final String modsXmlStructure =
                "<mods:mods xmlns:mods=\"http://www.loc.gov/mods/v3\">" +
                "  <mods:part>" +
                "    <mods:detail type=\"sectionLabel\">" +
                "      <mods:number>" + section + "</mods:number>" +
                "    </mods:detail>" +
                "  </mods:part>" +
                "</mods:mods>";
        return new AttributeParsingEvent(name) {
            @Override
            public InputStream getData() throws IOException {
                return new ByteArrayInputStream(modsXmlStructure.getBytes());
            }
            @Override
            public String getChecksum() throws IOException {
                throw new RuntimeException("not implemented");
            }
        };
    }
}
