package com.mattmerr.synacor;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("WeakerAccess")
public class VirtualMachine {

  private final InputStream inputStream;
  private final LogPrependerStream logPrepender;
  private final PrintStream logger;

  private final char[] registers = new char[8];
  private final Stack stack = new Stack();

  ByteBuffer program = null;
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
    program.order(ByteOrder.LITTLE_ENDIAN);
    while (pc >= 0) {
      var op = decodeOperation();
      if (op != Operation.OUT) {
        int a = 0; // watchpoint
      }
      op.exec(this);
    }
  }

  private static final Operation[] opcodes = Operation.values();
  private final Operation decodeOperation() {
    return opcodes[readMemRaw()];
  }
  private final char readMemRaw() {
    if (pc + 1 >= program.limit()) {
      throw new IndexOutOfBoundsException("pc too big");
    }
    char val = program.getChar(pc);
    pc += 2;
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
      throw new AssertionError("Invalid MemVal: " + (int) val);
    }
  }
  private final char readRegister() {
    return (char) (((int) readMemRaw()) % 32768);
  }

  ////////////////
  // OPERATIONS //
  ////////////////

  //  0: HALT
  public void halt() {
    pc = -1;
  }

  //   1: SET
  public void set() {
    char a = readRegister();
    char b = readMemVal();
    registers[a] = b;
  }

  //   2: PUSH
  public void push() {
    char a = readMemVal();
    stack.push(a);
  }

  //   3: POP
  public void pop() {
    char a = readRegister();
    registers[a] = stack.pop();
  }

  //   4: EQ
  public void eq() {
    char a = readRegister();
    char b = readMemVal();
    char c = readMemVal();
    registers[a] = (char) ((b == c) ? 1 : 0);
  }

  //   5: GT
  public void gt() {
    char a = readRegister();
    char b = readMemVal();
    char c = readMemVal();
    registers[a] = (char) ((b > c) ? 1 : 0);
  }

  //  6: JMP
  public void jmp() {
    pc = 2 * (int) ((char) readMemVal());
  }

  //  7: JT
  public void jt() {
    char a = readMemVal();
    char b = readMemVal();
    if (a != 0) {
      pc = 2 * (int) b;
    }
  }

  //  8: JF
  public void jf() {
    char a = readMemVal();
    char b = readMemVal();
    if (a == 0) {
      pc = 2 * (int) b;
    }
  }

  //   9: ADD
  public void add() {
    char a = readRegister();
    char b = readMemVal();
    char c = readMemVal();
    registers[a] = (char)((b + c) % 32768);
  }

  //  12: AND
  public void and() {
    char a = readRegister();
    char b = readMemVal();
    char c = readMemVal();
    registers[a] = (char)((b & c) % 32768);
  }

  //  13: OR
  public void or() {
    char a = readRegister();
    char b = readMemVal();
    char c = readMemVal();
    registers[a] = (char)((b | c) % 32768);
  }

  //  14: NOT
  public void not() {
    char a = readRegister();
    char b = readMemVal();
    registers[a] = (char)(b ^ 0x7FFF);
  }

  // 19: OUT
  public void out() {
    char a = readMemVal();
    logger.print((char) a);
  }

  public void nop() {
  }

}
