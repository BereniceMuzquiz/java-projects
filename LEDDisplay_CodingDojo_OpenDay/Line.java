import java.util.*;

public class Line {

    private static final int MAGIC_NUMBER = 5342145;
    public static int number = 0;

    public static List<String> number_line_1 = new ArrayList<String>();
    public static List<String> number_line_2 = new ArrayList<String>();
    public static List<String> number_line_3 = new ArrayList<String>();

    public static String line_1;
    public static String line_2;
    public static String line_3;

    public static void main(String[] args) throws Exception {

        getZahl(MAGIC_NUMBER);

        printNumber();
    }

    private static final String STRICH = "_";
    private static final String SPACE = " ";
    private static final String VERTICAL_LINE = "|";

    public static String getLine(int number) {

        HashMap<Integer, String> LineMap = new HashMap<Integer, String>(); // create Map called LineMap

        LineMap.put(1, SPACE + SPACE + VERTICAL_LINE); // instanz object class Line
        LineMap.put(2, VERTICAL_LINE + STRICH + SPACE);
        LineMap.put(3, SPACE + SPACE + STRICH);
        LineMap.put(4, SPACE + SPACE + VERTICAL_LINE);
        LineMap.put(5, SPACE + STRICH + SPACE);
        LineMap.put(6, SPACE + STRICH + VERTICAL_LINE);
        LineMap.put(7, SPACE + SPACE + SPACE);
        LineMap.put(8, VERTICAL_LINE + STRICH + VERTICAL_LINE);

        if (number == 1) {
            line_1 = LineMap.get(7);
            line_2 = LineMap.get(1);
            line_3 = LineMap.get(1);
            addColumn();
        }
        if (number == 2) {
            line_1 = LineMap.get(5);
            line_2 = LineMap.get(6);
            line_3 = LineMap.get(2);
            addColumn();
        }
        if (number == 3) {
            line_1 = LineMap.get(5);
            line_2 = LineMap.get(6);
            line_3 = LineMap.get(6);
            addColumn();
        }
        if (number == 4) {
            line_1 = LineMap.get(7);
            line_2 = LineMap.get(8);
            line_3 = LineMap.get(4);
            addColumn();
        }
        if (number == 5) {
            line_1 = LineMap.get(5);
            line_2 = LineMap.get(2);
            line_3 = LineMap.get(6);
            addColumn();
        }

        return ""; // number_line_1 + "\n" + number_line_2 + "\n" + number_line_3;

    }

    public static String getZahl(int number) {

        String zahlString = String.valueOf(MAGIC_NUMBER);

        for (int i = 0; i < zahlString.length(); i++) {
            char ziffer = zahlString.charAt(i);
            number = Integer.parseInt(String.valueOf(ziffer));
            getLine(number);
        }
        return "";// (String.valueOf(number_line_1));
    }

    public static void addColumn() {
        number_line_1.add(line_1);
        number_line_2.add(line_2);
        number_line_3.add(line_3);
    }

    public static void printNumber() {
        Iterator<String> itr1 = number_line_1.iterator();
        while (itr1.hasNext()) {
            System.out.print(itr1.next());
        }
        System.out.println("");
        Iterator<String> itr2 = number_line_2.iterator();
        while (itr2.hasNext()) {
            System.out.print(itr2.next());
        }
        System.out.println("");
        Iterator<String> itr3 = number_line_3.iterator();
        while (itr3.hasNext()) {
            System.out.print(itr3.next());
        }
        System.out.println("");

    }

}
