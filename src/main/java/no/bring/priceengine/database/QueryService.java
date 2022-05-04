package no.bring.priceengine.database;



import no.bring.priceengine.dao.*;
import no.bring.priceengine.util.PriceEngineConstants;

import org.apache.commons.lang3.time.DateFormatUtils;

import org.hibernate.HibernateException;

import org.springframework.dao.DataAccessException;



import javax.persistence.EntityManager;

import javax.persistence.NonUniqueResultException;

import javax.persistence.Query;

import javax.persistence.TypedQuery;

import javax.persistence.criteria.*;

import java.math.BigDecimal;

import java.sql.*;

import java.text.DateFormat;

import java.text.ParseException;

import java.text.SimpleDateFormat;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;

import java.util.*;

import java.util.Date;

import java.util.logging.Logger;



public class QueryService {
	

    private static final String SELECT_DELTACONTRACTDUMP = "SELECT organization_number, organization_name, customer_number, customer_name, div, artikelgrupp, statgrupp, prodno, proddescription, routefrom, routeto, startdate, todate, createddate, baseprice, currency, prum, dsclimcd, disclmtfrom, price, adsc, kgtill, updated, filecountry, enabled, routetype, zone_type, remark, createdate, price_id " +

            "FROM core.deltacontractdump ";
    private static final String SELECT_PERCENTAGEBASEDCONTRACTDUMP = "SELECT branch, parent_customer_number, parent_customer_name, customer_number, customer_name, prodno, startdate, enddate, routetype, from_location, to_location, precentage_discount, updated, enabled, filecountry, zone_type, remark, price_id FROM core.percentagebaseddeltadump";

    static Connection con;



    public static void main(String[] str) throws ParseException {



        QueryService queryService = new QueryService();

        queryService.findDistinctContractComponentFRMContractprice();





    }



    private static ArrayList<Integer> service336List() {





        ArrayList<Integer> service336List = new ArrayList<Integer>();



        service336List.add(336);

        service336List.add(33664);

        service336List.add(6133664);

        service336List.add(6233664);

//        service336List.add(3336);

//        service336List.add(12336);

//        service336List.add(13336);

//        service336List.add(25336);

//        service336List.add(26336);

//        service336List.add(29336);

//        service336List.add(30336);

//        service336List.add(33336);

//        service336List.add(39336);

//        service336List.add(41336);

//        service336List.add(42336);

//        service336List.add(43336);

//        service336List.add(47336);

//        service336List.add(54336);

//        service336List.add(60336);

//        service336List.add(62336);

//        service336List.add(67336);

//        service336List.add(68336);

//        service336List.add(73336);

//        service336List.add(75336);

//        service336List.add(76336);

//        service336List.add(77336);

//        service336List.add(79336);

//        service336List.add(96336);

//        service336List.add(99336);



        return service336List;

    }



    private ArrayList<String> getOrganizationList() {

        ArrayList<String> organizationList = new ArrayList<String>();

        organizationList.add("106614779");

        organizationList.add("20000182160");

        organizationList.add("20000209815");

        organizationList.add("20000226512");

        organizationList.add("20000231116");

        organizationList.add("20000251122");

        organizationList.add("10021608");

        organizationList.add("20000182129");

        organizationList.add("20003405840");

        organizationList.add("20000184562");

        organizationList.add("20000207074");

        organizationList.add("20000182434");

        organizationList.add("10004310164");

        organizationList.add("20000185890");

        organizationList.add("20000198935");

        organizationList.add("20002883955");

        organizationList.add("20000205615");

        organizationList.add("20000211134");

        organizationList.add("10033710749");

        organizationList.add("20000196202");

        organizationList.add("20000205227");

        organizationList.add("20000598498");

        organizationList.add("20000191872");

        organizationList.add("20000205136");

        organizationList.add("20000204832");

        organizationList.add("20000188092");

        organizationList.add("20000189801");

        organizationList.add("20000203883");

        organizationList.add("20000200871");

        organizationList.add("20003903992");

        organizationList.add("20000189561");

        organizationList.add("20000206498");

        organizationList.add("104352240");

        organizationList.add("20001065026");

        organizationList.add("20000199586");

        organizationList.add("20000190619");

        organizationList.add("20000206217");

        organizationList.add("20001156825");

        organizationList.add("20000200335");

        organizationList.add("20000187771");

        organizationList.add("20000192342");

        organizationList.add("20000209393");

        organizationList.add("20000191773");

        organizationList.add("20000205839");

        organizationList.add("10027471621");

        organizationList.add("20003132089");

        organizationList.add("20000196459");

        organizationList.add("20000191419");

        organizationList.add("20000906212");

        organizationList.add("20000189553");

        organizationList.add("20004238745");

        organizationList.add("20000204618");

        organizationList.add("20000206324");

        organizationList.add("20000209377");

        organizationList.add("20000193662");

        organizationList.add("20003318365");

        organizationList.add("20000200582");

        organizationList.add("20000196152");

        organizationList.add("20000524171");

        organizationList.add("20000190056");

        organizationList.add("20000196517");

        organizationList.add("20000206316");

        organizationList.add("20000422962");

        organizationList.add("20003791637");

        organizationList.add("20000193969");



        return organizationList;

    }



    public Item getItem(Integer serviceId) {

        Service service = null;

        Item item = null;

        ServiceAddOn serviceAddOn = null;

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            service = getService(serviceId.longValue());

            if (null == service) {

                serviceAddOn = findServiceAddOn(serviceId);

            }

            if (null == serviceAddOn)

                serviceAddOn = findServiceAddOnISourceRecord(serviceId);



            Query query = entityManager.createQuery(" from Item where itemId = :itemId");

            if (null != service && service.getItemId() != null)

                query.setParameter("itemId", service.getItemId());

            else if (null != serviceAddOn)

                query.setParameter("itemId", serviceAddOn.getItemId());

            else

                return null;

            if (query.getResultStream().findFirst().isPresent())

                item = (Item) query.getSingleResult();

            entityManager.close();

        } catch (HibernateException he) {

            he.printStackTrace();

        } finally {

            entityManager.close();

        }

