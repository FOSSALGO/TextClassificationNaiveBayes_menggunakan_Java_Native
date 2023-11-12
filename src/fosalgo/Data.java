package fosalgo;

public class Data {
    String judul;
    String namaDosenPembimbing;
    String[]tokens;

    public Data(String judul, String namaDosenPembimbing) {
        this.judul = judul;
        this.namaDosenPembimbing = namaDosenPembimbing;
    }

    @Override
    public String toString() {
        return "Data{" + "judul=" + judul + ", namaDosenPembimbing=" + namaDosenPembimbing + '}';
    }
    
    public void toLowerCase(){
        judul = judul.toLowerCase();
        namaDosenPembimbing = namaDosenPembimbing.toLowerCase();
    }
    
    public String filtering(String regex){
        judul = judul.replaceAll(regex, "");
        return judul;
    }
    
    public String[]tokenizing(){
        if(judul!=null&&judul.length()>0){
            tokens = judul.split("\\s+");
        }
        return tokens;
    }
}
