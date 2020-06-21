package com.powerdropship.powerdropship;


import com.powerdropship.powerdropship.model.Job;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import javafx.application.Platform;
import org.apache.commons.io.FileUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Parse {

    //Reference to the jobOverviewController.
    private JobOverviewController jobOverviewController;

    //Constructor, gives a reference back to jobOverviewController
    public Parse(JobOverviewController jobOverviewController) {
        this.jobOverviewController = jobOverviewController;
    }

    /**
     * Is called by jobOverviewController to give a reference back to itself.
     *
     * @param jobOverviewController
     */
    public void setJobOverviewController(JobOverviewController jobOverviewController) {
        this.jobOverviewController = jobOverviewController;
    }

    //Holds the column selection that is determined if the MAP price is used or not, which determines how the parser
    // parses the update file.
    private String columnSelection;

    public String getColumnSelection() {
        return columnSelection;
    }

    //Holds the rows parsed from the update file.
    private List<String[]> updateFileListOfRows;

    public List<String[]> getUpdateFileListOfRows() {
        return updateFileListOfRows;
    }

    //Holds the products removed fron the update file since the last iteration, so that they can be changed to "pending"
    // in the WooCommerce db.
    private List<String[]> listOfRemovedProds;

    public List<String[]> getListOfRemovedProds() {
        return listOfRemovedProds;
    }

    public List<String[]> discountFileListOfRows;

    private int numberOfQtyCols;

    public int getNumberOfQtyCols() {
        return numberOfQtyCols;
    }


    /**
     * public List<String[]> getDiscountFileListOfRows() {
     * return discountFileListOfRows;
     * }
     */

    //Helper function, gets the Univocity Parsers reader.
    public Reader getFileReader(String absolutePath, Job job) {
        try {
            return new InputStreamReader(new FileInputStream(new File(absolutePath)), "UTF-8");
        } catch (IOException e) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  getFileReader error. " + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            e.printStackTrace();
            throw new IllegalStateException(" Unable to read input", e);
        }
    }

    //Helper function: Assembles the String array that is passed to the parser settings, discount ID is at the very end of the array and
    // the qty cols are the next ones in from the end index.
    public String[] buildColList(Job job) {

        String qtyString = job.getUpdateFileQtyCol();

        List<String> messages = null;
        //MAP price col. name is added to the col. selection, if MAP price col. name is empty then Univocity Parsers doesn't use it during column selection.
        messages = Arrays.asList(job.getUpdateFileProdIDCol(), job.getUpdateFileProdUpcCol(), job.getUpdateFilePriceCol(), job.getUpdateFileMAPPriceCol());
        //Parse the qtyCols by "," and create an array of them
        List<String> qtyCols = Arrays.asList(qtyString.split("\\s*,\\s*"));

        //Determine the number of Quantity cols, so jobOverviewController can pass it to update, to offset the index
        numberOfQtyCols = qtyCols.size();
        //Concatenate List<String>
        List<String> newList = Stream.concat(messages.stream(), qtyCols.stream()).collect(Collectors.toList());

        List<String> discountIdCol = Arrays.asList(job.getUpdateFileDiscountIDCol());
        //Concatenate it to the end
        List<String> fullList = Stream.concat(newList.stream(), discountIdCol.stream()).collect(Collectors.toList());

        String[] stringArray = fullList.toArray(new String[0]);

        for (int i = 0; i < fullList.size(); i++) {
            System.out.println(fullList.get(i));
        }
        return stringArray;
    }


    //Parses the Products File then determines the columnSelection from the user input of weather or not MAP price is being
    // used, the columnSelection is then used in the quantity and price update of Woocommerce.
    public void parseUpdateFile(Job job) {

        System.out.println("[Enter] parseUpdateFile");
        Platform.runLater(() -> job.getTextArea().appendText("Enter parse update file" + System.getProperty("line.separator")));

        //Names the update file, it may originally have been a .xls file and converted to .csv
        // by ConvertFormat, this handles that.
        File updateFile = job.getUpdateFile();

        //Configure the Univocity Parsers settings
        RowListProcessor rowProcessor = new RowListProcessor();
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setRowProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.setSkipEmptyLines(true);
        // sets what is the default value to use when the parsed value is null
        parserSettings.setNullValue("0");
        // sets what is the default value to use when the parsed value is empty
        parserSettings.setEmptyValue("0"); // for CSV only
        parserSettings.setNumberOfRowsToSkip(job.getUpdateFileBeginningRowsToSkip());
        parserSettings.getFormat().setComment((char) job.getUpdateFileCommentIndicator());
        parserSettings.getFormat().setDelimiter((char) job.getUpdateFileSeparator());

        //Parser uses the buildColList(job) and the result is sent to update
        parserSettings.selectFields(buildColList(job));

        try {
            String updateFileString = updateFile.toString();
            CsvParser parser = new CsvParser(parserSettings);
            parser.parse(getFileReader(updateFileString, job));
            updateFileListOfRows = rowProcessor.getRows();


        } catch (Exception e) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  parseUpdateFile error. " + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            e.printStackTrace();
        }
        //Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Parsed update file " + System.getProperty("line.separator")));
        System.out.println("[Exit] parseUpdateFile");
        Platform.runLater(() -> job.getTextArea().appendText("Exit parse update file" + System.getProperty("line.separator")));

    }


    //Writes prodsInUsedUpdate.txt and prodsInNewUpdate.txt from updateFileListOfRows, which is the parse,
    // then uses FileUtils removeAll() both ways to isolate NEW and REMOVED prods.
    public void findUpdateFileNewAndRemovedProds(Job job) {

        System.out.println("[Enter] findUpdateFileNewAndRemovedProds");
        Platform.runLater(() -> job.getTextArea().appendText("Enter find new and removed products" + System.getProperty("line.separator")));


        File supplierDir = job.getSupplierDir();

        //Write to an in-memory byte array. This will be printed out to the standard output so you can easily see the result.
        ByteArrayOutputStream csvResult = new ByteArrayOutputStream();

        //CsvWriter (and all other file writers) work with an instance of java.io.Writer
        Writer outputWriter = new OutputStreamWriter(csvResult);

        CsvWriterSettings settings = new CsvWriterSettings();

        //Create a writer with the above settings;
        CsvWriter writer = new CsvWriter(outputWriter, settings);

        //The new updateFileListOfRows is compared to present updateFileListOfRows.
        //Write the parsed rows from the present prod update to ByteArrayOutputStream.
        for (int i = 0; i < updateFileListOfRows.size(); i++) {
            writer.writeRow(updateFileListOfRows.get(i)[0], updateFileListOfRows.get(i)[1]);

            // System.out.println(updateFileListOfRows.get(i)[0] + "   "  + updateFileListOfRows.get(i)[1]);
        }

        //Close the writer, since the parse is now written onto the csvResult.
        writer.close();

        //Create File objects.
        File prodsInUsedUpdate = new File(supplierDir + "/" + "prodsInUsedUpdate.txt");
        File prodsInNewUpdate = new File(supplierDir + "/" + "prodsInNewUpdate.txt");
        File removedProds = new File(supplierDir + "/" + "removedProds.txt");
        File newProds = new File(supplierDir + "/" + "newProds.txt");


        //If prodsInUsedUpdate.txt doesn't exist, make one by printing the ByteArrayOutputStream to a file.
        if (!prodsInUsedUpdate.exists()) {
            try (Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(prodsInUsedUpdate)))) {
                fileWriter.write(csvResult.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //If prodsInUsedUpdate.txt does exist, make a prodsInNewUpdate file by printing the ByteArrayOutputStream to a file.
        } else if (prodsInUsedUpdate.exists()) {
            try (Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(prodsInNewUpdate)))) {
                fileWriter.write(csvResult.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //If the prodsInNewUpdate file is of 0 length delete it so it doesn't ruin the content of the removedProds.txt.
            // This is in the case where a file of 0 length is downloaded and parsed.
            if (prodsInNewUpdate.length() == 0) {
                prodsInNewUpdate.delete();
            }

            //The following code uses HashSet.delete() to find REMOVED and NEW products.
            try {

                //Creates prodsInUsedUpdateHashSet and prodsInUsedUpdateHashCopy, because removeAll() deletes
                // some of the contents of prodsInUsedUpdateHashSet, after that it won't be able to be re-used to find
                // the new products.
                Set<String> prodsInUsedUpdateHashSet = new HashSet<String>(FileUtils.readLines(prodsInUsedUpdate));
                Set<String> prodsInUsedUpdateHashSetCopy = new HashSet<String>(FileUtils.readLines(prodsInUsedUpdate));
                Set<String> prodsInNewUpdateHashSet = new HashSet<String>(FileUtils.readLines(prodsInNewUpdate));

                //Delete the prodsInUsedUpdate File because a HashSet was already created from it, and it is no longer
                // needed.
                prodsInUsedUpdate.delete();

                //Delete all prods in prodsInUsedUpdateHashSet which are in prodsInNewUpdateHashSet,
                // this leaves only the REMOVED prods remaining in prodsInUsedUpdateHashSet.
                prodsInUsedUpdateHashSet.removeAll(prodsInNewUpdateHashSet);

                System.out.println("Number of removed prods: " + prodsInUsedUpdateHashSet.size());

                //Then iterate prodsInUsedUpdateHashSet, appending the prods to removedProds.txt.
                PrintStream removedProdsFileStream = new PrintStream(new FileOutputStream(removedProds, true));
                Iterator prodsInUsedUpdateHashSetIterator = prodsInUsedUpdateHashSet.iterator();
                while (prodsInUsedUpdateHashSetIterator.hasNext()) {
                    removedProdsFileStream.println(prodsInUsedUpdateHashSetIterator.next());
                }

                //Delete all prods in prodsInNewUpdateHashSet which are not in prodsInUsedUpdateHashSetCopy,
                // this leaves only NEW prods remaining in prodsInNewUpdateHashSet.
                prodsInNewUpdateHashSet.removeAll(prodsInUsedUpdateHashSetCopy);

                System.out.println("Number of new prods: " + prodsInNewUpdateHashSet.size());

                //Then iterate prodsInNewUpdateHashSet, appending these NEW prods to newProds.txt.
                PrintStream prodsInNewUpdateStream = new PrintStream(new FileOutputStream(newProds, true));
                Iterator prodsInNewUpdateHashSetIterator = prodsInNewUpdateHashSet.iterator();
                while (prodsInNewUpdateHashSetIterator.hasNext()) {
                    prodsInNewUpdateStream.println(prodsInNewUpdateHashSetIterator.next());
                }

                //The following code removes the duplicates from the removedProds.txt and newProds.txt files by using
                // side effects of LinkedHashSets which is that they ignore duplicates and preserve order. Remove the
                // duplicates that build up in removedProds.txt by creating a LinkedHashSet and reading the products in
                // removedProds.txt into it, and then overwrite back into removedProds.txt.

                //Create the LinkedHashSet, LinkedHashSets ignore duplicates and preserve order.
                TreeSet removedProdsTreeSet = new TreeSet();

                //Read from "removedProds.txt" into the removedProdsHashSet
                BufferedReader removedProdBufferedReader = new BufferedReader(new FileReader(removedProds));
                String line1;
                while ((line1 = removedProdBufferedReader.readLine()) != null) {
                    removedProdsTreeSet.add(line1);
                }
                removedProdBufferedReader.close();

                //Overwrite the contents of "removedProds.txt" file with the contents of removedProdsHashSet.
                PrintStream removedProdsWithoutDuplicates = new PrintStream(new FileOutputStream(removedProds));
                Iterator removedProdsTreeSetIterator = removedProdsTreeSet.iterator();
                //Overwrites removedProds.txt with the contents of removedProdsHashSet
                while (removedProdsTreeSetIterator.hasNext()) {
                    removedProdsWithoutDuplicates.println(removedProdsTreeSetIterator.next());
                }

                //Create the LinkedHashSet, LinkedHashSets ignore duplicates and preserve order.
                TreeSet newProdsTreeSet = new TreeSet();

                //Read from "newProds.txt" into the newProdsHashSet.
                BufferedReader newProdsBufferedReader = new BufferedReader(new FileReader(newProds));
                String line2;
                while ((line2 = newProdsBufferedReader.readLine()) != null) {
                    newProdsTreeSet.add(line2);
                }
                newProdsBufferedReader.close();

                //Overwrite the contents of "newProds.txt" with the contents of newProdsHashSet.
                PrintStream newProdsWithoutDuplicates = new PrintStream(new FileOutputStream(newProds));
                Iterator newProdsTreeSetIterator = newProdsTreeSet.iterator();
                //Overwrites newProds.txt with the contents of newProdsHashSet.
                while (newProdsTreeSetIterator.hasNext()) {
                    newProdsWithoutDuplicates.println(newProdsTreeSetIterator.next());
                }

                //Rename prodsInNewUpdate to prodsInUsedUpdate since it is now the present update file.
                prodsInNewUpdate.renameTo(prodsInUsedUpdate);

            } catch (Exception e) {
                Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  findUpdateFileNewAndRemovedProds error. " + System.getProperty("line.separator")));
                Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
                Platform.runLater(() -> job.getServiceInstanceReference().restart());
                e.printStackTrace();
            }
            //Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  DONE FINDING NEW AND REMOVED PRODS IN UPDATE FILE, IF ANY " + System.getProperty("line.separator")));
        }
        System.out.println("[Exit] findUpdateFileNewAndRemovedProds");
        Platform.runLater(() -> job.getTextArea().appendText("Exit find new and removed products" + System.getProperty("line.separator")));

    }


    //Parses the tracking attachments and email them to the customer using the emailing helper function.
    // Gets the billing email address from the Woo db, emails the tracking number to the customer, inserts the tracking
    // number into the woo db, changes the order status to wc-completed.
    public void parseTrackingAttachmentAndEmailToCust(Job job, Connection con) {

        System.out.println("[Enter] parseTrackingAttachmentAndEmailToCust ");
        Platform.runLater(() -> job.getTextArea().appendText("Enter parse tracking and email to customer" + System.getProperty("line.separator")));


        //Configure the Univocity Parsers settings
        RowListProcessor rowProcessor = new RowListProcessor();
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setRowProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.setSkipEmptyLines(true);
        // sets what is the default value to use when the parsed value is null
        parserSettings.setNullValue("0");
        // sets what is the default value to use when the parsed value is empty
        parserSettings.setEmptyValue("0"); // for CSV only
        parserSettings.setNumberOfRowsToSkip(job.getTrackingBeginningRowsToSkip());
        parserSettings.getFormat().setComment((char) job.getTrackingCommentIndicator());
        parserSettings.getFormat().setDelimiter((char) job.getTrackingCsvSeparator());
        parserSettings.selectFields(job.getTrackingOrderIdColName(), job.getTrackingNumberColName());

        try {
            //Set up the parser to parse each tracking number file in the Tracking directory.
            CsvParser parser = new CsvParser(parserSettings);

            File trackingDir = job.getTrackingDir();

            Iterator it = FileUtils.iterateFiles(trackingDir, null, false);

            //For each tracking file in the tracking directory
            while (it.hasNext()) {

                String trackingAttachmentName = (((File) it.next()).getName());

                //Parse it
                parser.parse(getFileReader((trackingDir + "/" + trackingAttachmentName), job));

                System.out.println(trackingDir + "/" + trackingAttachmentName);

                List<String[]> trackingAttachmentRows = rowProcessor.getRows();

                System.out.println("^^^^^^^^^^^^^^^^^^^^Tracking Rows Start^^^^^^^^^^^^^^^^^^^^");

                if (!trackingAttachmentRows.isEmpty()) {

                    //For each row in the tracking attachment.
                    for (int k = 0; k < trackingAttachmentRows.size(); k++) {

                        //Make sure there are no spaces in the PO number.
                        Pattern pattern = Pattern.compile("\\s");
                        Matcher matcher = pattern.matcher(trackingAttachmentRows.get(k)[0]);
                        boolean found = matcher.find();

                        //Make sure there are no spaces in the PO number.
                        if (!found) {

                            con.setAutoCommit(false);

                            //Use the order id in the tracking attachment to get the billing email from the woocommerce db.
                            Statement stmt = con.createStatement();

                            //ResultSet resultSet = stmt.executeQuery("SELECT meta_value FROM " + job.getDbName() + ".wp_postmeta WHERE post_status = 'wc-awaiting-shipment' and  post_id = " + trackingAttachmentRows.get(k)[0] + " and meta_key = '_billing_email';");

                            //Get the billing email for the orders that are awaiting shipment,
                            // if the order is not of status "awaiting shipment", then the email address is not gotten or the email emailed.
                            ResultSet resultSet = stmt.executeQuery("SELECT meta_value FROM " + job.getDbName() + ".wp_postmeta t1 JOIN " + job.getDbName() + ".wp_posts t2 ON t1.post_id = t2.ID WHERE t2.post_status = 'wc-awaiting-shipment' and post_id = " + trackingAttachmentRows.get(k)[0] + " and meta_key = '_billing_email';");

                            //Email the tracking number to the customer using the billing email.
                            while (resultSet.next()) {
                                String email = resultSet.getString("meta_value");

                                String orderId = trackingAttachmentRows.get(k)[0];
                                String trackingNumber = trackingAttachmentRows.get(k)[1];
                                //Email the tracking number to the customer.
                                emailTrackingNumberToCust(job, email, orderId, trackingNumber);
                                Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ": Tracking emailed: " + email + " Order ID: " + orderId + " Tracking number: " + trackingNumber + System.getProperty("line.separator")));

                                String supplierName = job.getJobSupplierName();

                                //Initialize the trackingNumberString, so if there is a value present in the trackingNumberResultSet
                                // the trackingNumberString can be appended to.
                                String trackingNumberString = "\n" + supplierName + "." + trackingNumber;

                                //Do another resultset to get any tracking numbers already present in the Woo db and then
                                // append them to the new tracking number insert. Because new tracking email might have been downloaded.
                                Statement statement = con.createStatement();
                                ResultSet trackingNumberResultSet = statement.executeQuery("SELECT meta_value FROM " + job.getDbName() + ".wp_postmeta WHERE post_id = " + trackingAttachmentRows.get(k)[0] + " and meta_key = '_tracking_number';");

                                while (trackingNumberResultSet.next()) {
                                    trackingNumberString = trackingNumberString + "," + trackingNumberResultSet.getString("meta_value");
                                }

                                //Insert the tracking number into the db and set the order status to wc-completed.
                                //Set up the prepared statements.
                                String updateString =
                                        "DELETE FROM " + job.getDbName() + ".wp_postmeta WHERE meta_key = '_tracking_number' AND  post_id = ?;";

                                String updateString2 =
                                        "INSERT " + job.getDbName() + ".wp_postmeta (post_id, meta_key, meta_value) VALUES (?, '_tracking_number', ?);";


                                PreparedStatement deleteTrackingNumberFieldIntoPostMeta = con.prepareStatement(updateString);
                                deleteTrackingNumberFieldIntoPostMeta.setString(1, (orderId));
                                deleteTrackingNumberFieldIntoPostMeta.executeUpdate();

                                PreparedStatement insertTrackingNumberFieldIntoPostMeta = con.prepareStatement(updateString2);
                                insertTrackingNumberFieldIntoPostMeta.setString(1, (orderId));
                                insertTrackingNumberFieldIntoPostMeta.setString(2, (trackingNumberString));
                                insertTrackingNumberFieldIntoPostMeta.executeUpdate();

                                String updateStatement =
                                        "UPDATE " + job.getDbName() + ".wp_posts SET post_status = 'wc-completed'  WHERE wp_posts.ID = ?;";

                                PreparedStatement updateToWcCompleted = con.prepareStatement(updateStatement);
                                updateToWcCompleted.setString(1, (orderId));
                                updateToWcCompleted.executeUpdate();

                                con.commit();

                            }
                        }
                    }
                }
                System.out.println("^^^^^^^^^^^^^^^^^^^^Tracking Rows End^^^^^^^^^^^^^^^^^^^^");
            }
        } catch (Exception ex) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  parseTrackingAttachmentAndEmailToCust error. " + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            ex.printStackTrace();
        }
        //Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Done inserting any tracking number into Woocommerce db and updating status to 'wc-completed'. " + System.getProperty("line.separator")));
        System.out.println("[Exit] parseTrackingAttachmentAndEmailToCust ");
        Platform.runLater(() -> job.getTextArea().appendText("Exit parse tracking and email to customer" + System.getProperty("line.separator")));
    }


    //Helper method called from parseAndEmailTrackingAttachment, that emails the tracking number to the customer.
    public void emailTrackingNumberToCust(Job job, String email, String orderId, String trackingNumber) {

        System.out.println("[Enter helper] emailTrackingNumberToCust ");

        String sourceEmail = job.getJobYourCompanyEmailAddress(); // requires valid Gmail id
        String password = job.getJobYourCompanyEmailPassword(); // correct password for Gmail id
        String toEmail = email; // any destination email id
        String emailHostIp = job.getJobYourCompanyEmailIpAddress();
        Properties props = new Properties();
        props.put("mail.smtp.host", emailHostIp);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Authenticator authentication = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sourceEmail, password);
            }
        };
        Session session = Session.getInstance(props, authentication);

        String subject = "Tracking Number From " + job.getJobYourCompanyName() + " | " + job.getJobYourCompanyEmailAddress();

        String body = "Greetings, <br><br> Regarding your order having Order Id: " + orderId + " that you've placed with us, " + job.getJobYourCompanyName() + ". <br><br>Tracking number: " + trackingNumber + "." + "<br><br> " +
                "Regards, <br>" + job.getJobYourCompanyName();

        try {

            MimeMessage message = new MimeMessage(session);
            message.addHeader("Content-type", "text/HTML; charset=UTF-8");
            message.addHeader("format", "flowed");
            message.addHeader("Content-Transfer-Encoding", "8bit");

            message.setFrom(new InternetAddress(sourceEmail));
            message.setReplyTo(InternetAddress.parse(sourceEmail, false));
            message.setSubject(subject, "UTF-8");
            message.setSentDate(new Date());
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(body, "text/html");

            // Create a multipart message for attachment
            Multipart multipart = new MimeMultipart();

            // Trick is to add the content-id header here
            messageBodyPart.setHeader("Content-ID", "image_id");
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);

            // Finally Send message
            Transport.send(message);

            System.out.println("\n Email Sent Successfully With Attachment. Check your email now..");
            System.out.println("\n generateAndSendEmail() ends..");

        } catch (MessagingException e) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  emailTrackingNumberToCust error. " + e.getMessage() + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            e.printStackTrace();
        }
        System.out.println("[Exit helper] emailTrackingNumberToCust ");
    }


    //This is called from JobOverviewController to parse the removedProds.txt so that the prods in it can be
    // updated to 'pending' in Woocommerce.
    public void parseRemovedProdsFile(Job job) {

        System.out.println("[Enter] parseRemovedProdsFile");

        try {

            File supplierDir = job.getSupplierDir();

            File removedProds = new File(supplierDir + "/" + "removedProds.txt");

            if (removedProds.length() != 0) {

                //Configure the Univocity Parsers settings
                RowListProcessor rowProcessor = new RowListProcessor();
                CsvParserSettings parserSettings = new CsvParserSettings();
                parserSettings.setRowProcessor(rowProcessor);
                parserSettings.setHeaderExtractionEnabled(false);
                parserSettings.setLineSeparatorDetectionEnabled(true);
                parserSettings.setSkipEmptyLines(true);
                CsvParser parser = new CsvParser(parserSettings);
                parser.parse(getFileReader(supplierDir + "/removedProds.txt", job));

                listOfRemovedProds = rowProcessor.getRows();

            /*
            for (int i = 0; i < listOfRemovedProds.size(); i++) {
                System.out.println("%%%%%%%%%%%    " + listOfRemovedProds.get(i)[0] + " " + listOfRemovedProds.get(i)[1]);
            }
            */
            }

        } catch (Exception ex) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  parseRemovedProdsFile error. " + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            ex.printStackTrace();
        }
        System.out.println("[Exit] parseRemovedProdsFile");
    }


    //Creates the .csv from the data queried from the WooDb for sending to sky43.
    public void writeSky43CsvFile(List<String[]> prodDataForSky43, Job job) {

        System.out.println("[Enter] writeSky43CsvFile");

        File supplierDir = job.getSupplierDir();

        try {

            //Write to an in-memory byte array. This will be printed out to the standard output so you can easily see the result.
            ByteArrayOutputStream csvResult = new ByteArrayOutputStream();

            //CsvWriter (and all other file writers) work with an instance of java.io.Writer
            Writer outputWriter = new OutputStreamWriter(csvResult);

            CsvWriterSettings settings = new CsvWriterSettings();

            //Create a writer with the above settings;
            CsvWriter writer = new CsvWriter(outputWriter, settings);

            

            //Close the writer, since the parse is now written onto the csvResult
            writer.close();

            //Create File objects.
            File prodsSentToSky43File = new File(supplierDir + "/" + job.getJobName() + "/prodsSentToSky43File.txt");

            //Print the ByteArrayOutputStream to a file.
            Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(prodsSentToSky43File)));
            fileWriter.write(csvResult.toString());

        } catch (Exception ex) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  writeSky43CsvFile error. " + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            ex.printStackTrace();
        }
        System.out.println("[Exit] writeSky43CsvFile");
    }


    public void parseDiscountFile(Job job) {

        System.out.println("[Enter] parseDiscountsFile");
        Platform.runLater(() -> job.getTextArea().appendText("Enter parse discount file" + System.getProperty("line.separator")));


        File discountFile = job.getDiscountFile();

        //Configure the Univocity Parsers settings
        RowListProcessor rowProcessor = new RowListProcessor();
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setRowProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.setSkipEmptyLines(true);
        // sets what is the default value to use when the parsed value is null
        parserSettings.setNullValue("0");
        // sets what is the default value to use when the parsed value is empty
        parserSettings.setEmptyValue("0"); // for CSV only
        parserSettings.setNumberOfRowsToSkip(0);
        parserSettings.getFormat().setComment((char) job.getUpdateFileCommentIndicator());
        parserSettings.getFormat().setDelimiter((char) job.getUpdateFileSeparator());
        parserSettings.selectFields(job.getDiscountFileIdColumnName(), job.getDiscountColumnName());

        try {

            if (discountFile.exists()) {
                String discountFileString = discountFile.toString();
                CsvParser parser = new CsvParser(parserSettings);
                parser.parse(getFileReader(discountFileString, job));
                discountFileListOfRows = rowProcessor.getRows();

                /*
                for (int i = 0; i < discountFileListOfRows.size(); i++) {
                    String[] strings = discountFileListOfRows.get(i);
                    for (int j = 0; j < strings.length; j++) {
                        System.out.print(strings[j] + " ");
                    }
                    System.out.println();
                }
                */

            }
        } catch (Exception e) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  parseDiscountsFile error. " + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            e.printStackTrace();
        }
        //Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Parsed update file " + System.getProperty("line.separator")));
        System.out.println("[Exit] parseDiscountsFile");
        Platform.runLater(() -> job.getTextArea().appendText("Exit parse discount file" + System.getProperty("line.separator")));

    }
}
    
