package compiler.tables;

import java.util.ArrayList;
import compiler.*;

/**
 * Created by Александр on 07.03.2016.
 */
public class SentenceTable
{
    private UserIdentifiersTable userIdentifiersTableSingleton = UserIdentifiersTable.getInstance();
    private AsmIdentifiersTable asmIdentifiersTableSingleton = AsmIdentifiersTable.getInstance();

    private ArrayList<LexemeTableRow> LexemeTableRows = new ArrayList<>();
    public LexemeTableRow GetLexemeTableRowByIndex(int index)
    {
        return LexemeTableRows.get(index);
    }
    public int getLexemesCount()
    {
        return LexemeTableRows.size();
    }

    public class LexemeTableRow
    {
        private int number;
        private String __lexeme;
        private int CharAmount;
        private LexemeType _type;

        public int GetNumber()
        {
            return number;
        }
        public String GetLexeme()
        {
            return __lexeme;
        }
        public int GetCharAmount()
        {
            return CharAmount;
        }
        public LexemeType GetType()
        {
            return _type;
        }

        LexemeTableRow(int num, String str)
        {
            number = num;
            CharAmount = str.length();

            if(str.startsWith("\"") && str.endsWith("\""))
            {
                __lexeme = new String(str);
                _type = LexemeType.TEXT_CONSTANT;
            }
            else
            {
                __lexeme = new String(str.toUpperCase());

                if (wtfChar(__lexeme.charAt(0)) == CharacterType.NUMERAL && __lexeme.endsWith("H"))
                {
                    _type = LexemeType.HEXADECIMAL_CONSTANT;
                }
                else if(asmIdentifiersTableSingleton.isAsmIdentifier(__lexeme))
                {
                    _type = asmIdentifiersTableSingleton.GetAsmIdentifier(__lexeme);

                    if (_type == LexemeType.IDENTIFIER_32BIT_DATA_REGISTER || _type == LexemeType.IDENTIFIER_16BIT_DATA_REGISTER)
                    {
                        for (LexemeTableRow x : LexemeTableRows)
                        {
                            if (x.__lexeme.equals("["))
                            {
                                if (_type == LexemeType.IDENTIFIER_32BIT_DATA_REGISTER)
                                    _type = LexemeType.IDENTIFIER_32BIT_ADDRESS_REGISTER;
                                else
                                    _type = LexemeType.IDENTIFIER_16BIT_ADDRESS_REGISTER;
                                break;
                            }
                        }
                    }
                }
                else
                {
                    _type = userIdentifiersTableSingleton.GetType(__lexeme);
                }
            }
        }

        @Override
        public String toString ()
        {
            return String.format("%-3d%-8s%-3d%-34s%n", number, __lexeme, CharAmount, _type.toString());
        }
    }

    public void SetSentence(String str)
    {
        LexemeTableRows.clear();

        if(str.toUpperCase().contains("EQU"))
        {
            String[] lexs = str.split("\\s");
            for (int i =0; i<lexs.length; i++)
                LexemeTableRows.add(new LexemeTableRow(i, lexs[i]));

        }
        else
        {
            str = userIdentifiersTableSingleton.replaceWithEQU(str);

            int i = 0, bufIter = 0, lexemeCount = 0, strLength = str.length();

            char ch = 0;
            StringBuilder buf = new StringBuilder();

            CharacterType wtf;

            if (i < strLength) ch = str.charAt(i);
            while (i < strLength) {
                wtf = wtfChar(ch);
                switch (wtf) {
                    case LETTER:
                        while (!isSYMBOL(ch) && i < strLength) {
                            buf.insert(bufIter, ch);
                            bufIter++;

                            i++;
                            if (i < strLength) ch = str.charAt(i);
                        }
                        break;

                    case NUMERAL:
                        while (isNUMERAL(ch) && i < strLength) {
                            buf.insert(bufIter, ch);
                            bufIter++;

                            i++;
                            if (i < strLength) ch = str.charAt(i);
                        }
                        break;

                    case SYMBOL:
                        buf.insert(bufIter, ch);
                        i++;
                        if (i < strLength) ch = str.charAt(i);
                        break;

                    case STRING:
                        do {
                            buf.insert(bufIter, ch);
                            bufIter++;

                            i++;
                            if (i < strLength) ch = str.charAt(i);
                        }
                        while (ch != '"' && i < strLength);

                        buf.insert(bufIter, ch);
                        i++;
                        if (i < strLength) ch = str.charAt(i);
                        break;

                    case DELIMITER:
                        i++;
                        if (i < strLength) ch = str.charAt(i);
                        break;

                }

                if (wtf != CharacterType.DELIMITER) {
                    LexemeTableRows.add(new LexemeTableRow(lexemeCount, buf.toString()));
                    lexemeCount++;
                }

                bufIter = 0;
                buf.delete(0, buf.length());
            }
        }
    }

    private enum CharacterType{LETTER, NUMERAL, SYMBOL, STRING, DELIMITER}

    private static CharacterType wtfChar(char ch)
    {
        if (('A' <= ch && 'Z' >= ch) || ('a' <= ch && 'z' >= ch)) return CharacterType.LETTER;
        else if(('0' <= ch && '9' >= ch) || ('a' <= ch && 'f' >= ch) || ('A' <= ch && 'F' >= ch) || ch == 'H' || ch == 'h'  ) return CharacterType.NUMERAL;
        else if (ch == '"') return CharacterType.STRING;
        else if(ch == ' ' || ch == '\t' ) return CharacterType.DELIMITER;
        else return CharacterType.SYMBOL;
    }

    private static boolean isNUMERAL(char ch)
    {
        return (('0' <= ch && '9' >= ch) || ('a' <= ch && 'f' >= ch) || ('A' <= ch && 'F' >= ch) || ch == 'H' || ch == 'h'  );
    }

    private static boolean isSYMBOL(char ch) {
        return !(('A' <= ch && 'Z' >= ch) || ('a' <= ch && 'z' >= ch))
                && !(('0' <= ch && '9' >= ch) || ('a' <= ch && 'f' >= ch) || ('A' <= ch && 'F' >= ch) || ch == 'H' || ch == 'h')
                && ch != '"';
    }

    @Override
    public String toString ()
    {
        StringBuilder buf  = new StringBuilder();

        for(LexemeTableRow x: LexemeTableRows)
            buf.append(x.toString());

        return buf.toString();
    }
}