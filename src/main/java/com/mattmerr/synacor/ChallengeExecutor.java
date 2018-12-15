package com.mattmerr.synacor;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;

import static java.lang.Integer.parseInt;
import static java.util.Map.entry;

public class ChallengeExecutor {

  private static Map<String, BiConsumer<VirtualMachine, String[]>> commands =
      Map.ofEntries(
          entry("!rmem", ChallengeExecutor::rmem),
          entry("!rreg", ChallengeExecutor::rreg)
      );

  public static void main(String[] args) throws IOException {
    if (args.length < 1) {
      System.err.println("Expecting one argument: path to challenge.bin");
      System.exit(1);
    }

    byte[] program = Files.readAllBytes(Paths.get(args[0]));

    var vmInp = new PipedInputStream();
    var vmOut = new PipedOutputStream();
    var vmInpWriter = new PrintStream(new PipedOutputStream(vmInp));
    var vmOutViewer = new PipedInputStream(vmOut);

    var vm = new VirtualMachine(vmInp, System.out);
    vm.program = ByteBuffer.wrap(program);
    vm.denoteInput = true;

    var execThread = new Thread(vm::execute);
    execThread.start();

    if (args.length == 2) {
      var solutionScn = new Scanner(Paths.get(args[1]));
      while (solutionScn.hasNextLine()) {
        var line = solutionScn.nextLine();
        if (!line.isEmpty()) {
          vmInpWriter.print(line + "\n");
        }
      }
    }

    var scn = new Scanner(System.in);
    while (scn.hasNextLine()) {
      var line = scn.nextLine();
      if (line.isEmpty()) {
        continue;
      }

      if (line.startsWith("!")) {
        try {
          var argv = line.split(" ");
          commands.get(argv[0]).accept(vm, argv);
        } catch (RuntimeException e) {
          e.printStackTrace();
        }
      }
      else {
        vmInpWriter.print(line);
        vmInpWriter.print('\n');
      }
    }
  }

  private static void rmem(VirtualMachine vm, String[] argv) {
    if (argv.length != 2) {
      System.out.print("!!!> ");
      System.err.println("!rmem [0-32767]");
    }
    var memIdx = parseInt(argv[1]);
    System.out.printf("!!!> %d%n", (int) vm.program.getChar(memIdx));
  }

  private static void rreg(VirtualMachine vm, String[] argv) {
    if (argv.length == 1) {
      System.out.println("!!!> " + Arrays.toString(vm.reg));
    }
    if (argv.length != 2) {
      System.out.print("!!!> ");
      System.err.println("!rreg (0-7)");
    }
    var regNum = parseInt(argv[1]);
    System.out.printf("!!!> %d%n", (int) vm.reg[regNum]);
  }

  private static void wreg(VirtualMachine vm, String[] argv) {
    if (argv.length != 3) {
      System.out.print("!!!> ");
      System.err.println("!wreg [0-7] [0-32767]");
    }
    var regNum = parseInt(argv[1]);
    var val = parseInt(argv[2]);
    vm.reg[regNum] = (char) val;
    System.out.println("!!!> Done");
  }

}
//