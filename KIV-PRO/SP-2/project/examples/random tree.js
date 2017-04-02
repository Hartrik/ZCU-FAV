
var MAX_WIDTH = 3;
var MAX_DEPTH = 8;

var holder = { last_id:0 };

function populate(vertex, depth) {
    if (depth >= MAX_DEPTH) return;

    if (Math.random() * (depth / MAX_DEPTH) > .2) return;

    var count = Math.random() * MAX_WIDTH;
    for (var i = 0; i < count; i++) {
        var next = ++holder.last_id;
        edge(vertex, next);
        populate(next, depth + 1);
    }
}

populate(++holder.last_id, 1);

view.setHorizontalGap(10);
view.setVerticalGap(25);
view.setScale(1);