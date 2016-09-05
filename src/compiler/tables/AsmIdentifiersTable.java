package compiler.tables;

import compiler.*;
import compiler.analyzers.LexicalInformation;

import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * Created by Александр on 07.03.2016.
 */

public class AsmIdentifiersTable
{
    private static AsmIdentifiersTable ourInstance = new AsmIdentifiersTable();

    public static AsmIdentifiersTable getInstance() {
        return ourInstance;
    }

    private AsmIdentifiersTable()
    {
        library = new HashMap<>();

        library.put("ES", new Attributes(LexemeType.SEGMENT_REGISTER));
        library.put("CS", new Attributes(LexemeType.SEGMENT_REGISTER));
        library.put("SS", new Attributes(LexemeType.SEGMENT_REGISTER));
        library.put("DS", new Attributes(LexemeType.SEGMENT_REGISTER));
        library.put("FS", new Attributes(LexemeType.SEGMENT_REGISTER));
        library.put("GS", new Attributes(LexemeType.SEGMENT_REGISTER));

        library.put("EAX", new Attributes(LexemeType.IDENTIFIER_32BIT_DATA_REGISTER));
        library.put("EBX", new Attributes(LexemeType.IDENTIFIER_32BIT_DATA_REGISTER));
        library.put("ECX", new Attributes(LexemeType.IDENTIFIER_32BIT_DATA_REGISTER));
        library.put("EDX", new Attributes(LexemeType.IDENTIFIER_32BIT_DATA_REGISTER));
        library.put("EDP", new Attributes(LexemeType.IDENTIFIER_32BIT_DATA_REGISTER));
        library.put("EBP", new Attributes(LexemeType.IDENTIFIER_32BIT_DATA_REGISTER));
        library.put("ESP", new Attributes(LexemeType.IDENTIFIER_32BIT_DATA_REGISTER));
        library.put("ESI", new Attributes(LexemeType.IDENTIFIER_32BIT_DATA_REGISTER));
        library.put("EDI", new Attributes(LexemeType.IDENTIFIER_32BIT_DATA_REGISTER));

        library.put("AX", new Attributes(LexemeType.IDENTIFIER_16BIT_DATA_REGISTER));
        library.put("BX", new Attributes(LexemeType.IDENTIFIER_16BIT_DATA_REGISTER));
        library.put("CX", new Attributes(LexemeType.IDENTIFIER_16BIT_DATA_REGISTER));
        library.put("DX", new Attributes(LexemeType.IDENTIFIER_16BIT_DATA_REGISTER));
        library.put("DP", new Attributes(LexemeType.IDENTIFIER_16BIT_DATA_REGISTER));
        library.put("BP", new Attributes(LexemeType.IDENTIFIER_16BIT_DATA_REGISTER));
        library.put("SP", new Attributes(LexemeType.IDENTIFIER_16BIT_DATA_REGISTER));
        library.put("SI", new Attributes(LexemeType.IDENTIFIER_16BIT_DATA_REGISTER));
        library.put("DI", new Attributes(LexemeType.IDENTIFIER_16BIT_DATA_REGISTER));

        library.put("AL", new Attributes(LexemeType.IDENTIFIER_8BIT_DATA_REGISTER));
        library.put("BL", new Attributes(LexemeType.IDENTIFIER_8BIT_DATA_REGISTER));
        library.put("CL", new Attributes(LexemeType.IDENTIFIER_8BIT_DATA_REGISTER));
        library.put("DL", new Attributes(LexemeType.IDENTIFIER_8BIT_DATA_REGISTER));
        library.put("AH", new Attributes(LexemeType.IDENTIFIER_8BIT_DATA_REGISTER));
        library.put("BH", new Attributes(LexemeType.IDENTIFIER_8BIT_DATA_REGISTER));
        library.put("CH", new Attributes(LexemeType.IDENTIFIER_8BIT_DATA_REGISTER));
        library.put("DH", new Attributes(LexemeType.IDENTIFIER_8BIT_DATA_REGISTER));

        library.put("CLI", new Attributes(LexemeType.MACHINE_INSTRUCTION, new cliStrategy()));
        library.put("DEC", new Attributes(LexemeType.MACHINE_INSTRUCTION, new decStrategy()));
        library.put("POP", new Attributes(LexemeType.MACHINE_INSTRUCTION, new popStrategy()));
        library.put("CMP", new Attributes(LexemeType.MACHINE_INSTRUCTION, new cmpStrategy()));
        library.put("OR", new Attributes(LexemeType.MACHINE_INSTRUCTION, new orStrategy()));
        library.put("SHR", new Attributes(LexemeType.MACHINE_INSTRUCTION, new shrStrategy()));
        library.put("MOV", new Attributes(LexemeType.MACHINE_INSTRUCTION, new movStrategy()));
        library.put("LDS", new Attributes(LexemeType.MACHINE_INSTRUCTION, new ldsStrategy()));
        library.put("JS", new Attributes(LexemeType.MACHINE_INSTRUCTION, new jsStrategy()));
        library.put("JMP", new Attributes(LexemeType.MACHINE_INSTRUCTION, new jmpStrategy()));

        library.put("DB", new Attributes(LexemeType.DIRECTIVE, new dbStrategy()));
        library.put("DD", new Attributes(LexemeType.DIRECTIVE, new ddStrategy()));
        library.put("DW", new Attributes(LexemeType.DIRECTIVE, new dwStrategy()));

        library.put("ASSUME", new Attributes(LexemeType.DIRECTIVE, new assumeStrategy()));
        library.put("SEGMENT", new Attributes(LexemeType.DIRECTIVE, new segmentStrategy()));
        library.put("ENDS", new Attributes(LexemeType.DIRECTIVE, new endsStrategy()));
        library.put("EQU", new Attributes(LexemeType.DIRECTIVE, new equStrategy()));
        library.put("END", new Attributes(LexemeType.DIRECTIVE, new endStrategy()));

        library.put("BYTE", new Attributes(LexemeType.ACCESS_MODIFIER));
        library.put("WORD", new Attributes(LexemeType.ACCESS_MODIFIER));
        library.put("DWORD", new Attributes(LexemeType.ACCESS_MODIFIER));
        library.put("FAR", new Attributes(LexemeType.ACCESS_MODIFIER));
        library.put("NEAR", new Attributes(LexemeType.ACCESS_MODIFIER));
        library.put("PTR", new Attributes(LexemeType.ACCESS_MODIFIER));

        library.put(";",  new Attributes(LexemeType.A_SINGLE_CHARACTER));
        library.put(",",  new Attributes(LexemeType.A_SINGLE_CHARACTER));
        library.put("\"", new Attributes(LexemeType.A_SINGLE_CHARACTER));
        library.put(":",  new Attributes(LexemeType.A_SINGLE_CHARACTER));
        library.put("[",  new Attributes(LexemeType.A_SINGLE_CHARACTER));
        library.put("]",  new Attributes(LexemeType.A_SINGLE_CHARACTER));
        library.put("(",  new Attributes(LexemeType.A_SINGLE_CHARACTER));
        library.put(")",  new Attributes(LexemeType.A_SINGLE_CHARACTER));
        library.put("+",  new Attributes(LexemeType.A_SINGLE_CHARACTER));
        library.put("*",  new Attributes(LexemeType.A_SINGLE_CHARACTER));
        library.put("-",  new Attributes(LexemeType.A_SINGLE_CHARACTER));
    }

