package compiler.tables;

import java.lang.*;
import compiler.InstructionCode;
import compiler.analyzers.LexicalInformation;

/**
 * Created by Александр Сергеевич on 12.04.2016.
 */

public interface AbstractAsmIdentifierStrategy
{
    default void HandleDirective(LexicalInformation inf) throws NoSuchMethodException
    {
        throw new NoSuchMethodException("HandleDirective can be called only for Directives");
    }

    default byte GetTheNumberOfBytes(LexicalInformation inf) throws NoSuchMethodException
    {
        throw new NoSuchMethodException("GetTheNumberOfBytes can be called only for Machine instruction or Directives");
    }

    default InstructionCode GetInstructionCode() throws NoSuchMethodException
    {
        throw new NoSuchMethodException("GetInstructionCode can be called only for Machine instructions or Directives");
    }

    default byte GetRegisterCapacity() throws NoSuchMethodException
    {
        throw new NoSuchMethodException("GetRegisterCapacity can be called only for Registers");
    }
}
