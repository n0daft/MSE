package tsm_alg.ex01;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.util.UniqueCoordinateArrayFilter;

import java.util.*;



/**
 * Computes the convex hull of a {@link com.vividsolutions.jts.geom.Geometry}. The convex hull is the
 * smallest convex Geometry that contains all the points in the input Geometry.
 * <p>
 * Uses an incremental approach
 * 
 * @version 1.0
 */
public class ConvexHull2 {
	private GeometryFactory geomFactory;
	private Coordinate[] inputPts;

	/**
	 * Create a new convex hull construction for the input {@link com.vividsolutions.jts.geom.Geometry}.
	 */
	public ConvexHull2(Geometry geometry) {
		this(extractCoordinates(geometry), geometry.getFactory());
	}

	/**
	 * Create a new convex hull construction for the input {@link com.vividsolutions.jts.geom.Coordinate}
	 * array.
	 */
	public ConvexHull2(Coordinate[] pts, GeometryFactory geomFactory) {
		inputPts = pts;
		this.geomFactory = geomFactory;
	}

	private static Coordinate[] extractCoordinates(Geometry geom) {
		UniqueCoordinateArrayFilter filter = new UniqueCoordinateArrayFilter();
		geom.apply(filter);
		return filter.getCoordinates();
	}

	/**
	 * Returns a {@link com.vividsolutions.jts.geom.Geometry} that represents the convex hull of the input
	 * geometry. The returned geometry contains the minimal number of points
	 * needed to represent the convex hull. In particular, no more than two
	 * consecutive points will be collinear.
	 * 
	 * @return if the convex hull contains 3 or more points, a {@link com.vividsolutions.jts.geom.Polygon};
	 *         2 points, a {@link com.vividsolutions.jts.geom.LineString}; 1 point, a {@link com.vividsolutions.jts.geom.Point}; 0
	 *         points, an empty {@link com.vividsolutions.jts.geom.GeometryCollection}.
	 */
	public Geometry getConvexHull() {
		if (inputPts.length == 0) {
			return geomFactory.createGeometryCollection(null);
		}
		if (inputPts.length == 1) {
			return geomFactory.createPoint(inputPts[0]);
		}
		if (inputPts.length == 2) {
			return geomFactory.createLineString(inputPts);
		}

        // Sort the coordinates by their x values.
        sortCoordinatesByXAxis(inputPts);

        // Calculate the two parts of the hull.
        List<Coordinate> upperHull = createUpperHull();
        List<Coordinate> lowerHull = createLowerHull();

        // Merge the two parts to form one convex hull.
        Coordinate[] hull = mergeHulls(upperHull, lowerHull);

		// compute correct geometry
		if (hull.length == 3) {
			return geomFactory.createLineString(new Coordinate[] { hull[0], hull[1] });
		}
		LinearRing linearRing = geomFactory.createLinearRing(hull);
		return geomFactory.createPolygon(linearRing, null);
	}


    /**
     * Creates the upper part of the convex hull by moving from
     * left to right on the sorted coordinates and continuously checking
     * if the convex condition is ok.
     * @return
     */
    public List<Coordinate> createUpperHull(){
        List<Coordinate> upperHull = new ArrayList();

        upperHull.add(inputPts[0]);
        upperHull.add(inputPts[1]);

        for (int i=2; i<inputPts.length; i++){
            upperHull.add(inputPts[i]);
            constructHull(upperHull);
        }

        return upperHull;
    }

    /**
     * Creates the lower part of the convex hull by moving from
     * right to left on the sorted coordinates and continuously checking
     * if the convex condition is ok.
     * @return
     */
    public List<Coordinate> createLowerHull(){
        List<Coordinate> lowerHull = new ArrayList();

        // Add the two most right points to the list.
        lowerHull.add(inputPts[inputPts.length-1]);
        lowerHull.add(inputPts[inputPts.length-2]);

        for (int i=inputPts.length-3; i>=0; i--){
            lowerHull.add(inputPts[i]);
            constructHull(lowerHull);
        }

        return lowerHull;
    }


    /**
     * Constructs the convex Hull of the given points by using a incremental construction approach.
     * Step by step points are added to the hull and checked if the convex condition is ok.
     * @param hull
     */
    public void constructHull(List<Coordinate> hull){

        int size = hull.size();
        while(hull.size() > 2 && !makesRightTurn(hull.get(size-3), hull.get(size-2), hull.get(size-1))){
            hull.remove(hull.get(size-2));
            size--;
        }

    }

    /**
     * Calculates from the point of view from left to right, if the three given points make a right turn or not.
     * @param p1
     * @param p2
     * @param p3
     * @return
     */
    public boolean makesRightTurn(Coordinate p1, Coordinate p2, Coordinate p3){

        // Computes the orientation of p2 to the directed line segment p1-p3
        int orientation = CGAlgorithms.computeOrientation(p1, p3, p2);
        return orientation == 1;

    }

    public Coordinate[] mergeHulls(List<Coordinate> upperHull, List<Coordinate> lowerHull){

        //upperHull.remove(upperHull.size()-1);
        //upperHull.addAll(1, lowerHull);

        lowerHull.remove(0);
        upperHull.addAll(lowerHull);

        return upperHull.toArray(new Coordinate[upperHull.size()]);
    }


    public void sortCoordinatesByXAxis(Coordinate[] coordinates){
        Arrays.sort(coordinates, new Comparator<Coordinate>() {
            public int compare(Coordinate o1, Coordinate o2) {
                if(o1.x == o2.x){
                    return Double.compare(o1.y, o2.y);
                }else{
                    return Double.compare(o1.x, o2.x);
                }

            }
        });
    }
}