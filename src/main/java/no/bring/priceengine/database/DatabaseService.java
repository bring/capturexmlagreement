package no.bring.priceengine.database;

import java.io.ByteArrayOutputStream;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.orm.hibernate5.HibernateQueryException;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import cdh.CustomerModel;
import no.bring.priceengine.dao.Cdcustomcountryroutetp;
import no.bring.priceengine.dao.Cdhomedeliverytp;
import no.bring.priceengine.dao.Contract;
import no.bring.priceengine.dao.ContractComponent;
import no.bring.priceengine.dao.ContractPrice;
import no.bring.priceengine.dao.ContractRole;
import no.bring.priceengine.dao.Contractdump;
import no.bring.priceengine.dao.Contractdumpservice;
import no.bring.priceengine.dao.Country;
import no.bring.priceengine.dao.Customervolfactordump;
import no.bring.priceengine.dao.Deltacontractdump;
import no.bring.priceengine.dao.Item;
import no.bring.priceengine.dao.Party;
import no.bring.priceengine.dao.Percentagebaseddeltadump;
import no.bring.priceengine.dao.Percentagebaseddump;
import no.bring.priceengine.dao.Price;
import no.bring.priceengine.dao.SlabBasedPriceEntry;
import no.bring.priceengine.dao.Surchargedump;
import no.bring.priceengine.service.ExcelService;


@Service
public class DatabaseService {
    public static final String AUTOMATION_USER = "Automation";
    private final String UPDATE_DELTACONTRACTDUMP = "UPDATE core.deltacontractdump ";
//    public static final String DB_CONNECTION_DRIVER = "org.postgresql.Driver";
    public static final String UPDATE_CONTRACTPRICE_SQL = "UPDATE core.contractprice SET contractprice_processing_tp_cd=? WHERE price_id=?";
    public static final String UPDATE_CONTRACTPRICE_PRIORITY_SQL = "UPDATE core.contractprice SET priority=? WHERE price_id=?";
    private static final Integer SIGNER_ROLE_TP_CD = new Integer(1);
    private static final Integer USER_ROLE_TP_CD = new Integer(2);
    private static final Integer ROLE_DISCOUNT_DEFAULT_STATUS_ACTIVE = new Integer(1);
    //    private final String INSERT_PRICE_SQL = " INSERT INTO core.price( base_price, percent_based_price, price_lower_bound, price_upper_bound, price_def_tp_cd"
//            + " , item_id, start_dt, end_dt, price_tp_cd, percentage_attribute_tp_cd, last_update_dt, last_update_user"
//            + ", last_update_tx_id, created_dt, created_by_user, price_calc_tp_cd, price_alternative_item_id,price_per, price_per_attribute_tp_cd,price_per_attribute_val_adj)  "
//            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) returning price_id ";
    private static final String INFINITY_DATE = "Infinity";
    private static final String FROM_POSTAL_FULL_TO_POSTAL_FULL = "FROM_POSTAL_FULL_TO_POSTAL_FULL";
    private static final String FROM_POSTAL_PARTIAL3_TO_POSTAL_FULL = "FROM_POSTAL_PARTIAL3_TO_POSTAL_FULL";
    private static final String FROM_POSTAL_FULL_TO_POSTAL_PARTIAL3 = "FROM_POSTAL_FULL_TO_POSTAL_PARTIAL3";
    private static final String FROM_POSTAL_PARTIAL2_TO_POSTAL_FULL = "FROM_POSTAL_PARTIAL2_TO_POSTAL_FULL";
    private static final String FROM_POSTAL_FULL_TO_POSTAL_PARTIAL2 = "FROM_POSTAL_FULL_TO_POSTAL_PARTIAL2";
    private static final String FROM_POSTAL_PARTIAL1_TO_POSTAL_FULL = "FROM_POSTAL_PARTIAL1_TO_POSTAL_FULL";
    private static final String FROM_POSTAL_FULL_TO_POSTAL_PARTIAL1 = "FROM_POSTAL_FULL_TO_POSTAL_PARTIAL1";
    private static final String FROM_POSTAL_PARTIAL3_TO_POSTAL_PARTIAL3 = "FROM_POSTAL_PARTIAL3_TO_POSTAL_PARTIAL3";
    private static final String FROM_POSTAL_PARTIAL3_TO_POSTAL_PARTIAL2 = "FROM_POSTAL_PARTIAL3_TO_POSTAL_PARTIAL2";
    private static final String FROM_POSTAL_PARTIAL2_TO_POSTAL_PARTIAL3 = "FROM_POSTAL_PARTIAL2_TO_POSTAL_PARTIAL3";
    private static final String FROM_POSTAL_PARTIAL3_TO_POSTAL_PARTIAL1 = "FROM_POSTAL_PARTIAL3_TO_POSTAL_PARTIAL1";
    private static final String FROM_POSTAL_PARTIAL1_TO_POSTAL_PARTIAL3 = "FROM_POSTAL_PARTIAL1_TO_POSTAL_PARTIAL3";
    private static final String FROM_POSTAL_PARTIAL2_TO_POSTAL_PARTIAL2 = "FROM_POSTAL_PARTIAL2_TO_POSTAL_PARTIAL2";
    private static final String FROM_POSTAL_PARTIAL2_TO_POSTAL_PARTIAL1 = "FROM_POSTAL_PARTIAL2_TO_POSTAL_PARTIAL1";
    private static final String FROM_POSTAL_PARTIAL1_TO_POSTAL_PARTIAL2 = "FROM_POSTAL_PARTIAL1_TO_POSTAL_PARTIAL2";
    private static final String FROM_POSTAL_PARTIAL1_TO_POSTAL_PARTIAL1 = "FROM_POSTAL_PARTIAL1_TO_POSTAL_PARTIAL1";
    private static final String FROM_POSTAL_FULL_TO_POSTAL_NULL = "FROM_POSTAL_FULL_TO_POSTAL_NULL";
    private static final String FROM_POSTAL_NULL_TO_POSTAL_FULL = "FROM_POSTAL_NULL_TO_POSTAL_FULL";
    private static final String FROM_POSTAL_NULL_TO_POSTAL_PARTIAL3 = "FROM_POSTAL_NULL_TO_POSTAL_PARTIAL3";
    private static final String FROM_POSTAL_PARTIAL3_TO_POSTAL_NULL = "FROM_POSTAL_PARTIAL3_TO_POSTAL_NULL";
    private static final String FROM_POSTAL_NULL_TO_POSTAL_PARTIAL2 = "FROM_POSTAL_NULL_TO_POSTAL_PARTIAL2";
    private static final String FROM_POSTAL_PARTIAL2_TO_POSTAL_NULL = "FROM_POSTAL_PARTIAL2_TO_POSTAL_NULL";
    private static final String FROM_POSTAL_NULL_TO_POSTAL_PARTIAL1 = "FROM_POSTAL_NULL_TO_POSTAL_PARTIAL1";
    private static final String FROM_POSTAL_PARTIAL1_TO_POSTAL_NULL = "FROM_POSTAL_PARTIAL1_TO_POSTAL_NULL";
    private static final String FROM_ZONE_NOT_NULL_TO_ZONE_NOT_NULL = "FROM_ZONE_NOT_NULL_TO_ZONE_NOT_NULL";
    private static final String FROM_ZONE_NULL_TO_ZONE_NOT_NULL = "FROM_ZONE_NULL_TO_ZONE_NOT_NULL";
    private static final String FROM_ZONE_NOT_NULL_TO_ZONE_NULL = "FROM_ZONE_NOT_NULL_TO_ZONE_NULL";
    private static final String ONLY_FROM_TO_COUNTRY = "ONLY_FROM_TO_COUNTRY";
//    static Connection con;
    final String INSERT_PERCENTAGE_CONTRACTPRICE_SQL = "  INSERT INTO core.contractprice"
            + " ( price_id, sequence, contractprice_application_tp_cd, contractprice_adjustment_tp_cd, priority, contractcomponent_id, formula"
            + ", source_reference, contractprice_st_tp_cd, item_id_dup, applicabilitycriteria_id"
            + ", from_country_tp_cd, from_country_postal_code, to_country_tp_cd,to_country_postal_code, to_country_zone_count_from, to_country_zone_count_to, "
            + "customer_currency, zone_id) "
            + " VALUES "
            + " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    //  private final String DB_CONNECTION_USER = "pe_webuser";
    //  private final String DB_CONNECTION_PASSWORD = "pewebuser";
    private final String INSERT_DELTACONTRACTDUMP = "INSERT INTO core.deltacontractdump( " +
            "  organization_number, organization_name, customer_number, customer_name, div, artikelgrupp, statgrupp, prodno, proddescription, routefrom, routeto, startdate, todate, createddate, baseprice, currency, prum, dsclimcd, disclmtfrom, price, adsc, kgtill, updated, filecountry, enabled, routetype, zone_type, remark, createdate, price_id)  " +
            "  VALUES ";
    private final String INSERT_PERCENTAGEBASEDDELTADUMP = "INSERT INTO core.percentagebaseddeltadump(" +
            "branch, parent_customer_number, parent_customer_name, customer_number, customer_name, prodno, startdate, enddate, routetype, from_location, to_location, precentage_discount, updated, enabled, filecountry, zone_type, remark, price_id) " +
            "VALUES ";
    private final String INSERT_PRICE_SQL = "insert into core.price (base_price, created_dt, created_by_user, end_dt, item_id, last_update_tx_id, percent_based_price, percentage_attribute_tp_cd, price_calc_tp_cd, price_def_tp_cd, price_lower_bound,  price_per_attribute_tp_cd, price_per_attribute_val_adj, price_per, price_status_tp_cd,  price_alternative_item_id,price_tp_cd, price_upper_bound, start_dt,last_update_user, last_update_dt)  values ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private final String INSERT_PRICE_HISTORY_SQL = "insert into core.pricehistory (base_price, created_by_user, end_dt, item_id, last_update_tx_id, percent_based_price, percentage_attribute_tp_cd, price_calc_tp_cd, price_def_tp_cd, price_lower_bound,  price_per_attribute_tp_cd, price_per_attribute_val_adj, price_per, price_status_tp_cd,  price_alternative_item_id, price_tp_cd,  start_dt , last_update_user, price_id, created_dt)  values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private final String INSERT_PRICE_SQL_MAX_PRICE_ID = "insert into core.price ( base_price, created_dt, created_by_user, end_dt, item_id, last_update_tx_id, percent_based_price, percentage_attribute_tp_cd, price_calc_tp_cd, price_def_tp_cd, price_lower_bound,  price_per_attribute_tp_cd, price_per_attribute_val_adj, price_per, price_status_tp_cd,  price_alternative_item_id,price_tp_cd, price_upper_bound, start_dt,last_update_user, last_update_dt, price_id)  values ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final String INSERT_PERCENTAGEBASED_PRICE_SQL = "insert into core.price ( percent_based_price, created_dt, created_by_user, end_dt, item_id, last_update_tx_id, percentage_attribute_tp_cd, price_calc_tp_cd, price_def_tp_cd, price_lower_bound,  price_per_attribute_tp_cd, price_per_attribute_val_adj, price_per, price_status_tp_cd,  price_alternative_item_id,price_tp_cd, price_upper_bound, start_dt)  values ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final String INSERT_SURCHARGE_PERCENTAGEBASED_PRICE_SQL = "insert into core.price (percent_based_price, created_dt, created_by_user, end_dt, item_id, last_update_tx_id, percentage_attribute_tp_cd, price_calc_tp_cd, price_def_tp_cd, price_lower_bound,  price_per_attribute_tp_cd, price_per_attribute_val_adj, price_per, price_status_tp_cd,  price_alternative_item_id,price_tp_cd, price_upper_bound, start_dt, price_id )  values ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final String INSERT_CONTRACTPRICE_SQL = "INSERT INTO core.contractprice"
            + " ( price_id, sequence, contractprice_application_tp_cd, contractprice_adjustment_tp_cd, priority, contractcomponent_id, formula"
            + ", source_reference, contractprice_st_tp_cd, item_id_dup, applicabilitycriteria_id"
            + ", from_country_tp_cd, from_country_postal_code, to_country_tp_cd,to_country_postal_code, to_country_zone_count_from, to_country_zone_count_to, "
            + " customer_currency, zone_id, appl_journey_tp_cd, contractprice_processing_tp_cd , from_routeid, to_routeid) "
            + " VALUES "
            + " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String INSERT_CONTRACTPRICE_HISTORY_SQL = "INSERT INTO core.contractpricehistory "
            + " ( price_id, sequence, contractprice_application_tp_cd, contractprice_adjustment_tp_cd, priority, contractcomponent_id, formula"
            + ", source_reference, contractprice_st_tp_cd, item_id_dup, applicabilitycriteria_id"
            + ", from_country_tp_cd, from_country_postal_code, to_country_tp_cd,to_country_postal_code, to_country_zone_count_from, to_country_zone_count_to, "
            + " customer_currency, zone_id, appl_journey_tp_cd, contractprice_processing_tp_cd , from_routeid, to_routeid) "
            + " VALUES "
            + " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String INSERT_PERCENT_SBASED_CONTRACTPRICE_SQL = "  INSERT INTO core.contractprice"
            + " ( price_id, sequence, contractprice_application_tp_cd, contractprice_adjustment_tp_cd, priority, contractcomponent_id, formula"
            + ", source_reference, contractprice_st_tp_cd, item_id_dup, applicabilitycriteria_id"
            + ", from_country_tp_cd, from_country_postal_code, to_country_tp_cd,to_country_postal_code, to_country_zone_count_from, to_country_zone_count_to, customer_currency) "
            + " VALUES "
            + " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)   ";
    private final String INSERT_SLABBASEDPRICE_SQL = " INSERT INTO core.slabbasedprice( " +
            "  price_id, slabbasis_tp_cd, created_dt, created_by_user,last_update_user, last_update_dt) " +
            " VALUES (?, ?, ?, ?, ?, ?) ";
    private final String INSERT_SLABBASEDPRICEENTRIES_SQL = "INSERT INTO core.slabbasedpriceentry( " +
            "  slabbasedprice_id, price_basis_lower_bound, price_basis_upper_bound, price, default_entry) " +
            " VALUES ( ?, ?, ?, ?, ?) ";
    private final String SELECT_CONTRACTCOMPONENT_BY_SSRPK_SQL = " SELECT party_id   pr_party_id"
            + ", party_name pr_party_name"
            + ", parent_party_id pr_parent_party_id "
            + ", source_system_record_pk pr_source_system_record_pk "
            + ", source_system_id pr_source_system_id "
            + " , parent_source_system_record_pk pr_parent_source_system_record_pk"
            + " FROM core.party pr where source_system_record_pk = ?";
    private final String INSERT_CONTRACTCOMPONENT_SQL = " INSERT INTO core.contractcomponent"
            + " ( 	contract_id, source_system_id, source_system_record_pk, last_update_dt, last_update_user, last_update_tx_id, created_dt, created_by_user, start_dt, end_dt, contractcomponent_st_tp_cd) "
            + " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) returning contractcomponent_id";
    private final String UPDATE_CONTRACTCOMPONENT_SQL = " UPDATE core.contractcomponent SET "
            + " last_update_dt=?, last_update_user=?, last_update_tx_id=?,  start_dt=?, end_dt=?, contractcomponent_st_tp_cd=?"
            + " WHERE contractcomponent_id =? ";
    private final String TERMINATE_CONTRACTCOMPONENT_SQL = " UPDATE core.contractcomponent SET end_dt = ?, last_update_dt=?, last_update_user=?, last_update_tx_id=? "
            + "WHERE contractcomponent_id = ? ";
    
    private final String PARENT_IS_NOW_CHILD = "Parent is now a child";
    private final String CHILD_IS_NOW_PARENT = "Child is now a parent";
    private final String CHILD_HAS_CHANGED_PARENT = "Child has changed parent";
    
    private final QueryService queryService = new QueryService();
    EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

