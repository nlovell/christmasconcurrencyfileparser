package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A way to parse standard UCan't
 * <p>
 * Author: [redacted]
 * Created: 20/12/2019
 */
public class FileParser {

    private boolean printEnabled;

    private static FileParser single_instance = null;

    /**
     * Instantiates a new File parser.
     *
     * @return the file parser
     */
    public static FileParser getInstance() {
        if (single_instance == null)
            single_instance = new FileParser();

        return single_instance;
    }

    /**
     * Instantiates a new File parser.
     *
     * @param file           the file
     * @param outputSource   boolean to enable output of the source file line-for-line to the console
     * @param enablePrinting boolean to enable log-style printing
     */
    public boolean parseFile(String file, final boolean outputSource, final boolean enablePrinting) {
        this.printEnabled = enablePrinting;

        outputTitle();
        togglePrint("Attempting to parse file: " + file);

        String[] fileToParse = new String[0];
        try {
            fileToParse = openFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (outputSource) {
            togglePrint("----------------------------");
            togglePrint("Source file:\r\n");
            for (String lineToParse : fileToParse) {
                togglePrint("    " + lineToParse);
            }
            togglePrint("\r\nEnd of source.");
        }

        for (String lineToParse : fileToParse) {
            if (lineToParse != null)
                regexFilter(lineToParse);
        }

        return true;
    }

    /**
     * Accesses the file and reads it line-by-line into an of strings
     *
     * @param path the filepath for the file
     * @return an array of strings representing the file
     * @throws IOException
     */
    private String[] openFile(String path) throws IOException {
        int numberOfLines = countLines(path);

        try (FileReader fileReader = new FileReader(path);
             BufferedReader textReader = new BufferedReader(fileReader);) {

            //Creates an array for writing the text to
            String[] textData = new String[numberOfLines];

            //Reads lines into the array
            for (int i = 0; i < numberOfLines; i++) {
                textData[i] = textReader.readLine();
            }

            togglePrint("File length: " + numberOfLines);

            return textData;
        }
    }

    private int countLines(String path) throws IOException {
        try (FileReader fileReader = new FileReader(path);
             BufferedReader textCounter = new BufferedReader(fileReader)) {
            int numberOfLines = 0;

            //Gets the length of the file, in lines
            while (textCounter.readLine() != null) {
                numberOfLines++;
            }

            fileReader.close();
            textCounter.close();

            return numberOfLines;
        }
    }

    /**
     * Filters for each line
     *
     * @param theData
     */
    private void regexFilter(String theData) {
        if (!theData.matches(Constants.otherReg))
            togglePrint("----------------------------");
        if (theData.matches(Constants.turntable)) {
            parseTurntable(theData);
        } else if (theData.matches(Constants.conveyor)) {
            parseConveyor(theData);
        } else if (theData.matches(Constants.sack)) {
            parseSack(theData);
        } else if (theData.matches(Constants.hopper)) {
            parseHopper(theData);
        } else if (theData.matches(Constants.present)) {
            parsePresent(theData);
        } else {
            if (!theData.matches(Constants.otherReg))
                togglePrint("This data format is unrecognised.");
        }
    }

    /**
     * Parse conveyor string into it's composite attributes using regular expressions.
     *
     * @param conveyor the string defining a conveyor
     */
    private void parseConveyor(final String conveyor) {
        Matcher idMat = Pattern.compile(Constants.conveyor).matcher(conveyor);
        idMat.find();
        togglePrint("Conveyor " + idMat.group(1) + " found in parsed data.");
        Pattern pattern = Pattern.compile(Constants.conveyor);
        Matcher matcher = pattern.matcher(conveyor);

        while (matcher.find()) {
            togglePrint("      length: " + matcher.group(2));
            togglePrint("destinations: " + Arrays.toString(stringArrayGenerator(matcher.group(3), " ")));
        }
    }

    /**
     * Parse hopper string into it's composite attributes using regular expressions.
     *
     * @param hopper the string defining a hopper
     */
    void parseHopper(final String hopper) {
        Matcher matcher = Pattern.compile(Constants.hopper).matcher(hopper);
        while (matcher.find()) {
            togglePrint("Hopper " + matcher.group(1) + " found in parsed data.");
            togglePrint("connected to: " + matcher.group(2));
            togglePrint("    capacity: " + matcher.group(3));
            togglePrint("       speed: " + matcher.group(4));
        }
    }


    /**
     * Parse sack string into it's composite attributes using regular expressions.
     *
     * @param sack the string defining a sack
     */
    private void parseSack(final String sack) {
        Matcher idMat = Pattern.compile(Constants.sack).matcher(sack);
        idMat.find();
        togglePrint("Sack " + idMat.group(1) + " found in parsed data.");
        Pattern pattern = Pattern.compile(Constants.sack);
        Matcher matcher = pattern.matcher(sack);

        while (matcher.find()) {
            String[] ages = stringArrayGenerator(matcher.group(3), "-");

            togglePrint("    capacity: " + matcher.group(2));
            togglePrint("        ages: " + Arrays.toString(ages));
        }
    }

    /**
     * Parse present string into it's composite attributes using regular expressions.
     *
     * @param turntable the string defining a turntable
     */
    private void parseTurntable(final String turntable) {
        Matcher idMat = Pattern.compile(Constants.turntable).matcher(turntable);
        idMat.find();
        togglePrint("Turntable " + idMat.group(1) + " found in parsed data.");

        Pattern pattern = Pattern.compile(Constants.turntableProp);
        Matcher matcher = pattern.matcher(turntable);

        while (matcher.find()) {
            togglePrint("-------------------");
            togglePrint(matcher.group());
            togglePrint(" orientation: " + matcher.group(1));
            togglePrint("        type: " + matcher.group(2));

            if (!matcher.group(2).equals("null"))
                togglePrint("   output id: " + matcher.group(3));
        }
    }

    /**
     * Parse present string into it's composite attributes using regular expressions.
     *
     * @param present the string defining a turntable
     */
    private void parsePresent(final String present) {
        Matcher idMat = Pattern.compile(Constants.present).matcher(present);
        idMat.find();
        togglePrint("Present " + idMat.group(1) + " found in parsed data.");

        Pattern pattern = Pattern.compile(Constants.presentProp);
        Matcher matcher = pattern.matcher(present);

        while (matcher.find()) {
            togglePrint("-------------------");
            togglePrint(matcher.group());
            //togglePrint("   age range: " + matcher.group(1));
            //togglePrint("        type: " + matcher.group(2));

            //if (!matcher.group(2).equals("null"))
            //togglePrint("   output id: " + matcher.group(3));
        }
    }

    /**
     * Splits a string into an array, based on a single-character delimiter
     *
     * @param input     the input to delimit
     * @param delimiter the character to split the string on
     * @return the delimited string array
     */
    private String[] stringArrayGenerator(final String input, final String delimiter) {
        return input.split(delimiter);
    }

    /**
     * Outputs the title to the console. Totally unecessary. But it looks pretty.
     */
    private void outputTitle() {
        togglePrint("  _____ _ _        ___                            _   \r\n |  ___(_) | ___  |_ _|_ __ "
                + "___  _ __   ___  _ __| |_ \n | |_  | | |/ _ \\  | || '_ ` _ \\| '_ \\ / _ \\| '__| __|\r\n |  _"
                + "| | | |  __/  | || | | | | | |_) | (_) | |  | |_ \n |_|   |_|_|\\___| |___|_| |_| |_| .__/ \\__"
                + "_/|_|   \\__|\n                                |_| ");
    }

    /**
     * Calls println if printEnabled is true
     *
     * @param printableLine the line to print
     */
    private void togglePrint(final String printableLine) {
        //TODO: Update with an ENUM instead of a boolean to enable log4j style logging levels
        boolean shouldPrint = false;
        if (this.printEnabled) {
            shouldPrint = true;
        }

        if (shouldPrint)
            System.out.println(printableLine);
    }
}
