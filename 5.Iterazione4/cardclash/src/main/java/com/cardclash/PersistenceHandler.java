package com.cardclash;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class PersistenceHandler {

    private static final String USER_FILE = "users.xml";
    private static final String TOURNAMENTS_FILE = "tournaments.xml";
    private static final String FORMATS_FILE = "formats.xml";

    // Carica gli utenti dal file XML
    public static void loadUsers(CardClash cardClash) {
        try {
            File file = new File(USER_FILE);
            if (!file.exists()) {
                System.out.println("File XML non trovato, creando uno nuovo...");
                createDefaultUsers();
                cardClash.registraGiocatore("admin", "admin@gmail.com", "admin123", "admin");
                cardClash.confermaRegistrazione();
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            NodeList users = doc.getElementsByTagName("user");
            for (int i = 0; i < users.getLength(); i++) {
                Element user = (Element) users.item(i);
                String role = user.getAttribute("role");
                String nome = user.getElementsByTagName("nome").item(0).getTextContent();
                String email = user.getElementsByTagName("email").item(0).getTextContent();
                String password = user.getElementsByTagName("password").item(0).getTextContent();
                String nickname = user.getElementsByTagName("nickname").item(0).getTextContent();
                String elo = user.getElementsByTagName("elo").item(0).getTextContent();
                cardClash.registraGiocatore(nome, email, password, nickname);
                cardClash.confermaRegistrazione();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(cardClash.getGiocatori().toString());
    }

    public static void loadTournaments(CardClash cardClash) {
        try {
            File file = new File(TOURNAMENTS_FILE);
            if (!file.exists()) {
                System.out.println("File tournaments.xml non trovato, nessun torneo verrà caricato.");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            NodeList tournamentNodes = doc.getElementsByTagName("torneo");
            for (int i = 0; i < tournamentNodes.getLength(); i++) {
                Element torneoElem = (Element) tournamentNodes.item(i);

                String nome = torneoElem.getElementsByTagName("nome").item(0).getTextContent().trim();
                String dataStr = torneoElem.getElementsByTagName("data").item(0).getTextContent().trim();
                String orarioStr = torneoElem.getElementsByTagName("orario").item(0).getTextContent().trim();
                String luogo = torneoElem.getElementsByTagName("luogo").item(0).getTextContent().trim();
                String codiceStr = torneoElem.getElementsByTagName("codFormato").item(0).getTextContent().trim();
                // Parsing di data e orario
                LocalDate data = LocalDate.parse(dataStr);
                try {
                    cardClash.creaTorneo(nome, data, orarioStr, luogo);
                    cardClash.selezionaFormato(Integer.valueOf(codiceStr));
                    cardClash.confermaCreazione();
                } catch (Exception e) {
                    System.out.println("Errore nella creazione del torneo");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadFormats(CardClash cardClash) {
        File file = new File(FORMATS_FILE);
        if (!file.exists()) {
            System.out.println("File " + FORMATS_FILE + " non trovato, nessun formato verrà caricato.");
            return;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList formatoNodes = doc.getElementsByTagName("formato");
            for (int i = 0; i < formatoNodes.getLength(); i++) {
                Element formatoElem = (Element) formatoNodes.item(i);
                try {
                    Integer codice = Integer.parseInt(formatoElem.getAttribute("codice"));
                    String nome = formatoElem.getAttribute("nome");
                    String giocoString = formatoElem.getAttribute("gioco");
                    Integer numMaxGiocatori = Integer.parseInt(formatoElem.getAttribute("numMaxGiocatori"));
                    Float victoryScore = Float.parseFloat(formatoElem.getAttribute("victoryScore"));
                    Float penaltyScore = Float.parseFloat(formatoElem.getAttribute("penaltyScore"));

                    cardClash.creaNuovoFormato(codice, nome, giocoString, numMaxGiocatori, victoryScore, penaltyScore);
                    cardClash.confermaFormato();
                } catch (GiocoNonSupportatoException e) {
                    System.err.println("Errore nel caricamento di un formato: " + e.getMessage());
                }
            }

            System.out.println("Formati caricati correttamente da " + FORMATS_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveFormat(FormatoTorneo formato) {
        File file = new File(FORMATS_FILE);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;
            Element root;

            if (!file.exists()) {
                doc = builder.newDocument();
                root = doc.createElement("formati");
                doc.appendChild(root);
            } else {
                doc = builder.parse(file);
                root = doc.getDocumentElement();
            }

            String giocoString = formato.getGioco().name();

            Element formatoElem = createFormat(
                    doc,
                    formato.getCodice(),
                    formato.getNome(),
                    giocoString,
                    formato.getNumMaxGiocatori(),
                    formato.getVictoryScore(),
                    formato.getPenaltyScore()
            );
            root.appendChild(formatoElem);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);

            transformer.transform(source, result);

            System.out.println("Formato salvato correttamente in " + FORMATS_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Element createFormat(Document doc, int codice, String nome, String gioco, int numMaxGiocatori, float victoryScore, float penaltyScore) {
        Element formatoElem = doc.createElement("formato");

        formatoElem.setAttribute("codice", String.valueOf(codice));
        formatoElem.setAttribute("nome", nome);
        formatoElem.setAttribute("gioco", gioco);
        formatoElem.setAttribute("numMaxGiocatori", String.valueOf(numMaxGiocatori));
        formatoElem.setAttribute("victoryScore", String.valueOf(victoryScore));
        formatoElem.setAttribute("penaltyScore", String.valueOf(penaltyScore));

        return formatoElem;
    }

    // Crea utenti di default e salva il file XML
    public static void createDefaultUsers() {
        try {
            File file = new File(USER_FILE);
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
            root.appendChild(createUser(doc, "admin", "admin", "admin@gmail.com", hashPassword("admin123"), "admin"));

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
    private static Element createUser(Document doc, String role, String nome, String email, String password,
            String nickname) {
        Element user = doc.createElement("user");
        user.setAttribute("role", role);

        Element nomeNode = doc.createElement("nome");
        nomeNode.appendChild(doc.createTextNode(nome));
        user.appendChild(nomeNode);

        Element emailNode = doc.createElement("email");
        emailNode.appendChild(doc.createTextNode(email));
        user.appendChild(emailNode);

        Element passwordNode = doc.createElement("password");
        passwordNode.appendChild(doc.createTextNode(password));
        user.appendChild(passwordNode);

        Element nicknameNode = doc.createElement("nickname");
        nicknameNode.appendChild(doc.createTextNode(nickname));
        user.appendChild(nicknameNode);

        Element eloNode = doc.createElement("elo");
        eloNode.appendChild(doc.createTextNode("0.0"));
        user.appendChild(eloNode);

        return user;
    }

    // Autentica l'utente confrontando l'email e la password (hashed)
    public static String authenticate(String email, String password) {
        try {
            File file = new File(USER_FILE);
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
    public static void registerUser(String role, String nome, String email, String password, String nickname) {
        try {
            File file = new File(USER_FILE);
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
            root.appendChild(createUser(doc, role, nome, email, hashPassword(password), nickname));

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

    public static void saveTournament(Torneo torneo) {
        try {
            File file = new File("tournaments.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;
            Element root;
            if (!file.exists()) {
                System.out.println("File tournaments.xml non trovato, creandolo...");
                doc = builder.newDocument();
                root = doc.createElement("tornei");
                doc.appendChild(root);
            } else {
                doc = builder.parse(file);
                root = doc.getDocumentElement();
            }
            // Crea l'elemento XML per il torneo e aggiungilo al documento
            Element torneoElem = createTournamentElement(doc, torneo);
            root.appendChild(torneoElem);

            // Scrive il documento aggiornato su file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

            System.out.println("Torneo salvato correttamente in tournaments.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Crea un elemento XML per rappresentare il torneo
    private static Element createTournamentElement(Document doc, Torneo torneo) {
        Element torneoElement = doc.createElement("torneo");

        Element nomeElem = doc.createElement("nome");
        nomeElem.appendChild(doc.createTextNode(torneo.getNome()));
        torneoElement.appendChild(nomeElem);

        Element dataElem = doc.createElement("data");
        dataElem.appendChild(doc.createTextNode(torneo.getData().toString()));
        torneoElement.appendChild(dataElem);

        Element orarioElem = doc.createElement("orario");
        orarioElem.appendChild(doc.createTextNode(torneo.getOrario().toString()));
        torneoElement.appendChild(orarioElem);

        Element luogoElem = doc.createElement("luogo");
        luogoElem.appendChild(doc.createTextNode(torneo.getLuogo()));
        torneoElement.appendChild(luogoElem);

        if (torneo.getFormato() != null) {
            Element formatoElem = doc.createElement("codFormato");
            formatoElem.appendChild(doc.createTextNode(torneo.getFormato().getCodice().toString()));
            torneoElement.appendChild(formatoElem);
        }

        return torneoElement;
    }
}
