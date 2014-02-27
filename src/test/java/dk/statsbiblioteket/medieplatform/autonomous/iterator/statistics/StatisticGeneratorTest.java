package dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics;

import java.lang.reflect.Method;
import java.util.Properties;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
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
        String EDITION1_1 = FILM1 + "/2012-11-11-1";
        String PAGE1_1 = EDITION1_1 + "/page1";
        String PAGE1_2 = EDITION1_1 + "/page2";
        String FILM2= DEFAULT_BATCH + "/film2";
        String EDITION2_1 = FILM1 + "/2012-11-13-1";
        String PAGE2_1 = EDITION2_1 + "/page3";
        statisticGenerator.handleNodeBegin(new NodeBeginsParsingEvent(FILM1));
        statisticGenerator.handleNodeBegin(new NodeBeginsParsingEvent(EDITION1_1));
        statisticGenerator.handleNodeBegin(new NodeBeginsParsingEvent(PAGE1_1));
        statisticGenerator.handleNodeEnd(new NodeEndParsingEvent(PAGE1_1));
        statisticGenerator.handleNodeBegin(new NodeBeginsParsingEvent(PAGE1_2));
        statisticGenerator.handleNodeEnd(new NodeEndParsingEvent(PAGE1_2));
        statisticGenerator.handleNodeEnd(new NodeEndParsingEvent(EDITION1_1));
        statisticGenerator.handleNodeEnd(new NodeEndParsingEvent(FILM1));

        statisticGenerator.handleNodeBegin(new NodeBeginsParsingEvent(FILM2));
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
                "        <Pages>2</Pages>\n" +
                "      </Edition>\n" +
                "      <Briks>0</Briks>\n" +
                "      <Editions>1</Editions>\n" +
                "      <Pages>2</Pages>\n" +
                "      <Edition-dates>\n" +
                "        <Date name=\"2012-11-11\">1</Date>\n" +
                "      </Edition-dates>\n" +
                "      <Sections summary=\"0\"></Sections>\n" +
                "    </Film>\n" +
                "    <Film name=\"film2\">\n" +
                "      <Edition name=\"2012-11-13-1\">\n" +
                "        <Briks>0</Briks>\n" +
                "        <Pages>1</Pages>\n" +
                "      </Edition>\n" +
                "      <Briks>0</Briks>\n" +
                "      <Editions>1</Editions>\n" +
                "      <Pages>1</Pages>\n" +
                "      <Edition-dates>\n" +
                "        <Date name=\"2012-11-13\">1</Date>\n" +
                "      </Edition-dates>\n" +
                "      <Sections summary=\"0\"></Sections>\n" +
                "    </Film>\n" +
                "    <Briks>0</Briks>\n" +
                "    <Editions>2</Editions>\n" +
                "    <Films>2</Films>\n" +
                "    <Pages>3</Pages>\n" +
                "    <Edition-dates>\n" +
                "      <Date name=\"2012-11-11\">1</Date>\n" +
                "      <Date name=\"2012-11-13\">1</Date>\n" +
                "    </Edition-dates>\n" +
                "    <Sections summary=\"0\"></Sections>\n" +
                "  </Batch>\n" +
                "</Statistics>");
    }
}
