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

    @BeforeClass
    public void initialize() throws IOException {
        XMLUnit.setIgnoreWhitespace(true);
        File dir = new File(OUTPUTFILE_DIR);
        FileUtils.deleteDirectory(dir);
        FileUtils.forceMkdir(dir);
    }

    protected void assertOutputEqual(String expectedXmlBody) {
        String expectedDoc = "<?xml version=\"1.0\" ?>" + expectedXmlBody;
        try {
            XMLUnit.setIgnoreWhitespace(true);
            assertXMLEqual(expectedDoc, readOutput());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String readOutput() throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(outputFileLocation))) {
            String sCurrentLine ;
            while ( (sCurrentLine = br.readLine()) != null) {
                sb.append(sCurrentLine);
            }
        }
        return sb.toString();
    }

    protected XmlFileIncrementalWriter createWriter(String name) {
        outputFileLocation = OUTPUTFILE_DIR + name + ".xml";
        return new XmlFileIncrementalWriter(outputFileLocation);
    }
}
