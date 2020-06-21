package com.powerdropship.powerdropship.model;

import javafx.beans.property.*;
import javafx.concurrent.ScheduledService;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;

import javax.xml.bind.annotation.XmlTransient;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Model class for a Job.
 */

public class Job {

    private final StringProperty jobName;
    
    private final StringProperty jobSupplierName;

    private final StringProperty jobSupplierOrderEmail;

    private final StringProperty jobYourCompanyName;

    private final StringProperty jobYourCompanyEmailAddress;

    private final StringProperty jobYourCompanyEmailIpAddress;

    private final StringProperty jobYourCompanyEmailPassword;


    private final StringProperty updateFileName;

    private final StringProperty updateFileLocalDir;

    private final StringProperty updateFileProdIDCol;

    private final StringProperty updateFilePriceCol;

    private final StringProperty updateFileMAPPriceCol;

    private final StringProperty updateFileQtyCol;

    private final StringProperty updateFileDiscountIDCol;

    private final StringProperty updateFileProdUpcCol;


    private final StringProperty updateFileFTPUrl;

    private final StringProperty updateFileFTPUser;

    private final StringProperty updateFileFTPPass;

    private final IntegerProperty updateFileSeparator;

    private final IntegerProperty updateFileBeginningRowsToSkip;

    private final IntegerProperty updateFileCommentIndicator;

    private final StringProperty updateFileSetRemovedProdsToPending;

    private final StringProperty updateFileUpdateAllProdsToPending;

    private final StringProperty updateFileEmailFrom;

    private final StringProperty updateFileEmailKeyword;


    private final DoubleProperty priceMultiplier;

    private final DoubleProperty priceMultiplierMAP;

    private final DoubleProperty priceTier1Start;

    private final DoubleProperty priceTier1End;

    private final DoubleProperty priceTier1Multiplier;

    private final DoubleProperty priceTier2Start;

    private final DoubleProperty priceTier2End;

    private final DoubleProperty priceTier2Multiplier;

    private final DoubleProperty priceTier3Start;

    private final DoubleProperty priceTier3End;

    private final DoubleProperty priceTier3Multiplier;

    private final DoubleProperty priceTier4Start;

    private final DoubleProperty priceTier4End;

    private final DoubleProperty priceTier4Multiplier;


    private final DoubleProperty delayDuration;

    private final DoubleProperty repeatInterval;


    private final StringProperty dbIPAddress;
    
    private final StringProperty sshUser;

    private final StringProperty sshPass;

    private final StringProperty dbName;

    private final StringProperty dbUser;

    private final StringProperty dbPass;


    private final StringProperty trackingEmailFrom;

    private final StringProperty trackingEmailKeyword;

    private final IntegerProperty trackingCsvSeparator;

    private final IntegerProperty trackingBeginningRowsToSkip;

    private final IntegerProperty trackingCommentIndicator;

    private final StringProperty trackingOrderIdColName;

    private final StringProperty trackingNumberColName;


    private final StringProperty discountFileName;

    private final StringProperty discountFileIdColumnName;

    private final StringProperty discountColumnName;


    //Holds the reference to the job instance, so that the job can be cancelled
    // when passing the job to jobOverviewController.cancelService
    private ScheduledService<Job> serviceInstanceReference;

    //ArrayList of orders. It is deleted at the start of the job, because there is no data that needs to be saved for the
    // next iteration.
    @XmlTransient
    public ArrayList<Order> listOfOrders = new ArrayList<Order>();

    //This is a File because it is set by JobOverview controller which uses user.home to set it which is a File function.
    @XmlTransient
    public File supplierDir;

    @XmlTransient
    public File discountFile;

    @XmlTransient
    public File updateFile;

    @XmlTransient
    public File trackingDir;

    @XmlTransient
    public File trackingFile;

    @XmlTransient
    public File orderDir;


    @XmlTransient
    public TextArea textArea;



    /**
     * Default constructor.
     */

    public Job() {
        this(null);
    }

    /**
     * Constructor with some initial data.
     *
     * @param jobName
     */

