import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Packages {

    private final RuleSet ruleSet;
    private final Set<String> toggledDependencies = new HashSet<>(); // Source dependencies including subtree
    private final Set<String> toggledSources = new HashSet<>(); // Source dependencies without subtree

    public Packages(RuleSet ruleSet) {
        this.ruleSet = ruleSet;
    }

    /**
     * Toggle some dependency
     * If dependency is already active, this function deactivate it with all sub-dependencies
     * In case of conflict with existing dependencies they are going to be deactivated
     *
     * @param a target dependency for toggling
     */
    public void toggle(String a) {
        if (toggledSources.contains(a)) {
            toggledSources.remove(a);
            toggledDependencies.clear();
            toggledDependencies.addAll(getDependencyTreeSafe(toggledSources, Set.of(), Set.of()));
        } else {
            toggledDependencies.clear();
            toggledDependencies.addAll(getDependencyTreeSafe(Set.of(a), Set.of(), Set.of()));
            Set<String> conflictingDependencies = getConflictingDependencies(toggledDependencies);
            Set<String> conflictingSources = new HashSet<>();
            for (String source : toggledSources) {
                Set<String> dTree = getDependencyTreeSafe(Set.of(source), conflictingDependencies, toggledDependencies);
                if (!dTree.isEmpty()) {
                    toggledDependencies.addAll(dTree);
                } else {
                    conflictingSources.add(source);
                }
            }
            toggledSources.removeAll(conflictingSources);
            toggledSources.add(a);
        }

    }

    /**
     * Check packages have similar active dependencies with given set
     *
     * @param targetSet raw dependencies to check
     * @return true if all dependencies are similar, order doesn't matter, case sensitive
     */
    public boolean isSameWith(Set<String> targetSet) {
        return toggledDependencies.equals(targetSet);
    }

    /**
     * Collects set of dependencies that are in conflict with any of path dependencies
     * @param pathDependencies Dependencies that are part of the path
     * @return Set of dependencies that are in conflict with path's dependencies
     */
    private Set<String> getConflictingDependencies(Set<String> pathDependencies) {
        Set<String> conflictingDependencies = new HashSet<>();
        for (String dependency : pathDependencies) {
            conflictingDependencies.addAll(ruleSet.getDepToConflicts().getOrDefault(dependency, Set.of()));
        }
        return conflictingDependencies;
    }

    /**
     * Basically using DFS starting from sources
     * Stopping DFS when visiting used dependency OR already enabled dependency (to speed-up process)
     * Interrupting DFS when prohibited dependency has occurred on the path and returning Empty Set
     * @param roots Dependency sources to start tree search from
     * @param prohibitedDependencies Dependencies that are prohibited to be visited during tree search
     * @param alreadyEnabledDependencies Dependencies that have already been enabled, used to speed up the whole process
     * @return Full DFS path or Empty Set if such contains prohibited dependency
     */
    private Set<String> getDependencyTreeSafe(
            Set<String> roots,
            Set<String> prohibitedDependencies,
            Set<String> alreadyEnabledDependencies
    ) {
        Stack<String> stack = new Stack<>();
        Set<String> used = new HashSet<>();
        stack.addAll(roots);
        while (!stack.isEmpty()) {
            String top = stack.pop();
            if (prohibitedDependencies.contains(top)) {
                return Set.of(); // Return empty in case some dependency is prohibited
            }
            if (used.contains(top) || alreadyEnabledDependencies.contains(top)) {
                continue;
            }
            used.add(top);
            stack.addAll(ruleSet.getDepToChildren().getOrDefault(top, Collections.emptySet()));
        }
        return used;
    }


}
