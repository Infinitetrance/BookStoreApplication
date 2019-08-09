package bookStore.ui.commons;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;

import oracle.jbo.domain.Date;


public class Utils {

    /**
     *Checks for following criteras
     *[ Not null, Not Empty, Length atleast 8, Contains atleast one lower case, Contains atleast one upper case, Contains atleast one digit,
     *Contains atleast one special(@#;%) character ]
     */
    public static boolean doesComplyPasswordCriteria(String password) {
        if (password == null || password.isEmpty())
            return false;
        if (password.length() < 8)
            return false;
        String[] criterias = new String[] { ".*[A-Z].*", ".*[a-z].*", ".*[0-9].*", ".*[@#;%].*" };
        for (String criteria : criterias) {
            if (!password.matches(criteria))
                return false;
        }
        return true;
    }

    private static void doesComplyPasswordCriteria_Test() {
        String[] passwords =
            new String[] { null, "", "len4", "abcdefgh", "ABCDEFGH", "abcdEFGH", "abcdEFG9", "$bcdEFG9", "$bcdEFG*", "@bcdEFG9", "@bcd3FG#",
                           "@bcd3FG#z;" };
        for (String password : passwords)
            System.out.println(doesComplyPasswordCriteria(password) + " :: " + password);
    }

    public static boolean isNullOrEmpty(String str) {
        if (str == null || str.isEmpty())
            return true;
        else
            return false;
    }

    public static Date addDaystoDate(String formate, Date before, int dayOfMonth) {
        SimpleDateFormat sdf = new SimpleDateFormat(formate);
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(before + ""));
            calendar.add(Calendar.DATE, dayOfMonth);
            return new Date(sdf.format(calendar.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addDaystoDate_Test() {
        Date b = new Date(Date.getCurrentDate());
        System.out.println(b);
        System.out.println(addDaystoDate("yyyy-MM-dd", b, 25));
    }

    public static void main(String[] args) {
        System.out.println("bookStore.ui.commons.Utils");
        System.out.println("\u2713");
    }
}
