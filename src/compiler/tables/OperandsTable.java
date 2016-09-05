package compiler.tables;

import compiler.ErrorStream;
import compiler.LexemeType;

import java.util.ArrayList;

/**
 * Created by Александр on 07.03.2016.
 */

public class OperandsTable
{
    enum OperandType {MEMORY, REGISTER, IMMEDIATE, UNKNOWN}

    public class Operand
    {
        private OperandType wtf = OperandType.UNKNOWN;

        private boolean PtrOperator = false;
        private boolean DataRegister = false;
        private boolean ReplacementSegmentPrefix = false;
        private boolean LabelOrNameIdentifier = false;
        private boolean UnknownIdentifier = false;
        private boolean AddressRegister = false;
        private boolean Operator = false;
        private boolean Constant = false;
        private boolean TextConstant = false;

        private String NULL = "NOTHING";
        private String _DataRegister = NULL;
        private String _ReplacementSegmentPrefix = NULL;
        private String _LabelOrNameIdentifier = NULL;
        private String _UnknownIdentifier = NULL;
        private String _AddressRegister = NULL;
        private String _Operator = NULL;
        private String _Constant = NULL;
        private String _TextConstant = NULL;

        public OperandType GetOperandType(){return wtf;}

        public boolean isPtrOperator(){return PtrOperator;}
        public boolean isDataRegister(){return DataRegister;}
        public boolean isReplacementSegmentPrefix(){return ReplacementSegmentPrefix;}
        public boolean isLabelOrNameIdentifier(){return LabelOrNameIdentifier;}
        public boolean isUnknownIdentifier(){return UnknownIdentifier;}
        public boolean isAddressRegister(){return AddressRegister;}
        public boolean isOperator(){return Operator;}
        public boolean isConstant(){return Constant;}
        public boolean isTextConstant(){return TextConstant;}

        public String GetRegister(){return _DataRegister;}
        public String GetReplacementSegmentPrefix(){return _ReplacementSegmentPrefix;}
        public String GetLabelOrNameIdentifier(){return _LabelOrNameIdentifier;}
        public String GetUnknownIdentifier(){return _UnknownIdentifier;}
        public String GetAddressRegister(){return _AddressRegister;}
        public String GetOperator(){return _Operator;}
        public String GetConstant(){return _Constant;}
        public String GetTextConstant(){return _TextConstant;}

        @Override
        public String toString()
        {
            return String.format("%-10s%-2s%-8s%-8s%-8s%-8s%-8s%-8s%-10s%-15s%n", wtf.toString(), ":",
                    _DataRegister, _ReplacementSegmentPrefix, _LabelOrNameIdentifier, _UnknownIdentifier,
                    _AddressRegister, _Operator, _Constant, _TextConstant);
        }
    }

    static private ArrayList<Operand> vector = new ArrayList<>();
    public Operand GetOperand(int index)
    {
        return vector.get(index);
    }
    public int GetOperandsCount()
    {
        return vector.size();
    }

    private static UserIdentifiersTable userIdentifiersTableSingleton = UserIdentifiersTable.getInstance();
    private static SegmentsTable segmentsTableSingleton = SegmentsTable.getInstance();
    private static ErrorStream ErrorStreamSingleton = ErrorStream.getInstance();

