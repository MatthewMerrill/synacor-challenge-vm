package com.mattmerr.synacor;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

@SuppressWarnings("WeakerAccess")
public class VirtualMachine {

  private final InputStream inputStream;
  private final LogPrependerStream logPrepender;
  private final PrintStream logger;

  private final char[] registers = new char[8];
  private final Stack stack = new Stack();

  byte[] program = new byte[0];
  private int pc = -1;

  public static void main(String[] args) {
    var vm = new VirtualMachine();
    vm.logger.println("wow");
  }

  public VirtualMachine() {
    inputStream = System.in;
    logPrepender = new LogPrependerStream(System.out);
    logger = new PrintStream(logPrepender);
  }

  public VirtualMachine(InputStream is, OutputStream os) {
    inputStream = is;
    logPrepender = new LogPrependerStream(os);
    logger = new PrintStream(logPrepender);
  }

  public void setLogPrepending(boolean prepending) {
    logPrepender.setPrepending(prepending);
  }

  public void execute() {
    pc = 0;
    while (pc >= 0) {
      var op = decodeOperation();
      op.exec(this);
    }
  }

  private static final Operation[] opcodes = Operation.values();
  private final Operation decodeOperation() {
    return opcodes[readMemRaw()];
  }
  private final char readMemRaw() {
    if (pc + 1 >= program.length) {
      throw new IndexOutOfBoundsException("pc too big");
    }
    byte lo = program[pc++];
    byte hi = program[pc++];
    char val = (char) ((hi << 8) | (lo));
    return val;
  }
  private final char readMemVal() {
    char val = readMemRaw();
    if (0 <= val && val <= 32767) {
      return val;
    }
    else if (32768 <= val && val <= 32775) {
      return registers[val - 32768];
    }
    else {
      throw new AssertionError("Invalid MemVal: " + val);
    }
  }

  ////////////////
  // OPERATIONS //
  ////////////////

  //  0: HALT
  public void halt() {
    pc = -1;
  }

  //  6: JMP
  public void jmp() {
    pc = (char) (2 * readMemVal());
  }

  // 19: OUT
  public void out() {
    char a = readMemVal();
    logger.print((char) a);
  }

  public void nop() {
  }

}
