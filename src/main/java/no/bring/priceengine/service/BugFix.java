package no.bring.priceengine.service;

import no.bring.priceengine.dao.Contractdump;
import no.bring.priceengine.database.JPAUtil;
import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BugFix {


    public static void main(String[] str){
        BugFix bugFix = new BugFix();

         ArrayList<Contractdump> contractdumps = (ArrayList) bugFix.findUpdatedContractdumps();
System.out.println("asdasdasd"  + contractdumps.size());


    }

    public List<Contractdump> findUpdatedContractdumps() throws DataAccessException {
        System.out.println("Inside findAllContractdumps() ");
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Contractdump> query = builder.createQuery(Contractdump.class);
            Root<Contractdump> root = query.from(Contractdump.class);
            query.select(root);
            Predicate namePredicate = builder.equal(root.get("updated"), true);
            Predicate enablePredicate = builder.equal(root.get("enabled"), true);
            Predicate predicates = builder.and(namePredicate, enablePredicate);
            query.where(predicates).orderBy(builder.asc(root.get("organizationNumber"))).
                    orderBy(builder.asc(root.get("customerNumber"))).orderBy(builder.asc(root.get("ProdNo")));
            TypedQuery<Contractdump> typedQuery = em.createQuery(query);
            if(typedQuery.getResultStream().findAny().isPresent())
                return typedQuery.getResultList();
            else
                return Collections.emptyList();
        }catch (HibernateException he){
            he.printStackTrace();
            return  null;
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(1);
            return  null;
        }finally {
            em.clear();
            em.close();
        }
    }
}
