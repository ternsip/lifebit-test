import java.util.*;


/**
 * Set of dependency rules: dependencies and conflicts
 * Keeping dependency information as adjacency tree child -> parents, and parent -> children
 * Keeping conflicts as list of pairs (a,b) and mapping: dependency -> set of conflicting
 */
public class RuleSet {

    private final HashMap<String, Set<String>> depToParents = new HashMap<>();
    private final HashMap<String, Set<String>> depToChildren = new HashMap<>();
    private final HashMap<String, Set<String>> depToConflicts = new HashMap<>();
    private final ArrayList<Conflict> conflicts = new ArrayList<>();

    /**
     * Register dependency to the structure
     * "A depends on B", or "for A to be selected, B needs to be selected"
     * @param a Target dependency
     * @param b Dependent dependency
     */
    public void addDep(String a, String b) {
        if (a.equals(b)) return;
        depToParents.computeIfAbsent(b, x -> new HashSet<>());
        depToChildren.computeIfAbsent(a, x -> new HashSet<>());
        depToParents.get(b).add(a);
        depToChildren.get(a).add(b);
    }

    /**
     * Register two conflicting dependencies
     * "A and B are exclusive", or "B and A are exclusive"
     * for A to be selected, B needs to be unselected;
     * for B to be selected, A needs to be unselected
     * @param a First dependency (A)
     * @param b Second dependency (B)
     */
    public void addConflict(String a, String b) {
        if (a.equals(b)) throw new IllegalArgumentException("Can not conflict with itself");
        conflicts.add(new Conflict(a, b));
        depToConflicts.computeIfAbsent(b, x -> new HashSet<>());
        depToConflicts.computeIfAbsent(a, x -> new HashSet<>());
        depToConflicts.get(b).add(a);
        depToConflicts.get(a).add(b);
    }

    /**
     * What we do here is basically run BFS from two vertices with different color
     * Setting fire in two different points of a tree and check if one can touch another
     * Parent-Graph is unidirectional and the path should exist between two conflicting nodes in order to say incoherent
     * Iterates only via parent graph
     * @param a conflicting dependency A
     * @param b conflicting dependency B
     * @return true in case parent graph is incoherent
     */
    public boolean isConflicting(String a, String b) {
        Map<String, Integer> depToColor = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(a); // Set fire on A
        queue.add(b); // Set fire on B
        depToColor.put(a, 0); // Color 0 (number)
        depToColor.put(b, 1); // Color 1 (number)
        while (!queue.isEmpty()) {
            String nextDep = queue.poll();
            int color = depToColor.get(nextDep);
            for (String parentDep : depToParents.getOrDefault(nextDep, Collections.emptySet())) {
                Integer parentColor = depToColor.get(parentDep);
                if (parentColor == null) {
                    // No color yet - set it with the current color
                    queue.add(parentDep);
                    depToColor.put(parentDep, color);
                } else if (parentColor != color) {
                    return true; // Found another color - means we have found conflicting path
                }
            }
        }
        return false;
    }

    /**
     * Check dependency graph coherency
     * @return whether the dependency graph is coherent
     */
    public boolean isCoherent() {
        for (Conflict conflict : conflicts) {
            if (isConflicting(conflict.a, conflict.b)) {
                return false; // Incoherent because two dependencies in conflict
            }
        }
        return true;
    }

    public HashMap<String, Set<String>> getDepToChildren() {
        return depToChildren;
    }

    public HashMap<String, Set<String>> getDepToConflicts() {
        return depToConflicts;
    }

    /**
     * Conflict structure just to keep conflicts
     */
    private static class Conflict {

        private final String a;
        private final String b;

        public Conflict(String a, String b) {
            this.a = a;
            this.b = b;
        }

    }

}


