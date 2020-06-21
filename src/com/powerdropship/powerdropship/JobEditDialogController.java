package com.powerdropship.powerdropship;

import com.powerdropship.powerdropship.model.Job;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/**
 * Dialog to edit details of a job.
 *
 * @author Marco Jakob
 */
public class JobEditDialogController {

    @FXML
    private TextField jobNameField;

    @FXML
    private TextField jobSupplierNameField;

    @FXML
    private TextField jobSupplierOrderEmailField;

    @FXML
    private TextField jobYourCompanyNameField;

    @FXML
    private TextField jobYourCompanyEmailAddressField;

    @FXML
    private TextField jobYourCompanyEmailPasswordField;

    @FXML
    private TextField jobYourCompanyEmailIpAddressField;



    @FXML
    private TextField delayDurationField;

    @FXML
    private TextField repeatIntervalField;



    @FXML
    private TextField updateFileNameField;

    @FXML
    private TextField updateFileProdIDColField;

    @FXML
    private TextField updateFilePriceColField;

    @FXML
    private TextField updateFileMAPPriceColField;

    @FXML
    private TextField updateFileQtyColField;

    @FXML
    private TextField updateFileProdUpcColField;

    @FXML
    private TextField updateFileDiscountIDColField;

    @FXML
    private TextField updateFileFTPUrlField;

    @FXML
    private TextField updateFileFTPUserField;

    @FXML
    private TextField updateFileFTPPassField;

    @FXML
    private TextField updateFileEmailFromField;

    @FXML
    private TextField updateFileEmailKeywordField;


    @FXML
    private TextField updateFileSeparatorField;

    @FXML
    private TextField updateFileBeginningRowsToSkipField;

    @FXML
    private TextField updateFileCommentIndicatorField;

    @FXML
    private TextField updateFileSetRemovedProdsToPendingField;

    @FXML
    private TextField updateFileUpdateAllProdsToPendingField;




    @FXML
    private TextField priceMultiplierField;

    @FXML
    private TextField priceMultiplierMAPField;

    @FXML
    private TextField priceTier1StartField;

    @FXML
    private TextField priceTier1EndField;

    @FXML
    private TextField priceTier1MultiplierField;

    @FXML
    private TextField priceTier2StartField;

    @FXML
    private TextField priceTier2EndField;

    @FXML
    private TextField priceTier2MultiplierField;

    @FXML
    private TextField priceTier3StartField;

    @FXML
    private TextField priceTier3EndField;

    @FXML
    private TextField priceTier3MultiplierField;

    @FXML
    private TextField priceTier4StartField;

    @FXML
    private TextField priceTier4EndField;

    @FXML
    private TextField priceTier4MultiplierField;




    @FXML
    private TextField dbIPAddressField;

    @FXML
    private TextField sshUserField;

    @FXML
    private TextField sshPassField;

    @FXML
    private TextField dbNameField;

    @FXML
    private TextField dbUserField;

    @FXML
    private TextField dbPassField;



    @FXML
    private TextField trackingEmailFromField;

    @FXML
    private TextField trackingEmailKeywordField;

    @FXML
    private TextField trackingCsvSeparatorField;

    @FXML
    private TextField trackingBeginningRowsToSkipField;

    @FXML
    private TextField trackingCommentIndicatorField;

    @FXML
    private TextField trackingOrderIdColNameField;

    @FXML
    private TextField trackingNumberColNameField;


    @FXML
    private TextField discountFileNameField;

    @FXML
    private TextField discountFileDiscountIDColField;

    @FXML
    private TextField discountColumnNameField;



