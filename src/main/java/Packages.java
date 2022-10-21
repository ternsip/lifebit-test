import java.util.HashSet;
import java.util.Set;

public class Packages {

    private final RuleSet ruleSet;
    private Set<String> toggledDependencies = new HashSet<>();
    private final Set<String> toggledRoots = new HashSet<>(); // Keep toggles to prevent sub-tree disabling

    public Packages(RuleSet ruleSet) {
        this.ruleSet = ruleSet;
    }

    public void toggle(String a) {
        if (toggledRoots.contains(a)) {
            toggledRoots.remove(a);
            toggledDependencies = ruleSet.getAllDependencies(toggledRoots);
        } else {
            toggledRoots.add(a);
            Set<String> newDependencies = ruleSet.getAllDependencies(Set.of(a));
            Set<String> directlyConflicting = ruleSet.getAllDirectlyConflictingDependencies(newDependencies);
            Set<String> conflictingDependencies = ruleSet.getAllDependencies(directlyConflicting);
            toggledRoots.removeAll(conflictingDependencies);
            toggledDependencies.addAll(newDependencies);
            toggledDependencies.removeAll(conflictingDependencies);
        }
    }

    public boolean isSameWith(Set<String> targetSet) {
        return toggledDependencies.equals(targetSet);
    }

}
