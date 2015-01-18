package dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.AttributeParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.InMemoryAttributeParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.NodeBeginsParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.NodeEndParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.writer.XmlFileTest;
import dk.statsbiblioteket.medieplatform.newspaper.statistics.StatisticGenerator;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class StatisticGeneratorTest extends XmlFileTest {
    /** Instance of the EventProcessor Under Test. */
    private StatisticGenerator statisticGenerator;
    private static final String DEFAULT_BATCH = "B4099-RT1";

    @BeforeMethod
    public void setupMethod(Method method) {
        Batch batch = new Batch();
        batch.setBatchID("4099");
        Properties properties = new Properties();
        outputFileLocation = OUTPUTFILE_DIR + DEFAULT_BATCH + "/full-statistics.xml";
        countStatisticsFileLocation = OUTPUTFILE_DIR + DEFAULT_BATCH + "/count-statistics.xml";
        dateStatisticsFileLocation = OUTPUTFILE_DIR + DEFAULT_BATCH + "/date-statistics.xml";
        properties.setProperty(StatisticGenerator.STATISTICS_FILE_LOCATION_PROPERTY, OUTPUTFILE_DIR);
        statisticGenerator = new StatisticGenerator(batch, properties);
    }

    @Test
    public void emptyBatchOutputTest() {
        statisticGenerator.handleNodeBegin(new NodeBeginsParsingEvent(DEFAULT_BATCH));
        statisticGenerator.handleNodeEnd(new NodeEndParsingEvent(DEFAULT_BATCH));
        statisticGenerator.handleFinish();
        assertOutputEqual(
                "<Statistics>\n" +
                "  <Batch name=\"B4099-RT1\">\n" +
                "    <Films>0</Films>\n" +
                "  </Batch>\n" +
                "</Statistics>");
    }

    @Test
    public void pageNodeCountTest() {
        statisticGenerator.handleNodeBegin(new NodeBeginsParsingEvent(DEFAULT_BATCH));
        String FILM1= DEFAULT_BATCH + "/film1";
        String FILM1_ATTRIBUTE = FILM1 + "/foobarpaper-4099-01.film.xml";
        String EDITION1_1 = FILM1 + "/2012-11-11-1";
        String PAGE1_1 = EDITION1_1 + "/page1";
        String PAGE1_2 = EDITION1_1 + "/page2";
        String PAGE1_3 = EDITION1_1 + "/page3";
        String PAGE1_1_ATTRIBUTE = "/foobarpaper-2012-11-11-1-0001.mods.xml";
        String PAGE1_2_ATTRIBUTE = "/foobarpaper-2012-11-11-1-0002.mods.xml";
        String PAGE1_3_ATTRIBUTE = "/foobarpaper-2012-11-11-1-0003.mods.xml";
        String PAGE1_1_CONTENTS = "<mods xmlns=\"http://www.loc.gov/mods/v3\"><part><detail type=\"sectionLabel\"><number>section 2</number></detail></part></mods>";
        String PAGE1_2_CONTENTS = "<mods xmlns=\"http://www.loc.gov/mods/v3\"><part><detail type=\"sectionLabel\"><number>Section 1</number></detail></part></mods>";
        String PAGE1_3_CONTENTS = "<mods xmlns=\"http://www.loc.gov/mods/v3\"><part><detail type=\"sectionLabel\"><number>Section 3</number></detail></part></mods>";
        String FILM2= DEFAULT_BATCH + "/film2";
        String FILM2_ATTRIBUTE = FILM2 + "/foobarpaper-4099-02.film.xml";
        String EDITION2_1 = FILM1 + "/2012-11-13-1";
        String PAGE2_1 = EDITION2_1 + "/page3";
        statisticGenerator.handleNodeBegin(new NodeBeginsParsingEvent(FILM1));
        statisticGenerator.handleAttribute(new InMemoryAttributeParsingEvent(FILM1_ATTRIBUTE, null, null));
        statisticGenerator.handleNodeBegin(new NodeBeginsParsingEvent(EDITION1_1));
        statisticGenerator.handleNodeBegin(new NodeBeginsParsingEvent(PAGE1_1));
        statisticGenerator.handleAttribute(new InMemoryAttributeParsingEvent(PAGE1_1_ATTRIBUTE, PAGE1_1_CONTENTS.getBytes(), null));
        statisticGenerator.handleNodeEnd(new NodeEndParsingEvent(PAGE1_1));
        statisticGenerator.handleNodeBegin(new NodeBeginsParsingEvent(PAGE1_2));
        statisticGenerator.handleAttribute(new InMemoryAttributeParsingEvent(PAGE1_2_ATTRIBUTE, PAGE1_2_CONTENTS.getBytes(), null));
        statisticGenerator.handleNodeEnd(new NodeEndParsingEvent(PAGE1_2));
        statisticGenerator.handleNodeBegin(new NodeBeginsParsingEvent(PAGE1_3));
        statisticGenerator.handleAttribute(new InMemoryAttributeParsingEvent(PAGE1_3_ATTRIBUTE, PAGE1_3_CONTENTS.getBytes(), null));
        statisticGenerator.handleNodeEnd(new NodeEndParsingEvent(PAGE1_3));
        statisticGenerator.handleNodeEnd(new NodeEndParsingEvent(EDITION1_1));
        statisticGenerator.handleNodeEnd(new NodeEndParsingEvent(FILM1));

        statisticGenerator.handleNodeBegin(new NodeBeginsParsingEvent(FILM2));
        statisticGenerator.handleAttribute(new InMemoryAttributeParsingEvent(FILM2_ATTRIBUTE, null, null));
        statisticGenerator.handleNodeBegin(new NodeBeginsParsingEvent(EDITION2_1));
        statisticGenerator.handleNodeBegin(new NodeBeginsParsingEvent(PAGE2_1));
        statisticGenerator.handleNodeEnd(new NodeEndParsingEvent(PAGE2_1));
        statisticGenerator.handleNodeEnd(new NodeEndParsingEvent(EDITION2_1));
        statisticGenerator.handleNodeEnd(new NodeEndParsingEvent(FILM2));

        statisticGenerator.handleNodeEnd(new NodeEndParsingEvent(DEFAULT_BATCH));
        statisticGenerator.handleFinish();
        assertOutputEqual(
                "<Statistics>\n" +
                "  <Batch name=\"B4099-RT1\">\n" +
                "    <Film name=\"film1\">\n" +
                "      <Edition name=\"2012-11-11-1\">\n" +
                "        <Briks>0</Briks>\n" +
                "        <Pages>3</Pages>\n" +
                "        <Pages-In-Sections>\n" +
                "          <Section name=\"section 2\">1</Section>\n" +
                "          <Section name=\"Section 1\">1</Section>\n" +
                "          <Section name=\"Section 3\">1</Section>\n" +
                "        </Pages-In-Sections>\n" +
                "      </Edition>\n" +
                "      <Briks>0</Briks>\n" +
                "      <Editions>1</Editions>\n" +
                "      <Pages>3</Pages>\n" +
                "      <AvisIDs>\n" +
                "        <AvisID name=\"foobarpaper\">1</AvisID>\n" + 
                "      </AvisIDs>\n" +
                "      <Edition-dates>\n" +
                "        <Date name=\"2012-11-11\">1</Date>\n" +
                "      </Edition-dates>\n" +
                "    <Sections summary=\"3\">\n" +
                "      <Section name=\"section 2\">1</Section>\n" +
                "      <Section name=\"Section 1\">1</Section>\n" +
                "      <Section name=\"Section 3\">1</Section>\n" +
                "    </Sections>\n" +
                "    </Film>\n" +
                "    <Film name=\"film2\">\n" +
                "      <Edition name=\"2012-11-13-1\">\n" +
                "        <Briks>0</Briks>\n" +
                "        <Pages>1</Pages>\n" +
                "      </Edition>\n" +
                "      <Briks>0</Briks>\n" +
                "      <Editions>1</Editions>\n" +
                "      <Pages>1</Pages>\n" +
                "      <AvisIDs>\n" +
                "        <AvisID name=\"foobarpaper\">1</AvisID>\n" + 
                "      </AvisIDs>\n" +
                "      <Edition-dates>\n" +
                "        <Date name=\"2012-11-13\">1</Date>\n" +
                "      </Edition-dates>\n" +
                "      <Sections summary=\"0\"></Sections>\n" +
                "    </Film>\n" +
                "    <Briks>0</Briks>\n" +
                "    <Editions>2</Editions>\n" +
                "    <Films>2</Films>\n" +
                "    <Pages>4</Pages>\n" +
                "    <AvisIDs>\n" +
                "      <AvisID name=\"foobarpaper\">2</AvisID>\n" + 
                "    </AvisIDs>\n" + 
                "    <Edition-dates>\n" +
                "      <Date name=\"2012-11-11\">1</Date>\n" +
                "      <Date name=\"2012-11-13\">1</Date>\n" +
                "    </Edition-dates>\n" +
                "    <Sections summary=\"3\">\n" +
                "      <Section name=\"section 2\">1</Section>\n" +
                "      <Section name=\"Section 1\">1</Section>\n" +
                "      <Section name=\"Section 3\">1</Section>\n" +
                "    </Sections>\n" +
                "  </Batch>\n" +
                "</Statistics>");

        assertCountStatisticsEqual(
                "  <Batch name=\"B4099-RT1\">\n" +
                "    <Film name=\"film1\">\n" +
                "      <Edition name=\"2012-11-11-1\">\n" +
                "        <Briks>0</Briks>\n" +
                "        <Pages>3</Pages>\n" +
                "        <Pages-In-Sections>\n" +
                "          <Section name=\"section 2\">1</Section>\n" +
                "          <Section name=\"Section 1\">1</Section>\n" +
                "          <Section name=\"Section 3\">1</Section>\n" +
                "        </Pages-In-Sections>\n" +
                "      </Edition>\n" +
                "      <Briks>0</Briks>\n" +
                "      <Editions>1</Editions>\n" +
                "      <Pages>3</Pages>\n" +
                "      <AvisIDs>\n" +
                "        <AvisID name=\"foobarpaper\">1</AvisID>\n" +
                "      </AvisIDs>\n" +
                "    <Sections summary=\"3\">\n" +
                "      <Section name=\"Section 1\">1</Section>\n" +
                "      <Section name=\"section 2\">1</Section>\n" +
                "      <Section name=\"Section 3\">1</Section>\n" +
                "    </Sections>\n" +
                "    </Film>\n" +
                "    <Film name=\"film2\">\n" +
                "      <Edition name=\"2012-11-13-1\">\n" +
                "        <Briks>0</Briks>\n" +
                "        <Pages>1</Pages>\n" +
                "      </Edition>\n" +
                "      <Briks>0</Briks>\n" +
                "      <Editions>1</Editions>\n" +
                "      <Pages>1</Pages>\n" +
                "      <AvisIDs>\n" +
                "        <AvisID name=\"foobarpaper\">1</AvisID>\n" +
                "      </AvisIDs>\n" +
                "      <Sections summary=\"0\"></Sections>\n" +
                "    </Film>\n" +
                "    <Briks>0</Briks>\n" +
                "    <Editions>2</Editions>\n" +
                "    <Films>2</Films>\n" +
                "    <Pages>4</Pages>\n" +
                "    <AvisIDs>\n" +
                "      <AvisID name=\"foobarpaper\">2</AvisID>\n" +
                "    </AvisIDs>\n" +
                "    <Sections summary=\"3\">\n" +
                "      <Section name=\"Section 1\">1</Section>\n" +
                "      <Section name=\"section 2\">1</Section>\n" +
                "      <Section name=\"Section 3\">1</Section>\n" +
                "    </Sections>\n" +
                "  </Batch>");

        assertDateStatisticsEqual(
                "  <Batch>\n" +
                "    <Film name=\"film1\">\n" +
                "      <Edition-dates>\n" +
                "        <Date name=\"2012-11-11\">1</Date>\n" +
                "      </Edition-dates>\n" +
                "    </Film>\n" +
                "    <Film name=\"film2\">\n" +
                "      <Edition-dates>\n" +
                "        <Date name=\"2012-11-13\">1</Date>\n" +
                "      </Edition-dates>\n" +
                "    </Film>\n" +
                "  </Batch>\n");
    }
}
