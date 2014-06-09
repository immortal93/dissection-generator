package ru.aptu.dissection_generator;

public class Generator {
    
    private Generator() {
    }

    public static void generateSubtreeFrom(Dissection root, int maxOrder) {
        for (UpperDissection upper : root.createAllUpper()) {
            LowerDissection lower = upper.createArbitraryLower();
            if (lower != null) {
                Dissection probableChild = lower.getUnderlyingDissection();
                if (probableChild.getOrder() <= maxOrder && lower.isParentFor(probableChild)) {
                    root.addChild(probableChild);
                    generateSubtreeFrom(probableChild, maxOrder);
                }
            }
        }
    }

}
