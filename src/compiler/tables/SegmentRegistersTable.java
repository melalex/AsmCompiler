package compiler.tables;

import compiler.ErrorStream;
import compiler.LexemeType;
import compiler.analyzers.LexicalInformation;

import java.util.HashMap;

/**
 * Created by Александр Сергеевич on 08.04.2016.
 */

public class SegmentRegistersTable
{
    private static SegmentRegistersTable ourInstance = new SegmentRegistersTable();

    public static SegmentRegistersTable getInstance() {
        return ourInstance;
    }

    private SegmentRegistersTable()
    {
        library.put("CS", null);
        library.put("DS", null);
        library.put("SS", null);
        library.put("ES", null);
        library.put("GS", null);
        library.put("FS", null);
    }

    private HashMap<String,String> library = new HashMap<>();

    private SegmentsTable segmentsTableSingleton = SegmentsTable.getInstance();
    private static ErrorStream ErrorStreamSingleton = ErrorStream.getInstance();

    private void SetSegmentRegister(String register, String appointment)
    {
        if(library.containsKey(register)) library.put(register, appointment);
    }

    public String GetSegment(String register)
    {
        return library.get(register);
    }

    public String GetRegister(String segment)
    {
        for (String register : library.keySet())
        {
            String SegmentInLibrary = library.get(register);
            if (SegmentInLibrary != null && SegmentInLibrary.equals(segment))
                return register;
        }

        return null;
    }

    public void HandleAssume(LexicalInformation inf)
    {
        int FirstLexemeOfOperand;
        int LexemeCount;
        int CurrLexeme;

        for(int i = 0; i < inf.GetFieldStructTable().GetOperandsCount(); i++)
        {
            CurrLexeme = 0;
            FirstLexemeOfOperand = inf.GetFieldStructTable().GetOperand(i).GetNumbOfLex();
            LexemeCount = inf.GetFieldStructTable().GetOperand(i).GetCountOfLex();

            String register = inf.GetSentenceTable().GetLexemeTableRowByIndex(FirstLexemeOfOperand).GetLexeme();
            String appointment = inf.GetSentenceTable().GetLexemeTableRowByIndex(FirstLexemeOfOperand).GetLexeme();

            if(CurrLexeme < LexemeCount && inf.GetSentenceTable().GetLexemeTableRowByIndex(FirstLexemeOfOperand).GetType() == LexemeType.SEGMENT_REGISTER)
            {
                register = inf.GetSentenceTable().GetLexemeTableRowByIndex(FirstLexemeOfOperand).GetLexeme();
                CurrLexeme++;
            }

            if(CurrLexeme < LexemeCount && inf.GetSentenceTable().GetLexemeTableRowByIndex(FirstLexemeOfOperand + CurrLexeme).GetLexeme().equals(":"))
                CurrLexeme++;

            if(CurrLexeme < LexemeCount && segmentsTableSingleton.isSegment(inf.GetSentenceTable().GetLexemeTableRowByIndex(FirstLexemeOfOperand + CurrLexeme).GetLexeme()))
            {
                appointment = inf.GetSentenceTable().GetLexemeTableRowByIndex(FirstLexemeOfOperand + CurrLexeme).GetLexeme();
                CurrLexeme++;
            }

            if(CurrLexeme + 1 != LexemeCount && LexemeCount != 3)
            {
                ErrorStreamSingleton.PutError(ErrorStream.ErrorType.INVALID_ASSUME_OPERANDS, inf.GetLine());
            }
            else
            {
                if(appointment.equals("NOTHING"))
                    SetSegmentRegister(register, null);
                else
                    SetSegmentRegister(register, appointment);
            }
        }
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        for (String reg : library.keySet())
            str.append(String.format("%-4s%-6s%n", reg, library.get(reg)));

        return str.toString();
    }
}
