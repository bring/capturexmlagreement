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

    private final String NEW_CUSTOMER = "New Customer";

    private final String CUSTOMER_WITHOUT_DISCOUNT_LINE = "Customer without discount line";

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

    //private static final String



    private static HashMap<String, Set<String>> filterOrgnizationAndCustomers(List<Deltacontractdump> contractdumps) {

        HashMap<String, Set<String>> orgMap = new HashMap<String, Set<String>>();

        if (!contractdumps.isEmpty()) {

            for (Deltacontractdump contractdump : contractdumps) {

                if (contractdump.getOrganizationNumber().equals(contractdump.getCustomerNumber())) {

                    if (!orgMap.containsKey(contractdump.getOrganizationNumber() + "~" + contractdump.getOrganizationName())) {

                        Set<String> customerList = new HashSet<String>();

                        //customerList.add(contractdump.getCustomerNumber() + "~" + contractdump.getCustomerName());

                        customerList.add(null);

                        orgMap.put(contractdump.getOrganizationNumber() + "~" + contractdump.getOrganizationName(), customerList);

                    }

//                    else {

//                        if (!orgMap.get(contractdump.getOrganizationNumber() + "~" + contractdump.getOrganizationName()).contains(contractdump.getCustomerNumber() + "~" + contractdump.getCustomerName()))

//                            orgMap.get(contractdump.getOrganizationNumber() + "~" + contractdump.getOrganizationName()).add(contractdump.getCustomerNumber() + "~" + contractdump.getCustomerName());

//                    }

                } else {

                    if (orgMap.containsKey(contractdump.getOrganizationNumber() + "~" + contractdump.getOrganizationName())) {



                        if (!orgMap.get(contractdump.getOrganizationNumber() + "~" + contractdump.getOrganizationName()).contains(contractdump.getCustomerNumber() + "~" + contractdump.getCustomerName())) {

                            orgMap.get(contractdump.getOrganizationNumber() + "~" + contractdump.getOrganizationName()).add(contractdump.getCustomerNumber() + "~" + contractdump.getCustomerName());

                            orgMap.get(contractdump.getOrganizationNumber() + "~" + contractdump.getOrganizationName()).remove(null);

                        }

                    } else {

                        Set<String> customerList = new HashSet<String>();

                        customerList.add(contractdump.getCustomerNumber() + "~" + contractdump.getCustomerName());

                        orgMap.put(contractdump.getOrganizationNumber() + "~" + contractdump.getOrganizationName(), customerList);

                    }

                }

            }

        }

        return orgMap;

    }



    public void processDeltaAgreements(String filecountry, Logger logger) throws ParseException {

        Set<Contractdump> newContractdumps = new HashSet<Contractdump>();
        Set<Deltacontractdump> deltacontractdumpsToUpload = new HashSet<Deltacontractdump>();
        Map<String, ArrayList<String>> ogranizationMap = new HashMap<String, ArrayList<String>>();
        ArrayList<Integer> priceIdToUpdatelist = new ArrayList<Integer>();
        ArrayList<Long> contractPricesToDelete = new ArrayList<Long>();
        ArrayList<Long> contractPricesToUpdateJourney1 = new ArrayList<Long>();
        ArrayList<Long> contractPricesToUpdateJourney2 = new ArrayList<Long>();
        ArrayList<Long> deltacontractdumpNewRoutesId = new ArrayList<Long>();
        Set<String> CHILD_IS_NOW_PARENT_SET = new HashSet<String>();
        Set<String> PARENT_IS_NOW_CHILD_SET = new HashSet<String>();

        HashMap<Long, ArrayList<Deltacontractdump>> contractPricesBothJourneyMap = new HashMap<Long, ArrayList<Deltacontractdump>>();
        List<Deltacontractdump> deltacontractdumps = queryService.findAllDeltaContractdumpsWithJDBC(filecountry, logger);
        if (null != deltacontractdumps && !deltacontractdumps.isEmpty()) {
            if (!deltacontractdumps.isEmpty()) {
                HashMap<String, Set<String>> organizationMap = filterOrgnizationAndCustomers(deltacontractdumps);
                if (!organizationMap.isEmpty()) {
                    int counter = 0;
                    for (String organizationDetails : organizationMap.keySet()) {
                        if (counter == 0) {
                            ArrayList<Deltacontractdump> dumps = new ArrayList<Deltacontractdump>();
                            if (checkPartyStatus(organizationDetails.split("~")[0], "PARENT").equals(PARENT_IS_NOW_CHILD)) {
                            //    dumps = queryService.findAllDeltaContractdumpsByOrganization(filecountry, organizationDetails.split("~")[0]);
                                databaseService.updateDeltaContractDumpUsingJDBC(dumps, CHILD_IS_NOW_PARENT, true, logger);
                                PARENT_IS_NOW_CHILD_SET.add(organizationDetails.split("~")[0]);
                            }
                            counter++;
                        }
                        Set<String> customersList = organizationMap.get(organizationDetails);
                        for (String customerDetails : customersList) {
                           ArrayList<Deltacontractdump> dumps = new ArrayList<Deltacontractdump>();
                            if (checkPartyStatus(organizationDetails.split("~")[0], "CHILD").equals(CHILD_IS_NOW_PARENT)) {
                            //    dumps = queryService.findAllDeltaContractdumpsByCustomer(filecountry, organizationDetails.split("~")[0]);
                           //     databaseService.updateDeltaAgreements(dumps, CHILD_IS_NOW_PARENT, false);
                                CHILD_IS_NOW_PARENT_SET.add(organizationDetails.split("~")[0]);
                                databaseService.updateDeltaContractDumpUsingJDBC(dumps, CHILD_IS_NOW_PARENT, true, logger);
                            }
                        }
                    }
                }
            }
            if(!PARENT_IS_NOW_CHILD_SET.isEmpty()){
                String disable="";
                for(String str : PARENT_IS_NOW_CHILD_SET){
                    disable = disable +"'"+ str + "', ";
                }
            }
            if(!CHILD_IS_NOW_PARENT_SET.isEmpty()){
                String disable="";
                for(String str : PARENT_IS_NOW_CHILD_SET){
                    disable = disable +"'"+ str + "', ";
                }
            }

            deltacontractdumps = queryService.findAllDeltaContractdumpsWithJDBC(filecountry, logger);

            ArrayList<Deltacontractdump> newOrganization = new ArrayList<Deltacontractdump>();
            for (Deltacontractdump deltacontractdump : deltacontractdumps) {
                if (null == queryService.findPartyByParentSSPK(deltacontractdump.getOrganizationNumber()))
                    newOrganization.add(deltacontractdump);
            }
            if (!newOrganization.isEmpty()) {
//                databaseService.updateDeltaAgreementsss(newOrganization, NEW_CUSTOMER, true);
                databaseService.updateDeltaContractDumpUsingJDBC(newOrganization, NEW_CUSTOMER, true, logger);
                deltacontractdumpsToUpload.addAll(newOrganization);
              //  newContractdumps.addAll(deltaToContractdumps(newOrganization));
            }
// WORK FROM HERER
            deltacontractdumps = queryService.findAllDeltaContractdumpsWithJDBC(filecountry, logger);

           // change for discount line
            for (Deltacontractdump deltacontractdump : deltacontractdumps) {
                    int insertedMissingRole = 0;
                int isInserted = 0;
                ContractRole contractRole = queryService.findCustomerInContractRoleAllType(deltacontractdump.getOrganizationNumber());
                if (null == contractRole) {
                  //  newContractdumps.add(deltaToContractdump(deltacontractdump));
                    deltacontractdumpsToUpload.add(deltacontractdump);
                    databaseService.updateDeltaContractDump(deltacontractdump, CUSTOMER_WITHOUT_DISCOUNT_LINE, true,logger);
                    isInserted++;
                    if(isInserted>1)
                        System.out.println("wait");
                    //databaseService.updateDeltaContractDump()
                } else {
                    Item item = queryService.getItem(deltacontractdump.getProdNo());
                    if (null != item) {
                        Item itemInCP = item;
                        if( item.getSourceSystemRecordPk().toString().length()==5 && item.getSourceSystemRecordPk().toString().endsWith("64")) {
                            itemInCP = queryService.getItem(Integer.parseInt(item.getSourceSystemRecordPk().toString().substring(0, 3)));
                        } else if( item.getSourceSystemRecordPk().toString().length()==7 && item.getSourceSystemRecordPk().toString().endsWith("64")) {
                            itemInCP = queryService.getItem(Integer.parseInt(item.getSourceSystemRecordPk().toString().substring(0, 5)));
                        }
                        List<ContractPrice> contractPrices = queryService.findContractPriceByComponentID(contractRole.getContractComponent().getContractComponentId(), itemInCP.getItemId());
                        if (contractPrices != null && !contractPrices.isEmpty()) {
                            // CHECK ITEM
                            ArrayList<Integer> existinsItems = new ArrayList<Integer>();
                            //for(ContractPrice cp : contractPrices){
                            Iterator iter = contractPrices.iterator();
                            while (iter.hasNext()) {
                                ContractPrice cp = (ContractPrice) iter.next();
                                if (cp.getItemIdDup().equals(itemInCP.getItemId())) {
                                    existinsItems.add(cp.getItemIdDup().intValue());
                                } else
                                    iter.remove();
                            }

                            if (!existinsItems.contains(itemInCP.getItemId().intValue())) {
                                //newContractdumps.add(deltaToContractdump(deltacontractdump));
                                deltacontractdumpsToUpload.add(deltacontractdump);
                                databaseService.updateSingleDeltaContractDumpUsingJDBC(deltacontractdump,NEW_SERVICE, true, logger);
                                isInserted++;
                                if(isInserted>1)
                                    System.out.println("wait");
                            }
                            // CHECK ROUTE
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
                                //newContractdumps.add(deltaToContractdump(deltacontractdump));
                                //databaseService.updateDeltaAgreement(deltacontractdump, NEW_ROUTES, true);
                                databaseService.updateSingleDeltaContractDumpUsingJDBC(deltacontractdump,NEW_ROUTES, true, logger);
                                //deltacontractdumpNewRoutesId.add(deltacontractdump.getId().longValue());
                                isInserted++;
                                if(isInserted>1)
                                    System.out.println("wait");
                            }

                            // CHECK DATES

                            if (!contractPrices.isEmpty()) {
                                Iterator itrDate = contractPrices.iterator();
                                Boolean isDateExist = false;
                                Boolean isDateConflict = false;
                                while (itrDate.hasNext()) {
                                    ContractPrice cp = (ContractPrice) itrDate.next();
                                    if (!deltaServiceImpl.isNotOverlapWithSameDates(cp, deltacontractdump)) {
                                        isDateExist = true;
                                        if (deltaServiceImpl.isSamePrice(cp, deltacontractdump)) {
                                            deltacontractdump.setPriceId(cp.getPriceId().intValue());
                                         //   databaseService.updateDeltaAgreement(deltacontractdump, RECORD_ALREADY_EXIST, false);
                                            isInserted++;
                                            if(isInserted>1)
                                                System.out.println("wait");
                                            databaseService.updateSingleDeltaContractDumpUsingJDBC(deltacontractdump,RECORD_ALREADY_EXIST, true, logger);
                                            itrDate.remove();
                                        } else {
                                            deltacontractdumpsToUpload.add(deltacontractdump);
                                            //newContractdumps.add(deltaToContractdump(deltacontractdump));
                                            deltacontractdump.setPriceId(cp.getPriceId().intValue());
                                            //databaseService.updateDeltaAgreement(deltacontractdump, NEW_PRICES, true);
                                            isInserted++;
                                            if(isInserted>1)
                                                System.out.println("wait");
                                            databaseService.updateSingleDeltaContractDumpUsingJDBC(deltacontractdump,NEW_PRICES, true, logger);
                                            itrDate.remove();
                                        }
                                    }
                                }
                            }
                            if (!contractPrices.isEmpty()) {
                                Iterator itrDateValidate = contractPrices.iterator();
                                ArrayList<ContractPrice> cpListConflict = new ArrayList<ContractPrice>();
                                while (itrDateValidate.hasNext()) {
                                    ContractPrice cp = (ContractPrice) itrDateValidate.next();
                                    if (!deltaServiceImpl.isNotOverlapDates(cp, deltacontractdump)) {
                                        cpListConflict.add(cp);
                                        deltacontractdump.setPriceId(cp.getPriceId().intValue());
                                     //   databaseService.updateDeltaAgreement(deltacontractdump, OVERLAP_WITH_EXISTING_DATES, true);
                                        isInserted++;
                                        if(isInserted>1)
                                            System.out.println("wait");
                                        databaseService.updateSingleDeltaContractDumpUsingJDBC(deltacontractdump,OVERLAP_WITH_EXISTING_DATES, true, logger);
                                     //   newContractdumps.add(deltaToContractdump(deltacontractdump));
                                        deltacontractdumpsToUpload.add(deltacontractdump);
                                        itrDateValidate.remove();
                                    } else {
                                        // new date
                                      //  newContractdumps.add(deltaToContractdump(deltacontractdump));
                                        deltacontractdumpsToUpload.add(deltacontractdump);
                                        //databaseService.updateDeltaAgreement(deltacontractdump, NEW_DATES, true);
                                        databaseService.updateSingleDeltaContractDumpUsingJDBC(deltacontractdump,NEW_DATES, true, logger);

                                        isInserted++;
                                        if(isInserted>1)
                                            System.out.println("wait");
                                        itrDateValidate.remove();

                                    }

                                }

                            }

                        } else {
                        //    newContractdumps.add(deltaToContractdump(deltacontractdump));
                            deltacontractdumpsToUpload.add(deltacontractdump);
                            databaseService.updateSingleDeltaContractDumpUsingJDBC(deltacontractdump,NEW_SERVICE, true, logger);
                            isInserted++;
                            if(isInserted>1)
                                System.out.println("wait");

                        }

                    } else {
                        //databaseService.updateDeltaAgreement(deltacontractdump, ITEM_ID_NOT_CONFIGURED, false);
                        databaseService.updateSingleDeltaContractDumpUsingJDBC(deltacontractdump,ITEM_ID_NOT_CONFIGURED, false, logger);
                        isInserted++;
                        if(isInserted>1)
                            System.out.println("wait");
                    }

                }

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
                    databaseService.updateDeltaAggRemovePrice(priceId);
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