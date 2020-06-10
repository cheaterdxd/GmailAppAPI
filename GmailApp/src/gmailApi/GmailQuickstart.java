/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gmailApi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.ArrayMap;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.common.collect.HashBiMap;
import static gmailApi.MessageProcess.getListMail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.BodyPart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;

public class GmailQuickstart {

    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "src/main/resources/tokens";

    /**
     * Global instance of the scopes required by this quickstart. If modifying
     * these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
	// Load client secrets.
	InputStream in = GmailQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
	if (in == null) {
	    throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
	}
	GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

	// Build flow and trigger user authorization request.
	GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
		HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
		.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
		.setAccessType("offline")
		.build();
	LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
	return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

//        /**
//     * Create a MimeMessage using the parameters provided.
//     *
//     * @param to email address of the receiver
//     * @param from email address of the sender, the mailbox account
//     * @param subject subject of the email
//     * @param bodyText body text of the email
//     * @return the MimeMessage to be used to send email
//     * @throws MessagingException
//     */
//    public static MimeMessage createEmail(String to,
//                                          String from,
//                                          String subject,
//                                          String bodyText)
//            throws MessagingException {
//        Properties props = new Properties();
//        Session session = Session.getDefaultInstance(props, null);
//
//        MimeMessage email = new MimeMessage(session);
//
//        email.setFrom(new InternetAddress(from));
//        email.addRecipient(javax.mail.Message.RecipientType.TO,
//                new InternetAddress(to));
//        email.setSubject(subject);
//        email.setText(bodyText);
//        return email;
//    }
    static Map<String, String> listFile = new ArrayMap<>();
        public static void loadBodyForMessageOb(List<MessagePart> parts) {
	for (MessagePart part : parts) {
	    String mimeType = part.getMimeType();
	    if (mimeType.equals("multipart/related")) {
		loadBodyForMessageOb( part.getParts());
	    }
//	    if (mimeType.equals("application/pdf")) {
//		MessagePartBody body = part.getBody();
//		String attId = body.getAttachmentId();
//		String filename = part.getFilename();
//		msgOb.listFile.put(filename, attId);
//	    }
//	    if (mimeType.equals("image/png")) {
//		MessagePartBody body = part.getBody();
//		String attId = body.getAttachmentId();
//		String filename = part.getFilename();
//		msgOb.listFile.put(filename, attId);
//	    }
//	    if (mimeType.equals("audio/mp3")) {
//		MessagePartBody body = part.getBody();
//		String attId = body.getAttachmentId();
//		String filename = part.getFilename();
//		msgOb.listFile.put(filename, attId);
//	    }
//	    if (mimeType.equals("text/plain")) {
		if (!part.getFilename().isEmpty()) {
		    MessagePartBody body = part.getBody();
		    String attId = body.getAttachmentId();
		    String filename = part.getFilename();
		    listFile.put(filename, attId);
		} else {
		    String data = part.getBody().getData();
		    Base64 base64Url = new Base64(true);
		    byte[] emailBytes = Base64.decodeBase64(data);
		    String text = new String(emailBytes);
		    
		}
//	    }
//	    else {
//		    String data = part.getBody().getData();
//		    Base64 base64Url = new Base64(true);
//		    byte[] emailBytes = Base64.decodeBase64(data);
//		    String text = new String(emailBytes);
//		    msgOb.mainText = text;
//		}
//	    String fileName = part.getFilename();
//	    if (!fileName.isEmpty()) {
//		MessagePartBody body = part.getBody();
//		String attId = body.getAttachmentId();
//		msgOb.listFile.put(fileName, attId);
//	    }
	}
    }
    
    
    public static void getContent(MimeMultipart parts) throws MessagingException, IOException {
	for (int i = 0; i < parts.getCount(); i++) {
	    if (parts.getBodyPart(i).isMimeType("multipart/*")) {
		System.out.println("part " + i + ":" + parts.getBodyPart(i).getContentType());
		System.out.println("Dispo: " + parts.getBodyPart(i).getDisposition());
		System.out.println("Descrip: " + parts.getBodyPart(i).getDescription());
		getContent((MimeMultipart) parts.getBodyPart(i).getContent());
	    } else {
		if (parts.getBodyPart(i).isMimeType("text/plain")) {
		    System.out.println("file: " + parts.getBodyPart(i).getFileName());
		    System.out.println("part " + i + ":" + parts.getBodyPart(i).getContent());
		    System.out.println(parts.getBodyPart(i).getClass());
		    System.out.println("Dispo: " + parts.getBodyPart(i).getDisposition());
		}
		if (parts.getBodyPart(i).isMimeType("text/html")) {
		    System.out.println("file: " + parts.getBodyPart(i).getFileName());
		    System.out.println("part " + i + ":" + parts.getBodyPart(i).getContent());
		    System.out.println(parts.getBodyPart(i).getClass());
		    System.out.println("Dispo: " + parts.getBodyPart(i).getDisposition());
		} else {
		    System.out.println("file: " + parts.getBodyPart(i).getFileName());
		    System.out.println("part " + i + ":" + parts.getBodyPart(i).getContent());
		    System.out.println(parts.getBodyPart(i).getClass());
		    System.out.println("Dispo: " + parts.getBodyPart(i).getDisposition());
		}
	    }
	}
    }
    

    public static void main(String... args) throws IOException, GeneralSecurityException, MessagingException {
	// Build a new authorized API client service.
	final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

	Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
		.setApplicationName(APPLICATION_NAME)
		.build();
	GlobalVariable.setService(service);
	// Print the labels in the user's account.
	String userId = "testdoan123456@gmail.com";
	GlobalVariable.userId = userId;
	String subject = "test send mail with header, bbc, cc, atttachment after fixed code by Gmail API";
	String body = "this is the second mail sent with attachment file";
	String[] toMail = {"n17dcat061@student.ptithcm.edu.vn", "thanhtuan9906@gmail.com", "h127qw81naskdb@gmail.com"};
	String[] cc = {"n17dcat061@student.ptithcm.edu.vn", "thanhtuan9906@gmail.com"};
	String[] bcc = {"n17dcat061@student.ptithcm.edu.vn", "thanhtuan9906@gmail.com"};
	String[] fileName = {"C:\\Users\\Admin\\Documents\\flag_buf1.txt"};

	String messageId = "17157330b4f66384";
	Message message = service.users().messages().get(userId, messageId).setFormat("raw").execute();
	Message messagefull = service.users().messages().get(userId, messageId).setFormat("full").execute();
	Base64 base64Url = new Base64(true);
	byte[] emailBytes = base64Url.decodeBase64(message.getRaw());

	Properties props = new Properties();
	Session session = Session.getDefaultInstance(props, null);

	MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
//	System.out.println(email.getMessageID());
	System.out.println(email.getSubject());
//	System.out.println(email.getContentType());
//	System.out.println(MimeMultipart.class == email.getContent().getClass());
//	MimeMultipart multipart = (MimeMultipart)email.getContent();
//	System.out.println(multipart.getBodyPart(0).getContent().getClass() == MimeMultipart.class);
	if (email.getContent().getClass() == MimeMultipart.class) {
	    System.out.println("email content: " + email.getContent().getClass());
	    MimeMultipart multipart = (MimeMultipart) email.getContent();
	    System.out.println(email.getContentType());
	    getContent(multipart);
	} else {
	    System.out.println(email.getContent());
	}
    }
}
//1712445165e55410 : test RE
//171809dd2f97dd6a : re re mail
// 1718c276288b984e: test reply
// 171748240b8f6f6f: nhieu loai file
