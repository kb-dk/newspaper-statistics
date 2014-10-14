package dk.statsbiblioteket.medieplatform.newspaper.statistics;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.TreeProcessorAbstractRunnableComponent;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.EventRunner;
import dk.statsbiblioteket.medieplatform.autonomous.iterator.eventhandlers.TreeEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class StatisticsRunnableComponent extends TreeProcessorAbstractRunnableComponent {

    private Logger log = LoggerFactory.getLogger(getClass());
    private Properties properties;

    protected StatisticsRunnableComponent(Properties properties) {
        super(properties);
        this.properties = properties;
    }

    @Override
    public String getEventID() {
        return "Statistics_Generated";
    }

    @Override
    public void doWorkOnItem(Batch batch, ResultCollector resultCollector) throws Exception {
        log.info("Starting statistics generation for '{}'", batch.getFullID());
        List<TreeEventHandler> statisticGenerator = Arrays.asList(new TreeEventHandler[]
                { new StatisticGenerator(batch, properties) });
        EventRunner eventRunner = new EventRunner(createIterator(batch), statisticGenerator, resultCollector);
        eventRunner.run();
        log.info("Done generating statistics '{}', success: {}", batch.getFullID(), resultCollector.isSuccess());
    }
}
