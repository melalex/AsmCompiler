package compiler;

import java.io.PrintWriter;

/**
 * Created by Александр Сергеевич on 12.04.2016.
 */
public class GlobalPrintWriter {
    private static GlobalPrintWriter ourInstance = new GlobalPrintWriter();

    public static GlobalPrintWriter getInstance() {
        return ourInstance;
    }

    private GlobalPrintWriter()
    {}

    private PrintWriter printWriter;

    public void SetGlobalPrintWriter(PrintWriter _printWriter)
    {
        printWriter = _printWriter;
    }

    public void print(Object string)
    {
        printWriter.print(string);
    }
}
