package com.github.meixuesong.aggregatepersistence;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

class RecursiveObject {
    private Set<DualObject> visited = new HashSet<>();
    private Stack<DualObject> stack = new Stack<>();

    private void addVisited(DualObject object) {
        visited.add(object);
    }

    public void push(DualObject dk) {
        if (!visited.contains(dk)) {
            stack.push(dk);
        }
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public DualObject pop() {
        DualObject object = stack.pop();
        addVisited(object);

        return object;
    }
}