    private Stage dialogStage;
    private Job job;
    private boolean okClicked = false;


    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the job to be edited in the dialog.
     *
     * @param job
     */
    public void setJob(Job job) {
        this.job = job;

        jobNameField.setText(job.getJobName());
        jobSupplierNameField.setText(job.getJobSupplierName());
        jobSupplierOrderEmailField.setText(job.getJobSupplierOrderEmail());
        jobYourCompanyNameField.setText(job.getJobYourCompanyName());
        jobYourCompanyEmailAddressField.setText(job.getJobYourCompanyEmailAddress());
        jobYourCompanyEmailPasswordField.setText(job.getJobYourCompanyEmailPassword());
        jobYourCompanyEmailIpAddressField.setText(job.getJobYourCompanyEmailIpAddress());

        delayDurationField.setText(String.valueOf(job.getDelayDuration()));
        repeatIntervalField.setText(Double.toString(job.getRepeatInterval()));

        updateFileNameField.setText(job.getUpdateFileName());
        updateFileProdIDColField.setText(job.getUpdateFileProdIDCol());
        updateFilePriceColField.setText(job.getUpdateFilePriceCol());
        updateFileMAPPriceColField.setText(job.getUpdateFileMAPPriceCol());
        updateFileQtyColField.setText(job.getUpdateFileQtyCol());
        updateFileProdUpcColField.setText(job.getUpdateFileProdUpcCol());
        updateFileDiscountIDColField.setText(job.getUpdateFileDiscountIDCol());
        updateFileFTPUrlField.setText(job.getUpdateFileFTPUrl());
        updateFileFTPUserField.setText(job.getUpdateFileFTPUser());
        updateFileFTPPassField.setText(job.getUpdateFileFTPPass());

        updateFileEmailFromField.setText(job.getUpdateFileEmailFrom());
        updateFileEmailKeywordField.setText(job.getUpdateFileEmailKeyword());


        updateFileSeparatorField.setText(Integer.toString(job.getUpdateFileSeparator()));
        updateFileBeginningRowsToSkipField.setText(Integer.toString(job.getUpdateFileBeginningRowsToSkip()));
        updateFileCommentIndicatorField.setText(Integer.toString(job.getUpdateFileCommentIndicator()));
        updateFileSetRemovedProdsToPendingField.setText(job.getUpdateFileSetRemovedProdsToPending());
        updateFileUpdateAllProdsToPendingField.setText(job.getUpdateFileUpdateAllProdsToPending());

        priceMultiplierField.setText(Double.toString(job.getPriceMultiplier()));
        priceMultiplierMAPField.setText(Double.toString(job.getPriceMultiplierMAP()));
        priceTier1StartField.setText(Double.toString(job.getPriceTier1Start()));
        priceTier1EndField.setText(Double.toString(job.getPriceTier1End()));
        priceTier1MultiplierField.setText(Double.toString(job.getPriceTier1Multiplier()));
        priceTier2StartField.setText(Double.toString(job.getPriceTier2Start()));
        priceTier2EndField.setText(Double.toString(job.getPriceTier2End()));
        priceTier2MultiplierField.setText(Double.toString(job.getPriceTier2Multiplier()));
        priceTier3StartField.setText(Double.toString(job.getPriceTier3Start()));
        priceTier3EndField.setText(Double.toString(job.getPriceTier3End()));
        priceTier3MultiplierField.setText(Double.toString(job.getPriceTier3Multiplier()));
        priceTier4StartField.setText(Double.toString(job.getPriceTier4Start()));
        priceTier4EndField.setText(Double.toString(job.getPriceTier4End()));
        priceTier4MultiplierField.setText(Double.toString(job.getPriceTier4Multiplier()));

        dbIPAddressField.setText(job.getDbIPAddress());
        sshUserField.setText(job.getSshUser());
        sshPassField.setText(job.getSshPass());
        dbNameField.setText(job.getDbName());
        dbUserField.setText(job.getDbUser());
        dbPassField.setText(job.getDbPass());

        trackingEmailFromField.setText(job.getTrackingEmailFrom());
        trackingEmailKeywordField.setText(job.getTrackingEmailKeyword());
        trackingCsvSeparatorField.setText(Integer.toString(job.getTrackingCsvSeparator()));
        trackingBeginningRowsToSkipField.setText(Integer.toString(job.getTrackingBeginningRowsToSkip()));
        trackingCommentIndicatorField.setText(Integer.toString(job.getTrackingCommentIndicator()));
        trackingOrderIdColNameField.setText(job.getTrackingOrderIdColName());
        trackingNumberColNameField.setText(job.getTrackingNumberColName());

        discountFileNameField.setText(job.getDiscountFileName());
        discountFileDiscountIDColField.setText(job.getDiscountFileIdColumnName());
        discountColumnNameField.setText(job.getDiscountColumnName());

    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {

            job.setJobName(jobNameField.getText());
            job.setJobSupplierName(jobSupplierNameField.getText());
            job.setJobSupplierOrderEmail(jobSupplierOrderEmailField.getText());
            job.setJobYourCompanyName(jobYourCompanyNameField.getText());
            job.setJobYourCompanyEmailAddress(jobYourCompanyEmailAddressField.getText());
            job.setJobYourCompanyEmailIpAddress(jobYourCompanyEmailIpAddressField.getText());
            job.setJobYourCompanyEmailPassword(jobYourCompanyEmailPasswordField.getText());

            job.setDelayDuration(Double.parseDouble(delayDurationField.getText()));
            job.setRepeatInterval(Double.parseDouble(repeatIntervalField.getText()));

            job.setUpdateFileName(updateFileNameField.getText());
            job.setUpdateFileProdIDCol(updateFileProdIDColField.getText());
            job.setUpdateFilePriceCol(updateFilePriceColField.getText());
            job.setUpdateFileMAPPriceCol(updateFileMAPPriceColField.getText());
            job.setUpdateFileQtyCol(updateFileQtyColField.getText());
            job.setUpdateFileProdUpcCol(updateFileProdUpcColField.getText());
            job.setUpdateFileDiscountIDCol(updateFileDiscountIDColField.getText());
            job.setUpdateFileFTPUrl(updateFileFTPUrlField.getText());
            job.setUpdateFileFTPUser(updateFileFTPUserField.getText());
            job.setUpdateFileFTPPass(updateFileFTPPassField.getText());
            job.setUpdateFileEmailFrom(updateFileEmailFromField.getText());
            job.setUpdateFileEmailKeyword(updateFileEmailKeywordField.getText());


            job.setUpdateFileSeparator(Integer.parseInt(updateFileSeparatorField.getText()));
            job.setUpdateFileBeginningRowsToSkip(Integer.parseInt(updateFileBeginningRowsToSkipField.getText()));
            job.setUpdateFileCommentIndicator(Integer.parseInt(updateFileCommentIndicatorField.getText()));
            job.setUpdateFileSetRemovedProdsToPending(updateFileSetRemovedProdsToPendingField.getText());
            job.setUpdateFileUpdateAllProdsToPending(updateFileUpdateAllProdsToPendingField.getText());


            job.setPriceMultiplier(Double.parseDouble(priceMultiplierField.getText()));
            job.setPriceMultiplierMAP(Double.parseDouble(priceMultiplierMAPField.getText()));
            job.setPriceTier1Start(Double.parseDouble(priceTier1StartField.getText()));
            job.setPriceTier1End(Double.parseDouble(priceTier1EndField.getText()));
            job.setPriceTier1Multiplier(Double.parseDouble(priceTier1MultiplierField.getText()));
            job.setPriceTier2Start(Double.parseDouble(priceTier2StartField.getText()));
            job.setPriceTier2End(Double.parseDouble(priceTier2EndField.getText()));
            job.setPriceTier2Multiplier(Double.parseDouble(priceTier2MultiplierField.getText()));
            job.setPriceTier3Start(Double.parseDouble(priceTier3StartField.getText()));
            job.setPriceTier3End(Double.parseDouble(priceTier3EndField.getText()));
            job.setPriceTier3Multiplier(Double.parseDouble(priceTier3MultiplierField.getText()));
            job.setPriceTier4Start(Double.parseDouble(priceTier4StartField.getText()));
            job.setPriceTier4End(Double.parseDouble(priceTier4EndField.getText()));
            job.setPriceTier4Multiplier(Double.parseDouble(priceTier4MultiplierField.getText()));

            job.setDbIPAddress(dbIPAddressField.getText());
            job.setSshUser(sshUserField.getText());
            job.setSshPass(sshPassField.getText());
            job.setDbName(dbNameField.getText());
            job.setDbUser(dbUserField.getText());
            job.setDbPass(dbPassField.getText());

            job.setTrackingEmailFrom(trackingEmailFromField.getText());
            job.setTrackingEmailKeyword(trackingEmailKeywordField.getText());
            job.setTrackingCsvSeparator(Integer.parseInt(trackingCsvSeparatorField.getText()));
            job.setTrackingBeginningRowsToSkip(Integer.parseInt(trackingBeginningRowsToSkipField.getText()));
            job.setTrackingCommentIndicator(Integer.parseInt(trackingCommentIndicatorField.getText()));
            job.setTrackingOrderIdColName(trackingOrderIdColNameField.getText());
            job.setTrackingNumberColName(trackingNumberColNameField.getText());

            job.setDiscountFileName(discountFileNameField.getText());
            job.setDiscountFileIdColumnName(discountFileDiscountIDColField.getText());
            job.setDiscountColumnName(discountColumnNameField.getText());

            okClicked = true;
            dialogStage.close();

        }
    }


    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (jobNameField.getText() == null || jobNameField.getText().length() == 0) {
            errorMessage += "No valid job name value!\n";
        }

