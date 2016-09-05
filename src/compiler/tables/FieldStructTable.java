package compiler.tables;

import compiler.ErrorStream;
import compiler.ErrorStream.ErrorType;
import compiler.LexemeType;

import java.util.ArrayList;

/**
 * Created by Александр on 07.03.2016.
 */

public class FieldStructTable
{
    private ErrorStream ErrorStreamSingleton = ErrorStream.getInstance();

    private int FieldOfLabelOrName;
    private field MnemoCodeField = new field();

    private String LabelOrName;
    private String MnemoCode;

    private ArrayList<field> Operands = new ArrayList<>();

    public int GetFieldOfLabelOrName()
    {
        return FieldOfLabelOrName;
    }
    public field GetMnemoCodeField()
    {
        return MnemoCodeField;
    }
    public String GetLabelOrName()
    {
        return LabelOrName;
    }
    public String GetMnemoCode()
    {
        return MnemoCode;
    }

    public field GetOperand(int index)
    {
        return Operands.get(index);
    }
    public int GetOperandsCount()
    {
        return Operands.size();
    }

    public class field
    {
        private int NumbOfLex;
        private int CountOfLex;
        
        public int GetNumbOfLex()
        {
            return NumbOfLex;
        }
        public int GetCountOfLex()
        {
            return CountOfLex;
        }

        field()
        { }

        field(int _NumbOfLex, int _CountOfLex)
        {
            NumbOfLex = _NumbOfLex;
            CountOfLex = _CountOfLex;
        }
    }

    public void SetFieldStruct(SentenceTable st, int Line)
    {
        FieldOfLabelOrName = -1;
        MnemoCodeField.NumbOfLex = -1;
        MnemoCodeField.CountOfLex = -1;
        LabelOrName = null;
        MnemoCode = null;
        Operands.clear();

        int LexemeNumb = 0;
        int size = st.getLexemesCount();
        if(size > 0)
        {
            LexemeType lexemeType = st.GetLexemeTableRowByIndex(LexemeNumb).GetType();

            if (LexemeNumb < size && (lexemeType == LexemeType.INDEFINITE
                    || lexemeType == LexemeType.LABEL
                    || lexemeType == LexemeType.EQU_IDENTIFIER
                    || lexemeType == LexemeType.BYTE_USER_IDENTIFIER
                    || lexemeType == LexemeType.WORD_USER_IDENTIFIER
                    || lexemeType == LexemeType.DWORD_USER_IDENTIFIER))
            {
                FieldOfLabelOrName = LexemeNumb;
                LabelOrName = st.GetLexemeTableRowByIndex(LexemeNumb).GetLexeme();
                LexemeNumb++;
                lexemeType = st.GetLexemeTableRowByIndex(LexemeNumb).GetType();
            }

            if (LexemeNumb < size && (st.GetLexemeTableRowByIndex(LexemeNumb).GetLexeme().equals(":") && LexemeNumb != 0))
            {
                LexemeNumb++;
                lexemeType = st.GetLexemeTableRowByIndex(LexemeNumb).GetType();
            }

            if (LexemeNumb < size && (lexemeType == LexemeType.MACHINE_INSTRUCTION
                    || lexemeType == LexemeType.DIRECTIVE))
            {
                MnemoCodeField.NumbOfLex = LexemeNumb;
                MnemoCodeField.CountOfLex = 1;
                MnemoCode = st.GetLexemeTableRowByIndex(LexemeNumb).GetLexeme();
                LexemeNumb++;
            }

            if (LexemeNumb < size && LexemeNumb != 0)
            {
                int FirstLexeme = LexemeNumb;
                while (LexemeNumb < size)
                {
                    if (st.GetLexemeTableRowByIndex(LexemeNumb).GetLexeme().equals(","))
                    {
                        Operands.add(new field(FirstLexeme, LexemeNumb - FirstLexeme));
                        FirstLexeme = LexemeNumb + 1;
                    }
                    LexemeNumb++;
                }
                Operands.add(new field(FirstLexeme, LexemeNumb - FirstLexeme));
            }

            if (LexemeNumb != size)
                ErrorStreamSingleton.PutError(ErrorType.INVALID_SENTENCE_STRUCTURE, Line);
        }
    }

    @Override
    public String toString()
    {
        StringBuilder string  = new StringBuilder();
        string.append(String.format("%-4d%-4d%-4d", FieldOfLabelOrName, MnemoCodeField.NumbOfLex, MnemoCodeField.CountOfLex));

        for(field x: Operands)
            string.append(String.format("%-4d%-4d", x.NumbOfLex, x.CountOfLex));

        string.append("\n");
        return string.toString();
    }

}
