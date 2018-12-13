package com.mattmerr.synacor;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import static org.junit.Assert.*;

public class VirtualMachineTest {

  private PipedOutputStream vmIn = new PipedOutputStream();
  private ByteArrayOutputStream vmOut;
  private VirtualMachine vm;

  @Before
  public void setup() throws IOException {
    var pis = new PipedInputStream(vmIn);
    this.vmOut = new ByteArrayOutputStream();
    this.vm = new VirtualMachine(pis, vmOut);
    this.vm.setLogPrepending(false);
  }

  @Test
  public void testOut() throws IOException {
    vm.out();
    vmIn.write((byte) 'a');
    vm.out();
    vmIn.write((byte) 'b');
    vm.out();
    vmIn.write((byte) 'c');
    byte[] expected = { 'a', 'b', 'c' };
    assertArrayEquals(expected, vmOut.toByteArray());
  }

}
