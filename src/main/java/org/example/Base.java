package org.example;

import javax.persistence.EntityManager;

public class Base <T> {

    public void aTransaction (T in, EntityManager em) {
        em.getTransaction().begin();
        try{
            em.persist(in);
            em.getTransaction().commit();
         } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

}
