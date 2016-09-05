package compiler.tables;

import compiler.ErrorStream;
import compiler.analyzers.LexicalInformation;

import java.util.HashMap;

/**
 * Created by Александр Сергеевич on 08.04.2016.
 */

public class SegmentsTable {
    private static SegmentsTable ourInstance = new SegmentsTable();

    public static SegmentsTable getInstance() {
        return ourInstance;
    }

    private SegmentsTable()
    {}

    class SegmentAttributes
    {
        private int line;
        private int CurrentShift;
        private int size;

        public int GetCurrentShift()
        {
            return size;
        }
        public int GetSize()
        {
            return CurrentShift;
        }

        SegmentAttributes(int _line)
        {
            line = _line;
            CurrentShift = 0;
            size = 0;
        }

        @Override
        public String toString()
        {
            return String.format("%-5d%-5d%-5d%n", line, CurrentShift, size);
        }
    }

    private HashMap<String,SegmentAttributes> library = new HashMap<>();

    private String CurrentSegment;
    private boolean Terminator = false;

    private ErrorStream ErrorStreamSingleton = ErrorStream.getInstance();

    public SegmentAttributes GetMainAttribute(String name)
    {
        return library.get(name);
    }
    public String GetCurrentSegment()
    {
        return CurrentSegment;
    }
    public SegmentAttributes GetCurrentSegmentAttributes()
    {
        return library.get(CurrentSegment);
    }

    public boolean isSegment(String name)
    {
        return library.containsKey(name);
    }
    public boolean isEnd(){return  Terminator;}

    public void HandleSegment(LexicalInformation inf)
    {
        AsmIdentifiersTable asmIdentifiersTableSingleton = AsmIdentifiersTable.getInstance();
        UserIdentifiersTable userIdentifiersTableSingleton = UserIdentifiersTable.getInstance();

        if (CurrentSegment != null)
        {
                ErrorStreamSingleton.PutError(ErrorStream.ErrorType.END_EXPECTED, inf.GetLine());
        }
        else
        {
            CurrentSegment = new String(inf.GetFieldStructTable().GetLabelOrName());
            if(CurrentSegment.length() > 5) ErrorStreamSingleton.PutError(ErrorStream.ErrorType.IDENTIFIER_IS_TOO_LONG, inf.GetLine());

            if (userIdentifiersTableSingleton.isUserIdentifier(CurrentSegment)
                    || asmIdentifiersTableSingleton.isAsmIdentifier(CurrentSegment)
                    || (library.containsKey(CurrentSegment) && inf.GetLine() != GetCurrentSegmentAttributes().line))
            {
                ErrorStreamSingleton.PutError(ErrorStream.ErrorType.INVALID_SEGMENT_NAME, inf.GetLine());
            }
            else if (isSegment(CurrentSegment) && inf.GetLine() == GetCurrentSegmentAttributes().line)
            {
                    GetCurrentSegmentAttributes().CurrentShift = 0;
            }
            else
            {
                library.put(CurrentSegment, new SegmentAttributes(inf.GetLine()));
            }
        }
    }

    public void HandleEnds()
    {
        GetCurrentSegmentAttributes().size = GetCurrentSegmentAttributes().CurrentShift;
        CurrentSegment = null;
    }

    public void HandleEnd()
    {
        Terminator = true;

        if(CurrentSegment != null)
            ErrorStreamSingleton.PutError(ErrorStream.ErrorType.END_EXPECTED, GetCurrentSegmentAttributes().line);
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        for (String name : library.keySet())
            str.append(String.format("%-6s%s", name, library.get(name).toString()));

        return str.toString();
    }
}
