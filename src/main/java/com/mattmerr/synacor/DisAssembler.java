package com.mattmerr.synacor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DisAssembler {

  private static final Operation[] opcodes = Operation.values();

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println("Expecting one argument: path to binary");
      System.exit(1);
    }

    var program = ByteBuffer.wrap(Files.readAllBytes(Paths.get(args[0])));
    program.order(ByteOrder.LITTLE_ENDIAN);

    int pc = 0;
    while (2 * pc < program.limit()) {
      System.out.printf("%08d ", pc);
      char opcode = program.getChar(2 * pc);
      if (opcode < opcodes.length) {
        var op = opcodes[opcode];
        System.out.printf("%4s:", op.name());
        pc += 1;
        for (int argIdx = 0; argIdx < op.numArgs; argIdx++) {
          System.out.printf(" %5d", (int) program.getChar(2*pc));
          pc += 1;
        }
        if (op == Operation.OUT) {
          System.out.printf(" '%s'", escape(program.getChar(2*(pc - 1))));
        }
        System.out.println();
      }
      else {
        System.out.printf("????: %5d << DATA %n", (int) opcode);
        pc += 1;
      }
    }
  }

  private static String escape(char ch) {
    switch (ch) {
      case '\r': return "\\r";
      case '\n': return "\\n";
      case '\t': return "\\t";
      case '\b': return "\\b";
      default: return ch + "";
    }
  }
}
