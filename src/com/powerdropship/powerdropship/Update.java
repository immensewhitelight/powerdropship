package com.powerdropship.powerdropship;

import com.powerdropship.powerdropship.model.Job;
import com.powerdropship.powerdropship.model.Order;
import com.powerdropship.powerdropship.model.OrderedProduct;
import javafx.application.Platform;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;


public class Update {

    //Constructor that gives jobOverviewController a reference back to itself.
    public Update(JobOverviewController jobOverviewController) {
        this.jobOverviewController = jobOverviewController;
    }

    //Reference to the jobOverviewController.
    private JobOverviewController jobOverviewController;

    /**
     * Called by jobOverviewController to give a reference back to itself.
     * We are actually using the constructor for this instead though.
     *
     * @param jobOverviewController
     */
    public void setJobOverviewController(JobOverviewController jobOverviewController) {
        this.jobOverviewController = jobOverviewController;
    }


    //Holds the product skus retrieved from  the cart before the update, these are used to filter
    // the skus returned by the update file parser, and create List<String[]> supplierProdDataOfWooDb which is
    // a distilled list of skus to update.
    private List<String> listOfAllCartSkus = new ArrayList<String>();

    //Holds the prod data that currently exists in the WooDB which was filtered down to just this supplier's prod data,
    // by matching against updateFileListOfRows.
    private List<String[]> supplierProdDataOfWooDb = new ArrayList<String[]>();

    //Holds the result set of the product data of ALL new order (includes all suppliers), of type woocommerce
    // "wc-processing".
    private List<String[]> allProductsOnOrder = new ArrayList<String[]>();

    //Holds the products filtered from allProductsOnOrder through comparing with supplierProdDataOfWooDb,
    // this determines the product being ordered from this job's supplier.
    private List<String[]> productsOrderedFromSupplier = new ArrayList<String[]>();

    //Holds the data that will be added to the .csv file uploaded to sky43
    private List<String[]> prodDataArrayForSky43 = new ArrayList<String[]>();

    public List<String[]> getProdDataForSky43() {
        return prodDataArrayForSky43;
    }


