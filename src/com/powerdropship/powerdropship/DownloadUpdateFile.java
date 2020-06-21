package com.powerdropship.powerdropship;

import com.powerdropship.powerdropship.model.Job;
import javafx.application.Platform;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.SearchTerm;
import java.io.*;
import java.util.Properties;


public class DownloadUpdateFile {

    public DownloadUpdateFile(JobOverviewController jobOverviewController) {
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


    public void ftpDownloadUpdateFile(Job job) {

        System.out.println("[Enter] ftpDownloadUpdateFile");

        Platform.runLater(() -> job.getTextArea().appendText("Enter handle ftp download of update file" + System.getProperty("line.separator")));

        String server = job.getUpdateFileFTPUrl();
        int port = 21;
        String user = job.getUpdateFileFTPUser();
        String pass = job.getUpdateFileFTPPass();

        FTPClient ftpClient = new FTPClient();

        String remoteFile = job.getUpdateFileName();
        File downloadFile = job.getUpdateFile();

        try {

            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
            InputStream inputStream = ftpClient.retrieveFileStream(remoteFile);
            byte[] bytesArray = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                outputStream.write(bytesArray, 0, bytesRead);
            }

            boolean success = ftpClient.completePendingCommand();

            if (!success) {
                //If the download fails delete the DownloadUpdateFile, so that it doesn't trouble the finding of REMOVED and NEW prods.
                downloadFile.delete();
                Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Products File Download Error. Couldn't download Products File... " + System.getProperty("line.separator")));
                Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
                Platform.runLater(() -> job.getServiceInstanceReference().restart());
            }

            outputStream.close();
            inputStream.close();

        } catch (Exception ex) {
            //If the download fails delete the DownloadUpdateFile, so that it doesn't trouble the finding of REMOVED and NEW prods.
            downloadFile.delete();
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Products File download error. " + ex.getMessage() + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            ex.printStackTrace();

        } finally {

            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("[Exit] ftpDownloadUpdateFile");
        Platform.runLater(() -> job.getTextArea().appendText("Exit handle ftp download of update file" + System.getProperty("line.separator")));
    }


    public void javaMailDownloadUpdateFile(Job job) {

        System.out.println("[Enter] javaMailDownloadUpdateFile");
        Platform.runLater(() -> job.getTextArea().appendText("Enter handle email download of update file" + System.getProperty("line.separator")));


        String host = job.getJobYourCompanyEmailIpAddress();
        String username = job.getJobYourCompanyEmailAddress();
        String password = job.getJobYourCompanyEmailPassword();

        Properties props = new Properties();
        props.setProperty("mail.imap.ssl.enable", "true");
        // set any other needed mail.imap.* properties here
        props.put("mail.mime.base64.ignoreerrors", "true");
        props.setProperty("mail.smtp.class", "com.sun.mail.smtp.SMTPTransport");
        props.put("mail.imaps.partialfetch", "false");
        Session session = Session.getInstance(props);

        //String keyword = job.getUpdateFileEmailKeyword();
        String keyword = job.getUpdateFileEmailKeyword();

        //String emailFrom = job.getUpdateFileEmailFrom();
        String emailFrom = job.getUpdateFileEmailFrom();

        String remoteFile = job.getUpdateFileName();

        try {

            File supplierDir = job.getSupplierDir();

            // Connects to the message store
            Store store = session.getStore("imap");

            store.connect(host, username, password);

            // Opens the inbox folder
            Folder folderInbox = store.getFolder("Inbox");
            folderInbox.open(Folder.READ_WRITE);

            // Creates a search criterion
            SearchTerm searchCondition = new SearchTerm() {
                @Override
                public boolean match(Message message) {
                    try {
                        if ((message.isMimeType("multipart/mixed")) & (message.getSubject().contains(keyword)) & (!message.isSet(Flags.Flag.DELETED)) & (!message.isSet(Flags.Flag.FLAGGED)) & (message.getFrom()[0].toString().contains(emailFrom))) {
                            return true;
                        }
                    } catch (MessagingException ex) {
                        ex.printStackTrace();
                    }
                    return false;
                }
            };

            // Fetches new messages from server, if they meet the search condition
            Message[] foundMessages = folderInbox.search(searchCondition);

            for (int i = 0; i < foundMessages.length; i++) {
                Message msg = foundMessages[i];

                System.out.println("HELLO: " + msg);

                if (msg.getContent() instanceof Multipart) {
                    Multipart multipart = (Multipart) msg.getContent();

                    for (int k = 0; k < multipart.getCount(); k++) {
                        Part part = multipart.getBodyPart(k);

                        String disposition = part.getDisposition();

                        if ((disposition != null) && ((disposition.equalsIgnoreCase(Part.ATTACHMENT) || (disposition.equalsIgnoreCase(Part.INLINE))))) {
                            MimeBodyPart mimeBodyPart = (MimeBodyPart) part;
                            //String fileName = part.getFileName();
                            File fileToSave = new File(supplierDir + "/" + job.getUpdateFileName());
                            mimeBodyPart.saveFile(fileToSave);
                            if (fileToSave.exists()) {
                                job.setUpdateFile(fileToSave);
                                msg.setFlag(Flags.Flag.FLAGGED, true);
                            }
                        }
                    }
                }
            }

            // disconnect
            folderInbox.close(false);
            store.close();

            //int trackingNumberAttachments = trackingDir.list().length;
            //int trackingFound = foundMessages.length;

        } catch (Exception e) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  javaMailDownloadUpdateFile error. " + e.getMessage() + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            e.printStackTrace();
        }
        System.out.println("[Exit] javaMailDownloadUpdateFile");
        Platform.runLater(() -> job.getTextArea().appendText("Exit handle email download of update file" + System.getProperty("line.separator")));
    }
}


