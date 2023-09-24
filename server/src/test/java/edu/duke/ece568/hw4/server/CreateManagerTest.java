package edu.duke.ece568.hw4.server;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

public class CreateManagerTest {
    @Test
    void test_addAccountAndPosition(){
        Database a = new Database();
        a.init();
        SessionFactory sessionFactory = a.getSessionFactory();
        Session session = sessionFactory.openSession();

        CreateManager cm = new CreateManager();
//        cm.addAccount(22, 566, session);
        String str = cm.addAccount(22, 500, session);
        System.out.println(str);
        str = cm.addAccount(24, 500, session);
        System.out.println(str);

        str = cm.addPosition("today", 500, 22, session);
        System.out.println(str);
        str = cm.addPosition("today", 500, 24, session);
        System.out.println(str);
        session.close();
    }
}