    public Job(String jobName) {
        this.jobName = new SimpleStringProperty(jobName);

    // Some initial dummy data, just for convenient testing.
        
        this.jobSupplierName = new SimpleStringProperty("MySupplier");
        this.jobSupplierOrderEmail = new SimpleStringProperty("supplieremail@mysupplier1.com");
        this.jobYourCompanyName = new SimpleStringProperty("MyCompany");
        this.jobYourCompanyEmailAddress = new SimpleStringProperty("mycompanyemail@mycompany.com");
        this.jobYourCompanyEmailPassword = new SimpleStringProperty("123456");
        this.jobYourCompanyEmailIpAddress = new SimpleStringProperty("10.10.10.10");

        this.delayDuration = new SimpleDoubleProperty(.01);
        this.repeatInterval = new SimpleDoubleProperty (720);

        this.updateFileName = new SimpleStringProperty("prodlistLong.csv");
        this.updateFileLocalDir = new SimpleStringProperty("/Users/user/Desktop/jobs");
        this.updateFileProdIDCol = new SimpleStringProperty("PETRA SKU");
        this.updateFileQtyCol = new SimpleStringProperty("AVAILABLE");
        this.updateFilePriceCol = new SimpleStringProperty("PRICE");
        this.updateFileMAPPriceCol = new SimpleStringProperty("MAP");
        this.updateFileProdUpcCol = new SimpleStringProperty("WEIGHT-UNPACKED");
        this.updateFileDiscountIDCol = new SimpleStringProperty("DISCOUNT ID");
        this.updateFileFTPUrl = new SimpleStringProperty("ftp.powerdropship.com");
        this.updateFileFTPUser = new SimpleStringProperty("testing1@powerdropship.com");
        this.updateFileFTPPass = new SimpleStringProperty("!Testing1!!");
        this.updateFileEmailKeyword = new SimpleStringProperty("email@where_update_file_is");
        this.updateFileEmailFrom = new SimpleStringProperty("update file email search keyword");
        this.updateFileSeparator = new SimpleIntegerProperty(44);
        this.updateFileBeginningRowsToSkip = new SimpleIntegerProperty(2);
        this.updateFileCommentIndicator = new SimpleIntegerProperty(42);
        this.updateFileSetRemovedProdsToPending = new SimpleStringProperty("false");
        this.updateFileUpdateAllProdsToPending = new SimpleStringProperty("false");


        this.priceMultiplier = new SimpleDoubleProperty (1.0);
        this.priceMultiplierMAP = new SimpleDoubleProperty (1.0);
        this.priceTier1Start = new SimpleDoubleProperty (0);
        this.priceTier1End = new SimpleDoubleProperty (0);
        this.priceTier1Multiplier = new SimpleDoubleProperty (0);
        this.priceTier2Start = new SimpleDoubleProperty (0);
        this.priceTier2End = new SimpleDoubleProperty (0);
        this.priceTier2Multiplier = new SimpleDoubleProperty (0);
        this.priceTier3Start = new SimpleDoubleProperty (0);
        this.priceTier3End = new SimpleDoubleProperty (0);
        this.priceTier3Multiplier = new SimpleDoubleProperty (0);
        this.priceTier4Start = new SimpleDoubleProperty (0);
        this.priceTier4End = new SimpleDoubleProperty (0);
        this.priceTier4Multiplier = new SimpleDoubleProperty (0);

        this.dbIPAddress = new SimpleStringProperty("10.10.10.10");
        this.sshUser = new SimpleStringProperty("sshUser");
        this.sshPass = new SimpleStringProperty("12345678");
        this.dbName = new SimpleStringProperty("myshopdb_wp1");
        this.dbUser = new SimpleStringProperty("myshopdb_wp1");
        this.dbPass = new SimpleStringProperty("123456789");

        this.trackingEmailFrom = new SimpleStringProperty("supplieremail@mysupplier1.com");
        this.trackingEmailKeyword = new SimpleStringProperty("shipping");
        this.trackingCsvSeparator = new SimpleIntegerProperty(44);
        this.trackingBeginningRowsToSkip = new SimpleIntegerProperty(0);
        this.trackingCommentIndicator = new SimpleIntegerProperty(42);
        this.trackingOrderIdColName = new SimpleStringProperty("CUST PO");
        this.trackingNumberColName = new SimpleStringProperty("TRACKING#s");

        this.discountFileName = new SimpleStringProperty("discount_file.txt");
        this.discountFileIdColumnName = new SimpleStringProperty("vendor_name");
        this.discountColumnName = new SimpleStringProperty("discount");
    }

