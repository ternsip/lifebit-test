import junit.framework.TestCase;
import org.junit.Assert;

import java.util.Set;

public class ApplicationTest extends TestCase {

    public static void testDependsAA() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.addDep("a", "a");
        Assert.assertTrue(ruleSet.isCoherent());
    }

    public static void testDependsAB_BA() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.addDep("a", "b");
        ruleSet.addDep("b", "a");
        Assert.assertTrue(ruleSet.isCoherent());
    }

    public static void testExclusiveAB() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.addDep("a", "b");
        ruleSet.addConflict("a", "b");
        Assert.assertFalse(ruleSet.isCoherent());
    }

    public static void testExclusiveAB_BC() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.addDep("a", "b");
        ruleSet.addDep("b", "c");
        ruleSet.addConflict("a", "c");
        Assert.assertFalse(ruleSet.isCoherent());
    }

    public static void testDeepDeps() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.addDep("a", "b");
        ruleSet.addDep("b", "c");
        ruleSet.addDep("c", "d");
        ruleSet.addDep("d", "e");
        ruleSet.addDep("a", "f");
        ruleSet.addConflict("e", "f");
        Assert.assertFalse(ruleSet.isCoherent());
    }

    public static void testExclusiveAB_BC_CA_DE() {
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

    public static void testAB_BC_Toggle() {
        RuleSet ruleSet = new RuleSet();
        ruleSet.addDep("a", "b");
        ruleSet.addDep("b", "c");
        Packages selectedPackages = new Packages(ruleSet);
        selectedPackages.toggle("c");
        Assert.assertTrue(selectedPackages.isSameWith(Set.of("c")));
    }

    public static void testAB_AC() {
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

}