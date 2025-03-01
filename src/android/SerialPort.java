package com.example.x6.serial;
/**
 * Created by X6 on 2017/5/4.
 */
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SerialPort {

  private static final String TAG = "SerialPort";

  /*
   * Do not remove or rename the field mFd: it is used by native method close();
   */
  private FileDescriptor mFd;
  private FileInputStream mFileInputStream;
  private FileOutputStream mFileOutputStream;

  public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {

    /* Check access permission */
    if (!device.canRead() || !device.canWrite()) {
      try {
        /* Missing read/write permission, trying to chmod the file */
        Process su;
        su = Runtime.getRuntime().exec("/system/bin/su");
        String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
          + "exit\n";
        su.getOutputStream().write(cmd.getBytes());
        if ((su.waitFor() != 0) || !device.canRead()
          || !device.canWrite()) {
          throw new SecurityException();
        }
      } catch (Exception e) {
        e.printStackTrace();
        throw new SecurityException();
      }
    }

    mFd = open(device.getAbsolutePath(), baudrate, flags);
    if (mFd == null) {
      Log.e(TAG, "native open returns null");
      throw new IOException();
    }
    mFileInputStream = new FileInputStream(mFd);
    mFileOutputStream = new FileOutputStream(mFd);
  }

  // Getters and setters
  public FileInputStream getFileInputStream() {
    return mFileInputStream;
  }

  public FileOutputStream getFileOutputStream() {
    return mFileOutputStream;
  }

  // JNI
  private native static FileDescriptor open(String path, int baudrate, int flags);
  public native void close();
  static {
    System.loadLibrary("serial_port");
  }
}

