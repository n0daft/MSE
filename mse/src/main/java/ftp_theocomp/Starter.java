package ftp_theocomp;

/**
 * Created by n0daft on 17.11.2014.
 */
public class Starter {

    public static void main(String[] args){

        Integer[] result = Additionsketten.calculateNumber(30);
        System.out.println(String.format("calculated number %d in %d iterations.", result[0], result[1]));

    }
}