        if (jobSupplierNameField.getText() == null || jobSupplierNameField.getText().length() == 0) {
            errorMessage += "No valid supplier name value!\n";
        }

        if (jobSupplierOrderEmailField.getText() == null || jobSupplierOrderEmailField.getText().length() == 0) {
            errorMessage += "No valid supplier order email value!\n";
        }

        if (jobYourCompanyNameField.getText() == null || jobYourCompanyNameField.getText().length() == 0) {
            errorMessage += "No valid your company name value!\n";
        }

        if (jobYourCompanyEmailAddressField.getText() == null || jobYourCompanyEmailAddressField.getText().length() == 0) {
            errorMessage += "No valid your company email address value!\n";
        }

        if (jobYourCompanyEmailPasswordField.getText() == null || jobYourCompanyEmailPasswordField.getText().length() == 0) {
            errorMessage += "No valid your company email password value!\n";
        }

        if (jobYourCompanyEmailIpAddressField.getText() == null || jobYourCompanyEmailIpAddressField.getText().length() == 0) {
            errorMessage += "No valid your company ip address value!\n";
        }




        if (delayDurationField.getText() == null || delayDurationField.getText().length() == 0) {
            errorMessage += "No delay duration value!\n";
        }

        if (repeatIntervalField.getText() == null || repeatIntervalField.getText().length() == 0) {
            errorMessage += "No valid repeat interval value!\n";
        }




