package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import server.Server;
import server.models.Course;
import server.models.RegistrationForm;

/**
 * 
 * Cette classe représente l'interface graphique pour les clients de
 * l'application
 * 
 * d'inscription à des cours de l'UdeM. Elle permet à l'utilisateur de se
 * connecter
 * 
 * à un serveur pour charger la liste des cours disponibles, de sélectionner un
 * cours,
 * 
 * puis de s'inscrire à ce cours en remplissant un formulaire.
 */
public class InterfaceClient implements ActionListener {

    JFrame frame;
    JButton chargeButton;
    JButton submitButton;
    JTextField prenomText;
    JTextField nomText;
    JTextField emailText;
    JTextField matriculeText;
    JComboBox choixSession;
    DefaultTableModel tableModel;
    JTable table;
    JScrollPane scrollPane;
    String sessionActuelle = "Automne";
    static InterfaceClient interfaceClient;
    String[][] classesActuelles;

    private String host;
    private int port;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    /**
     * 
     * Constructeur de la classe InterfaceClient. Initialise les éléments de
     * l'interface
     * graphique et se connecte au serveur.
     * 
     * @param host L'adresse du serveur.
     * @param port Le port sur lequel le serveur écoute.
     * @throws IOException Si une erreur se produit lors de la connexion.
     */
    public InterfaceClient(String host, int port) throws IOException {

        // set the port & host for all network operations
        this.host = host;
        this.port = port;

        connect();

        int windowWidth = 860;
        int windowHeight = 560;

        JLabel formulaireInscriptionText = new JLabel("Formulaire d'inscription");
        formulaireInscriptionText.setBounds(475, 15, 400, 30);
        formulaireInscriptionText.setFont(new Font("Serif", Font.PLAIN, 32));

        JLabel prenomLabel = new JLabel("Prénom");
        prenomLabel.setBounds(450, 75, 200, 30);
        prenomLabel.setFont(new Font("Serif", Font.PLAIN, 16));

        JLabel nomLabel = new JLabel("Nom");
        nomLabel.setBounds(450, 150, 200, 30);
        nomLabel.setFont(new Font("Serif", Font.PLAIN, 16));

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setBounds(450, 225, 200, 30);
        emailLabel.setFont(new Font("Serif", Font.PLAIN, 16));

        JLabel matriculeLabel = new JLabel("Matricule");
        matriculeLabel.setBounds(450, 300, 200, 30);
        matriculeLabel.setFont(new Font("Serif", Font.PLAIN, 16));

        JLabel listeCoursLabel = new JLabel("Liste des cours");
        listeCoursLabel.setBounds(100, 15, 200, 30);
        listeCoursLabel.setFont(new Font("Serif", Font.PLAIN, 32));

        chargeButton = new JButton();
        chargeButton.setBounds(212, 400, 100, 50);

        chargeButton.addActionListener(this);
        chargeButton.setText("Charger");
        chargeButton.setFocusable(false);

        submitButton = new JButton();
        submitButton.setBounds(575, 400, 100, 50);
        submitButton.addActionListener(this);
        submitButton.setText("Envoyer");
        submitButton.setFocusable(false);

        prenomText = new JTextField();
        prenomText.setBounds(525, 75, 200, 30);

        nomText = new JTextField();
        nomText.setBounds(525, 150, 200, 30);

        emailText = new JTextField();
        emailText.setBounds(525, 225, 200, 30);

        matriculeText = new JTextField();
        matriculeText.setBounds(525, 300, 200, 30);

        String[] sessions = { "Automne", "Hiver", "Été" };
        choixSession = new JComboBox(sessions);
        choixSession.setBounds(75, 400, 100, 50);
        choixSession.addActionListener(this);

        String[] columnNames = { "Code", "Cours" };
        Object[][] data = {

        };

        tableModel = new DefaultTableModel(data, columnNames) {
            // Override the isCellEditable method to make the cells non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        // ScrollPane
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(40, 80, 300, 300);
        ;

        frame = new JFrame();
        frame.setTitle("Inscription UdeM");
        frame.setSize(windowWidth, windowHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        frame.add(scrollPane);

        frame.add(chargeButton);
        frame.add(submitButton);
        frame.add(prenomText);
        frame.add(nomText);
        frame.add(emailText);
        frame.add(matriculeText);
        frame.add(choixSession);

        // Labels des textes
        frame.add(formulaireInscriptionText);
        frame.add(prenomLabel);
        frame.add(nomLabel);
        frame.add(emailLabel);
        frame.add(matriculeLabel);
        frame.add(listeCoursLabel);

        frame.setVisible(true);

    }

    /**
     * 
     * Méthode principale pour démarrer l'application client.
     * 
     * @param args Arguments de la ligne de commande.
     * @throws ClassNotFoundException Si la classe Course est introuvable.
     * @throws IOException            Si une erreur de communication réseau se
     *                                produit.
     */
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        try {

            interfaceClient = new InterfaceClient("localhost", 6000);

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    /**
     * 
     * Ouvre la connexion avec le serveur.
     * 
     * @throws IOException Si une erreur se produit lors de l'ouverture de la
     *                     connexion.
     */
    public void connect() throws IOException {
        socket = new Socket(host, port);
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * 
     * Ferme la connexion avec le serveur.
     * 
     * @throws IOException Si une erreur se produit lors de la fermeture de la
     *                     connexion
     *                     avec le serveur.
     */
    public void disconnect() throws IOException {
        objectInputStream.close();
        objectOutputStream.close();
        socket.close();
    }

    /**
     * 
     * Charge la liste des cours disponibles pour une session donnée depuis le
     * serveur.
     * 
     * @param session La session pour laquelle charger les cours.
     * @return La liste des cours disponibles pour la session donnée.
     * @throws IOException Si une erreur de communication réseau se produit.
     */
    public List<Course> loadCourses(String session) throws IOException {
        try {

            objectOutputStream.writeObject(Server.LOAD_COMMAND + " " + session);
            objectOutputStream.flush();

            List<Course> courses = (List<Course>) objectInputStream.readObject();

            return courses;

        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, "Une erreur s'est produite pendant le chargement des cours.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

    }

    /**
     * 
     * Méthode appelée lorsqu'une action est effectuée sur un composant de
     * l'interface
     * graphique.
     * 
     * @param e L'événement d'action déclenché.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        // Si l'utilisateur clique sur le boutton 'Charger'
        if (e.getSource() == chargeButton) {

            System.out.println("Charger les cours pour la session: " + choixSession.getSelectedItem().toString());
            try {

                List<Course> courses = this.loadCourses(choixSession.getSelectedItem().toString());

                this.updateTable(courses);

            } catch (Exception error) {
                JOptionPane.showMessageDialog(null, "Une erreur s'est produite.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        }

        // Si l'utilisateur clique sur le boutton 'Inscription'
        else if (e.getSource() == submitButton) {

            Course course = getSelectedCourse();

            if (matriculeText.getText().length() < 6) {

                JOptionPane.showMessageDialog(null,
                        "Le formulaire est invalide. " + "" + "Le champ 'Matricule' est invalide", "Error",
                        JOptionPane.ERROR_MESSAGE);

            } else if (course == null) {
                JOptionPane.showMessageDialog(null, "Veuillez sélectionner un cours.", "Error",
                        JOptionPane.ERROR_MESSAGE);

            } else {

                RegistrationForm registrationForm = new RegistrationForm(prenomText.getText(), nomText.getText(),
                        emailText.getText(), matriculeText.getText(),
                        new Course(course.getName(), course.getCode(), sessionActuelle));
                try {
                    this.registerForCourse(registrationForm);

                } catch (Exception error) {
                    JOptionPane.showMessageDialog(null, "Une erreur s'est produite.", "Error",
                            JOptionPane.ERROR_MESSAGE);

                }

            }
        // Si l'utilisateur change la session
        } else if (e.getSource() == choixSession) {
            sessionActuelle = (String) choixSession.getSelectedItem();

        }

    }

    /**
     * 
     * Envoie une demande d'inscription au serveur pour un étudiant et un cours
     * donnés.
     * 
     * @param registration Le formulaire d'inscription à envoyer.
     * @throws IOException            Si une erreur de communication réseau se
     *                                produit.
     * @throws ClassNotFoundException Si la classe Course est introuvable.
     */

    public void registerForCourse(RegistrationForm registration) throws IOException, ClassNotFoundException {

        System.out.println("Envoi de la demande d'inscription au serveur...: " +
                registration.toString());

        objectOutputStream.writeObject(Server.REGISTER_COMMAND);
        objectOutputStream.flush();

        objectOutputStream.writeObject(registration);
        objectOutputStream.flush();

        JOptionPane.showMessageDialog(null,
                "Félicitations! " + prenomText.getText() + " " + nomText.getText()
                        + "est inscrit(e) avec succès pour le cours "
                        + registration.getCourse().getCode() + "!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        connect();

    }

    /**
     * 
     * Met à jour le tableau de l'interface graphique avec la liste des cours
     * donnée.
     * 
     * @param courses La liste des cours à afficher.
     * @throws IOException Si une erreur de communication réseau se produit.
     */
    public void updateTable(List<Course> courses) throws IOException {

        connect();

        // Supprimer les cours existants dans le tableau
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.removeRow(i);
        }

        // Ajouter les cours de la session dans le tableau
        for (Course course : courses) {
            Object[] courseData = { course.getCode(), course.getName() };
            tableModel.addRow(courseData);
        }

    }

    /**
     * 
     * Récupère le cours sélectionné dans le tableau de l'interface graphique.
     * 
     * @return Le cours sélectionné, ou null si aucun cours n'est sélectionné.
     */
    private Course getSelectedCourse() {
        try {
            int selectedRow = table.getSelectedRow();

            if (selectedRow == -1) {
                throw new Exception("Action impossible: aucun cours n'est sélectionné.");
            }

            Course course = new Course((String) table.getValueAt(selectedRow, 1),
                    (String) table.getValueAt(selectedRow, 0), sessionActuelle);
            return course;
        } catch (Exception e) {
            return null;
        }

    }

}