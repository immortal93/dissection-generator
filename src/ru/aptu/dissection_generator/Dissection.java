package ru.aptu.dissection_generator;

import java.util.ArrayList;
import java.util.List;

public class Dissection {
    private int[] polygonSizes;
    private List<Dissection> children = new ArrayList<>();
    private int[][] adjacent;
    
    private static int[] EMPTY = new int[0];
    
    public Dissection(int size, int[] polygonSizes) {
        adjacent = new int[size][];
        for (int i = 0; i < size; ++i) {
            adjacent[i] = EMPTY;
        }
        this.polygonSizes = polygonSizes;
    }
    
    
    private Dissection(Dissection parent, int after, int size) {
        this.polygonSizes = parent.polygonSizes;
        adjacent = new int[parent.getOrder() + size - 2][];
        
        int[] ptrs = new int[getOrder()];
        int before = (after + size - 1) % getOrder();
        
        for (int i = after + 1; i < after + size - 1; ++i) {
            adjacent[i] = EMPTY;
        }
        
        ptrs[after]++;
        
        for (int i = 0; i < parent.getOrder(); ++i) {
            int to = (i <= after ? i : i + size - 2);
            int sz = parent.adjacent[i].length + (to == after || to == before ? 1 : 0);
            adjacent[to] = sz == 0 ? EMPTY : new int[sz];
            for (int v : parent.adjacent[i]) {
                adjacent[to][ptrs[to]++] = (v <= after ? v : v + size - 2);
            }
        }
        
        adjacent[after][0] = before;
        adjacent[before][ptrs[before]++] = after;
    }
    
    public List<Dissection> getChildren() {
        return children;
    }
    
    public void addChild(Dissection child) {
        children.add(child);
    }
    
    public int[] neighbors(int i) {
        return adjacent[i];
    }
    
    public List<UpperDissection> createAllUpper() {
        List<UpperDissection> result = new ArrayList<>();
        for (Integer size : polygonSizes) {
            for (int after = 0; after < getPeriod(); ++after) {
                result.add(new UpperDissection(this, after, size));
            }
        }
        return result;
    }
    
    public Dissection gluePolygon(int after, int size) {
        return new Dissection(this, after, size);
    }
    
    public boolean isCanonicalRoot(int v) {
        return v % getPeriod() == getFirstCanonicalRoot() % getPeriod();
    }
    
    public int getOrder() {
        return adjacent.length;
    }
    
    private int getFirstCanonicalRoot() {
        int best = -1;
        for (int shift = 0; shift < getPeriod(); ++shift) {
            if (canCut(shift) && (best == -1 || compareShifts(best, shift) > 0)) {
                best = shift;
            }
        }
        return best;
    }
    
    private boolean canCut(int at) {
        if (adjacent[at].length == 0) return false;
        int cur = at;
        do {
            cur = (cur + 1) % getOrder();
        } while (adjacent[cur].length == 0);
        return adjacent[at][0] == cur;
    }
    
    private int compareShifts(int s1, int s2) {
        for (int i = 0; i < getOrder(); ++i) {
            int[] a1 = adjacent[(s1 + i) % getOrder()];
            int[] a2 = adjacent[(s2 + i) % getOrder()];
            if (a1.length != a2.length) return a1.length - a2.length;
            
            for (int j = 0; j < a1.length; ++j) {
                int v1 = (a1[j] - s1 + getOrder()) % getOrder();
                int v2 = (a2[j] - s2 + getOrder()) % getOrder();
                if (v1 != v2) return v1 - v2;
            }
        }
        return 0;
    }
    
    private int period = -1;
    
    private int getPeriod() {
        if (period == -1) {
            period = getOrder();
            for (int i = 1; i <= getOrder() / 2; ++i) {
                if (getOrder() % i == 0 && compareShifts(0, i) == 0) {
                    period = i;
                    break;
                }
            }
        }
        return period;
    }
}