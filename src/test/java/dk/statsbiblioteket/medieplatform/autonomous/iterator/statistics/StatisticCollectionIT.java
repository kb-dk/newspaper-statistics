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
    private StatisticGenerator eventProcessor;
    private Batch batch;

    @BeforeMethod(groups = "integrationTest")
    public void setupMethod(Method method) {
        batch = new Batch();
        batch.setBatchID("4099");
        eventProcessor = new StatisticGenerator(batch, new Properties());
        setOutputFileLocation(eventProcessor.getStatisticsFile());
    }

    @Test(groups = "integrationTest")
    public void emptyBatchOutputTest() {

        eventProcessor.handleNodeBegin(new NodeBeginsParsingEvent(batch.getFullID()));
        eventProcessor.handleNodeEnd(new NodeEndParsingEvent(batch.getFullID()));
        eventProcessor.handleFinish();
        assertOutputEqual("<Statistics><Batch name=\"" + batch.getFullID() + "\"><Films>0</Films></Batch></Statistics>\n");
    }

    @Test(groups = "integrationTest")
    public void pageNodeCountTest() {
        eventProcessor.handleNodeBegin(new NodeBeginsParsingEvent(batch.getFullID()));
        String film1 = "film1";
        String FILM1 = batch.getBatchID() + "/" + film1;
        String date1 = "2012-11-11";
        String edition1 = date1 + "-1";
        String EDITION1_1 = FILM1 + "/" + edition1;
        String PAGE1_1 = EDITION1_1 + "/page1";
        String PAGE1_2 = EDITION1_1 + "/page2";
        String film2 = "film2";
        String FILM2 = batch.getBatchID() + "/" + film2;
        String date2 = "2012-11-13";
        String edition2 = date2 + "-1";
        String EDITION2_1 = FILM1 + "/" + edition2;
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

        eventProcessor.handleNodeEnd(new NodeEndParsingEvent(batch.getFullID()));
        eventProcessor.handleFinish();
        assertOutputEqual(
                "<Statistics>\n" +
                "    <Batch name=\""+batch.getFullID()+"\">\n" +
                "        <Film name=\"film1\">\n" +
                "            <Edition name=\""+edition1+"\">\n" +
                "                <Briks>0</Briks>\n" +
                "                <Pages>2</Pages>\n" +
                "            </Edition>\n" +
                "            <Briks>0</Briks>\n" +
                "            <Editions>1</Editions>\n" +
                "            <Pages>2</Pages>\n" +
                "            <Edition-dates>\n" +
                "                <Date name=\""+date1+"\">1</Date>\n" +
                "            </Edition-dates>\n" +
                "            <Sections summary=\"0\"></Sections>\n" +
                "        </Film>\n" +
                "        <Film name=\""+film2+"\">\n" +
                "            <Edition name=\""+edition2+"\">\n" +
                "                <Briks>0</Briks>\n" +
                "                <Pages>1</Pages>\n" +
                "            </Edition>\n" +
                "            <Briks>0</Briks>\n" +
                "            <Editions>1</Editions>\n" +
                "            <Pages>1</Pages>\n" +
                "            <Edition-dates>\n" +
                "                <Date name=\""+date2+"\">1</Date>\n" +
                "            </Edition-dates>\n" +
                "            <Sections summary=\"0\"></Sections>\n" +
                "        </Film>\n" +
                "        <Briks>0</Briks>\n" +
                "        <Editions>2</Editions>\n" +
                "        <Films>2</Films>\n" +
                "        <Pages>3</Pages>\n" +
                "        <Edition-dates>\n" +
                "            <Date name=\""+date1+"\">1</Date>\n" +
                "            <Date name=\""+date2+"\">1</Date>\n" +
                "        </Edition-dates>\n" +
                "        <Sections summary=\"0\"></Sections>\n" +
                "    </Batch>\n" +
                "</Statistics>");
    }
}
