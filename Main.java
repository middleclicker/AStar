import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/*
Fails to find the path A LOT...???
I have no idea why, if someone could look at it and tell me what went wrong that would be great.
*/
public class Main {
    public static void main(String[] args) {
        int[][] map = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };

        Node result = algorithm(map, 0, 0, 9, 9);
        List<Node> nodes = new ArrayList<>();
        nodes.add(new Node(0, 0, 0));
        nodes.addAll(tracePath(result, new ArrayList<>()));

        new Grid(output(nodes, map));
    }

    public static Node algorithm(int[][] map, int initX, int initY, int goalX, int goalY) {
        ArrayList<Node> OPEN = new ArrayList<>(); // Initialize the open list
        ArrayList<Node> CLOSED = new ArrayList<>(); // Initialize the closed list
        OPEN.add(new Node(initX, initY, 0)); // Put the starting node on the open list (you can leave its f at zero)
        while (!OPEN.isEmpty()) { // While the open list is not empty
            Node q = getSmallestF(OPEN); // Find the node with the least f on the open list, call it "q"
            OPEN.remove(q); // Pop q off the open list
            List<Node> successors = generateSuccessors(q, map); // generate q's 8 successors and set their parents to q
            for (Node n : successors) { // For each successor
                if (n.x == goalX && n.y == goalY) { // If successor is the goal, stop search
                    return n;
                }
                // Else, compute both g and h for successor
                n.g_cost = getDistAdjacent(n, q) + q.g_cost; // successor.g = q.g + distance between successor and q
                n.h_cost = calcDiagonalDist(n.x, n.y, goalX, goalY); // successor.h = distance from goal to successor
                n.f_cost = n.g_cost + n.h_cost; // successor.f = successor.g + successor.h

                // If a node with the same position as successor is in the OPEN list which has a lower f than successor skip this successor
                if (findElement(OPEN, n) != null && findElement(OPEN, n).f_cost < n.f_cost) continue;

                // If a node with the same position as successor is in the CLOSED list which has a lower f than successor, skip this successor.
                if (findElement(CLOSED, n) != null && findElement(CLOSED, n).f_cost < n.f_cost) continue;
                OPEN.add(n); // Otherwise, add the node to the open list.
            }
            CLOSED.add(q); // Push q on the closed list
        }
        return null;
    }

    public static List<Node> tracePath(Node n, List<Node> list) {
        if (n.parent == null) {
            return list;
        }
        list.add(n);
        return tracePath(n.parent, list);
    }

    public static Node findElement(List<Node> nodeList, Node target) {
        if (nodeList.isEmpty()) return null;
        for (Node n : nodeList) {
            if (n.x == target.x && n.y == target.y) {
                return n;
            }
        }
        return null;
    }

    public static int calcDiagonalDist(int x1, int y1, int x2, int y2) {
        /*
        dx = abs(current_cell.x – goal.x)
        dy = abs(current_cell.y – goal.y)

        h = D * (dx + dy) + (D2 - 2 * D) * min(dx, dy)

        where D is length of each node(usually = 1) and D2 is diagonal distance between each node (usually = sqrt(2) ).
        When to use this heuristic? – When we are allowed to move in eight directions only (similar to a move of a King in Chess)
         */
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        int D = 10;
        int D2 = 14;
        int h = D * (dx + dy) + (D2 - 2 * D) * Math.min(dx, dy);
        return h;
    }

    public static int getDistAdjacent(Node n1, Node n2) {
        /*
        Dumb way of doing it, but I can't think of anything better atm
        N2 N2 N2
        N2 N1 N2
        N2 N2 N2
         */
        // Corners
        if ((n2.x == n1.x - 1 && n2.y == n1.y + 1) || // Top left
                (n2.x == n1.x + 1 && n2.y == n1.y + 1) || // Top right
                (n2.x == n1.x - 1 && n2.y == n1.x - 1) || // Bottom left
                (n2.x == n1.x + 1 && n2.y == n1.y - 1)) { // Bottom right
            return 14;
        }
        // Sides
        if ((n2.x == n1.x && n2.y == n1.y + 1) || // Up
                (n2.x == n1.x - 1 && n2.y == n1.y) || // Left
                (n2.x == n1.x + 1 && n2.y == n1.y) || // Right
                (n2.x == n1.x && n2.y == n1.y - 1)) { // Down
            return 10;
        }
        return 0; // Not adjacent, idk go fuck yourself
    }

    public static Node getSmallestF(List<Node> nodeList) {
        if (nodeList.isEmpty()) return null;

        Node smallest = nodeList.get(0);
        for (Node i : nodeList) {
            if (i.f_cost < smallest.f_cost) smallest = i;
        }
        return smallest;
    }

    public static int[][] output(List<Node> nodeList, int[][] map) {
        for (Node i : nodeList) {
            map[i.x][i.y] = 2;
        }
        return map;
    }

    public static List<Node> generateSuccessors(Node q, int[][] map) {
        List<Node> output = new ArrayList<>();
        for (int i = -1; i < 2; i++) { // -1, 0, 1
            for (int j = -1; j < 2; j++) {
                int x = q.x + i;
                int y = q.y + j;
                if (x > 0 && y > 0 && x < 10 && y < 10 && x != q.x && y != q.y && map[x][y] != 1) {
                    output.add(new Node(x, y, 0, q)); // F_cost to be calculated
                }
            }
        }
        return output;
    }
}

class Node {
    public Node parent; // Parent node
    public int g_cost;
    public int h_cost;
    public int f_cost; // g_cost (Distance from starting node) + f_cost (Distance from end node)
    public int x;
    public int y;

    public Node(int x, int y, int f_cost) {
        this.f_cost = f_cost;
        this.x = x;
        this.y = y;
        this.parent = null; // Starting node
    }

    public Node(int x, int y, int f_cost, Node parent) {
        this.f_cost = f_cost;
        this.x = x;
        this.y = y;
        this.parent = parent;
    }
}

class Grid {
    public Grid(int[][] output) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                         UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                JFrame frame = new JFrame("A Star Pathfinding");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new TestPane(output));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class TestPane extends JPanel {
        int[][] output;

        public TestPane(int[][] output) {
            this.output = output;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(500, 500);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            int size = Math.min(getWidth() - 4, getHeight() - 4) / 10;
            int width = getWidth() - (size * 2);
            int height = getHeight() - (size * 2);

            int y = (getHeight() - (size * 10)) / 2;
            for (int horz = 0; horz < 10; horz++) {
                int x = (getWidth() - (size * 10)) / 2;
                for (int vert = 0; vert < 10; vert++) {
                    g.setColor(new Color(0, 0, 0));
                    g.drawRect(x, y, size, size);
                    if (output[horz][vert] == 1) {
                        g.setColor(Color.decode("#011627"));
                        g.fillRect(x, y, size, size);
                    } else if (output[horz][vert] == 2) {
                        g.setColor(Color.decode("#2EC4B6"));
                        g.fillRect(x, y, size, size);
                    }
                    x += size;
                }
                y += size;
            }
            g2d.dispose();
        }

    }
}
