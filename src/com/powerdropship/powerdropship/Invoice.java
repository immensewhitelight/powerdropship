package com.powerdropship.powerdropship;

import com.powerdropship.powerdropship.model.Job;
import com.powerdropship.powerdropship.model.Order;
import javafx.application.Platform;
import org.pdfclown.documents.Document;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.composition.BlockComposer;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.composition.XAlignmentEnum;
import org.pdfclown.documents.contents.composition.YAlignmentEnum;
import org.pdfclown.documents.contents.fonts.StandardType1Font;
import org.pdfclown.files.File;
import org.pdfclown.files.SerializationModeEnum;
import org.pdfclown.util.math.geom.Dimension;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.Properties;


public class Invoice {

    private static final int Margin_X = 50;
    private static final int Margin_Y = 50;

    //Constructor that gives jobOverviewController a reference back to itself.
    public Invoice(JobOverviewController jobOverviewController) {
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


    //Look through job.listOfOrders and job.listOfOrders.get(j).productsOfTheOrder to print and email the orders.
    public void printOrdersToPdfAndEmailToSupplier(Job job, Connection con) {

        System.out.println("[6. Enter] printOrdersToPdfAndEmailToSupplier ");
        Platform.runLater(() -> job.getTextArea().appendText("Enter print orders to pdf and email to supplier" + System.getProperty("line.separator")));


        int count = 0;

        try {

            //Create a directory to hold the order .pdfs
            String orderDir = job.getSupplierDir() + "/" + job.getJobName() + "/orders";
            Files.createDirectories(Paths.get(orderDir));

            for (int j = 0; j < job.listOfOrders.size(); j++) {
                //Only prints the pdf if the order is not already emailed. Products clears the orderliast from the previous
                // iteration and starts a new orderlist each time and only finds orders that are awaiting shipment.
                String orderId = job.listOfOrders.get(j).getOrderId();
                String name = job.listOfOrders.get(j).getFirstName();
                String lastName = job.listOfOrders.get(j).getLastName();
                String addressLine1 = job.listOfOrders.get(j).getAddressLine1();
                String addressLine2 = job.listOfOrders.get(j).getAddressLine2();
                String city = job.listOfOrders.get(j).getCity();
                String state = job.listOfOrders.get(j).getState();
                String postalCode = job.listOfOrders.get(j).getPostalCode();
                String country = job.listOfOrders.get(j).getCountry();
                String shippingMethod = job.listOfOrders.get(j).getShippingMethod();

                Document document = new File().getDocument();

                Page page = new Page(document);
                document.getPages().add(page);

                Dimension2D pageSize = page.getSize();

                PrimitiveComposer composer = new PrimitiveComposer(page);

                BlockComposer blockComposer = new BlockComposer(composer);

                composer.beginLocalState();

                Rectangle2D frame = new Rectangle2D.Double(Margin_X, Margin_Y, pageSize.getWidth() - Margin_X * 2, pageSize.getHeight() - Margin_Y * 2);

                blockComposer.begin(frame, XAlignmentEnum.Left, YAlignmentEnum.Top);

                Dimension breakSize = new Dimension(24, 8); // Indentation (24pt)
                // and top margin (8pt).

                composer.setFont(new StandardType1Font(document, StandardType1Font.FamilyEnum.Courier, true, false), 8);

                blockComposer.begin(frame, XAlignmentEnum.Left, YAlignmentEnum.Top);

                blockComposer.showText("ORDER NUMBER: " + orderId);
                blockComposer.showBreak(breakSize);
                blockComposer.showBreak(breakSize);
                blockComposer.showBreak(breakSize);
                blockComposer.showText(job.getJobYourCompanyName());
                blockComposer.showBreak(breakSize);
                blockComposer.showText(job.getJobYourCompanyEmailAddress());
                blockComposer.showBreak(breakSize);
                blockComposer.showBreak(breakSize);
                blockComposer.showText("ORDER SHIP TO ADDRESS:");
                blockComposer.showBreak(breakSize);
                blockComposer.showText(name + " " + lastName + " ");
                blockComposer.showBreak(breakSize);
                blockComposer.showText(addressLine1);
                blockComposer.showBreak(breakSize);
                blockComposer.showText(addressLine2);
                blockComposer.showBreak(breakSize);
                blockComposer.showText(city + " " + state + " " + postalCode + " " + country);
                blockComposer.showBreak(breakSize);
                blockComposer.showBreak(breakSize);
                blockComposer.showText("SHIPPING METHOD:");
                blockComposer.showBreak(breakSize);
                blockComposer.showText(shippingMethod);
                blockComposer.showBreak(breakSize);
                blockComposer.showBreak(breakSize);
                blockComposer.showBreak(breakSize);
                blockComposer.showText("ORDERED PRODUCTS:");
                blockComposer.showBreak(breakSize);

                for (int i = 0; i < job.listOfOrders.get(j).productsOfTheOrder.size(); i++) {

                    String productName = job.listOfOrders.get(j).productsOfTheOrder.get(i).getProductName();
                    String sku = job.listOfOrders.get(j).productsOfTheOrder.get(i).getVendorSku();
                    String upc = job.listOfOrders.get(j).productsOfTheOrder.get(i).getProductUpc();
                    String quantity = job.listOfOrders.get(j).productsOfTheOrder.get(i).getProductOrderQuantity();
                    String orderLine = "Product " + (i + 1) + ": qty: " + quantity + " sku: " + sku + " name: " + productName + " upc: " + upc;
                    blockComposer.showBreak(breakSize);
                    int orderLineTextIndex = 0;

                    //Seems to look at weather the space left is less than orderLine.length
                    while ((orderLineTextIndex += blockComposer.showText(orderLine.substring(orderLineTextIndex))) < orderLine.length()) {

                        blockComposer.end();
                        composer.flush();

                        document.getPages().add(page = new Page(document));
                        composer = new PrimitiveComposer(page);
                        blockComposer = new BlockComposer(composer);
                        blockComposer.begin(frame, XAlignmentEnum.Left, YAlignmentEnum.Top);
                        composer.setFont(new StandardType1Font(document, StandardType1Font.FamilyEnum.Courier, true, false), 8);
                    }
                    blockComposer.showBreak(new Dimension(10, 10));
                }

                blockComposer.end();
                composer.flush();

                //Creates the name of the order file.
                String orderFileName = orderId + ".pdf";

                //Saves the created pdf as this order file.
                document.getFile().save(orderDir + "/" + orderFileName, SerializationModeEnum.Standard);

                //Get the actual Order Object for passing to generateAndSendEmail
                Order thisOrder = job.listOfOrders.get(j);

                //Emails the Order to supplier after each iteration
                generateAndSendEmail(job, thisOrder, orderFileName, orderDir, con);

                count++;
            }

        } catch (Exception e) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Print Orders to PDF Error. " + e.getMessage() + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            e.printStackTrace();
        }

        final int finalCount = count;

        System.out.println(" Orders emailed this iteration: " + finalCount);
        System.out.println("[6. Exit] printOrdersToPdfAndEmailToSupplier ");

        Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ": Pdf order invoices emailed this iteration: " + finalCount + System.getProperty("line.separator")));
        Platform.runLater(() -> job.getTextArea().appendText("Exit print orders to pdf and email to supplier" + System.getProperty("line.separator")));
    }


    //Helper method called from printOrdersToPdf that sends the emails, customer billing email is passed in
    // and a copy is sent to the custmer the order is for .
    public void generateAndSendEmail(Job job, Order thisOrder, String orderFileName, String orderDir, Connection con) {

        String sourceEmail = job.getJobYourCompanyEmailAddress(); // requires valid Gmail id
        String password = job.getJobYourCompanyEmailPassword(); // correct password for Gmail id
        String toEmail = job.getJobSupplierOrderEmail(); // any destination email id
        String emailHostIp = job.getJobYourCompanyEmailIpAddress();

        System.out.println("\n1st ===> setup Mail Server Properties..");

        Properties props = new Properties();
        props.put("mail.smtp.host", emailHostIp);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        System.out.println("\n2nd ===> create Authenticator object to pass in Session.getInstance argument..");

        Authenticator authentication = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sourceEmail, password);
            }
        };
        Session session = Session.getInstance(props, authentication);

        String subject = "Purchase Order[" + thisOrder.getOrderId() + "] from " + job.getJobYourCompanyName() + " | " + job.getJobYourCompanyEmailAddress();
        String body = "Greetings, <br><br> Attached is a copy of an order from " + job.getJobYourCompanyName() + " | " +
                job.getJobYourCompanyEmailAddress() + " <br><br>Regards, <br>" + job.getJobYourCompanyName();

        try {
            System.out.println("\n3rd ===> generateAndSendEmail() starts..");

            MimeMessage message = new MimeMessage(session);
            message.addHeader("Content-type", "text/HTML; charset=UTF-8");
            message.addHeader("format", "flowed");
            message.addHeader("Content-Transfer-Encoding", "8bit");

            message.setFrom(new InternetAddress(sourceEmail));
            message.setReplyTo(InternetAddress.parse(sourceEmail, false));
            message.setSubject(subject, "UTF-8");
            message.setSentDate(new Date());
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            //message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(customerBillingEmail, false));

            // Create the message body part
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(body, "text/html");

            // Create a multipart message for attachment
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            messageBodyPart = new MimeBodyPart();

            // Valid file location
            String filename = orderDir + "/" + orderFileName;
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(orderFileName);

            // Trick is to add the content-id header here
            messageBodyPart.setHeader("Content-ID", "image_id");
            multipart.addBodyPart(messageBodyPart);

            System.out.println("\n4th ===> third part for displaying image in the email body..");
            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent("<br><h3>Find below attached image</h3>" + "<img src='cid:image_id'>", "text/html");
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);

            System.out.println("\n5th ===> Finally Send message..");

            // Finally Send message
            Transport.send(message);

            System.out.println("\n6th ===> Email Sent Successfully With Image Attachment. Check your email now..");
            System.out.println("\n7th ===> generateAndSendEmail() ends..");

            System.out.println("[6. Enter] setStatusToAwaitingShipment ");

            //We configure the prepared statements for the update.
            PreparedStatement updateToWcAwaitingShipment = null;

            String updateString =
                    "UPDATE " + job.getDbName() + ".wp_posts SET post_status = 'wc-awaiting-shipment'  WHERE wp_posts.ID = ?;";

            con.setAutoCommit(false);
            updateToWcAwaitingShipment = con.prepareStatement(updateString);

            updateToWcAwaitingShipment.setString(1, (thisOrder.getOrderId()));
            updateToWcAwaitingShipment.executeUpdate();

            con.commit();

        } catch (Exception e) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Emailing PDF to Supplier Error. " + e.getMessage() + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            e.printStackTrace();
        }
    }
}


