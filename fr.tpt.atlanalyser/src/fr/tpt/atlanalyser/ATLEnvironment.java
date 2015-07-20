/*******************************************************************************
 * Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Elie Richa - initial implementation
 *******************************************************************************/
package fr.tpt.atlanalyser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import fr.tpt.atlanalyser.atl.ATL.Binding;
import fr.tpt.atlanalyser.atl.ATL.InPatternElement;
import fr.tpt.atlanalyser.atl.ATL.MatchedRule;
import fr.tpt.atlanalyser.atl.ATL.Module;
import fr.tpt.atlanalyser.atl.ATL.ModuleElement;
import fr.tpt.atlanalyser.atl.ATL.OutPatternElement;
import fr.tpt.atlanalyser.atl.ATL.Rule;

public class ATLEnvironment {

    private Set<Module>       modules;
    private Map<String, Rule> nameToRule = new HashMap<String, Rule>();

    public ATLEnvironment(Collection<Module> modules)
            throws ATLAnalyserException {
        this();
        add(modules);
    }

    public ATLEnvironment() {
        this.modules = new LinkedHashSet<Module>();
    }

    public void add(Module newModule) throws ATLAnalyserException {
        add(Collections.singleton(newModule));
    }

    public void add(Collection<Module> newModules) throws ATLAnalyserException {
        if (modules.addAll(newModules)) {
            for (Module module : newModules) {
                final EList<ModuleElement> elements = module.getElements();
                for (ModuleElement moduleElement : elements) {
                    if (moduleElement instanceof Rule) {
                        Rule rule = (Rule) moduleElement;
                        if (!nameToRule.containsKey(rule.getName())) {
                            nameToRule.put(rule.getName(), rule);
                        } else {
                            throw new ATLAnalyserException(
                                    "Multiple rules with same name '%s' not yet supported",
                                    rule.getName());
                        }
                    }
                }
            }
        }
    }

    public Rule getRule(String name) {
        return nameToRule.get(name);
    }

    /**
     * Returns all OutPatternElements of rule, including inherited ones.
     * 
     * @param rule
     * @return
     */
    public List<OutPatternElement> getAllOutPatternElements(Rule rule) {
        EList<OutPatternElement> result = new BasicEList<OutPatternElement>();

        result.addAll(rule.getOutPattern().getElements());

        result.addAll(getAllSuperOutPatternElements(rule));

        return result;
    }

    /**
     * Returns the list of all OutPatternElements in super rules, recursively
     * following rule inheritance.
     * 
     * @param rule
     * @return
     */
    public List<OutPatternElement> getAllSuperOutPatternElements(Rule rule) {
        EList<OutPatternElement> result = new BasicEList<OutPatternElement>();

        if (rule instanceof MatchedRule) {
            MatchedRule mRule = (MatchedRule) rule;

            for (Rule superRule : getAllSuperRules(mRule)) {
                if (superRule.getOutPattern() != null) {
                    result.addAll(superRule.getOutPattern().getElements());
                }
            }
        }

        return result;
    }

    public List<OutPatternElement> getSuperOutPatternElements(Rule rule) {
        EList<OutPatternElement> result = new BasicEList<OutPatternElement>();

        if (rule instanceof MatchedRule) {
            MatchedRule mRule = (MatchedRule) rule;

            for (Rule superRule : getSuperRules(mRule)) {
                result.addAll(superRule.getOutPattern().getElements());
            }
        }

        return result;
    }

    /**
     * Returns the list of OutPatternElements, from super rules, that have the
     * same name as outPatternElement.
     * 
     * @param outPatternElement
     * @param context
     * @return
     */
    public List<OutPatternElement> getAllSuperOutPatternElements(
            OutPatternElement outPatternElement) {
        EList<OutPatternElement> result = new BasicEList<OutPatternElement>();

        if (outPatternElement.getOutPattern().getRule() instanceof MatchedRule) {
            MatchedRule rule = (MatchedRule) outPatternElement.getOutPattern()
                    .getRule();

            for (OutPatternElement superOutPatternElement : getAllSuperOutPatternElements(rule)) {
                if (superOutPatternElement.getVarName().equals(
                        outPatternElement.getVarName())) {
                    result.add(superOutPatternElement);
                }
            }
        }

        return result;
    }

    public List<OutPatternElement> getSuperOutPatternElements(
            OutPatternElement outPatternElement) {
        EList<OutPatternElement> result = new BasicEList<OutPatternElement>();

        if (outPatternElement.getOutPattern().getRule() instanceof MatchedRule) {
            MatchedRule rule = (MatchedRule) outPatternElement.getOutPattern()
                    .getRule();

            for (OutPatternElement superOutPatternElement : getSuperOutPatternElements(rule)) {
                if (superOutPatternElement.getVarName().equals(
                        outPatternElement.getVarName())) {
                    result.add(superOutPatternElement);
                }
            }
        }

        return result;
    }

    private static List<Binding> getBindingsOfProperty(List<Binding> bindings,
            String propertyName) {
        List<Binding> result = new ArrayList<Binding>();
        for (Binding binding : bindings) {
            if (binding.getPropertyName().equals(propertyName)) {
                result.add(binding);
            }
        }
        return result;
    }

