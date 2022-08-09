package org.example;


import javax.persistence.*;
import java.util.Scanner;

public class App {
    private static final String NAME = "ForBank";
    static EntityManagerFactory emf;
    static EntityManager em;
    public static void main( String[] args ) {

        try {
            Scanner sc = new Scanner(System.in);
            emf = Persistence.createEntityManagerFactory(NAME);
            em = emf.createEntityManager();
            User u1 = new User("Sergey");
            new Base<User>().aTransaction(u1,em);
            User u2 = new User("Andrey");
            new Base<User>().aTransaction(u2,em);
            User u3 = new User("Maksim");
            new Base<User>().aTransaction(u3,em);
            Rate rate = new Rate();
            new Base<Rate>().aTransaction(rate,em);

            while (true) {
                System.out.print("Please enter your name: ");
                String name = sc.nextLine();
                TypedQuery<User> tQuery = em.createQuery("select c from User c where c.name = :name", User.class);
                tQuery.setParameter("name", name);
                User user;
                try {
                    user = tQuery.getSingleResult();
                } catch (NoResultException ex) {
                    user = null;
                    System.out.println("Client not found, please try again");
                }
                    while (user != null) {
                        System.out.println("Hi " + user.getName());
                        Account ac = user.getAccount();
                        System.out.println("You have:");
                        System.out.println(ac.getUah() + " UAH; " + ac.getUsd() + " USD; " + ac.getEur() + " EUR.");
                        System.out.println("1: Deposit money");
                        System.out.println("2: Calculate my money");
                        System.out.println("3: Convert my money");
                        System.out.println("4: Send money");
                        System.out.println("9: for exit");
                        System.out.print("--> ");
                        String s = sc.nextLine();
                        switch (s) {
                            case "1":
                                addMoney(sc, ac);
                                break;
                            case "2":
                                getMyMany(sc, ac);
                                break;
                            case "3":
                                convertMoney(sc,ac);
                                break;
                            case "4":
                                startTransaction(sc,user);
                                break;
                            case "9":
                                user = null;
                                break;
                            default:
                                return;
                        }

                    }
            }


        } finally {
            em.close();
            emf.close();
        }

    }

