package edu.duke.ece568.hw4.server;

import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class CreateManager {

    public CreateManager() {
    }

    public String addAccount(int accountId, double balance, Session session) {
        List<Account> orderList = findAccount(accountId, session);
        if (orderList.size() != 0) {
            return "Account can not be added twice";
        }

        Transaction t = session.beginTransaction();
        Account acc1 = new Account(accountId, balance);
        session.save(acc1);
        t.commit();
        return null;
    }

    public String addPosition(String symbol, double amount, int accountId, Session session) {
        List<Account> accountList = findAccount(accountId, session);
        if (accountList.size() == 0) {
            return "Position can not be added to a non-existing account";
        }
        Account account = accountList.get(0);

        Position position = positionExist(symbol, accountId, session);
//        if (position == null){
//            System.out.println("Here position is null");
//        }
        Transaction t = session.beginTransaction();
        if (position == null) {
            Position pos1 = new Position(symbol, amount, account);
            session.save(pos1);
        } else {
            double currAmount = position.getAmount();
            position.setAmount(currAmount + amount);
            session.update(position);
        }
        t.commit();

        return null;
    }

    public Position positionExist(String symbol, int accountId, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Position> criteria = builder.createQuery(Position.class);
        Root<Position> root = criteria.from(Position.class);
        criteria.select(root);

        CreateManager cm = new CreateManager();
        List<Account> accountList = cm.findAccount(accountId, session);
        Account account = accountList.get(0);

//        System.out.println("Here is accountId");
//        System.out.println(accountId);
        criteria.where(builder.equal(root.get("account"), account),
                builder.equal(root.get("symbol"), symbol));
        List<Position> positionList = session.createQuery(criteria).getResultList();
        if (positionList.size() == 0) {
            return null;
        }
        Position position = positionList.get(0);
        return position;
    }
//    public boolean addPositions(HashMap<String, String> hashmap, int accountId, Session session){
//        Set<String> keyset = hashmap.keySet();
//        for (String symbol : keyset){
//            String amountStr = hashmap.get(symbol);
//            double amount = Double.valueOf(amountStr);
//            boolean flag = addPosition(symbol, amount, accountId, session);
//            if(!flag){
//                return false;
//            }
//        }
//        return true;
//    }

    public List<Account> findAccount(int accountId, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
        Root<Account> root = criteria.from(Account.class);
        criteria.select(root);

        criteria.where(builder.equal(root.get("accountId"), accountId));
        List<Account> orderList = session.createQuery(criteria).getResultList();

        return orderList;
    }

}
