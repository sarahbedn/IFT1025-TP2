package server;

import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**

La classe Server fournit une implémentation d'un serveur pour communiquer avec des clients.

Le serveur est capable de traiter des commandes pour enregistrer des formulaires d'inscription et charger des informations sur les cours d'une session spécifique.

Il utilise des {@code EventHandler} pour gérer les événements et communiquer avec les clients.
*/

public class Server {

    public final static String REGISTER_COMMAND = "INSCRIRE";
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     * Constructeur de la classe {@code Server}.
     * Crée un nouveau serveur en écoutant sur le port spécifié et initialise les
     * gestionnaires d'événements.
     *
     * @param port Le numéro de port sur lequel le serveur doit écouter
     * @throws IOException Si une erreur se produit lors de la création du
     *                     {@code ServerSocket}
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * Ajoute un nouvel {@code EventHandler} à la liste des gestionnaires
     * d'événements.
     *
     * @param h L'instance de l'{@code EventHandler} à ajouter
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    /**
     * Avertit tous les gestionnaires d'événements de la réception d'une commande et
     * de son argument.
     *
     * @param cmd La commande reçue
     * @param arg L'argument de la commande
     */
    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Méthode principale pour démarrer et exécuter le serveur en continu.
     * Cette méthode est bloquante et accepte les connexions entrantes des clients,
     * les écoute
     * et gère les commandes reçues avant de se déconnecter et d'attendre la
     * prochaine connexion.
     */
    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Écoute les commandes envoyées par le client et les traite en conséquence.
     * Cette méthode est bloquante et attend la réception d'une commande du client.
     *
     * @throws IOException            Si une erreur se produit lors de la lecture de
     *                                l'objet
     * @throws ClassNotFoundException Si la classe de l'objet reçu n'est pas trouvée
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * Traite la ligne de commande reçue du client en la divisant en une commande et
     * un argument.
     *
     * @param line La ligne de commande reçue du client
     * @return Une paire contenant la commande et l'argument extraits de la ligne
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * 
     * Ferme les flux d'entrée et de sortie et déconnecte le client.
     * 
     * @throws IOException Si une erreur se produit lors de la fermeture des flux ou
     *                     de la déconnexion du client
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * 
     * Gère les événements en fonction de la commande reçue.
     * Cette méthode est appelée par {@code alertHandlers} pour chaque commande
     * reçue.
     * 
     * @param cmd La commande reçue
     * @param arg L'argument associé à la commande
     * @throws IOException
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     Lire un fichier texte contenant des informations sur les cours et les transofmer en liste d'objets 'Course'.
     La méthode filtre les cours par la session spécifiée en argument.
     Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     @param arg la session pour laquelle on veut récupérer la liste des cours
     */
    public void handleLoadCourses(String arg) {
        // TODO: implémenter cette méthode
    }

    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() {
        // TODO: implémenter cette méthode
    }
}

