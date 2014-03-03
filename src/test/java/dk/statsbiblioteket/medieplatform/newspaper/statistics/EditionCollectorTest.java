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
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.writer.StatisticWriter;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.writer.XmlFileTest;
import dk.statsbiblioteket.medieplatform.newspaper.statistics.collector.EditionCollector;
import dk.statsbiblioteket.medieplatform.newspaper.statistics.collector.PageCollector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static dk.statsbiblioteket.medieplatform.newspaper.statistics.collector.PageCollector.SectionStatisticsKey;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class EditionCollectorTest extends XmlFileTest {
    private StatisticCollector parentCollector;
    private StatisticWriter writer;
    private Properties properties;

    @BeforeMethod
    public void setupMethod(Method method) {
        writer = createWriter(method.getName());
        parentCollector = mock(StatisticCollector.class);
        when(parentCollector.getStatistics()).thenReturn(mock(Statistics.class));
        properties = new Properties();
    }

    /**
     * Verifies that the sections are sorted according to the sequence they appear on the film.
     */
    @Test
    public void sectionSortTest() throws IOException {
        EditionCollector editionCollectorUT = new EditionCollector();
        editionCollectorUT.initialize("first-edition", parentCollector, writer, properties);

        editionCollectorUT.addStatistics(createPageInsSectionStatistics("1. sektion", "0001"));
        editionCollectorUT.addStatistics(createPageInsSectionStatistics("International", "0002"));
        editionCollectorUT.addStatistics(createPageInsSectionStatistics("2. sektion", "0003A"));
        editionCollectorUT.addStatistics(createPageInsSectionStatistics("2. sektion", "0003B"));

        editionCollectorUT.handleNodeEnd(new NodeEndParsingEvent("first-edition"));
        writer.finish();
        assertOutputEqual("<Statistics>\n" +
                "  <Edition name=\"first-edition\">\n" +
                "    <Briks>0</Briks>\n" +
                "    <Pages>0</Pages>\n" +
                "    <Pages-In-Sections>\n" +
                "      <Section name=\"1. sektion\">1</Section>\n" +
                "      <Section name=\"International\">1</Section>\n" +
                "      <Section name=\"2. sektion\">2</Section>\n" +
                "    </Pages-In-Sections>\n" +
                "  </Edition>\n" +
                "</Statistics>");
    }

    @Test
    public void sectionStatisticsKeyTest() {
        SectionStatisticsKey firstKey = new SectionStatisticsKey(PageCollector.SECTION_STAT_KEY,"1. sektion", "0001");
        SectionStatisticsKey secondKey = new SectionStatisticsKey(PageCollector.SECTION_STAT_KEY,"International", "0002");
        SectionStatisticsKey thirdKey = new SectionStatisticsKey(PageCollector.SECTION_STAT_KEY,"2. sektion", "0003");

        assertTrue(firstKey.compareTo(secondKey) < 0);
        assertTrue(firstKey.compareTo(firstKey) == 0);
        assertTrue(secondKey.compareTo(thirdKey) < 0);
        assertTrue(thirdKey.compareTo(firstKey) > 0);

        assertEquals(thirdKey, new SectionStatisticsKey(PageCollector.SECTION_STAT_KEY,"2. sektion", "0004"));
        assertEquals(thirdKey.hashCode(), new SectionStatisticsKey(PageCollector.SECTION_STAT_KEY,"2. sektion", "0004").hashCode());
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

    private Statistics createPageInsSectionStatistics(String section, String page) {
        Statistics sectionStatistics = new Statistics();
        sectionStatistics.addCount(
                new SectionStatisticsKey(PageCollector.SECTION_STAT_KEY, section, page), 1L);
        Statistics pageStatistics = new Statistics();
        pageStatistics.addSubstatistic(new StatisticsKey(PageCollector.PAGES_IN_SECTIONS_STAT), sectionStatistics);
        return pageStatistics;
    }
}
