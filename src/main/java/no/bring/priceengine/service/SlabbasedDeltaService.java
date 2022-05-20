package no.bring.priceengine.service;



import no.bring.priceengine.dao.*;

import no.bring.priceengine.database.DatabaseService;

import no.bring.priceengine.database.QueryService;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;


import java.text.ParseException;

import java.util.*;

import java.util.function.Function;

import java.util.logging.Logger;

import java.util.stream.Collectors;



public class SlabbasedDeltaService {

    private DeltaRecordValidation deltaRecordValidation = new DeltaRecordValidation();

    private final String NEW_PARENT_CUSTOMER = "New Parent Customer";
    private final String NEW_CHILD_CUSTOMER = "New Child Customer";
    private final String NEW_CUSTOMER = "New Customer";
    private final String CUSTOMER_WITHOUT_DISCOUNT_LINE = "Customer without discount line";
    private final String CUSTOMER_IN_PARTY_NOT_IN_PE_CONTRACTS = "Customer in party but not in contract tables";
    private final String NEW_SERVICE = "New Service";
    private final String NEW_ROUTES = "New Routes";
    private final String NEW_DATES = "New Dates";
    private final String OVERLAP_WITH_EXISTING_DATES = "Overlap with anyone of dates";
    private final String OVERLAP_WITH_BOTH_DATES = "Overlap with both start-end dates";
    private final String NEW_PRICES = "New Prices";
    private final String RECORD_ALREADY_EXIST = "Already exist";
    private final String ITEM_ID_NOT_CONFIGURED = "Item not configured in PEDB";
    private final String NO_CHANGE_IN_PARTY = "No change in Party";
    private final String PARENT_IS_NOW_CHILD = "Parent is now a child";
    private final String CHILD_IS_NOW_PARENT = "Child is now a parent";
    private final String INDEPENDENT_IS_NOW_CHILD = "Previously independent now child";
    private final String INDEPENDENT_IS_NOW_PARENT = "Previously independent now parent";
    private final DatabaseService databaseService = new DatabaseService();
    private final QueryService queryService = new QueryService();
    private final ExcelService excelService = new ExcelService();
    private final DeltaServiceImpl deltaServiceImpl = new DeltaServiceImpl();

    private static HashMap<String, Set<String>> filterOrgnizationAndCustomers(HashSet<Deltacontractdump> contractdumps) {
        HashMap<String, Set<String>> orgMap = new HashMap<String, Set<String>>();
        if (!contractdumps.isEmpty()) {
            for (Deltacontractdump contractdump : contractdumps) {
                if (contractdump.getOrganizationNumber().equals(contractdump.getCustomerNumber())) {
                    if (!orgMap.containsKey(contractdump.getOrganizationNumber())) {
                        Set<String> customerList = new HashSet<String>();
                        orgMap.put(contractdump.getOrganizationNumber(), customerList);
                    }
                } else {
                    if (orgMap.containsKey(contractdump.getOrganizationNumber())) {
                        if (!orgMap.get(contractdump.getOrganizationNumber()).contains(contractdump.getCustomerNumber())) {
                            orgMap.get(contractdump.getOrganizationNumber()).add(contractdump.getCustomerNumber());
                        }
                    } else {
                        Set<String> customerList = new HashSet<String>();
                        customerList.add(contractdump.getCustomerNumber());
                        orgMap.put(contractdump.getOrganizationNumber(), customerList);
                    }
                }
            }
        }
        return orgMap;
    }

