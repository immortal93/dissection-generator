package ru.aptu.dissection_generator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileGraphVizDissectionPrinter implements AutoCloseable {

    private static final String HEADER = "digraph G {layout=neato;\nnode[shape=point];\n";
    private static final String DISSECTION_VERTEX_FORMAT = "%d [label=\"\",pos=\"%f,%f!\"];";
    private static final String DISSECTION_EDGE_FORMAT = "%d -> %d [dir=none]";
    private static final String ARROW_VERTEX_FORMAT = "%d [shape=\"none\",label=\"\",pos=\"%f,%f!\"];";
    private static final String ARROW_EDGE_FORMAT = "%d -> %d [dir=\"back\", style=\"dashed\"]";
    private static final String FOOTER = "}";


    private PrintWriter pw;
    private int vertex = 1;
    private double globalX = 0;
    private double largestR = 0;


    public FileGraphVizDissectionPrinter(String filename) throws IOException {
        pw = new PrintWriter(new FileWriter(filename));
        pw.println(HEADER);
    }

    private void printForSingleDissection(Dissection dissection, double x, double y) {
        int len = dissection.getOrder();
        for (int i = 0; i < len; ++i) {
            String vertexData = String.format(DISSECTION_VERTEX_FORMAT,
                    (vertex + i),
                    x + 25 * Math.cos(Math.PI * 2 * i / len),
                    y + 25 * (Math.sin(Math.PI * 2 * i / len)));
            pw.println(vertexData);
            for (Integer v : dissection.neighbors(i)) {
                if (v < i) {
                    pw.println(String.format(DISSECTION_EDGE_FORMAT, vertex + i, vertex + v));
                }
            }
            if (len != 2 || i != 0) {
                pw.println(String.format(DISSECTION_EDGE_FORMAT, vertex + i, vertex + (i + 1) % len));
            }
        }
        vertex += len;
    }

    private void fillByDist(Dissection dissection, ArrayList<Integer> counts) {
        int size = dissection.getOrder();
        while (counts.size() <= size) counts.add(0);
        counts.set(size, counts.get(size) + 1);
        for (Dissection child : dissection.getChildren()) {
            fillByDist(child, counts);
        }
    }

    private void printByLevels(Dissection root, ArrayList<Integer> remaining, ArrayList<Integer> counts, ArrayList<Integer> radii) {
        int size = root.getOrder();

        largestR = Math.max(largestR, radii.get(size));

        double rootX = radii.get(size) * Math.sin(2 * Math.PI * remaining.get(size) / counts.get(size)) + globalX;
        double rootY = radii.get(size) * Math.cos(2 * Math.PI * remaining.get(size) / counts.get(size));
        remaining.set(size, remaining.get(size) - 1);

        printForSingleDissection(root, rootX, rootY);

        for (Dissection child : root.getChildren()) {
            size = child.getOrder();

            double childX = radii.get(size) * Math.sin(2 * Math.PI * remaining.get(size) / counts.get(size)) + globalX;
            double childY = radii.get(size) * Math.cos(2 * Math.PI * remaining.get(size) / counts.get(size));

            double mult = Math.sqrt(Math.pow(childX - rootX, 2) + Math.pow(childY - rootY, 2));
            double vx = (childX - rootX) / mult;
            double vy = (childY - rootY) / mult;


            pw.println(String.format(ARROW_VERTEX_FORMAT, vertex, childX - vx * 30, childY - vy * 30));

            pw.println(String.format(ARROW_VERTEX_FORMAT, vertex + 1, rootX + vx * 30, rootY + vy * 30));

            pw.println(String.format(ARROW_EDGE_FORMAT, vertex, vertex + 1));

            vertex += 2;

            printByLevels(child, remaining, counts, radii);
        }

    }

    public void print(Dissection dissection) {
        ArrayList<Integer> counts = new ArrayList<>();
        fillByDist(dissection, counts);

        ArrayList<Integer> remaining = new ArrayList<>();
        remaining.addAll(counts);

        ArrayList<Integer> radii = new ArrayList<>();
        radii.add(0);
        boolean first = true;
        for (int i = 1; i < counts.size(); ++i) {
            int count = counts.get(i);

            if (count == 0) {
                radii.add(radii.get(radii.size() - 1));
                continue;
            }

            if (first) {
                first = false;
                if (count == 1) {
                    radii.add(0);
                } else {
                    radii.add(count * 15);
                }
            } else {
                radii.add(Math.max(count * 15, radii.get(radii.size() - 1) + 150));
            }
        }
        globalX += largestR + radii.get(radii.size() - 1) + 100;
        printByLevels(dissection, remaining, counts, radii);
    }

    @Override
    public void close() {
        pw.println(FOOTER);
        pw.close();
    }
}