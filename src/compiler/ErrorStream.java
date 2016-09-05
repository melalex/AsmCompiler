package compiler;

import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Created by Александр Сергеевич on 04.04.2016.
 */

public class ErrorStream
{
    private static ErrorStream ourInstance = new ErrorStream();

    public static ErrorStream getInstance() {
        return ourInstance;
    }

    private ErrorStream()
    {
        errors = new ArrayList<>();
    }

    public enum ErrorType
    {
        NO_ERRORS,
        INVALID_SENTENCE_STRUCTURE,
        INVALID_IDENTIFIER_NAME,
        INVALID_SEGMENT_NAME,
        INVALID_ASSUME_OPERANDS,
        INVALID_OPERAND_STRUCTURE,
        INVALID_EQU,
        IDENTIFIER_EXPECTED,
        IDENTIFIER_IS_TOO_LONG,
        SEGMENT_EXPECTED,
        END_EXPECTED,
        SEGMENT_IS_NOT_ASSIGNED,
    }

    class Error
    {
        private ErrorType Type;
        private int Line;

        Error(ErrorType _type, int _line)
        {
            Type = _type;
            Line = _line;
        }

        public ErrorType GetType()
        {
            return Type;
        }
        public int GetLine()
        {
            return Line;
        }

        @Override
        public String toString()
        {
            return String.format("Error in line %d: %s%n", Line, Type.toString());
        }
    }

    private ArrayList<Error> errors;

    public void PutError(ErrorType err, int line)
    {
        if(err != ErrorType.NO_ERRORS) errors.add(new Error(err, line));
    }
    public int GetCount()
    {
        return errors.size();
    }
    public boolean isEmpty()
    {
        return errors.isEmpty();
    }
    public Stream<Error> GetStream()
    {
        return errors.stream();
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        for(Error x: errors)
            str.append(x.toString());

        return str.toString();
    }
}


