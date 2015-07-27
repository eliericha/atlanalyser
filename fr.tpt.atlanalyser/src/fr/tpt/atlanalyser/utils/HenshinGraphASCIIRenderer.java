package fr.tpt.atlanalyser.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Node;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;

public class HenshinGraphASCIIRenderer {

    public static String render(Graph graph) {

        if (graph.eIsProxy()) {
            return "proxy: " + EcoreUtil.getURI(graph);
        }

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
            dot += String.format("%s -> %s [label=\"%s\"];\n",
                    nodeToId.get(edge.getSource()),
                    nodeToId.get(edge.getTarget()), edge.getType().getName());
        }

        dot += "}";

        String pathVar = System.getenv("PATH");
        List<String> pathDirs = Lists.newArrayList(Splitter.on(
                File.pathSeparator).split(pathVar));

        pathDirs.add("/usr/local/bin");

        File graphEasyPath = null;
        for (String dir : pathDirs) {
            File candidate = new File(dir, "graph-easy");
            if (candidate.canExecute()) {
                graphEasyPath = candidate;
                break;
            }
        }

        if (graphEasyPath == null) {
            return "Could not find 'graph-easy' on PATH";
        }

        ProcessBuilder procBuilder = new ProcessBuilder(
                graphEasyPath.getPath(), "--as=ascii");
        procBuilder.redirectErrorStream(true);

        String output = null;
        try {
            Process process = procBuilder.start();
            OutputStream outputStream = process.getOutputStream();
            outputStream.write(dot.getBytes());
            outputStream.flush();
            outputStream.close();
            InputStream inputStream = process.getInputStream();
            InputStreamReader isReader = new InputStreamReader(inputStream);

            output = CharStreams.toString(isReader);
        } catch (IOException e) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(buffer));
            output = buffer.toString();
        }

        return "Name: " + graph.getName() + "\n" + output;
    }
}
