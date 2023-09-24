package edu.duke.ece568.hw4.server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

import org.hibernate.*;
import org.hibernate.Transaction;


class DatabaseTest {
    @Test
    void test_init() {
      Database a = new Database();
      assertEquals(false, a.isInit());
      a.init();
      assertEquals(true, a.isInit());
    }
    @Test
    void test_tables(){
        Database a = new Database();
        a.init();
        SessionFactory sessionFactory = a.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction t = session.beginTransaction();

        Account acc1 = new Account (22, 566);
        Account acc2 = new Account (24, 588);
//        Order order1 = new Order (2.66, 3.33, acc1, "good", true);
        BuySellOrder buyOrder1 = new BuySellOrder(
                2.66, 3.33, acc1, "good", true);
        Position pos1 = new Position("good", 20, acc1);
        Position pos2 = new Position("good", 20, acc2);

        session.save(acc1);
        session.save(acc2);
//        session.persist(order1);
        session.save(buyOrder1);
        session.save(pos1);
        session.save(pos2);

        t.commit();
        session.close();
    }

    @Test
    void test_match(){
        Database a = new Database();
        a.init();
        SessionFactory sessionFactory = a.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction t = session.beginTransaction();

        Account acc1 = new Account (22, 566);
        Account acc2 = new Account (24, 588);
        Account acc3 = new Account (28, 688);
        Account acc4 = new Account (29, 988);
//        Order order1 = new Order (2.66, 3.33, acc1, "good", true);
        BuySellOrder buyOrder1 = new BuySellOrder(
                2.66, 3.33, acc1, "good", true);
        BuySellOrder buyOrder2 = new BuySellOrder(
                2.77, 3.33, acc2, "good", true);
        BuySellOrder buyOrder3 = new BuySellOrder(
                2.88, 3.33, acc3, "good", true);
        BuySellOrder buyOrder4 = new BuySellOrder(
                2.99, 3.33, acc2, "good", true);
        BuySellOrder buyOrder5 = new BuySellOrder(
                3.66, 3.33, acc1, "good", true);
        BuySellOrder buyOrder6 = new BuySellOrder(
                3.77, 3.33, acc1, "good", true);

        BuySellOrder buyOrder7 = new BuySellOrder(
                2.80, 3.33, acc4, "good", false);
        BuySellOrder buyOrder8 = new BuySellOrder(
                2.90, 3.33, acc4, "good", false);
//        Position pos1

        session.persist(acc1);
        session.persist(acc2);
        session.persist(acc3);
        session.persist(acc4);
//        session.persist(order1);
        session.persist(buyOrder1);
        session.persist(buyOrder2);
        session.persist(buyOrder3);
        session.persist(buyOrder4);
        session.persist(buyOrder5);
        session.persist(buyOrder6);
        session.persist(buyOrder7);
        session.persist(buyOrder8);

        t.commit();

        ExecutionManager em = new ExecutionManager();
        BuySellOrder resOrder = em.compareOrder(buyOrder7, session);
        System.out.println("matching order is : ");
        System.out.println(resOrder.getOrderId());
        session.close();
    }

    @Test
    void test_update() {
        Database a = new Database();
        a.init();
        SessionFactory sessionFactory = a.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction t = session.beginTransaction();

        Account acc1 = new Account (22, 566);
        Account acc2 = new Account (24, 588);

        session.save(acc1);
        session.save(acc2);

        t.commit();

        Transaction t2 = session.beginTransaction();
        CreateManager cm = new CreateManager();
        List<Account> accountList = cm.findAccount(22, session);
        Account account = (Account)accountList.get(0);
        account.setBalance(700);
        session.update(account);
        t2.commit();

        session.close();
    }
}
