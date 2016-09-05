package compiler.tables;

import compiler.analyzers.LexicalInformation;

/**
 * Created by Александр Сергеевич on 12.04.2016.
 */

class MachineInstructionStrategy implements AbstractAsmIdentifierStrategy
{
    protected byte Shift = 1;

    @Override
    public byte GetTheNumberOfBytes(LexicalInformation inf) throws NoSuchMethodException {
        return Shift;
    }
}

class cliStrategy extends MachineInstructionStrategy
{

}

class decStrategy extends MachineInstructionStrategy
{
    decStrategy(){Shift = 2;}
}

class popStrategy extends MachineInstructionStrategy
{
    popStrategy(){Shift = 7;}
}

class cmpStrategy extends MachineInstructionStrategy
{
    @Override
    public byte GetTheNumberOfBytes(LexicalInformation inf) throws NoSuchMethodException {
        byte shift = 2;
        if(inf.GetOperands().GetOperand(0).GetRegister().startsWith("E"))
            shift++;

        return shift;
    }
}

class orStrategy extends MachineInstructionStrategy
{
    @Override
    public byte GetTheNumberOfBytes(LexicalInformation inf) throws NoSuchMethodException {
        byte shift = 4;
        if(inf.GetOperands().GetOperand(0).GetRegister().startsWith("E"))
            shift++;

        if(inf.GetOperands().GetOperand(1).isReplacementSegmentPrefix())
            shift++;

        return shift;
    }
}

class shrStrategy extends MachineInstructionStrategy
{
    shrStrategy(){Shift = 7;}
}

class movStrategy extends MachineInstructionStrategy
{
    @Override
    public byte GetTheNumberOfBytes(LexicalInformation inf) throws NoSuchMethodException {
        byte shift = 3;
        if(inf.GetOperands().GetOperand(0).GetRegister().startsWith("E"))
            shift+=3;

        return shift;
    }
}

class ldsStrategy extends MachineInstructionStrategy
{
    ldsStrategy(){Shift = 9;}
}

class jsStrategy extends MachineInstructionStrategy
{
    @Override
    public byte GetTheNumberOfBytes(LexicalInformation inf) throws NoSuchMethodException {
        byte shift = 2;
        if(inf.GetOperands().GetOperand(0).isUnknownIdentifier())
            shift+=2;

        return shift;
    }
}

class jmpStrategy extends MachineInstructionStrategy
{
    jmpStrategy(){Shift = 5;}
}