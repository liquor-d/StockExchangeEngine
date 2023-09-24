package edu.duke.ece568.hw4.server;

import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class MatchOrder {
    //    private boolean isBuyMatch;   //true = input buy, false = input sell
    public MatchOrder() {
//        if (order.isBuy()){
//            isBuyMatch = true;
//        }
//        else{
//            isBuyMatch = false;
//        }
    }


//    public buySellOrder compareOrder(buySellOrder nowOrder, Session session){
//        CriteriaBuilder builder = session.getCriteriaBuilder();
//        CriteriaQuery<buySellOrder> criteria = builder.createQuery(buySellOrder.class);
//        Root<buySellOrder> root = criteria.from(buySellOrder.class);
//        criteria.select(root);
//
////        Criteria criteria = session.createCriteria(Order.class);
//        if(nowOrder.isBuy()){
////            criteria.add(Restrictions.eq("isBuyOrder", false));
////            criteria.add(Restrictions.lt("limitPrice", nowOrder.getLimitPrice()));
////            criteria.addOrder(Order.asc("limitPrice"));
//            criteria.where(builder.equal(root.get("isBuyOrder"), false));
//            criteria.where(builder.lessThan(root.get("limitPrice"), nowOrder.getLimitPrice()));
//            criteria.orderBy(builder.asc(root.get("limitPrice")));
//        }
//        else{ //sellOrder
////            criteria.add(Restrictions.eq("isBuyOrder", true));
////            criteria.add(Restrictions.gt("limitPrice", nowOrder.getLimitPrice()));
////            criteria.addOrder(Order.desc("limitPrice"));
//            criteria.where(builder.equal(root.get("isBuyOrder"), true));
//            criteria.where(builder.greaterThan(root.get("limitPrice"), nowOrder.getLimitPrice()));
//            criteria.orderBy(builder.desc(root.get("limitPrice")));
//        }
//        List<buySellOrder> orderList = session.createQuery(criteria).getResultList();
//        if(orderList.size() == 0){
//            return null;
//        }
//        buySellOrder resOrder = (buySellOrder)orderList.get(0);
//        return resOrder;
//    }

    public void executeBalance(BuySellOrder buyOrder, BuySellOrder sellOrder,
                               double balance, Session session) {
        Account buyAccount = buyOrder.getAccount();
        Account sellAccount = sellOrder.getAccount();

        Transaction t = session.beginTransaction();

//        Account newBuyAccount = new Account(
//                buyAccount.getAccountId(), buyAccount.getBalance() - balance);
//        Account newSellAccount = new Account(
//                sellAccount.getAccountId(), sellAccount.getBalance() + balance);
        buyAccount.setBalance(buyAccount.getBalance() - balance);
        sellAccount.setBalance(buyAccount.getBalance() + balance);

        session.update(buyAccount);
        session.update(sellAccount);
        t.commit();
    }


    public List<BuySellOrder> getAccountOrder(Account account, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<BuySellOrder> buyCri = builder.createQuery(BuySellOrder.class);
        Root<BuySellOrder> buyRoot = buyCri.from(BuySellOrder.class);
        buyCri.select(buyRoot);
        buyCri.where(builder.equal(buyRoot.get("accountId"), account.getAccountId()));
        List<BuySellOrder> buyOrderList = session.createQuery(buyCri).getResultList();
//        buySellOrder order = (Account)buyAccList.get(0);
        return buyOrderList;
    }

//    public void editOrder()

}
