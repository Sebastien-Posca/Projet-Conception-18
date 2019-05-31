package fr.unice.polytech.cookieFactory;

import fr.unice.polytech.cookieFactory.Shop.RegisteredCustomer;
import fr.unice.polytech.cookieFactory.Shop.Shop;
import fr.unice.polytech.cookieFactory.order.Order;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatisticsCalculator {

    private StatisticsCalculator(){}

    private static Stream<Order> filter_date(Stream<Order> stream, LocalDate beginDate, LocalDate endDate){
        return stream.filter(order -> {

            LocalDate pickupDate = order.getPickupTime().toLocalDate();

            return (pickupDate.isAfter(beginDate) && pickupDate.isBefore(endDate))
                    || pickupDate.isEqual(beginDate)
                    || pickupDate.isEqual(endDate);

        });
    }

    private static Map computeRepartition(Map objectToLongMap){

        @SuppressWarnings("unchecked")
        Map<Object, Long> map = (Map<Object, Long>)objectToLongMap;

        @SuppressWarnings("unchecked")
        long order_count = map.values().stream().reduce(0L, (a, b) -> a + b);

        if (order_count == 0) {
            return new HashMap<Object, Float>(0);
        }

        Map<Object, Float> repartitionMap = new HashMap<>(24);

        for (Object o : map.keySet()) {
            repartitionMap.put(o, (float) map.get(o) / order_count);
        }

        return repartitionMap;
    }

    /**
     * Computes a pickup time repartition for delivered orders between beginDate (inclusive)
     * and endDate (inclusive)
     *
     * @param beginDate (inclusive)
     * @param endDate   (inclusive)
     * @return a mapping : hour -> percentage of people picking their order at this hour, an empty map if there is no
     * order between beginDate and endDate.
     */
    public static Map<Integer, Float> computePickUpTimeRepartition(LocalDate beginDate, LocalDate endDate, List<Order> orderList) {

        Map<Integer, Long> hourToCountMap =
                filter_date(orderList.stream(), beginDate, endDate)
                .map(order -> (Integer) order.getPickupTime().getHour())
                .collect(Collectors.groupingBy(
                        Function.identity(), Collectors.counting()
                ));

        return (Map<Integer, Float>)computeRepartition(hourToCountMap);
    }



    /**
     * Computes a recipe repartition for delivered orders between beginDate (inclusive)
     * and endDate (inclusive)
     *
     * @param beginDate (inclusive)
     * @param endDate   (inclusive)
     * @param args
     * @return a mapping : recipe's name -> percentage of item orders of this recipe, an empty map if there is no
     * order between beginDate and endDate.
     */
    public static Map<String, Float> computeRecipeRepartition(LocalDate beginDate, LocalDate endDate, List<Order> orderList
                                                             , Object[] args) {

        boolean group_custom_recipes = (Boolean)args[0];
        Map<String, Long> recipeNameToCount =
                filter_date(orderList.stream(), beginDate, endDate)
                .flatMap(order -> order.getItemOrders()
                                 .stream().flatMap(x -> {
                                     String name;
                                     if (x.getCookieRecipe().getName().contains(",") && group_custom_recipes)
                                         name = "Custom";
                                     else
                                        name = x.getCookieRecipe().getName();
                                     String[] cookies = new String[x.getCount()];
                                     for(int i=0; i < x.getCount(); i++) cookies[i] = name;
                                     return Arrays.stream(cookies);
                                  })
                ).collect(Collectors.groupingBy(
                        Function.identity(), Collectors.counting()
                ));

        return (Map<String, Float>)computeRepartition(recipeNameToCount);

    }

    public static int computeAverageDeliveredCookieNumber(LocalDate beginDate, LocalDate endDate, List<Order> orderList,
                                                          Object[] args){

        Map<LocalDate, Long> dayToCountMap =
                filter_date(orderList.stream(), beginDate, endDate)
                .collect(Collectors.groupingBy(
                    order -> order.getPickupTime().toLocalDate(), Collectors.counting()
                ));

        if(dayToCountMap.isEmpty()) return 0;

        long deliveredCookieCount = dayToCountMap.values().stream().reduce(0L, (a, b) -> a + b);

        return (int)deliveredCookieCount / dayToCountMap.size();
    }


    public static Object computeStatForShop(String statName, LocalDate beginDate, LocalDate endDate,
                                            Shop shop, Object... args){
        return computeStatForOrders(statName, beginDate, endDate, shop.getDeliveredOrders(), args);
    }

    public static Object computeStatForCatalog(String statName, LocalDate beginDate, LocalDate endDate,
                                               RegisteredCustomer.ShopCatalog shopCatalog, Object... args)
    {
        List<Order> orders = shopCatalog.stream().flatMap(shop -> shop.getDeliveredOrders()
                                        .stream()).collect(Collectors.toList());

        return computeStatForOrders(statName, beginDate, endDate, orders, args);
    }

    public static Object computeStatForOrders(String statName, LocalDate beginDate, LocalDate endDate,
                                              List<Order> orders, Object... args){
        String methodName = "compute".concat(statName);
        try {
            return  StatisticsCalculator.class.getMethod(methodName, LocalDate.class, LocalDate.class, List.class, Object[].class)
                    .invoke(null, beginDate, endDate, orders, args
                    );
        }catch (NoSuchMethodException e){
            return "the statistic " + statName + " does not exist.";
        }catch (IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

}