        if (updateFileNameField.getText() == null || updateFileNameField.getText().length() == 0) {
            errorMessage += "No valid update file name value!\n";
        }

        if (updateFileProdIDColField.getText() == null || updateFileProdIDColField.getText().length() == 0) {
            errorMessage += "No valid update file prod id col. value!\n";
        }

        if (updateFilePriceColField.getText() == null || updateFilePriceColField.getText().length() == 0) {
            errorMessage += "No valid update file price col. value!\n";
        }

        if (updateFileQtyColField.getText() == null || updateFileQtyColField.getText().length() == 0) {
            errorMessage += "No valid update file qty col. value!\n";
        }

        if (updateFileProdUpcColField.getText() == null || updateFileProdUpcColField.getText().length() == 0) {
            errorMessage += "No valid update file weight col. value!\n";
        }

        /*
        if (updateFileFTPUrlField.getText() == null || updateFileFTPUrlField.getText().length() == 0) {
            errorMessage += "No valid update file ftp url value!\n";
        }

        if (updateFileFTPUserField.getText() == null || updateFileFTPUserField.getText().length() == 0) {
            errorMessage += "No valid update file ftp user value!\n";
        }

        if (updateFileFTPPassField.getText() == null || updateFileFTPPassField.getText().length() == 0) {
            errorMessage += "No valid update file ftp password value!\n";
        }
        */

        if (updateFileSeparatorField.getText() == null || updateFileSeparatorField.getText().length() == 0) {
            errorMessage += "No valid update file separator value!\n";
        }

        if (updateFileBeginningRowsToSkipField.getText() == null || updateFileBeginningRowsToSkipField.getText().length() == 0) {
            errorMessage += "No valid beginning rows to skip value!\n";
        }

        if (updateFileSetRemovedProdsToPendingField.getText() == null || updateFileSetRemovedProdsToPendingField.getText().length() == 0) {
            errorMessage += "No valid set removed prods to pending value!\n";
        }




        if (priceMultiplierField.getText() == null || priceMultiplierField.getText().length() == 0) {
            errorMessage += "No valid price multiplier value!\n";
        }

