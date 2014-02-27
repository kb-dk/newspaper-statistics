package dk.statsbiblioteket.medieplatform.newspaper.statistics;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Properties;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.TreeIterator;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.EventRunner;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.TreeEventHandler;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.filesystem.transforming.TransformingIteratorForFileSystems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class StatisticsComponentIT {
    private final static String TEST_BATCH_ID = "400022028241";
    private File genericPropertyFile;
    private Properties properties;
    private Logger log = LoggerFactory.getLogger(getClass());
    @BeforeMethod(groups = "testDataTest")
    public void loadGeneralConfiguration() throws Exception {
        String pathToProperties = System.getProperty("integration.test.newspaper.properties");
        properties = new Properties();

        log.info("Loading general config from: " + pathToProperties);
        genericPropertyFile = new File(pathToProperties);
        properties.load(new FileInputStream(genericPropertyFile));
        loadSpecificProperties(genericPropertyFile.getParentFile() + "/newspaper-statistics-config/config.properties");
    }

    /**
     * Test that a reasonable batch can be run against the flagger component without generating any
     * errors or flags when the batch and configuration agree on the setup..
     * @throws Exception
     */
    @Test(groups = "testDataTest")
    public void testSmallBatch() throws Exception {
        processBatch("small-test-batch");
    }

    /**
     * Test that a the default batch with a configuration inconsistent with the metadata in the batch. This should
     * generate a lot of flags.
     * @throws Exception
     */
    @Test(groups = "testDataTest")
    public void testBadBatch() throws Exception {
        processBatch("bad-bad-batch");
    }

    private void loadSpecificProperties(String path) throws Exception {
        log.info("Loading specific config from: " + path);
        File specificProperties = new File(path);
        properties.load(new FileInputStream(specificProperties));
        properties.setProperty(StatisticGenerator.STATISTICS_FILE_LOCATION_PROPERTY, "target/statistics/Integration");
    }

    private void processBatch(String batchFolder)  throws Exception  {
        TreeIterator iterator = getIterator(batchFolder);
        EventRunner runner = new EventRunner(iterator);
        Batch batch = new Batch();

        batch.setBatchID(TEST_BATCH_ID);
        batch.setRoundTripNumber(1);

        runner.runEvents( Arrays.asList(new TreeEventHandler[]
                {new StatisticGenerator(batch, properties)}), new ResultCollector(getClass().getSimpleName(), "1", 10));
    }

    /**
     * Creates and returns a iteration based on the test batch file structure found in the test/ressources folder.
     *
     * @return A iterator the the test batch
     * @throws URISyntaxException
     */
    public TreeIterator getIterator(String batchFolder) throws URISyntaxException {
        File file = getBatchFolder(batchFolder);
        return new TransformingIteratorForFileSystems(file, "\\.", ".*\\.jp2$", ".md5");
    }

    private File getBatchFolder(String batch) {
        String pathToTestBatch = System.getProperty("integration.test.newspaper.testdata");
        String pathToBatch = pathToTestBatch + '/'+ batch + "/B" + TEST_BATCH_ID + "-RT1";
        log.info("Loading batch from: " + pathToBatch);
        return new File(pathToBatch);
    }
}
