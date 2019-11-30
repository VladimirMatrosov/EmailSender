package sample.mail;

import com.sun.mail.imap.protocol.FLAGS;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class MailSender {

    private Session session;
    private String login;
    private String password;

    public MailSender(String login, String password) {
        this.login = login;
        this.password = password;
        System.out.println("password: " + password);

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.mail.ru");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.imap.host", "imap.mail.ru");
        properties.put("mail.imap.auth", "true");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.imap.socketFactory.port", "993");
        properties.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.debug", "true");
        properties.setProperty("mail.store.protocol", "imap");
        properties.setProperty("mail.debug.auth", "true");
        properties.setProperty("mail.imap.ssl.enable", "true");

        session = Session.getInstance(properties, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(login, password);
            }
        });
    }

    public void sendMessage(String user, String subject, String text) throws MessagingException, IOException {
        System.out.println(text);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(login));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(user));
        message.setRecipients(Message.RecipientType.TO, new Address[]{
                new InternetAddress(user)
        });
        message.setSubject(subject);

        MimeMultipart multipart = new MimeMultipart();
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setDataHandler(new DataHandler(text, "text/plain; charset=\"utf-8\""));
        multipart.addBodyPart(textPart);

        message.setContent(multipart);
        message.setHeader("Precedence", "bulk");
        message.setHeader("Auto-Submitted", "auto-generated");
        Transport.send(message);
        System.out.println(message.getContent());

        Store store = session.getStore("imap");
        store.connect("imap.mail.ru", login, password);

        Folder folder = store.getFolder("Lenta");
        if (!folder.exists()) {
            folder.create(Folder.HOLDS_MESSAGES);
        }
        folder.open(Folder.READ_WRITE);
        System.out.println("appending...");

        folder.appendMessages(new Message[]{message});
        message.setFlag(FLAGS.Flag.RECENT, true);

        store.close();
    }

    public void sendMessage(String user, String subject, String text, File file) throws MessagingException, IOException {
        System.out.println(text);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(login));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(user));
        message.setRecipients(Message.RecipientType.TO, new Address[]{
                new InternetAddress(user)
        });
        message.setSubject(subject);

        MimeMultipart multipart = new MimeMultipart();
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setDataHandler(new DataHandler(text, "text/plain; charset=\"utf-8\""));
        multipart.addBodyPart(textPart);

        MimeBodyPart filePart = new MimeBodyPart();
        filePart.setFileName(MimeUtility.encodeWord(file.getName()));
        filePart.setDataHandler(new DataHandler(new FileDataSource(file)));
        multipart.addBodyPart(filePart);

        message.setContent(multipart);
        session.getTransport();
        Transport.send(message);
        System.out.println(message.getContent());

        Store store = session.getStore("imap");
        store.connect("imap.mail.ru", login, password);

        Folder folder = store.getFolder("Sent");
        if (!folder.exists()) {
            folder.create(Folder.HOLDS_MESSAGES);
        }
        folder.open(Folder.READ_WRITE);
        System.out.println("appending...");

        folder.appendMessages(new Message[]{message});
        message.setFlag(FLAGS.Flag.RECENT, true);

        store.close();
    }

    public void clean() throws MessagingException {
        session.getProperties().clear();
        session.getTransport().close();
        session = null;
    }
}