    public String getJobName() {
        return jobName.get();
    }

    public StringProperty jobNameProperty() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName.set(jobName);
    }

    public String getJobSupplierName() {
        return jobSupplierName.get();
    }

    public StringProperty jobSupplierNameProperty() {
        return jobSupplierName;
    }

    public void setJobSupplierName(String jobSupplierName) {
        this.jobSupplierName.set(jobSupplierName);
    }

    public String getJobSupplierOrderEmail() {
        return jobSupplierOrderEmail.get();
    }

    public StringProperty jobSupplierOrderEmailProperty() {
        return jobSupplierOrderEmail;
    }

    public void setJobSupplierOrderEmail(String jobSupplierOrderEmail) {
        this.jobSupplierOrderEmail.set(jobSupplierOrderEmail);
    }

    public String getJobYourCompanyName() {
        return jobYourCompanyName.get();
    }

    public StringProperty jobYourCompanyNameProperty() {
        return jobYourCompanyName;
    }

    public void setJobYourCompanyName(String jobYourCompanyName) {
        this.jobYourCompanyName.set(jobYourCompanyName);
    }

    public String getJobYourCompanyEmailAddress() {
        return jobYourCompanyEmailAddress.get();
    }

    public StringProperty jobYourCompanyEmailAddressProperty() {
        return jobYourCompanyEmailAddress;
    }

    public void setJobYourCompanyEmailAddress(String jobYourCompanyEmailAddress) {
        this.jobYourCompanyEmailAddress.set(jobYourCompanyEmailAddress);
    }

    public String getJobYourCompanyEmailIpAddress() {
        return jobYourCompanyEmailIpAddress.get();
    }

    public StringProperty jobYourCompanyEmailIpAddressProperty() {
        return jobYourCompanyEmailIpAddress;
    }

    public void setJobYourCompanyEmailIpAddress(String jobYourCompanyEmailIpAddress) {
        this.jobYourCompanyEmailIpAddress.set(jobYourCompanyEmailIpAddress);
    }

    public String getJobYourCompanyEmailPassword() {
        return jobYourCompanyEmailPassword.get();
    }

    public StringProperty jobYourCompanyEmailPasswordProperty() {
        return jobYourCompanyEmailPassword;
    }

    public void setJobYourCompanyEmailPassword(String jobYourCompanyEmailPassword) {
        this.jobYourCompanyEmailPassword.set(jobYourCompanyEmailPassword);
    }

    public String getUpdateFileName() {
        return updateFileName.get();
    }

    public StringProperty updateFileNameProperty() {
        return updateFileName;
    }

    public void setUpdateFileName(String updateFileName) {
        this.updateFileName.set(updateFileName);
    }

    public String getUpdateFileLocalDir() {
        return updateFileLocalDir.get();
    }

    public StringProperty updateFileLocalDirProperty() {
        return updateFileLocalDir;
    }

    public void setUpdateFileLocalDir(String updateFileLocalDir) {
        this.updateFileLocalDir.set(updateFileLocalDir);
    }

    public String getUpdateFileProdIDCol() {
        return updateFileProdIDCol.get();
    }

    public StringProperty updateFileProdIDColProperty() {
        return updateFileProdIDCol;
    }

    public void setUpdateFileProdIDCol(String updateFileProdIDCol) {
        this.updateFileProdIDCol.set(updateFileProdIDCol);
    }

    public String getUpdateFilePriceCol() {
        return updateFilePriceCol.get();
    }

    public StringProperty updateFilePriceColProperty() {
        return updateFilePriceCol;
    }

    public void setUpdateFilePriceCol(String updateFilePriceCol) {
        this.updateFilePriceCol.set(updateFilePriceCol);
    }

    public String getUpdateFileMAPPriceCol() {
        return updateFileMAPPriceCol.get();
    }

    public StringProperty updateFileMAPPriceColProperty() {
        return updateFileMAPPriceCol;
    }

    public void setUpdateFileMAPPriceCol(String updateFileMAPPriceCol) {
        this.updateFileMAPPriceCol.set(updateFileMAPPriceCol);
    }

    public String getUpdateFileQtyCol() {
        return updateFileQtyCol.get();
    }

    public StringProperty updateFileQtyColProperty() {
        return updateFileQtyCol;
    }

    public void setUpdateFileQtyCol(String updateFileQtyCol) {
        this.updateFileQtyCol.set(updateFileQtyCol);
    }

    public String getUpdateFileProdUpcCol() {
        return updateFileProdUpcCol.get();
    }

    public StringProperty updateFileProdUpcColProperty() {
        return updateFileProdUpcCol;
    }

    public void setUpdateFileProdUpcCol(String updateFileProdUpcCol) {
        this.updateFileProdUpcCol.set(updateFileProdUpcCol);
    }

    public String getUpdateFileDiscountIDCol() {
        return updateFileDiscountIDCol.get();
    }

    public StringProperty updateFileDiscountIDColProperty() {
        return updateFileDiscountIDCol;
    }

    public void setUpdateFileDiscountIDCol(String updateFileDiscountIDCol) {
        this.updateFileDiscountIDCol.set(updateFileDiscountIDCol);
    }

    public String getUpdateFileFTPUrl() {
        return updateFileFTPUrl.get();
    }

    public StringProperty updateFileFTPUrlProperty() {
        return updateFileFTPUrl;
    }

    public void setUpdateFileFTPUrl(String updateFileFTPUrl) {
        this.updateFileFTPUrl.set(updateFileFTPUrl);
    }

    public String getUpdateFileFTPUser() {
        return updateFileFTPUser.get();
    }

    public StringProperty updateFileFTPUserProperty() {
        return updateFileFTPUser;
    }

    public void setUpdateFileFTPUser(String updateFileFTPUser) {
        this.updateFileFTPUser.set(updateFileFTPUser);
    }

    public String getUpdateFileFTPPass() {
        return updateFileFTPPass.get();
    }

    public StringProperty updateFileFTPPassProperty() {
        return updateFileFTPPass;
    }

    public void setUpdateFileFTPPass(String updateFileFTPPass) {
        this.updateFileFTPPass.set(updateFileFTPPass);
    }

    public int getUpdateFileSeparator() {
        return updateFileSeparator.get();
    }

    public IntegerProperty updateFileSeparatorProperty() {
        return updateFileSeparator;
    }

    public void setUpdateFileSeparator(int updateFileSeparator) {
        this.updateFileSeparator.set(updateFileSeparator);
    }

    public int getUpdateFileBeginningRowsToSkip() {
        return updateFileBeginningRowsToSkip.get();
    }

    public IntegerProperty updateFileBeginningRowsToSkipProperty() {
        return updateFileBeginningRowsToSkip;
    }

    public void setUpdateFileBeginningRowsToSkip(int updateFileBeginningRowsToSkip) {
        this.updateFileBeginningRowsToSkip.set(updateFileBeginningRowsToSkip);
    }

    public int getUpdateFileCommentIndicator() {
        return updateFileCommentIndicator.get();
    }

    public IntegerProperty updateFileCommentIndicatorProperty() {
        return updateFileCommentIndicator;
    }

    public void setUpdateFileCommentIndicator(int updateFileCommentIndicator) {
        this.updateFileCommentIndicator.set(updateFileCommentIndicator);
    }

    public String getUpdateFileSetRemovedProdsToPending() {
        return updateFileSetRemovedProdsToPending.get();
    }

    public StringProperty updateFileSetRemovedProdsToPendingProperty() {
        return updateFileSetRemovedProdsToPending;
    }

    public void setUpdateFileSetRemovedProdsToPending(String updateFileSetRemovedProdsToPending) {
        this.updateFileSetRemovedProdsToPending.set(updateFileSetRemovedProdsToPending);
    }

    public String getUpdateFileUpdateAllProdsToPending() {
        return updateFileUpdateAllProdsToPending.get();
    }

    public StringProperty updateFileUpdateAllProdsToPendingProperty() {
        return updateFileUpdateAllProdsToPending;
    }

    public void setUpdateFileUpdateAllProdsToPending(String updateFileUpdateAllProdsToPending) {
        this.updateFileUpdateAllProdsToPending.set(updateFileUpdateAllProdsToPending);
    }

    public double getPriceMultiplier() {
        return priceMultiplier.get();
    }

    public DoubleProperty priceMultiplierProperty() {
        return priceMultiplier;
    }

    public void setPriceMultiplier(double priceMultiplier) {
        this.priceMultiplier.set(priceMultiplier);
    }

    public double getPriceMultiplierMAP() {
        return priceMultiplierMAP.get();
    }

    public DoubleProperty priceMultiplierMAPProperty() {
        return priceMultiplierMAP;
    }

    public void setPriceMultiplierMAP(double priceMultiplierMAP) {
        this.priceMultiplierMAP.set(priceMultiplierMAP);
    }

    public double getPriceTier1Start() {
        return priceTier1Start.get();
    }

    public DoubleProperty priceTier1StartProperty() {
        return priceTier1Start;
    }

    public void setPriceTier1Start(double priceTier1Start) {
        this.priceTier1Start.set(priceTier1Start);
    }

    public double getPriceTier1End() {
        return priceTier1End.get();
    }

    public DoubleProperty priceTier1EndProperty() {
        return priceTier1End;
    }

    public void setPriceTier1End(double priceTier1End) {
        this.priceTier1End.set(priceTier1End);
    }

    public double getPriceTier1Multiplier() {
        return priceTier1Multiplier.get();
    }

    public DoubleProperty priceTier1MultiplierProperty() {
        return priceTier1Multiplier;
    }

    public void setPriceTier1Multiplier(double priceTier1Multiplier) {
        this.priceTier1Multiplier.set(priceTier1Multiplier);
    }

    public double getPriceTier2Start() {
        return priceTier2Start.get();
    }

    public DoubleProperty priceTier2StartProperty() {
        return priceTier2Start;
    }

    public void setPriceTier2Start(double priceTier2Start) {
        this.priceTier2Start.set(priceTier2Start);
    }

    public double getPriceTier2End() {
        return priceTier2End.get();
    }

    public DoubleProperty priceTier2EndProperty() {
        return priceTier2End;
    }

    public void setPriceTier2End(double priceTier2End) {
        this.priceTier2End.set(priceTier2End);
    }

    public double getPriceTier2Multiplier() {
        return priceTier2Multiplier.get();
    }

    public DoubleProperty priceTier2MultiplierProperty() {
        return priceTier2Multiplier;
    }

    public void setPriceTier2Multiplier(double priceTier2Multiplier) {
        this.priceTier2Multiplier.set(priceTier2Multiplier);
    }

    public double getPriceTier3Start() {
        return priceTier3Start.get();
    }

    public DoubleProperty priceTier3StartProperty() {
        return priceTier3Start;
    }

    public void setPriceTier3Start(double priceTier3Start) {
        this.priceTier3Start.set(priceTier3Start);
    }

    public double getPriceTier3End() {
        return priceTier3End.get();
    }

    public DoubleProperty priceTier3EndProperty() {
        return priceTier3End;
    }

    public void setPriceTier3End(double priceTier3End) {
        this.priceTier3End.set(priceTier3End);
    }

    public double getPriceTier3Multiplier() {
        return priceTier3Multiplier.get();
    }

    public DoubleProperty priceTier3MultiplierProperty() {
        return priceTier3Multiplier;
    }

    public void setPriceTier3Multiplier(double priceTier3Multiplier) {
        this.priceTier3Multiplier.set(priceTier3Multiplier);
    }

    public double getPriceTier4Start() {
        return priceTier4Start.get();
    }

    public DoubleProperty priceTier4StartProperty() {
        return priceTier4Start;
    }

    public void setPriceTier4Start(double priceTier4Start) {
        this.priceTier4Start.set(priceTier4Start);
    }

    public double getPriceTier4End() {
        return priceTier4End.get();
    }

    public DoubleProperty priceTier4EndProperty() {
        return priceTier4End;
    }

    public void setPriceTier4End(double priceTier4End) {
        this.priceTier4End.set(priceTier4End);
    }

    public double getPriceTier4Multiplier() {
        return priceTier4Multiplier.get();
    }

    public DoubleProperty priceTier4MultiplierProperty() {
        return priceTier4Multiplier;
    }

    public void setPriceTier4Multiplier(double priceTier4Multiplier) {
        this.priceTier4Multiplier.set(priceTier4Multiplier);
    }

    public double getDelayDuration() {
        return delayDuration.get();
    }

    public DoubleProperty delayDurationProperty() {
        return delayDuration;
    }

    public void setDelayDuration(double delayDuration) {
        this.delayDuration.set(delayDuration);
    }

    public double getRepeatInterval() {
        return repeatInterval.get();
    }

    public DoubleProperty repeatIntervalProperty() {
        return repeatInterval;
    }

    public void setRepeatInterval(double repeatInterval) {
        this.repeatInterval.set(repeatInterval);
    }

    public String getDbIPAddress() {
        return dbIPAddress.get();
    }

    public StringProperty dbIPAddressProperty() {
        return dbIPAddress;
    }

    public void setDbIPAddress(String dbIPAddress) {
        this.dbIPAddress.set(dbIPAddress);
    }

    public String getSshUser() {
        return sshUser.get();
    }

    public StringProperty sshUserProperty() {
        return sshUser;
    }

    public void setSshUser(String sshUser) {
        this.sshUser.set(sshUser);
    }

    public String getSshPass() {
        return sshPass.get();
    }

    public StringProperty sshPassProperty() {
        return sshPass;
    }

    public void setSshPass(String sshPass) {
        this.sshPass.set(sshPass);
    }

    public String getDbName() {
        return dbName.get();
    }

    public StringProperty dbNameProperty() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName.set(dbName);
    }

    public String getDbUser() {
        return dbUser.get();
    }

    public StringProperty dbUserProperty() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser.set(dbUser);
    }

    public String getDbPass() {
        return dbPass.get();
    }

    public StringProperty dbPassProperty() {
        return dbPass;
    }

    public void setDbPass(String dbPass) {
        this.dbPass.set(dbPass);
    }

    public String getTrackingEmailFrom() {
        return trackingEmailFrom.get();
    }

    public StringProperty trackingEmailFromProperty() {
        return trackingEmailFrom;
    }

    public void setTrackingEmailFrom(String trackingEmailFrom) {
        this.trackingEmailFrom.set(trackingEmailFrom);
    }

    public String getTrackingEmailKeyword() {
        return trackingEmailKeyword.get();
    }

    public StringProperty trackingEmailKeywordProperty() {
        return trackingEmailKeyword;
    }

    public void setTrackingEmailKeyword(String trackingEmailKeyword) {
        this.trackingEmailKeyword.set(trackingEmailKeyword);
    }

    public int getTrackingCsvSeparator() {
        return trackingCsvSeparator.get();
    }

    public IntegerProperty trackingCsvSeparatorProperty() {
        return trackingCsvSeparator;
    }

    public void setTrackingCsvSeparator(int trackingCsvSeparator) {
        this.trackingCsvSeparator.set(trackingCsvSeparator);
    }

    public int getTrackingBeginningRowsToSkip() {
        return trackingBeginningRowsToSkip.get();
    }

    public IntegerProperty trackingBeginningRowsToSkipProperty() {
        return trackingBeginningRowsToSkip;
    }

    public void setTrackingBeginningRowsToSkip(int trackingBeginningRowsToSkip) {
        this.trackingBeginningRowsToSkip.set(trackingBeginningRowsToSkip);
    }

    public int getTrackingCommentIndicator() {
        return trackingCommentIndicator.get();
    }

    public IntegerProperty trackingCommentIndicatorProperty() {
        return trackingCommentIndicator;
    }

    public void setTrackingCommentIndicator(int trackingCommentIndicator) {
        this.trackingCommentIndicator.set(trackingCommentIndicator);
    }

    public String getTrackingOrderIdColName() {
        return trackingOrderIdColName.get();
    }

    public StringProperty trackingOrderIdColNameProperty() {
        return trackingOrderIdColName;
    }

    public void setTrackingOrderIdColName(String trackingOrderIdColName) {
        this.trackingOrderIdColName.set(trackingOrderIdColName);
    }

    public String getTrackingNumberColName() {
        return trackingNumberColName.get();
    }

    public StringProperty trackingNumberColNameProperty() {
        return trackingNumberColName;
    }

    public void setTrackingNumberColName(String trackingNumberColName) {
        this.trackingNumberColName.set(trackingNumberColName);
    }

    @XmlTransient
    public ScheduledService<Job> getServiceInstanceReference() {
        return serviceInstanceReference;
    }

    public void setServiceInstanceReference(ScheduledService<Job> serviceInstanceReference) {
        this.serviceInstanceReference = serviceInstanceReference;
    }

    public File getSupplierDir() {
        return supplierDir;
    }

    public void setSupplierDir(File supplierDir) {
        this.supplierDir = supplierDir;
    }


    public String getDiscountFileName() {
        return discountFileName.get();
    }

    public StringProperty discountFileNameProperty() {
        return discountFileName;
    }

    public void setDiscountFileName(String discountFileName) {
        this.discountFileName.set(discountFileName);
    }

    public String getDiscountFileIdColumnName() {
        return discountFileIdColumnName.get();
    }

    public StringProperty discountFileIdColumnNameProperty() {
        return discountFileIdColumnName;
    }

    public void setDiscountFileIdColumnName(String discountFileIdColumnName) {
        this.discountFileIdColumnName.set(discountFileIdColumnName);
    }

    public String getDiscountColumnName() {
        return discountColumnName.get();
    }

    public StringProperty discountColumnNameProperty() {
        return discountColumnName;
    }

    public void setDiscountColumnName(String discountColumnName) {
        this.discountColumnName.set(discountColumnName);
    }

    public File getDiscountFile() {
        return discountFile;
    }

    public void setDiscountFile(File discountFile) {
        this.discountFile = discountFile;
    }

    public File getUpdateFile() {
        return updateFile;
    }

    public void setUpdateFile(File updateFile) {
        this.updateFile = updateFile;
    }

    public File getTrackingDir() {
        return trackingDir;
    }

    public void setTrackingDir(File trackingDir) {
        this.trackingDir = trackingDir;
    }

    public File getTrackingFile() {
        return trackingFile;
    }

    public void setTrackingFile(File trackingFile) {
        this.trackingFile = trackingFile;
    }

    @XmlTransient
    public File getOrderDir() {
        return orderDir;
    }

    public void setOrderDir(File orderDir) {
        this.orderDir = orderDir;
    }

    public String getUpdateFileEmailKeyword() {
        return updateFileEmailKeyword.get();
    }

    public StringProperty updateFileEmailKeywordProperty() {
        return updateFileEmailKeyword;
    }

    public void setUpdateFileEmailKeyword(String updateFileEmailKeyword) {
        this.updateFileEmailKeyword.set(updateFileEmailKeyword);
    }

    public String getUpdateFileEmailFrom() {
        return updateFileEmailFrom.get();
    }

    public StringProperty updateFileEmailFromProperty() {
        return updateFileEmailFrom;
    }

    public void setUpdateFileEmailFrom(String updateFileEmailFrom) {
        this.updateFileEmailFrom.set(updateFileEmailFrom);
    }

    @XmlTransient
    public ArrayList<Order> getListOfOrders() {
        return listOfOrders;
    }

    public void setListOfOrders(ArrayList<Order> listOfOrders) {
        this.listOfOrders = listOfOrders;
    }

    @XmlTransient
    public TextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
    }

}
