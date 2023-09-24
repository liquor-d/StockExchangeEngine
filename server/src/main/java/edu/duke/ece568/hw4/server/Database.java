package edu.duke.ece568.hw4.server;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
    private static boolean isInitialized = false;
    private static SessionFactory sessionFactory;

    public void init() {
        if (!isInitialized) {
            try {
                Class.forName("org.postgresql.Driver");
                Connection conn = DriverManager.getConnection(
//                        "jdbc:postgresql://db:5432/hw", "postgres", "passw0rd"
                        "jdbc:postgresql://localhost:5432/hw", "postgres", "passw0rd"
                );

                conn.setAutoCommit(false);

//                Statement st=conn.createStatement();
//                String sqlStr="drop table executions";
//                st.execute(sqlStr);
//                sqlStr="drop table positions";
//                st.execute(sqlStr);
//                sqlStr="drop table orders";
//                st.execute(sqlStr);
//                sqlStr="drop table account";
//                st.execute(sqlStr);

                sessionFactory = buildSessionFactory();
                System.out.println("db connected!");
                isInitialized = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isInit() {
        return isInitialized;
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    private static SessionFactory buildSessionFactory() {
        try {
            // Load the hibernate.cfg.xml configuration file
            Configuration configuration = new Configuration().configure();
            configuration.addAnnotatedClass(Account.class);
            configuration.addAnnotatedClass(Position.class);
            configuration.addAnnotatedClass(Execution.class);
            configuration.addAnnotatedClass(BuySellOrder.class);
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
}
