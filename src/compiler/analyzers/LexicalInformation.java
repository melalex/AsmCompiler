package compiler.analyzers;

import compiler.tables.*;

/**
 * Created by Александр on 07.03.2016.
 */

public class LexicalInformation
{
    private SentenceTable SenTbl = new SentenceTable();
    private FieldStructTable FieldTbl = new FieldStructTable();
    private OperandsTable OperTbl = new OperandsTable();

    private String AsmString;
    private int Line = 0;
    private byte size;

    public void SetLexicalInformation (String str)
    {
        size = 0;
        AsmString = str;
        SenTbl.SetSentence(str);
        FieldTbl.SetFieldStruct(SenTbl, Line);
        OperTbl.SetOperands(SenTbl, FieldTbl, Line);
        Line++;
    }

    public void SetSize(byte sz)
    {
        size = sz;
    }

    public SentenceTable GetSentenceTable()
    {
        return SenTbl;
    }
    public FieldStructTable GetFieldStructTable()
    {
        return FieldTbl;
    }
    public OperandsTable GetOperands()
    {
        return OperTbl;
    }
    public int GetLine()
    {
        return Line;
    }
    public String GetAsmString()
    {
        return AsmString;
    }

    @Override
    public String toString()
    {
        return Line +": " + AsmString + "\n" + "Size: " + size + "\n" + SenTbl.toString() + FieldTbl.toString() + OperTbl.toString() + "\n";
    }
}