    //Helper function.
    //Gets the skus from woocommerce db and puts them into List<String> listOfAllCartSkus to reduce the size of
    // updateFileListOfRows by filtering it down to just those prods that are also present in the WooCommerce db.
    // For instance, if there are only 10 prods from this supplier in the WooCommerce db, then you only want
    // updateFileListOfRows to have a size of 10.
    public void getCartSkus(Job job, Connection con) {

        System.out.println("[Enter helper] getCartSkus ");

        try {
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery("select meta_value from " + job.getDbName() + ".wp_postmeta WHERE meta_key =  '_sku'");

            //get the skus from the remote db, and add them to list
            while (resultSet.next()) {
                listOfAllCartSkus.add(resultSet.getString(1));
            }

        } catch (Exception e) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  getCartSkus error. " + e.getMessage() + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            e.printStackTrace();
        }
        //Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  DONE GETTING WOOCOMMERCE PROD METADATA " + System.getProperty("line.separator")));
        System.out.println("[Exit helper] getCartSkus ");
    }


    //The actual quantity and price update.
    // Uses the job, listOfAllCartSkus, and updateFileListOfRows (which was returned by the Parser), and con,
    // to first filter updateFileListOfRows to the rows that match the skus in the cart, this filters to just the
    // prod data we need for the update from the entire Products File data which is held in updateFileListOfRows.
    public void updateCartDb(Job job, List<String[]> updateFileListOfRows, Connection con, List<String[]> discountFileListOfRows, int numberOfQtyCols) {

        System.out.println("[Enter] updateCartDb ");
        Platform.runLater(() -> job.getTextArea().appendText("Enter update db" + System.getProperty("line.separator")));

        //Use the helper function to  get the skus that are already in the WooCommerce db.
        getCartSkus(job, con);

        Price price = new Price();

        //Filters out just this suppliers prods from all the prods in the Woocommerce db.
        for (int i = 0; i < updateFileListOfRows.size(); i++) {
            for (int j = 0; j < listOfAllCartSkus.size(); j++) {
                if (updateFileListOfRows.get(i)[0].equals(listOfAllCartSkus.get(j))) {
                    supplierProdDataOfWooDb.add(updateFileListOfRows.get(i));
                }
            }
        }

        PreparedStatement updateQuantity = null;
        PreparedStatement updatePrice = null;

        try {
            //Creates t1, t2, and t3 from the same table, then ties them together using post id, then updates the prod  if the prod id and upc match those in supplierProdDataOfWooDb.
            String updateString = "UPDATE " + job.getDbName() + ".wp_postmeta t1 JOIN " + job.getDbName() + ".wp_postmeta t2 ON t1.post_id = t2.post_id JOIN " + job.getDbName() + ".wp_postmeta t3 ON t2.post_id = t3.post_id SET t1.meta_value = ? WHERE t1.meta_key = '_stock' AND t2.meta_value = ? AND t3.meta_value = ?;";
            String updateStatement = "UPDATE " + job.getDbName() + ".wp_postmeta t1 JOIN " + job.getDbName() + ".wp_postmeta t2 ON t1.post_id = t2.post_id JOIN " + job.getDbName() + ".wp_postmeta t3 ON t2.post_id = t3.post_id SET t1.meta_value = ? WHERE t1.meta_key = '_price' AND t2.meta_value = ? AND t3.meta_value = ?;";

            con.setAutoCommit(false);
            updateQuantity = con.prepareStatement(updateString);
            updatePrice = con.prepareStatement(updateStatement);

            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ": Update started for " + supplierProdDataOfWooDb.size() + " prods." + System.getProperty("line.separator")));
            System.out.println(supplierProdDataOfWooDb.size());

            //Convert the ArrayList to an Array of Arrays so that the update is a lot faster.
            String[][] supplierProdDataOfWooDbArrayOfArrays = supplierProdDataOfWooDb.toArray(new String[][]{});

            //supplierProdDataOfWooDb contains the products from this supplier that are in the WooCommerce db.
            for (int i = 0; i < supplierProdDataOfWooDbArrayOfArrays.length; i++) {

                String[] strings = supplierProdDataOfWooDbArrayOfArrays[i];


                for (int j = 0; j < strings.length; j++) {
                    System.out.println(strings[j]);
                }


                //Add the qty cols using numberOfQtyCols (qty cols are all at the end of strings array)
                //Since the discount ID col is the last col use -1 to ignore it, so just the qty cols are read.
                int qtySum = 0;
                //-1 takes into account the discount col at the very end of the array
                if (!job.getDiscountFileIdColumnName().isEmpty())
                    for (int n = strings.length - 1 - numberOfQtyCols; n < strings.length - 1; n++) {

                        if (job.getJobName().equals("EuropaX")) {

                            if (strings[n].equals("Yes")) {
                                qtySum = qtySum + 10;
                            }

                        } else {
                            qtySum = qtySum + Integer.parseInt(strings[n]);
                        }
                    }
                //If it is empty it the discount ID col doesn't exist in the strings array.
                if (job.getDiscountFileIdColumnName().isEmpty())
                    for (int n = strings.length - numberOfQtyCols + 1; n < strings.length; n++) {
                        qtySum = qtySum + Integer.parseInt(strings[n]);
                    }

                System.out.println(qtySum);
                final int finalQtySum = qtySum;

                //Do the qty update, Prod id and Upc are used as conditionals in the update queries.
                //Quantity
                updateQuantity.setInt(1, qtySum);
                //Prod id
                updateQuantity.setString(2, strings[0]);
                //Upc
                updateQuantity.setString(3, strings[1]);
                updateQuantity.executeUpdate();

                //Do the price update, consider MAP price, if it is present in the row.
                // If a discount File doesn't exist, don't apply the discounts.
                if (job.getDiscountFileName().isEmpty()) {

                    double mapPrice = price.firstConsiderMAP(job, Double.parseDouble(strings[2]), Double.parseDouble(strings[3]));
                    double productPrice = price.priceChange(job, Double.parseDouble(strings[2]));

                    if (strings[3].length() != 0) {
                        updatePrice.setDouble(1, mapPrice);
                        Platform.runLater(() -> job.getTextArea().appendText("Prod ID: " + strings[0] + " New price : " + mapPrice + " New quantity: " + finalQtySum + System.getProperty("line.separator")));

                    } else {
                        updatePrice.setDouble(1, productPrice);
                        Platform.runLater(() -> job.getTextArea().appendText("Prod ID: " + strings[0] + " New price : " + productPrice + " New quantity: " + finalQtySum + System.getProperty("line.separator")));
                    }
                    //Prod id
                    updatePrice.setString(2, strings[0]);
                    //Upc
                    updatePrice.setString(3, strings[1]);
                }

                //If there is a discount file, the discount ID exists and is at the very end of the strings array.
                if (!discountFileListOfRows.isEmpty()) {
                    for (int k = 0; k < discountFileListOfRows.size(); k++) {
                        String[] discountFileStrings = discountFileListOfRows.get(k);
                        if (strings[strings.length - 1].equals(discountFileStrings[0])) {
                            double discount = Double.parseDouble(discountFileStrings[1]);
                            double discountMultiplier = (100 - discount) / 100;

                            //Consider MAP price, if it is present in the row
                            //Multiply by discountMultiplier first before doing the comparison to regular price.
                            if (job.getUpdateFileMAPPriceCol().length() != 0 && !strings[3].isEmpty()) {
                                double mapPrice = price.firstConsiderMAP(job, Double.parseDouble(strings[2]) * discountMultiplier, (Double.parseDouble(strings[3]) * discountMultiplier));
                                updatePrice.setDouble(1, mapPrice);
                                Platform.runLater(() -> job.getTextArea().appendText("Prod ID: " + strings[0] + " New price : " + mapPrice + " New quantity: " + finalQtySum + System.getProperty("line.separator")));
                            } else {
                                double productPrice = price.priceChange(job, (Double.parseDouble(strings[2]) * discountMultiplier));
                                updatePrice.setDouble(1, productPrice);
                                Platform.runLater(() -> job.getTextArea().appendText("Prod ID: " + strings[0] + " New price : " + productPrice + " New quantity: " + finalQtySum + System.getProperty("line.separator")));
                            }
                            //Prod id
                            updatePrice.setString(2, strings[0]);
                            //Upc
                            updatePrice.setString(3, strings[1]);
                        }
                    }
                }
                updatePrice.executeUpdate();
                con.commit();
            }
        } catch (Exception exception) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ": updateCartDb error: " + exception.getMessage() + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            exception.printStackTrace();
        }
        System.out.println("[Exit] updateCartDb ");
        Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ": Prod. update complete, updated " + supplierProdDataOfWooDb.size() + " of " + listOfAllCartSkus.size() + " prods in remote db " + System.getProperty("line.separator")));
        Platform.runLater(() -> job.getTextArea().appendText("Exit update db" + System.getProperty("line.separator")));
    }


    //Gets all prod data from Woocommerce since the startOrderId.
    // The first ResultSet in this method returns product data, including the _product_id and order id, for all orders
    // that have status of "wc-processing". Several more ResultSets are gotten to return the rest of the product data
    // into singleProductData, which is then added to allProductsOnOrder at the end of the iteration.
    public void retrieveProductsOnOrderData(Job job, Connection con) {

        // Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Retrieving products on order data. " + System.getProperty("line.separator")));
        System.out.println("[Enter] retrieveProductsOnOrderData ");
        Platform.runLater(() -> job.getTextArea().appendText("Enter retrieve order data" + System.getProperty("line.separator")));

        try {

            Statement stmt = con.createStatement();

            //Retrieve products on order, that are of the status 'wc-processing'
            //Products resultset is a lot of Woocommerce tables joined together using the order id in each table, where the post_status = 'wc-processing'
            ResultSet resultSet = stmt.executeQuery("SELECT DISTINCT c.meta_value, c.order_item_id, b.order_item_name, a.ID, a.post_date FROM " + job.getDbName() + ".wp_posts a join wp_woocommerce_order_items b join wp_woocommerce_order_itemmeta c join wp_postmeta d on a.ID = b.order_id AND b.order_item_id = c.order_item_id WHERE c.meta_key = '_product_id' and a.post_status = 'wc-processing';");

            while (resultSet.next()) {

                String[] singleProductData = new String[18];

                singleProductData[0] = resultSet.getString("ID");
                singleProductData[1] = resultSet.getString("order_item_name");
                singleProductData[2] = resultSet.getString("meta_value");
                singleProductData[3] = resultSet.getString("post_date");


                //We need these additional inner ResultSets to return values that are identified by another value
                // on the same row, instead of by an entire column. These are all added to singleProductData[].

                // _sku
                Statement stmt1 = con.createStatement();
                ResultSet resultSet1 = stmt1.executeQuery("SELECT meta_value FROM " + job.getDbName() + ".wp_postmeta where post_id = " + resultSet.getString("meta_value") + " and meta_key = '_sku';");
                while (resultSet1.next()) {
                    singleProductData[4] = resultSet1.getString("meta_value");
                }

                //_upc
                Statement stmt2 = con.createStatement();
                ResultSet resultSet2 = stmt2.executeQuery("SELECT meta_value FROM " + job.getDbName() + ".wp_postmeta where post_id = " + resultSet.getString("meta_value") + " and meta_key = '_cpf_upc';");
                while (resultSet2.next()) {
                    singleProductData[5] = resultSet2.getString("meta_value");
                }

                //_qty
                Statement stmt3 = con.createStatement();
                ResultSet resultSet3 = stmt3.executeQuery("SELECT meta_value FROM " + job.getDbName() + ".wp_woocommerce_order_itemmeta where order_item_id = " + resultSet.getString("order_item_id") + " and meta_key = '_qty';");
                while (resultSet3.next()) {
                    singleProductData[6] = resultSet3.getString("meta_value");
                }

                //_shipping_first_name
                Statement stmt4 = con.createStatement();
                ResultSet resultSet4 = stmt4.executeQuery("SELECT meta_value FROM " + job.getDbName() + ".wp_postmeta where post_id = " + resultSet.getString("ID") + " and meta_key = '_shipping_first_name';");
                while (resultSet4.next()) {
                    singleProductData[7] = resultSet4.getString("meta_value");
                }

                //_shipping_last_name
                Statement stmt5 = con.createStatement();
                ResultSet resultSet5 = stmt5.executeQuery("SELECT meta_value FROM " + job.getDbName() + ".wp_postmeta where post_id = " + resultSet.getString("ID") + " and meta_key = '_shipping_last_name';");
                while (resultSet5.next()) {
                    singleProductData[8] = resultSet5.getString("meta_value");
                }

                //_shipping_address_1
                Statement stmt6 = con.createStatement();
                ResultSet resultSet6 = stmt6.executeQuery("SELECT meta_value FROM " + job.getDbName() + ".wp_postmeta where post_id = " + resultSet.getString("ID") + " and meta_key = '_shipping_address_1';");
                while (resultSet6.next()) {
                    singleProductData[9] = resultSet6.getString("meta_value");
                }

                //_shipping_address_2
                Statement stmt7 = con.createStatement();
                ResultSet resultSet7 = stmt7.executeQuery("SELECT meta_value FROM " + job.getDbName() + ".wp_postmeta where post_id = " + resultSet.getString("ID") + " and meta_key = '_shipping_address_2';");
                while (resultSet7.next()) {
                    singleProductData[10] = resultSet7.getString("meta_value");
                }

                //_shipping_city
                Statement stmt8 = con.createStatement();
                ResultSet resultSet8 = stmt8.executeQuery("SELECT meta_value FROM " + job.getDbName() + ".wp_postmeta where post_id = " + resultSet.getString("ID") + " and meta_key = '_shipping_city';");
                while (resultSet8.next()) {
                    singleProductData[11] = resultSet8.getString("meta_value");
                }

                //_shipping_postcode
                Statement stmt9 = con.createStatement();
                ResultSet resultSet9 = stmt9.executeQuery("SELECT meta_value FROM " + job.getDbName() + ".wp_postmeta where post_id = " + resultSet.getString("ID") + " and meta_key = '_shipping_postcode';");
                while (resultSet9.next()) {
                    singleProductData[12] = resultSet9.getString("meta_value");
                }

                //_shipping_country
                Statement stmt10 = con.createStatement();
                ResultSet resultSet10 = stmt10.executeQuery("SELECT meta_value FROM " + job.getDbName() + ".wp_postmeta where post_id = " + resultSet.getString("ID") + " and meta_key = '_shipping_country';");
                while (resultSet10.next()) {
                    singleProductData[13] = resultSet10.getString("meta_value");
                }

                //_shipping_state
                Statement stmt11 = con.createStatement();
                ResultSet resultSet11 = stmt11.executeQuery("SELECT meta_value FROM " + job.getDbName() + ".wp_postmeta where post_id = " + resultSet.getString("ID") + " and meta_key = '_shipping_state';");
                while (resultSet11.next()) {
                    singleProductData[14] = resultSet11.getString("meta_value");
                }

                //_billing_email
                Statement stmt13 = con.createStatement();
                ResultSet resultSet13 = stmt13.executeQuery("SELECT meta_value FROM " + job.getDbName() + ".wp_postmeta where post_id = " + resultSet.getString("ID") + " and meta_key = '_billing_email';");
                while (resultSet13.next()) {
                    singleProductData[15] = resultSet13.getString("meta_value");
                }

                //_billing_phone
                Statement stmt14 = con.createStatement();
                ResultSet resultSet14 = stmt14.executeQuery("SELECT meta_value FROM " + job.getDbName() + ".wp_postmeta where post_id = " + resultSet.getString("ID") + " and meta_key = '_billing_phone';");
                while (resultSet14.next()) {
                    singleProductData[16] = resultSet14.getString("meta_value");
                }

                //method_id (shipping method)
                Statement stmt15 = con.createStatement();
                ResultSet resultSet15 = stmt15.executeQuery("SELECT order_item_name FROM " + job.getDbName() + ".wp_woocommerce_order_items where order_id = " + resultSet.getString("ID") + " and order_item_type = 'shipping';");
                while (resultSet15.next()) {
                    singleProductData[17] = resultSet15.getString("order_item_name");
                }

                //todo:troubleshoot this
                for (int i = 0; i < singleProductData.length; i++) {
                    System.out.println(singleProductData[i]);
                }

                allProductsOnOrder.add(singleProductData);

            }

        } catch (Exception e) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  retrieveProductsOnOrderData error: " + e.getMessage() + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            e.printStackTrace();
        }


        // Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  DONE RETRIEVING PRODUCT ON ORDER DATA. " + System.getProperty("line.separator")));
        System.out.println("[Exit] retrieveProductsOnOrderData ");
        Platform.runLater(() -> job.getTextArea().appendText("Exit retrieve order data" + System.getProperty("line.separator")));

    }


    //Helper function, removes duplicates and orders orderIds.
    public TreeSet<String> returnWithoutDuplicates(List<String> orderIds) {

        TreeSet<String> withoutDuplicates = new TreeSet<String>();

        for (String orderId : orderIds) {
            //if (withoutDuplicates.add(orderId)) returns true if the item is not already in the Set
            if (withoutDuplicates.add(orderId)) {
                withoutDuplicates.add(orderId);
            }
        }
        return withoutDuplicates;
    }


    //Filters out only the products that were ordered from this supplier.
    public void createOrders(Job job, Connection con) {

        System.out.println("[Enter] createOrders ");
        Platform.runLater(() -> job.getTextArea().appendText("Enter create orders" + System.getProperty("line.separator")));


        for (int i = 0; i < supplierProdDataOfWooDb.size(); i++) {
            for (int j = 0; j < allProductsOnOrder.size(); j++) {

                String productId = supplierProdDataOfWooDb.get(i)[0];
                String productUpc = supplierProdDataOfWooDb.get(i)[1];
                String orderProductId = allProductsOnOrder.get(j)[4];
                String orderProductUpc = allProductsOnOrder.get(j)[5];

                if (productId.equals(orderProductId) & productUpc.equals(orderProductUpc)) {
                    productsOrderedFromSupplier.add(allProductsOnOrder.get(j));
                }
            }
        }

        //Clear out the old orderList from the last iteration.
        job.getListOfOrders().clear();

        // Create a List of order ids, using just the products ordered from this supplier.
        // Every new ordered product has an order id that is identical to other prods in the same order.
        // Therefore, orderIds will likely have duplicate order ids in it.
        ArrayList<String> orderIds = new ArrayList<String>();

        for (int i = 0; i < productsOrderedFromSupplier.size(); i++) {
            orderIds.add(productsOrderedFromSupplier.get(i)[0]);
        }

        //Use the helper function returnWithoutDuplicates to return a sorted TreeSet of orderIds without duplicates.
        TreeSet<String> treeSet = returnWithoutDuplicates(orderIds);

        //Create a List of Orders using the sorted duplicate-free TreeSet.
        for (String orderId : treeSet) {
            Order order = new Order(orderId);
            job.listOfOrders.add(order);
        }

        //Sorts all the products ordered from this supplier into separate Orders.
        //
        // If the orderId in productsOrderedFromSupplier matches the orderId in listOfOrders, add the demmographic data
        // to the Order and add OrderedProduct to the ArrayList<OrderedProduct> productsOfTheOrder property of Order.
        //
        // Note: This will repeat add demographic data from each ordered product to each Order in listOfOrders since it
        // is always overwritten by the last product added to the order.
        for (int i = 0; i < productsOrderedFromSupplier.size(); i++) {
            for (int j = 0; j < job.listOfOrders.size(); j++) {
                if (productsOrderedFromSupplier.get(i)[0].equals(job.listOfOrders.get(j).getOrderId())) {
                    job.listOfOrders.get(j).setFirstName(productsOrderedFromSupplier.get(i)[7]);
                    job.listOfOrders.get(j).setLastName(productsOrderedFromSupplier.get(i)[8]);
                    job.listOfOrders.get(j).setAddressLine1(productsOrderedFromSupplier.get(i)[9]);
                    job.listOfOrders.get(j).setAddressLine2(productsOrderedFromSupplier.get(i)[10]);
                    job.listOfOrders.get(j).setCity(productsOrderedFromSupplier.get(i)[11]);
                    job.listOfOrders.get(j).setState(productsOrderedFromSupplier.get(i)[14]);
                    job.listOfOrders.get(j).setPostalCode(productsOrderedFromSupplier.get(i)[12]);
                    job.listOfOrders.get(j).setCountry(productsOrderedFromSupplier.get(i)[13]);
                    job.listOfOrders.get(j).setBillingEmail(productsOrderedFromSupplier.get(i)[15]);
                    job.listOfOrders.get(j).setShippingMethod(productsOrderedFromSupplier.get(i)[17]);

                    OrderedProduct orderedProduct = new OrderedProduct();
                    orderedProduct.setVendorSku(productsOrderedFromSupplier.get(i)[4]);
                    orderedProduct.setProductUpc(productsOrderedFromSupplier.get(i)[5]);
                    orderedProduct.setProductName(productsOrderedFromSupplier.get(i)[1]);
                    orderedProduct.setProductOrderQuantity(productsOrderedFromSupplier.get(i)[6]);
                    job.listOfOrders.get(j).getProductsOfTheOrder().add(orderedProduct);


                    //A product is being added to the productsOfTheOrder and demographic data in the Order is being over
                    // written. At this time write the supplier name and the product skus to the Woo db.
                    // Retrieves the _supplier_name string from the Woo db and adds ito the supplierNameString, then turns it
                    // into an Array and removes duplicates then turns it back into a String before inserting it back into the Woo db.
                    String productSku = orderedProduct.getVendorSku();

                    String supplierNameString = productSku + "," + job.getJobSupplierName();

                    String orderId = job.listOfOrders.get(j).getOrderId();

                    try {
                        //Do another resultset to get any supplier names already present in the Woo db and then
                        // append them to the supplier name insert.
                        Statement statement = con.createStatement();
                        ResultSet supplierNameResultSet = statement.executeQuery("SELECT meta_value FROM " + job.getDbName() + ".wp_postmeta where post_id = " + orderId + " and meta_key = '_supplier_name';");

                        //Append any existing _supplier_name in the Woo db for this Order to the supplierNameString.
                        while (supplierNameResultSet.next()) {
                            supplierNameString = supplierNameString + "," + supplierNameResultSet.getString("meta_value") + ",";
                        }

                        //Create an ArrayList out of the supplierNameString
                        List<String> supplierNameList = new ArrayList<String>(Arrays.asList(supplierNameString.split(",")));

                        //Remove all duplicates, TreeSet preserves order.
                        TreeSet<String> supplierNamesWithoutDuplicates = returnWithoutDuplicates(supplierNameList);

                        //Turn the TreeSet back into a String.
                        String supplierString = "";

                        for (String s : supplierNamesWithoutDuplicates) {
                            supplierString += s + ",";
                        }

                        //Delete the existing _supplier_name String in the Woo db
                        String updateString =
                                "DELETE FROM " + job.getDbName() + ".wp_postmeta WHERE meta_key = '_supplier_name' AND  post_id = ?;";


                        PreparedStatement deleteSupplierNameFieldInPostMeta = con.prepareStatement(updateString);
                        deleteSupplierNameFieldInPostMeta.setString(1, (orderId));
                        deleteSupplierNameFieldInPostMeta.executeUpdate();

                        //Insert the new supplierString into _supplier_name in the Woo db.
                        String updateString2 =
                                "INSERT " + job.getDbName() + ".wp_postmeta (post_id, meta_key, meta_value) VALUES (?, '_supplier_name', ?);";

                        PreparedStatement insertTrackingNumberFieldIntoPostMeta = con.prepareStatement(updateString2);
                        insertTrackingNumberFieldIntoPostMeta.setString(1, (orderId));
                        insertTrackingNumberFieldIntoPostMeta.setString(2, (supplierString));
                        insertTrackingNumberFieldIntoPostMeta.executeUpdate();

                        con.commit();

                    } catch (Exception e) {
                        Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ": createOrders error: " + e.getMessage() + System.getProperty("line.separator")));
                        Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
                        Platform.runLater(() -> job.getServiceInstanceReference().restart());
                        e.printStackTrace();
                    }
                }
            }
        }

        //System.out.ln print the demographics in each order.
        for (int j = 0; j < job.listOfOrders.size(); j++) {
            System.out.println("  ***> Customer Information <***");
            System.out.println("    TimeStamp: " + Timestamp.valueOf(LocalDateTime.now()));
            System.out.println("    Name: " + job.listOfOrders.get(j).getFirstName());
            System.out.println("    Last Name: " + job.listOfOrders.get(j).getLastName());
            System.out.println("    Address Line 1: " + job.listOfOrders.get(j).getAddressLine1());
            System.out.println("    Address Line 2: " + job.listOfOrders.get(j).getAddressLine2());
            System.out.println("    City: " + job.listOfOrders.get(j).getCity());
            System.out.println("    State: " + job.listOfOrders.get(j).getState());
            System.out.println("    Postal Code: " + job.listOfOrders.get(j).getPostalCode());
            System.out.println("    Country: " + job.listOfOrders.get(j).getCountry());
            System.out.println("    Shipping Method: " + job.listOfOrders.get(j).getShippingMethod());


            for (int k = 0; k < job.listOfOrders.get(j).getProductsOfTheOrder().size(); k++) {
                System.out.println("  ** Ordered Product **");
                System.out.println("    Name: " + job.listOfOrders.get(j).getProductsOfTheOrder().get(k).getProductName());
                System.out.println("    Quantity: " + job.listOfOrders.get(j).getProductsOfTheOrder().get(k).getProductOrderQuantity());
                System.out.println("    Sku: " + job.listOfOrders.get(j).getProductsOfTheOrder().get(k).getVendorSku());
                System.out.println("    Up: " + job.listOfOrders.get(j).getProductsOfTheOrder().get(k).getProductUpc());
            }
        }

        //Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Number of new orders created on this iteration: " + job.listOfOrders.size() + System.getProperty("line.separator")));
        Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  New orders: " + returnWithoutDuplicates(orderIds) + System.getProperty("line.separator")));
        //Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  DONE CONSTRUCTING ANY NEW ORDERS " + System.getProperty("line.separator")));

        System.out.println("[Exit] createOrders ");
        Platform.runLater(() -> job.getTextArea().appendText("Exit create orders" + System.getProperty("line.separator")));


    }


    //If job.updateFileSetAllProdsToPending is set to true then all the suppliers prods are set to wc-pending in the Woo db.
    public void updateAllProdsToPending(Job job, Connection con) {

        System.out.println("[Enter] updateAllProdsToPending ");
        Platform.runLater(() -> job.getTextArea().appendText("Enter update all products to pending" + System.getProperty("line.separator")));


        if (job.getUpdateFileUpdateAllProdsToPending().equals("true")) {

            try {

                //Get the post id of each product in the removedProds.txt using sku and upc, which are present in the
                //removedProds.txt file.
                String selectString = "SELECT t1.post_id FROM " + job.getDbName() + ".wp_postmeta t1 INNER JOIN wp_postmeta t2 ON t1.post_id = t2.post_id WHERE t1.meta_key = '_sku' AND t1.meta_value = ? AND t2.meta_key = '_cpf_upc' AND t2.meta_value = ?;";

                con.setAutoCommit(false);
                PreparedStatement selectAllProdsPostId = con.prepareStatement(selectString);
                Statement updateAllProdsToPending = con.createStatement();

                for (int i = 0; i < supplierProdDataOfWooDb.size(); i++) {

                    selectAllProdsPostId.setString(1, supplierProdDataOfWooDb.get(i)[0]);
                    selectAllProdsPostId.setString(2, supplierProdDataOfWooDb.get(i)[1]);

                    //ResultSet contains the post ids.
                    ResultSet rs = selectAllProdsPostId.executeQuery();

                    //System.out.println(supplierProdDataOfWooDb.get(i)[0] + "  " + supplierProdDataOfWooDb.get(i)[1]);

                    //Using the post id update the woo db product to pending, so that is it not available for sale.
                    while (rs.next()) {
                        String updateString = "UPDATE " + job.getDbName() + ".wp_posts SET post_status = 'pending' WHERE ID = " + rs.getString("post_id");
                        updateAllProdsToPending.executeUpdate(updateString);
                    }
                    con.commit();
                }
            } catch (Exception e) {
                Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  updateAllProdsToPending error. " + e.getMessage() + System.getProperty("line.separator")));
                Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
                Platform.runLater(() -> job.getServiceInstanceReference().restart());
                e.printStackTrace();
            }
        }
        System.out.println("[Exit] updateAllProdsToPending ");
        Platform.runLater(() -> job.getTextArea().appendText("Exit update all products to pending" + System.getProperty("line.separator")));
    }


    //Products removed prods to pending.
    // If the user has set UpdateFileSetRemovedProdsToPending() to true, update the prods in removed.txt to 'pending'
    // status in Woo db, so that they are no longer published products.
    public void updateRemovedProdsToPending(Job job, List<String[]> listOfRemovedProds, Connection con) {

        System.out.println("[Enter] updateRemovedProdToPending ");
        Platform.runLater(() -> job.getTextArea().appendText("Enter update removed products to pending" + System.getProperty("line.separator")));

        File supplierDir = job.getSupplierDir();

        File removedProds = new File(supplierDir + "/" + "removedProds.txt");

        if (removedProds.exists() & removedProds.length() != 0 & job.getUpdateFileSetRemovedProdsToPending().equals("true")) {

            try {
                //Get the post id of each product in the removedProds.txt using sku and upc, which are present in the
                //removedProds.txt file.
                String selectString = "SELECT t1.post_id FROM " + job.getDbName() + ".wp_postmeta t1 INNER JOIN wp_postmeta t2 ON t1.post_id = t2.post_id WHERE t1.meta_key = '_sku' AND t1.meta_value = ? AND t2.meta_key = '_cpf_upc' AND t2.meta_value = ?;";

                con.setAutoCommit(false);

                PreparedStatement selectRemovedProdPostId = con.prepareStatement(selectString);
                Statement updateRemovedProdsToPending = con.createStatement();

                for (int i = 0; i < listOfRemovedProds.size(); i++) {

                    selectRemovedProdPostId.setString(1, listOfRemovedProds.get(i)[0]);
                    selectRemovedProdPostId.setString(2, listOfRemovedProds.get(i)[1]);

                    //ResultSet contains the post ids.
                    ResultSet rs = selectRemovedProdPostId.executeQuery();

                    //System.out.println(listOfRemovedProds.get(i)[0] + "  " + listOfRemovedProds.get(i)[1]);

                    //Using the post id update the woo db product to pending.
                    while (rs.next()) {
                        String updateString = "UPDATE " + job.getDbName() + ".wp_posts SET post_status = 'pending' WHERE ID = " + rs.getString("post_id");
                        updateRemovedProdsToPending.executeUpdate(updateString);
                    }
                    con.commit();
                }

            } catch (Exception e) {
                Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  updateRemovedProdsToPending error. " + e.getMessage() + System.getProperty("line.separator")));
                Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
                Platform.runLater(() -> job.getServiceInstanceReference().restart());
                e.printStackTrace();
            }
        }
        System.out.println("[Exit] updateRemovedProdToPending ");
        Platform.runLater(() -> job.getTextArea().appendText("Exit update removed products to pending" + System.getProperty("line.separator")));

    }


    //Uses this suppliers data sku and upc to get a resultset of the relevant data being sent to sky43 for each product,
    // then adds the data to an array that will be used to write a .csv file of this data.
    public void queryProdDataForSky43(Job job, Connection con) {

        System.out.println("[Enter] queryProdDataForSky43 ");
        Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Retrieving prod links for sky43. " + System.getProperty("line.separator")));

        PreparedStatement selectSky43ProductData = null;

        //All these tables, and virtual tables, are tied together with the post_id in the WHERE, a virtual table is used
        // when a value that needs to be retrieved is located by another value on the same row rather than being located by a column.
        try {
            String selectString = "SELECT t3.meta_id as unique_key, t3.post_id, t3.meta_value as price, t4.meta_value as stock, t5.post_title, t5.post_status, t6.option_value as siteurl, t7.option_value as location " +
                    "FROM " + job.getDbName() + ".wp_postmeta t1, " + job.getDbName() + ".wp_postmeta t2, " + job.getDbName() + ".wp_postmeta t3, " + job.getDbName() + ".wp_postmeta t4, "
                    + job.getDbName() + ".wp_posts t5, " + job.getDbName() + ".wp_options t6, " + job.getDbName() + ".wp_options t7 " +
                    "WHERE t1.post_id = t2.post_id AND t2.post_id = t3.post_id AND t3.post_id = t4.post_id AND t4.post_id = t5.ID AND t1.meta_value = ? AND t2.meta_value = ? AND t3.meta_key = '_price' " +
                    "AND t4.meta_key = '_stock' AND t5.post_status = 'publish' AND t6.option_name = 'siteurl' AND t7.option_name = 'woocommerce_default_country';";

            con.setAutoCommit(false);
            selectSky43ProductData = con.prepareStatement(selectString);

            for (int i = 0; i < supplierProdDataOfWooDb.size(); i++) {
                selectSky43ProductData.setString(1, supplierProdDataOfWooDb.get(i)[0]);
                selectSky43ProductData.setString(2, supplierProdDataOfWooDb.get(i)[1]);
                ResultSet resultSet = selectSky43ProductData.executeQuery();

                String[] singleProductData = new String[6];

                while (resultSet.next()) {

                    //Uses the URL class to derive the domain/host name
                    URL url = new URL(resultSet.getString("siteurl"));
                    String host = url.getHost();

                    singleProductData[0] = resultSet.getString("post_title");
                    singleProductData[1] = "<a href=" + resultSet.getString("siteurl") + "/checkout/?add-to-cart=" + resultSet.getString("post_id") + ">" + resultSet.getString("price") + "</a>";
                    singleProductData[2] = resultSet.getString("stock");
                    singleProductData[3] = resultSet.getString("location");
                    singleProductData[4] = host;
                    singleProductData[5] = job.getJobSupplierName();

                    prodDataArrayForSky43.add(singleProductData);
                }
            }
        } catch (Exception e) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  queryProdDataForSky43 error: " + e.getMessage() + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            e.printStackTrace();
        }
        System.out.println("[Exit] queryProdDataForSky43 ");
    }
}
