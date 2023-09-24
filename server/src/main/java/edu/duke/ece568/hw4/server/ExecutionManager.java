package edu.duke.ece568.hw4.server;

import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class ExecutionManager {
    public ExecutionManager() {
    }

    public String addOrder(double limitPrice, double amount, String symbol,
                           int accountId, boolean isBuy, Session session) {
        CreateManager cm = new CreateManager();
        List<Account> accounts = cm.findAccount(accountId, session);
        if (accounts.size() == 0) {
            return "-Account does not exist";
        }
        Account account = accounts.get(0);
        //if buyorder change balance
        //if sellorder change position
        if (isBuy) {
            String err_updateBalance = updateAccountBalance(accountId, limitPrice * amount, session);
            if (err_updateBalance != null) {
                return err_updateBalance;
            }
        } else {
//            System.out.println(isBuy);
            String err_updatePosition = updateAccountPosition(accountId, symbol, amount, session);
            if (err_updatePosition != null) {
                return err_updatePosition;
            }
        }
//        if (isBuy && getTempBalance(accountId, session) < limitPrice * amount) {
//            return "-not enough balance in account";
//        }
//        if (!isBuy && getTempPositionAmount(accountId, symbol, session) < amount) {
//            return "-not enough position to sell in account";
//        }
        Transaction t = session.beginTransaction();
        BuySellOrder order = new BuySellOrder(
                limitPrice, amount, account, symbol, isBuy);
        session.save(order);
        t.commit();

        BuySellOrder tempOrder = compareOrder(order, session);
        int newOrderId = order.getOrderId();
        while (tempOrder != null) {
            int nowOrderId = order.getOrderId();
            System.out.println("Here is going to execute");
            System.out.println(order.isBuy());
            System.out.println(tempOrder.isBuy());
            System.out.println(order.getSymbol());
            System.out.println(tempOrder.getSymbol());
            System.out.println(order.getLimitPrice());
            System.out.println(tempOrder.getLimitPrice());
            String err_execute = executeOrder(nowOrderId, tempOrder.getOrderId(), session);
            if (err_execute != null) {
                return err_execute;
            }
            List<BuySellOrder> orderList = findOrder(nowOrderId, session);
            BuySellOrder updatedOrder = orderList.get(0);
            tempOrder = compareOrder(updatedOrder, session);
        }

        return Integer.toString(newOrderId);
    }

    public BuySellOrder compareOrder(BuySellOrder nowOrder, Session session) {
        if (nowOrder.getAmount() == 0) {
            return null;
        }
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<BuySellOrder> criteria = builder.createQuery(BuySellOrder.class);
        Root<BuySellOrder> root = criteria.from(BuySellOrder.class);
        criteria.select(root);
        criteria.where(builder.equal(root.get("symbol"), nowOrder.getSymbol()));

        if (nowOrder.isBuy()) {
            criteria.where(builder.equal(root.get("isBuyOrder"), false),
                    builder.equal(root.get("symbol"), nowOrder.getSymbol()),
                    builder.lessThan(root.get("limitPrice"), nowOrder.getLimitPrice()),
                    builder.greaterThan(root.get("amount"), 0.00001),
                    builder.not(builder.equal(root.get("account"), nowOrder.getAccount())));
            criteria.orderBy(builder.asc(root.get("limitPrice"))
                    , builder.asc(root.get("orderId"))
            );
        } else { //sellOrder
            criteria.where(builder.equal(root.get("isBuyOrder"), true),
                    builder.equal(root.get("symbol"), nowOrder.getSymbol()),
                    builder.greaterThan(root.get("limitPrice"), nowOrder.getLimitPrice()),
                    builder.greaterThan(root.get("amount"), 0.00001),
                    builder.not(builder.equal(root.get("account"), nowOrder.getAccount())));
            criteria.orderBy(builder.desc(root.get("limitPrice"))
                    , builder.asc(root.get("orderId"))
            );
        }
        List<BuySellOrder> orderList = session.createQuery(criteria).getResultList();
        if (orderList.size() == 0) {
            return null;
        }
        BuySellOrder resOrder = orderList.get(0);
        return resOrder;
    }

    public String executeOrder(int orderId1, int orderId2, Session session) {
        List<BuySellOrder> OrderList1 = findOrder(orderId1, session);
        BuySellOrder order1 = OrderList1.get(0);
        List<BuySellOrder> OrderList2 = findOrder(orderId2, session);
        BuySellOrder order2 = OrderList2.get(0);

        int buyOrderId, sellOrderId;
        BuySellOrder buyOrder, sellOrder;
        if (order1.isBuy()) {
            buyOrderId = orderId1;
            sellOrderId = orderId2;
            buyOrder = order1;
            sellOrder = order2;
        } else {
            buyOrderId = orderId2;
            sellOrderId = orderId1;
            buyOrder = order2;
            sellOrder = order1;
        }

        double amount = updateOrderAndAddExecution(buyOrderId, sellOrderId, session);
//        System.out.println("Here is executed amount");
//        System.out.println(amount);
        System.out.println("Here is going to update position in buy");
        System.out.println(buyOrder.isBuy());
        System.out.println(buyOrder.getSymbol());
        System.out.println(0 - amount);
        String errBuyBPosition = updateAccountPosition(buyOrder.getAccount().getAccountId(),
                buyOrder.getSymbol(), 0 - amount, session);
        if (errBuyBPosition != null) {
            return errBuyBPosition;
        }
        double executionPrice;
        if (buyOrder.getOrderId() < sellOrder.getOrderId()) {   //buyorder price
            executionPrice = buyOrder.getLimitPrice();
            String errSellBalance = updateAccountBalance(sellOrder.getAccount().getAccountId(),
                    0 - executionPrice * amount, session);
            return errSellBalance;
        } else {
            executionPrice = sellOrder.getLimitPrice();
            double priceChange = buyOrder.getLimitPrice() - executionPrice;
            String errBuyBalance = updateAccountBalance(buyOrder.getAccount().getAccountId(),
                    0 - priceChange * amount, session);
            if (errBuyBalance != null) {
                return errBuyBalance;
            }
            String errSellBalance = updateAccountBalance(sellOrder.getAccount().getAccountId(),
                    0 - executionPrice * amount, session);
            return errSellBalance;
        }

//        updateAccount(buyOrderId, sellOrderId, amount, session);
//        String errUpdatePosition = updatePosition(buyOrderId, sellOrderId, session);
    }

    public boolean isQueryValid(int orderId, Session session) {
        List<BuySellOrder> orderList = findOrder(orderId, session);
        return orderList.size() != 0;
    }

    public double queryOrderShare(int orderId, Session session) {
        List<BuySellOrder> orderList = findOrder(orderId, session);
        BuySellOrder order = orderList.get(0);
        double share = order.getAmount();
        return share;
    }

    public Execution queryCancelExecution(int orderId, Session session) {
        List<Execution> executionList = findExecution(orderId, "cancel", session);
        if (executionList.size() == 0) {
            return null;
        }
        Execution execution = executionList.get(0);
        return execution;
    }

    public List<Execution> queryExecuteExecution(int orderId, Session session) {
        List<Execution> executionList = findExecution(orderId, "execute", session);
        if (executionList.size() == 0) {
            return null;
        }
        return executionList;
    }

    public String addCancelExecution(int orderId, Session session) {
        List<BuySellOrder> orderList = findOrder(orderId, session);
        if (orderList.size() == 0) {
            return "This order does not exist";
        }
        Execution execution = queryCancelExecution(orderId, session);
        if (execution != null) {
            return "This order has already been canceled";
        }
        BuySellOrder order = orderList.get(0);
        if (order.getAmount() < 0.0001) {
            return "This order has been executed totally";
        }
        if (order.isBuy()) {
            updateAccountBalance(order.getAccount().getAccountId(),
                    0 - order.getAmount() * order.getLimitPrice(), session);
        } else {
            updateAccountPosition(order.getAccount().getAccountId(),
                    order.getSymbol(), 0 - order.getAmount(), session);
        }
        addExecution("cancel", 0, order.getAmount(), orderId, session);

        return null;
    }

    //=====================================================
    //
    //             below is child function
    //
    //=====================================================
    public double getTempBalance(int accountId, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<BuySellOrder> criteria = builder.createQuery(BuySellOrder.class);
        Root<BuySellOrder> root = criteria.from(BuySellOrder.class);
        criteria.select(root);

        CreateManager cm = new CreateManager();
        List<Account> accountList = cm.findAccount(accountId, session);
        Account account = accountList.get(0);
        criteria.where(builder.equal(root.get("isBuyOrder"), true),
                builder.equal(root.get("account"), account));
        List<BuySellOrder> orderList = session.createQuery(criteria).getResultList();
        double tempBalance = account.getBalance();
        for (BuySellOrder order : orderList) {
            double amount = order.getAmount();
            double price = order.getLimitPrice();
            tempBalance -= amount * price;
        }
        return tempBalance;
    }

    public double getTempPositionAmount(int accountId, String symbol, Session session) {
        CreateManager cm = new CreateManager();
        List<Account> accountList = cm.findAccount(accountId, session);
        Account account = accountList.get(0);
        Position nowposition = findPosition(account, symbol, session);

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<BuySellOrder> criteria = builder.createQuery(BuySellOrder.class);
        Root<BuySellOrder> root = criteria.from(BuySellOrder.class);
        criteria.select(root);

        criteria.where(builder.equal(root.get("isBuyOrder"), false),
                builder.equal(root.get("symbol"), symbol),
                builder.equal(root.get("account"), account));
        List<BuySellOrder> orderList = session.createQuery(criteria).getResultList();

        double tempAmount = nowposition.getAmount();
        for (BuySellOrder order : orderList) {
            tempAmount -= order.getAmount();
        }

        return tempAmount;
    }

    public void addExecuteExecution(double price, double amount, int orderId, Session session) {
        addExecution("execute", price, amount, orderId, session);
    }

    public void addExecution(String type, double price, double amount, int orderId, Session session) {
        List<BuySellOrder> orderList = findOrder(orderId, session);
        BuySellOrder order = orderList.get(0);

        Transaction t = session.beginTransaction();
        Execution execution = new Execution(type, price, amount, order);
        session.save(execution);
        t.commit();
    }

    public String updateAccountPosition(int accountId, String symbol, double amount, Session session) {
        CreateManager cm = new CreateManager();
        List<Account> accountList = cm.findAccount(accountId, session);
        Account account = accountList.get(0);
        System.out.println("reach here in update position");
        System.out.println(accountId);
        System.out.println(symbol);
        Position position = findPosition(account, symbol, session);
        if (position == null) {
//            System.out.println(accountId);
//            System.out.println(symbol);
//            System.out.println(amount);
            return "-Account have not enough position amount";
        }
        double nowAmount = position.getAmount();
        if (nowAmount < amount) {
            return "-Account have not enough position amount";
        }

        Transaction t = session.beginTransaction();
        position.setAmount(nowAmount - amount);

        session.update(position);
        t.commit();
        return null;
    }

    public String updatePosition(int buyOrderId, int sellOrderId, Session session) {
        List<BuySellOrder> buyOrderList = findOrder(buyOrderId, session);
        BuySellOrder buyOrder = buyOrderList.get(0);
        String symbol = buyOrder.getSymbol();
        List<BuySellOrder> sellOrderList = findOrder(sellOrderId, session);
        BuySellOrder sellOrder = sellOrderList.get(0);
        Account buyAccount = buyOrder.getAccount();
        Account sellAccount = sellOrder.getAccount();

        Position buyPos = findPosition(buyAccount, symbol, session);
        Position sellPos = findPosition(sellAccount, symbol, session);

        Transaction t = session.beginTransaction();
        double amountChange = Math.max(buyOrder.getAmount(), sellOrder.getAmount());
        double buyAmount = buyPos.getAmount();
        double sellAmount = sellPos.getAmount();

        if (sellAmount < amountChange) {
            return "-Account have not enough position";
        }
        buyPos.setAmount(buyAmount + amountChange);
        sellPos.setAmount(sellAmount - amountChange);

        session.update(buyPos);
        session.update(sellPos);
        t.commit();

        return null;
    }

    public String updateAccountBalance(int accountId, double balanceChange, Session session) {
        CreateManager cm = new CreateManager();
        List<Account> accountList = cm.findAccount(accountId, session);
        Account account = accountList.get(0);

        double nowBalance = account.getBalance();
        if (nowBalance < balanceChange) {
            return "-Account have not enough balance";
        }
        Transaction t = session.beginTransaction();
        account.setBalance(nowBalance - balanceChange);

        session.update(account);
        t.commit();

        return null;
    }

    public void updateAccount(int buyOrderId, int sellOrderId, double amount, Session session) {
        List<BuySellOrder> buyOrderList = findOrder(buyOrderId, session);
        BuySellOrder buyOrder = buyOrderList.get(0);
        List<BuySellOrder> sellOrderList = findOrder(sellOrderId, session);
        BuySellOrder sellOrder = sellOrderList.get(0);
        Account buyAccount = buyOrder.getAccount();
        Account sellAccount = sellOrder.getAccount();

        Transaction t = session.beginTransaction();
        double price = sellOrder.getLimitPrice();
        double sellBalance = sellAccount.getBalance();
        double buyBalance = buyAccount.getBalance();
//        System.out.println("Balance change is :");
//        System.out.println(price * amount);
        sellAccount.setBalance(sellBalance + price * amount);
        buyAccount.setBalance(buyBalance - price * amount);

        session.update(sellAccount);
        session.update(buyAccount);
        t.commit();
    }

    public double updateOrderAndAddExecution(int buyOrderId, int sellOrderId, Session session) {
        List<BuySellOrder> buyOrderList = findOrder(buyOrderId, session);
        BuySellOrder buyOrder = buyOrderList.get(0);
        List<BuySellOrder> sellOrderList = findOrder(sellOrderId, session);
        BuySellOrder sellOrder = sellOrderList.get(0);

        Transaction t = session.beginTransaction();
        double buyAmount = buyOrder.getAmount();
        double sellAmount = sellOrder.getAmount();
        double executeAmount = Math.min(buyAmount, sellAmount);
        if (buyAmount == sellAmount) {
            buyOrder.setAmount(0);
            sellOrder.setAmount(0);
        } else if (buyAmount > sellAmount) {        //execute sellAmount
            buyOrder.setAmount(buyAmount - executeAmount);
            sellOrder.setAmount(0);
        } else {        //execute buyAmount
            buyOrder.setAmount(0);
            sellOrder.setAmount(sellAmount - executeAmount);
        }
        session.update(buyOrder);
        session.update(sellOrder);
        t.commit();
        addExecuteExecution(sellOrder.getLimitPrice(), executeAmount, buyOrderId, session);
        addExecuteExecution(sellOrder.getLimitPrice(), executeAmount, sellOrderId, session);
        return executeAmount;
    }

    public List<BuySellOrder> findOrder(int orderId, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<BuySellOrder> criteria = builder.createQuery(BuySellOrder.class);
        Root<BuySellOrder> root = criteria.from(BuySellOrder.class);
        criteria.select(root);

        criteria.where(builder.equal(root.get("orderId"), orderId));
        List<BuySellOrder> orderList = session.createQuery(criteria).getResultList();

        return orderList;
    }

    public List<Execution> findExecution(int orderId, String type, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Execution> criteria = builder.createQuery(Execution.class);
        Root<Execution> root = criteria.from(Execution.class);
        criteria.select(root);

        List<BuySellOrder> orderList = findOrder(orderId, session);
        BuySellOrder order = orderList.get(0);
        criteria.where(builder.equal(root.get("order"), order),
                builder.equal(root.get("type"), type));
        List<Execution> executionList = session.createQuery(criteria).getResultList();

        return executionList;
    }

    public Position findPosition(Account account, String symbol, Session session) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Position> criteria = builder.createQuery(Position.class);
        Root<Position> root = criteria.from(Position.class);
        criteria.select(root);

        criteria.where(builder.equal(root.get("account"), account),
                builder.equal(root.get("symbol"), symbol));
        List<Position> positionList = session.createQuery(criteria).getResultList();
        if (positionList.size() == 0) {
            return null;
        }
        Position position = positionList.get(0);

        return position;
    }
}
