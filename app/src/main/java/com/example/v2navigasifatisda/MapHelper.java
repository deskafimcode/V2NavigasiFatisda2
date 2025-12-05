package com.example.v2navigasifatisda;

import androidx.annotation.NonNull;

import java.util.*;

public class MapHelper {

    public static class Node {
        String name;
        int imageResId;

        public Node(String name, int imageResId) {
            this.name = name;
            this.imageResId = imageResId;
        }

        public Node(String name) {
            this.name = name;
            this.imageResId = R.drawable.ic_launcher_foreground;
        }

        @NonNull
        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(name, node.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
    public static class Edge {
        Node from, to;
        String direction1,direction2,vertikal;
        int distance;

        public Edge(Node from, Node to, String direction1,String direction2, int distance, String vertikal) {
            this.from = from;
            this.to = to;
            this.direction1 = direction1;
            this.direction2 = direction2;
            this.distance = distance;
            this.vertikal = vertikal;
        }
    }

    public static class MapGraph {
        Map<Node, List<Edge>> adjList = new HashMap<>();

        private String getBelok(String fromDir, String toDir) {
            List<String> arah = Arrays.asList("utara", "timur", "selatan", "barat");
            int fromIdx = arah.indexOf(fromDir.toLowerCase());
            int toIdx = arah.indexOf(toDir.toLowerCase());

            if (fromIdx == -1 || toIdx == -1) return "belok";

            int selisih = (toIdx - fromIdx + 4) % 4;
            if (selisih == 1) return "belok kanan";
            else if (selisih == 3) return "belok kiri";
            else if (selisih == 2) return "putar balik";
            else return "lurus";
        }

        public ArrayList findShortestPath(Node start, Node goal) {

            if(start.equals(goal)){
                ArrayList<Langkah> hasil = new ArrayList<>();
                hasil.add(new Langkah("Anda Sudah Berada Di Tempat Tujuan", goal.imageResId));
                return hasil;
            }

            Map<Node, Integer> distance = new HashMap<>();
            Map<Node, Node> prev = new HashMap<>();
            PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> distance.getOrDefault(n, Integer.MAX_VALUE)));

            for (Node node : adjList.keySet()) {
                distance.put(node, Integer.MAX_VALUE);
            }

            distance.put(start, 0);
            queue.add(start);

            while (!queue.isEmpty()) {
                Node current = queue.poll();
                if (current.equals(goal)) break;

                for (Edge edge : adjList.get(current)) {
                    Node neighbor = edge.to;
                    int newDist = distance.get(current) + edge.distance;
                    if (newDist < distance.get(neighbor)) {
                        distance.put(neighbor, newDist);
                        prev.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }

            if (!distance.containsKey(goal) || distance.get(goal) == Integer.MAX_VALUE) {
                ArrayList<Langkah> hasil = new ArrayList<>();
                hasil.add(new Langkah("Tidak ada jalur dari " + start + " ke " + goal, goal.imageResId));
                return hasil;
            }

            List<Node> path = new LinkedList<>();
            for (Node at = goal; at != null; at = prev.get(at)) {
                path.add(0, at);
            }

            ArrayList<Langkah> directions = new ArrayList<>();
            String lastDirection = "";
            String lastvertikal = "";
            int segmentDistance = 0;
            Node segmentStart = path.get(0);
            String currentDirection1 = "";
            String currentDirection2 = "";
            String currentvertikal = "";
            StringBuilder Kalimat = new StringBuilder();

            for (int i = 0; i < path.size() - 1; i++) {
                Node from = path.get(i);
                Node to = path.get(i + 1);
                int currentDistance = 0;
                for (Edge edge : adjList.get(from)) {
                    if (edge.to.equals(to)) {
                        currentDirection1 = edge.direction1.toLowerCase();
                        currentDirection2 = edge.direction2.toLowerCase();
                        currentDistance = edge.distance;
                        currentvertikal = edge.vertikal.toLowerCase();
                        break;
                    }
                }
                if (lastDirection.isEmpty()) {
                    segmentDistance = currentDistance;
                    lastDirection = currentDirection2;
                    lastvertikal = currentvertikal;
                    Kalimat.append("Dari ").append(segmentStart).append(", ke ").append(currentDirection1).append(" ");
                } else if (lastDirection.equals(currentDirection1) && lastvertikal.equals(currentvertikal)) {
                    segmentDistance += currentDistance;
                } else if (lastDirection.equals(currentDirection1) && !lastvertikal.equals(currentvertikal)) {
                    if(segmentDistance == 0)Kalimat.append(" menuju ").append(from);
                    else Kalimat.append(segmentDistance).append(" M menuju ").append(from);
                    directions.add(new Langkah(Kalimat.toString(), from.imageResId));
                    Kalimat.setLength(0);
                    Kalimat.append("Dari ").append(from).append(", ").append(currentvertikal).append(" ");
                    segmentStart = from;
                    segmentDistance = currentDistance;
                    lastDirection = currentDirection2;
                    lastvertikal = currentvertikal;
                }else {
                    if(segmentDistance == 0)Kalimat.append(" menuju ").append(from);
                    else Kalimat.append(segmentDistance).append(" M menuju ").append(from);
                    directions.add(new Langkah(Kalimat.toString(), from.imageResId));
                    Kalimat.setLength(0);
                    String belok = getBelok(lastDirection, currentDirection1);
                    Kalimat.append("Dari ").append(from).append(", ").append(belok).append(" lalu ").append(currentvertikal).append(" ");
                    segmentStart = from;
                    segmentDistance = currentDistance;
                    lastDirection = currentDirection2;
                    lastvertikal = currentvertikal;
                }
            }
            Kalimat.append(segmentDistance).append(" M menuju ").append(goal);
            directions.add(new Langkah(Kalimat.toString(), goal.imageResId));
            return directions;
        }


        public void addNode(Node node) {
            adjList.putIfAbsent(node, new ArrayList<>());
        }

        public void addEdge(Node from, Node to, String direction1,String direction2, int distance, String vertikal) {
            adjList.get(from).add(new Edge(from, to, direction1,direction2, distance, vertikal));
        }
        public void addEdge(Node from, Node to, String direction1, int distance, String vertikal) {
            adjList.get(from).add(new Edge(from, to, direction1,direction1, distance, vertikal));
        }

        public List<String> getAllNodeNames() {
            List<String> names = new ArrayList<>();
            for (Node node : adjList.keySet()) {
                names.add(node.name);
            }
            for (int i = 1; i < names.size(); i++) {
                String temp = names.get(i);
                int j = i;
                while (j > 0 && names.get(j - 1).compareToIgnoreCase(temp) > 0) {
                    names.set(j, names.get(j - 1));
                    j--;
                }
                names.set(j, temp);
            }
            return names;
        }

        public Node getNodeByName(String name) {
            for (Node node : adjList.keySet()) {
                if (node.name.equalsIgnoreCase(name)) {
                    return node;
                }
            }
            return null;
        }
    }

    public static MapGraph FMIPAFATISDA() {
        MapGraph map = new MapGraph();

        // Gedung B ------------------------------------------------------------------------------------------------------------------------------------------------------

        // Lantai 4
        Node A = new Node("Ruang B411 FMIPA FATISDA",R.drawable.b411);
        Node B = new Node("Ruang B408 FMIPA FATISDA",R.drawable.b408);
        Node C = new Node("Lift Lantai 4 Gedung B FMIPA FATISDA",R.drawable.lift_lt4);
        Node D = new Node("Ruang B413 FMIPA FATISDA",R.drawable.b413);
        Node E = new Node("Pertigaan WorkSpace FATISDA",R.drawable.pertigaan_workspace_fatisda);
        Node F = new Node("Ruang B403 FMIPA FATISDA",R.drawable.b403);
        Node G = new Node("Ruang B404 FMIPA FATISDA",R.drawable.b404);
        Node H = new Node("Ruang B405 FMIPA FATISDA",R.drawable.b405);
        Node I = new Node("Tangga Samping Barat Lantai 4 Gedung B",R.drawable.tangga_samping_barat_lt4);
        Node J = new Node("Tangga Tengah Lantai 4 Gedung B",R.drawable.tangga_tengah_lt4);
        Node K = new Node("Ruang Bagian Akademik FATISDA B401",R.drawable.akademik_fatisda);
        Node L = new Node("Ruang Administrasi Prodi FATISDA",R.drawable.admin_prodi_fatisda);
        Node M = new Node("Belokan Lorong Samping Kiri FATISDA",R.drawable.lorong_kiri_fatisda);
        Node N = new Node("Toilet Mahasiswa Lantai 4 FATISDA",R.drawable.toilet_fatisda);
        Node O = new Node("Ruang Sub Bagian Non Akademik FATISDA B407",R.drawable.sb_nonakademik_fatisda);
        Node P = new Node("Ruang B406 FMIPA FATISDA",R.drawable.b406);
        Node Q = new Node("Ruang B412 FMIPA FATISDA",R.drawable.b412);
        Node U = new Node("Ruang Kepala Bagian Tata Usaha FATISDA",R.drawable.kb_tatausaha_fatisda);
        Node S = new Node("Ruang B410 FMIPA FATISDA",R.drawable.b410);
        Node T = new Node("Tangga Samping Timur Lantai 4 Gedung B",R.drawable.tangga_samping_timur_lt4);

        map.addNode(A);
        map.addNode(B);
        map.addNode(C);
        map.addNode(D);
        map.addNode(E);
        map.addNode(F);
        map.addNode(G);
        map.addNode(H);
        map.addNode(I);
        map.addNode(J);
        map.addNode(K);
        map.addNode(L);
        map.addNode(M);
        map.addNode(N);
        map.addNode(O);
        map.addNode(P);
        map.addNode(Q);
        map.addNode(S);
        map.addNode(T);
        map.addNode(U);

        map.addEdge(A, B, "barat",8,"maju");
        map.addEdge(B, A, "timur",8,"maju");
        map.addEdge(B, C, "selatan",2,"maju");
        map.addEdge(C, B, "utara",2,"maju");
        map.addEdge(D, B, "timur",2,"maju");
        map.addEdge(B, D, "barat",2,"maju");
        map.addEdge(D, E, "barat",19,"maju");
        map.addEdge(E, D, "timur",19,"maju");
        map.addEdge(E, F, "barat",13,"maju");
        map.addEdge(F, E, "timur",13,"maju");
        map.addEdge(G, F, "timur",2,"maju");
        map.addEdge(F, G, "barat",2,"maju");
        map.addEdge(G, H, "barat",15,"maju");
        map.addEdge(H, G, "timur",15,"maju");
        map.addEdge(H, I, "barat",4,"maju");
        map.addEdge(I, H, "timur",4,"maju");
        map.addEdge(E, J, "utara",7,"maju");
        map.addEdge(J, E, "selatan",7,"maju");
        map.addEdge(J, K, "barat",2,"maju");
        map.addEdge(K, J, "timur",2,"maju");
        map.addEdge(J, L, "timur",2,"maju");
        map.addEdge(L, J, "barat",2,"maju");
        map.addEdge(K, M, "utara",4,"maju");
        map.addEdge(M, K, "selatan",4,"maju");
        map.addEdge(M, N, "barat",4,"maju");
        map.addEdge(M, N, "barat",4,"maju");
        map.addEdge(N, M, "timur",4,"maju");
        map.addEdge(N, O, "barat",2,"maju");
        map.addEdge(O, N, "timur" ,2,"maju");
        map.addEdge(O, P, "barat" ,2,"maju");
        map.addEdge(P, O, "timur" ,2,"maju");
        map.addEdge(P, Q, "barat" ,6,"maju");
        map.addEdge(Q, P, "timur" ,6,"maju");
        map.addEdge(L, U, "utara" ,4,"maju");
        map.addEdge(U, L, "selatan",4,"maju");
        map.addEdge(U, S, "timur",19,"maju");
        map.addEdge(S, U, "barat",19,"maju");
        map.addEdge(S, T, "timur",13,"maju");
        map.addEdge(T, S, "barat",13,"maju");

        // Lantai 3

        Node AA = new Node("Ruang B321 FMIPA");
        Node AB = new Node("Ruang B320 FMIPA");
        Node AC = new Node("Ruang B319 FMIPA");
        Node AD = new Node("Lift Lantai 3 Gedung B FMIPA");
        Node AE = new Node("Ruang B318 FMIPA");
        Node AF = new Node("Ruang B305 FMIPA");
        Node AG = new Node("Pertigaan WorkSpace Prodi Kimia");
        Node AH = new Node("Ruang B302 FMIPA");
        Node AI = new Node("Ruang B303 FMIPA");
        Node AJ = new Node("Ruang B304 FMIPA");
        Node AK = new Node("Ruang B305 FMIPA");
        Node AL = new Node("Ruang B306 FMIPA");
        Node AM = new Node("Ruang B307 FMIPA");
        Node AN = new Node("Ruang B308 FMIPA");
        Node AO = new Node("Ruang B309 FMIPA");
        Node AP = new Node("Tangga Samping Barat Lantai 3 Gedung B");
        Node AQ = new Node("Ruang Lab Geofisika B301 FMIPA");
        Node AR = new Node("Tangga Tengah Lantai 3 Gedung B");
        Node AS = new Node("Ruang Administrasi dan Kaprodi Kimia");
        Node AT = new Node("Ruang Pertemuan B316 FMIPA");
        Node AU = new Node("Pertigaan Barat Lantai 3 FMIPA Gedung B");
        Node AV = new Node("Ruang B313 FMIPA");
        Node AW = new Node("Ruang B312 FMIPA");
        Node AX = new Node("Ruang B311 FMIPA");
        Node AY = new Node("Ruang B310 FMIPA");
        Node AZ = new Node("Pertigaan Timur Lantai 3 FMIPA Gedung B");
        Node BA = new Node("Ruang Administrasi S2 & S3 Kimia B326 FMIPA");
        Node BB = new Node("Ruang B325 FMIPA");
        Node BC = new Node("Ruang B324 FMIPA");
        Node BD = new Node("Ruang B323 FMIPA");
        Node BE = new Node("Ruang B322 FMIPA");
        Node BF = new Node("Tangga Samping Timur Lantai 4 Gedung B");
        Node BG = new Node("Ruang B314 FMIPA");

        map.addNode(AA);
        map.addNode(AB);
        map.addNode(AC);
        map.addNode(AD);
        map.addNode(AE);
        map.addNode(AF);
        map.addNode(AG);
        map.addNode(AH);
        map.addNode(AI);
        map.addNode(AJ);
        map.addNode(AK);
        map.addNode(AL);
        map.addNode(AM);
        map.addNode(AN);
        map.addNode(AO);
        map.addNode(AP);
        map.addNode(AQ);
        map.addNode(AR);
        map.addNode(AS);
        map.addNode(AT);
        map.addNode(AU);
        map.addNode(AV);
        map.addNode(AW);
        map.addNode(AX);
        map.addNode(AY);
        map.addNode(AZ);
        map.addNode(BA);
        map.addNode(BB);
        map.addNode(BC);
        map.addNode(BD);
        map.addNode(BE);
        map.addNode(BF);
        map.addNode(BG);

        map.addEdge(AA, AB, "barat",1,"maju");
        map.addEdge(AB, AA, "timur",1,"maju");
        map.addEdge(AB, AC, "barat",8,"maju");
        map.addEdge(AC, AB, "timur",8,"maju");
        map.addEdge(AD, AC, "utara",2,"maju");
        map.addEdge(AC, AD, "selatan",2,"maju");
        map.addEdge(AC, AE, "barat",6,"maju");
        map.addEdge(AE, AC, "timur",6,"maju");
        map.addEdge(AE, AF, "barat",1,"maju");
        map.addEdge(AF, AE, "timur",1,"maju");
        map.addEdge(AF, AG, "barat",13,"maju");
        map.addEdge(AG, AF, "timur",13,"maju");
        map.addEdge(AG, AH, "barat",8,"maju");
        map.addEdge(AH, AG, "timur",8,"maju");
        map.addEdge(AH, AI, "barat",5,"maju");
        map.addEdge(AI, AH, "timur",5,"maju");
        map.addEdge(AI, AJ, "barat",3,"maju");
        map.addEdge(AJ, AI, "timur",3,"maju");
        map.addEdge(AJ, AK, "barat",1,"maju");
        map.addEdge(AK, AJ, "timur",1,"maju");
        map.addEdge(AK, AL, "barat",3,"maju");
        map.addEdge(AL, AK, "timur",3,"maju");
        map.addEdge(AL, AM, "barat",5,"maju");
        map.addEdge(AM, AL, "timur",5,"maju");
        map.addEdge(AM, AN, "barat",1,"maju");
        map.addEdge(AN, AM, "timur",1,"maju");
        map.addEdge(AN, AO, "barat",3,"maju");
        map.addEdge(AO, AN, "timur",3,"maju");
        map.addEdge(AO, AP, "barat",4,"maju");
        map.addEdge(AP, AO, "timur",4,"maju");
        map.addEdge(AG, AQ, "utara",3,"maju");
        map.addEdge(AQ, AG, "selatan",3,"maju");
        map.addEdge(AQ, AR, "utara",4,"maju");
        map.addEdge(AR, AQ, "selatan",4,"maju");
        map.addEdge(BG, AR, "timur",2,"maju");
        map.addEdge(AR, BG, "barat",2,"maju");
        map.addEdge(AS, AR, "barat",2,"maju");
        map.addEdge(AR, AS, "timur",2,"maju");
        map.addEdge(AS, AT, "timur",4,"maju");
        map.addEdge(AT, AS, "barat",4,"maju");
        map.addEdge(BG, AU, "utara",9,"maju");
        map.addEdge(AU, BG, "selatan",9,"maju");
        map.addEdge(AV, AU, "timur",6,"maju");
        map.addEdge(AU, AV, "barat",6,"maju");
        map.addEdge(AV, AW, "barat",6,"maju");
        map.addEdge(AW, AV, "timur",6,"maju");
        map.addEdge(AX, AW, "timur",6,"maju");
        map.addEdge(AW, AX, "barat",6,"maju");
        map.addEdge(AX, AY, "barat",6,"maju");
        map.addEdge(AY, AX, "timur",6,"maju");
        map.addEdge(AS, AZ, "utara",9,"maju");
        map.addEdge(AZ, AS, "selatan",9,"maju");
        map.addEdge(BA, AZ, "barat",4,"maju");
        map.addEdge(AZ, BA, "timur",4,"maju");
        map.addEdge(BA, BB, "timur",6,"maju");
        map.addEdge(BB, BA, "barat",6,"maju");
        map.addEdge(BC, BB, "barat",7,"maju");
        map.addEdge(BB, BC, "timur",7,"maju");
        map.addEdge(BC, BD, "timur",2,"maju");
        map.addEdge(BD, BC, "barat",2,"maju");
        map.addEdge(BE, BD, "barat",10,"maju");
        map.addEdge(BD, BE, "timur",10,"maju");
        map.addEdge(BE, BF, "timur",2,"maju");
        map.addEdge(BF, BE, "barat",2,"maju");

        // Lantai 2

        Node CA = new Node("Ruang B222 FMIPA");
        Node CB = new Node("Ruang B221 FMIPA");
        Node CC = new Node("Ruang B220 FMIPA");
        Node CD = new Node("Ruang B219 FMIPA");
        Node CE = new Node("Lift Lantai 2 Gedung B FMIPA");
        Node CF = new Node("Pertigaan Ruang B217 B218 FMIPA");
        Node CG = new Node("Ruang B217 dan B218 FMIPA");
        Node CH = new Node("Ruang B216 FMIPA");
        Node CI = new Node("Ruang B215 FMIPA");
        Node CJ = new Node("Ruang B214 FMIPA");
        Node CK = new Node("Ruang B213 FMIPA");
        Node CL = new Node("Ruang Administrasi (1) Fisika B212 FMIPA");
        Node CM = new Node("Pintu Masuk Utama Gedung B FMIPA");
        Node CN = new Node("Ruang Kaprodi S2 Ilmu Fisika B202 FMIPA");
        Node CO = new Node("Ruang Pokja B201 FMIPA");
        Node CP = new Node("Ruang Kaprodi S3 Fisika B203 FMIPA");
        Node CQ = new Node("Ruang B204 FMIPA");
        Node CR = new Node("Ruang B205 FMIPA");
        Node CS = new Node("Ruang B206 FMIPA");
        Node CT = new Node("Ruang B207 FMIPA");
        Node CU = new Node("Ruang B208 FMIPA");
        Node CV = new Node("Ruang B209 FMIPA");
        Node CW = new Node("Tangga Samping Barat Lantai 2 Gedung B");
        Node CX = new Node("Tangga Tengah Lantai 2 Gedung B");
        Node CY = new Node("Kamar Mandi Dosen Lantai 2 Gedung B");
        Node CZ = new Node("Pertigaan Barat Lantai 2 FMIPA Gedung B");
        Node DA = new Node("Ruang B211 FMIPA");
        Node DB = new Node("Ruang S2 Kimia B210B FMIPA");
        Node DC = new Node("Ruang B210 FMIPA");
        Node DD = new Node("Ruang Multimedia Departemen Fisika FMIPA Gedung B");
        Node DE = new Node("Pertigaan Timur Lantai 2 FMIPA Gedung B");
        Node DF = new Node("Ruang ??? Lantai 2 FMIPA");
        Node DG = new Node("Tangga Samping Timur Lantai 4 Gedung B");
        Node DH = new Node("Ruang Administrasi Fisika (2) B212 FMIPA");

        map.addNode(CA);
        map.addNode(CB);
        map.addNode(CC);
        map.addNode(CD);
        map.addNode(CE);
        map.addNode(CF);
        map.addNode(CG);
        map.addNode(CH);
        map.addNode(CI);
        map.addNode(CJ);
        map.addNode(CK);
        map.addNode(CL);
        map.addNode(CM);
        map.addNode(CN);
        map.addNode(CO);
        map.addNode(CP);
        map.addNode(CQ);
        map.addNode(CR);
        map.addNode(CS);
        map.addNode(CT);
        map.addNode(CU);
        map.addNode(CV);
        map.addNode(CW);
        map.addNode(CX);
        map.addNode(CY);
        map.addNode(CZ);
        map.addNode(DA);
        map.addNode(DB);
        map.addNode(DC);
        map.addNode(DD);
        map.addNode(DF);
        map.addNode(DE);
        map.addNode(DG);
        map.addNode(DH);

        map.addEdge(CA, CB, "barat",6,"maju");
        map.addEdge(CB, CA, "timur",6,"maju");
        map.addEdge(CB, CC, "barat",2,"maju");
        map.addEdge(CC, CB, "timur",2,"maju");
        map.addEdge(CC, CD, "barat",1,"maju");
        map.addEdge(CD, CC, "timur",1,"maju");
        map.addEdge(CD, CE, "selatan",2,"maju");
        map.addEdge(CE, CD, "utara",2,"maju");
        map.addEdge(CD, CF, "barat",3,"maju");
        map.addEdge(CD, CF, "timur",3,"maju");
        map.addEdge(CF, CG, "utara",3,"maju");
        map.addEdge(CG, CF, "selatan",3,"maju");
        map.addEdge(CF, CH, "barat",3,"maju");
        map.addEdge(CH, CF, "timur",3,"maju");
        map.addEdge(CH, CI, "barat",2,"maju");
        map.addEdge(CI, CH, "timur",2,"maju");
        map.addEdge(CI, CJ, "barat",2,"maju");
        map.addEdge(CJ, CI, "timur",2,"maju");
        map.addEdge(CJ, CK, "barat",5,"maju");
        map.addEdge(CK, CJ, "timur",5,"maju");
        map.addEdge(CK, CL, "barat",4,"maju");
        map.addEdge(CL, CK, "timur",4,"maju");
        map.addEdge(CL, CM, "barat",4,"maju");
        map.addEdge(CM, CL, "timur",4,"maju");
        map.addEdge(CM, CN, "barat",8,"maju");
        map.addEdge(CN, CM, "timur",8,"maju");
        map.addEdge(CN, CO, "barat",1,"maju");
        map.addEdge(CO, CN, "timur",1,"maju");
        map.addEdge(CO, CP, "barat",8,"maju");
        map.addEdge(CP, CO, "timur",8,"maju");
        map.addEdge(CP, CQ, "barat",1,"maju");
        map.addEdge(CQ, CP, "timur",1,"maju");
        map.addEdge(CQ, CR, "barat",3,"maju");
        map.addEdge(CR, CQ, "timur",3,"maju");
        map.addEdge(CR, CS, "barat",1,"maju");
        map.addEdge(CS, CR, "timur",1,"maju");
        map.addEdge(CS, CT, "barat",3,"maju");
        map.addEdge(CT, CS, "timur",3,"maju");
        map.addEdge(CT, CU, "barat",4,"maju");
        map.addEdge(CU, CT, "timur",4,"maju");
        map.addEdge(CU, CV, "barat",1,"maju");
        map.addEdge(CV, CU, "timur",1,"maju");
        map.addEdge(CV, CW, "barat",4,"maju");
        map.addEdge(CW, CV, "timur",4,"maju");
        map.addEdge(CM, CX, "utara",8,"maju");
        map.addEdge(CX, CM, "selatan",8,"maju");
        map.addEdge(CX, CY, "barat",2,"maju");
        map.addEdge(CY, CX, "timur",2,"maju");
        map.addEdge(DH, CX, "barat",2,"maju");
        map.addEdge(CX, DH, "timur",2,"maju");
        map.addEdge(CY, CZ, "utara",8,"maju");
        map.addEdge(CZ, CY, "selatan",8,"maju");
        map.addEdge(CZ, DA, "barat",7,"maju");
        map.addEdge(DA, CZ, "timur",7,"maju");
        map.addEdge(DA, DB, "barat",5,"maju");
        map.addEdge(DB, DA, "timur",5,"maju");
        map.addEdge(DB, DC, "barat",7,"maju");
        map.addEdge(DC, DB, "timur",7,"maju");
        map.addEdge(DC, DD, "barat",0,"maju");
        map.addEdge(DD, DC, "timur",0,"maju");
        map.addEdge(CZ, DE, "timur",4,"maju");
        map.addEdge(DE, CZ, "barat",4,"maju");
        map.addEdge(DE, DF, "timur",19,"maju");
        map.addEdge(DF, DE, "barat",19,"maju");
        map.addEdge(DF, DG, "timur",12,"maju");
        map.addEdge(DG, DF, "barat",12,"maju");
        map.addEdge(DH, DE, "utara",8,"maju");
        map.addEdge(DE, DH, "selatan",8,"maju");

        // Lantai 1

        Node EA = new Node("Ruang B115 FMIPA");
        Node EB = new Node("Ruang B114 FMIPA");
        Node EC = new Node("Lift Lantai 1 Gedung B FMIPA");
        Node ED = new Node("Ruang B112 / B113 FMIPA");
        Node EE = new Node("Ruang B111 FMIPA");
        Node EF = new Node("Ruang B110 FMIPA");
        Node EG = new Node("Ruang B109 FMIPA");
        Node EH = new Node("Ruang B108 FMIPA");
        Node EI = new Node("Tangga Tengah Lantai 1 Gedung B");
        Node EJ = new Node("Ruang B103 FMIPA");
        Node EK = new Node("Ruang B102 FMIPA");
        Node EL = new Node("Ruang B104 FMIPA");
        Node EM = new Node("Ruang B106 FMIPA");
        Node EN = new Node("Ruang B105 FMIPA");
        Node EO = new Node("Ruang B107 FMIPA");
        Node EP = new Node("Tangga Samping Barat Lantai 2 Gedung B");
        Node EQ = new Node("Kantin Sehat Gedung B FMIPA");
        Node ER = new Node("Tugu FMIPA / FATISDA");

        map.addNode(EA);
        map.addNode(EB);
        map.addNode(EC);
        map.addNode(ED);
        map.addNode(EE);
        map.addNode(EF);
        map.addNode(EG);
        map.addNode(EH);
        map.addNode(EI);
        map.addNode(EJ);
        map.addNode(EK);
        map.addNode(EL);
        map.addNode(EM);
        map.addNode(EN);
        map.addNode(EO);
        map.addNode(EP);
        map.addNode(EQ);
        map.addNode(ER);

        map.addEdge(EA, EB, "barat",6,"maju");
        map.addEdge(EB, EA, "timur",6,"maju");
        map.addEdge(EB, EC, "selatan",2,"maju");
        map.addEdge(EC, EB, "utara",2,"maju");
        map.addEdge(EB, ED, "barat",6,"maju");
        map.addEdge(ED, EB, "timur",6,"maju");
        map.addEdge(ED, EE, "barat",7,"maju");
        map.addEdge(EE, ED, "timur",7,"maju");
        map.addEdge(EE, EF, "barat",4,"maju");
        map.addEdge(EF, EE, "timur",4,"maju");
        map.addEdge(EF, EG, "utara",4,"maju");
        map.addEdge(EG, EF, "selatan",4,"maju");
        map.addEdge(EF, EH, "barat",6,"maju");
        map.addEdge(EH, EF, "timur",6,"maju");
        map.addEdge(EH, EI, "utara",10,"maju");
        map.addEdge(EI, EH, "selatan",10,"maju");
        map.addEdge(EH, EJ, "barat",9,"maju");
        map.addEdge(EJ, EH, "timur",9,"maju");
        map.addEdge(EJ, EK, "utara",4,"maju");
        map.addEdge(EK, EJ, "selatan",4,"maju");
        map.addEdge(EJ, EL, "barat",5,"maju");
        map.addEdge(EL, EJ, "timur",5,"maju");
        map.addEdge(EL, EM, "barat",7,"maju");
        map.addEdge(EM, EL, "timur",7,"maju");
        map.addEdge(EM, EN, "utara",4,"maju");
        map.addEdge(EN, EM, "selatan",4,"maju");
        map.addEdge(EM, EO, "barat",5,"maju");
        map.addEdge(EO, EM, "timur",5,"maju");
        map.addEdge(EO, EP, "barat",6,"maju");
        map.addEdge(EP, EO, "timur",6,"maju");
        map.addEdge(EP, EQ, "barat",10,"maju");
        map.addEdge(EQ, EP, "timur",10,"maju");
        map.addEdge(EH, ER, "selatan",9,"maju lalu naik");
        map.addEdge(ER, EH, "utara",9,"maju lalu turun");

        // Edge 3,5

        map.addEdge(C, AD, "utara",0,"turun");
        map.addEdge(AD, C, "utara",0,"naik");
        map.addEdge(I, AP, "utara","selatan",10,"turun");
        map.addEdge(AP, I, "utara","selatan",10,"naik");
        map.addEdge(J, AR, "utara","selatan",12,"turun");
        map.addEdge(AR, J, "utara","selatan",12,"naik");
        map.addEdge(T, BF, "selatan","utara",10,"turun");
        map.addEdge(BF, T, "selatan","utara",10,"naik");

        // Edge 2,5

        map.addEdge(AD, CE, "utara",0,"turun");
        map.addEdge(CE, AD, "utara",0,"naik");
        map.addEdge(AP, CW, "utara","selatan",10,"turun");
        map.addEdge(CW, AP, "utara","selatan",10,"naik");
        map.addEdge(AR, CX, "utara","selatan",12,"turun");
        map.addEdge(CX, AR, "utara","selatan",12,"naik");
        map.addEdge(BF, DG, "selatan","utara",10,"turun");
        map.addEdge(DG, BF, "selatan","utara",10,"naik");

        // Edge 1,5

        map.addEdge(CE, EC, "utara",0,"turun");
        map.addEdge(EC, CE, "utara",0,"naik");
        map.addEdge(CW, EP, "utara","selatan",10,"turun");
        map.addEdge(EP, CW, "utara","selatan",10,"naik");
        map.addEdge(CX, EI, "utara","selatan",12,"turun");
        map.addEdge(EI, CX, "utara","selatan",12,"naik");
        map.addEdge(CM, ER, "selatan",9,"maju lalu turun");
        map.addEdge(ER, CM, "utara",9,"maju lalu naik");

        return map;
    }

}