    private void recursiveMergeBindingsOverInheritance(
            OutPatternElement outPatternElement, List<Binding> targetList)
            throws Exception {
        for (Binding binding : outPatternElement.getBindings()) {
            List<Binding> existingBindings = getBindingsOfProperty(targetList,
                    binding.getPropertyName());

            if (existingBindings.size() == 1) {
                // Check for an inheritance conflict
                Rule rule1 = binding.getOutPatternElement().getOutPattern()
                        .getRule();
                Rule rule2 = existingBindings.get(0).getOutPatternElement()
                        .getOutPattern().getRule();
                if (!isSuperRuleOf(rule1, rule2)
                        && !isSuperRuleOf(rule2, rule1)) {
                    throw new Exception(
                            String.format(
                                    "Conflict in multiple inheritance detected with rules %s and %s",
                                    rule1.getName(), rule2.getName()));
                }
            } else {
                targetList.add(binding);
            }
        }

        // for (OutPatternElement superOutPatternElement :
        // getSuperOutPatternElements(superOutPatternElement)) {
        //
        // }
    }

    public List<Binding> mergeBindingsOverInheritance(
            OutPatternElement outPatternElement) {
        List<Binding> result = new ArrayList<Binding>(
                outPatternElement.getBindings());

        // TODO this is wrong

        List<Binding> inheritedBindings = new ArrayList<Binding>();

        // add inherited bindings
        for (OutPatternElement superOutPattern : getAllSuperOutPatternElements(outPatternElement)) {
            inheritedBindings.addAll(superOutPattern.getBindings());
        }

        for (Binding binding : inheritedBindings) {
            List<Binding> existingBindings = getBindingsOfProperty(result,
                    binding.getPropertyName());
            Binding existingBinding = existingBindings.size() > 0 ? existingBindings
                    .get(0) : null;

            if (existingBinding == null) {
                result.add(binding);
            } else {
                Rule existingRule = existingBinding.getOutPatternElement()
                        .getOutPattern().getRule();
                Rule newBindingRule = binding.getOutPatternElement()
                        .getOutPattern().getRule();

                if (isSuperRuleOf(existingRule, newBindingRule)) {
                    result.remove(existingBinding);
                    result.add(binding);
                }
            }
        }

        return result;
    }

    public boolean isSuperRuleOf(Rule parent, Rule child) {
        return getAllSuperRules(child).contains(parent);
    }

    public List<Rule> getSuperRules(Rule rule) {
        List<Rule> superRules = new ArrayList<Rule>();
        EList<String> precedingComments = rule.getCommentsBefore();

        for (String commentLine : precedingComments) {
            if (commentLine.contains("@extends")) {
                String[] split = commentLine.split("@extends\\s+");
                String extendedRules = split[1].trim();
                String[] ruleNames = extendedRules.split(",\\s+");

                for (String ruleName : ruleNames) {
                    final Rule superRule = getRule(ruleName);
                    superRules.add(superRule);
                }
            }
        }

        return superRules;
    }

    public List<Rule> getAllSuperRules(Rule rule) {
        List<Rule> allSuperRules = getSuperRules(rule);
        List<Rule> toAdd = new ArrayList<Rule>();

        // TODO cyclic inheritance will create an infinite recursion here
        for (Rule superRule : allSuperRules) {
            toAdd.addAll(getAllSuperRules(superRule));
        }

        allSuperRules.addAll(toAdd);

        return allSuperRules;
    }

    private static <T extends EObject> void copyIntoList(List<T> list, T obj) {
        list.add(EcoreUtil.copy(obj));
    }

    private static <T extends EObject> void copyIntoList(List<T> target,
            List<T> source) {
        for (T obj : source) {
            copyIntoList(target, obj);
        }
    }

    public List<InPatternElement> getAllSuperInPatternElements(MatchedRule rule) {
        List<InPatternElement> result = new ArrayList<InPatternElement>();

        if (rule instanceof MatchedRule) {
            MatchedRule mRule = (MatchedRule) rule;

            for (Rule superRule : getAllSuperRules(mRule)) {
                MatchedRule superMatchedRule = (MatchedRule) superRule;
                if (superMatchedRule.getInPattern() != null) {
                    result.addAll(superMatchedRule.getInPattern().getElements());
                }
            }
        }

        return result;
    }

    public List<InPatternElement> getAllSuperInPatternElements(
            InPatternElement patternElement) {
        EList<InPatternElement> result = new BasicEList<InPatternElement>();

        MatchedRule rule = patternElement.getInPattern().getRule();
        List<Rule> allSuperRules = getAllSuperRules(rule);
        String varName = patternElement.getVarName();

        for (Rule superRule : allSuperRules) {
            MatchedRule mRule = (MatchedRule) superRule;
            for (InPatternElement superPatElem : mRule.getInPattern()
                    .getElements()) {
                if (superPatElem.getVarName().equals(varName)) {
                    result.add(superPatElem);
                }
            }
        }

        return result;
    }
}