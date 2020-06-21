package com.powerdropship.powerdropship;


import com.powerdropship.powerdropship.model.Job;
import com.powerdropship.powerdropship.model.JobListWrapper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.prefs.Preferences;


public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    /**
     * The data as an observable list of Jobs.
     */
    private ObservableList<Job> jobData = FXCollections.observableArrayList();

    String powerdropshipDir = System.getProperty("user.home") + "/PowerDropship";

    /**
     * Constructor
     */
    public MainApp() {
    	
    	
    	System.out.println("Console is: " + System.console());
    	
        // Add some sample data
        jobData.add(new Job("Test Job")); 
        try {
            Files.createDirectories(Paths.get(powerdropshipDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the data as an observable list of Jobs.
     *
     * @return
     */
    public ObservableList<Job> getJobData() {
        return jobData;
    }


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("PowerDropship");
        
        initRootLayout();

        showJobOverview();

    }

    /**
     * Initializes the root layout and tries to load the last opened
     * job file.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            // Give the controller access to the main app.
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Try to load last opened job file.
        File file = getJobFilePath();
        if (file != null) {
            loadJobDataFromFile(file);
        }
    }

    /**
     * Shows the job overview inside the root layout.
     */
    public void showJobOverview() {
        try {
            // Load job overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/JobOverview.fxml"));
            HBox jobOverview = loader.load();

            // Set job overview into the center of root layout.
            rootLayout.setCenter(jobOverview);

            // Give the controller access to the main app.
            JobOverviewController controller = loader.getController();
            controller.setMainApp(this);

            controller.print("* Hello!  v1.00 * \n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the main stage.
     *
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {

        launch(args);

    }

    /**
     * Opens a dialog to edit details for the specified job. If the user
     * clicks OK, the changes are saved into the provided job object and true
     * is returned.
     *
     * @param job the job object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showJobEditDialog(Job job) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/JobEditDialog.fxml"));
            AnchorPane page = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Job");
            dialogStage.initModality(Modality.NONE);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the job into the controller.
            JobEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setJob(job);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the job file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     *
     * @return
     */
    public File getJobFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String filePath = prefs.get(powerdropshipDir, null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * Sets the file path of the currently loaded file. The path is persisted in
     * the OS specific registry.
     *
     * @param file the file or null to remove the path
     */
    public void setJobFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (file != null) {
            prefs.put(powerdropshipDir, file.getPath());

            primaryStage.setTitle("PowerDropship - " + file.getName());
        } else {
            prefs.remove(powerdropshipDir);

            primaryStage.setTitle("PowerDropship");
        }
    }

    /**
     * Loads job data from the specified file. The current job data will
     * be replaced.
     *
     * @param file
     */
    public void loadJobDataFromFile(File file) {
        try {

            JAXBContext context = JAXBContext.newInstance(JobListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();

            // Reading XML from the file and unmarshalling.
            JobListWrapper wrapper = (JobListWrapper) um.unmarshal(file);

            jobData.clear();
            jobData.addAll(wrapper.getJobs());

            // Save the file path to the registry.
            setJobFilePath(file);

        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    /**
     * Saves the current job data to the specified file.
     *
     * @param file
     */
    public void saveJobDataToFile(File file) {
        try {

            JAXBContext context = JAXBContext.newInstance(JobListWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Wrapping our job data.
            JobListWrapper wrapper = new JobListWrapper();
            wrapper.setJobs(jobData);

            // Marshalling and saving XML to the file.
            m.marshal(wrapper, file);

            // Save the file path to the registry.
            setJobFilePath(file);

        } catch (Exception e) { // catches ANY exception
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }
}
