package dk.statsbiblioteket.medieplatform.newspaper.statistics.collector;

import java.io.IOException;
import java.util.Properties;

import dk.statsbiblioteket.medieplatform.autonomous.iterator.common.AttributeParsingEvent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.SinkCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.StatisticCollector;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.model.Statistics;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.model.WeightedMean;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.statistics.writer.StatisticWriter;
import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.XPathSelector;
import org.w3c.dom.Document;

/**
 * Handles the collection of page level statistics.
 *
 * Uses SinkCollectors as children.
 */
public class PageCollector extends StatisticCollector {
    public static final String PAGES_IN_SECTIONS_STAT = "Pages-In-Sections";
    public static final String OCR_ACCURACY_STAT = "OCR-Accuracy";
    public static final String ALTO_IGNORE_ZERO_ACCURACY_PROPERTY = "statistics.zeroaccuracy.ignore";
    private boolean ignoreZeroAccuracy;

    private static final XPathSelector MODS_XPATH = DOM.createXPathSelector("mods", "http://www.loc.gov/mods/v3");
    private static final XPathSelector ALTO_XPATH = DOM.createXPathSelector("alto", "http://www.loc.gov/standards/alto/ns-v2#");

    /**
     * Suppress output.
     */
    @Override
    public void initialize(String name, StatisticCollector parentCollector, StatisticWriter writer, Properties properties) {
        ignoreZeroAccuracy = Boolean.parseBoolean(properties.getProperty(ALTO_IGNORE_ZERO_ACCURACY_PROPERTY));
        doNotWrite();
        super.initialize(name, parentCollector, writer, properties);
    }

    /**
     * Adds OCR accuracy statistics for the page, if this is a alto.xml attribute.
     * @param event The event to read the alto xml from
     */
    @Override
    public void handleAttribute(AttributeParsingEvent event) {
        if (event.getName().endsWith("alto.xml")) {
            addAltoWordAccuracyStatistics(event);
        } else if (event.getName().endsWith("mods.xml")) {
            addSectionStatistics(event);
        }
    }

    private void addAltoWordAccuracyStatistics(AttributeParsingEvent event) {
        Double accuracy = readAccuracy(event);
        if (!ignoreZeroAccuracy || accuracy > 0) {
            getStatistics().addRelative( OCR_ACCURACY_STAT, new WeightedMean(accuracy, 1));
        }
    }

    private void addSectionStatistics(AttributeParsingEvent event) {
        String section = readSection(event);
        Statistics sectionStatistics = new Statistics();
        sectionStatistics.addCount(section, 1L);
        getStatistics().addSubstatistic(PAGES_IN_SECTIONS_STAT, sectionStatistics);
    }

    public static String readSection(AttributeParsingEvent event) throws NumberFormatException {
        Document doc;
        try {
            doc = DOM.streamToDOM(event.getData(), true);
            if (doc == null) {
                throw new RuntimeException("Could not parse xml");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final String sectionXPath="mods:mods/mods:part/mods:detail[@type=\"sectionLabel\"]/mods:number";
        String section = MODS_XPATH.selectString(doc, sectionXPath);
        return section;
    }

    @Override
    protected StatisticCollector createChild(String eventName) {
        return new SinkCollector();
    }

    private static Double readAccuracy(AttributeParsingEvent event) throws NumberFormatException {
        Document doc;
        try {
            doc = DOM.streamToDOM(event.getData(), true);
            if (doc == null) {
                throw new RuntimeException("Could not parse xml");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final String accuracyXPath="alto:alto/alto:Layout/alto:Page/@ACCURACY";
        String accuracyString = ALTO_XPATH.selectString(doc, accuracyXPath);
        return Double.parseDouble(accuracyString);
    }
}
