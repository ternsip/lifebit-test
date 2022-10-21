import junit.framework.TestCase;
import org.junit.Assert;

import java.util.Set;

/**
 * These are unit tests for whole application, similar ones taken from opts_test.go, test.js or test.py
 * All methods/functions have original names for convenience
 */
public class ApplicationTest extends TestCase {

    public void testDependsAA() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.addDep("a", "a");
        Assert.assertTrue(ruleSet.isCoherent());
    }

    public void testDependsAB_BA() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.addDep("a", "b");
        ruleSet.addDep("b", "a");
        Assert.assertTrue(ruleSet.isCoherent());
    }

    public void testExclusiveAB() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.addDep("a", "b");
        ruleSet.addConflict("a", "b");
        Assert.assertFalse(ruleSet.isCoherent());
    }

    public void testExclusiveAB_BC() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.addDep("a", "b");
        ruleSet.addDep("b", "c");
        ruleSet.addConflict("a", "c");
        Assert.assertFalse(ruleSet.isCoherent());
    }

    public void testDeepDeps() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.addDep("a", "b");
        ruleSet.addDep("b", "c");
        ruleSet.addDep("c", "d");
        ruleSet.addDep("d", "e");
        ruleSet.addDep("a", "f");
        ruleSet.addConflict("e", "f");
        Assert.assertFalse(ruleSet.isCoherent());
    }

    public void testExclusiveAB_BC_CA_DE() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.addDep("a", "b");
        ruleSet.addDep("b", "c");
        ruleSet.addDep("c", "a");
        ruleSet.addDep("d", "e");
        ruleSet.addConflict("c", "e");
        Assert.assertTrue(ruleSet.isCoherent());

        Packages selectedPackages = new Packages(ruleSet);

        selectedPackages.toggle("a");
        Assert.assertTrue(selectedPackages.isSameWith(Set.of("a", "c", "b")));

        ruleSet.addDep("f", "f");
        selectedPackages.toggle("f");
        Assert.assertTrue(selectedPackages.isSameWith(Set.of("a", "c", "b", "f")));

        selectedPackages.toggle("e");
        Assert.assertTrue(selectedPackages.isSameWith(Set.of("e", "f")));

        selectedPackages.toggle("b");
        Assert.assertTrue(selectedPackages.isSameWith(Set.of("a", "c", "b", "f")));

        ruleSet.addDep("b", "g");
        selectedPackages.toggle("g");
        selectedPackages.toggle("b");
        Assert.assertTrue(selectedPackages.isSameWith(Set.of("g", "f")));
    }

    public void testAB_BC_Toggle() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.addDep("a", "b");
        ruleSet.addDep("b", "c");
        Packages selectedPackages = new Packages(ruleSet);
        selectedPackages.toggle("c");
        Assert.assertTrue(selectedPackages.isSameWith(Set.of("c")));
    }

    public void testAB_AC() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.addDep("a", "b");
        ruleSet.addDep("a", "c");
        ruleSet.addConflict("b", "d");
        ruleSet.addConflict("b", "e");
        Assert.assertTrue(ruleSet.isCoherent());
        Packages selectedPackages = new Packages(ruleSet);
        selectedPackages.toggle("d");
        selectedPackages.toggle("e");
        selectedPackages.toggle("a");
        Assert.assertTrue(selectedPackages.isSameWith(Set.of("a", "c", "b")));
    }

    public void testMyCustomBigCoherentTree() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.addDep("a", "c");
        ruleSet.addDep("c", "d");
        ruleSet.addDep("d", "f");
        ruleSet.addDep("c", "e");
        ruleSet.addDep("e", "f");
        ruleSet.addDep("b", "c");
        ruleSet.addDep("g", "k");
        ruleSet.addDep("k", "s");
        ruleSet.addDep("k", "m");
        ruleSet.addDep("m", "r");
        ruleSet.addDep("m", "o");
        ruleSet.addDep("s", "c");
        ruleSet.addConflict("a", "b");
        ruleSet.addConflict("s", "b");
        ruleSet.addConflict("a", "k");
        ruleSet.addConflict("b", "g");
        ruleSet.addConflict("a", "g");
        ruleSet.addConflict("a", "o");
        ruleSet.addConflict("a", "r");
        ruleSet.addConflict("a", "m");
        Assert.assertTrue(ruleSet.isCoherent());
        ruleSet.addConflict("f", "g");
        Assert.assertFalse(ruleSet.isCoherent());
    }

    public void testMyCustomWeb() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.addDep("b", "a");
        ruleSet.addDep("c", "a");
        ruleSet.addDep("d", "a");
        ruleSet.addDep("d", "e");
        ruleSet.addDep("a", "e");
        ruleSet.addDep("a", "f");
        ruleSet.addConflict("b", "c");
        ruleSet.addConflict("c", "d");
        ruleSet.addConflict("b", "d");
        Assert.assertTrue(ruleSet.isCoherent());

        Packages selectedPackages = new Packages(ruleSet);
        selectedPackages.toggle("a");
        Assert.assertTrue(selectedPackages.isSameWith(Set.of("a", "f", "e")));
        selectedPackages.toggle("c");
        Assert.assertTrue(selectedPackages.isSameWith(Set.of("a", "f", "e", "c")));

        ruleSet.addConflict("f", "d");
        Assert.assertFalse(ruleSet.isCoherent());
    }

    public void testMyCustomX() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.addDep("a", "b");
        ruleSet.addDep("b", "c");
        ruleSet.addDep("k", "d");
        ruleSet.addDep("d", "c");
        ruleSet.addDep("c", "e");
        ruleSet.addDep("c", "f");
        ruleSet.addConflict("b", "d");
        Assert.assertTrue(ruleSet.isCoherent());

        Packages selectedPackages = new Packages(ruleSet);
        selectedPackages.toggle("a");
        Assert.assertTrue(selectedPackages.isSameWith(Set.of("a", "b", "c", "e", "f")));
        selectedPackages.toggle("k");
        Assert.assertTrue(selectedPackages.isSameWith(Set.of("k", "d", "c", "e", "f")));

        ruleSet.addConflict("e", "f");
        Assert.assertFalse(ruleSet.isCoherent());
    }

}