package com.mattmerr.synacor;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public class CoinPermuter {

  public static class Coin {
    String name;
    int value;

    Coin(String name, int value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public String toString() {
      return this.name;
    }
  }

  public static void main(String[] args) {
    Coin[] coins = {
        new Coin("red", 2),
        new Coin("blue", 9),
        new Coin("shiny", 5),
        new Coin("corroded", 3),
        new Coin("concave", 7),
    };
    tryPermuting(coins, c -> c.value);
  }

  public static <T> void tryPermuting(T[] arr, ToIntFunction<T> toInt) {
    Object[][] workingPerm = { null };
    permute(arr, 0, perm -> {
      int eval = eval(perm, toInt);
//      System.out.printf("%s -> %d%n", Arrays.toString(perm), eval);
      if (eval == 399) {
        workingPerm[0] = Arrays.copyOf(perm, perm.length);
      }
    });

//    System.out.println("== RESULTS ==");
    if (workingPerm[0] != null) {
      System.out.printf("WORKS: %s%n", Arrays.toString(workingPerm[0]));
    }
    else {
      System.out.println("UNSAT");
    }
  }

  static <T> int eval(T[] perm, ToIntFunction<T> toInt) {
    int[] values = Arrays.stream(perm).mapToInt(toInt).toArray();
    return values[0]
        + values[1] * values[2] * values[2]
        + values[3] * values[3] * values[3]
        - values[4];
  }

  static <T> void permute(T[] arr, int off, Consumer<T[]> fn) {
    if (off == arr.length) {
      fn.accept(arr);
    }
    for (int i = off; i < arr.length; i++) {
      swap(arr, off, i);
      permute(arr, off+1, fn);
      swap(arr, off, i);
    }
  }

  static <T> void swap(T[] arr, int a, int b) {
    T tmp = arr[a];
    arr[a] = arr[b];
    arr[b] = tmp;
  }

}
