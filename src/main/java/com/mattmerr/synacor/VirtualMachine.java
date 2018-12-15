package com.mattmerr.synacor;

import java.io.IOException;
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

  public boolean denoteInput = false;

  final char[] reg = new char[8];
  final Stack stack = new Stack();
  ByteBuffer program = null;
  int pc = -1;

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
      return reg[val - 32768];
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
    set(a, b);
  }
  public void set(char a, char b) {
    reg[a] = b;
  }

  //   2: PUSH
  public void push() {
    char a = readMemVal();
    push(a);
  }
  public void push(char a) {
    stack.push(a);
  }

  //   3: POP
  public void pop() {
    char a = readRegister();
    pop(a);
  }
  public void pop(char a) {
    reg[a] = stack.pop();
  }

  //   4: EQ
  public void eq() {
    char a = readRegister();
    char b = readMemVal();
    char c = readMemVal();
    eq(a, b, c);
  }
  public void eq(char a, char b, char c) {
    reg[a] = (char) ((b == c) ? 1 : 0);
  }

  //   5: GT
  public void gt() {
    char a = readRegister();
    char b = readMemVal();
    char c = readMemVal();
    gt(a, b, c);
  }
  public void gt(char a, char b, char c) {
    reg[a] = (char) ((b > c) ? 1 : 0);
  }

  //  6: JMP
  public void jmp() {
    char a = readMemVal();
    jmp(a);
  }
  public void jmp(char a) {
    pc = 2*(int) (a);
  }

  //  7: JT
  public void jt() {
    char a = readMemVal();
    char b = readMemVal();
    jt(a, b);
  }
  public void jt(char a, char b) {
    if (a != 0) {
      pc = 2 * (int) b;
    }
  }

  //  8: JF
  public void jf() {
    char a = readMemVal();
    char b = readMemVal();
    jf(a, b);
  }
  public void jf(char a, char b) {
    if (a == 0) {
      pc = 2 * (int) b;
    }
  }

  //  9: ADD
  public void add() {
    char a = readRegister();
    char b = readMemVal();
    char c = readMemVal();
    add(a, b, c);
  }
  public void add(char a, char b, char c) {
    reg[a] = (char)((b + c) % 32768);
  }

  // 10: MULT
  public void mult() {
    char a = readRegister();
    int b = readMemVal();
    int c = readMemVal();
    mult(a, b, c);
  }
  public void mult(char a, int b, int c) {
    reg[a] = (char)((b * c) % 32768);
  }

  // 11: MOD
  public void mod() {
    char a = readRegister();
    char b = readMemVal();
    char c = readMemVal();
    mod(a, b, c);
  }
  public void mod(char a, char b, char c) {
    reg[a] = (char) ((b%c)%32768);
  }

  // 12: AND
  public void and() {
    char a = readRegister();
    char b = readMemVal();
    char c = readMemVal();
    and(a, b, c);
  }
  public void and(char a, char b, char c) {
    reg[a] = (char) ((b & c)%32768);
  }

  // 13: OR
  public void or() {
    char a = readRegister();
    char b = readMemVal();
    char c = readMemVal();
    or(a, b, c);
  }
  public void or(char a, char b, char c) {
    reg[a] = (char)((b | c) % 32768);
  }

  // 14: NOT
  public void not() {
    char a = readRegister();
    char b = readMemVal();
    not(a, b);
  }
  public void not(char a, char b) {
    reg[a] = (char)(b ^ 0x7FFF);
  }

  // 15: RMEM
  public void rmem() {
    char a = readRegister();
    char b = readMemVal();
    rmem(a, b);
  }
  public void rmem(char a, char b) {
    reg[a] = program.getChar(2 * b);
  }

  // 15: WMEM
  public void wmem() {
    char a = readMemVal();
    char b = readMemVal();
    wmem(a, b);
  }
  public void wmem(char a, char b) {
    program.putChar(2 * a, b);
  }

  // 17: CALL
  public void call() {
    char a = readMemVal();
    call(a);
  }
  public void call(char a) {
    stack.push((char) (pc / 2));
    pc = 2 * (int) a;
  }

  // 18: RET
  public void ret() {
    if (stack.isEmpty()) {
      halt();
    }
    pc = 2 * stack.pop();
  }

  // 19: OUT
  public void out() {
    char a = readMemVal();
    out(a);
  }
  public void out(char a) {
    logger.print(a);
  }

  // 20: IN
  public void in() {
    char a = readRegister();
    in(a);
  }
  public void in(char a) {
    try {
//      if (denoteInput) {
//        logger.print('§');
//        logger.flush();
//      }
      int read = inputStream.read();
      if (read == -1) {
        throw new IOException("reached end of input");
      }
      System.err.write(read);
      reg[a] = (char) read;
    }
    catch (IOException e) {
      e.printStackTrace();
      halt();
    }
  }

  // 21: NOP
  public void nop() {
  }

}
