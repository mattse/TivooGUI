import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import parser.BasketballParser;
import parser.DukeParser;
import parser.GoogleParser;
import parser.NFLParser;
import parser.Parser;
import parser.TVParser;
import view.Publisher;
import calendar.CalendarFilter;
import calendar.XMLCal;
import event.Event;

/**
 * @author Yi Ding
 * @author Kirill Klimuk
 * @author Leo Rofe
 * @author Matthew Tse
 * @author Chenbo Zhu
 */
public class TivooSystem {
	// public final static String[] files = {"resources/tvcal.xml",
	// "resources/dbbcal.xml"};
	// public final static String[] files = {"resources/dukecal.xml",
	// "resources/googlecal.xml"};
	public final static String[] files = { "resources/dbbcal.xml",
			"resources/nflcal.xml" };
	public final static Calendar before = new GregorianCalendar(2011, 8, 12);
	public final static Calendar after = new GregorianCalendar(2011, 10, 17);

	public XMLCal parse(String[] files) {
		ArrayList<Event> events = new ArrayList<Event>();
		// loops through inputed files
		for (int i = 0; i < files.length; i++) {
			File inputXml = new File(files[i]);

			SAXReader saxReader = new SAXReader();
			try {
				Document document = saxReader.read(inputXml);
				Element root = document.getRootElement();

				ArrayList<Parser.Factory> factory = new ArrayList<Parser.Factory>();
				factory.add(new DukeParser.Factory());
				factory.add(new GoogleParser.Factory());
				factory.add(new NFLParser.Factory());
				factory.add(new BasketballParser.Factory());
				factory.add(new TVParser.Factory());

				// checks to see what type of calendar was inputed and builds
				// appropriate calendar
				// parses calendar and add all events to a list of events.
				boolean exists = false;
				for (Parser.Factory check : factory)
					if (check.isType(root)) {
						events.addAll(check.buildCalendar(root).parse()
								.getEvents());
						exists = true;
					}

				if (!exists)
					throw new Error("This calendar type is not recognized.");

			} catch (DocumentException e) {
				throw new Error(e.getMessage());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return new XMLCal(events);
	}

	public static void main(String[] argv) throws ParseException, IOException,
			NoSuchAlgorithmException {

		TivooSystem s = new TivooSystem();
		XMLCal calendar = s.parse(files);
		// here you have the option to filter and/or sort your files.
		// You need to first create an instance of a filter/sorter class (ex.
		// CalendarFilter or KeywordFilter)
		// then pick the method within that class that filters/sorts it the way
		// you like.
		CalendarFilter filter = new CalendarFilter(calendar);
		filter.filterDay(before);

		// Sorter sorter = new Sorter(calendar);
		// sorter.sortByTitle(false);

		// publishes events
		Publisher publisher = new Publisher(calendar);
		publisher.publish();
	}

}
