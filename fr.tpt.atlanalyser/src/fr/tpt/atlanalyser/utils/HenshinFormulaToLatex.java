package fr.tpt.atlanalyser.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.model.And;
import org.eclipse.emf.henshin.model.BinaryFormula;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Not;
import org.eclipse.emf.henshin.model.Or;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.util.HenshinSwitch;

import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

public class HenshinFormulaToLatex {

    private static final double SCALE      = 0.5;
    private static final String FACTOR_VAR = "factor";

    private static class InternalFormulaToLatex extends HenshinSwitch<String> {

        private static final String       COLORSCHEME = "dark28";
        private static final List<String> COLORS;

        static {
            COLORS = Lists.<Integer> newArrayList(1, 2, 3, 4, 5, 6, 7, 8)
                    .stream().map(i -> "/" + COLORSCHEME + "/" + i.toString())
                    .collect(Collectors.toList());
        }

        Map<Node, String>                 nodeToColor = Maps.newHashMap();

        private static final String       NL          = "\n";
        File                              tmpDir;

        public InternalFormulaToLatex(File tmpDir) {
            this.tmpDir = tmpDir;
        }

        private int tmpCounter = 0;

        @Override
        public String caseBinaryFormula(BinaryFormula object) {
            String op = object instanceof And ? "\\wedge"
                    : object instanceof Or ? "\\vee" : "?";

            List<Formula> junctionedFormulas = NGCUtils
                    .collectJunctionedFormulas(object);

            String result = "& " + op + " \\left\\{ \\begin{aligned}" + NL;

            for (Formula formula : junctionedFormulas) {
                result += this.doSwitch(formula) + " \\\\" + NL;
            }

            result += "\\end{aligned} \\right.";

            return result;
        }

        @Override
        public String caseOr(Or object) {
            Formula left = object.getLeft();
            Formula right = object.getRight();

            if (left instanceof Not && !(right instanceof Not)) {
                Not not = (Not) left;
                return this.doSwitch(not.getChild()) + " \\; \\Rightarrow \\; "
                        + this.doSwitch(right);
            }

            return super.caseOr(object);
        }

        @Override
        public String caseNot(Not object) {
            if (NGCUtils.isFalse(object)) {
                return "false";
            } else {
                if (object.getChild() instanceof NestedCondition) {
                    NestedCondition nc = (NestedCondition) object.getChild();
                    Graph conclusion = nc.getConclusion();
                    Formula subFormula = conclusion.getFormula();
                    if (subFormula instanceof Not) {
                        Not not = (Not) subFormula;
                        File pdfFile = toPdf(nc.getConclusion());
                        String result = String
                                .format("& \\forall \\left( \\vcenter{\\hbox{\\includegraphics[scale=\\%s]{%s}}}",
                                        FACTOR_VAR, pdfFile);

                        result += " ," + NL + "\\begin{aligned} "
                                + this.doSwitch(not.getChild()) + NL
                                + "\\end{aligned}";

                        result += " \\right)";

                        return result;
                    }
                }
            }

            return "\\neg " + this.doSwitch(object.getChild());
        }

        @Override
        public String caseNestedCondition(NestedCondition object) {
            if (NGCUtils.isTrue(object)) {
                return "true";
            }

            Graph conclusion = object.getConclusion();
            Formula subFormula = conclusion.getFormula();

            File pdfFile = toPdf(conclusion);

            String result = String
                    .format("& \\exists \\left( \\vcenter{\\hbox{\\includegraphics[scale=\\%s]{%s}}}",
                            FACTOR_VAR, pdfFile);

            if (subFormula != null) {
                result += " ," + NL + "\\begin{aligned} "
                        + this.doSwitch(subFormula) + NL + "\\end{aligned}";
            }

            result += " \\right)";

            return result;
        }

        public File toPdf(Graph conclusion) {
            String dot = "digraph g { node [shape=box]\n";
            dot += "margin=\"0.1,0.01\"\n";

            NestedCondition nc = (NestedCondition) conclusion.eContainer();
            MappingList mappings = nc.getMappings();

            EList<Node> nodes = conclusion.getNodes();

            List<String> usedColors = Lists.newArrayList();

            for (Node node : nodes) {
                Node origin = mappings.getOrigin(node);
                if (origin != null) {
                    usedColors.add(nodeToColor.get(origin));
                }
            }

            ArrayDeque<String> remainingColors = Queues.newArrayDeque(COLORS);
            remainingColors.removeAll(usedColors);

            // assign colors
            for (Node n : nodes) {
                Node origin = mappings.getOrigin(n);
                if (origin != null) {
                    nodeToColor.put(n, nodeToColor.get(origin));
                } else {
                    String color = remainingColors.isEmpty() ? "black"
                            : remainingColors.pop();
                    nodeToColor.put(n, color);
                }
            }

            Map<Node, String> nodeToId = Maps.newHashMap();

            int i = 0;

            for (Node node : nodes) {
                String nodeId = "n" + Integer.toString(i++);
                nodeToId.put(node, nodeId);

                String attributes = "";
                if (!node.getAttributes().isEmpty()) {
                    attributes += "\n"
                            + Joiner.on("\n").join(
                                    node.getAttributes()
                                            .stream()
                                            .map(a -> String.format("%s = %s",
                                                    a.getType().getName(),
                                                    a.getValue()))
                                            .collect(Collectors.toList()));
                }

                String color = nodeToColor.get(node);

                dot += String.format("%s [label=\"%s:%s%s\",color=\"%s\"];\n",
                        nodeId, node.getName() != null ? node.getName() : "",
                        node.getType().getName(), attributes,
                        color);
            }

            for (Edge edge : conclusion.getEdges()) {
                String index = edge.getIndex();
                dot += String.format("%s -> %s [label=\"%s"
                        + (index != null && index != "" ? "[%s]" : "") + "\"];\n", nodeToId
                        .get(edge.getSource()), nodeToId.get(edge.getTarget()),
                        edge.getType().getName(), index);
            }

            dot += "}";

            File dotPath = Utils.findExecOnPath("dot");

            File pdfFile = new File(tmpDir, String.format("tmp%d.pdf",
                    tmpCounter++));

            String[] cmd = new String[] { dotPath.toString(), "-Tpdf",
                    "-o" + pdfFile.toString() };

            Utils.executeCommand(cmd, dot);
            return pdfFile;
        }
    }

    public static String getHeader() {
        return String.format("\\newcommand{\\%s}{%.2f}\n", FACTOR_VAR, SCALE);
    }

    public static String toLatex(Formula f) {
        Path tmpDir = null;
        try {
            tmpDir = Files.createTempDirectory("atlanalyser-henshinlatex");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String latex = new InternalFormulaToLatex(tmpDir.toFile()).doSwitch(f);
        return latex;
    }

    public static String toLatex(Module module) {
        String latex = getHeader();
        for (Unit unit : module.getUnits()) {
            if (unit instanceof Rule) {
                Rule rule = (Rule) unit;
                Formula formula = rule.getLhs().getFormula();
                if (formula != null) {
                    latex += "\\begin{align*} " + rule.getName() + " = ";
                    latex += toLatex(formula);
                    latex += " \\end{align*}\n";
                }
            }
        }
        return latex;
    }

}
