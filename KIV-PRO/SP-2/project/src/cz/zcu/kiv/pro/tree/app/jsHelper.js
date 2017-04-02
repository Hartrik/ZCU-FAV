
/* global graph */

function edge(x, y) {
    graph.addEdge(x, y);
};

function edges() {
    for (var i = 0; i < arguments.length; i++) {
        var pair = arguments[i];
        graph.addEdge(pair[0], pair[1]);
    }
}