package no.bring.priceengine.precentagebased;

import no.bring.priceengine.dao.Percentagebaseddump;
import no.bring.priceengine.database.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PercentageDatabaseService {


    public Boolean insertContractData(List<Percentagebaseddump> dumps){

        try {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            System.out.println("Inside upsertContractData " );
            System.out.println("Begin transaction" );

            if(!dumps.isEmpty()){
                for(Percentagebaseddump percentagebaseddump :  dumps) {
                    percentagebaseddump.setRemark(7);
                    if(percentagebaseddump.getFromLocation().equalsIgnoreCase("NULL") || percentagebaseddump.getFromLocation().isEmpty()) {
                    	percentagebaseddump.setFromLocation(null);
                    }
                    if(percentagebaseddump.getToLocation().equalsIgnoreCase("NULL") || percentagebaseddump.getToLocation().isEmpty()) {
                    	percentagebaseddump.setToLocation(null);
                    }
                    if(percentagebaseddump.getRouteType().equalsIgnoreCase("NULL") || percentagebaseddump.getRouteType().isEmpty()) {
                    	percentagebaseddump.setRouteType(null);
                    } 
                    if(percentagebaseddump.getZoneType().equalsIgnoreCase("NULL") || percentagebaseddump.getZoneType().isEmpty()) {
                    	percentagebaseddump.setZoneType(null);
                    } 
                    entityManager.persist(percentagebaseddump);
                }
            }
            entityManager.getTransaction().commit();
            System.out.println("Data inserted/updated successfully....");
            entityManager.close();
            return true;
        }
        catch (NullPointerException ex){
            System.out.println("######Got a null piointer");
            ex.printStackTrace();
            ex.getMessage();
            System.exit(1);
        }
        catch (PersistenceException ex){
            System.out.println("######Persistance exception occures");
            System.out.println("Message :::::::::::::::: "+ex.getMessage());
            ex.printStackTrace();
            System.exit(1);

        }
        catch (Exception ex){
            System.out.println("######Error while upserting data in Contractdump table.");
            ex.printStackTrace();
            System.exit(1);
        }
        return false;
    }



}

