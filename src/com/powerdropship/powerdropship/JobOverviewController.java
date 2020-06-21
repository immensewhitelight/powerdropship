package com.powerdropship.powerdropship;

import com.powerdropship.powerdropship.model.Job;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.sql.Connection;


public class JobOverviewController {


    @FXML
    private TableView<Job> jobTable;

    @FXML
    private TableColumn<Job, String> jobNameColumn;

    @FXML
    private Label jobNameLabel;

    @FXML
    private Label jobSupplierNameLabel;

    @FXML
    private Label jobSupplierOrderEmailLabel;

    @FXML
    private Label jobYourCompanyNameLabel;

    @FXML
    private Label jobYourCompanyEmailAddressLabel;

    @FXML
    private Label jobYourCompanyEmailIpAddressLabel;

    @FXML
    private Label jobYourCompanyEmailPasswordLabel;


    @FXML
    private Label delayDurationLabel;

    @FXML
    private Label repeatIntervalLabel;


    @FXML
    private Label updateFileNameLabel;

    @FXML
    private Label updateFileProdIDColLabel;

    @FXML
    private Label updateFilePriceColLabel;

    @FXML
    private Label updateFileMAPPriceColLabel;

    @FXML
    private Label updateFileQtyColLabel;

    @FXML
    private Label updateFileDiscountIDColLabel;

    @FXML
    private Label updateFileProdUpcColLabel;

    @FXML
    private Label updateFileFTPUrlLabel;

    @FXML
    private Label updateFileFTPUserLabel;

    @FXML
    private Label updateFileFTPPassLabel;

    @FXML
    private Label updateFileEmailFromLabel;

    @FXML
    private Label updateFileEmailKeywordLabel;

    @FXML
    private Label updateFileSeparatorLabel;

    @FXML
    private Label updateFileBeginningRowsToSkipLabel;

    @FXML
    private Label updateFileCommentIndicatorLabel;

    @FXML
    private Label updateFileSetRemovedProdsToPendingLabel;

    @FXML
    private Label updateFileUpdateAllProdsToPendingLabel;


    @FXML
    private Label priceMultiplierLabel;

    @FXML
    private Label priceMultiplierMAPLabel;

    @FXML
    private Label priceTier1StartLabel;

    @FXML
    private Label priceTier1EndLabel;

    @FXML
    private Label priceTier1MultiplierLabel;

    @FXML
    private Label priceTier2StartLabel;

    @FXML
    private Label priceTier2EndLabel;

    @FXML
    private Label priceTier2MultiplierLabel;

    @FXML
    private Label priceTier3StartLabel;

    @FXML
    private Label priceTier3EndLabel;

    @FXML
    private Label priceTier3MultiplierLabel;

    @FXML
    private Label priceTier4StartLabel;

    @FXML
    private Label priceTier4EndLabel;

    @FXML
    private Label priceTier4MultiplierLabel;


    @FXML
    private Label dbIPAddressLabel;

    @FXML
    private Label sshUserLabel;

    @FXML
    private Label sshPassLabel;

    @FXML
    private Label dbNameLabel;

    @FXML
    private Label dbUserLabel;

    @FXML
    private Label dbPassLabel;

    @FXML
    private TextArea textArea;


    @FXML
    private Label trackingEmailFromLabel;

    @FXML
    private Label trackingEmailKeywordLabel;

    @FXML
    private Label trackingCsvSeparatorLabel;

    @FXML
    private Label trackingBeginningRowsToSkipLabel;

    @FXML
    private Label trackingCommentIndicatorLabel;

    @FXML
    private Label trackingOrderIdColNameLabel;

    @FXML
    private Label trackingNumberColNameLabel;


    @FXML
    private Label discountFileNameLabel;

    @FXML
    private Label discountFileIdColumnNameLabel;

    @FXML
    private Label discountColumnNameLabel;


    @FXML
    private TabPane tabPane;


    // Reference to the main application.
    private MainApp mainApp;


    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public JobOverviewController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {




        // Initialize the job table with the one column.
        jobNameColumn.setCellValueFactory(cellData -> cellData.getValue().jobNameProperty());

        // Clear job details.
        showJobDetails(null);

        // Listen for selection changes and show the job details when changed.
        jobTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showJobDetails(newValue));

