 package com.mattmerr.synacor;

 import java.util.ArrayList;
 import java.util.NoSuchElementException;

 public class Stack {
    private final ArrayList<Character> stack = new ArrayList<>();

    public char peek() {
      if (stack.isEmpty()) {
        throw new NoSuchElementException();
      }
      return stack.get(stack.size() - 1);
    }

    public char pop() {
      if (stack.isEmpty()) {
        throw new NoSuchElementException();
      }
      return stack.remove(stack.size() - 1);
    }

    public void push(char v) {
      stack.add(v);
    }

    public boolean isEmpty() {
      return stack.isEmpty();
    }
  }
