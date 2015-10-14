package ftp_theocomp;

/**
 * Created by n0daft on 17.11.2014.
 */
public class Additionsketten {



    public static Integer[] calculateNumber(int n){
        int bound = n%2 == 0 ? n/2 : (n+1)/2;

        int x0 = 0;
        int x1 = 1;

        // We already did one iteration theoretically.
        int count = 1;

        for(int i=1; i<bound; i++){

            x0 = x1;
            x1 = x1+ 1;

            count++;

        }

        int calculatedNumber = (n%2 == 0) ? x1+x1 : x0 + x1;

        return new Integer[]{calculatedNumber, count};
    }

}
