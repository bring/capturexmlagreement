package no.bring.priceengine.repository;

import no.bring.priceengine.dao.*;
import no.bring.priceengine.database.DatabaseService;
import no.bring.priceengine.database.JPAUtil;
import no.bring.priceengine.util.PriceEngineConstants;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class BeanRepository {

    //private String INSERT_CUSTOMPRODUCTATTRIBUTE_QUERY = "INSERT INTO core.customproductattribute(item_id, contractcomponent_id, custom_attributes, start_dt, customproductattribute_st_tp_cd, applicabilitycriteria_id, source_reference,appl_journey_tp_cd)  VALUES ( ?, ?, ?, ?, ?, ?, ?, ?)";
    private String INSERT_CUSTOMPRODUCTATTRIBUTE_QUERY = "INSERT INTO core.customproductattribute(item_id, contractcomponent_id, start_dt, customproductattribute_st_tp_cd, applicabilitycriteria_id, source_reference,appl_journey_tp_cd)  VALUES ( ?, ?, ?, ?, ?, ?, ?)";

    public static CustomProductAttribute buildCustomProductAttribute(Customervolfactordump customervolfactordump, ContractComponent contractComponent, Service service)
    throws Exception {
        CustomProductAttribute customProductAttribute = new CustomProductAttribute();
        ApplicabilityCriteriaEntity applicabilityCriteria =  findApplicationCriteria("TO_COUNTRY_"+customervolfactordump.getDestinaiton()+"_"+customervolfactordump.getService());

        customProductAttribute.setContractComponent(contractComponent);
        customProductAttribute.setItemId(service.getItemId());
        customProductAttribute.setCustomAttributesContainer(getContainer(customervolfactordump));
        customProductAttribute.setStartDt(contractComponent.getStartDt());
//        if(null!=applicabilityCriteria && null!=applicabilityCriteria.getApplicabilityCriteriaId()){
//            customProductAttribute.setApplicabilityCriteriaId(applicabilityCriteria.getApplicabilityCriteriaId());
//            customProductAttribute.setApplicabilityCriteriaEntity(applicabilityCriteria);
//        }
        customProductAttribute.setCustomProductattributeStTpCd(7);
     //   customProductAttribute.setCustomAttributes(getCustomAttributes());

        return customProductAttribute;
    }

    private static CustomAttributesContainer getContainer(Customervolfactordump customervolfactordump){
        CustomAttributesContainer customAttributesContainer = new CustomAttributesContainer();
        Map<String, CustomAttribute> maps = new HashMap<String, CustomAttribute>();
        CustomAttribute customAttribute = new CustomAttribute();
            customAttribute.setName("Name");
            customAttribute.setValue(customervolfactordump.getVolumetricFactor().toString());
        maps.put("volWtConversionFactor", customAttribute);

        customAttributesContainer.setCustomAttributes(maps);

        return  customAttributesContainer;

    }


    public void insertCustomerProdAttribute(CustomProductAttribute customProductAttribute){

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");
            String date = "2022-01-01 00:00:00";
            Query q= entityManager.createNativeQuery(INSERT_CUSTOMPRODUCTATTRIBUTE_QUERY);

            q.setParameter(1, customProductAttribute.getItemId().intValue());
            q.setParameter(2, customProductAttribute.getContractComponent().getContractComponentId().intValue());
            //stmt.setNString(3, getCustomAttributes().toString());

            q.setParameter(3, new java.sql.Date(df.parse(date).getTime()));
            q.setParameter(4,1);
            if(null!=customProductAttribute.applicabilityCriteriaEntity)
            	q.setParameter(5,customProductAttribute.applicabilityCriteriaEntity.getApplicabilityCriteriaId());
            else
            	q.setParameter(5,Types.INTEGER);
            q.setParameter(6, Types.VARCHAR);
            if(null!=customProductAttribute.getApplJourneyTpCd())
            	q.setParameter(7, customProductAttribute.getApplJourneyTpCd());
            else
            	q.setParameter(7,Types.INTEGER);
            int i = q.executeUpdate();
            Integer id = (Integer) q.getSingleResult();
            Long customProductAttributeId = id.longValue();
               System.out.println("CustomerProductAttribute has been created ID- "+ customProductAttributeId);


            System.out.println("New CustomProductAttribute has been created");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while creating new CustomProductAttribute");
            System.exit(1);
        } finally {
            entityManager.clear();
            entityManager.close();
        }
    }



    public void insertCustomerProdAttributeOld(CustomProductAttribute customProductAttribute){

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {

            entityManager.getTransaction().begin();
            entityManager.merge(customProductAttribute);
            entityManager.getTransaction().commit();
            System.out.println("New CustomProductAttribute has been created");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while creating new CustomProductAttribute");
            System.exit(1);
        } finally {
            entityManager.clear();
            entityManager.close();
        }
    }

    private static String getJsonValue() throws Exception{
        String value = null;
        JSONObject outterObject = new JSONObject();
        JSONObject object = new JSONObject();
        JSONObject innerObject = new JSONObject();
        innerObject.put("Name","volWtConversionFactor");
        innerObject.put("Value","1");

        object.put("volWtConversionFactor", innerObject);
        outterObject.put("CustomAttribute", object);


        return outterObject.toString();
    }

    private static CustomAttributes getCustomAttributes() throws Exception{
        CustomAttributes customAttributes = new CustomAttributes();


        String value = null;
        JSONObject outterObject = new JSONObject();
        JSONObject object = new JSONObject();
        JSONObject innerObject = new JSONObject();
        innerObject.put("Name","volWtConversionFactor");
        innerObject.put("Value","1");
        customAttributes.setVolWtConversionFactor(innerObject);
//        object.put("volWtConversionFactor", innerObject);
//        outterObject.put("CustomAttribute", object);


        return customAttributes;
    }


    @org.springframework.data.jpa.repository.Query
    public static ApplicabilityCriteriaEntity findApplicationCriteria(String criteria) throws DataAccessException {
        System.out.println("Inside findApplicationCriteria() ");
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            if(criteria.contains("GB_343"))
                System.out.println("wait");

            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<ApplicabilityCriteriaEntity> query = builder.createQuery(ApplicabilityCriteriaEntity.class);
            Root<ApplicabilityCriteriaEntity> root = query.from(ApplicabilityCriteriaEntity.class);
            query.select(root);
            Predicate predicate = builder.equal(root.get("name"), criteria);
            query.where(predicate);
            TypedQuery<ApplicabilityCriteriaEntity> typedQuery = em.createQuery(query);
            if(typedQuery.getResultStream().findAny().isPresent())
                return typedQuery.getSingleResult();
            else
                return  null;
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


    @org.springframework.data.jpa.repository.Query
    public static Boolean validateCustomProdAvailable(ContractComponent contractComponent, Long itemId) throws DataAccessException {
        System.out.println("Inside findApplicationCriteria() ");
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<CustomProductAttribute> query = builder.createQuery(CustomProductAttribute.class);
            Root<CustomProductAttribute> root = query.from(CustomProductAttribute.class);
            query.select(root);
            Predicate namePredicate = builder.equal(root.get("contractComponent"), contractComponent);
            Predicate countryPredicate = builder.equal(root.get("itemId"), itemId);
            Predicate predicates = builder.and(namePredicate, countryPredicate);
            query.where(predicates);
            TypedQuery<CustomProductAttribute> typedQuery = em.createQuery(query);
            if(typedQuery.getResultStream().findAny().isPresent())
                return true;
            else
                return  false;
        }catch (HibernateException he){
            he.printStackTrace();
            return  false;
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(1);
            return  false;
        }finally {
            em.clear();
            em.close();
        }
    }

    public static void main(String[] str){

     //   ArrayList<ApplicabilityCriteriaEntity> s =  BeanRepository.findApplicationCriteria("%342%");
        System.out.println("asdasdasdsad");

    }




}

