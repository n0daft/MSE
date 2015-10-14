package tsm_alg.helpers;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Created by n0daft on 06.10.2014.
 */
public class VectorHelper {

    public static double det(double a1, double a2, double b1, double b2){
        return (a1 * b2) - (a2 * b1);
    }

    public static Coordinate crossProduct(Coordinate v1, Coordinate v2){

        double x = det(v1.y, v1.z, v2.y, v2.z);
        double y = -det(v1.x, v1.z, v2.x, v2.z);
        double z = det(v1.x, v1.y, v2.x, v2.y);
        return new Coordinate(x, y, z);
    }

}
