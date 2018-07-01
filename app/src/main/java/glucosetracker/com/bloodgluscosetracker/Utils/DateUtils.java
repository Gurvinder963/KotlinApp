package glucosetracker.com.bloodgluscosetracker.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static final String dd_MM_yyyy = "dd/MM/yyyy";
    public static final String yyyy_MM_dd = "yyyy-MM-dd";
    public static final String SERVER_DATE = "yyyy-MM-dd'T'HH:mm:ss";
    private static DateUtils dateFactory;


    private static Locale locale = Locale.US;

    private DateUtils() {
        // set locale
        locale = Locale.US;
    }

    public static DateUtils getInstance() {
        if (dateFactory == null) {
            dateFactory = new DateUtils();
        }

        return dateFactory;
    }
    public String getDateFromDateFormat(Date inputDate, String formatDest) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatDest, locale);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inputDate);
        Date d = calendar.getTime();
        return sdf.format(d);
    }
}