    public void processDeltaAgreements(String filecountry, Logger logger) throws ParseException {

        HashSet<Contractdump> newContractdumps = new HashSet<Contractdump>();
        Set<Deltacontractdump> deltacontractdumpsToUpload = new HashSet<Deltacontractdump>();
        ArrayList<Long> contractPricesToDelete = new ArrayList<Long>();
        ArrayList<Long> contractPricesToUpdateJourney1 = new ArrayList<Long>();
        ArrayList<Long> contractPricesToUpdateJourney2 = new ArrayList<Long>();

        HashMap<Long, ArrayList<Deltacontractdump>> contractPricesBothJourneyMap = new HashMap<Long, ArrayList<Deltacontractdump>>();
        HashSet<Deltacontractdump> deltacontractdumps = queryService.findAllDeltaContractdumpsWithJDBC(filecountry, null, logger, false);
        if (null != deltacontractdumps && !deltacontractdumps.isEmpty()) {
            HashMap<String, Set<String>> organizationMap = filterOrgnizationAndCustomers(deltacontractdumps);
            // All the discount lines, where customer'sstatus has changed will be processed in below method.
            // We will update the status and review and ask customer to validate before proceed.
            deltaRecordValidation.validateCustomerStatus(organizationMap, filecountry, logger, false);

            // Re call the method for remaining discount lines.
            deltacontractdumps = queryService.findAllDeltaContractdumpsWithJDBC(filecountry, null, logger, false);
            // change for discount line
            for (Deltacontractdump deltacontractdump : deltacontractdumps) {
                Party childParty = queryService.findChildPartyBySSPK(deltacontractdump.getCustomerNumber());
                Party parentParty = queryService.findParentParty(deltacontractdump.getOrganizationNumber());
                if (null != childParty && null!=parentParty){
                ContractRole contractRole = queryService.findCustomerInContractRoleAllType(deltacontractdump.getOrganizationNumber());
                ContractRole childContractRole = queryService.findCustomerInContractRoleAllType(deltacontractdump.getCustomerNumber());
                if (null == contractRole || null == childContractRole) {
                    deltacontractdumpsToUpload.add(deltacontractdump);
                    databaseService.updateDeltaContractDump(deltacontractdump, CUSTOMER_IN_PARTY_NOT_IN_PE_CONTRACTS, true, logger);
                } else {
                    Item item = queryService.getItem(deltacontractdump.getProdNo());
                    if (null != item) {
                        Item itemInCP = item;
                        if (item.getSourceSystemRecordPk().toString().length() == 5 && item.getSourceSystemRecordPk().toString().endsWith("64")) {
                            itemInCP = queryService.getItem(Integer.parseInt(item.getSourceSystemRecordPk().toString().substring(0, 3)));
                        } else if (item.getSourceSystemRecordPk().toString().length() == 7 && item.getSourceSystemRecordPk().toString().endsWith("64")) {
                            itemInCP = queryService.getItem(Integer.parseInt(item.getSourceSystemRecordPk().toString().substring(0, 5)));
                        }
                        List<ContractPrice> contractPrices = queryService.findContractPriceByComponentID(contractRole.getContractComponent().getContractComponentId(), itemInCP.getItemId());
                        if (contractPrices != null && !contractPrices.isEmpty()) {
                            // CHECK ITEM
                            Set<Integer> existinsItems = new HashSet<Integer>();
                            Iterator iter = contractPrices.iterator();
                            while (iter.hasNext()) {
                                ContractPrice cp = (ContractPrice) iter.next();
                                if (cp.getItemIdDup().equals(itemInCP.getItemId())) {
                                    existinsItems.add(cp.getItemIdDup().intValue());
                                } else
                                    iter.remove();
                            }

                            if (!existinsItems.contains(itemInCP.getItemId().intValue())) {
                                deltacontractdumpsToUpload.add(deltacontractdump);
                                databaseService.updateSingleDeltaContractDumpUsingJDBC(deltacontractdump, NEW_SERVICE, true, logger);
                                }
                            // CHECK ROUTE
                            if(!contractPrices.isEmpty()){
                            Boolean isRouteExist = false;
                            Iterator itr = contractPrices.iterator();
                            while (itr.hasNext()) {
                                ContractPrice cp = (ContractPrice) itr.next();
                                boolean isServicePassiveReturned = deltacontractdump.getProdNo().toString().startsWith("64", deltacontractdump.getProdNo().toString().length() - 2);
                                if (deltaServiceImpl.validateRoutes(cp, deltacontractdump, isServicePassiveReturned, contractPricesBothJourneyMap)) {
                                    isRouteExist = true;
                                } else
                                    itr.remove();
                            }
                            if (!isRouteExist) {
                                deltacontractdumpsToUpload.add(deltacontractdump);
                                databaseService.updateSingleDeltaContractDumpUsingJDBC(deltacontractdump, NEW_ROUTES, true, logger);
                            }
                        }
                            // CHECK SAME DATES
                            if (!contractPrices.isEmpty()) {
                                Iterator itrDate = contractPrices.iterator();

                                Boolean isDateConflict = false;
                                while (itrDate.hasNext()) {
                                    ContractPrice cp = (ContractPrice) itrDate.next();
                                    if (deltaServiceImpl.isOverlapWithSameDates(cp, deltacontractdump)) {
                                       if (deltaServiceImpl.isSamePrice(cp, deltacontractdump)) {
                                            deltacontractdump.setPriceId(cp.getPriceId().intValue());
                                            if(deltacontractdump.getRemark()!=null && !deltacontractdump.getRemark().equals("null"))
                                                databaseService.updateSingleDeltaContractDumpUsingJDBC(deltacontractdump, deltacontractdump.getRemark(), true, logger);
                                            else
                                                databaseService.updateSingleDeltaContractDumpUsingJDBC(deltacontractdump, RECORD_ALREADY_EXIST, true, logger);
                                            itrDate.remove();
                                        } else {
                                            deltacontractdumpsToUpload.add(deltacontractdump);
                                            deltacontractdump.setPriceId(cp.getPriceId().intValue());
                                            if(deltacontractdump.getRemark()!=null && !deltacontractdump.getRemark().equals("null"))
                                                databaseService.updateSingleDeltaContractDumpUsingJDBC(deltacontractdump, deltacontractdump.getRemark(), true, logger);
                                            else
                                                databaseService.updateSingleDeltaContractDumpUsingJDBC(deltacontractdump, NEW_PRICES, true, logger);
                                            itrDate.remove();
                                        }
                                    }
                                }
                            }
                            // CHECK OVERLAP IN DIFFERENT DATES
                            if (!contractPrices.isEmpty()) {
                                Iterator itrDateValidate = contractPrices.iterator();
                                ArrayList<ContractPrice> cpListConflict = new ArrayList<ContractPrice>();
                                while (itrDateValidate.hasNext()) {
                                    ContractPrice cp = (ContractPrice) itrDateValidate.next();
                                    if (!deltaServiceImpl.isNotOverlapDates(cp, deltacontractdump)) {
                                        cpListConflict.add(cp);
                                        deltacontractdump.setPriceId(cp.getPriceId().intValue());
                                        databaseService.updateSingleDeltaContractDumpUsingJDBC(deltacontractdump, OVERLAP_WITH_EXISTING_DATES, true, logger);
                                        deltacontractdumpsToUpload.add(deltacontractdump);
                                        itrDateValidate.remove();
                                    } else {
                                        // new date
                                        deltacontractdumpsToUpload.add(deltacontractdump);
                                        databaseService.updateSingleDeltaContractDumpUsingJDBC(deltacontractdump, NEW_DATES, true, logger);
                                        itrDateValidate.remove();
                                    }
                                }
                            }
                        } else {
                            deltacontractdumpsToUpload.add(deltacontractdump);
                            databaseService.updateSingleDeltaContractDumpUsingJDBC(deltacontractdump, NEW_SERVICE, true, logger);
                          }
                    } else {
                        databaseService.updateSingleDeltaContractDumpUsingJDBC(deltacontractdump, ITEM_ID_NOT_CONFIGURED, false, logger);
                    }
                }
            }else if(childParty==null && parentParty!=null)
                    databaseService.updateDeltaContractDump(deltacontractdump, NEW_CHILD_CUSTOMER, true, logger);
            else if(childParty!=null && parentParty==null)
                    databaseService.updateDeltaContractDump(deltacontractdump, NEW_PARENT_CUSTOMER, true, logger);
            else if(childParty==null && parentParty==null)
                    databaseService.updateDeltaContractDump(deltacontractdump, NEW_CUSTOMER, true, logger);
        }
    }
        // COMPARE AND UPDATE
        if (!contractPricesBothJourneyMap.isEmpty()) {
            for (Long priceId : contractPricesBothJourneyMap.keySet()) {
                Boolean isForward = false;
                Boolean isBackward = false;
                Boolean deltaDumpHavPriceID = false;
                for (Deltacontractdump deltacontractdump : contractPricesBothJourneyMap.get(priceId)) {
                    if (deltacontractdump.getProdNo().toString().endsWith("64"))
                        isBackward = true;
                    else
                        isForward = true;
                    if (deltacontractdump.getPriceId() != null)
                        deltaDumpHavPriceID = true;
                }
                if (isBackward && isForward && deltaDumpHavPriceID) {
                    contractPricesToDelete.add(priceId);
                } else if (isForward && !isBackward && deltaDumpHavPriceID) {
                    contractPricesToUpdateJourney2.add(priceId);
                   // databaseService.updateDeltaAggRemovePrice(priceId);
                } else if (!isForward && isBackward && deltaDumpHavPriceID) {
                    contractPricesToUpdateJourney1.add(priceId);
                    databaseService.updateDeltaAggRemovePrice(priceId);
                }
            }
        }
        if (!contractPricesToUpdateJourney1.isEmpty()) {
            for (Long priceId : contractPricesToUpdateJourney1) {
                ContractPrice contractPrice = queryService.findContractPriceObject(priceId);
                Price price = queryService.findPriceForDelete(contractPrice);
                databaseService.insertPriceHistory(price);
                databaseService.insertContractPriceHistory(contractPrice);
                contractPrice.setApplJourneyTpCd(1);
                databaseService.updateContractPriceJourneyType(1, priceId);
            }
        }

        if (!contractPricesToUpdateJourney2.isEmpty()) {
            for (Long priceId : contractPricesToUpdateJourney2) {
                ContractPrice contractPrice = queryService.findContractPriceObject(priceId);
                Price price = queryService.findPriceForDelete(contractPrice);
                databaseService.insertPriceHistory(price);
                databaseService.insertContractPriceHistory(contractPrice);
                contractPrice.setApplJourneyTpCd(2);
                databaseService.updateContractPriceJourneyType(2, priceId);
            }
        }
        if (!contractPricesBothJourneyMap.isEmpty()) {
            for (Long priceId : contractPricesBothJourneyMap.keySet()) {
                databaseService.updateDeltaAggRemovePrice(priceId);
            }
        }

        if(!deltacontractdumpsToUpload.isEmpty()){
            ArrayList<Deltacontractdump> dumps = new ArrayList<Deltacontractdump>(deltacontractdumpsToUpload);
            newContractdumps.addAll(deltaToContractdumps(dumps));
            databaseService.upsertContractData(newContractdumps);
        }
    }
    // Search in price tables and filter records

