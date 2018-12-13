package com.mattmerr.synacor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ChallengeExecutor {

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println("Expecting one argument: path to challenge.bin");
      System.exit(1);
    }

    byte[] program = Files.readAllBytes(Paths.get(args[0]));
    var vm = new VirtualMachine();
    vm.program = ByteBuffer.wrap(program);
    vm.execute();
  }

}