    private static void addMoney (Scanner sc, Account acc) {
        System.out.print("1: UAH, 2: USD, 3: EUR --> ");
        String m = sc.nextLine();
        System.out.print("Enter sum --> ");
        String s = sc.nextLine();
        double sum = Double.parseDouble(s);

        if (!(m.equals("1")||m.equals("2")||m.equals("3"))) {
            System.out.println("Invalid request");
            return;
        }

        em.getTransaction().begin();
        try {
            if (m.equals("1")) {
                acc.setUah(acc.getUah() + sum);
            } else if (m.equals("2")) {
                acc.setUsd(acc.getUsd() + sum);
            } else {
                acc.setEur(acc.getEur() + sum);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void getMyMany (Scanner sc, Account ac) {
        TypedQuery<Rate> query = em.createQuery("select r from Rate r where r.test = 'last'", Rate.class);
        Rate rate = query.getSingleResult();
        double result = 0;
        result = ac.getUah();
        result += ac.getUsd() * rate.getUsd();
        result += ac.getEur() * rate.getEur();
        System.out.println("You have - " + result + " UAH");
    }

    private static void convertMoney (Scanner sc, Account ac) {
        System.out.print("Current from 1: UAH, 2: USD, 3: EUR --> ");
        String from = sc.nextLine();
        System.out.print("Enter sum --> ");
        String sum = sc.nextLine();
        System.out.print("Current to 1: UAH, 2: USD, 3: EUR --> ");
        String to = sc.nextLine();
        TypedQuery<Rate> query = em.createQuery("select r from Rate r where r.test = 'last'", Rate.class);
        Rate rate = query.getSingleResult();
        if (!(from.equals("1")||from.equals("2")||from.equals("3")||to.equals("1")||to.equals("2")||to.equals("3"))) {
            System.out.println("Invalid request");
            return;
        }
        if (from.equals(to)) {
            return;
        }
        em.getTransaction().begin();
        try{
           fSend(ac,ac,from,to,sum,rate);
           em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }

    }

    private static void startTransaction (Scanner sc, User u1) {
        System.out.print("Enter user name --> ");
        String name = sc.nextLine();
        TypedQuery<User> query = em.createQuery("select c from User c where c.name = :name", User.class);
        query.setParameter("name", name);
        User u2;
        try {
            u2 = query.getSingleResult();
        } catch (NoResultException ex) {
            System.out.println("Client not found, please try again");
            return;
        }
        System.out.print("Current from 1: UAH, 2: USD, 3: EUR --> ");
        String from = sc.nextLine();
        System.out.print("Enter sum --> ");
        String sum = sc.nextLine();
        System.out.print("Current to 1: UAH, 2: USD, 3: EUR --> ");
        String to = sc.nextLine();

        TypedQuery<Rate> queryR = em.createQuery("select r from Rate r where r.test = 'last'", Rate.class);
        Rate rate = queryR.getSingleResult();
        Transaction t;
        em.getTransaction().begin();
        try {
            String[] res = fSend(u1.getAccount(), u2.getAccount(), from, to, sum, rate);
            t = new Transaction(u1.getName(), Double.parseDouble(sum), res[0],u2.getName(),
                    Double.parseDouble(res[2]), res[1]);
            em.persist(t);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static String[] fSend (Account out, Account in, String o, String i, String sum, Rate r) {
        String[] val = new String[3];
        double dSum = Double.parseDouble(sum);
        if (o.equals("1")) {
            if (myCh(out.getUah(), dSum)) {
                out.setUah(out.getUah() - dSum);
                val[0] = "UAH";
                String[] b = uahTo(in, i, dSum, r);
                val[1] = b[0];
                val[2] = b[1];
            }
        } else if (o.equals("2")) {
            if (myCh(out.getUsd(), dSum)) {
                out.setUsd(out.getUsd() - dSum);
                val[0] = "USD";
                String[] b = usdTo(in, i, dSum, r);
                val[1] = b[0];
                val[2] = b[1];
            }
        } else {
            if (myCh(out.getEur(), dSum)) {
                out.setEur(out.getEur() - dSum);
                val[0] = "EUR";
                String[] b = eurTo(in, i, dSum, r);
                val[1] = b[0];
                val[2] = b[1];
            }
        }
        return val;
    }

    private static String[] uahTo (Account in, String i, double dSum, Rate r) {
        String[] ret = new String[2];
        if (i.equals("1")) {
            in.setUah(in.getUah() + dSum);
            ret[0] = "UAH";
            ret[1] = String.valueOf(dSum);
        } else if (i.equals("2")) {
            double f = dSum / r.getUsd();
            in.setUsd(in.getUsd() + f);
            ret[0] = "USD";
            ret[1] = String.valueOf(f);
        } else {
            double f = dSum / r.getEur();
            in.setEur(in.getEur() + f);
            ret[0] = "EUR";
            ret[1] = String.valueOf(f);
        }
        return ret;
    }

    private static String[] usdTo (Account in, String i, double dSum, Rate r) {
        String[] ret = new String[2];
        if (i.equals("1")) {
            double f = dSum * r.getUsd();
            in.setUah(in.getUah() + f);
            ret[0] = "UAH";
            ret[1] = String.valueOf(f);

        } else if (i.equals("2")) {
            in.setUsd(in.getUsd() + dSum);
            ret[0] = "USD";
            ret[1] = String.valueOf(dSum);
        } else {
            double f = dSum * r.getUsd() / r.getEur();
            in.setEur(in.getEur() + f);
            ret[0] = "EUR";
            ret[1] = String.valueOf(f);
        }
        return ret;
    }

    private static String[] eurTo (Account in, String i, double dSum, Rate r) {
        String[] ret = new String[2];
        if (i.equals("1")) {
            double f = dSum * r.getEur();
            in.setUah(in.getUah() + f);
            ret[0] = "UAH";
            ret[1] = String.valueOf(f);
        } else if (i.equals("2")) {
            double f = dSum * r.getEur() / r.getUsd();
            in.setUsd(in.getUsd() + f);
            ret[0] = "USD";
            ret[1] = String.valueOf(f);
        } else {
            in.setEur(in.getEur() + dSum);
            ret[0] = "EUR";
            ret[1] = String.valueOf(dSum);
        }
        return ret;
    }

    private static boolean myCh (double a, double b) {
        if (a < b) {
            System.out.println("You do not have enough money");
        }
        return a >= b;
    }
}
