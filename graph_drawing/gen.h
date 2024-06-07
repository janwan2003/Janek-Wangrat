#include <iostream>
#include <string>
#include <vector>

#define NEW_LINE_CHARACTER '\n'

constexpr auto LATEX_OUTPUT_TEMPLATE = R"(\begin{center}
    \begin{tikzpicture}[shorten >=1pt,node distance=3cm,on grid,auto]
<NODES>
        \path[->] 
<EDGES>;
    \end{tikzpicture}
\end{center})";

constexpr auto NODE_TEMPLATE = "        \\node (q_<i>) [state<TYPE>] at (<x>,<y>) {$q_<i>$};";

constexpr auto EDGE_TEMPLATE = "                    (q_<a>) edge node {<LABEL>} (q_<b>)";

enum StateType {
    Initial, //0
    Accepting, //1
    Default //2
};

struct Node {
    size_t index;
    double x;
    double y;
    StateType state_type;
};

struct Edge {
    size_t from;
    size_t to;
    char label;
};

Node example_nodes[7] = {
    {0, 0, 1.15312, Initial},
    {1, 6.85171, 6.55823, Default},
    {2, 9.9, 0.971473, Default},
    {3, 0.385457, 5.89088, Accepting},
    {4, 7.52838, 0, Accepting},
    {5, 3.32077, 1.8421, Default},
    {6, 1.44917, 4.02544, Default},
};

Edge example_edges[8] = {
    {0, 1, 'a'},
    {0, 2, 'b'},
    {1, 3, 'b'},
    {2, 4, 'a'},
    {2, 5, 'b'},
    {4, 5, 'a'},
    {3, 5, 'a'},
    {5, 6, 'b'}
};

void replace_all(std::string& str, const std::string& old_substr, const std::string& new_substr) {
    size_t pos = str.find(old_substr);

    while (pos != std::string::npos) {
        str.replace(pos, old_substr.length(), new_substr);

        pos = str.find(old_substr);
    }
}

std::string map_state_type_to_string(StateType type) {
    switch (type) {
        case StateType::Initial:
            return ", initial";
        case StateType::Accepting:
            return ", accepting";
        default:
            return "";
    }
}

std::string generate_node(Node node) {
    std::string result = NODE_TEMPLATE;

    replace_all(result, "<i>", std::to_string(node.index));
    replace_all(result, "<x>", std::to_string(node.x));
    replace_all(result, "<y>", std::to_string(node.y));
    replace_all(result, "<TYPE>", map_state_type_to_string(node.state_type));

    return result;
}

std::string generate_edge(Edge edge) {
    std::string result = EDGE_TEMPLATE;

    replace_all(result, "<a>", std::to_string(edge.from));
    replace_all(result, "<b>", std::to_string(edge.to));
    replace_all(result, "<LABEL>", std::string(&edge.label, 1));

    return result;
}

std::string generate_nodes(std::vector<Node> nodes) {
    std::string result = "";

    for (size_t i = 0; i < nodes.size(); i++) {
        result += generate_node(nodes[i]);

        if (i != nodes.size() - 1) {
            result += NEW_LINE_CHARACTER;
        }
    }

    return result;
}

std::string generate_edges(std::vector<Edge> edges) {
    std::string result = "";

    for (size_t i = 0; i < edges.size(); i++) {
        result += generate_edge(edges[i]);

        if (i != edges.size() - 1) {
            result += NEW_LINE_CHARACTER;
        }
    }

    return result;
}

std::string generate_latex(std::vector<Node> nodes, std::vector<Edge>edges) {
    std::string result = LATEX_OUTPUT_TEMPLATE;

    replace_all(result, "<NODES>", generate_nodes(nodes));
    replace_all(result, "<EDGES>", generate_edges(edges));

    return result;
}