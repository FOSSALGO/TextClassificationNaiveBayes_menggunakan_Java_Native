package fosalgo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class TestBacaDataset {
    public static void main(String[] args) {
        File file = new File("src/fosalgo/datatraining.csv");
        ArrayList<Data>dataset = null;
        try {
            Scanner sc = new Scanner(file);
            dataset = new ArrayList<>();
            while(sc.hasNextLine()){
                String baris = sc.nextLine();
                String[]kolom = baris.split(";");
                String judul = kolom[3];
                String namaDosenPembimbing = kolom[4];
                //System.out.println(kolom[3]+" : "+kolom[4]);
                Data data = new Data(judul, namaDosenPembimbing);
                dataset.add(data);   
            }
            //System.out.println(dataset);
            for (Data d: dataset) {
                d.tokenizing();
                System.out.println(Arrays.toString(d.tokens));
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
