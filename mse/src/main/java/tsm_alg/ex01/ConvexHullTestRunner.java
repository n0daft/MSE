package tsm_alg.ex01;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileReader;
import java.io.IOException;

public class ConvexHullTestRunner {
	//private static String input = "E:/Dropbox/FHNW/_7. Semester/TSM_Alg/jts-1.13/testxml/general/TestConvexHull.xml";
	//private static String input = "E:/Dropbox/FHNW/_7. Semester/TSM_Alg/jts-1.13/testxml/general/TestConvexHull-big.xml";
    //private static String input = "E:/Dropbox/FHNW/_7. Semester/TSM_Alg/jts-1.13/testxml/general/OwnTestConvexHull.xml";

    private static String input = "/Users/n0daft/Dropbox/FHNW/7. Semester/TSM_Alg/jts-1.13/testxml/general/TestConvexHull.xml";

    
	
	private static boolean runCase(Geometry gIn, Geometry gSolution) {
		if (gIn == null || gSolution == null) return false;
		
		//Geometry gResult = new ConvexHull(gIn).getConvexHull();
		Geometry gResult = new ConvexHull2(gIn).getConvexHull();
		
		System.out.println("Result    = " + gResult);
		System.out.println("Solution  = " + gSolution);
		
		if (gSolution.equals(gResult)) {
			System.out.println("Result: ok\n");
			return true;
		} else {
			System.out.println("*** Wrong Result ***\n");
			return false;
		}
	}
	
	public static void main(String[] args) throws ParseException {
		if (input == null || input.isEmpty()) {
			if (args.length == 0) { 
				System.err.println("Usage: java ConvexHullTestRunner file" ); 
				return;
			}
			input = args[0];
		}

		int nRuns = 0, nFailures = 0, nExceptions = 0;
		long time = 0;
		
		try {
			FileReader fr = new FileReader(input);
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader parser = factory.createXMLStreamReader(fr);
			boolean inDesc = false, inInput = false, inSolution = false;
			StringBuilder sb = new StringBuilder();
			String s;
			GeometryFactory fact = new GeometryFactory();
			WKTReader wktRdr = new WKTReader(fact);
			Geometry gIn = null, gSolution = null;
			long tStart = System.currentTimeMillis();
			
			for (int event = parser.next(); event != XMLStreamConstants.END_DOCUMENT; event = parser.next()) {
				switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					s = parser.getLocalName();
					if (s.equals("desc")) inDesc = true;
					if (s.equals("a")) inInput = true;
					if (s.equals("op")) inSolution = true;
					sb.setLength(0);
					break;
				case XMLStreamConstants.END_ELEMENT:
					s = parser.getLocalName();
					if (s.equals("desc")) inDesc = false;
					if (s.equals("a")) {
						inInput = false;
						gIn = wktRdr.read(sb.toString());
					}
					if (s.equals("op")) {
						inSolution = false;
						gSolution = wktRdr.read(sb.toString());
						try {
							nRuns++;
							if (!runCase(gIn, gSolution)) {
								nFailures++;
							}
						} catch(Exception ex) {
							System.out.println("Exception: " + ex.getMessage() + "\n");
							nExceptions++;
						}
					}
					break;
				case XMLStreamConstants.CHARACTERS:
					s = parser.getText();
					if (inDesc) 
						System.out.println(s);
					if (inInput) {
						sb.append(s);
					}
					if (inSolution) {
						sb.append(s);
					}
					break;
				case XMLStreamConstants.CDATA:
					break;
				} // end switch
			} // end while
			
			long tEnd = System.currentTimeMillis();
			time = tEnd - tStart;
			
			parser.close();
		} catch (XMLStreamException ex) {
			System.out.println(ex);
		} catch (IOException ex) {
			System.out.println("IOException while parsing " + input);
		}

		System.out.println("" + nRuns + " tests (successful: " + (nRuns - nFailures - nExceptions) + ", failures: " + nFailures + ", exceptions: " + nExceptions + ")");
		System.out.println("" + time + " ms");
	}
}
