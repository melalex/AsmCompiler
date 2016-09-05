package compiler;

import compiler.analyzers.Compiler;

import java.util.*;

public class Main
{
    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        System.out.print("Source file [.ASM]: ");
        String asm = in.nextLine();
        asm = "Test.asm";
        System.out.print("Listing file [.LST]: ");
        String lst = in.nextLine();
        Compiler.Compile(asm, lst);
    }
}