    public void SetOperands(SentenceTable SenTbl, FieldStructTable FieldTbl, int Line)
    {
        vector.clear();

        int CurrLexeme;
        int LexemeCount;
        int FirstLexemeOfOperand;
        Operand operand;
        LexemeType lexT;

        for(int i = 0; i < FieldTbl.GetOperandsCount(); i++)
        {
            CurrLexeme = 0;
            LexemeCount = FieldTbl.GetOperand(i).GetCountOfLex();
            FirstLexemeOfOperand = FieldTbl.GetOperand(i).GetNumbOfLex();
            lexT = SenTbl.GetLexemeTableRowByIndex(FieldTbl.GetOperand(i).GetNumbOfLex()).GetType();
            operand = new Operand();

            if (CurrLexeme < LexemeCount && (lexT == LexemeType.IDENTIFIER_8BIT_DATA_REGISTER
                    || lexT == LexemeType.IDENTIFIER_16BIT_DATA_REGISTER
                    || lexT == LexemeType.IDENTIFIER_32BIT_DATA_REGISTER))
            {
                operand.DataRegister = true;
                operand._DataRegister = SenTbl.GetLexemeTableRowByIndex(FirstLexemeOfOperand).GetLexeme();
                operand.wtf = OperandType.REGISTER;
                CurrLexeme++;
            }
            else if (CurrLexeme < LexemeCount && lexT == LexemeType.HEXADECIMAL_CONSTANT) {
                operand.Constant = true;
                operand._Constant = SenTbl.GetLexemeTableRowByIndex(FirstLexemeOfOperand).GetLexeme();
                operand.wtf = OperandType.IMMEDIATE;
                CurrLexeme++;
            }
            else if (CurrLexeme < LexemeCount && lexT == LexemeType.TEXT_CONSTANT)
            {
                operand.TextConstant = true;
                operand._TextConstant = SenTbl.GetLexemeTableRowByIndex(FirstLexemeOfOperand).GetLexeme();
                operand.wtf = OperandType.IMMEDIATE;
                CurrLexeme++;
            }
            else
            {
                boolean FirstBracket = false;

                if (CurrLexeme < LexemeCount && lexT == LexemeType.SEGMENT_REGISTER)
                {
                    operand.ReplacementSegmentPrefix = true;
                    operand._ReplacementSegmentPrefix = SenTbl.GetLexemeTableRowByIndex(FirstLexemeOfOperand).GetLexeme();
                    CurrLexeme++;
                }

                if (CurrLexeme < LexemeCount
                        && (SenTbl.GetLexemeTableRowByIndex(FieldTbl.GetOperand(i).GetNumbOfLex() + CurrLexeme).GetLexeme().equals(":")
                        && CurrLexeme != 0))
                {
                    CurrLexeme++;
                    lexT = SenTbl.GetLexemeTableRowByIndex(FieldTbl.GetOperand(i).GetNumbOfLex() + CurrLexeme).GetType();
                }

                if (CurrLexeme < LexemeCount
                        && userIdentifiersTableSingleton.isUserIdentifier(SenTbl.GetLexemeTableRowByIndex(FirstLexemeOfOperand+ CurrLexeme).GetLexeme()))
                {
                    operand.LabelOrNameIdentifier = true;
                    operand._LabelOrNameIdentifier = SenTbl.GetLexemeTableRowByIndex(FirstLexemeOfOperand + CurrLexeme).GetLexeme();
                    operand.wtf = OperandType.IMMEDIATE;
                    CurrLexeme++;
                }
                else if (CurrLexeme < LexemeCount && lexT == LexemeType.INDEFINITE)
                {
                    operand.UnknownIdentifier = true;
                    operand._UnknownIdentifier = SenTbl.GetLexemeTableRowByIndex(FirstLexemeOfOperand + CurrLexeme).GetLexeme();
                    CurrLexeme++;
                }

                if (CurrLexeme < LexemeCount && SenTbl.GetLexemeTableRowByIndex(FieldTbl.GetOperand(i).GetNumbOfLex() + CurrLexeme).GetLexeme().equals("["))
                {
                    FirstBracket = true;
                    CurrLexeme++;
                    lexT = SenTbl.GetLexemeTableRowByIndex(FieldTbl.GetOperand(i).GetNumbOfLex() + CurrLexeme).GetType();
                }

                if (CurrLexeme < LexemeCount && (lexT == LexemeType.IDENTIFIER_32BIT_ADDRESS_REGISTER
                        || lexT == LexemeType.IDENTIFIER_16BIT_ADDRESS_REGISTER))
                {
                    operand.AddressRegister = true;
                    operand._AddressRegister = SenTbl.GetLexemeTableRowByIndex(FirstLexemeOfOperand + CurrLexeme).GetLexeme();
                    CurrLexeme++;
                }

                if (CurrLexeme < LexemeCount && (SenTbl.GetLexemeTableRowByIndex(FieldTbl.GetOperand(i).GetNumbOfLex() + CurrLexeme).GetLexeme().equals("+")
                        || SenTbl.GetLexemeTableRowByIndex(FieldTbl.GetOperand(i).GetNumbOfLex() + CurrLexeme).GetLexeme().equals("-")))
                {
                    operand.Operator = true;
                    operand._Operator = SenTbl.GetLexemeTableRowByIndex(FirstLexemeOfOperand + CurrLexeme).GetLexeme();
                    CurrLexeme++;
                    lexT = SenTbl.GetLexemeTableRowByIndex(FieldTbl.GetOperand(i).GetNumbOfLex() + CurrLexeme).GetType();
                }

                if (CurrLexeme < LexemeCount && lexT == LexemeType.HEXADECIMAL_CONSTANT)
                {
                    operand.Constant = true;
                    operand._Constant = SenTbl.GetLexemeTableRowByIndex(FirstLexemeOfOperand + CurrLexeme).GetLexeme();
                    CurrLexeme++;
                }

                if (CurrLexeme < LexemeCount && FirstBracket && SenTbl.GetLexemeTableRowByIndex(FieldTbl.GetOperand(i).GetNumbOfLex() + CurrLexeme).GetLexeme().equals("]"))
                {
                    operand.wtf = OperandType.MEMORY;
                    CurrLexeme++;
                }
            }

            if(operand.LabelOrNameIdentifier && segmentsTableSingleton.GetCurrentSegment() != null)
            {
                String register = userIdentifiersTableSingleton.GetSegmentRegister(operand._LabelOrNameIdentifier, Line);
                if(register != null && !register.equals("DS"))
                {
                    operand.ReplacementSegmentPrefix = true;
                    operand._ReplacementSegmentPrefix = register;
                }
            }

            if(CurrLexeme != LexemeCount)
            {
                operand.wtf = OperandType.UNKNOWN;
                ErrorStreamSingleton.PutError(ErrorStream.ErrorType.INVALID_OPERAND_STRUCTURE, Line);
            }

            vector.add(operand);
        }
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        for(Operand x: vector)
            str.append(x.toString());

        return str.toString();
    }
}