package com.cardclash;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class PersistenceHandler {

    private static final String FILE_NAME = "users.xml";

    // Carica gli utenti dal file XML
    public static void loadUsers() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                System.out.println("File XML non trovato, creando uno nuovo...");
                createDefaultUsers();
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            NodeList users = doc.getElementsByTagName("user");
            for (int i = 0; i < users.getLength(); i++) {
                Element user = (Element) users.item(i);
                String role = user.getAttribute("role");
                String email = user.getElementsByTagName("email").item(0).getTextContent();
                System.out.println("Caricato utente: " + email + " (ruolo: " + role + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Crea utenti di default e salva il file XML
    public static void createDefaultUsers() {
        try {
            File file = new File(FILE_NAME);
            if (file.exists()) {
                System.out.println("File XML già esistente.");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("users");
            doc.appendChild(root);

            // Crea un utente di default (admin)
            root.appendChild(createUser(doc, "admin", "admin@gmail.com", hashPassword("admin123")));

            // Scrittura del file XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

            System.out.println("File XML creato con utenti di default.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Crea un elemento utente con ruolo, email e password
    private static Element createUser(Document doc, String role, String email, String password) {
        Element user = doc.createElement("user");
        user.setAttribute("role", role);

        // Crea il nodo email
        Element emailNode = doc.createElement("email");
        emailNode.appendChild(doc.createTextNode(email));
        user.appendChild(emailNode);

        // Crea il nodo password
        Element passwordNode = doc.createElement("password");
        passwordNode.appendChild(doc.createTextNode(password));
        user.appendChild(passwordNode);

        return user;
    }

    // Autentica l'utente confrontando l'email e la password (hashed)
    public String authenticate(String email, String password) {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                return null;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            NodeList users = doc.getElementsByTagName("user");
            for (int i = 0; i < users.getLength(); i++) {
                Element user = (Element) users.item(i);
                String storedEmail = user.getElementsByTagName("email").item(0).getTextContent();
                String storedPassword = user.getElementsByTagName("password").item(0).getTextContent();

                // Verifica che email e password corrispondano (password hashed)
                if (storedEmail.equals(email) && storedPassword.equals(hashPassword(password))) {
                    return user.getAttribute("role");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Crea l'hash della password
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Errore nella generazione dell'hash della password", e);
        }
    }

    // Registra un nuovo utente nel file XML
    public static void registerUser(String role, String email, String password) {
        try {
            File file = new File(FILE_NAME);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;

            // Se il file non esiste, creiamo uno nuovo con utenti di default
            if (!file.exists()) {
                createDefaultUsers();
                doc = builder.parse(file);
            } else {
                doc = builder.parse(file);
            }

            // Aggiungi il nuovo utente
            Element root = doc.getDocumentElement();
            root.appendChild(createUser(doc, role, email, hashPassword(password)));

            // Scrittura del file XML aggiornato
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

            System.out.println("Utente " + email + " registrato con successo!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
