package com.mattmerr.synacor;

import java.io.IOException;
import java.io.OutputStream;

public class LogPrependerStream extends OutputStream {

  private final OutputStream os;
  private boolean needsPrepend = true;
  private boolean prepending = true;

  public LogPrependerStream(OutputStream os) {
    this.os = os;
  }

  @Override
  public void write(int b) throws IOException {
    if (prepending && needsPrepend) {
      needsPrepend = false;
      os.write("svm> ".getBytes());
    }
    os.write(b);
    if (b == '\n') {
      needsPrepend = true;
    }
    else {
      needsPrepend = false;
    }
  }

  public void setPrepending(boolean prepending) {
    this.prepending = prepending;
  }

}
