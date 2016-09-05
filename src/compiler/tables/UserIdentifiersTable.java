package compiler.tables;

import compiler.ErrorStream;
import compiler.ErrorStream.ErrorType;
import compiler.LexemeType;
import compiler.analyzers.LexicalInformation;

import java.util.HashMap;

/**
 * Created by Александр Сергеевич on 29.03.2016.
 */

public class UserIdentifiersTable
{
    private static UserIdentifiersTable ourInstance = new UserIdentifiersTable();

    public static UserIdentifiersTable getInstance() {
        return ourInstance;
    }

    private UserIdentifiersTable()
    {
        library = new HashMap<>();
    }

    private class Attributes
    {
        LexemeType type;
        int value = -1;
        String SegmentOrEQU;

        Attributes(LexemeType _type, String _SegmentOrEQU)
        {
            type = _type;
            SegmentOrEQU = _SegmentOrEQU;
        }

        Attributes(LexemeType _type, int _value, String _SegmentOrEQU)
        {
            type = _type;
            value = _value;
            SegmentOrEQU = _SegmentOrEQU;
        }

        @Override
        public String toString()
        {
            return String.format("%-22s%-5d%-6s%n", type.toString(), value, SegmentOrEQU);
        }
    }

    private HashMap<String, Attributes> library;

    private SegmentsTable segmentsTableSingleton = SegmentsTable.getInstance();
    private SegmentRegistersTable segmentRegistersTableSingleton = SegmentRegistersTable.getInstance();
    private ErrorStream ErrorStreamSingleton = ErrorStream.getInstance();

    private void PutToLibrary(String name, Attributes attr, int Line)
    {
        if(name.length() > 5) ErrorStreamSingleton.PutError(ErrorType.IDENTIFIER_IS_TOO_LONG, Line);
        library.put(name, attr);
    }

    public void SetVar(LexicalInformation inf, LexemeType type, String Segment)
    {
        AsmIdentifiersTable asmIdentifiersTableSingleton = AsmIdentifiersTable.getInstance();
        String name = inf.GetFieldStructTable().GetLabelOrName();
        if (!library.containsKey(name) && !asmIdentifiersTableSingleton.isAsmIdentifier(name) && !segmentsTableSingleton.isSegment(name))
            PutToLibrary(name, new Attributes(type, Segment), inf.GetLine());
        else
            ErrorStreamSingleton.PutError(ErrorType.INVALID_IDENTIFIER_NAME, inf.GetLine());
    }

    public void SetEqu(LexicalInformation inf)
    {
        AsmIdentifiersTable asmIdentifiersTableSingleton = AsmIdentifiersTable.getInstance();
        String name = inf.GetFieldStructTable().GetLabelOrName(),
        EQUname = inf.GetSentenceTable().GetLexemeTableRowByIndex(2).GetLexeme();
        if(inf.GetFieldStructTable().GetOperandsCount() != 1)
            ErrorStreamSingleton.PutError(ErrorType.INVALID_EQU, inf.GetLine());
        else if (!library.containsKey(name) && !asmIdentifiersTableSingleton.isAsmIdentifier(name) && !segmentsTableSingleton.isSegment(name))
            PutToLibrary(name, new Attributes(LexemeType.EQU_IDENTIFIER, EQUname), inf.GetLine());
        else
            ErrorStreamSingleton.PutError(ErrorType.INVALID_IDENTIFIER_NAME, inf.GetLine());
    }

    public void SetLabel(LexicalInformation inf, String Segment)
    {
        AsmIdentifiersTable asmIdentifiersTableSingleton = AsmIdentifiersTable.getInstance();
        String name = inf.GetFieldStructTable().GetLabelOrName();
        if (!library.containsKey(name) && !asmIdentifiersTableSingleton.isAsmIdentifier(name) && !segmentsTableSingleton.isSegment(name))
            PutToLibrary(name, new Attributes(LexemeType.LABEL, Segment), inf.GetLine());
        else
            ErrorStreamSingleton.PutError(ErrorType.INVALID_IDENTIFIER_NAME, inf.GetLine());
    }

    public LexemeType GetType(String name)
    {
        return library.containsKey(name) ? library.get(name).type : LexemeType.INDEFINITE;
    }

    public int GetValue(String name)
    {
        return library.get(name).value;
    }

    public String GetSegment(String name)
    {
        return library.get(name).SegmentOrEQU;
    }

    public String GetEQU(String name)
    {
        return library.get(name).SegmentOrEQU;
    }

    public String GetSegmentRegister(String name, int Line)
    {
        if(GetType(name) != LexemeType.EQU_IDENTIFIER && GetType(name) != LexemeType.LABEL)
        {
            String register = segmentRegistersTableSingleton.GetRegister(GetSegment(name));
            if(register != null)
                return register;
            else
                ErrorStreamSingleton.PutError(ErrorType.SEGMENT_IS_NOT_ASSIGNED, Line);
        }
        return null;
    }

    public boolean isUserIdentifier(String name)
    {
        return library.containsKey(name);
    }

    public String replaceWithEQU(String str)
    {
        for (String name : library.keySet())
            if(GetType(name) == LexemeType.EQU_IDENTIFIER)
                str = str.toUpperCase().replace(name, GetEQU(name));

        return str;
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