        tabPane.getTabs().get(0).setText("Status");

        Tab tab = new Tab();
        tab.setText("Instructions");
        TextArea textArea = new TextArea("\n1. " +
                "Click the “New” button to add a new job, fill the details into the “Edit Job” \n" +
                "window. Use the test job at left as a setup example. “Edit Job” windows for \n" +
                "different jobs can be opened at the same time.\n" +
                "\n" +
                "2. Select the new job by clicking on it in the “Jobs” list.\n" +
                "\n" +
                "3. Click the “Run” button to run the job, this will do all the following \n" +
                "things in succession: \n" +
                "\n" +
                "\ta. Creates the DroshipCommerce dir in the user home dir.\n" +
                "\n" +
                "\tb. Downloads the update file.\n" +
                "\n" +
                "\tc. Creates files of the prods added and removed from the update file.\n" +
                "\n" +
                "\td. Updates the WooCommerce db prices and quantities.\n" +
                "\n" +
                "\te. Emails pdf order invoices to the supplier.\n" +
                "\n" +
                "\tf. Downloads tracking numbers and emails them to customers.\n" +
                "\n" +
                "\tg. Updates certain products to pending.");

        tab.setContent(textArea);
        tabPane.getTabs().add(tab);

    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        jobTable.setItems(mainApp.getJobData());
    }


    /**
     * Fills all text fields to show details about the job.
     * If the specified job is null, all text fields are cleared.
     *
     * @param job the job or null
     */
    private void showJobDetails(Job job) {
        if (job != null) {
            // Fill the labels with info from the job object.
            jobNameLabel.setText(job.getJobName());
            jobSupplierNameLabel.setText(job.getJobSupplierName());
            jobSupplierOrderEmailLabel.setText(job.getJobSupplierOrderEmail());
            jobYourCompanyNameLabel.setText(job.getJobYourCompanyName());
            jobYourCompanyEmailAddressLabel.setText(job.getJobYourCompanyEmailAddress());
            jobYourCompanyEmailIpAddressLabel.setText(job.getJobYourCompanyEmailIpAddress());
            jobYourCompanyEmailPasswordLabel.setText(job.getJobYourCompanyEmailPassword());

            delayDurationLabel.setText(String.valueOf(job.getDelayDuration()));
            repeatIntervalLabel.setText(String.valueOf(job.getRepeatInterval()));

            updateFileNameLabel.setText(job.getUpdateFileName());
            updateFileProdIDColLabel.setText(job.getUpdateFileProdIDCol());
            updateFilePriceColLabel.setText(job.getUpdateFilePriceCol());
            updateFileMAPPriceColLabel.setText(job.getUpdateFileMAPPriceCol());
            updateFileQtyColLabel.setText(job.getUpdateFileQtyCol());
            updateFileProdUpcColLabel.setText(job.getUpdateFileProdUpcCol());
            updateFileDiscountIDColLabel.setText(job.getUpdateFileDiscountIDCol());
            updateFileFTPUrlLabel.setText(job.getUpdateFileFTPUrl());
            updateFileFTPUserLabel.setText(job.getUpdateFileFTPUser());
            updateFileFTPPassLabel.setText(job.getUpdateFileFTPPass());
            updateFileEmailFromLabel.setText(job.getUpdateFileEmailFrom());
            updateFileEmailKeywordLabel.setText(job.getUpdateFileEmailKeyword());

            updateFileSeparatorLabel.setText(String.valueOf(job.getUpdateFileSeparator()));
            updateFileBeginningRowsToSkipLabel.setText(String.valueOf(job.getUpdateFileBeginningRowsToSkip()));
            updateFileCommentIndicatorLabel.setText(String.valueOf(job.getUpdateFileCommentIndicator()));
            updateFileSetRemovedProdsToPendingLabel.setText(job.getUpdateFileSetRemovedProdsToPending());
            updateFileUpdateAllProdsToPendingLabel.setText(job.getUpdateFileUpdateAllProdsToPending());


            priceMultiplierLabel.setText(String.valueOf(job.getPriceMultiplier()));
            priceMultiplierMAPLabel.setText(String.valueOf(job.getPriceMultiplierMAP()));

            priceTier1StartLabel.setText(String.valueOf(job.getPriceTier1Start()));
            priceTier1EndLabel.setText(String.valueOf(job.getPriceTier1End()));
            priceTier1MultiplierLabel.setText(String.valueOf(job.getPriceTier1Multiplier()));
            priceTier2StartLabel.setText(String.valueOf(job.getPriceTier2Start()));
            priceTier2EndLabel.setText(String.valueOf(job.getPriceTier2End()));
            priceTier2MultiplierLabel.setText(String.valueOf(job.getPriceTier2Multiplier()));
            priceTier3StartLabel.setText(String.valueOf(job.getPriceTier3Start()));
            priceTier3EndLabel.setText(String.valueOf(job.getPriceTier3End()));
            priceTier3MultiplierLabel.setText(String.valueOf(job.getPriceTier3Multiplier()));
            priceTier4StartLabel.setText(String.valueOf(job.getPriceTier4Start()));
            priceTier4EndLabel.setText(String.valueOf(job.getPriceTier4End()));
            priceTier4MultiplierLabel.setText(String.valueOf(job.getPriceTier4Multiplier()));

            dbIPAddressLabel.setText(job.getDbIPAddress());
            sshUserLabel.setText(job.getSshUser());
            sshPassLabel.setText(job.getSshPass());
            dbNameLabel.setText(job.getDbName());
            dbUserLabel.setText(job.getDbUser());
            dbPassLabel.setText(job.getDbPass());

            trackingEmailFromLabel.setText(job.getTrackingEmailFrom());
            trackingEmailKeywordLabel.setText(job.getTrackingEmailKeyword());
            trackingCsvSeparatorLabel.setText(String.valueOf(job.getTrackingCsvSeparator()));
            trackingBeginningRowsToSkipLabel.setText(String.valueOf(job.getTrackingBeginningRowsToSkip()));
            trackingCommentIndicatorLabel.setText(String.valueOf(job.getTrackingCommentIndicator()));
            trackingOrderIdColNameLabel.setText(job.getTrackingOrderIdColName());
            trackingNumberColNameLabel.setText(job.getTrackingNumberColName());


            discountFileNameLabel.setText(job.getDiscountFileName());
            discountFileIdColumnNameLabel.setText(job.getDiscountFileIdColumnName());
            discountColumnNameLabel.setText(job.getDiscountColumnName());


        } else {
            // Job is null, remove all the text.
            jobNameLabel.setText("");
            jobSupplierNameLabel.setText("");
            jobSupplierOrderEmailLabel.setText("");
            jobYourCompanyNameLabel.setText("");
            jobYourCompanyEmailAddressLabel.setText("");
            jobYourCompanyEmailIpAddressLabel.setText("");
            jobYourCompanyEmailPasswordLabel.setText("");


            updateFileNameLabel.setText("");
            updateFileProdIDColLabel.setText("");
            updateFilePriceColLabel.setText("");
            updateFileMAPPriceColLabel.setText("");
            updateFileQtyColLabel.setText("");
            updateFileProdUpcColLabel.setText("");
            updateFileDiscountIDColLabel.setText("");
            updateFileFTPUrlLabel.setText("");
            updateFileFTPUserLabel.setText("");
            updateFileFTPPassLabel.setText("");
            updateFileEmailFromLabel.setText("");
            updateFileEmailKeywordLabel.setText("");


            updateFileSeparatorLabel.setText("");
            updateFileBeginningRowsToSkipLabel.setText("");
            updateFileCommentIndicatorLabel.setText("");
            updateFileSetRemovedProdsToPendingLabel.setText("");
            updateFileUpdateAllProdsToPendingLabel.setText("");


            priceMultiplierLabel.setText("");
            priceMultiplierMAPLabel.setText("");
            priceTier1StartLabel.setText("");
            priceTier1EndLabel.setText("");
            priceTier1MultiplierLabel.setText("");
            priceTier2StartLabel.setText("");
            priceTier2EndLabel.setText("");
            priceTier2MultiplierLabel.setText("");
            priceTier3StartLabel.setText("");
            priceTier3EndLabel.setText("");
            priceTier3MultiplierLabel.setText("");
            priceTier4StartLabel.setText("");
            priceTier4EndLabel.setText("");
            priceTier4MultiplierLabel.setText("");


            delayDurationLabel.setText("");
            repeatIntervalLabel.setText("");


            dbIPAddressLabel.setText("");
            sshUserLabel.setText("");
            sshPassLabel.setText("");
            dbNameLabel.setText("");
            dbUserLabel.setText("");
            dbPassLabel.setText("");


            trackingEmailFromLabel.setText("");
            trackingEmailKeywordLabel.setText("");
            trackingCsvSeparatorLabel.setText("");
            trackingBeginningRowsToSkipLabel.setText("");
            trackingCommentIndicatorLabel.setText("");
            trackingOrderIdColNameLabel.setText("");
            trackingNumberColNameLabel.setText("");


        }
    }

    public void print(String message) {

        textArea.appendText(message);
    }

    @FXML
    public void clear() {

        textArea.setText("Cleared by click...wait for next message..." + System.getProperty("line.separator"));

    }

    /**
     * Called when the user clicks on the delete button.
     */
    @FXML
    private void handleDeleteJob() {

        int selectedIndex = jobTable.getSelectionModel().getSelectedIndex();
        Job selectedJob = jobTable.getSelectionModel().getSelectedItem();
        if (selectedIndex >= 0) {
            jobTable.getItems().remove(selectedIndex);
            cancelService(selectedJob);
        } else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Job Selected");
            alert.setContentText("Please select a job in the table.");

            alert.showAndWait();
        }
    }

    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new job.
     */
    @FXML
    private void handleNewJob() {
        Job tempJob = new Job();
        boolean okClicked = mainApp.showJobEditDialog(tempJob);
        if (okClicked) {
            mainApp.getJobData().add(tempJob);
        }
    }

    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected job.
     */
    @FXML
    private void handleEditJob() {
        Job selectedJob = jobTable.getSelectionModel().getSelectedItem();
        if (selectedJob != null) {
            boolean okClicked = mainApp.showJobEditDialog(selectedJob);
            if (okClicked) {
                showJobDetails(selectedJob);
            }

        } else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Job Selected");
            alert.setContentText("Please select a job in the table.");

            alert.showAndWait();
        }
    }

    @FXML
    private void handleRunJob() {
        Job selectedJob = jobTable.getSelectionModel().getSelectedItem();
        scheduleService(selectedJob);
    }

    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected job.
     */
    @FXML
    private void handleCancelJob() {
        Job selectedJob = jobTable.getSelectionModel().getSelectedItem();
        if (selectedJob != null) {

            cancelService(selectedJob);

        } else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Job Selected");
            alert.setContentText("Please select a job in the table.");

            alert.showAndWait();
        }
    }

    //Uses ScheduleService to create a thread for a new or edited job.
    // Sets the job instance ServiceReference property to service reference.
    private void scheduleService(Job newJob) {

        ScheduledService<Job> service = new ScheduledService<Job>() {
            @Override
            protected Task<Job> createTask() {
                return new Task<Job>() {
                    @Override
                    protected Job call() {
                        updateGo(newJob);
                        return newJob;
                    }
                };
            }
        };
        service.setOnSucceeded(event -> print(newJob.getJobName() + ":  Job SUCCEEDED and will repeat in " + newJob.getRepeatInterval() + " minutes..." + System.getProperty("line.separator")));
        service.setDelay(Duration.minutes(newJob.getDelayDuration()));
        service.setPeriod(Duration.minutes(newJob.getRepeatInterval()));
        service.setRestartOnFailure(true);
        service.start();
        newJob.setServiceInstanceReference(service);
    }


    //Cancels the service mentioned by the job instance serviceReference property.
    private void cancelService(Job selectedJob) {

        if (selectedJob.getServiceInstanceReference() != null) {
            selectedJob.getServiceInstanceReference().cancel();
            print(selectedJob.getJobName() + ":  Changed to " + selectedJob.getServiceInstanceReference().getState() + System.getProperty("line.separator"));
            print(selectedJob.getJobName() + ":  ...Trying to shutdown the job...wait" + System.getProperty("line.separator"));
        }
    }

    //Runs the actual update.
    public void updateGo(Job job) {

        //Only open a tab for the job if a textarea in the tab doesn't exist.
        if (job.getTextArea() == null) {
            //Add new tab for the job, and a TextArea to the tab.
            Tab tab = new Tab();
            tab.setText(job.getJobName());
            TextArea textArea = new TextArea(job.getJobName());
            tab.setContent(textArea);
            job.setTextArea(textArea);
            Platform.runLater(() -> tabPane.getTabs().add(tab));
        }

        //Print to the TextArea in the tab.
        Platform.runLater(() -> job.getTextArea().setText(":  Product update pending...delayed " + job.getDelayDuration() + " minutes..." + System.getProperty("line.separator")));
        Platform.runLater(() -> job.getTextArea().setText("*** " + job.getJobName() + ": Begin iteration ***" + System.getProperty("line.separator")));

        //Create the Files.
        File supplierDir = new File(System.getProperty("user.home") + "/PowerDropship/" + job.getJobSupplierName());
        File updateFile = new File(System.getProperty("user.home") + "/PowerDropship/" + job.getJobSupplierName() + "/" + job.getUpdateFileName());
        if (!job.getDiscountFileName().isEmpty()) {
            File discountFile = new File(System.getProperty("user.home") + "/PowerDropship/" + job.getJobSupplierName() + "/" + job.getDiscountFileName());
            job.setDiscountFile(discountFile);
        }
        File trackingDir = new File(System.getProperty("user.home") + "/PowerDropship/" + job.getJobSupplierName() + "/" + job.getJobName() + "/tracking");
        File trackingFile = new File(System.getProperty("user.home") + "/PowerDropship/" + job.getJobSupplierName() + "/" + job.getJobName() + "/tracking/tracking");
        File orderDir = new File(System.getProperty("user.home") + "/PowerDropship/" + job.getJobSupplierName() + "/" + job.getJobName() + "/orders");

        //Set the Job Model for the Files.
        job.setSupplierDir(supplierDir);

        job.setUpdateFile(updateFile);
        job.setTrackingDir(trackingDir);
        job.setTrackingFile(trackingFile);
        job.setOrderDir(orderDir);

        //MainApp creates the PowerDropship dir when the software is first started.
        // supplierDir is created here using the supplier name when the job first runs, and is set on the Job model.
        if (!supplierDir.exists()) {
            if (supplierDir.mkdirs()) {
                System.out.println("Supplier Directory created!");
            } else {
                System.out.println("Failed to create Supplier Directory!");
            }
        }

        if (!trackingDir.exists()) {
            if (trackingDir.mkdirs()) {
                System.out.println("Supplier Tracking Directory created!");
            } else {
                System.out.println("Failed to create Supplier Tracking Directory!");
            }
        }

        if (!orderDir.exists()) {
            if (orderDir.mkdirs()) {
                System.out.println("Supplier Order Directory created!");
            } else {
                System.out.println("Failed to create Supplier Order Directory!");
            }
        }

        //Creates the actual Tracking File.
        try {
            job.getTrackingFile().createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Login to Supplier website to click the link that generates the shipment tracking file.
       /*
        if (job.getJobName().equals("EuropaX")) {
            DownloadTrackingFromWebsite downloadTrackingFromWebsite = new DownloadTrackingFromWebsite(this);
            downloadTrackingFromWebsite.downloadTrackingFromWebsite(job);
        }
        */
        //Downloads the remote updateFile to the Supplier directory, from ftp source or email source.
        DownloadUpdateFile downloadUpdateFile = new DownloadUpdateFile(this);
        if (!job.getUpdateFileFTPUrl().isEmpty()) {
            downloadUpdateFile.ftpDownloadUpdateFile(job);
        }

        if (job.getUpdateFileFTPUrl().isEmpty()) {
            downloadUpdateFile.javaMailDownloadUpdateFile(job);
        }

        //Instantiate the ConvertFormat, and Parse classes.
        ConvertFormat convertFormat = new ConvertFormat(this);
        Parse parse = new Parse(this);

        //Get the updateFile extension, so that it can be known weather the file needs to be converted.
        String updateFilePath = updateFile.getAbsolutePath();
        String updateFileExt = FilenameUtils.getExtension(updateFilePath);

        //Get the discountFile extension, so that it can be known weather the file needs to be converted.
        String discountFilePath = updateFile.getAbsolutePath();
        String discountFileExt = FilenameUtils.getExtension(discountFilePath);

        //Converts the downloaded updateFile to csv.
        if (updateFile.exists() & (updateFileExt.equals("xls") | updateFileExt.equals("xlsx"))) {
            convertFormat.convertUpdateFileToCsv(job);
        }

        //Parses the products file after conversion if it was required,
        // uses the parse to compare to prodsInUsedUpdate.txt so that new or removed products are found.
        parse.parseUpdateFile(job);
        //Appends any new or removed products into the newProds.txt and removedProds.txt files.
        parse.findUpdateFileNewAndRemovedProds(job);

        //Convert the discount file to csv if needed, so that it can be parsed by the parser.
        if (job.getDiscountFile().exists() & (discountFileExt.equals("xls") | discountFileExt.equals("xlsx"))) {
            convertFormat.convertDiscountFileToCsv(job);
        }

        parse.parseDiscountFile(job);

        //Connects to remote WooCommerce db, job and jobDataSize(to assist random port selection) are passed in, an ssh session is
        // established and a connection is returned.
        int jobDataSize = mainApp.getJobData().size();
        DbConnect dbConnect = new DbConnect(this);
        //Pass in the job List size to help the random port selection.
        Connection con = null;
        if (job.getSshUser().isEmpty()) {
            con = dbConnect.directDbConnect(job);
        } else {
            con = dbConnect.sshConnect(job, jobDataSize);
        }
        // Updates the price and qty using the parse, retrieves products being ordered, creates orders.
        Update update = new Update(this);
        //Update the WooCommerce db, pass in the parsed discounts.
        //Begins by getting the skus from the WooCommerce db that are in the Update File so that only they are handled.
        update.updateCartDb(job, parse.getUpdateFileListOfRows(), con, parse.discountFileListOfRows, parse.getNumberOfQtyCols());
        //Retrieves each prod on order separately, filters against the Update File, thus filtering out the prods just for this supplier.
        update.retrieveProductsOnOrderData(job, con);
        //Create the Orders from the filtered prods on order, and create the Order List.
        update.createOrders(job, con);
        //Prints the pdf and emails them to the supplier, updates WooCommerce products order status to awaiting shipment.
        // immediately after the pdf is printed and emailed.
        Invoice invoice = new Invoice(this);
        invoice.printOrdersToPdfAndEmailToSupplier(job, con);
        //Searches the email messages for ones that contain the tracking number keyword terms, downloads each email's
        // tracking attachment, and sets the email as "flagged" on the email server so it isn't downloaded again.
        DownloadTrackingFile downloadTrackingFile = new DownloadTrackingFile(this);
        downloadTrackingFile.downloadTrackingFile(job);
        //Parses the tracking number attachments, by iterating through the tracking file directory and emails the tracking
        // to the customer.
        parse.parseTrackingAttachmentAndEmailToCust(job, con);

        //Updates all prods to "pending" if that option is set. This is so supplier's prods can be removed from the WooCommerce db.
        update.updateAllProdsToPending(job, con);

        //Set the prods in removedProds.txt to "pending" in WooCommerce by parsing the removedProds.txt file.
        parse.parseRemovedProdsFile(job);
        //Set prods in removed.txt to "pending" in Woocommerce.
        update.updateRemovedProdsToPending(job, parse.getListOfRemovedProds(), con);


        //Query products for prod data and create an array for sending to Sky43.
        //products.queryProdDataForSky43(job, con);

        //Write from the prodDataForSky43 Array to a .csv file.
        //parse.writeSky43CsvFile(products.getProdDataForSky43(), job);

        //Upload the prod data .csv file to sky43.
        //Sky43 uploadProdDataFileToSky43 = new Sky43(this);
        //uploadProdDataFileToSky43.sftpUploadLinkFileToSky43(job);

        //Close the connections used by this job, this automatically closes all the associated resources too.
        try {
            con.close();
        } catch (Exception e) { /* ignored */ }

        Platform.runLater(() -> print("$$$       " + job.getJobName() + ": End interation       $$$" + System.getProperty("line.separator")));
        System.out.println("Job Complete ");
    }
}

