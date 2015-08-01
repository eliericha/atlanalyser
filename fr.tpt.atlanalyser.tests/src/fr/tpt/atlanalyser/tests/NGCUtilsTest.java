package fr.tpt.atlanalyser.tests;

import static org.junit.Assert.assertTrue;

import org.eclipse.emf.henshin.model.And;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Or;
import org.junit.Test;

import com.google.common.collect.Lists;

import fr.tpt.atlanalyser.utils.NGCUtils;

public class NGCUtilsTest {

    private static final HenshinFactory HF = HenshinFactory.eINSTANCE;

    @Test
    public void testCreateDisjunction() {
        Formula res = NGCUtils.createDisjunction(Lists.newArrayList());

        assertTrue(NGCUtils.isFalse(res));

        NestedCondition nc = HF.createNestedCondition();
        res = NGCUtils.createDisjunction(Lists.newArrayList(nc));
        assertTrue(res == nc);

        NestedCondition nc1 = nc;
        NestedCondition nc2 = HF.createNestedCondition();
        res = NGCUtils.createDisjunction(Lists.newArrayList(nc1, nc2));
        assertTrue(res instanceof Or);
        assertTrue(((Or) res).getLeft() == nc1);
        assertTrue(((Or) res).getRight() == nc2);

        NestedCondition nc3 = HF.createNestedCondition();
        res = NGCUtils.createDisjunction(nc1, nc2, nc3);
        assertTrue(res instanceof Or);
    }

    @Test
    public void testCreateConjunction() {
        Formula res = NGCUtils.createConjunction(Lists.newArrayList());

        assertTrue(NGCUtils.isFalse(res));

        NestedCondition nc = HF.createNestedCondition();
        res = NGCUtils.createConjunction(Lists.newArrayList(nc));
        assertTrue(res == nc);

        NestedCondition nc1 = nc;
        NestedCondition nc2 = HF.createNestedCondition();
        res = NGCUtils.createConjunction(Lists.newArrayList(nc1, nc2));
        assertTrue(res instanceof And);
        assertTrue(((And) res).getLeft() == nc1);
        assertTrue(((And) res).getRight() == nc2);

        NestedCondition nc3 = HF.createNestedCondition();
        res = NGCUtils.createConjunction(nc1, nc2, nc3);
        assertTrue(res instanceof And);
    }
}
