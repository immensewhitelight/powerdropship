package com.powerdropship.powerdropship;


import com.powerdropship.powerdropship.model.Job;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import javafx.application.Platform;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ThreadLocalRandom;


public class DbConnect {

    public DbConnect(JobOverviewController jobOverviewController) {
        this.jobOverviewController = jobOverviewController;
    }

    // Reference to the jobOverviewController.
    private JobOverviewController jobOverviewController;

    /**
     * Is called by jobOverviewController to give a reference back to itself.
     *
     * @param jobOverviewController
     */
    public void setJobOverviewController(JobOverviewController jobOverviewController) {
        this.jobOverviewController = jobOverviewController;
    }


    public Connection directDbConnect(Job job) {

        System.out.println(" Trying connection to Woocommerce db. ");
        Platform.runLater(() -> job.getTextArea().appendText("Enter connect to db" + System.getProperty("line.separator")));


        String host = job.getDbIPAddress();

        int remote_port = 3306;
        String db = job.getDbName();
        String dbUser = job.getDbUser();
        String dbPasswd = job.getDbPass();

        Connection con = null;
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://" + host + ":" + remote_port + "/";
        String allowMultiQueries = "?allowMultiQueries=true";


        try {

            Class.forName(driver);
            con = DriverManager.getConnection(url + db + allowMultiQueries, dbUser, dbPasswd);

            if (con.isValid(10)) {
                // Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Connected to Woocommerce. " + System.getProperty("line.separator")));
                System.out.println(" Connected to Woocommerce for update. ");
            }

        } catch (Exception e) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Connect to Woocommerce for update error. " + e.getMessage() + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            e.printStackTrace();
        }

        Platform.runLater(() -> job.getTextArea().appendText("Exit connect to db" + System.getProperty("line.separator")));

        return con;
    }


    public Connection sshConnect(Job job, int jobDataSize) {

        //Use ssh to connect to the remote server.
        //System.out.println(" Trying connection to remote server ");

        Platform.runLater(() -> job.getTextArea().appendText("Enter connect to db using ssh" + System.getProperty("line.separator")));

        int lport = 0;
        String rhost = "host";
        int rport = 0;

        int rand = jobDataSize + ThreadLocalRandom.current().nextInt(5, 60000);

        String user = job.getSshUser();
        String password = job.getSshPass();
        String host = job.getDbIPAddress();

        int port = 22;

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            lport = 5535 + rand;
            rhost = "localhost";
            rport = 3306;
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            int assigned_port = session.setPortForwardingL(lport, rhost, rport);
            final String finalRhost = rhost;
            final int finalRport = rport;
            //Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Local port: " + assigned_port + " -> " + "Remote port: " + finalRport + System.getProperty("line.separator")));
            System.out.println(" Local port: " + assigned_port + " -> " + "Remote port: " + finalRport);

            if (session.isConnected()) {
                // Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Connected to remote server." + System.getProperty("line.separator")));
                System.out.println(" Connected to remote cart server for update ");
            }

        } catch (Exception e) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Connect to remote server for update error. " + e.getMessage() + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job in a few moments..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            e.printStackTrace();
        }


        //Connect to the cart db
        //Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Trying connection to Woocommerce." + System.getProperty("line.separator")));
        System.out.println(" Trying connection to Woocommerce for update. ");

        Connection con = null;
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://" + rhost + ":" + lport + "/";
        String allowMultiQueries = "?allowMultiQueries=true";
        String db = job.getDbName();
        String dbUser = job.getDbUser();
        String dbPasswd = job.getDbPass();

        try {

            Class.forName(driver);
            con = DriverManager.getConnection(url + db + allowMultiQueries, dbUser, dbPasswd);

            if (con.isValid(10)) {
                // Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Connected to Woocommerce. " + System.getProperty("line.separator")));
                System.out.println(" Connected to Woocommerce for update. ");
            }

        } catch (Exception e) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Connect to Woocommerce for update error. " + e.getMessage() + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            e.printStackTrace();
        }

        //Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Connected to remote server " + System.getProperty("line.separator")));

        Platform.runLater(() -> job.getTextArea().appendText("Exit connect to db using ssh" + System.getProperty("line.separator")));

        return con;
    }


    public Connection sshConnectToSky43(Job job, int jobDataSize) {

        //Use ssh to connect to the sky43 server.
        //System.out.println(" Trying connection to remote server ");

        int lport = 0;
        String rhost = "host";
        int rport = 0;

        int rand = jobDataSize + ThreadLocalRandom.current().nextInt(5, 60000);

        String user = "dropship";
        String password = "adfasf9874!!W";
        String host = "107.180.2.29";

        int port = 22;

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            lport = 5535 + rand;
            rhost = "localhost";
            rport = 3306;
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            int assigned_port = session.setPortForwardingL(lport, rhost, rport);
            final String finalRhost = rhost;
            final int finalRport = rport;
            //Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Local port: " + assigned_port + " -> " + "Remote port: " + finalRport + System.getProperty("line.separator")));
            System.out.println(" Local port: " + assigned_port + " -> " + "Remote port: " + finalRport);

            if (session.isConnected()) {
                // Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Connected to remote server." + System.getProperty("line.separator")));
                System.out.println(" Connected to remote server for sky43. ");
            }

        } catch (Exception e) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Connect to remote server for sky43 error. " + e.getMessage() + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job in a few moments..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            e.printStackTrace();
        }


        //Connect to the cart db
        //Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Trying connection to Woocommerce." + System.getProperty("line.separator")));
        System.out.println(" Trying connection to sky43 db ");

        Connection sky43Con = null;
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://" + rhost + ":" + lport + "/";
        String allowMultiQueries = "?allowMultiQueries=true";
        String db = "dropship_sky43";
        String dbUser = "dropship_sky43";
        String dbPasswd = "p.JSGf]6Wv.c";

        try {

            Class.forName(driver);
            sky43Con = DriverManager.getConnection(url + db + allowMultiQueries, dbUser, dbPasswd);

            if (sky43Con.isValid(10)) {
                // Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Connected to Woocommerce. " + System.getProperty("line.separator")));
                System.out.println(" Connected to sky43 ");
            }

        } catch (Exception e) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Connect to sky43 error " + e.getMessage() + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            e.printStackTrace();
        }

        //Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Connected to remote server " + System.getProperty("line.separator")));

        return sky43Con;
    }

}