    public static Boolean upsertContractRoles(List<ContractRole> roles) {

        try {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            System.out.println("Inside upsertContractData ");
            System.out.println("Begin transaction");
            if (!roles.isEmpty()) {
                for (ContractRole contractRole : roles) {
                    if (contractRole.getStartDt() != null)
                        entityManager.merge(contractRole);
                }
            }
            entityManager.getTransaction().commit();
            System.out.println("Data inserted/updated successfully....");
            entityManager.close();
            return true;
        } catch (NullPointerException ex) {
            System.out.println("######Got a null piointer");
            ex.printStackTrace();
            ex.getMessage();
            System.exit(1);
        } catch (PersistenceException ex) {
            System.out.println("######Persistance exception occures");
            System.out.println("Message :::::::::::::::: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        } catch (Exception ex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            ex.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public static Boolean updateDumpData(Customervolfactordump customervolfactordump) {

        try {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            customervolfactordump.setUpdated(true);
            System.out.println("Inside updateDumpData ");
            System.out.println("Begin transaction");
            entityManager.merge(customervolfactordump);
            entityManager.getTransaction().commit();
            System.out.println("Data updated successfully....");
            entityManager.close();
            return true;
        } catch (NullPointerException ex) {
            System.out.println("######Got a null piointer");
            ex.printStackTrace();
            ex.getMessage();
            System.exit(1);
        } catch (PersistenceException ex) {
            System.out.println("######Persistance exception occures");
            System.out.println("Message :::::::::::::::: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        } catch (Exception ex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            ex.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    private static void updateRoleDates() {

        QueryService queryService = new QueryService();
        ArrayList<ContractComponent> components = queryService.findContractComponents();
        ArrayList<ContractRole> contractRoles = new ArrayList<ContractRole>();
        for (ContractComponent component : components) {
            ArrayList<ContractRole> roles = queryService.findContractRolesById(component);
            for (ContractRole cr : roles) {
                cr.setStartDt(component.getStartDt());
                contractRoles.add(cr);
            }
        }
        upsertContractRoles(contractRoles);
    }

    public static void main(String[] str) throws ParseException {

        DatabaseService databaseService = new DatabaseService();
        // QueryService queryService = new QueryService();
           databaseService.cleanUp();
        //databaseService.cleanUpPERCENT();
//     databaseService.cleanUpSLABBASED();
 //       databaseService.cleanUpInFixedANDSlabbased();
        //databaseService.fixContractroles();
      //  databaseService.mergeSimilarJourneyType();
     // databaseService.insertPriority();

        //        databaseService.fixDatesINPriceTable();
//        databaseService.fixPercentageBasedDatesINPriceTable();
//        databaseService.updateAllContractPriceForProcessing();
//  databaseService.fixItemInPrice();
//        List<Contractdump> dumps =  queryService.findAllContractdumps("SE");


    }

    private HashMap<String, Integer> getProcessingId() {
        HashMap<String, Integer> processingMap = new HashMap<>();

        processingMap.put(ContractProcessing.PURE_COUNTRY_CODE.toString(), 1);
        processingMap.put(ContractProcessing.COUNTRY_CODE_FULL_POSTAL_CODE.toString(), 2);
        processingMap.put(ContractProcessing.COUNTRY_CODE_PARTIAL_POSTAL_CODE.toString(), 2);
        processingMap.put(ContractProcessing.STANDARD_ZONE.toString(), 4);
        processingMap.put(ContractProcessing.CUSTOM_ZONE.toString(), 5);
        processingMap.put(ContractProcessing.SURCHARE_EXEMPTION.toString(), 6);
        processingMap.put(ContractProcessing.COUNTRY_CODE_PARTIAL_FULL_POSTAL_CODE.toString(), 2);
        processingMap.put(ContractProcessing.ALL_SOURCE_ALL_DESTINATION.toString(), 8);
        return processingMap;
    }

    public Boolean upsertContractData(Set<Contractdump> contracts) {

        try {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            System.out.println("Inside upsertContractData ");
            System.out.println("Begin transaction");
            if (!contracts.isEmpty()) {
                for (Contractdump contractdump : contracts) {
                    if (contractdump.getOrganizationNumber() != null)
                        entityManager.persist(contractdump);
                }
            }
            entityManager.getTransaction().commit();
            System.out.println("Data inserted/updated successfully....");
            entityManager.close();
            return true;
        } catch (HibernateQueryException sqex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            sqex.printStackTrace();
            System.exit(1);
        } catch (NullPointerException ex) {
            System.out.println("######Got a null piointer");
            ex.printStackTrace();
            ex.getMessage();
            System.exit(1);
        } catch (PersistenceException ex) {
            System.out.println("######Persistance exception occures");
            System.out.println("Message :::::::::::::::: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);

        } catch (Exception ex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            ex.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    private String getDateAsString(Date d) {
        return DateFormatUtils.format(d, "yyyy-MM-dd");
    }

    private boolean isEmpty(String s) {
        return s == null || "".equals(s.trim());
    }

    public int insertDeltaContractDumpUsingJDBC(Deltacontractdump deltacontractdump, Logger logger) {
        try {
            
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
//            Statement stmt = con.createStatement();
            String discLmtFrom = deltacontractdump.getDiscLmtFrom() == null ? String.valueOf(-1) : String.valueOf(deltacontractdump.getDiscLmtFrom());
            String fromRoute = isEmpty(deltacontractdump.getRouteFrom()) ? "NULL" : deltacontractdump.getRouteFrom();
            String toRoute = isEmpty(deltacontractdump.getRouteTo()) ? "NULL" : deltacontractdump.getRouteTo();
            String endDateStr = null;
            if(null!=deltacontractdump.getToDate())
                endDateStr = getDateAsString(deltacontractdump.getToDate());
            else
                endDateStr = "9999-12-31 00:00:00+01";

            String values = "('" + deltacontractdump.getOrganizationNumber() +
                    "','" + deltacontractdump.getOrganizationName().replaceAll("'", "") +
                    "','" + deltacontractdump.getCustomerNumber() +
                    "','" + deltacontractdump.getCustomerName().replaceAll("'", "") +
                    "'," + deltacontractdump.getDiv() +
                    ",'" + deltacontractdump.getArtikelgrupp() +
                    "','" + deltacontractdump.getStatGrupp() +
                    "'," + deltacontractdump.getProdNo() +
                    ",'" + deltacontractdump.getProdDescr() +
                    "','" + fromRoute +
                    "','" + toRoute +
                    "','" + getDateAsString(deltacontractdump.getFromDate()) +
                    "','" + endDateStr +
                    "', current_timestamp" +
                    "," + deltacontractdump.getBasePrice() +
                    ",'" + deltacontractdump.getCurr() +
                    "','" + deltacontractdump.getPrUM() +
                    "','" + deltacontractdump.getDscLimCd() +
                    "'," + discLmtFrom +
                    "," + deltacontractdump.getPrice() +
                    "," + deltacontractdump.getADsc() +
                    ",'" + deltacontractdump.getKgTill() +
                    "','" + deltacontractdump.isUpdated() +
                    "','" + deltacontractdump.getFileCountry() +
                    "','" + deltacontractdump.isEnabled() +
                    "','" + deltacontractdump.getRouteType() +
                    "','" + deltacontractdump.getZoneType() +
                    "','" + deltacontractdump.getRemark() +
                    "',current_timestamp " +
                    "," + deltacontractdump.getPriceId() +
                    ")";

            String sql_final = INSERT_DELTACONTRACTDUMP + values;
            entityManager.getTransaction().begin();
           int insertCount=entityManager.createNativeQuery(sql_final).executeUpdate();
           entityManager.getTransaction().commit();
           entityManager.close();
            return insertCount;
        } catch (Exception ex) {
            logger.warning("Error[" + ex.getMessage() + "] while processing delta for: OrganizationNumber[{" + deltacontractdump.getOrganizationNumber() + "}],customerNumber[{" + deltacontractdump.getCustomerNumber() + "}],RouteFrom[{" + deltacontractdump.getRouteFrom() + "}],RouteTo[{" + deltacontractdump.getRouteTo() + "}],ProdNo[{" + deltacontractdump.getProdNo() + "}],Price[{" + deltacontractdump.getBasePrice() + "}]");
            return 0;
        }
    }

    public int insertDeltaContractDumpUsingJDBC(Percentagebaseddeltadump percentagebaseddeltadump, Logger logger) {
        try {
            
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            
            String routeType = isEmpty(percentagebaseddeltadump.getRouteType()) ? "NULL" : percentagebaseddeltadump.getRouteType();
            String fromRoute = isEmpty(percentagebaseddeltadump.getFromLocation()) ? "NULL" : percentagebaseddeltadump.getFromLocation();
            String toRoute = isEmpty(percentagebaseddeltadump.getToLocation()) ? "NULL" : percentagebaseddeltadump.getToLocation();

            String endDateStr = null;
            if(null!=percentagebaseddeltadump.getEnddate())
                endDateStr = getDateAsString(percentagebaseddeltadump.getEnddate());
            else
                endDateStr = "9999-12-31 00:00:00+01";

            String values = "(" + percentagebaseddeltadump.getBranch() +
                    ",'" + percentagebaseddeltadump.getParentCustomerNumber() +
                    "','" + percentagebaseddeltadump.getParentCustomerName().replaceAll("'", "") +
                    "'," + percentagebaseddeltadump.getCustomerNumber().replaceAll("'", "") +
                    ",'" + percentagebaseddeltadump.getCustomerName().replaceAll("'", "") +
                    "','" + percentagebaseddeltadump.getProdno() +
                    "','" + getDateAsString(percentagebaseddeltadump.getStartdate()) +
                    "','" + endDateStr +
                    "','" + routeType +
                    "','" + fromRoute +
                    "','" + toRoute +
                    "','" + (percentagebaseddeltadump.getPrecentageDiscount()) +
                    "','" + (percentagebaseddeltadump.getUpdated()) +
                    "','" + (percentagebaseddeltadump.getEnabled()) +
                    "','" + (percentagebaseddeltadump.getFileCountry()) +
                    "','" + (percentagebaseddeltadump.getZoneType()) +
                    "','" + (percentagebaseddeltadump.getRemark()) +
                    "'," + (percentagebaseddeltadump.getPriceId()) +
               //     "',now()::date" +

                    ")";
            entityManager.getTransaction().begin();
            String sql_final = INSERT_PERCENTAGEBASEDDELTADUMP + values;
           int insertCount= entityManager.createNativeQuery(sql_final).executeUpdate();
           entityManager.getTransaction().commit();
           entityManager.close();
            return insertCount;
        } catch (Exception ex) {
            logger.warning("Error[" + ex.getMessage() + "] while processing delta for: ParentCustomerNumber[{" + percentagebaseddeltadump.getParentCustomerNumber() + "}],customerNumber[{" + percentagebaseddeltadump.getCustomerNumber() + "}],FromLocation[{" + percentagebaseddeltadump.getFromLocation() + "}],ToLocation[{" + percentagebaseddeltadump.getToLocation() + "}],ProdNo[{" + percentagebaseddeltadump.getProdno() + "}],Price[{" + percentagebaseddeltadump.getPrecentageDiscount() + "}]");
            return 0;
        }
    }

    public Boolean upsertDeltaContracts(List<Deltacontractdump> deltacontractdumps, Logger logger) {
        try {
            if (!deltacontractdumps.isEmpty()) {
                for (Deltacontractdump deltacontractdump : deltacontractdumps) {
                    if (deltacontractdump.getOrganizationNumber() != null) {
                        try {
                            int x = insertDeltaContractDumpUsingJDBC(deltacontractdump, logger);
                        } catch (Exception ee) {
                            logger.warning("Error " + ee.getMessage() + " while processing delta for: OrganizationNumber[{" + deltacontractdump.getOrganizationNumber() + "}],customerNumber[{" + deltacontractdump.getCustomerNumber() + "}],RouteFrom[{" + deltacontractdump.getRouteFrom() + "}],RouteTo[{" + deltacontractdump.getRouteTo() + "}],ProdNo[{" + deltacontractdump.getProdNo() + "}],Price[{" + deltacontractdump.getBasePrice() + "}]");
                            System.exit(1);
                        }
                    }
                }
            }
            return true;
        }
//
//        catch (HibernateException sqlException) {
//            System.out.println("######Error while upserting data in Contractdump table. " + sqlException.getMessage() );
//            sqlException.printStackTrace();
//         //   System.exit(1);
//        }
//        catch (HibernateQueryException sqex) {
//            System.out.println("######Error while upserting data in Contractdump table.");
//            sqex.printStackTrace();
//       //     System.exit(1);
//        } catch (NullPointerException ex) {
//            System.out.println("######Got a null piointer");
//            ex.printStackTrace();
//            ex.getMessage();
//     //       System.exit(1);
//        } catch (PersistenceException ex) {
//            System.out.println("######Persistance exception occures");
//            System.out.println("Message :::::::::::::::: " + ex.getMessage());
//            ex.printStackTrace();
//       //     System.exit(1);

        //}
        catch (Exception ex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            ex.printStackTrace();
            //   System.exit(1);
        }
        return false;
    }

    public Boolean upsertPercentDeltaContracts(List<Percentagebaseddeltadump> deltacontractdumps, Logger logger) {

        try {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

            System.out.println("Inside upsertContractData ");
            System.out.println("Begin transaction");
            if (!deltacontractdumps.isEmpty()) {
                for (Percentagebaseddeltadump deltacontractdump : deltacontractdumps) {
                    try {
                        if (deltacontractdump.getParentCustomerNumber() != null) {
                           /* entityManager.getTransaction().begin();
                            entityManager.persist(deltacontractdump);
                            entityManager.getTransaction().commit();
                            entityManager.flush();*/
                            insertDeltaContractDumpUsingJDBC(deltacontractdump, logger);
                        }
                    } catch (Exception ex) {
                        System.out.println("Some Unknown Error:" + ex.getMessage());
                    }
                }
            }

            System.out.println("Data inserted/updated successfully....");
            entityManager.close();
            return true;
        } catch (HibernateQueryException sqex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            sqex.printStackTrace();
            System.exit(1);
        } catch (NullPointerException ex) {
            System.out.println("######Got a null piointer");
            ex.printStackTrace();
            ex.getMessage();
            System.exit(1);
        } catch (PersistenceException ex) {
            System.out.println("######Persistance exception occures");
            System.out.println("Message :::::::::::::::: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);

        } catch (Exception ex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            ex.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public Boolean disableNonCDHData(Set<Contractdump> contracts, int remark) {

        try {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            System.out.println("Inside upsertContractData ");
            System.out.println("Begin transaction");
            if (!contracts.isEmpty()) {
                for (Contractdump contractdump : contracts) {
                    contractdump.setRemark(remark);
                    contractdump.setEnabled(false);
                    entityManager.merge(contractdump);
                }
            }
            entityManager.getTransaction().commit();
            System.out.println("Data inserted/updated successfully....");
            entityManager.close();
            return true;
        } catch (HibernateQueryException sqex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            sqex.printStackTrace();
            System.exit(1);
        } catch (NullPointerException ex) {
            System.out.println("######Got a null piointer");
            ex.printStackTrace();
            ex.getMessage();
            System.exit(1);
        } catch (PersistenceException ex) {
            System.out.println("######Persistance exception occures");
            System.out.println("Message :::::::::::::::: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);

        } catch (Exception ex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            ex.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public Boolean disableNonCDHDataPercent(Set<Percentagebaseddump> contracts, Integer remark) {

        try {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            System.out.println("Inside upsertContractData ");
            System.out.println("Begin transaction");
            if (!contracts.isEmpty()) {
                for (Percentagebaseddump contractdump : contracts) {
                    contractdump.setRemark(remark);
                    contractdump.setEnabled(false);
                    entityManager.merge(contractdump);
                }
            }
            entityManager.getTransaction().commit();
            System.out.println("Data inserted/updated successfully....");
            entityManager.close();
            return true;
        } catch (HibernateQueryException sqex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            sqex.printStackTrace();
            System.exit(1);
        } catch (NullPointerException ex) {
            System.out.println("######Got a null piointer");
            ex.printStackTrace();
            ex.getMessage();
            System.exit(1);
        } catch (PersistenceException ex) {
            System.out.println("######Persistance exception occures");
            System.out.println("Message :::::::::::::::: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);

        } catch (Exception ex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            ex.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    @Transactional
    public Boolean upsertSurchargeData(List<Surchargedump> dumps) {

        try {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            System.out.println("Inside upsertUpsertData ");
            System.out.println("Begin transaction");
            if (!dumps.isEmpty()) {
                for (Surchargedump surchargedump : dumps) {
                    entityManager.persist(surchargedump);
                }
            }
            entityManager.getTransaction().commit();
            System.out.println("Data inserted/updated successfully....");
            entityManager.close();
            return true;
        } catch (HibernateQueryException sqex) {
            System.out.println("######Error while upserting data in Surchargedump table.");
            sqex.printStackTrace();
            System.exit(1);
        } catch (NullPointerException ex) {
            System.out.println("######Got a null piointer");
            ex.printStackTrace();
            ex.getMessage();
            System.exit(1);
        } catch (PersistenceException ex) {
            System.out.println("######Persistance exception occures");
            System.out.println("Message :::::::::::::::: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);

        } catch (Exception ex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            ex.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public Boolean upsertUpdatedContractData(List<Contractdumpservice> contracts) {

        try {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            System.out.println("Inside upsertContractData ");
            System.out.println("Begin transaction");
            if (!contracts.isEmpty()) {
                for (Contractdumpservice contractdump : contracts) {
                    if (contractdump.getOrganizationNumber() != null)
                        entityManager.persist(contractdump);
                }
            }
            entityManager.getTransaction().commit();
            System.out.println("Data inserted/updated successfully....");
            entityManager.close();
            return true;
        } catch (NullPointerException ex) {
            System.out.println("######Got a null piointer");
            ex.printStackTrace();
            ex.getMessage();
            System.exit(1);
        } catch (PersistenceException ex) {
            System.out.println("######Persistance exception occures");
            System.out.println("Message :::::::::::::::: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);

        } catch (Exception ex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            ex.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public Boolean upsertCustomerVolumetricFactorData(List<Customervolfactordump> contracts) {
        try {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            System.out.println("Inside upsertCustomerVolumetricFactorData ");
            System.out.println("Begin transaction");
            if (!contracts.isEmpty()) {
                for (Customervolfactordump contractdump : contracts) {
                    entityManager.persist(contractdump);
                }
            }
            entityManager.getTransaction().commit();
            System.out.println("Data inserted/updated successfully....");
            entityManager.close();
            return true;
        } catch (NullPointerException ex) {
            System.out.println("######Got a null piointer");
            ex.printStackTrace();
            ex.getMessage();
            System.exit(1);
        } catch (PersistenceException ex) {
            System.out.println("######Persistance exception occures");
            System.out.println("Message :::::::::::::::: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);

        } catch (Exception ex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            ex.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    private void insertContractRoleData() {
        try {
            List<Contractdump> contractdumpList = fetchContractdumpData();
            LocalDate date = LocalDate.now();
            LocalDate endDate = date.plusYears(1);
            for (Contractdump contractdump : contractdumpList) {
                //ContractRole contractRole = new ContractRole();
                //     ContractComponent contractComponent = new ContractComponent();


            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private ContractComponent setContractComponentValues() {
        //     ContractComponent contractComponent = new ContractComponent();
        //contractComponent.se
        return null;
    }

    public List<Contractdump> fetchContractdumpData() {
        List<Contractdump> contractdumpList = new ArrayList<Contractdump>();
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        System.out.println("Inside fetchDataToContractRole() ");
        List<Query> query = entityManager.createQuery(" from Contractdump where updated = false").getResultList();
        ListIterator listIterator = query.listIterator();
        while (listIterator.hasNext()) {
            contractdumpList.add((Contractdump) listIterator.next());
        }
        entityManager.close();
        return contractdumpList;
    }

    public Contract buildContract(Contractdump contractdump, Party party) throws Exception {
        if (party == null)
            System.out.println("asdasdadadadad");

        Contract contract = new Contract();
        contract.setCreatedByUser(AUTOMATION_USER);
        contract.setContractDescription(contractdump.getOrganizationName());
        Instant instant = contractdump.getFromDate().toInstant();
        ZoneId defaultZone = ZoneId.of("Australia/Sydney");
        LocalDateTime dateTime = instant.atZone(defaultZone).toLocalDateTime();
        contract.setSignDt(dateTime);
        contract.setSourceSystemId(3);
        if (null != party.getParentSourceSystemRecordPk())
            contract.setSourceSystemRecordPk(party.getParentSourceSystemRecordPk());
        else
            contract.setSourceSystemRecordPk(party.getSourceSystemRecordPk());
        contract.setCreatedDt(instant.atZone(defaultZone).toLocalDate());
        contract.setAgreementName(contractdump.getOrganizationName());
        contract.setLastUpdateDt(LocalDate.now());
        contract.setLastUpdateUser(AUTOMATION_USER);
        return contract;
    }

    public int updateDeltaAgreementsPERCENTUsingJDBC(List<Percentagebaseddeltadump> deltacontractdumps, String remark, boolean isUpdated, Logger logger) {
        try {
           
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            int insertCount=0;
            for (Percentagebaseddeltadump d : deltacontractdumps) {
                if(null!=d.getZoneType() && d.getZoneType().equalsIgnoreCase("null"))
                    d.setZoneType(null);
                if(null==d.getFromLocation())
                    d.setFromLocation("NULL");
                if(null==d.getToLocation())
                    d.setToLocation("NULL");
                String setClause = " SET remark='" + remark + "', updated= '" + isUpdated + "'\\:\\:boolean ";
                String whereClause = " WHERE parent_customer_number ='" + d.getParentCustomerNumber() + "' AND customer_number='" + d.getCustomerNumber() + "' AND prodno='" + d.getProdno() + "'\\:\\:integer AND from_location='" +
                        d.getFromLocation() + "' AND to_location='" + d.getToLocation() + "' AND startdate='" + getDateAsString(d.getStartdate()) + "'\\:\\:date AND enddate='" + getDateAsString(d.getEnddate()) + "'\\:\\:date AND precentage_discount='" +
                        d.getPrecentageDiscount() + "' AND routetype='" + d.getRouteType() + "'";
                String final_sql = "UPDATE core.percentagebaseddeltadump " + setClause + whereClause;
                insertCount+=entityManager.createNativeQuery(final_sql).executeUpdate();
                entityManager.getTransaction().commit();
                entityManager.close();
            }
            return insertCount;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public void updateDeltaAgreementPERCENT(Percentagebaseddeltadump deltacontractdump, String remark, Boolean isUpdated, Logger logger) {
        List<Percentagebaseddeltadump> dumps = new ArrayList<>();
        dumps.add(deltacontractdump);
        updateDeltaAgreementsPERCENTUsingJDBC(dumps,remark,isUpdated,logger);
    }

    public Contract buildContractSURCHARGE(String customerNumber, String customerName, Party newParty) throws Exception {

        Contract contract = new Contract();
        ZoneId defaultZone = ZoneId.of("Australia/Sydney");

        contract.setCreatedByUser(AUTOMATION_USER);
        contract.setContractDescription(customerName);
        Instant instant = LocalDateTime.now().toInstant(ZoneOffset.UTC);

        LocalDateTime dateTime = instant.atZone(defaultZone).toLocalDateTime();
        contract.setSignDt(dateTime);
        contract.setSourceSystemId(3);
        contract.setSourceSystemRecordPk(newParty.getSourceSystemRecordPk());
        contract.setCreatedDt(instant.atZone(defaultZone).toLocalDate());
        contract.setAgreementName(newParty.getPartyName());
        contract.setLastUpdateDt(LocalDate.now());
        contract.setLastUpdateUser(AUTOMATION_USER);
        return contract;
    }

    public Contract buildContractPercentageBased(Percentagebaseddump contractdump, Party party) throws Exception {
        Contract contract = new Contract();
        contract.setCreatedByUser(AUTOMATION_USER);
        contract.setContractDescription(contractdump.getParentCustomerNumber());
        Instant instant = contractdump.getStartdate().toInstant();
        ZoneId defaultZone = ZoneId.of("Australia/Sydney");
        //     LocalDate dateTime2 =instant.atZone(defaultZone).toLocalDate();
        LocalDateTime dateTime = instant.atZone(defaultZone).toLocalDateTime();
        contract.setSignDt(dateTime);
        //  contract.setSignDt(contractdump.getStartdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        contract.setSourceSystemId(3);
        contract.setSourceSystemRecordPk(party.getSourceSystemRecordPk());
        contract.setCreatedDt(contractdump.getStartdate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        contract.setAgreementName(contractdump.getParentCustomerNumber());
        contract.setLastUpdateDt(LocalDate.now());
        contract.setLastUpdateUser(AUTOMATION_USER);
        return contract;
    }

    public ContractRole buildContractRole(ContractComponent contractComponent, Contractdump contractdump, Party party, String customer) {
        ContractRole signer = buildSignerRole(contractComponent, party, contractdump, customer);

        return signer;
    }

    public ContractRole buildContractRolePercentageBased(ContractComponent contractComponent, Percentagebaseddump contractdump, Party party, String customer) {
        ContractRole signer = buildSignerRolePercentageBased(contractComponent, party, contractdump, customer);

        return signer;
    }

    public ContractRole buildContractRoleForSurcharge(ContractComponent contractComponent, Surchargedump surchargedump, Party party) throws ParseException {
        ContractRole signer = buildSignerRoleForSurcharge(contractComponent, party, surchargedump);

        return signer;
    }

    private ContractRole buildSignerRole(ContractComponent contractComponent, Party party, Contractdump contractdump, String customer) {
        ContractRole signerRole = new ContractRole();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        ZoneId defaultZone = ZoneId.of("Australia/Sydney");
        Instant startInstant = contractdump.getFromDate().toInstant();
        Instant endInstant = contractdump.getToDate().toInstant();
        String customerId = customer.split("~")[0];

        LocalDate startDateTime = startInstant.atZone(defaultZone).toLocalDate();
        LocalDate endDateTime = endInstant.atZone(defaultZone).toLocalDate();

        if (party == null)
            System.out.println("wait contractcomponent - " + contractComponent.getContractComponentId() + " \\:\\:\\:\\: contractdump - " + contractdump.getId());
        if (party != null && party.getParentSourceSystemRecordPk() == null)
            signerRole.setContractRoleTpCd(1);
        else
            signerRole.setContractRoleTpCd(2);
        signerRole.setContractRoleStTpCd(ROLE_DISCOUNT_DEFAULT_STATUS_ACTIVE);
        signerRole.setPartySourceSystemRecordPk(customerId);
        signerRole.setAgreementCurrency(contractdump.getCurr());
        signerRole.setContractComponent(contractComponent);
        signerRole.setCreatedByUser(AUTOMATION_USER);
        signerRole.setCreatedDt(LocalDate.now());
        signerRole.setStartDt(startDateTime);
        signerRole.setEndDt(endDateTime);
        signerRole.setParty(party);
        signerRole.setLastUpdateUser(AUTOMATION_USER);
        signerRole.setLastUpdateDt(LocalDate.now());

        return signerRole;

    }

    private ContractRole buildSignerRole(ContractComponent contractComponent, Party party, Surchargedump surchargedump, String customer) throws ParseException {
        ContractRole signerRole = new ContractRole();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String startDate = "2022-01-01 00:00:00+01";
        String endDate = "2022-12-31 00:00:00+01";
        String customerId = customer.split("~")[0];

        Date startDateTime = dateFormat.parse(startDate);
        Date endDateTime = dateFormat.parse(endDate);

        if (party == null)
            System.out.println("wait contractcomponent - " + contractComponent.getContractComponentId() + " \\:\\:\\:\\: surchargedump - " + surchargedump.getId());
        if (party != null && party.getParentSourceSystemRecordPk() == null)
            signerRole.setContractRoleTpCd(1);
        else
            signerRole.setContractRoleTpCd(2);
        signerRole.setContractRoleStTpCd(ROLE_DISCOUNT_DEFAULT_STATUS_ACTIVE);
        signerRole.setPartySourceSystemRecordPk(customerId);
        signerRole.setContractComponent(contractComponent);
        signerRole.setCreatedByUser(AUTOMATION_USER);
        signerRole.setCreatedDt(LocalDate.now());
        signerRole.setStartDt(startDateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        signerRole.setEndDt(endDateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        signerRole.setParty(party);
        signerRole.setLastUpdateUser(AUTOMATION_USER);
        signerRole.setLastUpdateDt(LocalDate.now());

        return signerRole;

    }

    private ContractRole buildSignerRoleForSurcharge(ContractComponent contractComponent, Party party, Surchargedump surchargedump) throws ParseException {
        ContractRole signerRole = new ContractRole();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ZoneId defaultZone = ZoneId.of("Australia/Sydney");
        String startDate = "2022-01-01 00:00:00";
        String endDate = "2022-12-31 00:00:00";
        String customerId = surchargedump.getCustomerNumber();

        Date startDateTime = dateFormat.parse(startDate);
        LocalDate startLocalDate = startDateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date endDateTime = dateFormat.parse(endDate);
        LocalDate endLocalDate = endDateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (party.getParentSourceSystemRecordPk() == null)
            signerRole.setContractRoleTpCd(1);
        else
            signerRole.setContractRoleTpCd(2);
        signerRole.setContractRoleStTpCd(ROLE_DISCOUNT_DEFAULT_STATUS_ACTIVE);
        signerRole.setPartySourceSystemRecordPk(customerId);
        //signerRole.setAgreementCurrency(contractdump.getCurr());
        signerRole.setContractComponent(contractComponent);
        signerRole.setCreatedByUser(AUTOMATION_USER);
        signerRole.setCreatedDt(LocalDate.now());
        signerRole.setStartDt(startLocalDate);
        signerRole.setEndDt(endLocalDate);
        signerRole.setParty(party);
        signerRole.setLastUpdateUser(AUTOMATION_USER);
        signerRole.setLastUpdateDt(LocalDate.now());

        return signerRole;

    }

    // PAUSED HERE
    private ContractRole buildSignerRolePercentageBased(ContractComponent contractComponent, Party party, Percentagebaseddump contractdump, String customer) {
        ContractRole signerRole = new ContractRole();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        ZoneId defaultZone = ZoneId.of("Australia/Sydney");
        Instant startInstant = contractdump.getStartdate().toInstant();
        Instant endInstant = contractdump.getEnddate().toInstant();
        String customerId = customer.split("~")[0];

        LocalDate startDateTime = startInstant.atZone(defaultZone).toLocalDate();
        LocalDate endDateTime = endInstant.atZone(defaultZone).toLocalDate();

        if (party.getParentSourceSystemRecordPk() == null)
            signerRole.setContractRoleTpCd(1);
        else
            signerRole.setContractRoleTpCd(2);
        signerRole.setContractRoleStTpCd(ROLE_DISCOUNT_DEFAULT_STATUS_ACTIVE);
        signerRole.setPartySourceSystemRecordPk(customerId);
        signerRole.setContractComponent(contractComponent);
        signerRole.setCreatedByUser(AUTOMATION_USER);
        signerRole.setCreatedDt(startDateTime);
        signerRole.setStartDt(startDateTime);
        signerRole.setParty(party);
        signerRole.setLastUpdateUser(AUTOMATION_USER);
        signerRole.setLastUpdateDt(LocalDate.now());

        return signerRole;


    }

    @Transactional
    public Party buildNewParty(Contract contract) {
        Party party = new Party();
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            Double random = Math.random() * 10000;
            Long longVal = new Double(random).longValue();
            if (null != contract.getContractDescription())
                party.setPartyName(contract.getContractDescription());
            else
                System.out.println("Contract description is null here");

            if (null != contract.getSourceSystemId())
                party.setSourceSystemId(Long.valueOf(contract.getSourceSystemId()));
            else
                party.setSourceSystemId(Long.valueOf(3));
            party.setSourceSystemRecordPk(longVal.toString());
            party.setCreatedDt(LocalDate.now());
            party.setCreatedByUser(AUTOMATION_USER);
            entityManager.getTransaction().begin();
            entityManager.persist(party);
            entityManager.getTransaction().commit();
            //entityManager.getTransaction().begin();
            //entityManager.persist(party);
            //entityManager.getTransaction().commit();
            System.out.println("ContractComponent Data inserted/updated successfully....");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while creating new Party");
            System.exit(1);
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return party;
    }

    @Transactional
    public Party buildNewCustomerParty(Contractdump contractdump) {
        Party party = new Party();
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {

            party.setPartyName(contractdump.getCustomerName());
            if (null != contractdump.getCustomerNumber())
                party.setSourceSystemRecordPk(contractdump.getCustomerNumber());
            if (null != contractdump.getOrganizationNumber())
                party.setParentSourceSystemRecordPk(contractdump.getOrganizationNumber());

            party.setPartyName(contractdump.getCustomerName());

            party.setCreatedDt(LocalDate.now());
            party.setCreatedByUser(AUTOMATION_USER);
            entityManager.getTransaction().begin();
            entityManager.persist(party);
            entityManager.getTransaction().commit();
            System.out.println("ContractComponent Data inserted/updated successfully....");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while creating new Party");
            System.exit(1);
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return party;
    }

    @Transactional
    public Party buildNewOrganizationParty(Contractdump contractdump) {
        Party party = new Party();
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {

            party.setPartyName(contractdump.getOrganizationName());
            if (null != contractdump.getCustomerNumber())
                party.setSourceSystemRecordPk(contractdump.getOrganizationNumber());
            party.setCreatedDt(LocalDate.now());
            party.setCreatedByUser(AUTOMATION_USER);
            entityManager.getTransaction().begin();
            entityManager.persist(party);
            entityManager.getTransaction().commit();
            System.out.println("ContractComponent Data inserted/updated successfully....");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while creating new Party");
            System.exit(1);
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return party;
    }

    @Transactional
    public Long insertContract(Contract contract) {
        EntityManagerFactory entityManagerFactory = JPAUtil.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            LocalDateTime now = LocalDateTime.now();
            long txId = getTxId();
            contract.setLastUpdateTxId(txId);
            contract.setCreatedByUser(AUTOMATION_USER);
            contract.setCreatedDt(LocalDate.now());

            entityManager.persist(contract);
            entityManager.getTransaction().commit();
            System.out.println("upsertcontract Contract inserted successfully....");
            entityManager.close();

            return txId;

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            entityManager.close();
        }
        return null;
    }

    @Transactional
    public ContractComponent insertContractComponent(ContractComponent contractComponent) {

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            long txId = getTxId();
            contractComponent.setLastUpdateTxId(txId);
            contractComponent.setCreatedByUser(AUTOMATION_USER);
            contractComponent.setCreatedDt(LocalDate.now());
            entityManager.getTransaction().begin();
            entityManager.merge(contractComponent);
            entityManager.getTransaction().commit();
            System.out.println("insertContractComponent ContractComponent inserted successfully....");

            contractComponent = queryService.findComponent(txId);
            return contractComponent;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {

            entityManager.close();
        }
        return null;
    }

    @Transactional
    public ContractComponent updateContractComponent(ContractComponent contractComponent) {

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(contractComponent);
            entityManager.getTransaction().commit();
            System.out.println("insertContractComponent ContractComponent inserted successfully....");
            return contractComponent;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {

            entityManager.close();
        }
        return null;
    }

    public int updateDeltaContractDumpUsingJDBC(List<Deltacontractdump> deltacontractdumps, String remark, boolean isUpdated, Logger logger) {
        try {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            int insertCount=0;
            for (Deltacontractdump d : deltacontractdumps) {
               Double discLmtFrom = d.getDiscLmtFrom()==null?-1:d.getDiscLmtFrom();
                String setClause = " SET remark='" + remark + "', updated= true "  ;
                String whereClause = " WHERE organization_number='" + d.getOrganizationNumber() + "' AND customer_number='" + d.getCustomerNumber() + "' AND prodno=" + d.getProdNo() + " AND routefrom='" +
                        d.getRouteFrom().toUpperCase() + "' AND routeto='" + d.getRouteTo().toUpperCase() + "' AND startdate='" + getDateAsString(d.getFromDate()) + "' AND todate='" + getDateAsString(d.getToDate()) + "'  AND baseprice=" +
                        d.getBasePrice() + " AND disclmtfrom= " +discLmtFrom ;
                String final_sql = UPDATE_DELTACONTRACTDUMP + setClause + whereClause;
                
                insertCount+=entityManager.createNativeQuery(final_sql).executeUpdate();
            }
            entityManager.getTransaction().commit();
            entityManager.close();
            return insertCount;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
            return 0;
        }
    }


    @Transactional
    public void updateDeltaAgreement(Deltacontractdump deltacontractdump, String remark, Boolean isUpdated) {

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            entityManager.getTransaction().begin();
            deltacontractdump.setRemark(remark);
            deltacontractdump.setUpdated(isUpdated);
            entityManager.merge(deltacontractdump);
            entityManager.getTransaction().commit();
            System.out.println("insertContractComponent ContractComponent inserted successfully....");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {

            entityManager.close();
        }
    }

    public int updateDeltaContractDumps(HashSet<Deltacontractdump> deltacontractdumps, String remark, boolean isUpdated, Logger logger) {
        ArrayList<Deltacontractdump> dumps = new ArrayList<Deltacontractdump>(deltacontractdumps);
        return updateDeltaContractDumpUsingJDBC(dumps, remark,isUpdated,logger);
    }

    public int updateDeltaContractDump(Deltacontractdump deltacontractdump, String remark, boolean isUpdated, Logger logger) {
        List<Deltacontractdump> deltacontractdumps = new ArrayList<>();deltacontractdumps.add(deltacontractdump);
        return updateDeltaContractDumpUsingJDBC(deltacontractdumps, remark,isUpdated,logger);
    }

    public int updateSingleDeltaContractDumpUsingJDBC(Deltacontractdump d, String remark, boolean isUpdated, Logger logger) {
        try {
           
            EntityManager entityManager=JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            String setClause = null;
            int insertCount=0;
            if(d.getRouteFrom()==null)
                d.setRouteFrom("NULL");
            if(d.getRouteTo()==null)
                d.setRouteTo("NULL");
            if(d.getDiscLmtFrom()==null)
                d.setDiscLmtFrom(new Double(-1));
            if(d.getPriceId()!=null && d.getPriceId()!=0)
                 setClause = " SET price_id= "+ d.getPriceId() +", remark='" + remark + "', updated= '" + isUpdated + "'\\:\\:boolean ";
            else
                setClause = " SET remark='" + remark + "', updated= '" + isUpdated + "'\\:\\:boolean ";

                String whereClause = " WHERE organization_number='" + d.getOrganizationNumber() + "' AND customer_number='" + d.getCustomerNumber() + "' AND prodno='" + d.getProdNo() + "'\\:\\:integer AND routefrom='" +
                        d.getRouteFrom().toUpperCase() + "' AND routeto='" + d.getRouteTo().toUpperCase() + "' AND startdate='" + getDateAsString(d.getFromDate()) + "'\\:\\:date AND todate='" + getDateAsString(d.getToDate()) + "'\\:\\:date AND baseprice='" +
                        d.getBasePrice() + "'\\:\\:DECIMAL AND disclmtfrom='" + d.getDiscLmtFrom() + "'\\:\\:DECIMAL";
                String final_sql = "update core.deltacontractdump "+ setClause + whereClause;
                insertCount+=entityManager.createNativeQuery(final_sql).executeUpdate();
                entityManager.getTransaction().commit();
                entityManager.close();
            return insertCount;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
            //logger.warning("Error[" + ex.getMessage() + "] while processing delta for: OrganizationNumber[{" + d.getOrganizationNumber() + "}],customerNumber[{" + deltacontractdump.getCustomerNumber() + "}],RouteFrom[{" + deltacontractdump.getRouteFrom() + "}],RouteTo[{" + deltacontractdump.getRouteTo() + "}],ProdNo[{" + deltacontractdump.getProdNo() + "}],Price[{" + deltacontractdump.getBasePrice() + "}]");
            return 0;
        }
    }



    @Transactional
    public void updateDeltaAgreementsss(ArrayList<Deltacontractdump> deltacontractdumps, String remark, Boolean isUpdated) {

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            entityManager.getTransaction().begin();
            for (Deltacontractdump deltacontractdump : deltacontractdumps) {
                deltacontractdump.setRemark(remark);
                deltacontractdump.setUpdated(isUpdated);
                entityManager.merge(deltacontractdump);
            }
            entityManager.getTransaction().commit();
            System.out.println("insertContractComponent ContractComponent inserted successfully....");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {

            entityManager.close();
        }
    }

    @Transactional
    public void updateDeltaAgreementPERCENT(Percentagebaseddeltadump deltacontractdump, String remark, Boolean isUpdated) {

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            entityManager.getTransaction().begin();
            deltacontractdump.setRemark(remark);
            deltacontractdump.setUpdated(isUpdated);
            entityManager.merge(deltacontractdump);
            entityManager.getTransaction().commit();
            System.out.println("insertContractComponent ContractComponent inserted successfully....");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {

            entityManager.close();
        }
    }

    @Transactional
    public void updateDeltaAgreementsPERCENT(HashSet<Percentagebaseddeltadump> deltacontractdumps, String remark, Logger logger) {

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            entityManager.getTransaction().begin();
            for (Percentagebaseddeltadump deltacontractdump : deltacontractdumps) {
                deltacontractdump.setRemark(remark);
                deltacontractdump.setUpdated(true);
                entityManager.merge(deltacontractdump);
            }
            entityManager.getTransaction().commit();
            System.out.println("insertContractComponent ContractComponent inserted successfully....");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {

            entityManager.close();
        }
    }

    @Transactional
    public Long insertContractRole(ContractRole contractRole) {

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            LocalDateTime now = LocalDateTime.now();
            long txId = getTxId();
            contractRole.setCreatedByUser(AUTOMATION_USER);
            contractRole.setCreatedDt(LocalDate.now());
            contractRole.setLastUpdateTxId(txId);
            entityManager.getTransaction().begin();
            entityManager.merge(contractRole);
            entityManager.getTransaction().commit();
            System.out.println("insertContractRole ContractRole inserted successfully....");

            return txId;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            entityManager.close();
        }
        return null;
    }

    @Transactional
    public Long insertContractRoleSurcharge(ContractRole contractRole) {

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            LocalDateTime now = LocalDateTime.now();
            long txId = getTxId();
            contractRole.setCreatedByUser(AUTOMATION_USER);
            contractRole.setCreatedDt(LocalDate.now());
            contractRole.setLastUpdateTxId(txId);
            entityManager.getTransaction().begin();
            entityManager.merge(contractRole);
            entityManager.getTransaction().commit();
            System.out.println("insertContractRole ContractRole inserted successfully....");

            return txId;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            entityManager.close();
        }
        return null;
    }

    @Transactional
    public void updateStartDateINPrice(Long priceId, LocalDate startDate, LocalDate endDate) {
        try {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            String SQL="update core.price set start_dt=? , end_dt=?, last_update_dt=? where price_id= ?";
            
            Query q=entityManager.createNativeQuery(SQL);
            q.setParameter(1, java.sql.Date.valueOf(startDate));
            q.setParameter(2,java.sql.Date.valueOf(endDate));
            if (endDate != null)
                q.setParameter(3, java.sql.Date.valueOf(LocalDate.now()));
            else
            	q.setParameter(3, Types.DATE);
            q.setParameter(4, priceId);
            q.executeUpdate();
            entityManager.getTransaction().commit();
            entityManager.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }


    @Transactional
    public void updateEndDateINOldPrice(Long priceId, LocalDate endDate) {
        try {
            EntityManager entityManager=JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            String SQL="update core.price set end_dt=?, last_update_dt=? where price_id= ?";
            Query q= entityManager.createNativeQuery(SQL);
            q.setParameter(1, java.sql.Date.valueOf(endDate));
            q.setParameter(2, java.sql.Date.valueOf(LocalDate.now()));
            q.setParameter(3, priceId);
            q.executeUpdate();
            entityManager.getTransaction().commit();
            entityManager.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    @Transactional
    public Long insertPrice(Contractdump contractdump, Item item, String priceType) {
        try {

            //  Integer priceId = queryService.findMaxPriceId()+1;
            java.util.Date date = new java.util.Date();
            ZoneId defaultZone = ZoneId.of("Australia/Sydney");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");

            String startdate = "2022-01-01 00:00:00";
            Date parsedStartDate = df.parse(startdate);
            java.sql.Timestamp startDate = new java.sql.Timestamp(parsedStartDate.getTime());
            Instant fromInstant = startDate.toInstant();
            LocalDateTime fromdatetime = fromInstant.atZone(defaultZone).toLocalDateTime();
            Date fromDateFromDB = null;
            LocalDateTime fromDateDBByZone = null;
            if (null != contractdump.getFromDate()) {
                fromDateFromDB = contractdump.getFromDate();
                Instant fromInstantByDB = fromDateFromDB.toInstant();
                fromDateDBByZone = fromInstantByDB.atZone(defaultZone).toLocalDateTime();
            }

            String endDate = "2022-12-31 00:00:00";
            Date parsedEndDate = df.parse(endDate);
            Instant toInstant = parsedEndDate.toInstant();
            LocalDateTime todatetime = toInstant.atZone(defaultZone).toLocalDateTime();
            Date toDateFromDB = null;
            LocalDateTime toDateDBByZone = null;
            if (null != contractdump.getToDate()) {
                toDateFromDB = contractdump.getToDate();
                Instant toInstantByDB = toDateFromDB.toInstant();
                toDateDBByZone = toInstantByDB.atZone(defaultZone).toLocalDateTime();
            }
            EntityManager entityManager=JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            String SQL = INSERT_PRICE_SQL;
            Query q=entityManager.createNativeQuery(SQL);
            if (priceType.equals(ExcelService.BASE_PRICE))
            	q.setParameter(1, contractdump.getBasePrice());
            else
            	q.setParameter(1, Types.DOUBLE);

            q.setParameter(2, new java.sql.Date(date.getTime()));
            q.setParameter(3, AUTOMATION_USER);

            if (null != contractdump.getToDate())
            	q.setParameter(4, java.sql.Date.valueOf(toDateDBByZone.toLocalDate()));
            else
            	q.setParameter(4, java.sql.Date.valueOf(todatetime.toLocalDate()));

            q.setParameter(5, item.getItemId());
            q.setParameter(6, getTxId());
            q.setParameter(7, Types.DOUBLE);
            q.setParameter(8, Types.DOUBLE);
            q.setParameter(9, 3);
            if (priceType.equals(ExcelService.BASE_PRICE))
            	q.setParameter(10, 1);
            else
            	q.setParameter(10, 6);

            q.setParameter(11, Types.DOUBLE);
            q.setParameter(12, 0);
            q.setParameter(13, Types.INTEGER);
            q.setParameter(14, 1);
            q.setParameter(15, Types.INTEGER);
            q.setParameter(16, Types.INTEGER);
            q.setParameter(17, 3);
            q.setParameter(18, Types.INTEGER);
            if (null != contractdump.getFromDate())
            	q.setParameter(19, java.sql.Date.valueOf(fromDateDBByZone.toLocalDate()));
            else
            	q.setParameter(19, java.sql.Date.valueOf(fromdatetime.toLocalDate()));
            q.setParameter(20, Types.VARCHAR);
            q.setParameter(21, Types.DATE); // cehck what date is saving
            Long priceId = null;

            int i = q.executeUpdate();
            
            Integer id = (Integer) q.getSingleResult();
            priceId = id.longValue();
            entityManager.getTransaction().commit();
            entityManager.close();
            return priceId;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("exception while loading data of contractdump _ " + contractdump.getId());
            System.exit(1);
        }
        return null;
    }

    @Transactional
    public void insertPriceHistory(Price price) {
        try {
        	EntityManager entityManager=JPAUtil.getEntityManagerFactory().createEntityManager();
        	entityManager.getTransaction().begin();
            Query q = entityManager.createNativeQuery(INSERT_PRICE_HISTORY_SQL);

            if (null != price.getBasePrice())
            	q.setParameter(1, price.getBasePrice().doubleValue());
            else
            	q.setParameter(1, Types.DOUBLE);

          //  q.setParameter(2, createDt);
            q.setParameter(2, AUTOMATION_USER);
            q.setParameter(3, java.sql.Date.valueOf(price.getEndDt()));

            q.setParameter(4, price.getItemId());
            q.setParameter(5, getTxId());
            if(price.getPercentBasedPrice()!=null)
                q.setParameter(6, price.getPercentBasedPrice());
            else
                q.setParameter(6, Types.NULL);
            if(price.getPercentageAttributeTpCd()!=null)
                q.setParameter(7, price.getPercentageAttributeTpCd());
                else
                q.setParameter(7, Types.NULL);

            q.setParameter(8, price.getPriceCalcTpCd());
            q.setParameter(9, price.getPriceDefTpCd());
            q.setParameter(10, Types.NULL);
            q.setParameter(11, Types.NULL);
            q.setParameter(12, Types.NULL);
            q.setParameter(13, 1);
            q.setParameter(14, Types.NULL);
            q.setParameter(15, Types.NULL);
            q.setParameter(16, 3);

            q.setParameter(17, java.sql.Date.valueOf(price.getStartDt()));
            q.setParameter(18, AUTOMATION_USER);
          //  q.setParameter(21, Types.DATE); // cehck what date is saving
            q.setParameter(19, price.getPriceId());
            q.setParameter(20, new Date());
            int i = q.executeUpdate();
            entityManager.getTransaction().commit();
            entityManager.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Transactional
    public Long insertPercentageBasedPrice(Percentagebaseddump contractdump, Item item, String priceType) {
        try {
            java.util.Date date = new java.util.Date();
            Long priceId = null;
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
            String dt = "2022-01-01 00:00:00";
            Date parsedDate = df.parse(dt);
            java.sql.Timestamp startDate = new java.sql.Timestamp(parsedDate.getTime());

            String endDate = "2022-12-31 00:00:00";
            Date parsedEndDate = df.parse(endDate);
            java.sql.Timestamp endDt = new java.sql.Timestamp(parsedEndDate.getTime());

            EntityManager entityManager= JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            Query q=entityManager.createNativeQuery(INSERT_PERCENTAGEBASED_PRICE_SQL);
            //if(null!=contractdump.getPrecentageDiscount())
            Double d = new Double(contractdump.getPrecentageDiscount());
            // d = d * (-1);
            q.setParameter(1, d);
            q.setParameter(2, new java.sql.Date(date.getTime()));
            q.setParameter(3, AUTOMATION_USER);
            if (null != contractdump.getEnddate())
            	q.setParameter(4, new java.sql.Date(contractdump.getEnddate().getTime()));
            else
            	q.setParameter(4, new java.sql.Date(endDt.getTime()));

            q.setParameter(5, item.getItemId());
            q.setParameter(6, getTxId());
            q.setParameter(7, 7);
            q.setParameter(8, 1);

            q.setParameter(9, 3);
            q.setParameter(10, Types.DOUBLE);
            q.setParameter(11, 0);
            q.setParameter(12, Types.INTEGER);

            q.setParameter(13, 1);
            q.setParameter(14, Types.INTEGER);
            q.setParameter(15, Types.INTEGER);
            q.setParameter(16, 3);
            q.setParameter(17, Types.INTEGER);
            if (null != contractdump.getStartdate())
            	q.setParameter(18, new java.sql.Date(contractdump.getStartdate().getTime()));
            else
            	q.setParameter(18, new java.sql.Date(startDate.getTime()));
            int i = q.executeUpdate();
            entityManager.getTransaction().commit();
            Integer id = (Integer) q.getSingleResult();
            priceId = id.longValue();
            
            entityManager.close();
            return priceId;

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    private Integer getMaxPriceID() {
        Integer maxPriceID = null;
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        System.out.println("Inside getAllContractPrice() ");
        //List<Query> query = entityManager.createQuery(" from Price price where price.basePrice is null AND price.percentBasedPrice is null").getResultList();
        List<Query> query = entityManager.createNativeQuery("select max(price_id) from core.price").getResultList();
        Iterator listIterator = query.listIterator();
        while (listIterator.hasNext()) {
            maxPriceID = Integer.parseInt(listIterator.next().toString());
            maxPriceID = maxPriceID + 1;
        }
        return maxPriceID;

    }

    @Transactional
    public Long insertSurchargePrice(ContractComponent contractComponent, Item item, double percentValue) {
        try {
            java.util.Date date = new java.util.Date();
            Integer priceId = getMaxPriceID();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dt = "2022-01-01 00:00:00";
            Date parsedDate = df.parse(dt);
            java.sql.Timestamp startDate = new java.sql.Timestamp(parsedDate.getTime());

            String endDate = "2022-12-31 00:00:00";
            Date parsedEndDate = df.parse(endDate);
            java.sql.Timestamp endDt = new java.sql.Timestamp(parsedEndDate.getTime());
            EntityManager entityManager= JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            Query q = entityManager.createNativeQuery(INSERT_SURCHARGE_PERCENTAGEBASED_PRICE_SQL);
            //if(null!=contractdump.getPrecentageDiscount())
            q.setParameter(1, percentValue);
            q.setParameter(2, new java.sql.Date(date.getTime()));
            q.setParameter(3, AUTOMATION_USER);
            q.setParameter(4, new java.sql.Date(endDt.getTime()));

            q.setParameter(5, item.getItemId());
            q.setParameter(6, getTxId());
            q.setParameter(7, 6);
            q.setParameter(8, 5);

            q.setParameter(9, 3);
            q.setParameter(10, Types.DOUBLE);
            q.setParameter(11, 0);
            q.setParameter(12, Types.INTEGER);

            q.setParameter(13, 1);
            q.setParameter(14, Types.INTEGER);
            q.setParameter(15, Types.INTEGER);
            q.setParameter(16, 3);

            q.setParameter(17, Types.INTEGER);
            q.setParameter(18, new java.sql.Date(startDate.getTime()));
            q.setParameter(19, priceId.longValue());
            int i = q.executeUpdate();
            entityManager.getTransaction().commit();
            priceId = (Integer) q.getSingleResult();

            entityManager.close();
            return priceId.longValue();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    private synchronized long getTxId() {
        return new Long(StringUtils.right(String.valueOf(System.currentTimeMillis()), 9));


    }

    // Need to set contractId, contractPrice, contractRoles
    public ContractComponent buildContractComponent(Contract contract, Contractdump contractdump, Party party) {
        ZoneId defaultZone = ZoneId.of("Australia/Sydney");
        Instant fromInstant = contractdump.getFromDate().toInstant();
        LocalDateTime fromdateTime = fromInstant.atZone(defaultZone).toLocalDateTime();

        Instant toInstant = contractdump.getToDate().toInstant();
        LocalDateTime todateTime = toInstant.atZone(defaultZone).toLocalDateTime();

        ContractComponent newContractComponent = new ContractComponent();
        newContractComponent.setContract(contract);
        newContractComponent.setStartDt(fromdateTime.toLocalDate());
        newContractComponent.setEndDt(todateTime.toLocalDate());
        newContractComponent.setSourceSystemRecordPk(party.getSourceSystemRecordPk());

        newContractComponent.setLastUpdateUser(AUTOMATION_USER);
        newContractComponent.setContract(contract);
        newContractComponent.setSourceSystemRecordPk(contract.getSourceSystemRecordPk());
        newContractComponent.setContractComponentStTpCd(1);
        newContractComponent.setSourceSystemId(3);
        newContractComponent.setCreatedDt(LocalDate.now());
        newContractComponent.setLastUpdateDt(LocalDate.now());
        newContractComponent.setLastUpdateUser(AUTOMATION_USER);

        return newContractComponent;
    }

    // Need to set contractId, contractPrice, contractRoles
    public ContractComponent buildContractComponentForSurcharge(Contract contract) throws ParseException {

        java.util.Date date = new java.util.Date();
        ZoneId defaultZone = ZoneId.of("Australia/Sydney");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");

        String startdate = "2022-01-01 00:00:00";
        Date parsedStartDate = df.parse(startdate);
        java.sql.Timestamp startDate = new java.sql.Timestamp(parsedStartDate.getTime());
        Instant fromInstant = startDate.toInstant();
        LocalDateTime fromdatetime = fromInstant.atZone(defaultZone).toLocalDateTime();

        String endDate = "2022-12-31 00:00:00";
        Date parsedEndDate = df.parse(endDate);
        Instant toInstant = parsedEndDate.toInstant();
        LocalDateTime todatetime = toInstant.atZone(defaultZone).toLocalDateTime();

        ContractComponent newContractComponent = new ContractComponent();
        newContractComponent.setContract(contract);
        newContractComponent.setStartDt(fromdatetime.toLocalDate());
        newContractComponent.setEndDt(todatetime.toLocalDate());
        newContractComponent.setSourceSystemRecordPk(contract.getSourceSystemRecordPk());
        newContractComponent.setLastUpdateUser(AUTOMATION_USER);
        newContractComponent.setContract(contract);
        newContractComponent.setSourceSystemRecordPk(contract.getSourceSystemRecordPk());
        newContractComponent.setContractComponentStTpCd(1);
        newContractComponent.setSourceSystemId(3);
        newContractComponent.setCreatedDt(LocalDate.now());
        newContractComponent.setLastUpdateDt(LocalDate.now());
        newContractComponent.setLastUpdateUser(AUTOMATION_USER);

        return newContractComponent;
    }

    // Need to set contractId, contractPrice, contractRoles
    public ContractComponent buildPercentageBasedContractComponent(Contract contract, Percentagebaseddump contractdump, Party party) {
        ZoneId defaultZone = ZoneId.of("Australia/Sydney");
        Instant fromInstant = contractdump.getStartdate().toInstant();
        LocalDateTime fromdateTime = fromInstant.atZone(defaultZone).toLocalDateTime();

        Instant toInstant = contractdump.getStartdate().toInstant();
        LocalDateTime todateTime = toInstant.atZone(defaultZone).toLocalDateTime();

        ContractComponent newContractComponent = new ContractComponent();
        newContractComponent.setContract(contract);
        newContractComponent.setStartDt(fromdateTime.toLocalDate());

        newContractComponent.setSourceSystemRecordPk(party.getSourceSystemRecordPk());

        newContractComponent.setLastUpdateUser(AUTOMATION_USER);
        newContractComponent.setContract(contract);
        newContractComponent.setSourceSystemRecordPk(contract.getSourceSystemRecordPk());
        newContractComponent.setContractComponentStTpCd(1);
        newContractComponent.setSourceSystemId(3);
        newContractComponent.setCreatedDt(LocalDate.now());
        newContractComponent.setLastUpdateDt(LocalDate.now());
        newContractComponent.setLastUpdateUser(AUTOMATION_USER);

        return newContractComponent;
    }

//    public SlabBasedPrice insertSlabBasedPriceEntries(Price price) {
//        SlabBasedPrice slabBasedPrice = new SlabBasedPrice();
//        slabBasedPrice.setPrice(price);
//        slabBasedPrice.setSlabBasisTpCd(new Long(1));
//        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
//        entityManager.getTransaction().begin();
//        entityManager.merge(slabBasedPrice);
//        entityManager.getTransaction().commit();
//        System.out.println("SlabBasedPrice Data inserted/updated successfully....");
//        entityManager.close();
//
//        return slabBasedPrice;
//    }

    public void insertSlabbasedPriceEntries(List<Contractdump> dumps, Long slabBasedPriceId) {
        try {
            Collections.sort(dumps);
            Double maxVal = null;

            insertFirstSlabbasedEnteryValue(slabBasedPriceId, dumps.get(0));
            List<Double> slabs = new ArrayList<Double>();
            for (int i = 0; i < dumps.size(); i++) {
                if (dumps.get(i).getBasePrice() != null && dumps.get(i).getDiscLmtFrom() != null) {
                    if (i + 1 < dumps.size()) {
                        //maxVal = dumps.get(i + 1).getDiscLmtFrom(); // commented because its getting value highre then expected during the dat
                        maxVal = dumps.get(i + 1).getDiscLmtFrom(); // commented because its getting value highre then expected during the dat
                    } else
                        maxVal = new Double(9999);
                    if (i < dumps.size() - 1 && dumps.get(i + 1).getDiscLmtFrom().doubleValue() == maxVal)
                        insertSlabbasedEnteryValues(slabBasedPriceId, dumps.get(i), maxVal);
                    else
                        insertSlabbasedEnteryValues(slabBasedPriceId, dumps.get(i), maxVal);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private Long insertFirstSlabbasedEnteryValue(Long slabbasedId, Contractdump contractdump) {
        try {
        	EntityManager entityManager= JPAUtil.getEntityManagerFactory().createEntityManager();
        	entityManager.getTransaction().begin();
            Query q = entityManager.createNativeQuery(INSERT_SLABBASEDPRICEENTRIES_SQL);
            java.util.Date date = new java.util.Date();
            q.setParameter(1, slabbasedId);
            q.setParameter(2, 0);
            if (contractdump.getDiscLmtFrom() != null)
            	q.setParameter(3, contractdump.getDiscLmtFrom().intValue());
            else
            	q.setParameter(3, 9999);
            q.setParameter(4, contractdump.getBasePrice());
            q.setParameter(5, false);

            int i = q.executeUpdate();
            entityManager.getTransaction().commit();
            Integer id = (Integer) q.getSingleResult();
                slabbasedId = id.longValue();
                entityManager.close();

            //     slabbasedId = queryService.findMaxSlabbasedPriceId();

            return slabbasedId;
        } catch (NullPointerException nf) {
            nf.printStackTrace();
            System.exit(1);
        }catch (Exception sql) {
            System.out.println("Details \\:\\:  " + contractdump.getOrganizationNumber() + " ProdNo \\:\\:  " + contractdump.getProdNo());
            sql.printStackTrace();
            System.exit(1);
        } 
        return null;
    }

    private Long insertSlabbasedEnteryValues(Long slabbasedId, Contractdump contractdump, Double maxVal) {
        try {
        	EntityManager entityManager= JPAUtil.getEntityManagerFactory().createEntityManager();
        	entityManager.getTransaction().begin();
            Query q=entityManager.createNativeQuery(INSERT_SLABBASEDPRICEENTRIES_SQL);
            java.util.Date date = new java.util.Date();
            q.setParameter(1, slabbasedId);
            q.setParameter(2, contractdump.getDiscLmtFrom().intValue());
            q.setParameter(3, maxVal.intValue());
            if (contractdump.getDiscLmtFrom().intValue() + 1 > maxVal.intValue())
                System.out.println("wait   lklklk");
            if (contractdump.getDiscLmtFrom().toString().equals(maxVal.toString()))
                System.out.println("check here");

            q.setParameter(4, contractdump.getPrice());
            q.setParameter(5, false);

            int i = q.executeUpdate();
            entityManager.getTransaction().commit();
            Integer id = (Integer) q.getSingleResult();
                slabbasedId = id.longValue();

                entityManager.close();
            //     slabbasedId = queryService.findMaxSlabbasedPriceId();

            return slabbasedId;
        } catch (Exception sql) {
            sql.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public void insertSlabbasedPriceEntriesForItem(List<Contractdump> dumps, Long slabBasedPriceId) {
        try {
            Collections.sort(dumps);
            Double maxVal = null;

            insertFirstSlabbasedEnteryValueForITEMS(slabBasedPriceId, dumps.get(0));
            List<Double> slabs = new ArrayList<Double>();
            for (int i = 0; i < dumps.size(); i++) {
                if (dumps.get(i).getBasePrice() != null && dumps.get(i).getDiscLmtFrom() != null) {
                    if (i + 1 < dumps.size()) {
                        maxVal = dumps.get(i + 1).getDiscLmtFrom();
                    } else
                        maxVal = new Double(9999);
                    if (dumps.get(i).getDiscLmtFrom().doubleValue() == maxVal)
                        System.out.println("wait  fdfd");
                    insertSlabbasedEnteryValuesForITEMS(slabBasedPriceId, dumps.get(i), maxVal);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private Long insertFirstSlabbasedEnteryValueForITEMS(Long slabbasedId, Contractdump contractdump) {
        try {
        	EntityManager entityManager= JPAUtil.getEntityManagerFactory().createEntityManager();
        	entityManager.getTransaction().begin();
        	Query q = entityManager.createNativeQuery(INSERT_SLABBASEDPRICEENTRIES_SQL);
            java.util.Date date = new java.util.Date();
            q.setParameter(1, slabbasedId);
            q.setParameter(2, 0);
            if (contractdump.getDiscLmtFrom() != null) {
                if (contractdump.getDiscLmtFrom().intValue() > 1)
                	q.setParameter(3, contractdump.getDiscLmtFrom().intValue() - 1);
                else
                	q.setParameter(3, contractdump.getDiscLmtFrom().intValue());
            } else
            	q.setParameter(3, 9999);
            q.setParameter(4, contractdump.getBasePrice());
            q.setParameter(5, false);

            int i = q.executeUpdate();
            entityManager.getTransaction().commit();
            Integer id = (Integer) q.getSingleResult();
            slabbasedId = id.longValue();
            //     slabbasedId = queryService.findMaxSlabbasedPriceId();
            entityManager.close();
            return slabbasedId;
        } catch (NullPointerException nf) {
            nf.printStackTrace();
            System.exit(1);
        } catch (Exception sql) {
            System.out.println("Details ::  " + contractdump.getOrganizationNumber() + " ProdNo ::  " + contractdump.getProdNo());
            sql.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    private Long insertSlabbasedEnteryValuesForITEMS(Long slabbasedId, Contractdump contractdump, Double maxVal) {
        try {
        	EntityManager entityManager= JPAUtil.getEntityManagerFactory().createEntityManager();
        	entityManager.getTransaction().begin();
            Query q= entityManager.createNativeQuery(INSERT_SLABBASEDPRICEENTRIES_SQL);
            java.util.Date date = new java.util.Date();
            q.setParameter(1, slabbasedId);
            q.setParameter(2, contractdump.getDiscLmtFrom().intValue());
            if (maxVal.intValue() != 9999)
            	q.setParameter(3, maxVal.intValue() - 1);
            else
            	q.setParameter(3, maxVal.intValue());
            if (contractdump.getDiscLmtFrom().toString().equals(maxVal.toString()))
                System.out.println("check here");
            q.setParameter(4, contractdump.getPrice());
            q.setParameter(5, false);

            int i = q.executeUpdate();
            entityManager.getTransaction().commit();
            Integer id = (Integer) q.getSingleResult();
            slabbasedId = id.longValue();
            //     slabbasedId = queryService.findMaxSlabbasedPriceId();
            entityManager.close();
            return slabbasedId;
       
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public void insertContractPrice(Contractdump contractdump, Item item, Long priceId, ContractComponent contractComponent, Boolean isServicePassiveReturned) {

        Country fromCountry = null;
        Country toCountry = null;
        String fromPostalCode = null;
        String toPostalCode = null;
        String fromZone = null;
        String toZone = null;
        Integer toZoneId349 = null;
        String zoneType = null;

        Integer fromCustomZone = null;
        Integer toCustomZone = null;

        if (isServicePassiveReturned)
            System.out.println("isServicePassiveReturned is true");

        ArrayList<Integer> excludedServiceList = new ArrayList<Integer>();
        excludedServiceList.add(349);
        excludedServiceList.add(34964);

        ArrayList<Integer> service336List = service336List();

        List<String> countryCodes = new ArrayList<String>();
        List<Country> countries = new ArrayList<Country>();
        if (null != contractdump.getRouteType() && contractdump.getRouteType().equals("P") && null != contractdump.getRouteFrom()) {
            if (contractdump.getRouteFrom().toCharArray().length > 2) {
                fromPostalCode = contractdump.getRouteFrom().substring(2, contractdump.getRouteFrom().toCharArray().length);
                countryCodes.add(contractdump.getRouteFrom().substring(0, 2));
            } else
                countryCodes.add(contractdump.getRouteFrom());
        }
        if (null != contractdump.getRouteType() && contractdump.getRouteType().equals("P") && null != contractdump.getRouteTo()) {
            if (contractdump.getRouteTo().toCharArray().length > 2) {
                if (!excludedServiceList.contains(contractdump.getProdNo())) {
                    toPostalCode = contractdump.getRouteTo().substring(2, contractdump.getRouteTo().toCharArray().length);
                    countryCodes.add(contractdump.getRouteTo().substring(0, 2));
                } else {
                    toPostalCode = contractdump.getRouteTo().substring(2, contractdump.getRouteTo().toCharArray().length);
                    countryCodes.add(contractdump.getRouteTo().substring(0, 2));
                }
                // before inserting in prod for 349
//                else {
//                    Cdhomedeliverytp cdhomedeliverytp = queryService.findCdhomedeliverytp(contractdump.getRouteTo());
//                    if (null != cdhomedeliverytp)
//                        toZoneId349 = cdhomedeliverytp.getZoneId();
//                    else
//                        System.out.println("wait -- no");
//                    countryCodes.add(contractdump.getRouteTo().substring(0, 2));
//                }
            } else
                countryCodes.add(contractdump.getRouteTo());
        }

        if (null != contractdump.getRouteType() && contractdump.getRouteType().equals("Z") && null != contractdump.getRouteFrom()) {
            if (!excludedServiceList.contains(contractdump.getProdNo()) && null != contractdump.getZoneType()
                    && contractdump.getZoneType().equals("S")) {
                if (contractdump.getRouteFrom().toCharArray().length > 2)
                    fromZone = contractdump.getRouteFrom().substring(2, contractdump.getRouteFrom().toCharArray().length);
                countryCodes.add(contractdump.getRouteFrom().substring(0, 2));
            } else if (!excludedServiceList.contains(contractdump.getProdNo()) && null != contractdump.getZoneType() &&
                    contractdump.getZoneType().equals("C")) {
                Cdcustomcountryroutetp cdcustomcountryroutetp = queryService.findCdcustomcountryroutetp(contractdump.getRouteFrom());
                if (null != cdcustomcountryroutetp)
                    fromCustomZone = cdcustomcountryroutetp.getRouteId();
                else {
                    Cdhomedeliverytp cdhomedeliverytp = queryService.findCdhomedeliverytp(contractdump.getRouteTo());
//                    String countryCode = contractdump.getRouteFrom().substring(0, 2);
//                    Country country = queryService.findCountry(countryCode);
//                    cdcustomcountryroutetp = upsertCdcustomcountryroutetp(country.getCountryTpCd(), contractdump.getRouteFrom());
                    toZoneId349 = cdhomedeliverytp.getZoneId();
                    fromCountry = queryService.findCountry(contractdump.getRouteFrom());
                }
            } else if (excludedServiceList.contains(contractdump.getProdNo())) {
                String countryCode = contractdump.getRouteFrom().substring(0, 2);
                Country country = queryService.findCountry(countryCode);
                fromCountry = country;
            }
            zoneType = contractdump.getZoneType();
        }
        if (null != contractdump.getRouteType() && contractdump.getRouteType().equals("Z") && null != contractdump.getRouteTo()) {
            if (contractdump.getRouteTo().toCharArray().length > 2) {
                if (!excludedServiceList.contains(contractdump.getProdNo()) && null != contractdump.getZoneType() && contractdump.getZoneType().equals("S")) {
                    if (service336List.contains(contractdump.getProdNo())) {
                        toZone = contractdump.getRouteTo().substring(2, contractdump.getRouteTo().toCharArray().length);
                        countryCodes.add(contractdump.getRouteTo().substring(0, 2));
                    } else {
                        if (null != contractdump.getRouteFrom() && null != contractdump.getRouteTo() &&
                                !contractdump.getRouteFrom().equals(contractdump.getRouteTo())) {
                            countryCodes.add(contractdump.getRouteTo().substring(0, 2));
                        } else if (contractdump.getProdNo().equals(330) && null == contractdump.getRouteFrom() && null != contractdump.getRouteTo()) {
                            countryCodes.add(contractdump.getFileCountry());
                            System.out.println("route from cannot be null .. " + contractdump.getId());
                            System.exit(1);
                        } else if (contractdump.getProdNo().equals(33064) && null == contractdump.getRouteFrom() && null != contractdump.getRouteTo()) {
                            countryCodes.add(contractdump.getFileCountry());
                            System.out.println("route from cannot be null .. " + contractdump.getId());
                            System.exit(1);
                        }
                        toZone = contractdump.getRouteTo().substring(2, contractdump.getRouteTo().toCharArray().length);
                    }
                } else if (!excludedServiceList.contains(contractdump.getProdNo()) && null != contractdump.getZoneType() && contractdump.getZoneType().equals("C")) {
                    Cdcustomcountryroutetp cdcustomcountryroutetp = queryService.findCdcustomcountryroutetp(contractdump.getRouteTo());
                    if (null != cdcustomcountryroutetp)
                        toCustomZone = cdcustomcountryroutetp.getRouteId();
                    else {
                        String countryCode = null;
                        if (null != contractdump.getRouteFrom()) {
                            countryCode = contractdump.getRouteFrom().substring(0, 2);
                            Country country = queryService.findCountry(countryCode);
                          //  cdcustomcountryroutetp = upsertCdcustomcountryroutetp(country.getCountryTpCd(), contractdump.getRouteTo());
                            if(null!= cdcustomcountryroutetp) {
                                toCustomZone = cdcustomcountryroutetp.getRouteId();
                            }
//                            else{
//                                Cdhomedeliverytp cdhomedeliverytp = queryService.findCdhomedeliverytp(contractdump.getRouteTo());
//                                toZoneId349 = cdhomedeliverytp.getZoneId();
//                                fromCountry = queryService.findCountry(contractdump.getRouteFrom());
//                            }

                        } else {
                            countryCode = null;// earlier discuss that in case of zone, from and to will never be null, but neha said in UI that its possible
//                            System.out.println("wait");
//                            System.exit(1);
                        }
                    }
                } else if (excludedServiceList.contains(contractdump.getProdNo()) && contractdump.getRouteTo() != null) {
                    Cdhomedeliverytp cdhomedeliverytp = queryService.findCdhomedeliverytp(contractdump.getRouteTo());
                    if (null != cdhomedeliverytp)
                        toZoneId349 = cdhomedeliverytp.getZoneId();
                    else {
                        System.out.println("wait --njj   contractdump id " + contractdump.getId());
                        //  System.exit(1);
                    }

                    //   String countryCode = contractdump.getRouteTo().substring(0, 2);
                    //    toCountry = queryService.findCountry(countryCode);
                }
            } else if (contractdump.getRouteTo().toCharArray().length == 2) {
                if (service336List.contains(contractdump.getProdNo())) {
                    countryCodes.add(contractdump.getRouteTo());
                } else if (excludedServiceList.contains(contractdump.getProdNo())) {
                    countryCodes.add(contractdump.getRouteTo());
                    Cdhomedeliverytp cdhomedeliverytp = queryService.findCdhomedeliverytp(contractdump.getRouteTo());
                    if (null != cdhomedeliverytp)
                        toZoneId349 = cdhomedeliverytp.getZoneId();
                    else {
                        System.out.println("wait --njj" + contractdump.getId());
                        System.exit(1);
                    }
                } else {
                    if (contractdump.getRouteFrom() != null && contractdump.getRouteTo() != null &&
                            !contractdump.getRouteFrom().equals(contractdump.getRouteTo()) && !excludedServiceList.contains(contractdump.getProdNo())
                            && service336List.contains(contractdump.getProdNo())) {
                        countryCodes.add(contractdump.getRouteTo().substring(0, 2));
                    }
                }
            } else {
                if (service336List.contains(contractdump.getProdNo()) && contractdump.getRouteTo() == null) {
                    toCountry = queryService.findCountry("SE");
                }
                System.out.println("NOT POSSIBLE");
                System.exit(1);
            }
            zoneType = contractdump.getZoneType();
        }

        if (null == contractdump.getRouteType() && null != contractdump.getRouteFrom()) {
            if (contractdump.getRouteFrom().toCharArray().length == 2)
                countryCodes.add(contractdump.getRouteFrom());
            else {
                System.out.println("Some data is wrong. route type is null be route from contains mode details than country code only " + contractdump.getId());
                System.exit(1);
            }
        }
        if (null == contractdump.getRouteType() && null != contractdump.getRouteTo()) {
            countryCodes.add(contractdump.getRouteTo());
            if (excludedServiceList.contains(contractdump.getProdNo()) && contractdump.getZoneType() != null) {
                Cdhomedeliverytp cdhomedeliverytp = queryService.findCdhomedeliverytp(contractdump.getRouteTo());
                if (null != cdhomedeliverytp)
                    toZoneId349 = cdhomedeliverytp.getZoneId();
                else {
                    System.out.println("Cdhomedeliverytp not found - contractdump table primary key -  " + contractdump.getId());
                    System.exit(1);
                }
            }
        }

        //  if(null==contractdump.getRouteType() || contractdump.getRouteType().equals("P")) {
        if (!countryCodes.isEmpty()) {
            countries = queryService.findCountries(countryCodes);
            if (countries.size() > 2)
                System.out.println("break");
            for (Country country : countries) {

                if (null != contractdump.getRouteFrom() && country.getCountryCode().equals(contractdump.getRouteFrom().substring(0, 2)))
                    fromCountry = country;
                if (null != contractdump.getRouteTo() && country.getCountryCode().equals(contractdump.getRouteTo().substring(0, 2)))
                    toCountry = country;
            }
        }

        try {
        	EntityManager entityManager= JPAUtil.getEntityManagerFactory().createEntityManager();
        	entityManager.getTransaction().begin();
            Query q= entityManager.createNativeQuery(INSERT_CONTRACTPRICE_SQL);
            Long longNull = null;
            q.setParameter(1, priceId);
            q.setParameter(2, Types.BIGINT);
            q.setParameter(3, 3);
            q.setParameter(4, Types.BIGINT);
            // if(contractdump.getKgTill()!=null)
            //   stmt.setInt(5, Integer.parseInt(contractdump.getKgTill()));
            //else
            q.setParameter(5, Types.BIGINT);
            q.setParameter(6, contractComponent.getContractComponentId());
            q.setParameter(7, Types.BIGINT);
            q.setParameter(8, Types.VARCHAR);
            q.setParameter(9, Types.BIGINT);
            Item itemWithoutPassiveReturn = item;
            item = null;
            Integer vaueOfourney = null;
            item = queryService.getNormalItemId(contractdump.getProdNo());
            if (null != item) {
            	q.setParameter(10, item.getItemId());
                if (isServicePassiveReturned)
                    vaueOfourney = 2;
                else
                    vaueOfourney = 1;
            } else if (isServicePassiveReturned) {
                item = itemWithoutPassiveReturn;
                q.setParameter(10, item.getItemId());
                vaueOfourney = 2;
            } else {
                item = queryService.getPassiveItemId2(contractdump.getProdNo());
                if (item != null && contractdump.getProdNo().toString().startsWith("64", contractdump.getProdNo().toString().length() - 2)) {
                    Integer temp = Integer.parseInt(contractdump.getProdNo().toString().substring(0, contractdump.getProdNo().toString().length() - 2));
                    Item itemForAadd = queryService.getNormalItemId(temp);
                    q.setParameter(10, itemForAadd.getItemId());
                    vaueOfourney = 2;
                } else {
                    item = queryService.getPassiveItemId(contractdump.getProdNo());
                    if (null != item) {
                    	q.setParameter(10, item.getItemId());
                        vaueOfourney = 1;
                    } else {
                        System.out.println(contractdump.getOrganizationNumber() + " ::: ID " + contractdump.getId());
                        System.exit(1);
                    }
                }
            }

            q.setParameter(11, Types.BIGINT);
            if (null != fromCountry)
            	q.setParameter(12, fromCountry.getCountryTpCd());
            else
            	q.setParameter(12, Types.BIGINT);

            if (fromPostalCode != null)
            	q.setParameter(13, fromPostalCode);
            else
            	q.setParameter(13, Types.VARCHAR);

            if (null != toCountry)
            	q.setParameter(14, toCountry.getCountryTpCd());
            else
            	q.setParameter(14, Types.BIGINT);

            if (toPostalCode != null)
            	q.setParameter(15, toPostalCode);
            else
            	q.setParameter(15, Types.VARCHAR);

            if (null != fromZone)
            	q.setParameter(16, Integer.parseInt(fromZone));
            else
            	q.setParameter(16, Types.BIGINT);

            if (null != toZone)
            	q.setParameter(17, Integer.parseInt(toZone));
            else
            	q.setParameter(17, Types.BIGINT);
            q.setParameter(18, contractdump.getCurr());

            if (toZoneId349 != null)
            	q.setParameter(19, toZoneId349);
            else
            	q.setParameter(19, Types.BIGINT);

            // add 20th here
            if (vaueOfourney != null)
            	q.setParameter(20, vaueOfourney);
            else
            	q.setParameter(20, Types.BIGINT);
            if (null != contractdump.getZoneType() && contractdump.getZoneType().equals("C")) {
            	q.setParameter(21, 5);
            } else if (null != contractdump.getZoneType() && contractdump.getZoneType().equals("S")) {
            	q.setParameter(21, 4);
            } else {
            	q.setParameter(21, Types.INTEGER);
            }

            if (null != fromCustomZone)
            	q.setParameter(22, fromCustomZone);
            else
            	q.setParameter(22, Types.INTEGER);

            if (null != toCustomZone)
            	q.setParameter(23, toCustomZone);
            else
            	q.setParameter(23, Types.INTEGER);

            int i = q.executeUpdate();
            entityManager.getTransaction().commit();
            return;
        } catch (NullPointerException ne) {
            System.out.println("Error comes here in contractcomonent table " + contractComponent.getContractComponentId());
            System.out.println();
            System.exit(1);
        } catch (Exception sql) {
            sql.printStackTrace();
            System.out.println("Contractdump " + contractdump.getId());
            System.exit(1);
        }
        return;
    }

    // surcharge exemption
    public Boolean insertContractPriceExemmption(ContractComponent contractComponent, Item item, Long priceId) {
        Boolean status = false;
        try {
        	EntityManager entityManager=JPAUtil.getEntityManagerFactory().createEntityManager();
        	entityManager.getTransaction().begin();
            Query q = entityManager.createNativeQuery(INSERT_CONTRACTPRICE_SQL);
            Long longNull = null;
            q.setParameter(1, priceId);
            q.setParameter(2, Types.BIGINT);
            q.setParameter(3, 2);
            //  stmt.setNull(4, Types.BIGINT);
            if (item.getItemId().equals(new Long(2316)) || item.getItemId().equals(new Long(2318)))
            	q.setParameter(4, 3205);
            else if (item.getItemId().equals(new Long(2317)) || item.getItemId().equals(new Long(2319)))
            	q.setParameter(4, 3216);
            q.setParameter(5, Types.BIGINT);
            q.setParameter(6, contractComponent.getContractComponentId());
            q.setParameter(7, Types.BIGINT);
            q.setParameter(8, Types.VARCHAR);
            q.setParameter(9, Types.BIGINT);
            q.setParameter(10, item.getItemId());
            q.setParameter(11, Types.BIGINT);
            q.setParameter(12, Types.BIGINT);
            q.setParameter(13, Types.VARCHAR);
            q.setParameter(14, Types.BIGINT);
            q.setParameter(15, Types.VARCHAR);
            q.setParameter(16, Types.BIGINT);
            q.setParameter(17, Types.BIGINT);
            q.setParameter(18, Types.VARCHAR);
            q.setParameter(19, Types.BIGINT);
            q.setParameter(20, Types.BIGINT);
            q.setParameter(21, 6);
            q.setParameter(22, Types.INTEGER);
            q.setParameter(23, Types.INTEGER);


            int i = q.executeUpdate();
            entityManager.getTransaction().commit();
            entityManager.close();
            return true;
        } catch (NullPointerException ne) {
            ne.printStackTrace();
            System.exit(1);
        } catch (Exception sql) {
            sql.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public void insertPercentageBasedContractPrice(Percentagebaseddump contractdump, Item item, Long priceId, ContractComponent contractComponent) {

        Country fromCountry = null;
        Country toCountry = null;
        String fromPostalCode = null;
        String toPostalCode = null;
        String fromZone = null;
        String toZone = null;
        Integer toZoneId349 = null;
        String zoneType = null;

        Integer fromCustomZone = null;
        Integer toCustomZone = null;

        ArrayList<Integer> excludedServiceList = new ArrayList<Integer>();
        excludedServiceList.add(349);
        excludedServiceList.add(34964);

        ArrayList<Integer> service336List = service336List();

        List<String> countryCodes = new ArrayList<String>();
        List<Country> countries = new ArrayList<Country>();

        // NEW CONDITION OF R
        if (null != contractdump.getRouteType() && contractdump.getRouteType().equals("R") && null != contractdump.getFromLocation()) {
            if (contractdump.getFromLocation().toCharArray().length > 2) {
                fromPostalCode = contractdump.getFromLocation().substring(2, contractdump.getFromLocation().toCharArray().length);
                countryCodes.add(contractdump.getFromLocation().substring(0, 2));
            } else if (contractdump.getFromLocation().toCharArray().length == 2)
                countryCodes.add(contractdump.getFromLocation());
        }
        if (null != contractdump.getRouteType() && contractdump.getRouteType().equals("R") && null != contractdump.getToLocation()) {
            if (contractdump.getToLocation().toCharArray().length > 2) {
                if (!excludedServiceList.contains(contractdump.getProdno())) {
                    toPostalCode = contractdump.getToLocation().substring(2, contractdump.getToLocation().toCharArray().length);
                    countryCodes.add(contractdump.getToLocation().substring(0, 2));
                } else {
                    //countryCodes.add(contractdump.getToLocation().substring(0, 2));
                    Cdhomedeliverytp cdhomedeliverytp = queryService.findCdhomedeliverytp(contractdump.getToLocation());
                    if (null != cdhomedeliverytp)
                        toZoneId349 = cdhomedeliverytp.getZoneId();
                    else
                        System.out.println("wait  bb");
                    countryCodes.add(contractdump.getToLocation().substring(0, 2));
                }
            } else if (contractdump.getToLocation().toCharArray().length == 2)
                countryCodes.add(contractdump.getToLocation());
        }

        ////   END NEW CONDITION OF R


        if (null != contractdump.getRouteType() && contractdump.getRouteType().equals("P") && null != contractdump.getFromLocation()) {
            if (contractdump.getFromLocation().toCharArray().length > 2) {
                fromPostalCode = contractdump.getFromLocation().substring(2, contractdump.getFromLocation().toCharArray().length);
                countryCodes.add(contractdump.getFromLocation().substring(0, 2));
            } else
                countryCodes.add(contractdump.getFromLocation());
        }
        if (null != contractdump.getRouteType() && contractdump.getRouteType().equals("P") && null != contractdump.getToLocation()) {
            if (contractdump.getToLocation().toCharArray().length > 2) {
                if (!excludedServiceList.contains(contractdump.getProdno())) {
                    toPostalCode = contractdump.getToLocation().substring(2, contractdump.getToLocation().toCharArray().length);
                    countryCodes.add(contractdump.getToLocation().substring(0, 2));
                } else {
                    Cdhomedeliverytp cdhomedeliverytp = queryService.findCdhomedeliverytp(contractdump.getToLocation());
                    if (null != cdhomedeliverytp)
                        toZoneId349 = cdhomedeliverytp.getZoneId();
                    else
                        System.out.println("wait gg");
                    countryCodes.add(contractdump.getToLocation().substring(0, 2));
                }
            } else
                countryCodes.add(contractdump.getToLocation());
        }

        if (null != contractdump.getRouteType() && contractdump.getRouteType().equals("Z") && null != contractdump.getFromLocation()) {
            if (!excludedServiceList.contains(contractdump.getProdno()) && null != contractdump.getZoneType() &&
                    contractdump.getZoneType().equals("S")) {
                if (contractdump.getFromLocation().toCharArray().length > 2)
                    fromZone = contractdump.getFromLocation().substring(2, contractdump.getFromLocation().toCharArray().length);
                countryCodes.add(contractdump.getFromLocation().substring(0, 2));
            } else if (!excludedServiceList.contains(contractdump.getProdno()) && null != contractdump.getZoneType()
                    && contractdump.getZoneType().equals("C")) {
                Cdcustomcountryroutetp cdcustomcountryroutetp = queryService.findCdcustomcountryroutetp(contractdump.getFromLocation());
                if (null != cdcustomcountryroutetp)
                    fromCustomZone = cdcustomcountryroutetp.getRouteId();
                else {
                    String countryCode = contractdump.getFromLocation().substring(0, 2);
                    Country country = queryService.findCountry(countryCode);
                    Cdhomedeliverytp cdhomedeliverytp = queryService.findCdhomedeliverytp(contractdump.getToLocation());
                    if (null != cdhomedeliverytp)
                        toZoneId349 = cdhomedeliverytp.getZoneId();
//                    cdcustomcountryroutetp = upsertCdcustomcountryroutetp(country.getCountryTpCd(), contractdump.getFromLocation());
//                    fromCustomZone = cdcustomcountryroutetp.getRouteId();
                }
            }
            countryCodes.add(contractdump.getFromLocation().substring(0, 2));
            zoneType = contractdump.getZoneType();
        }

        if (null != contractdump.getRouteType() && contractdump.getRouteType().equals("Z") && null != contractdump.getToLocation()) {
            if (contractdump.getToLocation().toCharArray().length > 2) {
                if (!excludedServiceList.contains(contractdump.getProdno()) && null != contractdump.getZoneType() && contractdump.getZoneType().equals("S")) {
                    if (service336List.contains(contractdump.getProdno())) {
                        toZone = contractdump.getToLocation().substring(2, contractdump.getFromLocation().toCharArray().length);
                        countryCodes.add(contractdump.getToLocation().substring(0, 2));
                    } else {
                        if (null != contractdump.getFromLocation() && null != contractdump.getToLocation() &&
                                !contractdump.getFromLocation().equals(contractdump.getToLocation())) {
                            countryCodes.add(contractdump.getToLocation().substring(0, 2));
                        }
                        toZone = contractdump.getToLocation().substring(2, contractdump.getToLocation().toCharArray().length);
                    }
                } else if (!excludedServiceList.contains(contractdump.getProdno()) && null != contractdump.getZoneType() && contractdump.getZoneType().equals("C")) {
                    Cdcustomcountryroutetp cdcustomcountryroutetp = queryService.findCdcustomcountryroutetp(contractdump.getToLocation());
                    if (null != cdcustomcountryroutetp)
                        toCustomZone = cdcustomcountryroutetp.getRouteId();
                    else {
                        String countryCode = contractdump.getFromLocation().substring(0, 2);
                        Country country = queryService.findCountry(countryCode);
                        cdcustomcountryroutetp = upsertCdcustomcountryroutetp(country.getCountryTpCd(), contractdump.getToLocation());
                        toCustomZone = cdcustomcountryroutetp.getRouteId();
                    }
                } else if (excludedServiceList.contains(contractdump.getProdno())) {
                    Cdhomedeliverytp cdhomedeliverytp = queryService.findCdhomedeliverytp(contractdump.getToLocation());
                    if (null != cdhomedeliverytp)
                        toZoneId349 = cdhomedeliverytp.getZoneId();
                    else {
                        System.out.println("wait kj");
                        System.exit(1);
                    }
                }
            } else if (contractdump.getToLocation().toCharArray().length == 2) {
                if (service336List.contains(contractdump.getProdno())) {
                    countryCodes.add(contractdump.getToLocation());
                } else {
                    if (contractdump.getFromLocation() != null && contractdump.getToLocation() != null &&
                            !contractdump.getFromLocation().equals(contractdump.getToLocation()) && !excludedServiceList.contains(contractdump.getProdno())
                            && service336List.contains(contractdump.getProdno())) {
                        countryCodes.add(contractdump.getToLocation().substring(0, 2));
                    }
                }
            } else {
                System.out.println("NOT POSSIBLE");
                System.exit(1);
            }
            zoneType = contractdump.getZoneType();
        }

        if (null == contractdump.getRouteType() && null != contractdump.getFromLocation()) {
            if (contractdump.getFromLocation().toCharArray().length == 2)
                countryCodes.add(contractdump.getFromLocation());
            else {
                System.out.println("Some data is wrong. route type is null be route from contains mode details than country code only" + contractdump.getId());
                System.exit(1);
            }
        }
        if (null == contractdump.getRouteType() && null != contractdump.getToLocation()) {
            if (contractdump.getToLocation().toCharArray().length == 2)
                countryCodes.add(contractdump.getToLocation());
            else {
                System.out.println("Some data is wrong. route type is null be route from contains mode details than country code only" + contractdump.getId());
                System.exit(1);
            }
        }

        //  if(null==contractdump.getRouteType() || contractdump.getRouteType().equals("P")) {
        if (!countryCodes.isEmpty()) {
            if (countryCodes.size() > 2)
                System.out.println("wait... ERROR");
            countries = queryService.findCountries(countryCodes);
            for (Country country : countries) {
                if (null != contractdump.getFromLocation() && country.getCountryCode().equals(contractdump.getFromLocation().substring(0, 2)))
                    fromCountry = country;
                if (null != contractdump.getToLocation() && country.getCountryCode().equals(contractdump.getToLocation().substring(0, 2)))
                    toCountry = country;
            }
        }
        ///}

        try {
        	EntityManager entityManager= JPAUtil.getEntityManagerFactory().createEntityManager();
        	entityManager.getTransaction().begin();
            Query q = entityManager.createNativeQuery(INSERT_CONTRACTPRICE_SQL);// Percentage based
            Long longNull = null;
            q.setParameter(1, priceId);
            q.setParameter(2, Types.BIGINT);
            q.setParameter(3, 3);
            q.setParameter(4, Types.BIGINT);
            // if(contractdump.getKgTill()!=null)
            //   stmt.setInt(5, Integer.parseInt(contractdump.getKgTill()));
            //else
            q.setParameter(5, Types.BIGINT);
            q.setParameter(6, contractComponent.getContractComponentId());
            q.setParameter(7, Types.BIGINT);
            q.setParameter(8, Types.VARCHAR);
            q.setParameter(9, Types.BIGINT);
            item = null;
            Integer vaueOfourney = null;
            item = queryService.getNormalItemId(contractdump.getProdno());
            if (null != item) {
            	q.setParameter(10, item.getItemId());
                vaueOfourney = 1;
            } else {
                item = queryService.getPassiveItemId2(contractdump.getProdno());
                if (item != null && contractdump.getProdno().toString().startsWith("64", contractdump.getProdno().toString().length() - 2)) {
                    Integer temp = Integer.parseInt(contractdump.getProdno().toString().substring(0, contractdump.getProdno().toString().length() - 2));
                    Item itemForAadd = queryService.getNormalItemId(temp);
                    q.setParameter(10, itemForAadd.getItemId());
                    vaueOfourney = 2;
                } else {
                    item = queryService.getPassiveItemId(contractdump.getProdno());
                    if (null != item) {
                    	q.setParameter(10, item.getItemId());
                        vaueOfourney = 1;
                    } else {
                        System.out.println(contractdump.getParentCustomerNumber() + " ::: ID " + contractdump.getId());
                        System.exit(1);
                    }
                }
            }

            q.setParameter(11, Types.BIGINT);
            if (null != fromCountry)
            	q.setParameter(12, fromCountry.getCountryTpCd());
            else
            	q.setParameter(12, Types.BIGINT);

            if (fromPostalCode != null)
            	q.setParameter(13, fromPostalCode);
            else
            	q.setParameter(13, Types.VARCHAR);

            if (null != toCountry)
            	q.setParameter(14, toCountry.getCountryTpCd());
            else
            	q.setParameter(14, Types.BIGINT);

            if (toPostalCode != null)
            	q.setParameter(15, toPostalCode);
            else
            	q.setParameter(15, Types.VARCHAR);

            if (null != fromZone)
            	q.setParameter(16, Integer.parseInt(fromZone));
            else
            	q.setParameter(16, Types.BIGINT);

            if (null != toZone)
            	q.setParameter(17, Integer.parseInt(toZone));
            else
            	q.setParameter(17, Types.BIGINT);
            q.setParameter(18, Types.VARCHAR);

            if (toZoneId349 != null)
            	q.setParameter(19, toZoneId349);
            else
            	q.setParameter(19, Types.BIGINT);

            // add 20th here
            if (vaueOfourney != null)
            	q.setParameter(20, vaueOfourney);
            else
            	q.setParameter(20, Types.BIGINT);
            if (null != contractdump.getZoneType() && contractdump.getZoneType().equals("C")) {
            	q.setParameter(21, 5);
            } else if (null != contractdump.getZoneType() && contractdump.getZoneType().equals("S")) {
            	q.setParameter(21, 4);
            } else {
            	q.setParameter(21, Types.INTEGER);
            }
            q.setParameter(22, Types.INTEGER);
            q.setParameter(23, Types.INTEGER);


            int i = q.executeUpdate();
            entityManager.getTransaction().commit();
            entityManager.close();
            return;
        } catch (NullPointerException ne) {
            ne.printStackTrace();
            System.exit(1);
        } catch (Exception sql) {
            sql.printStackTrace();
            System.exit(1);
        }
        return;
    }

    @Transactional
    public void updateDeltaAggRemovePrice(Long id) {
        try {
        	EntityManager entityManager=JPAUtil.getEntityManagerFactory().createEntityManager();
        	entityManager.getTransaction().begin();
        	String query = "update core.deltacontractdump set price_id=null where price_id = " + id;
            Query q = entityManager.createNativeQuery(query);
            q.executeUpdate();
            entityManager.getTransaction().commit();
            entityManager.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void insertContractPriceHistory(ContractPrice contractPrice) {

        try {
        	EntityManager entityManager=JPAUtil.getEntityManagerFactory().createEntityManager();
        	entityManager.getTransaction().begin();
        	Query q = entityManager.createNativeQuery(INSERT_CONTRACTPRICE_HISTORY_SQL);
            Long longNull = null;
            q.setParameter(1, contractPrice.getPriceId());
            q.setParameter(2, Types.BIGINT);
            q.setParameter(3, 3);
            q.setParameter(4, Types.BIGINT);
            // if(contractdump.getKgTill()!=null)
            //   stmt.setInt(5, Integer.parseInt(contractdump.getKgTill()));
            //else
            if (null != contractPrice.getPriority())
            	q.setParameter(5, contractPrice.getPriority());
            else
            	q.setParameter(5, Types.BIGINT);

            q.setParameter(6, contractPrice.getContractcomponentId());
            q.setParameter(7, Types.BIGINT);
            q.setParameter(8, Types.VARCHAR);
            q.setParameter(9, Types.BIGINT);
            q.setParameter(10, contractPrice.getItemIdDup());
            q.setParameter(11, Types.BIGINT);
            if (null != contractPrice.getFromCountry())
            	q.setParameter(12, contractPrice.getFromCountry());
            else
            	q.setParameter(12, Types.INTEGER);
            if (null != contractPrice.getFromPostalCode())
            	q.setParameter(13, contractPrice.getFromPostalCode());
            else
            	q.setParameter(13, Types.INTEGER);

            if (null != contractPrice.getToCountry())
            	q.setParameter(14, contractPrice.getToCountry());
            else
            	q.setParameter(14, Types.BIGINT);

            if (contractPrice.getToPostcalCode() != null)
            	q.setParameter(15, contractPrice.getToPostcalCode());
            else
            	q.setParameter(15, Types.INTEGER);

            if (null != contractPrice.getToCountryZoneCountFrom())
            	q.setParameter(16, contractPrice.getToCountryZoneCountFrom());
            else
            	q.setParameter(16, Types.BIGINT);

            if (null != contractPrice.getToCountryZoneCountTo())
            	q.setParameter(17, contractPrice.getToCountryZoneCountTo());
            else
            	q.setParameter(17, Types.BIGINT);
            q.setParameter(18, contractPrice.getCurrency());

            if (contractPrice.getZoneId() != null)
            	q.setParameter(19, contractPrice.getZoneId());
            else
            	q.setParameter(19, Types.BIGINT);

            // add 20th here
            if (contractPrice.getApplJourneyTpCd() != null)
            	q.setParameter(20, contractPrice.getApplJourneyTpCd());
            else
            	q.setParameter(20, Types.BIGINT);
            q.setParameter(21, contractPrice.getContractpriceProcessingTpCd());

            if (null != contractPrice.getFromRouteId())
            	q.setParameter(22, contractPrice.getFromRouteId());
            else
            	q.setParameter(22, Types.INTEGER);

            if (null != contractPrice.getToRouteId())
            	q.setParameter(23, contractPrice.getToRouteId());
            else
            	q.setParameter(23, Types.INTEGER);

            int i = q.executeUpdate();
            entityManager.getTransaction().commit();
            entityManager.close();
            return;
        } catch (NullPointerException ne) {
            ne.printStackTrace();
            System.exit(1);
        } catch (Exception sql) {
            sql.printStackTrace();
            System.exit(1);
        } 
        return;
    }

    // also check overlapping .. with code p1, without pcodee p2
    public Long insertSlabbasedPrice(Long priceId, Contractdump contractdump) {

        try {
            Long slabbasedId = null;
            EntityManager entityManager=JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
        	Query q = entityManager.createNativeQuery(INSERT_SLABBASEDPRICE_SQL);
            java.util.Date date = new java.util.Date();
            q.setParameter(1, priceId);

            if (null != contractdump.getDscLimCd() && contractdump.getDscLimCd().equals(2))
            	q.setParameter(2, 1);
                //stmt.setInt(2,2);
            else if (null != contractdump.getDscLimCd() && contractdump.getDscLimCd().equals(3))
            	q.setParameter(2, 2);
                //stmt.setInt(2,1);
            else
            	q.setParameter(2, Types.INTEGER);

            ///   consider
            q.setParameter(3, new java.sql.Date(date.getTime()));
            q.setParameter(4, AUTOMATION_USER);
            q.setParameter(5, AUTOMATION_USER);
            q.setParameter(6, new java.sql.Date(date.getTime()));


            int i = q.executeUpdate();
            entityManager.getTransaction().commit();
            Integer id = (Integer) q.getSingleResult();
            slabbasedId = id.longValue();

            //     slabbasedId = queryService.findMaxSlabbasedPriceId();
            entityManager.close();
            return slabbasedId;
        }
         catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public Boolean disableContractdumpEntry(List<Contractdump> contracts) {

        try {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            
            entityManager.getTransaction().begin();
            System.out.println("Inside upsertContractData ");
            System.out.println("Begin transaction");
            if (!contracts.isEmpty()) {
                for (Contractdump contractdump : contracts) {
                    contractdump.setUpdated(true);
                    entityManager.merge(contractdump);
                    //  entityManager.merge(contractdump);
                }
            }
            entityManager.getTransaction().commit();
            System.out.println("Data disabled successfully....");
            entityManager.close();
            return true;
        } catch (NullPointerException ex) {
            System.out.println("######Got a null piointer");
            ex.printStackTrace();
            ex.getMessage();
            System.exit(1);
        } catch (PersistenceException ex) {
            System.out.println("######Persistance exception occures");
            System.out.println("Message :::::::::::::::: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        } catch (Exception ex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            ex.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public Boolean disableVoilatedSurchargeDump(Surchargedump surchargedump, Party party) {
        try {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            System.out.println("Inside upsertContractData ");
            System.out.println("Begin transaction");
            if (surchargedump.isEnabled()) {
                surchargedump.setUpdated(false);
                surchargedump.setRemark("C/O" + party.getParentSourceSystemRecordPk());
                entityManager.merge(surchargedump);
            }
            entityManager.getTransaction().commit();
            System.out.println("Data disabled successfully....");
            entityManager.close();
            return true;
        } catch (NullPointerException ex) {
            System.out.println("######Got a null piointer");
            ex.printStackTrace();
            ex.getMessage();
        } catch (PersistenceException ex) {
            System.out.println("######Persistance exception occures");
            System.out.println("Message :::::::::::::::: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);

        } catch (Exception ex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            ex.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public Boolean disableSurchargeDump(Surchargedump surchargedump) {

        try {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            System.out.println("Inside upsertContractData ");
            System.out.println("Begin transaction");
            if (surchargedump.isEnabled()) {
                surchargedump.setUpdated(true);
                entityManager.merge(surchargedump);
            }
            entityManager.getTransaction().commit();
            System.out.println("Data disabled successfully....");
            entityManager.close();
            return true;
        } catch (NullPointerException ex) {
            System.out.println("######Got a null piointer");
            ex.printStackTrace();
            ex.getMessage();
        } catch (PersistenceException ex) {
            System.out.println("######Persistance exception occures");
            System.out.println("Message :::::::::::::::: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);

        } catch (Exception ex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            ex.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public Boolean updateSurchargeDump(Surchargedump surchargedump) {

        try {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            System.out.println("Inside upsertContractData ");
            System.out.println("Begin transaction");
            entityManager.merge(surchargedump);

            entityManager.getTransaction().commit();
            System.out.println("Data disabled successfully....");
            entityManager.close();
            return true;
        } catch (NullPointerException ex) {
            System.out.println("######Got a null piointer");
            ex.printStackTrace();
            ex.getMessage();
            System.exit(1);
        } catch (PersistenceException ex) {
            System.out.println("######Persistance exception occures");
            System.out.println("Message :::::::::::::::: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        } catch (Exception ex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            ex.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    private ArrayList<Integer> service336List() {


        ArrayList<Integer> service336List = new ArrayList<Integer>();
        service336List.add(336);
        service336List.add(33664);
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
        //     service336List.add(6133664);
        //     service336List.add(6233664);

        return service336List;
    }

    public Boolean disablePercentageBasedContractdumpEntry(List<Percentagebaseddump> contracts) {

        try {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            System.out.println("Inside upsertContractData ");
            System.out.println("Begin transaction");
            if (!contracts.isEmpty()) {
                for (Percentagebaseddump contractdump : contracts) {
                    contractdump.setUpdated(true);
                    entityManager.merge(contractdump);
                    //  entityManager.merge(contractdump);
                }
            }
            entityManager.getTransaction().commit();
            System.out.println("Data disabled successfully....");
            entityManager.close();
            return true;
        } catch (NullPointerException ex) {
            System.out.println("######Got a null piointer");
            ex.printStackTrace();
            ex.getMessage();
            System.exit(1);
        } catch (PersistenceException ex) {
            System.out.println("######Persistance exception occures");
            System.out.println("Message :::::::::::::::: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);

        } catch (Exception ex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            ex.printStackTrace();
            System.exit(1);
        }
        return false;
    }

    @Transactional
    public Party insertOrganizationParty(String partyDetails) {

        Party party = new Party();
        String orgNumber = partyDetails.split("~")[0];
        String orgName = partyDetails.split("~")[1];
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            party.setSourceSystemId(Long.valueOf(3));
            party.setSourceSystemRecordPk(orgNumber);
            party.setPartyName(orgName);
            party.setCreatedDt(LocalDate.now());
            party.setCreatedByUser(AUTOMATION_USER);
            entityManager.getTransaction().begin();
            entityManager.persist(party);
            entityManager.getTransaction().commit();
            System.out.println("New Party has been created");

            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while creating new Party");
            System.exit(1);
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return party;
    }

    @Transactional
    public ArrayList<Party> insertCustomerParty(Set<String> partyList, Party orgParty) {
        ArrayList<Party> parties = new ArrayList<Party>();
        if (!partyList.isEmpty()) {
            for (String partyDetails : partyList) {
                Party party = new Party();
                String orgNumber = partyDetails.split("~")[0];
                String orgName = partyDetails.split("~")[1];
                if (orgNumber == null)
                    System.out.println("wait here");
                if (orgParty == null)
                    System.out.println("wait here");
                if (orgParty.getSourceSystemRecordPk() == null)
                    System.out.println("wait here");
                if (!orgNumber.equals(orgParty.getSourceSystemRecordPk())) {
                    EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
                    try {
                        party.setSourceSystemId(Long.valueOf(3));
                        party.setSourceSystemRecordPk(orgNumber);
                        party.setPartyName(orgName);

                        party.setParentSourceSystemRecordPk(orgParty.getSourceSystemRecordPk());
                        party.setParentParty(orgParty);
                        party.setParentPartyId(orgParty.getPartyId());

                        party.setCreatedDt(LocalDate.now());
                        party.setCreatedByUser(AUTOMATION_USER);
                        entityManager.getTransaction().begin();
                        entityManager.persist(party);
                        entityManager.getTransaction().commit();
                        System.out.println("New Party has been created");
                        parties.add(party);

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error while creating new Party");
                        System.exit(1);
                    } finally {
                        entityManager.clear();
                        entityManager.close();
                    }
                }
            }
        }
        return parties;
    }

    @Transactional
    public Party insertCustomerPartyNew(String customerDetails, Party orgParty) {

        Party party = new Party();
        String customerNumber = customerDetails.split("~")[0];
        String customerName = customerDetails.split("~")[1];

        if (!customerNumber.equals(orgParty.getSourceSystemRecordPk())) {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            try {
                party.setSourceSystemId(Long.valueOf(3));
                party.setSourceSystemRecordPk(customerNumber);
                party.setPartyName(customerName);

                party.setParentSourceSystemRecordPk(orgParty.getSourceSystemRecordPk());
                party.setParentParty(orgParty);
                party.setParentPartyId(orgParty.getPartyId());

                party.setCreatedDt(LocalDate.now());
                party.setCreatedByUser(AUTOMATION_USER);
                entityManager.getTransaction().begin();
                entityManager.persist(party);
                entityManager.getTransaction().commit();
                System.out.println("New Party has been created");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error while creating new Party" + customerDetails + "  Parent Party :: " + orgParty.getSourceSystemRecordPk());
                System.exit(1);

            } finally {
                entityManager.clear();
                entityManager.close();
            }
        }
        return party;
    }

    public void upsertPartyDetails(HashMap<String, Set<String>> partyMap) {
        ArrayList<String> orgIdList = new ArrayList<String>();
        Set<String> partySet = partyMap.keySet();
        for (String str : partySet) {
            orgIdList.add(str.split("~")[0]);
        }

        List<Party> partiesInDB = new ArrayList<Party>();
        if (!orgIdList.isEmpty())
            partiesInDB = queryService.findAllParentParty(orgIdList);
        if (!partiesInDB.isEmpty()) {
            for (Party parentParty : partiesInDB) {
                String id = parentParty.getSourceSystemRecordPk() + "~" + parentParty.getPartyName();
                partySet.remove(id);
            }
        }
        for (String orgParty : partySet) {
            Set<String> customerDetails = partyMap.get(orgParty);
            Party parentParty = insertOrganizationParty(orgParty);
            if (customerDetails.size() == 1) {
                String customerDetail = customerDetails.iterator().next();
                if (!customerDetail.split("~")[0].equals(parentParty.getSourceSystemRecordPk())) {
                    Set<String> customer = new HashSet<String>();
                    customer.add(customerDetail);
                    insertCustomerParty(customer, parentParty);
                }
            } else {
                for (String customerDetail : customerDetails) {
                    if (!customerDetail.split("~")[0].equals(parentParty.getSourceSystemRecordPk())) {
                        Set<String> customer = new HashSet<String>();
                        customer.add(customerDetail);
                        insertCustomerParty(customer, parentParty);
                    }
                }
            }
        }
    }

    // VALIDATE THAT CUSTOMER NUMBBER AND ORGANIZATION UMBBER  IS NOT SAME
    @Transactional
    public void upsertPartyDetailsNEW(HashMap<String, Set<String>> partyMap) {
        String failedCustomers = "";
        if (!partyMap.isEmpty()) {
            Set<String> organizations = partyMap.keySet();

            for (String orgKey : organizations) {
                String orgId = orgKey.split("~")[0];
                Party parentParty = queryService.findParentParty(orgId);
                Party childParty = queryService.findChildPartyBySSPK(orgId);
                if (null == parentParty && childParty == null) {
                    parentParty = insertOrganizationParty(orgKey);
                }
                if (null != parentParty) {
                    Set<String> customers = partyMap.get(orgKey);
                    for (String customer : customers) {
                        if (!customer.split("~")[0].equals(orgKey.split("~")[0])) {
                            String customerId = customer.split("~")[0];
                            if (!customerId.equals(orgId)) {
                                Party customerParty = queryService.findChildParty(customerId, orgId);
                                if (customerParty == null)
                                    customerParty = insertCustomerPartyNew(customer, parentParty);
                                if (customerParty == null) {
                                    System.out.println("Please cehck records.. data already exist.  Customer Id : " + customer + " parent id " + parentParty.getSourceSystemRecordPk());

                                    failedCustomers = failedCustomers + ", " + customer;
                                    System.exit(1);
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("Some error occured while creating Parent Party... " + orgId);
                    System.exit(1);
                }
            }

        }
        System.out.println(" failed customer s-- " + failedCustomers);

    }

    @Transactional
    public void updateContractPriceProcessingDetails(String priceId, Integer processingId) {
        try {
        	EntityManager entityManager=JPAUtil.getEntityManagerFactory().createEntityManager();
        	entityManager.getTransaction().begin();
            Query q= entityManager.createNativeQuery(UPDATE_CONTRACTPRICE_SQL);
            q.setParameter(1, processingId);
            q.setParameter(2, new Long(priceId));
            int i = q.executeUpdate();
            entityManager.getTransaction().commit();
            entityManager.close();
            return;

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    @Transactional
    public void updateContractPricePriority(Integer priority, List<Integer> priceIds) {
        try {
            String str = "";
            if (!priceIds.isEmpty()) {
                for (Integer priceId : priceIds) {
                    str = priceId + "," + str;
                }
                if (str != "")
                    str = str.substring(0, str.length() - 1);

            }
            //  for(Integer priceId : priceIds) {
            EntityManager entityManager=JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            String query = "update core.contractprice set priority= " + priority + " where price_id in (" + str + ")";
            Query q= entityManager.createNativeQuery(query);
            q.executeUpdate();
            entityManager.getTransaction().commit();
            entityManager.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Transactional
    public void updateContractPriceJourneyType(Integer journey, Long priceId) {
        try {
        	EntityManager entityManager=JPAUtil.getEntityManagerFactory().createEntityManager();
        	entityManager.getTransaction().begin();
        	String query = "update core.contractprice set appl_journey_tp_cd = " + journey + " where price_id =" + priceId;
            Query q= entityManager.createNativeQuery(query);
            q.executeUpdate();
            entityManager.getTransaction().commit();
            entityManager.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private String filterRouteByProcesingTypes(String fromCountry, String fromPostalCode, String toCountry,
                                               String toPostalCode, String fromZone, String toZone, String customZone) {

        String processingType = null;
        Boolean isFULLFromPostalCodeAvailable = false;
        Boolean isFULLToPostalCodeAvailable = false;

        Boolean isPARTIALFromPostalCodeAvailable = false;
        Boolean isPARTIALToPostalCodeAvailable = false;
        ///  TYPE 8 ALL TO ALL
        if (null == fromCountry && null == toCountry && null == fromPostalCode && null == toPostalCode &&
                null == fromZone && null == toZone && null == customZone) {
            processingType = ContractProcessing.ALL_SOURCE_ALL_DESTINATION.toString();
            processingType = getProcessingId().get(processingType).toString();
        } else if (null != fromCountry && null == toCountry && null == fromPostalCode && null == toPostalCode &&
                null == fromZone && null == toZone && null == customZone) {
            processingType = ContractProcessing.ALL_SOURCE_ALL_DESTINATION.toString();
            processingType = getProcessingId().get(processingType).toString();
        } else if (null == fromCountry && null != toCountry && null == fromPostalCode && null == toPostalCode &&
                null == fromZone && null == toZone && null == customZone) {
            processingType = ContractProcessing.ALL_SOURCE_ALL_DESTINATION.toString();
            processingType = getProcessingId().get(processingType).toString();
        }
        // TYPE 1 --  PURE COUNTRY CODE
        else if (null != fromCountry && null != toCountry && null == fromPostalCode && null == toPostalCode &&
                null == fromZone && null == toZone && null == customZone) {
            processingType = ContractProcessing.PURE_COUNTRY_CODE.toString();
            processingType = getProcessingId().get(processingType).toString();
        }
        //TYPE 2 -- COUNTRY CODE AND FULL POSTAL CODE
        else if (null != fromCountry && null != toCountry && fromPostalCode == null && toPostalCode != null && toPostalCode.toCharArray().length > 3) {
            isFULLFromPostalCodeAvailable = true;
            processingType = ContractProcessing.COUNTRY_CODE_FULL_POSTAL_CODE.toString();
            processingType = getProcessingId().get(processingType).toString();
        }
        //TYPE 2 -- COUNTRY CODE AND FULL POSTAL CODE
        else if (null != fromCountry && null != toCountry && fromPostalCode != null && toPostalCode == null && fromPostalCode.toCharArray().length > 3) {
            isFULLFromPostalCodeAvailable = true;
            processingType = ContractProcessing.COUNTRY_CODE_FULL_POSTAL_CODE.toString();
            processingType = getProcessingId().get(processingType).toString();
        }
        //TYPE 2 -- COUNTRY CODE AND FULL POSTAL CODE
        else if (null != fromCountry && null != toCountry && fromPostalCode != null && toPostalCode != null && fromPostalCode.toCharArray().length > 3
                && toPostalCode.toCharArray().length > 3) {
            isFULLFromPostalCodeAvailable = true;
            processingType = ContractProcessing.COUNTRY_CODE_FULL_POSTAL_CODE.toString();
            processingType = getProcessingId().get(processingType).toString();

        }

        //TYPE 3 -- COUNTRY CODE AND PARTIAL POSTAL CODE
        else if (null != fromCountry && null != toCountry && fromPostalCode == null && toPostalCode != null && toPostalCode.toCharArray().length < 4) {

            processingType = ContractProcessing.COUNTRY_CODE_PARTIAL_POSTAL_CODE.toString();
            processingType = getProcessingId().get(processingType).toString();
        }
        //TYPE 3 -- COUNTRY CODE AND PARTIAL POSTAL CODE
        else if (null != fromCountry && null != toCountry && fromPostalCode != null && toPostalCode == null && fromPostalCode.toCharArray().length < 4) {
            isFULLFromPostalCodeAvailable = true;
            processingType = ContractProcessing.COUNTRY_CODE_PARTIAL_POSTAL_CODE.toString();
            processingType = getProcessingId().get(processingType).toString();
        }
        //TYPE 3 -- COUNTRY CODE AND PARTIAL POSTAL CODE
        else if (null != fromCountry && null != toCountry && fromPostalCode != null && toPostalCode != null && fromPostalCode.toCharArray().length < 4
                && toPostalCode.toCharArray().length < 4) {
            processingType = ContractProcessing.COUNTRY_CODE_PARTIAL_POSTAL_CODE.toString();
            processingType = getProcessingId().get(processingType).toString();
        }

        /////// TYPE 7 COUNTRY CODE AND COMBINED FULL AND PARTIAL POSTAL CODE

        else if (null != fromCountry && null != toCountry && fromPostalCode != null && toPostalCode != null && fromPostalCode.toCharArray().length > 3 && toPostalCode.toCharArray().length < 4) {
            processingType = ContractProcessing.COUNTRY_CODE_PARTIAL_FULL_POSTAL_CODE.toString();
            processingType = getProcessingId().get(processingType).toString();
        } else if (null != fromCountry && null != toCountry && fromPostalCode != null && toPostalCode != null && fromPostalCode.toCharArray().length < 4 && toPostalCode.toCharArray().length > 3) {
            processingType = ContractProcessing.COUNTRY_CODE_PARTIAL_FULL_POSTAL_CODE.toString();
            processingType = getProcessingId().get(processingType).toString();
        }
        // END TYPE 7

        else if (fromZone != null || toZone != null || customZone != null) {
            if (customZone != null) {
                processingType = ContractProcessing.CUSTOM_ZONE.toString();
                processingType = getProcessingId().get(processingType).toString();
            } else if (fromZone != null && toZone != null) {
                processingType = ContractProcessing.STANDARD_ZONE.toString();
                processingType = getProcessingId().get(processingType).toString();
            }
        } else
            processingType = "NOT IN ANY TYPE";

        return processingType;

    }

    public void updateAllContractPriceForProcessing() {

        List<SlabBasedPriceEntry> entries = new ArrayList<SlabBasedPriceEntry>();
        StringBuilder builder = new StringBuilder();
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        System.out.println("Inside getAllContractPrice() ");
        //List<Query> query = entityManager.createQuery(" from Price price where price.basePrice is null AND price.percentBasedPrice is null").getResultList();
        //List<Query> query = entityManager.createNativeQuery("select * from core.contractprice WHERE  contractprice_processing_tp_cd is null ").getResultList();
        List<Query> query = entityManager.createNativeQuery("select cp.* from core.contractprice cp join core.price price on cp.price_id = price.price_id WHERE cp.contractprice_processing_tp_cd is null and price.created_by_user='Automation' ").getResultList();
        Iterator listIterator = query.listIterator();
        while (listIterator.hasNext()) {

            Object[] object = (Object[]) listIterator.next();
            String fromCountry = null;
            String toCountry = null;
            String fromPostalCode = null;
            String toPostalCode = null;
            String fromZone = null;
            String toZone = null;
            String zoneIdCustom = null;
            String priceId = null;

            if (object[0] != null)
                priceId = object[0].toString();
            if (object[11] != null)
                fromCountry = object[11].toString();
            if (object[12] != null)
                fromPostalCode = object[12].toString();
            if (object[13] != null)
                toCountry = object[13].toString();
            if (object[14] != null)
                toPostalCode = object[14].toString();
            if (object[15] != null)
                fromZone = object[15].toString();
            if (object[16] != null)
                toZone = object[16].toString();
            if (object[19] != null)
                zoneIdCustom = object[19].toString();

            String processingType = filterRouteByProcesingTypes(fromCountry, fromPostalCode, toCountry, toPostalCode, fromZone, toZone, zoneIdCustom);
            if (null != processingType && !processingType.equals("NOT IN ANY TYPE")) {
                updateContractPriceProcessingDetails(object[0].toString(), Integer.parseInt(processingType));
                builder.append(object[0].toString() + ",");
            } else {
                System.out.println("PROCESSING TYPE NOT FOUND FOR PRICE_ID -------------- " + priceId);
                System.exit(1);
            }
            System.out.println("Price ID - " + object[0].toString() + " :: Processing type - " + processingType);
        }
        System.out.println(builder);
        entityManager.close();

    }

    private Cdcustomcountryroutetp upsertCdcustomcountryroutetp(Integer countryId, String routeName) {
        Cdcustomcountryroutetp cdcustomcountryroutetp = new Cdcustomcountryroutetp();
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            cdcustomcountryroutetp.setCreatedByUser(AUTOMATION_USER);
            cdcustomcountryroutetp.setCreatedDt(LocalDate.now());
            cdcustomcountryroutetp.setCountryId(countryId);
            cdcustomcountryroutetp.setRouteName(routeName);
            cdcustomcountryroutetp.setStartDt(LocalDate.now());
            entityManager.getTransaction().begin();
            entityManager.persist(cdcustomcountryroutetp);
            entityManager.getTransaction().commit();
            System.out.println("upsertCdcustomcountryroutetp Cdcustomcountryroutetp inserted successfully....");

            cdcustomcountryroutetp = queryService.findCdcustomcountryroutetp(routeName);
            return cdcustomcountryroutetp;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return cdcustomcountryroutetp;
    }

    private void updateDate(ContractComponent contractComponent, String oldStartDate, String newStartDate) {
        try {
        	EntityManager entityManager=JPAUtil.getEntityManagerFactory().createEntityManager();
        	entityManager.getTransaction().begin();
            Query q= entityManager.createNativeQuery("update core.contractcomponent set start_Dt ='" + newStartDate + "' where start_dt = '" + oldStartDate + "'  and   contractcomponent_id =" + contractComponent.getContractComponentId());
//            PreparedStatement stmt = connection.prepareStatement(UPDATE_CONTRACTPRICE_SQL, Statement.RETURN_GENERATED_KEYS);
//            stmt.setInt(1,processingId);
//            stmt.setLong(2, new Long(priceId));
//            int i = stmt.executeUpdate();
//            connection.close();
            int i = q.executeUpdate();
            entityManager.getTransaction().commit();
            entityManager.close();
        } catch (HibernateQueryException sqex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            sqex.printStackTrace();
            System.exit(1);
        } catch (NullPointerException ex) {
            System.out.println("######Got a null piointer");
            ex.printStackTrace();
            ex.getMessage();
            System.exit(1);
        } catch (PersistenceException ex) {
            System.out.println("######Persistance exception occures");
            System.out.println("Message :::::::::::::::: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);

        } catch (Exception ex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            ex.printStackTrace();
            System.exit(1);
        }

    }

    private void fixDatesINPriceTable() throws ParseException {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sb = new StringBuilder();
        Integer counter = 0;

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        List<Query> query = entityManager.createNativeQuery(" select * from core.price WHERE created_by_user = 'Automation' and  percent_based_price is null  and price_id in (select distinct price_id from core.contractprice)").getResultList();
        //   List<Query> query = entityManager.createNativeQuery("select * from core.price where end_dt != 'infinity' and  percent_based_price is null and start_dt in ('2009-08-23 20:30:00','2009-10-31 19:30:00','2009-11-30 19:30:00','2010-01-01 00:00:00','2010-05-26 20:30:00','2010-08-16 00:00:00','2011-11-27 19:30:00','2012-04-03 20:30:00','2012-11-30 19:30:00','2014-06-01 00:00:00','2014-12-31 19:30:00','2015-01-01 00:00:00','2015-03-01 00:00:00','2015-06-30 20:30:00','2015-11-05 19:30:00','2018-11-21 19:30:00') and price_id in (select distinct price_id from core.contractprice)").getResultList();
        Iterator listIterator = query.listIterator();
        while (listIterator.hasNext()) {
            Object[] price = (Object[]) listIterator.next();
            LocalDate priceStartDate = LocalDate.parse(price[7].toString().split(" ")[0]);
            LocalDate priceEndDate = null;
            if (price[8].toString() != "infinity")
                priceEndDate = LocalDate.parse(price[8].toString().split(" ")[0]);
            ContractPrice contractprice = queryService.findContractPriceObject(new Long(price[0].toString()));
            ContractComponent contractComponent = queryService.findContractComponent(new Long(contractprice.getContractcomponentId()));
            List<ContractRole> contractRoles = queryService.findContractRoles(contractComponent);
            if (!contractRoles.isEmpty()) {
                for (ContractRole role : contractRoles) {
                    if (role.getContractRoleTpCd().equals(1)) {
                        String spk = role.getPartySourceSystemRecordPk();
                        List<Contractdump> dumps = queryService.findContractdumpsByDate(spk, priceStartDate.toString(), contractprice, role.getContractRoleTpCd().toString());
                        if (!dumps.isEmpty()) {
                            Date date = sdfDate.parse(priceStartDate.toString());
                            System.out.println("check here bcoz as per code analu=ysis, data is correct.");
                        } else {
                            dumps = queryService.findContractdumpsByDate(spk, priceStartDate.plusDays(1).toString(), contractprice, role.getContractRoleTpCd().toString());
                            if (!dumps.isEmpty()) {
                                LocalDate newLocalDateStart = priceStartDate.plusDays(1);
                                LocalDate newLocalDateEnd = null;
                                if (priceEndDate != null)
                                    newLocalDateEnd = priceEndDate.plusDays(1);
                                updateStartDateINPrice(new Long(price[0].toString()), newLocalDateStart, newLocalDateEnd);
                                counter++;
                                break;
                            } else {
                                System.out.println("Date not found " + spk + "  :: pri ce id -   " + price[0].toString());
                                sb.append(price[0].toString() + ", ");
                            }
                        }

                    }
                }
            }
        }
        System.out.println(sb);
    }

    private void fixPercentageBasedNegativeVaues() throws ParseException {

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        List<Query> query = entityManager.createNativeQuery(" select * from core.price where percent_based_price<0 ").getResultList();
        Iterator listIterator = query.listIterator();
        while (listIterator.hasNext()) {

        }
    }

    private void fixPercentageBasedDatesINPriceTable() throws ParseException {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sb = new StringBuilder();
        Integer counter = 0;

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        List<Query> query = entityManager.createNativeQuery(" select * from core.price WHERE created_by_user = 'Automation' and  percent_based_price is NOT null  and price_id in (select distinct price_id from core.contractprice)").getResultList();
        //   List<Query> query = entityManager.createNativeQuery("select * from core.price where end_dt != 'infinity' and  percent_based_price is null and start_dt in ('2009-08-23 20:30:00','2009-10-31 19:30:00','2009-11-30 19:30:00','2010-01-01 00:00:00','2010-05-26 20:30:00','2010-08-16 00:00:00','2011-11-27 19:30:00','2012-04-03 20:30:00','2012-11-30 19:30:00','2014-06-01 00:00:00','2014-12-31 19:30:00','2015-01-01 00:00:00','2015-03-01 00:00:00','2015-06-30 20:30:00','2015-11-05 19:30:00','2018-11-21 19:30:00') and price_id in (select distinct price_id from core.contractprice)").getResultList();
        Iterator listIterator = query.listIterator();
        while (listIterator.hasNext()) {
            Object[] price = (Object[]) listIterator.next();
            LocalDate priceStartDate = LocalDate.parse(price[7].toString().split(" ")[0]);
            LocalDate priceEndDate = null;
            if (price[8].toString() != "infinity")
                priceEndDate = LocalDate.parse(price[8].toString().split(" ")[0]);
            ContractPrice contractprice = queryService.findContractPriceObject(new Long(price[0].toString()));
            ContractComponent contractComponent = queryService.findContractComponent(new Long(contractprice.getContractcomponentId()));
            List<ContractRole> contractRoles = queryService.findContractRoles(contractComponent);
            if (!contractRoles.isEmpty()) {
                for (ContractRole role : contractRoles) {
                    if (role.getContractRoleTpCd().equals(1)) {
                        String spk = role.getPartySourceSystemRecordPk();
                        List<Percentagebaseddump> dumps = queryService.findPercentageBasedContractdumpsByDate(spk, priceStartDate.toString(), contractprice, role.getContractRoleTpCd().toString());
                        if (!dumps.isEmpty()) {
                            Date date = sdfDate.parse(priceStartDate.toString());
                            System.out.println("check here bcoz as per code analu=ysis, data is correct.");
                        } else {
                            dumps = queryService.findPercentageBasedContractdumpsByDate(spk, priceStartDate.plusDays(1).toString(), contractprice, role.getContractRoleTpCd().toString());
                            if (!dumps.isEmpty()) {
                                LocalDate newLocalDateStart = priceStartDate.plusDays(1);
                                LocalDate newLocalDateEnd = null;
                                if (priceEndDate != null)
                                    newLocalDateEnd = priceEndDate.plusDays(1);
                                updateStartDateINPrice(new Long(price[0].toString()), newLocalDateStart, newLocalDateEnd);
                                counter++;
                                break;
                            } else {
                                System.out.println("Date not found " + spk + "  :: pri ce id -   " + price[0].toString());
                                sb.append(price[0].toString() + ", ");
                            }
                        }

                    }
                }
            }
        }
        System.out.println(sb);
    }

    private void fixDatesContractComponent() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        List<Query> query = entityManager.createQuery(" from ContractComponent WHERE createdByUser = 'Automation'").getResultList();
        Iterator listIterator = query.listIterator();
        while (listIterator.hasNext()) {
            //  ContractComponent contractComponent = new ContractComponent();
            ContractComponent obj = (ContractComponent) listIterator.next();
            List<ContractRole> contractRoles = queryService.findContractRoles(obj);
            if (!contractRoles.isEmpty()) {
                for (ContractRole role : contractRoles) {
                    if (role.getContractRoleTpCd().equals(1)) {
                        String spk = role.getPartySourceSystemRecordPk();
                        List<Contractdump> dumps = null; // queryService.findContractdumpsByDate(spk,obj.getStartDt().toString(), "2");
                        if (!dumps.isEmpty()) {
                            Date date = sdfDate.parse(obj.getStartDt().toString());
                            System.out.println("Asdasdasd");
                        } else {
                            dumps = null;//ueryService.findContractdumpsByDate(spk,obj.getStartDt().plusDays(1).toString(),"2");
                            if (!dumps.isEmpty()) {
                                obj.setStartDt(obj.getStartDt().plusDays(1));
                                obj.setLastUpdateDt(LocalDate.now());
                                entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
                                entityManager.getTransaction().begin();
                                entityManager.merge(obj);
                                entityManager.getTransaction().commit();
                                System.out.println("Data inserted/updated successfully....");
                                entityManager.close();


                            }
                            System.out.println("Asdasdasd");
                        }

                    }
                }
            }
        }
    }

    public void fixItemInPrice() {
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        List<Query> query = entityManager.createNativeQuery("SELECT price.price_id, price.item_id,  cp.item_id_dup FROM core.price price inner join CORE.CONTRACTPRICE cp on price.price_id = cp.price_id and price.item_id!= cp.item_id_dup").getResultList();
        Iterator listIterator = query.listIterator();
        while (listIterator.hasNext()) {
            Object[] price = (Object[]) listIterator.next();
            Integer price_id = Integer.parseInt(price[0].toString());
            Integer priceItemId = Integer.parseInt(price[1].toString());
            Integer contractPriceItemId = Integer.parseInt(price[2].toString());
            if (!priceItemId.equals(contractPriceItemId))
                updateItemInPriceTable(price_id, contractPriceItemId);
        }
        System.out.println("Data inserted/updated successfully....");
        entityManager.close();

    }

    @Transactional
    public void updateItemInPriceTable(Integer priceId, Integer contractPriceItemId) {
        try {
        	EntityManager entityManager=JPAUtil.getEntityManagerFactory().createEntityManager();
        	entityManager.getTransaction().begin();
            Query q= entityManager.createNativeQuery("update core.price set item_id=? where price_id = ?");
            q.setParameter(1, contractPriceItemId);
            q.setParameter(2, priceId);
            q.executeUpdate();
            entityManager.getTransaction().commit();
            entityManager.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public void disableNonCDHCustomers(String filecountry, Map<String, CustomerModel> cdhDetails) {
        List<Contractdump> contractdumpsAll = queryService.findAllContractdumps(filecountry);
        Set<Contractdump> customerdataForDisable = new HashSet<Contractdump>();
        Set<Contractdump> parentdataForDisable = new HashSet<Contractdump>();
        Iterator iterator = contractdumpsAll.iterator();
        while (iterator.hasNext()) {
            Contractdump contractdump = (Contractdump) iterator.next();
            if (!cdhDetails.containsKey(contractdump.getCustomerNumber()))
                customerdataForDisable.add(contractdump);
            if (!cdhDetails.containsKey(contractdump.getOrganizationNumber()))
                parentdataForDisable.add(contractdump);
        }
        disableNonCDHData(customerdataForDisable, 12);// NO-29-- SE-120
        disableNonCDHData(parentdataForDisable, 11);
    }

    public void disableNonCDHCustomersPercent(String filecountry, Map<String, CustomerModel> cdhDetails) {
        List<Percentagebaseddump> contractdumpsAll = queryService.findAllPercentageBasedContractdumps(filecountry);
        Set<Percentagebaseddump> dataForDisable = new HashSet<Percentagebaseddump>();
        Set<Percentagebaseddump> dataForDisableParent = new HashSet<Percentagebaseddump>();
        Iterator iterator = contractdumpsAll.iterator();
        while (iterator.hasNext()) {
            Percentagebaseddump contractdump = (Percentagebaseddump) iterator.next();
            if (!cdhDetails.containsKey(contractdump.getCustomerNumber())) {
                dataForDisable.add(contractdump);
            }
            if (!cdhDetails.containsKey(contractdump.getParentCustomerNumber())) {
                dataForDisableParent.add(contractdump);
            }
        }
        disableNonCDHDataPercent(dataForDisableParent, 11);
        disableNonCDHDataPercent(dataForDisable, 12);
    }

    public void addEndDateContractComponent(ContractComponent contractComponent, Contractdump contractdump) {
        try {
            EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            System.out.println("Inside upsertContractData ");
            System.out.println("Begin transaction");
            Instant toInstant = contractdump.getToDate().toInstant();
            contractComponent.setEndDt(toInstant.atZone(ZoneId.systemDefault()).toLocalDate());
            entityManager.merge(contractComponent);
            entityManager.getTransaction().commit();
            System.out.println("Data disabled successfully....");
            entityManager.close();
        } catch (NullPointerException ex) {
            System.out.println("######Got a null piointer");
            ex.printStackTrace();
            ex.getMessage();
            System.exit(1);
        } catch (PersistenceException ex) {
            System.out.println("######Persistance exception occures");
            System.out.println("Message :::::::::::::::: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        } catch (Exception ex) {
            System.out.println("######Error while upserting data in Contractdump table.");
            ex.printStackTrace();
            System.exit(1);
        }

    }

    @Transactional
    public void updateContractPriceJourney(List<Integer> priceIds) {
        try {

            String str = "";
            if (!priceIds.isEmpty()) {
                for (Integer priceId : priceIds) {
                    str = priceId + "," + str;
                }
                if (str != "")
                    str = str.substring(0, str.length() - 1);

            }
            EntityManager entityManager=JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            String query = "update core.contractprice set appl_journey_tp_cd=3 where price_id in (" + str + ")";
            Query q=entityManager.createNativeQuery(query);
            q.executeUpdate();
            entityManager.getTransaction().commit();
            entityManager.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Transactional
    public void deleteContractPrice(List<Integer> priceIds) {
        try {

            String str = "";
            if (!priceIds.isEmpty()) {
                for (Integer priceId : priceIds) {
                    str = priceId + "," + str;
                }
                if (str != "")
                    str = str.substring(0, str.length() - 1);

            }
            //  for(Integer priceId : priceIds) {
            EntityManager entityManager=JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            String query = "delete from core.price where price_id in (" + str + ")";
            Query q= entityManager.createNativeQuery(query);
            q.executeUpdate();
            entityManager.getTransaction().commit();
            entityManager.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    @Transactional
    public void deletePrice(Long priceIds) {
        try {

            String str = priceIds.toString();

            //  for(Integer priceId : priceIds) {
            EntityManager entityManager=JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            String query = "delete from core.price where price_id =" + str;
            Query q=entityManager.createNativeQuery(query);
            q.executeUpdate();
            entityManager.getTransaction().commit();
            entityManager.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    @Transactional
    public void deletePrices(ArrayList<Long> priceIds) {
        try {

            String str = "";
            if (!priceIds.isEmpty()) {
                for (Long priceId : priceIds) {
                    str = priceId + "," + str;
                }
                if (str != "")
                    str = str.substring(0, str.length() - 1);

            }
            //  for(Integer priceId : priceIds) {
            EntityManager entityManager=JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            String query = "delete from core.price where price_id in (" + str + ")";
            Query q=entityManager.createNativeQuery(query);
            
            q.executeUpdate();
            entityManager.getTransaction().commit();
            entityManager.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    public void mergeSimilarJourneyType() throws ParseException {

        ArrayList<Integer> priceToRemove = new ArrayList<Integer>();
        ArrayList<Integer> priceToUpdate = new ArrayList<Integer>();

        ArrayList<Integer> contractcomponents = queryService.findDistinctContractComponentFRMCPJourneyPassive();
        for (Integer contractComponentId : contractcomponents) {

            ArrayList<Long> items = queryService.findDistinctItemsFRMContractprice(contractComponentId);
            for (Long itemId : items) {

                ArrayList<ContractPrice> contractPrices = queryService.findDistinctRoutesWithNullFRMContractprice(contractComponentId, itemId);
                HashMap<String, ArrayList<ContractPrice>> routeMap = new HashMap<String, ArrayList<ContractPrice>>();
                int counter = 0;
                for (ContractPrice cp : contractPrices) {
                    Integer fromCountry = cp.getFromCountry();
                    String fromPostalCode = cp.getFromPostalCode();
                    Integer toCountry = cp.getToCountry();
                    String toPostalCode = cp.getToPostcalCode();
                    Integer toZoneCountfrom = cp.getToCountryZoneCountFrom();
                    Integer toZoneCountTo = cp.getToCountryZoneCountTo();
                    Integer fromRouteId = cp.getFromRouteId();
                    Integer toRouteId = cp.getToRouteId();
                    Integer zoneId = cp.getZoneId();

                    String routes = "fromCountry:" + fromCountry + "-fromPostalCode:" + fromPostalCode + "-toCountry:" + toCountry + "-toPostalCode:" + toPostalCode
                            + "-toZoneCountfrom:" + toZoneCountfrom + "-toZoneCountTo:" + toZoneCountTo + "-fromRouteID:" + fromRouteId + "-toRouteID:" + toRouteId
                            + "-zoneId:" + zoneId;
                    if (routeMap.containsKey(routes)) {
                        routeMap.get(routes).add(cp);
                    } else {
                        ArrayList<ContractPrice> list = new ArrayList<ContractPrice>();
                        list.add(cp);
                        routeMap.put(routes, list);
                    }
                    counter++;
                    if (counter == contractPrices.size())
                        System.out.println("wait");
                }
                if (!routeMap.isEmpty()) {
                    Set<String> keys = routeMap.keySet();
                    for (String key : routeMap.keySet()) {
                        ArrayList<ContractPrice> contractPriceArrayList = routeMap.get(key);
                        HashMap<String, ArrayList<Price>> priceMapByDate = new HashMap<String, ArrayList<Price>>();
                        for (ContractPrice cprice : contractPriceArrayList) {
                            Price price = queryService.findPrice(cprice);
                            if (priceMapByDate.containsKey(price.getStartDt().toString())) {
                                priceMapByDate.get(price.getStartDt().toString()).add(price);
                            } else {
                                ArrayList<Price> priceList = new ArrayList<>();
                                priceList.add(price);
                                priceMapByDate.put(price.getStartDt().toString(), priceList);
                            }
                        }
                        if (!priceMapByDate.isEmpty()) {
                            Set<String> keySet = priceMapByDate.keySet();
                            for (String stDate : keySet) {
                                if (priceMapByDate.get(stDate).size() == 2 &&
                                        priceMapByDate.get(stDate).get(0).getBasePrice() != null
                                        && priceMapByDate.get(stDate).get(1).getBasePrice() != null) {
                                    if (priceMapByDate.get(stDate).get(0).getBasePrice().equals(priceMapByDate.get(stDate).get(1).getBasePrice())) {
                                        priceToRemove.add(priceMapByDate.get(stDate).get(0).getPriceId().intValue());
                                        priceToUpdate.add(priceMapByDate.get(stDate).get(1).getPriceId().intValue());
                                    }
                                } else if (priceMapByDate.get(stDate).size() == 2 &&
                                        priceMapByDate.get(stDate).get(0).getPercentBasedPrice() != null
                                        && priceMapByDate.get(stDate).get(1).getPercentBasedPrice() != null) {
                                    if (priceMapByDate.get(stDate).get(0).getPercentBasedPrice().equals(priceMapByDate.get(stDate).get(1).getPercentBasedPrice())) {
                                        priceToRemove.add(priceMapByDate.get(stDate).get(0).getPriceId().intValue());
                                        priceToUpdate.add(priceMapByDate.get(stDate).get(1).getPriceId().intValue());
                                    }
                                }
//                                else if (priceMapByDate.get(stDate).size() == 2 &&
//                                        priceMapByDate.get(stDate).get(0).getPercentBasedPrice() == null
//                                        && priceMapByDate.get(stDate).get(1).getPercentBasedPrice() == null &&
//                                        priceMapByDate.get(stDate).get(0).getBasePrice() == null
//                                        && priceMapByDate.get(stDate).get(1).getBasePrice() == null){
//                                    Boolean isDuplicate = queryService.compareSlabbasedPrice(priceMapByDate.get(stDate).get(0).getPriceId(),
//                                            priceMapByDate.get(stDate).get(1).getPriceId());
//                                    if(isDuplicate) {
//                                        priceToRemove.add(priceMapByDate.get(stDate).get(0).getPriceId().intValue());
//                                        priceToUpdate.add(priceMapByDate.get(stDate).get(1).getPriceId().intValue());
//                                    }
//                                }
                            }
                        }
                    }
                }
            }
        }
        if (!priceToRemove.isEmpty() && !priceToUpdate.isEmpty() && priceToRemove.size() == priceToUpdate.size()) {
            System.out.println("wait");
            System.out.println(priceToRemove.size());
            System.out.println(priceToUpdate.size());
            updateContractPriceJourney(priceToUpdate);
            deleteContractPrice(priceToRemove);
        }


    }

    private Boolean comparePriceAndJourney(ContractPrice cp1, ContractPrice cp2) throws ParseException {
        if (cp1.getApplJourneyTpCd() != cp2.getApplJourneyTpCd()) {
            Price price1 = queryService.findPrice(cp1);
            Price price2 = queryService.findPrice(cp2);
        }
        return false;
    }

    public void insertPriority() {
        //  ArrayList<Item> items = queryService.findAllItems();
        ArrayList<Integer> priorityOneList = new ArrayList<>();
        ArrayList<Integer> priorityTwoList = new ArrayList<>();
        ArrayList<Integer> priorityThreeList = new ArrayList<>();
        ArrayList<Integer> priorityFourList = new ArrayList<>();
        ArrayList<Integer> priorityFiveList = new ArrayList<>();
        ArrayList<Integer> prioritySixList = new ArrayList<>();
        ArrayList<Integer> prioritySevenList = new ArrayList<>();
        ArrayList<Integer> priorityEightList = new ArrayList<>();
        ArrayList<Integer> priorityNineList = new ArrayList<>();
        ArrayList<Integer> priorityTenList = new ArrayList<>();
        ArrayList<Integer> priorityElevenList = new ArrayList<>();
        ArrayList<Integer> priorityTwelveList = new ArrayList<>();
        ArrayList<Integer> priorityThirteenList = new ArrayList<>();
        ArrayList<Integer> priorityFourteenList = new ArrayList<>();
        ArrayList<Integer> priorityFifteenList = new ArrayList<>();
        ArrayList<Integer> prioritySixteenList = new ArrayList<>();
        ArrayList<Integer> prioritySeventeenList = new ArrayList<>();
        ArrayList<Integer> priorityEighteenList = new ArrayList<>();
        ArrayList<Integer> priorityNineteenList = new ArrayList<>();
        ArrayList<Integer> priorityTweentyList = new ArrayList<>();

        ArrayList<Integer> percentBasedPriceIds = queryService.findPercentBasedPriceIDs();
        ArrayList<Integer> PriceIds = queryService.findPercentBasedPriceIDs();

        HashMap<Integer, String> finalMap = new HashMap<Integer, String>();
        StringBuilder builder = new StringBuilder();
        ArrayList<Integer> contractcomponents = queryService.findDistinctContractComponentFRMContractprice();
        for (Integer contractComponentId : contractcomponents) {
            System.out.println("adsadasdasd");
            ArrayList<Long> items = queryService.findDistinctItemsFRMContractprice(contractComponentId);
            for (Long itemId : items) {
                //    if(itemId.equals(new Long(34))) {
                ArrayList<ContractPrice> contractPrices = queryService.findDistinctRoutesFRMContractprice(contractComponentId, itemId);
                HashMap<String, ArrayList<ContractPrice>> routeMap = new HashMap<String, ArrayList<ContractPrice>>();
                int counter = 0;
                for (ContractPrice cp : contractPrices) {
                    if (null != cp.getFromCountry() || null != cp.getToCountry()) {
                        String routes = "fromCountry" + cp.getFromCountry() + "-toCountry" + cp.getToCountry();
                        if (routeMap.containsKey(routes)) {
                            routeMap.get(routes).add(cp);
                        } else {
                            ArrayList<ContractPrice> list = new ArrayList<ContractPrice>();
                            list.add(cp);
                            routeMap.put(routes, list);
                        }
                    }
                    counter++;
                }

                finalMap.putAll(assignPriorityToDiscountLines(routeMap, percentBasedPriceIds));
                //}
            }

        }

        for (Integer key : finalMap.keySet()) {
            if (finalMap.get(key).equals("1"))
                priorityOneList.add(key);
            else if (finalMap.get(key).equals("2"))
                priorityTwoList.add(key);
            else if (finalMap.get(key).equals("3"))
                priorityThreeList.add(key);
            else if (finalMap.get(key).equals("4"))
                priorityFourList.add(key);
            else if (finalMap.get(key).equals("5"))
                priorityFiveList.add(key);
            else if (finalMap.get(key).equals("6"))
                prioritySixList.add(key);
            else if (finalMap.get(key).equals("7"))
                prioritySevenList.add(key);
            else if (finalMap.get(key).equals("8"))
                priorityEightList.add(key);
            else if (finalMap.get(key).equals("9"))
                priorityNineList.add(key);
            else if (finalMap.get(key).equals("10"))
                priorityTenList.add(key);
            else if (finalMap.get(key).equals("11"))
                priorityElevenList.add(key);
            else if (finalMap.get(key).equals("12"))
                priorityTwelveList.add(key);
            else if (finalMap.get(key).equals("13"))
                priorityThirteenList.add(key);
            else if (finalMap.get(key).equals("14"))
                priorityFourteenList.add(key);
            else if (finalMap.get(key).equals("15"))
                priorityFifteenList.add(key);
            else if (finalMap.get(key).equals("16"))
                prioritySeventeenList.add(key);
            else if (finalMap.get(key).equals("17"))
                prioritySeventeenList.add(key);
            else if (finalMap.get(key).equals("18"))
                priorityEighteenList.add(key);
            else if (finalMap.get(key).equals("19"))
                priorityNineteenList.add(key);
            else if (finalMap.get(key).equals("20"))
                priorityTweentyList.add(key);
        }
        if (!priorityOneList.isEmpty())
            updateContractPricePriority(1, priorityOneList);
        if (!priorityTwoList.isEmpty())
            updateContractPricePriority(2, priorityTwoList);
        if (!priorityThreeList.isEmpty())
            updateContractPricePriority(3, priorityThreeList);
        if (!priorityFourList.isEmpty())
            updateContractPricePriority(4, priorityFourList);
        if (!priorityFiveList.isEmpty())
            updateContractPricePriority(5, priorityFiveList);
        if (!prioritySixList.isEmpty())
            updateContractPricePriority(6, prioritySixList);
        if (!prioritySevenList.isEmpty())
            updateContractPricePriority(7, prioritySevenList);
        if (!priorityEightList.isEmpty())
            updateContractPricePriority(8, priorityEightList);
        if (!priorityNineList.isEmpty())
            updateContractPricePriority(9, priorityEightList);
        if (!priorityTenList.isEmpty())
            updateContractPricePriority(10, priorityEightList);
    }

    // Priority
    private HashMap<Integer, String> assignPriorityToDiscountLines(HashMap<String, ArrayList<ContractPrice>> routeMap, ArrayList<Integer> percentBasedPriceIds) {
        HashMap<Integer, String> hMap = new HashMap<Integer, String>();
        if (!routeMap.isEmpty()) {
            Set<String> keySet = routeMap.keySet();
            for (String sourceDestination : keySet) {
                HashMap<Integer, String> priorityMap = new HashMap<Integer, String>();
                ArrayList<ContractPrice> contractPrices = routeMap.get(sourceDestination);
                for (ContractPrice cp : contractPrices) {
                    String priorityLabel = defaultPriorityAllocation(cp);
                    System.out.println("Price ID - " + cp.getPriceId() + "  ::: priority -  " + priorityLabel);
                    priorityMap.put(cp.getPriceId().intValue(), priorityLabel);
                }
                hMap.putAll(setPriority(priorityMap, percentBasedPriceIds));
            }
        }
        return hMap;
    }

    private HashMap<Integer, String> setPriority(HashMap<Integer, String> priorityMap, ArrayList<Integer> percentBasedPriceIds) {
        if (!priorityMap.isEmpty()) {
            Set<String> priorityTypes = priorityMap.values().stream().collect(Collectors.toSet());
            if (priorityTypes.size() == 1) {
                for (Integer priceId : priorityMap.keySet()) {
                    if (percentBasedPriceIds.contains(priceId))
                        priorityMap.put(priceId, "2");
                    else
                        priorityMap.put(priceId, "1");
                }
            } else {
                System.out.println("wait");
                HashMap<String, Integer> priorityTypeMap = getPriorityByType();
                Set<Integer> prices = priorityMap.keySet();
                Set<String> finalPriority = new HashSet<String>();
                for (Integer priceID : prices) {
                    finalPriority.add(priorityMap.get(priceID));
                }
                HashMap<Integer, String> optimizePriority = new HashMap<Integer, String>();
                for (String priorityStr : finalPriority) {
                    optimizePriority.put(priorityTypeMap.get(priorityStr), priorityStr);
                }
                ArrayList<Integer> keys = new ArrayList<Integer>(optimizePriority.keySet());
                if (null != keys && !keys.isEmpty() && !keys.contains(null))
                    Collections.sort(keys);
                else {
                    System.out.println("wait");
                    System.exit(1);

                }

                for (int i = 0; i < keys.size(); i++) {
                    int comp = i + 1;
                    if (keys.get(i) > comp) {
                        String priorityVal = optimizePriority.get(keys.get(i));
                        Integer priorityId = priorityTypeMap.get(priorityVal);
                        if (priorityId > comp) {
                            priorityId = comp;
                            optimizePriority.remove(keys.get(i));
                            optimizePriority.put(priorityId, priorityVal);
                        }
                    }
                }
                for (Integer priority : optimizePriority.keySet()) {
                    for (Integer priceId : priorityMap.keySet()) {
                        if (priorityMap.get(priceId).equals(optimizePriority.get(priority))) {
                            if (percentBasedPriceIds.contains(priceId))
                                priority = priority + 1;
                            priorityMap.put(priceId, priority.toString());
                        }
                        System.out.println("PriceId " + priceId + "  ::: Priority - " + priority);
                    }
                }
            }
        }
        return priorityMap;
    }

    private HashMap<String, Integer> getPriorityByType() {
        HashMap<String, Integer> priorty = new HashMap<String, Integer>();

        priorty.put("FROM_POSTAL_FULL_TO_POSTAL_FULL", 1);

        priorty.put("FROM_POSTAL_PARTIAL3_TO_POSTAL_FULL", 2);
        priorty.put("FROM_POSTAL_FULL_TO_POSTAL_PARTIAL3", 2);

        priorty.put("FROM_POSTAL_PARTIAL2_TO_POSTAL_FULL", 3);
        priorty.put("FROM_POSTAL_FULL_TO_POSTAL_PARTIAL2", 3);

        priorty.put("FROM_POSTAL_PARTIAL1_TO_POSTAL_FULL", 4);
        priorty.put("FROM_POSTAL_FULL_TO_POSTAL_PARTIAL1", 4);

        priorty.put("FROM_POSTAL_PARTIAL3_TO_POSTAL_PARTIAL3", 5);

        priorty.put("FROM_POSTAL_PARTIAL3_TO_POSTAL_PARTIAL2", 6);
        priorty.put("FROM_POSTAL_PARTIAL2_TO_POSTAL_PARTIAL3", 6);

        priorty.put("FROM_POSTAL_PARTIAL3_TO_POSTAL_PARTIAL1", 7);
        priorty.put("FROM_POSTAL_PARTIAL1_TO_POSTAL_PARTIAL3", 7);

        priorty.put("FROM_POSTAL_PARTIAL2_TO_POSTAL_PARTIAL2", 8);

        priorty.put("FROM_POSTAL_PARTIAL2_TO_POSTAL_PARTIAL1", 9);
        priorty.put("FROM_POSTAL_PARTIAL1_TO_POSTAL_PARTIAL2", 9);

        priorty.put("FROM_POSTAL_PARTIAL1_TO_POSTAL_PARTIAL1", 10);

        priorty.put("FROM_POSTAL_FULL_TO_POSTAL_NULL", 11);
        priorty.put("FROM_POSTAL_NULL_TO_POSTAL_FULL", 11);

        priorty.put(FROM_POSTAL_PARTIAL3_TO_POSTAL_NULL, 12);
        priorty.put(FROM_POSTAL_NULL_TO_POSTAL_PARTIAL3, 12);

        priorty.put("FROM_POSTAL_NULL_TO_POSTAL_PARTIAL2", 13);
        priorty.put("FROM_POSTAL_PARTIAL2_TO_POSTAL_NULL", 13);

        priorty.put("FROM_POSTAL_NULL_TO_POSTAL_PARTIAL1", 14);
        priorty.put("FROM_POSTAL_PARTIAL1_TO_POSTAL_NULL", 14);

        priorty.put("FROM_ZONE_NOT_NULL_TO_ZONE_NOT_NULL", 15);

        priorty.put("FROM_ZONE_NULL_TO_ZONE_NOT_NULL", 16);
        priorty.put("FROM_ZONE_NOT_NULL_TO_ZONE_NULL", 16);

        priorty.put("ONLY_FROM_TO_COUNTRY", 17);
        return priorty;
    }

    private String defaultPriorityAllocation(ContractPrice contractprice) {

        String defaultAssignedPriorityScenario = null;
        if (contractprice != null) {
            // POSTAL CODES // PRIORITY 1
            if (contractprice.getFromPostalCode() != null && contractprice.getToPostcalCode() != null) {
                if (contractprice.getFromPostalCode().length() > 3 && contractprice.getToPostcalCode().length() > 3) {
                    defaultAssignedPriorityScenario = FROM_POSTAL_FULL_TO_POSTAL_FULL;
                }
                // PRIORITY 2
                else if (contractprice.getFromPostalCode().length() == 3 && contractprice.getToPostcalCode().length() > 3)
                    defaultAssignedPriorityScenario = FROM_POSTAL_PARTIAL3_TO_POSTAL_FULL;

                else if (contractprice.getFromPostalCode().length() > 3 && contractprice.getToPostcalCode().length() == 3)
                    defaultAssignedPriorityScenario = FROM_POSTAL_FULL_TO_POSTAL_PARTIAL3;

                    // priority 3
                else if (contractprice.getFromPostalCode().length() == 2 && contractprice.getToPostcalCode().length() > 3)
                    defaultAssignedPriorityScenario = FROM_POSTAL_PARTIAL2_TO_POSTAL_FULL;
                else if (contractprice.getFromPostalCode().length() > 3 && contractprice.getToPostcalCode().length() == 2)
                    defaultAssignedPriorityScenario = FROM_POSTAL_FULL_TO_POSTAL_PARTIAL2;

                    // priority 4
                else if (contractprice.getFromPostalCode().length() == 1 && contractprice.getToPostcalCode().length() > 3)
                    defaultAssignedPriorityScenario = FROM_POSTAL_PARTIAL1_TO_POSTAL_FULL;
                else if (contractprice.getFromPostalCode().length() > 3 && contractprice.getToPostcalCode().length() == 1)
                    defaultAssignedPriorityScenario = FROM_POSTAL_FULL_TO_POSTAL_PARTIAL1;

                    // priority 5
                else if (contractprice.getFromPostalCode().length() == 3 && contractprice.getToPostcalCode().length() == 3)
                    defaultAssignedPriorityScenario = FROM_POSTAL_PARTIAL3_TO_POSTAL_PARTIAL3;

                    // priority 6
                else if (contractprice.getFromPostalCode().length() == 3 && contractprice.getToPostcalCode().length() == 2)
                    defaultAssignedPriorityScenario = FROM_POSTAL_PARTIAL3_TO_POSTAL_PARTIAL2;
                else if (contractprice.getFromPostalCode().length() == 2 && contractprice.getToPostcalCode().length() == 3)
                    defaultAssignedPriorityScenario = FROM_POSTAL_PARTIAL2_TO_POSTAL_PARTIAL3;

                    // priority 7
                else if (contractprice.getFromPostalCode().length() == 3 && contractprice.getToPostcalCode().length() == 1)
                    defaultAssignedPriorityScenario = FROM_POSTAL_PARTIAL3_TO_POSTAL_PARTIAL1;
                else if (contractprice.getFromPostalCode().length() == 1 && contractprice.getToPostcalCode().length() == 3)
                    defaultAssignedPriorityScenario = FROM_POSTAL_PARTIAL1_TO_POSTAL_PARTIAL3;
                    // priority 8
                else if (contractprice.getFromPostalCode().length() == 2 && contractprice.getToPostcalCode().length() == 2)
                    defaultAssignedPriorityScenario = FROM_POSTAL_PARTIAL2_TO_POSTAL_PARTIAL2;
                    // priority 9
                else if (contractprice.getFromPostalCode().length() == 2 && contractprice.getToPostcalCode().length() == 1)
                    defaultAssignedPriorityScenario = FROM_POSTAL_PARTIAL2_TO_POSTAL_PARTIAL1;
                else if (contractprice.getFromPostalCode().length() == 1 && contractprice.getToPostcalCode().length() == 2)
                    defaultAssignedPriorityScenario = FROM_POSTAL_PARTIAL1_TO_POSTAL_PARTIAL2;

                    // priorty 10
                else if (contractprice.getFromPostalCode().length() == 1 && contractprice.getToPostcalCode().length() == 1)
                    defaultAssignedPriorityScenario = FROM_POSTAL_PARTIAL1_TO_POSTAL_PARTIAL1;
                else if (contractprice.getToCountryZoneCountFrom() != null && contractprice.getToCountryZoneCountTo() != null)
                    defaultAssignedPriorityScenario = FROM_ZONE_NOT_NULL_TO_ZONE_NOT_NULL;

            }
            //priority 11
            if (contractprice.getFromPostalCode() == null && contractprice.getToPostcalCode() != null) {
                if (contractprice.getFromPostalCode() == null && contractprice.getToPostcalCode().length() > 3)
                    defaultAssignedPriorityScenario = FROM_POSTAL_FULL_TO_POSTAL_NULL;
                else if (contractprice.getFromPostalCode() == null && contractprice.getToPostcalCode().length() == 3)
                    defaultAssignedPriorityScenario = FROM_POSTAL_NULL_TO_POSTAL_PARTIAL3;
                else if (contractprice.getFromPostalCode() == null && contractprice.getToPostcalCode().length() == 2)
                    defaultAssignedPriorityScenario = FROM_POSTAL_NULL_TO_POSTAL_PARTIAL2;
                else if (contractprice.getFromPostalCode() == null && contractprice.getToPostcalCode().length() == 1)
                    defaultAssignedPriorityScenario = FROM_POSTAL_NULL_TO_POSTAL_PARTIAL1;

            }
            if (contractprice.getFromPostalCode() != null && contractprice.getToPostcalCode() == null) {
                if (contractprice.getFromPostalCode().length() > 3 && contractprice.getToPostcalCode() == null)
                    defaultAssignedPriorityScenario = FROM_POSTAL_FULL_TO_POSTAL_NULL;
                else if (contractprice.getFromPostalCode().length() == 3 && contractprice.getToPostcalCode() == null)
                    defaultAssignedPriorityScenario = FROM_POSTAL_PARTIAL3_TO_POSTAL_NULL;
                else if (contractprice.getFromPostalCode().length() == 2 && contractprice.getToPostcalCode() == null)
                    defaultAssignedPriorityScenario = FROM_POSTAL_PARTIAL2_TO_POSTAL_NULL;
                else if (contractprice.getFromPostalCode().length() == 1 && contractprice.getToPostcalCode() == null)
                    defaultAssignedPriorityScenario = FROM_POSTAL_PARTIAL1_TO_POSTAL_NULL;
                // priority 16
            }
            if (contractprice.getFromPostalCode() == null && contractprice.getToPostcalCode() == null) {
                if (contractprice.getToCountryZoneCountFrom() != null && contractprice.getToCountryZoneCountTo() == null)
                    defaultAssignedPriorityScenario = FROM_ZONE_NOT_NULL_TO_ZONE_NULL;

                else if (contractprice.getToCountryZoneCountFrom() == null && contractprice.getToCountryZoneCountTo() != null)
                    defaultAssignedPriorityScenario = FROM_ZONE_NULL_TO_ZONE_NOT_NULL;
                else if (contractprice.getToCountryZoneCountFrom() != null && contractprice.getToCountryZoneCountTo() != null)
                    defaultAssignedPriorityScenario = FROM_ZONE_NOT_NULL_TO_ZONE_NOT_NULL;
            }
            if (contractprice.getToCountryZoneCountFrom() == null && contractprice.getToCountryZoneCountTo() == null
                    && contractprice.getFromPostalCode() == null && contractprice.getToPostcalCode() == null) {
                defaultAssignedPriorityScenario = ONLY_FROM_TO_COUNTRY;
            }

            if (defaultAssignedPriorityScenario == null) {
                System.out.println("check this case.. which scenario is not covered... ");
                System.exit(1);
            }
        }
        return defaultAssignedPriorityScenario;
    }

    @Transactional
    public void updateDeltaAgreementDuplicate(Contractdump contractdump, Deltacontractdump deltacontractdump) {
        try {
            String remark = "" + contractdump.getId();
            EntityManager entityManager=JPAUtil.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            String query = "update core.deltacontractdump set remark=" + remark + " where customer_number = '" + deltacontractdump.getCustomerNumber() + "' and  prodno = " + deltacontractdump.getProdNo();
            Query q=entityManager.createNativeQuery(query);
            q.executeUpdate();
            entityManager.getTransaction().commit();
            entityManager.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void fixContractroles(){
        ArrayList<ContractComponent> contractComponents = queryService.findContractComponents();
        ArrayList<String> duplicatelist = new ArrayList<String>();
        ArrayList<String> list = new ArrayList<String>();
        for(ContractComponent contractComponent : contractComponents) {
            List<ContractRole> roles = queryService.findContractRoles(contractComponent);
            for(ContractRole role : roles){
                if(list.contains(role.getPartySourceSystemRecordPk()) && role.getContractRoleTpCd()==1)
                    duplicatelist.add(role.getPartySourceSystemRecordPk());
             //    if(role.getContractRoleTpCd()==2)
                 //   list.add(role.getParty().getPartyId()+"~"+ role.getPartySourceSystemRecordPk()+"~"+role.);
            }
        }
        if(!duplicatelist.isEmpty()){
        for(String dupRole : duplicatelist)
            System.out.println(dupRole);
        }

    }

    enum ContractProcessing {
        PURE_COUNTRY_CODE,
        COUNTRY_CODE_FULL_POSTAL_CODE,
        COUNTRY_CODE_PARTIAL_POSTAL_CODE,
        STANDARD_ZONE,
        CUSTOM_ZONE,
        SURCHARE_EXEMPTION,
        COUNTRY_CODE_PARTIAL_FULL_POSTAL_CODE,
        ALL_SOURCE_ALL_DESTINATION
    }


    public void cleanUp() throws ParseException {
            List<ContractComponent> components = queryService.findAllContractComponents();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ConcurrentHashMap<String, ArrayList<Long>> validateMap = new ConcurrentHashMap<String, ArrayList<Long>>();
            for(ContractComponent component : components) {
                ArrayList<Long> items = queryService.findDistinctItemsFRMContractprice(component.getContractComponentId().intValue());
                for(Long itemID : items) {
                    List<ContractPrice> contractPrices = queryService.findContractPriceByComponentIDFIXEDPrice(component.getContractComponentId(), itemID);
                    if(!contractPrices.isEmpty()){
                        List<Price> duplicates = new ArrayList<Price>();
                        Iterator iterator = contractPrices.iterator();
                        Set<String> keySet = new HashSet<String>();
                        while(iterator.hasNext()) {
                            ContractPrice cp = (ContractPrice) iterator.next();
                            Price price = queryService.findPrice(cp);
                            Date startDate = Date.from(price.getStartDt().atStartOfDay(ZoneId.systemDefault()).toInstant());
                            Date endDate = Date.from(price.getEndDt().atStartOfDay(ZoneId.systemDefault()).toInstant());
                            String startDt = sdf.format(startDate);
                            String endDt = sdf.format(endDate);

                                String key = cp.getContractcomponentId() + "_" + cp.getItemIdDup() +"_" + // +endDt+"_"+ // "_" +endDt+"_"+ // price.getBasePrice()+"_"+
                                        cp.getFromCountry() + "_" + cp.getFromPostalCode() + "_" + cp.getToCountryZoneCountFrom() + "_" +
                                        cp.getToCountry() + "_" + cp.getToPostcalCode() + "_" + cp.getToCountryZoneCountTo() + "_" +
                                        cp.getZoneId() + "_" + cp.getFromRouteId() + "_" + cp.getToRouteId() + "_" + cp.getApplJourneyTpCd();
                                //if (validateMap.containsKey(key+"_1")) {
                                if(validateMap.containsKey(key)){
                                    validateMap.get(key).add(cp.getPriceId());
                                } else {
                                    ArrayList<Long> prices = new ArrayList<Long>();
                                    prices.add(cp.getPriceId());
                                    validateMap.put(key, prices);
                                }
                        }
                    }
                }
            }

        if (!validateMap.keySet().isEmpty()) {
            for (String keys : validateMap.keySet()) {
                if (validateMap.get(keys).size() <2)
                    validateMap.remove(keys);
                else{
                    System.out.println("asdsadasd");
                }
            }
        }
        ConcurrentHashMap<String, ArrayList<Long>> validateMap2 = new ConcurrentHashMap<String, ArrayList<Long>>();
            for(String key : validateMap.keySet()){
                ArrayList<Long> prices = validateMap.get(key);
                if(prices.size()==2){
                    Collections.sort(prices);
//                    ArrayList<Long> removePrice = new ArrayList<Long>();
//                    removePrice.add(prices.get(0));
             //       deletePrices(removePrice);
                    updatePriceEndDate(prices.get(0), prices.get(1));
//                    if(findOverlapCases(prices.get(0), prices.get(1))){
//                        validateMap2.put(key, prices);
//                    }
                    System.out.println(prices.get(0));
                }
            }
            System.out.println(validateMap2.size());
//            if(!validateMap2.isEmpty()) {
//                for(String key : validateMap2.keySet()){
//                ArrayList<Long> prices = validateMap2.get(key);
//                if(prices.size()>1) {
//                    Collections.sort(prices);
//                    updatePriceEndDate(prices.get(0), prices.get(1));
//                }
//            }
//            }
        }

    // Percent based
    public void cleanUpPERCENT() throws ParseException {
        List<ContractComponent> components = queryService.findAllContractComponents();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ConcurrentHashMap<String, ArrayList<Long>> validateMap = new ConcurrentHashMap<String, ArrayList<Long>>();
        for(ContractComponent component : components) {
            ArrayList<Long> items = queryService.findDistinctItemsFRMContractprice(component.getContractComponentId().intValue());
            for(Long itemID : items) {
                List<ContractPrice> contractPrices = queryService.findContractPriceByComponentAndItemIDPERCENT(component.getContractComponentId(), itemID);
                if(!contractPrices.isEmpty()){
                    List<Price> duplicates = new ArrayList<Price>();
                    Iterator iterator = contractPrices.iterator();
                    Set<String> keySet = new HashSet<String>();
                    while(iterator.hasNext()){
                        ContractPrice cp = (ContractPrice) iterator.next();
                        Price price = queryService.findPricePERCENT(cp);

                        Date startDate = Date.from(price.getStartDt().atStartOfDay(ZoneId.systemDefault()).toInstant());
                        Date endDate = Date.from(price.getEndDt().atStartOfDay(ZoneId.systemDefault()).toInstant());
                        String startDt = sdf.format(startDate);
                        String endDt = sdf.format(endDate);
                        String key = cp.getContractcomponentId()+"_"+cp.getItemIdDup()+ "_" +   endDt+"_" +  // price.getPercentBasedPrice()+
                                cp.getFromCountry()+"_"+cp.getFromPostalCode()+"_"+cp.getToCountryZoneCountFrom()+"_"+
                                cp.getToCountry()+"_"+cp.getToPostcalCode()+"_"+cp.getToCountryZoneCountTo() +  "_"+   cp.getApplJourneyTpCd()+"_"+
                                cp.getZoneId()+"_"+cp.getFromRouteId()+"_"+cp.getToRouteId();
                        if(validateMap.containsKey(key)){
                            validateMap.get(key).add(cp.getPriceId());
                        }else{
                            ArrayList<Long> prices = new ArrayList<Long>();
                            prices.add(cp.getPriceId());
                            validateMap.put(key, prices);
                        }
                    }
                }
/// delete code
            }
        }
        if(!validateMap.keySet().isEmpty()) {
            for (String key : validateMap.keySet()) {
                if (validateMap.get(key).size()<2)
                    validateMap.remove(key);
            }
        }
        ConcurrentHashMap<String, ArrayList<Long>> validateMap2 = new ConcurrentHashMap<String, ArrayList<Long>>();
        for(String key : validateMap.keySet()){
            ArrayList<Long> prices = validateMap.get(key);
            if(prices.size()==2){
                Collections.sort(prices);
//                updatePriceStartDate(prices.get(0), prices.get(1));
                ArrayList<Long> removePrice = new ArrayList<Long>();
                removePrice.add(prices.get(1));
                     deletePrices(removePrice);
                System.out.println(prices.get(0));
//                if(findOverlapCases(prices.get(0), prices.get(1))){
//                    validateMap2.put(key, prices);
//                }
                System.out.println(prices.get(0));
            }
            System.out.println(validateMap2.size());
        }
    }

    // remove slabbase duplicates
    public void cleanUpSLABBASED() throws ParseException {
        List<ContractComponent> components = queryService.findAllContractComponents();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ConcurrentHashMap<String, ArrayList<Long>> validateMap = new ConcurrentHashMap<String, ArrayList<Long>>();
        for(ContractComponent component : components) {
            ArrayList<Long> items = queryService.findDistinctItemsFRMContractprice(component.getContractComponentId().intValue());
            for(Long itemID : items) {
                List<ContractPrice> contractPrices = queryService.findContractPriceByComponentAndItemIDSLABBASED(component.getContractComponentId(), itemID);
                if(!contractPrices.isEmpty()){
                    List<Price> duplicates = new ArrayList<Price>();
                    Iterator iterator = contractPrices.iterator();
                    Set<String> keySet = new HashSet<String>();
                    while(iterator.hasNext()){
                        ContractPrice cp = (ContractPrice) iterator.next();

                        Price price = queryService.findPriceSLABBASED(cp);
                        Date startDate = Date.from(price.getStartDt().atStartOfDay(ZoneId.systemDefault()).toInstant());
                        Date endDate = Date.from(price.getEndDt().atStartOfDay(ZoneId.systemDefault()).toInstant());
                        String startDt = sdf.format(startDate);
                        String endDt = sdf.format(endDate);
                        String key = cp.getContractcomponentId()+"_"+cp.getItemIdDup()+ "_"+endDt+"_"+
                               "_"+cp.getFromCountry()+"_"+cp.getFromPostalCode()+ "_"+price.getBasePrice()+"_"+price.getPercentBasedPrice()+"_"
                            //    +cp.getContractpriceProcessingTpCd()+
                             +   "_"+cp.getToCountryZoneCountFrom()+"_"+cp.getToCountry()+"_"+cp.getToPostcalCode()+"_"+cp.getToCountryZoneCountTo()
                                +"_"+cp.getApplJourneyTpCd()
                               + cp.getZoneId()+"_"+cp.getFromRouteId()+"_"+cp.getToRouteId();
                        if(validateMap.containsKey(key)){
                            validateMap.get(key).add(cp.getPriceId());
                        }else{
                            ArrayList<Long> prices = new ArrayList<Long>();
                            prices.add(cp.getPriceId());
                            validateMap.put(key, prices);
                        }
                    }
                }
            }
        }
        if(!validateMap.keySet().isEmpty()) {
            for (String key : validateMap.keySet()) {
                if (validateMap.get(key).size()<2)
                    validateMap.remove(key);
            }
        }
        System.out.println("wait");ConcurrentHashMap<String, ArrayList<Long>> validateMap2 = new ConcurrentHashMap<String, ArrayList<Long>>();

        for(String key : validateMap.keySet()){
            ArrayList<Long> prices = validateMap.get(key);
            if(prices.size()==2){
                Collections.sort(prices);
                updatePriceStartDate(prices.get(0), prices.get(1));
//                ArrayList<Long> removePrice = new ArrayList<Long>();
//                removePrice.add(prices.get(0));
//                deletePrices(removePrice);
//                System.out.println(prices.get(0));
//                if(findOverlapCases(prices.get(0), prices.get(1))){
//                    validateMap2.put(key, prices);
//                }
                System.out.println(prices.get(0));
            }
        System.out.println(validateMap2.size());
//        System.out.println(validateMap2.size());
//        if(!validateMap2.isEmpty()) {
//            for(String key : validateMap2.keySet()){
//                ArrayList<Long> prices = validateMap2.get(key);
//                if(prices.size()>1) {
//                    Collections.sort(prices);
//                    updatePriceEndDate(prices.get(0), prices.get(1));
//                }
//            }
        }
    }

    public void cleanUpInFixedANDSlabbased() throws ParseException {
        List<ContractComponent> components = queryService.findAllContractComponents();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ConcurrentHashMap<String, ArrayList<Long>> validateMap = new ConcurrentHashMap<String, ArrayList<Long>>();

        for(ContractComponent component : components) {
            ArrayList<Long> items = queryService.findDistinctItemsFRMContractprice(component.getContractComponentId().intValue());
            for(Long itemID : items) {
                List<ContractPrice> contractPrices = queryService.findContractPriceByComponentIDFixedANDSlabbasedPrice(component.getContractComponentId(), itemID);
                if(!contractPrices.isEmpty()){
                    List<Price> duplicates = new ArrayList<Price>();
                    Iterator iterator = contractPrices.iterator();
                    Set<String> keySet = new HashSet<String>();
                    while(iterator.hasNext()) {

                        ContractPrice cp = (ContractPrice) iterator.next();
                        Price price = queryService.findPrice(cp);
                        Date startDate = Date.from(price.getStartDt().atStartOfDay(ZoneId.systemDefault()).toInstant());
                        Date endDate = Date.from(price.getEndDt().atStartOfDay(ZoneId.systemDefault()).toInstant());
                        String startDt = sdf.format(startDate);
                        String endDt = sdf.format(endDate);

                        String key = cp.getContractcomponentId() + "_" + cp.getItemIdDup() +  "_" +endDt+"_"+ // price.getBasePrice()+"_"+
                                cp.getFromCountry() + "_" + cp.getFromPostalCode() + "_" + cp.getToCountryZoneCountFrom() + "_" +
                                cp.getToCountry() + "_" + cp.getToPostcalCode() + "_" + cp.getToCountryZoneCountTo() + "_" +
                                cp.getZoneId() + "_" + cp.getFromRouteId() + "_" + cp.getToRouteId() ;//+ "_" + cp.getApplJourneyTpCd();
                        System.out.println("KEY --  "+key);
                        //if (validateMap.containsKey(key+"_1")) {
                        if(validateMap.containsKey(key)){
                            validateMap.get(key).add(cp.getPriceId());
                        } else {
                            ArrayList<Long> prices = new ArrayList<Long>();
                            prices.add(cp.getPriceId());
                            validateMap.put(key, prices);
                        }
                    }
                }
            }
        }

        if (!validateMap.keySet().isEmpty()) {
            for (String keys : validateMap.keySet()) {
                if (validateMap.get(keys).size() <3)
                    validateMap.remove(keys);
                else{
                    System.out.println("asdsadasd");
                }
            }
        }
        ConcurrentHashMap<String, ArrayList<Long>> validateMap2 = new ConcurrentHashMap<String, ArrayList<Long>>();
        for(String key : validateMap.keySet()){
            ArrayList<Long> prices = validateMap.get(key);
            if(prices.size()==3){
                Collections.sort(prices);
                ArrayList<Long> removePrice = new ArrayList<Long>();
                    removePrice.add(prices.get(0));
                    deletePrices(removePrice);
                //       updatePriceEndDate(prices.get(0), prices.get(1));
//                if(findOverlapCases(prices.get(0), prices.get(1))){
//                    validateMap2.put(key, prices);
//                }
                System.out.println(prices.get(0));
            }
            if(prices.size()==4){
                Collections.sort(prices);
                ArrayList<Long> removePrice = new ArrayList<Long>();
                    removePrice.add(prices.get(0));
                    removePrice.add(prices.get(1));
                    deletePrices(removePrice);
                //       updatePriceEndDate(prices.get(0), prices.get(1));
               }
        }
        System.out.println(validateMap2.size());
        if(!validateMap2.isEmpty()) {
            for(String key : validateMap2.keySet()){
                ArrayList<Long> prices = validateMap2.get(key);
                if(prices.size()>1) {
                    Collections.sort(prices);
                    updatePriceEndDate(prices.get(0), prices.get(1));
                }
            }
        }
    }



    public void updatePriceStartDate(Long oldPirce,  Long newPrice){
            Price oldContractPrice = queryService.findContractPrice(oldPirce);
            Price newContractPrice = queryService.findContractPrice(newPrice);

            if(oldContractPrice.getStartDt().isBefore(newContractPrice.getStartDt())){
                System.out.println("wait");
                oldContractPrice.setEndDt(newContractPrice.getStartDt().minusDays(1));
                updateEndDateINOldPrice( oldPirce, newContractPrice.getStartDt().minusDays(1));
            } else if(oldContractPrice.getStartDt().isAfter(newContractPrice.getStartDt())){
                System.out.println("wait");
             //   deletePrice(oldPirce);
            }
        }

        public Boolean findOverlapCases(Long oldPrice, Long newPrice){
            Price oldContractPrice = queryService.findContractPrice(oldPrice);
            Price newContractPrice = queryService.findContractPrice(newPrice);
            if(oldContractPrice.getEndDt().isAfter(newContractPrice.getStartDt()) &&
                    oldContractPrice.getStartDt().isBefore(newContractPrice.getStartDt())){
                return true;
            } else if(oldContractPrice.getStartDt().isAfter(newContractPrice.getStartDt())
                    && oldContractPrice.getStartDt().isBefore(newContractPrice.getEndDt())){
                return true;
            }else if(oldContractPrice.getStartDt().isBefore(newContractPrice.getStartDt()) &&
                    oldContractPrice.getEndDt().isAfter(newContractPrice.getEndDt())){
                return true;
            }else if(oldContractPrice.getStartDt().isAfter(newContractPrice.getStartDt()) &&
                    oldContractPrice.getEndDt().isBefore(newContractPrice.getEndDt())){
                return true;
            }else if(oldContractPrice.getStartDt().isEqual(newContractPrice.getStartDt()) &&
                    oldContractPrice.getEndDt().isEqual(newContractPrice.getEndDt())){
                if(!oldContractPrice.getPriceDefTpCd().equals(newContractPrice.getPriceDefTpCd()))
                    return true;
                else
                    return true;
            }
            else
                return false;
        }

    public void updatePriceEndDate(Long oldPirce,  Long newPrice){
        Price oldContractPrice = queryService.findContractPrice(oldPirce);
        Price newContractPrice = queryService.findContractPrice(newPrice);

        if(oldContractPrice.getEndDt().isAfter(newContractPrice.getStartDt())){
            System.out.println("wait");
            oldContractPrice.setEndDt(newContractPrice.getStartDt().minusDays(1));
            updateEndDateINOldPrice( oldPirce, newContractPrice.getStartDt().minusDays(1));
        } else if(oldContractPrice.getStartDt().isAfter(newContractPrice.getStartDt())){
            System.out.println("wait");
            //   deletePrice(oldPirce);
        }else if(oldContractPrice.getStartDt().isEqual(newContractPrice.getStartDt()) &&
                   newContractPrice.getEndDt().isEqual(oldContractPrice.getEndDt())){
            deletePrice(oldPirce);
        }
    }
    

	public static Set<String> buildParentCustomerSetDeltaContractDump(List<Deltacontractdump> list) {
		Set<String> customerParentSet=new HashSet<>();
		for(Deltacontractdump d:list) {
			String key=d.getCustomerNumber()+"~"+d.getOrganizationNumber();
			customerParentSet.add(key);
		}
		return customerParentSet;
	}
	
	public static Set<String> buildParentCustomerSetPercentagebaseddeltadump(List<Percentagebaseddeltadump> list) {
		Set<String> customerParentSet=new HashSet<>();
		for(Percentagebaseddeltadump d:list) {
			String key=d.getCustomerNumber()+"~"+d.getParentCustomerNumber();
			customerParentSet.add(key);
		}
		return customerParentSet;
	}
	public void email() {
		String smtpHost = "smtp.posten.no"; 
		int smtpPort = 25; 

		String sender = ""; //TODO replace this with a valid sender email address
		
		String recipient = ""; //TODO replace this with a valid recipient email address
		
		String content = "Hi,\n\nSharing the PDF for voilation of parent child details in delta agreements. \n\n Thanks and Regards,\n Price Engine Team"; //this will be the text of the email
		
		String subject = "PARENT CHILD VOILATION CASES IN DELTA AGREEMENTS"; //this will be the subject of the email
		
		String USERNAME="";//TODO add username for sending email
		
		String Password="";//TODO add Passowrd for email sender
		
		Properties properties = new Properties();
		
		properties.put("mail.smtp.host", smtpHost);
		properties.put("mail.smtp.port", smtpPort);    
		properties.put("mail.smtp.auth", true);
		
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USERNAME,Password );
			}
		});

		ByteArrayOutputStream outputStream = null;

		try {           
			MimeBodyPart textBodyPart = new MimeBodyPart();
			textBodyPart.setText(content);

			outputStream = new ByteArrayOutputStream();
			writePdf(outputStream);
			byte[] bytes = outputStream.toByteArray();
			if(bytes.length==0) {
				return;
			}

			DataSource dataSource = new ByteArrayDataSource(bytes, "application/pdf");
			MimeBodyPart pdfBodyPart = new MimeBodyPart();
			pdfBodyPart.setDataHandler(new DataHandler(dataSource));
			pdfBodyPart.setFileName("voilation-cases.pdf");

			MimeMultipart mimeMultipart = new MimeMultipart();
			mimeMultipart.addBodyPart(textBodyPart);
			mimeMultipart.addBodyPart(pdfBodyPart);

			InternetAddress iaSender = new InternetAddress(sender);
			InternetAddress iaRecipient = new InternetAddress(recipient);

			MimeMessage mimeMessage = new MimeMessage(session);
			mimeMessage.setSender(iaSender);
			mimeMessage.setSubject(subject);
			mimeMessage.setRecipient(Message.RecipientType.TO, iaRecipient);
			mimeMessage.setContent(mimeMultipart);

			Transport.send(mimeMessage);

			System.out.println("sent from " + sender + 
					", to " + recipient + 
					"; server = " + smtpHost + ", port =  " + smtpPort);         
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			if(null != outputStream) {

				try { outputStream.close(); outputStream = null; }
				catch(Exception ex) { }
			}
		}
	}

	public void writePdf(java.io.OutputStream outputStream) throws Exception {
		String[] headers = new String[]{"S.No", "IMI PARENT", "IMI CHILD", "PE PARENT","PE CHILD","REMARK"};
		
		Document document = new Document();

		try {
			PdfWriter.getInstance(document, outputStream);
			document.open();

			
			Font fontHeader = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			Font fontRow = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

			PdfPTable table = new PdfPTable(headers.length);
			for (String header : headers) {
				PdfPCell cell = new PdfPCell();
				cell.setGrayFill(0.9f);
				cell.setPhrase(new Phrase(header.toUpperCase(), fontHeader));
				table.addCell(cell);
			}
			table.completeRow();

			try {
				int sno=1;
				Set<String> set=buildParentCustomerSetDeltaContractDump(queryService.findAllDeltaContractdumpsForParentChild(PARENT_IS_NOW_CHILD));
						set.addAll(buildParentCustomerSetPercentagebaseddeltadump(queryService.findAllpercendumpsForParentChild(PARENT_IS_NOW_CHILD)));
				for(String key:set) {
					String[] arr=key.split("~");
					List<Party> parties=queryService.findPartyListForParentChildVoilation(arr[0]);
					if(parties==null || parties.isEmpty()) {
						parties=queryService.findPartyListForParentChildVoilationForParentNull(arr[0]);
					}
					for(Party p:parties) {
						String row=sno+"~";
						String IMI=arr[0]+"~"+arr[1]+"~";
						String PE=p.getParentSourceSystemRecordPk()+"~"+p.getSourceSystemRecordPk()+"~";
						row+=IMI+PE+"PARENT IS NOW CHILD";
						insertRow(row,table);
						sno++;
						row="";
					}
				}
				set=buildParentCustomerSetDeltaContractDump(queryService.findAllDeltaContractdumpsForParentChild(CHILD_HAS_CHANGED_PARENT));
				set.addAll(buildParentCustomerSetPercentagebaseddeltadump(queryService.findAllpercendumpsForParentChild(CHILD_HAS_CHANGED_PARENT)));
				for(String key:set) {
					String[] arr=key.split("~");
					List<Party> parties=queryService.findPartyListForParentChildVoilationForChildHasChengedarents(arr[1]);
					for(Party p:parties) {
						String row=sno+"~";
						String IMI=arr[0]+"~"+arr[1]+"~";
						String PE=p.getParentSourceSystemRecordPk()+"~"+p.getSourceSystemRecordPk()+"~";
						row+=IMI+PE+"CHILD HAS CHANGED PARENTS";
						insertRow(row,table);
						sno++;
						row="";
					}
				}
				set=buildParentCustomerSetDeltaContractDump(queryService.findAllDeltaContractdumpsForParentChild(CHILD_IS_NOW_PARENT));
				set.addAll(buildParentCustomerSetPercentagebaseddeltadump(queryService.findAllpercendumpsForParentChild(CHILD_IS_NOW_PARENT)));
				for(String key:set) {
					String[] arr=key.split("~");
					List<Party> parties=queryService.findPartyListForParentChildVoilation(arr[0]);
					for(Party p:parties) {
						String row=sno+"~";
						String IMI=arr[0]+"~"+arr[1]+"~";
						String PE=p.getParentSourceSystemRecordPk()+"~"+p.getSourceSystemRecordPk()+"~";
						row+=IMI+PE+"CHILD IS NOW PARENT";
						insertRow(row,table);
						sno++;
						row="";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			

			document.addTitle("PARENT CHILD VOILATION");
			document.add(table);
			document.close();
		}catch(Exception e) {
			System.out.println("Exception");
		}
	}
	
	public void insertRow(String str,PdfPTable table) {
		String[] rows=str.split("~");
			for (String data : rows) {
				Phrase phrase = new Phrase(data);
				table.addCell(new PdfPCell(phrase));
			}
			table.completeRow();
	}

}


