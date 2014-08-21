package dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.writer;

import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.model.StatisticsKey;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Writes statistics to a xml fil as the different statistics are generated minimising the
 * in-memory model.
 */
public class XmlFileIncrementalWriter implements StatisticWriter {
    private static Logger log = LoggerFactory.getLogger(XmlFileIncrementalWriter.class);
    private final XMLStreamWriter out;

    public XmlFileIncrementalWriter(String outputFilePath) {
        log.info("Preparing to write statistics to " + outputFilePath);
        File outputFile = new File(outputFilePath);
        try {
            FileUtils.forceMkdir(outputFile.getParentFile());
            OutputStream outputStream = new FileOutputStream(new File(outputFilePath));

            out = XMLOutputFactory.newInstance().createXMLStreamWriter(
                    new OutputStreamWriter(outputStream, "utf-8"));
            out.writeStartDocument();
            out.writeStartElement("Statistics");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize xml writer for statistics for file "+outputFile.getAbsolutePath(), e);
        }
    }

    @Override
    public void addNode(String type, String name) {
        addNode(type, name, null);
    }
    @Override
    public void addNode(String type, String name, String summary) {
        try {
            out.writeStartElement(replaceSpaces(type));
            if (name != null) out.writeAttribute("name", name);
            if (summary != null) out.writeAttribute("summary", summary);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Failed to add node: " + name, e);
        }
    }

    @Override
    public void endNode() {
        try {
            out.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException("Failed to end node.", e);
        }
    }

    /**
     * Will write a statistic element. Ex: addStatistic(pages, 3)
     * will add a line:
     * <pages>1</pages>.
     */
    @Override
    public void addStatistic(StatisticsKey key, Number metric) {
        try {
            out.writeStartElement(key.getType());
            if (key.isNameDefined()) {
                out.writeAttribute("name", key.getName());
            }
            out.writeCharacters(metric.toString());
            out.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException("Failed to write statistic.", e);
        }
    }

    @Override
    public void finish() {
        try {
            out.writeEndDocument();
            out.flush();
            out.close();
        } catch (XMLStreamException e) {
            throw new RuntimeException("Failed to close xml writer.", e);
        }
        log.info("Finished writing statistics ");
    }

    private String replaceSpaces(String input) {
        return input.replace(' ', '_');
    }
}