    public void validateDeltaChangesFRMPriceTables(String filecountry) {
        ArrayList<Contractdump> newContractduumps = new ArrayList<Contractdump>();
        List<Deltacontractdump> deltalist = queryService.findAllDeltaContractdumps(filecountry);
        if (null != deltalist && !deltalist.isEmpty()) {
            for (Deltacontractdump deltacontractdump : deltalist) {
                ContractRole parentCustomerContractRole = queryService.findCustomerInContractRole(deltacontractdump.getOrganizationNumber());
                ContractRole childCustomerContractRole = null;
                if (null == parentCustomerContractRole) {

                } else {

                    childCustomerContractRole = queryService.findCustomerInContractRole(deltacontractdump.getCustomerNumber());

                }

            }

        }

    }



    private String checkPartyStatus(String customerNumber, String type) {

        Party childParty = null;
        Party parentParty = null;
        String status = NO_CHANGE_IN_PARTY;
        // Check child TO Parent
        if (type.equals("CHILD")) {
            childParty = queryService.findChildPartyBySSPK(customerNumber);
            if (null != childParty)
                return CHILD_IS_NOW_PARENT;
        }
       // check parent to child
        if (type.equals("PARENT")) {
            parentParty = queryService.findParentParty(customerNumber);
            if (null != parentParty)
                return CHILD_IS_NOW_PARENT;
        }
        // CHECK Independent is now child
        return NO_CHANGE_IN_PARTY;
    }

    private Contractdump deltaToContractdump(Deltacontractdump deltacontractdump) {

        Contractdump contractdump = new Contractdump();
        contractdump.setOrganizationNumber(deltacontractdump.getOrganizationNumber());
        contractdump.setOrganizationName(deltacontractdump.getOrganizationName());
        contractdump.setCustomerNumber(deltacontractdump.getCustomerNumber());
        contractdump.setCustomerName(deltacontractdump.getCustomerName());
        contractdump.setDiv(deltacontractdump.getDiv());
        contractdump.setRouteTo(deltacontractdump.getRouteTo());
        contractdump.setRouteFrom(deltacontractdump.getRouteFrom());
        contractdump.setEnabled(true);
        contractdump.setRemark(5);
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

        return contractdump;
    }

    private ArrayList<Contractdump> deltaToContractdumps(ArrayList<Deltacontractdump> deltacontractdumps) {

        ArrayList<Contractdump> contractdumps = new ArrayList<Contractdump>();
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