    private class Attributes
    {
        LexemeType type;
        AbstractAsmIdentifierStrategy strategy;

        Attributes(LexemeType _type)
        {
            type = _type;
        }

        Attributes(LexemeType _type, AbstractAsmIdentifierStrategy _strategy)
        {
            type = _type;
            strategy = _strategy;
        }
    }

    private HashMap<String, Attributes> library;

    public void HandleDirective(LexicalInformation inf) throws NoSuchMethodException
    {
        library.get(inf.GetFieldStructTable().GetMnemoCode()).strategy.HandleDirective(inf);
    }

    public byte GetTheNumberOfBytes(LexicalInformation inf) throws NoSuchMethodException
    {
        try
        {
            return library.get(inf.GetFieldStructTable().GetMnemoCode()).strategy.GetTheNumberOfBytes(inf);
        }
        catch(NullPointerException ex)
        {
            return 0;
        }
    }

    public InstructionCode GetInstructionCode(LexicalInformation inf) throws NoSuchMethodException
    {
        return library.get(inf.GetFieldStructTable().GetMnemoCode()).strategy.GetInstructionCode();
    }

    public byte GetRegisterCapacity(LexicalInformation inf) throws NoSuchMethodException
    {
        return library.get(inf.GetFieldStructTable().GetMnemoCode()).strategy.GetRegisterCapacity();
    }

    LexemeType GetAsmIdentifier(String key)
    {
        return library.containsKey(key) ? library.get(key).type : LexemeType.INDEFINITE;
    }

    boolean isAsmIdentifier(String key)
    {
        return library.containsKey(key);
    }
}
