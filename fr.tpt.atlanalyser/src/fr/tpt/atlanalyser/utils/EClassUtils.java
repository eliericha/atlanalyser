package fr.tpt.atlanalyser.utils;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class EClassUtils {

    public static Set<EReference> getAllPossibleContainingReferences(
            EClass eClass) {
        Set<EClass> superTypes = Sets.newHashSet(eClass.getEAllSuperTypes());
        Set<EClass> subTypes = getAllSubclasses(eClass);
        SetView<EClass> allTypes = Sets.union(Collections.singleton(eClass),
                Sets.union(superTypes, subTypes));

        Resource eResource = eClass.eResource();
        Iterable<EObject> contents = () -> eResource.getAllContents();

        Set<EReference> res = StreamSupport
                .stream(contents.spliterator(), false)
                .filter(o -> o instanceof EReference)
                .map(o -> (EReference) o)
                .filter(r -> r.isContainment()
                        && allTypes.contains(r.getEType()))
                .collect(Collectors.toSet());

        return res;
    }

    public static Set<EClass> getAllSubclasses(EClass eClass) {
        Resource resource = eClass.eResource();

        Iterable<EObject> contents = () -> resource.getAllContents();

        Set<EClass> res = StreamSupport.stream(contents.spliterator(), false)
                .filter(o -> o instanceof EClass).map(o -> (EClass) o)
                .filter(c -> c != eClass && eClass.isSuperTypeOf(c))
                .collect(Collectors.toSet());

        return res;
    }

}
