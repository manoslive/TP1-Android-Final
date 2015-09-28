package com.example.manu.testandroid;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    // Constantes
    public final int TIMEOUT = 500;
    public final int PAUSE = 500;

    // Composantes
    private EditText ET_AdresseSousReseau;
    private EditText ET_DebutPlage;
    private EditText ET_FinPlage;
    private EditText ET_NumeroPort;
    private ProgressBar PB_LoadBar;
    private TextView TV_Liste;

    // Variables
    public String adresse;
    public String debut;
    public String fin;
    public String port;
    public int numPort;
    public boolean estEnArret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupération des composants
        ET_AdresseSousReseau = (EditText) findViewById(R.id.editText1);
        ET_DebutPlage = (EditText) findViewById(R.id.editText2);
        ET_FinPlage = (EditText) findViewById(R.id.editText3);
        ET_NumeroPort = (EditText) findViewById(R.id.editText4);
        PB_LoadBar = (ProgressBar) findViewById(R.id.progressBar);
        TV_Liste = (TextView) findViewById(R.id.textView5);
    }

    public void Demarrer(View v) {
        // On vérifie que tous les champs sont remplis
        if (!VerifierChamps()) {
            Toast message = Toast.makeText(MainActivity.this,
                    // Message affiché à l'utilisateur
                    "Un des champs requis est vide. Veuillez le remplir IMMÉDIATEMENT!", Toast.LENGTH_SHORT);
            message.show();
        } else {
            // Si tous les champs sont remplis, on affecte leur valeur au variables correspondantes
            adresse = ET_AdresseSousReseau.getText().toString();
            debut = ET_DebutPlage.getText().toString();
            fin = ET_FinPlage.getText().toString();
            port = ET_NumeroPort.getText().toString();
            numPort = Integer.parseInt(fin) - Integer.parseInt(debut);
        }
        if (VerifierValeursEntrees()) {
            estEnArret = false;
            RechercherIps laRecherche = new RechercherIps();
            // Début de la recherche
            laRecherche.execute();
        }

    }

    public class RechercherIps extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),
                    "Début de la recherche Ip",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... args) {
            //int progres;
            // Variables contenant les ips valides
            String ipValides;

            for (int i = Integer.parseInt(debut); i <= Integer.parseInt(fin); i++) {
                ipValides = "";

                // Vérification de chaque adresse ip
                if (testerIpPort(adresse + "." + i, Integer.parseInt(port), TIMEOUT)) {
                    ipValides = adresse + "." + i;
                }

                // Lorsque le bouton suspendre est appuyé, on active la boucle infinie. Elle est arrêtée lorsque réappuyée.
                while (estEnArret) {
                    try {
                        Thread.sleep(PAUSE);
                    } catch (Exception e) {
                    }
                }

                // la méthode publishProgress met à jour l'IUG en
                // invoquant indirectement la méthode onProgressUpdate
                publishProgress(i + "", ipValides);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... valeurs) {
            super.onProgressUpdate(valeurs);
            // valeurs[0] contient l'adresse courante. valeurs[1] contient le dernier ip valide

            // Mise à jour de la ProgressBar
            int progression = (Integer.parseInt(valeurs[0]) - Integer.parseInt(debut)) * 100 / numPort;
            PB_LoadBar.setProgress(progression);

            // Mise à jour du TextView si l'ip est valide.
            if (!valeurs[1].equals("")) {
                TV_Liste.append(valeurs[1] + "\n");
            }
        }

        @Override
        protected void onPostExecute(Void resultat) {
            Toast.makeText(getApplicationContext(),
                    "La recherche est terminée",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public boolean testerIpPort(String leIp, int lePort, int leTimeOut) {
        boolean estValide = true;
        try {
            // On crée le socket
            Socket unSocket = new Socket();
            InetSocketAddress unISAddress = new InetSocketAddress(leIp, lePort);

            // On met le timeout
            unSocket.connect(unISAddress, leTimeOut);
            unSocket.close();
        } catch (Exception e) {
            // Dans le cas d'une connection qui a échouée, on met la booléenne à false
            estValide = false;
        }
        return estValide;
    }

    public boolean VerifierChamps() {
        boolean estVerifie = true;

        // Si un des EditText est vide la booléenne est mise à false et on ne peut pas démarrer
        if (ET_AdresseSousReseau.getText().equals("") || ET_DebutPlage.getText().equals("") || ET_FinPlage.getText().equals("") || ET_NumeroPort.getText().equals(""))
            estVerifie = false;

        return estVerifie;
    }

    public boolean VerifierValeursEntrees() {
        boolean estValide = true;
        String messageErreur = "";

        // La début de la plage doit être plus petite que celle de la fin
        if (Integer.parseInt(fin) <= Integer.parseInt(debut)) {
            estValide = false;
            messageErreur = "ERREUR : La début de la plage doit être plus petite que celle de la fin";
        } else if (Integer.parseInt(fin) < 2 || Integer.parseInt(debut) < 2) {
            estValide = false;
            messageErreur = "ERREUR : La valeur du champs doit être supérieure à 2";
        } else if (Integer.parseInt(fin) > 254 || Integer.parseInt(debut) > 254) {
            estValide = false;
            messageErreur = "ERREUR : La valeur du champs doit être inférieure à 255";
        }

        // Si le champs est invalide, on affiche le message d'erreur
        if (!estValide) {
            Toast Unmessage = Toast.makeText(MainActivity.this, messageErreur, Toast.LENGTH_LONG);
            Unmessage.show();
        }
        return estValide;
    }

    public void mettreSurPause(View v)
    {
        estEnArret = true;
    }
}
