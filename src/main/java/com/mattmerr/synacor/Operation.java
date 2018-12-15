package com.mattmerr.synacor;

import java.util.function.Consumer;

public enum Operation {

  HALT(0, VirtualMachine::halt),     //  0
  SET(2, VirtualMachine::set),       //  1
  PUSH(1, VirtualMachine::push),     //  2
  POP(1, VirtualMachine::pop),       //  3
  EQ(3, VirtualMachine::eq),         //  4
  GT(3, VirtualMachine::gt),         //  5
  JMP(1, VirtualMachine::jmp),       //  6
  JT(2, VirtualMachine::jt),         //  7
  JF(2, VirtualMachine::jf),         //  8
  ADD(3, VirtualMachine::add),       //  9
  MULT(3, VirtualMachine::mult),     // 10
  MOD(3, VirtualMachine::mod),       // 11
  AND(3, VirtualMachine::and),       // 12
  OR(3, VirtualMachine::or),         // 13
  NOT(2, VirtualMachine::not),       // 14
  RMEM(2, VirtualMachine::rmem),     // 15
  WMEM(2, VirtualMachine::wmem),     // 16
  CALL(1, VirtualMachine::call),     // 17
  RET(0, VirtualMachine::ret),       // 18
  OUT(1, VirtualMachine::out),       // 19
  IN(1, VirtualMachine::in),         // 20
  NOP(0, VirtualMachine::nop),       // 21

  ;

  public final int numArgs;
  private final Consumer<VirtualMachine> op;

  Operation(int numArgs) {
    this.numArgs = numArgs;
    this.op = null;
  }

  Operation(int numArgs, Consumer<VirtualMachine> op) {
    this.numArgs = numArgs;
    this.op = op;
  }

  public final void exec(VirtualMachine vm) {
    if (this.op == null) {
      throw new UnsupportedOperationException(
          this.name() + " is not implemented yet");
    }
    op.accept(vm);
  }

}
