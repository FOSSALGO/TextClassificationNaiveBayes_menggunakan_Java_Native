package fosalgo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class NaiveBayes {

    public static void main(String[] args) {
        // START OF PROSES TRANING NAIVE BAYES--------------------------------
        //----------------------------------------------------------------------
        // Step-1: Baca dataset dari file csv
        //----------------------------------------------------------------------
        File file = new File("src/fosalgo/datatraining.csv");
        ArrayList<Data> dataset = null;
        try {
            Scanner sc = new Scanner(file);
            dataset = new ArrayList<>();
            while (sc.hasNextLine()) {
                String baris = sc.nextLine();
                String[] kolom = baris.split(";");
                String judul = kolom[3];
                String namaDosenPembimbing = kolom[4];
                Data data = new Data(judul, namaDosenPembimbing);
                dataset.add(data);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        if (dataset != null && dataset.size() > 0) {
            //----------------------------------------------------------------------
            // Step-2: CASE FOLDING
            //----------------------------------------------------------------------
            for (Data d:dataset) {
                d.toLowerCase();//mengubah case judul dan nama dosen pembimbing ke lowercase
                //System.out.println(d);
            }
            
            //----------------------------------------------------------------------
            // Step-3: FILTERING
            //----------------------------------------------------------------------
            // persiapkan stopSymbols
            String[] stopSymbols = {"[0-9]","\\@"};
            String[] stopWords = {"dan", "untuk", "atau", "di", "dengan"};
            
            //filtering symbol
            for (String s : stopSymbols) {
                for (Data d : dataset) {
                    d.filtering(s);
                }
            }
            
            //filtering word
            for (String s : stopWords) {
                for (Data d : dataset) {
                    d.filtering(s);
                }
            } 
            
            //----------------------------------------------------------------------
            // Step-4: TOKENIZING
            //----------------------------------------------------------------------
            for (Data d:dataset) {
                d.tokenizing();
            }
            
            //Test Print Dataset
            for (Data d:dataset) {
                System.out.println(Arrays.toString(d.tokens));
            }

            //----------------------------------------------------------------------
            // Step-5: Extract Class dari dataset
            //----------------------------------------------------------------------
            ArrayList<String> stringKelas = new ArrayList<>();
            for (Data d:dataset) {
                String pembimbing = d.namaDosenPembimbing;
                if (!stringKelas.contains(pembimbing)) {
                    stringKelas.add(pembimbing);
                }
            }
            
            //print class
            System.out.println("Class: " + stringKelas);
            System.out.println("number of class: " + stringKelas.size());
            
            //----------------------------------------------------------------------
            // Step-6: Extract Fitur dari dataset
            //----------------------------------------------------------------------
            ArrayList<String> stringFitur = new ArrayList<>();
            for (Data d:dataset) {
                String[] tokens = d.tokens;
                for (String token: tokens) {
                    if(!stringFitur.contains(token)){
                        stringFitur.add(token);
                    }
                }
            }
            //print fitur
            System.out.println("Fitur: " + stringFitur);
            System.out.println("number of fitur: " + stringFitur.size());
            
            //----------------------------------------------------------------------
            // Step-7: Hitung Frekuensi Class dan Fitur
            //----------------------------------------------------------------------
            int nKelas = stringKelas.size(); //banyaknya Class
            int nFitur = stringFitur.size(); //banyaknya feature
            // deklarasikan array frekuensiKELAS dan frekuensiFITUR
            // untuk menyimpan hasil perhitungan frekuensi document dan fitur-fitur di document
            
            int[] frekuensiKelas = new int[nKelas];//frekuensi class
            int[][] frekuensiFitur = new int[nKelas][nFitur];//frekuensi fitur di tiap class
            
            //menghitung frekuensi class dan fitur dari dataset
            for (Data d : dataset) {
                String namaDosenPembimbing = d.namaDosenPembimbing;
                String[] tokens = d.tokens;
                int indexKelas = -1;
                //hitung frekuensi class
                for (int i = 0; i < nKelas; i++) {
                    if (namaDosenPembimbing.equalsIgnoreCase(stringKelas.get(i))){
                        indexKelas = i;
                        frekuensiKelas[indexKelas]++;//increment frekuensi class
                        break;
                    }
                }
                
                //hitung frekuensi fitur di class
                for (String token : tokens) {
                    for (int j = 0; j < nFitur; j++) {
                        if (token.equalsIgnoreCase(stringFitur.get(j))) {
                            frekuensiFitur[indexKelas][j]++;
                            break;
                        }
                    }
                }
            }//end of hitung frekuensi class-i dan frekuensi fitur di class-i
            
            //----------------------------------------------------------------------
            // Step-8: Proses Training untuk membangun model Naive Bayes
            //----------------------------------------------------------------------
            double[] pC = null;//peluang class-j
            double[][] pWC = null;//peluang word-k di class-j
            int[] sumFrekuensiFiturTiapKelas = null;
            int nData = dataset.size();
            
            //1. inisialisasi model naive bayes
            pC = new double[nKelas];
            pWC = new double[nKelas][nFitur];
            sumFrekuensiFiturTiapKelas = new int[nKelas];
            
            // 2. hitung probabilitas  class-j (pCj)
            for (int j = 0; j < nKelas; j++) {
                int f = frekuensiKelas[j];//frekuensi class-j
                double probabilityKelas = (double)f/(double)nData;
                pC[j] = probabilityKelas;
                // hitung total frekuensi FITUR di masing-masing KELAS
                for (int k = 0; k < nFitur; k++) {
                    sumFrekuensiFiturTiapKelas[j] += frekuensiFitur[j][k];
                }                
            }
            
            // 3.hitung prob FITUR-FITUR (P(WK |cj))
            double wSize = stringFitur.size();
            for (int j = 0; j < nKelas; j++) {
                for (int k = 0; k < nFitur; k++) {
                     double probabilityFitur_k_di_kelas_j = (double) (frekuensiFitur[j][k] + 1) / (double) (sumFrekuensiFiturTiapKelas[j] + wSize);
                      pWC[j][k] = probabilityFitur_k_di_kelas_j;
                }
            }
            // END OF PROSES TRANING NAIVE BAYES--------------------------------
            
            //----------------------------------------------------------------------
            // Step-9: PROSES TESTING
            //----------------------------------------------------------------------
            String judulBaru = "Implementasi Algoritma Spider Monkey Optimization untuk desain Bendungan";
            String dosenPembimbing = "NULL";
            
            // identifikasi keberadaan FITUR di document baru dan hitung probability naive bayesnya
            int[] frekuensiFiturBaru = null;
            double[] probabilitasNB = null;
            try {
                frekuensiFiturBaru = new int[nFitur];
                judulBaru = judulBaru.toLowerCase();
                String[] tokens = judulBaru.split("\\s+");
                for (int j = 0; j < tokens.length; j++) {
                    for (int k = 0; k < nFitur; k++) {
                        if (tokens[j].equalsIgnoreCase(stringFitur.get(k))) {
                            frekuensiFiturBaru[k]++;
                            break;
                        }
                    }
                }
                
                //hitung peluang judul baru
                double arg_max = Double.MIN_VALUE;
                int iMAX = -1;
                
                probabilitasNB = new double[nKelas];
                for (int j = 0; j < nKelas; j++) {
                    probabilitasNB[j] = pC[j];
                    for (int k = 0; k < nFitur; k++) {
                        if (frekuensiFiturBaru[k] > 0) {
                            probabilitasNB[j] *= pWC[j][k];
                        }
                    }
                    
                    //evaluasi arg_max
                    if(probabilitasNB[j]>arg_max){
                        arg_max = probabilitasNB[j];
                        iMAX = j;
                    }                    
                }//
                
                //penentuan class
                if(iMAX>-1){
                    String hasilKlasifikasi = stringKelas.get(iMAX);
                    System.out.println("Hasil Klasifikasi: " + hasilKlasifikasi);
                }                
                
            }catch (Exception e) {
                e.printStackTrace();
            }
            
        }//end of if (dataset != null && dataset.size() > 0)

    }//end of main method
}