        if (priceMultiplierMAPField.getText() == null || priceMultiplierMAPField.getText().length() == 0) {
            errorMessage += "No valid MAP price multiplier value!\n";
        }

        if (priceTier1StartField.getText() == null || priceTier1StartField.getText().length() == 0) {
            errorMessage += "No valid price tier 1 start value!\n";
        }

        if (priceTier1EndField.getText() == null || priceTier1EndField.getText().length() == 0) {
            errorMessage += "No valid price tier 1 end value!\n";
        }

        if (priceTier1MultiplierField.getText() == null || priceTier1MultiplierField.getText().length() == 0) {
            errorMessage += "No valid price tier 1 multiplier value!\n";
        }

        if (priceTier2StartField.getText() == null || priceTier2StartField.getText().length() == 0) {
            errorMessage += "No valid price tier 2 start value!\n";
        }

        if (priceTier2EndField.getText() == null || priceTier2EndField.getText().length() == 0) {
            errorMessage += "No valid price tier 2 end value!\n";
        }

        if (priceTier2MultiplierField.getText() == null || priceTier2MultiplierField.getText().length() == 0) {
            errorMessage += "No valid price tier 2 multiplier value!\n";
        }

        if (priceTier3StartField.getText() == null || priceTier3StartField.getText().length() == 0) {
            errorMessage += "No valid price tier 3 start value!\n";
        }

        if (priceTier3EndField.getText() == null || priceTier3EndField.getText().length() == 0) {
            errorMessage += "No valid price tier 3 end value!\n";
        }

        if (priceTier3MultiplierField.getText() == null || priceTier3MultiplierField.getText().length() == 0) {
            errorMessage += "No valid price tier 3 multiplier value!\n";
        }

        if (priceTier4StartField.getText() == null || priceTier4StartField.getText().length() == 0) {
            errorMessage += "No valid price tier 4 start value!\n";
        }

        if (priceTier4EndField.getText() == null || priceTier4EndField.getText().length() == 0) {
            errorMessage += "No valid price tier 4 end value!\n";
        }

        if (priceTier4MultiplierField.getText() == null || priceTier4MultiplierField.getText().length() == 0) {
            errorMessage += "No valid price tier 4 multiplier value!\n";
        }



        if (dbIPAddressField.getText() == null || dbIPAddressField.getText().length() == 0) {
            errorMessage += "No valid remote db ip address value!\n";
        }

        if (sshUserField.getText() == null || sshUserField.getText().length() == 0) {
            errorMessage += "No valid ssh user value!\n";
        }

        if (sshPassField.getText() == null || sshPassField.getText().length() == 0) {
            errorMessage += "No valid ssh password value!\n";
        }

        if (dbNameField.getText() == null || dbNameField.getText().length() == 0) {
            errorMessage += "No valid db name value!\n";
        }

        if (dbUserField.getText() == null || dbUserField.getText().length() == 0) {
            errorMessage += "No valid db user value!\n";
        }

        if (dbPassField.getText() == null || dbPassField.getText().length() == 0) {
            errorMessage += "No valid db password value!\n";
        }



        if (trackingEmailFromField.getText() == null || trackingEmailFromField.getText().length() == 0) {
            errorMessage += "No valid tracking email from value!\n";
        }

        if (trackingEmailKeywordField.getText() == null || trackingEmailKeywordField.getText().length() == 0) {
            errorMessage += "No valid tracking email keyword value!\n";
        }

        if (trackingCsvSeparatorField.getText() == null || trackingCsvSeparatorField.getText().length() == 0) {
            errorMessage += "No valid csv separator value!\n";
        }

        if (trackingBeginningRowsToSkipField.getText() == null || trackingBeginningRowsToSkipField.getText().length() == 0) {
            errorMessage += "No valid tracking beginning rows to skip value!\n";
        }

        if (trackingOrderIdColNameField.getText() == null || trackingOrderIdColNameField.getText().length() == 0) {
            errorMessage += "No valid tracking order id col. name value!\n";
        }

        if (trackingNumberColNameField.getText() == null || trackingNumberColNameField.getText().length() == 0) {
            errorMessage += "No valid tracking number col. name value!\n";
        }


        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
}
