package no.bring.priceengine.service;

import no.bring.priceengine.dao.Contractdump;
import no.bring.priceengine.dao.Deltacontractdump;
import no.bring.priceengine.dao.Party;
import no.bring.priceengine.dao.Percentagebaseddeltadump;
import no.bring.priceengine.database.DatabaseService;
import no.bring.priceengine.database.QueryService;

import java.util.*;
import java.util.logging.Logger;

public class DeltaRecordValidation {

    private DatabaseService databaseService = new DatabaseService();
    private DeltaServiceImpl deltaServiceImpl = new DeltaServiceImpl();
    private QueryService queryService = new QueryService();

    private final String NEW_CHILD_PARTY = "New Child Party";
    private final String NEW_PARENT_PARTY = "New Parent Party";
    private final String NO_CHANGE_IN_PARTY = "No change in Party";
    private final String PARENT_IS_NOW_CHILD = "Parent is now a child";
    private final String CHILD_IS_NOW_PARENT = "Child is now a parent";
    private final String CHILD_HAS_CHANGED_PARENT = "Child has changed parent";
    private final String INDEPENDENT_IS_NOW_CHILD = "Previously independent now child";
    private final String INDEPENDENT_IS_NOW_PARENT = "Previously independent now parent";


    public void validateCustomerStatus(HashMap<String, Set<String>> organizationMap, String filecountry, Logger logger, Boolean isPercentBased){

            for (String organizationDetails : organizationMap.keySet()) {
                //HashSet<Deltacontractdump> dumps = new HashSet<Deltacontractdump>();
                String status = checkPartyStatus(organizationDetails, null, "PARENT");
                if (!status.equals(NO_CHANGE_IN_PARTY)) {
                    HashSet<Deltacontractdump> dumps = queryService.findAllDeltaContractdumpsWithJDBC(filecountry, "'" + organizationDetails + "'", logger, false);
                    databaseService.updateDeltaContractDumps(dumps, status, true, logger);
                }// IF STATUS IS CHANGES.. BASE ON THAT .. APPLY FOR CHILD ALSO
                Set<String> customersList = organizationMap.get(organizationDetails);
                if (!customersList.isEmpty()) {
                    for (String customerDetails : customersList) {
                        HashSet<Deltacontractdump> dumps = new HashSet<Deltacontractdump>();
                        String result = checkPartyStatus(customerDetails, organizationDetails, "CHILD");
                        if (!result.equals(NO_CHANGE_IN_PARTY)) {
                            dumps = queryService.findAllDeltaContractdumpsWithJDBC(filecountry, "'" + customerDetails + "'", logger, true);
                            databaseService.updateDeltaContractDumps(dumps, result, true, logger);
                        }
                    }
                }
            }
    }

    public void validateCustomerStatusPercent(HashMap<String, Set<String>> organizationMap, String filecountry, Logger logger, Boolean isPercentBased){
        for (String organizationDetails : organizationMap.keySet()) {
            HashSet<Percentagebaseddeltadump> dumps = new HashSet<Percentagebaseddeltadump>();
            String status = checkPartyStatus(organizationDetails, null, "PARENT");
            if (!status.equals(NO_CHANGE_IN_PARTY) && !status.equals(NEW_PARENT_PARTY)) {
                dumps = queryService.findAllPercentbasedDeltaContractdumpsWithJDBC(filecountry, "'" + organizationDetails + "'", logger);
                databaseService.updateDeltaAgreementsPERCENT(dumps, PARENT_IS_NOW_CHILD,  logger);
            }
            Set<String> customersList = organizationMap.get(organizationDetails);
            if (!customersList.isEmpty()) {
                for (String customerDetails : customersList) {
                    dumps = new HashSet<Percentagebaseddeltadump>();
                    String result = checkPartyStatus(customerDetails, organizationDetails, "CHILD");
                    if (!result.equals(NO_CHANGE_IN_PARTY) && !result.equals(NEW_CHILD_PARTY)) {
                        dumps = queryService.findAllPercentbasedDeltaContractdumpsWithJDBC(filecountry, "'" + customerDetails + "'", logger);
                        databaseService.updateDeltaAgreementsPERCENT(dumps, result, logger);
                    }
                }
            }
        }
    }

