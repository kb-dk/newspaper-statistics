package dk.statsbiblioteket.medieplatform.newspaper.statistics;

import java.util.Properties;

import dk.statsbiblioteket.medieplatform.autonomous.CallResult;
import dk.statsbiblioteket.medieplatform.autonomous.RunnableComponent;
import dk.statsbiblioteket.medieplatform.autonomous.SBOIDomsAutonomousComponentUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticsComponent {

    private static Logger log = LoggerFactory.getLogger(StatisticsComponent.class);

    /**
     * The class must have a main method, so it can be started as a command line tool
     *
     * @param args the arguments.
     *
     * @throws Exception
     * @see dk.statsbiblioteket.medieplatform.autonomous.AutonomousComponentUtils#parseArgs(String[])
     */
    public static void main(String[] args) throws Exception {
        log.info("Starting with args {}", args);

        //Parse the args to a properties construct
        Properties properties = SBOIDomsAutonomousComponentUtils.parseArgs(args);

        //make a new runnable component from the properties
        RunnableComponent component = new StatisticsRunnableComponent(properties);

        CallResult result = SBOIDomsAutonomousComponentUtils.startAutonomousComponent(properties, component);
        log.info(result.toString());
        System.exit(result.containsFailures());
    }
}
