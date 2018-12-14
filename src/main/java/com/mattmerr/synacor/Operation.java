package com.mattmerr.synacor;

import java.util.function.Consumer;

public enum Operation {

  HALT(VirtualMachine::halt),     //  0
  SET(VirtualMachine::set),       //  1
  PUSH(VirtualMachine::push),     //  2
  POP(VirtualMachine::pop),       //  3
  EQ(VirtualMachine::eq),         //  4
  GT(VirtualMachine::gt),         //  5
  JMP(VirtualMachine::jmp),       //  6
  JT(VirtualMachine::jt),         //  7
  JF(VirtualMachine::jf),         //  8
  ADD(VirtualMachine::add),       //  9
  MULT(),                         // 10
  MOD(),                          // 11
  AND(VirtualMachine::and),       // 12
  OR(VirtualMachine::or),                           // 13
  NOT(VirtualMachine::not),                          // 14
  RMEM(),                         // 15
  WMEM(),                         // 16
  CALL(),                         // 17
  RET(),                          // 18
  OUT(VirtualMachine::out),       // 19
  IN(),                           // 20
  NOP(VirtualMachine::nop),       // 21

  ;

  private final Consumer<VirtualMachine> op;

  Operation() {
    this.op = null;
  }

  Operation(Consumer<VirtualMachine> op) {
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
