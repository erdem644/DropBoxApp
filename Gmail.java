package com.example.cip_a.dropboxapp;

import android.content.Context;
import android.location.Address;
import android.provider.Telephony;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import javax.mail.Part;

/**
 * Created by cip-a on 11.03.2016.
 */
public class Gmail {

    final String username = "eszwahli@gmail.com";
    final String password = "gmailtest";

    public Session createSessionObjectForSend() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

    }

    //Create message
    public Message createMessage(InternetAddress[] emails, String subject, String messageBody, Session session, DataHandler photo) throws MessagingException, UnsupportedEncodingException {
        // Create a default MimeMessage object.
        Message message = new MimeMessage(session);
        // Set From: header field of the header.
        message.setFrom(new InternetAddress(username, "Drive Test Mail"));
        // Set To: header field of the header.
        message.addRecipients(Message.RecipientType.TO, emails);
        // Set Subject: header field
        message.setSubject(subject);

        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();
        // Now set the actual message
        messageBodyPart.setText(messageBody);
        // Create a multipar message
        Multipart multipart = new MimeMultipart();
        // Set text message part
        multipart.addBodyPart(messageBodyPart);
        // Part two is attachment
        messageBodyPart = new MimeBodyPart();

        messageBodyPart.setDataHandler(photo);
        messageBodyPart.setFileName("test");

        multipart.addBodyPart(messageBodyPart);
        // Send the complete message parts
        message.setContent(multipart);
        return message;
    }

    public Session createSessionObjectForGet() {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "pop3");
        properties.put("mail.pop3.host", "pop.gmail.com");
        properties.put("mail.pop3.port", "995");
        properties.put("mail.pop3.starttls.enable", "true");

        Session session = Session.getDefaultInstance(properties);
        session.setDebug(true);
        return session;
/*
        return Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        */
    }

    public Message getMessage(Session session) throws MessagingException, UnsupportedEncodingException {
        try {

            Store store = session.getStore("pop3s");

            store.connect("pop.gmail.com", username, password);

            Folder folder = store.getFolder("INBOX");

            folder.open(Folder.READ_ONLY);
            int sayi = folder.getMessageCount();
            Message msg5 = folder.getMessage(1);
            //Message msg2 = folder.getMessage(2);
            //Message msg3 = folder.getMessage(3);
           // Message msg4 = folder.getMessage(4);
            //Message msg5 = folder.getMessage(5);
           // Message msg6 = folder.getMessage(6);
            javax.mail.Address[] getAllRecipients = msg5.getAllRecipients();
            javax.mail.Address[] fromMail = msg5.getFrom();
            String konu = msg5.getSubject();
            String edsd = msg5.getFileName();
            Date tarih = msg5.getSentDate();
            Object aaaa;
            try {
                aaaa = msg5.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            DataHandler data = msg5.getDataHandler();
            GetMessageBody(msg5);
            System.out.println("messages.length---");

            folder.close(false);
            store.close();
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void GetMessageBody (Part p)
    {
        try {
            //check if the content is plain text
            if (p.isMimeType("text/plain")) {
                System.out.println("This is plain text");
                System.out.println("---------------------------");
                System.out.println((String) p.getContent());
            }
            //check if the content has attachment
            else if (p.isMimeType("multipart/*")) {
                System.out.println("This is a Multipart");
                System.out.println("---------------------------");
                Multipart mp = (Multipart) p.getContent();
                int count = mp.getCount();
                for (int i = 0; i < count; i++)
                    GetMessageBody(mp.getBodyPart(i));
            }
            //check if the content is a nested message
            else if (p.isMimeType("message/rfc822")) {
                System.out.println("This is a Nested Message");
                System.out.println("---------------------------");
                GetMessageBody((Part) p.getContent());
            }
            //check if the content is an inline image
            /*
            else if (p.isMimeType("image/png")) {
                Object o = p.getContent();
                InputStream x = (InputStream) o;
                // Construct the required byte array
                while ((i = (int) ((InputStream) x).available()) > 0) {
                    int result = (int) (((InputStream) x).read(bArray));
                    if (result == -1)
                        int i = 0;
                    byte[] bArray = new byte[x.available()];

                    break;
                }
                FileOutputStream f2 = new FileOutputStream("/tmp/image.jpg");
                f2.write(bArray);
            }
            */
            else if (p.getContentType().contains("image/png")) {
                System.out.println("content type" + p.getContentType());
                File f = new File("test.png");
                FileOutputStream fos = new FileOutputStream(f);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                DataOutputStream output = new DataOutputStream(bos);
                com.sun.mail.util.BASE64DecoderStream test = (com.sun.mail.util.BASE64DecoderStream) p.getContent();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = test.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
            else {
                Object o = p.getContent();
                if (o instanceof String) {
                    System.out.println("This is a string");
                    System.out.println("---------------------------");
                    System.out.println((String) o);
                }
                else if (o instanceof InputStream) {
                    System.out.println("This is just an input stream");
                    System.out.println("---------------------------");
                    InputStream is = (InputStream) o;
                    is = (InputStream) o;
                    int c;
                    while ((c = is.read()) != -1)
                        System.out.write(c);
                }
                else {
                    System.out.println("This is an unknown type");
                    System.out.println("---------------------------");
                    System.out.println(o.toString());
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
