package icecream;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class IceCream {
    public static void main(String[] args) {
        List<SaleRecord> salesData = readSalesData("C:\\Users\\91620\\OneDrive\\Desktop\\sales-data.txt");

        int totalSales = calculateTotalSales(salesData);
        System.out.println("Total sales: " + totalSales);

        Map<String, Double> monthlySales = calculateMonthlySales(salesData);
        System.out.println("Monthly sales totals:");
        for (Map.Entry<String, Double> entry : monthlySales.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
       // Map<String, String> mostPopularItemlist = findMostPopularItemlist(salesData);
        
        Map<String, String> mostPopularItems = findMostPopularItem(salesData);
        System.out.println("Most popular item in each month:");
        for (Map.Entry<String, String> entry : mostPopularItems.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        Map<String, String> itemsGeneratingMostRevenue = findItemsGeneratingMostRevenue(salesData);
        System.out.println("Items generating most revenue in each month:");
        for (Map.Entry<String, String> entry : itemsGeneratingMostRevenue.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        for (Map.Entry<String, String> entry : mostPopularItems.entrySet()) {
            String month = entry.getKey();
            String item = entry.getValue();
            List<Integer> orders = findOrdersForItemAndMonth(salesData, item, month);
            OrdersStats stats = calculateOrdersStats(orders);
            System.out.println("Orders stats for " + item + " in " + month);
            System.out.println("Min orders: " + stats.getMinOrders());
            System.out.println("Max orders: " + stats.getMaxOrders());
            System.out.println("Average orders: " + stats.getAvgOrders());
        }
    }

    private static List<SaleRecord> readSalesData(String filename) {
        List<SaleRecord> salesData = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] record = line.split(",");
                SaleRecord saleRecord = new SaleRecord(record[0], record[1], Double.parseDouble(record[2]),
                        Integer.parseInt(record[3]), Double.parseDouble(record[4]));
                salesData.add(saleRecord);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return salesData;
    }

    private static int calculateTotalSales(List<SaleRecord> salesData) {
        int totalSales = 0;
        for (SaleRecord record : salesData) {
            totalSales += record.getTotalPrice();
        }
        return totalSales;
    }

    private static Map<String, Double> calculateMonthlySales(List<SaleRecord> salesData) {
        Map<String, Double> monthlySales = new HashMap<>();
        for (SaleRecord record : salesData) {
            String month = record.getDate().substring(0, 7); // Extracting year-month from the date
            double totalPrice = record.getTotalPrice();
            monthlySales.put(month, monthlySales.getOrDefault(month, 0.0) + totalPrice);
        }
        return monthlySales;
    }
    
    
    //xxx
   /** private static Map<String, String> findMostPopularItem(List<SaleRecord> salesData) {
        Map<String, String> mostPopularItems = new HashMap<>();
        for (SaleRecord record : salesData) {
            String month = record.getDate().substring(0, 7); // Extracting year-month from the date
            String item = record.getItem();
            int quantity = record.getQuantity();

            if (!mostPopularItems.containsKey(month)) {
                mostPopularItems.put(month, item);
            } else {
                String currentPopularItem = mostPopularItems.get(month);
                int currentQuantity = getQuantityForItemAndMonth(salesData, currentPopularItem, month);
                if (quantity > currentQuantity) {
                    mostPopularItems.put(month, item);
                }
            }
        }
        return mostPopularItems;
    }*/
    //xxx
    
    private static Map<String, String> findMostPopularItem(List<SaleRecord> salesData) {
        Map<String, Map<String, Integer>> popularItems = new HashMap<>();
        for (SaleRecord record : salesData) {
            String month = record.getDate().substring(0, 7); // Extracting year-month from the date
            String item = record.getItem();
            int quantity = record.getQuantity();
            if (!popularItems.containsKey(month)) {
                popularItems.put(month, new HashMap<>());
            }
            Map<String, Integer> items = popularItems.get(month);
            items.put(item, items.getOrDefault(item, 0) + quantity);
        }

        Map<String, String> mostPopularItems = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : popularItems.entrySet()) {
            String month = entry.getKey();
            Map<String, Integer> items = entry.getValue();
            int maxQuantity = Collections.max(items.values());
            for (Map.Entry<String, Integer> itemEntry : items.entrySet()) {
                if (itemEntry.getValue() == maxQuantity) {
                    mostPopularItems.put(month, itemEntry.getKey());
                    break;
                }
            }
        }

        return mostPopularItems;
    }

    
    private static Map<String, String> findItemsGeneratingMostRevenue(List<SaleRecord> salesData) {
        Map<String, Map<String, Double>> revenueItems = new HashMap<>();
        for (SaleRecord record : salesData) {
            String month = record.getDate().substring(0, 7); // Extracting year-month from the date
            String item = record.getItem();
            
            double revenue = record.getTotalPrice();
            if (!revenueItems.containsKey(month)) {
                revenueItems.put(month, new HashMap<>());
            }
            Map<String, Double> items = revenueItems.get(month);
            items.put(item, items.getOrDefault(item, 0.0) + revenue);
        }

        Map<String, String> itemsGeneratingMostRevenue = new HashMap<>();
        for (Map.Entry<String, Map<String, Double>> entry : revenueItems.entrySet()) {
            String month = entry.getKey();
            Map<String, Double> items = entry.getValue();
            double maxRevenue = Collections.max(items.values());
            for (Map.Entry<String, Double> itemEntry : items.entrySet()) {
                if (itemEntry.getValue() == maxRevenue) {
                    itemsGeneratingMostRevenue.put(month, itemEntry.getKey());
                    break;
                }
            }
        }

        return itemsGeneratingMostRevenue;
    }

  /**  private static Map<String, String> findItemsGeneratingMostRevenue(List<SaleRecord> salesData) {
        Map<String, String> itemsGeneratingMostRevenue = new HashMap<>();
        for (SaleRecord record : salesData) {
            String month = record.getDate().substring(0, 7); // Extracting year-month from the date
            String item = record.getItem();
            double revenue = record.getTotalPrice();

            if (!itemsGeneratingMostRevenue.containsKey(month)) {
                itemsGeneratingMostRevenue.put(month, item);
            } else {
                String currentRevenueItem = itemsGeneratingMostRevenue.get(month);
                double currentRevenue = getRevenueForItemAndMonth(salesData, currentRevenueItem, month);
                if (revenue > currentRevenue) {
                    itemsGeneratingMostRevenue.put(month, item);
                }
            }
        }
        return itemsGeneratingMostRevenue;
    } */

    private static List<Integer> findOrdersForItemAndMonth(List<SaleRecord> salesData, String item, String month) {
        List<Integer> orders = new ArrayList<>();
        for (SaleRecord record : salesData) {
            String salesMonth = record.getDate().substring(0, 7); // Extracting year-month from the date
            String salesItem = record.getItem();
            int quantity = record.getQuantity();
            if (salesItem.equals(item) && salesMonth.equals(month)) {
                orders.add(quantity);
            }
        }
        return orders;
    }

    private static OrdersStats calculateOrdersStats(List<Integer> orders) {
        int minOrders = Collections.min(orders);
        int maxOrders = Collections.max(orders);
        double avgOrders = orders.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        return new OrdersStats(minOrders, maxOrders, avgOrders);
    }

    private static class SaleRecord {
        private final String date;
        private final String item;
        private final double unitPrice;
        private final int quantity;
        private final double totalPrice;

        public SaleRecord(String date, String item, double unitPrice, int quantity, double totalPrice) {
            this.date = date;
            this.item = item;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
        }

        public String getDate() {
            return date;
        }

        public String getItem() {
            return item;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getTotalPrice() {
            return totalPrice;
        }
    }

    private static int getQuantityForItemAndMonth(List<SaleRecord> salesData, String item, String month) {
        int quantity = 0;
        for (SaleRecord record : salesData) {
            String salesMonth = record.getDate().substring(0, 7); // Extracting year-month from the date
            String salesItem = record.getItem();
            if (salesItem.equals(item) && salesMonth.equals(month)) {
                quantity += record.getQuantity();
            }
        }
        
        return quantity;
    }

    private static double getRevenueForItemAndMonth(List<SaleRecord> salesData, String item, String month) {
        double revenue = 0.0;
        for (SaleRecord record : salesData) {
            String salesMonth = record.getDate().substring(0, 7); // Extracting year-month from the date
            String salesItem = record.getItem();
            if (salesItem.equals(item) && salesMonth.equals(month)) {
                revenue += record.getTotalPrice();
            }
        }
        return revenue;
    }

    private static class OrdersStats {
        private final int minOrders;
        private final int maxOrders;
        private final double avgOrders;

        public OrdersStats(int minOrders, int maxOrders, double avgOrders) {
            this.minOrders = minOrders;
            this.maxOrders = maxOrders;
            this.avgOrders = avgOrders;
        }

        public int getMinOrders() {
            return minOrders;
        }

        public int getMaxOrders() {
            return maxOrders;
        }

        public double getAvgOrders() {
            return avgOrders;
        }
    }
}
