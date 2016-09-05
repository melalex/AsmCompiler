package compiler.tables;

import compiler.ErrorStream;
import compiler.GlobalPrintWriter;
import compiler.LexemeType;
import compiler.analyzers.LexicalInformation;

/**
 * Created by Александр Сергеевич on 12.04.2016.
 */

class DirectiveStrategy implements AbstractAsmIdentifierStrategy
{
    protected byte Shift = 0;

    UserIdentifiersTable userIdentifiersTableSingleton = UserIdentifiersTable.getInstance();
    SegmentsTable segmentsTableSingleton = SegmentsTable.getInstance();
    ErrorStream ErrorStreamSingleton = ErrorStream.getInstance();
    SegmentRegistersTable segmentRegistersTableSingleton = SegmentRegistersTable.getInstance();

    @Override
    public byte GetTheNumberOfBytes(LexicalInformation inf) throws NoSuchMethodException
    {
        return Shift;
    }
}

class dbStrategy extends DirectiveStrategy
{
    dbStrategy()
    {
        Shift = 1;
    }

    @Override
    public void HandleDirective(LexicalInformation inf) throws NoSuchMethodException
    {
        if(inf.GetOperands().GetOperand(0).isTextConstant())
        {
            Shift = (byte)((inf.GetSentenceTable().GetLexemeTableRowByIndex(2).GetCharAmount()-2)*2);
        }

        if(segmentsTableSingleton.GetCurrentSegment() != null)
            userIdentifiersTableSingleton.SetVar(inf, LexemeType.BYTE_USER_IDENTIFIER, segmentsTableSingleton.GetCurrentSegment());
        else
            ErrorStreamSingleton.PutError(ErrorStream.ErrorType.SEGMENT_EXPECTED,inf.GetLine());
    }
}

class dwStrategy extends DirectiveStrategy
{
    dwStrategy()
    {
        Shift = 2;
    }

    @Override
    public void HandleDirective(LexicalInformation inf) throws NoSuchMethodException
    {
        if(segmentsTableSingleton.GetCurrentSegment() != null)
            userIdentifiersTableSingleton.SetVar(inf, LexemeType.WORD_USER_IDENTIFIER, segmentsTableSingleton.GetCurrentSegment());
        else
            ErrorStreamSingleton.PutError(ErrorStream.ErrorType.SEGMENT_EXPECTED,inf.GetLine());
    }
}

class ddStrategy extends DirectiveStrategy
{
    ddStrategy()
    {
        Shift = 4;
    }

    @Override
    public void HandleDirective(LexicalInformation inf) throws NoSuchMethodException
    {
        if(segmentsTableSingleton.GetCurrentSegment() != null)
            userIdentifiersTableSingleton.SetVar(inf, LexemeType.DWORD_USER_IDENTIFIER, segmentsTableSingleton.GetCurrentSegment());
        else
            ErrorStreamSingleton.PutError(ErrorStream.ErrorType.SEGMENT_EXPECTED,inf.GetLine());
    }
}

class equStrategy extends DirectiveStrategy
{
    @Override
    public void HandleDirective(LexicalInformation inf) throws NoSuchMethodException
    {
        userIdentifiersTableSingleton.SetEqu(inf);
    }
}

class segmentStrategy extends DirectiveStrategy
{
    @Override
    public void HandleDirective(LexicalInformation inf) throws NoSuchMethodException
    {
        segmentsTableSingleton.HandleSegment(inf);
    }
}

class endsStrategy extends DirectiveStrategy
{
    @Override
    public void HandleDirective(LexicalInformation inf) throws NoSuchMethodException
    {
        if(segmentsTableSingleton.GetCurrentSegment() != null)
            segmentsTableSingleton.HandleEnds();
        else
            ErrorStreamSingleton.PutError(ErrorStream.ErrorType.SEGMENT_EXPECTED,inf.GetLine());
    }
}

class endStrategy extends DirectiveStrategy
{
    @Override
    public void HandleDirective(LexicalInformation inf) throws NoSuchMethodException
    {
        segmentsTableSingleton.HandleEnd();
    }
}

class assumeStrategy extends DirectiveStrategy
{
    private GlobalPrintWriter DebugInformationFile = GlobalPrintWriter.getInstance();

    @Override
    public void HandleDirective(LexicalInformation inf) throws NoSuchMethodException
    {
        if(segmentsTableSingleton.GetCurrentSegment() != null)
            segmentRegistersTableSingleton.HandleAssume(inf);
        else
            ErrorStreamSingleton.PutError(ErrorStream.ErrorType.SEGMENT_EXPECTED,inf.GetLine());

        DebugInformationFile.print(segmentRegistersTableSingleton + "\n");
    }
}
















