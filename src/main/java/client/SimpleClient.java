package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import server.models.Course;
import server.models.RegistrationForm;
import server.Server;

/**
 * Une classe représentant un client pour un système d'inscription aux cours.
 * Ce client fournit des fonctionnalités pour charger des informations sur les
 * cours
 * et s'inscrire à des cours spécifiques.
 */
public class SimpleClient {

    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    /**
     * Constructeur pour créer un nouveau client en établissant une connexion
     * avec le serveur via un hôte et un port spécifiques.
     * 
     * @param host L'adresse du serveur auquel se connecter.
     * @param port Le numéro de port pour établir la connexion.
     * @throws IOException Si une erreur se produit lors de la connexion au serveur.
     */
    public SimpleClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    public static void main(String[] args) throws ClassNotFoundException {
        try {
            SimpleClient client = new SimpleClient("localhost", 6000);
            Scanner scanner = new Scanner(System.in);

            System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");

            System.out
                    .println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:");
            System.out.println("1. Automne");
            System.out.println("2. Hiver");
            System.out.println("3. Ete");
            System.out.print("› Choix: ");

            int choice = scanner.nextInt();
            String session = "";

            if (choice == 1) {
                session = "Automne";
            } else if (choice == 2) {
                session = "Hiver";
            } else if (choice == 3) {
                session = "Ete";
            } else {
                System.out.println("Choix non valide");
                scanner.close();
                return;
            }

            client.loadCourses(session);

            // reconnect to the server
            client = new SimpleClient("localhost", 6000);

            System.out.println("1. Consulter les cours offerts pour une autre session");
            System.out.println("2. Inscription à un cours");
            System.out.print("› Choix: ");

            int actionChoice = scanner.nextInt();
            scanner.nextLine();

            if (actionChoice == 2) {
                System.out.print("Veuillez saisir votre prénom: ");
                String firstName = scanner.nextLine();

                System.out.print("Veuillez saisir votre nom: ");
                String lastName = scanner.nextLine();

                System.out.print("Veuillez saisir votre mail: ");
                String email = scanner.nextLine();

                System.out.print("Veuillez saisir votre matricule: ");
                String matricule = scanner.nextLine();

                System.out.print("Veuillez saisir le code du cours: ");
                String courseCode = scanner.nextLine();

                RegistrationForm registrationForm = new RegistrationForm(firstName, lastName, email, matricule,
                        new Course("IFT1015", courseCode, session));

                client.registerForCourse(registrationForm);
            }
            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Charge la liste des cours pour une session donnée.
     * 
     * @param session Le nom de la session pour laquelle charger les cours.
     * @throws IOException Si une erreur se produit lors de la communication avec le
     *                     serveur.
     */
    public void loadCourses(String session) throws IOException {
        try {
            objectOutputStream.writeObject(Server.LOAD_COMMAND + " " + session);
            objectOutputStream.flush();

            List<Course> courses = (List<Course>) objectInputStream.readObject();

            System.out.println("Les cours offerts pendant la session d'" + session + " sont :");
            for (Course course : courses) {
                System.out.println(" - " + course.getCode() + "        " + course.getName());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur: la classe Course n'a pas été trouvée.");
            e.printStackTrace();
        }

    }

    /**
     * Inscrit l'utilisateur à un cours en utilisant les informations du formulaire
     * d'inscription.
     * 
     * @param registration Les informations d'inscription à un cours.
     * @throws IOException            Si une erreur se produit lors de la
     *                                communication avec le serveur.
     * @throws ClassNotFoundException Si la classe Course n'est pas trouvée.
     */
    public void registerForCourse(RegistrationForm registration) throws IOException, ClassNotFoundException {

        System.out.println("Envoi de la demande d'inscription au serveur...: " +
                registration.toString());

        objectOutputStream.writeObject(Server.REGISTER_COMMAND);
        objectOutputStream.flush();

        objectOutputStream.writeObject(registration);
        objectOutputStream.flush();

        String message = (String) objectInputStream.readObject();
        System.out.println("Message du serveur: " + message);
        System.out.println(message);
    }

    /**
     * Ferme la connexion avec le serveur.
     * 
     * @throws IOException Si une erreur se produit lors de la fermeture de la
     *                     connexion.
     */
    public void disconnect() throws IOException {
        objectInputStream.close();
        objectOutputStream.close();
        socket.close();
    }
}
