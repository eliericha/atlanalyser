package fr.tpt.atlanalyser.utils;

import java.io.File;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Node;

import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;

public class HenshinGraphASCIIRenderer {

    public static String render(Graph graph) {

        if (graph.eIsProxy()) {
            return "proxy: " + EcoreUtil.getURI(graph);
        }

        String dot = graphToDot(graph);

        File graphEasyPath = getGraphEasyExec();

        if (graphEasyPath == null) {
            return "Could not find 'graph-easy' on PATH";
        }

        String[] cmd = new String[] { graphEasyPath.getPath(), "--as=ascii" };
        String output = Utils.executeCommand(cmd, dot);

        return "Name: " + graph.getName() + "\n" + output;
        // return dot;
    }

    public static File getGraphEasyExec() {
        File graphEasyPath = Utils.findExecOnPath("graph-easy");
        return graphEasyPath;
    }

    public static String graphToDot(Graph graph) {
        String dot = "digraph g { node [shape=box]\n";

        Map<Node, String> nodeToId = Maps.newHashMap();

        int i = 0;
        for (Node node : graph.getNodes()) {
            String nodeId = "n" + Integer.toString(i++);
            nodeToId.put(node, nodeId);

            dot += String.format("%s [label=\"%s:%s\"];\n", nodeId, node
                    .getName() != null ? node.getName() : "", node.getType()
                    .getName());
        }

        for (Edge edge : graph.getEdges()) {
            Integer index = edge.getIndexConstant();
            dot += String.format("%s -> %s [label=\"%s"
                    + (index != null ? "[%s]" : "") + "\"];\n",
                    nodeToId.get(edge.getSource()),
                    nodeToId.get(edge.getTarget()), edge.getType().getName(),
                    index);
        }

        dot += "}";
        return dot;
    }
}
