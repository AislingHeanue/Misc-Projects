use hash_visualisation::{Node, NodeColour};
fn main() {
    let new_node = Box::new(Node::Add(
        Box::new(Node::X),
        Box::new(Node::Mult(Box::new(Node::Y), Box::new(Node::Number(-0.1)))),
    ));
    let c = NodeColour {
        r: new_node,
        g: Box::new(Node::Number(-0.3)),
        b: Box::new(Node::Number(0.7)),
    };

    let (r, g, b) = c.eval(&0.5, &0.5).unwrap();
    println!("hello");
    println!("Node: {}", c);
    println!("Node at 0.5,0.5 ({}, {}, {})", r, g, b)
}
