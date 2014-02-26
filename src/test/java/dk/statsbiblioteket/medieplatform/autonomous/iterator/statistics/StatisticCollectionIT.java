package dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.NodeBeginsParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.NodeEndParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.writer.XmlFileTest;
import dk.statsbiblioteket.medieplatform.newspaper.statistics.StatisticGenerator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Properties;

public class StatisticCollectionIT extends XmlFileTest {
    /** Instance of the EventProcessor Under Test. */
    private EventProcessor eventProcessor;
    private static final String DEFAULT_BATCH = "4099";

    @BeforeMethod(groups = "integrationTest")
    public void setupMethod(Method method) {
        Batch batch = new Batch();
        batch.setBatchID(DEFAULT_BATCH);
        eventProcessor = new StatisticGenerator(batch, new Properties());
    }

    @Test(groups = "integrationTest")
    public void emptyBatchOutputTest() {
        eventProcessor.handleNodeBegin(new NodeBeginsParsingEvent(DEFAULT_BATCH));
        eventProcessor.handleNodeEnd(new NodeEndParsingEvent(DEFAULT_BATCH));
        eventProcessor.handleFinish();
        assertOutputEqual(
                "<Statistics>" +
                "  <Batch name=\"4099\"></Batch>" +
                "</Statistics>");
    }

    @Test(groups = "integrationTest")
    public void pageNodeCountTest() {
        eventProcessor.handleNodeBegin(new NodeBeginsParsingEvent(DEFAULT_BATCH));
        String FILM1= DEFAULT_BATCH + "/film1";
        String EDITION1_1 = FILM1 + "/2012-11-11-1";
        String PAGE1_1 = EDITION1_1 + "/page1";
        String PAGE1_2 = EDITION1_1 + "/page2";
        String FILM2= DEFAULT_BATCH + "/film2";
        String EDITION2_1 = FILM1 + "/2012-11-13-1";
        String PAGE2_1 = EDITION2_1 + "/page3";
        eventProcessor.handleNodeBegin(new NodeBeginsParsingEvent(FILM1));
        eventProcessor.handleNodeBegin(new NodeBeginsParsingEvent(EDITION1_1));
        eventProcessor.handleNodeBegin(new NodeBeginsParsingEvent(PAGE1_1));
        eventProcessor.handleNodeEnd(new NodeEndParsingEvent(PAGE1_1));
        eventProcessor.handleNodeBegin(new NodeBeginsParsingEvent(PAGE1_2));
        eventProcessor.handleNodeEnd(new NodeEndParsingEvent(PAGE1_2));
        eventProcessor.handleNodeEnd(new NodeEndParsingEvent(EDITION1_1));
        eventProcessor.handleNodeEnd(new NodeEndParsingEvent(FILM1));

        eventProcessor.handleNodeBegin(new NodeBeginsParsingEvent(FILM2));
        eventProcessor.handleNodeBegin(new NodeBeginsParsingEvent(EDITION2_1));
        eventProcessor.handleNodeBegin(new NodeBeginsParsingEvent(PAGE2_1));
        eventProcessor.handleNodeEnd(new NodeEndParsingEvent(PAGE2_1));
        eventProcessor.handleNodeEnd(new NodeEndParsingEvent(EDITION2_1));
        eventProcessor.handleNodeEnd(new NodeEndParsingEvent(FILM2));

        eventProcessor.handleNodeEnd(new NodeEndParsingEvent(DEFAULT_BATCH));
        eventProcessor.handleFinish();
        assertOutputEqual(
                "<Statistics>\n" +
                        "  <Batch name=\"4099\">\n" +
                        "    <Film name=\"film1\">\n" +
                        "      <Edition name=\"2012-11-11-1\">\n" +
                        "        <Pages>2</Pages>\n" +
                        "      </Edition>\n" +
                        "      <Editions>1</Editions>\n" +
                        "      <Pages>2</Pages>\n" +
                        "      <EDITION_DATES>\n" +
                        "        <E2012-11-11>1</E2012-11-11>\n" +
                        "      </EDITION_DATES>\n" +
                        "    </Film>\n" +
                        "    <Film name=\"film2\">\n" +
                        "      <Edition name=\"2012-11-13-1\">\n" +
                        "        <Pages>1</Pages>\n" +
                        "      </Edition>\n" +
                        "      <Editions>1</Editions>\n" +
                        "      <Pages>1</Pages>\n" +
                        "      <EDITION_DATES>\n" +
                        "        <E2012-11-13>1</E2012-11-13>\n" +
                        "      </EDITION_DATES>\n" +
                        "    </Film>\n" +
                        "    <Editions>2</Editions>\n" +
                        "    <Films>2</Films>\n" +
                        "    <Pages>3</Pages>\n" +
                        "    <EDITION_DATES>\n" +
                        "      <E2012-11-11>1</E2012-11-11>\n" +
                        "      <E2012-11-13>1</E2012-11-13>\n" +
                        "    </EDITION_DATES>\n" +
                        "  </Batch>\n" +
                        "</Statistics>");
    }
}
