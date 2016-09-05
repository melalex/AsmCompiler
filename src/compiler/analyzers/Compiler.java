package compiler.analyzers;

import compiler.ErrorStream;
import compiler.GlobalPrintWriter;
import compiler.LexemeType;
import compiler.tables.AsmIdentifiersTable;
import compiler.tables.UserIdentifiersTable;
import compiler.tables.SegmentsTable;

import java.io.*;
import java.nio.file.InvalidPathException;

/**
 * Created by Александр Сергеевич on 09.04.2016.
 */

public class Compiler
{
    private static AsmIdentifiersTable asmIdentifiersTableSingleton = AsmIdentifiersTable.getInstance();
    private static UserIdentifiersTable userIdentifiersTableSingleton = UserIdentifiersTable.getInstance();
    private static SegmentsTable segmentsTableSingleton = SegmentsTable.getInstance();
    private static ErrorStream ErrorStreamSingleton = ErrorStream.getInstance();
    private static GlobalPrintWriter DebugInformationFile = GlobalPrintWriter.getInstance();
    
    static public void Compile(String asm, String lst)
    {
        first_pass(asm);
    }

    private static void first_pass(String arg)
    {
        try(BufferedReader AsmFileBufferedReader = new BufferedReader(new FileReader(arg), 128);
            PrintWriter debugInformationFile = new PrintWriter(new File(arg.substring(0, arg.length()-3) + "txt"));
            PrintWriter ListingPrintWriter = new PrintWriter(new File(arg.substring(0, arg.length()-3) + "lst")))
        {
            DebugInformationFile.SetGlobalPrintWriter(debugInformationFile);
            LexicalInformation inf = new LexicalInformation();
            String line;
            while((line = AsmFileBufferedReader.readLine()) != null && !segmentsTableSingleton.isEnd())
            {
                inf.SetLexicalInformation(line);
                int MnemoCodePosition = inf.GetFieldStructTable().GetMnemoCodeField().GetNumbOfLex();
                if(MnemoCodePosition != -1 && inf.GetSentenceTable().GetLexemeTableRowByIndex(MnemoCodePosition).GetType() == LexemeType.DIRECTIVE)
                {
                    asmIdentifiersTableSingleton.HandleDirective(inf);
                }
                else if(MnemoCodePosition != -1 && inf.GetSentenceTable().GetLexemeTableRowByIndex(MnemoCodePosition).GetType() == LexemeType.MACHINE_INSTRUCTION)
                {

                }

                inf.SetSize(asmIdentifiersTableSingleton.GetTheNumberOfBytes(inf));

                DebugInformationFile.print(inf);

                if (inf.GetFieldStructTable().GetFieldOfLabelOrName() != -1 && inf.GetSentenceTable().GetLexemeTableRowByIndex(1).GetLexeme().equals(":"))
                    userIdentifiersTableSingleton.SetLabel(inf, segmentsTableSingleton.GetCurrentSegment());
            }
            DebugInformationFile.print(userIdentifiersTableSingleton);
            DebugInformationFile.print("\n");
            DebugInformationFile.print(segmentsTableSingleton);
            DebugInformationFile.print("\n");
            DebugInformationFile.print(ErrorStreamSingleton);
        }
        catch(IOException e)
        {
            System.out.println("IO Error" + e);
        }
        catch(InvalidPathException e)
        {
            System.out.println("Path Error" + e);
        }
        catch(NoSuchMethodException e)
        {
            System.out.println("Strategy Error" + e);
        }
    }
}