        return item;

    }



    public Service getService(Long prodNo) {

        System.out.println("Inside getServiceTemp() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Service> query = builder.createQuery(Service.class);

            Root<Service> root = query.from(Service.class);

            query.select(root);

            Predicate namePredicate = builder.equal(root.get("sourceSystemRecordPk"), prodNo);

            query.where(namePredicate);

            TypedQuery<Service> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public ServiceAddOn getServiceAddon(Contractdump contractdump) {



        ServiceAddOn service = new ServiceAddOn();

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        System.out.println("Inside fetchDataToContractRole() ");

        Query query = entityManager.createQuery(" from ServiceAddOn where  iSourceSystemRecordPk =:paramServiceId").setMaxResults(1);

        query.setParameter("paramServiceId", contractdump.getProdNo().longValue());

        if (query.getFirstResult() != 0)

            service = (ServiceAddOn) query.getSingleResult();

        entityManager.clear();

        entityManager.close();



        return service;

    }



    public ServiceAddOn findServiceAddOnISourceRecord(Integer serviceId) {

        System.out.println("Inside findServiceAddOn() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        List<ServiceAddOn> list = new ArrayList<ServiceAddOn>();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ServiceAddOn> query = builder.createQuery(ServiceAddOn.class);

            Root<ServiceAddOn> root = query.from(ServiceAddOn.class);

            query.select(root);

            Predicate predicate = builder.equal(root.get("iSourceSystemRecordPk"), serviceId.toString());

            query.where(predicate);

            TypedQuery<ServiceAddOn> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public ServiceAddOn findProdNoFromServiceAddOnISource(Integer serviceId) {

        System.out.println("Inside findServiceAddOn() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        List<ServiceAddOn> list = new ArrayList<ServiceAddOn>();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ServiceAddOn> query = builder.createQuery(ServiceAddOn.class);

            Root<ServiceAddOn> root = query.from(ServiceAddOn.class);

            query.select(root);

            Predicate predicate = builder.equal(root.get("itemId"), serviceId);

            query.where(predicate);

            TypedQuery<ServiceAddOn> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public ServiceAddOn findServiceAddOn(Integer serviceId) {

        System.out.println("Inside findServiceAddOn() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        List<ServiceAddOn> list = new ArrayList<ServiceAddOn>();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ServiceAddOn> query = builder.createQuery(ServiceAddOn.class);

            Root<ServiceAddOn> root = query.from(ServiceAddOn.class);

            query.select(root);

            Expression<String> conactValue = builder.concat(root.get("aSourceSystemRecordPk").as(String.class), root.get("sSourceSystemRecordPk").as(String.class));

            Predicate predicate = builder.equal(conactValue, serviceId.toString());

            query.where(predicate);

            TypedQuery<ServiceAddOn> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public ServiceAddOn findProdNoByServiceAddOn(Integer serviceId) {

        System.out.println("Inside findProdNoByServiceAddOn() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ServiceAddOn> query = builder.createQuery(ServiceAddOn.class);

            Root<ServiceAddOn> root = query.from(ServiceAddOn.class);

            query.select(root);

            Predicate predicate = builder.equal(root.get("itemId"), serviceId);

            query.where(predicate);

            TypedQuery<ServiceAddOn> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public ArrayList<Item> findAllItems() {

        System.out.println("Inside findAllItems() ");

        ArrayList<Item> services = new ArrayList<Item>();

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        List<Item> list = new ArrayList<Item>();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Item> query = builder.createQuery(Item.class);

            Root<Item> root = query.from(Item.class);

            query.select(root);

            TypedQuery<Item> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return (ArrayList<Item>) typedQuery.getResultList();

            else

                return new ArrayList<Item>();

        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<Item>();

        } finally {

            em.clear();

            em.close();

        }

    }



    public ArrayList<ServiceAddOn> findAllServiceAddon() {

        System.out.println("Inside findAllServiceAddon() ");

        ArrayList<ServiceAddOn> services = new ArrayList<ServiceAddOn>();

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        List<Item> list = new ArrayList<Item>();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ServiceAddOn> query = builder.createQuery(ServiceAddOn.class);

            Root<ServiceAddOn> root = query.from(ServiceAddOn.class);

            query.select(root);

            TypedQuery<ServiceAddOn> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return (ArrayList<ServiceAddOn>) typedQuery.getResultList();

            else

                return new ArrayList<ServiceAddOn>();

        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<ServiceAddOn>();

        } finally {

            em.clear();

            em.close();

        }

    }



    public Item findItem(Integer itemId) {

        System.out.println("Inside findAllServices() ");

        ArrayList<Item> services = new ArrayList<Item>();

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        List<Item> list = new ArrayList<Item>();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Item> query = builder.createQuery(Item.class);

            Root<Item> root = query.from(Item.class);

            query.select(root);

            Predicate predicate = builder.equal(root.get("itemId"), itemId);

            query.where(predicate);

            TypedQuery<Item> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public ArrayList<Service> findAllServices() {

        System.out.println("Inside findAllServices() ");

        ArrayList<Service> services = new ArrayList<Service>();

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        List<Service> list = new ArrayList<Service>();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Service> query = builder.createQuery(Service.class);

            Root<Service> root = query.from(Service.class);

            query.select(root);

            TypedQuery<Service> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return (ArrayList<Service>) typedQuery.getResultList();

            else

                return new ArrayList<Service>();

        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<Service>();

        } finally {

            em.clear();

            em.close();

        }

    }



    // Used in priority

    public ArrayList<Integer> findDistinctContractComponentFRMContractprice() {

        System.out.println("Inside findDistinctContractComponentFRMContractprice() ");

        ArrayList<Integer> services = new ArrayList<Integer>();

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        List<Integer> list = new ArrayList<Integer>();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Integer> query = builder.createQuery(Integer.class);

            Root<ContractPrice> root = query.from(ContractPrice.class);

            query.select(root.get("contractcomponentId")).distinct(true);

            TypedQuery<Integer> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent()) {

                System.out.println("total count  -- " + typedQuery.getResultList().size());

                return (ArrayList<Integer>) typedQuery.getResultList();

            } else

                return new ArrayList<Integer>();

        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<Integer>();

        } finally {

            em.clear();

            em.close();

        }

    }



    // Used in priority

    public ArrayList<Long> findDistinctItemsFRMContractprice(Integer contractComponentId) {

        System.out.println("Inside findDistinctContractComponentFRMContractprice() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        List<Long> list = new ArrayList<Long>();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Long> query = builder.createQuery(Long.class);

            Root<ContractPrice> root = query.from(ContractPrice.class);

            Predicate predicate = builder.equal(root.get("contractcomponentId"), new Double(contractComponentId));

            query.select(root.get("itemIdDup")).distinct(true).where(predicate);

            TypedQuery<Long> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent()) {

                System.out.println("total count  -- " + typedQuery.getResultList().size());

                return (ArrayList<Long>) typedQuery.getResultList();

            } else

                return new ArrayList<Long>();

        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<Long>();

        } finally {

            em.clear();

            em.close();

        }

    }


    public List<Percentagebaseddeltadump> findAllPercentbasedDeltaContractdumpsWithJDBC(String fileCountry, Logger logger) {
        return findAllPercentbasedDeltaContractdumpsWithJDBC(fileCountry, null, logger);
    }

    private boolean isEmpty(String s) {
        return s == null || "".equals(s.trim());
    }

    public List<Percentagebaseddeltadump> findAllPercentbasedDeltaContractdumpsWithJDBC(String fileCountry, String customerNumber, Logger logger) {
        List<Percentagebaseddeltadump> dumps = new ArrayList<>();
        String whereClause;
        if (isEmpty(customerNumber)) {
            whereClause = " WHERE updated=false and enabled = true and (remark is null or remark='null') and filecountry='" + fileCountry + "'";
        } else {
            whereClause = " WHERE updated=false and enabled = true and  (remark is null or remark='null') and customer_number='" + customerNumber + "' and filecountry='" + fileCountry + "'";
        }
        String sql_final = SELECT_PERCENTAGEBASEDCONTRACTDUMP + whereClause;
        try {
            if (con == null || con.isClosed()) {
                con = DriverManager.getConnection(PriceEngineConstants.DB_CONNECTION_URL, PriceEngineConstants.DB_CONNECTION_USERNAME, PriceEngineConstants.DB_CONNECTION_PASSWORD);
            }
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql_final);
            while (resultSet.next()) {
                Percentagebaseddeltadump dump = new Percentagebaseddeltadump();
                dump.setBranch(resultSet.getInt("branch"));
                dump.setParentCustomerNumber(resultSet.getString("parent_customer_number"));
                dump.setParentCustomerName(resultSet.getString("parent_customer_name"));
                dump.setCustomerNumber(resultSet.getString("customer_number"));
                dump.setCustomerName(resultSet.getString("customer_name"));
                dump.setProdno(resultSet.getInt("prodno"));
                dump.setStartdate(resultSet.getDate("startdate"));
                dump.setEnddate(resultSet.getDate("enddate"));
                dump.setRouteType(resultSet.getString("routetype"));
                dump.setFromLocation(resultSet.getString("from_location"));
                dump.setToLocation(resultSet.getString("to_location"));
                dump.setPrecentageDiscount((resultSet.getString("precentage_discount")));
                dump.setUpdated(resultSet.getBoolean("updated"));
                dump.setEnabled(resultSet.getBoolean("enabled"));
                dump.setFileCountry(resultSet.getString("filecountry"));
                dump.setZoneType(resultSet.getString("zone_type"));
                dump.setRemark(resultSet.getString("remark"));
                dump.setPriceId(Long.valueOf(resultSet.getInt("price_id")));
                dumps.add(dump);
            }
            int insertCount = dumps.size();
            return dumps;
        } catch (Exception ex) {
            logger.warning("Error[" + ex.getMessage() + "] while fetching records ");
            return Collections.emptyList();
        }
    }

    // Used in priority

    public ArrayList<ContractPrice> findDistinctRoutesFRMContractpriceOLD(Integer contractComponentId, Long itemId) {

        System.out.println("Inside findDistinctRoutesFRMContractprice() ");

        ArrayList<ContractPrice> services = new ArrayList<ContractPrice>();

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        List<ContractPrice> list = new ArrayList<ContractPrice>();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ContractPrice> query = builder.createQuery(ContractPrice.class);

            Root<ContractPrice> root = query.from(ContractPrice.class);

            Predicate predicateCCID = builder.equal(root.get("contractcomponentId"), new Double(contractComponentId));

            Predicate predicateItemid = builder.equal(root.get("itemIdDup"), new Double(itemId));

            Predicate predicateFromCountry = builder.isNotNull(root.get("fromCountry"));

            Predicate predicateToCountry = builder.isNotNull(root.get("toCountry"));

            Predicate predicatePriority = builder.isNull(root.get("priority"));

            // remove after check

            //    Predicate predicateFromPostalCode = builder.isNull(root.get("toCountryZoneCountFrom"));

            builder.and(predicateFromCountry, predicateToCountry, predicatePriority);

            query.select(root).where(predicateCCID, predicateItemid);

            TypedQuery<ContractPrice> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent()) {

                System.out.println("total count  -- " + typedQuery.getResultList().size());

                return (ArrayList<ContractPrice>) typedQuery.getResultList();

            } else

                return new ArrayList<ContractPrice>();

        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<ContractPrice>();

        } finally {

            em.clear();

            em.close();

        }

    }



    public ArrayList<ContractPrice> findDistinctRoutesFRMContractprice(Integer contractComponentId, Long itemId) {

        System.out.println("Inside findDistinctRoutesFRMContractprice() ");



        ArrayList<ContractPrice> contractPrices = new ArrayList<>();

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        System.out.println("Inside ContractPrice() ");

        List<Query> query = entityManager.createNativeQuery("select * from core.contractprice where contractcomponent_id = " + contractComponentId + " and item_id_dup =" + itemId + " and from_country_tp_cd is not null and to_country_tp_cd is not null and priority is null ").getResultList();

//        List<Query> query = entityManager.createNativeQuery("select * from core.contractprice where item_id_dup ="+itemId +" and from_country_tp_cd is not null and to_country_tp_cd is not null and contractcomponent_id=1960").getResultList();

        Iterator listIterator = query.listIterator();

        while (listIterator.hasNext()) {

            Object[] object = (Object[]) listIterator.next();

            ContractPrice contractPrice = new ContractPrice();

            contractPrice.setPriceId(new Long(object[0].toString()));

            contractPrice.setPriceId(new Long(object[0].toString()));

            contractPrice.setContractcomponentId(Integer.parseInt(object[5].toString()));

            contractPrice.setItemIdDup(new Long(object[9].toString()));

            if (null != object[11])

                contractPrice.setFromCountry(Integer.parseInt(object[11].toString()));

            if (null != object[12])

                contractPrice.setFromPostalCode(object[12].toString());

            if (null != object[13])

                contractPrice.setToCountry(Integer.parseInt(object[13].toString()));

            if (null != object[14])

                contractPrice.setFromPostalCode(object[14].toString());

            if (null != object[15])

                contractPrice.setToCountryZoneCountFrom(Integer.parseInt(object[15].toString()));

            if (null != object[16])

                contractPrice.setToCountryZoneCountTo(Integer.parseInt(object[16].toString()));

            contractPrice.setApplJourneyTpCd(Integer.parseInt(object[18].toString()));

            if (null != object[19])

                contractPrice.setZoneId(Integer.parseInt(object[19].toString()));

            if (null != object[21])

                contractPrice.setFromRouteId(Integer.parseInt(object[21].toString()));

            if (null != object[22])

                contractPrice.setToRouteId(Integer.parseInt(object[22].toString()));

            contractPrices.add(contractPrice);

        }

        entityManager.close();

        return contractPrices;

    }



    public ArrayList<Integer> findPercentBasedPriceIDs() {

        System.out.println("Inside getContractPriceByComponentID() ");

        ArrayList<Integer> contractPrices = new ArrayList<Integer>();

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        System.out.println("Inside ContractPrice() ");

        List<Query> query = entityManager.createNativeQuery("select price_id, item_id from core.price where percent_based_price is not null ").getResultList();

        Iterator listIterator = query.listIterator();

        while (listIterator.hasNext()) {

            Object[] object = (Object[]) listIterator.next();

            ContractPrice contractPrice = new ContractPrice();

            Integer priceId = Integer.parseInt(object[0].toString());

            Integer itemId = Integer.parseInt(object[1].toString());

            contractPrices.add(priceId);

        }

        entityManager.close();

        return contractPrices;

    }



    public ArrayList<ContractPrice> findDistinctRoutesWithNullFRMContractpriceOLD(Integer contractComponentId, Long itemIdDup) {

        System.out.println("Inside findDistinctRoutesFRMContractprice() ");

        ArrayList<ContractPrice> services = new ArrayList<ContractPrice>();

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        List<ContractPrice> list = new ArrayList<ContractPrice>();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ContractPrice> query = builder.createQuery(ContractPrice.class);

            Root<ContractPrice> root = query.from(ContractPrice.class);

            Predicate predicateCCID = builder.equal(root.get("contractcomponentId"), new Double(contractComponentId));

            Predicate predicateItemidDup = builder.equal(root.get("itemIdDup"), itemIdDup);

            query.select(root).where(predicateCCID, predicateItemidDup);

            TypedQuery<ContractPrice> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent()) {

                System.out.println("totsl count  -- " + typedQuery.getResultList().size());

                return (ArrayList<ContractPrice>) typedQuery.getResultList();

            } else

                return new ArrayList<ContractPrice>();

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

        } finally {

            em.clear();

            em.close();

        }

        return null;

    }



    public ArrayList<ContractPrice> findDistinctRoutesWithNullFRMContractprice(Integer contractComponentId, Long itemIdDup) {

        System.out.println("Inside findDistinctRoutesFRMContractprice() ");
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        ArrayList<ContractPrice> contractPrices = new ArrayList<ContractPrice>();
        System.out.println("Inside ContractPrice() ");
        StringBuilder builder = new StringBuilder();
        //List<Query> query = entityManager.createQuery(" from Price price where price.basePrice is null AND price.percentBasedPrice is null").getResultList();
        List<Query> query = entityManager.createNativeQuery("select * from core.contractprice where appl_journey_tp_cd <> 3 and contractcomponent_id= " + contractComponentId + " and item_id_dup=" + itemIdDup).getResultList();
        Iterator listIterator = query.listIterator();
        while (listIterator.hasNext()) {
            Object[] object = (Object[]) listIterator.next();
            ContractPrice contractPrice = new ContractPrice();
            contractPrice.setPriceId(new Long(object[0].toString()));
            contractPrice.setPriceId(new Long(object[0].toString()));

            contractPrice.setContractcomponentId(Integer.parseInt(object[5].toString()));

            contractPrice.setItemIdDup(new Long(object[9].toString()));

            if (null != object[11])

                contractPrice.setFromCountry(Integer.parseInt(object[11].toString()));

            if (null != object[12])

                contractPrice.setFromPostalCode(object[12].toString());

            if (null != object[13])

                contractPrice.setToCountry(Integer.parseInt(object[13].toString()));

            if (null != object[14])

                contractPrice.setFromPostalCode(object[14].toString());

            if (null != object[15])

                contractPrice.setToCountryZoneCountFrom(Integer.parseInt(object[15].toString()));

            if (null != object[16])

                contractPrice.setToCountryZoneCountTo(Integer.parseInt(object[16].toString()));

            contractPrice.setApplJourneyTpCd(Integer.parseInt(object[18].toString()));

            if (null != object[19])

                contractPrice.setZoneId(Integer.parseInt(object[19].toString()));

            if (null != object[21])

                contractPrice.setFromRouteId(Integer.parseInt(object[21].toString()));

            if (null != object[22])

                contractPrice.setToRouteId(Integer.parseInt(object[22].toString()));

            contractPrices.add(contractPrice);

        }

        System.out.println(" Duplicate entries :  " + builder);

        entityManager.close();

        return contractPrices;

    }



    public ArrayList<Integer> findDistinctContractComponentFRMCPJourneyPassive() {

        System.out.println("Inside findDistinctContractComponentFRMCPJourneyPassive() ");

        ArrayList<Integer> services = new ArrayList<Integer>();

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        List<Integer> list = new ArrayList<Integer>();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Integer> query = builder.createQuery(Integer.class);

            Root<ContractPrice> root = query.from(ContractPrice.class);

            query.select(root.get("contractcomponentId")).distinct(true);

            Predicate predicate = builder.equal(root.get("applJourneyTpCd"), 2);

            query.where(predicate);

            TypedQuery<Integer> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent()) {

                System.out.println("total count  -- " + typedQuery.getResultList().size());

                return (ArrayList<Integer>) typedQuery.getResultList();

            } else

                return new ArrayList<Integer>();

        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<Integer>();

        } finally {

            em.clear();

            em.close();

        }

    }



    public Service findServiceByID(Integer serviceId) {

        System.out.println("Inside findServiceByID() ");



        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        List<Service> list = new ArrayList<Service>();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Service> query = builder.createQuery(Service.class);

            Root<Service> root = query.from(Service.class);



            Predicate predicate = builder.equal(root.get("sourceSystemRecordPk"), new Double(serviceId));



            query.where(predicate);



            query.select(root);

            TypedQuery<Service> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public Contract findContract(String sspkId) {

        System.out.println("Inside findServiceByID() ");
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Contract> query = builder.createQuery(Contract.class);
            Root<Contract> root = query.from(Contract.class);
            Predicate systemIdPredicate = builder.equal(root.get("sourceSystemId"), new Double(3));
//            Predicate userPredicate = builder.equal(root.get("createdByUser"), "Automation");
            Predicate sspkPredicate = builder.equal(root.get("sourceSystemRecordPk"), sspkId);
            Predicate predicates = builder.and(systemIdPredicate,  sspkPredicate);



            query.where(predicates);



            query.select(root);

            TypedQuery<Contract> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public ContractComponent findContractComponent(Contract contract) {

        System.out.println("Inside findContractComponent() ");
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<ContractComponent> query = builder.createQuery(ContractComponent.class);
            Root<ContractComponent> root = query.from(ContractComponent.class);
            Predicate systemIdPredicate = builder.equal(root.get("sourceSystemId"), new Double(3));
//            Predicate userPredicate = builder.equal(root.get("createdByUser"), "Automation");
            Predicate sspkPredicate = builder.equal(root.get("sourceSystemRecordPk"), contract.getSourceSystemRecordPk());

            Predicate predicates = builder.and(systemIdPredicate, sspkPredicate);



            query.where(predicates);



            query.select(root);

            TypedQuery<ContractComponent> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {
            em.clear();
            em.close();
        }

    }



    public List<ContractComponent> findAllContractComponents() {

        System.out.println("Inside findAllContractComponents() ");



        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ContractComponent> query = builder.createQuery(ContractComponent.class);

            Root<ContractComponent> root = query.from(ContractComponent.class);



            Predicate systemIdPredicate = builder.equal(root.get("sourceSystemId"), new Double(3));

            Predicate userPredicate = builder.equal(root.get("createdByUser"), "Automation");

            Predicate predicates = builder.and(systemIdPredicate, userPredicate);



            query.where(predicates);



            query.select(root);

            TypedQuery<ContractComponent> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public List<ContractRole> findContractRoles(ContractComponent contractComponent) {

        System.out.println("Inside findContractComponent() ");
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<ContractRole> query = builder.createQuery(ContractRole.class);
            Root<ContractRole> root = query.from(ContractRole.class);
            // dISABLING IT BECAUSE UNABLE TO RETRIEVEA A SCENARIO WHERE PARENT AND CHILD PARTY IS TOTALLY DIFFERENT .. NEED TO VALIDATE FOR ALL SCENARIO
            //   Predicate systemIdPredicate = builder.equal(root.get("contractRoleTpCd"), new Double(1));
//            Predicate userPredicate = builder.equal(root.get("createdByUser"), "Automation");
            //Predicate sspkPredicate =  builder.equal(root.get("partySourceSystemRecordPk"), contractComponent.getSourceSystemRecordPk());
            Predicate sspkPredicate = builder.equal(root.get("contractComponent"), contractComponent);
            Predicate predicates = builder.and(sspkPredicate);
            query.where(predicates);
            query.select(root);

            TypedQuery<ContractRole> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return Collections.emptyList();

        } catch (Exception e) {

            e.printStackTrace();

            return Collections.emptyList();

        } finally {

            em.clear();

            em.close();

        }

    }



    public ContractRole findContractRolesSurcharge(ContractComponent contractComponent) {

        System.out.println("Inside findContractComponent() ");



        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ContractRole> query = builder.createQuery(ContractRole.class);

            Root<ContractRole> root = query.from(ContractRole.class);



            Predicate systemIdPredicate = builder.equal(root.get("contractRoleTpCd"), new Double(1));

//            Predicate userPredicate = builder.equal(root.get("createdByUser"), "Automation");//

            //Predicate sspkPredicate =  builder.equal(root.get("partySourceSystemRecordPk"), contractComponent.getSourceSystemRecordPk());

            Predicate sspkPredicate = builder.equal(root.get("contractComponent"), contractComponent);

            Predicate predicates = builder.and(sspkPredicate);



            query.where(predicates, systemIdPredicate);



            query.select(root);

            TypedQuery<ContractRole> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList().get(0);

            else

                return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public Contract findContractById(Long transactionId) {



        Contract contract = new Contract();

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            System.out.println("Inside fetchDataToContractRole() ");

            Query query = entityManager.createQuery(" from Contract where  lastUpdateTxId =:paramTransactionId").setMaxResults(1);

            query.setParameter("paramTransactionId", transactionId);

            if (query.getResultList().size() > 0)

                contract = (Contract) query.getSingleResult();

            if (entityManager.isOpen())

                entityManager.close();



            return contract;

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            entityManager.close();



        }

        return contract;

    }



    public ContractComponent findContractComponent(Long componentId) {



        ContractComponent contractComponent = new ContractComponent();

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        System.out.println("Inside fetchDataToContractRole() ");

        Query query = entityManager.createQuery(" from ContractComponent where  contractComponentId =:componentId and createdByUser = :paramUser").setMaxResults(1);

        query.setParameter("componentId", componentId);

        query.setParameter("paramUser", DatabaseService.AUTOMATION_USER);

        if (query.getResultList().size() > 0)

            contractComponent = (ContractComponent) query.getSingleResult();

        if (entityManager.isOpen())

            entityManager.close();



        return contractComponent;

    }



    public ContractRole findContractRole(Long lastUpdateTxId) {



        ContractRole contractRole = new ContractRole();

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        System.out.println("Inside fetchDataToContractRole() ");

        Query query = entityManager.createQuery(" from ContractRole where lastUpdateTxId =:paramLastUpdateTxId and createdByUser = :paramCreatedByUser").setMaxResults(1);

        query.setParameter("paramLastUpdateTxId", lastUpdateTxId);

        query.setParameter("paramCreatedByUser", DatabaseService.AUTOMATION_USER);

        if (query.getResultList().size() > 0)

            contractRole = (ContractRole) query.getSingleResult();

        if (entityManager.isOpen())

            entityManager.close();



        return contractRole;

    }



    public ArrayList<ContractRole> findContractRolesById(ContractComponent componentId) {



        ArrayList<ContractRole> roles = new ArrayList<ContractRole>();

        ContractRole contractRole = new ContractRole();

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        System.out.println("Inside fetchDataToContractRole() ");

        Query query = entityManager.createQuery(" from ContractRole where contractComponent = :paramComponentId");

        query.setParameter("paramComponentId", componentId);

        if (query.getResultList().size() > 0)

            roles = (ArrayList<ContractRole>) query.getResultList();

        if (entityManager.isOpen())

            entityManager.close();



        return roles;

    }



    public ArrayList<ContractComponent> findContractComponents() {



        ArrayList<ContractComponent> contractComponents = new ArrayList<ContractComponent>();

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        System.out.println("Inside fetchDataToContractRole() ");

        List<Query> query = entityManager.createQuery(" from ContractComponent").getResultList();

        ListIterator listIterator = query.listIterator();

        while (listIterator.hasNext()) {

            contractComponents.add((ContractComponent) listIterator.next());

        }

        entityManager.close();

        return contractComponents;

    }



    public List<Integer> fetchDistinctServices(String organizationId) {

        System.out.println("Inside fetchDataToContractRole() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Integer> query = builder.createQuery(Integer.class);

            Root<Contractdump> root = query.from(Contractdump.class);

            query.select(root.get("ProdNo")).distinct(true);



//            Predicate isUpdatePredicate = builder.equal(root.get("updated"), false);

//            Predicate servicePredicate = builder.equal(root.get("ProdNo"), serviceId);

//            Predicate predicate  = builder.and(isUpdatePredicate,servicePredicate);



            Predicate predicateUpdate = builder.equal(root.get("updated"), false);

            Predicate predicateOrg = builder.equal(root.get("organizationNumber"), new Double(organizationId));

            Predicate predicate = builder.and(predicateUpdate, predicateOrg);

            query.where(predicate);

            TypedQuery<Integer> typedQuery = em.createQuery(query);

            return typedQuery.getResultList();

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            //     em.clear();

            em.close();

        }

    }



    public List<Integer> fetchDistinctServicesPercentageBased() {

        System.out.println("Inside fetchDistinctServicesPercentageBased() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Integer> query = builder.createQuery(Integer.class);

            Root<Percentagebaseddump> root = query.from(Percentagebaseddump.class);

            query.select(root.get("prodno")).distinct(true);

            Predicate predicate = builder.equal(root.get("updated"), false);

            query.where(predicate);

            TypedQuery<Integer> typedQuery = em.createQuery(query);

            return typedQuery.getResultList();

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            //     em.clear();

            em.close();

        }

    }



    public List<Contractdump> findContractByService(Integer serviceId, String organization) {

        String organizationNmumber = organization.split("~")[0];

        System.out.println("Inside findContractByService() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Contractdump> query = builder.createQuery(Contractdump.class);

            Root<Contractdump> root = query.from(Contractdump.class);

            query.select(root);

            Predicate isUpdatePredicate = builder.equal(root.get("updated"), false);

            Predicate servicePredicate = builder.equal(root.get("ProdNo"), serviceId);

            Predicate orgnizationPredicate = builder.equal(root.get("organizationNumber"), new Double(organizationNmumber));

            Predicate predicate = builder.and(isUpdatePredicate, servicePredicate, orgnizationPredicate);

            query.where(predicate);

            query.orderBy(builder.asc(root.get("RouteFrom")), builder.asc(root.get("RouteTo")));

            TypedQuery<Contractdump> typedQuery = em.createQuery(query);



            return typedQuery.getResultList();

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            //      em.clear();

            em.close();

        }

    }



    public List<Percentagebaseddump> findContractByServicePercentageBased(Integer serviceId) {

        System.out.println("Inside findContractByService() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Percentagebaseddump> query = builder.createQuery(Percentagebaseddump.class);

            Root<Percentagebaseddump> root = query.from(Percentagebaseddump.class);

            query.select(root);

            Predicate isUpdatePredicate = builder.equal(root.get("updated"), false);

            Predicate servicePredicate = builder.equal(root.get("product"), serviceId);

            Predicate predicate = builder.and(isUpdatePredicate, servicePredicate);

            query.where(predicate);

            query.orderBy(builder.asc(root.get("source")), builder.asc(root.get("destination")));

            TypedQuery<Percentagebaseddump> typedQuery = em.createQuery(query);



            return typedQuery.getResultList();

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            //      em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public Party findChildPartyBySSPK(String customerNumber) throws DataAccessException {

        System.out.println("Inside findPartyBySSPK() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Party> query = builder.createQuery(Party.class);

            Root<Party> root = query.from(Party.class);

            query.select(root);

            Predicate namePredicate = builder.equal(root.get("sourceSystemRecordPk"), customerNumber);

            Predicate sourceSystemPredicate = builder.equal(root.get("sourceSystemId"), 3);

            Predicate parentNotNull = builder.isNotNull(root.get("parentSourceSystemRecordPk"));

            Predicate predicates = builder.and(namePredicate, sourceSystemPredicate, parentNotNull);

            query.where(predicates);

            TypedQuery<Party> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            System.exit(1);

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    // // THIS METHOD HAS CHANGED.. CONDITION OF PARENT = NULL IS REMOVE .. BE CAREFUL AND VALIDATE

    @org.springframework.data.jpa.repository.Query

    public Party findPartyBySSPK(String customerNumber) throws DataAccessException {

        System.out.println("Inside findPartyBySSPK() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Party> query = builder.createQuery(Party.class);

            Root<Party> root = query.from(Party.class);

            query.select(root);

            Predicate namePredicate = builder.equal(root.get("sourceSystemRecordPk"), customerNumber);

            Predicate sourceSystemPredicate = builder.equal(root.get("sourceSystemId"), 3);

            //  Predicate parentNull = builder.isNull(root.get("parentSourceSystemRecordPk"));

            Predicate predicates = builder.and(namePredicate, sourceSystemPredicate);

            query.where(predicates);

            TypedQuery<Party> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            System.exit(1);

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public Party findPartyByParentSSPK(String customerNumber) throws DataAccessException {

        System.out.println("Inside findPartyByParentSSPK() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Party> query = builder.createQuery(Party.class);

            Root<Party> root = query.from(Party.class);

            query.select(root);

            Predicate namePredicate = builder.equal(root.get("sourceSystemRecordPk"), customerNumber);

            Predicate sourceSystemPredicate = builder.equal(root.get("sourceSystemId"), 3);

            Predicate parentNull = builder.isNull(root.get("parentSourceSystemRecordPk"));

            query.where(namePredicate, sourceSystemPredicate, parentNull);

            TypedQuery<Party> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            System.exit(1);

            return null;

        } catch (NonUniqueResultException non) {

            non.printStackTrace();

            System.out.println("Cusotmer Number not unique = " + customerNumber);

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public List<Party> findAllParentParty(ArrayList<String> organizations) throws DataAccessException {

        System.out.println("Inside findAllParty() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            Query query = em.createQuery(" from Party where parentSourceSystemRecordPk in (:orgList)");

            query.setParameter("orgList", organizations);

            List<Party> list = query.getResultList();

            return list;



        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    } // Customervolfactordump



    @org.springframework.data.jpa.repository.Query

    public Party findParentParty(String organizationId) throws DataAccessException {

        System.out.println("Inside findContractBySSPK() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Party> query = builder.createQuery(Party.class);

            Root<Party> root = query.from(Party.class);

            query.select(root);

            Predicate sspkPredicate = builder.equal(root.get("sourceSystemRecordPk"), organizationId);

            Predicate parentSSPKPredicate = builder.isNull(root.get("parentSourceSystemRecordPk"));

            Predicate sourceSystemSSPKPredicate = builder.equal(root.get("sourceSystemId"), 3);

            Predicate predicates = builder.and(sspkPredicate, parentSSPKPredicate, sourceSystemSSPKPredicate);

            query.where(predicates);

            TypedQuery<Party> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            System.out.println("Organization id ::: " + organizationId);

            System.exit(1);

            return null;



        } catch (Exception e) {

            e.printStackTrace();

            System.out.println("Organization id ::: " + organizationId);

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public Party findChildParty(String customerId, String parentId) throws DataAccessException {

        System.out.println("Inside findContractBySSPK() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Party> query = builder.createQuery(Party.class);

            Root<Party> root = query.from(Party.class);

            query.select(root);

            Predicate sspkPredicate = builder.equal(root.get("sourceSystemRecordPk"), customerId);

            Predicate parentSSPKPredicate = builder.equal(root.get("parentSourceSystemRecordPk"), parentId);

            Predicate predicates = builder.and(sspkPredicate, parentSSPKPredicate);

            query.where(predicates);

            TypedQuery<Party> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public Party findChildPartyWithoutParent(String customerId) throws DataAccessException {

        System.out.println("Inside findContractBySSPK() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Party> query = builder.createQuery(Party.class);

            Root<Party> root = query.from(Party.class);

            query.select(root);

            Predicate sspkPredicate = builder.equal(root.get("sourceSystemRecordPk"), customerId);

            Predicate parentSSPKPredicate = builder.isNotNull(root.get("parentSourceSystemRecordPk"));

            Predicate predicates = builder.and(sspkPredicate, parentSSPKPredicate);

            query.where(predicates);

            TypedQuery<Party> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public ArrayList<Customervolfactordump> findVolMetricDump() throws DataAccessException {

        System.out.println("Inside findVolMetricDump() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Customervolfactordump> query = builder.createQuery(Customervolfactordump.class);

            Root<Customervolfactordump> root = query.from(Customervolfactordump.class);

            query.select(root);

            Predicate namePredicate = builder.equal(root.get("updated"), false);

            query.where(namePredicate);

            TypedQuery<Customervolfactordump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return (ArrayList<Customervolfactordump>) typedQuery.getResultList();

            else

                return new ArrayList<Customervolfactordump>();

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public ArrayList<CustomProductAttribute> findCustomProductAttributes() throws DataAccessException {

        System.out.println("Inside CustomProductAttribute() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<CustomProductAttribute> query = builder.createQuery(CustomProductAttribute.class);

            Root<CustomProductAttribute> root = query.from(CustomProductAttribute.class);

            query.select(root);

            TypedQuery<CustomProductAttribute> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return (ArrayList<CustomProductAttribute>) typedQuery.getResultList();

            else

                return new ArrayList<CustomProductAttribute>();

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public List<CustomProductAttribute> findCustomProductAttributes2() throws DataAccessException {

        System.out.println("Inside CustomProductAttribute() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<CustomProductAttribute> query = builder.createQuery(CustomProductAttribute.class);

            Root<CustomProductAttribute> root = query.from(CustomProductAttribute.class);

            query.select(root);

            TypedQuery<CustomProductAttribute> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return new ArrayList<CustomProductAttribute>();

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public ArrayList<Customervolfactordump> findVolMetricDumpByName(ArrayList<String> names) throws DataAccessException {

        System.out.println("Inside findVolMetricDumpByName() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Customervolfactordump> query = builder.createQuery(Customervolfactordump.class);

            Root<Customervolfactordump> root = query.from(Customervolfactordump.class);

            query.select(root);

            query.where(names.toArray(new Predicate[]{}));



            TypedQuery<Customervolfactordump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return (ArrayList<Customervolfactordump>) typedQuery.getResultList();

            else

                return new ArrayList<Customervolfactordump>();

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public List<Country> findCountries(List<String> countryCodes) {

        System.out.println("Inside findCountries() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Country> query = builder.createQuery(Country.class);

            Root<Country> root = query.from(Country.class);

            query.select(root).where(root.get("countryCode").in(countryCodes));

            TypedQuery<Country> typedQuery = em.createQuery(query);

            return typedQuery.getResultList();

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.close();

        }

    }



    public Country findCountry(String countryCode) {

        System.out.println("Inside findCounty() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Country> query = builder.createQuery(Country.class);

            Root<Country> root = query.from(Country.class);

            query.select(root).where(root.get("countryCode").in(countryCode));

            TypedQuery<Country> typedQuery = em.createQuery(query);

            return typedQuery.getSingleResult();

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.close();

        }

    }



    public List<Price> findPrices() {

        System.out.println("Inside findPrices() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        List<Integer> ids = new ArrayList<Integer>();

        ids.add(384184);

//        ids.add(73138);

//        ids.add(73139);



        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Price> query = builder.createQuery(Price.class);

            Root<Price> root = query.from(Price.class);

            query.select(root).where(root.get("priceId").in(ids));

            TypedQuery<Price> typedQuery = em.createQuery(query);

            return typedQuery.getResultList();

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.close();

        }

    }



    public ContractComponent findComponent(Long txId) {

        System.out.println("Inside findComponent() ");

        ContractComponent contractComponent = new ContractComponent();



        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ContractComponent> query = builder.createQuery(ContractComponent.class);

            Root<ContractComponent> root = query.from(ContractComponent.class);

            query.select(root);

            Predicate namePredicate = builder.equal(root.get("lastUpdateTxId"), txId);

            query.where(namePredicate);

            TypedQuery<ContractComponent> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent()) {

                contractComponent = typedQuery.getSingleResult();

                return contractComponent;

            } else

                return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            //      em.clear();

            em.close();

        }

    }



    public Price findContractPrice(Long priceId) {

        System.out.println("Inside findContractPrice() ");
        Price contractPrice = new ContractPrice();
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<ContractPrice> query = builder.createQuery(ContractPrice.class);
            Root<ContractPrice> root = query.from(ContractPrice.class);
            query.select(root);
            Predicate namePredicate = builder.equal(root.get("priceId"), priceId);
            query.where(namePredicate);
            TypedQuery<ContractPrice> typedQuery = em.createQuery(query);
            if (typedQuery.getResultList().size() == 1) {
                contractPrice = typedQuery.getSingleResult();
                return contractPrice;
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }



    public ContractPrice findContractPriceObject(Long priceId) {

        ContractPrice contractPrice = null;

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        Query query = entityManager.createNativeQuery(" select * from core.contractprice WHERE price_id = " + priceId).setMaxResults(1);

        Object[] object = (Object[]) query.getSingleResult();

        contractPrice = new ContractPrice();

        contractPrice.setPriceId(new Long(object[0].toString()));

        contractPrice.setContractcomponentId(Integer.parseInt(object[5].toString()));

        contractPrice.setItemIdDup(new Long(object[9].toString()));

        if (object[11] != null)

            contractPrice.setFromCountry(Integer.parseInt(object[11].toString()));

        if (object[12] != null)

            contractPrice.setFromPostalCode(object[12].toString());

        if (object[13] != null)

            contractPrice.setToCountry(Integer.parseInt(object[13].toString()));

        if (object[14] != null)

            contractPrice.setToPostcalCode(object[14].toString());

        if (object[15] != null)

            contractPrice.setToCountryZoneCountFrom(Integer.parseInt(object[15].toString()));

        if (object[16] != null)

            contractPrice.setToCountryZoneCountTo(Integer.parseInt(object[16].toString()));

        if (object[18] != null)

            contractPrice.setApplJourneyTpCd(Integer.parseInt(object[18].toString()));

        if (object[19] != null)

            contractPrice.setZoneId(Integer.parseInt(object[19].toString()));

        if (object[20] != null)

            contractPrice.setContractpriceProcessingTpCd(Integer.parseInt(object[20].toString()));

        if (object[21] != null)

            contractPrice.setFromRouteId(Integer.parseInt(object[21].toString()));

        if (object[22] != null)

            contractPrice.setToRouteId(Integer.parseInt(object[22].toString()));



        return contractPrice;

    }



    public ArrayList<ContractPrice> findContractPriceByComponentID(Long contractcomponentID, Long itemID) {
        ArrayList<ContractPrice> contractPriceList = new ArrayList<ContractPrice>();
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        //List<Query> query = entityManager.createNativeQuery("select cp.* from core.contractprice cp inner join core.price price on cp.price_id = price.price_id WHERE cp.contractcomponent_Id = "+contractcomponentID +" and price.item_id= "+ itemID ).getResultList();
        List<Query> query = entityManager.createNativeQuery("select cp.* from core.contractprice cp inner join core.price p on p.price_id=cp.price_id WHERE cp.contractcomponent_id = " + contractcomponentID + " and cp.item_id_dup = " + itemID + " and p.percent_based_price is null").getResultList();
        Iterator listIterator = query.listIterator();
        while (listIterator.hasNext()) {
            Object[] object = (Object[]) listIterator.next();
            ContractPrice contractPrice = new ContractPrice();
            contractPrice.setPriceId(new Long(object[0].toString()));
            contractPrice.setContractcomponentId(Integer.parseInt(object[5].toString()));
            contractPrice.setItemIdDup(new Long(object[9].toString()));
            if (object[11] != null)
                contractPrice.setFromCountry(Integer.parseInt(object[11].toString()));
            if (object[12] != null)
                contractPrice.setFromPostalCode(object[12].toString());
            if (object[13] != null)
                contractPrice.setToCountry(Integer.parseInt(object[13].toString()));
            if (object[14] != null)
                contractPrice.setToPostcalCode(object[14].toString());
            if (object[15] != null)
                contractPrice.setToCountryZoneCountFrom(Integer.parseInt(object[15].toString()));
            if (object[16] != null)
                contractPrice.setToCountryZoneCountTo(Integer.parseInt(object[16].toString()));
            if (object[18] != null)
                contractPrice.setApplJourneyTpCd(Integer.parseInt(object[18].toString()));
            if (object[19] != null)
                contractPrice.setZoneId(Integer.parseInt(object[19].toString()));
            if (object[21] != null)
                contractPrice.setFromRouteId(Integer.parseInt(object[21].toString()));
            if (object[22] != null)
                contractPrice.setToRouteId(Integer.parseInt(object[22].toString()));


            contractPriceList.add(contractPrice);
        }

        return contractPriceList;

    }


    public ArrayList<ContractPrice> findContractPriceByComponentIDFIXEDPrice(Long contractcomponentID, Long itemID) {
        ArrayList<ContractPrice> contractPriceList = new ArrayList<ContractPrice>();
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        //List<Query> query = entityManager.createNativeQuery("select cp.* from core.contractprice cp inner join core.price price on cp.price_id = price.price_id WHERE cp.contractcomponent_Id = "+contractcomponentID +" and price.item_id= "+ itemID ).getResultList();
        List<Query> query = entityManager.createNativeQuery("select cp.* from core.contractprice cp inner join core.price p on p.price_id=cp.price_id WHERE cp.contractcomponent_id = " + contractcomponentID + " and cp.item_id_dup = " + itemID + " and p.price_def_tp_cd=1").getResultList();
        Iterator listIterator = query.listIterator();
        while (listIterator.hasNext()) {
            Object[] object = (Object[]) listIterator.next();
            ContractPrice contractPrice = new ContractPrice();
            contractPrice.setPriceId(new Long(object[0].toString()));
            contractPrice.setContractcomponentId(Integer.parseInt(object[5].toString()));
            contractPrice.setItemIdDup(new Long(object[9].toString()));
            if (object[11] != null)
                contractPrice.setFromCountry(Integer.parseInt(object[11].toString()));
            if (object[12] != null)
                contractPrice.setFromPostalCode(object[12].toString());
            if (object[13] != null)
                contractPrice.setToCountry(Integer.parseInt(object[13].toString()));
            if (object[14] != null)
                contractPrice.setToPostcalCode(object[14].toString());
            if (object[15] != null)
                contractPrice.setToCountryZoneCountFrom(Integer.parseInt(object[15].toString()));
            if (object[16] != null)
                contractPrice.setToCountryZoneCountTo(Integer.parseInt(object[16].toString()));
            if (object[18] != null)
                contractPrice.setApplJourneyTpCd(Integer.parseInt(object[18].toString()));
            if (object[19] != null)
                contractPrice.setZoneId(Integer.parseInt(object[19].toString()));
            if (object[21] != null)
                contractPrice.setFromRouteId(Integer.parseInt(object[21].toString()));
            if (object[22] != null)
                contractPrice.setToRouteId(Integer.parseInt(object[22].toString()));

            contractPriceList.add(contractPrice);
        }

        return contractPriceList;

    }

    public ArrayList<ContractPrice> findContractPriceByComponentIDFixedANDSlabbasedPrice(Long contractcomponentID, Long itemID) {
        ArrayList<ContractPrice> contractPriceList = new ArrayList<ContractPrice>();
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        //List<Query> query = entityManager.createNativeQuery("select cp.* from core.contractprice cp inner join core.price price on cp.price_id = price.price_id WHERE cp.contractcomponent_Id = "+contractcomponentID +" and price.item_id= "+ itemID ).getResultList();
        List<Query> query = entityManager.createNativeQuery("select cp.* from core.contractprice cp inner join core.price p on p.price_id=cp.price_id WHERE cp.contractcomponent_id = " + contractcomponentID + " and cp.item_id_dup = " + itemID + " and p.price_def_tp_cd in (1,6)").getResultList();
        Iterator listIterator = query.listIterator();
        while (listIterator.hasNext()) {
            Object[] object = (Object[]) listIterator.next();
            ContractPrice contractPrice = new ContractPrice();
            contractPrice.setPriceId(new Long(object[0].toString()));
            contractPrice.setContractcomponentId(Integer.parseInt(object[5].toString()));
            contractPrice.setItemIdDup(new Long(object[9].toString()));
            if (object[11] != null)
                contractPrice.setFromCountry(Integer.parseInt(object[11].toString()));
            if (object[12] != null)
                contractPrice.setFromPostalCode(object[12].toString());
            if (object[13] != null)
                contractPrice.setToCountry(Integer.parseInt(object[13].toString()));
            if (object[14] != null)
                contractPrice.setToPostcalCode(object[14].toString());
            if (object[15] != null)
                contractPrice.setToCountryZoneCountFrom(Integer.parseInt(object[15].toString()));
            if (object[16] != null)
                contractPrice.setToCountryZoneCountTo(Integer.parseInt(object[16].toString()));
            if (object[18] != null)
                contractPrice.setApplJourneyTpCd(Integer.parseInt(object[18].toString()));
            if (object[19] != null)
                contractPrice.setZoneId(Integer.parseInt(object[19].toString()));
            if (object[21] != null)
                contractPrice.setFromRouteId(Integer.parseInt(object[21].toString()));
            if (object[22] != null)
                contractPrice.setToRouteId(Integer.parseInt(object[22].toString()));

            contractPriceList.add(contractPrice);
        }

        return contractPriceList;

    }


    public ArrayList<ContractPrice> findContractPriceByComponentID(Long contractcomponentID) {
        ArrayList<ContractPrice> contractPriceList = new ArrayList<ContractPrice>();
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        //List<Query> query = entityManager.createNativeQuery("select cp.* from core.contractprice cp inner join core.price price on cp.price_id = price.price_id WHERE cp.contractcomponent_Id = "+contractcomponentID +" and price.item_id= "+ itemID ).getResultList();
        List<Query> query = entityManager.createNativeQuery("select cp.* from core.contractprice cp inner join core.price p on p.price_id=cp.price_id WHERE cp.contractcomponent_id = " + contractcomponentID  + " and p.price_def_tp_cd=1").getResultList();
        Iterator listIterator = query.listIterator();
        while (listIterator.hasNext()) {
            Object[] object = (Object[]) listIterator.next();
            ContractPrice contractPrice = new ContractPrice();
            contractPrice.setPriceId(new Long(object[0].toString()));
            contractPrice.setContractcomponentId(Integer.parseInt(object[5].toString()));
            contractPrice.setItemIdDup(new Long(object[9].toString()));
            if (object[11] != null)
                contractPrice.setFromCountry(Integer.parseInt(object[11].toString()));
            if (object[12] != null)
                contractPrice.setFromPostalCode(object[12].toString());
            if (object[13] != null)
                contractPrice.setToCountry(Integer.parseInt(object[13].toString()));
            if (object[14] != null)
                contractPrice.setToPostcalCode(object[14].toString());
            if (object[15] != null)
                contractPrice.setToCountryZoneCountFrom(Integer.parseInt(object[15].toString()));
            if (object[16] != null)
                contractPrice.setToCountryZoneCountTo(Integer.parseInt(object[16].toString()));
            if (object[18] != null)
                contractPrice.setApplJourneyTpCd(Integer.parseInt(object[18].toString()));
            if (object[19] != null)
                contractPrice.setZoneId(Integer.parseInt(object[19].toString()));
            if (object[21] != null)
                contractPrice.setFromRouteId(Integer.parseInt(object[21].toString()));
            if (object[22] != null)
                contractPrice.setToRouteId(Integer.parseInt(object[22].toString()));


            contractPriceList.add(contractPrice);
        }

        return contractPriceList;

    }



//    public SlabBasedPrice findSlabbasedPrice(Long priceId) {

//

//        SlabBasedPrice slabBasedPrice = new SlabBasedPrice();

//        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

//        System.out.println("Inside fetchDataToContractRole() ");

//        List<Query> query = entityManager.createNativeQuery("select * from core.slabbasedprice where price_id = " + priceId).getResultList();

//        Iterator listIterator = query.listIterator();

//        while (listIterator.hasNext()) {

//            Object[] object = (Object[]) listIterator.next();

//            //  Price price = findContractPrice(new Long(Integer.parseInt(object[1].toString())));

//            slabBasedPrice.setSlabBasedPriceId(new Long(Integer.parseInt(object[0].toString())));

//            //    slabBasedPrice.setPrice(price);

//            slabBasedPrice.setSlabBasisTpCd(new Long(Integer.parseInt(object[2].toString())));

//        }

//        entityManager.close();

//        return slabBasedPrice;

//    }





//    public Long findMaxSlabbasedPriceId(){

//        System.out.println("Inside findMaxPriceId() ");

//        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

//        try {

//            CriteriaBuilder builder = em.getCriteriaBuilder();

//            CriteriaQuery<Long> query = builder.createQuery(Long.class);

//            Root<SlabBasedPrice> root = query.from(SlabBasedPrice.class);

//            Expression<Long> attributeFieldToCheck = root.get("price");

//            query.select(builder.max(attributeFieldToCheck));

//            TypedQuery<Long> typedQuery = em.createQuery(query);

//            if(typedQuery.getResultStream().findAny().isPresent())

//                return typedQuery.getSingleResult();

//            else

//                return null;

//        }catch (Exception e){

//            e.printStackTrace();

//            return  null;

//        }finally {

//            //      em.clear();

//            em.close();

//        }

//    }



    public ArrayList<ContractPrice> findContractPriceByComponentIDPERCENT(Long contractcomponentID) {

        ArrayList<ContractPrice> contractPriceList = new ArrayList<ContractPrice>();

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        //List<Query> query = entityManager.createNativeQuery("select cp.* from core.contractprice cp inner join core.price price on cp.price_id = price.price_id WHERE cp.contractcomponent_Id = "+contractcomponentID +" and price.item_id= "+ itemID ).getResultList();

        List<Query> query = entityManager.createNativeQuery("select cp.* from core.contractprice cp inner join core.price p on p.price_id=cp.price_id WHERE cp.contractcomponent_id = " + contractcomponentID + " and p.percent_based_price is not null").getResultList();

        Iterator listIterator = query.listIterator();

        while (listIterator.hasNext()) {

            Object[] object = (Object[]) listIterator.next();

            ContractPrice contractPrice = new ContractPrice();

            contractPrice.setPriceId(new Long(object[0].toString()));

            contractPrice.setContractcomponentId(Integer.parseInt(object[5].toString()));

            contractPrice.setItemIdDup(new Long(object[9].toString()));

            if (object[11] != null)

                contractPrice.setFromCountry(Integer.parseInt(object[11].toString()));

            if (object[12] != null)

                contractPrice.setFromPostalCode(object[12].toString());

            if (object[13] != null)

                contractPrice.setToCountry(Integer.parseInt(object[13].toString()));

            if (object[14] != null)

                contractPrice.setToPostcalCode(object[14].toString());

            if (object[15] != null)

                contractPrice.setToCountryZoneCountFrom(Integer.parseInt(object[15].toString()));

            if (object[16] != null)

                contractPrice.setToCountryZoneCountTo(Integer.parseInt(object[16].toString()));

            if (object[18] != null)

                contractPrice.setApplJourneyTpCd(Integer.parseInt(object[18].toString()));

            contractPriceList.add(contractPrice);

        }

        return contractPriceList;

    }

    public ArrayList<ContractPrice> findContractPriceByComponentAndItemIDPERCENT(Long contractcomponentID, Long itemid) {

        ArrayList<ContractPrice> contractPriceList = new ArrayList<ContractPrice>();

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        //List<Query> query = entityManager.createNativeQuery("select cp.* from core.contractprice cp inner join core.price price on cp.price_id = price.price_id WHERE cp.contractcomponent_Id = "+contractcomponentID +" and price.item_id= "+ itemID ).getResultList();
         List<Query> query = entityManager.createNativeQuery("select cp.* from core.contractprice cp inner join core.price p on p.price_id=cp.price_id WHERE cp.contractcomponent_id = " + contractcomponentID + " and p.price_def_tp_cd=3 and cp.item_id_dup = "+ itemid).getResultList();

        Iterator listIterator = query.listIterator();

        while (listIterator.hasNext()) {

            Object[] object = (Object[]) listIterator.next();

            ContractPrice contractPrice = new ContractPrice();

            contractPrice.setPriceId(new Long(object[0].toString()));

            contractPrice.setContractcomponentId(Integer.parseInt(object[5].toString()));

            contractPrice.setItemIdDup(new Long(object[9].toString()));

            if (object[11] != null)

                contractPrice.setFromCountry(Integer.parseInt(object[11].toString()));

            if (object[12] != null)

                contractPrice.setFromPostalCode(object[12].toString());

            if (object[13] != null)

                contractPrice.setToCountry(Integer.parseInt(object[13].toString()));

            if (object[14] != null)

                contractPrice.setToPostcalCode(object[14].toString());

            if (object[15] != null)

                contractPrice.setToCountryZoneCountFrom(Integer.parseInt(object[15].toString()));

            if (object[16] != null)

                contractPrice.setToCountryZoneCountTo(Integer.parseInt(object[16].toString()));

            if (object[18] != null)

                contractPrice.setApplJourneyTpCd(Integer.parseInt(object[18].toString()));

            contractPriceList.add(contractPrice);

        }

        return contractPriceList;

    }

    public ArrayList<ContractPrice> findContractPriceByComponentAndItemIDSLABBASED(Long contractcomponentID, Long itemid) {

        ArrayList<ContractPrice> contractPriceList = new ArrayList<ContractPrice>();

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        //List<Query> query = entityManager.createNativeQuery("select cp.* from core.contractprice cp inner join core.price price on cp.price_id = price.price_id WHERE cp.contractcomponent_Id = "+contractcomponentID +" and price.item_id= "+ itemID ).getResultList();
        List<Query> query = entityManager.createNativeQuery("select cp.* from core.contractprice cp inner join core.price p on p.price_id=cp.price_id WHERE cp.contractcomponent_id = " + contractcomponentID + " and p.price_def_tp_cd=6 and cp.item_id_dup = "+ itemid).getResultList();
        Iterator listIterator = query.listIterator();
        while (listIterator.hasNext()) {
            Object[] object = (Object[]) listIterator.next();
            ContractPrice contractPrice = new ContractPrice();
            contractPrice.setPriceId(new Long(object[0].toString()));
            contractPrice.setContractcomponentId(Integer.parseInt(object[5].toString()));
            contractPrice.setItemIdDup(new Long(object[9].toString()));
            if (object[11] != null)
                contractPrice.setFromCountry(Integer.parseInt(object[11].toString()));
            if (object[12] != null)
                contractPrice.setFromPostalCode(object[12].toString());
            if (object[13] != null)
                contractPrice.setToCountry(Integer.parseInt(object[13].toString()));
            if (object[14] != null)
                contractPrice.setToPostcalCode(object[14].toString());
            if (object[15] != null)
                contractPrice.setToCountryZoneCountFrom(Integer.parseInt(object[15].toString()));
            if (object[16] != null)
                contractPrice.setToCountryZoneCountTo(Integer.parseInt(object[16].toString()));
            if (object[18] != null)
                contractPrice.setApplJourneyTpCd(Integer.parseInt(object[18].toString()));
            if (object[19] != null)
                contractPrice.setZoneId(Integer.parseInt(object[19].toString()));
            if (object[21] != null)
                contractPrice.setFromRouteId(Integer.parseInt(object[21].toString()));
            if (object[22] != null)
                contractPrice.setToRouteId(Integer.parseInt(object[22].toString()));

            contractPriceList.add(contractPrice);
        }

        return contractPriceList;

    }





    public SlabBasedPrice findSlabbasedPrice(Price priceId) {
        System.out.println("Inside findContractPrice() ");
        SlabBasedPrice slabBasedPrice = new SlabBasedPrice();
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<SlabBasedPrice> query = builder.createQuery(SlabBasedPrice.class);
            Root<SlabBasedPrice> root = query.from(SlabBasedPrice.class);
            query.select(root);
            Predicate namePredicate = builder.equal(root.get("price_id"), priceId);
            query.where(namePredicate);
            TypedQuery<SlabBasedPrice> typedQuery = em.createQuery(query);
            slabBasedPrice = typedQuery.getSingleResult();
            return slabBasedPrice;

//            }else

//                return null;

        } catch (Exception e) {

            e.printStackTrace();

            //    System.exit(1);

            return null;

        } finally {

            //      em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public List<Contractdump> findAllContractdumps(String fileCoutnry) throws DataAccessException {
        System.out.println("Inside findAllContractdumps() ");
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Contractdump> query = builder.createQuery(Contractdump.class);
            Root<Contractdump> root = query.from(Contractdump.class);
            query.select(root);
            Predicate namePredicate = builder.equal(root.get("updated"), false);
            Predicate countryPredicate = builder.equal(root.get("fileCountry"), fileCoutnry);
            Predicate enablePredicate = builder.equal(root.get("enabled"), true);
            //     Predicate zoneTypeNotNull =  builder.isNotNull(root.get("zoneType"));
            //        Predicate service349 = builder.equal(root.get("ProdNo"), 34964);
            //     Predicate service34964 = builder.equal(root.get("ProdNo"), 34964);
            Predicate predicates = builder.and(namePredicate, enablePredicate, countryPredicate);
            //         Predicate predicates = builder.and(countryPredicate);
            query.where(predicates);
            TypedQuery<Contractdump> typedQuery = em.createQuery(query);
            if (typedQuery.getResultStream().findAny().isPresent())
                return typedQuery.getResultList();
            else
                return Collections.emptyList();
        } catch (HibernateException he) {
            he.printStackTrace();
            System.exit(1);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        } finally {

            em.clear();

            em.close();

        }

    }



    private String getDateAsString(Date d) {

        return DateFormatUtils.format(d, "yyyy-MM-dd");

    }



    public List<Deltacontractdump> findAllDeltaContractdumpsWithJDBC(String fileCountry, Logger logger) {

        List<Deltacontractdump> deltacontractdumps = new ArrayList<>();

        String whereClause = " WHERE updated=false and enabled = true and (remark is null or remark='null') and filecountry='" + fileCountry + "'";

        String sql_final = SELECT_DELTACONTRACTDUMP + whereClause;

        try {

            if (con == null || con.isClosed()) {

                con = DriverManager.getConnection(PriceEngineConstants.DB_CONNECTION_URL, PriceEngineConstants.DB_CONNECTION_USERNAME, PriceEngineConstants.DB_CONNECTION_PASSWORD);

            }

            Statement stmt = con.createStatement();

            ResultSet resultSet = stmt.executeQuery(sql_final);

            while (resultSet.next()) {

                Deltacontractdump deltacontractdump = new Deltacontractdump();

                Integer dsclimcd = resultSet.getString("dsclimcd") != null && resultSet.getString("dsclimcd").equalsIgnoreCase("null") ? null : Integer.parseInt(resultSet.getString("dsclimcd"));

                Double disclmtFrom = resultSet.getDouble("disclmtfrom") == -1 ? null : resultSet.getDouble("disclmtfrom");

                deltacontractdump.setOrganizationNumber(resultSet.getString("organization_number"));

                deltacontractdump.setOrganizationName(resultSet.getString("organization_name"));

                deltacontractdump.setCustomerNumber(resultSet.getString("customer_number"));

                deltacontractdump.setCustomerName(resultSet.getString("customer_name"));

                deltacontractdump.setDiv(9999);

                deltacontractdump.setArtikelgrupp(9999);

                deltacontractdump.setStatGrupp("9999");

                deltacontractdump.setProdNo(resultSet.getInt("prodno"));

                deltacontractdump.setProdDescr(resultSet.getString("proddescription"));

                deltacontractdump.setRouteFrom(resultSet.getString("routefrom"));

                deltacontractdump.setRouteTo(resultSet.getString("routeto"));

                deltacontractdump.setFromDate(resultSet.getDate("startdate"));

                deltacontractdump.setToDate(resultSet.getDate("todate"));

                deltacontractdump.setCreateddate(resultSet.getDate("createddate"));

                deltacontractdump.setBasePrice(resultSet.getDouble("baseprice"));

                deltacontractdump.setCurr(resultSet.getString("currency"));

                deltacontractdump.setPrUM(resultSet.getString("prum"));

                deltacontractdump.setDscLimCd(dsclimcd);

                deltacontractdump.setDiscLmtFrom(disclmtFrom);

                deltacontractdump.setPrice(resultSet.getDouble("price"));

                deltacontractdump.setADsc(resultSet.getInt("adsc"));

                deltacontractdump.setKgTill(resultSet.getString("kgtill"));

                deltacontractdump.setUpdated(resultSet.getBoolean("updated"));

                deltacontractdump.setFileCountry(resultSet.getString("filecountry"));

                deltacontractdump.setEnabled(resultSet.getBoolean("enabled"));

                deltacontractdump.setRouteType(resultSet.getString("routetype"));

                deltacontractdump.setZoneType(resultSet.getString("zone_type"));

                deltacontractdump.setRemark(resultSet.getString("remark"));

                deltacontractdump.setCreateddate(resultSet.getDate("createdate"));

                deltacontractdump.setPriceId(resultSet.getInt("price_id"));

                deltacontractdumps.add(deltacontractdump);

            }

            int insertCount = deltacontractdumps.size();

            return deltacontractdumps;

        } catch (Exception ex) {

            logger.warning("Error[" + ex.getMessage() + "] while fetching records ");

            return Collections.emptyList();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public List<Deltacontractdump> findAllDeltaContractdumps(String fileCoutnry) throws DataAccessException {

        System.out.println("Inside findAllDeltaContractdumps() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Deltacontractdump> query = builder.createQuery(Deltacontractdump.class);

            Root<Deltacontractdump> root = query.from(Deltacontractdump.class);

            query.select(root);

            Predicate namePredicate = builder.equal(root.get("updated"), false);

            Predicate countryPredicate = builder.equal(root.get("fileCountry"), fileCoutnry);

            Predicate enablePredicate = builder.equal(root.get("enabled"), true);

            Predicate predicates = builder.and(namePredicate, enablePredicate, countryPredicate);



            query.where(predicates);

            TypedQuery<Deltacontractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return Collections.emptyList();

        } catch (HibernateException he) {

            he.printStackTrace();

            System.exit(1);

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public ArrayList<Deltacontractdump> findAllDeltaContractdumpsByOrganization(String fileCoutnry, String organizationNumber) throws DataAccessException {

        System.out.println("Inside findAllDeltaContractdumps() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Deltacontractdump> query = builder.createQuery(Deltacontractdump.class);

            Root<Deltacontractdump> root = query.from(Deltacontractdump.class);

            query.select(root);

            Predicate namePredicate = builder.equal(root.get("updated"), false);

            Predicate countryPredicate = builder.equal(root.get("fileCountry"), fileCoutnry);

            Predicate enablePredicate = builder.equal(root.get("enabled"), true);

            Predicate orgPredicate = builder.equal(root.get("organizationNumber"), organizationNumber);

            Predicate predicates = builder.and(namePredicate, enablePredicate, countryPredicate, orgPredicate);



            query.where(predicates);

            TypedQuery<Deltacontractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return new ArrayList(typedQuery.getResultList());

            else

                return new ArrayList<Deltacontractdump>();

        } catch (HibernateException he) {

            he.printStackTrace();

            System.exit(1);

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public ArrayList<Deltacontractdump> findAllDeltaContractdumpsByCustomer(String fileCoutnry, String customerNumber) throws DataAccessException {

        System.out.println("Inside Deltacontractdump() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Deltacontractdump> query = builder.createQuery(Deltacontractdump.class);
            Root<Deltacontractdump> root = query.from(Deltacontractdump.class);
            query.select(root);
            Predicate namePredicate = builder.equal(root.get("updated"), false);
            Predicate countryPredicate = builder.equal(root.get("fileCountry"), fileCoutnry);
            Predicate enablePredicate = builder.equal(root.get("enabled"), true);
            Predicate orgPredicate = builder.equal(root.get("customerNumber"), customerNumber);
            Predicate predicates = builder.and(namePredicate, enablePredicate, countryPredicate, orgPredicate);

            query.where(predicates);

            TypedQuery<Deltacontractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return new ArrayList(typedQuery.getResultList());

            else

                return new ArrayList<Deltacontractdump>();

        } catch (HibernateException he) {

            he.printStackTrace();

            System.exit(1);

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public ArrayList<Percentagebaseddeltadump> findAllDeltaContractdumpsByCustomerPERCENT(String fileCoutnry, String customerNumber) throws DataAccessException {

        System.out.println("Inside findAllDeltaContractdumpsByCustomerPERCENT() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Percentagebaseddeltadump> query = builder.createQuery(Percentagebaseddeltadump.class);

            Root<Percentagebaseddeltadump> root = query.from(Percentagebaseddeltadump.class);

            query.select(root);

            Predicate namePredicate = builder.equal(root.get("updated"), false);

            Predicate countryPredicate = builder.equal(root.get("fileCountry"), fileCoutnry);

            Predicate enablePredicate = builder.equal(root.get("enabled"), true);

            Predicate orgPredicate = builder.equal(root.get("customerNumber"), customerNumber);

            Predicate predicates = builder.and(namePredicate, enablePredicate, countryPredicate, orgPredicate);



            query.where(predicates);

            TypedQuery<Percentagebaseddeltadump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return new ArrayList(typedQuery.getResultList());

            else

                return new ArrayList<Percentagebaseddeltadump>();

        } catch (HibernateException he) {

            he.printStackTrace();

            System.exit(1);

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public List<Percentagebaseddeltadump> findAllPercentbasedDeltaContractdumps(String fileCoutnry) throws DataAccessException {

        System.out.println("Inside findAllPercentbasedDeltaContractdumps() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Percentagebaseddeltadump> query = builder.createQuery(Percentagebaseddeltadump.class);

            Root<Percentagebaseddeltadump> root = query.from(Percentagebaseddeltadump.class);

            query.select(root);

            Predicate namePredicate = builder.equal(root.get("updated"), false);

            Predicate countryPredicate = builder.equal(root.get("fileCountry"), fileCoutnry);

            Predicate enablePredicate = builder.equal(root.get("enabled"), true);

            Predicate predicates = builder.and(namePredicate, enablePredicate, countryPredicate);



            query.where(predicates);

            TypedQuery<Percentagebaseddeltadump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return Collections.emptyList();

        } catch (HibernateException he) {

            he.printStackTrace();

            System.exit(1);

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public List<Contractdump> findContractByCustomer(String customer) throws DataAccessException {

        System.out.println("Inside findAllContractdumps() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Contractdump> query = builder.createQuery(Contractdump.class);

            Root<Contractdump> root = query.from(Contractdump.class);

            query.select(root);

            List<Integer> prodList = Arrays.asList(349, 34964);

            CriteriaBuilder.In<Integer> in = builder.in(root.get("ProdNo"));

            prodList.forEach(p -> in.value(p));

            query.where(in);

            TypedQuery<Contractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return Collections.emptyList();

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public List<Item> getSurchargeItems() throws DataAccessException {

        System.out.println("Inside getSurchargeItems() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Item> query = builder.createQuery(Item.class);

            Root<Item> root = query.from(Item.class);

            query.select(root).where(builder.like(root.get("itemName"), "%SURCHARGE%"));

            TypedQuery<Item> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return Collections.emptyList();

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public ArrayList<ContractPrice> getContractPriceByComponentID(Integer componentId, List<Integer> items) throws DataAccessException {

        System.out.println("Inside getContractPriceByComponentID() ");

        ArrayList<ContractPrice> contractPrices = new ArrayList<>();

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        System.out.println("Inside ContractPrice() ");

        List<Query> query = entityManager.createNativeQuery("select price_id, item_id_dup from core.contractprice where contractcomponent_id = " + componentId + " and item_id_dup in ( " + items.get(0) + "," + items.get(1) + "," + items.get(2) + "," + items.get(3) + " )").getResultList();

        Iterator listIterator = query.listIterator();

        while (listIterator.hasNext()) {

            Object[] object = (Object[]) listIterator.next();

            ContractPrice contractPrice = new ContractPrice();

            Integer priceId = Integer.parseInt(object[0].toString());

            Integer itemId = Integer.parseInt(object[1].toString());

            contractPrice.setPriceId(priceId.longValue());

            contractPrice.setItemIdDup(itemId.longValue());

            contractPrices.add(contractPrice);

        }

        entityManager.close();

        return contractPrices;

    }



    @org.springframework.data.jpa.repository.Query

    public List<Surchargedump> findAllSurchargeDumps() throws DataAccessException {

        System.out.println("Inside findSurchargeDump() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Surchargedump> query = builder.createQuery(Surchargedump.class);

            Root<Surchargedump> root = query.from(Surchargedump.class);

            query.select(root);

            Predicate updated = builder.equal(root.get("updated"), false);

            Predicate enabled = builder.equal(root.get("enabled"), true);



            query.where(updated, enabled);

            TypedQuery<Surchargedump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return Collections.emptyList();

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public ContractComponent findCustomerInContractComponent(String customerNumber) throws DataAccessException {

        System.out.println("Inside findCustomerInContractComponent() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ContractComponent> query = builder.createQuery(ContractComponent.class);

            Root<ContractComponent> root = query.from(ContractComponent.class);

            query.select(root);

            Predicate sspkCheck = builder.equal(root.get("sourceSystemRecordPk"), customerNumber);

            Predicate sourceSystemidCheck = builder.equal(root.get("sourceSystemId"), 3);



            query.where(sspkCheck, sourceSystemidCheck);

            TypedQuery<ContractComponent> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public ContractRole findCustomerInContractRole(String customerNumber) throws DataAccessException {

        System.out.println("Inside findCustomerInContractRole() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ContractRole> query = builder.createQuery(ContractRole.class);

            Root<ContractRole> root = query.from(ContractRole.class);

            query.select(root);

            Predicate sspkCheck = builder.equal(root.get("partySourceSystemRecordPk"), customerNumber);

            Predicate contractroleTpCdCheck = builder.equal(root.get("contractRoleTpCd"), 1);

            System.out.println("Customer No - " + customerNumber);

            query.where(sspkCheck, contractroleTpCdCheck);

            TypedQuery<ContractRole> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList().get(0);

            else

                return null;

        } catch (NonUniqueResultException non) {

            System.out.println(" customerNumber " + customerNumber);

            non.printStackTrace();

            System.exit(1);

            return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            System.exit(1);

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public ContractRole findCustomerInContractRoleAllType(String customerNumber) throws DataAccessException {

        System.out.println("Inside findCustomerInContractRole() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ContractRole> query = builder.createQuery(ContractRole.class);

            Root<ContractRole> root = query.from(ContractRole.class);

            query.select(root);

            Predicate sspkCheck = builder.equal(root.get("partySourceSystemRecordPk"), customerNumber);

            System.out.println("Customer No - " + customerNumber);

            query.where(sspkCheck);

            TypedQuery<ContractRole> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList().get(0);

            else

                return null;

        } catch (NonUniqueResultException non) {

            System.out.println(" customerNumber " + customerNumber);

            non.printStackTrace();

            System.exit(1);

            return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            System.exit(1);

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public Customerbranch findCustomerBranch(String customerNumber) throws DataAccessException {

        System.out.println("Inside findCustomerBranch() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Customerbranch> query = builder.createQuery(Customerbranch.class);

            Root<Customerbranch> root = query.from(Customerbranch.class);

            query.select(root);

            Predicate customerBranchCheck = builder.equal(root.get("customerNumber"), customerNumber);



            query.where(customerBranchCheck);

            TypedQuery<Customerbranch> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public ArrayList<String> filterOrganizations(Set<String> partyKeySet) throws DataAccessException {

        System.out.println("Inside filterOrganizations() ");
        Set<String> partyIds = new HashSet<String>();
        for (String partyDetails : partyKeySet) {
            partyIds.add(partyDetails.split("~")[0]);
        }
        ArrayList<String> parties = new ArrayList<String>();
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Party> query = builder.createQuery(Party.class);
            Root<Party> root = query.from(Party.class);
            query.select(root);
            List<String> prodList = new ArrayList<String>(partyIds);
            CriteriaBuilder.In<String> in = builder.in(root.get("sourceSystemRecordPk"));
            prodList.forEach(p -> in.value(p));
            Predicate sourceSystemId = builder.equal(root.get("sourceSystemId"), 3);
            Predicate parrentIsNull = builder.isNull(root.get("parentSourceSystemRecordPk"));
            Predicate predicates = builder.and(sourceSystemId, parrentIsNull);
            query.where(predicates, in);
            TypedQuery<Party> typedQuery = em.createQuery(query);
            if (typedQuery.getResultStream().findAny().isPresent()) {
                ArrayList<Party> partyList = (ArrayList<Party>) typedQuery.getResultList();
                for (Party party : partyList)
                    parties.add(party.getSourceSystemRecordPk());
            }
            return parties;
        } catch (HibernateException he) {
            he.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        } finally {
            em.clear();
            em.close();
        }
    }



    @org.springframework.data.jpa.repository.Query

    public ArrayList<Contractdump> findContractdumpByOrganization(String organizationNumber) throws DataAccessException {

        System.out.println("Inside filterOrganizations() ");

        ArrayList<Contractdump> contractdumpArrayList = new ArrayList<Contractdump>();

        Set<String> customerNumbers = new HashSet<String>();

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Contractdump> query = builder.createQuery(Contractdump.class);

            Root<Contractdump> root = query.from(Contractdump.class);

            query.select(root);

            Predicate sourceSystemId = builder.equal(root.get("organizationNumber"), organizationNumber);

            Predicate namePredicate = builder.equal(root.get("updated"), false);

            Predicate enablePredicate = builder.equal(root.get("enabled"), true);



            Predicate predicates = builder.and(sourceSystemId, namePredicate, enablePredicate);

            query.where(predicates);

            TypedQuery<Contractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent()) {

                contractdumpArrayList = (ArrayList<Contractdump>) typedQuery.getResultList();

                for (Contractdump contractdump : contractdumpArrayList) {

                    if (!contractdump.getOrganizationNumber().equals(contractdump.getCustomerNumber()))

                        customerNumbers.add(contractdump.getCustomerNumber());

                    if (contractdump.getOrganizationNumber().equals("20000186625"))

                        System.out.println("wait");

                }

            }

            if (customerNumbers.size() > 1)

                contractdumpArrayList.addAll(findContractdumpByCustomersAsOrganization(customerNumbers));

            else if (customerNumbers.size() == 1 && !customerNumbers.iterator().next().equals(organizationNumber))

                contractdumpArrayList.addAll(findContractdumpByCustomersAsOrganization(customerNumbers));



            return contractdumpArrayList;



        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public ArrayList<Contractdump> findContractdumpByCustomersAsOrganization(Set<String> customerNumbers) throws DataAccessException {

        System.out.println("Inside findContractdumpByCustomersAsOrganization() ");



        ArrayList<Contractdump> parties = new ArrayList<Contractdump>();

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Contractdump> query = builder.createQuery(Contractdump.class);

            Root<Contractdump> root = query.from(Contractdump.class);

            query.select(root);

            CriteriaBuilder.In<String> in = builder.in(root.get("organizationNumber"));

            customerNumbers.forEach(p -> in.value(p));

            Predicate namePredicate = builder.equal(root.get("updated"), false);

            Predicate enablePredicate = builder.equal(root.get("enabled"), true);



            Predicate predicates = builder.and(namePredicate, enablePredicate);

            query.where(in, predicates);

            TypedQuery<Contractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return (ArrayList<Contractdump>) typedQuery.getResultList();

            else

                return new ArrayList<Contractdump>();



        } catch (HibernateException he) {

            he.printStackTrace();

            return new ArrayList<Contractdump>();

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public List<Percentagebaseddump> findAllPercentageBasedContractdumps(String fileCoutnry) throws DataAccessException {

        System.out.println("Inside findAllPercentageBasedContractdumps() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Percentagebaseddump> query = builder.createQuery(Percentagebaseddump.class);

            Root<Percentagebaseddump> root = query.from(Percentagebaseddump.class);

            query.select(root);

            Predicate namePredicate = builder.equal(root.get("updated"), false);

            Predicate countryPredicate = builder.equal(root.get("fileCountry"), fileCoutnry);

            Predicate enablePredicate = builder.equal(root.get("enabled"), true);



            //Predicate service349 = builder.equal(root.get("ProdNo"), 349);

            //  Predicate service34964 = builder.equal(root.get("ProdNo"), 34964);



            Predicate predicates = builder.and(countryPredicate, namePredicate, enablePredicate);

            query.where(predicates);

            TypedQuery<Percentagebaseddump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return Collections.emptyList();

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public List<Contractdump> findAllContractdumpsByOrganization() throws DataAccessException {

        System.out.println("Inside findAllContractdumpsByOrganization() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Contractdump> query = builder.createQuery(Contractdump.class);

            Root<Contractdump> root = query.from(Contractdump.class);

            query.select(root);

            //    query.where(root.get("organizationNumber").in(getOrganizationList()));

            TypedQuery<Contractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return Collections.emptyList();

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public List<Contractdump> findContractdumpsByCustomer(String customerId) throws DataAccessException {

        System.out.println("Inside findAllContractdumps() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Contractdump> query = builder.createQuery(Contractdump.class);

            Root<Contractdump> root = query.from(Contractdump.class);

            query.select(root);

            Predicate namePredicate = builder.equal(root.get("updated"), false);

            Predicate customerPredicate = builder.equal(root.get("customerNumber"), customerId);

            Predicate enablePredicate = builder.equal(root.get("enabled"), true);

            Predicate predicates = builder.and(namePredicate, customerPredicate, enablePredicate);

            query.where(predicates);

            TypedQuery<Contractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return Collections.emptyList();

        } catch (HibernateException he) {

            he.printStackTrace();

            System.exit(1);

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public List<Contractdump> findContractdumpsByOrganization(String customerId) throws DataAccessException {

        System.out.println("Inside findAllContractdumps() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Contractdump> query = builder.createQuery(Contractdump.class);

            Root<Contractdump> root = query.from(Contractdump.class);

            query.select(root);

            Predicate namePredicate = builder.equal(root.get("updated"), false);

            Predicate customerPredicate = builder.equal(root.get("organizationNumber"), customerId);

            Predicate enablePredicate = builder.equal(root.get("enabled"), true);

            Predicate predicates = builder.and(namePredicate, customerPredicate, enablePredicate);

            query.where(predicates);

            TypedQuery<Contractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return Collections.emptyList();

        } catch (HibernateException he) {

            he.printStackTrace();

            System.exit(1);

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public List<Percentagebaseddump> findPercentageBasedContractdumpsByDate(String organizationNumber, String startDate, ContractPrice contractPrice, String rollType) throws DataAccessException {

        System.out.println("Inside findPercentageBasedContractdumpsByDate() ");

        Integer prodNo = null;



        if (rollType.equals("1"))

            rollType = "parentCustomerNumber";

        else

            rollType = "customerNumber";

        Item item = findItem(contractPrice.getItemIdDup().intValue());

        if (null == item) {

            item = findProdNoByServiceAddOn(contractPrice.getItemIdDup().intValue());

            if (item == null) {

                item = findProdNoFromServiceAddOnISource(contractPrice.getItemIdDup().intValue());

                if (item == null) {

                    System.out.println("Item not found for " + contractPrice.getItemIdDup().intValue() + "  :: Price ID - " + contractPrice.getPriceId());

                    System.exit(1);

                } else {

                    System.out.println("check here" + ((ServiceAddOn) item).getiSourceSystemRecordPk());

                    System.out.println("check here" + ((ServiceAddOn) item).getiSourceSystemRecordPk());

                    // for add on Isource



                }

            } else {

                // for service add on

                System.out.println("check here" + ((ServiceAddOn) item).getiSourceSystemRecordPk());

                System.out.println("check here" + ((ServiceAddOn) item).getiSourceSystemRecordPk());

            }

        } else

            prodNo = item.getSourceSystemRecordPk().intValue();

        if (prodNo.toString().toCharArray().length == 5) {

            String temp = prodNo.toString().substring(3, 5) + "" + prodNo.toString().substring(0, 3);

            prodNo = Integer.parseInt(temp);

        } else if (prodNo.toString().toCharArray().length > 5) {

            System.out.println("wait and fix it");

        }



        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Percentagebaseddump> query = builder.createQuery(Percentagebaseddump.class);

            Root<Percentagebaseddump> root = query.from(Percentagebaseddump.class);

            query.select(root);

            Predicate namePredicate = builder.equal(root.get("updated"), true);

            Predicate customerPredicate = builder.equal(root.get(rollType), organizationNumber);

            Predicate enablePredicate = builder.equal(root.get("enabled"), true);

            Predicate startPredicate = builder.equal(root.get("startdate"), sdf.parse(startDate));

            //      Predicate itemPredicate = builder.equal(root.get("ProdNo"),prodNo);

            Predicate predicates = builder.and(namePredicate, customerPredicate, enablePredicate, startPredicate);

            query.where(predicates);

            TypedQuery<Percentagebaseddump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return Collections.emptyList();

        } catch (HibernateException he) {

            he.printStackTrace();

            System.exit(1);

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public List<Contractdump> findContractdumpsByDate(String organizationNumber, String startDate, ContractPrice contractPrice, String rollType) throws DataAccessException {

        System.out.println("Inside findAllContractdumps() ");

        Integer prodNo = null;



        if (rollType.equals("1"))

            rollType = "organizationNumber";

        else

            rollType = "customerNumber";

        Item item = findItem(contractPrice.getItemIdDup().intValue());

        if (null == item) {

            item = findProdNoByServiceAddOn(contractPrice.getItemIdDup().intValue());

            if (item == null) {

                item = findProdNoFromServiceAddOnISource(contractPrice.getItemIdDup().intValue());

                if (item == null) {

                    System.out.println("Item not found for " + contractPrice.getItemIdDup().intValue() + "  :: Price ID - " + contractPrice.getPriceId());

                    System.exit(1);

                } else {

                    System.out.println("check here" + ((ServiceAddOn) item).getiSourceSystemRecordPk());

                    System.out.println("check here" + ((ServiceAddOn) item).getiSourceSystemRecordPk());

                    // for add on Isource



                }

            } else {

                // for service add on

                System.out.println("check here" + ((ServiceAddOn) item).getiSourceSystemRecordPk());

                System.out.println("check here" + ((ServiceAddOn) item).getiSourceSystemRecordPk());

            }

        } else

            prodNo = item.getSourceSystemRecordPk().intValue();

        if (prodNo.toString().toCharArray().length == 5) {

            String temp = prodNo.toString().substring(3, 5) + "" + prodNo.toString().substring(0, 3);

            prodNo = Integer.parseInt(temp);

        } else if (prodNo.toString().toCharArray().length > 5) {

            System.out.println("wait and fix it");

        }



        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Contractdump> query = builder.createQuery(Contractdump.class);

            Root<Contractdump> root = query.from(Contractdump.class);

            query.select(root);

            Predicate namePredicate = builder.equal(root.get("updated"), true);

            Predicate customerPredicate = builder.equal(root.get(rollType), organizationNumber);

            Predicate enablePredicate = builder.equal(root.get("enabled"), true);

            Predicate startPredicate = builder.equal(root.get("FromDate"), sdf.parse(startDate));

            //      Predicate itemPredicate = builder.equal(root.get("ProdNo"),prodNo);

            Predicate predicates = builder.and(namePredicate, customerPredicate, enablePredicate, startPredicate);

            query.where(predicates);

            TypedQuery<Contractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return Collections.emptyList();

        } catch (HibernateException he) {

            he.printStackTrace();

            System.exit(1);

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public List<Percentagebaseddump> findPercentageBasedContractdumpsByCustomer(String parentCustomerId) throws DataAccessException {

        System.out.println("Inside findPercentageBasedContractdumpsByCustomer() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Percentagebaseddump> query = builder.createQuery(Percentagebaseddump.class);

            Root<Percentagebaseddump> root = query.from(Percentagebaseddump.class);

            query.select(root);

            //Predicate namePredicate = builder.equal(root.get("updated"), false);

            Predicate customerPredicate = builder.equal(root.get("parentCustomerNumber"), parentCustomerId);

            //Predicate enablePredicate = builder.equal(root.get("enabled"), true);

            // Predicate predicates = builder.and(namePredicate,  customerPredicate, enablePredicate);

            Predicate predicates = builder.and(customerPredicate);

            query.where(predicates);

            TypedQuery<Percentagebaseddump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return Collections.emptyList();

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public Contract findContractBySSPK(String sspkId) throws DataAccessException {

        System.out.println("Inside findContractBySSPK() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Contract> query = builder.createQuery(Contract.class);

            Root<Contract> root = query.from(Contract.class);

            query.select(root);

            Predicate sspkPredicate = builder.equal(root.get("sourceSystemRecordPk"), sspkId);

            query.where(sspkPredicate);

            TypedQuery<Contract> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public ArrayList<ContractRole> findContractRolesBySSPK(String sspkId) throws DataAccessException {

        System.out.println("Inside findContractBySSPK() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<ContractRole> query = builder.createQuery(ContractRole.class);

            Root<ContractRole> root = query.from(ContractRole.class);

            query.select(root);

            Predicate sspkPredicate = builder.equal(root.get("partySourceSystemRecordPk"), sspkId);

            query.where(sspkPredicate);

            TypedQuery<ContractRole> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return (ArrayList<ContractRole>) typedQuery.getResultList();

            else

                return new ArrayList<ContractRole>();

        } catch (HibernateException he) {

            he.printStackTrace();

            return new ArrayList<ContractRole>();

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public Cdhomedelivery findCdhomedelivery(String zoneName) {

        System.out.println("Inside findContractComponent() ");



        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Cdhomedelivery> query = builder.createQuery(Cdhomedelivery.class);

            Root<Cdhomedelivery> root = query.from(Cdhomedelivery.class);

            Predicate zoneNamePredicate = builder.equal(root.get("zoneName"), zoneName);

            query.where(zoneNamePredicate);

            query.select(root);

            TypedQuery<Cdhomedelivery> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public Cdhomedeliverytp findCdhomedeliverytp(String zoneName) {

        System.out.println("Inside findCdcustomzonetp() ");



        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Cdhomedeliverytp> query = builder.createQuery(Cdhomedeliverytp.class);

            Root<Cdhomedeliverytp> root = query.from(Cdhomedeliverytp.class);

            Predicate zoneNamePredicate = builder.equal(root.get("zoneName"), zoneName);

            query.where(zoneNamePredicate);

            query.select(root);

            TypedQuery<Cdhomedeliverytp> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public SlabBasedPriceEntry findSlabbasedPriceEntry(Long slabbasedprice_id) {

        SlabBasedPriceEntry slabBasedPriceEntry = new SlabBasedPriceEntry();

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        System.out.println("Inside fetchDataToContractRole() ");

        List<Query> query = entityManager.createNativeQuery("select * from core.slabbasedpriceentry where slabbasedprice_id =" + slabbasedprice_id).getResultList();

        Iterator listIterator = query.listIterator();

        while (listIterator.hasNext()) {

            Object[] object = (Object[]) listIterator.next();

            slabBasedPriceEntry.setPriceBasisLowerBound(new Long(object[2].toString()));

            slabBasedPriceEntry.setPriceBasisUpperBound(new Long(object[3].toString()));

            slabBasedPriceEntry.setPriceValue(new BigDecimal(object[4].toString()));



        }

        entityManager.close();

        return slabBasedPriceEntry;

    }



    // used this method when I have to increase slabbasedpriceentry table's price_basis_lower_bound by 1

    public List<SlabBasedPriceEntry> findSlabbasedPriceEntries() {



        List<SlabBasedPriceEntry> entries = new ArrayList<SlabBasedPriceEntry>();

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        System.out.println("Inside fetchDataToContractRole() ");

        //List<Query> query = entityManager.createQuery(" from Price price where price.basePrice is null AND price.percentBasedPrice is null").getResultList();

        List<Query> query = entityManager.createNativeQuery("select * from core.slabbasedpriceentry where price_basis_lower_bound !=0 order by slabbasedprice_id, price_basis_lower_bound desc ").getResultList();

        Iterator listIterator = query.listIterator();

        while (listIterator.hasNext()) {

            Object[] object = (Object[]) listIterator.next();

            updateSlabbasedPriceEntries(Integer.parseInt(object[0].toString()));

            //  SlabBasedPriceEntry slabBasedPriceEntry = (SlabBasedPriceEntry)object;

            //  entries.add(slabBasedPriceEntry);

        }

        entityManager.close();

        return entries;

    }



    public List<SlabBasedPriceEntry> updateSlabbasedPriceEntries(Integer slabbasedpriceentry_id) {



        List<SlabBasedPriceEntry> entries = new ArrayList<SlabBasedPriceEntry>();

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        entityManager.getTransaction().begin();

        System.out.println("Inside fetchDataToContractRole() ");

        //List<Query> query = entityManager.createQuery(" from Price price where price.basePrice is null AND price.percentBasedPrice is null").getResultList();

        Query query = entityManager.createNativeQuery("update core.slabbasedpriceentry set price_basis_lower_bound =  price_basis_lower_bound+1 where slabbasedpriceentry_id= " + slabbasedpriceentry_id);



        query.executeUpdate();

        entityManager.getTransaction().commit();

        entityManager.close();

        return entries;

    }



    public void findProdNoContractDump() {



        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            ArrayList<Integer> dumps = new ArrayList<Integer>();

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Integer> query = builder.createQuery(Integer.class);

            Root<Contractdump> root = query.from(Contractdump.class);

            query.select(root.get("ProdNo")).distinct(true);

            TypedQuery<Integer> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent()) {

                dumps = (ArrayList<Integer>) typedQuery.getResultList();

                updateContractPriceItemDetails(dumps);

                System.out.println("asdasdasd" + dumps.size());

            }

        } catch (Exception e) {

            e.printStackTrace();



        } finally {

            em.clear();

            em.close();

        }

    }



    public void updateContractPriceItemDetails(ArrayList<Integer> prodNumbers) {



        HashMap<String, ArrayList<Integer>> itemsByTypeMap = new HashMap<String, ArrayList<Integer>>();

        itemsByTypeMap.put("normal", new ArrayList<Integer>());

        itemsByTypeMap.put("passive", new ArrayList<Integer>());

        itemsByTypeMap.put("passive2", new ArrayList<Integer>());

        itemsByTypeMap.put("unmapped", new ArrayList<Integer>());



        for (Integer prodNo : prodNumbers) {

            Item item = getNormalItemId(prodNo);

            if (item != null) {

                //  itemsByTypeMap.get("normal").add(item.getItemId().intValue());

                itemsByTypeMap.get("normal").add(prodNo);

            } else {

                item = getPassiveItemId(prodNo);

                if (item != null) {

                    //    itemsByTypeMap.get("passive").add(item.getItemId().intValue());

                    itemsByTypeMap.get("passive").add(prodNo);

                } else {

                    item = getPassiveItemId2(prodNo);

                    if (item != null && prodNo.toString().startsWith("64", prodNo.toString().length() - 2)) {

                        Integer temp = Integer.parseInt(prodNo.toString().substring(0, prodNo.toString().length() - 2));

                        Item itemForActice = getItem(temp);

                        updateContractPrice(item, itemForActice);

                        itemsByTypeMap.get("passive2").add(prodNo);

                    } else {

                        itemsByTypeMap.get("unmapped").add(prodNo);

                    }

                }

            }



        }

        Set<String> keys = itemsByTypeMap.keySet();

        for (String key : keys) {

            System.out.println("    ");

            System.out.println("Type : " + key + "  :");

            ArrayList<Integer> items = itemsByTypeMap.get(key);

            for (Integer itemId : items) {

                System.out.print(itemId + ", ");

            }

        }

    }



    public void updateContractPrice(Item oldItem, Item newItem) {

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        entityManager.getTransaction().begin();

        System.out.println("Inside fetchDataToContractRole() ");

        Query query = entityManager.createNativeQuery("update core.contractprice set item_id_dup= " + newItem.getItemId() + ", appl_journey_tp_cd=2 where item_id_dup= " + oldItem.getItemId());

        query.executeUpdate();

        entityManager.getTransaction().commit();

        entityManager.close();



    }



    public Item getNormalItemId(Integer serviceId) {

        Service service = null;

        Item item = null;

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            service = getService(serviceId.longValue());

            if (null != service) {

                Query query = entityManager.createQuery(" from Item where itemId = :itemId");

                query.setParameter("itemId", service.getItemId());



                if (query.getResultStream().findFirst().isPresent())

                    item = (Item) query.getSingleResult();

                entityManager.close();

            }

        } catch (HibernateException he) {

            he.printStackTrace();

        } finally {

            entityManager.close();

        }

        return item;

    }



    public Item getPassiveItemId(Integer serviceId) {

        Item item = null;

        ServiceAddOn serviceAddOn = null;

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            serviceAddOn = findServiceAddOn(serviceId);

            if (null != serviceAddOn) {

                Query query = entityManager.createQuery(" from Item where itemId = :itemId");

                query.setParameter("itemId", serviceAddOn.getItemId());

                if (query.getResultStream().findFirst().isPresent())

                    item = (Item) query.getSingleResult();

                entityManager.close();

            }

        } catch (HibernateException he) {

            he.printStackTrace();

        } finally {

            entityManager.close();

        }

        return item;

    }



    public Item getPassiveItemId2(Integer serviceId) {

        Item item = null;

        ServiceAddOn serviceAddOn = null;

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            serviceAddOn = findServiceAddOnISourceRecord(serviceId);

            if (null != serviceAddOn) {

                Query query = entityManager.createQuery(" from Item where itemId = :itemId");

                query.setParameter("itemId", serviceAddOn.getItemId());

                if (query.getResultStream().findFirst().isPresent())

                    item = (Item) query.getSingleResult();

                entityManager.close();

            }

        } catch (HibernateException he) {

            he.printStackTrace();

        } finally {

            entityManager.close();

        }

        return item;

    }



    // used this method when I have to increase slabbasedpriceentry table's price_basis_lower_bound by 1

    public List<SlabBasedPriceEntry> findSlabbasedPriceEntriesForItem() {



        List<SlabBasedPriceEntry> entries = new ArrayList<SlabBasedPriceEntry>();

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        System.out.println("Inside fetchDataToContractRole() ");

        //List<Query> query = entityManager.createQuery(" from Price price where price.basePrice is null AND price.percentBasedPrice is null").getResultList();

        List<Query> query = entityManager.createNativeQuery("select entry.* from core.slabbasedpriceentry entry inner join core.slabbasedprice slab on entry.slabbasedprice_id = slab.slabbasedprice_id where slab.slabbasis_tp_cd=1 and slab.created_by_user='Automation' order by entry.slabbasedprice_id , price_basis_lower_bound ").getResultList();

        Iterator listIterator = query.listIterator();

        while (listIterator.hasNext()) {

            Object[] object = (Object[]) listIterator.next();

            updateSlabbasedPriceEntriesForITEMS(object);

            //  SlabBasedPriceEntry slabBasedPriceEntry = (SlabBasedPriceEntry)object;

            //  entries.add(slabBasedPriceEntry);

        }

        entityManager.close();

        return entries;

    }



    private Object[] modifySlabbasedEntry(Object[] object) {



        if (Integer.parseInt(object[2].toString()) != 0) {

            object[2] = Integer.parseInt(object[2].toString()) - 1;

        }

        if (Integer.parseInt(object[3].toString()) != 9999 && Integer.parseInt(object[3].toString()) != 1)

            object[3] = Integer.parseInt(object[3].toString()) - 1;



        return object;

    }



    public List<SlabBasedPriceEntry> updateSlabbasedPriceEntriesForITEMS(Object[] object) {



        object = modifySlabbasedEntry(object);



        List<SlabBasedPriceEntry> entries = new ArrayList<SlabBasedPriceEntry>();

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        entityManager.getTransaction().begin();

        System.out.println("Inside fetchDataToContractRole() ");

        //List<Query> query = entityManager.createQuery(" from Price price where price.basePrice is null AND price.percentBasedPrice is null").getResultList();

        Query query = entityManager.createNativeQuery("update core.slabbasedpriceentry set price_basis_lower_bound = " + object[2] + " , price_basis_upper_bound = " + object[3] + " where slabbasedpriceentry_id= " + object[0]);

        query.executeUpdate();

        entityManager.getTransaction().commit();

        entityManager.close();

        return entries;

    }



    private String filterDuplicates(String vals) {

        String finalStr = "";

        String[] valArray = vals.split(",");

        ArrayList<String> list = new ArrayList<String>();

        ArrayList<String> filterlist = new ArrayList<String>();

        list.addAll(Arrays.asList(valArray));

        Set<String> set = new HashSet<String>();

        set.addAll(list);



        for (String str : set) {

            System.out.println(set + ", ");

            if (finalStr != "")

                finalStr = finalStr + ", " + str;

            else

                finalStr = str;

        }

        return finalStr;

    }



    public List<ContractPrice> removeDuplicates() throws ParseException {



        List<ContractPrice> entries = new ArrayList<ContractPrice>();

        Object[] temp = null;

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        System.out.println("Inside ContractPrice() ");

        StringBuilder finalBuilder = new StringBuilder();

        //List<Query> query = entityManager.createQuery(" from Price price   price.basePrice is null AND price.percentBasedPrice is null").getResultList();

        List<Query> contratccomponentQuery = entityManager.createNativeQuery("select distinct contractcomponent_id from core.contractprice where contractprice_processing_tp_cd  in (8)").getResultList();

        Iterator listIterator = contratccomponentQuery.listIterator();

        while (listIterator.hasNext()) {

            Integer object = (Integer) listIterator.next();

            Integer contractcomponent_id = object;

            List<Query> componentQuery = entityManager.createNativeQuery("select distinct item_id_dup from core.contractprice where contractcomponent_Id=" + contractcomponent_id + " AND contractprice_processing_tp_cd  in (8)").getResultList();

            Iterator innerListIterator = componentQuery.listIterator();

            ArrayList<Integer> items = new ArrayList<Integer>();

            while (innerListIterator.hasNext()) {

                Integer obectItem = (Integer) innerListIterator.next();

                items.add(obectItem);

            }

            for (Integer item : items) {

                List<Query> componentByItemIdQuery = entityManager.createNativeQuery("select * from core.contractprice where contractcomponent_Id=" + contractcomponent_id + "  AND contractprice_processing_tp_cd  in (8) and item_id_dup =" + item).getResultList();

                Iterator innerListByItemIterator = componentByItemIdQuery.listIterator();

                ArrayList<ContractPrice> contractPrices = new ArrayList<ContractPrice>();

                while (innerListByItemIterator.hasNext()) {

                    Object[] objectByItem = (Object[]) innerListByItemIterator.next();

                    System.out.println(objectByItem[0].toString());

                    ContractPrice contractPrice = new ContractPrice();

                    contractPrice.setContractComponent(new Integer(objectByItem[5].toString()));

                    contractPrice.setPriceId(new Long(objectByItem[0].toString()));

                    contractPrice.setItemIdDup(new Long(objectByItem[9].toString()));

                    if (null != objectByItem[11])

                        contractPrice.setFromCountry(Integer.parseInt(objectByItem[11].toString()));

                    if (null != objectByItem[12])

                        contractPrice.setFromPostalCode(objectByItem[12].toString());

                    if (null != objectByItem[13])

                        contractPrice.setToCountry(Integer.parseInt(objectByItem[13].toString()));

                    if (null != objectByItem[14])

                        contractPrice.setFromPostalCode(objectByItem[14].toString());

                    if (null != objectByItem[15])

                        contractPrice.setToCountryZoneCountFrom(Integer.parseInt(objectByItem[15].toString()));

                    if (null != objectByItem[16])

                        contractPrice.setToCountryZoneCountTo(Integer.parseInt(objectByItem[16].toString()));

                    if (null != objectByItem[18])

                        contractPrice.setApplJourneyTpCd(Integer.parseInt(objectByItem[18].toString()));

                    if (null != objectByItem[19])

                        contractPrice.setZoneId(Integer.parseInt(objectByItem[19].toString()));

                    if (null != objectByItem[20])

                        contractPrice.setContractpriceProcessingTpCd(Integer.parseInt(objectByItem[20].toString()));

                    if (null != objectByItem[21])

                        contractPrice.setFromRouteId(Integer.parseInt(objectByItem[21].toString()));

                    if (null != objectByItem[22])

                        contractPrice.setToRouteId(Integer.parseInt(objectByItem[22].toString()));



                    contractPrices.add(contractPrice);

                }



                if (contractPrices.size() > 1) {

                    StringBuilder builder = new StringBuilder();

                    identifyDuplicates(contractPrices, builder);

                    if (builder.toString().contains(",")) {

                        String[] prices = builder.toString().split(",");

                        for (String price : prices) {

                            if (!finalBuilder.toString().contains(price)) {

                                finalBuilder.append(builder);

                            }

                        }

                    }

                }

            }

        }

        String filteredPrices = filterDuplicates(finalBuilder.toString());

        System.out.println(" Duplicate entries :  " + filteredPrices);

        entityManager.close();

        return entries;

    }



    private void identifyDuplicates(ArrayList<ContractPrice> contractPrices, StringBuilder builder) throws ParseException {

        if (contractPrices.size() > 1) {

            int counter = 0;

            //for(ContractPrice contractPrice : contractPrices){

            Iterator iterator = contractPrices.iterator();

            while ((iterator.hasNext())) {

                ContractPrice contractPrice = (ContractPrice) iterator.next();

                //  builder.append( contractPrice.getPriceId() +",");

                compareContractPriceEntries(contractPrice, contractPrices, builder);

                iterator.remove();

            }

        }

    }



    private void compareContractPriceEntries(ContractPrice contractPrice, ArrayList<ContractPrice> contractPrices, StringBuilder builder) throws ParseException {

        StringBuilder sb = new StringBuilder();

        for (ContractPrice cp : contractPrices) {

            if (!cp.getPriceId().equals(contractPrice.getPriceId())) {

                if (contractPrice.getFromCountry() != null && cp.getFromCountry() != null && contractPrice.getFromCountry().equals(cp.getFromCountry())) {

                    checkToCountry(contractPrice, cp, builder);

                } else if (contractPrice.getFromCountry() == null && cp.getFromCountry() == null) {

                    checkToCountry(contractPrice, cp, builder);

                } else {

                    // do nothing

                }

            }



        }

        sb.append("/n");

    }



    private void checkToCountry(ContractPrice contractPrice1, ContractPrice contractPrice2, StringBuilder builder) throws ParseException {

        if (contractPrice1.getToCountry() != null && contractPrice2.getToCountry() != null && contractPrice1.getToCountry().equals(contractPrice2.getToCountry())) {

            checkFromPostalCode(contractPrice1, contractPrice2, builder);

        } else if (contractPrice1.getToCountry() == null && contractPrice2.getToCountry() == null) {

            checkFromPostalCode(contractPrice1, contractPrice2, builder);

        } else {

            // do nothing

        }

    }



    private void checkFromPostalCode(ContractPrice contractPrice1, ContractPrice contractPrice2, StringBuilder builder) throws ParseException {

        if (contractPrice1.getFromPostalCode() != null && contractPrice2.getFromPostalCode() != null && contractPrice1.getFromPostalCode().equals(contractPrice2.getFromPostalCode())) {

            checkToPostalCode(contractPrice1, contractPrice2, builder);

        } else if (contractPrice1.getFromPostalCode() == null && contractPrice2.getFromPostalCode() == null) {

            checkToPostalCode(contractPrice1, contractPrice2, builder);

        } else {

            // do nothing

        }

    }



    private void checkToPostalCode(ContractPrice contractPrice1, ContractPrice contractPrice2, StringBuilder builder) throws ParseException {

        if (contractPrice1.getFromPostalCode() != null && contractPrice2.getFromPostalCode() != null && contractPrice1.getFromPostalCode().equals(contractPrice2.getFromPostalCode())) {

            checkToZoneCountFrom(contractPrice1, contractPrice2, builder);

        } else if (contractPrice1.getFromPostalCode() == null && contractPrice2.getFromPostalCode() == null) {

            checkToZoneCountFrom(contractPrice1, contractPrice2, builder);

        } else {

            // do nothing

        }

    }



    private void checkToZoneCountFrom(ContractPrice contractPrice1, ContractPrice contractPrice2, StringBuilder builder) throws ParseException {

        if (contractPrice1.getToCountryZoneCountFrom() != null && contractPrice2.getToCountryZoneCountFrom() != null && contractPrice1.getToCountryZoneCountFrom().equals(contractPrice2.getToCountryZoneCountFrom())) {

            checkToZoneCountTo(contractPrice1, contractPrice2, builder);

        } else if (contractPrice1.getToCountryZoneCountFrom() == null && contractPrice2.getToCountryZoneCountFrom() == null) {

            checkToZoneCountTo(contractPrice1, contractPrice2, builder);

        } else {

            // do nothing

        }

    }



    private void checkToZoneCountTo(ContractPrice contractPrice1, ContractPrice contractPrice2, StringBuilder builder) throws ParseException {

        if (contractPrice1.getToCountryZoneCountTo() != null && contractPrice2.getToCountryZoneCountTo() != null && contractPrice1.getToCountryZoneCountTo().equals(contractPrice2.getToCountryZoneCountTo())) {

            checkToApplJourney(contractPrice1, contractPrice2, builder);

        } else if (contractPrice1.getToCountryZoneCountTo() == null && contractPrice2.getToCountryZoneCountTo() == null) {

            checkToApplJourney(contractPrice1, contractPrice2, builder);

        } else {

            // do nothing

        }

    }



    private void checkToApplJourney(ContractPrice contractPrice1, ContractPrice contractPrice2, StringBuilder builder) throws ParseException {

        if (contractPrice1.getApplJourneyTpCd() != null && contractPrice2.getApplJourneyTpCd() != null && contractPrice1.getApplJourneyTpCd().equals(contractPrice2.getApplJourneyTpCd())) {

            checkZoneId(contractPrice1, contractPrice2, builder);

        } else if (contractPrice1.getApplJourneyTpCd() == null && contractPrice2.getApplJourneyTpCd() == null) {

            checkZoneId(contractPrice1, contractPrice2, builder);

        } else {

            // do nothing

        }

    }



    private void checkZoneId(ContractPrice contractPrice1, ContractPrice contractPrice2, StringBuilder builder) throws ParseException {

        if (contractPrice1.getContractComponent().equals(25400) && contractPrice1.getItemIdDup().equals(new Long(34))) {

            System.out.println("wait");

        }

        if (contractPrice1.getZoneId() != null && contractPrice2.getZoneId() != null && contractPrice1.getZoneId().equals(contractPrice2.getZoneId())) {

            checkContractProcessing(contractPrice1, contractPrice2, builder);

        } else if (contractPrice1.getZoneId() == null && contractPrice2.getZoneId() == null) {

            checkContractProcessing(contractPrice1, contractPrice2, builder);

        } else {

            // do nothing

        }

    }



    private void checkContractProcessing(ContractPrice contractPrice1, ContractPrice contractPrice2, StringBuilder builder) throws ParseException {

        if (contractPrice1.getContractpriceProcessingTpCd() != null && contractPrice2.getContractpriceProcessingTpCd() != null &&

                contractPrice1.getContractpriceProcessingTpCd().equals(contractPrice2.getContractpriceProcessingTpCd())) {

            checkFromRouteId(contractPrice1, contractPrice2, builder);

        } else {

            // do nothing

        }

    }



    private void checkFromRouteId(ContractPrice contractPrice1, ContractPrice contractPrice2, StringBuilder builder) throws ParseException {

        if (contractPrice1.getFromRouteId() != null && contractPrice2.getFromRouteId() != null && contractPrice1.getFromRouteId().equals(contractPrice2.getFromRouteId())) {

            checkToRouteId(contractPrice1, contractPrice2, builder);

        } else if (contractPrice1.getFromRouteId() == null && contractPrice2.getFromRouteId() == null) {

            checkToRouteId(contractPrice1, contractPrice2, builder);

        } else {

            // do nothing

        }

    }



    private void checkToRouteId(ContractPrice contractPrice1, ContractPrice contractPrice2, StringBuilder builder) throws ParseException {

        if (contractPrice1.getToRouteId() != null && contractPrice2.getToRouteId() != null && contractPrice1.getToRouteId().equals(contractPrice2.getToRouteId())) {

            Price price1 = findPrice(contractPrice1);

            Price price2 = findPrice(contractPrice2);

            checkStartEndDate(price1, price2, builder);

            //builder.append(contractPrice1.getPriceId() +" and "+ contractPrice2.getPriceId()+",");

        } else if (contractPrice1.getToRouteId() == null && contractPrice2.getToRouteId() == null) {

            Price price1 = findPrice(contractPrice1);

            Price price2 = findPrice(contractPrice2);

            checkStartEndDate(price1, price2, builder);

            //builder.append(contractPrice1.getPriceId() +" and "+ contractPrice2.getPriceId()+",");

        } else {

            // do nothing

        }

    }



    private void checkStartEndDate(Price price1, Price price2, StringBuilder builder) {

        if (price1.getStartDt().compareTo(price2.getStartDt()) == 0 && price1.getItemId().equals(price2.getItemId())) {

            if (price1.getBasePrice() != null && price2.getBasePrice() != null && price1.getBasePrice().equals(price2.getBasePrice()))

                builder.append(price1.getPriceId() + "," + price2.getPriceId() + ",");

            else if (price1.getBasePrice() == null && price2.getBasePrice() == null)

                builder.append(price1.getPriceId() + "," + price2.getPriceId() + ",");

        }



    }



    public Price findPrice(ContractPrice contractPrice) throws ParseException {

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        System.out.println("Inside ContractPrice() ");
        Price price = new Price();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
        System.out.println("Price ID --------" + contractPrice.getPriceId());
        List<Query> query = entityManager.createNativeQuery("select * from core.price where price_id =" + contractPrice.getPriceId()).getResultList();
        Iterator listIterator = query.listIterator();
        while (listIterator.hasNext()) {
            Object[] object = (Object[]) listIterator.next();
            if (object[2] == null) {
                price.setPriceId(new Long((Integer.parseInt(object[0].toString()))));
                LocalDate startLocalDate = LocalDate.parse(object[7].toString().substring(0, 19), formatter);
                LocalDate createdLocalDate = LocalDate.parse(object[16].toString().substring(0, 19), formatter);
                LocalDate endLocalDate = null;
                if (object[8] != null && ((Timestamp) object[8]).toLocalDateTime().toLocalDate().getYear() < 1000000)
                    endLocalDate = LocalDate.parse(object[8].toString().substring(0, 19), formatter);
                if (object[1] != null)
                    price.setBasePrice(new BigDecimal(object[1].toString()));
                price.setStartDt(startLocalDate);
                price.setEndDt(endLocalDate);
                if (object[2] != null)
                    price.setPercentBasedPrice(new BigDecimal(object[2].toString()));
                price.setItemId(Integer.parseInt(object[6].toString()));
                price.setCreatedDt(createdLocalDate);
            } else {
                entityManager.close();

                return null;

            }

        }

        entityManager.close();

        return price;



    }



    public Price findPricePERCENT(ContractPrice contractPrice) throws ParseException {

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        System.out.println("Inside ContractPrice() ");

        Price price = new Price();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");

        System.out.println("Price ID --------" + contractPrice.getPriceId());



        //List<Query> query = entityManager.createQuery(" from Price price where price.basePrice is null AND price.percentBasedPrice is null").getResultList();

        List<Query> query = entityManager.createNativeQuery("select * from core.price where price_id =" + contractPrice.getPriceId()).getResultList();

        Iterator listIterator = query.listIterator();

        while (listIterator.hasNext()) {

            Object[] object = (Object[]) listIterator.next();

            if (object[2] != null) {

                price.setPriceId(new Long((Integer.parseInt(object[0].toString()))));

                LocalDate startLocalDate = LocalDate.parse(object[7].toString().substring(0, 19), formatter);

                LocalDate createdLocalDate = LocalDate.parse(object[16].toString().substring(0, 19), formatter);

                LocalDate endLocalDate = null;

                if (object[8] != null && ((Timestamp) object[8]).toLocalDateTime().toLocalDate().getYear() < 100000)

                    endLocalDate = LocalDate.parse(object[8].toString().substring(0, 19), formatter);

                if (object[1] != null)

                    price.setBasePrice(new BigDecimal(object[1].toString()));

                price.setStartDt(startLocalDate);



                price.setEndDt(endLocalDate);

                if (object[2] != null)

                    price.setPercentBasedPrice(new BigDecimal(object[2].toString()));

                price.setItemId(Integer.parseInt(object[6].toString()));

                price.setCreatedDt(createdLocalDate);

            } else {

                entityManager.close();

                return null;

            }



        }

        entityManager.close();

        return price;



    }

    public Price findPriceSLABBASED(ContractPrice contractPrice) throws ParseException {
    EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        System.out.println("Inside ContractPrice() ");
        Price price = new Price();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
        System.out.println("Price ID --------" + contractPrice.getPriceId());
        //List<Query> query = entityManager.createQuery(" from Price price where price.basePrice is null AND price.percentBasedPrice is null").getResultList();

        List<Query> query = entityManager.createNativeQuery("select * from core.price where price_id =" + contractPrice.getPriceId()).getResultList();
        Iterator listIterator = query.listIterator();
        while (listIterator.hasNext()) {
            Object[] object = (Object[]) listIterator.next();
                price.setPriceId(new Long((Integer.parseInt(object[0].toString()))));
                LocalDate startLocalDate = LocalDate.parse(object[7].toString().substring(0, 19), formatter);
                LocalDate createdLocalDate = LocalDate.parse(object[16].toString().substring(0, 19), formatter);
                LocalDate endLocalDate = null;
                if (object[8] != null && ((Timestamp) object[8]).toLocalDateTime().toLocalDate().getYear() < 100000)
                    endLocalDate = LocalDate.parse(object[8].toString().substring(0, 19), formatter);
                if (object[1] != null)
                    price.setBasePrice(new BigDecimal(object[1].toString()));
                price.setStartDt(startLocalDate);
                price.setEndDt(endLocalDate);
                if (object[2] != null)
                    price.setPercentBasedPrice(new BigDecimal(object[2].toString()));
                price.setItemId(Integer.parseInt(object[6].toString()));
                price.setCreatedDt(createdLocalDate);
                entityManager.close();
                return price;
        }
        entityManager.close();
        return price;
    }




    public Price findPriceForDelete(ContractPrice contractPrice) throws ParseException {

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        System.out.println("Inside ContractPrice() ");

        Price price = new Price();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");

        System.out.println("Price ID --------" + contractPrice.getPriceId());



        //List<Query> query = entityManager.createQuery(" from Price price where price.basePrice is null AND price.percentBasedPrice is null").getResultList();

        List<Query> query = entityManager.createNativeQuery("select * from core.price where price_id =" + contractPrice.getPriceId()).getResultList();

        Iterator listIterator = query.listIterator();

        while (listIterator.hasNext()) {

            Object[] object = (Object[]) listIterator.next();

            price.setPriceId(new Long((Integer.parseInt(object[0].toString()))));

            LocalDate startLocalDate = LocalDate.parse(object[7].toString().substring(0, 19), formatter);

            LocalDate endLocalDate = null;



            price.setPriceId(new Long(object[0].toString()));

            if (null != object[1])

                price.setBasePrice(new BigDecimal(object[1].toString()));

            if (null != object[2])

                price.setPercentBasedPrice(new BigDecimal(object[2].toString()));

            price.setPriceDefTpCd(new Long(object[5].toString()));

            price.setItemId(Integer.parseInt(object[6].toString()));

            price.setStartDt(((Timestamp) object[7]).toLocalDateTime().toLocalDate());

            if (object[8] != null && ((Timestamp) object[8]).toLocalDateTime().toLocalDate().getYear() < 2100)

                endLocalDate = LocalDate.parse(object[8].toString().substring(0, 19), formatter);

            price.setPriceTpCd(new Long(object[9].toString()));

            if (object[10] != null)

                price.setPercentageAttributeTpCd(new Long(object[10].toString()));

            if (object[11] != null)

                price.setPriceCalcTpCd(new Long(object[11].toString()));



            price.setStartDt(startLocalDate);

            price.setEndDt(endLocalDate);

            if (object[2] != null)

                price.setPercentBasedPrice(new BigDecimal(object[2].toString()));





        }

        entityManager.close();

        return price;



    }



    public String validatePriceDuplicacy(String price1, String price2) {



        String result = null;

        Object[] temp = null;

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        System.out.println("Inside ContractPrice() ");

        StringBuilder builder = new StringBuilder();

        //List<Query> query = entityManager.createQuery(" from Price price where price.basePrice is null AND price.percentBasedPrice is null").getResultList();

        List<Query> query = entityManager.createNativeQuery("select * from core.price where price_id in ( " + price1 + "," + price2 + " )").getResultList();

        Iterator listIterator = query.listIterator();

        while (listIterator.hasNext()) {

            Object[] object = (Object[]) listIterator.next();

            System.out.println(object[0]);

            if (temp != null) {

                if ((((Timestamp) object[7]).compareTo(((Timestamp) temp[7])) == 0) && (((Timestamp) object[8]).compareTo(((Timestamp) temp[8])) == 0)) {

                    result = price1 + " and " + price2 + " are same ";

                }

            } else {

                temp = object;

            }

        }

        System.out.println(" Duplicate entries :  " + builder);

        entityManager.close();

        return result;

    }



    public void findContractPrice() {



        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        System.out.println("Inside ContractPrice() ");

        StringBuilder builder = new StringBuilder();

        //List<Query> query = entityManager.createQuery(" from Price price where price.basePrice is null AND price.percentBasedPrice is null").getResultList();

        List<Query> query = entityManager.createNativeQuery("select price_id, item_id_dup from core.contractprice where appl_journey_tp_cd = 2").getResultList();

        Iterator listIterator = query.listIterator();

        while (listIterator.hasNext()) {

            Object[] object = (Object[]) listIterator.next();

            Integer priceId = Integer.parseInt(object[0].toString());

            Integer itemId = Integer.parseInt(object[1].toString());



            updateItemIdInPrice(priceId, itemId);



        }

        entityManager.close();

    }



    public void updateItemIdInPrice(Integer priceId, Integer itemId) {



        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        entityManager.getTransaction().begin();

        System.out.println("Inside fetchDataToContractRole() ");

        //List<Query> query = entityManager.createQuery(" from Price price where price.basePrice is null AND price.percentBasedPrice is null").getResultList();

        Query query = entityManager.createNativeQuery("update core.price set item_id = " + itemId + " where price_id = " + priceId);

        query.executeUpdate();

        entityManager.getTransaction().commit();

        entityManager.close();

    }



    public Cdcustomcountryroutetp findCdcustomcountryroutetp(String route) {

        System.out.println("Inside findCdcustomcountryroutetp() ");



        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Cdcustomcountryroutetp> query = builder.createQuery(Cdcustomcountryroutetp.class);

            Root<Cdcustomcountryroutetp> root = query.from(Cdcustomcountryroutetp.class);

            Predicate zoneNamePredicate = builder.equal(root.get("routeName"), route);

            query.where(zoneNamePredicate);

            query.select(root);

            TypedQuery<Cdcustomcountryroutetp> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public List<Contractdump> findCustomerInDump(String customerNumber) throws DataAccessException {

        System.out.println("Inside findCustomerBranch() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Contractdump> query = builder.createQuery(Contractdump.class);

            Root<Contractdump> root = query.from(Contractdump.class);

            query.select(root);

            Predicate customerBranchCheck = builder.equal(root.get("customerNumber"), customerNumber);



            query.where(customerBranchCheck);

            TypedQuery<Contractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    // This method filers Organization based records only.

    public Map<String, ArrayList<Deltacontractdump>> filterDeltaAgrementsByOrganization(List<Deltacontractdump> deltalist) {



        Map<String, ArrayList<Deltacontractdump>> map = new HashMap<String, ArrayList<Deltacontractdump>>();

        for (Deltacontractdump deltacontractdump : deltalist) {

            if (map.containsKey(deltacontractdump.getOrganizationNumber()))

                map.get(deltacontractdump.getOrganizationNumber()).add(deltacontractdump);

            else {

                ArrayList<Deltacontractdump> list = new ArrayList<Deltacontractdump>();

                list.add(deltacontractdump);

                map.put(deltacontractdump.getOrganizationNumber(), list);

            }

        }

        return map;

    }



    public void vaildateOrganizationStatueInPEDB(String organizationNumber) {



        Party existParentParty = findParentParty(organizationNumber);





    }





    public Contractdump searchDiscountLineInDump(Deltacontractdump deltacontractdump) {

        System.out.println("Inside getServiceTemp() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Contractdump> query = builder.createQuery(Contractdump.class);

            Root<Contractdump> root = query.from(Contractdump.class);

            query.select(root);

            Predicate orgNumberPredicate = builder.equal(root.get("organizationNumber"), deltacontractdump.getOrganizationNumber());

            Predicate orgNamePredicate = builder.equal(root.get("organizationName"), deltacontractdump.getOrganizationName());

            Predicate customerNumberPredicate = builder.equal(root.get("customerNumber"), deltacontractdump.getCustomerNumber());

            Predicate customerNamePredicate = builder.equal(root.get("customerName"), deltacontractdump.getCustomerName());

            Predicate prodNoPredicate = builder.equal(root.get("ProdNo"), deltacontractdump.getProdNo());



            Predicate routeFromPredicate = null;

            if (null != deltacontractdump.getRouteFrom())

                routeFromPredicate = builder.equal(root.get("RouteFrom"), deltacontractdump.getRouteFrom());

            else

                routeFromPredicate = builder.isNull(root.get("RouteFrom"));



            Predicate routeToPredicate = null;

            if (null != deltacontractdump.getRouteTo())

                routeToPredicate = builder.equal(root.get("RouteTo"), deltacontractdump.getRouteTo());

            else

                routeToPredicate = builder.isNull(root.get("RouteTo"));



            Predicate routetypePredicate = null;

            if (null != deltacontractdump.getRouteType())

                routetypePredicate = builder.equal(root.get("routetype"), deltacontractdump.getRouteType());

            else

                routetypePredicate = builder.isNull(root.get("routetype"));



            Predicate fromDatePredicate = builder.equal(root.get("FromDate"), deltacontractdump.getFromDate());

            Predicate toDatePredicate = builder.equal(root.get("ToDate"), deltacontractdump.getToDate());

            Predicate basepricePredicate = builder.equal(root.get("basePrice"), deltacontractdump.getBasePrice());

            Predicate currPredicate = builder.equal(root.get("Curr"), deltacontractdump.getCurr());



            Predicate dsclimcdPredicate = null;

            if (null != deltacontractdump.getDscLimCd())

                dsclimcdPredicate = builder.equal(root.get("DscLimCd"), deltacontractdump.getDscLimCd().toString());

            else

                dsclimcdPredicate = builder.isNull(root.get("DscLimCd"));



            Predicate kgtillPredicate = null;

            if (null != deltacontractdump.getKgTill())

                kgtillPredicate = builder.equal(root.get("KgTill"), deltacontractdump.getKgTill());

            else

                kgtillPredicate = builder.isNull(root.get("KgTill"));



            Predicate discLmtFromPredicate = null;

            if (null != deltacontractdump.getDiscLmtFrom())

                discLmtFromPredicate = builder.equal(root.get("DiscLmtFrom"), deltacontractdump.getDiscLmtFrom());

            else

                discLmtFromPredicate = builder.isNull(root.get("DiscLmtFrom"));



            Predicate pricePredicate = null;

            if (null != deltacontractdump.getPrice())

                pricePredicate = builder.equal(root.get("price"), deltacontractdump.getPrice());

            else

                pricePredicate = builder.isNull(root.get("price"));



            Predicate zoneTypePredicate = null;

            if (null != deltacontractdump.getZoneType())

                zoneTypePredicate = builder.equal(root.get("zoneType"), deltacontractdump.getZoneType());

            else

                zoneTypePredicate = builder.isNull(root.get("zoneType"));





            Predicate predicates = builder.and(orgNumberPredicate, customerNumberPredicate, prodNoPredicate, routeFromPredicate,

                    routeToPredicate, routetypePredicate, fromDatePredicate, toDatePredicate, basepricePredicate,

                    currPredicate, kgtillPredicate, discLmtFromPredicate, pricePredicate, zoneTypePredicate);



            query.where(predicates);



            TypedQuery<Contractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public Percentagebaseddump searchDiscountLineInPercentBasedDump(Percentagebaseddeltadump percentagebaseddeltadump) {

        System.out.println("Inside searchDiscountLineInPercentBasedDump() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Percentagebaseddump> query = builder.createQuery(Percentagebaseddump.class);

            Root<Percentagebaseddump> root = query.from(Percentagebaseddump.class);

            query.select(root);

            Predicate orgNumberPredicate = builder.equal(root.get("parentCustomerNumber"), percentagebaseddeltadump.getParentCustomerNumber());

            Predicate orgNamePredicate = builder.equal(root.get("parentCustomerName"), percentagebaseddeltadump.getParentCustomerName());

            Predicate customerNumberPredicate = builder.equal(root.get("customerNumber"), percentagebaseddeltadump.getCustomerNumber());

            Predicate customerNamePredicate = builder.equal(root.get("customerName"), percentagebaseddeltadump.getCustomerName());

            Predicate prodNoPredicate = builder.equal(root.get("prodno"), percentagebaseddeltadump.getProdno());



            Predicate routeFromPredicate = null;

            if (null != percentagebaseddeltadump.getFromLocation())

                routeFromPredicate = builder.equal(root.get("fromLocation"), percentagebaseddeltadump.getFromLocation());

            else

                routeFromPredicate = builder.isNull(root.get("fromLocation"));



            Predicate routeToPredicate = null;

            if (null != percentagebaseddeltadump.getToLocation())

                routeToPredicate = builder.equal(root.get("toLocation"), percentagebaseddeltadump.getToLocation());

            else

                routeToPredicate = builder.isNull(root.get("toLocation"));



            Predicate routetypePredicate = null;

            if (null != percentagebaseddeltadump.getRouteType())

                routetypePredicate = builder.equal(root.get("routeType"), percentagebaseddeltadump.getRouteType());

            else

                routetypePredicate = builder.isNull(root.get("routeType"));



            Predicate fromDatePredicate = builder.equal(root.get("startdate"), percentagebaseddeltadump.getStartdate());

            Predicate toDatePredicate = builder.equal(root.get("enddate"), percentagebaseddeltadump.getEnddate());

            Predicate percentpricePredicate = builder.equal(root.get("precentageDiscount"), percentagebaseddeltadump.getPrecentageDiscount());



            Predicate zoneTypePredicate = null;

            if (null != percentagebaseddeltadump.getZoneType())

                zoneTypePredicate = builder.equal(root.get("zoneType"), percentagebaseddeltadump.getZoneType());

            else

                zoneTypePredicate = builder.isNull(root.get("zoneType"));



            Predicate predicates = builder.and(orgNumberPredicate, customerNumberPredicate, prodNoPredicate, routeFromPredicate,

                    routeToPredicate, routetypePredicate, fromDatePredicate, toDatePredicate, percentpricePredicate,

                    zoneTypePredicate);



            query.where(predicates);



            TypedQuery<Percentagebaseddump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getSingleResult();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }





    public Contractdump searchDiscountLineInDumpXX(Deltacontractdump dd) {



        System.out.println("Inside searchDiscountLineInDump() ");

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();



        StringBuilder builder = new StringBuilder();

        List<Query> query = entityManager.createNativeQuery("select * from core.contractdump where organization_number = " + dd.getOrganizationNumber() + " " +

                "and organization_name=" + dd.getOrganizationName() + " and customer_number = " + dd.getCustomerNumber() + " and customer_name=" + dd.getCustomerName()

                + " and prodno= " + dd.getProdNo() + " and routefrom=" + dd.getRouteFrom() + " and routeto=" + dd.getRouteTo() + " and startdate= " + dd.getFromDate()

                + " and enddate = " + dd.getToDate() + " and baseprice = " + dd.getBasePrice() + " and currency=" + dd.getCurr() + " and ").getResultList();

        Iterator listIterator = query.listIterator();

        while (listIterator.hasNext()) {

            Object[] object = (Object[]) listIterator.next();

            ContractPrice contractPrice = new ContractPrice();

            contractPrice.setPriceId(new Long(object[0].toString()));

            contractPrice.setPriceId(new Long(object[0].toString()));

            contractPrice.setContractcomponentId(Integer.parseInt(object[5].toString()));

            contractPrice.setItemIdDup(new Long(object[9].toString()));

            if (null != object[11])

                contractPrice.setFromCountry(Integer.parseInt(object[11].toString()));

            if (null != object[12])

                contractPrice.setFromPostalCode(object[12].toString());

            if (null != object[13])

                contractPrice.setToCountry(Integer.parseInt(object[13].toString()));

            if (null != object[14])

                contractPrice.setFromPostalCode(object[14].toString());

            if (null != object[15])

                contractPrice.setToCountryZoneCountFrom(Integer.parseInt(object[15].toString()));

            if (null != object[16])

                contractPrice.setToCountryZoneCountTo(Integer.parseInt(object[16].toString()));

            contractPrice.setApplJourneyTpCd(Integer.parseInt(object[18].toString()));

            if (null != object[19])

                contractPrice.setZoneId(Integer.parseInt(object[19].toString()));

            if (null != object[21])

                contractPrice.setFromRouteId(Integer.parseInt(object[21].toString()));

            if (null != object[22])

                contractPrice.setToRouteId(Integer.parseInt(object[22].toString()));



        }

        System.out.println(" Duplicate entries :  " + builder);

        entityManager.close();

        return null;



    }



    public List<Contractdump> searchDiscountLineByCustomer(Deltacontractdump deltacontractdump) {

        System.out.println("Inside getServiceTemp() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Contractdump> query = builder.createQuery(Contractdump.class);

            Root<Contractdump> root = query.from(Contractdump.class);

            query.select(root);

            Predicate orgNumberPredicate = builder.equal(root.get("organizationNumber"), deltacontractdump.getOrganizationNumber());

            Predicate customerNumberPredicate = builder.equal(root.get("customerNumber"), deltacontractdump.getCustomerNumber());

            query.where(orgNumberPredicate, customerNumberPredicate);



            TypedQuery<Contractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public List<Percentagebaseddump> searchDiscountLineByCustomerPERCENT(Percentagebaseddeltadump percentagebaseddeltadump) {

        System.out.println("Inside searchDiscountLineByCustomerPERCENT() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Percentagebaseddump> query = builder.createQuery(Percentagebaseddump.class);

            Root<Percentagebaseddump> root = query.from(Percentagebaseddump.class);

            query.select(root);

            Predicate orgNumberPredicate = builder.equal(root.get("parentCustomerNumber"), percentagebaseddeltadump.getParentCustomerNumber());

            Predicate customerNumberPredicate = builder.equal(root.get("customerNumber"), percentagebaseddeltadump.getCustomerNumber());

            query.where(orgNumberPredicate, customerNumberPredicate);



            TypedQuery<Percentagebaseddump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public List<Contractdump> searchDiscountLineByProdNo(Deltacontractdump deltacontractdump) {

        System.out.println("Inside getServiceTemp() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Contractdump> query = builder.createQuery(Contractdump.class);

            Root<Contractdump> root = query.from(Contractdump.class);

            query.select(root);

            Predicate orgNumberPredicate = builder.equal(root.get("organizationNumber"), deltacontractdump.getOrganizationNumber());

            Predicate customerNumberPredicate = builder.equal(root.get("customerNumber"), deltacontractdump.getCustomerNumber());

            Predicate prodNoPredicate = builder.equal(root.get("ProdNo"), deltacontractdump.getProdNo());



            query.where(orgNumberPredicate, customerNumberPredicate, prodNoPredicate);



            TypedQuery<Contractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public List<Percentagebaseddump> searchDiscountLineByProdNoPERCENT(Percentagebaseddeltadump percentagebaseddeltadump) {

        System.out.println("Inside getServiceTemp() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Percentagebaseddump> query = builder.createQuery(Percentagebaseddump.class);

            Root<Percentagebaseddump> root = query.from(Percentagebaseddump.class);

            query.select(root);

            Predicate orgNumberPredicate = builder.equal(root.get("parentCustomerNumber"), percentagebaseddeltadump.getParentCustomerNumber());

            Predicate customerNumberPredicate = builder.equal(root.get("customerNumber"), percentagebaseddeltadump.getCustomerNumber());

            Predicate prodNoPredicate = builder.equal(root.get("prodno"), percentagebaseddeltadump.getProdno());



            query.where(orgNumberPredicate, customerNumberPredicate, prodNoPredicate);



            TypedQuery<Percentagebaseddump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public List<Percentagebaseddump> searchDiscountLineByRoutesPERCENT(Percentagebaseddeltadump percentagebaseddeltadump) {

        System.out.println("Inside searchDiscountLineByRoutesPERCENT() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Percentagebaseddump> query = builder.createQuery(Percentagebaseddump.class);

            Root<Percentagebaseddump> root = query.from(Percentagebaseddump.class);

            query.select(root);

            Predicate orgNumberPredicate = builder.equal(root.get("parentCustomerNumber"), percentagebaseddeltadump.getParentCustomerNumber());

            Predicate customerNumberPredicate = builder.equal(root.get("customerNumber"), percentagebaseddeltadump.getCustomerNumber());

            Predicate prodNoPredicate = builder.equal(root.get("prodno"), percentagebaseddeltadump.getProdno());

            Predicate routeFromPredicate = null;

            if (null != percentagebaseddeltadump.getFromLocation())

                routeFromPredicate = builder.equal(root.get("fromLocation"), percentagebaseddeltadump.getFromLocation());

            else

                routeFromPredicate = builder.isNull(root.get("fromLocation"));



            Predicate routeToPredicate = null;

            if (null != percentagebaseddeltadump.getToLocation())

                routeToPredicate = builder.equal(root.get("toLocation"), percentagebaseddeltadump.getToLocation());

            else

                routeToPredicate = builder.isNull(root.get("toLocation"));



            Predicate routetypePredicate = null;

            if (null != percentagebaseddeltadump.getRouteType())

                routetypePredicate = builder.equal(root.get("routeType"), percentagebaseddeltadump.getRouteType());

            else

                routetypePredicate = builder.isNull(root.get("routeType"));



            query.where(orgNumberPredicate, customerNumberPredicate, prodNoPredicate, routeFromPredicate, routeToPredicate, routetypePredicate);



            TypedQuery<Percentagebaseddump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public List<Percentagebaseddump> searchDiscountLineByDatesPERCENT(Percentagebaseddeltadump percentagebaseddeltadump) {

        System.out.println("Inside searchDiscountLineByDatesPERCENT() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Percentagebaseddump> query = builder.createQuery(Percentagebaseddump.class);

            Root<Percentagebaseddump> root = query.from(Percentagebaseddump.class);

            query.select(root);

            Predicate orgNumberPredicate = builder.equal(root.get("parentCustomerNumber"), percentagebaseddeltadump.getParentCustomerNumber());

            Predicate customerNumberPredicate = builder.equal(root.get("customerNumber"), percentagebaseddeltadump.getCustomerNumber());

            Predicate prodNoPredicate = builder.equal(root.get("prodno"), percentagebaseddeltadump.getProdno());

            Predicate routeFromPredicate = null;

            if (null != percentagebaseddeltadump.getFromLocation())

                routeFromPredicate = builder.equal(root.get("fromLocation"), percentagebaseddeltadump.getFromLocation());

            else

                routeFromPredicate = builder.isNull(root.get("fromLocation"));



            Predicate routeToPredicate = null;

            if (null != percentagebaseddeltadump.getToLocation())

                routeToPredicate = builder.equal(root.get("toLocation"), percentagebaseddeltadump.getToLocation());

            else

                routeToPredicate = builder.isNull(root.get("toLocation"));



            Predicate routetypePredicate = null;

            if (null != percentagebaseddeltadump.getRouteType())

                routetypePredicate = builder.equal(root.get("routeType"), percentagebaseddeltadump.getRouteType());

            else

                routetypePredicate = builder.isNull(root.get("routeType"));



            Predicate fromDatePredicate = builder.equal(root.get("startdate"), percentagebaseddeltadump.getStartdate());

            Predicate toDatePredicate = builder.equal(root.get("enddate"), percentagebaseddeltadump.getEnddate());



            query.where(orgNumberPredicate, customerNumberPredicate, prodNoPredicate, routeFromPredicate, routeToPredicate, routetypePredicate

                    , fromDatePredicate, toDatePredicate);



            TypedQuery<Percentagebaseddump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public List<Percentagebaseddump> searchDiscountLineByPricePERCENT(Percentagebaseddeltadump percentagebaseddeltadump) {

        System.out.println("Inside searchDiscountLineByDatesPERCENT() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Percentagebaseddump> query = builder.createQuery(Percentagebaseddump.class);

            Root<Percentagebaseddump> root = query.from(Percentagebaseddump.class);

            query.select(root);

            Predicate orgNumberPredicate = builder.equal(root.get("parentCustomerNumber"), percentagebaseddeltadump.getParentCustomerNumber());

            Predicate customerNumberPredicate = builder.equal(root.get("customerNumber"), percentagebaseddeltadump.getCustomerNumber());

            Predicate prodNoPredicate = builder.equal(root.get("prodno"), percentagebaseddeltadump.getProdno());

            Predicate routeFromPredicate = null;

            if (null != percentagebaseddeltadump.getFromLocation())

                routeFromPredicate = builder.equal(root.get("fromLocation"), percentagebaseddeltadump.getFromLocation());

            else

                routeFromPredicate = builder.isNull(root.get("fromLocation"));



            Predicate routeToPredicate = null;

            if (null != percentagebaseddeltadump.getToLocation())

                routeToPredicate = builder.equal(root.get("toLocation"), percentagebaseddeltadump.getToLocation());

            else

                routeToPredicate = builder.isNull(root.get("toLocation"));



            Predicate routetypePredicate = null;

            if (null != percentagebaseddeltadump.getRouteType())

                routetypePredicate = builder.equal(root.get("routeType"), percentagebaseddeltadump.getRouteType());

            else

                routetypePredicate = builder.isNull(root.get("routeType"));



            Predicate fromDatePredicate = builder.equal(root.get("startdate"), percentagebaseddeltadump.getStartdate());

            Predicate toDatePredicate = builder.equal(root.get("enddate"), percentagebaseddeltadump.getEnddate());

            Predicate percentpricePredicate = builder.equal(root.get("precentageDiscount"), percentagebaseddeltadump.getPrecentageDiscount());



            query.where(orgNumberPredicate, customerNumberPredicate, prodNoPredicate, routeFromPredicate, routeToPredicate, routetypePredicate

                    , fromDatePredicate, toDatePredicate, percentpricePredicate);



            TypedQuery<Percentagebaseddump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public List<Contractdump> searchDiscountLineByRoutes(Deltacontractdump deltacontractdump) {

        System.out.println("Inside getServiceTemp() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Contractdump> query = builder.createQuery(Contractdump.class);

            Root<Contractdump> root = query.from(Contractdump.class);

            query.select(root);

            Predicate orgNumberPredicate = builder.equal(root.get("organizationNumber"), deltacontractdump.getOrganizationNumber());

            Predicate customerNumberPredicate = builder.equal(root.get("customerNumber"), deltacontractdump.getCustomerNumber());

            Predicate prodNoPredicate = builder.equal(root.get("ProdNo"), deltacontractdump.getProdNo());

            Predicate routeFromPredicate = null;

            if (null != deltacontractdump.getRouteFrom())

                routeFromPredicate = builder.equal(root.get("RouteFrom"), deltacontractdump.getRouteFrom());

            else

                routeFromPredicate = builder.isNull(root.get("RouteFrom"));



            Predicate routeToPredicate = null;

            if (null != deltacontractdump.getRouteTo())

                routeToPredicate = builder.equal(root.get("RouteTo"), deltacontractdump.getRouteTo());

            else

                routeToPredicate = builder.isNull(root.get("RouteTo"));



            Predicate routetypePredicate = null;

            if (null != deltacontractdump.getRouteType())

                routetypePredicate = builder.equal(root.get("routetype"), deltacontractdump.getRouteType());

            else

                routetypePredicate = builder.isNull(root.get("routetype"));

            Predicate zoneTypePredicate = null;

            if (null != deltacontractdump.getZoneType())

                zoneTypePredicate = builder.equal(root.get("zoneType"), deltacontractdump.getZoneType());

            else

                zoneTypePredicate = builder.isNull(root.get("zoneType"));





            query.where(orgNumberPredicate, customerNumberPredicate, prodNoPredicate, routeFromPredicate,

                    routeToPredicate, routetypePredicate, zoneTypePredicate);



            TypedQuery<Contractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }





    public List<Contractdump> searchDiscountLineByDates(Deltacontractdump deltacontractdump) {

        System.out.println("Inside searchDiscountLineInDumpTillDates() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Contractdump> query = builder.createQuery(Contractdump.class);

            Root<Contractdump> root = query.from(Contractdump.class);

            query.select(root);

            Predicate orgNumberPredicate = builder.equal(root.get("organizationNumber"), deltacontractdump.getOrganizationNumber());

            Predicate customerNumberPredicate = builder.equal(root.get("customerNumber"), deltacontractdump.getCustomerNumber());

            Predicate prodNoPredicate = builder.equal(root.get("ProdNo"), deltacontractdump.getProdNo());

            Predicate routeFromPredicate = null;

            if (null != deltacontractdump.getRouteFrom())

                routeFromPredicate = builder.equal(root.get("RouteFrom"), deltacontractdump.getRouteFrom());

            else

                routeFromPredicate = builder.isNull(root.get("RouteFrom"));



            Predicate routeToPredicate = null;

            if (null != deltacontractdump.getRouteTo())

                routeToPredicate = builder.equal(root.get("RouteTo"), deltacontractdump.getRouteTo());

            else

                routeToPredicate = builder.isNull(root.get("RouteTo"));



            Predicate routetypePredicate = null;

            if (null != deltacontractdump.getRouteType())

                routetypePredicate = builder.equal(root.get("routetype"), deltacontractdump.getRouteType());

            else

                routetypePredicate = builder.isNull(root.get("routetype"));

            Predicate zoneTypePredicate = null;

            if (null != deltacontractdump.getZoneType())

                zoneTypePredicate = builder.equal(root.get("zoneType"), deltacontractdump.getZoneType());

            else

                zoneTypePredicate = builder.isNull(root.get("zoneType"));

            Predicate fromDatePredicate = builder.equal(root.get("FromDate"), deltacontractdump.getFromDate());

            Predicate toDatePredicate = builder.equal(root.get("ToDate"), deltacontractdump.getToDate());





            query.where(orgNumberPredicate, customerNumberPredicate, prodNoPredicate, routeFromPredicate,

                    routeToPredicate, routetypePredicate, zoneTypePredicate, fromDatePredicate, toDatePredicate);



            TypedQuery<Contractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    public List<Contractdump> searchDiscountLineByPrice(Deltacontractdump deltacontractdump) {

        System.out.println("Inside searchDiscountLineInDumpTillDates() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Contractdump> query = builder.createQuery(Contractdump.class);

            Root<Contractdump> root = query.from(Contractdump.class);

            query.select(root);

            Predicate orgNumberPredicate = builder.equal(root.get("organizationNumber"), deltacontractdump.getOrganizationNumber());

            Predicate customerNumberPredicate = builder.equal(root.get("customerNumber"), deltacontractdump.getCustomerNumber());

            Predicate prodNoPredicate = builder.equal(root.get("ProdNo"), deltacontractdump.getProdNo());

            Predicate routeFromPredicate = null;

            if (null != deltacontractdump.getRouteFrom())

                routeFromPredicate = builder.equal(root.get("RouteFrom"), deltacontractdump.getRouteFrom());

            else

                routeFromPredicate = builder.isNull(root.get("RouteFrom"));



            Predicate routeToPredicate = null;

            if (null != deltacontractdump.getRouteTo())

                routeToPredicate = builder.equal(root.get("RouteTo"), deltacontractdump.getRouteTo());

            else

                routeToPredicate = builder.isNull(root.get("RouteTo"));



            Predicate routetypePredicate = null;

            if (null != deltacontractdump.getRouteType())

                routetypePredicate = builder.equal(root.get("routetype"), deltacontractdump.getRouteType());

            else

                routetypePredicate = builder.isNull(root.get("routetype"));

            Predicate zoneTypePredicate = null;

            if (null != deltacontractdump.getZoneType())

                zoneTypePredicate = builder.equal(root.get("zoneType"), deltacontractdump.getZoneType());

            else

                zoneTypePredicate = builder.isNull(root.get("zoneType"));

            Predicate fromDatePredicate = builder.equal(root.get("FromDate"), deltacontractdump.getFromDate());

            Predicate toDatePredicate = builder.equal(root.get("ToDate"), deltacontractdump.getToDate());



            Predicate basepricePredicate = builder.equal(root.get("basePrice"), deltacontractdump.getBasePrice());

            Predicate currPredicate = builder.equal(root.get("Curr"), deltacontractdump.getCurr());



            Predicate dsclimcdPredicate = null;

            if (null != deltacontractdump.getDscLimCd())

                dsclimcdPredicate = builder.equal(root.get("DscLimCd"), deltacontractdump.getDscLimCd().toString());

            else

                dsclimcdPredicate = builder.isNull(root.get("DscLimCd"));



            Predicate kgtillPredicate = null;

            if (null != deltacontractdump.getKgTill())

                kgtillPredicate = builder.equal(root.get("KgTill"), deltacontractdump.getKgTill());

            else

                kgtillPredicate = builder.isNull(root.get("KgTill"));



            Predicate discLmtFromPredicate = null;

            if (null != deltacontractdump.getDiscLmtFrom())

                discLmtFromPredicate = builder.equal(root.get("DiscLmtFrom"), deltacontractdump.getDiscLmtFrom());

            else

                discLmtFromPredicate = builder.isNull(root.get("DiscLmtFrom"));



            Predicate pricePredicate = null;

            if (null != deltacontractdump.getPrice())

                pricePredicate = builder.equal(root.get("price"), deltacontractdump.getPrice());

            else

                pricePredicate = builder.isNull(root.get("price"));



            query.where(orgNumberPredicate, customerNumberPredicate, prodNoPredicate, routeFromPredicate,

                    routeToPredicate, routetypePredicate, zoneTypePredicate, fromDatePredicate, toDatePredicate,

                    basepricePredicate, currPredicate, dsclimcdPredicate, kgtillPredicate, discLmtFromPredicate, pricePredicate);



            TypedQuery<Contractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return null;

        } catch (HibernateException he) {

            he.printStackTrace();

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



//        public boolean compareSlabbasedPrice(Long priceId1, Long priceId2){

//            SlabBasedPrice slabBasedPrice1 = findSlabbasedPrice(priceId1);

//            SlabBasedPrice slabBasedPrice2 = findSlabbasedPrice(priceId2);

//            if(null != slabBasedPrice1 && null!= slabBasedPrice2){

//                Set<Long> set1 = slabBasedPrice1.getSlabBasedPriceEntryMap().keySet();

//                Set<Long> set2 = slabBasedPrice2.getSlabBasedPriceEntryMap().keySet();

//                for(Long slab1: set1){

//                       SlabBasedPriceEntry entry1 = slabBasedPrice1.getSlabBasedPriceEntryMap().get(slab1);

//                       SlabBasedPriceEntry entry2 = slabBasedPrice2.getSlabBasedPriceEntryMap().get(slab1);

//                       if(entry1.getPriceBasisLowerBound().equals(entry2.getPriceBasisLowerBound()) &&

//                               entry1.getPriceBasisUpperBound().equals(entry2.getPriceBasisUpperBound()) &&

//                               entry1.getPriceValue().equals(entry2.getPriceValue())){

//                                System.out.println("entry  matched ");

//                       }else{

//                           System.out.println("entry not  matched for slabbasedentry :: "+ entry1.getSlabBasedPriceEntryId()

//                           +" :::: "+ entry2.getSlabBasedPriceEntryId());

//                           return false;

//                       }

//                }

//            }else{

//                System.out.println("not slabbased records. check again these price ids- "+ priceId1 +" :::: "+ priceId2);

//                System.exit(1);

//            }

//            return true;

//        }

//

//    }



//    public boolean compareSlabbasedPrice(Long priceId1, Long priceId2) {
//
//        SlabBasedPrice slabBasedPrice1 = findSlabbasedPrice(priceId1);
//
//        SlabBasedPrice slabBasedPrice2 = findSlabbasedPrice(priceId2);
//
//        if (null != slabBasedPrice1 && null != slabBasedPrice2) {
//
//            SlabBasedPriceEntry slabBasedPriceEntry1 = findSlabbasedPriceEntry(slabBasedPrice1.getSlabBasedPriceId());
//
//            SlabBasedPriceEntry slabBasedPriceEntry2 = findSlabbasedPriceEntry(slabBasedPrice2.getSlabBasedPriceId());
//
//            if (slabBasedPriceEntry1.getPriceBasisLowerBound().equals(slabBasedPriceEntry2.getPriceBasisLowerBound())
//
//                    && slabBasedPriceEntry1.getPriceBasisUpperBound().equals(slabBasedPriceEntry2.getPriceBasisUpperBound())
//
//                    && slabBasedPriceEntry1.getPriceValue().equals(slabBasedPriceEntry2.getPriceValue())) {
//
//                System.out.println("Entry matched ");
//
//                return true;
//
//            } else {
//
//                System.out.println("entry not matched  SLABBASED PRICE ID::  " + slabBasedPrice1.getSlabBasedPriceId() + " :::: " + slabBasedPrice2.getSlabBasedPriceId());
//
//                return false;
//
//            }
//
//        }
//
//        return false;
//
//    }



    @org.springframework.data.jpa.repository.Query

    public List<Deltacontractdump> findDeltaContractdumpsWithPriceID(String fileCoutnry) throws DataAccessException {

        System.out.println("Inside findDeltaContractdumpsWithPriceID() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Deltacontractdump> query = builder.createQuery(Deltacontractdump.class);

            Root<Deltacontractdump> root = query.from(Deltacontractdump.class);

            query.select(root);

            Predicate namePredicate = builder.equal(root.get("updated"), false);

            Predicate countryPredicate = builder.equal(root.get("fileCountry"), fileCoutnry);

            Predicate enablePredicate = builder.equal(root.get("enabled"), true);

            Predicate pricePredicate = builder.isNotNull(root.get("priceId"));

            Predicate predicates = builder.and(namePredicate, enablePredicate, countryPredicate);

            query.where(predicates);

            TypedQuery<Deltacontractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return Collections.emptyList();

        } catch (HibernateException he) {

            he.printStackTrace();

            System.exit(1);

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }





    @org.springframework.data.jpa.repository.Query

    public List<Deltacontractdump> findDeltaContractdumpsWithPriceIDForDelete(String fileCoutnry) throws DataAccessException {

        System.out.println("Inside findDeltaContractdumpsWithPriceID() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Deltacontractdump> query = builder.createQuery(Deltacontractdump.class);

            Root<Deltacontractdump> root = query.from(Deltacontractdump.class);

            query.select(root);

            Predicate namePredicate = builder.equal(root.get("updated"), true);

            Predicate countryPredicate = builder.equal(root.get("fileCountry"), fileCoutnry);

            Predicate enablePredicate = builder.equal(root.get("enabled"), true);

            Predicate pricePredicate = builder.isNotNull(root.get("priceId"));

            Predicate priceCriteriaPredicate = builder.equal(root.get("remark"), "New Prices");

            //      Predicate overlapPredicate = builder.notEqual(root.get("remark"),"Overlap with anyone of dates");

            Predicate predicates = builder.and(namePredicate, enablePredicate, countryPredicate, pricePredicate, priceCriteriaPredicate);

            query.where(predicates);

            TypedQuery<Deltacontractdump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return Collections.emptyList();

        } catch (HibernateException he) {

            he.printStackTrace();

            System.exit(1);

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }



    @org.springframework.data.jpa.repository.Query

    public List<Percentagebaseddeltadump> findDeltaContractdumpsWithPriceIDForDeletePERCENT(String fileCoutnry) throws DataAccessException {

        System.out.println("Inside findDeltaContractdumpsWithPriceID() ");

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Percentagebaseddeltadump> query = builder.createQuery(Percentagebaseddeltadump.class);

            Root<Percentagebaseddeltadump> root = query.from(Percentagebaseddeltadump.class);

            query.select(root);

            Predicate namePredicate = builder.equal(root.get("updated"), true);

            Predicate countryPredicate = builder.equal(root.get("fileCountry"), fileCoutnry);

            Predicate enablePredicate = builder.equal(root.get("enabled"), true);

            Predicate pricePredicate = builder.isNotNull(root.get("priceId"));

            Predicate priceCriteriaPredicate = builder.equal(root.get("remark"), "New Prices");

            //      Predicate overlapPredicate = builder.notEqual(root.get("remark"),"Overlap with anyone of dates");

            Predicate predicates = builder.and(namePredicate, enablePredicate, countryPredicate, pricePredicate, priceCriteriaPredicate);

            query.where(predicates);

            TypedQuery<Percentagebaseddeltadump> typedQuery = em.createQuery(query);

            if (typedQuery.getResultStream().findAny().isPresent())

                return typedQuery.getResultList();

            else

                return Collections.emptyList();

        } catch (HibernateException he) {

            he.printStackTrace();

            System.exit(1);

            return null;

        } catch (Exception e) {

            e.printStackTrace();

            System.exit(1);

            return null;

        } finally {

            em.clear();

            em.close();

        }

    }
    

}