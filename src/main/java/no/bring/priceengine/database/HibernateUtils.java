package no.bring.priceengine.database;

import no.bring.priceengine.dao.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {
    private static ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();

    private static SessionFactory sessionFactory = null;
    static {
        try{
//            System.setProperty("javax.xml.accessExternalDTD", "all");
//        sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(Price.class);
            configuration.addAnnotatedClass(Item.class);
            configuration.addAnnotatedClass(PriceMatrixPrice.class);
            configuration.addAnnotatedClass(PriceMatrixEntry.class);
            configuration.addAnnotatedClass(PriceMatrixEntryExt.class);
            configuration.addAnnotatedClass(SlabBasedPrice.class);
            configuration.addAnnotatedClass(SlabBasedPriceEntry.class);
            configuration.addAnnotatedClass(WeekdayBasedPrice.class);
            configuration.addAnnotatedClass(ApplicabilityCriteriaEntity.class);
            configuration.addAnnotatedClass(PriceMatrix.class);
            configuration.addAnnotatedClass(PriceMatrixZoneTable.class);
            configuration.addAnnotatedClass(PriceMatrixZonePriceLimit.class);
            configuration.addAnnotatedClass(ContractPrice.class);
            configuration.addAnnotatedClass(ContractComponent.class);
            configuration.addAnnotatedClass(ContractRole.class);
            configuration.addAnnotatedClass(Contract.class);
            configuration.addAnnotatedClass(Contractdump.class);
            configuration.addAnnotatedClass(Party.class);

            configuration.configure("hibernate.cfg.xml");
            StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
             sessionFactory = configuration.buildSessionFactory(ssrb.build());

        } catch (Throwable ex) {
            ex.printStackTrace();
            System.err.println("Initial SessionFactory creation failed." + ex);

            throw new ExceptionInInitializerError(ex);

        }

    }

    public static Session getSession() {

        Session session = null;
        if (threadLocal.get() == null) {
            // Create Session object
            session = sessionFactory.openSession();
            threadLocal.set(session);
        } else {
            session = threadLocal.get();
        }
        return session;
    }

    public static void closeSession() {
        Session session = null;
        if (threadLocal.get() != null) {
            session = threadLocal.get();
            session.close();
            threadLocal.remove();
        }
    }

    public static void closeSessionFactory() {
        sessionFactory.close();
    }
}
