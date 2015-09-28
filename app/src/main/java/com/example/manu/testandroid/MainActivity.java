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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class MainActivity extends AppCompatActivity {
    // Constantes
    public final int TIMEOUT = 500;
    public final int PAUSE = 500;

    // Composantes
    private EditText ET_AdressePart1;
    private EditText ET_AdressePart2;
    private EditText ET_AdressePart3;
    private EditText ET_DebutPlage;
    private EditText ET_FinPlage;
    private EditText ET_NumeroPort;
    private ProgressBar PB_LoadBar;
    private TextView TV_Liste;
    private ScrollView SV_Scroll;

    // Variables
    public String adressePart1;
    public String adressePart2;
    public String adressePart3;
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
        ET_AdressePart1 = (EditText) findViewById(R.id.ET_Part1);
        ET_AdressePart2 = (EditText) findViewById(R.id.ET_Part2);
        ET_AdressePart3 = (EditText) findViewById(R.id.ET_Part3);
        ET_DebutPlage = (EditText) findViewById(R.id.editText2);
        ET_FinPlage = (EditText) findViewById(R.id.editText3);
        ET_NumeroPort = (EditText) findViewById(R.id.editText4);
        PB_LoadBar = (ProgressBar) findViewById(R.id.progressBar);
        TV_Liste = (TextView) findViewById(R.id.textView5);
        SV_Scroll = (ScrollView) findViewById(R.id.scrollView);
    }

    public void Demarrer(View v) {
        TV_Liste.setText("");
        // On vérifie que tous les champs sont remplis
        if (VerifierValeursEntrees()) {
            // Si tous les champs sont remplis, on affecte leur valeur au variables correspondantes
            adressePart1 = ET_AdressePart1.getText().toString();
            adressePart2 = ET_AdressePart2.getText().toString();
            adressePart3 = ET_AdressePart3.getText().toString();

            debut = ET_DebutPlage.getText().toString();
            fin = ET_FinPlage.getText().toString();
            port = ET_NumeroPort.getText().toString();
            numPort = Integer.parseInt(fin) - Integer.parseInt(debut);

            estEnArret = false;
            RechercherIps laRecherche = new RechercherIps();
            // Début de la recherche
            laRecherche.execute();
            adresse = adressePart1 + "." + adressePart2 + "." + adressePart3;
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

                try {
                    Socket soc = new Socket();
                    InetSocketAddress unISAddress = new InetSocketAddress(adresse + "." + i, Integer.parseInt(port));
                    soc.connect(unISAddress, TIMEOUT);
                    ipValides = soc.getRemoteSocketAddress().toString();
                }
                catch(SocketException so)
                {
                    //ipValides = so.toString();
                }
                catch(SocketTimeoutException ste)
                {
                    //ipValides = ste.toString();
                }
                catch (IOException ioe) {
                    //ipValides = ioe.toString();
                }

                /*
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
                */
                while (estEnArret) {
                    try {
                        Thread.sleep(PAUSE);
                    } catch (Exception e) {
                    }
                }
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
                SV_Scroll.fullScroll(View.FOCUS_DOWN);
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
            unSocket.setSoTimeout(leTimeOut);
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
        if (ET_AdressePart1.getText().toString().equals("") || ET_AdressePart2.getText().toString().equals("") || ET_AdressePart3.getText().equals("") || ET_DebutPlage.getText().toString().equals("") || ET_FinPlage.getText().toString().equals("") || ET_NumeroPort.getText().toString().equals(""))
            estVerifie = false;

        return estVerifie;
    }

    public boolean VerifierValeursEntrees() {
        boolean estValide = true;
        String messageErreur = "";

        if (ET_AdressePart1.getText().toString().equals("") || ET_AdressePart2.getText().toString().equals("") || ET_AdressePart3.getText().equals("") || ET_DebutPlage.getText().toString().equals("") || ET_FinPlage.getText().toString().equals("") || ET_NumeroPort.getText().toString().equals(""))
        {
            estValide = false;
            messageErreur = "ERREUR : Les champs ne doivent pas être vides";
        }
        // La début de la plage doit être plus petite que celle de la fin
        else if (Integer.parseInt(ET_FinPlage.getText().toString()) <= Integer.parseInt(ET_DebutPlage.getText().toString())) {
            estValide = false;
            messageErreur = "ERREUR : La début de la plage doit être plus petite que celle de la fin";
        } else if (Integer.parseInt(ET_FinPlage.getText().toString()) < 2 || Integer.parseInt(ET_DebutPlage.getText().toString()) < 2) {
            estValide = false;
            messageErreur = "ERREUR : La valeur du champs doit être supérieure à 2";
        } else if (Integer.parseInt(ET_FinPlage.getText().toString()) > 254 || Integer.parseInt(ET_DebutPlage.getText().toString()) > 254) {
            estValide = false;
            messageErreur = "ERREUR : La valeur du champs doit être inférieure à 255";
        }
        else if(Integer.parseInt(ET_AdressePart1.getText().toString()) < 1 || Integer.parseInt(ET_AdressePart1.getText().toString()) > 255 || Integer.parseInt(ET_AdressePart2.getText().toString()) < 1 || Integer.parseInt(ET_AdressePart2.getText().toString()) > 255 ||Integer.parseInt(ET_AdressePart3.getText().toString()) < 1 || Integer.parseInt(ET_AdressePart3.getText().toString()) > 255)
        {
            estValide = false;
            messageErreur = "ERREUR : La valeur du champs doit être inférieure à 255 et supérieure à 1";
        }
        else if(Integer.parseInt(ET_NumeroPort.getText().toString()) < 1 || Integer.parseInt(ET_NumeroPort.getText().toString()) > 65535)
        {
            estValide = false;
            messageErreur = "ERREUR : Le numéro de port doit se situer en 1 et 65535";
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