    private String checkPartyStatus(String customerNumber,String parentCustomerInDelta, String type){

        if(customerNumber.equals("20010055307"))
            System.out.println("Wait");
        Party childParty = null;
        Party parentParty = null;
        String status = NO_CHANGE_IN_PARTY;
        // Check child TO Parent
        if(type.equals("CHILD")){
            childParty = queryService.findChildPartyBySSPK(customerNumber);
            parentParty = queryService.findParentParty(customerNumber);
            if(null==childParty && parentParty==null)
                return NEW_CHILD_PARTY;
            else if(null==childParty && null!=parentParty)
                return PARENT_IS_NOW_CHILD;
            else if(childParty!=null && childParty.getParentSourceSystemRecordPk().equals(parentCustomerInDelta))
                return  NO_CHANGE_IN_PARTY;
            else if(childParty!=null && !childParty.getParentSourceSystemRecordPk().equals(parentCustomerInDelta))
                return CHILD_HAS_CHANGED_PARENT;
        }
        // check parent to child
        if(type.equals("PARENT")){
            parentParty = queryService.findParentParty(customerNumber);
            childParty = queryService.findChildPartyBySSPK(customerNumber);
            if(null!=parentParty && childParty==null)
                return NO_CHANGE_IN_PARTY;
            else if(null == parentParty && null !=queryService.findChildPartyBySSPK(customerNumber))
                return CHILD_IS_NOW_PARENT;
            else if(null !=queryService.findChildPartyBySSPK(customerNumber))
                return CHILD_IS_NOW_PARENT;
            else if(parentParty==null && childParty==null)
                return NEW_PARENT_PARTY;
        }
        // CHECK Independent is now child
       return NO_CHANGE_IN_PARTY;
    }

    private HashSet<Contractdump> deltaToContractdumps(HashSet<Deltacontractdump> deltacontractdumps) {

        HashSet<Contractdump> contractdumps = new HashSet<Contractdump>();
        for (Deltacontractdump deltacontractdump : deltacontractdumps) {
            Contractdump contractdump = new Contractdump();
            contractdump.setOrganizationNumber(deltacontractdump.getOrganizationNumber());
            contractdump.setOrganizationName(deltacontractdump.getOrganizationName());
            contractdump.setCustomerNumber(deltacontractdump.getCustomerNumber());
            contractdump.setCustomerName(deltacontractdump.getCustomerName());
            contractdump.setDiv(deltacontractdump.getDiv());
            contractdump.setRouteTo(deltacontractdump.getRouteTo());
            contractdump.setRouteFrom(deltacontractdump.getRouteFrom());
            contractdump.setEnabled(true);
            contractdump.setRemark(6);
            contractdump.setADsc(deltacontractdump.getADsc());
            contractdump.setArtikelgrupp(deltacontractdump.getArtikelgrupp());
            contractdump.setBasePrice(deltacontractdump.getBasePrice());
            contractdump.setCurr(deltacontractdump.getCurr());
            contractdump.setDiscLmtFrom(deltacontractdump.getDiscLmtFrom());
            contractdump.setDscLimCd(deltacontractdump.getDscLimCd());
            contractdump.setFileCountry(deltacontractdump.getFileCountry());
            contractdump.setFromDate(deltacontractdump.getFromDate());
            contractdump.setKgTill(deltacontractdump.getKgTill());
            contractdump.setPrice(deltacontractdump.getPrice());
            contractdump.setProdDescr(deltacontractdump.getProdDescr());
            contractdump.setProdNo(deltacontractdump.getProdNo());
            contractdump.setPrUM(deltacontractdump.getPrUM());
            contractdump.setRouteType(deltacontractdump.getRouteType());
            contractdump.setStatGrupp(deltacontractdump.getStatGrupp());
            contractdump.setToDate(deltacontractdump.getToDate());
            contractdump.setUpdated(false);
            contractdump.setZoneType(deltacontractdump.getZoneType());
            contractdumps.add(contractdump);
        }

        return contractdumps;

    }



}
