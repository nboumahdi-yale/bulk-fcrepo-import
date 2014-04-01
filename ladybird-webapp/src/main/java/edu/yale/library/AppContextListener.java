package edu.yale.library;

import edu.yale.library.ladybird.kernel.TimeUtils;
import edu.yale.library.ladybird.kernel.ApplicationProperties;
import edu.yale.library.ladybird.kernel.JobModule;
import edu.yale.library.ladybird.kernel.KernelContext;
import edu.yale.library.ladybird.kernel.ServicesManager;
import edu.yale.library.ladybird.kernel.persistence.HibernateUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppContextListener implements ServletContextListener {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AppContextListener.class);
    private static long START_HIBERNATE = 0;
    private static long START_DB = 0;

    private ServicesManager servicesManager;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.debug("Application Start up.");
        try {
            if (ApplicationProperties.CONFIG_STATE.DEFAULT_DB_CONFIGURED) {
                logger.debug("Trying to start embedded DB");
                servicesManager.initDB();
                START_DB = System.currentTimeMillis();
                logger.debug("Started embedded DB");
            }
            START_HIBERNATE = HibernateUtil.getSessionFactory().getStatistics().getStartTime();
            logger.debug("Built Session Factory");

            //bootstrap notification:
            KernelContext kernelContext = new KernelContext();
            kernelContext.setAbstractModule(new JobModule());
            KernelContext.initNotificationScheduler();
        } catch (Throwable t) {
            logger.error("Error in context initialization", t);
            t.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            //TODO check state to ensure DB is running
            if (ApplicationProperties.CONFIG_STATE.DEFAULT_DB_CONFIGURED) {
                logger.debug("Trying to stop embedded DB");
                servicesManager.stopDB();
                logger.debug("Closed embedded database. Time : " + TimeUtils.elapsedMinutes(START_DB));
            }
            HibernateUtil.shutdown();
            logger.debug("Closed Hibernate Session Factory. Time : " + TimeUtils.elapsedMinutes(START_HIBERNATE));
        } catch (Throwable t) {
            logger.error("Error in context shutdown", t);
            t.printStackTrace();
        }
    }

    public AppContextListener() {
        servicesManager = new ServicesManager(); //
    }
}
