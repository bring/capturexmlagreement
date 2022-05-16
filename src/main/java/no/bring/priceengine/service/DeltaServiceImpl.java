package no.bring.priceengine.service;

import no.bring.priceengine.dao.*;
import no.bring.priceengine.database.DatabaseService;
import no.bring.priceengine.database.QueryService;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DeltaServiceImpl {


    private DatabaseService databaseService = new DatabaseService();
    private QueryService queryService = new QueryService();
    private ExcelService excelService = new ExcelService();

    public void findRoutes(ArrayList<ContractPrice> contractPrices, Deltacontractdump deltacontractdump){
        for(ContractPrice contractPrice : contractPrices){

        }
    }

    public Boolean checkDates(ContractPrice contractPrice, Deltacontractdump contractdump) throws ParseException {
        Price price = queryService.findPrice(contractPrice);
        if(contractdump.getFromDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().compareTo(price.getStartDt())==0
           &&  contractdump.getToDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().compareTo(price.getEndDt())==0)
            return true;
        else
            return false;
    }

    public Boolean isNotOverlapDates(ContractPrice contractPrice, Deltacontractdump contractdump) throws ParseException{

        Price price = queryService.findPrice(contractPrice);
        Date startDate = Date.from(price.getStartDt().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(price.getEndDt().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date newStartDate = contractdump.getFromDate();
        Date newEndDate = contractdump.getToDate();
        if(newStartDate.compareTo(startDate)>0 && newStartDate.compareTo(endDate)<0)
            return false;
        if(newStartDate.compareTo(startDate)>0 && newEndDate.compareTo(endDate)<0)
            return false;
        if(newStartDate.compareTo(startDate)<0 && newEndDate.compareTo(endDate)>0)
            return false;
        if(newStartDate.compareTo(startDate)<0 && newEndDate.compareTo(startDate)<0)
            return true;
        if(newStartDate.compareTo(startDate)>0 && newEndDate.compareTo(endDate)>0)
            return true;
        else
            return false;
    }

    public Boolean isOverlapWithSameDates(ContractPrice contractPrice, Deltacontractdump contractdump) throws ParseException{
        Price price = queryService.findPrice(contractPrice);
        Date startDate = Date.from(price.getStartDt().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(price.getEndDt().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date newStartDate = contractdump.getFromDate();
        Date newEndDate = contractdump.getToDate();
            if(newEndDate.compareTo(endDate)==0  && newStartDate.compareTo(startDate)==0)
            return true;
        else
            return false;
    }

    public Boolean isNotOverlapWithSameDatesPERCENT(ContractPrice contractPrice, Percentagebaseddeltadump contractdump) throws ParseException{
        Price price = queryService.findPricePERCENT(contractPrice);
        Date startDate = Date.from(price.getStartDt().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(price.getEndDt().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date newStartDate = contractdump.getStartdate();
        Date newEndDate = contractdump.getEnddate();
            if(newEndDate.compareTo(endDate)==0  && newStartDate.compareTo(startDate)==0)
                return false;
            else {
                contractdump.setPriceId(price.getPriceId());
                return true;
            }
    }

    public Boolean isSamePrice(ContractPrice contractPrice, Deltacontractdump deltacontractdump) throws ParseException {

       Price price = queryService.findPrice(contractPrice);
                if(price.getPriceDefTpCd().equals(new Long(1)) && null!=deltacontractdump.getBasePrice() &&
                        deltacontractdump.getBasePrice().equals(Double.valueOf(String.valueOf(price.getBasePrice()))) &&
                        null!= deltacontractdump.getDscLimCd() && deltacontractdump.getDscLimCd().equals("null")) {
                    deltacontractdump.setPriceId(contractPrice.getPriceId().intValue());
                    return true;
                }else if(deltacontractdump.getDiscLmtFrom()!=null && deltacontractdump.getDiscLmtFrom().equals(new Double(-1))
                && price.getPriceDefTpCd().equals(new Long(6))){
                    deltacontractdump.setRemark("PRICE TYPE CHANGED FROM SLAB TO FIXED");
                    deltacontractdump.setPriceId(price.getPriceId().intValue());
                    return false;
                }else if(price.getPriceDefTpCd().equals(new Long(6)) && !deltacontractdump.getDiscLmtFrom().equals(new Double(-1))){
                    SlabBasedPrice slabBasedPrice = queryService.findSlabbasedPrice(price);
                    if(slabBasedPrice!=null && slabBasedPrice.getSlabBasedPriceId()!=null) {
                        Boolean result = false;
                        List<SlabBasedPriceEntry> slabs = queryService.findSlabbasedPriceEntry(slabBasedPrice.getSlabBasedPriceId());
                        for (SlabBasedPriceEntry slab : slabs) {
                            if (slab.getPriceValue().equals(new BigDecimal(deltacontractdump.getPrice()))) {
                                result = true;
                                return true;
                            }
                        }
                        return false;
                    }else{
                        System.out.println("Some data issue occures --  deltacontractdump - " + deltacontractdump.getOrganizationNumber() + "  :: customer "+ deltacontractdump.getCustomerNumber() +"  :: prodno " + deltacontractdump.getProdNo());
                    }

                }
            else if(price.getPriceDefTpCd().equals(new Long(1)) && deltacontractdump.getDiscLmtFrom()!=null && !deltacontractdump.getDiscLmtFrom().equals(new Double(-1))){
                deltacontractdump.setRemark("PRICE TYPE CHANGED FROM FIXED TO SLAB");
                return false;
            }else{
                deltacontractdump.setPriceId(contractPrice.getPriceId().intValue());
                return false;
            }
            return false;
    }

    public Boolean isSamePricePERCENT(ContractPrice contractPrice, Percentagebaseddeltadump deltacontractdump) throws ParseException {
        Price price = queryService.findPricePERCENT(contractPrice);
        if(null!= price){
            if(null!= price.getPercentBasedPrice()){
                BigDecimal d = new BigDecimal(deltacontractdump.getPrecentageDiscount()).setScale(5);
                if(price.getPercentBasedPrice().setScale(5).equals(d)) {
                    deltacontractdump.setPriceId(contractPrice.getPriceId().longValue());
                    return true;
                }
            }else{
                deltacontractdump.setPriceId(contractPrice.getPriceId().longValue());
                return false;
            }
        }else
            return false;
        return false;
    }

    public Boolean validateRoutes(ContractPrice contractPrice, Deltacontractdump contractdump, Boolean isServicePassiveReturned,
                                  HashMap<Long, ArrayList<Deltacontractdump>> contractPricesBothJourneyMap) throws ParseException{
        Boolean isSameRoutes = false;
        Country fromCountry = null;
        Country toCountry = null;
        String fromPostalCode = null;
        String toPostalCode = null;
        String fromZone = null;
        String toZone = null;
        Integer toZoneId349 = null;
        String zoneType = null;

        if(contractdump.getRouteFrom()!=null && contractdump.getRouteFrom().equals("NULL"))
            contractdump.setRouteFrom(null);
        if(contractdump.getRouteTo()!=null && contractdump.getRouteTo().equals("NULL"))
            contractdump.setRouteTo(null);
        if(contractdump.getRouteType()!=null && contractdump.getRouteType().equalsIgnoreCase("null"))
            contractdump.setRouteType(null);
        if(contractdump.getRoutetype()!=null && contractdump.getRoutetype().equalsIgnoreCase("null"))
            contractdump.setRoutetype(null);

        Integer fromCustomZone = null;
        Integer toCustomZone = null;

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
                } else{
                    toPostalCode = contractdump.getRouteTo().substring(2, contractdump.getRouteTo().toCharArray().length);
                    countryCodes.add(contractdump.getRouteTo().substring(0, 2));
                }
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
            }
            else if(excludedServiceList.contains(contractdump.getProdNo()) ){
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
                            System.out.println("route from cannot be null .. " + contractdump.getCustomerNumber() + ":::: "+contractdump.getProdNo());
                            System.exit(1);
                        } else if (contractdump.getProdNo().equals(33064) && null == contractdump.getRouteFrom() && null != contractdump.getRouteTo()) {
                            countryCodes.add(contractdump.getFileCountry());
                            System.out.println("route from cannot be null .. " + contractdump.getCustomerNumber());
                            System.exit(1);
                        }
                        toZone = contractdump.getRouteTo().substring(2, contractdump.getRouteTo().toCharArray().length);
                    }
                } else if (!excludedServiceList.contains(contractdump.getProdNo()) && null != contractdump.getZoneType() && contractdump.getZoneType().equals("C")) {
                    Cdcustomcountryroutetp cdcustomcountryroutetp = queryService.findCdcustomcountryroutetp(contractdump.getRouteTo());
                    if (null != cdcustomcountryroutetp)
                        toCustomZone = cdcustomcountryroutetp.getRouteId();

                } else if (excludedServiceList.contains(contractdump.getProdNo()) && contractdump.getRouteTo() != null) {
                    Cdhomedeliverytp cdhomedeliverytp = queryService.findCdhomedeliverytp(contractdump.getRouteTo());
                    if (null != cdhomedeliverytp)
                        toZoneId349 = cdhomedeliverytp.getZoneId();
                    else {
                        System.out.println("wait --njj   contractdump id " + contractdump.getCustomerNumber() + ":::: "+contractdump.getProdNo());
                        System.exit(1);
                    }
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
                        System.out.println("wait --njj" + contractdump.getCustomerNumber() + ":::: "+contractdump.getProdNo());
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

        if (null == contractdump.getRouteType()  && null != contractdump.getRouteFrom()) {
            if (contractdump.getRouteFrom().toCharArray().length == 2)
                countryCodes.add(contractdump.getRouteFrom());
            else {
                System.out.println("Some data is wrong. route type is null be route from contains mode details than country code only " + contractdump.getCustomerNumber() + ":::: "+contractdump.getProdNo());
                System.exit(1);
            }
        }
        if (null == contractdump.getRouteType() && null != contractdump.getRouteTo()) {
            countryCodes.add(contractdump.getRouteTo());
            if (excludedServiceList.contains(contractdump.getProdNo()) && contractdump.getZoneType()!=null) {
                Cdhomedeliverytp cdhomedeliverytp = queryService.findCdhomedeliverytp(contractdump.getRouteTo());
                if (null != cdhomedeliverytp)
                    toZoneId349 = cdhomedeliverytp.getZoneId();
                else {
                    System.out.println("Cdhomedeliverytp not found - contractdump table primary key -  " + contractdump.getCustomerNumber() + ":::: "+contractdump.getProdNo());
                  //  System.exit(1);
                }
            }
        }

        //  if(null==contractdump.getRouteType() || contractdump.getRouteType().equals("P")) {
        if (!countryCodes.isEmpty()) {
            countries = queryService.findCountries(countryCodes);
            if(countries.size()>2)
                System.out.println("break");
            for (Country country : countries) {

                if (null != contractdump.getRouteFrom() && country.getCountryCode().equals(contractdump.getRouteFrom().substring(0, 2)))
                    fromCountry = country;
                if (null != contractdump.getRouteTo() && country.getCountryCode().equals(contractdump.getRouteTo().substring(0, 2)))
                    toCountry = country;
            }
        }

        if(checkFromContry(contractPrice, fromCountry) && checkFromCountryPostalCode(contractPrice,fromPostalCode) &&
            checkToCountry(contractPrice, toCountry) && checkToCountryPostalCode(contractPrice, toPostalCode) &&
                checkToCountryZoneCountFrom(contractPrice, fromZone) && checkToCountryZoneCountTo(contractPrice, toZone) &&
                    checkFromRouteID(contractPrice, fromCustomZone) && checkToRouteID(contractPrice, toCustomZone)
                        && checkZoneID(contractPrice, toZoneId349)
        ){
            Boolean journeyStatus = false;
            if(null != contractPrice.getApplJourneyTpCd() && contractPrice.getApplJourneyTpCd()==1 && !isServicePassiveReturned)
                journeyStatus= true;
            else if(null != contractPrice.getApplJourneyTpCd() && contractPrice.getApplJourneyTpCd()==2 && isServicePassiveReturned)
                journeyStatus = true;
            else if(null != contractPrice.getApplJourneyTpCd() && contractPrice.getApplJourneyTpCd()==3){
                if(contractPricesBothJourneyMap.containsKey(contractPrice.getPriceId())){
                    contractPricesBothJourneyMap.get(contractPrice.getPriceId()).add(contractdump);
                }else{
                    ArrayList<Deltacontractdump> list = new ArrayList<Deltacontractdump>();
                    list.add(contractdump);
                    contractPricesBothJourneyMap.put(contractPrice.getPriceId(), list);
                }
                journeyStatus = true;
            }
            if(journeyStatus)
                    return true;
            else return
                false;
        }else{
            return false;
        }
    }

    public Boolean validateRoutesPERCENTBASED(ContractPrice contractPrice, Percentagebaseddeltadump contractdump, Boolean isServicePassiveReturned,
                                              ArrayList<ContractPrice> contractPricesBothJourney,
                                              HashMap<Long, ArrayList<Percentagebaseddeltadump>> contractPricesBothJourneyMap) throws ParseException{
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
                   // System.out.println("Custom country code not found "+ contractdump.getId());
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
                       // System.out.println("CUSTOM COUNTRY CODE NOT FOUND -  "+ contractdump.getId());

//                        cdcustomcountryroutetp = upsertCdcustomcountryroutetp(country.getCountryTpCd(), contractdump.getToLocation());
//                        toCustomZone = cdcustomcountryroutetp.getRouteId();
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
           //     System.out.println("Some data is wrong. route type is null be route from contains mode details than country code only" + contractdump.getId());
                System.exit(1);
            }
        }
        if (null == contractdump.getRouteType() && null != contractdump.getToLocation()) {
            if (contractdump.getToLocation().toCharArray().length == 2)
                countryCodes.add(contractdump.getToLocation());
            else {
             //   System.out.println("Some data is wrong. route type is null be route from contains mode details than country code only" + contractdump.getId());
                System.exit(1);
            }
        }

        //  if(null==contractdump.getRouteType() || contractdump.getRouteType().equals("P")) {
        if (!countryCodes.isEmpty()) {
            if(countryCodes.size()>2)
                System.out.println("wait... ERROR");
            countries = queryService.findCountries(countryCodes);
            for (Country country : countries) {
                if (null != contractdump.getFromLocation() && country.getCountryCode().equals(contractdump.getFromLocation().substring(0, 2)))
                    fromCountry = country;
                if (null != contractdump.getToLocation() && country.getCountryCode().equals(contractdump.getToLocation().substring(0, 2)))
                    toCountry = country;
            }
        }
        Boolean journeyStatus = false;

        if(checkFromContry(contractPrice, fromCountry) && checkFromCountryPostalCode(contractPrice,fromPostalCode) &&
                checkToCountry(contractPrice, toCountry) && checkToCountryPostalCode(contractPrice, toPostalCode) &&
                checkToCountryZoneCountFrom(contractPrice, fromZone) && checkToCountryZoneCountTo(contractPrice, toZone) &&
                checkFromRouteID(contractPrice, fromCustomZone) && checkToRouteID(contractPrice, toCustomZone)
                && checkZoneID(contractPrice, toZoneId349)
            ){
            if(null != contractPrice.getApplJourneyTpCd() && contractPrice.getApplJourneyTpCd()==1 && !isServicePassiveReturned) {
                journeyStatus = true;
            return true;
            }else if(null != contractPrice.getApplJourneyTpCd() && contractPrice.getApplJourneyTpCd()==2 && isServicePassiveReturned) {
                journeyStatus = true;
                return true;
            }
            else if(null != contractPrice.getApplJourneyTpCd() && contractPrice.getApplJourneyTpCd()==3){
                if(contractPricesBothJourneyMap.containsKey(contractPrice.getPriceId())){
                    contractPricesBothJourneyMap.get(contractPrice.getPriceId()).add(contractdump);
                }else{
                    ArrayList<Percentagebaseddeltadump> list = new ArrayList<Percentagebaseddeltadump>();
                    list.add(contractdump);
                    contractPricesBothJourneyMap.put(contractPrice.getPriceId(), list);
                }
            return  true;
            }else
            return false;
        }else{
            return false;
        }
    }

    private Boolean checkFromContry(ContractPrice contractPrice, Country fromCountry){
        if(fromCountry!=null){
            if(fromCountry.getCountryTpCd().equals(contractPrice.getFromCountry()))
                return true;
            else
                 return false;
            }else{
            if(contractPrice.getFromCountry()==null)
                return true;
            else
                return false;
        }
    }




    private Boolean checkFromCountryPostalCode(ContractPrice contractPrice, String postalCode){
        if(postalCode!=null){
            if(postalCode.equals(contractPrice.getFromPostalCode()))
                return true;
            else
                return false;
        }else{
            if(contractPrice.getFromPostalCode()==null)
                return true;
            else
                return false;
        }
    }

    private Boolean checkToCountry(ContractPrice contractPrice, Country toCountry){
        if(toCountry!=null){
            if(toCountry.getCountryTpCd().equals(contractPrice.getToCountry()))
                return true;
            else
                return false;
        }else{
            if(contractPrice.getToCountry()==null)
                return true;
            else
                return false;
        }
    }

    private Boolean checkToCountryPostalCode(ContractPrice contractPrice, String postalCode){
        if(postalCode!=null){
            if(postalCode.equals(contractPrice.getToPostcalCode()))
                return true;
            else
                return false;
        }else{
            if(contractPrice.getToPostcalCode()==null)
                return true;
            else
                return false;
        }
    }

    private Boolean checkToCountryZoneCountFrom(ContractPrice contractPrice, String toCountryZoneCountFrom){
        if(toCountryZoneCountFrom!=null && !toCountryZoneCountFrom.equalsIgnoreCase("NULL")){
            if(contractPrice.getToCountryZoneCountFrom()!=null && toCountryZoneCountFrom.equals(contractPrice.getToCountryZoneCountFrom().toString()))
                return true;
            else
                return false;
        }else{
            if(contractPrice.getToCountryZoneCountFrom()==null)
                return true;
            else
                return false;
        }
    }

    private Boolean checkToCountryZoneCountTo(ContractPrice contractPrice, String toCountryZoneCountTo){
        if(toCountryZoneCountTo!=null && !toCountryZoneCountTo.equalsIgnoreCase("NULL")){
            if(contractPrice.getToCountryZoneCountTo()!=null && toCountryZoneCountTo.equals(contractPrice.getToCountryZoneCountTo().toString()))
                return true;
            else
                return false;
        }else{
            if(contractPrice.getToCountryZoneCountFrom()==null)
                return true;
            else
                return false;
        }
    }

    private Boolean checkCustomerCurrency(ContractPrice contractPrice, String customerCurrency){
        if(customerCurrency!=null){
            if(customerCurrency.equals(contractPrice.getCurrency()))
                return true;
            else
                return false;
        }else{
            if(contractPrice.getCurrency()==null)
                return true;
            else
                return false;
        }
    }

    private Boolean checkZoneID(ContractPrice contractPrice, String zoneId){
        if(zoneId!=null){
            if(zoneId.equals(contractPrice.getZoneId()))
                return true;
            else
                return false;
        }else{
            if(contractPrice.getZoneId()==null)
                return true;
            else
                return false;
        }
    }

    private Boolean checkFromRouteID(ContractPrice contractPrice, Integer fromRouteId){
        if(fromRouteId!=null){
            if(fromRouteId.equals(contractPrice.getFromRouteId()))
                return true;
            else
                return false;
        }else{
            if(contractPrice.getFromRouteId()==null)
                return true;
            else
                return false;
        }
    }

    private Boolean checkZoneID(ContractPrice contractPrice, Integer zoneId){
        if(zoneId!=null){
            if(zoneId.equals(contractPrice.getZoneId()))
                return true;
            else
                return false;
        }else{
            if(contractPrice.getZoneId()==null)
                return true;
            else
                return false;
        }
    }

    private Boolean checkToRouteID(ContractPrice contractPrice, Integer toRouteId){
        if(toRouteId!=null){
            if(toRouteId.equals(contractPrice.getToRouteId()))
                return true;
            else
                return false;
        }else{
            if(contractPrice.getToRouteId()==null)
                return true;
            else
                return false;
        }
    }

    private Boolean valueOfJourney(ContractPrice contractPrice,  Boolean isServicePassiveReturned, Integer prodno,
                                   ArrayList<ContractPrice> contractPricesBothJourney) throws ParseException {
       Item  item = queryService.getItem(prodno);
       Item itemWithoutPassiveReturn = null;
        Integer valueOfourney = null;
        item = queryService.getNormalItemId(prodno);
        if (null != item) {
            if (isServicePassiveReturned)
                valueOfourney = 2;
            else
                valueOfourney = 1;
        } else {
            item = queryService.getPassiveItemId2(prodno);
            if (item != null && prodno.toString().substring(prodno.toString().length() - 2, prodno.toString().length()).equals("64")) {
                Integer temp = Integer.parseInt(prodno.toString().substring(0, prodno.toString().length() - 2));
                Item itemForAadd = queryService.getNormalItemId(temp);
                valueOfourney = 2;
            } else {
                item = queryService.getPassiveItemId(prodno);
                if (null != item) {
                    valueOfourney = 1;
                }
            }
        }
        if(contractPrice.getApplJourneyTpCd()!=null && contractPrice.getApplJourneyTpCd()==2 &&
                valueOfourney!=null && contractPrice.getApplJourneyTpCd().equals(valueOfourney))
            return true;
        else if(contractPrice.getApplJourneyTpCd()!=null && contractPrice.getApplJourneyTpCd()==3) {
            Price price = queryService.findPrice(contractPrice);
            LocalDate date= LocalDate.now();
            date = date.minusDays(3);
            if(price.getCreatedDt().isBefore(date))
            contractPricesBothJourney.add(contractPrice);
            return true;
        }
        else if(null==contractPrice.getApplJourneyTpCd() && valueOfourney==null)
            return true;
        else
            return false;
    }

    private ArrayList<Integer> service336List() {

        ArrayList<Integer> service336List = new ArrayList<Integer>();
        service336List.add(336);
        service336List.add(33664);
        return service336List;
    }


}
