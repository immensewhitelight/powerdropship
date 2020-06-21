package com.powerdropship.powerdropship;

import com.powerdropship.powerdropship.model.Job;
import javafx.application.Platform;
import org.apache.commons.io.FileUtils;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.SearchTerm;
import java.io.File;
import java.util.Properties;


public class DownloadTrackingFile {

    //Constructor that gives jobOverviewController a reference back to itself.
    public DownloadTrackingFile(JobOverviewController jobOverviewController) {
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

    /**
     * Downloads new messages and fetches details for each message, puts the downloaded attachments into a temp folder
     */

    //Processes link to tracking file download in the body and tracking file attachment downloads.
    // A supplier will only do one or the other.
    public void downloadTrackingFile(Job job) {

        System.out.println("[6. Enter] downloadTrackingFiles");
        Platform.runLater(() -> job.getTextArea().appendText("Enter download tracking file" + System.getProperty("line.separator")));


        String host = job.getJobYourCompanyEmailIpAddress();
        String username = job.getJobYourCompanyEmailAddress();
        String password = job.getJobYourCompanyEmailPassword(); // correct password for Gmail id

        Properties props = new Properties();
        props.setProperty("mail.imap.ssl.enable", "true");
        // set any other needed mail.imap.* properties here
        props.put("mail.mime.base64.ignoreerrors", "true");
        props.setProperty("mail.smtp.class", "com.sun.mail.smtp.SMTPTransport");
        props.put("mail.imaps.partialfetch", "false");
        Session session = Session.getInstance(props);

        String keyword = job.getTrackingEmailKeyword();

        try {

            File trackingDir = job.getTrackingDir();

            if (trackingDir.exists()) {
                FileUtils.cleanDirectory(trackingDir);
            }

            System.out.println(trackingDir.getName());

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
                        if ((message.getSubject().contains(keyword)) & (!message.isSet(Flags.Flag.DELETED)) & (!message.isSet(Flags.Flag.FLAGGED)) & (message.getFrom() != null) & (message.getFrom().length > 0) & (message.getFrom()[0].toString().contains(job.getTrackingEmailFrom()))) {
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

                //Writes the whole message to System.out
                //msg.writeTo(System.out);

                if (msg.getContent() instanceof Multipart) {
                    Multipart multipart = (Multipart) msg.getContent();

                    for (int k = 0; k < multipart.getCount(); k++) {
                        Part part = multipart.getBodyPart(k);

                        String disposition = part.getDisposition();

                        if ((disposition != null) && ((disposition.equalsIgnoreCase(Part.ATTACHMENT) || (disposition.equalsIgnoreCase(Part.INLINE))))) {
                            MimeBodyPart mimeBodyPart = (MimeBodyPart) part;
                            String fileName = "tracking_" + i;
                            File fileToSave = new File(trackingDir + "/" + fileName);
                            mimeBodyPart.saveFile(fileToSave);
                            if (fileToSave.exists()) {
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

            System.out.println(" Downloaded any new tracking attachments from email. ");

        } catch (Exception e) {
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  downloadTrackingFiles error. " + e.getMessage() + System.getProperty("line.separator")));
            Platform.runLater(() -> jobOverviewController.print(job.getJobName() + ":  Restarting job..." + System.getProperty("line.separator")));
            Platform.runLater(() -> job.getServiceInstanceReference().restart());
            e.printStackTrace();
        }
        System.out.println("[6. Exit] downloadTrackingFiles");
        Platform.runLater(() -> job.getTextArea().appendText("Exit download tracking file" + System.getProperty("line.separator")));

    }
}
