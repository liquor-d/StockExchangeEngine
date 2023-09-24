package edu.duke.ece568.hw4.server;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ExecutionManagerTest {
    @Test
    void test_addOrder(){
        Database a = new Database();
        a.init();
        SessionFactory sessionFactory = a.getSessionFactory();
        Session session = sessionFactory.openSession();

        ExecutionManager em = new ExecutionManager();
        //Buy price33, amount44, acount22
        String str = em.addOrder(2.5, 1.3, "today", 22, true, session);
        System.out.println(str);
        str = em.addOrder(2.4, 0.5, "today", 22, true, session);
        System.out.println(str);
        str = em.addOrder(2.3, 0.7, "today", 22, true, session);
        System.out.println(str);
        //sell 之前的order还剩0.3 新的execution1.9 这个order还剩0

        str = em.addOrder(2.2, 1.9, "yesterday", 24, true, session);
        System.out.println(str);


        str = em.addOrder(500, 200, "today", 24, true, session);
        System.out.println(str);
        str = em.addOrder(2.2, 1.9, "yesterday", 24, false, session);
        System.out.println(str);
        str = em.addOrder(22, 55, "today", 24, true, session);
        System.out.println(str);
        str = em.addOrder(70, 1.9, "yesterday", 22, true, session);
        System.out.println(str);


        session.close();
    }
    @Test
    void test_cancel(){
        Database a = new Database();
        a.init();
        SessionFactory sessionFactory = a.getSessionFactory();
        Session session = sessionFactory.openSession();
        ExecutionManager em = new ExecutionManager();

        String str = em.addCancelExecution(2359, session);
        System.out.println(str);
//        List
    }
    @Test
    void test_query(){
        Database a = new Database();
        a.init();
        SessionFactory sessionFactory = a.getSessionFactory();
        Session session = sessionFactory.openSession();
        ExecutionManager em = new ExecutionManager();


        Execution execution = em.queryCancelExecution(2362, session);
        if(execution == null){
            System.out.println("is not cancel");
        }else{
            System.out.println(execution.getDate());
        }

        List<Execution>  executionList = em.queryExecuteExecution(2362, session);
        System.out.println("Here is execution list length");
        System.out.println(executionList.size());
//        List
    }
}
