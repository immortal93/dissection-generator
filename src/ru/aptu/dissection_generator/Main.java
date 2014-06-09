package ru.aptu.dissection_generator;

import java.io.IOException;

public class Main {
    
    private static final String USAGE = "\n\nUsage: <filename> <max_order> <polygon_sides_1> <polygon_sides_2> ... , where:\n" +
    "<filename> is a *.gv file to store output in graphviz format;\n" +
    "<max_order> is an integer, specifying maximal number of sides in dissected polygon;\n" +
    "<polygon_sides_i> is an integer, showing that generated dissecitons can contain polygons with such number of sides.\n\n" +
    "Try to pass 'graph.gv 8 3 4', to see what happens.\n";
    
    public static void main(String[] args) {
        if(args.length < 3) {
            System.out.println(USAGE);
            return;
        }
        
        int maxOrder;
        int[] polygonSizes;
        
        try {
            maxOrder = Integer.parseInt(args[1]);
            polygonSizes = new int[args.length - 2];
            for (int i = 2; i < args.length; ++i) {
                polygonSizes[i - 2] = Integer.parseInt(args[i]);
            }
        } catch (NumberFormatException e) {
            System.err.println("Can't parse integer value. Exception generated " + e.getMessage());
            return;
        }
        
        try (FileGraphVizDissectionPrinter printer = new FileGraphVizDissectionPrinter(args[0])) {
            for (int size : polygonSizes) {
                Dissection root = new Dissection(size, polygonSizes);
                Generator.generateSubtreeFrom(root, maxOrder);
                printer.print(root);
            }
        } catch (IOException e) {
            System.err.println("Can't open file " + args[0]);
        }
    }
}
