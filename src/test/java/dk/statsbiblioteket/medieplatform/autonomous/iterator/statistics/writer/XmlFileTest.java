package dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.writer;

import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.testng.annotations.BeforeClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

public class XmlFileTest {
    protected final String OUTPUTFILE_DIR = "target/" + getClass().getSimpleName() + "/";
    protected String outputFileLocation;
    protected String countStatisticsFileLocation;
    protected String dateStatisticsFileLocation;

    @BeforeClass
    public void initialize() throws IOException {
        XMLUnit.setIgnoreWhitespace(true);
        File dir = new File(OUTPUTFILE_DIR);
        FileUtils.deleteDirectory(dir);
        FileUtils.forceMkdir(dir);
    }

    protected void assertOutputEqual(String expectedXmlBody) {
        try {
            assertXmlEqual(expectedXmlBody, readOutput(outputFileLocation));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void assertCountStatisticsEqual(String expectedXmlBody) {
        try {
            assertXmlEqual(expectedXmlBody, readOutput(countStatisticsFileLocation));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void assertDateStatisticsEqual(String expectedXmlBody) {
        try {
            assertXmlEqual(expectedXmlBody, readOutput(dateStatisticsFileLocation));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void assertXmlEqual(String expectedXmlBody, String actualDoc) {
        String expectedDoc = "<?xml version=\"1.0\" ?>" + expectedXmlBody;
        try {
            XMLUnit.setIgnoreWhitespace(true);
            assertXMLEqual("Expecting:\n" + expectedDoc + "\nFound:" + "\n" + actualDoc,
                           expectedDoc, actualDoc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String readOutput(String outputFileLocation1) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(outputFileLocation1))) {
            String sCurrentLine ;
            while ( (sCurrentLine = br.readLine()) != null) {
                sb.append(sCurrentLine);
            }
        }
        return sb.toString();
    }

    protected synchronized XmlFileIncrementalWriter createWriter(String name) {
        outputFileLocation = OUTPUTFILE_DIR + name + ".xml";
        return new XmlFileIncrementalWriter(outputFileLocation);
    }
}
